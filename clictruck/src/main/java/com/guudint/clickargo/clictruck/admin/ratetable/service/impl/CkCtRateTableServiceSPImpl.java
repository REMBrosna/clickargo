/**
 *
 * Copyright(c) vCargo Cloud Pte Ltd, All Rights Reserved.
 * Developed by vCargo Cloud Pte Ltd
 * PROJECT : ClicTruck
 * NAME: CkCtRateTableService2
 * DESC: CkCtRateTable Service
 * HISTORY: 2023/02/16/Adenny - first cut
 * 			
 **/
package com.guudint.clickargo.clictruck.admin.ratetable.service.impl;

import java.text.SimpleDateFormat;
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

import com.guudint.clickargo.clictruck.admin.ratetable.constant.CkCtRateConstant;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.service.impl.TripRateWorkflowServiceImpl.TripRateStatus;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.Roles;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.dto.MstCurrency;
import com.vcc.camelone.master.model.TMstCurrency;
import com.vcc.camelone.util.PrincipalUtilService;

public class CkCtRateTableServiceSPImpl extends AbstractClickCargoEntityService<TCkCtRateTable, String, CkCtRateTable>
		implements ICkConstant {

	private static Logger LOG = Logger.getLogger(CkCtRateTableServiceSPImpl.class);

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	private CkCtTripRateServiceImpl tripRateServiceImpl;

	public CkCtRateTableServiceSPImpl() {
		super(CkCtRateConstant.Table.NAME_DAO, CkCtRateConstant.Prefix.AUDIT_TAG, CkCtRateConstant.Table.NAME_ENTITY,
				CkCtRateConstant.Table.NAME);
	}

	@Override
	public CkCtRateTable findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtRateTable tCkCtRateTable = dao.find(id);
			if (tCkCtRateTable == null) {
				throw new EntityNotFoundException("findById -> id:" + id);
			}
			initEnity(tCkCtRateTable);
			return dtoFromEntity(tCkCtRateTable);
		} catch (Exception e) {
			LOG.error("findById" + e);
		}
		return null;
	}

	@Override
	public CkCtRateTable deleteById(String paramString, Principal paramPrincipal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtRateTable> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {

			if (filterRequest == null) {
				throw new ParameterException("param filterRequest null");
			}
			CkCtRateTable ckCtRateTable = whereDto(filterRequest);
			if (ckCtRateTable == null) {
				throw new ProcessingException("whereDto null result");
			}

			Principal principal = principalUtilService.getPrincipal();
			if (null == principal)
				throw new ProcessingException("principa null");

			Optional<List<String>> opAuthRoles = Optional.ofNullable(principal.getRoleList());
			if (!opAuthRoles.isPresent())
				throw new ProcessingException("principal roles null or empty");

			filterRequest.setTotalRecords(super.countByAnd(ckCtRateTable));
			String orderClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtRateTable> entities = super.findEntitiesByAnd(ckCtRateTable, "from TCkCtRateTable o ",
					orderClause, filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtRateTable> dtos = entities.stream().map(x -> {
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

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtRateTable initEnity(TCkCtRateTable entity) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (null != entity) {
			Hibernate.initialize(entity.getTCoreAccnByRtCompany());
			Hibernate.initialize(entity.getTCoreAccnByRtCoFf());
			Hibernate.initialize(entity.getTMstCurrency());
		}
		return entity;
	}

	@Override
	protected TCkCtRateTable entityFromDTO(CkCtRateTable dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtRateTable entity = new TCkCtRateTable();
			entity = dto.toEntity(entity);

			Optional<CoreAccn> opCoreAccnByRtCompany = Optional.ofNullable(dto.getTCoreAccnByRtCompany());
			entity.setTCoreAccnByRtCompany(
					opCoreAccnByRtCompany.isPresent() ? opCoreAccnByRtCompany.get().toEntity(new TCoreAccn()) : null);

			Optional<CoreAccn> opCoreAccnByRtCoFf = Optional.ofNullable(dto.getTCoreAccnByRtCoFf());
			entity.setTCoreAccnByRtCoFf(
					opCoreAccnByRtCoFf.isPresent() ? opCoreAccnByRtCoFf.get().toEntity(new TCoreAccn()) : null);

			Optional<MstCurrency> opMstCurrency = Optional.ofNullable(dto.getTMstCurrency());
			entity.setTMstCurrency(opMstCurrency.isPresent() ? opMstCurrency.get().toEntity(new TMstCurrency()) : null);

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
	protected CkCtRateTable dtoFromEntity(TCkCtRateTable entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (null == entity)
			throw new ParameterException("param entity null");

		CkCtRateTable dto = new CkCtRateTable(entity);

		Optional<TCoreAccn> opCoreAccnByRtCompany = Optional.ofNullable(entity.getTCoreAccnByRtCompany());
		dto.setTCoreAccnByRtCompany(
				opCoreAccnByRtCompany.isPresent() ? new CoreAccn(opCoreAccnByRtCompany.get()) : null);

		Optional<TCoreAccn> opCoreAccnByRtCoFf = Optional.ofNullable(entity.getTCoreAccnByRtCoFf());
		dto.setTCoreAccnByRtCoFf(opCoreAccnByRtCoFf.isPresent() ? new CoreAccn(opCoreAccnByRtCoFf.get()) : null);

		Optional<TMstCurrency> opMstCurrency = Optional.ofNullable(entity.getTMstCurrency());
		dto.setTMstCurrency(opMstCurrency.isPresent() ? new MstCurrency(opMstCurrency.get()) : null);

		try {
			List<CkCtTripRate> tripRateList = tripRateServiceImpl.getByRateTable(dto);
			dto.setHasSubmitTripRate(
					tripRateList.stream().anyMatch(e -> e.getTrStatus() == TripRateStatus.SUB.getStatusCode()));

			dto.setHasVerifyTripRate(
					tripRateList.stream().anyMatch(e -> e.getTrStatus() == TripRateStatus.VER.getStatusCode()));

		} catch (Exception e) {
			throw new ProcessingException(e);
		}

		return dto;
	}

	@Override
	protected String entityKeyFromDTO(CkCtRateTable dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (null == dto)
			throw new ParameterException("dto param null");

		return dto.getRtId();
	}

	@Override
	protected TCkCtRateTable updateEntity(ACTION paramACTION, TCkCtRateTable paramE, Principal paramPrincipal,
			Date paramDate) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkCtRateTable updateEntityStatus(TCkCtRateTable paramE, char paramChar)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtRateTable preSaveUpdateDTO(TCkCtRateTable storedEntity, CkCtRateTable dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");
		if (null == storedEntity)
			throw new ParameterException("param storedEntity null");
		if (null == dto)
			throw new ParameterException("param dto null");

		dto.setRtUidCreate(storedEntity.getRtUidCreate());
		dto.setRtDtCreate(storedEntity.getRtDtCreate());
		return dto;
	}

	@Override
	protected void preSaveValidation(CkCtRateTable paramD, Principal paramPrincipal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtRateTable paramD, Principal paramPrincipal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected String getWhereClause(CkCtRateTable dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		if (null == dto)
			throw new ParameterException("param dto null");

		StringBuffer searchStatement = new StringBuffer();

		Principal principal = principalUtilService.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principa null");

		Optional<List<String>> opAuthRoles = Optional.ofNullable(principal.getRoleList());
		if (!opAuthRoles.isPresent())
			throw new ProcessingException("principal roles null or empty");

		String trStatus = "";
		if (opAuthRoles.get().contains(Roles.SP_L1.name())) {
			trStatus = String.valueOf(TripRateStatus.SUB.getStatusCode());
		} else if (opAuthRoles.get().contains(Roles.SP_FIN_HD.name())) {
			trStatus = String.valueOf(TripRateStatus.VER.getStatusCode());
		}

		searchStatement.append(getOperator(false) + "o.rtId"
				+ " IN (SELECT b.TCkCtRateTable.rtId FROM TCkCtTripRate b WHERE b.trStatus = '" + trStatus + "')");
		wherePrinted = true;

		if (StringUtils.isNotBlank(dto.getRtName())) {
			searchStatement.append(getOperator(wherePrinted) + "o.rtName like :rtName");
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(dto.getRtId())) {
			searchStatement.append(getOperator(wherePrinted) + "o.rtId = :rtId");
			wherePrinted = true;
		}

		Optional<CoreAccn> opCoreAccnByRtCompany = Optional.ofNullable(dto.getTCoreAccnByRtCompany());
		if (opCoreAccnByRtCompany.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccnByRtCompany.get().getAccnId())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccnByRtCompany.accnId = :toAccnId");
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(opCoreAccnByRtCompany.get().getAccnName())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccnByRtCompany.accnName like :toAccnName");
				wherePrinted = true;
			}
		}

		Optional<CoreAccn> opCoreAccnByRtCoFf = Optional.ofNullable(dto.getTCoreAccnByRtCoFf());
		if (opCoreAccnByRtCoFf.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccnByRtCoFf.get().getAccnId())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccnByRtCoFf.accnId = :coFfAccnId");
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(opCoreAccnByRtCoFf.get().getAccnName())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccnByRtCoFf.accnName like :coFfAccnName");
				wherePrinted = true;
			}
		}
		Optional<Date> opRtDtStart = Optional.ofNullable(dto.getRtDtStart());
		if (opRtDtStart.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.rtDtStart" + ",'" + DateFormat.MySql.D_M_Y
					+ "') = :rtDtStart");
			wherePrinted = true;
		}
		Optional<Date> opRtDtEnd = Optional.ofNullable(dto.getRtDtEnd());
		if (opRtDtEnd.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.rtDtEnd" + ",'" + DateFormat.MySql.D_M_Y
					+ "') = :rtDtEnd");
			wherePrinted = true;
		}
		Optional<Date> opRtDtCreate = Optional.ofNullable(dto.getRtDtCreate());
		if (opRtDtCreate.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.rtDtCreate" + ",'"
					+ DateFormat.MySql.D_M_Y + "') = :rtDtCreate");
			wherePrinted = true;
		}
		Optional<Date> opRtDtLupd = Optional.ofNullable(dto.getRtDtLupd());
		if (opRtDtLupd.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.rtDtLupd" + ",'" + DateFormat.MySql.D_M_Y
					+ "') = :rtDtLupd");
			wherePrinted = true;
		}
		
		searchStatement.append(getOperator(wherePrinted) + "o.rtStatus = :rtStatus");

		return searchStatement.toString();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtRateTable dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		if (null == dto)
			throw new ParameterException("param dto null");

		SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		Principal principal = principalUtilService.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principa null");

		Optional<List<String>> opAuthRoles = Optional.ofNullable(principal.getRoleList());
		if (!opAuthRoles.isPresent())
			throw new ProcessingException("principal roles null or empty");
		
		if (null==dto.getRtStatus()) {
			parameters.put("rtStatus", RecordStatus.ACTIVE.getCode());
		} else if (Character.isAlphabetic(dto.getRtStatus()))
			parameters.put("rtStatus", dto.getRtStatus());

		if (StringUtils.isNotBlank(dto.getRtName()))
			parameters.put("rtName", "%" + dto.getRtName() + "%");

		Optional<CoreAccn> opCoreAccnByRtCompany = Optional.ofNullable(dto.getTCoreAccnByRtCompany());
		if (opCoreAccnByRtCompany.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccnByRtCompany.get().getAccnId()))
				parameters.put("toAccnId", opCoreAccnByRtCompany.get().getAccnId());
			if (StringUtils.isNotBlank(opCoreAccnByRtCompany.get().getAccnName()))
				parameters.put("toAccnName", "%" + opCoreAccnByRtCompany.get().getAccnName() + "%");
		}
		Optional<CoreAccn> opCoreAccnByRtCoFf = Optional.ofNullable(dto.getTCoreAccnByRtCoFf());
		if (opCoreAccnByRtCoFf.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccnByRtCoFf.get().getAccnId()))
				parameters.put("coFfAccnId", opCoreAccnByRtCoFf.get().getAccnId());
			if (StringUtils.isNotBlank(opCoreAccnByRtCoFf.get().getAccnName()))
				parameters.put("coFfAccnName", "%" + opCoreAccnByRtCoFf.get().getAccnName() + "%");
		}
		if (StringUtils.isNotBlank(dto.getRtId())) {
			parameters.put("rtId", dto.getRtId());
		}
		Optional<Date> opRtDtStart = Optional.ofNullable(dto.getRtDtStart());
		if (opRtDtStart.isPresent() && null != opRtDtStart.get())
			parameters.put("rtDtStart", sdfDate.format(opRtDtStart.get()));
		Optional<Date> opRtDtEnd = Optional.ofNullable(dto.getRtDtEnd());
		if (opRtDtEnd.isPresent() && null != opRtDtEnd.get())
			parameters.put("rtDtEnd", sdfDate.format(opRtDtEnd.get()));
		Optional<Date> opRtDtCreate = Optional.ofNullable(dto.getRtDtCreate());
		if (opRtDtCreate.isPresent() && null != opRtDtCreate.get())
			parameters.put("rtDtCreate", sdfDate.format(opRtDtCreate.get()));
		Optional<Date> opRtDtLupd = Optional.ofNullable(dto.getRtDtLupd());
		if (opRtDtLupd.isPresent() && null != opRtDtLupd.get())
			parameters.put("rtDtLupd", sdfDate.format(opRtDtLupd.get()));

		return parameters;
	}

	@Override
	protected CkCtRateTable whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);

			CkCtRateTable dto = new CkCtRateTable();
			CoreAccn coreAccnByRtCompany = new CoreAccn();
			CoreAccn coreAccnByRtCoFf = new CoreAccn();
			MstCurrency mstCurrency = new MstCurrency();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				String attribute = "o." + entityWhere.getAttribute();
				if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_NAME))
					dto.setRtName(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_STATUS))
					dto.setRtStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_ID))
					dto.setRtId(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_COMPANY_ID))
					coreAccnByRtCompany.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_COMPANY_NAME))
					coreAccnByRtCompany.setAccnName(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_CO_FF_ID))
					coreAccnByRtCoFf.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_CO_FF_NAME))
					coreAccnByRtCoFf.setAccnName(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_DT_START))
					dto.setRtDtStart(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_DT_END))
					dto.setRtDtEnd(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_DT_CREATE))
					dto.setRtDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_DT_LUPD))
					dto.setRtDtLupd(sdfDate.parse(opValue.get()));
			}

			dto.setTCoreAccnByRtCompany(coreAccnByRtCompany);
			dto.setTCoreAccnByRtCoFf(coreAccnByRtCoFf);
			dto.setTMstCurrency(mstCurrency);

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
	protected CoreMstLocale getCoreMstLocale(CkCtRateTable dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("getCoreMstLocale");

		if (null == dto) {
			throw new ParameterException("param dto null");
		}
		CoreMstLocale coreMstLocale = dto.getCoreMstLocale();
		if (null == coreMstLocale) {
			throw new ProcessingException("coreMstLocale null");
		}
		return coreMstLocale;
	}

	@Override
	protected CkCtRateTable setCoreMstLocale(CoreMstLocale paramCoreMstLocale, CkCtRateTable paramD)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CkCtRateTable newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
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

	/**
	 * 
	 * @param orderBy
	 * @return
	 */
	private EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) {
		if (orderBy == null) {
			return null;
		}
		if (StringUtils.isEmpty(orderBy.getAttribute())) {
			return null;
		}
		String newAttr = orderBy.getAttribute();
		newAttr = newAttr.replaceAll("tcoreAccnByRtCompany", "TCoreAccnByRtCompany")
				.replaceAll("tcoreAccnByRtCoFf", "TCoreAccnByRtCoFf")
				.replaceAll("tmstCurrency", "TMstCurrency");
		orderBy.setAttribute(newAttr);
		return orderBy;
	}
	
}
