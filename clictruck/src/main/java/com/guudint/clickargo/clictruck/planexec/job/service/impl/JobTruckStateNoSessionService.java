package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clicservice.service.impl.CkSvcWorkflowService;
import com.guudint.clickargo.clictruck.finacing.service.ITruckJobCreditService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobStateChangeEvent;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.job.dao.CkJobRemarksDao;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.dto.CkJobReject;
import com.guudint.clickargo.job.dto.CkJobRemarks;
import com.guudint.clickargo.job.dto.CkJobRemarks.RemarkType;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.job.model.TCkJobRemarks;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;

@Service
public class JobTruckStateNoSessionService {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(JobTruckStateNoSessionService.class);

//	@Autowired
//	private ITruckJobCreditService truckJobCreditService;
//
//	@Autowired
//	private CkSvcWorkflowService workflowService;
//
//	@Autowired
//	private IEntityService<TCkRecordDate, String, CkRecordDate> ckRecordService;
//
//	@Autowired
//	private IEntityService<TCkJob, String, CkJob> ckJobService;
//
//	@Autowired
//	private CkJobRemarksDao ckJobRemarksDao;
//
//	@Autowired
//	protected ApplicationEventPublisher eventPublisher;
//
//	@Autowired
//	protected ClickargoAccnService clickargoAccnService;
//	
//	private ObjectMapper mapper = new ObjectMapper();
//
//	public CkJobTruck rejectJobPayment(CkJobTruck dto, Principal principal)
//			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
//		LOG.debug("rejectJobPayment");
//
//		if (null != dto.getTCkJob().getTCkMstJobState()) {
//			if (dto.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.DLV.name())) {
//				throw new ProcessingException("Already rejected by a different user");
//			}
//		}
//
//		// Call the reserveCredit for reimbursement only since bill job will submit it
//		// for reserve. Do not touch the trip charge
//		// reserved during job submit. Only call reverseJobTruckCredit for
//		// job_submit_reimbursement if there is reimbursement
//		if (dto.getJobTotalReimbursements() == null || dto.getJobTotalReimbursements() != BigDecimal.ZERO) {
//			truckJobCreditService.reverseJobTruckCredit(JournalTxnType.JOB_SUBMIT_REIMBURSEMENT, dto, principal);
//		}
//
//		CkJob ckJob = workflowService.moveState(FormActions.REJECT_BILL, dto.getTCkJob(), principal,
//				ServiceTypes.CLICTRUCK);
//		dto.setTCkJob(ckJob);
//
//		dto.getTCkJob().getTCkRecordDate().setRcdDtBillRejected(new Date());
//		dto.getTCkJob().getTCkRecordDate().setRcdUidBillRejected(principal.getUserId());
//		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
//		ckJobService.update(dto.getTCkJob(), principal);
//
//		workflowService.audit(dto.getJobId(), principal, FormActions.REJECT_BILL);
//		// create reject record
////		createJobReject(dto, principal);
//		createJobRemarks(dto, FormActions.REJECT_BILL, principal);
//
//		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.REJECT_BILL, dto, principal));
//		return dto;
//	}
//
//	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
//	public CkJobTruck verifyJobPayment(CkJobTruck dto, String accnId, Principal principal)
//			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
//		LOG.info("verifyJobPayment entered state:" + dto.getJobId() + " - "
//				+ dto.getTCkJob().getTCkMstJobState().getJbstId());
//
//		checkSuspendedAccount(principal.getUserAccnId());
//
//		checkJobState(dto.getTCkJob().getJobId(), JobStates.VER_BILL);
//		// just check in case the finance officer already verified and approved
//		checkJobState(dto.getTCkJob().getJobId(), JobStates.ACK_BILL);
//
//		CkJob ckJob = workflowService.moveState(FormActions.VERIFY_BILL, dto.getTCkJob(), principal,
//				ServiceTypes.CLICTRUCK);
//		dto.setTCkJob(ckJob);
//		dto.getTCkJob().getTCkRecordDate().setRcdDtBillVerified(new Date());
//		dto.getTCkJob().getTCkRecordDate().setRcdUidBillVerified(principal.getUserId());
//		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
//		ckJobService.update(dto.getTCkJob(), principal);
//		workflowService.audit(dto.getJobId(), principal, FormActions.VERIFY_BILL);
//
//		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.VERIFY_BILL, dto, principal));
//
//		createJobRemarks(dto, FormActions.VERIFY_BILL, principal);
//
//		// call approveJobPayment is principal is also a FF_FINANCE. Call is made here
//		// to not add workflow for BILLED -> APPROVED
//		if (principal.getRoleList().contains(Roles.FF_FINANCE.name())) {
//			Thread.sleep(1000);
//			this.acknowledgeJobPayment(dto, principal);
//		}
//
//		return dto;
//	}
//
//	/**
//	 * This method is called for approve bill action. This will move from ver_bill
//	 * to ack_bill.
//	 */
//	@Override
//	public CkJobTruck acknowledgeJobPayment(CkJobTruck dto, Principal principal)
//			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
//		LOG.info("acknowledgeJobPayment entered state: " + dto.getJobId() + " - "
//				+ dto.getTCkJob().getTCkMstJobState().getJbstId());
//
//		checkSuspendedAccount(principal.getUserAccnId());
//
//		// just check in case the finance officer already verified and approved
//		checkJobState(dto.getTCkJob().getJobId(), JobStates.ACK_BILL);
//
//		CkJob ckJob = workflowService.moveState(FormActions.ACKNOWLEDGE_BILL, dto.getTCkJob(), principal,
//				ServiceTypes.CLICTRUCK);
//		dto.setTCkJob(ckJob);
//		dto.getTCkJob().getTCkRecordDate().setRcdDtBillAcknowledged(new Date());
//		dto.getTCkJob().getTCkRecordDate().setRcdUidBillAcknowledged(principal.getUserId());
//		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
//		ckJobService.update(dto.getTCkJob(), principal);
//		workflowService.audit(dto.getJobId(), principal, FormActions.ACKNOWLEDGE_BILL);
//
//		createJobRemarks(dto, FormActions.APPROVE_BILL, principal);
//
//		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.ACKNOWLEDGE_BILL, dto, principal));
//
//		// If document verification is disabled, call the approveJobPaument here.
//		String gliDocVerEnable = getSysParam(KEY_DOCUMENT_VERIFICATION_ENABLE);
//		LOG.info("CLICTRUCK_DOC_VERIFY_ENABLE = " + gliDocVerEnable);
//		if (gliDocVerEnable != null && gliDocVerEnable.equalsIgnoreCase("N")) {
//			approveJobPayment(dto, principal);
//		}
//
//		return dto;
//	}
//
//	private CkJobRemarks createJobRemarks(CkJobTruck jobTruck, FormActions action, Principal principal)
//			throws Exception {
//		if (jobTruck == null)
//			throw new ParameterException("param jobTruck null");
//
//		// Check if the remarks is empty, no need to proceed
//		if (StringUtils.isNotBlank(jobTruck.getJobRemarks())) {
//			// Query first if there is an existing remark and get the max seq
//			int seqNo = ckJobRemarksDao.getMaxSeq(jobTruck.getTCkJob().getJobId());
//			TCkJobRemarks remarks = new TCkJobRemarks();
//			remarks.setJobrSeq(seqNo);
//			if (action == FormActions.REJECT_BILL) {
//				remarks.setJobrRemarkType(RemarkType.REJECTED.getCode());
//			} else if (action == FormActions.VERIFY_BILL) {
//				remarks.setJobrRemarkType(RemarkType.VERIFIED.getCode());
//			} else if (action == FormActions.APPROVE_BILL) {
//				remarks.setJobrRemarkType(RemarkType.APPROVED.getCode());
//			} else if (action == FormActions.REJECT) {
//				remarks.setJobrRemarkType(RemarkType.JOB_REJECTED.getCode());
//			}
//
//			remarks.setJobrId(CkUtil.generateId(CkJobReject.PREFIX_ID));
//			remarks.setJobrDtRemarks(new Date());
//			remarks.setJobrUidCreated(principal.getUserId());
//			remarks.setJobrReason(jobTruck.getJobRemarks());
//			remarks.setTCkJob(jobTruck.getTCkJob().toEntity(new TCkJob()));
//			ckJobRemarksDao.add(remarks);
//
//			CkJobRemarks dto = new CkJobRemarks(remarks);
//			dto.setTCkJob(new CkJob(remarks.getTCkJob()));
//			return dto;
//		}
//
//		return null;
//	}
//
//	private void checkSuspendedAccount(String accnId) throws Exception {
//		if (StringUtils.isNotBlank(accnId)) {
//			if (clickargoAccnService.isAccountSuspended(accnId)) {
//				Map<String, Object> validateErrParam = new HashMap<>();
//				validateErrParam.put("suspended-accn", "Your account is suspended.");
//				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
//			}
//
//		}
//	}
}
