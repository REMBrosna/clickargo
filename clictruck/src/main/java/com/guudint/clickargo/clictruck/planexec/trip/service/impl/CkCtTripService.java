/**
 * 
 */
package com.guudint.clickargo.clictruck.planexec.trip.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCharge;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDoAttach;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.CkCtTripWorkflowServiceImpl.TripStatus;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCharge;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.validator.TripValidator;
import com.guudint.clickargo.common.CkErrorCodes;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
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
import com.vcc.camelone.locale.dto.CoreMstLocale;

/**
 * @author adenny
 *
 */
public class CkCtTripService extends AbstractJobService<CkCtTrip, TCkCtTrip, String> implements ICkConstant {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtTripService.class);
	private static String AUDIT_TAG = "CK CT TRIP";
	private static String TABLE_NAME = "T_CK_CT_TRIP";

	// Attributes
	/////////////
	@Autowired
	GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	GenericDao<TCkCtTrip, String> ckCtTripDao;

	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;

	@Autowired
	@Qualifier("ckCtContactDetailDao")
	private GenericDao<TCkCtContactDetail, String> ckCtContactDetailDao;

	@Autowired
	private CkCtTripCargoFmService ckCtTripCargoFmService;

	@Autowired
	private CkCtTripDoAttachService ckCtTripDoAttachService;

	@Autowired
	private CkCtTripDoService ckCtTripDoService;

	// Constructor
	///////////////////
	public CkCtTripService() {
		super("ckCtTripDao", AUDIT_TAG, TCkCtTrip.class.getName(), TABLE_NAME);
	}

	// Override Methods
	///////////////////
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTrip findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTrip entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);
			this.initEnity(entity);

			return this.dtoFromEntity(entity);
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
	public CkCtTrip deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
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

			TCkCtTrip entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtTrip dto = dtoFromEntity(entity);
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
	public CkCtTrip delete(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
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
	public List<CkCtTrip> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTrip dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkCtTrip o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTrip> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtTrip> dtos = entities.stream().map(x -> {
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
	protected TCkCtTrip initEnity(TCkCtTrip entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkCtTripCharge());
			Hibernate.initialize(entity.getTCkCtTripLocationByTrTo());
			Hibernate.initialize(entity.getTCkCtTripLocationByTrFrom());
			Hibernate.initialize(entity.getTCkCtTripLocationByTrDepot());
			Hibernate.initialize(entity.getTCkJobTruck());
			if (entity.getTCkJobTruck() != null) {
				Hibernate.initialize(entity.getTCkJobTruck().getTCoreAccnByJobPartyTo());
				Hibernate.initialize(entity.getTCkJobTruck().getTCoreAccnByJobPartyCoFf());
			}
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
	protected TCkCtTrip entityFromDTO(CkCtTrip dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtTrip entity = new TCkCtTrip();
			entity = dto.toEntity(entity);
			// no deep copy from BeanUtils

			entity.setTCkCtTripCharge(
					null == dto.getTCkCtTripCharge() ? null : dto.getTCkCtTripCharge().toEntity(new TCkCtTripCharge()));
			entity.setTCkCtTripLocationByTrTo(null == dto.getTCkCtTripLocationByTrTo() ? null
					: dto.getTCkCtTripLocationByTrTo().toEntity(new TCkCtTripLocation()));
			entity.setTCkCtTripLocationByTrFrom(null == dto.getTCkCtTripLocationByTrFrom() ? null
					: dto.getTCkCtTripLocationByTrFrom().toEntity(new TCkCtTripLocation()));
			entity.setTCkCtTripLocationByTrDepot(null == dto.getTCkCtTripLocationByTrDepot() ? null
					: dto.getTCkCtTripLocationByTrDepot().toEntity(new TCkCtTripLocation()));
			entity.setTCkJobTruck(
					null == dto.getTCkJobTruck() ? null : dto.getTCkJobTruck().toEntity(new TCkJobTruck()));

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
	public CkCtTrip dtoFromEntity(TCkCtTrip entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtTrip dto = new CkCtTrip(entity);

			Optional<TCkCtTripCharge> opCkCtTripCharge = Optional.ofNullable(entity.getTCkCtTripCharge());
			dto.setTCkCtTripCharge(opCkCtTripCharge.isPresent() ? new CkCtTripCharge(opCkCtTripCharge.get()) : null);

			Optional<TCkCtTripLocation> opCkCtTripLocationByTrTo = Optional
					.ofNullable(entity.getTCkCtTripLocationByTrTo());
			dto.setTCkCtTripLocationByTrTo(
					opCkCtTripLocationByTrTo.isPresent() ? new CkCtTripLocation(opCkCtTripLocationByTrTo.get()) : null);
			if (opCkCtTripLocationByTrTo.isPresent()) {
				Optional<TCkCtLocation> opCkCtLocationTo = Optional
						.ofNullable(entity.getTCkCtTripLocationByTrTo().getTCkCtLocation());
				opCkCtLocationTo.ifPresent(c -> dto.getTCkCtTripLocationByTrTo().setTCkCtLocation(new CkCtLocation(c)));
			}

			Optional<TCkCtTripLocation> opCkCtTripLocationByTrFrom = Optional
					.ofNullable(entity.getTCkCtTripLocationByTrFrom());
			dto.setTCkCtTripLocationByTrFrom(
					opCkCtTripLocationByTrFrom.isPresent() ? new CkCtTripLocation(opCkCtTripLocationByTrFrom.get())
							: null);
			if (opCkCtTripLocationByTrFrom.isPresent()) {
				Optional<TCkCtLocation> opCkCtLocationFrom = Optional
						.ofNullable(entity.getTCkCtTripLocationByTrFrom().getTCkCtLocation());
				opCkCtLocationFrom
						.ifPresent(c -> dto.getTCkCtTripLocationByTrFrom().setTCkCtLocation(new CkCtLocation(c)));
			}

			Optional<TCkCtTripLocation> opCkCtTripLocationByTrDepot = Optional
					.ofNullable(entity.getTCkCtTripLocationByTrDepot());
			dto.setTCkCtTripLocationByTrDepot(
					opCkCtTripLocationByTrDepot.isPresent() ? new CkCtTripLocation(opCkCtTripLocationByTrDepot.get())
							: null);
			if (opCkCtTripLocationByTrDepot.isPresent()) {
				Optional<TCkCtLocation> opCkCtLocationDepot = Optional
						.ofNullable(entity.getTCkCtTripLocationByTrDepot().getTCkCtLocation());
				opCkCtLocationDepot
						.ifPresent(c -> dto.getTCkCtTripLocationByTrDepot().setTCkCtLocation(new CkCtLocation(c)));
			}

			Optional<TCkJobTruck> opCkJobTruck = Optional.ofNullable(entity.getTCkJobTruck());
			dto.setTCkJobTruck(opCkJobTruck.isPresent() ? new CkJobTruck(opCkJobTruck.get()) : null);
			if (opCkJobTruck.isPresent()) {
				dto.getTCkJobTruck()
						.setTCoreAccnByJobPartyTo(new CoreAccn(opCkJobTruck.get().getTCoreAccnByJobPartyTo()));
				dto.getTCkJobTruck()
						.setTCoreAccnByJobPartyCoFf(new CoreAccn(opCkJobTruck.get().getTCoreAccnByJobPartyCoFf()));
			}

			List<CkCtTripCargoFm> tckCtTripCargoFms = ckCtTripCargoFmService.findTripCargoFmsByTripId(dto.getTrId());
			dto.setTckCtTripCargoFmList(
					!ObjectUtils.isEmpty(tckCtTripCargoFms) ? tckCtTripCargoFms : new ArrayList<CkCtTripCargoFm>());

			CkCtTripDoAttach unSignedDo = ckCtTripDoAttachService.findByTripId(dto.getTrId(), false);
			dto.setUnsignedDo(null != unSignedDo ? unSignedDo : null);
			CkCtTripDoAttach signedDo = ckCtTripDoAttachService.findByTripId(dto.getTrId(), true);
			dto.setSignedDo(null != signedDo ? signedDo : null);

			CkCtTripDo ckCtTripDo = ckCtTripDoService.findByTripId(dto.getTrId());
			dto.setTckCtTripDo(ckCtTripDo);

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
	protected String entityKeyFromDTO(CkCtTrip dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getTrId() ? null : dto.getTrId();
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
	protected TCkCtTrip updateEntity(ACTION attriubte, TCkCtTrip entity, Principal principal, Date date)
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
				entity.setTrUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setTrDtCreate(date);
				entity.setTrUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setTrDtLupd(date);
				break;

			case MODIFY:
				entity.setTrUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setTrDtLupd(date);
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
	protected TCkCtTrip updateEntityStatus(TCkCtTrip entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setTrStatus(status);
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
	protected CkCtTrip preSaveUpdateDTO(TCkCtTrip storedEntity, CkCtTrip dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setTrUidCreate(storedEntity.getTrUidCreate());
			dto.setTrDtCreate(storedEntity.getTrDtCreate());

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
	protected void preSaveValidation(CkCtTrip dto, Principal principal) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

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
	protected ServiceStatus preUpdateValidation(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
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
	protected String getWhereClause(CkCtTrip dto, boolean wherePrinted) throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			StringBuffer searchStatement = new StringBuffer();

			searchStatement.append(getOperator(wherePrinted) + "o.trStatus in (:validStatus) ");
			wherePrinted = true;

			if (StringUtils.isNotBlank(dto.getTrId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.trId LIKE :trId");
				wherePrinted = true;
			}

			if (null != dto.getTrStatus() && Character.isAlphabetic(dto.getTrStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.trStatus=:trStatus");
				wherePrinted = true;
			}

			Optional<CkJobTruck> opCkJobTruck = Optional.of(dto.getTCkJobTruck());
			if (opCkJobTruck.isPresent()) {
				searchStatement.append(getOperator(wherePrinted)).append("o.TCkJobTruck.jobId= :jobId");
				wherePrinted = true;
			}

			Optional<CkCtTripLocation> opCkCtTripLocationFrom = Optional.of(dto.getTCkCtTripLocationByTrFrom());
			if (opCkCtTripLocationFrom.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtTripLocationFrom.get().getTlocId())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCkCtTripLocationByTrFrom.tlocId = :tripLocIdFrom");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opCkCtTripLocationFrom.get().getTlocLocName())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCkCtTripLocationByTrFrom.tlocLocName LIKE :tripLocFrom");
					wherePrinted = true;
				}
			}

			Optional<CkCtTripLocation> opCkCtTripLocationTo = Optional.of(dto.getTCkCtTripLocationByTrTo());
			if (opCkCtTripLocationTo.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtTripLocationTo.get().getTlocId())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCkCtTripLocationByTrTo.tlocId = :tripLocIdTo");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opCkCtTripLocationTo.get().getTlocLocName())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCkCtTripLocationByTrTo.tlocLocName LIKE :tripLocTo");
					wherePrinted = true;
				}
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
	protected HashMap<String, Object> getParameters(CkCtTrip dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			parameters.put("validStatus",
					Arrays.asList(TripStatus.M_ACTIVE.getStatusCode(), TripStatus.M_DELIVERED.getStatusCode(),
							TripStatus.M_PICKED_UP.getStatusCode(), TripStatus.DLV.getStatusCode()));

			if (StringUtils.isNotBlank(dto.getTrId()))
				parameters.put("trId", "%" + dto.getTrId() + "%");

			if (dto.getTrStatus() != null && Character.isAlphabetic(dto.getTrStatus()))
				parameters.put("trStatus", dto.getTrStatus());

			Optional<CkJobTruck> opCkJobTruck = Optional.of(dto.getTCkJobTruck());
			if (opCkJobTruck.isPresent()) {
				parameters.put("jobId", opCkJobTruck.get().getJobId());
			}

			Optional<CkCtTripLocation> opCkCtTripLocationFrom = Optional.of(dto.getTCkCtTripLocationByTrFrom());
			if (opCkCtTripLocationFrom.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtTripLocationFrom.get().getTlocId())) {
					parameters.put("tripLocIdFrom", opCkCtTripLocationFrom.get().getTlocId());
				}
				if (StringUtils.isNotBlank(opCkCtTripLocationFrom.get().getTlocLocName())) {
					parameters.put("tripLocFrom", "%" + opCkCtTripLocationFrom.get().getTlocLocName() + "%");
				}
			}
			Optional<CkCtTripLocation> opCkCtTripLocationTo = Optional.of(dto.getTCkCtTripLocationByTrTo());
			if (opCkCtTripLocationTo.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtTripLocationTo.get().getTlocId())) {
					parameters.put("tripLocIdTo", opCkCtTripLocationTo.get().getTlocId());
				}
				if (StringUtils.isNotBlank(opCkCtTripLocationTo.get().getTlocLocName())) {
					parameters.put("tripLocTo", "%" + opCkCtTripLocationTo.get().getTlocLocName() + "%");
				}
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
	protected CkCtTrip whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTrip dto = new CkCtTrip();
			CkJobTruck tCkJobTruck = new CkJobTruck();
			CkCtTripLocation tCkCtTripLocationByTrFrom = new CkCtTripLocation();
			CkCtTripLocation tCkCtTripLocationByTrTo = new CkCtTripLocation();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("trId"))
					dto.setTrId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("trStatus"))
					dto.setTrStatus(opValue.get().charAt(0));

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkJobTruck.jobId"))
					tCkJobTruck.setJobId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtTripLocationByTrFrom.tlocId"))
					tCkCtTripLocationByTrFrom.setTlocId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtTripLocationByTrFrom.tlocLocName"))
					tCkCtTripLocationByTrFrom.setTlocLocName(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtTripLocationByTrTo.tlocId"))
					tCkCtTripLocationByTrTo.setTlocId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtTripLocationByTrTo.tlocLocName"))
					tCkCtTripLocationByTrTo.setTlocLocName(opValue.get());

			}
			dto.setTCkCtTripLocationByTrFrom(tCkCtTripLocationByTrFrom);
			dto.setTCkCtTripLocationByTrTo(tCkCtTripLocationByTrTo);
			dto.setTCkJobTruck(tCkJobTruck);

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
	protected CoreMstLocale getCoreMstLocale(CkCtTrip dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
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
	protected CkCtTrip setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtTrip dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.bzValidator = new TripValidator();
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_validateGroupClass(com.guudint.clickargo.job.service.IJobEvent.JobEvent)
	 *
	 */
	@Override
	protected Class<?>[] _validateGroupClass(JobEvent jobEvent) {
		// TODO Auto-generated method stub
		LOG.debug("_validateGroupClass");
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
	protected void _auditEvent(JobEvent jobEvent, CkCtTrip dto, Principal principal) {
		// TODO Auto-generated method stub
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
			coreAuditLog.setAudtReckey(StringUtils.isEmpty(dto.getTrId()) ? DASH : dto.getTrId());
			coreAuditLog.setAudtParam1(DASH);
			coreAuditLog.setAudtParam2(DASH);
			coreAuditLog.setAudtParam3(DASH);
			coreAuditLog.setAudtRemarks(DASH);
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
	protected void _auditError(JobEvent jobEvent, CkCtTrip dto, Exception ex, Principal principal) {
		// TODO Auto-generated method stub
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
	protected AbstractJobEvent<CkCtTrip> _getJobEvent(JobEvent jobEvent, CkCtTrip dto, Principal principal) {
		// TODO Auto-generated method stub
		LOG.debug("_getJobEvent");
		return null;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_newJob(com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	protected CkCtTrip _newJob(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return new CkCtTrip();
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
	protected CkCtTrip _createJob(CkCtTrip dto, CkJob parentJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {
		LOG.debug("_createJob");

		if (null == dto)
			throw new ParameterException("param dto null;");
		if (null == principal)
			throw new ParameterException("param principal null");

		// TODO: add properties
		return this.add(dto, principal);
	}

	/**
	 * Override update from {@code AbstractEntityService} and update the children
	 * individually.
	 */
	@Override
	public CkCtTrip update(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		try {
			return super.update(dto, principal);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
	}

	/**
	 *
	 * (non-Javadoc)
	 *
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#add(java.lang.Object,
	 *      com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	public CkCtTrip add(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		try {
			return super.add(dto, principal);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
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
	protected CkCtTrip _submitJob(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("_submitJob");
		return null;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_rejectJob(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	protected CkCtTrip _rejectJob(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("_rejectJob");
		return null;
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
	protected CkCtTrip _cancelJob(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		LOG.debug("_cancelJob");
		return null;
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
	protected CkCtTrip _confirmJob(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
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
	protected CkCtTrip _payJob(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
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
	protected CkCtTrip _paidJob(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
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
	protected CkCtTrip _completeJob(CkCtTrip dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
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
		if (StringUtils.contains(newAttr, "tckCtTripCharge"))
			newAttr = newAttr.replace("tckCtTripCharge", "TCkCtTripCharge");

		if (StringUtils.contains(newAttr, "tckCtTripLocationByTrTo"))
			newAttr = newAttr.replace("tckCtTripLocationByTrTo", "TCkCtTripLocationByTrTo");

		if (StringUtils.contains(newAttr, "tckCtTripLocationByTrFrom"))
			newAttr = newAttr.replace("tckCtTripLocationByTrFrom", "TCkCtTripLocationByTrFrom");

		if (StringUtils.contains(newAttr, "tckCtTripLocationByTrDepot"))
			newAttr = newAttr.replace("tckCtTripLocationByTrDepot", "TCkCtTripLocationByTrDepot");

		if (StringUtils.contains(newAttr, "tckJobTruck"))
			newAttr = newAttr.replace("tckJobTruck", "TCkJobTruck");

		return newAttr;
	}

	@Transactional
	public List<CkCtTrip> findTripsByTruckJobAndStatus(String id, List<Character> listStatus) {
		List<CkCtTrip> ckCtTripList = new ArrayList<CkCtTrip>();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("jobId", id);
			parameters.put("listStatus", listStatus);

			String hql = "FROM TCkCtTrip o WHERE o.TCkJobTruck.jobId = :jobId AND o.trStatus in :listStatus ";
//			if needed add order by here to make sure that the first and last trip is retrieved correctly
//					+ "ORDER BY o.TCkCtTripLocationByTrTo.tlocDtLoc DESC";
			List<TCkCtTrip> ckCtTrips = dao.getByQuery(hql, parameters);

			ckCtTrips.forEach(inst -> {
				try {
					initEnity(inst);
					ckCtTripList.add(this.dtoFromEntity(inst));
				} catch (ParameterException | ProcessingException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckCtTripList;
	}

	/**
	 * This methods retrieves a list of trips associated with a truck job
	 *
	 * @param id
	 * @param listStatus
	 * @return
	 */
	@Transactional
	public Set<CkCtTrip> findTripsByTruckJobId(String id, List<Character> listStatus) {
		Set<CkCtTrip> tripList = new LinkedHashSet<CkCtTrip>();
		Set<String> tripIds = new HashSet<>();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("jobId", id);
			parameters.put("listStatus", listStatus);

			String hql = "FROM TCkCtTrip o WHERE o.TCkJobTruck.jobId = :jobId AND o.trStatus in :listStatus ";
			List<TCkCtTrip> ckCtTrips = dao.getByQuery(hql, parameters);

			if (ckCtTrips != null && ckCtTrips.size() > 0) {
				for (TCkCtTrip e : ckCtTrips) {
					if (tripIds.add(e.getTrId())) {
						CkCtTrip dto = new CkCtTrip(e);
						dto.setTCkJobTruck(new CkJobTruck(e.getTCkJobTruck()));
						dto.setTCkCtTripLocationByTrFrom(new CkCtTripLocation(
								null != e.getTCkCtTripLocationByTrFrom() ? e.getTCkCtTripLocationByTrFrom() : null));
						dto.setTCkCtTripLocationByTrTo(new CkCtTripLocation(
								null != e.getTCkCtTripLocationByTrTo() ? e.getTCkCtTripLocationByTrTo() : null));
						dto.getTCkCtTripLocationByTrFrom()
								.setTCkCtLocation(new CkCtLocation(null != e.getTCkCtTripLocationByTrFrom()
										? e.getTCkCtTripLocationByTrFrom().getTCkCtLocation()
										: null));
						dto.getTCkCtTripLocationByTrTo()
								.setTCkCtLocation(new CkCtLocation(null != e.getTCkCtTripLocationByTrTo()
										? e.getTCkCtTripLocationByTrTo().getTCkCtLocation()
										: null));
						tripList.add(dto);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tripList;
	}

	@Transactional
	public List<TCkCtTrip> getTripsByTruckJobId(String jobId) {
		List<TCkCtTrip> tripList = new ArrayList<>();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("jobId", jobId);
			String hql = "FROM TCkCtTrip o WHERE o.TCkJobTruck.jobId = :jobId ";
			List<TCkCtTrip> ckCtTrips = dao.getByQuery(hql, parameters);

			if (ckCtTrips != null && ckCtTrips.size() > 0) {
				tripList = ckCtTrips;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("getTripsByTruckJobId:", e);
		}
		return tripList;
	}
}
