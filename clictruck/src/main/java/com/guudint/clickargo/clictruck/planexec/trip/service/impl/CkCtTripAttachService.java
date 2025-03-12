package com.guudint.clickargo.clictruck.planexec.trip.service.impl;

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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstTripAttachType;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
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

public class CkCtTripAttachService extends AbstractClickCargoEntityService<TCkCtTripAttach, String, CkCtTripAttach> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtTripAttachService.class);
	private static String AUDIT_TAG = "CK CT TRIP ATTACH";
	private static String TABLE_NAME = "T_CK_CT_TRIP_ATTACH";
	
	// Attributes
	/////////////
	@Autowired
	protected ICkSession ckSession;
	
	// Constructor
	//////////////
	public CkCtTripAttachService() {
		super("ckCtTripAttachDao", AUDIT_TAG, TCkCtTripAttach.class.getName(), TABLE_NAME);
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
	public CkCtTripAttach newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return new CkCtTripAttach();
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
	public CkCtTripAttach deleteById(String id, Principal principal)
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

			TCkCtTripAttach entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtTripAttach dto = dtoFromEntity(entity);
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
	public List<CkCtTripAttach> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripAttach dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkCtTripAttach o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTripAttach> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtTripAttach> dtos = entities.stream().map(x -> {
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
	public CkCtTripAttach findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTripAttach entity = dao.find(id);
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
	protected CkCtTripAttach dtoFromEntity(TCkCtTripAttach entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtTripAttach dto = new CkCtTripAttach(entity);
			
			Optional<TCkCtTrip> opCkCtTrip = Optional.ofNullable(entity.getTCkCtTrip());
			dto.setTCkCtTrip(opCkCtTrip.isPresent() ? new CkCtTrip(opCkCtTrip.get()) : null);

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
	protected TCkCtTripAttach entityFromDTO(CkCtTripAttach dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtTripAttach entity = new TCkCtTripAttach();
			entity = dto.toEntity(entity);
			// no deep copy from BeanUtils
			entity.setTCkCtTrip(
					null == dto.getTCkCtTrip() ? null : dto.getTCkCtTrip().toEntity(new TCkCtTrip()));

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
	protected String entityKeyFromDTO(CkCtTripAttach dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getAtId() ? null : dto.getAtId();
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
	protected CoreMstLocale getCoreMstLocale(CkCtTripAttach arg0)
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
	protected HashMap<String, Object> getParameters(CkCtTripAttach dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = ckSession.getPrincipal();
			
			if(principal == null)
				throw new ProcessingException("principal is null");

			if (StringUtils.isNotBlank(dto.getAtId()))
				parameters.put("atId", "%" + dto.getAtId() + "%");

			if (dto.getAtStatus() != null && Character.isAlphabetic(dto.getAtStatus()))
				parameters.put("atStatus", dto.getAtStatus());
			
			Optional<CkCtMstTripAttachType> opCkCtMstTripAttachType = Optional.ofNullable(dto.getTCkCtMstTripAttachType());
			if (opCkCtMstTripAttachType.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtMstTripAttachType.get().getAtypId()))
					parameters.put("atypId", opCkCtMstTripAttachType.get().getAtypId());

				if (StringUtils.isNotBlank(opCkCtMstTripAttachType.get().getAtypName()))
					parameters.put("atypName", "%" + opCkCtMstTripAttachType.get().getAtypName() + "%");
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
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#getWhereClause(java.lang.Object,
	 *      boolean)
	 *
	 */
	@Override
	protected String getWhereClause(CkCtTripAttach dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			
			Principal principal = ckSession.getPrincipal();
			if(principal == null)
				throw new ProcessingException("principal is null");
			
			StringBuffer searchStatement = new StringBuffer();
			
			if (StringUtils.isNotBlank(dto.getAtId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.atId LIKE :atId");
				wherePrinted = true;
			}
			
			if (null != dto.getAtStatus() && Character.isAlphabetic(dto.getAtStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.atStatus = :atStatus");
				wherePrinted = true;
			}

			Optional<CkCtMstTripAttachType> opCkCtMstTripAttachType = Optional.ofNullable(dto.getTCkCtMstTripAttachType());
			if (opCkCtMstTripAttachType.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtMstTripAttachType.get().getAtypId())) {
					searchStatement
							.append(getOperator(wherePrinted) + "o.TCkCtMstTripAttachType.atypId = :atypId");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opCkCtMstTripAttachType.get().getAtypName())) {
					searchStatement
							.append(getOperator(wherePrinted) + "o.TCkCtMstTripAttachType.atypName LIKE :atypName");
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
	 * @see
	 * com.vcc.camelone.common.service.entity.AbstractEntityService#initEnity(java.
	 * lang.Object)
	 *
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtTripAttach initEnity(TCkCtTripAttach entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkCtTrip());

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
	protected CkCtTripAttach preSaveUpdateDTO(TCkCtTripAttach storedEntity, CkCtTripAttach dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setAtUidCreate(storedEntity.getAtUidCreate());
			dto.setAtDtCreate(storedEntity.getAtDtCreate());

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
	protected void preSaveValidation(CkCtTripAttach arg0, Principal arg1)
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
	protected ServiceStatus preUpdateValidation(CkCtTripAttach arg0, Principal arg1)
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
	protected CkCtTripAttach setCoreMstLocale(CoreMstLocale arg0, CkCtTripAttach arg1)
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
	protected TCkCtTripAttach updateEntity(ACTION attriubte, TCkCtTripAttach entity, Principal principal, Date date)
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
				entity.setAtUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAtDtCreate(date);
				entity.setAtUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAtDtLupd(date);
				break;

			case MODIFY:
				entity.setAtUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAtDtLupd(date);
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
	protected TCkCtTripAttach updateEntityStatus(TCkCtTripAttach entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setAtStatus(status);
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
	protected CkCtTripAttach whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripAttach dto = new CkCtTripAttach();
			CkCtMstTripAttachType ckCtMstTripAttachType = new CkCtMstTripAttachType();
			
			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("atId"))
					dto.setAtId(opValue.get());				
				if (entityWhere.getAttribute().equalsIgnoreCase("atStatus"))
					dto.setAtStatus(opValue.get().charAt(0));

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtMstTripAttachType.atypId"))
					ckCtMstTripAttachType.setAtypId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtMstTripAttachType.atypName"))
					ckCtMstTripAttachType.setAtypName(opValue.get());
				
			}

			dto.setTCkCtMstTripAttachType(ckCtMstTripAttachType);
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
    private EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) {
        if (orderBy == null) {
            return null;
        }
        if (StringUtils.isEmpty(orderBy.getAttribute())) {
            return null;
        }
        String newAttr = orderBy.getAttribute();
        newAttr = newAttr.replaceAll("tckCtMstTripAttachType", "TCkCtMstTripAttachType").
        		replaceAll("tckCtTrip", "TCkCtTrip");
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
		if (StringUtils.contains(newAttr, "tckCtTrip"))
			newAttr = newAttr.replace("tckCtTrip", "TCkCtTrip");
		
		return newAttr;
	}
	
	@Transactional
	public CkCtTripAttach findByTripId(String id) {
		CkCtTripAttach ckCtTripDo = new CkCtTripAttach();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("trId", id);
			parameters.put("doStatus", RecordStatus.ACTIVE.getCode());

			String hql = "FROM TCkCtTripAttach o WHERE o.TCkCtTrip.trId = :trId AND o.doStatus = :doStatus";
			List<TCkCtTripAttach> attach = dao.getByQuery(hql, parameters);
			if (null != attach && attach.size() > 0)
				ckCtTripDo = new CkCtTripAttach(attach.get(0));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckCtTripDo;
	}
	
	
	
}
