package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clicservice.dao.CkSvcWorkflowDao;
import com.guudint.clickargo.clicservice.model.TCkSvcWorkflow;
import com.guudint.clickargo.clicservice.service.ICkWorkflowService;
import com.guudint.clickargo.clictruck.finacing.service.impl.TruckPaymentService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTermReq;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTermReq;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.IJobTruckStateService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.payment.dao.CkPaymentTxnDao;
import com.guudint.clickargo.payment.enums.PaymentStates;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.audit.dao.CoreAuditLogDao;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.dto.MstAccnType;


@Service
public class CkJobTermReqWorkflowService implements ICkWorkflowService<TCkCtJobTermReq, CkCtJobTermReq> {

	private static final Logger LOG = Logger.getLogger(CkJobTermReqWorkflowService.class);


	@Autowired
	private GenericDao<TCkCtJobTermReq, String> ckCtJobTermReqDao;

	@Autowired
	private CkSvcWorkflowDao ckSvcWorkflowDao;

	@Autowired
	protected HttpServletRequest request;

	@Autowired
	private CoreAuditLogDao auditLogDao;

	@Autowired
	private CkPaymentTxnDao paymentTxnDao;

	@Autowired
	private IJobTruckStateService<CkJobTruck> jobTruckStateService;
	
    @Autowired
    private TruckPaymentService truckPaymentService;

	@Autowired
	private CkJobTruckService ckJobTruckService;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtJobTermReq moveState(FormActions action, CkCtJobTermReq dto, Principal principal, ServiceTypes serviceTypes)
			throws ParameterException, ProcessingException, Exception {
		
		try {
			if (null == action)
				throw new ParameterException("param action null");
			if (null == dto)
				throw new ParameterException("param dto null");
			if (null == principal)
				throw new ParameterException("param principal null");
			if (null == serviceTypes)
				throw new ParameterException("param serviceTypes null");

			TCkCtJobTermReq tckctJobTermReq = ckCtJobTermReqDao.find(dto.getJtrId());
			if (tckctJobTermReq == null)
				throw new EntityNotFoundException("trip rate " + dto.getJtrId() + "not found");

			List<TCkSvcWorkflow>  listWfState = this.getNextWorfkflowStatus(action, principal, serviceTypes, tckctJobTermReq);
			
			TCkSvcWorkflow wkfl = listWfState.get(0);
			
			tckctJobTermReq
					.setJtrState(wkfl.getTCkMstJobStateByWkflToState().getJbstId());
			tckctJobTermReq.setJtrDtLupd(new Date());
			tckctJobTermReq.setJtrUidLupd(principal.getUserId());
			
			switch (action) {
			case SUBMIT:
				tckctJobTermReq.setJtrCommentRequestor(dto.getJtrCommentRequestor());
				tckctJobTermReq.setJtrUidRequestor(principal.getUserId());
				tckctJobTermReq.setJtrDtSubmit(new Date());
				break;
			case APPROVE:
				tckctJobTermReq.setJtrCommentApprover(dto.getJtrCommentApprover());
				tckctJobTermReq.setJtrUidApprover(principal.getUserId());
				tckctJobTermReq.setJtrDtApproveReject(new Date());
				break;
			case REJECT:
				tckctJobTermReq.setJtrCommentApprover(dto.getJtrCommentApprover());
				tckctJobTermReq.setJtrUidApprover(principal.getUserId());
				tckctJobTermReq.setJtrDtApproveReject(new Date());
				break;
			default:
				break;
			}
			
			
			ckCtJobTermReqDao.update(tckctJobTermReq);

			LOG.info("moveState " + tckctJobTermReq.getJtrState() + " -> " + wkfl.getTCkMstJobStateByWkflToState().getJbstId());
			
			if(FormActions.APPROVE == action ) {
				// list all 
				List<TCkJobTruck> jobList = tckctJobTermReq.getTCkCtJobTerms().stream().map( jt -> jt.getTCkJobTruck()).collect(Collectors.toList());
				List<String> jobIdList = tckctJobTermReq.getTCkCtJobTerms().stream().map( jt -> jt.getTCkJobTruck().getJobId()).collect(Collectors.toList());

				// terminate job
				for(TCkJobTruck job: jobList) {
					CkJobTruck jobDto = ckJobTruckService.findById(job.getJobId());
					jobTruckStateService.terminateJob( jobDto, principal);
				}
				// list all payment transaction;
				List<String> paymentTxnIdList = new ArrayList<>();
				for(String jobId: jobIdList) {
					
					List<TCkPaymentTxn> paymentTxnList = paymentTxnDao.findByJobId(jobId);
					
					if( null != paymentTxnList && paymentTxnList.size() > 0) {
						
						for(TCkPaymentTxn paymentTxn: paymentTxnList ) {
							// new paying
							if( PaymentStates.NEW.name().equalsIgnoreCase(paymentTxn.getPtxPaymentState())
									|| PaymentStates.PAYING.name().equalsIgnoreCase(paymentTxn.getPtxPaymentState())) {
								
								if( !paymentTxnIdList.contains(paymentTxn.getPtxId())) {
									paymentTxnIdList.add(paymentTxn.getPtxId());
								}
							}
						}
					}
				}
				//
				for( String paymentTxnId: paymentTxnIdList) {
					truckPaymentService.executeTerminatePay(paymentTxnId, jobIdList);
				}
				
			}
			
			audit(tckctJobTermReq.getJtrId(), principal, action);
			return new CkCtJobTermReq(tckctJobTermReq);

		} catch (Exception e) {
			LOG.error("moveState");
			throw e;
		}
	}
	
	private List<TCkSvcWorkflow> getNextWorfkflowStatus(FormActions action, Principal principal, ServiceTypes serviceTypes, TCkCtJobTermReq tCkCtTripRate) throws Exception {

		Optional<MstAccnType> opAccnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType());
		if (!opAccnType.isPresent())
			throw new ProcessingException("account type null");

		Optional<List<String>> opAuthRoles = Optional.ofNullable(principal.getRoleList());
		if (!opAuthRoles.isPresent())
			throw new ProcessingException("principal roles null or empty");

		Optional<String> opAppsCode = Optional.ofNullable(principal.getAppsCode());
		if (!opAppsCode.isPresent())
			throw new ProcessingException("principal appscode null or empty");

		StringBuilder str = new StringBuilder("from TCkSvcWorkflow o where o.wkflStatus=:wkflStatus");
		str.append(" and o.TCkMstFormAction.fmactId=:action");
		str.append(" and o.TCkMstJobStateByWkflFromState.jbstId=:fromState");
		str.append(" and o.TCkMstServiceType.svctId=:serviceType");
		str.append(" and o.TCoreApps.appsCode=:appsCode");
		str.append(" and o.TMstAccnType.atypId=:accnType");
		str.append(" and o.TCoreRole.id.roleId in (:roles)");
		//to only fetch those that has RT or rate table id
		str.append(" and o.wkflId LIKE 'OT%'"); // This OT(Order Termination) is very important.

		Map<String, Object> params = new HashMap<>();
		params.put("wkflStatus", RecordStatus.ACTIVE.getCode());
		params.put("action", action.name());

		params.put("fromState", tCkCtTripRate.getJtrState());
		params.put("serviceType", serviceTypes.getId());
		params.put("appsCode", opAppsCode.get());
		params.put("accnType", opAccnType.get().getAtypId());
		params.put("roles", opAuthRoles.get());

		List<TCkSvcWorkflow> listWfState = ckSvcWorkflowDao.getByQuery(str.toString(), params);
		if (listWfState == null || listWfState.size() <= 0 || listWfState.isEmpty())
			throw new ProcessingException("no state machine configured for " + tCkCtTripRate.getJtrStatus()
					+ " with action  [" + action.name() + "] for this principal");
		
		return listWfState;
	}

	public void audit(String key, Principal principal, FormActions action) {
		Date now = Calendar.getInstance().getTime();
		try {
			if (null == principal)
				throw new ParameterException("param principal null");
			if (action == null)
				throw new ParameterException("param action null");

			TCoreAuditlog auditLog = new TCoreAuditlog();
			auditLog.setAudtId(CkUtil.generateId("TRAL"));
			auditLog.setAudtReckey(key);
			auditLog.setAudtTimestamp(now);
			switch (action) {
			case SUBMIT:
				auditLog.setAudtEvent("JOB TERMINATION SUBMITTED");
				break;
			case VERIFY:
				auditLog.setAudtEvent("JOB TERMINATION VERIFIED");
				break;
			case APPROVE:
				auditLog.setAudtEvent("JOB TERMINATION APPROVED");
				break;
			case ACTIVATE:
				auditLog.setAudtEvent("JOB TERMINATION ACTIVATED");
				break;
			case DEACTIVATE:
				auditLog.setAudtEvent("JOB TERMINATION DEACTIVATED");
				break;
			case REJECT:
				auditLog.setAudtEvent("JOB TERMINATION REJECTED");
				break;
			default:
				break;

			}

			auditLog.setAudtUid(principal.getUserId());
			auditLog.setAudtRemoteIp(getLocalAddress());
			auditLog.setAudtUname(principal.getUserName());
			auditLog.setAudtRemarks("-");
			auditLogDao.add(auditLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FormActions convert2FormActionByJobAction(JobActions action) throws ParameterException {

		if (action == null)
			throw new ParameterException("param action is null");

		switch (action) {
		case SUBMIT:
			return FormActions.SUBMIT;
		case VERIFY:
			return FormActions.VERIFY;
		case APPROVE:
			return FormActions.APPROVE;
		case REJECT:
			return FormActions.REJECT;
		case DELETE:
			return FormActions.NEW;
		default:
			return null;
		}

	}

	public String getLocalAddress() {
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (StringUtils.isEmpty(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	public List<String> getPermissionState2Termation() {
		List<JobStates> statesList = new ArrayList<>(Arrays.asList(JobStates.values()));
		statesList.remove(JobStates.PAID);
		statesList.remove(JobStates.NEW);
		statesList.remove(JobStates.DRF);
		statesList.remove(JobStates.CAN);
		statesList.remove(JobStates.TERMINATED);
		
		List<String> permission2TerminationList = statesList.stream().map( s -> s.name()).collect(Collectors.toList());
		return permission2TerminationList;
	}
}
