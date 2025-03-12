package com.guudint.clickargo.clictruck.finacing.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.CkPaymentTypes;
import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.core.model.TCoreApps;

public class PaymentPostEventListenerService extends AbstractPaymentPostEventListenerService {

	private static Logger log = Logger.getLogger(PaymentPostEventListenerService.class);

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("coreAppsDao")
	private GenericDao<TCoreApps, String> coreAppsDao;

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	private final String url = "/applications/finance/payments/transactions/details/";

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processTxnCreated(TCkPaymentTxn txn, CkPaymentTypes paymentType, Principal principal) throws Exception {
		// TODO Auto-generated method
		log.info("processTxnCreated");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.SP_PAY_TXN_CREATED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (null != txn) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_FIN_ADMIN));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobPaymentNumber",
					txn.getPtxPaymentState().equalsIgnoreCase(PaymentStates.NEW.getCode()) ? txn.getPtxId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + txn.getPtxId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processTxnVerified(TCkPaymentTxn txn, CkPaymentTypes paymentType, Principal principal)
			throws Exception {
		// TODO Auto-generated method stub
		log.info("processTxnVerified");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.SP_PAY_TXN_VERIFIED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (null != txn) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_FIN_HD));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobPaymentNumber",
					txn.getPtxPaymentState().equalsIgnoreCase(PaymentStates.VER_BILL.getCode()) ? txn.getPtxId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + txn.getPtxId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processTxnCompleted(TCkPaymentTxn txn, CkPaymentTypes paymentType, Principal principal)
			throws Exception {
		// TODO Auto-generated method stub
		log.info("processTxnCompleted");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.SP_PAY_TXN_PAID.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (null != txn) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_OP_ADMIN));
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.OFFICER_FINANCE));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobPaymentNumber",
					txn.getPtxPaymentState().equalsIgnoreCase(PaymentStates.APP_BILL.getCode()) ? txn.getPtxId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + txn.getPtxId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processTxnFailed(TCkPaymentTxn txn, CkPaymentTypes paymentType, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processTxnFailed");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.SP_PAY_TXN_FAILED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (null != txn) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_OP_ADMIN));
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.OFFICER_FINANCE));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobPaymentNumber",
					txn.getPtxPaymentState().equalsIgnoreCase(PaymentStates.CAN.getCode()) ? txn.getPtxId() : "-");
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + txn.getPtxId() : "-");
			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

}
