package com.guudint.clickargo.clictruck.admin.contract.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.event.NotifyExpireEvent;
import com.guudint.clickargo.common.enums.ClickargoNotifTemplates;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreContact;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.ErrorCodes;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.entity.IEntityService;

@Component
public class NotifyExpirePostListener implements ApplicationListener<NotifyExpireEvent> {

	// Static Attributes
	/////////////////////
	private static Logger log = Logger.getLogger(NotifyExpirePostListener.class);

	@Autowired
	@Qualifier("ccmAccnService")
	protected IEntityService<TCoreAccn, String, CoreAccn> ccmAccnService;
	
	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void onApplicationEvent(NotifyExpireEvent event) {
		log.debug("onApplicationEvent");

		try {

			if (event == null)
				throw new ParameterException("param event is null");

			if (event.getContract() == null)
				throw new ParameterException("param contract is null");

			CkCtContract contract = event.getContract();
			Optional<CoreAccn> opCoreAccnCoFf = Optional.ofNullable(contract.getTCoreAccnByConCoFf());
			Optional<CoreAccn> opCoreAccnTo = Optional.ofNullable(contract.getTCoreAccnByConTo());
			CoreAccn coFfAccn = ccmAccnService.findById(opCoreAccnCoFf.get().getAccnId());
			CoreAccn toAccn = ccmAccnService.findById(opCoreAccnTo.get().getAccnId());

			Optional<CoreContact> opAccnContactCoFf = Optional.ofNullable(coFfAccn.getAccnContact());
			Optional<CoreContact> opAccnContactTo = Optional.ofNullable(toAccn.getAccnContact());
			
			// proceed only if the user has email
			if ((opAccnContactCoFf.isPresent() && StringUtils.isNotBlank(opAccnContactCoFf.get().getContactEmail()))
					&& (opAccnContactTo.isPresent() && StringUtils.isNotBlank(opAccnContactTo.get().getContactEmail()))) {
				
				NotificationParam param = new NotificationParam();
				param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
				param.setTemplateId(ClickargoNotifTemplates.CNTRCT_EXPIRING.getId());
				
				HashMap<String, String> contentFields = new HashMap<>();
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				contentFields.put(":coAccnName", contract.getTCoreAccnByConCoFf().getAccnName());
				contentFields.put(":toAccnName", contract.getTCoreAccnByConTo().getAccnName());
				contentFields.put(":conDtEnd", event.getExpiredDate());
				
				ArrayList<String> recipients = new ArrayList<>();
				recipients.add(opAccnContactCoFf.get().getContactEmail());
				recipients.add(opAccnContactTo.get().getContactEmail());
				param.setRecipients(recipients);
				param.setContentFeilds(contentFields);

				notificationUtilService.saveNotificationLog(param.toJson(), null, true);

			} else {
				log.debug("Recipients not present");
			}

		} catch (Exception ex) {
			log.error("onApplicationEvent", ex);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_UNKNOWN, ErrorCodes.MSG_GEN_UNKNOWN,
					"NotifyExpiredPostListener", ex);
		}
	}

}
