package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dsv.edi.xml.DSV_StatusMessage_v1.DSVStatusMessage;
import com.guudint.clicdo.common.service.CkCtCommonService;
import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;

import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;

import com.guudint.clickargo.clictruck.dsv.service.IPodService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDoAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.guudint.clickargo.job.dao.CkJobDao;
import com.guudint.clickargo.job.model.TCkJob;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.SFTPUtil;
import com.vcc.camelone.util.sftp.model.SFTPConfig;
import com.vcc.camelone.util.xml.XmlUtil;

@Service
public class DSVProcesAfterDeliverdService {

	private static Logger log = Logger.getLogger(DSVProcesAfterDeliverdService.class);

	private static SimpleDateFormat yyyyMMdd_HHmmssSDF = new SimpleDateFormat("yyyyMMdd_HHmmss");

	@Autowired
	private CkCtShipmentDao shipmentDao;
	@Autowired
	private DsvShipmentService dsvShipmentService;
	@Autowired
	private CkJobDao ckJobDao;
	@Autowired
	private CkJobTruckDao ckJobTruckDao;
	@Autowired
	private CkJobTruckExtDao ckJobTruckExtDao;
	@Autowired
	private DsvPodService podService;
	@Autowired
	private SysParam sysParam;
	@Autowired
	private DsvJobPhotoService dsvJobPhotoService;
	@Autowired
	private DsvStatusMessageService dsvStatusMessageService;
	@Autowired
	private CkCtCommonService ckCtCommonService;
	@Autowired
	private CkCtTripDoAttachDao tripDoAttachDao;
	@Autowired
	@Qualifier("podDsvService")
	private IPodService podDsvService;
	@Autowired
	private DsvJobPhotoService jobPhotoService;
	@Autowired
	private DsvShipmentDaoService shipmentDaoService;
	@Autowired
	private DsvUtilService dsvUtilService;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void afterDsvJobIsDelivered(String shId) throws Exception {

		try {
			// 0: Is this DSV job?
			this.validDsvJob(shId);

			// 1: Upload stateMessage.xml to SFTP
			this.processStatusMessage(shId);

			// 2: Send ePOD PDF file email to DSV
			this.processEpod(shId);

			// 3: Send photo PDF file email to DSV
			this.processPhoto(shId);

		} catch (Exception e) {
			log.error("Fail to process delivery job, shID: " + shId, e);
			throw e;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void processStatusMessage(String shId) throws Exception {

		log.info("shId " + shId);

		try {
			this.generateStatusXMLfile(shId);
			this.pushStatusXMLfile2SFTP(shId);

		} catch (Exception e) {
			shipmentDaoService.updateStatusMsgRemark(shId, e.getMessage());
			throw e;
		}
	}

	public String generateStatusXMLfile(String shId) throws Exception {

		log.info("shId " + shId);

		try {
			// Prepare data;
			TCkCtShipment shipment = shipmentDao.find(shId);
			String parentId = shipment.getTCkJob().getJobId();
			List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByParentId(parentId);
			String jobTruckId = jobTruckList.get(0).getJobId();

			TCkJob ckJob = ckJobDao.find(parentId);

			List<TCkJobTruckExt> extList = ckJobTruckExtDao.findAllByJobId(parentId);

			// 1: Generate DSVStatusMessage Object
			Date deliverDate = new Date();

			if (ckJob != null && ckJob.getTCkRecordDate() != null
					&& ckJob.getTCkRecordDate().getRcdDtComplete() != null) {
				deliverDate = ckJob.getTCkRecordDate().getRcdDtComplete();
			}

			DSVStatusMessage statusMsg = dsvStatusMessageService.generateStatusMessage(parentId, deliverDate, extList);

			String statusMsgStr = XmlUtil.obj2Xml(statusMsg, DSVStatusMessage.class);

			// 2: File path and name
			String parentPath = ckCtCommonService.getCkCtAttachmentPathJob(jobTruckId, true);
			String fileName = get2DsvStatueMessageFileName(statusMsg.getHeader().getMessageId(), deliverDate,
					shipment.getShDirection());
			String absoluteFilePath = parentPath + File.separator + fileName;

			// 3: Save File
			FileUtils.writeStringToFile(new File(absoluteFilePath), statusMsgStr, Charset.forName("UTF-8"));

			// 4: update db;
			shipmentDaoService.updateStatusMsgPath(shId, absoluteFilePath);

			return absoluteFilePath;

		} catch (Exception e) {
			log.error("Fail to generate or upload StateMessage.xml", e);
			throw new Exception("Fail to generate StateMessage.xml, shId: " + shId, e);
		}
	}

	public void pushStatusXMLfile2SFTP(String shId) throws Exception {

		log.info("shId " + shId);

		try {
			TCkCtShipment shipment = shipmentDao.find(shId);
			String statusMessagePath = shipment.getShStatusmessagePath();

			List<File> fileList = Arrays.asList(new File(statusMessagePath));

			SFTPConfig sftpConfig = dsvShipmentService.getDsvSftpConfig();

			SFTPUtil.store(sftpConfig, fileList);

			shipmentDaoService.updatePushStatusMsg2SFTP(shId);
		} catch (Exception e) {
			log.error("Fail to upload StateMessage.xml to SFTP", e);
			throw new Exception("Fail to generate or upload StateMessage.xml, shId: " + shId, e);
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void processEpod(String shId) throws Exception {

		log.info("shId " + shId);

		try {
			this.createEpodFile(shId);
			this.sendEpodEmail(shId);

		} catch (Exception e) {
			shipmentDaoService.updateEpodRemark(shId, e.getMessage());
			throw e;
		}
	}

	public void createEpodFile(String shId) throws Exception {

		log.info("shId " + shId);

		try {

			TCkCtShipment shipment = shipmentDao.find(shId);
			String parentId = shipment.getTCkJob().getJobId();
			List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByParentId(parentId);
			String jobTruckId = jobTruckList.get(0).getJobId();

			// StrckJobTruckPod = podService.createPod(parentId); // create row in table

			List<TCkCtTripDoAttach> doAttachList = tripDoAttachDao.findByJobId(jobTruckId);
			TCkCtTripDoAttach doAttach = null;
			if (doAttachList == null || doAttachList.size() == 0) {
				// process via mobile
				String epodPdfFile = podService.generatePODfile(parentId); // create epod PDF file

				doAttach = podService.createTripDoAttach(parentId, epodPdfFile);

			} else {
				// TO officer uploaded DO file.
				doAttach = doAttachList.get(0);

				// need to copy and rename file name.
				String podFilePath = podDsvService.generatePodFileName(jobTruckList.get(0), null);

				// copy file
				Files.copy(Paths.get(doAttach.getDoaLoc()), Paths.get(podFilePath),
						StandardCopyOption.REPLACE_EXISTING);
				doAttach.setDoaLoc(podFilePath);
				tripDoAttachDao.saveOrUpdate(doAttach);
			}
			shipmentDaoService.updateEpodFilePath(shId, doAttach.getDoaLoc());

		} catch (Exception e) {
			log.error("Fail to create ePOD, shId: ", e);
			throw new Exception("Fail to create ePOD, shId: " + shId, e);
		}
	}

	public void sendEpodEmail(String shId) throws Exception {

		log.info("shId " + shId);
		try {
			TCkCtShipment shipment = shipmentDao.find(shId);
			String parentId = shipment.getTCkJob().getJobId();
			// TCkJobTruckPod ckJobTruckPod = ckJobTruckPodDao.find(parentId);

			List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByParentId(parentId);
			String jobTruckId = jobTruckList.get(0).getJobId();
			List<TCkCtTripDoAttach> doAttachList = tripDoAttachDao.findByJobId(jobTruckId);

			if (doAttachList != null && doAttachList.size() > 0) {
				for(TCkCtTripDoAttach doAttach: doAttachList) {
					if(StringUtils.isNoneBlank(doAttach.getDoaLoc()) ) {
						podService.sendPodEmail(parentId, doAttach.getDoaLoc());
						shipmentDaoService.updateSendEpodEmailTime(shId);
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("Fail to send epod email, shId: " + shId, e);
			throw new Exception("Fail to send epod email, shId: " + shId, e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void processPhoto(String shId) throws Exception {

		log.info("shId " + shId);

		try {
			this.createPhotoFile(shId);
			this.sendPhotoEmail(shId);

		} catch (Exception e) {
			shipmentDaoService.updatePhotoRemark(shId, e.getMessage());
			throw e;
		}
	}

	public void createPhotoFile(String shId) throws Exception {

		log.info("shId " + shId);

		try {

			TCkCtShipment shipment = shipmentDao.find(shId);
			String parentId = shipment.getTCkJob().getJobId();
			List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByParentId(parentId);
			String jobTruckId = jobTruckList.get(0).getJobId();

			// merge image to PDF file
			List<TCkCtTripAttach> tripAttachList = dsvJobPhotoService.listImages(jobTruckId);

			if (tripAttachList == null || tripAttachList.size() == 0) {
				throw new Exception("Images is null or empty.");
			}

			List<String> imageList = tripAttachList.stream().map(TCkCtTripAttach::getAtLoc)
					.collect(Collectors.toList());

			String phonePdfFile = dsvJobPhotoService.mergeImage2Pdf(imageList, jobTruckList.get(0),
					shipment.getShMsgId());

			shipmentDaoService.updatePhotoPath(shId, phonePdfFile);

		} catch (Exception e) {
			log.error("Fail to create photo, shId: ", e);
			throw new Exception("Fail to create photo, shId: " + shId, e);
		}
	}

	public void sendPhotoEmail(String shId) throws Exception {

		log.info("shId " + shId);

		try {

			TCkCtShipment shipment = shipmentDao.find(shId);

			dsvUtilService.sendDSVEmailNotification(shipment.getShPhotoPath(), shipment.getShMsgId(),
					DsvPodService.DSV_EMAIL_NOTIFY_TYPE_PHO);
			shipment.setShDtPhotoEmailNotify(new Date());

			shipmentDaoService.updateSendPhotoEmailTime(shId);

		} catch (Exception e) {
			log.error("Fail to send photo email, shId: " + shId, e);
			throw new Exception("Fail to send photo email, shId: " + shId, e);
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public int patchPushStatusMessage2SFTP() throws Exception {

		List<TCkCtShipment> shipmentList = shipmentDao.fetchDtStatusMessagePush2SftpIsNullAndStatusMessageIsNotNull();

		if (shipmentList != null && shipmentList.size() > 0) {
			for (TCkCtShipment shipment : shipmentList) {
				this.pushStatusXMLfile2SFTP(shipment.getShId());
			}
			return shipmentList.size();
		}
		return 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void validDsvJob(String shId) throws Exception {

		TCkCtShipment shipment = null;
		TCkJobTruck ckJobTruck = null;
		String jobTruckId = null;

		try {
			shipment = shipmentDao.find(shId);

			List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByParentId(shipment.getTCkJob().getJobId());

			if (jobTruckList != null && jobTruckList.size() > 0) {
				ckJobTruck = jobTruckList.get(0);
				jobTruckId = ckJobTruck.getJobId();
			} else {
				throw new ProcessingException(String.format("Fail to find job by shId: %s ", shId));
			}

			// DSV air/sea
			String dsvasAccnId = sysParam.getValString("CLICTRUCK_DSVAS_ACCN_ID", "DSVAS");

			if (!dsvasAccnId.equalsIgnoreCase(ckJobTruck.getTCoreAccnByJobPartyCoFf().getAccnId())) {
				// not DSV job
				Log.info(ckJobTruck + " not DSV air/sea job");
				throw new ProcessingException(String.format("jobTruckId: %s account is %s, is not DSV acount: %s ",
						jobTruckId, ckJobTruck.getTCoreAccnByJobPartyCoFf().getAccnId(), dsvasAccnId));
			}
		} catch (Exception e) {
			shipmentDaoService.updateStatusMsgRemark(shId, e.getMessage());
			throw e;
		}
	}

	private String get2DsvStatueMessageFileName(String messageId, Date date, String shipDirection) {
		// DSV_StatusMessage_SZAV0227690_20231120193644.xml
		// If it's export, it's POP. If it's import, it's POD
		return String.format("%s_%s_%s.xml", yyyyMMdd_HHmmssSDF.format(date), messageId,
				"Import".equalsIgnoreCase(shipDirection) ? "POD" : "POP");
	}

	/**
	 * 
	 * @param doAttachFile
	 * @param epodFile
	 * @throws IOException
	 */
	private void copyEpodFromDoAttach(String doAttachFile, String epodFile) throws IOException {

		if (doAttachFile.toUpperCase().endsWith("PDF")) {
			// need to copy and rename file name.
			Files.copy(new File(doAttachFile).toPath(), new File(epodFile).toPath());
		} else {
			// if images, need to merge to 1 pdf files
			jobPhotoService.mergeImage2Pdf(Arrays.asList(doAttachFile), epodFile);
		}

	}

}
