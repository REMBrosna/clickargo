package com.guudint.clickargo.clictruck.planexec.trip.mobile.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.common.dto.NotificationTemplateName;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtJobTripDeliveryDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobMTripDto;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobTruckMobileDto;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTripDelivery;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.IJobDeliveryService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService.DeliveryStatus;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.track.dto.DistanceMatrixDto;
import com.guudint.clickargo.clictruck.track.dto.ImeiLatestStatusDto;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnDao;
import com.guudint.clickargo.common.service.impl.CKEncryptionUtil;
import com.guudint.clickargo.master.enums.Roles;
import com.vcc.camelone.cac.model.TCoreUsrRole;
import com.vcc.camelone.cac.model.TCoreUsrRoleId;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.config.dao.CoreSysparamDao;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.scheduler.dto.CoreScheduleJoblog;
import com.vcc.camelone.scheduler.service.AbstractJob;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@EnableAsync
public class TripDeliveryMonitoringScheduler extends AbstractJob {

	private static final Logger log = Logger.getLogger(TripDeliveryMonitoringScheduler.class);
	private static String CLICTRUCK_DELIVERY_API_CALL = "CLICTRUCK_DELIVERY_API_CALL";
	@Autowired
	private CkAccnDao ckAccnDao;
	@Autowired
	private CkJobTruckDao ckJobTruckDao;
	@Autowired
	private CkCtJobTripDeliveryDao ckCtJobTripDeliveryDao;
	@Autowired
	protected GenericDao<TCoreUsrRole, TCoreUsrRoleId> coreUsrRoleDao;
	@Autowired
	private CoreSysparamDao coreSysparamDao;
	@Autowired
	private CkCtTripDao ckCtTripDao;
	@Autowired
	private IJobDeliveryService jobDeliveryService;
	@Autowired
	private TripMobileService tripMobileService;
	@Autowired
	private CkJobTruckServiceUtil ckJobTruckServiceUtil;
	@Autowired
	protected ClickargoAccnService clickargoAccnService;
	@Autowired
	@Qualifier("ckJobTruckService")
	private IEntityService<TCkJobTruck, String, CkJobTruck> ckJobTruckService;
	@Autowired
	@Qualifier("ccmAccnService")
	protected IEntityService<TCoreAccn, String, CoreAccn> ccmAccnService;
	public TripDeliveryMonitoringScheduler() {
		super.setTaskName(this.getClass().getSimpleName());
	}
	@Autowired
	@Qualifier("ckCtAlertDao")
	private GenericDao<TCkCtAlert, String> ckCtAlertDao;
	private static final String KEY_APPROVAL_LINK_VALIDITY = "CLICTRUCK_EMAIL_APPROVAL_VALIDITY";
	protected static ObjectMapper mapper = new ObjectMapper();

	@Override
	@Scheduled(cron = "0 0/2 * * * *") // Need to pay to Google when call google API.
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void doJob() throws Exception {
		log.info("TripDeliveryMonitoringScheduler running in " + InetAddress.getLocalHost().getHostName());
		String taskNo = super.getTaskNo();
		CoreScheduleJoblog coreScheduleJoblog;
		try {
			coreScheduleJoblog = super.getTask(TASK_STATE.START, taskNo, TASK_STATE.START.toString());
			log.info("TripDeliveryMonitoringScheduler Started: " + Calendar.getInstance().getTime().toString());

			try {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find(CLICTRUCK_DELIVERY_API_CALL));
				 if (opTCoreSysparam.isPresent()
						&& opTCoreSysparam.get().getSysVal().equalsIgnoreCase("Y")) {
					this.executeTripDelivery();
				}

				coreScheduleJoblog = super.getTask(TASK_STATE.COMPLETE, taskNo,
						ServiceStatus.STATUS.COMPLETED.toString(), "SUCCESS");

			} catch (Exception ex) {
				log.error("Error occurred in DSVLoadXMLFileScheduler, taskNo: " + taskNo, ex);
				coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, ServiceStatus.STATUS.FAILED.toString(),
						ex.getMessage());
			}

			super.logTask(coreScheduleJoblog);
			log.info("TripDeliveryMonitoringScheduler Ended: " + Calendar.getInstance().getTime().toString());

		} catch (Exception ex) {
			log.error("doJob", ex);
			coreScheduleJoblog = super.getTask(TASK_STATE.EXCEPTION, taskNo, "EXCEPTION",
					ExceptionUtils.getStackTrace(ex));
			super.logTask(coreScheduleJoblog);
		}
	}
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void executeTripDelivery() throws Exception {
		log.info("executeTripDelivery");
		List<TCkJobTruck> listingJobsTrucksOngoing = ckJobTruckDao.findOngoingJob();

		for (TCkJobTruck tCkJobTruck : listingJobsTrucksOngoing) {
			Optional<TCkCtJobTripDelivery> tCkCtJobTripDelivery = Optional.ofNullable(ckCtJobTripDeliveryDao.findByJobId(tCkJobTruck.getJobId()));

			if (tCkCtJobTripDelivery.isPresent()
					&& tCkCtJobTripDelivery.get().getJtdStatus().equalsIgnoreCase(DeliveryStatus.DELIVERING.name())) {
					Optional<String> imei = Optional.ofNullable(tCkCtJobTripDelivery.get().getJtdImei());
					
					if (imei.isPresent()) {
						// for call API ext.logistics.myascents.net
						log.info("getImeiLocation Param : " + imei.get() + " , " + tCkCtJobTripDelivery.get().getJtdJobId());
						List<ImeiLatestStatusDto> locImei = jobDeliveryService.getImeiLocation(imei.get(), tCkCtJobTripDelivery.get().getJtdJobId());
						if (!locImei.isEmpty()) {
							//Filter locImei where ignition is true meaning trucks are running
							List<ImeiLatestStatusDto> filteredLocImei = locImei.stream()
									.filter(ImeiLatestStatusDto::isIgnition)  // Assuming isIgnition() method returns boolean
									.collect(Collectors.toList());

							String originLoc = filteredLocImei.get(0).getGPS();
							// for call Google API's
							log.info("getDistanceMatrix Param : " + originLoc + " , "
									+ tCkCtJobTripDelivery.get().getJtdDestLoc() + " , "
									+ tCkCtJobTripDelivery.get().getJtdJobId());
							DistanceMatrixDto distanceMatrixDto = jobDeliveryService.getDistanceMatrix(originLoc, tCkCtJobTripDelivery.get().getJtdDestLoc(), tCkCtJobTripDelivery.get().getJtdJobId());

							tCkCtJobTripDelivery.get().setJtdOriginLoc(originLoc);
							if (distanceMatrixDto != null) {
								Optional<Integer> distance = Optional.ofNullable(distanceMatrixDto.getRows().get(0).getElements().get(0).getDistance().getValue());
								tCkCtJobTripDelivery.get().setJtdDistance(distance.isPresent() ? distance.get() : 0);
								Optional<Integer> duration = Optional.ofNullable(distanceMatrixDto.getRows().get(0).getElements().get(0).getDuration().getValue());
								tCkCtJobTripDelivery.get().setJtdDuration(duration.isPresent() ? new Double(duration.get()) : 0);
								tCkCtJobTripDelivery.get().setJtdDtLastScan(new Date());
								tCkCtJobTripDelivery.get().setJtdDtLupd(new Date());

								this.sendWhatApp(tCkJobTruck, duration.get(), tCkCtJobTripDelivery.get());
								tCkCtJobTripDelivery.get().setJtdDtPreNotify(new Date());
								ckCtJobTripDeliveryDao.saveOrUpdate(tCkCtJobTripDelivery.get());
							}
						}
					}
//				}
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void sendWhatApp(TCkJobTruck tCkJobTruck, Integer duration,  TCkCtJobTripDelivery tCkCtJobTripDelivery) throws Exception {
		log.debug("sendWhatApp :");
		try {
			// Convert entity to DTO
			CkJobTruck ckJobTruck = tripMobileService.dtoFromEntity(tCkJobTruck);
			// Retrieve the jobTruck to get the vehicle details
			CkJobTruck jobTruckDto = ckJobTruckService.findById(tCkJobTruck.getJobId());
			// Retrieve trips associated with the job truck
			TCkCtTrip tCkCtTrip = getTripDetails((CkJobTruckMobileDto) ckJobTruck);
			// Send WhatsApp notification
			this.sendWhatsAppNotification(tCkCtTrip, jobTruckDto, duration, tCkCtJobTripDelivery);
		} catch (Exception e) {
			log.error("Failed to send Whatsapp message : " + e.getMessage());
			throw new ProcessingException(e);
		}
	}
	private TCkCtTrip getTripDetails(CkJobTruckMobileDto ckJobTruckDto) throws Exception {
		TCkCtTrip tCkCtTrip = null;
		for (CkJobMTripDto dto : ckJobTruckDto.getTrips()) {
			tCkCtTrip = ckCtTripDao.find(dto.getId());
		}
		return tCkCtTrip;
	}
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void sendWhatsAppNotification(TCkCtTrip tCkCtTrip, CkJobTruck jobTruckDto,
										 Integer duration,  TCkCtJobTripDelivery tCkCtJobTripDelivery) throws Exception {
		String clasquinPhoneNum = ckJobTruckServiceUtil.getClasquinPhoneNumber(jobTruckDto);
		NotificationTemplateName template = determineTemplate(duration, tCkCtJobTripDelivery, jobTruckDto);
		String generateLink = constructGenerateLink(jobTruckDto);
		if (template != null && template.getDesc() != null) {
			formatContent(clasquinPhoneNum, jobTruckDto, tCkCtTrip, template.getDesc(), generateLink);
		}
	}
	private NotificationTemplateName determineTemplate(Integer duration, TCkCtJobTripDelivery tCkCtJobTripDelivery, CkJobTruck jobTruckDto) throws Exception {
		int convertToMinutes = duration / 60;

		log.info("duration :" + duration);

		NotificationTemplateName template = null;

		String currentState = tCkCtJobTripDelivery.getJtdMsgState();
		// Handle "CARGO_1_HOUR" state
		if (currentState.equalsIgnoreCase(NotificationTemplateName.CARGO_PICKED_UP.getDesc())
				&& !currentState.equalsIgnoreCase(NotificationTemplateName.CARGO_1_HOUR.getDesc())
				&& convertToMinutes == 60) {
			template = NotificationTemplateName.CARGO_1_HOUR;
		}
		// Handle "CARGO_30_MIN" state
		else if (!currentState.equalsIgnoreCase(NotificationTemplateName.CARGO_30_MIN.getDesc()) && convertToMinutes <= 30) {
			template = NotificationTemplateName.CARGO_30_MIN;
		}
		// Update state and save changes if a template is determined
		if (template != null) {
			tCkCtJobTripDelivery.setJtdMsgState(template.getDesc().toUpperCase());
			tCkCtJobTripDelivery.setJtdDtPreNotify(new Date());
			ckCtJobTripDeliveryDao.saveOrUpdate(tCkCtJobTripDelivery);
		}
		return template;
	}
	public String constructGenerateLink(CkJobTruck ckJobTruck) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		// append the max date that the link is still valid to access
		int maxDaysLinkValidity = Integer.parseInt(getSysParam(KEY_APPROVAL_LINK_VALIDITY, "7"));
		cal.add(Calendar.DATE, maxDaysLinkValidity);

		String encRoles = CKEncryptionUtil.encrypt(Roles.FF_FINANCE.name() + ":" + Roles.OFFICER.name(), ckJobTruck.getJobId());
		String encJobId = CKEncryptionUtil.encrypt(ckJobTruck.getJobId(), "000");
		String encAccnId = CKEncryptionUtil.encrypt(ckJobTruck.getTCoreAccnByJobPartyTo().getAccnId(), ckJobTruck.getJobId()); // Assuming first recipient is the target
		String encValidity = CKEncryptionUtil.encrypt(sdf.format(cal.getTime()), ckJobTruck.getJobId());
		TCoreSysparam sysParam = coreSysparamDao.find("APP_HOST_URL");
		String baseUrl = sysParam.getSysVal();

        String generateTrackingUrl = baseUrl +"/clictruck/approve/billing/job/truck/:jobId/:accn/:roles/:validityDate";
        return generateTrackingUrl.replace(":jobId", encJobId)
				.replace(":accn", encAccnId)
				.replace(":roles", encRoles)
				.replace(":validityDate", encValidity);
	}

	//send whatApp via YCloud
	public void formatContent(String clasquinPhoneNum, CkJobTruck jobTruck, TCkCtTrip tCkCtTrip, String templateDesc, String approvalLink) throws Exception {
		Boolean isSubscribed = ckAccnDao.findByAccnIdSubscribed(jobTruck.getTCoreAccnByJobPartyTo().getAccnId(), RecordStatus.ACTIVE.getCode(), null);
		if (!isSubscribed) {
			log.info("This account ID not subscribed to WhatsApp: " + jobTruck.getTCoreAccnByJobPartyTo().getAccnId());
		}else {
            ArrayList<String> texts = new ArrayList<>();
            String accnName = null;
            String accnId = null;

            if (jobTruck.getTCoreAccnByJobPartyTo() != null) {
                accnName = jobTruck.getTCoreAccnByJobPartyTo().getAccnName();
                accnId = jobTruck.getTCoreAccnByJobPartyTo().getAccnId();
            }
            TCoreSysparam sysParam = coreSysparamDao.find("CLICTRUCK_CLASQUIN_MILLESIMA");
            if (clasquinPhoneNum != null && sysParam != null) {
                accnName = sysParam.getSysVal();
            }
            if (jobTruck.getTCkJob().getTCoreAccnByJobSlAccn() != null && clasquinPhoneNum != null) {
                accnName = jobTruck.getTCkJob().getTCoreAccnByJobSlAccn().getAccnName();
            }
            List<TCkCtAlert> listAlerts = findByAlert(accnId);
            for (TCkCtAlert entity : listAlerts) {
                String alertTemplateId = entity.gettCkCtMstAlert().getAltTemplateId();
                if (templateDesc.equalsIgnoreCase(NotificationTemplateName.CARGO_1_HOUR.getDesc()) &&
                        alertTemplateId.equalsIgnoreCase(NotificationTemplateName.CARGO_1_HOUR.getDesc()) ||
                        templateDesc.equalsIgnoreCase(NotificationTemplateName.CARGO_30_MIN.getDesc()) &&
                                alertTemplateId.equalsIgnoreCase(NotificationTemplateName.CARGO_30_MIN.getDesc())) {
                    texts.add(accnName != null ? accnName : "");
                    texts.add(" " + (accnName != null ? accnName : ""));
                    texts.add(jobTruck.getJobId());
                    texts.add(approvalLink != null ? approvalLink : "");
                }
            }
			log.info("Sending WhatsApp notification for trip: " + (tCkCtTrip != null ? tCkCtTrip : "null") + templateDesc);
            if (tCkCtTrip != null && tCkCtTrip.getTCkCtTripLocationByTrTo() != null && Objects.nonNull(tCkCtTrip.getTCkCtTripLocationByTrTo().getTlocMobileNo())) {
                jobDeliveryService.sendYCloudWhatAppMsg(clasquinPhoneNum,
                        tripMobileService.removeSpecialCharacters(tCkCtTrip.getTCkCtTripLocationByTrTo().getTlocMobileNo()),
                        texts, jobTruck.getJobId(), templateDesc);
            }
		}
	}

	protected String getSysParam(String key, String defValue) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam == null)
			return defValue;

		return sysParam.getSysVal();

	}
	private List<TCkCtAlert> findByAlert(String accnId) throws Exception {
		String hql = "FROM TCkCtAlert a WHERE a.altStatus = :status AND a.tCoreAccn.accnId = :accnId";
		Map<String, Object> params = new HashMap<>();
		params.put("accnId", accnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return ckCtAlertDao.getByQuery(hql, params);
	}
}
