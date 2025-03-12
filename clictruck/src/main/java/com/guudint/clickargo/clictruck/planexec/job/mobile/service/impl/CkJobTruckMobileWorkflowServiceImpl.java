package com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clicservice.model.TCkSvcWorkflow;
import com.guudint.clickargo.clicservice.service.AbstractWorkflowService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.dto.MstAccnType;

/**
 * Workflow service implementation for mobile related state changes.
 */
@Service("ckJobTruckMobileWorkflowService")
public class CkJobTruckMobileWorkflowServiceImpl extends AbstractWorkflowService<TCkJobTruck, CkJobTruck> {

	@Autowired
	private GenericDao<TCkJob, String> ckJobDao;

	@Override
	public CkJobTruck moveState(FormActions action, CkJobTruck dto, Principal principal, ServiceTypes serviceTypes)
			throws ParameterException, ProcessingException, Exception {
		if (null == principal)
			throw new ParameterException("param principal null");

		if (null == serviceTypes)
			throw new ParameterException("param serviceTypes null");

		if (null == action)
			throw new ParameterException("param action null");

		CkJob ckJob = dto.getTCkJob();
		if (null == ckJob)
			throw new ParameterException("param ckJob null");

		TCkJob ckJobEntity = ckJobDao.find(ckJob.getJobId());
		if (ckJobEntity == null)
			throw new EntityNotFoundException("job " + ckJob.getJobId() + " not found");

		Hibernate.initialize(ckJobEntity.getTCkMstJobState());

		Optional<MstAccnType> opAccnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType());
		if (!opAccnType.isPresent())
			throw new ProcessingException("account type null");

		Optional<List<String>> opAuthRoles = Optional.ofNullable(principal.getRoleList());
		if (!opAuthRoles.isPresent())
			throw new ProcessingException("principal roles null or empty");

		Optional<String> opAppsCode = Optional.ofNullable(principal.getAppsCode());
		if (!opAppsCode.isPresent())
			throw new ProcessingException("principal appscode null or empty");

		Optional<TCkSvcWorkflow> tCkWorkFlowOpt = ckSvcWorkflowDao.findToState(action.name(),
				ckJobEntity.getTCkMstJobState().getJbstId(), serviceTypes.getId(), opAppsCode.get(),
				opAccnType.get().getAtypId(), principal.getRoleList(), "MJOB");

		if (tCkWorkFlowOpt.isPresent()) {
			ckJobEntity.setTCkMstJobState(tCkWorkFlowOpt.get().getTCkMstJobStateByWkflToState());
			ckJobEntity.setJobDtLupd(new Date());
			ckJobEntity.setJobUidLupd(principal.getUserId());
			ckJobDao.update(ckJobEntity);

			String auditEvent = action.getAudit();
			audit(dto.getJobId(), principal, action, auditEvent);

			ckJob.setTCkMstJobState(new CkMstJobState(tCkWorkFlowOpt.get().getTCkMstJobStateByWkflToState()));
			return dto;
		} else {
			throw new ProcessingException(
					String.format("no state machine configured for %s with action [%s] for this principal",
							ckJobEntity.getTCkMstJobState().getJbstId(), action.name()));
		}
	}

}
