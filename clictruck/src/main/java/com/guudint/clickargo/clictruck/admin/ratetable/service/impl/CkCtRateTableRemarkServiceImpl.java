package com.guudint.clickargo.clictruck.admin.ratetable.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTableRemark;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTableRemark;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
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

public class CkCtRateTableRemarkServiceImpl
		extends AbstractClickCargoEntityService<TCkCtRateTableRemark, String, CkCtRateTableRemark> {

	public CkCtRateTableRemarkServiceImpl() {
		super("ckCtRateTableRemarkDao", "CKCT RATE TABLE REMARK", TCkCtRateTableRemark.class.getName(),
				"T_CK_CT_RATE_TABLE_REMARK");
	}

	private static Logger LOG = Logger.getLogger(CkCtRateTableRemarkServiceImpl.class);
	private static final String PREFIX_KEY = "CKCTRTR";

	@Override
	public CkCtRateTableRemark newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtRateTableRemark findById(String id)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtRateTableRemark tCkCtRateTableRemark = dao.find(id);
			if (tCkCtRateTableRemark == null) {
				throw new EntityNotFoundException("findById -> id:" + id);
			}
			initEnity(tCkCtRateTableRemark);
			return dtoFromEntity(tCkCtRateTableRemark);
		} catch (Exception e) {
			LOG.error("findById" + e);
		}
		return null;
	}

	@Override
	public CkCtRateTableRemark deleteById(String paramString, Principal paramPrincipal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtRateTableRemark> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtRateTableRemark ckCtRateTableRemark = whereDto(filterRequest);
		if (ckCtRateTableRemark == null) {
			throw new ProcessingException("whereDto null result");
		}

		filterRequest.setTotalRecords(countByAnd(ckCtRateTableRemark));
		List<CkCtRateTableRemark> ckCtRateTableRemarks = new ArrayList<>();
		try {
			String orderClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtRateTableRemark> tCkCtRateTableRemarks = findEntitiesByAnd(ckCtRateTableRemark,
					"from TCkCtRateTableRemark o ", orderClause, filterRequest.getDisplayLength(),
					filterRequest.getDisplayStart());
			for (TCkCtRateTableRemark tCkCtRateTableRemark : tCkCtRateTableRemarks) {
				CkCtRateTableRemark dto = dtoFromEntity(tCkCtRateTableRemark);
				if (dto != null) {
					ckCtRateTableRemarks.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtRateTableRemarks;
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

		return newAttr;
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return LOG;
	}

	@Override
	protected TCkCtRateTableRemark initEnity(TCkCtRateTableRemark entity)
			throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (null != entity) {
			Hibernate.initialize(entity.getTCkCtRateTable());
			Hibernate.initialize(entity.getTCkCtRateTable().getTCoreAccnByRtCompany());
			Hibernate.initialize(entity.getTCkCtRateTable().getTCoreAccnByRtCoFf());
			Hibernate.initialize(entity.getTCkCtRateTable().getTMstCurrency());
		}
		return entity;
	}

	@Override
	protected TCkCtRateTableRemark entityFromDTO(CkCtRateTableRemark dto)
			throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtRateTableRemark entity = new TCkCtRateTableRemark();
			entity = dto.toEntity(entity);

			Optional<CkCtRateTable> opRateTable = Optional.ofNullable(dto.getTCkCtRateTable());
			entity.setTCkCtRateTable(opRateTable.isPresent() ? opRateTable.get().toEntity(new TCkCtRateTable()) : null);

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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtRateTableRemark dtoFromEntity(TCkCtRateTableRemark entity)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtRateTableRemark dto = new CkCtRateTableRemark(entity);

			TCkCtRateTable trE = entity.getTCkCtRateTable();
			dto.setTCkCtRateTable(new CkCtRateTable(trE));

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
	protected String entityKeyFromDTO(CkCtRateTableRemark dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (null == dto)
			throw new ParameterException("dto param null");

		return dto.getRtrId();
	}

	@Override
	protected TCkCtRateTableRemark updateEntity(ACTION attribute, TCkCtRateTableRemark entity, Principal principal,
			Date date) throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (null == entity)
			throw new ParameterException("param entity null");
		if (null == principal)
			throw new ParameterException("param principal null");
		if (null == date)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (attribute) {
		case CREATE:
			entity.setRtrId(CkUtil.generateId(PREFIX_KEY));
			entity.setRtrUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			entity.setRtrStatus(RecordStatus.ACTIVE.getCode());
			entity.setRtrDtCreate(date);
			entity.setRtrDtLupd(date);
			entity.setRtrUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		default:
			break;
		}

		return entity;
	}

	@Override
	protected TCkCtRateTableRemark updateEntityStatus(TCkCtRateTableRemark paramE, char paramChar)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtRateTableRemark preSaveUpdateDTO(TCkCtRateTableRemark paramE, CkCtRateTableRemark paramD)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preSaveValidation(CkCtRateTableRemark paramD, Principal paramPrincipal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtRateTableRemark paramD, Principal paramPrincipal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtRateTableRemark dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (null == dto)
			throw new ParameterException("param dto null");

		StringBuffer searchStatement = new StringBuffer();

		if (Character.isAlphabetic(dto.getRtrType())) {
			searchStatement.append(getOperator(wherePrinted) + "o.rtrType" + EQUAL + "rtrType");
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(dto.getRtrComment())) {
			searchStatement.append(getOperator(wherePrinted) + "o.rtrComment" + CONTAIN + "rtrComment");
			wherePrinted = true;
		}
		Optional<Date> opRtrDtCreate = Optional.ofNullable(dto.getRtrDtCreate());
		if (opRtrDtCreate.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.rtrDtCreate" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "rtrDtCreate");
			wherePrinted = true;
		}

		Optional<CkCtRateTable> opTCkCtRateTable = Optional.ofNullable(dto.getTCkCtRateTable());
		if (opTCkCtRateTable.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "o.TCkCtRateTable.rtId" + EQUAL + "rtId");
			wherePrinted = true;
		}

		return searchStatement.toString();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtRateTableRemark dto)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		if (null == dto)
			throw new ParameterException("param dto null");

		SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		if (Character.isAlphabetic(dto.getRtrType())) {
			parameters.put("rtrType", dto.getRtrType());
		}
		if (StringUtils.isNotBlank(dto.getRtrComment())) {
			parameters.put("rtrComment", dto.getRtrComment());
		}
		Optional<Date> opRtrDtCreate = Optional.ofNullable(dto.getRtrDtCreate());
		if (opRtrDtCreate.isPresent() && null != opRtrDtCreate.get()) {
			parameters.put("rtrDtCreate", sdfDate.format(opRtrDtCreate.get()));
		}

		Optional<CkCtRateTable> opTCkCtRateTable = Optional.ofNullable(dto.getTCkCtRateTable());
		if (opTCkCtRateTable.isPresent() && null != opTCkCtRateTable.get().getRtId()) {
			parameters.put("rtId", opTCkCtRateTable.get().getRtId());
		}

		return parameters;
	}

	@Override
	protected CkCtRateTableRemark whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);

			CkCtRateTableRemark dto = new CkCtRateTableRemark();
			CkCtRateTable tCkCtRateTable = new CkCtRateTable();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				String attribute = "o." + entityWhere.getAttribute();
				if (attribute.equalsIgnoreCase("o.rtrType"))
					dto.setRtrType((opValue.get() == null) ? null : opValue.get().charAt(0));
				else if (attribute.equalsIgnoreCase("o.rtrComment"))
					dto.setRtrComment(opValue.get());
				else if (attribute.equalsIgnoreCase("o.rtrDtCreate"))
					dto.setRtrDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("o.TCkCtRateTable.rtId"))
					tCkCtRateTable.setRtId(opValue.get());
			}

			dto.setTCkCtRateTable(tCkCtRateTable);

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
	protected CoreMstLocale getCoreMstLocale(CkCtRateTableRemark paramD)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtRateTableRemark setCoreMstLocale(CoreMstLocale paramCoreMstLocale, CkCtRateTableRemark paramD)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

}
