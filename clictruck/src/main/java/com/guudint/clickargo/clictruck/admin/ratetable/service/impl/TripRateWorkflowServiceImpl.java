package com.guudint.clickargo.clictruck.admin.ratetable.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clicservice.model.TCkSvcWorkflow;
import com.guudint.clickargo.clicservice.service.AbstractWorkflowService;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.dto.MstAccnType;

/**
 * Implement the workflow for ratetable. You may refer to CkSvcWorkflowService.
 */
@Service("tripRateWorkflowService")
public class TripRateWorkflowServiceImpl extends AbstractWorkflowService<TCkCtTripRate, CkCtTripRate> {

	private static final Logger LOG = Logger.getLogger(TripRateWorkflowServiceImpl.class);

	public static enum TripRateStatus {
		NEW('N'), SUB('S'), VER('V'), APP('A'), INACTIVE('I');

		private char status;

		TripRateStatus(char status) {
			this.status = status;
		}

		public char getStatusCode() {
			return this.status;
		}

		public static String getNameByValue(char status) {
			for (TripRateStatus e : TripRateStatus.values()) {
				if (e.getStatusCode() == status)
					return e.name();
			}
			return null;
		}

		public static char getCodeByName(String name) {

			for (TripRateStatus e : TripRateStatus.values()) {
				if (e.name().equalsIgnoreCase(name))
					return e.getStatusCode();
			}

			return '\0';
		}

	}

	@Autowired
	private GenericDao<TCkCtTripRate, String> ckCtTripRateDao;

	@Override
	public CkCtTripRate moveState(FormActions action, CkCtTripRate dto, Principal principal, ServiceTypes serviceTypes)
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

			TCkCtTripRate tCkCtTripRate = ckCtTripRateDao.find(dto.getTrId());
			if (tCkCtTripRate == null)
				throw new EntityNotFoundException("trip rate " + dto.getTrId() + "not found");

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
			str.append(" and o.wkflId LIKE 'RT%'");

			Map<String, Object> params = new HashMap<>();
			params.put("wkflStatus", RecordStatus.ACTIVE.getCode());
			params.put("action", action.name());
			Character status = tCkCtTripRate.getTrStatus();
			if (null == status)
				throw new ProcessingException("param status null");

			params.put("fromState", TripRateStatus.getNameByValue(status));
			params.put("serviceType", serviceTypes.getId());
			params.put("appsCode", opAppsCode.get());
			params.put("accnType", opAccnType.get().getAtypId());
			params.put("roles", opAuthRoles.get());

			List<TCkSvcWorkflow> listWfState = ckSvcWorkflowDao.getByQuery(str.toString(), params);
			if (listWfState == null || listWfState.size() <= 0 || listWfState.isEmpty())
				throw new ProcessingException("no state machine configured for " + tCkCtTripRate.getTrStatus()
						+ " with action  [" + action.name() + "] for this principal");

			TCkSvcWorkflow toStateWf = listWfState.get(0);
			tCkCtTripRate
					.setTrStatus(TripRateStatus.getCodeByName(toStateWf.getTCkMstJobStateByWkflToState().getJbstId()));
			tCkCtTripRate.setTrDtLupd(new Date());
			tCkCtTripRate.setTrUidLupd(principal.getUserId());
			ckCtTripRateDao.update(tCkCtTripRate);

			LOG.info("moveState " + status + " -> " + toStateWf.getTCkMstJobStateByWkflToState().getJbstId());

			String event = "";
			switch (action) {
			case SUBMIT:
				event = "TRIP RATE SUBMITTED";
				break;
			case VERIFY:
				event = "TRIP RATE VERIFIED";
				break;
			case APPROVE:
				event = "TRIP RATE APPROVED";
				break;
			case ACTIVATE:
				event = "TRIP RATE ACTIVATED";
				break;
			case DEACTIVATE:
				event = "TRIP RATE DEACTIVATED";
				break;
			case REJECT:
				event = "TRIP RATE REJECTED";
				break;
			default:
				break;

			}
			audit(tCkCtTripRate.getTrId(), principal, action, event);
			return dto;

		} catch (Exception e) {
			LOG.error("moveState");
			throw e;
		}
	}

}
