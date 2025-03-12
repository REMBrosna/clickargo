package com.guudint.clickargo.clictruck.jobupload.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.constant.TruckTripAttachmentEnum;
import com.guudint.clickargo.clictruck.dsv.pod.PodServiceImpl;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.model.TCoreNotificationLog;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.util.email.SysParam;

@Service
public class SendEpodService {

	private static Logger log = Logger.getLogger(SendEpodService.class);

	@Autowired
	private CkJobTruckDao jobTruckDao;
	@Autowired
	private CkCtTripDoAttachDao tripDoAttachDao;
	@Autowired
	private CkCtTripDao tripDao;
	@Autowired
	protected CkCtTripDoDao ckCtTripDoDao;
	@Autowired
	private PodServiceImpl podService;

	@Autowired
	protected CkNotificationUtilService notificationUtilService;
	@Autowired
	protected NotificationService notificationService;
	@Autowired
	protected SysParam sysParam;

	@Transactional
	public synchronized String sendePodEmail() {
		
		String rst = "";

		try {
			// find all jobs, need to send ePod file
			//List<TCkJobTruck> jobTruckList = jobTruckDao.findByStatusAndEpodIsNull(JobStates.DLV.name(), 10);
			String dsvasAccnId = sysParam.getValString("CLICTRUCK_DSVAS_ACCN_ID", "DSVAS");
			List<TCkJobTruck> jobTruckList = jobTruckDao.findByStatusAndEpodIsNull(JobStates.DLV.name(), dsvasAccnId);

			if (null == jobTruckList || jobTruckList.size() == 0) {
				return "";
			}
			log.info("Total jobs: " + jobTruckList.size() );
			
			for (TCkJobTruck jobTruck : jobTruckList) {
				
				rst = rst + this.sendEpodEmail(jobTruck);
			}
		} catch (Exception e) {
			log.error("", e);
			rst = rst + e.getMessage();
		}
		return rst;
	}
	@Transactional
	public String sendEpodEmail(TCkJobTruck jobTruck) {
		try {
			// copy file to
			List<TCkCtTripDoAttach> doAttachList = this.createAndSaveEPod(jobTruck);
			// send email to
			for (TCkCtTripDoAttach doAttach : doAttachList) {
				this.sendEmailNotification(doAttach.getDoaLoc(), jobTruck);
			}
			// update TCkJobTruck.jobDtEpod
			Date now = new Date();
			jobTruck.setJobDtEpod(now);
			jobTruck.setJobDtLupd(now);
			jobTruckDao.saveOrUpdate(jobTruck);
			
		} catch (Exception e) {
			//throw e;
			log.error("Fail to process: " + jobTruck.getJobId(), e);
			return jobTruck.getJobId() + " not ok, " + e.getMessage();
		}
		return jobTruck.getJobId() + " ok, " ;
	}
	

	@Transactional
	public List<TCkCtTripDoAttach> refreshEpodFile(String jobTruckId) throws Exception {

		log.info("refreshEpodFile: " + jobTruckId );
		
		List<TCkCtTrip> tripList = tripDao.findByJobId(jobTruckId);
		List<TCkCtTripDoAttach> doAttachList = new ArrayList<>();

		for (TCkCtTrip trip : tripList) {

			TCkCtTripDoAttach doAttach = this.createEpod(trip);
			doAttachList.add(doAttach);
			
			this.updateTripDo(trip, doAttach);
		}
		return doAttachList;
	}

	private List<TCkCtTripDoAttach> createAndSaveEPod(TCkJobTruck jobTruck) throws Exception {

		log.info("createAndSaveEPod: " + jobTruck.getJobId() );
		
		List<TCkCtTrip> tripList = tripDao.findByJobId(jobTruck.getJobId());
		List<TCkCtTripDoAttach> doAttachList = new ArrayList<>();

		List<TCkCtTripDoAttach> exitingDoAttachList = tripDoAttachDao.findByJobId(jobTruck.getJobId());

		for (TCkCtTrip trip : tripList) {

			TCkCtTripDoAttach doAttach = exitingDoAttachList.stream()
					.filter(da -> trip.getTrId().equalsIgnoreCase(da.getTCkCtTrip().getTrId())).findFirst()
					.orElse(null);

			if (null == doAttach) {
				doAttach = this.createEpod(trip);
			}
			doAttachList.add(doAttach);
			
			this.updateTripDo(trip, doAttach);
		}
		return doAttachList;
	}

	private TCkCtTripDoAttach createEpod(TCkCtTrip trip) throws Exception {

		log.info("createEpod by trip id: " + trip.getTrId() );
		
		String filePath = podService.generateShipmentReport(trip);

		String fileName = FilenameUtils.getName(filePath);

		TCkCtTripDoAttach ckCtTripDoAttach = new TCkCtTripDoAttach();
		ckCtTripDoAttach.setDoaId(CkUtil.generateId(ICkConstant.PREFIX_JOB_ATT));
		ckCtTripDoAttach.setTCkCtTrip(trip);
		ckCtTripDoAttach.setDoaName(fileName);
		ckCtTripDoAttach.setDoaLoc(filePath);
		ckCtTripDoAttach.setDoaSource(TruckTripAttachmentEnum.SCHEDULER.name());

		ckCtTripDoAttach.setDoaStatus(RecordStatus.ACTIVE.getCode());
		ckCtTripDoAttach.setDoaDtCreate(Calendar.getInstance().getTime());
		ckCtTripDoAttach.setDoaUidCreate("SYS");
		ckCtTripDoAttach.setDoaDtLupd(Calendar.getInstance().getTime());
		ckCtTripDoAttach.setDoaUidLupd("SYS");

		tripDoAttachDao.saveOrUpdate(ckCtTripDoAttach);

		return ckCtTripDoAttach;
	}

	private void sendEmailNotification(String file, TCkJobTruck jobTruck) throws Exception {

		log.info(" send ePOD: sendEmailNotification: " + file );
		try {
			NotificationParam param = new NotificationParam();
			param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
			param.setTemplateId(ClicTruckNotifTemplates.DSV_POD_PHO.getId());

			String recipientStr = this.getReceiver(jobTruck);

			HashMap<String, String> contentFields = new HashMap<>();
			// contentFields.put(":sp_details", "'");

			param.setContentFeilds(contentFields);
			param.setAttachments(new ArrayList<>(Arrays.asList(file)));

			param.setRecipients(new ArrayList<>(Arrays.asList(recipientStr.split(","))));

			HashMap<String, String> subjectFields = new HashMap<>();
			subjectFields.put(":subject", "ediDocManager SHP POD " + jobTruck.getJobShipmentRef());
			param.setSubjectFields(subjectFields);

			TCoreNotificationLog notifLog = notificationUtilService.saveNotificationLog(param.toJson(), null, false);
			log.info("TCoreNotificationLog id: " + notifLog.getNlogId());
			notificationService.sendNotificationsByLogId(notifLog.getNlogId());
			// notificationService.notifySyn(param);

		} catch (Exception e) {
			log.error("Fail to send email." + file + " " + jobTruck.getJobShipmentRef() + " " + jobTruck.getJobId(), e);
			throw e;
		}
	}
	
	private void updateTripDo(TCkCtTrip trip, TCkCtTripDoAttach doAttach) {
		try {
			// 1 trip, 1 tripDo, 1 tripDoAttach
			List<TCkCtTripDo> tripDoList = ckCtTripDoDao.findByTripId(trip.getTrId());
			if(tripDoList != null && tripDoList.size() > 0) {
				for(TCkCtTripDo tripDo: tripDoList) {
					//if(StringUtils.isBlank(tripDo.getDoSigned())) {
						tripDo.setDoSigned(doAttach.getDoaId());
						tripDo.setDoDtLupd(new Date());
						ckCtTripDoDao.update(tripDo);
					//	break;
					//}
				}
			}
		} catch (Exception e) {
			log.error("",e);
		}
	}

	private String getReceiver(TCkJobTruck jobTruck) throws Exception {
		if (jobTruck.getTCkCtContactDetailByJobContactCoFf() != null
				&& jobTruck.getTCkCtContactDetailByJobContactCoFf().getCdEmail() != null) {
			return jobTruck.getTCkCtContactDetailByJobContactCoFf().getCdEmail();
		}
		if (jobTruck.getTCoreAccnByJobPartyCoFf() != null
				&& jobTruck.getTCoreAccnByJobPartyCoFf().getAccnContact() != null
				&& jobTruck.getTCoreAccnByJobPartyCoFf().getAccnContact().getContactEmail() != null) {
			return jobTruck.getTCoreAccnByJobPartyCoFf().getAccnContact().getContactEmail();
		}
		throw new Exception("Fail to find email receiver: " + jobTruck.getJobId());
	}
}
