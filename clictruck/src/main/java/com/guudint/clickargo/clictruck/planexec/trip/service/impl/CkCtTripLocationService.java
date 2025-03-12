package com.guudint.clickargo.clictruck.planexec.trip.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;

public class CkCtTripLocationService extends AbstractClickCargoEntityService<TCkCtTripLocation, String, CkCtTripLocation> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtTripLocationService.class);
	private static String AUDIT_TAG = "CK CT TRIP LOCATION";
	private static String TABLE_NAME = "T_CK_CT_TRIP_LOCATION";
	
	// Attributes
	/////////////
	@Autowired
	protected ICkSession ckSession;
	
	// Constructor
	//////////////
	public CkCtTripLocationService() {
		super("ckCtTripLocationDao", AUDIT_TAG, TCkCtTripLocation.class.getName(), TABLE_NAME);
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.guudint.clickargo.common.IClickargoEntityService#newObj(com.vcc.camelone.
	 * cac.model.Principal)
	 *
	 */
	@Override
	public CkCtTripLocation newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return new CkCtTripLocation();
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vcc.camelone.common.service.entity.IEntityService#deleteById(java.lang.
	 * String, com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripLocation deleteById(String id, Principal principal)
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

			TCkCtTripLocation entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtTripLocation dto = dtoFromEntity(entity);
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
	 * @see com.vcc.camelone.common.service.entity.IEntityService#filterBy(com.vcc.
	 * camelone.common.controller.entity.EntityFilterRequest)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtTripLocation> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripLocation dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkCtTripLocation o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTripLocation> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtTripLocation> dtos = entities.stream().map(x -> {
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
	 * @see com.vcc.camelone.common.service.entity.IEntityService#findById(java.lang.String)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripLocation findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTripLocation entity = dao.find(id);
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

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#dtoFromEntity(java.lang.Object)
	 *
	 */
	@Override
	protected CkCtTripLocation dtoFromEntity(TCkCtTripLocation entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtTripLocation dto = new CkCtTripLocation(entity);
			
			Optional<TCkCtLocation> opCkCtLocation = Optional.ofNullable(entity.getTCkCtLocation());
			dto.setTCkCtLocation(opCkCtLocation.isPresent() ? new CkCtLocation(opCkCtLocation.get()) : null);

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
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#entityFromDTO(java.lang.Object)
	 *
	 */
	@Override
	protected TCkCtTripLocation entityFromDTO(CkCtTripLocation dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtTripLocation entity = new TCkCtTripLocation();
			entity = dto.toEntity(entity);
			// no deep copy from BeanUtils
			entity.setTCkCtLocation(
					null == dto.getTCkCtLocation() ? null : dto.getTCkCtLocation().toEntity(new TCkCtLocation()));

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
	 * @see
	 * com.vcc.camelone.common.service.entity.AbstractEntityService#entityKeyFromDTO
	 * (java.lang.Object)
	 *
	 */
	@Override
	protected String entityKeyFromDTO(CkCtTripLocation dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getTlocId() ? null : dto.getTlocId();
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
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#getCoreMstLocale(java.lang.Object)
	 *
	 */
	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtTripLocation arg0)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#getParameters(java.lang.Object)
	 *
	 */
	@Override
	protected HashMap<String, Object> getParameters(CkCtTripLocation dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = ckSession.getPrincipal();
			
			if(principal == null)
				throw new ProcessingException("principal is null");

			if (StringUtils.isNotBlank(dto.getTlocId()))
				parameters.put("tlocId", "%" + dto.getTlocId() + "%");

			if (dto.getTlocStatus() != null && Character.isAlphabetic(dto.getTlocStatus()))
				parameters.put("tlocStatus", dto.getTlocStatus());
			
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
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#getWhereClause(java.lang.Object,
	 *      boolean)
	 *
	 */
	@Override
	protected String getWhereClause(CkCtTripLocation dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			
			Principal principal = ckSession.getPrincipal();
			if(principal == null)
				throw new ProcessingException("principal is null");
			
			StringBuffer searchStatement = new StringBuffer();
			
			if (StringUtils.isNotBlank(dto.getTlocId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.tlocId LIKE :tlocId");
				wherePrinted = true;
			}
			
			if (null != dto.getTlocStatus() && Character.isAlphabetic(dto.getTlocStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.tlocStatus = :tlocStatus");
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
	 * @see
	 * com.vcc.camelone.common.service.entity.AbstractEntityService#initEnity(java.
	 * lang.Object)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtTripLocation initEnity(TCkCtTripLocation entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkCtLocation());

		}
		return entity;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#preSaveUpdateDTO(java.lang.Object,
	 *      java.lang.Object)
	 *
	 */
	@Override
	protected CkCtTripLocation preSaveUpdateDTO(TCkCtTripLocation storedEntity, CkCtTripLocation dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setTlocUidCreate(storedEntity.getTlocUidCreate());
			dto.setTlocDtCreate(storedEntity.getTlocDtCreate());

			return dto;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("preSaveUpdateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#
	 * preSaveValidation(java.lang.Object, com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	protected void preSaveValidation(CkCtTripLocation arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#preUpdateValidation(java.lang.Object,
	 *      com.vcc.camelone.cac.model.Principal)
	 *
	 */
	@Override
	protected ServiceStatus preUpdateValidation(CkCtTripLocation arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#setCoreMstLocale(com.vcc.camelone.locale.dto.CoreMstLocale,
	 *      java.lang.Object)
	 *
	 */
	@Override
	protected CkCtTripLocation setCoreMstLocale(CoreMstLocale arg0, CkCtTripLocation arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
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
	protected TCkCtTripLocation updateEntity(ACTION attriubte, TCkCtTripLocation entity, Principal principal, Date date)
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
				entity.setTlocUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setTlocDtCreate(date);
				entity.setTlocUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setTlocDtLupd(date);
				break;

			case MODIFY:
				entity.setTlocUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setTlocDtLupd(date);
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

	@Override
	protected TCkCtTripLocation updateEntityStatus(TCkCtTripLocation entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setTlocStatus(status);
			return entity;
		} catch (ParameterException ex) {
			LOG.error("updateEntityStatus", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("updateEntityStatus", ex);
			throw new ProcessingException(ex);
		}
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vcc.camelone.common.service.entity.AbstractEntityService#whereDto(com.vcc
	 * .camelone.common.controller.entity.EntityFilterRequest)
	 *
	 */
	@Override
	protected CkCtTripLocation whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripLocation dto = new CkCtTripLocation();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("tlocId"))
					dto.setTlocId(opValue.get());				
				if (entityWhere.getAttribute().equalsIgnoreCase("tlocStatus"))
					dto.setTlocStatus(opValue.get().charAt(0));

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
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	protected String formatOrderBy(String attribute) throws Exception {

		String newAttr = attribute;
		if (StringUtils.contains(newAttr, "tckCtLocation"))
			newAttr = newAttr.replace("tckCtLocation", "TCkCtLocation");
		
		return newAttr;
	}
}
