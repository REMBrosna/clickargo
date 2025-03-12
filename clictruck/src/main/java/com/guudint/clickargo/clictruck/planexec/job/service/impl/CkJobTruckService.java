/**
 * 
 */
package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.guudint.clickargo.clictruck.dashboard.service.impl.TruckJobsDashboardService;
import com.guudint.clickargo.clictruck.jobupload.service.SendEpodService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.guudint.clickargo.clictruck.planexec.trip.dto.*;
import com.guudint.clickargo.master.model.TCkMstJobState;
import com.vcc.camelone.ccm.dto.CoreContact;
import com.vcc.camelone.ccm.model.embed.TCoreContact;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clicdo.common.IClicTruckConstant;
import com.guudint.clickargo.clicservice.service.impl.CkSvcActionMaskService;
import com.guudint.clickargo.clicservice.service.impl.CkSvcWorkflowService;
import com.guudint.clickargo.clictruck.accnconfigex.service.ClictruckAccnConfigExService;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.dto.DriverStates;
import com.guudint.clickargo.clictruck.common.dto.VehStates;
import com.guudint.clickargo.clictruck.common.model.TCkCtDept;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.finacing.constant.FinancingConstants.FinancingTypes;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.service.ITruckJobCreditService;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehState;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.opm.service.IOpmService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.dto.FailedDescription;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiAssignDrvVeh;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiRecordRequest;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiRecordResponse;
import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobCreateEvent;
import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobDeleteEvent;
import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobStateChangeEvent;
import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobSubmitEvent;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.IJobTruckStateService;
import com.guudint.clickargo.clictruck.planexec.job.validator.TruckJobValidator;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripReimbursementDao;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.CkCtTripWorkflowServiceImpl.TripStatus;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCharge;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripReimbursement;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoMmService;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.common.CkErrorCodes;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstJobType;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.JobTypes;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.guudint.clickargo.master.model.TCkMstShipmentType;
import com.guudint.clickargo.validator.ValidationGroup.CreateValid;
import com.guudint.clickargo.validator.ValidationGroup.DeleteValid;
import com.guudint.clickargo.validator.ValidationGroup.GetValid;
import com.guudint.clickargo.validator.ValidationGroup.PayValid;
import com.guudint.clickargo.validator.ValidationGroup.SubmitValid;
import com.guudint.clickargo.validator.ValidationGroup.UpdateValid;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.util.email.SysParam;

/**
 * @author adenny
 *
 */
public class CkJobTruckService extends AbstractJobService<CkJobTruck, TCkJobTruck, String> implements ICkConstant {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkJobTruckService.class);
	private static String AUDIT_TAG = "CK JOB TRUCK";
	private static String TABLE_NAME = "T_CK_JOB_TRUCK";
	private static String HISTORY = "history";
	private static String DEFAULT = "default";
	private HashMap<String, String> listServices;

	private static final Character FINANCE = 'F';
	private static final Character NON_FINANCE = 'N';
	private static final Character FINANCE_EXT = 'E';
	private static final String SAGAWA_ACCN = "SSA";
	private static final String MAWB = "MAWB";
	private static final String HAWB = "HAWB";

	// Attributes
	/////////////
	@Autowired
	GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	private CkJobTruckDao ckJobTruckDao;

	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;

	@Autowired
	@Qualifier("ckCtContactDetailDao")
	private GenericDao<TCkCtContactDetail, String> ckCtContactDetailDao;

	@Autowired
	@Qualifier("ckCtTripDao")
	private GenericDao<TCkCtTrip, String> ckCtTripDao;

	@Autowired
	@Qualifier("ckCtRateTableDao")
	private GenericDao<TCkCtRateTable, String> ckCtRateTableDao;

	@Autowired
	@Qualifier("ckCtTripRateDao")
	private GenericDao<TCkCtTripRate, String> ckCtTripRateDao;

	@Autowired
	@Qualifier("ckCtTripChargeDao")
	private GenericDao<TCkCtTripCharge, String> ckCtTripChargeDao;

	@Autowired
	@Qualifier("ckCtDrvService")
	private IEntityService<TCkCtDrv, String, CkCtDrv> ckCtDrvService;

	@Autowired
	@Qualifier("ckCtVehService")
	private IEntityService<TCkCtVeh, String, CkCtVeh> ckCtVehService;

	@Autowired
	private CkCtContractDao ckCtContractDao;

	@Autowired
	private CkSvcActionMaskService ckSvcActionMaskService;

	@Autowired
	private CkCtTripService ckCtTripService;

	@Autowired
	private CkSvcWorkflowService workflowService;

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	@Autowired
	private CkCtTripCargoMmService mmTripService;

	@Autowired
	CkTruckMiscMobileService ckTruckMiscMobileService;

	@Autowired
	ClicTruckMiscService clicTruckMiscService;

	@Autowired
	private IJobTruckStateService<CkJobTruck> jobStateService;

	@Autowired
	private CkCtTripReimbursementDao ckCtTripReimbursementDao;

	@Autowired
	private ITruckJobCreditService truckJobCreditService;

	@Autowired
	private IEntityService<TCkCtToInvoice, String, CkCtToInvoice> ckCtToInvoiceService;

	@Autowired
	protected SysParam sysParam;

	@Autowired
	@Qualifier("clictruckAccnConfigExService")
	private ClictruckAccnConfigExService cltAccnConfigExService;

	@Autowired
	private CkJobTruckAddtlAttrService jobTruckAddAttrService;

	@Autowired
	private IOpmService opmService;
	
	@Autowired
	ClictruckAccnConfigExService accnConfigExService;

	@Autowired
	private CkJobTruckExtDao ckJobTruckExtDao;

	@Autowired
	private SendEpodService sendEpodService;

	// Constructor
	///////////////////
	public CkJobTruckService() {
		super("ckJobTruckDao", AUDIT_TAG, TCkJobTruck.class.getName(), TABLE_NAME);
	}

	private ObjectMapper mapper = new ObjectMapper();

	// Override Methods
	///////////////////
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			Principal principal = ckSession.getPrincipal();
			if (principal == null) {
				// throw new ProcessingException("principal is null");
				// principal is null at scheduler, payIn and payout.
			}

			TCkJobTruck entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);
			this.initEnity(entity);

			if (principal != null) {
				// principal is null at scheduler, payIn and payout.
				this.isPermissionOperateJob(entity, principal);
			}

			CkJobTruck jobTruck = this.dtoFromEntity(entity);

			// clear amount to 0 for FF-CO
			if (principal != null && principal.getCoreAccn() != null) {
				if (principal.getCoreAccn().getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF_CO.name())) {

					jobTruck = this.clearAmountTo0(jobTruck);
				}
			}
			return jobTruck;

		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("findById", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("findById", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.IEntityService#deleteById(java.lang.String,
	 *      com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("deleteById");

		Date now = Calendar.getInstance().getTime();
		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");
			if (null == principal)
				throw new ParameterException("param prinicipal null");

			String[] idParts = id.split(":");
			if (idParts.length != 2)
				throw new ParameterException("id not formulated " + id);

			TCkJobTruck entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkJobTruck dto = dtoFromEntity(entity);
			this.delete(dto, principal);
			return dto;
		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("deleteById", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("deleteById", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#delete(java.lang.Object,
	 *      com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruck delete(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		dto.getTCkJob().getTCkMstJobState().setJbstId(JobStates.DEL.name());
		dto.getTCkJob().getTCkRecordDate().setRcdDtDelete(new Date());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal, false);
		ckJobService.update(dto.getTCkJob(), principal, false);
		return super.delete(dto, principal);
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.IEntityService#filterBy(com.vcc.
	 *      camelone.common.controller.entity.EntityFilterRequest)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkJobTruck> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkJobTruck dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkJobTruck o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkJobTruck> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkJobTruck> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x);
				} catch (ParameterException | ProcessingException e) {
					LOG.error("filterBy", e);
				}
				return null;
			}).collect(Collectors.toList());

			return dtos;
		} catch (ParameterException | ProcessingException ex) {
			LOG.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("filterBy", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#initEnity(java.lang.Object)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkJobTruck initEnity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkJob());
			Optional.ofNullable(entity.getTCkJob().getTCkMstJobState()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobCoAccn()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobFfAccn()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobOwnerAccn())
					.ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobToAccn()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobSlAccn()).ifPresent(e -> Hibernate.initialize(e));
			Hibernate.initialize(entity.getTCkCtContactDetailByJobContactTo());
			Hibernate.initialize(entity.getTCkCtContactDetailByJobContactCoFf());
			Hibernate.initialize(entity.getTCkCtDebitNoteByJobInvoiceeDebitNote());
			Hibernate.initialize(entity.getTCkCtDebitNoteByJobInvoicerDebitNote());
			Hibernate.initialize(entity.getTCkCtDrv());
			Hibernate.initialize(entity.getTCkCtMstVehType());
			Hibernate.initialize(entity.getTCkCtToInvoice());
			Hibernate.initialize(entity.getTCkCtVeh());
			Hibernate.initialize(entity.getTCoreAccnByJobPartyTo());
			Hibernate.initialize(entity.getTCoreAccnByJobPartyCoFf());
			Optional.ofNullable(entity.getTCkCtDeptByJobCoDepartment()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkCtDeptByJobToDepartment()).ifPresent(e -> Hibernate.initialize(e));
		}
		return entity;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#entityFromDTO(java.lang.Object)
	 *
	 */
	@Override
	protected TCkJobTruck entityFromDTO(CkJobTruck dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkJobTruck entity = new TCkJobTruck();
			entity = dto.toEntity(entity);
			// no deep copy from BeanUtils
			TCkJob ckParentJob = new TCkJob();
			TCkMstJobState tCkMstJobState = new TCkMstJobState();
			entity.setTCkJob(null == dto.getTCkJob() ? null : dto.getTCkJob().toEntity(ckParentJob));
			if (dto.getTCkJob() != null) {
				ckParentJob.setTCkMstShipmentType(null != dto.getTCkJob().getTCkMstShipmentType()
						? dto.getTCkJob().getTCkMstShipmentType().toEntity(new TCkMstShipmentType())
						: null);
				ckParentJob.setTCoreAccnByJobCoAccn(null != dto.getTCkJob().getTCoreAccnByJobCoAccn()
						? dto.getTCkJob().getTCoreAccnByJobCoAccn().toEntity(new TCoreAccn())
						: null);
				ckParentJob.setTCoreAccnByJobFfAccn(dto.getTCoreAccnByJobPartyCoFf().toEntity(new TCoreAccn()));
				ckParentJob.setTCoreAccnByJobOwnerAccn(null != dto.getTCkJob().getTCoreAccnByJobOwnerAccn()
						? dto.getTCkJob().getTCoreAccnByJobOwnerAccn().toEntity(new TCoreAccn())
						: null);
				ckParentJob.setTCoreAccnByJobToAccn(null != dto.getTCkJob().getTCoreAccnByJobToAccn()
						? dto.getTCkJob().getTCoreAccnByJobToAccn().toEntity(new TCoreAccn())
						: null);
				tCkMstJobState.setJbstId(dto.getTCkJob().getTCkMstJobState() != null ?
						dto.getTCkJob().getTCkMstJobState().getJbstId() : null);
				ckParentJob.setTCkMstJobState(tCkMstJobState);
			}

			entity.setTCkCtContactDetailByJobContactTo(null == dto.getTCkCtContactDetailByJobContactTo() ? null
					: dto.getTCkCtContactDetailByJobContactTo().toEntity(new TCkCtContactDetail()));
			entity.setTCkCtContactDetailByJobContactCoFf(null == dto.getTCkCtContactDetailByJobContactCoFf() ? null
					: dto.getTCkCtContactDetailByJobContactCoFf().toEntity(new TCkCtContactDetail()));

			entity.setTCkCtDebitNoteByJobInvoiceeDebitNote(null == dto.getTCkCtDebitNoteByJobInvoiceeDebitNote() ? null
					: dto.getTCkCtDebitNoteByJobInvoiceeDebitNote().toEntity(new TCkCtDebitNote()));
			entity.setTCkCtDebitNoteByJobInvoicerDebitNote(null == dto.getTCkCtDebitNoteByJobInvoicerDebitNote() ? null
					: dto.getTCkCtDebitNoteByJobInvoicerDebitNote().toEntity(new TCkCtDebitNote()));

			// check drvId is present or not
			entity.setTCkCtDrv(null == dto.getTCkCtDrv() || StringUtils.isEmpty(dto.getTCkCtDrv().getDrvId()) ? null
					: dto.getTCkCtDrv().toEntity(new TCkCtDrv()));

			// check vhTyId is present or not
			entity.setTCkCtMstVehType(
					null == dto.getTCkCtMstVehType() || StringUtils.isEmpty(dto.getTCkCtMstVehType().getVhtyId()) ? null
							: dto.getTCkCtMstVehType().toEntity(new TCkCtMstVehType()));
			entity.setTCkCtToInvoice(
					null == dto.getTCkCtToInvoice() ? null : dto.getTCkCtToInvoice().toEntity(new TCkCtToInvoice()));
			// check vhId is present or not
			entity.setTCkCtVeh(null == dto.getTCkCtVeh() || StringUtils.isEmpty(dto.getTCkCtVeh().getVhId()) ? null
					: dto.getTCkCtVeh().toEntity(new TCkCtVeh()));

			entity.setTCoreAccnByJobPartyTo(null == dto.getTCoreAccnByJobPartyTo() ? null
					: dto.getTCoreAccnByJobPartyTo().toEntity(new TCoreAccn()));
			entity.setTCoreAccnByJobPartyCoFf(null == dto.getTCoreAccnByJobPartyCoFf() ? null
					: dto.getTCoreAccnByJobPartyCoFf().toEntity(new TCoreAccn()));

			entity.setJobDrvOth(null == dto.getJobDrvOth() ? null : dto.getJobDrvOth().toString());
			entity.setJobVehOth(null == dto.getJobVehOth() ? null : dto.getJobVehOth().toString());

			entity.setTCkCtDeptByJobCoDepartment(null == dto.getTCkCtDeptByJobCoDepartment() ? null
					: dto.getTCkCtDeptByJobCoDepartment().toEntity(new TCkCtDept()));
			entity.setTCkCtDeptByJobToDepartment(null == dto.getTCkCtDeptByJobToDepartment() ? null
					: dto.getTCkCtDeptByJobToDepartment().toEntity(new TCkCtDept()));
			return entity;
		} catch (ParameterException ex) {
			LOG.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#dtoFromEntity(java.lang.Object)
	 *
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck dtoFromEntity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		Principal principal = ckSession.getPrincipal();
		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobTruck dto = new CkJobTruck(entity);
			// no deep copy from BeanUtils
			TCkJob ckJobE = entity.getTCkJob();
			if (null != ckJobE) {
				CkJob ckJob = new CkJob(ckJobE);
				Optional.ofNullable(entity.getTCkJob().getTCkMstJobState())
						.ifPresent(e -> ckJob.setTCkMstJobState(new CkMstJobState(e)));
				Optional.ofNullable(ckJobE.getTCkMstShipmentType())
						.ifPresent(e -> ckJob.setTCkMstShipmentType(new CkMstShipmentType(e)));
				Optional.ofNullable(ckJobE.getTCkMstJobType())
						.ifPresent(e -> ckJob.setTCkMstJobType(new CkMstJobType(e)));
				Optional.ofNullable(ckJobE.getTCkRecordDate())
						.ifPresent(e -> ckJob.setTCkRecordDate(new CkRecordDate(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobCoAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobCoAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobFfAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobFfAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobOwnerAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobOwnerAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobToAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobToAccn(new CoreAccn(e)));
				Optional.ofNullable(ckJobE.getTCoreAccnByJobSlAccn())
						.ifPresent(e -> ckJob.setTCoreAccnByJobSlAccn(new CoreAccn(e)));

				dto.setTCkJob(ckJob);

				// Optional<TCkMstJobState> opJobState =
				// Optional.ofNullable(ckJobE.getTCkMstJobState());
				// if (opJobState.isPresent()) {
				// if (StringUtils.isNotBlank(opJobState.get().getJbstId())
				// && StringUtils.equalsIgnoreCase(JobStates.REJ.name(),
				// opJobState.get().getJbstId())) {
				// // only check if the state is rejected
				dto.setHasRemarks(ckJobTruckUtilService.isJobRemarked(ckJob.getJobId()));
				// }
				// }

			}

			Optional<TCkCtDebitNote> opckCtDebitNoteByJobInvoiceeDebitNote = Optional
					.ofNullable(entity.getTCkCtDebitNoteByJobInvoiceeDebitNote());
			dto.setTCkCtDebitNoteByJobInvoiceeDebitNote(opckCtDebitNoteByJobInvoiceeDebitNote.isPresent()
					? new CkCtDebitNote(opckCtDebitNoteByJobInvoiceeDebitNote.get())
					: null);

			Optional<TCkCtDebitNote> opCkCtDebitNoteByJobInvoicerDebitNote = Optional
					.ofNullable(entity.getTCkCtDebitNoteByJobInvoicerDebitNote());
			dto.setTCkCtDebitNoteByJobInvoicerDebitNote(opCkCtDebitNoteByJobInvoicerDebitNote.isPresent()
					? new CkCtDebitNote(opCkCtDebitNoteByJobInvoicerDebitNote.get())
					: null);

			Optional<TCkCtDrv> opCkCtDrv = Optional.ofNullable(entity.getTCkCtDrv());
			dto.setTCkCtDrv(opCkCtDrv.isPresent() ? new CkCtDrv(opCkCtDrv.get()) : null);

			Optional<TCkCtMstVehType> opCkCtMstVehType = Optional.ofNullable(entity.getTCkCtMstVehType());
			dto.setTCkCtMstVehType(opCkCtMstVehType.isPresent() ? new CkCtMstVehType(opCkCtMstVehType.get()) : null);

			Optional<TCkCtToInvoice> opCkCtToInvoice = Optional.ofNullable(entity.getTCkCtToInvoice());
			dto.setTCkCtToInvoice(opCkCtToInvoice.isPresent() ? new CkCtToInvoice(opCkCtToInvoice.get()) : null);

			Optional<TCkCtVeh> opCkCtVeh = Optional.ofNullable(entity.getTCkCtVeh());
			dto.setTCkCtVeh(opCkCtVeh.isPresent() ? new CkCtVeh(opCkCtVeh.get()) : null);
			if (opCkCtVeh.isPresent()) {
				dto.getTCkCtVeh().setTCkCtMstVehType(new CkCtMstVehType(opCkCtVeh.get().getTCkCtMstVehType()));
			}

			Optional<TCkCtContactDetail> opCkCtContactDetailByJobContactTo = Optional
					.ofNullable(entity.getTCkCtContactDetailByJobContactTo());
			dto.setTCkCtContactDetailByJobContactTo(opCkCtContactDetailByJobContactTo.isPresent()
					? new CkCtContactDetail(opCkCtContactDetailByJobContactTo.get())
					: null);

			Optional<TCkCtContactDetail> opCkCtContactDetailByJobContactCoFf = Optional
					.ofNullable(entity.getTCkCtContactDetailByJobContactCoFf());
			dto.setTCkCtContactDetailByJobContactCoFf(opCkCtContactDetailByJobContactCoFf.isPresent()
					? new CkCtContactDetail(opCkCtContactDetailByJobContactCoFf.get())
					: null);

			Optional<TCoreAccn> opCoreAccnByJobPartyTo = Optional.ofNullable(entity.getTCoreAccnByJobPartyTo());
			dto.setTCoreAccnByJobPartyTo(
					opCoreAccnByJobPartyTo.isPresent() ? new CoreAccn(opCoreAccnByJobPartyTo.get()) : null);
			Optional<TCoreAccn> opCoreAccnByJobPartyCoFf = Optional.ofNullable(entity.getTCoreAccnByJobPartyCoFf());
			dto.setTCoreAccnByJobPartyCoFf(
					opCoreAccnByJobPartyCoFf.isPresent() ? new CoreAccn(opCoreAccnByJobPartyCoFf.get()) : null);

			Optional<TCkCtDept> opToDept = Optional.ofNullable(entity.getTCkCtDeptByJobToDepartment());
			dto.setTCkCtDeptByJobToDepartment(opToDept.isPresent() ? new CkCtDept(opToDept.get()) : null);

			Optional<TCkCtDept> opCoDept = Optional.ofNullable(entity.getTCkCtDeptByJobCoDepartment());
			dto.setTCkCtDeptByJobCoDepartment(opCoDept.isPresent() ? new CkCtDept(opCoDept.get()) : null);

			// Load job trips if have
			List<CkCtTrip> tckCtTrips = ckCtTripService.findTripsByTruckJobAndStatus(dto.getJobId(),
					Arrays.asList(TripStatus.M_ACTIVE.getStatusCode(), TripStatus.DLV.getStatusCode(),
							TripStatus.M_DELIVERED.getStatusCode(), TripStatus.M_PICKED_UP.getStatusCode()));

			if (tckCtTrips != null && tckCtTrips.size() > 0) {
				boolean isFirstMile = ckJobTruckUtilService
						.isFirstMile(dto.getTCkJob().getTCkMstShipmentType().getShtId());
				if (isFirstMile) {
					// expecting only one for first mile
					tckCtTrips.forEach(e -> {
						try {
							CkCtTripCargoFm fmTrip = ckJobTruckUtilService.getTripCargoFirstMile(e);
							e.setTckCtTripCargoFmList(Arrays.asList(fmTrip));

							dto.setPickUp(e.getTCkCtTripLocationByTrFrom());
							dto.setLastDrop(e.getTCkCtTripLocationByTrTo());

						} catch (Exception ex) {
							LOG.error("fm trip error", ex);
						}
					});
				} else {
					tckCtTrips.forEach(t -> {
						try {
							List<CkCtTripCargoMm> fmTrip = mmTripService.findTripCargoFmmsByTripId(t.getTrId());
							if (!fmTrip.isEmpty()) {
								List<TCkJobTruckExt> ckJobTruckEXTs = null;
								if (null != principal && principal.getCoreAccn() != null){
									if (principal.getCoreAccn().getAccnId().equalsIgnoreCase(SAGAWA_ACCN)) {
										if (ckJobE != null){
											ckJobTruckEXTs = this.loadJobTruckExt(ckJobE.getJobId());
											// Sum values for cargo quantity and weight, handling potential null values
											double totalCargoQty = fmTrip.stream()
													.filter(Objects::nonNull)
													.mapToDouble(trip -> trip.getCgCargoQty() != null ? trip.getCgCargoQty() : 0.000)
													.sum();
											double totalCargoWeight = fmTrip.stream()
													.filter(Objects::nonNull)
													.mapToDouble(trip -> trip.getCgCargoWeight() != null ? trip.getCgCargoWeight() : 0.000)
													.sum();
											dto.setSumQty(String.valueOf(totalCargoQty));
											dto.setSumWeight(String.valueOf(totalCargoWeight));
											// Check if ckJobTruckEXTs is not null or empty
											if (ckJobTruckEXTs != null && !ckJobTruckEXTs.isEmpty()) {
												for (TCkJobTruckExt entityEXTs : ckJobTruckEXTs) {
													if (entityEXTs.getJextKey().equalsIgnoreCase(MAWB)) {
														dto.setMawb(entityEXTs.getJextVal());
													} else if (entityEXTs.getJextKey().equalsIgnoreCase(HAWB)) {
														dto.setHawb(entityEXTs.getJextVal());
													}
												}
											}
										}
									}
								}
								t.setTripCargoMmList(fmTrip);
							}
						} catch (Exception ex) {
							LOG.error("fm trip error : " + ex.getMessage());
						}
					});

					tckCtTrips.forEach(e -> {
						try {
							// Compute total reimbursement for each trip
							List<TCkCtTripReimbursement> tCkCtTripReimbursements = ckCtTripReimbursementDao
									.findByTripIdAndStatus(e.getTrId(), RecordStatus.ACTIVE.getCode());

							BigDecimal totalReimbursementCharge = BigDecimal.ZERO;

							if (tCkCtTripReimbursements != null && tCkCtTripReimbursements.size() > 0) {
								totalReimbursementCharge = tCkCtTripReimbursements.stream()
										.map(TCkCtTripReimbursement::getTrTotal)
										.reduce(BigDecimal.ZERO, BigDecimal::add);
							}
							e.setTotalReimbursementCharge(totalReimbursementCharge);

						} catch (Exception ex) {
							LOG.error("fm trip error", ex);
						}
					});

					dto.setPickUp(tckCtTrips.get(0).getTCkCtTripLocationByTrFrom());
					dto.setLastDrop(tckCtTrips.get(tckCtTrips.size() - 1).getTCkCtTripLocationByTrTo());
				}

				dto.setTckCtTripList(tckCtTrips);
			}

			// is Domestic
			dto.setDomestic(!ckJobTruckUtilService.isFirstMile(dto.getTCkJob().getTCkMstShipmentType().getShtId()));

			if (entity.getJobDrvOth() != null) {
				Map<String, Object> map = stringToObject(entity.getJobDrvOth());
				Map<String, Object> data = new HashMap<>();
				data.put("drvId", "OTHER");
				String name = map.containsKey("drvName") ? map.get("drvName").toString() : null;
				String phone = map.containsKey("drvPhone") ? map.get("drvPhone").toString() : null;
				data.put("drvName", "null".equals(name) ? null : name);
				data.put("drvPhone", "null".equals(phone) ? null : phone);
				dto.setJobDrvOth(data);
			}

			if (entity.getJobVehOth() != null) {
				Map<String, Object> map = stringToObject(entity.getJobVehOth());
				Map<String, Object> data = new HashMap<>();
				String plateNo = map.containsKey("vhPlateNo") ? map.get("vhPlateNo").toString() : null;
				data.put("vhId", "OTHER");
				data.put("vhPlateNo", "null".equals(plateNo) ? null : plateNo);
				dto.setJobVehOth(data);
			}

			// SG REQUIREMENT: Set the hidden fields if there is
			dto.setHiddenFields(cltAccnConfigExService.getFieldsToHide(principalUtilService.getPrincipal()));

			// SG REQUIREMENT: set flag to show additional fields
			CKCountryConfig ctryCfg = cltAccnConfigExService.getCtryEnv();
			dto.setShowAdditionalFields(ctryCfg.getCountry().equalsIgnoreCase("SG"));

			// Get the additional fields
			dto.setAddtlFields(jobTruckAddAttrService.getAdditionalFields(dto));

			Optional<TCoreContact> tCoreContact = Optional.ofNullable(entity.getTCoreAccnByJobPartyTo().getAccnContact());
			CoreContact coreContact = new CoreContact();
			tCoreContact.ifPresent(contact -> {
				coreContact.setContactTel(contact.getContactTel());
				coreContact.setContactEmail(contact.getContactEmail());
				coreContact.setContactFax(contact.getContactFax());
				dto.getTCoreAccnByJobPartyTo().setAccnContact(coreContact);
			});

			return dto;
		} catch (ParameterException ex) {
			LOG.error("dtoFromEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#entityKeyFromDTO(java.lang.Object)
	 *
	 */
	@Override
	protected String entityKeyFromDTO(CkJobTruck dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getJobId() ? null : dto.getJobId();
		} catch (ParameterException ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#updateEntity(com.vcc.camelone.common.service.entity.AbstractEntityService.ACTION,
	 *      java.lang.Object, com.vcc.camelone.cac.model.Principal, java.util.Date)
	 *
	 */
	@Override
	protected TCkJobTruck updateEntity(ACTION attriubte, TCkJobTruck entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");
			if (null == principal)
				throw new ParameterException("param principal null");
			if (null == date)
				throw new ParameterException("param date null");

			Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
			switch (attriubte) {
			case CREATE:
				entity.setJobUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setJobDtCreate(date);
				entity.setJobUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setJobDtLupd(date);
				break;

			case MODIFY:
				entity.setJobUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setJobDtLupd(date);
				break;

			default:
				break;
			}

			return entity;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("updateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#updateEntityStatus(java.lang.Object,
	 *      char)
	 *
	 */
	@Override
	protected TCkJobTruck updateEntityStatus(TCkJobTruck entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setJobStatus(status);
			return entity;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("updateEntityStatus", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#preSaveUpdateDTO
	 *      (java.lang.Object, java.lang.Object)
	 *
	 */
	@Override
	protected CkJobTruck preSaveUpdateDTO(TCkJobTruck storedEntity, CkJobTruck dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setJobUidCreate(storedEntity.getJobUidCreate());
			dto.setJobDtCreate(storedEntity.getJobDtCreate());

			return dto;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("preSaveUpdateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#
	 *      preSaveValidation(java.lang.Object,
	 *      com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	protected void preSaveValidation(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {

	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#
	 *      preUpdateValidation(java.lang.Object,
	 *      com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	protected ServiceStatus preUpdateValidation(CkJobTruck dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#getWhereClause(java.lang.Object,
	 *      boolean)
	 *
	 */
	@Override
	protected String getWhereClause(CkJobTruck dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			StringBuffer searchStatement = new StringBuffer();
			if (null != dto.getJobStatus()) {
				searchStatement.append(getOperator(wherePrinted) + "o.jobStatus=:jobStatus");
				wherePrinted = true;
			}

			Principal principal = ckSession.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal is null");

			CoreAccn accn = principal.getCoreAccn();

			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.TCoreAccnByJobPartyCoFf.accnId = :accnId");
				wherePrinted = true;
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;
			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {
				// For Truck Operators
				searchStatement.append(getOperator(wherePrinted)).append("o.TCoreAccnByJobPartyTo.accnId = :accnId");
				wherePrinted = true;
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;

				searchStatement.append(getOperator(wherePrinted)).append("o.jobIsFinanced IN :financeOptions");
				wherePrinted = true;

			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF_CO.name())) {
				// For FF-CO
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCoreAccnByJobSlAccn.accnId = :accnId");
				wherePrinted = true;

				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId IN :jobTruckStates");
				wherePrinted = true;
			}

			// check if the user is in a department
			CkCtDept principalDept = ckJobTruckUtilService.getPrincipalDepartment(principal);
			if (principalDept != null) {
				if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
						|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())) {
					// OR means can still view jobs that no dept associated
					searchStatement.append(getOperator(wherePrinted)).append(
							"(o.TCkCtDeptByJobCoDepartment.deptId = :deptId OR o.TCkCtDeptByJobCoDepartment.deptId is null)");

				} else {
					// OR means can still view jobs that no dept associated
					searchStatement.append(getOperator(wherePrinted)).append(
							"(o.TCkCtDeptByJobToDepartment.deptId = :deptId OR o.TCkCtDeptByJobToDepartment.deptId is null)");

				}

				wherePrinted = true;
			}

			if (StringUtils.isNotBlank(dto.getJobId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.jobId LIKE :jobId");
				wherePrinted = true;
			}

			Optional<CkJob> opCkJob = Optional.of(dto.getTCkJob());
			if (opCkJob.isPresent()) {
				Optional<CkMstShipmentType> opShipmntType = opCkJob.map(CkJob::getTCkMstShipmentType);
				if (opShipmntType.isPresent()) {
					if (StringUtils.isNotBlank(opShipmntType.get().getShtId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstShipmentType.shtId = :shtId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opShipmntType.get().getShtName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstShipmentType.shtName = :shtName");
						wherePrinted = true;

					}
				}

				Optional<CkRecordDate> opCkRecordDate = Optional.of(dto.getTCkJob()).map(CkJob::getTCkRecordDate);
				if (opCkRecordDate.isPresent()) {
					if (opCkRecordDate.get().getRcdDtSubmit() != null) {
						searchStatement.append(getOperator(wherePrinted))
								.append("DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtSubmit,'%d/%m/%Y') = :rcdDtSubmit");
						wherePrinted = true;
					}
				}

				Optional<CkMstJobState> opCkJobMstState = Optional.of(dto.getTCkJob()).map(CkJob::getTCkMstJobState);
				if (opCkJobMstState.isPresent()) {
					if (StringUtils.isNotBlank(opCkJobMstState.get().getJbstId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstJobState.jbstId in :jobState");
						wherePrinted = true;
					}
				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyTo = Optional.of(dto.getTCoreAccnByJobPartyTo());
			if (opCoreAccnByJobPartyTo.isPresent()) {
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnId())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCoreAccnByJobPartyTo.accnId = :toAccnId");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnName())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCoreAccnByJobPartyTo.accnName LIKE :toAccnName");
					wherePrinted = true;
				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyCoFf = Optional.of(dto.getTCoreAccnByJobPartyCoFf());
			if (opCoreAccnByJobPartyCoFf.isPresent()) {
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyCoFf.get().getAccnId())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCoreAccnByJobPartyCoFf.accnId = :coFfAccnId");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyCoFf.get().getAccnName())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCoreAccnByJobPartyCoFf.accnName LIKE :coFfAccnName");
					wherePrinted = true;
				}
			}

			if (StringUtils.isNotBlank(dto.getJobShipmentRef())) {
				searchStatement.append(getOperator(wherePrinted) + "o.jobShipmentRef LIKE :jobShipmentRef");
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(dto.getJobCustomerRef())) {
				searchStatement.append(getOperator(wherePrinted) + "o.jobCustomerRef LIKE :jobCustomerRef");
				wherePrinted = true;
			}

			if (Objects.nonNull(dto.getPickUp()) && StringUtils.isNotBlank(dto.getPickUp().getTlocLocAddress())) {
				searchStatement.append(
						getOperator(wherePrinted) +
								" o.jobId IN ( " +
								"SELECT trip.TCkJobTruck.jobId FROM TCkCtTrip trip " +
								"JOIN trip.TCkCtTripLocationByTrFrom loc " +
								"WHERE LOWER(TRIM(loc.tlocLocAddress)) LIKE LOWER(TRIM(:trFromLoc))) "

				);
				wherePrinted = true;
			}

			if (Objects.nonNull(dto.getLastDrop()) && StringUtils.isNotBlank(dto.getLastDrop().getTlocLocAddress())) {
				searchStatement.append(
						getOperator(wherePrinted) +
								" o.jobId IN ( " +
								"SELECT trip.TCkJobTruck.jobId FROM TCkCtTrip trip " +
								"JOIN trip.TCkCtTripLocationByTrTo loc " +
								"WHERE LOWER(TRIM(loc.tlocLocAddress)) LIKE LOWER(TRIM(:trToLoc))) "
				);
				wherePrinted = true;
			}

			Optional<Date> opJobDtBooking = Optional.ofNullable(dto.getJobDtBooking());
			if (opJobDtBooking.isPresent() && null != opJobDtBooking.get()) {
				searchStatement
						.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jobDtBooking,'%d/%m/%Y') = :jobDtBooking");
				wherePrinted = true;
			}
			Optional<Date> opJobDtPan = Optional.ofNullable(dto.getJobDtPlan());
			if (opJobDtPan.isPresent() && null != opJobDtPan.get()) {
				searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jobDtPlan,'%d/%m/%Y') = :jobDtPlan");
				wherePrinted = true;
			}
			Optional<Date> opJobDtDelivery = Optional.ofNullable(dto.getJobDtDelivery());
			if (opJobDtDelivery.isPresent() && null != opJobDtDelivery.get()) {
				searchStatement
						.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jobDtDelivery,'%d/%m/%Y') = :jobDtDelivery");
				wherePrinted = true;
			}
			Optional<Date> opJobDtCreate = Optional.ofNullable(dto.getJobDtCreate());
			if (opJobDtCreate.isPresent() && null != opJobDtCreate.get()) {
				searchStatement
						.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jobDtCreate,'%d/%m/%Y') = :jobDtCreate");
				wherePrinted = true;
			}
			Optional<Date> opJobDtLupd = Optional.ofNullable(dto.getJobDtLupd());
			if (opJobDtLupd.isPresent() && null != opJobDtLupd.get()) {
				searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jobDtLupd,'%d/%m/%Y') = :jobDtLupd");
				wherePrinted = true;
			}

			Optional<Date> opJobInPaymentDtDue = Optional.ofNullable(dto.getJobInPaymentDtDue());
			if (opJobInPaymentDtDue.isPresent() && null != opJobInPaymentDtDue.get()) {
				searchStatement.append(
						getOperator(wherePrinted) + "DATE_FORMAT(o.jobInPaymentDtDue,'%d/%m/%Y') = :jobInPaymentDtDue");
				wherePrinted = true;
			}
			Optional<Date> opJobOutPaymentDtDue = Optional.ofNullable(dto.getJobOutPaymentDtDue());
			if (opJobOutPaymentDtDue.isPresent() && null != opJobOutPaymentDtDue.get()) {
				searchStatement.append(getOperator(wherePrinted)
						+ "DATE_FORMAT(o.jobOutPaymentDtDue,'%d/%m/%Y') = :jobOutPaymentDtDue");
				wherePrinted = true;
			}

			if (StringUtils.isNotBlank(dto.getJobOutPaymentState())) {
				searchStatement.append(getOperator(wherePrinted)
						+ "( o.jobOutPaymentState in :jobOutPaymentState or o.jobOutPaymentState is null) ");
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(dto.getTCkCtDrv().getDrvName())) {
				searchStatement.append(getOperator(wherePrinted) + "(o.TCkCtDrv.drvName LIKE :drvName OR o.TCkCtDrv.drvLicenseNo LIKE :drvName)");
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(dto.getTCkCtVeh().getVhPlateNo())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TCkCtVeh.vhPlateNo LIKE :vhPlateNo");
				wherePrinted = true;
			}

			return searchStatement.toString();
		} catch (ParameterException ex) {
			LOG.error("getWhereClause", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getWhereClause", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#getParameters(
	 *      java.lang.Object)
	 *
	 */
	@Override
	protected HashMap<String, Object> getParameters(CkJobTruck dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = ckSession.getPrincipal();

			if (principal == null)
				throw new ProcessingException("principal is null");
			CoreAccn accn = principal.getCoreAccn();

			// Account Type Freight Forwarder
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())) {

				parameters.put("accnId", accn.getAccnId());

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {

					// for billed jobs, history is acknowledged and approved
					if (dto.isBilledJob()) {
						parameters.put("jobTruckStates", Arrays.asList(JobStates.VER.name(), JobStates.APP.name(),
								JobStates.ACK_BILL.name(), JobStates.APP_BILL.name()));
					} else {
						parameters.put("jobTruckStates",
								Arrays.asList(JobStates.APP.name(), JobStates.VER_BILL.name(), JobStates.DLV.name(),
										JobStates.ACK_BILL.name(), JobStates.APP_BILL.name(), JobStates.REJ.name(),
										JobStates.CAN.name()));
					}

				} else {
					// for bill jobs active is billed, added ver_bill for co/ff finance to
					// acknowledged.
					if (dto.isBilledJob()) {
						parameters.put("jobTruckStates", Arrays.asList(JobStates.BILLED.name(),
								JobStates.VER_BILL.name(), JobStates.ACK_BILL.name()));
					} else {
						parameters.put("jobTruckStates",
								Arrays.asList(JobStates.NEW.name(), JobStates.SUB.name(), JobStates.ACP.name(),
										JobStates.ASG.name(), JobStates.STRTD.name(), JobStates.VER.name(),
										JobStates.ONGOING.name(), JobStates.PAUSED.name()));
					}

				}
				// Account Type Trucking Operator
			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {

				parameters.put("accnId", accn.getAccnId());

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {
					// for jobs to bill history is approved
					if (dto.isJobForBilling()) {
						parameters.put("jobTruckStates",
								Arrays.asList(JobStates.APP.name(), JobStates.APP_BILL.name()));
						// Update Job Billing Dashboard and Listing - Do not include jobs that are
						// NON-FINANCED in Active/History.
						parameters.put("financeOptions", Arrays.asList(FINANCE, FINANCE_EXT));
					} else {
						parameters.put("jobTruckStates",
								Arrays.asList(JobStates.REJ.name(), JobStates.APP.name(), JobStates.VER.name(),
										JobStates.APP_BILL.name(), JobStates.VER_BILL.name(), JobStates.DLV.name()));
						// Update Job Billing Dashboard and Listing - Include all finance options here
						parameters.put("financeOptions", Arrays.asList(FINANCE, FINANCE_EXT, NON_FINANCE));
					}

				} else {
					// for jobs to bill active is delivered and billed
					if (dto.isJobForBilling()) {
						// added VERIFIED here
						parameters.put("jobTruckStates", Arrays.asList(JobStates.DLV.name(), JobStates.BILLED.name(),
								JobStates.VER.name(), JobStates.VER_BILL.name()));
						// Update Job Billing Dashboard and Listing - Do not include jobs that are
						// NON-FINANCED in Active/History.
						parameters.put("financeOptions", Arrays.asList(FINANCE, FINANCE_EXT));
					} else {
						parameters.put("jobTruckStates",
								Arrays.asList(JobStates.SUB.name(), JobStates.ACP.name(), JobStates.ASG.name(),
										JobStates.STRTD.name(), JobStates.VER.name(), JobStates.VER_BILL.name(),
										JobStates.ONGOING.name(), JobStates.PAUSED.name()));
						// Update Job Billing Dashboard and Listing - Include all finance options here
						parameters.put("financeOptions", Arrays.asList(FINANCE, FINANCE_EXT, NON_FINANCE));
					}

				}
			} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF_CO.name())) {

				parameters.put("accnId", accn.getAccnId());

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {
					// for jobs to bill history is approved
					if (dto.isJobForBilling()) {
						parameters.put("jobTruckStates",
								Arrays.asList(JobStates.APP.name(), JobStates.APP_BILL.name()));
					} else {
						parameters.put("jobTruckStates",
								Arrays.asList(JobStates.REJ.name(), JobStates.CAN.name(), JobStates.APP.name(),
										JobStates.VER.name(), JobStates.APP_BILL.name(), JobStates.VER_BILL.name(),
										JobStates.DLV.name()));
					}

				} else {
					// for jobs to bill active is delivered and billed
					if (dto.isJobForBilling()) {
						// added VERIFIED here
						parameters.put("jobTruckStates", Arrays.asList(JobStates.DLV.name(), JobStates.BILLED.name(),
								JobStates.VER.name(), JobStates.VER_BILL.name()));
					} else {
						parameters.put("jobTruckStates",
								Arrays.asList(JobStates.SUB.name(), JobStates.ACP.name(), JobStates.ASG.name(),
										JobStates.STRTD.name(), JobStates.VER.name(), JobStates.VER_BILL.name(),
										JobStates.ONGOING.name(), JobStates.PAUSED.name()));
					}

				}
			}

			// check if the user is in a department
			CkCtDept principalDept = ckJobTruckUtilService.getPrincipalDepartment(principal);
			if (principalDept != null) {
				parameters.put("deptId", principalDept.getDeptId());
			}

			if (StringUtils.isNotBlank(dto.getJobId())) {
				parameters.put("jobId", "%" + dto.getJobId() + "%");
			}

			Optional<CkJob> opCkJob = Optional.of(dto.getTCkJob());
			if (opCkJob.isPresent()) {
				Optional<CkMstShipmentType> opShipmntType = opCkJob.map(CkJob::getTCkMstShipmentType);
				if (opShipmntType.isPresent()) {
					if (StringUtils.isNotBlank(opShipmntType.get().getShtId()))
						parameters.put("shtId", opShipmntType.get().getShtId());

					if (StringUtils.isNotBlank(opShipmntType.get().getShtName()))
						parameters.put("shtName", opShipmntType.get().getShtName());
				}

				Optional<CkRecordDate> opCkRecordDate = Optional.of(dto.getTCkJob()).map(CkJob::getTCkRecordDate);
				if (opCkRecordDate.isPresent()) {
					if (opCkRecordDate.get().getRcdDtSubmit() != null)
						parameters.put("rcdDtSubmit", sdfDate.format(opCkRecordDate.get().getRcdDtSubmit()));
				}

				Optional<CkMstJobState> opCkJobMstState = Optional.of(dto.getTCkJob()).map(CkJob::getTCkMstJobState);
				if (opCkJobMstState.isPresent()) {
					if (StringUtils.isNotBlank(opCkJobMstState.get().getJbstId()))
						// parameters.put("jobState", opCkJobMstState.get().getJbstId());
						parameters.put("jobState",
								Arrays.asList(dto.getTCkJob().getTCkMstJobState().getJbstId().split(",")));

				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyTo = Optional.of(dto.getTCoreAccnByJobPartyTo());
			if (opCoreAccnByJobPartyTo.isPresent()) {
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnId())) {
					parameters.put("toAccnId", opCoreAccnByJobPartyTo.get().getAccnId());
				}
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnName())) {
					parameters.put("toAccnName", "%" + opCoreAccnByJobPartyTo.get().getAccnName() + "%");
				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyCoFf = Optional.of(dto.getTCoreAccnByJobPartyCoFf());
			if (opCoreAccnByJobPartyCoFf.isPresent()) {
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyCoFf.get().getAccnId())) {
					parameters.put("coFfAccnId", opCoreAccnByJobPartyCoFf.get().getAccnId());
				}
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyCoFf.get().getAccnName())) {
					parameters.put("coFfAccnName", "%" + opCoreAccnByJobPartyCoFf.get().getAccnName() + "%");
				}
			}

			if (StringUtils.isNotBlank(dto.getJobShipmentRef())) {
				parameters.put("jobShipmentRef", "%" + dto.getJobShipmentRef() + "%");
			}
			if (StringUtils.isNotBlank(dto.getJobCustomerRef())) {
				parameters.put("jobCustomerRef", "%" + dto.getJobCustomerRef() + "%");
			}

			Optional<Date> opJobDtBooking = Optional.ofNullable(dto.getJobDtBooking());
			if (opJobDtBooking.isPresent() && null != opJobDtBooking.get())
				parameters.put("jobDtBooking", sdfDate.format(opJobDtBooking.get()));

			Optional<Date> opJobDtPlan = Optional.ofNullable(dto.getJobDtPlan());
			if (opJobDtPlan.isPresent() && null != opJobDtPlan.get())
				parameters.put("jobDtPlan", sdfDate.format(opJobDtPlan.get()));

			Optional<Date> opJobDtDelivery = Optional.ofNullable(dto.getJobDtDelivery());
			if (opJobDtDelivery.isPresent() && null != opJobDtDelivery.get())
				parameters.put("jobDtDelivery", sdfDate.format(opJobDtDelivery.get()));

			Optional<Date> opJobDtCreate = Optional.ofNullable(dto.getJobDtCreate());
			if (opJobDtCreate.isPresent() && null != opJobDtCreate.get())
				parameters.put("jobDtCreate", sdfDate.format(opJobDtCreate.get()));

			Optional<Date> opJobDtLupd = Optional.ofNullable(dto.getJobDtLupd());
			if (opJobDtLupd.isPresent() && null != opJobDtLupd.get())
				parameters.put("jobDtLupd", sdfDate.format(opJobDtLupd.get()));

			Optional<Date> opJobInPaymentDtDue = Optional.ofNullable(dto.getJobInPaymentDtDue());
			if (opJobInPaymentDtDue.isPresent() && null != opJobInPaymentDtDue.get())
				parameters.put("jobInPaymentDtDue", sdfDate.format(opJobInPaymentDtDue.get()));

			Optional<Date> opJobOutPaymentDtDue = Optional.ofNullable(dto.getJobOutPaymentDtDue());
			if (opJobOutPaymentDtDue.isPresent() && null != opJobOutPaymentDtDue.get())
				parameters.put("jobOutPaymentDtDue", sdfDate.format(opJobOutPaymentDtDue.get()));

			if (StringUtils.isNotBlank(dto.getJobOutPaymentState()))
				parameters.put("jobOutPaymentState", Arrays.asList(dto.getJobOutPaymentState().split(",")));

			if (Objects.nonNull(dto.getPickUp()) && StringUtils.isNotBlank(dto.getPickUp().getTlocLocAddress())) {
				parameters.put("trFromLoc", '%' + dto.getPickUp().getTlocLocAddress().trim() + '%');
			}

			if (Objects.nonNull(dto.getLastDrop()) && StringUtils.isNotBlank(dto.getLastDrop().getTlocLocAddress())) {
				parameters.put("trToLoc", '%' + dto.getLastDrop().getTlocLocAddress().trim() + '%');
			}
			if (Objects.nonNull(dto.getTCkCtDrv()) && StringUtils.isNotBlank(dto.getTCkCtDrv().getDrvName())) {
				parameters.put("drvName", '%' + dto.getTCkCtDrv().getDrvName().trim() + '%');
			}
			if (Objects.nonNull(dto.getTCkCtVeh()) && StringUtils.isNotBlank(dto.getTCkCtVeh().getVhPlateNo())) {
				parameters.put("vhPlateNo", '%' + dto.getTCkCtVeh().getVhPlateNo().trim() + '%');
			}

			return parameters;
		} catch (ParameterException ex) {
			LOG.error("getParameters", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getParameters", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#whereDto(com.vcc.camelone.common.controller.entity.EntityFilterRequest)
	 *
	 */
	@Override
	protected CkJobTruck whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

			CkJobTruck dto = new CkJobTruck();
			CkJob ckJob = new CkJob();
			CkRecordDate recordDate = new CkRecordDate();
			CkMstJobState ckMstState = new CkMstJobState();
			CkMstShipmentType ckMstShpType = new CkMstShipmentType();
			CkCtTripLocation ckCtTripLocation = new CkCtTripLocation();

			CoreAccn tCoreAccnByJobPartyTo = new CoreAccn();
			CoreAccn tCoreAccnByJobPartyCoFf = new CoreAccn();
			CkCtDrv driverObj = new CkCtDrv();
			CkCtVeh vehObj = new CkCtVeh();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("jobId"))
					dto.setJobId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkMstShipmentType.shtId"))
					ckMstShpType.setShtId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkMstShipmentType.shtName"))
					ckMstShpType.setShtName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkRecordDate.rcdDtSubmit"))
					recordDate.setRcdDtSubmit(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJob.TCkMstJobState.jbstId"))
					ckMstState.setJbstId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccnByJobPartyTo.accnId"))
					tCoreAccnByJobPartyTo.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccnByJobPartyTo.accnName"))
					tCoreAccnByJobPartyTo.setAccnName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccnByJobPartyCoFf.accnId"))
					tCoreAccnByJobPartyCoFf.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccnByJobPartyCoFf.accnName"))
					tCoreAccnByJobPartyCoFf.setAccnName(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("jobShipmentRef"))
					dto.setJobShipmentRef(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("jobCustomerRef"))
					dto.setJobCustomerRef(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("jobDtBooking"))
					dto.setJobDtBooking(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("jobDtPlan"))
					dto.setJobDtPlan(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("jobDtDelivery"))
					dto.setJobDtDelivery(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("jobDtCreate"))
					dto.setJobDtCreate(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("jobDtLupd"))
					dto.setJobDtLupd(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("tckCtDrv.drvName")){
					driverObj.setDrvName(opValue.get());
				}
				if (entityWhere.getAttribute().equalsIgnoreCase("tckCtVeh.vhPlateNo")){
					vehObj.setVhPlateNo(opValue.get());
				}
				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}

				// for jobs to bill or billed jobs to approve or reject
				if (entityWhere.getAttribute().equalsIgnoreCase("billedJob"))
					dto.setBilledJob(Boolean.valueOf(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("jobForBilling"))
					dto.setJobForBilling(Boolean.valueOf(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("jobInPaymentDtDue"))
					dto.setJobInPaymentDtDue(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("jobOutPaymentDtDue"))
					dto.setJobOutPaymentDtDue(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("jobOutPaymentState"))
					dto.setJobOutPaymentState(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("pickUp")){
					ckCtTripLocation.setTlocLocAddress(opValue.get());
					dto.setPickUp(ckCtTripLocation);
				}

				if (entityWhere.getAttribute().equalsIgnoreCase("lastDrop.tckCtLocation.locName")){
					ckCtTripLocation.setTlocLocAddress(opValue.get());
					dto.setLastDrop(ckCtTripLocation);
				}

			}

			ckJob.setTCkMstShipmentType(ckMstShpType);
			ckJob.setTCkRecordDate(recordDate);
			ckJob.setTCkMstJobState(ckMstState);
			dto.setTCkJob(ckJob);
			dto.setTCkCtDrv(driverObj);
			dto.setTCkCtVeh(vehObj);
			dto.setTCoreAccnByJobPartyTo(tCoreAccnByJobPartyTo);
			dto.setTCoreAccnByJobPartyCoFf(tCoreAccnByJobPartyCoFf);
			return dto;
		} catch (ParameterException ex) {
			LOG.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#getCoreMstLocale(java.lang.Object)
	 *
	 */
	@Override
	protected CoreMstLocale getCoreMstLocale(CkJobTruck dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#setCoreMstLocale
	 *      (com.vcc.camelone.locale.dto.CoreMstLocale, java.lang.Object)
	 *
	 */
	@Override
	protected CkJobTruck setCoreMstLocale(CoreMstLocale coreMstLocale, CkJobTruck dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_initBzValidator()
	 * 
	 */
	@Override
	protected void initBusinessValidator() {
		super.bzValidator = new TruckJobValidator();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_validateGroupClass(com.guudint.clickargo.job.service.IJobEvent.JobEvent)
	 * 
	 */
	@Override
	protected Class<?>[] _validateGroupClass(JobEvent jobEvent) {
		LOG.debug("_validateGroupClass");

		switch (jobEvent) {
		case CREATE:
			return new Class<?>[] { CreateValid.class };
		case CONFIRM:
			return new Class<?>[] { UpdateValid.class };
		case SUBMIT:
			return new Class<?>[] { SubmitValid.class };
		case CANCEL:
			return new Class<?>[] { UpdateValid.class };
		case COMPLETE:
			return new Class<?>[] { UpdateValid.class };
		case DELETE:
			return new Class<?>[] { DeleteValid.class };
		case GET:
			return new Class<?>[] { GetValid.class };
		case PAID:
			return new Class<?>[] { UpdateValid.class };
		case PAY:
			return new Class<?>[] { PayValid.class };
		case REJECT:
			return new Class<?>[] { UpdateValid.class };
		case UPDATE:
			return new Class<?>[] { UpdateValid.class };
		default:
			break;
		}
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_auditEvent(com.guudint.clickargo.job.service.IJobEvent.JobEvent,
	 *      com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected void _auditEvent(JobEvent jobEvent, CkJobTruck dto, Principal principal) {
		LOG.debug("_auditEvent");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			if (null == principal)
				throw new ParameterException("param principal null");

			Date now = Calendar.getInstance().getTime();
			TCoreAuditlog coreAuditLog = new TCoreAuditlog();
			coreAuditLog.setAudtId(String.valueOf(System.currentTimeMillis()));
			coreAuditLog.setAudtEvent(jobEvent.getDesc());
			coreAuditLog.setAudtTimestamp(now);
			Optional<String> opAccnId = Optional.ofNullable(principal.getCoreAccn().getAccnId());
			coreAuditLog.setAudtAccnid(opAccnId.isPresent() ? opAccnId.get() : DASH);
			coreAuditLog.setAudtUid(StringUtils.isEmpty(principal.getUserId()) ? DASH : principal.getUserId());
			coreAuditLog.setAudtUname(StringUtils.isEmpty(principal.getUserName()) ? DASH : principal.getUserName());
			coreAuditLog.setAudtRemoteIp(DASH);
			coreAuditLog.setAudtReckey(StringUtils.isEmpty(dto.getJobId()) ? DASH : dto.getJobId());
			coreAuditLog.setAudtParam1(DASH);
			coreAuditLog.setAudtParam2(DASH);
			coreAuditLog.setAudtParam3(DASH);
			coreAuditLog.setAudtRemarks(dto.getAuditRemark() == null ? DASH : dto.getAuditRemark());
			auditLogDao.saveOrUpdate(coreAuditLog);

		} catch (Exception ex) {
			LOG.error("_auditEvent", ex);
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_auditError(com.guudint.clickargo.job.service.IJobEvent.JobEvent,
	 *      com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected void _auditError(JobEvent jobEvent, CkJobTruck dto, Exception ex, Principal principal) {
		LOG.debug("_auditError");
		COException.create(COException.ERROR, CkErrorCodes.ERR_JOB_EXCEPTION, CkErrorCodes.MSG_JOB_EXCEPTION,
				jobEvent.getDesc(), ex);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_getJobEvent(com.guudint.clickargo.job.service.IJobEvent.JobEvent,
	 *      com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	protected AbstractJobEvent<CkJobTruck> _getJobEvent(JobEvent jobEvent, CkJobTruck dto, Principal principal) {
		LOG.debug("_getJobEvent");

		switch (jobEvent) {
		case CREATE:
			return new TruckJobCreateEvent(this, jobEvent, dto);
		case CANCEL:
			break;
		case COMPLETE:
			break;
		case CONFIRM:
			break;
		case DELETE:
			return new TruckJobDeleteEvent(this, jobEvent, dto);
		case GET:
			break;
		case PAID:
			break;
		case PAY:
			break;
		case REJECT:
			break;
		case SUBMIT:
			return new TruckJobSubmitEvent(this, jobEvent, dto);
		case UPDATE:
			break;
		default:
			break;
		}
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_newJob(com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	protected CkJobTruck _newJob(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {

		CkJobTruck ckJobTruck = new CkJobTruck();
		CkJob ckJob = new CkJob();
		ckJob.setTCkMstJobState(new CkMstJobState());
		ckJob.setTCkMstShipmentType(new CkMstShipmentType());
		ckJob.setTCkRecordDate(new CkRecordDate());
		ckJobTruck.setTCkJob(ckJob);

		// Initialize Job Party CoFf - Account
		CoreAccn tCoreAccnByJobPartyCoFf = principal.getCoreAccn();
		ckJobTruck.setTCoreAccnByJobPartyCoFf(tCoreAccnByJobPartyCoFf);

		try {
			TCoreUsr coreUsr = coreUserDao.find(principal.getUserId());
			if (null != coreUsr) {
				CkCtContactDetail contactCoFf = new CkCtContactDetail();
				contactCoFf.setCdName(coreUsr.getUsrName());
				contactCoFf.setCdEmail(coreUsr.getUsrContact().getContactEmail());
				contactCoFf.setCdPhone(coreUsr.getUsrContact().getContactTel());
				ckJobTruck.setTCkCtContactDetailByJobContactCoFf(contactCoFf);
			}
		} catch (Exception ex) {
			LOG.error("_newJob", ex);
		}

		// initialize one element first
		ckJobTruck.setTckCtTripList(Arrays.asList(new CkCtTrip()));

		// SG REQUIREMENT: Set the hidden fields if there is
		try {
			ckJobTruck.setHiddenFields(cltAccnConfigExService.getFieldsToHide(principalUtilService.getPrincipal()));
			CKCountryConfig ctryCfg = cltAccnConfigExService.getCtryEnv();
			ckJobTruck.setShowAdditionalFields(ctryCfg.getCountry().equalsIgnoreCase("SG"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ckJobTruck;
	}

	/**
	 * 
	 * @return
	 * @throws ProcessingException
	 */
	@SuppressWarnings("unused")
	private String getSLAccnId() throws ProcessingException {
		String sLAccnId;
		try {
			sLAccnId = getSysParam(ICkConstant.CK_DEF_SL_ACCN);
		} catch (Exception e) {
			throw new ProcessingException(e);
		}
		if (StringUtils.isBlank(sLAccnId))
			throw new ProcessingException("sLAccnId is not configured");

		return sLAccnId;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_createJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck _createJob(CkJobTruck dto, CkJob parentJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {
		LOG.debug("_createJob");

		if (null == dto)
			throw new ParameterException("param dto null;");
		if (null == principal)
			throw new ParameterException("param principal null");

		parentJob.getTCkRecordDate().setRcdDtStart(dto.getTCkJob().getTCkRecordDate().getRcdDtStart());
		parentJob.getTCkRecordDate().setRcdDtExpiry(dto.getTCkJob().getTCkRecordDate().getRcdDtExpiry());
		ckRecordService.update(parentJob.getTCkRecordDate(), principal);
		// if the shipment type is IMPORT and DOMESTIC, it's TRKI, otherwise TRKO,
		CkMstJobType jobType = new CkMstJobType();
		Optional<String> opShipmentType = Optional.ofNullable(dto.getShipmentType());
		if (opShipmentType.isPresent()) {
			CkMstShipmentType shipmentType = new CkMstShipmentType();
			shipmentType.setShtId(opShipmentType.get());
			parentJob.setTCkMstShipmentType(shipmentType);

			if (opShipmentType.get().equalsIgnoreCase(ShipmentTypes.IMPORT.getId())
					|| opShipmentType.get().equalsIgnoreCase(ShipmentTypes.DOMESTIC.getId())) {
				jobType.setJbtId(JobTypes.TRKI.name());
			} else {
				jobType.setJbtId(JobTypes.TRKO.name());
			}

		}
		parentJob.setTCkMstJobType(jobType);

		parentJob.setTCoreAccnByJobOwnerAccn(principal.getCoreAccn());

		// Get the co/ff and the to
		Optional<CoreAccn> opCoffAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyCoFf());
		// Check if the principal, the one who owns this job is co or ff and save in the
		// corresponding columns
		Optional<MstAccnType> opAccnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType());
		if (opAccnType.isPresent()) {
			if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
				parentJob.setTCoreAccnByJobCoAccn(opCoffAccn.get());
			} else if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {
				parentJob.setTCoreAccnByJobFfAccn(opCoffAccn.get());
			}
		}

		Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
		if (opToAccn.isPresent()) {
			parentJob.setTCoreAccnByJobToAccn(opToAccn.get());
			// Call checkAndSetMobileEnable here to avoid npe on opToAccn
			ckTruckMiscMobileService.checkAndSetMobileEnable(dto);
		}
		
		if( dto.getTCkJob().getTCoreAccnByJobSlAccn() != null) {
			parentJob.setTCoreAccnByJobSlAccn(dto.getTCkJob().getTCoreAccnByJobSlAccn());
		}
		
		ckJobService.update(parentJob, principal);

		dto.setTCkJob(parentJob);
		dto.setJobId(CkUtil.generateId(IClicTruckConstant.PREFIX_CK_TRUCK_JOB));
		dto.setJobStatus(RecordStatus.ACTIVE.getCode());
		dto.setJobDtBooking(null);
		dto.setJobDtDelivery(null);

		CkCtContactDetail coFfContactDetail = ckJobTruckUtilService.createContactDetails(principal);
		dto.setTCkCtContactDetailByJobContactCoFf(coFfContactDetail);

		CKCountryConfig ctryCfg = cltAccnConfigExService.getCtryEnv();
		// only checks if it's non-SG, as SG don't have finance
		clicTruckMiscService.checkAndSetFinanceOptions(dto, !ctryCfg.getCountry().equalsIgnoreCase("SG"));

//		// SG2 for department
		ckJobTruckUtilService.setDepartment(dto, AccountTypes.valueOf(opAccnType.get().getAtypId()), principal);

		this.add(dto, principal);
		ckJobTruckUtilService.addOrUpdateTrips(dto, principal);

		// SG REQUIREMENT: Save to T_CK_JOB_TRUCK_ADD_ATTR if dto.addtlFields is not
		// empty
		jobTruckAddAttrService.saveAdditionalFields(dto, principal);

		return findById(dto.getJobId());

	}

	/**
	 * Override update from {@code AbstractEntityService} and update the children
	 * individually.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CkJobTruck update(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		try {

			LOG.info(String.format("jobId: %s, userId: %s, action: %s", dto.getJobId(), principal.getUserId(),
					dto.getAction()));

			this.isPermissionOperateJob(dto, principal);

			if (null != dto.getAction()) {
				switch (dto.getAction()) {
				case DELETE:
					// CT-51 - [CO Operations-Import Job] System goes in processing after Click of
					// Delete button
					return jobStateService.deleteJob(dto, principal);
				case SUBMIT:
					return submitJob(dto, principal);
				case CANCEL:
					return cancelJob(dto, principal);
				case CONFIRM:
					return confirmJob(dto, principal);
				case PAY:
					return payJob(dto, principal);
				case PAID:
					return paidJob(dto, principal);
				case COMPLETE:
					return completJob(dto, principal);
				case CLONE:
					return jobStateService.cloneJob(dto, principal);
				case WITHDRAW:
					return jobStateService.withdrawJob(dto, principal);
				case REJECT:
					return jobStateService.rejectJob(dto, principal);
				case ACCEPT:
					return jobStateService.acceptJob(dto, principal);
				case ASSIGN:
					// Change in Driver Assignment for mobile
					ckTruckMiscMobileService.checkAndSetMobileEnable(dto);
					// call update to update driver/truck before moving to new state
					super.update(dto, principal);
					return jobStateService.assignJob(dto, principal);
				case START:
					return jobStateService.startJob(dto, principal);
				case STOP:
					jobStateService.stopJob(dto, principal);
					if(dto.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.DLV.name())) {
						LOG.error("Immediately Epo Executed : "+ dto.getTCkJob().getJobId());
						sendEpodService.sendePodEmail();
					}
					return super.update(dto, principal);
				case BILLJOB:
					return jobStateService.billJob(dto, principal);
				case VERIFY_BILL:
					return jobStateService.verifyJobPayment(dto, principal);
				case ACKNOWLEDGE_BILL:
					return jobStateService.acknowledgeJobPayment(dto, principal);
				case APPROVE_BILL:
					return jobStateService.approveJobPayment(dto, principal);
				case REJECT_BILL:
					return jobStateService.rejectJobPayment(dto, principal);
				default: {
					// Update children2
					ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
					ckJobService.update(dto.getTCkJob(), principal);

					// SG REQUIREMENT: Save to T_CK_JOB_TRUCK_ADD_ATTR if dto.addtlFields is not
					// empty
					jobTruckAddAttrService.saveAdditionalFields(dto, principal);
					this._auditEvent(JobEvent.UPDATE,dto, principal);
					return super.update(dto, principal, false);
				}
				}
			} else if (isEnableForToSaveJob(dto, principal)) {
				if(dto.isModifyAcceptTrip()) {
					ckJobTruckUtilService.addOrUpdateTrips(dto, principal);
				}else {
					ckJobTruckUtilService.updateTripDateTimeRemark(dto, principal);
				}
				dto.setAuditRemark("MODIFIED TRIPS");
				this._auditEvent(JobEvent.UPDATE,dto, principal);
				return super.update(dto, principal, false); // update driver and vehicle.
			} else if (isEnableForToSaveJobWhenOnGoingAndAssigned(dto, principal)){
				ckJobTruckUtilService.addOrUpdateTripsWhenOnGoingAndAssigned(dto, principal);
				dto.setAuditRemark("MODIFIED TRIPS");
				this._auditEvent(JobEvent.UPDATE,dto, principal);
				return super.update(dto, principal, false);
			}else {
				// Only update trips if NEW State
				if (dto.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.NEW.name()))
					ckJobTruckUtilService.addOrUpdateTrips(dto, principal);

				// Update TO Invoice if have
				if (dto.getToInvoiceList() != null) {
					// Date now = new Date();
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

				Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
				if (opToAccn.isPresent()) {
					ckTruckMiscMobileService.checkAndSetMobileEnable(dto);
				}

				CKCountryConfig ctryCfg = cltAccnConfigExService.getCtryEnv();
				// only checks if it's non-SG, as SG don't have finance
				clicTruckMiscService.checkAndSetFinanceOptions(dto, !ctryCfg.getCountry().equalsIgnoreCase("SG"));

				// Update children
				ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
				ckJobService.update(dto.getTCkJob(), principal);

				// SG REQUIREMENT: Save to T_CK_JOB_TRUCK_ADD_ATTR if dto.addtlFields is not
				// empty
				jobTruckAddAttrService.saveAdditionalFields(dto, principal);
				dto.setAuditRemark("UPDATE JOB");
				this._auditEvent(JobEvent.UPDATE,dto, principal);
				return super.update(dto, principal, false);
			}

		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

	}

	/**
	 * Is permission to Update, Delete and Route.
	 * 
	 * @param dto
	 * @param principal
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void isPermissionOperateJob(CkJobTruck dto, Principal principal) throws Exception {

		String loginAccnId = principal.getUserAccnId();

		// 1: if GLI, return true;
		String gliAccnId = sysParam.getValString("CLICKTRUCK-GLI-ACCNID", "GLI");
		if (gliAccnId.equalsIgnoreCase(loginAccnId)) {
			return;
		}

		// 2: should be CO or TO
		TCkJobTruck job = ckJobTruckDao.find(dto.getJobId());
		this.isPermissionOperateJob(job, principal);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_submitJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck _submitJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("_submitJob");

		TCkJobTruck jobToSubmit = dao.find(dto.getJobId());
		if (null != jobToSubmit
				&& jobToSubmit.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.SUB.name()))
			throw new ProcessingException("Already submitted by a different user");

		// In case there is draft records and COFF wants to submit but the account is
		// suspended. Check if account suspended before proceed
		this.checkSuspendedAccount(principal);

		checkValidContract(principal, dto);

		this.checkJobState(dto.getTCkJob().getJobId(), JobStates.SUB);

		// Refresh trips before submitting
		ckJobTruckUtilService.addOrUpdateTrips(dto, principal);

		// Recalculate the total trip charge and reserve credit before proceed to change
		// the state to submit
		// This will throw exception in case of issue during credit reservation, so that
		// it won't proceed to update job truck state.
		List<CkCtTrip> listTrips = ckCtTripService.findTripsByTruckJobAndStatus(dto.getJobId(),
				Arrays.asList(RecordStatus.ACTIVE.getCode()));
		double totalTripCharge = 0.0;
		if (listTrips != null && listTrips.size() > 0) {
			for (CkCtTrip trip : listTrips) {
				CkCtTripCharge tripCharge = trip.getTCkCtTripCharge();
				if (tripCharge != null) {
					totalTripCharge += tripCharge.getTcPrice() == null ? 0 : tripCharge.getTcPrice().doubleValue();
				}
			}
		}

		dto.setJobNoTrips((listTrips == null) ? 0 : (short) listTrips.size());

		CKCountryConfig ctryCfg = cltAccnConfigExService.getCtryEnv();
		// only checks if it's non-SG, as SG don't have finance
		clicTruckMiscService.checkAndSetFinanceOptions(dto, !ctryCfg.getCountry().equalsIgnoreCase("SG"));

		// 20240325 Check truck job finance option is OC or OT
		if (dto.getJobFinanceOpt() != null && Arrays.asList(FinancingTypes.OC.name(), FinancingTypes.OT.name()).contains(dto.getJobFinanceOpt())) {
			// JournalTxnType journalTxnType, CkJobTruck jobTruck, BigDecimal amount,
			// FinancingTypes financingTypes
			opmService.reserveOpmJobTruckCredit(JournalTxnType.JOB_SUBMIT, dto, new BigDecimal(totalTripCharge),
					principal);
		} else if (dto.getJobFinanceOpt() != null && dto.getJobFinanceOpt().equalsIgnoreCase(FinancingTypes.BC.name())) {
			// It's the co/ff who submits a trucking job
			truckJobCreditService.reserveJobTruckCredit(JournalTxnType.JOB_SUBMIT, dto, new BigDecimal(totalTripCharge),
					principal);
		}
		
		if(ctryCfg.getCountry().equalsIgnoreCase("SG")) {
			dto.setJobMobileEnabled('Y');
		}

		Date now = Calendar.getInstance().getTime();
		dto.setJobDtBooking(now);

		CkJob ckJob = workflowService.moveState(FormActions.SUBMIT, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtSubmit(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidSubmit(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);

		// Update TO
		Optional<CoreAccn> opToAccn = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
		if (opToAccn.isPresent()) {
			// Call checkAndSetMobileEnable here to avoid npe on opToAccn
			ckTruckMiscMobileService.checkAndSetMobileEnable(dto);
			dto.getTCkJob().setTCoreAccnByJobToAccn(opToAccn.get());
		}

		ckJobService.update(dto.getTCkJob(), principal);
		workflowService.audit(dto.getJobId(), principal, FormActions.SUBMIT);

		// SG REQUIREMENT: set flag to show additional fields
		dto.setShowAdditionalFields(ctryCfg.getCountry().equalsIgnoreCase("SG"));

		// Get the additional fields
		dto.setAddtlFields(jobTruckAddAttrService.getAdditionalFields(dto));

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.SUBMIT, dto, principal));
		return super.update(dto, principal, false);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_rejectJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck _rejectJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("_rejectJob");

		TCkJobTruck jobToReject = dao.find(dto.getJobId());
		if (null != jobToReject
				&& jobToReject.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.REJ.name()))
			throw new ProcessingException("Already rejected by a different user");
		if (null != jobToReject
				&& jobToReject.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.NEW.name()))
			throw new ProcessingException("Already withdrawn by a different user");

		// This will throw exception in case of issue during credit reversal, so that
		// it won't proceed to update job truck state.
		// Credit is provided to the CO/FF
		truckJobCreditService.reverseJobTruckCredit(JournalTxnType.JOB_REJECT, dto, principal);

		CkJob ckJob = workflowService.moveState(FormActions.REJECT, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtReject(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidReject(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);

		eventPublisher.publishEvent(new TruckJobStateChangeEvent(this, JobActions.REJECT, dto, principal));
		return super.update(dto, principal);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_cancelJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck _cancelJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {

		// dto.setCancelled(false);
		// if (null != dto.getTCkJob().getTCkMstJobState()) {
		// if
		// (dto.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.CAN.name()))
		// {
		// dto.setCancelled(true);
		// return dto;
		// }
		// }
		if (null != dto.getTCkJob().getTCkMstJobState()) {
			if (dto.getTCkJob().getTCkMstJobState().getJbstId().equalsIgnoreCase(JobStates.CAN.name())) {
				throw new ProcessingException("Already cancelled by a different user");
			}
		}

		CkJob ckJob = workflowService.moveState(FormActions.CANCEL, dto.getTCkJob(), principal, ServiceTypes.CLICTRUCK);
		dto.setTCkJob(ckJob);
		dto.getTCkJob().getTCkRecordDate().setRcdDtCancel(new Date());
		dto.getTCkJob().getTCkRecordDate().setRcdUidCancel(principal.getUserId());
		ckRecordService.update(dto.getTCkJob().getTCkRecordDate(), principal);
		ckJobService.update(dto.getTCkJob(), principal);
		return super.update(dto, principal);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_confirmJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck _confirmJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_payJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck _payJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_paidJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck _paidJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_completeJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkJobTruck _completeJob(CkJobTruck dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.common.AbstractClickCargoEntityService#getLogger()
	 *
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#formatOrderBy(java.lang.String)
	 *
	 */
	@Override
	protected String formatOrderBy(String attribute) throws Exception {

		String newAttr = attribute;
		if (StringUtils.contains(newAttr, "tcoreAccn"))
			newAttr = newAttr.replace("tcoreAccn", "TCoreAccn");

		if (StringUtils.contains(newAttr, "tcoreAccnByJobPartyTo"))
			newAttr = newAttr.replace("tcoreAccnByJobPartyTo", "TCoreAccnByJobPartyTo");

		if (StringUtils.contains(newAttr, "tcoreAccnByJobPartyCoFf"))
			newAttr = newAttr.replace("tcoreAccnByJobPartyCoFf", "TCoreAccnByJobPartyCoFf");

		if (StringUtils.contains(newAttr, "tckJob"))
			newAttr = newAttr.replace("tckJob", "TCkJob");

		if (StringUtils.contains(newAttr, "tckMstShipmentType"))
			newAttr = newAttr.replace("tckMstShipmentType", "TCkMstShipmentType");

		if (StringUtils.contains(newAttr, "tckRecordDate"))
			newAttr = newAttr.replace("tckRecordDate", "TCkRecordDate");

		if (StringUtils.contains(newAttr, "tckMstJobState"))
			newAttr = newAttr.replace("tckMstJobState", "TCkMstJobState");

		if (StringUtils.contains(newAttr, "tckMstJobType"))
			newAttr = newAttr.replace("tckMstJobType", "TCkMstJobType");

		return newAttr;
	}

	/**
	 * Is permission to find job.
	 * 
	 * @param dto
	 * @param principal
	 * @return
	 */
	private void isPermissionOperateJob(TCkJobTruck job, Principal principal) throws Exception {

		String loginAccnId = principal.getUserAccnId();

		// 1: if GLI, return true;
		String gliAccnId = sysParam.getValString("CLICKTRUCK-GLI-ACCNID", "GLI");
		if (gliAccnId.equalsIgnoreCase(loginAccnId)) {
			return;
		}

		// 2: should be CO or TO
		String coAccnId = job.getTCoreAccnByJobPartyCoFf() != null ? job.getTCoreAccnByJobPartyCoFf().getAccnId() : "";
		String toAccnId = job.getTCoreAccnByJobPartyTo() != null ? job.getTCoreAccnByJobPartyTo().getAccnId() : "";
		String ffCoAccnId = job.getTCkJob().getTCoreAccnByJobSlAccn() != null
				? job.getTCkJob().getTCoreAccnByJobSlAccn().getAccnId()
				: "";

		boolean isPermission = loginAccnId.equalsIgnoreCase(coAccnId) || loginAccnId.equalsIgnoreCase(toAccnId)
				|| loginAccnId.equalsIgnoreCase(ffCoAccnId);

		if (!isPermission) {
			throw new Exception(String.format("%S no permission to find %S", loginAccnId, job.getJobId()));
		}
	}

	public Map<String, Object> stringToObject(String data) {

		Map<String, Object> map = new HashMap<>();

		String[] pairs = data.substring(1, data.length() - 1).split(", ");

		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			String key = keyValue[0];
			Object value = keyValue[1];
			map.put(key, value);
		}

		return map;
	}

	private CkJobTruck clearAmountTo0(CkJobTruck jobTruck) {

		jobTruck.setJobTotalCharge(BigDecimal.ZERO);
		jobTruck.setJobTotalPlatformFees(BigDecimal.ZERO);
		jobTruck.setJobTotalReimbursements(BigDecimal.ZERO);

		// Trip
		Optional<CkJobTruck> jobTruckOpt = Optional.of(jobTruck);
		jobTruckOpt.map(jt -> jt.getTckCtTripList()).orElse(Collections.emptyList()).forEach(trip -> {

			trip.setTotalReimbursementCharge(BigDecimal.ZERO);
			trip.setTotalTripOpenPrice(BigDecimal.ZERO);
			trip.setTotalTripPrice(BigDecimal.ZERO);

			CkCtTripCharge tc = trip.getTCkCtTripCharge();
			if (tc != null) {
				tc.setTcPrice(BigDecimal.ZERO);
				tc.setTcPlatformFee(BigDecimal.ZERO);
				tc.setTcGovtTax(BigDecimal.ZERO);
				tc.setTcWitholdTax(BigDecimal.ZERO);
			}
		});

		return jobTruck;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public MultiRecordResponse multiRecord(MultiRecordRequest request) {
		Principal principal = ckSession.getPrincipal();
		CoreAccn coreAccn = Optional.ofNullable(principal.getCoreAccn()).orElse(new CoreAccn());
		MultiRecordResponse response = new MultiRecordResponse();
		response.setAccType(request.getAccType());
		response.setAction(request.getAction());
		response.setRole(request.getRole());
		response.getId().addAll(request.getId());
		if ('S' == coreAccn.getAccnStatus()) {
			response.setSuspended(true);
		} else {
			for (String id : request.getId()) {
				FailedDescription failedDescription = new FailedDescription();
				try {
					CkJobTruck ckJobTruck = findById(id);
					ckJobTruck.setAction(request.getAction());
					if (request.getAction() == JobActions.REJECT && StringUtils.isNotBlank(request.getRemarks())) {
						ckJobTruck.setRejectRemarks(request.getRemarks());
					}
					updateObj(ckJobTruck, principal);
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
				} catch (ProcessingException e) {
					failedDescription.setId(id);
					failedDescription.setReason(e.getMessage());
					LOG.error(e.getMessage(), e);
				} catch (ValidationException e) {
					failedDescription.setId(id);
					failedDescription.setReason(e.getMessage());
					LOG.error(e.getMessage(), e);
				}
				if (failedDescription.getId() != null) {
					response.getFailed().add(failedDescription);
				}
			}
			response.setNoSuccess(response.getSuccess().size());
			response.setNoFailed(response.getFailed().size());
		}
		return response;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void multiAssignDrvVeh(MultiAssignDrvVeh request, Principal principal)
			throws ParameterException, ProcessingException, ValidationException, Exception {
		LOG.debug("multiAssignDrvVeh");

		List<TCkJobTruck> listTCkJobTruck = ckJobTruckDao.findByIds(request.getId());

		if (!listTCkJobTruck.isEmpty()) {
			for (TCkJobTruck tCkJobTruck : listTCkJobTruck) {

				//if (!tCkJobTruck.getJobSource().contains("XML"))
				//	throw new ProcessingException("jobs is not DSV / XML");

				checkJobState(tCkJobTruck.getTCkJob().getJobId(), JobStates.ASG);

				CkJobTruck ckJobTruck = dtoFromEntity(tCkJobTruck);
				ckJobTruck.setAction(request.getAction());

				// update the driver and vehicle
				Optional<CkCtDrv> asgDriver = Optional.ofNullable(request.getCkCtDrv())
						.filter(ckCtDrv -> ckCtDrv.getDrvId() != null && !ckCtDrv.getDrvId().isEmpty());
				if (asgDriver.isPresent()) {
					// find the driver and check if it's already assigned or not
					CkCtDrv drvEntity = ckCtDrvService.find(asgDriver.get());
					if (drvEntity != null) {
						//if (StringUtils.isNotBlank(drvEntity.getDrvState())
						//		&& drvEntity.getDrvState().equalsIgnoreCase(DriverStates.ASSIGNED.name())
						//		// Change in Driver Assignment for mobile
						//		&& tCkJobTruck.getJobMobileEnabled() == 'N') {
						//	throw new ProcessingException("Driver already assigned to another job");
						//} else {
							// update the state
							drvEntity.setDrvState(DriverStates.ASSIGNED.name());
							ckCtDrvService.update(drvEntity, principal);
							TCkCtDrv tCkCtDrv = new TCkCtDrv();
							tCkJobTruck.setTCkCtDrv(drvEntity.toEntity(tCkCtDrv));
							ckJobTruck.setTCkCtDrv(drvEntity);
						//}
					}
				}

				Optional<CkCtVeh> asgVeh = Optional.ofNullable(request.getCkCtVeh())
						.filter(ckCtVeh -> ckCtVeh.getVhId() != null && !ckCtVeh.getVhId().isEmpty());
				CkCtVeh vehEntity = null;
				if (asgVeh.isPresent()) {
					// find the veh and check if it's already assigned or not
					vehEntity = ckCtVehService.find(asgVeh.get());
					if (vehEntity != null) {
						//if (vehEntity.getTCkCtMstVehState().getVhstId().equalsIgnoreCase(VehStates.ASSIGNED.name())
						//		|| vehEntity.getTCkCtMstVehState().getVhstId()
						//				.equalsIgnoreCase(VehStates.MAINTENANCE.name())) {
						//	throw new ProcessingException("Vehicle already assigned to another job or in Maintenance");
						//} else {
							// update the vehicle state
							CkCtMstVehState state = new CkCtMstVehState();
							state.setVhstId(VehStates.ASSIGNED.name());
							vehEntity.setTCkCtMstVehState(state);
							ckCtVehService.update(vehEntity, principal);
							TCkCtVeh tCkCtVeh = new TCkCtVeh();
							tCkJobTruck.setTCkCtVeh(vehEntity.toEntity(tCkCtVeh));
							ckJobTruck.setTCkCtVeh(vehEntity);
						//}
					}
				}

				CkJob ckJob = workflowService.moveState(FormActions.ASSIGN, ckJobTruck.getTCkJob(), principal,
						ServiceTypes.CLICTRUCK);
				ckJobTruck.setTCkJob(ckJob);
				ckJobTruck.getTCkJob().getTCkRecordDate().setRcdDtAssigned(new Date());
				ckJobTruck.getTCkJob().getTCkRecordDate().setRcdUidAssigned(principal.getUserId());

				if (vehEntity != null && tCkJobTruck.getTCkCtMstVehType() != null && ICkConstant.VEHICLE_TYPE_UNDEFINE
						.equalsIgnoreCase(tCkJobTruck.getTCkCtMstVehType().getVhtyId())) {

					this.updateVehicleTypeFromVehicle(tCkJobTruck.getJobId(), vehEntity);
				} else if (vehEntity != null && tCkJobTruck.getTCkCtMstVehType() != null) {
					tCkJobTruck.setTCkCtMstVehType(new TCkCtMstVehType(vehEntity.getTCkCtMstVehType().getVhtyId(),
							vehEntity.getTCkCtMstVehType().getVhtyName()));
				}

				List<ValidationError> validationErrors = bzValidator.validateUpdate(ckJobTruck, principal);
				if (null != validationErrors && !validationErrors.isEmpty())
					throw new ValidationException(validationErrorMap(validationErrors));

				ckRecordService.update(ckJobTruck.getTCkJob().getTCkRecordDate(), principal);
				ckJobService.update(ckJobTruck.getTCkJob(), principal);
				workflowService.audit(ckJobTruck.getJobId(), principal, FormActions.ASSIGN);
				tCkJobTruck.setJobDtLupd(new Date());
				ckJobTruckDao.saveOrUpdate(tCkJobTruck);

				eventPublisher
						.publishEvent(new TruckJobStateChangeEvent(this, JobActions.ASSIGN, ckJobTruck, principal));
			}
		}
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

	public List<String> getActions(String accnType, String role, String jobId) {
		try {
			boolean hasNonFinancedJob = false;
			List<String> jobIds = Arrays.asList(jobId.split(";"));
			List<TCkJobTruck> tCkJobTrucks = ckJobTruckDao.findByIds(jobIds);
			List<String> state = new ArrayList<>();
			for (TCkJobTruck tCkJobTruck : tCkJobTrucks) {
				Hibernate.initialize(tCkJobTruck.getTCkJob());
				Hibernate.initialize(tCkJobTruck.getTCkJob().getTCkMstJobState());
				state.add(tCkJobTruck.getTCkJob().getTCkMstJobState().getJbstId());
				hasNonFinancedJob = tCkJobTruck.getJobIsFinanced() == 'N' ? true : false;
			}
			List<String> actions = ckSvcActionMaskService.getActions("CKT", accnType, role, "CLICTRUCK", state);
			// Removed the below options for non-financed jobs
			if (hasNonFinancedJob) {
				actions.removeAll(Arrays.asList(JobActions.VERIFY_BILL.name(), JobActions.APPROVE_BILL.name(),
						JobActions.REJECT_BILL.name()));
			}

			return actions;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	protected void checkValidContract(Principal principal, CkJobTruck ckJobTruck) throws Exception {

		if (principal != null) {

			List<TCkCtContract> listContract = ckCtContractDao.findValidContract(
					ckJobTruck.getTCoreAccnByJobPartyCoFf().getAccnId(),
					ckJobTruck.getTCoreAccnByJobPartyTo().getAccnId());

			if (listContract.isEmpty()) {
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("contract-accn", "Contract is not valid.");
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			}
		}
	}

	public HashMap<String, String> getListServices() {
		return listServices;
	}

	public void setListServices(HashMap<String, String> listServices) {
		this.listServices = listServices;
	}

	public Workbook getDownloadData(ResponseEntity<Object> b) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method find a list of CkJobTruck DTO based on drvId
	 * 
	 * @param drvId
	 * @return
	 */
	public List<CkJobTruck> findJobTrucksByDrvId(String drvId, String... status) {
		List<CkJobTruck> ckJobTruckList = new ArrayList<CkJobTruck>();
		try {
			List<TCkJobTruck> tckJobTruckList = ckJobTruckDao.findByDrvIdAndJobStatus(drvId, status);
			for (TCkJobTruck tckJobTruck : tckJobTruckList) {
				ckJobTruckList.add(this.dtoFromEntity(tckJobTruck));
			}
		} catch (Exception ex) {
			LOG.error("findJobTrucksByDrvId ", ex);
		}
		return ckJobTruckList;
	}

	private boolean isEnableForToSaveJob(CkJobTruck job, Principal principal) {
		try {
			String toAccnId = job.getTCoreAccnByJobPartyTo() != null ? job.getTCoreAccnByJobPartyTo().getAccnId() : "";
			String jobStatus = job.getTCkJob().getTCkMstJobState().getJbstId();

			boolean isOfficeOrOperateRole = principal.getRoleList().contains(Roles.OFFICER.name())
					|| principal.getRoleList().contains(Roles.OP_ADMIN.name());

			return principal.getUserAccnId().equalsIgnoreCase(toAccnId)
					&& JobStates.ACP.name().equalsIgnoreCase(jobStatus) && isOfficeOrOperateRole;

		} catch (Exception e) {
			LOG.error("", e);
			return false;
		}
	}

	//CT2SG-150 (https://jira.vcargocloud.com/browse/CT2SG-150)
	private boolean isEnableForToSaveJobWhenOnGoingAndAssigned(CkJobTruck job, Principal principal) {
		try {
			String toAccnId = job.getTCoreAccnByJobPartyTo() != null ? job.getTCoreAccnByJobPartyTo().getAccnId() : "";
			String jobStatus = job.getTCkJob().getTCkMstJobState().getJbstId();

			boolean isOfficeOrOperateRole = principal.getRoleList().contains(Roles.OFFICER.name())
					|| principal.getRoleList().contains(Roles.OP_ADMIN.name());

			return principal.getUserAccnId().equalsIgnoreCase(toAccnId)
					&& (JobStates.ASG.name().equalsIgnoreCase(jobStatus) ||
					JobStates.ONGOING.name().equalsIgnoreCase(jobStatus) ||
					JobStates.PAUSED.name().equalsIgnoreCase(jobStatus)
			) && isOfficeOrOperateRole;
		} catch (Exception e) {
			LOG.error("", e);
			return false;
		}
	}

	public boolean isSingapore() throws Exception {

		try {
			CKCountryConfig countryConfig = accnConfigExService.getCtryEnv();

			return "SG".equalsIgnoreCase(countryConfig.getCountry());

		} catch (Exception e) {
			throw e;
		}
	}


	private List<TCkJobTruckExt> loadJobTruckExt(String jobId) throws Exception {
        return ckJobTruckExtDao.findAllByJobId(jobId);
	}
}
