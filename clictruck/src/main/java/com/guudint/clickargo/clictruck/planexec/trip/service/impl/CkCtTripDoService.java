package com.guudint.clickargo.clictruck.planexec.trip.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripDo;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
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

public class CkCtTripDoService extends AbstractClickCargoEntityService<TCkCtTripDo, String, CkCtTripDo> {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtTripDoService.class);
	private static String AUDIT_TAG = "CK CT TRIP DO";
	private static String TABLE_NAME = "T_CK_CT_TRIP_DO";
	
	// Attributes
	/////////////
	@Autowired
	protected ICkSession ckSession;
	
	// Constructor
	//////////////
	public CkCtTripDoService() {
		super("ckCtTripDoDao", AUDIT_TAG, TCkCtTripDo.class.getName(), TABLE_NAME);
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
	public CkCtTripDo newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return new CkCtTripDo();
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
	public CkCtTripDo deleteById(String id, Principal principal)
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

			TCkCtTripDo entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtTripDo dto = dtoFromEntity(entity);
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
	public List<CkCtTripDo> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripDo dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkCtTripDo o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTripDo> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtTripDo> dtos = entities.stream().map(x -> {
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
	public CkCtTripDo findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTripDo entity = dao.find(id);
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
	protected CkCtTripDo dtoFromEntity(TCkCtTripDo entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtTripDo dto = new CkCtTripDo(entity);
			
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
	protected TCkCtTripDo entityFromDTO(CkCtTripDo dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtTripDo entity = new TCkCtTripDo();
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
	protected String entityKeyFromDTO(CkCtTripDo dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getDoId() ? null : dto.getDoId();
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
	protected CoreMstLocale getCoreMstLocale(CkCtTripDo arg0)
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
	protected HashMap<String, Object> getParameters(CkCtTripDo dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = ckSession.getPrincipal();
			
			if(principal == null)
				throw new ProcessingException("principal is null");

			if (StringUtils.isNotBlank(dto.getDoId()))
				parameters.put("doId", "%" + dto.getDoId() + "%");

			if (dto.getDoStatus() != null && Character.isAlphabetic(dto.getDoStatus()))
				parameters.put("doStatus", dto.getDoStatus());
			
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
	protected String getWhereClause(CkCtTripDo dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			
			Principal principal = ckSession.getPrincipal();
			if(principal == null)
				throw new ProcessingException("principal is null");
			
			StringBuffer searchStatement = new StringBuffer();
			
			if (StringUtils.isNotBlank(dto.getDoId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.doId LIKE :doId");
				wherePrinted = true;
			}
			
			if (null != dto.getDoStatus() && Character.isAlphabetic(dto.getDoStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.doStatus = :doStatus");
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
	protected TCkCtTripDo initEnity(TCkCtTripDo entity) throws ParameterException, ProcessingException {
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
	protected CkCtTripDo preSaveUpdateDTO(TCkCtTripDo storedEntity, CkCtTripDo dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setDoUidCreate(storedEntity.getDoUidCreate());
			dto.setDoDtCreate(storedEntity.getDoDtCreate());

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
	protected void preSaveValidation(CkCtTripDo arg0, Principal arg1)
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
	protected ServiceStatus preUpdateValidation(CkCtTripDo arg0, Principal arg1)
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
	protected CkCtTripDo setCoreMstLocale(CoreMstLocale arg0, CkCtTripDo arg1)
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
	protected TCkCtTripDo updateEntity(ACTION attriubte, TCkCtTripDo entity, Principal principal, Date date)
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
				entity.setDoUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setDoDtCreate(date);
				entity.setDoUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setDoDtLupd(date);
				break;

			case MODIFY:
				entity.setDoUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setDoDtLupd(date);
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
	protected TCkCtTripDo updateEntityStatus(TCkCtTripDo entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setDoStatus(status);
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
	protected CkCtTripDo whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripDo dto = new CkCtTripDo();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("doId"))
					dto.setDoId(opValue.get());				
				if (entityWhere.getAttribute().equalsIgnoreCase("doStatus"))
					dto.setDoStatus(opValue.get().charAt(0));

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
	public CkCtTripDo findByTripId(String id) {
		CkCtTripDo ckCtTripDo = new CkCtTripDo();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("trId", id);
			parameters.put("doStatus", RecordStatus.ACTIVE.getCode());

			String hql = "FROM TCkCtTripDo o WHERE o.TCkCtTrip.trId = :trId AND o.doStatus = :doStatus";
			List<TCkCtTripDo> attach = dao.getByQuery(hql, parameters);
			if (null != attach && attach.size() > 0)
				ckCtTripDo = new CkCtTripDo(attach.get(0));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckCtTripDo;
	}
	
	@Transactional
	public CkCtTripDo findByDoNo(String doNo) {
		CkCtTripDo ckCtTripDo = null;
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("doNo", doNo);
			parameters.put("doStatus", RecordStatus.ACTIVE.getCode());

			String hql = "FROM TCkCtTripDo o WHERE o.doNo = :doNo AND o.doStatus = :doStatus";
			List<TCkCtTripDo> attach = dao.getByQuery(hql, parameters);
			if (null != attach && attach.size() > 0)
				ckCtTripDo = new CkCtTripDo(attach.get(0));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckCtTripDo;
	}
	
	/**
	 * CT-260 - File type validation when upload pickup, dropoff images and DO attachment
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	public boolean isMimeTypeAllowed(byte[] file, boolean isImageOnly, boolean enableZip) throws Exception {

		if (file == null)
			throw new ParameterException("param file is null");

		List<String> mimeTypes = new ArrayList<String>();
		mimeTypes.add("image/jpeg");
		mimeTypes.add("image/png");
		if (!isImageOnly) {
			mimeTypes.add("application/pdf");
		}
		if( enableZip ) {
			mimeTypes.add("application/zip");
		}
		
		TikaConfig tika = new TikaConfig();
		InputStream is = new ByteArrayInputStream(file);
		MediaType mimeType = tika.getDetector().detect(TikaInputStream.get(is), new Metadata());
		
		return mimeTypes != null && mimeTypes.contains(mimeType.toString());
	}
	
}
