package com.guudint.clickargo.clictruck.sage.export.excel.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.sage.dao.CkSageIntegrationDao;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntStateEnum;
import com.guudint.clickargo.sage.model.TCkCtMstSageIntState;
import com.guudint.clickargo.sage.model.TCkSageIntegration;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.model.TCoreNotificationLog;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.util.email.SysParam;
import com.vcc.camelone.util.sftp.SFTPUtil;
import com.vcc.camelone.util.sftp.model.SFTPConfig;

@Service
public class SageIntegrationService {

	private static Logger LOG = Logger.getLogger(SageIntegrationService.class);

	@Autowired
	protected SysParam sysParam;
	@Autowired
	protected CkSageIntegrationDao sageIntegrartionDao;
	@Autowired
	protected CkNotificationUtilService notificationUtilService;
	@Autowired
	protected NotificationService notificationService;

	/**
	 * GLI approve
	 * 
	 * @param sageId
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public TCkSageIntegration doApprove(String sageId, String userId) throws Exception {

		TCkSageIntegration sageIntegration = sageIntegrartionDao.find(sageId);

		if (sageIntegration == null) {
			throw new Exception("Fail to find Sage by: " + sageId);
		}

		if (!CkCtMstSageIntStateEnum.SUBMITTED.name()
				.equalsIgnoreCase(sageIntegration.getTCkCtMstSageIntState().getSisId())) {
			LOG.error("Should be SUBMITTED status, but now it is "
					+ sageIntegration.getTCkCtMstSageIntState().getSisId());
			throw new Exception("Sage status is not correct!");
		}

		// push file to SFTP
		// push to SFTP server CLICTRUCK_SAGE_SFTP
		this.storeFile2SFTP(sageIntegration);
		
		// send email to Herry
		this.sendEmailToSageConsult(sageIntegration);

		sageIntegration.setTCkCtMstSageIntState(new TCkCtMstSageIntState(CkCtMstSageIntStateEnum.APPROVE.name()));
		sageIntegration.setSintUidLupd(userId);
		sageIntegration.setSintDtLupd(new Date());

		return sageIntegration;
	}

	@Transactional
	public TCkSageIntegration doAcknowledge(String sageId, String userId) throws Exception {

		TCkSageIntegration sageIntegration = sageIntegrartionDao.find(sageId);

		if (sageIntegration == null) {
			throw new Exception("Fail to find Sage by: " + sageId);
		}

		if (!CkCtMstSageIntStateEnum.ERROR.name()
				.equalsIgnoreCase(sageIntegration.getTCkCtMstSageIntState().getSisId())) {
			LOG.error("Should be ERROR status, but now it is " + sageIntegration.getTCkCtMstSageIntState().getSisId());
			throw new Exception("Sage status is not correct!");
		}

		// push file to SFTP

		sageIntegration.setTCkCtMstSageIntState(new TCkCtMstSageIntState(CkCtMstSageIntStateEnum.APPROVE.name()));
		sageIntegration.setSintUidLupd(userId);
		sageIntegration.setSintDtLupd(new Date());

		return sageIntegration;
	}

	private void storeFile2SFTP(TCkSageIntegration sageIntegration) throws JsonParseException, JsonMappingException, IOException {
		String sageSftp = sysParam.getValString("CLICTRUCK_SAGE_SFTP", null);

		if (StringUtils.isNotBlank(sageSftp)) {
			SFTPConfig sftpConfig = (new ObjectMapper()).readValue(sageSftp, SFTPConfig.class);

			SFTPUtil.store(sftpConfig, Arrays.asList(new File(sageIntegration.getSintLocExport())));
		}
	}
	

	@Transactional
	public void sendEmailToSageConsult(TCkSageIntegration sageIntegration) throws Exception {

		String bDate = SageExportExcelService.yyyyMMddSDF.format(sageIntegration.getSintDtStart());
		String eDate = SageExportExcelService.yyyyMMddSDF.format(sageIntegration.getSintDtEnd());
		
		String emailSubject = "Please check Sage Excel files [" + bDate + "-" + eDate + "]";
		String emailBody = emailSubject;

		try {
			NotificationParam param = new NotificationParam();
			param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
			param.setTemplateId(ClicTruckNotifTemplates.BLANK.getId());

			String recipientStr = sysParam.getValString("CLICTRUCK_SAGE_CONSULT_EMAIL", "Zhang.Ji@guud.company");

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":sp_details", emailBody);

			param.setContentFeilds(contentFields);

			param.setRecipients(new ArrayList<>(Arrays.asList(recipientStr.split(","))));

			HashMap<String, String> subjectFields = new HashMap<>();
			subjectFields.put(":subject", emailSubject);
			param.setSubjectFields(subjectFields);

			param.setAttachments(new ArrayList<>(Arrays.asList(sageIntegration.getSintLocExport())));

			TCoreNotificationLog notifLog = notificationUtilService.saveNotificationLog(param.toJson(), null, true);
			
			LOG.info("TCoreNotificationLog id: " + notifLog.getNlogId());
			//notificationService.sendNotificationsByLogId(notifLog.getNlogId());
			// notificationService.notifySyn(param);

		} catch (Exception e) {
			LOG.error("Fail to send email." + emailSubject, e);
			throw e;
		}
	}
}
