package com.guudint.clickargo.clictruck.finacing.listener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.finacing.event.AccountSuspensionEvent;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.enums.ClickargoNotifTemplates;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.TCoreUsrRole;
import com.vcc.camelone.cac.model.TCoreUsrRoleId;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.embed.TCoreContact;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.ErrorCodes;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.core.model.TCoreApps;

@Component
public class AccountSuspensionPostEventListener implements ApplicationListener<AccountSuspensionEvent> {

	// Static Attributes
	/////////////////////
	private static Logger log = Logger.getLogger(AccountSuspensionPostEventListener.class);

	@Autowired
	@Qualifier("ccmAccnService")
	protected IEntityService<TCoreAccn, String, CoreAccn> ccmAccnService;

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("coreAppsDao")
	private GenericDao<TCoreApps, String> coreAppsDao;

	@Autowired
	protected GenericDao<TCoreUsrRole, TCoreUsrRoleId> coreUsrRoleDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void onApplicationEvent(AccountSuspensionEvent event) {
		log.debug("onApplicationEvent");

		try {

			if (event == null)
				throw new ParameterException("param event null");

			if (event.getDue() == null)
				throw new ParameterException("param due null");

			switch (event.getDue()) {
			case "UNPAID_INVOICE":
				processAccnSusDueUnpaidInvoice(event);
				break;
			case "CONTRACT_EXPIRY":
				processAccnSusDueContractExpiry(event);
				break;
			default:
				break;
			}

		} catch (Exception ex) {
			log.error("onApplicationEvent", ex);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_UNKNOWN, ErrorCodes.MSG_GEN_UNKNOWN,
					"ClictruckPaymentEventListener", ex);
		}
	}

	public void processAccnSusDueUnpaidInvoice(AccountSuspensionEvent event) throws Exception {
		// TODO Auto-generated method stub
		log.info("processAccnSusDueUnpaidInvoice");

		if (event.getCoreAccn() == null)
			throw new ParameterException("param coreAccn null");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.ACCN_SUS_DUE_UNPAID_INV.getId());

		ArrayList<String> recipients = new ArrayList<>();
		Optional<CoreAccn> opGliAccn = Optional
				.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
		if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
			recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_L1));
			recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_FIN_HD));
		}

		if (Stream.of(AccountTypes.ACC_TYPE_CO.name(), AccountTypes.ACC_TYPE_FF.name())
				.anyMatch(type -> type.equals(event.getCoreAccn().getTMstAccnType().getAtypId()))) {
			recipients.addAll(getRecipients(event.getCoreAccn().getAccnId(), Roles.OFFICER));
		}

		HashMap<String, String> contentFields = new HashMap<>();
		contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
		contentFields.put(":accnName", event.getCoreAccn().getAccnName());
		param.setContentFeilds(contentFields);

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), null, true);

	}

	public void processAccnSusDueContractExpiry(AccountSuspensionEvent event) throws Exception {
		// TODO Auto-generated method stub
		log.info("processAccnSusDueContractExpiry");

		if (event.getCoreAccn() == null)
			throw new ParameterException("param coreAccn null");

		if (event.getCkCtContract() == null)
			throw new ParameterException("param contract null");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClickargoNotifTemplates.ACCN_SUS_DUE_CONTRACT_EXP.getId());

		ArrayList<String> recipients = new ArrayList<>();

		Optional<CoreAccn> opGliAccn = Optional
				.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
		if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
			recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_L1));
			recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_FIN_HD));
		}

		if (Stream.of(AccountTypes.ACC_TYPE_CO.name(), AccountTypes.ACC_TYPE_FF.name())
				.anyMatch(type -> type.equals(event.getCoreAccn().getTMstAccnType().getAtypId()))) {
			recipients.addAll(getRecipients(event.getCoreAccn().getAccnId(), Roles.OFFICER));
		}

		HashMap<String, String> contentFields = new HashMap<>();
		contentFields.put(":toAccn",
				event.getCkCtContract().getTCoreAccnByConTo() != null
						? event.getCkCtContract().getTCoreAccnByConTo().getAccnName()
						: "-");
		contentFields.put(":accnName", event.getCoreAccn().getAccnName());
		contentFields.put(":contractStartDt",
				event.getCkCtContract().getConDtStart() != null ? sdf.format(event.getCkCtContract().getConDtStart())
						: "-");
		contentFields.put(":contractEndDt",
				event.getCkCtContract().getConDtEnd() != null ? sdf.format(event.getCkCtContract().getConDtEnd())
						: "-");

		contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
		param.setContentFeilds(contentFields);

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), null, true);

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected List<String> getRecipients(String accnId, Roles roleType) throws Exception {
		if (StringUtils.isBlank(accnId))
			throw new ParameterException("param accnId null or empty");

		List<String> emailsList = new ArrayList<>();
		if (roleType != null) {
			String hql = "from TCoreUsrRole o where o.TCoreUsr.TCoreAccn.accnId LIKE :accnId and o.TCoreUsr.usrStatus=:status and o.id.urolRoleid=:roleTypeId and o.id.urolAppscode=:appsCode";
			Map<String, Object> params = new HashMap<>();
			params.put("accnId", "%" + accnId + "%");
			params.put("status", RecordStatus.ACTIVE.getCode());
			params.put("roleTypeId", roleType.name());
			params.put("appsCode", ServiceTypes.CLICTRUCK.getAppsCode());

			List<TCoreUsrRole> usrList = coreUsrRoleDao.getByQuery(hql, params);
			if (usrList != null && usrList.size() > 0) {
				for (TCoreUsrRole usr : usrList) {
					Hibernate.initialize(usr.getTCoreUsr());
					Hibernate.initialize(usr.getTCoreUsr().getUsrContact());
					Optional<TCoreContact> opUsrContact = Optional.ofNullable(usr.getTCoreUsr().getUsrContact());
					if (opUsrContact.isPresent() && StringUtils.isNotBlank(opUsrContact.get().getContactEmail())) {
						if (!emailsList.contains(opUsrContact.get().getContactEmail()))
							emailsList.add(opUsrContact.get().getContactEmail());
					}

				}

			}
		}

		return emailsList;
	}

}
