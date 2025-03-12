package com.guudint.clickargo.clictruck.planexec.job.event.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTerm;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTermReq;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTermReq;
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

public class JobTermPostListenerService extends AbstractWfPostEventListenerService<TCkCtJobTermReq, CkCtJobTermReq> {

	private static Logger LOG = Logger.getLogger(JobTermPostListenerService.class);

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("coreAppsDao")
	private GenericDao<TCoreApps, String> coreAppsDao;

	private final String jobUrl = "/applications/services/job/truck/view/";
	private final String jobTermUrl = "/opadmin/order-termination/view/";

	@Override
	public void processSubmit(CkCtJobTermReq dto, Principal principal) throws Exception {
		LOG.info("processSubmit");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.JOB_TERM_SUB.getId());
		
		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_FIN_HD));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobTermId", dto.getJtrId() != null ? dto.getJtrId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobTermLink", apps != null ? apps.getAppsLaunchUrl() + jobTermUrl + dto.getJtrId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processVerify(CkCtJobTermReq dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void processApprove(CkCtJobTermReq dto, Principal principal) throws Exception {
		LOG.info("processApprove");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.JOB_TERM_APP.getId());

		// send to Roles.SP_L1
		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_L1));
			}
			
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_L1));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobTermId", dto.getJtrId() != null ? dto.getJtrId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobTermLink", apps != null ? apps.getAppsLaunchUrl() + jobTermUrl + dto.getJtrId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

		this.processApproveForCoTo(dto, principal);
	}


	@Override
	public void processReject(CkCtJobTermReq dto, Principal principal) throws Exception {
		LOG.info("processReject");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.JOB_TERM_REJ.getId());
		
		ArrayList<String> recipients = new ArrayList<>();
		if (null != dto) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_L1));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobTermId", dto.getJtrId() != null ? dto.getJtrId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobTermLink", apps != null ? apps.getAppsLaunchUrl() + jobTermUrl + dto.getJtrId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
	}
	
	/**
	 * Send email notification to CO and TO
	 * @param dto
	 * @param principal
	 * @throws Exception
	 */
	private void processApproveForCoTo(CkCtJobTermReq dto, Principal principal) throws Exception {
		LOG.info("processApprove");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.JOB_TERM_APP.getId());


		ArrayList<String> recipients = new ArrayList<>();
		List<CkCtJobTerm> termList = new ArrayList<>();
		// send to CO and TO
		if (null != termList && termList.size()> 0) {
			
			for(CkCtJobTerm term: termList ) {
				
				Optional<CoreAccn> opCoFfAccn = Optional.ofNullable(term.getTCkJobTruck().getTCoreAccnByJobPartyCoFf());
				if (opCoFfAccn.isPresent() && StringUtils.isNotBlank(opCoFfAccn.get().getAccnId())) {
					recipients.addAll(getRecipients(opCoFfAccn.get().getAccnId(), Roles.OFFICER));
				}
				
				Optional<CoreAccn> opTOAccn = Optional.ofNullable(term.getTCkJobTruck().getTCoreAccnByJobPartyTo());
				if (opTOAccn.isPresent() && StringUtils.isNotBlank(opTOAccn.get().getAccnId())) {
					recipients.addAll(getRecipients(opTOAccn.get().getAccnId(), Roles.OFFICER));
				}
				
				HashMap<String, String> contentFields = new HashMap<>();
				contentFields.put(":jobTermId", term.getJtId() != null ? term.getJtId() : "-");
				TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
				contentFields.put(":jobTermLink", apps != null ? apps.getAppsLaunchUrl() + jobUrl + term.getJtId() : "-");
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				param.setContentFeilds(contentFields);
				
				param.setRecipients(recipients);
				notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
			}
		}

	}

}
