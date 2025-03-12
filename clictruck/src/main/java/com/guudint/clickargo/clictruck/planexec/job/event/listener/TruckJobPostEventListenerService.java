package com.guudint.clickargo.clictruck.planexec.job.event.listener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.vcc.camelone.config.model.TCoreSysparam;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtToInvoiceDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.common.service.impl.CKEncryptionUtil;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.core.model.TCoreApps;

/**
 * {@code IJobPostEventListener} implementation for {@code CkJobTruck}.
 */
public class TruckJobPostEventListenerService extends AbstractJobPostEventListenerService {

	// Static Attributes
	/////////////////////
	private static Logger log = Logger.getLogger(TruckJobPostEventListenerService.class);

	@Autowired
	@Qualifier("coreAppsDao")
	private GenericDao<TCoreApps, String> coreAppsDao;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	private CkPaymentTxnDao ckPaymentTxnDao;

	@Autowired
	private CkCtToInvoiceDao ckCtToInvoiceDao;

	@Autowired
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private final String url = "/applications/services/job/truck/view/";
	private String approvalUrl = "/approve/billing/job/truck/:jobId/:accn/:roles/:validityDate";

	private static final String KEY_APPROVAL_LINK_VALIDITY = "CLICTRUCK_EMAIL_APPROVAL_VALIDITY";

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processSubmit(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processSubmit");
		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.TO_JOB_SUBMITTED.getId());

		// Recipient will be all the operations under the TO account
		ArrayList<String> recipients = new ArrayList<>();
		if (dto != null) {
			Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
			if (opToAccn.isPresent() && StringUtils.isNotBlank(opToAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.OFFICER));
			}

			// Set values for the power fields
			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobNumber", dto.getJobId());
//			CoreApps apps = getCoreApps(ServiceTypes.CLICTRUCK);
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if (opToAccn.isPresent() && "SG".equalsIgnoreCase(opToAccn.get().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);
		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processAccepted(CkJobTruck dto, Principal principal) throws Exception {
		log.info("processAccepted");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.COFF_JOB_ACCEPTED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		// Get the co/ff who submitted the account
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opCoFfAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyCoFf());
			if (opCoFfAccn.isPresent() && StringUtils.isNotBlank(opCoFfAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opCoFfAccn.get().getAccnId(), Roles.OFFICER));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobNumber", dto.getJobId());
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");

			// If the city code is SG, get sp_details from TCoreSysparam
			if (opCoFfAccn.isPresent() && "SG".equalsIgnoreCase(opCoFfAccn.get().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}

			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processAssigned(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processAssigned");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processRejected(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processRejected");
		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.COFF_JOB_REJECTED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opCoFfAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyCoFf());
			if (opCoFfAccn.isPresent() && StringUtils.isNotBlank(opCoFfAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opCoFfAccn.get().getAccnId(), Roles.OFFICER));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobNumber", dto.getJobId());
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if (opCoFfAccn.isPresent() && "SG".equalsIgnoreCase(opCoFfAccn.get().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processStarted(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processStarted by " + principal.getUserId());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processDelivered(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processDelivered");

		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.COFF_JOB_DELIVERED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opCoFfAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyCoFf());
			if (opCoFfAccn.isPresent() && StringUtils.isNotBlank(opCoFfAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opCoFfAccn.get().getAccnId(), Roles.OFFICER));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":jobNumber", dto.getJobId());
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if (opCoFfAccn.isPresent() && "SG".equalsIgnoreCase(opCoFfAccn.get().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void proccessBilled(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("proccessBilled");

		// TO submitted Invoice (CKT_NTL_0004)
		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.COFF_INV_SUBMITTED_APPROVALLINK.getId());

		ArrayList<String> recipients = new ArrayList<>();
		String coFfAccnId = null;
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opCoFfAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyCoFf());
			if (opCoFfAccn.isPresent() && StringUtils.isNotBlank(opCoFfAccn.get().getAccnId())) {
				
				coFfAccnId = opCoFfAccn.get().getAccnId();
				recipients.addAll(getRecipients(opCoFfAccn.get().getAccnId(), Roles.OFFICER));
				recipients.addAll(getRecipients(opCoFfAccn.get().getAccnId(), Roles.FF_FINANCE));
			}

			HashMap<String, String> contentFields = new HashMap<>();

			// Get the TO details
			Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
			if (opToAccn.isPresent()) {
				contentFields.put(":toAccnName", opToAccn.get().getAccnName());
			}

			Calendar cal = Calendar.getInstance();
			// append the max date that the link is still valid to access
			int maxDaysLinkValidity = Integer.valueOf(getSysParam(KEY_APPROVAL_LINK_VALIDITY, "7"));
			cal.add(Calendar.DATE, maxDaysLinkValidity);

			// jobId/:accn/:role/:yyyyMMdd
			String encRoles = CKEncryptionUtil.encrypt(Roles.FF_FINANCE.name() + ":" + Roles.OFFICER.name(),
					dto.getJobId());
			String encJobId = CKEncryptionUtil.encrypt(dto.getJobId(), "000");
			String encAccnId = CKEncryptionUtil.encrypt(coFfAccnId, dto.getJobId());
			String encValidity = CKEncryptionUtil.encrypt(sdf.format(cal.getTime()), dto.getJobId());

			String approvalLink = approvalUrl.replace(":jobId", encJobId).replace(":accn", encAccnId)
					.replace(":roles", encRoles).replace(":validityDate", encValidity);

			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobNumber", dto.getJobId());
			contentFields.put(":approvalLink", apps.getAppsLaunchUrl() + approvalLink);
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if (opCoFfAccn.isPresent() && "SG".equalsIgnoreCase(opCoFfAccn.get().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processBillVerified(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processBillVerified");

		// CO verified the invoice (CKT_NTL_0005)
		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.COFF_INV_VERIFIED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opCoFfAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyCoFf());
			if (opCoFfAccn.isPresent() && StringUtils.isNotBlank(opCoFfAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opCoFfAccn.get().getAccnId(), Roles.FF_FINANCE));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByJobId(dto.getJobId());
			ArrayList<String> invoiceNumbers = new ArrayList<>();
			for (TCkCtToInvoice tCkCtToInvoice : tCkCtToInvoices) {
				CkCtToInvoice ckCtToInvoice = new CkCtToInvoice(tCkCtToInvoice);
				invoiceNumbers.add(ckCtToInvoice.getInvId());
			}
			contentFields.put(":invoiceNumber", String.join(",", invoiceNumbers));
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if (opCoFfAccn.isPresent() && "SG".equalsIgnoreCase(opCoFfAccn.get().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
	}

	@Override
	public void processBillAcknowledged(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processBillAcknowledged");

		// CO approved the invoice (CKT_NTL_0007)
		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.TO_INV_APPROVED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
			if (opToAccn.isPresent() && StringUtils.isNotBlank(opToAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.OFFICER));
				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.FF_FINANCE));
			}

			// Notify GLI admin for Document Verification
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_OP_ADMIN));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByJobId(dto.getJobId());
			ArrayList<String> invoiceNumbers = new ArrayList<>();
			for (TCkCtToInvoice tCkCtToInvoice : tCkCtToInvoices) {
				CkCtToInvoice ckCtToInvoice = new CkCtToInvoice(tCkCtToInvoice);
				invoiceNumbers.add(ckCtToInvoice.getInvId());
			}
			contentFields.put(":invoiceNumber", String.join(",", invoiceNumbers));
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if (opToAccn.isPresent() && "SG".equalsIgnoreCase(opToAccn.get().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processBillApproved(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processBillApproved");

		// 20230904 Nina Not sending bill approved by GLI notification

		// CO approved the invoice (CKT_NTL_0007)
//		NotificationParam param = new NotificationParam();
//		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
//		param.setTemplateId(ClicTruckNotifTemplates.TO_INV_APPROVED.getId());
//
//		ArrayList<String> recipients = new ArrayList<>();
//		if (dto.getTCkJob() != null) {
//			Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
//			if (opToAccn.isPresent() && StringUtils.isNotBlank(opToAccn.get().getAccnId())) {
//				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.OFFICER));
//				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.FF_FINANCE));
//			}
//
//			HashMap<String, String> contentFields = new HashMap<>();
//			List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByJobId(dto.getJobId());
//			ArrayList<String> invoiceNumbers = new ArrayList<>();
//			for (TCkCtToInvoice tCkCtToInvoice : tCkCtToInvoices) {
//				CkCtToInvoice ckCtToInvoice = new CkCtToInvoice(tCkCtToInvoice);
//				invoiceNumbers.add(ckCtToInvoice.getInvId());
//			}
//			contentFields.put(":invoiceNumber", String.join(",", invoiceNumbers));
//			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
//			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
//			contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
//			param.setContentFeilds(contentFields);
//
//		}
//
//		param.setRecipients(recipients);
//		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processBillRejected(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processBillRejected");

		// CO rejected the invoice (CKT_NTL_0008)
		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.TO_INV_REJECTED.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
			if (opToAccn.isPresent() && StringUtils.isNotBlank(opToAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.OFFICER));
				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.FF_FINANCE));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByJobId(dto.getJobId());
			ArrayList<String> invoiceNumbers = new ArrayList<>();
			for (TCkCtToInvoice tCkCtToInvoice : tCkCtToInvoices) {
				CkCtToInvoice ckCtToInvoice = new CkCtToInvoice(tCkCtToInvoice);
				invoiceNumbers.add(ckCtToInvoice.getInvId());
			}
			contentFields.put(":invoiceNumber", String.join(",", invoiceNumbers));
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if (opToAccn.isPresent() && "SG".equalsIgnoreCase(opToAccn.get().getCityCode())) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void processInPaid(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processInPaid");

		// COFF pay to GLI (CKT_NTL_0013)
		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.SP_PAY_TXN_PAID.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opGliAccn = Optional
					.ofNullable(clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP));
			if (opGliAccn.isPresent() && StringUtils.isNotBlank(opGliAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_OP_ADMIN));
				recipients.addAll(getRecipients(opGliAccn.get().getAccnId(), Roles.SP_FIN_ADMIN));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			ArrayList<String> jobPaymentNumbers = new ArrayList<>();
			List<TCkPaymentTxn> listPayment = ckPaymentTxnDao.findByJobId(dto.getJobId());
			for (TCkPaymentTxn tCkPaymentTxn : listPayment) {
				if (tCkPaymentTxn.getPtxPaymentState().equalsIgnoreCase(PaymentStates.PAYING.getCode()))
					jobPaymentNumbers.add(tCkPaymentTxn.getPtxId());
			}
			contentFields.put(":jobPaymentNumber", String.join(",", jobPaymentNumbers));
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if ("SG".equalsIgnoreCase(dto.getTCoreAccnByJobPartyTo().getCityCode() != null ? dto.getTCoreAccnByJobPartyTo().getCityCode() : null)) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);

	}

	@Override
	public void processOutPaid(CkJobTruck dto, Principal principal) throws Exception {
		// TODO Auto-generated method stub
		log.info("processOutPaid");

		// GLI Pay to TO (CKT_NTL_0009)
		NotificationParam param = new NotificationParam();
		param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
		param.setTemplateId(ClicTruckNotifTemplates.TO_SP_PAY_COMPLETE.getId());

		ArrayList<String> recipients = new ArrayList<>();
		if (dto.getTCkJob() != null) {
			Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
			if (opToAccn.isPresent() && StringUtils.isNotBlank(opToAccn.get().getAccnId())) {
				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.OFFICER));
				recipients.addAll(getRecipients(opToAccn.get().getAccnId(), Roles.FF_FINANCE));
			}

			HashMap<String, String> contentFields = new HashMap<>();
			List<TCkCtToInvoice> tCkCtToInvoices = ckCtToInvoiceDao.findByJobId(dto.getJobId());
			ArrayList<String> invoiceNumbers = new ArrayList<>();
			for (TCkCtToInvoice tCkCtToInvoice : tCkCtToInvoices) {
				CkCtToInvoice ckCtToInvoice = new CkCtToInvoice(tCkCtToInvoice);
				invoiceNumbers.add(ckCtToInvoice.getInvId());
			}
			contentFields.put(":invoiceNumber", String.join(",", invoiceNumbers));
			TCoreApps apps = coreAppsDao.find(ServiceTypes.CLICTRUCK.getAppsCode());
			contentFields.put(":jobDetailLink", apps != null ? apps.getAppsLaunchUrl() + url + dto.getJobId() : "-");
			// If the city code is SG, get sp_details from TCoreSysparam
			if ("SG".equalsIgnoreCase(dto.getTCoreAccnByJobPartyTo().getCityCode() != null ? dto.getTCoreAccnByJobPartyTo().getCityCode() : null)) {
				Optional<TCoreSysparam> opTCoreSysparam = Optional.ofNullable(coreSysparamDao.find("CLICTRUCK_ADDR_SG_API_CALL"));
				if (opTCoreSysparam.isPresent()) {
					try {
						TCoreSysparam sysparam = opTCoreSysparam.get();
						JSONObject jsonSpDetails = new JSONObject(sysparam.getSysVal());

						// Construct the sp_details string
						String spDetailsMapped = jsonSpDetails.getString("accnName") + " " +
								jsonSpDetails.getString("addrCity") + " " +
								jsonSpDetails.getString("addrPcode") + ", " +
								jsonSpDetails.getString("ctyDescription");

						contentFields.put(":sp_details", spDetailsMapped);
					} catch (JSONException e) {
						log.error("Error parsing sysparam JSON", e);
						// Fallback if JSON parsing fails
						contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
					}
				} else {
					// Fallback if sysparam is not found
					contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
				}
			} else {
				// Default case if not SG
				contentFields.put(":sp_details", clickargoAccnService.getServiceProvider());
			}
			param.setContentFeilds(contentFields);

		}

		param.setRecipients(recipients);
		notificationUtilService.saveNotificationLog(param.toJson(), principal, true);
	}

}
