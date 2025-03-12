package com.guudint.clickargo.clictruck.planexec.trip.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstUomSize;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstUomVolume;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstUomWeight;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomSize;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomVolume;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomWeight;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstCargoType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstCargoType;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoMm;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoMm;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.util.PrincipalUtilService;

public class CkCtTripCargoMmService extends AbstractClickCargoEntityService<TCkCtTripCargoMm, String, CkCtTripCargoMm> {

	// Static Attributes
	////////////////////
	private static final Logger LOG = Logger.getLogger(CkCtTripCargoFmService.class);
	private static final String AUDIT_TAG = "CK CT TRIP MM";
	private static final String TABLE_NAME = "T_CK_CT_TRIP_CARGO_MM";

	@Autowired
	protected PrincipalUtilService principalUtilService;

	public CkCtTripCargoMmService() {
		super("ckCtTripCargoMmDao", AUDIT_TAG, TCkCtTripCargoMm.class.getName(), TABLE_NAME);
	}

	@Override
	public CkCtTripCargoMm newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return new CkCtTripCargoMm();

	}

	@Transactional
	@Override
	public CkCtTripCargoMm findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTripCargoMm entity = dao.find(id);
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

	@Transactional
	@Override
	public CkCtTripCargoMm deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
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

			TCkCtTripCargoMm entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtTripCargoMm dto = dtoFromEntity(entity);
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

	@Transactional
	@Override
	public List<CkCtTripCargoMm> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		
		if (null == filterRequest) {
			throw new ParameterException("param filterRequest null");
		}

		CkCtTripCargoMm dto = this.whereDto(filterRequest);
		
		if (null == dto) {
			throw new ProcessingException("whereDto null");
		}
		
		filterRequest.setTotalRecords(super.countByAnd(dto));
		
		List<CkCtTripCargoMm> ckCtTripCargoMm = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTripCargoMm> tCkCtTripCargoMms = findEntitiesByAnd(dto,
	                    "FROM TCkCtTripCargoMm o ", orderByClause,
	                    filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtTripCargoMm tCkCtTripCargoMm : tCkCtTripCargoMms) {
				CkCtTripCargoMm dtoCkCtTripCargoMm = dtoFromEntity(tCkCtTripCargoMm);
                if (dtoCkCtTripCargoMm != null) {
                	ckCtTripCargoMm.add(dtoCkCtTripCargoMm);
                }
            }
		 } catch (Exception e) {
	            LOG.error("filterBy", e);
		 }
		return ckCtTripCargoMm;
			
	}

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
	
	protected String formatOrderBy(String attribute) throws Exception {

		String newAttr = attribute;
		
		if (StringUtils.contains(newAttr, "tckCtTrip"))
			newAttr = newAttr.replace("tckCtTrip", "TCkCtTrip");
		
		if (StringUtils.contains(newAttr, "tckCtMstCargoType"))
			newAttr = newAttr.replace("tckCtMstCargoType", "TCkCtMstCargoType");
		
		return newAttr;
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public TCkCtTripCargoMm initEnity(TCkCtTripCargoMm entity) throws ParameterException, ProcessingException {
		if (entity != null) {
			Hibernate.initialize(entity.getTCkCtMstCargoType());
			Hibernate.initialize(entity.getTCkCtTrip());
		}

		return entity;
	}

	@Override
	protected TCkCtTripCargoMm entityFromDTO(CkCtTripCargoMm dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtTripCargoMm entity = new TCkCtTripCargoMm();
			entity = dto.toEntity(entity);
			// no deep copy from BeanUtils

			entity.setTCkCtMstCargoType(null == dto.getTCkCtMstCargoType() ? null
					: dto.getTCkCtMstCargoType().toEntity(new TCkCtMstCargoType()));
			entity.setTCkCtTrip(null == dto.getTCkCtTrip() ? null : dto.getTCkCtTrip().toEntity(new TCkCtTrip()));

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
	protected CkCtTripCargoMm dtoFromEntity(TCkCtTripCargoMm entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtTripCargoMm dto = new CkCtTripCargoMm(entity);

			Optional<TCkCtMstCargoType> opCkCtMstCargoType = Optional.ofNullable(entity.getTCkCtMstCargoType());
			dto.setTCkCtMstCargoType(
					opCkCtMstCargoType.isPresent() ? new CkCtMstCargoType(opCkCtMstCargoType.get()) : null);

			Optional<TCkCtTrip> opCkCtTrip = Optional.ofNullable(entity.getTCkCtTrip());
			dto.setTCkCtTrip(opCkCtTrip.isPresent() ? new CkCtTrip(opCkCtTrip.get()) : null);

			Optional<TCkCtMstUomVolume> optionalTCkCtMstUomVolume = Optional.ofNullable(entity.getTCkCtMstUomVolume());
			dto.setTCkCtMstUomVolume(optionalTCkCtMstUomVolume.isPresent() ?
					optionalTCkCtMstUomVolume.map(CkCtMstUomVolume::new).orElse(null) :
					null);

			Optional<TCkCtMstUomSize> optionalTCkCtMstUomSize = Optional.ofNullable(entity.getTCkCtMstUomSize());
			dto.setTCkCtMstUomSize(optionalTCkCtMstUomSize.isPresent() ?
					optionalTCkCtMstUomSize.map(CkCtMstUomSize::new).orElse(null) :
					null);

			Optional<TCkCtMstUomWeight> optionalTCkCtMstUomWeight = Optional.ofNullable(entity.getTCkCtMstUomWeight());
			dto.setTCkCtMstUomWeight(optionalTCkCtMstUomWeight.isPresent() ?
					optionalTCkCtMstUomWeight.map(CkCtMstUomWeight::new).orElse(null) :
					null);

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
	protected String entityKeyFromDTO(CkCtTripCargoMm dto) throws ParameterException, ProcessingException {
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

	@Override
	protected TCkCtTripCargoMm updateEntity(ACTION attriubte, TCkCtTripCargoMm entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		LOG.info("updateEntity");
		if(entity == null) {
			throw new ParameterException("param entity null");
		}
		if(principal == null) {
			throw new ParameterException("param principal null");
		}
		Optional<String> opUserId = Optional.of(principal.getUserId());
		switch(attriubte) {
			case CREATE:
				entity.setCgUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				entity.setCgDtCreate(date);
				entity.setCgDtCreate(date);
				entity.setCgUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				break;
				
			case MODIFY:
				entity.setCgDtCreate(date);
				entity.setCgUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				break;
				
			default:
				break;
		}
		return entity;
	}

	@Override
	protected TCkCtTripCargoMm updateEntityStatus(TCkCtTripCargoMm entity, char status)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		LOG.debug("updateEntityStatus");
		
		try {
			if (null == entity)
				throw new ParameterException("entity param null");
	
			entity.setCgStatus(status);
			return entity;
			
		} catch (ParameterException e) {
			// TODO: handle exception
			LOG.error("updateEntity", e);
			throw e;
		} catch (Exception e) {
			LOG.error("updateEntityStatus", e);
			throw new ProcessingException(e);
		}
	}
	

	@Override
	protected CkCtTripCargoMm preSaveUpdateDTO(TCkCtTripCargoMm storedEntity, CkCtTripCargoMm dto)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preSaveValidation(CkCtTripCargoMm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtTripCargoMm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtTripCargoMm dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal is null");

			StringBuffer searchStatement = new StringBuffer();
			
			searchStatement.append(getOperator(wherePrinted) + "o.cgStatus= 'A' ");
			wherePrinted = true;

			Optional<CkCtTrip> opCkTrip = Optional.ofNullable(dto.getTCkCtTrip());
			if (opCkTrip.isPresent()) {
				if (StringUtils.isNotBlank(opCkTrip.get().getTrId())) {
					searchStatement.append(getOperator(wherePrinted)).append("o.TCkCtTrip.trId = :trId");
					wherePrinted = true;
				}
			}

			Optional<CkCtMstCargoType> opMstCargoType = Optional.ofNullable(dto.getTCkCtMstCargoType());
			if (opMstCargoType.isPresent()) {
				if (StringUtils.isNotBlank(opMstCargoType.get().getCrtypId())) {
					searchStatement.append(getOperator(wherePrinted)).append("o.TCkCtMstCargoType.crtypId = :crtypId");
					wherePrinted = true;
				}
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

	@Override
	protected HashMap<String, Object> getParameters(CkCtTripCargoMm dto)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal is null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Optional<CkCtTrip> opCkTrip = Optional.ofNullable(dto.getTCkCtTrip());
			if (opCkTrip.isPresent()) {
				if (StringUtils.isNotBlank(opCkTrip.get().getTrId())) {
					parameters.put("trId", opCkTrip.get().getTrId());
				}
			}

			Optional<CkCtMstCargoType> opMstCargoType = Optional.ofNullable(dto.getTCkCtMstCargoType());
			if (opMstCargoType.isPresent()) {
				if (StringUtils.isNotBlank(opMstCargoType.get().getCrtypId())) {
					parameters.put("crtypId", opMstCargoType.get().getCrtypId());
				}
			}

			if (null != dto.getCgStatus() && Character.isAlphabetic(dto.getCgStatus())) {
				parameters.put("cgStatus", dto.getCgStatus());
			}

			return parameters;
		} catch (ParameterException ex) {
			LOG.error("getWhereClause", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getWhereClause", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtTripCargoMm whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripCargoMm dto = new CkCtTripCargoMm();
			CkCtMstCargoType ctype = new CkCtMstCargoType();
			CkCtTrip trip = new CkCtTrip();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtTrip.trId"))
					trip.setTrId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtMstCargoType.crtypId"))
					ctype.setCrtypId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtMstCargoType.crtypName"))
					ctype.setCrtypName(opValue.get());

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

	@Transactional
	public List<CkCtTripCargoMm> findTripCargoFmmsByTripId(String id) {
		List<CkCtTripCargoMm> mmTripList = new ArrayList<CkCtTripCargoMm>();
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("trId", id);
			parameters.put("cgStatus", RecordStatus.ACTIVE.getCode());

			String hql = "FROM TCkCtTripCargoMm o WHERE o.TCkCtTrip.trId = :trId AND o.cgStatus = :cgStatus";
			List<TCkCtTripCargoMm> ckCtTrips = dao.getByQuery(hql, parameters);

			ckCtTrips.forEach(inst -> {
				try {
					mmTripList.add(this.dtoFromEntity(inst));
				} catch (ParameterException | ProcessingException ex) {
					ex.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mmTripList;
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtTripCargoMm dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTripCargoMm setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtTripCargoMm dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

}
