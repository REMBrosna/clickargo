package com.guudint.clickargo.clictruck.finacing.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToPayment;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.finacing.event.PaymentStateChangeEvent;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToPayment;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService.CkPaymentTypes;
import com.guudint.clickargo.clictruck.finacing.service.ITruckOperatorPayoutService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtPaymentService;
import com.guudint.clickargo.common.CkErrorCodes;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.external.dto.CreateFundTransferRequest;
import com.guudint.clickargo.external.dto.CreateFundTransferResponse;
import com.guudint.clickargo.external.services.IPaymentGateway;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.payment.dto.CkPaymentTxn;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.guudint.clickargo.payment.service.impl.CkPaymentTxnLogService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.util.email.SysParam;

@Service
public class TruckOperatorPayOutServiceImpl implements ITruckOperatorPayoutService {

	private static Logger log = Logger.getLogger(TruckOperatorPayOutServiceImpl.class);

	@Autowired
	@Qualifier("ckCtToPaymentDao")
	private GenericDao<TCkCtToPayment, String> ckCtToPaymentDao;

	@Autowired
	private GenericDao<TCkPaymentTxn, String> ckPaymentTxnDao;

	@Autowired
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Autowired
	private CkCtPaymentService ckctPaymentService;

	@Autowired
	private TruckPaymentService truckPaymentService;

	@Autowired
	@Qualifier("paymentGatewayService")
	private IPaymentGateway paymentGateway;

	@Autowired
	@Qualifier("ckJobTruckService")
	protected IEntityService<TCkJobTruck, String, CkJobTruck> ckJobTruckService;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	private SysParam sysParam;

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected NotificationService notificationService;

	@Autowired
	protected CkPaymentTxnLogService paymentTxnLogService;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public CkCtToPayment createTruckOperatorPayment(CkPaymentTxn txn, String payload, Principal principal)
			throws ParameterException, EntityNotFoundException, Exception {

		if (txn == null)
			throw new ParameterException("param txn null");

		if (principal == null)
			throw new ParameterException("param principal null");

		// Check if there is no existing active job for payment
		CkCtToPayment dto = getTruckOperatorPayment(txn.getPtxId());
		if (dto != null)
			throw new ProcessingException(
					"Trucking Operator Payout already created for transaction: " + txn.getPtxId());

		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		TCkCtToPayment toPayOut = new TCkCtToPayment();
		toPayOut.setTopId(CkUtil.generateId(CkCtToPayment.PREFIX_ID));
		// The trucking operator account
		toPayOut.setTCoreAccn(txn.getTCoreAccnByPtxPayee().toEntity(new TCoreAccn()));

		toPayOut.setTopJson(payload);
		toPayOut.setTopDtTransfer(txn.getPtxDtDue() == null ? calendar.getTime() : txn.getPtxDtDue());
		toPayOut.setTopAmt(txn.getPtxAmount());
		toPayOut.setTopReference(txn.getPtxId());
		toPayOut.setTopStatus(CkCtToPayment.Status.NEW.getCode());
		toPayOut.setTopDtCreate(now);
		toPayOut.setTopUidCreate(principal.getUserId());
		toPayOut.setTopUidCreate(principal.getUserId());
		toPayOut.setTopDtLupd(now);
		ckCtToPaymentDao.add(toPayOut);

		CkCtToPayment toPayoutDto = new CkCtToPayment(toPayOut);
		toPayoutDto.setTCoreAccn(new CoreAccn(toPayOut.getTCoreAccn()));
		return toPayoutDto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtToPayment getTruckOperatorPayment(String txnId)
			throws ParameterException, EntityNotFoundException, Exception {
		String hql = "from TCkCtToPayment o where o.topReference=:txnId and o.topStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("txnId", txnId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtToPayment> listToPayment = ckCtToPaymentDao.getByQuery(hql, params);
		if (listToPayment != null && listToPayment.size() > 0) {
			// expected only one
			TCkCtToPayment topEntity = listToPayment.get(0);
			Hibernate.initialize(topEntity.getTCoreAccn());
			CkCtToPayment topDto = new CkCtToPayment(topEntity);
			topDto.setTCoreAccn(new CoreAccn(topEntity.getTCoreAccn()));
			return topDto;
		}

		return null;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public synchronized TCkCtToPayment executeFundsTransfer(TCkCtToPayment entity)
			throws ParameterException, EntityNotFoundException, Exception {
		try {

			if (entity == null)
				throw new ParameterException("param dto null");

			if (StringUtils.isBlank(entity.getTopReference()))
				throw new ParameterException("param txn reference null or empty");

			// 1: verify before payment.
			try {
				this.verifyBeforePayment(entity);
			} catch (Exception e) {
				log.error("Fail to verify before payment.", e);
				this.updatePaymentStatus(entity, CkCtToPayment.Status.Error);
				throw e;
			}

			// 2: update status to Paying before payment
			this.updatePaymentStatus(entity, CkCtToPayment.Status.PAYING);

			// 3: transfer
			CreateFundTransferRequest req = mapper.readValue(entity.getTopJson(), CreateFundTransferRequest.class);
			CreateFundTransferResponse resp = paymentGateway.createFundTransferBIFast(req);

			Date now = new Date();

			// 4: update status by payment status
			this.updateFundsTransferResult(!resp.hasError(), entity, now);
			
			if(resp.hasError()) {
				// Fail to payment
				this.sendEmailNotification("Fail to pay, id: " + entity.getTopId() + "  ref: " + entity.getTopReference());
			}

		} catch (Exception ex) {
			COException.create(COException.ERROR, CkErrorCodes.ERR_JOB_EXCEPTION, CkErrorCodes.MSG_JOB_EXCEPTION,
					"FundTransfer Error", ex);
			// do not throw exception, so that do not rollback transaction.

			// send notification
			this.sendEmailNotification(ex.getMessage());
		}
		return null;
	}

	/**
	 * Retrieves the records eligible for payment via funds transfer using current
	 * date and status.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<TCkCtToPayment> getRecordsForPayout(Date dtFundTransfer, Character... status)
			throws ParameterException, Exception {

		if (dtFundTransfer == null)
			throw new ParameterException("param dtFundTransfer null");
		if (status == null)
			throw new ParameterException("param status null");

		List<TCkCtToPayment> listDto = new ArrayList<>();

		String hql = "from TCkCtToPayment o where o.topDtTransfer <= :dtFundTransfer" + " and o.topStatus in (:status)";
		Map<String, Object> params = new HashMap<>();
		params.put("dtFundTransfer", dtFundTransfer);
		params.put("status", Arrays.asList(status));
		List<TCkCtToPayment> listToPayment = ckCtToPaymentDao.getByQuery(hql, params);
		if (listToPayment != null && listToPayment.size() > 0) {
			for (TCkCtToPayment obj : listToPayment) {
				Hibernate.initialize(obj.getTCoreAccn());
				listDto.add(obj);
			}
		}
		return listDto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateFundsTransferResult(boolean isPaymentSuccessful, String tCkCtToPaymentId, Date paymentDate)
			throws Exception {

		TCkCtToPayment entity = ckCtToPaymentDao.find(tCkCtToPaymentId);

		this.updateFundsTransferResult(isPaymentSuccessful, entity, paymentDate);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateFundsTransferResult(boolean isPaymentSuccessful, TCkCtToPayment entity, Date paymentDate)
			throws Exception {

		TCkPaymentTxn txn = ckPaymentTxnDao.find(entity.getTopReference());

		if (txn == null)
			throw new EntityNotFoundException("txn not found for : " + entity.getTopReference());

		String txnLogRemarks = "";
		if (!isPaymentSuccessful) {
			// Failed
			txn.setPtxPaymentState(PaymentStates.FAILED.getCode());
			txn.setPtxUidLupd(Constant.ACCN_CREATE_SYS_USER);
			txn.setPtxDtLupd(paymentDate);
			ckPaymentTxnDao.update(txn);

			// TODO temporary while there is not remarks from frontend yet
			txnLogRemarks = "TRANSACTION FAILED";

			entity.setTopDtLupd(paymentDate);
			entity.setTopUidLupd(Constant.ACCN_CREATE_SYS_USER);
			entity.setTopStatus(CkCtToPayment.Status.FAILED.getCode());
			ckCtToPaymentDao.update(entity);

			// publish event for email notification
			eventPublisher.publishEvent(
					new PaymentStateChangeEvent(this, CkPaymentTypes.OUTBOUND, PaymentStates.FAILED, txn, null));
		} else {

			entity.setTopDtLupd(paymentDate);
			entity.setTopUidLupd(Constant.ACCN_CREATE_SYS_USER);
			entity.setTopStatus(CkCtToPayment.Status.SUCCESS.getCode());
			ckCtToPaymentDao.update(entity);
			
			this.updateFundsTransferResultSuccessfulByPaymentTxn(txn, paymentDate);
		}

		// Create payment txn log
		paymentTxnLogService.createPaymentTxnLog(txn, txnLogRemarks, null);
	}
	
	/**
	 * OPM workflow will call this function.
	 * @param txn
	 * @param paymentDate
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void updateFundsTransferResultSuccessfulByPaymentTxn(TCkPaymentTxn txn, Date paymentDate) throws Exception {

		// success
		txn.setPtxPaymentState(PaymentStates.PAID.getCode());
		txn.setPtxUidLupd(Constant.ACCN_CREATE_SYS_USER);
		txn.setPtxDtLupd(paymentDate);
		txn.setPtxDtPaid(paymentDate);
		ckPaymentTxnDao.update(txn);

		// Update tckCtPayment
		ckctPaymentService.updateCtPaymentByTxn(txn.getPtxId(), JobPaymentStates.PAID);
		// Update the jobs
		truckPaymentService.updateJobIdsFromCtPayment(txn.getPtxId(), JobPaymentStates.PAID,
				CkPaymentTypes.OUTBOUND);

		// publish event for email notification
		eventPublisher.publishEvent(
				new PaymentStateChangeEvent(this, CkPaymentTypes.OUTBOUND, PaymentStates.PAID, txn, null));
		
	}

	/**
	 * Get TCkCtToPayment by accnId (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.clictruck.finacing.service.ITruckOperatorPayoutService#
	 *      getByTruckJobIdAndAccn(java.lang.String)
	 *
	 */
	@Override
	public List<TCkCtToPayment> getByAccnId(String accnId) throws Exception {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(TCkCtToPayment.class);
//	        criteria.add(Restrictions.eq("topStatus", RecordStatus.ACTIVE.getCode()));
			criteria.add(Restrictions.eq("TCoreAccn.accnId", accnId));
			return ckCtToPaymentDao.getByCriteria(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Check the associated jobs under the payment transaction if the COFF account
	 * is suspended or not. If at least one account is suspended, the associated
	 * transactions will be cancelled and other jobs will be reverted for payment.
	 */
	@Override
	public List<String> checkJobsPayoutSuspendedAccount(TCkCtToPayment entity) throws Exception {
		// TODO Auto-generated method stub
		if (entity == null)
			throw new ParameterException("param toPayment null");
		if (StringUtils.isBlank(entity.getTopReference()))
			throw new ParameterException("param top_reference null or empty");

		// Retrieve related tckpaymenttxn of this ckcttopayment
		TCkPaymentTxn txn = ckPaymentTxnDao.find(entity.getTopReference());
		if (txn == null)
			throw new EntityNotFoundException("txn not found for : " + entity.getTopReference());

		List<CkJobTruck> txnJobList = new ArrayList<>();
		List<String> suspendedAccns = new ArrayList<>();
		// Iterate through the PTX_SVC_REF and check if account is suspended
		if (StringUtils.isNotBlank(txn.getPtxSvcRef())) {
			// txn can have multiple jobs associated to it separated in comma
			List<String> jobs = Arrays.asList(txn.getPtxSvcRef().split(","));
			if (!jobs.isEmpty()) {
				for (String jobId : jobs) {
					CkJobTruck jobDto = ckJobTruckService.findById(jobId);
					txnJobList.add(jobDto);
					CoreAccn coFfAccn = jobDto.getTCoreAccnByJobPartyCoFf();
					if (jobDto != null && coFfAccn != null
							&& coFfAccn.getAccnStatus() == RecordStatus.SUSPENDED.getCode()) {
						if (!suspendedAccns.contains(coFfAccn.getAccnId())) {
							suspendedAccns.add(coFfAccn.getAccnId());
						}
					}
				}
			}
		}

		// if the mapAccnStatus is not empty, that means there's at least one account is
		// suspended
		if (!suspendedAccns.isEmpty()) {
			// Update the txn
			txn.setPtxPaymentState(PaymentStates.FAILED.getCode());
			txn.setPtxUidLupd(Constant.ACCN_CREATE_SYS_USER);
			txn.setPtxDtLupd(new Date());
			ckPaymentTxnDao.update(txn);

			// Create payment txn log
			paymentTxnLogService.createPaymentTxnLog(txn,
					"TRANSACTION FAILED DUE TO SUSPENDED ACCOUNT(S): " + StringUtils.join(suspendedAccns, ","), null);

			// cancel the ct payment txn
			ckctPaymentService.updateCtPaymentByTxn(txn.getPtxId(), JobPaymentStates.FAILED);
			// Update the jobs out payment state by setting it to NEW again
			truckPaymentService.updateJobIdsFromCtPayment(txn.getPtxId(), JobPaymentStates.NEW,
					CkPaymentTypes.OUTBOUND);

			return suspendedAccns;
		}

		return null;
	}

	private void verifyBeforePayment(TCkCtToPayment entity) throws Exception {

		TCkPaymentTxn txn = ckPaymentTxnDao.find(entity.getTopReference());

		// 1: check payment status in TCkPaymentTxn

		if (JobPaymentStates.PAID.name().equalsIgnoreCase(txn.getPtxPaymentState())) {
			// already paid in TCkPaymentTxn
			throw new Exception(String.format(
					"Already paid in TCkPaymentTxn, TCkCtToPayment id: %s, : TCkPaymentTxn id: %s is paid.",
					entity.getTopId(), entity.getTopReference()));
		}

		// 2: check job status and job CO is not suspend
		String[] jobIds = txn.getPtxSvcRef().split(",");

		for (String jobId : jobIds) {

			TCkJobTruck jobTruck = ckJobTruckDao.find(jobId);

			// 2.1 make sure job is existing
			if (null == jobTruck) {
				// fail to find job by jobId
				throw new Exception(
						String.format("Fail to find job id: %s in payment transaction: %s : ", jobId, txn.getPtxId()));
			}
			// 2.2 make sure job is APP_BILL status

			// 2.3 job pay out status is not PAID

			// 2.4 CO is not S(suspend) status
			if (Constant.SUSPENDED_STATUS == jobTruck.getTCoreAccnByJobPartyCoFf().getAccnStatus()) {
				throw new Exception(String.format("Job: %s, CO: %s is suspend.",
						jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId(), jobId));
			}
		}

		// 3: call clicPay query API, to make sure not duplicate pay.

		// 4: verify amount with Mysql native query, to make sure amount is correct.
	}

	/**
	 * Update payment status to P(Paying), make sure not revert(roll back) to N(new)
	 * again
	 * 
	 * @param entity
	 * @throws Exception
	 */
	private void updatePaymentStatus(TCkCtToPayment entity, CkCtToPayment.Status paymentStatus) throws Exception {
		entity.setTopStatus(paymentStatus.getCode());
		entity.setTopDtLupd(new Date());
		entity.setTopUidLupd(Constant.ACCN_CREATE_SYS_USER);
		ckCtToPaymentDao.update(entity);
	}

	private void sendEmailNotification(String msg) {

		try {
			NotificationParam param = new NotificationParam();
			param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
			param.setTemplateId(ClicTruckNotifTemplates.SP_PAY_TXN_FAILED.getId());

			String recipientStr = sysParam.getValString("CLICTRUCK_SYSTEM_MONITOR_NOTIFY",
					"nina.catudio@guud.company,Zhang.Ji@guud.company");

			HashMap<String, String> contentFields = new HashMap<>();
			contentFields.put(":sp_details", "[" + CkUtil.getComputerName() + "]" + msg);
			param.setContentFeilds(contentFields);

			param.setRecipients(new ArrayList<>(Arrays.asList(recipientStr.split(","))));

			// send twice
			notificationUtilService.saveNotificationLog(param.toJson(), null, true);
			notificationService.notifySyn(param);

		} catch (Exception e) {
			log.error("Fail to send payment email notification", e);
			// catch exception, make sure doesn't rollback JDBC transaction.
		}

	}

}
