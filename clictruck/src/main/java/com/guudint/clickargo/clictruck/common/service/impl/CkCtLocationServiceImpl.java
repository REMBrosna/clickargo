/**
 *
 * Copyright(c) vCargo Cloud Pte Ltd, All Rights Reserved.
 * Developed by vCargo Cloud Pte Ltd
 * PROJECT : ClicTruck
 * NAME: CkCtLocationService
 * DESC: CkCtLocation Service
 * HISTORY: 2023/02/16/Adenny - first cut
 * 			
 **/
package com.guudint.clickargo.clictruck.common.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant;
import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.common.service.CkCtLocationService;
import com.guudint.clickargo.clictruck.common.validator.LocationValidator;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstLocationType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstLocationType;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.GoogleApiUtil;
import com.guudint.clickargo.clictruck.track.service.TrackTraceCoordinateService;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.RecordStatusNew;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.util.email.SysParam;

public class CkCtLocationServiceImpl extends AbstractClickCargoEntityService<TCkCtLocation, String, CkCtLocation>
		implements ICkConstant, CkCtLocationService {

	private static Logger LOG = Logger.getLogger(CkCtLocationServiceImpl.class);

	private static final String CLICTRUCK_DELIVERY_API_CALL = "CLICTRUCK_DELIVERY_API_CALL";
	
	@Autowired
	GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	TrackTraceCoordinateService coordinateService;

	@Autowired
	SysParam sysParam;
	
	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	private LocationValidator locationValidator;

	public CkCtLocationServiceImpl() {
		super(CkCtLocationConstant.Table.NAME_DAO, CkCtLocationConstant.Prefix.AUDIT_TAG,
				CkCtLocationConstant.Table.NAME_ENTITY, CkCtLocationConstant.Table.NAME);
	}

	@Override
	public CkCtLocation deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.debug("deleteById");

		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		return updateStatus(id, "delete");
	}

	@Override
	public List<CkCtLocation> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtLocation ckCtLocation = whereDto(filterRequest);
		if (ckCtLocation == null) {
			throw new ProcessingException("whereDto null");
		}
		filterRequest.setTotalRecords(countByAnd(ckCtLocation));
		List<CkCtLocation> ckCtLocations = new ArrayList<>();
		try {
			String orderByClause = formatOrderBy(filterRequest.getOrderBy().toString());
			List<TCkCtLocation> tCkCtLocations = findEntitiesByAnd(ckCtLocation, "from TCkCtLocation o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtLocation tCkCtLocation : tCkCtLocations) {
				CkCtLocation dto = dtoFromEntity(tCkCtLocation);
				if (dto != null) {
					ckCtLocations.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtLocations;
	}

	@Override
	public CkCtLocation findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtLocation entity = dao.find(id);
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
	protected CkCtLocation dtoFromEntity(TCkCtLocation tCkCtLocation) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtLocation == null) {
			throw new ParameterException("param entity null");
		}
		CkCtLocation ckCtLocation = new CkCtLocation(tCkCtLocation);
		if (tCkCtLocation.getTCoreAccn() != null) {
			ckCtLocation.setTCoreAccn(new CoreAccn(tCkCtLocation.getTCoreAccn()));
		}
		if (tCkCtLocation.getTCkCtMstLocationType() != null) {
			ckCtLocation.setTCkCtMstLocationType(new CkCtMstLocationType(tCkCtLocation.getTCkCtMstLocationType()));
		}
		return ckCtLocation;
	}

	@Override
	protected TCkCtLocation entityFromDTO(CkCtLocation ckCtLocation) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtLocation == null) {
			throw new ParameterException("param entity null");
		}
		TCkCtLocation tCkCtLocation = ckCtLocation.toEntity(new TCkCtLocation());
		if (ckCtLocation.getTCoreAccn() != null) {
			tCkCtLocation.setTCoreAccn(ckCtLocation.getTCoreAccn().toEntity(new TCoreAccn()));
		}
		if (ckCtLocation.getTCkCtMstLocationType() != null) {
			tCkCtLocation.setTCkCtMstLocationType(
					ckCtLocation.getTCkCtMstLocationType().toEntity(new TCkCtMstLocationType()));
		}
		return tCkCtLocation;
	}

	@Override
	protected String entityKeyFromDTO(CkCtLocation ckCtLocation) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtLocation == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtLocation.getLocId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtLocation ckCtLocation)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckCtLocation == null) {
			throw new ParameterException("dto param null");
		}
		if (ckCtLocation.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return ckCtLocation.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtLocation ckCtLocation)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (ckCtLocation == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();
		if (ckCtLocation.getTCoreAccn() != null) {
			if (ckCtLocation.getTCoreAccn().getAccnId() != null) {
				parameters.put(CkCtLocationConstant.ColumnParam.LOC_COMPANY, ckCtLocation.getTCoreAccn().getAccnId());
			}
		}
		if (StringUtils.isNotBlank(ckCtLocation.getLocName())) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_NAME, "%" + ckCtLocation.getLocName() + "%");
		}
		if (ckCtLocation.getTCkCtMstLocationType() != null) {
			if (StringUtils.isNotBlank(ckCtLocation.getTCkCtMstLocationType().getLctyId())) {
				parameters.put(CkCtLocationConstant.ColumnParam.LOC_TYPE,
						ckCtLocation.getTCkCtMstLocationType().getLctyId());
			}
		}
		if (StringUtils.isNotBlank(ckCtLocation.getLocId())) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_ID, ckCtLocation.getLocId());
		}
		if (StringUtils.isNotBlank(ckCtLocation.getLocAddress())) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_ADDRESS, "%" + ckCtLocation.getLocAddress() + "%");
		}
		if (StringUtils.isNotBlank(ckCtLocation.getLocRemarks())) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_REMARKS, "%" + ckCtLocation.getLocRemarks() + "%");
		}
		if (ckCtLocation.getLocDtStart() != null) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_DT_START, sdf.format(ckCtLocation.getLocDtStart()));
		}
		if (ckCtLocation.getLocDtEnd() != null) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_DT_END, sdf.format(ckCtLocation.getLocDtEnd()));
		}
		if (ckCtLocation.getLocDtCreate() != null) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_DT_CREATE, sdf.format(ckCtLocation.getLocDtCreate()));
		}
		if (ckCtLocation.getLocDtLupd() != null) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_DT_LUPD, sdf.format(ckCtLocation.getLocDtLupd()));
		}
		if (ckCtLocation.getLocStatus() == null) {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_STATUS, RecordStatusNew.DELETE.getCode());
		} else {
			parameters.put(CkCtLocationConstant.ColumnParam.LOC_STATUS, ckCtLocation.getLocStatus());
		}

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtLocation ckCtLocation, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		String EQUAL = " = :", CONTAIN = " like :", NOT_EQUAL = " != :";
		if (ckCtLocation == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();
		if (ckCtLocation.getTCoreAccn() != null) {
			if (ckCtLocation.getTCoreAccn().getAccnId() != null) {
				condition.append(getOperator(wherePrinted) + CkCtLocationConstant.Column.LOC_COMPANY + EQUAL
						+ CkCtLocationConstant.ColumnParam.LOC_COMPANY);
				wherePrinted = true;
			}
		}
		if (StringUtils.isNotBlank(ckCtLocation.getLocName())) {
			condition.append(getOperator(wherePrinted) + CkCtLocationConstant.Column.LOC_NAME + CONTAIN
					+ CkCtLocationConstant.ColumnParam.LOC_NAME);
			wherePrinted = true;
		}
		if (ckCtLocation.getTCkCtMstLocationType() != null) {
			if (StringUtils.isNotBlank(ckCtLocation.getTCkCtMstLocationType().getLctyId())) {
				condition.append(getOperator(wherePrinted) + CkCtLocationConstant.Column.LOC_TYPE + EQUAL
						+ CkCtLocationConstant.ColumnParam.LOC_TYPE);
				wherePrinted = true;
			}
		}
		if (StringUtils.isNotBlank(ckCtLocation.getLocId())) {
			condition.append(getOperator(wherePrinted) + CkCtLocationConstant.Column.LOC_ID + EQUAL
					+ CkCtLocationConstant.ColumnParam.LOC_ID);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtLocation.getLocAddress())) {
			condition.append(getOperator(wherePrinted) + CkCtLocationConstant.Column.LOC_ADDRESS + CONTAIN
					+ CkCtLocationConstant.ColumnParam.LOC_ADDRESS);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtLocation.getLocRemarks())) {
			condition.append(getOperator(wherePrinted) + CkCtLocationConstant.Column.LOC_REMARKS + CONTAIN
					+ CkCtLocationConstant.ColumnParam.LOC_REMARKS);
			wherePrinted = true;
		}
		if (ckCtLocation.getLocDtStart() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtLocationConstant.Column.LOC_DT_START
					+ ",'" + DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtLocationConstant.ColumnParam.LOC_DT_START);
			wherePrinted = true;
		}
		if (ckCtLocation.getLocDtEnd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtLocationConstant.Column.LOC_DT_END + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtLocationConstant.ColumnParam.LOC_DT_END);
			wherePrinted = true;
		}
		if (ckCtLocation.getLocDtCreate() != null) {
			condition.append(
					getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtLocationConstant.Column.LOC_DT_CREATE + ",'"
							+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtLocationConstant.ColumnParam.LOC_DT_CREATE);
			wherePrinted = true;
		}
		if (ckCtLocation.getLocDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtLocationConstant.Column.LOC_DT_LUPD + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtLocationConstant.ColumnParam.LOC_DT_LUPD);
			wherePrinted = true;
		}
		if (ckCtLocation.getLocStatus() == null) {
			condition.append(getOperator(wherePrinted) + CkCtLocationConstant.Column.LOC_STATUS + NOT_EQUAL
					+ CkCtLocationConstant.ColumnParam.LOC_STATUS);
		} else {
			condition.append(getOperator(wherePrinted) + CkCtLocationConstant.Column.LOC_STATUS + EQUAL
					+ CkCtLocationConstant.ColumnParam.LOC_STATUS);
		}

		return condition.toString();
	}

	@Override
	protected TCkCtLocation initEnity(TCkCtLocation tCkCtLocation) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (null != tCkCtLocation) {
			Hibernate.initialize(tCkCtLocation.getTCkCtMstLocationType());
			Hibernate.initialize(tCkCtLocation.getTCoreAccn());
		}
		return tCkCtLocation;
	}

	@Override
	protected CkCtLocation preSaveUpdateDTO(TCkCtLocation storedEntity, CkCtLocation dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		if (null == storedEntity)
			throw new ParameterException("param storedEntity null");
		if (null == dto)
			throw new ParameterException("param dto null");

		dto.setLocUidCreate(storedEntity.getLocUidCreate());
		dto.setLocDtCreate(storedEntity.getLocDtCreate());
		return dto;
	}

	@Override
	protected void preSaveValidation(CkCtLocation arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtLocation arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtLocation setCoreMstLocale(CoreMstLocale arg0, CkCtLocation arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkCtLocation updateEntity(ACTION attribute, TCkCtLocation entity, Principal principal, Date date)
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
			switch (attribute) {
				case CREATE:
					entity.setLocUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
					entity.setLocDtCreate(date);
					entity.setLocDtLupd(date);
					entity.setLocUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);

					//
					this.updateGPS(entity);
					break;

				case MODIFY:
					entity.setLocDtLupd(date);
					entity.setLocUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);

					if (isUpdateGPS(entity)) {
						this.updateGPS(entity);
					}
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
	protected TCkCtLocation updateEntityStatus(TCkCtLocation entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (null == entity)
			throw new ParameterException("entity param null");

		entity.setLocStatus(status);
		return entity;
	}

	@Override
	protected CkCtLocation whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (null == filterRequest)
			throw new ParameterException("param filterRequest null");

		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtLocation ckCtLocation = new CkCtLocation();
		CkCtMstLocationType ckCtMstLocationType = new CkCtMstLocationType();
		CoreAccn coreAccn = new CoreAccn();
		ckCtLocation.setTCoreAccn(coreAccn);
		ckCtLocation.setTCkCtMstLocationType(ckCtMstLocationType);
		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}
			String attribute = "o." + entityWhere.getAttribute();
			if (CkCtLocationConstant.Column.LOC_COMPANY.equalsIgnoreCase(attribute)) {
				coreAccn.setAccnId(entityWhere.getValue());
			} else if (CkCtLocationConstant.Column.LOC_ID.equalsIgnoreCase(attribute)) {
				ckCtLocation.setLocId(entityWhere.getValue());
			} else if (CkCtLocationConstant.Column.LOC_ADDRESS.equalsIgnoreCase(attribute)) {
				ckCtLocation.setLocAddress(entityWhere.getValue());
			} else if (CkCtLocationConstant.Column.LOC_STATUS.equalsIgnoreCase(attribute)) {
				ckCtLocation
						.setLocStatus((entityWhere.getValue() == null) ? null : entityWhere.getValue().charAt(0));
			} else if (CkCtLocationConstant.Column.LOC_NAME.equalsIgnoreCase(attribute)) {
				ckCtLocation.setLocName(entityWhere.getValue());
			} else if (CkCtLocationConstant.Column.LOC_TYPE.equalsIgnoreCase(attribute)) {
				ckCtMstLocationType.setLctyId(entityWhere.getValue());
			} else if (CkCtLocationConstant.Column.LOC_REMARKS.equalsIgnoreCase(attribute)) {
				ckCtLocation.setLocRemarks(entityWhere.getValue());
			} else if (CkCtLocationConstant.Column.LOC_DT_START.equalsIgnoreCase(attribute)) {
				try {
					ckCtLocation.setLocDtStart(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtLocationConstant.Column.LOC_DT_END.equalsIgnoreCase(attribute)) {
				try {
					ckCtLocation.setLocDtEnd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtLocationConstant.Column.LOC_DT_CREATE.equalsIgnoreCase(attribute)) {
				try {
					ckCtLocation.setLocDtCreate(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtLocationConstant.Column.LOC_DT_LUPD.equalsIgnoreCase(attribute)) {
				try {
					ckCtLocation.setLocDtLupd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			}
		}

		return ckCtLocation;
	}

	private String formatOrderBy(String attribute) throws Exception {

		String newAttr = attribute;
		if (StringUtils.contains(newAttr, "tckCtMstLocationType"))
			newAttr = newAttr.replace("tckCtMstLocationType", "TCkCtMstLocationType");

		if (StringUtils.contains(newAttr, "tcoreAccn"))
			newAttr = newAttr.replace("tcoreAccn", "TCoreAccn");

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
	public CkCtLocation updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtLocation ckCtLocation = findById(id);
		if (ckCtLocation == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			ckCtLocation.setLocStatus(RecordStatusNew.ACTIVE.getCode());
		} else if ("deactive".equals(status)) {
			ckCtLocation.setLocStatus(RecordStatusNew.DEACTIVE.getCode());
		} else if ("delete".equals(status)) {
			ckCtLocation.setLocStatus(RecordStatusNew.DELETE.getCode());
		}
		update(ckCtLocation, principal);
		return ckCtLocation;
	}

	private boolean isUpdateGPS(TCkCtLocation entity) {

		try {
			if (!StringUtils.isBlank(entity.getLocAddress())) {

				TCkCtLocation loc = dao.find(entity.getLocId());
				if (!entity.getLocAddress().equalsIgnoreCase(loc.getLocAddress())) {
					// not equal;
					return true;
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
			e.printStackTrace();
		}

		return false;
	}

	private void updateGPS(TCkCtLocation entity) throws Exception {
		
		String address = entity.getLocAddress();
		
		if(StringUtils.isBlank(address)) {
			address = entity.getLocName();
		}

		if(!StringUtils.isBlank(address)) {
			
			String apiCall = this.getSysParam(CLICTRUCK_DELIVERY_API_CALL);
			String gps = "";
			
			if (null == apiCall || StringUtils.equals(apiCall, "N")) {
				LOG.info("API Call is set to N, use testing.");
				gps = this.fetchCoordinateForTesting(address);
			} else {
				gps = coordinateService.fetchCoordinate(address);
			}

			LOG.info("entity.getLocAddress():" + address + "  GPS: " + gps);
			if (!StringUtils.isBlank(gps)) {
				entity.setLocGps(gps);
			}
		}
		
	}

	@Override
	public CkCtLocation newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtLocation ckCtLocation = new CkCtLocation();
		ckCtLocation.setTCoreAccn(principal.getCoreAccn());
		DateUtil dateUtil = new DateUtil(new Date());
		ckCtLocation.setLocDtCreate(dateUtil.toDate(dateUtil.getDateOnly()));
		ckCtLocation.setLocDtEnd(dateUtil.getDefaultEndDate());
		return ckCtLocation;
	}

	@Override
	public CkCtLocation add(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (ckCtLocation == null) {
			throw new ParameterException("param dto null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		ckCtLocation.setTCoreAccn(principal.getCoreAccn());
		ckCtLocation.setLocId(CkUtil.generateId(CkCtLocationConstant.Prefix.PREFIX_CK_CT_LOCATION));
		ckCtLocation.setLocStatus(RecordStatus.ACTIVE.getCode());
		ckCtLocation.setTCkCtMstLocationType(ckCtLocation.getTCkCtMstLocationType());
		try {
			return super.add(ckCtLocation, principal);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}

	@Override
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtLocation ckCtLocation = (CkCtLocation) object;
		List<ValidationError> validationErrors = locationValidator.validateCreate(ckCtLocation, principal);
		if(!validationErrors.isEmpty()){
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return add(ckCtLocation, principal);
		}
	}

	@Override
	public Object updateObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtLocation ckCtLocation = (CkCtLocation) object;
		List<ValidationError> validationErrors = locationValidator.validateUpdate(ckCtLocation, principal);
		if(!validationErrors.isEmpty()){
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return super.update(ckCtLocation, principal);
		}
	}
	
	/**
	 * This method is used for testing, if there's no baseUrl and apiKey configured in sysparam
	 * @param address
	 * @return
	 */
	public String fetchCoordinateForTesting(String address) {
		LOG.info("fetchCoordinateForTesting " + address);

		Random rand = new Random();
		List<String> geoCodes = Arrays.asList(GoogleApiUtil.GEOCODE_1, GoogleApiUtil.GEOCODE_2, GoogleApiUtil.GEOCODE_3,
				GoogleApiUtil.GEOCODE_4, GoogleApiUtil.GEOCODE_5, GoogleApiUtil.GEOCODE_6, GoogleApiUtil.GEOCODE_7,
				GoogleApiUtil.GEOCODE_8, GoogleApiUtil.GEOCODE_9, GoogleApiUtil.GEOCODE_0); 
	    String geCodeResp = "";
	    for (int i = 0; i < geoCodes.size(); i++) {
	        int randomIndex = rand.nextInt(geoCodes.size());
	        geCodeResp = geoCodes.get(randomIndex);
	    }
		
		JSONObject json = new JSONObject(geCodeResp.toString());

		LOG.info(json.toString());

		if (json.getString("status").equals("OK")) {
			JSONArray results = json.getJSONArray("results");
			JSONObject result = results.getJSONObject(0);
			JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
			double latitude = location.getDouble("lat");
			double longitude = location.getDouble("lng");

			double[] latLong = new double[] { latitude, longitude };

			return Arrays.toString(latLong);
		} else {
			LOG.error("Fail to find geocode: " + address);
		}
		return null;
	}

	/**
	 * This method gets 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}

		return null;
	}
}
