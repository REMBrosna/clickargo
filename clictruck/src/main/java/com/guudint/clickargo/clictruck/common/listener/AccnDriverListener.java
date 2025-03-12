package com.guudint.clickargo.clictruck.common.listener;

import com.guudint.clickargo.admin.dto.ClickargoNotifTemplates;
import com.guudint.clickargo.admin.event.ClickargoPostUserUpdateEvent;
import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.NotificationTemplateName;
import com.guudint.clickargo.clictruck.common.event.AccnDriverEvent;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.ccm.dto.CoreContact;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.ErrorCodes;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.core.model.TCoreApps;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Component
public class AccnDriverListener implements ApplicationListener<AccnDriverEvent> {

	// Static Attributes
	/////////////////////
	private static Logger log = Logger.getLogger(AccnDriverListener.class);

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void onApplicationEvent(AccnDriverEvent event) {
		log.debug("onApplicationEvent");

		try {

			if (event == null)
				throw new ParameterException("param event null");

			Optional<TCkCtDrv> opUsrContact = Optional.ofNullable(event.getDrv());
			// proceed only if the user has email
			if (opUsrContact.isPresent() && StringUtils.isNotBlank(opUsrContact.get().getDrvEmail())) {
				NotificationParam param = new NotificationParam();
				param.setAppsCode(ServiceTypes.CLICKARGO.getAppsCode());
				HashMap<String, String> contentFields = new HashMap<>();
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());

				switch (event.getAccnDrvEventAction()) {
					case NEW_DRIVER: {
						param.setTemplateId(NotificationTemplateName.E_NEW_DRIVER.getDesc());
						contentFields.put(":userId", event.getDrv().getDrvMobileId());
						contentFields.put(":password", event.getGeneratePwd());
						break;
					}
					case FORGOT_PASSWORD: {
						param.setTemplateId(NotificationTemplateName.E_FORGOT_PASSWORD.getDesc());
						contentFields.put(":userId", event.getDrv().getDrvMobileId());
						contentFields.put(":password", event.getGeneratePwd());
						break;
					}
					case RESET_PASSWORD: {
						param.setTemplateId(NotificationTemplateName.E_RESET_PASSWORD.getDesc());
						contentFields.put(":userId", event.getDrv().getDrvMobileId());
						contentFields.put(":password", event.getGeneratePwd());
						break;
					}
					default:
						break;
				}

				ArrayList<String> recipients = new ArrayList<>();
				recipients.add(opUsrContact.get().getDrvEmail());
				param.setRecipients(recipients);
				param.setContentFeilds(contentFields);

				notificationUtilService.saveNotificationLog(param.toJson(), null, true);

			}

		} catch (Exception ex) {
			log.error("onApplicationEvent", ex);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_UNKNOWN, ErrorCodes.MSG_GEN_UNKNOWN,
					"accnDriverListener", ex);
		}
	}
}
