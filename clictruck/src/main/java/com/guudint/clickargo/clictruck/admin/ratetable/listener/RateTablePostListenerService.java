package com.guudint.clickargo.clictruck.admin.ratetable.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.common.enums.ClickargoNotifTemplates;
import com.guudint.clickargo.common.event.listener.AbstractWfPostEventListenerService;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.core.model.TCoreApps;

public class RateTablePostListenerService extends AbstractWfPostEventListenerService<TCkCtRateTable, CkCtRateTable> {

	private static Logger LOG = Logger.getLogger(RateTablePostListenerService.class);

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("coreAppsDao")
	private GenericDao<TCoreApps, String> coreAppsDao;

	private final String urlRateTableAdministration = "/administrations/rateTable-management/view/";
	private final String urlRateTableManagement = "/manage/ratetable-details/view/";

	@Override
	public void processSubmit(CkCtRateTable dto, Principal principal) throws Exception {
		LOG.info("processSubmit");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.RATE_TABLE_SUB.getId());
		
		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_L1));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":rateTableReqId", dto.getRtId() != null ? dto.getRtId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":rateTableReqLink", apps != null ? apps.getAppsLaunchUrl() + urlRateTableManagement + dto.getRtId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processVerify(CkCtRateTable dto, Principal principal) throws Exception {
		LOG.info("processVerify");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.RATE_TABLE_VER.getId());
		
		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_FIN_HD));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":rateTableReqId", dto.getRtId() != null ? dto.getRtId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":rateTableReqLink", apps != null ? apps.getAppsLaunchUrl() + urlRateTableManagement + dto.getRtId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processApprove(CkCtRateTable dto, Principal principal) throws Exception {
		LOG.info("processApprove");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.RATE_TABLE_APP.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opTOAccn = Optional.ofNullable(dto.getTCoreAccnByRtCompany());
			if (opTOAccn.isPresent() && StringUtils.isNotBlank(opTOAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opTOAccn.get().getAccnId(), Roles.OFFICER));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":rateTableReqId", dto.getRtId() != null ? dto.getRtId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":rateTableReqLink", apps != null ? apps.getAppsLaunchUrl() + urlRateTableAdministration + dto.getRtId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processReject(CkCtRateTable dto, Principal principal) throws Exception {
		LOG.info("processReject");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.RATE_TABLE_REJ.getId());
		
		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opTOAccn = Optional.ofNullable(dto.getTCoreAccnByRtCompany());
			if (opTOAccn.isPresent() && StringUtils.isNotBlank(opTOAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opTOAccn.get().getAccnId(), Roles.OFFICER));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":rateTableReqId", dto.getRtId() != null ? dto.getRtId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":rateTableReqLink", apps != null ? apps.getAppsLaunchUrl() + urlRateTableAdministration + dto.getRtId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
	}

}
