package com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkJobTruckMobileDto;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripReimbursementDao;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripReimbursement;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripCargoMmService;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dto.CkRecordDate;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.model.TCkJob;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.dto.CkMstJobType;
import com.guudint.clickargo.master.dto.CkMstShipmentType;
import com.guudint.clickargo.master.model.TCkMstShipmentType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.util.email.SysParam;

/**
 * Extension service of {@code CkJobTruckService} for mobile application. This
 * service does the workflow state machine for mobile related actions associated
 * to a job.
 */
public class CkJobTruckMobileService extends AbstractClickCargoEntityService<TCkJobTruck, String, CkJobTruckMobileDto> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkJobTruckService.class);
	private static String AUDIT_TAG = "CK JOB TRUCK";
	private static String TABLE_NAME = "T_CK_JOB_TRUCK";

	@Autowired
	protected SysParam sysParam;

	@Autowired
	@Qualifier("ckJobTruckService")
	private IEntityService<TCkJobTruck, String, CkJobTruck> ckJobTruckService;

	@Autowired
	private CkCtTripCargoMmService mmTripService;

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	@Autowired
	private CkCtTripService ckCtTripService;

	@Autowired
	private CkCtTripReimbursementDao ckCtTripReimbursementDao;

	public CkJobTruckMobileService() {
		super("ckJobTruckDao", AUDIT_TAG, TCkJobTruck.class.getName(), TABLE_NAME);
	}

	@Override
	public CkJobTruckMobileDto newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CkJobTruckMobileDto deleteById(String arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CkJobTruckMobileDto> filterBy(EntityFilterRequest arg0)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkJobTruckMobileDto findById(String id)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			Principal principal = principalUtilService.getPrincipal();
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

			return this.dtoFromEntity(entity);

		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("findById", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("findById", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		return LOG;
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

				dto.setHasRemarks(ckJobTruckUtilService.isJobRemarked(ckJob.getJobId()));

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

			// Load job trips if have
			List<CkCtTrip> tckCtTrips = ckCtTripService.findTripsByTruckJobAndStatus(dto.getJobId(),
					Arrays.asList(RecordStatus.ACTIVE.getCode()));

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
					tckCtTrips.forEach(e -> {
						try {
							List<CkCtTripCargoMm> fmTrip = mmTripService.findTripCargoFmmsByTripId(e.getTrId());
							e.setTripCargoMmList(fmTrip);

						} catch (Exception ex) {
							LOG.error("fm trip error", ex);
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
	protected TCkJobTruck entityFromDTO(CkJobTruckMobileDto dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkJobTruck entity = new TCkJobTruck();
			entity = dto.toEntity(entity);
			// no deep copy from BeanUtils
			TCkJob ckParentJob = new TCkJob();

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
			}

			entity.setTCkCtContactDetailByJobContactTo(null == dto.getTCkCtContactDetailByJobContactTo() ? null
					: dto.getTCkCtContactDetailByJobContactTo().toEntity(new TCkCtContactDetail()));
			entity.setTCkCtContactDetailByJobContactCoFf(null == dto.getTCkCtContactDetailByJobContactCoFf() ? null
					: dto.getTCkCtContactDetailByJobContactCoFf().toEntity(new TCkCtContactDetail()));

			entity.setTCkCtDebitNoteByJobInvoiceeDebitNote(null == dto.getTCkCtDebitNoteByJobInvoiceeDebitNote() ? null
					: dto.getTCkCtDebitNoteByJobInvoiceeDebitNote().toEntity(new TCkCtDebitNote()));
			entity.setTCkCtDebitNoteByJobInvoicerDebitNote(null == dto.getTCkCtDebitNoteByJobInvoicerDebitNote() ? null
					: dto.getTCkCtDebitNoteByJobInvoicerDebitNote().toEntity(new TCkCtDebitNote()));

			entity.setTCkCtDrv(null == dto.getTCkCtDrv() ? null : dto.getTCkCtDrv().toEntity(new TCkCtDrv()));

			entity.setTCkCtMstVehType(
					null == dto.getTCkCtMstVehType() ? null : dto.getTCkCtMstVehType().toEntity(new TCkCtMstVehType()));
			entity.setTCkCtToInvoice(
					null == dto.getTCkCtToInvoice() ? null : dto.getTCkCtToInvoice().toEntity(new TCkCtToInvoice()));
			entity.setTCkCtVeh(null == dto.getTCkCtVeh() ? null : dto.getTCkCtVeh().toEntity(new TCkCtVeh()));

			entity.setTCoreAccnByJobPartyTo(null == dto.getTCoreAccnByJobPartyTo() ? null
					: dto.getTCoreAccnByJobPartyTo().toEntity(new TCoreAccn()));
			entity.setTCoreAccnByJobPartyCoFf(null == dto.getTCoreAccnByJobPartyCoFf() ? null
					: dto.getTCoreAccnByJobPartyCoFf().toEntity(new TCoreAccn()));

			entity.setJobDrvOth(null == dto.getJobDrvOth() ? null : dto.getJobDrvOth().toString());
			entity.setJobVehOth(null == dto.getJobVehOth() ? null : dto.getJobVehOth().toString());

			return entity;
		} catch (ParameterException ex) {
			LOG.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected String entityKeyFromDTO(CkJobTruckMobileDto dto) throws ParameterException, ProcessingException {
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

	@Override
	protected CoreMstLocale getCoreMstLocale(CkJobTruckMobileDto arg0)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HashMap<String, Object> getParameters(CkJobTruckMobileDto arg0)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkJobTruckMobileDto arg0, boolean arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkJobTruck initEnity(TCkJobTruck arg0) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkJobTruckMobileDto preSaveUpdateDTO(TCkJobTruck arg0, CkJobTruckMobileDto arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preSaveValidation(CkJobTruckMobileDto arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkJobTruckMobileDto arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkJobTruckMobileDto setCoreMstLocale(CoreMstLocale arg0, CkJobTruckMobileDto arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkJobTruck updateEntity(ACTION arg0, TCkJobTruck arg1, Principal arg2, Date arg3)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkJobTruck updateEntityStatus(TCkJobTruck arg0, char arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkJobTruckMobileDto whereDto(EntityFilterRequest arg0) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	// Helper Methods
	//////////////////////
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

		boolean isPermission = loginAccnId.equalsIgnoreCase(coAccnId) || loginAccnId.equalsIgnoreCase(toAccnId);
		if (!isPermission) {
			throw new Exception(String.format("%S no permission to find %S", loginAccnId, job.getJobId()));
		}
	}

	private Map<String, Object> stringToObject(String data) {

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

}
