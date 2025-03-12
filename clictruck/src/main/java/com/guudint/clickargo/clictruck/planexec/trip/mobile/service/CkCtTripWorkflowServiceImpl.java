package com.guudint.clickargo.clictruck.planexec.trip.mobile.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clicservice.model.TCkSvcWorkflow;
import com.guudint.clickargo.clicservice.service.AbstractWorkflowService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

/**
 * Workflow service implementation for mobile related state changes.
 */
@Service("ckCtTripWorkflowServiceImpl")
public class CkCtTripWorkflowServiceImpl extends AbstractWorkflowService<TCkCtTrip, CkCtTrip> {

	private static final Logger LOG = Logger.getLogger(CkCtTripWorkflowServiceImpl.class);

	public static enum TripStatus {
		M_PICKED_UP('P'), M_DELIVERED('R'), M_ACTIVE('A'), DLV('D');

		private char status;

		TripStatus(char status) {
			this.status = status;
		}

		public char getStatusCode() {
			return this.status;
		}

		public static String getNameByValue(char status) {
			for (TripStatus e : TripStatus.values()) {
				if (e.getStatusCode() == status)
					return e.name();
			}
			return null;
		}

		public static char getCodeByName(String name) {

			for (TripStatus e : TripStatus.values()) {
				if (e.name().equalsIgnoreCase(name))
					return e.getStatusCode();
			}
			return '\0';
		}

	}

	@Autowired
	private CkCtTripDao ckCtTripDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTrip moveState(FormActions action, CkCtTrip dto, Principal principal, ServiceTypes serviceTypes)
			throws ParameterException, ProcessingException, Exception {
		LOG.debug("moveState");
		if (null == action)
			throw new ParameterException("param action null");
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");
		if (null == serviceTypes)
			throw new ParameterException("param serviceTypes null");

		TCkCtTrip tCkCtTrip = ckCtTripDao.find(dto.getTrId());
		if (tCkCtTrip == null)
			throw new EntityNotFoundException("trip " + dto.getTrId() + "not found");

		Hibernate.initialize(tCkCtTrip.getTCkJobTruck());

		Optional<List<String>> opAuthRoles = Optional.ofNullable(principal.getRoleList());
		if (!opAuthRoles.isPresent())
			throw new ProcessingException("principal roles null or empty");

		Character status = tCkCtTrip.getTrStatus();
		if (null == status)
			throw new ProcessingException("param status null");

		Optional<TCkSvcWorkflow> tCkWorkFlowOpt = ckSvcWorkflowDao.findToState(action.name(),
				TripStatus.getNameByValue(status), serviceTypes.getId(), ServiceTypes.CLICTRUCK.getAppsCode(),
				AccountTypes.ACC_TYPE_TO.name(), principal.getRoleList(), "MOBILETRIP");

		if (tCkWorkFlowOpt.isPresent()) {
			tCkCtTrip.setTrStatus(
					TripStatus.getCodeByName(tCkWorkFlowOpt.get().getTCkMstJobStateByWkflToState().getJbstId()));
			tCkCtTrip.setTrDtLupd(new Date());
			tCkCtTrip.setTrUidLupd(principal.getUserId());
			ckCtTripDao.update(tCkCtTrip);

			String event = "";
			switch (action) {
			case MPICKUP:
				event = "SUBMITTED PICK UP CARGO";
				break;
			case MREDO:
				event = "REDO PICK UP CARGO";
				break;
			case MDELIVER:
				event = "DELIVER CARGO";
				break;
			case MDROPOFF:
				event = "CONFIRMED DROP OFF CARGO";
				break;
			default:
				break;
			}
			audit(tCkCtTrip.getTrId(), principal, action, event);
			CkCtTrip tripDto = new CkCtTrip(tCkCtTrip);
			tripDto.setTCkJobTruck(new CkJobTruck(tCkCtTrip.getTCkJobTruck()));
			return tripDto;
		} else {
			throw new ProcessingException(
					String.format("no state machine configured for %s with action [%s] for this principal",
							tCkCtTrip.getTrStatus(), action.name()));
		}

	}

}