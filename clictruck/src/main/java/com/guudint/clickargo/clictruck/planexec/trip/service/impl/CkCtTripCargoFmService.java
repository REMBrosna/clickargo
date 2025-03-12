/**
 * 
 */
package com.guudint.clickargo.clictruck.planexec.trip.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstCargoType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstCargoType;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtContactDetail;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoFm;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.master.dto.CkMstCntType;
import com.guudint.clickargo.master.model.TCkMstCntType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
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
public class CkCtTripCargoFmService extends AbstractClickCargoEntityService<TCkCtTripCargoFm, String, CkCtTripCargoFm> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtTripCargoFmService.class);
	private static String AUDIT_TAG = "CK CT TRIP";
	private static String TABLE_NAME = "T_CK_CT_TRIP_CARGO_FM";

	// Attributes
	/////////////
	@Autowired
	GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	protected ICkSession ckSession;

	@Autowired
	GenericDao<TCkCtTripCargoFm, String> ckCtTripDao;

	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;

	@Autowired
	@Qualifier("ckCtContactDetailDao")
	private GenericDao<TCkCtContactDetail, String> ckCtContactDetailDao;

	// Constructor
	///////////////////
	public CkCtTripCargoFmService() {
		super("ckCtTripCargoFmDao", AUDIT_TAG, TCkCtTripCargoFm.class.getName(), TABLE_NAME);
	}

	// Override Methods
	///////////////////
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripCargoFm findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTripCargoFm entity = dao.find(id);
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
	public CkCtTripCargoFm deleteById(String id, Principal principal)
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

			TCkCtTripCargoFm entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtTripCargoFm dto = dtoFromEntity(entity);
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
	public CkCtTripCargoFm delete(CkCtTripCargoFm dto, Principal principal)
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
	public List<CkCtTripCargoFm> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripCargoFm dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkCtTripCargoFm o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTripCargoFm> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtTripCargoFm> dtos = entities.stream().map(x -> {
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
	protected TCkCtTripCargoFm initEnity(TCkCtTripCargoFm entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkCtMstCargoType());
			Hibernate.initialize(entity.getTCkCtTrip());
			Hibernate.initialize(entity.getTCkMstCntType());
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
	protected TCkCtTripCargoFm entityFromDTO(CkCtTripCargoFm dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtTripCargoFm entity = new TCkCtTripCargoFm();
			entity = dto.toEntity(entity);
			// no deep copy from BeanUtils

			entity.setTCkCtMstCargoType(null == dto.getTCkCtMstCargoType() || null == dto.getTCkCtMstCargoType().getCrtypId() ? null
					: dto.getTCkCtMstCargoType().toEntity(new TCkCtMstCargoType()));
			entity.setTCkCtTrip(null == dto.getTCkCtTrip() ? null : dto.getTCkCtTrip().toEntity(new TCkCtTrip()));
			entity.setTCkMstCntType(
					null == dto.getTCkMstCntType() || null == dto.getTCkMstCntType().getCnttId() ? null : dto.getTCkMstCntType().toEntity(new TCkMstCntType()));

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
	protected CkCtTripCargoFm dtoFromEntity(TCkCtTripCargoFm entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtTripCargoFm dto = new CkCtTripCargoFm(entity);

			Optional<TCkCtMstCargoType> opCkCtMstCargoType = Optional.ofNullable(entity.getTCkCtMstCargoType());
			dto.setTCkCtMstCargoType(
					opCkCtMstCargoType.isPresent() ? new CkCtMstCargoType(opCkCtMstCargoType.get()) : null);

			Optional<TCkCtTrip> opCkCtTrip = Optional.ofNullable(entity.getTCkCtTrip());
			dto.setTCkCtTrip(opCkCtTrip.isPresent() ? new CkCtTrip(opCkCtTrip.get()) : null);

			Optional<TCkMstCntType> opCkMstCntType = Optional.ofNullable(entity.getTCkMstCntType());
			dto.setTCkMstCntType(opCkMstCntType.isPresent() ? new CkMstCntType(opCkMstCntType.get()) : null);

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
	protected String entityKeyFromDTO(CkCtTripCargoFm dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getCgId() ? null : dto.getCgId();
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
	protected TCkCtTripCargoFm updateEntity(ACTION attriubte, TCkCtTripCargoFm entity, Principal principal, Date date)
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
				entity.setCgUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCgDtCreate(date);
				entity.setCgUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCgDtLupd(date);
				break;

			case MODIFY:
				entity.setCgUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCgDtLupd(date);
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
	protected TCkCtTripCargoFm updateEntityStatus(TCkCtTripCargoFm entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setCgStatus(status);
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
	protected CkCtTripCargoFm preSaveUpdateDTO(TCkCtTripCargoFm storedEntity, CkCtTripCargoFm dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setCgUidCreate(storedEntity.getCgUidCreate());
			dto.setCgDtCreate(storedEntity.getCgDtCreate());

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
	protected void preSaveValidation(CkCtTripCargoFm dto, Principal principal)
			throws ParameterException, ProcessingException {
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
	protected ServiceStatus preUpdateValidation(CkCtTripCargoFm dto, Principal principal)
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
	protected String getWhereClause(CkCtTripCargoFm dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			Principal principal = ckSession.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal is null");

			StringBuffer searchStatement = new StringBuffer();
			
			searchStatement.append(getOperator(wherePrinted) + "o.cgStatus='A' ");
			wherePrinted = true;

			if (StringUtils.isNotBlank(dto.getCgId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.trId LIKE :trId");
				wherePrinted = true;
			}

			if (null != dto.getCgStatus() && Character.isAlphabetic(dto.getCgStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.cgStatus=:cgStatus");
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
	protected HashMap<String, Object> getParameters(CkCtTripCargoFm dto)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = ckSession.getPrincipal();

			if (principal == null)
				throw new ProcessingException("principal is null");

			if (StringUtils.isNotBlank(dto.getCgId()))
				parameters.put("cgId", "%" + dto.getCgId() + "%");

			if (dto.getCgStatus() != null && Character.isAlphabetic(dto.getCgStatus()))
				parameters.put("cgStatus", dto.getCgStatus());

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
	protected CkCtTripCargoFm whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripCargoFm dto = new CkCtTripCargoFm();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("cgId"))
					dto.setCgId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("cgStatus"))
					dto.setCgStatus(opValue.get().charAt(0));
			}

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
	protected CoreMstLocale getCoreMstLocale(CkCtTripCargoFm dto)
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
	protected CkCtTripCargoFm setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtTripCargoFm dto)
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
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#_newJob(com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public CkCtTripCargoFm newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return new CkCtTripCargoFm();
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
	 * Override update from {@code AbstractEntityService} and update the children
	 * individually.
	 */
	@Override
	public CkCtTripCargoFm update(CkCtTripCargoFm dto, Principal principal)
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
	 * @see com.guudint.clickargo.common.AbstractClickCargoEntityService#getLogger()
	 *
	 */
	@Override
	protected Logger getLogger() {
		return LOG;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public List<CkCtTripCargoFm> findTripCargoFmsByTripId(String id) {
		List<CkCtTripCargoFm> ckCtTripList = new ArrayList<CkCtTripCargoFm>();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("trId", id);
			parameters.put("cgStatus", RecordStatus.ACTIVE.getCode());

			String hql = "FROM TCkCtTripCargoFm o WHERE o.TCkCtTrip.trId = :trId AND o.cgStatus = :cgStatus";
			List<TCkCtTripCargoFm> ckCtTrips = dao.getByQuery(hql, parameters);

			ckCtTrips.forEach(inst -> {
				try {
					ckCtTripList.add(this.dtoFromEntity(inst));
				} catch (ParameterException | ProcessingException ex) {
					ex.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckCtTripList;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public TCkCtTripCargoFm findOneTripCargoFmByTripId(String id) {
		TCkCtTripCargoFm ckCtTripCargoFm = new TCkCtTripCargoFm();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("trId", id);
			parameters.put("cgStatus", RecordStatus.ACTIVE.getCode());

			String hql = "FROM TCkCtTripCargoFm o WHERE o.TCkCtTrip.trId = :trId AND o.cgStatus = :cgStatus";
			List<TCkCtTripCargoFm> ckCtTrips = dao.getByQuery(hql, parameters);
			ckCtTripCargoFm = ckCtTrips.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckCtTripCargoFm;
	}

	/**
	 * 
	 * @param orderBy
	 * @return
	 * @throws Exception
	 */
	protected EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) throws Exception {

		if (orderBy == null)
			return null;

		if (StringUtils.isEmpty(orderBy.getAttribute()))
			return null;

		String newAttr = formatOrderBy(orderBy.getAttribute());
		if (StringUtils.isEmpty(newAttr))
			return orderBy;

		orderBy.setAttribute(newAttr);
		return orderBy;

	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.AbstractJobService#formatOrderBy(java.lang.String)
	 *
	 */
	protected String formatOrderBy(String attribute) throws Exception {

		String newAttr = attribute;
		if (StringUtils.contains(newAttr, "tckCtTripCharge"))
			newAttr = newAttr.replace("tckCtTripCharge", "TCkCtTripCargoFmCharge");

		if (StringUtils.contains(newAttr, "tckCtTripLocationByTrTo"))
			newAttr = newAttr.replace("tckCtTripLocationByTrTo", "TCkCtTripCargoFmLocationByTrTo");

		if (StringUtils.contains(newAttr, "tckCtTripLocationByTrFrom"))
			newAttr = newAttr.replace("tckCtTripLocationByTrFrom", "TCkCtTripCargoFmLocationByTrFrom");

		if (StringUtils.contains(newAttr, "tckJobTruck"))
			newAttr = newAttr.replace("tckJobTruck", "TCkJobTruck");
		
		if (StringUtils.contains(newAttr, "tckCtTrip"))
			newAttr = newAttr.replace("tckCtTrip", "TCkCtTrip");
		
		if (StringUtils.contains(newAttr, "tckCtMstCargoType"))
			newAttr = newAttr.replace("tckCtMstCargoType", "TCkCtMstCargoType");

		return newAttr;
	}
}
