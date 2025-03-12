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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDoAttach;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
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

public class CkCtTripDoAttachService extends AbstractClickCargoEntityService<TCkCtTripDoAttach, String, CkCtTripDoAttach> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtTripDoAttachService.class);
	private static String AUDIT_TAG = "CK CT TRIP DO ATTACH";
	private static String TABLE_NAME = "T_CK_CT_TRIP_DO_ATTACH";
	
	// Attributes
	/////////////
	@Autowired
	protected ICkSession ckSession;
	
	@Autowired
	private CkCtTripDoService ckCtTripDoService;
	
	// Constructor
	//////////////
	public CkCtTripDoAttachService() {
		super("ckCtTripDoAttachDao", AUDIT_TAG, TCkCtTripDoAttach.class.getName(), TABLE_NAME);
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
	public CkCtTripDoAttach newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return new CkCtTripDoAttach();
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
	public CkCtTripDoAttach deleteById(String id, Principal principal)
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

			TCkCtTripDoAttach entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtTripDoAttach dto = dtoFromEntity(entity);
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
	public List<CkCtTripDoAttach> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripDoAttach dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkCtTripDoAttach o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTripDoAttach> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtTripDoAttach> dtos = entities.stream().map(x -> {
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
	public CkCtTripDoAttach findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTripDoAttach entity = dao.find(id);
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
	protected CkCtTripDoAttach dtoFromEntity(TCkCtTripDoAttach entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtTripDoAttach dto = new CkCtTripDoAttach(entity);
			
			Optional<TCkCtTrip> opCkCtTrip = Optional.ofNullable(entity.getTCkCtTrip());
			dto.setTCkCtTrip(opCkCtTrip.isPresent() ? new CkCtTrip(opCkCtTrip.get()) : null);
			
			if (opCkCtTrip.isPresent()) {
				CkCtTripDo ckCtTripDo = ckCtTripDoService.findByTripId(opCkCtTrip.get().getTrId());
				dto.setCkCtTripDo(ckCtTripDo);
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

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.service.entity.AbstractEntityService#entityFromDTO(java.lang.Object)
	 *
	 */
	@Override
	protected TCkCtTripDoAttach entityFromDTO(CkCtTripDoAttach dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtTripDoAttach entity = new TCkCtTripDoAttach();
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
	protected String entityKeyFromDTO(CkCtTripDoAttach dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getDoaId() ? null : dto.getDoaId();
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
	protected CoreMstLocale getCoreMstLocale(CkCtTripDoAttach arg0)
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
	protected HashMap<String, Object> getParameters(CkCtTripDoAttach dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = ckSession.getPrincipal();
			
			if(principal == null)
				throw new ProcessingException("principal is null");

			if (StringUtils.isNotBlank(dto.getDoaId()))
				parameters.put("doaId", "%" + dto.getDoaId() + "%");

			if (dto.getDoaStatus() != null && Character.isAlphabetic(dto.getDoaStatus()))
				parameters.put("doaStatus", dto.getDoaStatus());
			
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
	protected String getWhereClause(CkCtTripDoAttach dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			
			Principal principal = ckSession.getPrincipal();
			if(principal == null)
				throw new ProcessingException("principal is null");
			
			StringBuffer searchStatement = new StringBuffer();
			
			if (StringUtils.isNotBlank(dto.getDoaId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.doaId LIKE :doaId");
				wherePrinted = true;
			}
			
			if (null != dto.getDoaStatus() && Character.isAlphabetic(dto.getDoaStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.doaStatus = :doaStatus");
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
	protected TCkCtTripDoAttach initEnity(TCkCtTripDoAttach entity) throws ParameterException, ProcessingException {
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
	protected CkCtTripDoAttach preSaveUpdateDTO(TCkCtTripDoAttach storedEntity, CkCtTripDoAttach dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setDoaUidCreate(storedEntity.getDoaUidCreate());
			dto.setDoaDtCreate(storedEntity.getDoaDtCreate());

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
	protected void preSaveValidation(CkCtTripDoAttach arg0, Principal arg1)
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
	protected ServiceStatus preUpdateValidation(CkCtTripDoAttach arg0, Principal arg1)
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
	protected CkCtTripDoAttach setCoreMstLocale(CoreMstLocale arg0, CkCtTripDoAttach arg1)
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
	protected TCkCtTripDoAttach updateEntity(ACTION attriubte, TCkCtTripDoAttach entity, Principal principal, Date date)
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
				entity.setDoaUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setDoaDtCreate(date);
				entity.setDoaUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setDoaDtLupd(date);
				break;

			case MODIFY:
				entity.setDoaUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setDoaDtLupd(date);
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
	protected TCkCtTripDoAttach updateEntityStatus(TCkCtTripDoAttach entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setDoaStatus(status);
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
	protected CkCtTripDoAttach whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripDoAttach dto = new CkCtTripDoAttach();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("doaId"))
					dto.setDoaId(opValue.get());				
				if (entityWhere.getAttribute().equalsIgnoreCase("doaStatus"))
					dto.setDoaStatus(opValue.get().charAt(0));

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
		if (StringUtils.contains(newAttr, "tckCtTrip"))
			newAttr = newAttr.replace("tckCtTrip", "TCkCtTrip");
		
		return newAttr;
	}
	
	@Transactional
	public CkCtTripDoAttach findByTripId(String id) {
		CkCtTripDoAttach ckCtTripDoAttach = new CkCtTripDoAttach();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("trId", id);
			parameters.put("doaStatus", RecordStatus.ACTIVE.getCode());

			String hql = "FROM TCkCtTripDoAttach o WHERE o.TCkCtTrip.trId = :trId AND o.doaStatus = :doaStatus";
			List<TCkCtTripDoAttach> attach = dao.getByQuery(hql, parameters);
			if (null != attach && attach.size() > 0)
				ckCtTripDoAttach = new CkCtTripDoAttach(attach.get(0));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckCtTripDoAttach;
	}
	
//	@Transactional
//	public CkCtTripDoAttach findByTripId(String id, boolean signed) {
//		CkCtTripDoAttach ckCtTripDoAttach = new CkCtTripDoAttach();
//		try {
//			Map<String, Object> parameters = new HashMap<>();
//			parameters.put("trId", id);
//			parameters.put("doaStatus", RecordStatus.ACTIVE.getCode());
//			StringBuilder hql = new StringBuilder("FROM TCkCtTripDoAttach o WHERE o.TCkCtTrip.trId = :trId AND o.doaStatus = :doaStatus ");
//			if (signed) {
//				hql.append(" ORDER BY o.doaDtCreate DESC");
//			} else {
//				hql.append(" ORDER BY o.doaDtCreate ASC");
//			}
//			List<TCkCtTripDoAttach> attach = dao.getByQuery(hql.toString(), parameters);
//			if (null != attach && attach.size() > 0)
//				ckCtTripDoAttach = new CkCtTripDoAttach(attach.get(0));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return ckCtTripDoAttach;
//	}
	
	@Transactional
	public CkCtTripDoAttach findByTripId(String id, boolean signed) {
		CkCtTripDoAttach ckCtTripDoAttach = new CkCtTripDoAttach();
		try {
			
			DetachedCriteria dc = DetachedCriteria.forClass(TCkCtTripDoAttach.class);
			dc.add(Restrictions.eq("TCkCtTrip.trId", id));
			dc.add(Restrictions.eq("doaStatus", RecordStatus.ACTIVE.getCode()));
			if (signed) {
				dc.addOrder(Order.desc("doaDtCreate"));
			} else {
				dc.addOrder(Order.asc("doaDtCreate"));
			}
			
			List<TCkCtTripDoAttach> attach = dao.getByCriteria(dc);
			if (null != attach && attach.size() > 0)
				ckCtTripDoAttach = new CkCtTripDoAttach(attach.get(0));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckCtTripDoAttach;
	}
}
