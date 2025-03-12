package com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstCargoType;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtJobTripDeliveryDao;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruckAddAttr;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobMTripDto;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobMTruckAddAttrDto;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobTruckMobileDto;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkMTripCargoDetails;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTripDelivery;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckAddtlAttrService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.CkCtTripWorkflowServiceImpl.TripStatus;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCharge;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoFmService;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoMmService;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.common.service.AbstractCkListingService;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.dto.CkMstCntType;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstJobType;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.model.TCkMstJobState;
import com.guudint.clickargo.master.model.TCkMstShipmentType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.util.email.SysParam;

/**
 * Service to retrieve assigned jobs to a driver.
 */
public class CkJobTruckPAUListService extends AbstractCkListingService<TCkJobTruck, String, CkJobTruckMobileDto> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkJobTruckService.class);
	private static String AUDIT_TAG = "CK JOB TRUCK";
	private static String TABLE_NAME = "T_CK_JOB_TRUCK";
	private static String HISTORY = "history";
	private static String DEFAULT = "default";

	@Autowired
	GenericDao<TCoreAuditlog, String> auditLogDao;

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
	private CkJobTruckDao ckJobTruckDao;

	@Autowired
	@Qualifier("ckJobDao")
	private GenericDao<TCkJob, String> ckJobDao;

	@Autowired
	private CkCtTripService ckCtTripService;

	@Autowired
	protected SysParam sysParam;

	@Autowired
	protected ICkSession ckSession;

	@Autowired
	private CkCtJobTripDeliveryDao ckCtJobTripDeliveryDao;

	@Autowired
	private CkJobTruckMobileWorkflowServiceImpl ckJobTruckMobileWorkflowServiceImpl;

	@Autowired
	private CkJobTruckAddtlAttrService addAttrService;

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	@Autowired
	private CkCtTripCargoMmService mmTripService;

	@Autowired
	private CkCtTripCargoFmService tripFmService;

	public CkJobTruckPAUListService() {
		super("ckJobTruckDao", AUDIT_TAG, TCkJobTruck.class.getName(), TABLE_NAME);
	}

	@Override
	@Transactional
	public List<CkJobTruckMobileDto> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		List<CkJobTruckMobileDto> dtos = new ArrayList<>();
		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkJobTruckMobileDto dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkJobTruck o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkJobTruck> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkJobTruck tCkJobTruck : entities) {
				CkJobTruckMobileDto ckJobTruckAsgDto = dtoFromEntity(tCkJobTruck);
				dtos.add(ckJobTruckAsgDto);
			}

		} catch (ParameterException | ProcessingException ex) {
			LOG.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("filterBy", ex);
			throw new ProcessingException(ex);
		}
		return dtos;
	}

	@Override
	protected TCkJobTruck initEnity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkJob());
			Optional.ofNullable(entity.getTCkJob().getTCkMstJobState()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobCoAccn()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobFfAccn()).ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobOwnerAccn())
					.ifPresent(e -> Hibernate.initialize(e));
			Optional.ofNullable(entity.getTCkJob().getTCoreAccnByJobToAccn()).ifPresent(e -> Hibernate.initialize(e));
			Hibernate.initialize(entity.getTCkCtContactDetailByJobContactCoFf());
			Hibernate.initialize(entity.getTCkCtDebitNoteByJobInvoiceeDebitNote());
			Hibernate.initialize(entity.getTCkCtDebitNoteByJobInvoicerDebitNote());
			Hibernate.initialize(entity.getTCkCtDrv());
			Hibernate.initialize(entity.getTCkCtMstVehType());
			Hibernate.initialize(entity.getTCkCtToInvoice());
			Hibernate.initialize(entity.getTCkCtVeh());
			Hibernate.initialize(entity.getTCoreAccnByJobPartyTo());
			Hibernate.initialize(entity.getTCoreAccnByJobPartyCoFf());
		}
		return entity;
	}

	@Override
	protected CkJobTruckMobileDto dtoFromEntity(TCkJobTruck entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkJobTruckMobileDto dto = new CkJobTruckMobileDto(entity);

			// no deep copy from BeanUtils
			TCkJob ckJobE = entity.getTCkJob();
			if (null != ckJobE) {
				Optional<TCkMstShipmentType> opShipmentType = Optional.ofNullable(ckJobE.getTCkMstShipmentType());
				if (opShipmentType.isPresent()) {
					dto.setShipmentType(ckJobE.getTCkMstShipmentType().getShtName());
				}

				Optional<TCkMstJobState> opJobState = Optional.ofNullable(ckJobE.getTCkMstJobState());
				if (opJobState.isPresent()) {
					dto.setJobState(ckJobE.getTCkMstJobState().getJbstId());
				}

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
				dto.setTCkJob(ckJob);
			}
			// Load job trips if have
			List<CkCtTrip> tckCtTrips = ckCtTripService.findTripsByTruckJobAndStatus(dto.getJobId(),
					Arrays.asList(TripStatus.M_ACTIVE.getStatusCode(), TripStatus.M_PICKED_UP.getStatusCode(),
							TripStatus.M_DELIVERED.getStatusCode(), TripStatus.DLV.getStatusCode()));
			List<CkJobMTripDto> jobMtrips = new ArrayList<>();
			if (tckCtTrips != null) {
				for (CkCtTrip t : tckCtTrips) {
					CkJobMTripDto mTripDto = new CkJobMTripDto();
					mTripDto.setSeqNo(t.getTrSeq());
					mTripDto.setId(t.getTrId());
					mTripDto.setStatus(t.getTrStatus());

					Optional<CkCtTripLocation> opFromTripLoc = Optional.ofNullable(t.getTCkCtTripLocationByTrFrom());
					if (opFromTripLoc.isPresent()) {
						CkCtTripLocation from = opFromTripLoc.get();
						mTripDto.setFromLocName(from.getTlocLocName());
						mTripDto.setFromLocAddr(from.getTlocLocAddress());
						mTripDto.setEstPickupTime(from.getTlocDtLoc());
						mTripDto.setJobStartTime(from.getTlocDtStart());
						mTripDto.setPickedUpTime(from.getTlocDtEnd());
						mTripDto.setFromLocRemarks(from.getTlocRemarks());

					}

					Optional<CkCtTripLocation> opToTripLoc = Optional.ofNullable(t.getTCkCtTripLocationByTrTo());
					if (opToTripLoc.isPresent()) {
						CkCtTripLocation to = opToTripLoc.get();
						mTripDto.setToLocName(to.getTlocLocName());
						mTripDto.setToLocAddr(to.getTlocLocAddress());
						mTripDto.setEstDropOffTime(to.getTlocDtLoc());
						mTripDto.setDeliverStartTime(to.getTlocDtStart());
						mTripDto.setJobFinishTime(to.getTlocDtEnd());
						mTripDto.setCargoRecipient(to.getTlocCargoRec());
						mTripDto.setToLocRemarks(to.getTlocRemarks());

					}

					List<CkMTripCargoDetails> tripCargos = new ArrayList<>();
					// determine if the job is domestic
					if (ckJobTruckUtilService.isFirstMile(dto.getShipmentType())) {
						List<CkCtTripCargoFm> fmTripCargos = tripFmService.findTripCargoFmsByTripId(t.getTrId());

						fmTripCargos.forEach(el -> {
							CkMTripCargoDetails cargoDetails = new CkMTripCargoDetails();
							// populate relevant fields such as cargo details, special instructions
							Optional<CkMstCntType> opCntType = Optional.ofNullable(el.getTCkMstCntType());
							if (opCntType.isPresent())
								cargoDetails.setCntType(opCntType.get().getCnttName());

							cargoDetails.setCntNo(el.getCgCntNo());
							cargoDetails.setCntSealNo(el.getCgCntSealNo());
							cargoDetails.setCntLoad(el.getCgCntFullLoadStr());

							Optional<CkCtMstCargoType> opCargoType = Optional.ofNullable(el.getTCkCtMstCargoType());
							if (opCargoType.isPresent())
								cargoDetails.setGoodsType(opCargoType.get().getCrtypName());
							cargoDetails.setGoodsDesc(el.getCgCargoDesc());
							cargoDetails.setSpecialInstructions(el.getCgCargoSpecialInstn());
							tripCargos.add(cargoDetails);
						});
					} else {
						List<CkCtTripCargoMm> mmTripCargos = mmTripService.findTripCargoFmmsByTripId(t.getTrId());
						mmTripCargos.forEach(el -> {
							CkMTripCargoDetails cargoDetails = new CkMTripCargoDetails();
							// populate relevant fields such as cargo details, special instructions

							Optional<CkCtMstCargoType> opCargoType = Optional.ofNullable(el.getTCkCtMstCargoType());
							if (opCargoType.isPresent())
								cargoDetails.setGoodsType(opCargoType.get().getCrtypName());
							cargoDetails.setGoodsDesc(el.getCgCargoDesc());
							cargoDetails.setSpecialInstructions(el.getCgCargoSpecialInstn());
							tripCargos.add(cargoDetails);
						});

					}

					mTripDto.setCargos(tripCargos);
					jobMtrips.add(mTripDto);

				}

				dto.setTrip(jobMtrips);
			}

			// initialize driver ref no from job id
			dto.setDriverRefNo(entity.getJobId());

			// Set the driver ref no. from shipmentRef to be displayed in the mobile app
			// First element from comma-separated string will be used and the rest will be
			// displayed in the details
			// below the card header
			if (StringUtils.isNotBlank(entity.getJobShipmentRef())) {
				dto.setDriverRefNo(entity.getJobShipmentRef());
			} else if (StringUtils.isNotBlank(entity.getJobCustomerRef())) {
				// Split and get the first
				List<String> values = Arrays.asList(entity.getJobCustomerRef().split(","));
				if (values != null && values.size() > 0) {
					dto.setDriverRefNo(values.get(0));
				}
			}

			// SG Requirement for additional fields
			List<CkJobTruckAddAttr> addFieldsList = addAttrService.getAdditionalFields(dto);
			if (addFieldsList != null && addFieldsList.size() > 0) {
				List<CkJobMTruckAddAttrDto> list = new ArrayList<>();
				for (CkJobTruckAddAttr aF : addFieldsList) {
					String label = Optional.of(aF.getTCkCtConAddAttr().getCaaLabel()).orElse("");
					String value = Optional.of(aF.getJaaValue()).orElse("");
					if (StringUtils.isNotBlank(label) && StringUtils.isNotBlank(value)) {
						CkJobMTruckAddAttrDto d = new CkJobMTruckAddAttrDto(label, value);
						list.add(d);
					}
				}

				dto.setAddAttrDto(list);
			}

			return dto;
		} catch (ParameterException ex) {
			LOG.error("dtoFromEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected String getWhereClause(CkJobTruckMobileDto dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			Principal principal = ckSession.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal is null");

			StringBuffer searchStatement = new StringBuffer();
			if (null != dto.getJobStatus()) {
				searchStatement.append(getOperator(wherePrinted) + "o.jobStatus=:jobStatus");
				wherePrinted = true;
			}

			if (StringUtils.isNotBlank(dto.getJobId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.jobId LIKE :jobId");
				wherePrinted = true;
			}

			Optional<CkJob> opCkJob = Optional.ofNullable(dto.getTCkJob());
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

				Optional<CkRecordDate> opCkRecordDate = Optional.ofNullable(dto.getTCkJob())
						.map(CkJob::getTCkRecordDate);
				if (opCkRecordDate.isPresent()) {
					if (opCkRecordDate.get().getRcdDtSubmit() != null) {
						searchStatement.append(getOperator(wherePrinted))
								.append("DATE_FORMAT(o.TCkJob.TCkRecordDate.rcdDtSubmit,'%d/%m/%Y') = :rcdDtSubmit");
						wherePrinted = true;
					}
				}

				Optional<CkMstJobState> opCkJobMstState = Optional.ofNullable(dto.getTCkJob())
						.map(CkJob::getTCkMstJobState);
				if (opCkJobMstState.isPresent()) {
					if (StringUtils.isNotBlank(opCkJobMstState.get().getJbstId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCkJob.TCkMstJobState.jbstId in :jobState");
						wherePrinted = true;
					}
				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyTo = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
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

			Optional<CoreAccn> opCoreAccnByJobPartyCoFf = Optional.ofNullable(dto.getTCoreAccnByJobPartyCoFf());
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

			Optional<Date> opJobDtBooking = Optional.ofNullable(dto.getJobDtBooking());
			if (opJobDtBooking.isPresent() && null != opJobDtBooking.get()) {
				searchStatement
						.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jobDtBooking,'%d/%m/%Y') = :jobDtBooking");
				wherePrinted = true;
			}
			Optional<Date> opJobDtPlan = Optional.ofNullable(dto.getJobDtPlan());
			if (opJobDtPlan.isPresent() && null != opJobDtPlan.get()) {
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
			if (dto.getHistory().equals(DEFAULT)) {
				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkJob.TCkMstJobState.jbstId = :jobTruckStates");
				wherePrinted = true;
			}

			// searchStatement.append(getOperator(wherePrinted) + "(o.TCkCtDrv.drvMobileId = '" + principal.getUserId()
			//		+ "' AND o.TCkCtDrv.drvState = '" + JobStates.ASG.getDesc() + "')");
			
			searchStatement.append(getOperator(wherePrinted) + "(o.TCkCtDrv.drvMobileId = '" + principal.getUserId()
					+ "' )");

			return searchStatement.toString();
		} catch (ParameterException ex) {
			LOG.error("getWhereClause", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getWhereClause", ex);
			throw new ProcessingException(ex);
		}

	}

	@Override
	protected HashMap<String, Object> getParameters(CkJobTruckMobileDto dto)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(DEFAULT)) {
				parameters.put("jobTruckStates", JobStates.PAUSED.name());
			}

			if (StringUtils.isNotBlank(dto.getJobId())) {
				parameters.put("jobId", "%" + dto.getJobId() + "%");
			}

			Optional<CkJob> opCkJob = Optional.ofNullable(dto.getTCkJob());
			if (opCkJob.isPresent()) {
				Optional<CkMstShipmentType> opShipmntType = opCkJob.map(CkJob::getTCkMstShipmentType);
				if (opShipmntType.isPresent()) {
					if (StringUtils.isNotBlank(opShipmntType.get().getShtId()))
						parameters.put("shtId", opShipmntType.get().getShtId());

					if (StringUtils.isNotBlank(opShipmntType.get().getShtName()))
						parameters.put("shtName", opShipmntType.get().getShtName());
				}

				Optional<CkRecordDate> opCkRecordDate = Optional.ofNullable(dto.getTCkJob())
						.map(CkJob::getTCkRecordDate);
				if (opCkRecordDate.isPresent()) {
					if (opCkRecordDate.get().getRcdDtSubmit() != null)
						parameters.put("rcdDtSubmit", sdfDate.format(opCkRecordDate.get().getRcdDtSubmit()));
				}

				Optional<CkMstJobState> opCkJobMstState = Optional.ofNullable(dto.getTCkJob())
						.map(CkJob::getTCkMstJobState);
				if (opCkJobMstState.isPresent()) {
					if (StringUtils.isNotBlank(opCkJobMstState.get().getJbstId()))
						parameters.put("jobState",
								Arrays.asList(dto.getTCkJob().getTCkMstJobState().getJbstId().split(",")));

				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyTo = Optional.ofNullable(dto.getTCoreAccnByJobPartyTo());
			if (opCoreAccnByJobPartyTo.isPresent()) {
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnId())) {
					parameters.put("toAccnId", opCoreAccnByJobPartyTo.get().getAccnId());
				}
				if (StringUtils.isNotBlank(opCoreAccnByJobPartyTo.get().getAccnName())) {
					parameters.put("toAccnName", "%" + opCoreAccnByJobPartyTo.get().getAccnName() + "%");
				}
			}

			Optional<CoreAccn> opCoreAccnByJobPartyCoFf = Optional.ofNullable(dto.getTCoreAccnByJobPartyCoFf());
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

			return parameters;
		} catch (ParameterException ex) {
			LOG.error("getParameters", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getParameters", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkJobTruckMobileDto whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

			CkJobTruckMobileDto dto = new CkJobTruckMobileDto();
			CkJob ckJob = new CkJob();
			CkRecordDate recordDate = new CkRecordDate();
			CkMstJobState ckMstState = new CkMstJobState();
			CkMstShipmentType ckMstShpType = new CkMstShipmentType();

			CoreAccn tCoreAccnByJobPartyTo = new CoreAccn();
			CoreAccn tCoreAccnByJobPartyCoFf = new CoreAccn();

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

				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}
			}

			ckJob.setTCkMstShipmentType(ckMstShpType);
			ckJob.setTCkRecordDate(recordDate);
			ckJob.setTCkMstJobState(ckMstState);
			dto.setTCkJob(ckJob);

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

		return newAttr;
	}

	@Transactional
	public String checkStatusJobTruck() throws Exception {

		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		String status = "";

		List<TCkJobTruck> listJobTruck = ckJobTruckDao.findByDrvMobileId(principal.getUserId(),
				Arrays.asList(JobStates.ONGOING.name()));
		List<String> ongoingJobIds = listJobTruck.stream().map(tCkJobTruck -> {
			if (StringUtils.isNotBlank(tCkJobTruck.getJobCustomerRef())) {
				// Split and get the first
				List<String> values = Arrays.asList(tCkJobTruck.getJobCustomerRef().split(","));
				if (values != null && values.size() > 0) {
					return StringUtils.join(values, " | ");
				}
			} else if (StringUtils.isNotBlank(tCkJobTruck.getJobShipmentRef())) {
				return tCkJobTruck.getJobShipmentRef();
			}

			return tCkJobTruck.getJobId();

		}).collect(Collectors.toList());

		if (!ongoingJobIds.isEmpty()) {
			String ongoingJobIdsString = String.join(", ", ongoingJobIds);
			status = ongoingJobIdsString;
		}

		return status;
	}

	@Transactional
	public CkJobTruck resumePauseJob(String jobId) throws Exception {
		LOG.debug("resumePauseJob");

		if (null == jobId)
			throw new ParameterException("param jobId null");

		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		CkJobTruck ckJobTruck = new CkJobTruck();
		try {
			List<TCkJobTruck> listJobTruck = ckJobTruckDao.findByDrvMobileId(principal.getUserId(),
					Arrays.asList(JobStates.ONGOING.name()));
			if (!listJobTruck.isEmpty()) {
				for (TCkJobTruck tCkJobTruckLast : listJobTruck) {
					ckJobTruck = dtoFromEntity(tCkJobTruckLast);
					ckJobTruckMobileWorkflowServiceImpl.moveState(FormActions.PAUSE, ckJobTruck, principal,
							ServiceTypes.CLICTRUCK);
					Optional<TCkCtJobTripDelivery> tCkCtJobTripDelivery = Optional
							.ofNullable(ckCtJobTripDeliveryDao.findByJobId(ckJobTruck.getJobId()));
					if (tCkCtJobTripDelivery.isPresent()) {
						ckCtJobTripDeliveryDao.remove(tCkCtJobTripDelivery.get());
					}
				}
			}

			Optional<TCkJobTruck> opTCkJobTruckNew = Optional.ofNullable(ckJobTruckDao.find(jobId));
			if (opTCkJobTruckNew.isPresent()) {
				ckJobTruck = ckJobTruckMobileWorkflowServiceImpl.moveState(FormActions.START,
						dtoFromEntity(opTCkJobTruckNew.get()), principal, ServiceTypes.CLICTRUCK);
			}
			return ckJobTruck;
		} catch (Exception e) {
			LOG.error("resumePauseJob", e);
			throw new ProcessingException(e);
		}
	}
}
