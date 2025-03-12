package com.guudint.clickargo.clictruck.admin.contract.listener;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.dto.ContractReqStateEnum;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractReq;
import com.guudint.clickargo.common.enums.ClickargoNotifTemplates;
import com.guudint.clickargo.common.event.listener.AbstractWfPostEventListenerService;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.core.model.TCoreApps;

public class ContractRequestPostListenerService extends AbstractWfPostEventListenerService<TCkCtContractReq, CkCtContractReq> {

	private static Logger LOG = Logger.getLogger(ContractRequestPostListenerService.class);

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("coreAppsDao")
	private GenericDao<TCoreApps, String> coreAppsDao;

	private final String url = "/opadmin/contractrequest/view/";

	@Override
	public void processSubmit(CkCtContractReq dto, Principal principal) throws Exception {
		LOG.info("processSubmit");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.NEW_SUBMITTED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.NEW_CONTRACT_REQ.getId());
		} else if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.UPDATE_SUBMITTED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.CONTRACT_UPDATE_REQ.getId());
		} else if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.RENEWAL_SUBMITTED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.CONTRACT_RENEWAL_REQ.getId());
		}

		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_FIN_HD));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":toAccnName", dto.getTCoreAccnByCrTo() != null ? dto.getTCoreAccnByCrTo().getAccnName() : "-");
			contentFields.put(":coFfAccnName", dto.getTCoreAccnByCrCoFf() != null ? dto.getTCoreAccnByCrCoFf().getAccnName() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":contractReqLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getCrId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processVerify(CkCtContractReq dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void processApprove(CkCtContractReq dto, Principal principal) throws Exception {
		LOG.info("processApprove");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.NEW_APPROVED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.NEW_CONTRACT_APP.getId());
		} else if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.UPDATE_APPROVED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.CONTRACT_UPDATE_APP.getId());
		} else if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.RENEWAL_APPROVED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.CONTRACT_RENEWAL_APP.getId());
		}

		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_L1));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":toAccnName", dto.getTCoreAccnByCrTo() != null ? dto.getTCoreAccnByCrTo().getAccnName() : "-");
			contentFields.put(":coFfAccnName", dto.getTCoreAccnByCrCoFf() != null ? dto.getTCoreAccnByCrCoFf().getAccnName() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":contractReqLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getCrId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processReject(CkCtContractReq dto, Principal principal) throws Exception {
		LOG.info("processReject");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.NEW_REJECTED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.NEW_CONTRACT_REJ.getId());
		} else if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.UPDATE_REJECTED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.CONTRACT_UPDATE_REJ.getId());
		} else if(dto.getTCkCtMstContractReqState().getStId().equals(ContractReqStateEnum.RENEWAL_REJECTED.getCode())) {
			param.setTemplateId(ClickargoNotifTemplates.CONTRACT_RENEWAL_REJ.getId());
		}
		
		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_L1));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":toAccnName", dto.getTCoreAccnByCrTo() != null ? dto.getTCoreAccnByCrTo().getAccnName() : "-");
			contentFields.put(":coFfAccnName", dto.getTCoreAccnByCrCoFf() != null ? dto.getTCoreAccnByCrCoFf().getAccnName() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":contractReqLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getCrId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
	}

}
