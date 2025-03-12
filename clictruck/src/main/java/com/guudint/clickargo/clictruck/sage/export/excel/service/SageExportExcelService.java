package com.guudint.clickargo.clictruck.sage.export.excel.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.sage.dao.CkSageIntegrationDao;
import com.guudint.clickargo.sage.model.TCkSageIntegration;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.model.TCoreNotificationLog;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.util.email.SysParam;

public abstract class SageExportExcelService<D extends AbstractDTO<D, ?>> {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(SageExportExcelService.class);

	@Autowired
	protected SysParam sysParam;

	@Autowired
	protected CkSageIntegrationDao sageIntegrartionDao;
	@Autowired
	protected CkNotificationUtilService notificationUtilService;
	@Autowired
	protected NotificationService notificationService;

	public static SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat jobApproveDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static SimpleDateFormat yyyy_MM_ddSDF = new SimpleDateFormat("yyyy-MM-dd");

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void exportSageExcel(Date beginDate, Date endDate) throws Exception {

		TCkSageIntegration sageIntegration = null;

		try {
			List<D> dtoList = fetchRecords(beginDate, endDate);

			byte[] excelFilebody = this.generateExcelFile(dtoList);

			String fileName = this.getFileName(beginDate, endDate);
			log.info("fileName:" + fileName);
			log.info("excelFilebody:" + excelFilebody.length);

			// String dateFolderName = sdf.format(date);

			// file path

			String parentPath = sysParam.getValString("CLICTRUCK_TO_SAGE_PATH", "/home/vcc/sage300/toSage/");
			Files.createDirectories(Paths.get(parentPath));
			
			Files.write(Paths.get(parentPath + fileName), excelFilebody);
			File file = new File(parentPath + fileName);

			sageIntegration = this.createTCkSageIntegration(beginDate, endDate, dtoList.size(), file.getAbsolutePath());

			// save to storage;
			// Don't auto push to SFTP
			/*
			// push to SFTP server CLICTRUCK_SAGE_SFTP
			String sageSftp = sysParam.getValString("CLICTRUCK_SAGE_SFTP", null);

			if (StringUtils.isNotBlank(sageSftp)) {
				SFTPConfig sftpConfig = (new ObjectMapper()).readValue(sageSftp, SFTPConfig.class);

				SFTPUtil.store(sftpConfig, Arrays.asList(file));
			}
			*/

		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			if (null != sageIntegration) {
				try {
					//sageIntegration.setSintDtExport(new Date());
					sageIntegrartionDao.saveOrUpdate(sageIntegration);
				} catch (Exception e) {
					log.error("", e);
					throw e;
				}
			}
		}
	}

	@Transactional
	public void sendEmailToFinance(String emailSubject, String emailBody) throws Exception {

		try {
			NotificationParam param = new NotificationParam();
			param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
			param.setTemplateId(ClicTruckNotifTemplates.BLANK.getId());

			String recipientStr = sysParam.getValString("CLICTRUCK_FINANCE_EMAIL_RECEIVER", "Zhang.Ji@guud.company");

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":sp_details", emailBody);

			param.setContentFeilds(contentFields);

			param.setRecipients(new ArrayList<>(Arrays.asList(recipientStr.split(","))));

			HashMap<String, String> subjectFields = new HashMap<>();
			subjectFields.put(":subject", emailSubject);
			param.setSubjectFields(subjectFields);

			TCoreNotificationLog notifLog = notificationUtilService.saveNotificationLog(param.toJson(), null, false);
			log.info("TCoreNotificationLog id: " + notifLog.getNlogId());
			notificationService.sendNotificationsByLogId(notifLog.getNlogId());
			// notificationService.notifySyn(param);

		} catch (Exception e) {
			log.error("Fail to send email." + emailSubject, e);
			throw e;
		}
	}
	
	protected abstract List<D> fetchRecords(Date beginDate, Date endDate);

	protected abstract String getFileName(Date beginDate, Date endDate);

	protected abstract byte[] generateExcelFile(List<D> dtoList) throws IOException;

	protected TCkSageIntegration createTCkSageIntegration(Date beginDate, Date endDate, int noRecords, String locExport) {
		
		TCkSageIntegration sageInte = new TCkSageIntegration();
		sageInte.setSintId(CkUtil.generateId(TCkSageIntegration.PREFIX_ID));
		
		sageInte.setSintDtStart(beginDate);
		sageInte.setSintDtEnd(endDate);
		sageInte.setSintNoRecords(noRecords);
		//sageInte.setSintNoSuccess(null);
		//sageInte.setSintNoFail(null);
		//sageInte.setSintDtExport(new Date());
		//sageInte.setSintDtImport(endDate);
		sageInte.setSintLocExport(locExport);
		//sageInte.setSintLocImport(null);
		
		sageInte.setSintStatus(Constant.ACTIVE_STATUS);
		sageInte.setSintUidCreate(Constant.ACCN_CREATE_SYS_USER);
		sageInte.setSintDtCreate(new Date());
		//sageInte.setSintDtLupd(endDate);
		//sageInte.setSintUidLupd(null);
		return sageInte;
	}
}
