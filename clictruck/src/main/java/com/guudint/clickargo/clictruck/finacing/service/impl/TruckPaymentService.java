package com.guudint.clickargo.clictruck.finacing.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clicservice.model.TCkSvcJournal;
import com.guudint.clickargo.clicservice.service.impl.CkSvcActionMaskService;
import com.guudint.clickargo.clictruck.finacing.constant.FinancingConstants.FinancingTypes;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote.DebitNoteStates;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToPayment;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentDetails;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentDetails.InvoiceDetails;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.finacing.dto.ToInvoiceStates;
import com.guudint.clickargo.clictruck.finacing.event.PaymentStateChangeEvent;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.service.AbstractPaymentService;
import com.guudint.clickargo.clictruck.finacing.service.IDebitNoteService;
import com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService;
import com.guudint.clickargo.clictruck.finacing.service.ITruckJobCreditService;
import com.guudint.clickargo.clictruck.finacing.service.ITruckOperatorPayoutService;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstDebitNoteState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstToInvoiceState;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.dto.FailedDescription;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiRecordRequest;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiRecordResponse;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice.PlatformInvoiceStates;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtPaymentService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.impl.CkCoreAccnService;
import com.guudint.clickargo.external.dto.CancelPaymentRequest;
import com.guudint.clickargo.external.dto.CancelPaymentResponse;
import com.guudint.clickargo.external.dto.CreateFundTransferRequest;
import com.guudint.clickargo.external.dto.MakePaymentRequest;
import com.guudint.clickargo.external.dto.MakePaymentResponse;
import com.guudint.clickargo.external.services.IPaymentGateway;
import com.guudint.clickargo.master.dao.CoreAccnDao;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.model.TCkMstPaymentType;
import com.guudint.clickargo.master.model.TCkMstServiceType;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.guudint.clickargo.payment.dto.CkPaymentTxn;
import com.guudint.clickargo.payment.dto.PaymentCallbackRequest;
import com.guudint.clickargo.payment.dto.PaymentCallbackResponse;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.payment.enums.PaymentTypes;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.guudint.clickargo.payment.model.TCkPaymentTxnInvoice;
import com.guudint.clickargo.payment.service.impl.CkPaymentTxnLogService;
import com.guudint.clickargo.payment.service.impl.CkPaymentTxnService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.master.dto.MstBank;
import com.vcc.camelone.master.model.TMstBank;
import com.vcc.camelone.master.model.TMstCurrency;
import com.vcc.camelone.util.PrincipalUtilService;
import com.vcc.camelone.util.email.SysParam;

@Service
public class TruckPaymentService extends AbstractPaymentService {

	private static Logger LOG = Logger.getLogger(TruckPaymentService.class);

	public static final String POSTFIX_VA_IDR = "2SP_VA_IDR";
	public static final String POSTFIX_VA_IDR_TO = "2TO_VA_IDR";
	private static final String KEY_IN_PAYMENT_CALLBACK_URL = "CLICTRUCK_IN_PAYMENT_CALLBACK_URL";

	private static final String CONFIG_SP_BANK_ACCN = "DO_SP_BNK_ACCN";
	private static final String CONFIG_SP_BANK = "DO_SP_BNK";
	private static final String CONFIG_BANK_DETAILS = "BANK_DETAIL";

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	private IDebitNoteService debiteNoteService;

	@Autowired
	private IPlatformInvoiceService platformFeeService;

	@Autowired
	@Qualifier("ckJobTruckService")
	private IEntityService<TCkJobTruck, String, CkJobTruck> ckJobTruckService;

	@Autowired
	private CkCoreAccnService ckCoreAccnService;

	@Autowired
	private CkPaymentTxnService ckPaymentTxnService;

	@Autowired
	@Qualifier("ckPaymentTxnDao")
	private CkPaymentTxnDao ckPaymentTxnDao;

	@Autowired
	@Qualifier("paymentGatewayService")
	private IPaymentGateway paymentGateway;

	@Autowired
	private CkCtPaymentService ckctPaymentService;

	@Autowired
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Autowired
	private CoreAccnDao coreAccnDao;

	@Autowired
	private ITruckJobCreditService truckJobCreditService;

	@Autowired
	private CkJobTruckServiceUtil jobTruckServiceUtil;

	@Autowired
	private ITruckOperatorPayoutService toPayoutService;

	@Autowired
	private GenericDao<TCkCtPlatformInvoice, String> ckCtPlatformInvDao;

	@Autowired
	private GenericDao<TCkCtDebitNote, String> ckCtDebitNoteDao;

	@Autowired
	private SysParam sysParam;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	private CkSvcActionMaskService ckSvcActionMaskService;

	@Autowired
	private ClickargoAccnService ckAccnService;

	@Autowired
	private CkPaymentTxnLogService paymentTxnLogService;

	@Autowired
	@Qualifier("mstBankService")
	private IEntityService<TMstBank, String, MstBank> mstBankService;

	@Autowired
	private IPlatformInvoiceService platformInvoiceService;

	@Autowired
	private IDebitNoteService debitNoteService;

	@Override
	public JobPaymentDetails getPaymentDetails(String reqBody, boolean isSubmit)
			throws ParameterException, EntityNotFoundException, ValidationException, Exception {

		JobPaymentDetails jobInvDetails = new JobPaymentDetails();
		try {
			if (StringUtils.isBlank(reqBody))
				throw new ParameterException("param reqBodyOfDoIds null");

			Principal principal = principalUtilService.getPrincipal();

			if (principal == null)
				throw new ProcessingException("param principal null");

			// Parse the reqBodyOfDoIds
			Map<String, String> reqBodyMap = mapper.readValue(reqBody, new TypeReference<HashMap<String, String>>() {
			});

			List<String> listJobTruckIds = Arrays.asList(reqBodyMap.get("jobIds").split(","));

			jobInvDetails.setBillingDate(new Date());

			List<InvoiceDetails> listInvoiceDetails = new ArrayList<>();
			// Using the list of job truck IDs, retrieve the debit note and the platform fee
			int seq = 1;
			double total = 0.0;
			CoreAccn accn = principal.getCoreAccn();
			String accnType = accn.getTMstAccnType().getAtypId();

			// List as placeholder for jobs that has not been paid yet by the SP
			List<String> spJobNonPaidInvoicesList = new ArrayList<>();

			for (String truckJobId : listJobTruckIds) {

				CkJobTruck jobTruck = ckJobTruckService.findById(truckJobId);
				if (jobTruck == null)
					throw new EntityNotFoundException("job truck " + truckJobId + " not found");

				// for Debit Note
				CkCtDebitNote dn = debiteNoteService.getDebitNote(jobTruck, principal.getCoreAccn(),
						DebitNoteStates.NEW);
				// we don't throw exception if no dn found, but up to here, the dn should
				// already been created during payment approval
				if (dn != null) {
					InvoiceDetails invDets = new InvoiceDetails(dn.getDnId(), seq, jobTruck.getJobId(), 1,
							JobPaymentDetails.JobPaymentPdfType.DEBIT_NOTE.name(), dn.getDnNo(),
							CkCtDebitNote.DEFAULT_CURRENCY, dn.getDnTotal(),
							String.format(CkCtDebitNote.DEFAULT_DESC, truckJobId), jobTruck.getTCkJob().getJobId());
					invDets.setFileLocation(dn.getDnLoc());
					listInvoiceDetails.add(invDets);
					total += dn.getDnTotal().doubleValue();
					seq++;
				}

				// If the account type is SP, do not include platform fee, of it isIncludePfIn
				// is true
				// For Platform Fees, the pf to is trucking operator
				if (accnType.equals(AccountTypes.ACC_TYPE_SP.name())) {
					accn = jobTruck.getTCoreAccnByJobPartyTo();
				}

				CkCtPlatformInvoice pf = platformFeeService.getPlatformInvoice(jobTruck, accn,
						PlatformInvoiceStates.NEW);
				// we don't throw any exception if no platform fee found but up to here, the pf
				// should already been created during payment approval
				if (pf != null) {
					// exclude for SP

					String desc = String.format(CkCtPlatformInvoice.DEFAULT_DESC, truckJobId);
					if (accnType.equals(AccountTypes.ACC_TYPE_SP.name())) {
						desc = "(" + String.format(CkCtPlatformInvoice.DEFAULT_DESC, truckJobId) + ")";
						total -= pf.getInvTotal().doubleValue();
					} else {
						total += pf.getInvTotal().doubleValue();
					}

					InvoiceDetails invDetPf = new InvoiceDetails(pf.getInvId(), seq, jobTruck.getJobId(), 1,
							JobPaymentDetails.JobPaymentPdfType.PLATFORM_FEE.name(), pf.getInvNo(),
							CkCtPlatformInvoice.DEFAULT_CURRENCY, pf.getInvTotal(), desc,
							jobTruck.getTCkJob().getJobId());
					invDetPf.setFileLocation(pf.getInvLoc());
					listInvoiceDetails.add(invDetPf);
					seq++;
				}

				// 20231107 Add validation if the accnType = CO/FF if the other dn/pf is issued
				// to SP and has state =
				// PAID already then proceed,
				// otherwise prompt warning and hide the submit button.
				if (isSubmit && (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())
						|| accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name()))) {

					// 20240519: check if the job is OPM, do not check for DN paid, as we don't
					// create DN for OPM
					boolean isOpm = Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name())
							.contains(jobTruck.getJobFinanceOpt());
					if (!isOpm) {

						CoreAccn spAccn = ckAccnService.getServiceProviderAccn();

						// Only check the debit note is paid.
						CkCtDebitNote dnSp = debiteNoteService.getDebitNote(jobTruck, spAccn, DebitNoteStates.PAID);
						// if this returns null, it means it's not yet paid
						if (dnSp == null) {
							if (!spJobNonPaidInvoicesList.contains(jobTruck.getJobId())) {
								spJobNonPaidInvoicesList.add(jobTruck.getJobId());
							}
						}
					}

				}

			}

			if (spJobNonPaidInvoicesList != null && !spJobNonPaidInvoicesList.isEmpty()) {
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("job-out-not-paid-yet", StringUtils.join(spJobNonPaidInvoicesList, ","));
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			}

			// SP don't need to get account config as user can select multiple different TO
			//
			if (!accnType.equals(AccountTypes.ACC_TYPE_SP.name())) {
				StringBuilder keyStr = new StringBuilder();
				String accnId = principal.getCoreAccn().getAccnId();
				if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name()))
					keyStr.append("FF").append(POSTFIX_VA_IDR);
				else if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name()))
					keyStr.append("CO").append(POSTFIX_VA_IDR);
				else if (accnType.equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
					// if SP is paying get the bank account details of the TO that the funds to be
					// transfered to
					keyStr.append(CONFIG_BANK_DETAILS);
					if (reqBodyMap.get("toAccnId") != null) {
						accnId = reqBodyMap.get("toAccnId");
					}
				}

				String principalVaIdr = ckCoreAccnService.getAccnConfig(accnId, keyStr.toString()).getAcfgVal();
				jobInvDetails.setVaIdr(principalVaIdr);

				// To display the bank name from T_MST_BANK
				if (principalVaIdr.contains(":")) {
					// first token is the bank code, 2nd is the accnNo, 3rd is the bank accn name
					String[] splits = principalVaIdr.split(":");
					if (splits.length == 3) {
						jobInvDetails.setBankAccnName(splits[2]);
					}
				}

			}

			jobInvDetails.setTotalIdr(total);
			jobInvDetails.setInvoiceDetails(listInvoiceDetails);

		} catch (ValidationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw ex;
		}

		return jobInvDetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void executeInboundPay(String reqBody) throws ParameterException, EntityNotFoundException, Exception {
		// TODO Auto-generated method stub
		try {

			if (StringUtils.isBlank(reqBody))
				throw new ParameterException("param reqBodyOfDoIds null");

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("param principal null");

			// Parse the reqBody
			Map<String, Object> reqBodyMap = mapper.readValue(reqBody, new TypeReference<HashMap<String, Object>>() {
			});

			List<String> listJobIds = (List<String>) reqBodyMap.get("jobIds");

			if (listJobIds == null || listJobIds.isEmpty())
				throw new ProcessingException("jobIds is null or empty");

			// Check for concurrent jobId payments
			List<String> jobIdsAlreadySelected = jobTruckServiceUtil.concurrentCheckJobsForInPayment(listJobIds);
			if (!jobIdsAlreadySelected.isEmpty()) {
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("job-in-pay-processing", StringUtils.join(jobIdsAlreadySelected, ","));
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			}

			// use above method getPaymentDetails to get some details
			Map<String, String> reqParam = new HashMap<>();
			reqParam.put("jobIds", StringUtils.join(listJobIds, ","));
			JobPaymentDetails doInvDetails = getPaymentDetails(mapper.writeValueAsString(reqParam), true);

			// Before proceed check if there is an existing payment txn, static VA should
			// only have one NEW payment transaction
			String newTxn = ckPaymentTxnService.getLatestNewPaymentTxn(doInvDetails.getVaIdr());
			if (StringUtils.isNotBlank(newTxn)) {
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("existing-new-txn", newTxn);
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			}

			// Create payment txn first before creating ck_ct_payment
			// 1. Create t_ck_payment_txn
			TCkPaymentTxn txnEntity = ckPaymentTxnService.createPaymentTxn(ServiceTypes.CLICTRUCK, listJobIds,
					principal);
			txnEntity.setPtxAmount(new BigDecimal(doInvDetails.getTotalIdr()));
			txnEntity.setPtxPaymentState(JobPaymentStates.NEW.name());
			ckPaymentTxnDao.update(txnEntity);

			// 2. Create TCkCtPayment from the invoice details list.
			// The details from invoiceDetails list are per jobs (2 each: 1 platform fee, 1
			// debit note per account)
			double totalPaymentToTally = 0.0;
			if (doInvDetails.getInvoiceDetails() != null) {
				for (InvoiceDetails inv : doInvDetails.getInvoiceDetails()) {
					TCkCtPayment ctPayment = ckctPaymentService.createCtPayment(txnEntity, inv.getJobId(), inv,
							principal, CkPaymentTypes.INBOUND);
					totalPaymentToTally += ctPayment.getCtpAmount().doubleValue();
				}
			}

			// Update the job inbound payment state
			updateJobIdsFromCtPayment(txnEntity.getPtxId(), JobPaymentStates.PAYING, CkPaymentTypes.INBOUND);

			if (totalPaymentToTally != doInvDetails.getTotalIdr())
				LOG.error("total not tally " + totalPaymentToTally + " with " + doInvDetails.getTotalIdr());

			// 2.b Retrieve the callback api from sysparam for clicpay to inform clictruck
			// that paid has been made. Throw exception if no record found, since clicpay
			// need this as one of the required parameters.
			String ctInCallBackPayUrl = getConfigFromSysParam(KEY_IN_PAYMENT_CALLBACK_URL);
			if (StringUtils.isBlank(ctInCallBackPayUrl))
				throw new EntityNotFoundException("sysparam key " + KEY_IN_PAYMENT_CALLBACK_URL + " not configured ");

			// 3. Make payment to payment gateway
			MakePaymentRequest request = new MakePaymentRequest();
			request.setNode(txnEntity.getPtxMerchantBank());
			request.setServiceID(txnEntity.getTCkMstServiceType().getSvctId());
			request.setRefID(txnEntity.getPtxId());
			request.setVa(txnEntity.getPtxPayerBankAccn());
			request.setCallback(ctInCallBackPayUrl);
			request.setAmount(txnEntity.getPtxAmount());
			request.setCcy(txnEntity.getTMstCurrency().getCcyCode());
			MakePaymentResponse response = paymentGateway.makePayment(request);
			if (response.hasError()) {
				// if error throw exception to revert transaction. payment txn should not be
				// created.
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("processing-txn", response.getErr().getMsg());
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			} else {
				// this part here it should be NEW and clictruck should expect a callback from
				// clicpay to do the transfer later on.
				if (response.getStatus().equalsIgnoreCase("COMPLETED")) {
					txnEntity.setPtxPaymentState(JobPaymentStates.PAYING.name());
					paymentTxnLogService.createPaymentTxnLog(txnEntity,
							JobPaymentStates.PAYING.name() + " VIA STATIC VA", principal);

				} else {
					txnEntity.setPtxPaymentState(response.getStatus());
					paymentTxnLogService.createPaymentTxnLog(txnEntity,
							response.getStatus() + "-" + response.getMessage(), principal);
				}

				if (response.getData() != null) {
					txnEntity.setPtxBankRef(response.getData().getTxnNodeRef());
				}

				// only update if it's successful
				txnEntity.setPtxDtLupd(new Date());
				ckPaymentTxnDao.update(txnEntity);

			}

		} catch (Exception ex) {
			throw ex;
		}
	}

	@Override
	public Map<String, String> downloadTempInvoicePdf(Map<String, Object> pdfDetails)
			throws ParameterException, EntityNotFoundException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void executeOutboundPay(String reqBody) throws ParameterException, EntityNotFoundException, Exception {
		// TODO Auto-generated method stub
		try {

			if (StringUtils.isBlank(reqBody))
				throw new ParameterException("param reqBodyOfDoIds null");

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("param principal null");

			// Parse the reqBody
			Map<String, Object> reqBodyMap = mapper.readValue(reqBody, new TypeReference<HashMap<String, Object>>() {
			});

			List<String> listJobIds = (List<String>) reqBodyMap.get("jobIds");

			// check for suspended accounts, before proceeding
			checkForSuspendedAccounts(listJobIds);

			if (listJobIds == null || listJobIds.isEmpty())
				throw new ProcessingException("jobIds is null or empty");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// Check for concurrent jobId payments
			List<String> jobIdsAlreadySelected = jobTruckServiceUtil.concurrentCheckJobsPaying(listJobIds);
			if (!jobIdsAlreadySelected.isEmpty()) {
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("job-out-pay-processing", StringUtils.join(jobIdsAlreadySelected, ","));
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			}

			Map<String, Map<String, List<InvoiceDetails>>> mapAccnByDueDate = new HashMap<String, Map<String, List<InvoiceDetails>>>();

			// 1. Iterate through the job ids and fetch the records to determine the TO
			for (String truckJobId : listJobIds) {
				CkJobTruck jobTruck = ckJobTruckService.findById(truckJobId);
				if (jobTruck == null)
					throw new EntityNotFoundException("job truck " + truckJobId + " not found");

				mapAccnByDueDate.compute(jobTruck.getTCoreAccnByJobPartyTo().getAccnId(), (accnId, dueDateMap) -> {

					if (Objects.isNull(dueDateMap)) {
						dueDateMap = new HashMap<String, List<InvoiceDetails>>();
					}

					try {
						// for Debit Note
						CkCtDebitNote dn = debiteNoteService.getDebitNote(jobTruck, principal.getCoreAccn(),
								DebitNoteStates.NEW);

						// we don't throw exception if no dn found, but up to here, the dn should
						// already been created during payment approval
						if (dn != null) {

							// group the dn by the due date
							dueDateMap.compute(sdf.format(dn.getDnDtDue()), (dueDate, listInvDetailsByJob) -> {
								if (Objects.isNull(listInvDetailsByJob)) {
									listInvDetailsByJob = new ArrayList<InvoiceDetails>();
								}
								int seq = 1;
								InvoiceDetails invDets = new InvoiceDetails(dn.getDnId(), seq, jobTruck.getJobId(), 1,
										JobPaymentDetails.JobPaymentPdfType.DEBIT_NOTE.name(), dn.getDnNo(),
										CkCtDebitNote.DEFAULT_CURRENCY, dn.getDnTotal(),
										String.format(CkCtDebitNote.DEFAULT_DESC, truckJobId),
										jobTruck.getTCkJob().getJobId());
								invDets.setFileLocation(dn.getDnLoc());

								listInvDetailsByJob.add(invDets);
								seq++;

								// Platform fee is another line
								CoreAccn accn = principal.getCoreAccn();
								String accnType = accn.getTMstAccnType().getAtypId();
								// If the account type is SP, do not include platform fee, of it isIncludePfIn
								// is true

								// For Platform Fees, the pf to is trucking operator
								if (accnType.equals(AccountTypes.ACC_TYPE_SP.name())) {
									accn = jobTruck.getTCoreAccnByJobPartyTo();
								}

								try {
									CkCtPlatformInvoice pf = platformFeeService.getPlatformInvoice(jobTruck, accn,
											PlatformInvoiceStates.NEW);
									// we don't throw any exception if no platform fee found but up to here, the pf
									// should already been created during payment approval
									if (pf != null) {
										// exclude for SP

										String desc = String.format(CkCtPlatformInvoice.DEFAULT_DESC, truckJobId);

										InvoiceDetails invDetPf = new InvoiceDetails(pf.getInvId(), seq,
												jobTruck.getJobId(), 1,
												JobPaymentDetails.JobPaymentPdfType.PLATFORM_FEE.name(), pf.getInvNo(),
												CkCtPlatformInvoice.DEFAULT_CURRENCY, pf.getInvTotal(), desc,
												jobTruck.getTCkJob().getJobId());
										invDetPf.setFileLocation(pf.getInvLoc());
										listInvDetailsByJob.add(invDetPf);
										seq++;
									}
								} catch (Exception ex) {
									LOG.error(ex);
								}

								return listInvDetailsByJob;
							});
						}
					} catch (Exception ex) {
						LOG.error(ex);
					}

					return dueDateMap;
				});

			}

			// 1.5
			BigDecimal max250M = sysParam.getValBigDecimal("CLICTRUCK_PAYMENT_LIMIT", new BigDecimal(250_000_000)); // 250M

			List<Map<String, Map<String, List<InvoiceDetails>>>> splitBy250MList = new ArrayList<>();

			// Map<accnId, Map<"yyyy-MM-dd", List<InvoiceDetails>>
			for (Map.Entry<String, Map<String, List<InvoiceDetails>>> entry : mapAccnByDueDate.entrySet()) {

				// iterate through the mapByDates and create txn.
				Map<String, List<InvoiceDetails>> mapByDates = entry.getValue();

				for (Map.Entry<String, List<InvoiceDetails>> byDateEntry : mapByDates.entrySet()) {

					List<InvoiceDetails> invDetaisList = byDateEntry.getValue();
					List<InvoiceDetails> invList = new ArrayList<InvoiceDetails>();

					for (InvoiceDetails invDetail : invDetaisList) {

						if (max250M.compareTo(invDetail.getInvAmt()) < 0) {
							throw new Exception(String.format("Invoice amount %d greater than %d ",
									invDetail.getInvAmt(), max250M));
						}

						BigDecimal byDateTotal = invList.stream().map(el -> {
							if (el.getInvType()
									.equalsIgnoreCase(JobPaymentDetails.JobPaymentPdfType.PLATFORM_FEE.name())) {
								return el.getInvAmt().negate();
							} else {
								return el.getInvAmt();
							}
						}).reduce(BigDecimal.ZERO, BigDecimal::add);

						BigDecimal invAmt = JobPaymentDetails.JobPaymentPdfType.PLATFORM_FEE.name().equalsIgnoreCase(
								invDetail.getInvType()) ? invDetail.getInvAmt().negate() : invDetail.getInvAmt();

						if (max250M.compareTo(byDateTotal.add(invAmt)) < 0) {
							// > 250M
							splitBy250MList.add(createPaymentInvMap(entry.getKey(), byDateEntry.getKey(), invList));
							invList = new ArrayList<InvoiceDetails>();
						}
						invList.add(invDetail);
					}
					if (invList.size() > 0) {
						splitBy250MList.add(createPaymentInvMap(entry.getKey(), byDateEntry.getKey(), invList));
					}
				}
			}

			for (Map<String, Map<String, List<InvoiceDetails>>> mapAccnByDueDateSplit : splitBy250MList) {
				// 2. Using mapAccnByDueDate from above, create transaction per due date
				for (Map.Entry<String, Map<String, List<InvoiceDetails>>> entry : mapAccnByDueDateSplit.entrySet()) {

					// iterate through the mapByDates and create txn.
					Map<String, List<InvoiceDetails>> mapByDates = entry.getValue();
					// form the jobIds from mapByDates to create txn
					List<String> jobIdByDateList = new ArrayList<>();
					BigDecimal byDateTotal = new BigDecimal(0);
					for (Map.Entry<String, List<InvoiceDetails>> byDateEntry : mapByDates.entrySet()) {

						jobIdByDateList.addAll(byDateEntry.getValue().stream().map(el -> el.getJobId()).distinct()
								.collect(Collectors.toList()));
						byDateTotal = byDateEntry.getValue().stream().map(el -> {
							if (el.getInvType()
									.equalsIgnoreCase(JobPaymentDetails.JobPaymentPdfType.PLATFORM_FEE.name())) {
								return el.getInvAmt().negate();
							} else {
								return el.getInvAmt();
							}
						}).reduce(BigDecimal.ZERO, BigDecimal::add);

						// create txn by jobIdByDateList by account
						TCkPaymentTxn txnEntity = this.createPaymentTxnOutbound(ServiceTypes.CLICTRUCK, jobIdByDateList,
								entry.getKey(), sdf.parse(byDateEntry.getKey()), principal);
						txnEntity.setPtxAmount(byDateTotal);
						ckPaymentTxnDao.update(txnEntity);

						// this is just for checking
						BigDecimal totalPaymentToTally = new BigDecimal(0);
						List<String> jobIds = new ArrayList<>();

						// Create TCkCtPayment by the list of each of the date key
						for (InvoiceDetails invDets : byDateEntry.getValue()) {
							TCkCtPayment ctPayment = ckctPaymentService.createCtPayment(txnEntity, invDets.getJobId(),
									invDets, principal);
							totalPaymentToTally.add(ctPayment.getCtpAmount());

							if (jobIds.contains(invDets.getJobId())) {
								jobIds.add(invDets.getJobId());
							}
						}

						// Update tckCtPayment
						ckctPaymentService.updateCtPaymentByTxn(txnEntity.getPtxId(), JobPaymentStates.PENDING);

						updateJobIdsFromCtPayment(txnEntity.getPtxId(), JobPaymentStates.PENDING,
								CkPaymentTypes.OUTBOUND);

						if (totalPaymentToTally != byDateTotal)
							LOG.error("total not tally " + totalPaymentToTally + " with " + byDateTotal);

						txnEntity.setPtxDtLupd(new Date());
						ckPaymentTxnDao.update(txnEntity);

						// Create payment txn
						paymentTxnLogService.createPaymentTxnLog(txnEntity,
								JobPaymentStates.PENDING.name() + " FOR VERIFICATION/APPROVAL", principal);

						// publish event per transaction created
						eventPublisher.publishEvent(new PaymentStateChangeEvent(this, CkPaymentTypes.OUTBOUND,
								PaymentStates.NEW, txnEntity, null));
					}

				}
			}

		} catch (Exception ex) {
			throw ex;
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public PaymentCallbackResponse executeCallBack(String reqBody)
			throws ParameterException, EntityNotFoundException, Exception {
		// TODO Auto-generated method stub
		LOG.info("txnPaymentCallback " + reqBody);
		PaymentCallbackResponse response = new PaymentCallbackResponse();
		try {

			if (StringUtils.isBlank(reqBody))
				throw new ProcessingException("param reqBody is null or empty");

			PaymentCallbackRequest callbackRequest = mapper.readValue(reqBody, PaymentCallbackRequest.class);

			// Txn ID is saved in bank_ref
			TCkPaymentTxn inPayTxn = ckPaymentTxnDao.getByBankRefAndStatus(callbackRequest.getUserRefNo(),
					Constant.ACTIVE_STATUS);
			if (Objects.isNull(inPayTxn)) {
				throw new ProcessingException(
						"PaymentCallback - no record found for user_ref_no " + callbackRequest.getUserRefNo());
			}

			// Added checking below so that it won't continue since it has already been
			// processed. To avoid unnecessary
			// transactions.
			if (inPayTxn.getPtxPaymentState().equalsIgnoreCase("SUCCESS")) {
				response.setCode("200");
				response.setMessage("Already acknowledged");
			}

			// Update payment audit log associated to this transaction
			updatePaymentAudit(inPayTxn.getPtxId(), callbackRequest);

			// update the ffToSpPaymentTxn to SUCCESS
			inPayTxn.setPtxDtPaid(new Date());
			inPayTxn.setPtxPaymentState(PaymentStates.PAID.getCode());
			ckPaymentTxnDao.update(inPayTxn);

			// Update tckCtPayment
			ckctPaymentService.updateCtPaymentByTxn(inPayTxn.getPtxId(), JobPaymentStates.PAID);

			// Update job state and the inpayment state
			// Update the jobs associated to this transaction
			updateJobIdsFromCtPayment(inPayTxn.getPtxId(), JobPaymentStates.PAID, CkPaymentTypes.INBOUND);

			response.setCode("200");
			response.setMessage("success");

		} catch (Exception e) {
			LOG.error("executeCallBack", e);
			throw e;
		}

		return response;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void executeCancelPay(String txnId, CkPaymentTypes ckPaymentType)
			throws ParameterException, EntityNotFoundException, Exception {

		if (StringUtils.isBlank(txnId))
			throw new ParameterException("param txnId null or empty");

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("param principal null");

		// Retrieve the txn and check if the status is not yet paid
		TCkPaymentTxn txnEntity = ckPaymentTxnDao.find(txnId);
		if (txnEntity == null)
			throw new EntityNotFoundException("transaction " + txnId + " not found");

		if (txnEntity.getPtxPaymentState().equalsIgnoreCase(JobPaymentStates.PAID.name()))
			throw new ProcessingException("Transaction " + txnId + " already paid!");

		try {

			// only call payment gateway cancellation for INBOUND since the OUTBOUND is
			// fundstransfer
			if (ckPaymentType == CkPaymentTypes.INBOUND) {
				// TODO Call to payment gateway for cancellation of transaction
				CancelPaymentRequest request = new CancelPaymentRequest();
				request.setNode(txnEntity.getPtxMerchantBank());
				request.setServiceID(txnEntity.getTCkMstServiceType().getSvctId());
				request.setRefID(txnEntity.getPtxId());
				request.setRef(txnEntity.getPtxId());
				request.setVa(txnEntity.getPtxPayerBankAccn());
				request.setCallback(null);
				CancelPaymentResponse response = paymentGateway.cancelPayment(request);
				if (response.hasError()) {
					// if error throw exception to revert transaction. payment txn should not be
					// created.
					Map<String, Object> validateErrParam = new HashMap<>();
					validateErrParam.put("processing-txn-cancel", response.getErr().getMsg());
					throw new ValidationException(mapper.writeValueAsString(validateErrParam));
				} else {
					// this part here it should be NEW and clictruck should expect a callback from
					// clicpay to do the transfer later on.
					if (response.getStatus().equalsIgnoreCase("COMPLETED")) {
						txnEntity.setPtxPaymentState(JobPaymentStates.CANCELLED.name());

						// Create transaction log
						paymentTxnLogService.createPaymentTxnLog(txnEntity,
								JobPaymentStates.CANCELLED.name() + " STATIC VA", principal);

					} else {
						txnEntity.setPtxPaymentState(
								response.getStatus().equalsIgnoreCase("FAILED") ? PaymentStates.FAILED.getCode()
										: response.getStatus());

						// Create transaction log
						paymentTxnLogService.createPaymentTxnLog(txnEntity,
								response.getStatus() + "-" + response.getMessage(), principal);
					}

					if (response.getData() != null) {
						txnEntity.setPtxBankRef(response.getData().getTxnNodeRef());
					}

					// only update if it's successful
					txnEntity.setPtxDtLupd(new Date());
					ckPaymentTxnDao.update(txnEntity);
				}
			} else {
				// Update the txn payment state to CANCELLED. For payment type not specified
				// (e.g. outbound)
				txnEntity.setPtxPaymentState(JobPaymentStates.CANCELLED.name());
				ckPaymentTxnDao.update(txnEntity);

				// Create transaction log
				paymentTxnLogService.createPaymentTxnLog(txnEntity,
						"PAYMENT TRANSACTION " + JobPaymentStates.CANCELLED.name(), principal);
			}

			// Update the jobs associated to this transaction
			updateJobIdsFromCtPayment(txnEntity.getPtxId(), JobPaymentStates.NEW, ckPaymentType);

			// Update tckctpayment
			ckctPaymentService.updateCtPaymentByTxn(txnEntity.getPtxId(), JobPaymentStates.CANCELLED);

		} catch (ValidationException ex) {
			LOG.error("ValidationException in executeCancelPay: ", ex);
			throw ex;

		} catch (Exception ex) {
			LOG.error("Error encountered in executeCancelPay: ", ex);
			throw ex;

		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void executeTerminatePay(String txnId, List<String> terminalJobIdlist) throws Exception {

		TCkPaymentTxn txnEntity = ckPaymentTxnDao.find(txnId);
		if (txnEntity == null)
			throw new EntityNotFoundException("transaction " + txnId + " not found");

		if (txnEntity.getPtxPaymentState().equalsIgnoreCase(JobPaymentStates.PAID.name()))
			throw new ProcessingException("Transaction " + txnId + " already paid!");

		Principal principal = principalUtilService.getPrincipal();

		//
		txnEntity.setPtxPaymentState(JobPaymentStates.TERMINATED.name());
		ckPaymentTxnDao.update(txnEntity);

		List<String> jobIdList = Arrays.asList(txnEntity.getPtxSvcRef().split(","));

		List<String> jobId2NewList = jobIdList.stream().filter(id -> !terminalJobIdlist.contains(id))
				.collect(Collectors.toList());

		for (String jobId : jobId2NewList) {
			TCkJobTruck ckJobEntity = ckJobTruckDao.find(jobId);
			ckJobEntity.setJobOutPaymentState(jobId);
			ckJobEntity.setJobDtLupd(new Date());
			ckJobEntity.setJobUidLupd(principal.getUserId());
			ckJobTruckDao.update(ckJobEntity);

			// update invoice
			jobTruckServiceUtil.updateInv2Status(jobId, ToInvoiceStates.NEW, principal.getUserId(), new Date());

			// update debit note
			jobTruckServiceUtil.updateDnInv2Status(jobId, DebitNoteStates.NEW, principal.getUserId(), new Date());
		}

		ckctPaymentService.updateCtPaymentByTxn(txnId, JobPaymentStates.TERMINATED);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void executeRevertCancelPay(String txnId, CkPaymentTypes ckPaymentType)
			throws ParameterException, EntityNotFoundException, Exception {

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("param principal null");

		TCkPaymentTxn txnEntity = ckPaymentTxnDao.find(txnId);
		if (txnEntity == null)
			throw new EntityNotFoundException("transaction " + txnId + " not found");

		if (txnEntity.getPtxPaymentState().equalsIgnoreCase(JobPaymentStates.PAID.name()))
			throw new ProcessingException("Transaction " + txnId + " already paid!");

		boolean isTxnCancelled = false;
		if (txnEntity.getPtxPayeeBankAccn().equalsIgnoreCase(JobPaymentStates.CANCELLED.name()))
			isTxnCancelled = true;

		// only applicable to inbound and txn is not really cancelled
		if (ckPaymentType == CkPaymentTypes.INBOUND && !isTxnCancelled) {
			String ctInCallBackPayUrl = getConfigFromSysParam(KEY_IN_PAYMENT_CALLBACK_URL);
			if (StringUtils.isBlank(ctInCallBackPayUrl))
				throw new EntityNotFoundException("sysparam key " + KEY_IN_PAYMENT_CALLBACK_URL + " not configured ");
			MakePaymentRequest request = new MakePaymentRequest();
			request.setNode(txnEntity.getPtxMerchantBank());
			request.setServiceID(txnEntity.getTCkMstServiceType().getSvctId());
			request.setRefID(txnEntity.getPtxId());
			request.setVa(txnEntity.getPtxPayerBankAccn());
			request.setCallback(ctInCallBackPayUrl);
			request.setAmount(txnEntity.getPtxAmount());
			request.setCcy(txnEntity.getTMstCurrency().getCcyCode());
			MakePaymentResponse response = paymentGateway.makePayment(request);

			if (response.hasError()) {
				// just in case another user called cancel and got exception too?
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("processing-txn", response.getErr().getMsg());
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			} else {
				if (response.getStatus().equalsIgnoreCase("COMPLETED")) {
					// no need to update t_ck_payment_txn state as it is still paying this time, it
					// just got exception
					paymentTxnLogService.createPaymentTxnLog(txnEntity, "REVERTED STATIC VA CANCELLATION", principal);

				} else {
					paymentTxnLogService.createPaymentTxnLog(txnEntity, "REVERTED STATIC VA CANCELLATION ERROR: "
							+ response.getStatus() + "-" + response.getMessage(), principal);
				}

			}
		}

	}

	@Override
	public CkPaymentTxn payoutAction(String ptxId, PayoutRequest request)
			throws ParameterException, EntityNotFoundException, Exception {

		if (StringUtils.isBlank(ptxId))
			throw new ParameterException("param ptxId null or empty");

		if (request == null)
			throw new ParameterException("param request null");

		if (!request.isValid(request.getAction()))
			throw new ParameterException("not a valid request");

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null) {
			throw new ProcessingException("param principal null");
		}

		CkPaymentTxn txn = ckPaymentTxnService.findById(ptxId);

		checkForSuspendedAccounts(Arrays.asList(txn.getPtxSvcRef().split(",")));

		// Check for concurrent update - if paymentstate is in approved state or paid
		// (PAID will be updated by the scheduler
		// for fundtransfer
		if (txn.getPtxPaymentState().equalsIgnoreCase(PayOutAction.valueOf(request.getAction()).getAltState())
				|| txn.getPtxPaymentState().equalsIgnoreCase(PaymentStates.PAID.getCode())) {
			Map<String, Object> validateErrParam = new HashMap<>();
			validateErrParam.put("processing-txn-concurrent",
					"Payment is already  " + PayOutAction.valueOf(request.getAction()).getAltState());
			throw new ValidationException(mapper.writeValueAsString(validateErrParam));
		}

		txn.setPtxPaymentState(PayOutAction.valueOf(request.getAction()).getAltState());
		txn.setPtxUidLupd(principal.getUserId());

		String txnLogRemarks = "";
		if (request.getAction().equals(PayOutAction.VERIFY_BILL.name())) {
			txn.setPtxDtFinVerified(new Date());
			txn.setPtxUidFinVerified(principal.getUserId());
			// TODO temporary while there is no remarks fieldd yet in the frontend
			txnLogRemarks = "PAYMENT TRANSACTION VERIFIED";
		} else if (request.getAction().equals(PayOutAction.APPROVE_BILL.name())) {
//			Calendar cal = Calendar.getInstance();

			txn.setPtxDtFinApproved(new Date());
			txn.setPtxUidFinApproved(principal.getUserId());

			// 20231031 Nina: Due to change in dn/pf workflow, the ptxn_dt_due is based on
			// the dn/inv draft dt due, when approved, update the ptxn_dt_due to the date
			// the transaction is approved
			// by SP finance head
//			int dueDate = Integer.valueOf(getConfigFromSysParam(IPaymentService.KEY_CLICTRUCK_DEFAULT_PAYTERMS_TO));
//			cal.add(Calendar.DAY_OF_YEAR, dueDate);
//			txn.setPtxDtDue(cal.getTime());

			// TODO temporary while there is no remarks fieldd yet in the frontend
			txnLogRemarks = "PAYMENT TRANSACTION APPROVED";

		}
		ckPaymentTxnService.update(txn, principal);

		// Create payment txn log
		paymentTxnLogService.createPaymentTxnLog(txn.toEntity(new TCkPaymentTxn()), txnLogRemarks, principal);

		// Record FundTransfer to T_CK_CT_TO_PAYMENT
		if (request.getAction().equals(PayOutAction.APPROVE_BILL.name())) {

			String gliBankNode = ckCoreAccnService.getAccnConfig(principal.getCoreAccn().getAccnId(), CONFIG_SP_BANK)
					.getAcfgVal();

			CreateFundTransferRequest ftreq = new CreateFundTransferRequest();
			ftreq.setNode(gliBankNode);
			ftreq.setServiceID(txn.getTCkMstServiceType().getSvctId());
			// store SP TO SL txn id here to use every after response from transfer
			ftreq.setRefID(txn.getPtxId());
			// account of SP/GLI where bank will deduct money from
			ftreq.setSenderAccount(txn.getPtxPayerBankAccn());
			// va of TO
			ftreq.setVa(txn.getPtxPayeeBankAccn());
			ftreq.setBeneficiaryAccount(txn.getPtxPayeeBankAccn());
			ftreq.setBank(txn.getPtxMerchantBank());
			// amount should be dn - pf (+incl tax which should already been computed before
			// inserted into t_ck_ct_payment)); which is already computed when
			// executeInbound is triggered
			ftreq.setAmount(txn.getPtxAmount());
			ftreq.setCcy(Currencies.IDR.getCode());
			// Create t_ck_ct_to_payment record for the scheduler to process payout.
			CkCtToPayment toPayout = toPayoutService.createTruckOperatorPayment(txn, ftreq.toJson(), principal);
			LOG.info("Payout: " + toPayout.getTopId());
		} else if (request.getAction().equals(PayOutAction.VERIFY_BILL.name())) {
			// publish for verify
			eventPublisher.publishEvent(new PaymentStateChangeEvent(this, CkPaymentTypes.OUTBOUND,
					PaymentStates.VER_BILL, txn.toEntity(new TCkPaymentTxn()), principal));
		}

		return txn;
	}

	// Helper Methods
	///////////////////

	static class RequestHolder {
		CreateFundTransferRequest fundTransferRequest;
		List<TCkSvcJournal> journals = new ArrayList<>();
		List<TCkPaymentTxnInvoice> doInvoiceList = new ArrayList<>();
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateJobIdsFromCtPayment(String txnId, JobPaymentStates toState, CkPaymentTypes ckPaymentType)
			throws Exception {
		List<String> jobIdList = new ArrayList<>();
		List<TCkCtPayment> listCtPayment = ckctPaymentService.getCtPaymentByTxn(txnId);
		if (listCtPayment != null && listCtPayment.size() > 0) {
			for (TCkCtPayment cp : listCtPayment) {
				if (!jobIdList.contains(cp.getCtpJob())) {
					jobIdList.add(cp.getCtpJob());
				}
			}
		}

		if (!jobIdList.isEmpty()) {
			Date now = new Date();
			for (String jobId : jobIdList) {

				TCkJobTruck jobTruckEntity = ckJobTruckDao.find(jobId);

				if (jobTruckEntity != null) {

					CkJobTruck ckJobTruck = ckJobTruckService.findById(jobId);
					if (ckPaymentType == CkPaymentTypes.INBOUND) {
						jobTruckEntity.setJobInPaymentState(toState.name());

						// Reverse utilize only if the state is PAID
						if (toState == JobPaymentStates.PAID) {
							if (ckJobTruck != null) {
								truckJobCreditService.reverseUtilized(JournalTxnType.JOB_PAYMENT, ckJobTruck, null);
								// Update Debit Notes and Platform Invoice upon PAID
								this.updateDnAndPfInv(ckJobTruck, txnId, JobPaymentStates.PAID, CkPaymentTypes.INBOUND);
							}
						} else {
							// Update Debit Notes and Platform Invoice upon CANCELATION set to NEW
							this.updateDnAndPfInv(ckJobTruck, txnId, toState, CkPaymentTypes.INBOUND);
						}

					} else {
						jobTruckEntity.setJobOutPaymentState(toState.name());
						// Update Debit Notes and Platform Invoice
						this.updateDnAndPfInv(ckJobTruck, txnId, toState, CkPaymentTypes.OUTBOUND);

					}

					jobTruckEntity.setJobDtLupd(now);
					jobTruckEntity.setJobUidLupd("SYS");
					ckJobTruckDao.update(jobTruckEntity);

				}
			}
		}

	}

	/**
	 * 
	 * @param ckJobTruck
	 * @param state
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateDnAndPfInv(CkJobTruck jobTruck, String txnId, JobPaymentStates state, CkPaymentTypes paymentTypes)
			throws Exception {
		// Update platform invoice state so that scheduler will not pickup.
		String accnId = jobTruck.getTCoreAccnByJobPartyTo().getAccnId();// default to to

		if (paymentTypes == CkPaymentTypes.INBOUND)
			accnId = jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId();

		TCkCtPlatformInvoice pfInv = platformFeeService.getByTruckJobIdAndAccn(jobTruck.getJobId(), accnId);
		if (null != pfInv) {
			TCkCtMstToInvoiceState pfInvState = new TCkCtMstToInvoiceState();
			pfInvState.setInstId(state.name());
			pfInv.setTCkCtMstToInvoiceState(pfInvState);
			// Set invPaymentTxnRef and invDtPaid
			pfInv.setInvPaymentTxnRef(txnId);
			// if (state == JobPaymentStates.PAID) {
			// pfInv.setInvDtPaid(new Date());
			// }
			ckCtPlatformInvDao.update(pfInv);
		}

		if (state == JobPaymentStates.PAID && paymentTypes == CkPaymentTypes.OUTBOUND) {
			// paid outbound successful
			platformInvoiceService.afterPaid2TO(jobTruck, null);
			debitNoteService.afterPaid2TO(jobTruck, null);

		}
		// Update debit note state
		TCkCtDebitNote dn = debiteNoteService.getByTruckJobIdAndAccn(jobTruck.getJobId(),
				paymentTypes == CkPaymentTypes.OUTBOUND
						? ckAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP).getAccnId()
						: accnId);
		if (null != dn) {
			TCkCtMstDebitNoteState dnState = new TCkCtMstDebitNoteState();
			dnState.setDnstId(state.name());
			dn.setTCkCtMstDebitNoteState(dnState);
			// Set dnPaymentTxnRef and dnDtPaid
			dn.setDnPaymentTxnRef(txnId);
			if (state == JobPaymentStates.PAID)
				dn.setDnDtPaid(new Date());
			ckCtDebitNoteDao.update(dn);
		}
	}

	// This method creates an outbound payment from GLI to TO
	@Transactional
	public TCkPaymentTxn createPaymentTxnOutbound(ServiceTypes serviceTypes, List<String> jobIds, String toAccnId,
			Date dueDate, Principal principal) throws Exception {
		Date now = new Date();
		TCkPaymentTxn txn = new TCkPaymentTxn();
		txn.setPtxId(CkUtil.generateIdSynch(CkPaymentTxnService.PREFIX_DO_TXN));
		txn.setPtxDtCreate(now);
		txn.setPtxStatus(RecordStatus.ACTIVE.getCode());
		txn.setPtxUidCreate(principal.getUserId());
		txn.setPtxDtLupd(now);
		txn.setPtxPaymentState(JobPaymentStates.NEW.name());
		txn.setPtxUidLupd(principal.getUserId());
		txn.setPtxDtDue(dueDate);
		txn.setPtxSvcRef(StringUtils.join(jobIds, ","));

		txn.setTMstCurrency(
				new TMstCurrency(Currencies.IDR.getCode(), Currencies.IDR.getDesc(), RecordStatus.ACTIVE.getCode()));
		txn.setTCkMstPaymentType(
				new TCkMstPaymentType(PaymentTypes.BANK_TRANSFER.getId(), PaymentTypes.BANK_TRANSFER.getDesc()));
		txn.setTCkMstServiceType(new TCkMstServiceType(serviceTypes.getId(), serviceTypes.getDesc()));

		// payer is the one who executes the pay
		txn.setTCoreAccnByPtxPayer(principal.getCoreAccn().toEntity(new TCoreAccn()));
		// outpay don't use VA but fund transfer bank account only
		txn.setPtxPayerBankAccn(
				ckCoreAccnService.getAccnConfig(principal.getCoreAccn().getAccnId(), CONFIG_SP_BANK_ACCN).getAcfgVal());

		TCoreAccn toAccount = coreAccnDao.find(toAccnId);
		if (toAccount == null)
			throw new EntityNotFoundException("account " + toAccnId + " not found");

		String toBankDetails = ckCoreAccnService.getAccnConfig(toAccount.getAccnId(), CONFIG_BANK_DETAILS).getAcfgVal();
		if (StringUtils.isBlank(toBankDetails))
			throw new EntityNotFoundException("BANK_DETAILS account config for " + toAccnId + "not  found");

		String[] details = toBankDetails.split(":");
		txn.setTCoreAccnByPtxPayee(toAccount);
		txn.setPtxMerchantBank(details[0]);
		txn.setPtxPayeeBankAccn(details[1]);
		// check to avoid arrayindexoutofboundsexception
		if (details.length == 3) {
			txn.setPtxPayeeBankAccnName(details[2]);
		}
		ckPaymentTxnDao.add(txn);

		// Retrieve from db to commit?
		return ckPaymentTxnDao.find(txn.getPtxId());
	}

	private Map<String, Map<String, List<InvoiceDetails>>> createPaymentInvMap(String accnId, String dateStr,
			List<InvoiceDetails> invList) {

		Map<String, Map<String, List<InvoiceDetails>>> accnMap = new HashMap<String, Map<String, List<InvoiceDetails>>>();
		Map<String, List<InvoiceDetails>> dateStrMap = new HashMap<String, List<InvoiceDetails>>();
		dateStrMap.put(dateStr, invList);
		accnMap.put(accnId, dateStrMap);

		return accnMap;
	}

	public static enum PayOutAction {
		VERIFY("VER", "VERIFIED"), APPROVE("APP", "APPROVED"), REJECT("REJ", "REJECTED"),
		VERIFY_BILL("VER_BILL", "VER_BILL"), APPROVE_BILL("APP_BILL", "APP_BILL");

		private String state;
		private String altState;

		private PayOutAction() {
		}

		private PayOutAction(String state, String altState) {
			this.state = state;
			this.altState = altState;
		}

		public String getState() {
			return state;
		}

		public String getAltState() {
			return altState;
		}

	}

	public static class PayoutRequest {
		String action;

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public boolean isValid(String txt) {
			for (PayOutAction a : PayOutAction.values()) {
				if (a.name().equalsIgnoreCase(txt))
					return true;
			}

			return false;
		}

	}

	public List<String> getActions(String accnType, String role, String paymentId) {
		try {
			List<String> paymentIds = Arrays.asList(paymentId.split(";"));
			List<TCkPaymentTxn> tCkPaymentTxns = ckPaymentTxnDao.findByIds(paymentIds);
			List<String> state = new ArrayList<>();
			for (TCkPaymentTxn tCkPaymentTxn : tCkPaymentTxns) {
				state.add(tCkPaymentTxn.getPtxPaymentState());
			}
			List<String> actions = ckSvcActionMaskService.getActions("CKT", accnType, role, "CLICTRUCK", state);
			return actions;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public MultiRecordResponse multiRecord(MultiRecordRequest request) {
		MultiRecordResponse response = new MultiRecordResponse();
		response.setAccType(request.getAccType());
		response.setAction(request.getAction());
		response.setRole(request.getRole());
		response.getId().addAll(request.getId());
		for (String id : request.getId()) {
			FailedDescription failedDescription = new FailedDescription();
			PayoutRequest payoutRequest = new PayoutRequest();
			payoutRequest.setAction(request.getAction().name());
			boolean isPayoutAction = true;
			try {
				TCkPaymentTxn tCkPaymentTxn = ckPaymentTxnDao.find(id);
				if (tCkPaymentTxn != null) {
					for (String jobId : tCkPaymentTxn.getPtxSvcRef().split(",")) {
						TCkJobTruck tCkJobTruck = ckJobTruckDao.find(jobId);
						Hibernate.initialize(tCkJobTruck.getTCoreAccnByJobPartyCoFf());
						if ('S' == tCkJobTruck.getTCoreAccnByJobPartyCoFf().getAccnStatus()) {
							failedDescription.setId(id);
							failedDescription.setReason("Account is suspended");
							isPayoutAction = false;
							break;
						}
					}
				}
			} catch (Exception e) {
				failedDescription.setId(id);
				failedDescription.setReason(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
			if (isPayoutAction) {
				try {
					payoutAction(id, payoutRequest);
					response.getSuccess().add(id);
					response.setSuspended(false);
				} catch (ParameterException e) {
					failedDescription.setId(id);
					failedDescription.setReason(e.getMessage());
					LOG.error(e.getMessage(), e);
				} catch (EntityNotFoundException e) {
					failedDescription.setId(id);
					failedDescription.setReason(e.getMessage());
					LOG.error(e.getMessage(), e);
				} catch (Exception e) {
					failedDescription.setId(id);
					failedDescription.setReason(e.getMessage());
					LOG.error(e.getMessage(), e);
				}
				if (failedDescription.getId() != null) {
					response.getFailed().add(failedDescription);
				}
			}
		}
		response.setNoSuccess(response.getSuccess().size());
		response.setNoFailed(response.getFailed().size());
		return response;
	}

	public void checkForSuspendedAccounts(List<String> jobs) throws Exception {
		List<String> suspendedAccns = new ArrayList<>();
		// Check for suspended accounts before proceed
		if (!jobs.isEmpty()) {
			for (String jobId : jobs) {
				CkJobTruck jobDto = ckJobTruckService.findById(jobId);
				CoreAccn coFfAccn = jobDto.getTCoreAccnByJobPartyCoFf();
				if (jobDto != null && coFfAccn != null && ckAccnService.isAccountSuspended(coFfAccn)) {
					if (!suspendedAccns.contains(coFfAccn.getAccnId())) {
						suspendedAccns.add(coFfAccn.getAccnId());
					}
				}
			}
		}

		if (!suspendedAccns.isEmpty()) {
			String strAccnsSuspended = StringUtils.join(suspendedAccns, ",");
			Map<String, Object> validateErrParam = new HashMap<>();
			validateErrParam.put("processing-txn-suspended-accn",
					"Unable to proceed. One or more accounts is suspended: " + strAccnsSuspended);
			throw new ValidationException(mapper.writeValueAsString(validateErrParam));
		}
	}

}
