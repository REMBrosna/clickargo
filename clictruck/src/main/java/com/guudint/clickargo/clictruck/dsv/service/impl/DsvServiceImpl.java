package com.guudint.clickargo.clictruck.dsv.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dsv.edi.xml.DSV_ShipmentMessage_v1.DSVShipmentMessage;
import com.guudint.clickargo.clictruck.dsv.dao.CkCtShipmentDao;
import com.guudint.clickargo.clictruck.dsv.dto.DsvMstShipmentStateEnum;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtMstShipmentState;
import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.clictruck.dsv.service.IDsvService;
import com.guudint.clickargo.clictruck.dsv.service.IPodService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.util.CkXmlUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.job.model.TCkJob;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.SFTPUtil;
import com.vcc.camelone.util.sftp.model.SFTPConfig;

@Service
public class DsvServiceImpl implements IDsvService {

	private static Logger log = Logger.getLogger(DsvServiceImpl.class);

	// private static SimpleDateFormat yyyyMMddSDF = new
	// SimpleDateFormat("yyyyMMdd");

	@Autowired
	private CkCtShipmentDao shipmentDao;
	@Autowired
	private DsvShipmentService dsvShipmentService;
	@Autowired
	private CkJobTruckExtDao ckJobTruckExtDao;
	@Autowired
	private DsvPodService podService;
	@Autowired
	private SysParam sysParam;
	@Autowired
	@Qualifier("podDsvService")
	private IPodService podDsvService;
	@Autowired
	private CkJobTruckDao ckJobTruckDao;

	@Override
	public List<File> loadFilesFromSftp(SFTPConfig sftpConfig, String parentPath) throws Exception {

		List<File> importFiles = new ArrayList<>();

		// Max download 50 files
		int maxDownloadFiles = sysParam.getValInteger("CLICTRUCK_DSV_MAX_DOWNLOAD_FILES", 50);
		DsvUtilService.getSftpFiles(sftpConfig, new File(parentPath), importFiles, maxDownloadFiles);

		return importFiles;
	}

	@Override
	// not Rollback for
	// @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {
	// Exception.class })
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void processDsvFile(File xmlFile) throws Exception {

		if (xmlFile == null || !xmlFile.exists()) {
			log.error("Parameter xmlFiel is null or not exists.");
			throw new ParameterException("Parameter xmlFiel is null or not exists.");
		}

		// 1: validate XML file
		// boolean isValidate = this.validateInputXml(xmlFile);
		// log.info("isValidate: " + xmlFile.getAbsolutePath() + " " + isValidate);

		Optional<DSVShipmentMessage> optMsg = null;
		DSVShipmentMessage shipMsg = null;
		TCkCtShipment tCkCtDsvShipment = new TCkCtShipment(
				CkUtil.generateId(TCkCtShipment.PREFIX_ID) + ThreadLocalRandom.current().nextInt(0, 99999 + 1));
		tCkCtDsvShipment.setShName(xmlFile.getName()); // xml file name
		tCkCtDsvShipment.setShDtLoad(new Date());
		tCkCtDsvShipment.setShContent(FileUtils.readFileToString(xmlFile, StandardCharsets.UTF_8));
		tCkCtDsvShipment.setShSourcePath(xmlFile.getAbsolutePath());
		tCkCtDsvShipment.setShContentSize((int) xmlFile.length());
		tCkCtDsvShipment.setShMsgVer((short) 0);

		try {

			// 1: validation and unmarshal to Object
			try {
				optMsg = new CkXmlUtil().unmarshal(xmlFile);

				if (!optMsg.isPresent()) {
					throw new JAXBException("unmarshall failed!");
				}

				if (!optMsg.map(m -> m.getHeader()).map(h -> h.getMessageId()).isPresent()
						|| StringUtils.isBlank(optMsg.get().getHeader().getMessageId())) {
					throw new JAXBException("Header or Message Id is null");
				}

				//
				shipMsg = optMsg.get();
				tCkCtDsvShipment.setShMsgId(shipMsg.getHeader().getMessageId());
				tCkCtDsvShipment.setShDtIssue(dsvShipmentService.getDocumentDate(shipMsg));

			} catch (IOException | JAXBException | XMLStreamException e) {
				String errorMsg = "Fail to unmarshal.";
				log.error(errorMsg, e);
				tCkCtDsvShipment.setTCkCtMstShipmentState(
						new TCkCtMstShipmentState(DsvMstShipmentStateEnum.CONTENT_ERROR.name()));
				tCkCtDsvShipment.setShRemark(errorMsg + " " + e.getMessage());
				throw new Exception(errorMsg, e);
			}

			// 2: Is ignore the shipment XML file?
			try {

				String msg = dsvShipmentService.isIgnoreShipment(shipMsg);

				if (StringUtils.isNoneBlank(msg)) {
					// Ignore the shipment XML file, Need NOT create job
					tCkCtDsvShipment.setShRemark(msg);
					tCkCtDsvShipment.setTCkCtMstShipmentState(
							new TCkCtMstShipmentState(DsvMstShipmentStateEnum.NEED_NOT_CREATE_JOB.name()));
					return;
				}
				
				msg = dsvShipmentService.isIgnoreShipmentWhenMigration(shipMsg);

				if (StringUtils.isNoneBlank(msg)) {
					// Ignore the shipment XML file, Need NOT create job when do migration
					tCkCtDsvShipment.setShRemark(msg);
					tCkCtDsvShipment.setTCkCtMstShipmentState(
							new TCkCtMstShipmentState(DsvMstShipmentStateEnum.NEED_NOT_CREATE_JOB_MIGRATION.name()));
					return;
				}
			} catch (Exception e) {
				String errorMsg = "Fail to call isCreteJob() ";
				log.error(errorMsg, e);
				throw e;
			}

			// 3: prepare TCkJobTruckExt;
			// Map<String, String> msgMap = new CkXmlUtil().xmlElements2Map(xmlFile);
			Map<String, String> msgMap = new HashMap<>();
			try {
				msgMap = podService.convert2Map(shipMsg);
			} catch (Exception e) {
				throw new Exception("Fail to convert XML to key value pair: ", e);
			}

			// 4: Is Update Job ?
			TCkJobTruck jobTruck = null;
			TCkCtShipment replacedShipment = dsvShipmentService.isUpdateJob(tCkCtDsvShipment.getShMsgId());

			// 5: Create or Update job.
			if (replacedShipment == null) {
				// 5.1: Create Job
				jobTruck = dsvShipmentService.createJob(shipMsg);

				tCkCtDsvShipment.setTCkCtMstShipmentState(
						new TCkCtMstShipmentState(DsvMstShipmentStateEnum.JOB_CREATED.name()));

			} else {
				TCkJob replacedJob = replacedShipment.getTCkJob();
				// 5.2: Update Job
				jobTruck = dsvShipmentService.updateJob(shipMsg, replacedJob.getJobId(), 1);
				// delete JobTruckTxt by jobId
				ckJobTruckExtDao.deleteByJobId(jobTruck.getJobId());

				tCkCtDsvShipment.setTCkCtMstShipmentState(
						new TCkCtMstShipmentState(DsvMstShipmentStateEnum.JOB_UPDATED.name()));

				tCkCtDsvShipment.setShMsgVer(replacedShipment.getShMsgVer() + 1);

				this.updateShipment2ReplaceStaus(replacedShipment, 'Y');

			}
			tCkCtDsvShipment.setShDirection(dsvShipmentService.getShipmentType(shipMsg).name()); // Import or Export
			tCkCtDsvShipment.setShHawb(msgMap.get("houseBillValue"));
			tCkCtDsvShipment.setShMawb(msgMap.get("masterBillValue"));
			tCkCtDsvShipment.setTCkJob(jobTruck.getTCkJob());

			// 6: save to TCkJobTruckExt to generate ePOD.pdf file
			String jobTruckId = jobTruck.getJobId();

			if (null != msgMap) {
				this.save2JobTruckExt(msgMap, jobTruckId, "GLI");
			}

		} catch (Exception e) {
			log.error("", e);

			if (StringUtils.isBlank(tCkCtDsvShipment.getShRemark())) {
				tCkCtDsvShipment.setShRemark(e.getMessage());
			}

			if (tCkCtDsvShipment.getTCkCtMstShipmentState() == null
					|| StringUtils.isBlank(tCkCtDsvShipment.getTCkCtMstShipmentState().getStId())) {

				tCkCtDsvShipment.setTCkCtMstShipmentState(
						new TCkCtMstShipmentState(DsvMstShipmentStateEnum.FAIL_TO_CREATE_JOB.name()));
			}
			// throw Exception, so don't delete files
			throw e;

		} finally {
			tCkCtDsvShipment.setShDtJob(new Date());
			tCkCtDsvShipment.setShDtCreate(new Date());
			tCkCtDsvShipment.setShUidCreate("sys");
			tCkCtDsvShipment.setShStatus(Constant.ACTIVE_STATUS);

			// maybe throw exception if failed to add
			shipmentDao.add(tCkCtDsvShipment);
		}
	}


	public void rmFileFromSftp(SFTPConfig sftpConfig, File file) throws Exception {

		List<String> fileNameList = new ArrayList<>();
		fileNameList.add(file.getName());
		// remove from output path
		SFTPUtil.rm(sftpConfig, fileNameList);
	}

	@Override
	public void mvFile2HisotoryFolder(SFTPConfig sftpConfig, File file) throws Exception {

		List<String> fileNameList = new ArrayList<>();
		fileNameList.add(file.getName());
		// remove from output path
		String hisotryFolderName = sysParam.getValString("CLICTRUCK_DSVAS_HISTORY_FOLDER", "history");
		DsvUtilService.moveFileFromOut2HistoryFolder(sftpConfig, fileNameList, hisotryFolderName);
	}


	private void save2JobTruckExt(Map<String, String> msgMap, String jobTruckId, String accnId) throws Exception {

		if (msgMap == null || msgMap.size() == 0) {
			return;
		}
		Date now = new Date();
		TCoreAccn accn = new TCoreAccn();
		accn.setAccnId(accnId);

		TCkJobTruck jobTruck = new TCkJobTruck(jobTruckId, null);

		for (Map.Entry<String, String> entry : msgMap.entrySet()) {
			int ranNo = ThreadLocalRandom.current().nextInt(0, 99999 + 1);
			TCkJobTruckExt truckExt = new TCkJobTruckExt(CkUtil.generateId(TCkJobTruckExt.PREFIX_ID) + ranNo,
					entry.getKey(), entry.getValue());

			truckExt.setTCkJobTruck(jobTruck);
			truckExt.setTCoreAccn(accn);
			truckExt.setJextStatus(Constant.ACTIVE_STATUS);
			truckExt.setJextDtCreate(now);
			truckExt.setJextUidCreate("sys");

			ckJobTruckExtDao.add(truckExt);
		}
	}

	@SuppressWarnings("unused")
	private boolean validateInputXml(File xmlFile) {
		ClassLoader classLoader = getClass().getClassLoader();
		File xsdFile = new File(classLoader.getResource("/xsd/dsv/DSV_ShipmentMessage_v1.xsd").getFile());

		try {
			return CkXmlUtil.validateXMLSchema(xsdFile, xmlFile);
		} catch (Exception e) {
			log.error("", e);
			return false;
		}
	}


	private void updateShipment2ReplaceStaus(TCkCtShipment replacedShipment, char replacedStatus) throws Exception {

		replacedShipment.setShReplaced(replacedStatus);
		replacedShipment.setShDtLupd(new Date());
		shipmentDao.saveOrUpdate(replacedShipment);
	}

	/*-
		public DSVShipmentMessage unmarshal(File xmlFile) throws Exception {
			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(DSVShipmentMessage.class);
				//jaxbContext = JAXBContext.newInstance("com.dsv.edi.xml.dsv_shipmentmessage_v1");
				//jaxbContext = JAXBContext.newInstance(com.dsv.edi.xml.Header.class, DSVShipmentMessage.class);
	
				//jaxbContext = JAXBContext.newInstance("com.dsv.edi.xml.dsv_shipmentmessage_v1:com.dsv.edi.xml.sharedelements_v1");
				
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	
				DSVShipmentMessage msg = (DSVShipmentMessage) jaxbUnmarshaller.unmarshal(xmlFile);
				//DSVShipmentMessage msg = (DSVShipmentMessage) jaxbUnmarshaller.unmarshal(doc);
				//jaxbUnmarshaller.unmarshal(xmlFile);
				//JAXBElement<DSVShipmentMessage> jaxbElement = (JAXBElement<DSVShipmentMessage>)jaxbUnmarshaller.unmarshal(xmlFile);
				
				//return jaxbElement.getValue();
				return msg;
			} catch (Exception e) {
				log.error("", e);
				throw e;
			}
			//return null;
		}
	*/
	
	public void auxiliaryRefreshJobTruckExt(String shId) throws Exception {

		TCkCtShipment shipment = shipmentDao.find(shId);
		DSVShipmentMessage shipMsg = new CkXmlUtil().unmarshal(new File(shipment.getShSourcePath())).get();

		Map<String, String> msgMap = podService.convert2Map(shipMsg);

		String parentId = shipment.getTCkJob().getJobId();
		List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByParentId(parentId);
		String jobTruckId = jobTruckList.get(0).getJobId();
		
		// delete JobTruckTxt by jobId
		ckJobTruckExtDao.deleteByJobId(jobTruckId);
		//
		this.save2JobTruckExt(msgMap, jobTruckId, "GLI");
	}


	public static void main(String[] argv) throws Exception {

	}

	public static void main1(String[] argv) throws Exception {
		String xmlFile = "/Users/zhangji/home/vcc/thirdParty/dsv/20231115/DSV_GUUDEDIID_SHIPMENT_20220604_1155_4872276774.xml";
		File file = new File(xmlFile);
		String fileBody = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		System.out.print(fileBody);

		DSVShipmentMessage a = new CkXmlUtil().unmarshal(file)
				.orElseThrow(() -> new JAXBException("unmarshall failed!"));

		// DSVShipmentMessage a = (DSVShipmentMessage)XmlUtil.xml2Obj(fileBody,
		// DSVShipmentMessage.class, com.dsv.edi.xml.sharedelements_v1.Header.class);

		System.out.print(a);
	}

}
