package com.guudint.clickargo.clictruck.admin.contract.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clicservice.model.TCkSvcWorkflow;
import com.guudint.clickargo.clicservice.service.AbstractWorkflowService;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtMstContractReqState;
import com.guudint.clickargo.clictruck.admin.contract.dto.ContractReqStateEnum;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtMstContractReqState;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.dto.MstAccnType;

@Service("contractReqWorkflowService")
public class ContractReqWorkflowServiceImpl extends AbstractWorkflowService<TCkCtContractReq, CkCtContractReq> {

	private static final Logger LOG = Logger.getLogger(ContractReqWorkflowServiceImpl.class);

	@Autowired
	@Qualifier("ckCtContractReqDao")
	private GenericDao<TCkCtContractReq, String> ckCtContractReqDao;

	@Autowired
	@Qualifier("ckCtMstContractReqStateDao")
	private GenericDao<TCkCtMstContractReqState, String> ckCtMstContractReqStateDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtContractReq moveState(FormActions action, CkCtContractReq dto, Principal principal,
			ServiceTypes serviceTypes) throws ParameterException, ProcessingException, Exception {
		try {
			if (null == action)
				throw new ParameterException("param action null");
			if (null == dto)
				throw new ParameterException("param dto null");
			if (null == principal)
				throw new ParameterException("param principal null");
			if (null == serviceTypes)
				throw new ParameterException("param serviceTypes null");

			TCkCtContractReq crEntity = ckCtContractReqDao.find(dto.getCrId());
			if (crEntity == null)
				throw new EntityNotFoundException("contract request " + dto.getCrId() + "not found");

			Hibernate.initialize(crEntity.getTCkCtMstContractReqState());

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
			// to only fetch those that has RT or rate table id
			Optional<TCkCtMstContractReqState> opState = Optional.ofNullable(crEntity.getTCkCtMstContractReqState());
			if (!opState.isPresent())
				throw new ProcessingException("No state for " + dto.getCrId());

			boolean isNewReq = false;
			if (StringUtils.isNotBlank(opState.get().getStId())
					&& StringUtils.startsWith(opState.get().getStId(), "NEW_")
					&& !StringUtils.equalsIgnoreCase(ContractReqStateEnum.NEW_UPDATE.name(), opState.get().getStId())) {
				str.append(" and o.wkflId LIKE 'NEWCTRCT%'");
				isNewReq = true;
			} else if(StringUtils.isNotBlank(opState.get().getStId())
					&& StringUtils.startsWith(opState.get().getStId(), "RENEWAL_")){
				str.append(" and o.wkflId LIKE 'RNCTRCT%'");
			}else {
				str.append(" and o.wkflId LIKE 'UPCTRCT%'");
			}

			Map<String, Object> params = new HashMap<>();
			params.put("wkflStatus", RecordStatus.ACTIVE.getCode());
			params.put("action", action.name());

			params.put("fromState", ContractReqStateEnum.getAltCodeByState(opState.get().getStId()));
			params.put("serviceType", serviceTypes.getId());
			params.put("appsCode", opAppsCode.get());
			params.put("accnType", opAccnType.get().getAtypId());
			params.put("roles", opAuthRoles.get());

			List<TCkSvcWorkflow> listWfState = ckSvcWorkflowDao.getByQuery(str.toString(), params);
			if (listWfState == null || listWfState.size() <= 0 || listWfState.isEmpty())
				throw new ProcessingException("no state machine configured for " + opState.get().getStId()
						+ " with action  [" + action.name() + "] for this principal");

			TCkSvcWorkflow toStateWf = listWfState.get(0);
			String newState = ContractReqStateEnum.getStateByAltCode(isNewReq,
					toStateWf.getTCkMstJobStateByWkflToState().getJbstId());
			crEntity.setTCkCtMstContractReqState(new TCkCtMstContractReqState(newState));
			crEntity.setCrDtLupd(new Date());
			crEntity.setCrUidLupd(principal.getUserId());
			ckCtContractReqDao.update(crEntity);

			LOG.info("moveState " + opState.get().getStId() + " -> " + newState);

			String event = "";
			switch (action) {
			case SUBMIT:
				event = isNewReq ? "NEW CONTRACT REQUEST SUBMITTED" : "CONTRACT UPDATE REQUEST SUBMITTED";
				break;
			case APPROVE:
				event = isNewReq ? "NEW CONTRACT REQUEST APPROVED" : "CONTRACT UPDATE REQUEST APPROVED";
				break;
			case DELETE:
				event = "CONTRACT REQUEST DELETED";
				break;
			case REJECT:
				event = isNewReq ? "NEW CONTRACT REQUEST REJECTED" : "CONTRACT UPDATE REQUEST REEJECTED";
				break;
			default:
				break;

			}
			audit(dto.getCrId(), principal, action, event);
			
			dto.setTCkCtMstContractReqState(new CkCtMstContractReqState(crEntity.getTCkCtMstContractReqState()));
			return dto;

		} catch (Exception e) {
			LOG.error("moveState");
			throw e;
		}
	}

}
