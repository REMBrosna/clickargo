package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clicdo.common.IClicTruckConstant;
import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clicservice.service.impl.CkSvcWorkflowService;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractService;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.dto.DriverStates;
import com.guudint.clickargo.clictruck.common.dto.VehStates;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.finacing.constant.FinancingConstants.FinancingTypes;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote.DebitNoteStates;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.finacing.dto.ToInvoiceStates;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.service.IDebitNoteService;
import com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService;
import com.guudint.clickargo.clictruck.finacing.service.ITruckJobCreditService;
import com.guudint.clickargo.clictruck.finacing.service.impl.TruckPaymentService;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.opm.service.IOpmService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobStateChangeEvent;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.job.service.IJobTruckStateService;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripAttachDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripCargoFmDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripCargoMmDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripChargeDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripLocationDao;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripReimbursementDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCharge;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.CkCtTripWorkflowServiceImpl.TripStatus;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService.TripAttachTypeEnum;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCharge;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceEnterExitLocService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkRecordDateDao;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.job.dao.CkJobDao;
import com.guudint.clickargo.job.dao.CkJobRejectDao;
import com.guudint.clickargo.job.dao.CkJobRemarksDao;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.dto.CkJobReject;
import com.guudint.clickargo.job.dto.CkJobRemarks;
import com.guudint.clickargo.job.dto.CkJobRemarks.RemarkType;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.job.model.TCkJobReject;
import com.guudint.clickargo.job.model.TCkJobRemarks;
import com.guudint.clickargo.job.service.IJobEvent.JobEvent;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.guudint.clickargo.master.model.TCkMstJobState;
import com.guudint.clickargo.master.model.TCkMstJobType;
import com.guudint.clickargo.master.model.TCkMstShipmentType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.config.model.TCoreSysparam;

/**
 * Truck job state service implementation.
 */
@Service
public class JobTruckStateServiceImpl implements IJobTruckStateService<CkJobTruck> {

	private static final String KEY_DOCUMENT_VERIFICATION_ENABLE = "CLICTRUCK_DOC_VERIFY_ENABLE";

	@Autowired
	private IEntityService<TCkJob, String, CkJob> ckJobService;

	@Autowired
	private IEntityService<TCkRecordDate, String, CkRecordDate> ckRecordService;

	@Autowired
	private IEntityService<TCkCtToInvoice, String, CkCtToInvoice> ckCtToInvoiceService;

	@Autowired
	private IEntityService<TCkJobTruck, String, CkJobTruck> ckCtJobTruckService;

	@Autowired
	@Qualifier("ckCtDrvService")
	private IEntityService<TCkCtDrv, String, CkCtDrv> ckCtDrvService;

	@Autowired
	@Qualifier("ckCtVehService")
	private IEntityService<TCkCtVeh, String, CkCtVeh> ckCtVehService;

	@Autowired
	@Qualifier("ckJobTruckService")
	private IEntityService<TCkJobTruck, String, CkJobTruck> ckJobTruckService;

	@Autowired
	private CkSvcWorkflowService workflowService;

	@Autowired
	@Qualifier("ckCtContactDetailDao")
	private GenericDao<TCkCtContactDetail, String> ckCtContactDetailDao;

	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;

	@Autowired
	private IDebitNoteService debitNoteService;

	@Autowired
	private IPlatformInvoiceService platformFeeService;

	@Autowired
	private CkCtContractService contractService;

	@Autowired
	private CkCtTripService ckCtTripService;

	@Autowired
	private CkCtTripReimbursementDao reimbursementDao;

	@Autowired
	private CkCtPlatformInvoiceDao platformInvoiceDao;

	@Autowired
	private ITruckJobCreditService truckJobCreditService;

	@Autowired
	private CkJobTruckService ckJobTruckService2;

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	@Autowired
	private CkJobTruckDao ckJobTruckDao;

	@Autowired
	private CkCtTripAttachDao attachDao;

	@Autowired
	private CkJobDao ckJobDao;

	@Autowired
	private CkJobRejectDao ckJobRejectDao;

	@Autowired
	private CkJobRemarksDao ckJobRemarksDao;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	protected TruckPaymentService paymentService;

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	TrackTraceEnterExitLocService trackTraceEnterExitLocService;

	@Autowired
	private IOpmService opmService;

	@Autowired
	private CkRecordDateDao ckRecordDateDao;
	@Autowired
	private CkCtTripLocationDao ckCtTripLocationDao;
	@Autowired
	private CkCtTripChargeDao ckCtTripChargeDao;
	@Autowired
	private CkCtTripDao ckCtTripDao;
	@Autowired
	private CkCtTripCargoFmDao ckCtTripCargoFmDao;
	@Autowired
	private CkCtTripCargoMmDao ckCtTripCargoMmDao;
	@Autowired
	private CkJobTruckExtDao ckJobTruckExtDao;
	
	private ObjectMapper mapper = new ObjectMapper();

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(JobTruckStateServiceImpl.class);

	@Override
	public CkJobTruck cloneJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("cloneJob");

		if (null == dto)
			throw new ParameterException("param dto null;");
		if (null == principal)
			throw new ParameterException("param principal null");

		CkJobTruck ckJobTruck = new CkJobTruck();

		if (dto.getGeneralDetailsClone() != null && dto.getGeneralDetailsClone() == true) {
			ckJobTruck.setShipmentType(dto.getShipmentType());
			ckJobTruck.setJobShipmentRef(dto.getJobShipmentRef());
			ckJobTruck.setJobCustomerRef(dto.getJobCustomerRef());
			ckJobTruck.setTCoreAccnByJobPartyCoFf(dto.getTCoreAccnByJobPartyCoFf());
			ckJobTruck.setJobDtPlan(new Date());
		}

		if (dto.getPartiesDetailsClone() != null && dto.getPartiesDetailsClone() == true) {
			ckJobTruck.setTCoreAccnByJobPartyTo(dto.getTCoreAccnByJobPartyTo());
		}

		try {
			if (dto.getTckCtTripList() != null) {
				List<CkCtTrip> listCkCtTrip = new ArrayList<>();
				CkCtTrip data = null;
				for (CkCtTrip ckCtTrip : dto.getTckCtTripList()) {
					if (dto.getLocationDetailsClone() != null && dto.getLocationDetailsClone() == true) {
						if (ckCtTrip.getTCkCtTripLocationByTrTo() != null){
							ckCtTrip.getTCkCtTripLocationByTrTo().setTlocCargoRec("");
						}
						data = new CkCtTrip();
						data.setTrSeq(ckCtTrip.getTrSeq());
						data.setTCkCtTripLocationByTrFrom(ckCtTrip.getTCkCtTripLocationByTrFrom());
						data.setTCkCtTripLocationByTrTo(ckCtTrip.getTCkCtTripLocationByTrTo());
						data.setTCkCtTripLocationByTrDepot(ckCtTrip.getTCkCtTripLocationByTrDepot());
						data.setTCkCtTripCharge(ckCtTrip.getTCkCtTripCharge());
						data.setTrStatus(RecordStatus.ACTIVE.getCode());
						ckJobTruck.setTCkCtMstVehType(dto.getTCkCtMstVehType());
					}

					if (dto.getCargoDetailsClone() != null && dto.getCargoDetailsClone() == true) {
						data.setTckCtTripCargoFmList(ckCtTrip.getTckCtTripCargoFmList());
						data.setTripCargoMmList(ckCtTrip.getTripCargoMmList().stream().map(val -> {
							val.setCgDropOffStatus('U');
							val.setCgPickupStatus('U');
							return val;
						}).collect(Collectors.toList()));

					}
					listCkCtTrip.add(data);
				}
				ckJobTruck.setTckCtTripList(listCkCtTrip);
			}

		} catch (Exception ex) {
			LOG.error("cloneJob", ex);
		}

		ckJobTruck.setTCkJob(dto.getTCkJob());
		ckJobTruck.setJobDtCreate(new Date());
		ckJobTruck.setJobUidCreate(dto.getJobUidCreate());

		if (dto.getNumberOfCopy() != null && dto.getNumberOfCopy() > 0) {
			for (int i = 0; i < dto.getNumberOfCopy(); i++) {

				// create truck job
				ckJobTruck = createJob(ckJobTruck, principal);

				if (dto.getContactDetailsClone() != null && dto.getContactDetailsClone() == true) {
					// create contact detail
					CkCtContactDetail ckCtContactDetail = createContactDetails(principal);
					ckJobTruck.setTCkCtContactDetailByJobContactCoFf(ckCtContactDetail);
					ckJobTruck.setTCkCtContactDetailByJobContactTo(dto.getTCkCtContactDetailByJobContactTo());
				}

				// create record date
				CkRecordDate crCkRecordDate = createRecordDate(principal);
				ckJobTruck.getTCkJob().setTCkRecordDate(crCkRecordDate);

				// create parent job
				CkJob ckJob = createParentJob(ckJobTruck, principal);
				ckJobTruck.setTCkJob(ckJob);

				ckJobTruckService2.update(ckJobTruck, principal);
			}
		} else {
			throw new ParameterException("Number of copy can not null or 0 (zero)");
		}

		return ckJobTruck;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck withdrawJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("withdrawJob");

		// CT-129 - [CO Operation] [Trucking Jobs] Cannot withdraw job
		checkJobState(dto.getTCkJob().getJobId(), JobStates.NEW);

		// This will throw exception in case of issue during credit reversal, so that
		// it won't proceed to update job truck state.
		// NOTE: reverseJobTruckCredit may need to add JOB_WITHDRAW if required
		if (Arrays.asList(FinancingTypes.OC.name()).contains(dto.getJobFinanceOpt())) {
			// only OC is applicable since OC will reserve during submit, OT will reserve
			// during ACCEPT
			opmService.reverseOpmJobTruckCredit(JournalTxnType.JOB_CANCEL, dto, principal);
		} else if (Objects.nonNull(dto.getJobFinanceOpt())
				&& dto.getJobFinanceOpt().equalsIgnoreCase(FinancingTypes.BC.name())) {
			truckJobCreditService.reverseJobTruckCredit(JournalTxnType.JOB_CANCEL, dto, principal);
		}

		CkJob ckJob = workflowService.moveState(FormActions.WITHDRAW, dto.getTCkJob(), principal,
				ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtCancel(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidCancel(principal.getUserId());

		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.WITHDRAW);
		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CkJobTruck rejectJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("withdrawJob");

		if (null != dto.getTCkJob().getTCkMstJobState()) {
			if (dto.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.NEW.name())) {
				throw new ProcessingException("Already rejected by a different user");
			}
		}

		// This will throw exception in case of issue during credit reversal, so that
		// it won't proceed to update job truck state.
		if (Objects.nonNull(dto.getJobFinanceOpt())
				&& Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name()).contains(dto.getJobFinanceOpt())) {
			// check if the financing option is OT and the state is ACP/ASG before reversing
			// the
			// credit because OT will reserve upon accept only
			if ((dto.getJobFinanceOpt().equalsIgnoreCase(FinancingTypes.OT.name())
					&& Arrays.asList(JobStates.ACP.name(), JobStates.ASG.name())
							.contains(dto.getTCkJob().getTCkMstJobState().getJbstId()))
					|| dto.getJobFinanceOpt().equalsIgnoreCase(FinancingTypes.OC.name())) {

				opmService.reverseOpmJobTruckCredit(JournalTxnType.JOB_REJECT, dto, principal);
			} else {
				opmService.reverseOpmJobTruckCredit(JournalTxnType.JOB_REJECT, dto, principal);
			}

		} else if (Objects.nonNull(dto.getJobFinanceOpt())
				&& dto.getJobFinanceOpt().equalsIgnoreCase(FinancingTypes.BC.name())) {
			truckJobCreditService.reverseJobTruckCredit(JournalTxnType.JOB_REJECT, dto, principal);
		}

		// update the driver and vehicle first
		CkCtDrv asgDriver = dto.getTCkCtDrv();
		if (asgDriver != null) {
			CkCtDrv drvEntity = ckCtDrvService.find(asgDriver);
			if (drvEntity != null) {
				drvEntity.setDrvState(DriverStates.UNASSIGNED.name());
				ckCtDrvService.update(drvEntity, principal);
			}
		}

		CkCtVeh asgVeh = dto.getTCkCtVeh();
		if (asgVeh != null) {
			// find the veh and check if it's already assigned or not
			CkCtVeh vehEntity = ckCtVehService.find(asgVeh);
			if (vehEntity != null) {
				// update the vehicle state
				CkCtMstVehState state = new CkCtMstVehState();
				state.setVhstId(VehStates.UNASSIGNED.name());
				vehEntity.setTCkCtMstVehState(state);
				ckCtVehService.update(vehEntity, principal);
			}
		}
//		}

		CkJob ckJob = workflowService.moveState(FormActions.REJECT, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);

		// if state is Billed or Verified, then this means that the rejection is for the
		// billed job
//		if(!iRejectForBilledJob) {
		dto.getTCkJob().getTCkRecordDate().setRcdDtReject(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidCancel(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
//		}

		workflowService.audit(dto.getJobId(), principal, FormActions.REJECT);
		// create reject record
//		createJobReject(dto, principal);
		createJobRemarks(dto, FormActions.REJECT, principal);

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.REJECT, dto, principal));
		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck acceptJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("acceptJob");

		CkJob jobToAccept = checkJobState(dto.getTCkJob().getJobId(), JobStates.ACP);

		if (null != jobToAccept && jobToAccept.getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.NEW.name()))
			throw new ProcessingException("Already withdrawn by a different user");

		// 20250321 Check if the financing option is OT, then do the reserve. Otherwise,
		// do nothing.
		if (StringUtils.equalsIgnoreCase(FinancingTypes.OT.name(), dto.getJobFinanceOpt())) {
			opmService.reserveOpmJobTruckCredit(JournalTxnType.JOB_OPM_ACCEPT, dto, dto.getJobTotalCharge(), principal);
		}

		CkJob ckJob = workflowService.moveState(FormActions.ACCEPT, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtAccepted(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidAccepted(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.ACCEPT);
		try {
			TCoreUsr coreUsr = coreUserDao.find(principal.getUserId());
			CkJobTruck ckJobTruck = ckJobTruckService.findById(dto.getJobId());

			if (null != coreUsr) {
				TCkCtContactDetail tCkCtContactDetailTo = new TCkCtContactDetail();
				tCkCtContactDetailTo.setCdId(CkUtil.generateIdSynch(CkJobTruckServiceUtil.CD_PREFIX));
				tCkCtContactDetailTo.setCdStatus(RecordStatus.ACTIVE.getCode());
				tCkCtContactDetailTo.setCdName(coreUsr.getUsrName());
				tCkCtContactDetailTo.setCdEmail(coreUsr.getUsrContact().getContactEmail());
				tCkCtContactDetailTo.setCdPhone(coreUsr.getUsrContact().getContactTel());
				tCkCtContactDetailTo.setCdUidCreate(principal.getUserId());
				tCkCtContactDetailTo.setCdDtCreate(Calendar.getInstance().getTime());
				tCkCtContactDetailTo.setRrUidLupd(principal.getUserId());
				tCkCtContactDetailTo.setCdDtLupd(Calendar.getInstance().getTime());
				ckCtContactDetailDao.add(tCkCtContactDetailTo);
				dto.setTCkCtContactDetailByJobContactTo(new CkCtContactDetail(tCkCtContactDetailTo));
				ckJobTruck.setTCkCtContactDetailByJobContactTo(new CkCtContactDetail(tCkCtContactDetailTo));

				ckJobTruckUtilService.setDepartment(ckJobTruck, AccountTypes.ACC_TYPE_TO, principal);

				ckCtJobTruckService.update(ckJobTruck, principal);

				// update trip location GPS;
				ckJobTruckUtilService.updateTripLocGPS(ckJobTruck.getJobId());

				eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.ACCEPT, dto, principal));
			}
		} catch (Exception ex) {
			LOG.error("acceptJob", ex);
		}

		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck assignJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("assignJob");

		checkJobState(dto.getTCkJob().getJobId(), JobStates.ASG);

		// update the driver and vehicle first
		CkCtDrv asgDriver = dto.getTCkCtDrv();
		if (asgDriver != null) {
			// find the driver and check if it's already assigned or not
			CkCtDrv drvEntity = ckCtDrvService.find(asgDriver);
			if (drvEntity != null) {
				if (StringUtils.isNotBlank(drvEntity.getDrvState())
						&& drvEntity.getDrvState().equalsIgnoreCase(DriverStates.ASSIGNED.name())
						// Change in Driver Assignment for mobile
						&& dto.getJobMobileEnabled() == 'N') {
					throw new ProcessingException("Driver already assigned to another job");
				} else {
					// update the state
					drvEntity.setDrvState(DriverStates.ASSIGNED.name());
					ckCtDrvService.update(drvEntity, principal);
				}
			}
		}

		CkCtVeh asgVeh = dto.getTCkCtVeh();
		CkCtVeh vehEntity = null;
		if (asgVeh != null) {
			// find the veh and check if it's already assigned or not
			if (StringUtils.isBlank(asgVeh.getVhId())){
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("vehicle", "Vehicle cannot be empty");
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			}
			vehEntity = ckCtVehService.find(asgVeh);
			if (vehEntity != null) {
				if ((vehEntity.getTCkCtMstVehState().getVhstId().equalsIgnoreCase(VehStates.ASSIGNED.name())
						|| vehEntity.getTCkCtMstVehState().getVhstId().equalsIgnoreCase(VehStates.MAINTENANCE.name()))
						// Change in Vehicle Assignment for mobile
						&& dto.getJobMobileEnabled() == 'N') {
					throw new ProcessingException("Vehicle already assigned to another job or in Maintenance");
				} else {
					// update the vehicle state
					CkCtMstVehState state = new CkCtMstVehState();
					state.setVhstId(VehStates.ASSIGNED.name());
					vehEntity.setTCkCtMstVehState(state);
					ckCtVehService.update(vehEntity, principal);
				}
			}
		}

		// proceed to change

		CkJob ckJob = workflowService.moveState(FormActions.ASSIGN, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtAssigned(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidAssigned(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.ASSIGN);

		//
		if (vehEntity != null && dto.getTCkCtMstVehType() != null
				&& ICkConstant.VEHICLE_TYPE_UNDEFINE.equalsIgnoreCase(dto.getTCkCtMstVehType().getVhtyId())) {

			this.updateVehicleTypeFromVehicle(dto.getJobId(), vehEntity);
		}

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.ASSIGN, dto, principal));
		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck startJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("startJob");

		checkJobState(dto.getTCkJob().getJobId(), JobStates.ONGOING);

		CkJob ckJob = workflowService.moveState(FormActions.START, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtStart(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidStart(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.START);

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.START, dto, principal));
		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck stopJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("stopJob");

		checkJobState(dto.getTCkJob().getJobId(), JobStates.DLV);
		// Moved to validator - validateStop
//		checkAttachJob(dto.getTckCtTripList().get(0).getTrId());

		// update the driver and vehicle first
		CkCtDrv asgDriver = dto.getTCkCtDrv();
		if (asgDriver != null) {
			CkCtDrv drvEntity = ckCtDrvService.find(asgDriver);
			if (drvEntity != null) {
				drvEntity.setDrvState(DriverStates.UNASSIGNED.name());
				ckCtDrvService.update(drvEntity, principal);
			}
		}

		CkCtVeh asgVeh = dto.getTCkCtVeh();
		if (asgVeh != null) {
			// find the veh and check if it's already assigned or not
			CkCtVeh vehEntity = ckCtVehService.find(asgVeh);
			if (vehEntity != null) {
				// update the vehicle state
				CkCtMstVehState state = new CkCtMstVehState();
				state.setVhstId(VehStates.UNASSIGNED.name());
				vehEntity.setTCkCtMstVehState(state);
				ckCtVehService.update(vehEntity, principal);
			}
		}

		CkJob ckJob = workflowService.moveState(FormActions.STOP, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.setJobDtDelivery(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdDtComplete(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidComplete(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.STOP);

		//
		trackTraceEnterExitLocService.getEnterExitTimeOfLocation(dto.getJobId());

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.STOP, dto, principal));
		return dto;
	}

	/** Moved to validator - validateStop method */
	@SuppressWarnings("unused")
	private void checkAttachJob(String trId) throws Exception {
		LOG.debug("checkAttachJob");
		List<TCkCtTripAttach> dropOff = attachDao.findByTrIdAndAtyId(trId, TripAttachTypeEnum.PHOTO_DROPOFF.name());
		List<TCkCtTripAttach> pickUp = attachDao.findByTrIdAndAtyId(trId, TripAttachTypeEnum.PHOTO_PICKUP.name());
		if (dropOff == null || dropOff.size() == 0) {
			throw new ProcessingException("drop off images does not exist");
		} else if (pickUp == null || pickUp.size() == 0) {
			throw new ProcessingException("pick up images does not exist");
		}
	}

	@Override
	public CkJobTruck rejectJobPayment(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("withdrawJob");

		if (null != dto.getTCkJob().getTCkMstJobState()) {
			if (dto.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.DLV.name())) {
				throw new ProcessingException("Already rejected by a different user");
			}
		}

		// Call the reverseCredit for reimbursement only since bill job will submit it
		// for reserve. Do not touch the trip charge
		// reserved during job submit. Only call reverseJobTruckCredit for
		// job_submit_reimbursement if there is reimbursement

		if (dto.getJobTotalReimbursements() == null || dto.getJobTotalReimbursements() != BigDecimal.ZERO) {
			if (Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name()).contains(dto.getJobFinanceOpt())) {
				opmService.reverseOpmJobTruckCredit(JournalTxnType.JOB_SUBMIT_REIMBURSEMENT, dto, principal);
			} else if (dto.getJobFinanceOpt().equalsIgnoreCase(FinancingTypes.BC.name())) {
				truckJobCreditService.reverseJobTruckCredit(JournalTxnType.JOB_SUBMIT_REIMBURSEMENT, dto, principal);
			}

		}

		CkJob ckJob = workflowService.moveState(FormActions.REJECT_BILL, dto.getTCkJob(), principal,
				ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);

		dto.getTCkJob().getTCkRecordDate().setRcdDtBillRejected(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidBillRejected(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);

		workflowService.audit(dto.getJobId(), principal, FormActions.REJECT_BILL);
		// create reject record
//		createJobReject(dto, principal);
		createJobRemarks(dto, FormActions.REJECT_BILL, principal);

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.REJECT_BILL, dto, principal));
		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck verifyJobPayment(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.info("verifyJobPayment entered state:" + dto.getJobId() + " - "
				+ dto.getTCkJob().getTCkMstJobState().getJbstId());

		checkSuspendedAccount(principal);

		checkJobState(dto.getTCkJob().getJobId(), JobStates.VER_BILL);
		// just check in case the finance officer already verified and approved
		checkJobState(dto.getTCkJob().getJobId(), JobStates.ACK_BILL);

		CkJob ckJob = workflowService.moveState(FormActions.VERIFY_BILL, dto.getTCkJob(), principal,
				ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtBillVerified(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidBillVerified(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.VERIFY_BILL);

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.VERIFY_BILL, dto, principal));

		createJobRemarks(dto, FormActions.VERIFY_BILL, principal);

		// call approveJobPayment is principal is also a FF_FINANCE. Call is made here
		// to not add workflow for BILLED -> APPROVED
		if (principal.getRoleList().contains(Roles.FF_FINANCE.name())) {
			Thread.sleep(1000);
			this.acknowledgeJobPayment(dto, principal);
		}

		return dto;
	}

	/**
	 * This method is called for approve bill action. This will move from ver_bill
	 * to ack_bill.
	 */
	@Override
	public CkJobTruck acknowledgeJobPayment(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.info("acknowledgeJobPayment entered state: " + dto.getJobId() + " - "
				+ dto.getTCkJob().getTCkMstJobState().getJbstId());

		checkSuspendedAccount(principal);

		// just check in case the finance officer already verified and approved
		checkJobState(dto.getTCkJob().getJobId(), JobStates.ACK_BILL);

		CkJob ckJob = workflowService.moveState(FormActions.ACKNOWLEDGE_BILL, dto.getTCkJob(), principal,
				ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtBillAcknowledged(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidBillAcknowledged(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.ACKNOWLEDGE_BILL);

		createJobRemarks(dto, FormActions.APPROVE_BILL, principal);

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.ACKNOWLEDGE_BILL, dto, principal));

		// If document verification is disabled, call the approveJobPaument here.
		String gliDocVerEnable = getSysParam(KEY_DOCUMENT_VERIFICATION_ENABLE);
		LOG.info("CLICTRUCK_DOC_VERIFY_ENABLE = " + gliDocVerEnable);
		if (gliDocVerEnable != null && gliDocVerEnable.equalsIgnoreCase("N")) {
			approveJobPayment(dto, principal);
		}

		return dto;
	}

	/**
	 * This is called when acknowledge_bill action is clicked.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck approveJobPayment(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.info("------------- START TIME ------ " + System.currentTimeMillis());
		LOG.info("approveJobPayment entered state: " + dto.getJobId() + " - "
				+ dto.getTCkJob().getTCkMstJobState().getJbstId());

		checkSuspendedAccount(principal);

		checkJobState(dto.getTCkJob().getJobId(), JobStates.APP_BILL);

		// for OPM no credit service will be called
		if (!Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name(), FinancingTypes.NF.name())
				.contains(dto.getJobFinanceOpt())) {
			// call Reverse and utilize first before proceed to update the job truck state
			// if any issues encountered, this will throw exception and job state will not
			// be moved.
			truckJobCreditService.reverseJobTruckCredit(JournalTxnType.JOB_PAYMENT_APPROVE, dto, principal);
			truckJobCreditService.utilizeJobTruckCredit(JournalTxnType.JOB_PAYMENT_APPROVE, dto, principal);

		}

		// reset action so it won't end up infinite
		dto.setAction(null);
		dto.setJobInPaymentState(JobPaymentStates.NEW.name());
		dto.setJobOutPaymentState(JobPaymentStates.NEW.name());
		// force to set the dt opm utilize
		dto.setJobDtOpmUtilize(null);
		ckCtJobTruckService.update(dto, principal);

		CkJob ckJob = workflowService.moveState(FormActions.APPROVE_BILL, dto.getTCkJob(), principal,
				ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtBillApproved(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidBillApproved(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);

		createJobRemarks(dto, FormActions.APPROVE_BILL, principal);

		CoreAccn accnGli = clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP);
		if (accnGli == null)
			throw new EntityNotFoundException("SP account not found!");

		// Only create debit note for non-opm
		if (!Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name()).contains(dto.getJobFinanceOpt())) {
			createDebitNotes(dto, accnGli, principal);
		}

		createPlatformFees(dto, accnGli, principal);

		workflowService.audit(dto.getJobId(), principal, FormActions.APPROVE_BILL);

		LOG.info("------------- END TIME ------ " + System.currentTimeMillis());
		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.APPROVE_BILL, dto, principal));
		return dto;
	}

	@Override
	public CkJobTruck billJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		checkJobState(dto.getTCkJob().getJobId(), JobStates.BILLED);

		// Check if the shipment type is import/export then it is first mile and do
		// logic below:
		Optional<CkMstShipmentType> opShipmentType = Optional.ofNullable(dto.getTCkJob().getTCkMstShipmentType());
		if (opShipmentType.isPresent()) {
			if (opShipmentType.get().getShtId().equalsIgnoreCase(ShipmentTypes.IMPORT.getId())
					|| opShipmentType.get().getShtId().equalsIgnoreCase(ShipmentTypes.EXPORT.getId())) {
				if (dto.getToInvoiceList() != null) {
					for (CkCtToInvoice toInv : dto.getToInvoiceList()) {
						if (StringUtils.isBlank(toInv.getInvId())) {
							toInv.setInvJobId(dto.getJobId());
							ckCtToInvoiceService.addObj(toInv, principal);
						} else {
							// else update the existing based from the dto.getToInvoiceList
							toInv = ckCtToInvoiceService.update(toInv, principal);
						}
					}
				}

			}
		}

		// recalculate total trip charge, reimbursements
		List<CkCtTrip> listTrips = ckCtTripService.findTripsByTruckJobAndStatus(dto.getJobId(),
				Arrays.asList(TripStatus.M_ACTIVE.getStatusCode(), TripStatus.M_DELIVERED.getStatusCode(),
						TripStatus.M_PICKED_UP.getStatusCode(), TripStatus.DLV.getStatusCode()));
		BigDecimal totalTripCharge = BigDecimal.ZERO;
		BigDecimal totalReimbursements = BigDecimal.ZERO;
		if (listTrips != null && listTrips.size() > 0) {
			for (CkCtTrip trip : listTrips) {
				CkCtTripCharge tripCharge = trip.getTCkCtTripCharge();
				totalTripCharge = totalTripCharge
						.add(tripCharge.getTcPrice() == null ? BigDecimal.ZERO : tripCharge.getTcPrice());

				totalReimbursements = totalReimbursements
						.add(reimbursementDao.sumTotalByTripIdAndStatus(trip.getTrId(), RecordStatus.ACTIVE.getCode()));
			}
		}

		// do not call reserve credit if total imbursement is zero
		if (totalReimbursements != BigDecimal.ZERO) {
			if (Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name()).contains(dto.getJobFinanceOpt())) {
				opmService.reserveOpmJobTruckCredit(JournalTxnType.JOB_SUBMIT_REIMBURSEMENT, dto,
						totalReimbursements.setScale(0, RoundingMode.HALF_UP), principal);
			} else if (dto.getJobFinanceOpt().equalsIgnoreCase(FinancingTypes.BC.name())) {
				// Call the reserveCredit
				truckJobCreditService.reserveJobTruckCredit(JournalTxnType.JOB_SUBMIT_REIMBURSEMENT, dto,
						totalReimbursements.setScale(0, RoundingMode.HALF_UP), principal);
			}

		}

		// reset the action so that it won't call infinitely
		dto.setAction(null);
		dto.setJobTotalReimbursements(totalReimbursements.setScale(0, RoundingMode.HALF_UP));
		dto.setJobTotalCharge(totalTripCharge.setScale(0, RoundingMode.HALF_UP));
		dto.setJobInPaymentState(JobPaymentStates.NEW.name());
		ckCtJobTruckService.update(dto, principal);

		CkJob ckJob = workflowService.moveState(FormActions.BILLJOB, dto.getTCkJob(), principal,
				ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtBilled(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidBilled(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.BILLJOB);

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.BILLJOB, dto, principal));
		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck terminateJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {

		LOG.debug("terminateJob");

		// update Job, change to a private function?
		String parentId = null;
		if (dto.getTCkJob() == null) {
			TCkJobTruck jobTruck = ckJobTruckDao.find(dto.getJobId());
			parentId = jobTruck.getTCkJob().getJobId();
		} else {
			parentId = dto.getTCkJob().getJobId();
		}
		TCkJob ckJobEntity = ckJobDao.find(parentId);
		String oldJobState = ckJobEntity.getTCkMstJobState().getJbstId();

		ckJobEntity.setTCkMstJobState(new TCkMstJobState(JobStates.TERMINATED.name(), ""));
		ckJobEntity.setJobDtLupd(new Date());
		ckJobEntity.setJobUidLupd(principal.getUserId());
		ckJobDao.update(ckJobEntity);

		// update invoice
		ckJobTruckUtilService.updateInv2Status(dto.getJobId(), ToInvoiceStates.TERMINATED, principal.getUserId(),
				new Date());

		// update debit note
		ckJobTruckUtilService.updateDnInv2Status(dto.getJobId(), DebitNoteStates.TERMINATED, principal.getUserId(),
				new Date());

		// update Credit limit
		if (JobStates.APP_BILL.name().equalsIgnoreCase(oldJobState)) {
			truckJobCreditService.reverseUtilized(JournalTxnType.JOB_TERMINATION, dto, principal);
		} else {
			truckJobCreditService.reverseJobTruckCredit(JournalTxnType.JOB_TERMINATION, dto, principal);
		}

		workflowService.audit(dto.getJobId(), principal, FormActions.TERMINATE);

		// eventPublisher.publishEvent(new TruckJobStateChangeEvent(this,
		// JobActions.TERMINATE, dto, principal));

		return dto;
	}

	/*
	 * This is Auxiliary function
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createPlatformFees(String jobTruckId, CoreAccn gliAccn, Principal principal) throws Exception {

		CkJobTruck jobTruck = ckJobTruckService.findById(jobTruckId);
		List<TCkCtPlatformInvoice> invoices = platformInvoiceDao.findByJobId(jobTruckId);

		if (null != invoices && invoices.size() > 0) {
			throw new ProcessingException("Invoice is existing for: " + jobTruckId);
		}

		if (!JobStates.APP_BILL.name().equals(jobTruck.getTCkJob().getTCkMstJobState().getJbstId())) {

			throw new ProcessingException("Job is not approved for: " + jobTruckId);
		}

		this.createPlatformFees(jobTruck, gliAccn, principal);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void splitJob(String jobTruckId, int numCopies, Principal principal) throws Exception {
		
		LOG.info("splitJob: " + jobTruckId + " - " + numCopies);

		if (StringUtils.isBlank(jobTruckId))
			throw new ParameterException("param jobTruckId null;");
		if (null == principal)
			throw new ParameterException("param principal null");
		if (numCopies <= 0)
			throw new ParameterException("param numCopies < 0");

		try {

			TCkJobTruck jobTruck = ckJobTruckDao.find(jobTruckId);
			TCkJob job = jobTruck.getTCkJob();
			TCkRecordDate recordDate = job.getTCkRecordDate();

			TCkCtContactDetail contactDtlFf = jobTruck.getTCkCtContactDetailByJobContactCoFf();
			TCkCtContactDetail contactDtlTo = jobTruck.getTCkCtContactDetailByJobContactTo();
			
			// Trip
			List<TCkCtTrip> tripList = ckCtTripDao.findByJobId(jobTruckId);

			for (TCkCtTrip trip : tripList) {

				List<TCkCtTripCargoFm> fmList = ckCtTripCargoFmDao.findTripCargoFmsByTripId(trip.getTrId());
				List<TCkCtTripCargoMm> mmmList = ckCtTripCargoMmDao.findTripCargoFmmsByTripId(trip.getTrId());

				trip.setFmCargoList(fmList);
				trip.setMmCargoList(mmmList);
			}

			String[] jobIdPrefixSeq = getIdPrefixSeq(jobTruckId);
			String jobPrefix = jobIdPrefixSeq[0];
			String jobIdSeq = jobIdPrefixSeq[1];
			
			for (int i = 1; i < numCopies +1 ; i++) {
				
				int seq = new Integer(jobIdSeq) + i;
				// CkJobTruck
				// TCkRecordDate
				// TCkJob
				// ckCtContactDetail FF
				// ckCtContactDetail TO
				// T_CK_JOB_TRUCK_EXT
				TCkJobTruck jobTruckClone = (TCkJobTruck) jobTruck.clone();
				TCkJob jobClone = (TCkJob) job.clone();
				TCkRecordDate recordDateClone = (TCkRecordDate) recordDate.clone();
				TCkCtContactDetail contactDtlFfClone = (TCkCtContactDetail) contactDtlFf.clone();
				TCkCtContactDetail contactDtlToClone = (TCkCtContactDetail) contactDtlTo.clone();
				List<TCkJobTruckExt> jobTruckExtList = ckJobTruckExtDao.findAllByJobTruckId(jobTruckId);
				List<TCkJobTruckExt> jobTruckExtListClone = new ArrayList<>();
				for(TCkJobTruckExt je: jobTruckExtList) {
					jobTruckExtListClone.add((TCkJobTruckExt)je.clone());
				}
				// update Id;
				jobClone.setJobId(this.getNewCloneId(getIdPrefix(job.getJobId()), seq));
				recordDateClone.setRcdId(this.getNewCloneId(getIdPrefix(recordDate.getRcdId()), seq));
				contactDtlFfClone.setCdId(this.getNewCloneId(getIdPrefix(contactDtlFf.getCdId()), seq));
				contactDtlToClone.setCdId(this.getNewCloneId(getIdPrefix(contactDtlTo.getCdId()), seq));
				
				jobTruckClone.setJobId(this.getNewCloneId(jobPrefix, seq));
				jobTruckClone.setTCkJob(jobClone);
				jobTruckClone.setTCkCtContactDetailByJobContactCoFf(contactDtlFfClone);
				jobTruckClone.setTCkCtContactDetailByJobContactTo(contactDtlToClone);
				jobClone.setTCkRecordDate(recordDateClone);

				jobTruckExtListClone.forEach( je -> {
					je.setJextId(this.getNewCloneId(je.getJextId(), seq));
					je.setTCkJobTruck(jobTruckClone);
				});
				
				ckRecordDateDao.add(recordDateClone);
				ckJobDao.add(jobClone);
				ckJobTruckDao.add(jobTruckClone);
				ckCtContactDetailDao.add(contactDtlFfClone);
				ckCtContactDetailDao.add(contactDtlToClone);
				
				for(TCkJobTruckExt je: jobTruckExtListClone) {
					ckJobTruckExtDao.add(je);
				}
				
				// TCkCtTrip
				// TCkCtTripLocation
				// TCkCtTripCharge
				// TCkCtTripDo // no need
				for (TCkCtTrip trip : tripList) {
					
					TCkCtTripCharge tripCharge = trip.getTCkCtTripCharge();
					TCkCtTripLocation locTo = trip.getTCkCtTripLocationByTrTo();
					TCkCtTripLocation locFrom = trip.getTCkCtTripLocationByTrFrom();
					TCkCtTripLocation locDepot = trip.getTCkCtTripLocationByTrDepot();

					TCkCtTrip tripClone = (TCkCtTrip)trip.clone();
					TCkCtTripCharge tripChargeClone = null;
					if( tripCharge != null ) {
						tripChargeClone = (TCkCtTripCharge) tripCharge.clone();
					}
					TCkCtTripLocation locToClone = (TCkCtTripLocation) locTo.clone();
					TCkCtTripLocation locFromClone = (TCkCtTripLocation) locFrom.clone();
					TCkCtTripLocation locDepotClone = null;
					if (locDepot != null) {
						locDepotClone = (TCkCtTripLocation) locDepot.clone();
					}
					
					// update Id;
					if( tripChargeClone != null ) {
						tripChargeClone.setTcId(this.getNewCloneId(getIdPrefix(tripCharge.getTcId()), seq));
					}
					locToClone.setTlocId(this.getNewCloneId(getIdPrefix(locTo.getTlocId()), seq));
					locFromClone.setTlocId(this.getNewCloneId(getIdPrefix(locFrom.getTlocId()), seq));
					if (locDepotClone != null) {
						locDepotClone.setTlocId(this.getNewCloneId(getIdPrefix(locDepot.getTlocId()), seq));
					}

					tripClone.setTrId(this.getNewCloneId(getIdPrefix(trip.getTrId()), seq));
					tripClone.setTCkCtTripCharge(tripChargeClone);
					tripClone.setTCkCtTripLocationByTrTo(locToClone);
					tripClone.setTCkCtTripLocationByTrFrom(locFromClone);
					tripClone.setTCkCtTripLocationByTrDepot(locDepotClone);
					tripClone.setTCkJobTruck(jobTruckClone);
					
					ckCtTripDao.add(tripClone);
					if( tripChargeClone != null ) {
						ckCtTripChargeDao.add(tripChargeClone);
					}
					ckCtTripLocationDao.add(locToClone);
					ckCtTripLocationDao.add(locFromClone);
					if (locDepotClone != null) {
						ckCtTripLocationDao.add(locDepotClone);
					}
					
					// TCkCtTripCargoMm
					// TCkCtTripCargoFm
					List<TCkCtTripCargoFm> fmList = trip.getFmCargoList();
					List<TCkCtTripCargoMm> mmList = trip.getMmCargoList();

					if (fmList != null) {
						for (TCkCtTripCargoFm fm : fmList) {
							TCkCtTripCargoFm fmClone = (TCkCtTripCargoFm)fm.clone();
							fmClone.setCgId(this.getNewCloneId(getIdPrefix(fm.getCgId()), seq));
							fmClone.setTCkCtTrip(tripClone);
							ckCtTripCargoFmDao.add(fmClone);
						}
					}
					if (mmList != null) {
						for (TCkCtTripCargoMm mm : mmList) {
							TCkCtTripCargoMm mmClone = (TCkCtTripCargoMm)mm.clone();
							mmClone.setCgId(this.getNewCloneId(getIdPrefix(mm.getCgId()), seq));
							mmClone.setTCkCtTrip(tripClone);
							ckCtTripCargoMmDao.add(mmClone);
						}
					}
				}
				// add audit log
				workflowService.audit(jobTruckClone.getJobId(), principal, FormActions.SPLIT);
			}

		} catch (Exception e) {
			LOG.error("", e);
			throw e;
		}
	}

	// Helper Method
	////////////////////
	private CkJob checkJobState(String jobId, JobStates jobState) throws Exception {
		CkJob job = ckJobService.findById(jobId);
		if (null != job) {
			LOG.info("--------- checkjobstate ------" + job.getTCkMstJobState().getJbstId());
			if (job.getTCkMstJobState().getJbstId().equalsIgnoreCase(jobState.name()))
				throw new ProcessingException("Truck Job is already  " + jobState.getDesc());
		}

		return job;
	}

	private void checkSuspendedAccount(Principal principal) throws Exception {
		if (principal != null) {
			CoreAccn coFfAccn = principal.getCoreAccn();
			if (coFfAccn != null && clickargoAccnService.isAccountSuspended(coFfAccn)) {
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("suspended-accn", "Your account is suspended.");
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			}

		}
	}

	private void createDebitNotes(CkJobTruck jobTruck, CoreAccn gliAccn, Principal principal) throws Exception {
		if (jobTruck == null)
			throw new ParameterException("param jobTruck null");

		if (gliAccn == null)
			throw new ParameterException("param gliAccn null");

		CoreAccn toAccn = jobTruck.getTCoreAccnByJobPartyTo();
		CoreAccn coFfAccn = jobTruck.getTCoreAccnByJobPartyCoFf();

		// Retrieve the contract for platform fee charge of the corresponding accounts
		CkCtContract coffToContract = contractService.getContractByAccounts(toAccn.getAccnId(), coFfAccn.getAccnId());
		if (coffToContract == null)
			throw new ProcessingException(
					"no contract found for " + toAccn.getAccnId() + " and " + coFfAccn.getAccnId());

		CkCtDebitNote dnNoteTo = debitNoteService.createDebitNote(jobTruck, null, toAccn, gliAccn, false);
		CkCtDebitNote dnNoteCoFf = debitNoteService.createDebitNote(jobTruck, coffToContract, gliAccn, coFfAccn, true);

		// update these dates based on the DN, this is applicable for Platform Fee as
		// well
		jobTruck.setJobInPaymentDtDue(dnNoteCoFf.getDnDtDue());
		jobTruck.setJobOutPaymentDtDue(dnNoteTo.getDnDtDue());
		ckJobTruckService.update(jobTruck, principal);

	}

	private void createPlatformFees(CkJobTruck jobTruck, CoreAccn gliAccn, Principal principal) throws Exception {
		// Find the trips by jobId
		if (jobTruck == null)
			throw new ParameterException("param jobTruck null");

		CoreAccn toAccn = jobTruck.getTCoreAccnByJobPartyTo();
		CoreAccn coFfAccn = jobTruck.getTCoreAccnByJobPartyCoFf();

		// Retrieve the contract for platform fee charge of the corresponding accounts
		CkCtContract coffToContract = contractService.getContractByAccounts(toAccn.getAccnId(), coFfAccn.getAccnId());
		if (coffToContract == null)
			throw new ProcessingException(
					"no contract found for " + toAccn.getAccnId() + " and " + coFfAccn.getAccnId());

		// create the platform invoice per trip, create for TO and CO/FF
		platformFeeService.createPlatFormInvoice(jobTruck, null, coffToContract.getTCkCtContractChargeByConChargeTo(),
				gliAccn, toAccn);
		CkCtPlatformInvoice pfCoFf = platformFeeService.createPlatFormInvoice(jobTruck,
				coffToContract.getConPaytermCoFf(), coffToContract.getTCkCtContractChargeByConChargeCoFf(), gliAccn,
				coFfAccn);

		// if it's opm get the payment in due date from platform fee since there will be
		// no debit note
		boolean isOpm = Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name())
				.contains(jobTruck.getJobFinanceOpt());
		if (isOpm) {
			jobTruck.setJobInPaymentDtDue(pfCoFf.getInvDtDue());
			ckJobTruckService.update(jobTruck, principal);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CkJobReject createJobReject(CkJobTruck jobTruck, Principal principal) throws Exception {
		if (jobTruck == null)
			throw new ParameterException("param jobTruck null");

		// Query first if there is an existing remark and get the max seq
		int seqNo = ckJobRejectDao.getMaxSeq(jobTruck.getTCkJob().getJobId());
		TCkJobReject reject = new TCkJobReject();
		reject.setJobrSeq(seqNo);
		reject.setJobrId(CkUtil.generateId(CkJobReject.PREFIX_ID));
		reject.setJobrDtReject(new Date());
		reject.setJobrUidReject(principal.getUserId());
		reject.setJobrReason(jobTruck.getRejectRemarks());
		reject.setTCkJob(jobTruck.getTCkJob().toEntity(new TCkJob()));
		ckJobRejectDao.add(reject);

		CkJobReject dto = new CkJobReject(reject);
		dto.setTCkJob(new CkJob(reject.getTCkJob()));
		return dto;

	}

	private CkJobRemarks createJobRemarks(CkJobTruck jobTruck, FormActions action, Principal principal)
			throws Exception {
		if (jobTruck == null)
			throw new ParameterException("param jobTruck null");

		// Check if the remarks is empty, no need to proceed
		if (StringUtils.isNotBlank(jobTruck.getJobRemarks())) {
			// Query first if there is an existing remark and get the max seq
			int seqNo = ckJobRemarksDao.getMaxSeq(jobTruck.getTCkJob().getJobId());
			TCkJobRemarks remarks = new TCkJobRemarks();
			remarks.setJobrSeq(seqNo);
			if (action == FormActions.REJECT_BILL) {
				remarks.setJobrRemarkType(RemarkType.REJECTED.getCode());
			} else if (action == FormActions.VERIFY_BILL) {
				remarks.setJobrRemarkType(RemarkType.VERIFIED.getCode());
			} else if (action == FormActions.APPROVE_BILL) {
				remarks.setJobrRemarkType(RemarkType.APPROVED.getCode());
			} else if (action == FormActions.REJECT) {
				remarks.setJobrRemarkType(RemarkType.JOB_REJECTED.getCode());
			}

			remarks.setJobrId(CkUtil.generateId(CkJobReject.PREFIX_ID));
			remarks.setJobrDtRemarks(new Date());
			remarks.setJobrUidCreated(principal.getUserId());
			remarks.setJobrReason(jobTruck.getJobRemarks());
			remarks.setTCkJob(jobTruck.getTCkJob().toEntity(new TCkJob()));
			ckJobRemarksDao.add(remarks);

			CkJobRemarks dto = new CkJobRemarks(remarks);
			dto.setTCkJob(new CkJob(remarks.getTCkJob()));
			return dto;
		}

		return null;
	}

	private CkCtContactDetail createContactDetails(Principal principal) throws Exception {
		return ckJobTruckUtilService.createContactDetails(principal);
	}

	private CkJobTruck createJob(CkJobTruck ckJobTruck, Principal principal) throws Exception {

		ckJobTruck.setJobId(CkUtil.generateId(IClicTruckConstant.PREFIX_CK_TRUCK_JOB));
		ckJobTruck.setJobStatus(RecordStatus.ACTIVE.getCode());
		ckJobTruck.setJobDtBooking(null);
		ckJobTruck.setJobDtDelivery(null);

		ckJobTruckUtilService.addOrUpdateTrips(ckJobTruck, principal);

		TCkJobTruck tckJobTruck = new TCkJobTruck();
		tckJobTruck = ckJobTruck.toEntity(tckJobTruck);
		// no deep copy from BeanUtils
		TCkJob tCkJob = new TCkJob();
		tckJobTruck.setTCkJob(null == ckJobTruck.getTCkJob() ? null : ckJobTruck.getTCkJob().toEntity(tCkJob));

		TCkCtContactDetail tCkCtContactDetailCoFF = new TCkCtContactDetail();
		tckJobTruck
				.setTCkCtContactDetailByJobContactCoFf(null == ckJobTruck.getTCkCtContactDetailByJobContactCoFf() ? null
						: ckJobTruck.getTCkCtContactDetailByJobContactCoFf().toEntity(tCkCtContactDetailCoFF));

		TCkCtContactDetail tCkCtContactDetailTo = new TCkCtContactDetail();
		tckJobTruck.setTCkCtContactDetailByJobContactTo(null == ckJobTruck.getTCkCtContactDetailByJobContactTo() ? null
				: ckJobTruck.getTCkCtContactDetailByJobContactTo().toEntity(tCkCtContactDetailTo));

		tckJobTruck.setTCkCtDebitNoteByJobInvoiceeDebitNote(
				null == ckJobTruck.getTCkCtDebitNoteByJobInvoiceeDebitNote() ? null
						: ckJobTruck.getTCkCtDebitNoteByJobInvoiceeDebitNote().toEntity(new TCkCtDebitNote()));
		tckJobTruck.setTCkCtDebitNoteByJobInvoicerDebitNote(
				null == ckJobTruck.getTCkCtDebitNoteByJobInvoicerDebitNote() ? null
						: ckJobTruck.getTCkCtDebitNoteByJobInvoicerDebitNote().toEntity(new TCkCtDebitNote()));

		tckJobTruck.setTCkCtDrv(
				null == ckJobTruck.getTCkCtDrv() ? null : ckJobTruck.getTCkCtDrv().toEntity(new TCkCtDrv()));

		tckJobTruck.setTCkCtVeh(
				null == ckJobTruck.getTCkCtVeh() ? null : ckJobTruck.getTCkCtVeh().toEntity(new TCkCtVeh()));

		tckJobTruck.setJobDrvOth(null == ckJobTruck.getJobDrvOth() ? null : ckJobTruck.getJobDrvOth().toString());
		tckJobTruck.setJobVehOth(null == ckJobTruck.getJobVehOth() ? null : ckJobTruck.getJobVehOth().toString());

		tckJobTruck.setTCkCtMstVehType(null == ckJobTruck.getTCkCtMstVehType() ? null
				: ckJobTruck.getTCkCtMstVehType().toEntity(new TCkCtMstVehType()));

		tckJobTruck.setTCkCtToInvoice(null == ckJobTruck.getTCkCtToInvoice() ? null
				: ckJobTruck.getTCkCtToInvoice().toEntity(new TCkCtToInvoice()));

		tckJobTruck.setTCkCtVeh(
				null == ckJobTruck.getTCkCtVeh() ? null : ckJobTruck.getTCkCtVeh().toEntity(new TCkCtVeh()));

		tckJobTruck.setTCoreAccnByJobPartyCoFf(null == ckJobTruck.getTCoreAccnByJobPartyCoFf() ? null
				: ckJobTruck.getTCoreAccnByJobPartyCoFf().toEntity(new TCoreAccn()));
		tckJobTruck.setTCoreAccnByJobPartyTo(null == ckJobTruck.getTCoreAccnByJobPartyTo() ? null
				: ckJobTruck.getTCoreAccnByJobPartyTo().toEntity(new TCoreAccn()));

		tckJobTruck.setJobDtBooking(null == ckJobTruck.getJobDtBooking() ? null : ckJobTruck.getJobDtBooking());
		tckJobTruck.setJobDtPlan(null == ckJobTruck.getJobDtPlan() ? null : ckJobTruck.getJobDtPlan());
		tckJobTruck.setJobDtDelivery(null == ckJobTruck.getJobDtDelivery() ? null : ckJobTruck.getJobDtDelivery());
		tckJobTruck.setJobDtStart(null == ckJobTruck.getJobDtStart() ? null : ckJobTruck.getJobDtStart());
		tckJobTruck.setJobDtEnd(null == ckJobTruck.getJobDtEnd() ? null : ckJobTruck.getJobDtEnd());
		tckJobTruck.setJobDtCreate(null == ckJobTruck.getJobDtCreate() ? null : ckJobTruck.getJobDtCreate());
		tckJobTruck.setJobUidCreate(null == ckJobTruck.getJobUidCreate() ? null : ckJobTruck.getJobUidCreate());
		tckJobTruck.setJobDtLupd(null == ckJobTruck.getJobDtLupd() ? null : ckJobTruck.getJobDtLupd());
		tckJobTruck.setJobUidLupd(null == ckJobTruck.getJobUidLupd() ? null : ckJobTruck.getJobUidLupd());

		ckJobTruckDao.add(tckJobTruck);
		ckJobTruckService2._auditEvent(JobEvent.CREATE, ckJobTruck, principal);

		return ckJobTruck;
	}

	private CkRecordDate createRecordDate(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkRecordDate jobRecordDate = new CkRecordDate();
		jobRecordDate.setRcdId(CkUtil.generateId());
		jobRecordDate.setRcdDtDrft(new Date());

		jobRecordDate.setRcdDtSubmit(new Date());
		jobRecordDate.setRcdUidSubmit(CkUtil.generateId());

		jobRecordDate.setRcdDtStart(null);
		jobRecordDate.setRcdUidStart(CkUtil.generateId());

		jobRecordDate.setRcdDtExpiry(null);
		jobRecordDate.setRcdUidExpiry(CkUtil.generateId());

		return ckRecordService.add(jobRecordDate, principal);
	}

	private CkJob createParentJob(CkJobTruck ckJobTruck, Principal principal) throws Exception {

		CkJob ckJob = ckJobTruck.getTCkJob();

		ckJob.setJobId(CkUtil.generateId(ICkConstant.PREFIX_PARENT_JOB));
		ckJob.setJobStatus(RecordStatus.ACTIVE.getCode());
		ckJob.setJobDtCreate(new Date());

		CkMstJobState jobState = new CkMstJobState();
		jobState.setJbstId(JobStates.NEW.name());
		ckJob.setTCkMstJobState(jobState);

		TCkJob tckJob = new TCkJob();
		tckJob = ckJob.toEntity(tckJob);

		tckJob.setJobId(ckJob.getJobId());
		tckJob.setJobReference(ckJob.getJobReference());
		tckJob.setJobStatus(ckJob.getJobStatus());
		tckJob.setJobDtCreate(new Date());
		tckJob.setTCkMstJobState(
				null == ckJob.getTCkMstJobState() ? null : ckJob.getTCkMstJobState().toEntity(new TCkMstJobState()));
		tckJob.setTCkMstJobType(
				null == ckJob.getTCkMstJobType() ? null : ckJob.getTCkMstJobType().toEntity(new TCkMstJobType()));
		tckJob.setTCkMstShipmentType(null == ckJob.getTCkMstShipmentType() ? null
				: ckJob.getTCkMstShipmentType().toEntity(new TCkMstShipmentType()));

		TCkRecordDate tCkRecordDate = new TCkRecordDate();
		tckJob.setTCkRecordDate(
				null == ckJob.getTCkRecordDate() ? null : ckJob.getTCkRecordDate().toEntity(tCkRecordDate));

		tckJob.setTCoreAccnByJobCoAccn(null == ckJob.getTCoreAccnByJobCoAccn() ? null
				: ckJob.getTCoreAccnByJobCoAccn().toEntity(new TCoreAccn()));
		tckJob.setTCoreAccnByJobFfAccn(null == ckJob.getTCoreAccnByJobFfAccn() ? null
				: ckJob.getTCoreAccnByJobFfAccn().toEntity(new TCoreAccn()));
		tckJob.setTCoreAccnByJobOwnerAccn(null == ckJob.getTCoreAccnByJobOwnerAccn() ? null
				: ckJob.getTCoreAccnByJobOwnerAccn().toEntity(new TCoreAccn()));
		tckJob.setTCoreAccnByJobSlAccn(null == ckJob.getTCoreAccnByJobSlAccn() ? null
				: ckJob.getTCoreAccnByJobSlAccn().toEntity(new TCoreAccn()));
		tckJob.setTCoreAccnByJobToAccn(null == ckJob.getTCoreAccnByJobToAccn() ? null
				: ckJob.getTCoreAccnByJobToAccn().toEntity(new TCoreAccn()));

		ckJobDao.add(tckJob);

		return ckJob;
	}

	protected String getSysParam(String key) throws Exception {
		LOG.debug("getSysParam");
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam == null)
			throw new EntityNotFoundException("sysParam " + key + " not configured");

		return sysParam.getSysVal();

	}

	// CT-51 - [CO Operations-Import Job] System goes in processing after Click of
	// Delete button
	@Override
	public CkJobTruck deleteJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// DELETE JOB sets the status to CANCELLED instead of DELETED as per RTM
		checkJobState(dto.getTCkJob().getJobId(), JobStates.CAN);

		CkJob ckJob = workflowService.moveState(FormActions.DELETE, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		// set RcdDtDelete and RcdUidDelete
		dto.getTCkJob().getTCkRecordDate().setRcdDtDelete(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidDelete(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.DELETE);
		return dto;
	}

	private void updateVehicleTypeFromVehicle(String jobTruckId, CkCtVeh vehEntity) throws Exception {

		TCkJobTruck jobTruck = ckJobTruckDao.find(jobTruckId);

		// if vehicle is undefine, update vehicleType in JobTruck table.
		if (null != jobTruck.getTCkCtMstVehType() && null != vehEntity.getTCkCtMstVehType()
				&& ICkConstant.VEHICLE_TYPE_UNDEFINE.equalsIgnoreCase(jobTruck.getTCkCtMstVehType().getVhtyId())) {

			jobTruck.setTCkCtMstVehType(new TCkCtMstVehType(vehEntity.getTCkCtMstVehType().getVhtyId(), ""));
			ckJobTruckDao.saveOrUpdate(jobTruck);
		}
	}
	
	private String getIdPrefix(String id) {
		return id.replaceAll("-(\\d+)$", "");
	}
	
	private String getNewCloneId(String prefix, int seq) {
		return prefix + "-" + seq;
	}

	private String[] getIdPrefixSeq(String jobId) throws Exception {

		String jobIdPre = this.getIdPrefix(jobId);
		List<TCkJobTruck> jobTruckList = ckJobTruckDao.findByIdPrefixs(jobIdPre);
		List<String> idSeqList = jobTruckList.stream().map(TCkJobTruck::getJobId)
				.map(id -> id.replaceAll(".*-(\\d+)$", "$1")).collect(Collectors.toList());

		List<Integer> idSeqIntList = idSeqList.stream().map(id -> {
			try {
				return new Integer(id);
			} catch (Exception e) {
			}
			return 0;
		}).collect(Collectors.toList());

		String maxSeq = Collections.max(idSeqIntList) + "";
		String[] strArray = { jobIdPre, maxSeq };
		
		LOG.info("getIdPrefixSeq " + jobIdPre + "-" + maxSeq);

		return strArray;
	}
}
