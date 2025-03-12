package com.guudint.clickargo.clictruck.common.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.common.dao.CkCtAccnInqReqDocsDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq;
import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReqDocs;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.ErrorCodes;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class NotifyAccnInquiryRequestListener implements ApplicationListener<AccnInquiryEvent> {

	private static final String ACCN_INQ_TEMPLATE = "CKT_NTL_0062";
	// Static Attributes
	/////////////////////
	private static Logger log = Logger.getLogger(NotifyAccnInquiryRequestListener.class);

	@Autowired
	private CkCtAccnInqReqDocsDao accnInqReqDocsDao;

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void onApplicationEvent(AccnInquiryEvent event) {
		log.debug("onApplicationEvent");
		try {

			if (event == null)
				throw new ParameterException("param event is null");

			CkCtAccnInqReq req = event.getAccnInqReq();

			if (req == null)
				throw new ProcessingException("ckCtAccnInqReq null");

			String zipPath = "";
			// Retrieve the related docs
			List<TCkCtAccnInqReqDocs> uploadedDocs = accnInqReqDocsDao.getDocsByAccnReq(req.getAirId());
			if (uploadedDocs != null) {
				List<File> files = new ArrayList<>();
				for (TCkCtAccnInqReqDocs doc : uploadedDocs) {
					if (StringUtils.isNotBlank(doc.getAirdDoc())) {
						files.add(new File(doc.getAirdDoc()));
					}
				}

				zipPath = FileUtil.zipFiles(files, req.getAirId() + ".zip");
			}

			// do not save notification if email is not set and the zip path is empty
			if (StringUtils.isNotBlank(req.getAirEmailReq()) && StringUtils.isNotBlank(zipPath)) {
				NotificationParam param = new NotificationParam();
				param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
				param.setTemplateId(ACCN_INQ_TEMPLATE);

				HashMap<String, String> subjectField = new HashMap<>();
				subjectField.put(":subject", req.getTCoreAccn().getAccnName());
				param.setSubjectFields(subjectField);

				HashMap<String, String> contentFields = new HashMap<>();
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());

				ArrayList<String> recipient = new ArrayList<>();
				recipient.add(req.getAirEmailReq());
				param.setRecipients(recipient);

				ArrayList<String> attachment = new ArrayList<>();
				attachment.add(zipPath);
				param.setAttachments(attachment);

				notificationUtilService.saveNotificationLog(param.toJson(), null, true);

			}

		} catch (Exception ex) {
			log.error("onApplicationEvent", ex);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_UNKNOWN, ErrorCodes.MSG_GEN_UNKNOWN,
					"NotifyExpiredPostListener", ex);
		}
	}

}
