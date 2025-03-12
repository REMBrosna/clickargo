package com.guudint.clickargo.clictruck.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.guudint.clickargo.clictruck.common.dto.CkCtCo2x;
import com.guudint.clickargo.clictruck.common.model.TCkCtCo2x;
import com.guudint.clickargo.clictruck.common.service.CkCtCo2xService;
import com.guudint.clickargo.clictruck.common.validator.CkCtCo2xValidator;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.util.email.SysParam;

public class CkCtCo2xServiceImpl extends AbstractClickCargoEntityService<TCkCtCo2x, String, CkCtCo2x>
		implements CkCtCo2xService {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtCo2xServiceImpl.class);
	private static String AUDIT = "CO2X";
	private static String TABLE = "T_CK_CT_CO2X";
	private static String CO2X_PREFIX = "CO2X";
	public static final String KEY_CO2_CONNECT_URL = "CLICTRUCK_CO2_CONNECT";

	@Autowired
	private CkCtCo2xValidator validator;

	@Autowired
	private GenericDao<TCoreAccn, String> coreAccDao;

	@Autowired
	private SysParam sysParam;

	private static enum Status {
		NEW('N'), EXPIRED('E'), DELETED('D');

		private char code;

		Status(char code) {
			this.code = code;
		}

		char getCode() {
			return this.code;
		}
	}

	public CkCtCo2xServiceImpl() {
		super("ckCtCo2xDao", AUDIT, TCkCtCo2x.class.getName(), TABLE);
	}

	@Override
	public CkCtCo2x newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {

		if (principal == null)
			throw new ParameterException("param principal null");
		CkCtCo2x co2x = new CkCtCo2x();
		return co2x;

	}

	// Overriding this to add validation
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		CkCtCo2x dto = (CkCtCo2x) object;
		List<ValidationError> validationErrors = validator.validateCreate(dto, principal);

		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return add(dto, principal);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtCo2x add(CkCtCo2x dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		dto.setCo2xId(CkUtil.generateId(CO2X_PREFIX));
		dto.setCo2xAccnName(dto.getTCoreAccn().getAccnName());
		dto.setCo2xStatus(RecordStatus.ACTIVE.getCode());
		return super.add(dto, principal);
	}

	// Overriding this with update validation
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object updateObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtCo2x dto = (CkCtCo2x) object;
		List<ValidationError> validationErrors = validator.validateUpdate(dto, principal);
		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			dto.setCo2xAccnName(dto.getCo2xAccnName());
			return update(dto, principal);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtCo2x findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtCo2x entity = dao.find(id);
			if (entity == null) {
				throw new EntityNotFoundException("findById -> id:" + id);
			}
			initEnity(entity);
			return dtoFromEntity(entity);
		} catch (Exception e) {
			LOG.error("findById" + e);
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtCo2x deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		LOG.debug("deleteById -> id:" + id);
		try {
			TCkCtCo2x entity = dao.find(id);
			if (entity != null) {
				dao.remove(entity);
				return dtoFromEntity(entity);
			}
		} catch (Exception e) {
			LOG.error("deleteById", e);
		}
		return null;
	}

	@Override
	public CkCtCo2x delete(CkCtCo2x dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (dto == null)
			throw new ParameterException("param dto null");
		try {

			TCkCtCo2x entity = dao.find(dto.getCo2xId());
			if (entity == null)
				throw new EntityNotFoundException("key: " + dto.getCo2xId());

			this.updateEntityStatus(entity, 'D');
			this.updateEntity(ACTION.DELETE, entity, principal, new Date());
			dao.update(entity);

			audit(principal, dto.getCo2xId(), ACTION.DELETE.toString());
			return dtoFromEntity(entity);

		} catch (Exception ex) {
			LOG.error("delete", ex);
			throw new ProcessingException(ex);

		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtCo2x> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtCo2x co2x = whereDto(filterRequest);
		if (co2x == null) {
			throw new ProcessingException("whereDto null result");
		}
		filterRequest.setTotalRecords(countByAnd(co2x));
		List<CkCtCo2x> co2xDtoList = new ArrayList<>();
		try {
			String orderClause = filterRequest.getOrderBy().toString();
			List<TCkCtCo2x> co2xEntityList = findEntitiesByAnd(co2x, "from TCkCtCo2x o ", orderClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtCo2x entity : co2xEntityList) {
				CkCtCo2x dto = dtoFromEntity(entity);
				if (dto != null) {
					co2xDtoList.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return co2xDtoList;
	}

	@Override
	protected void initBusinessValidator() {

	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtCo2x initEnity(TCkCtCo2x entity) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (entity != null) {
			Hibernate.initialize(entity.getTCoreAccn());
		}
		return entity;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtCo2x entityFromDTO(CkCtCo2x dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (dto == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtCo2x entity = new TCkCtCo2x();
		dto.copyBeanProperties(entity);
		if (dto.getTCoreAccn() != null) {
			entity.setTCoreAccn(dto.getTCoreAccn().toEntity(new TCoreAccn()));
		}
		return entity;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtCo2x dtoFromEntity(TCkCtCo2x entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (entity == null) {
			throw new ParameterException("param entity null");
		}
		CkCtCo2x dto = new CkCtCo2x(entity);
		if (entity.getTCoreAccn() != null) {
			dto.setTCoreAccn(new CoreAccn(entity.getTCoreAccn()));
			dto.setCo2xAccnName(entity.getTCoreAccn().getAccnName());
		}

		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected String entityKeyFromDTO(CkCtCo2x dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (dto == null) {
			throw new ParameterException("dto param null");
		}
		return dto.getCo2xId();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtCo2x updateEntity(ACTION action, TCkCtCo2x entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (entity == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");
		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			entity.setCo2xUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			entity.setCo2xDtCreate(date);
			entity.setCo2xUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			entity.setCo2xDtLupd(date);
			break;

		case MODIFY:
			entity.setCo2xUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			entity.setCo2xDtLupd(date);
			break;

		default:
			break;
		}
		return entity;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtCo2x updateEntityStatus(TCkCtCo2x entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (entity == null) {
			throw new ParameterException("entity param null");
		}
		entity.setCo2xStatus(status);
		return entity;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtCo2x preSaveUpdateDTO(TCkCtCo2x storedEntity, CkCtCo2x dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		if (storedEntity == null)
			throw new ParameterException("param storedEntity null");
		if (dto == null)
			throw new ParameterException("param dto null");
		dto.setCo2xUidCreate(storedEntity.getCo2xUidCreate());
		dto.setCo2xDtCreate(storedEntity.getCo2xDtCreate());

		return dto;
	}

	@Override
	protected void preSaveValidation(CkCtCo2x dto, Principal principal) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected String getWhereClause(CkCtCo2x dto, boolean wherePrinted) throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		if (dto == null)
			throw new ParameterException("param dto null");

		StringBuilder condition = new StringBuilder();

		condition.append(getOperator(wherePrinted) + " o.co2xStatus in :status");
		wherePrinted = true;
		if (dto.getTCoreAccn() != null) {
			CoreAccn accn = dto.getTCoreAccn();

			if (StringUtils.isNotBlank(accn.getAccnId())) {
				condition.append(getOperator(wherePrinted) + "o.TCoreAccn.accnId=:accnId");
				wherePrinted = true;
			}

			if (StringUtils.isNotBlank(accn.getAccnName())) {
				condition.append(getOperator(wherePrinted) + "o.TCoreAccn.accnName LIKE :accnName");
				wherePrinted = true;
			}
		}

		if (StringUtils.isNotBlank(dto.getCo2xAccnName())) {
			condition.append(getOperator(wherePrinted) + "o.co2xAccnName LIKE :co2xAccnName");
			wherePrinted = true;
		}

		if (StringUtils.isNotBlank(dto.getCo2xUid())) {
			condition.append(getOperator(wherePrinted) + "o.co2xUid LIKE :co2xUid");
			wherePrinted = true;
		}

		if (StringUtils.isNotBlank(dto.getCo2xCoyId())) {
			condition.append(getOperator(wherePrinted) + " o.co2xCoyId=:companyId");
			wherePrinted = true;
		}

		if (dto.getCo2xDtExpiry() != null) {
			condition.append(getOperator(wherePrinted) + " DATE_FORMAT(o.co2xDtExpiry,'%d/%m/%Y') = :expiryDate");
			wherePrinted = true;
		}

		if (dto.getCo2xDtCreate() != null) {
			condition.append(getOperator(wherePrinted) + " DATE_FORMAT(o.co2xDtCreate,'%d/%m/%Y') = :createdDate");
			wherePrinted = true;
		}

		if (dto.getCo2xDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + " DATE_FORMAT(o.co2xDtLupd,'%d/%m/%Y') = :updateDate");
			wherePrinted = true;
		}

		// user email
		if (StringUtils.isNotBlank(dto.getCo2xUid())) {
			condition.append(getOperator(wherePrinted) + " o.co2xUid LIKE :userEmail");
			wherePrinted = true;
		}

		// status
		if (dto.getCo2xStatus() != null) {
			condition.append(getOperator(wherePrinted) + " o.co2xStatus = :co2xStatus");
			wherePrinted = true;
		}

		return condition.toString();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected HashMap<String, Object> getParameters(CkCtCo2x dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		if (dto == null)
			throw new ParameterException("param dto null");

		HashMap<String, Object> parameters = new HashMap<>();
		if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase("history")) {
			parameters.put("status",
					Arrays.asList(RecordStatus.INACTIVE.getCode(), Status.EXPIRED.getCode(), Status.DELETED.getCode()));
		} else {
			parameters.put("status", Arrays.asList(RecordStatus.ACTIVE.getCode(), Status.NEW.getCode()));
		}

		if (dto.getTCoreAccn() != null) {
			CoreAccn accn = dto.getTCoreAccn();

			if (StringUtils.isNotBlank(accn.getAccnId()))
				parameters.put("accnId", accn.getAccnId());

			if (StringUtils.isNotBlank(accn.getAccnName()))
				parameters.put("accnName", "%" + accn.getAccnName() + "%");
		}

		if (StringUtils.isNotBlank(dto.getCo2xAccnName()))
			parameters.put("co2xAccnName", "%" + dto.getCo2xAccnName() + "%");

		if (StringUtils.isNotBlank(dto.getCo2xCoyId()))
			parameters.put("companyId", dto.getCo2xCoyId());

		if (StringUtils.isNotBlank(dto.getCo2xUid()))
			parameters.put("co2xUid", "%" + dto.getCo2xUid() + "%");

		if (dto.getCo2xDtExpiry() != null)
			parameters.put("expiryDate", sdfDate.format(dto.getCo2xDtExpiry()));

		if (dto.getCo2xDtCreate() != null)
			parameters.put("createdDate", sdfDate.format(dto.getCo2xDtCreate()));

		if (dto.getCo2xDtLupd() != null)
			parameters.put("updateDate", sdfDate.format(dto.getCo2xDtLupd()));

		// user email
		if (StringUtils.isNotBlank(dto.getCo2xUid()))
			parameters.put("userEmail", "%" + dto.getCo2xUid() + "%");

		// status
		if (dto.getCo2xStatus() != null)
			parameters.put("co2xStatus", dto.getCo2xStatus());
		
		return parameters;

	}

	@Override
	protected CkCtCo2x whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
			CkCtCo2x dto = new CkCtCo2x();
			CoreAccn accn = new CoreAccn();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnId"))
					accn.setAccnId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnName"))
					accn.setAccnName(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("co2xAccnName"))
					dto.setCo2xAccnName(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("co2xCoyId"))
					dto.setCo2xCoyId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("co2xUid"))
					dto.setCo2xUid(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("co2xDtExpiry"))
					dto.setCo2xDtExpiry(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("co2xDtCreate"))
					dto.setCo2xDtCreate(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("co2xDtLupd"))
					dto.setCo2xDtLupd(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("co2xStatus"))
					dto.setCo2xStatus(opValue.get().charAt(0));

				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}

			}

			dto.setTCoreAccn(accn);
			return dto;
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtCo2x dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtCo2x setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtCo2x dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/** Lists all accounts that are eligible for CO2x monitoring registration */
	@Transactional
	public List<CoreAccn> listEligibleAccounts(String isFilter) throws Exception {
		String hql = "from TCoreAccn o where o.TMstAccnType.atypId in (:accnTypes) and o.accnStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("accnTypes", Arrays.asList(AccountTypes.ACC_TYPE_TO.name()));
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCoreAccn> accnsEntityList = coreAccDao.getByQuery(hql, params);

		List<CoreAccn> coreAccnList = new ArrayList<>();
		if (isFilter.equalsIgnoreCase("N")) {
			coreAccnList = accnsEntityList.stream().map(el -> {
				CoreAccn accn = new CoreAccn(el);
				return accn;
			}).collect(Collectors.toList());

		} else {
			// Iterate through T_CK_CT_CO2X and check if there are accounts already in the
			String hqlCo2x = "from TCkCtCo2x o where o.co2xStatus in (:status)";
			params.clear();
			// include the Inactive as this can still be activated
			params.put("status", Arrays.asList(RecordStatus.ACTIVE.getCode(), Status.NEW.getCode(),
					RecordStatus.INACTIVE.getCode()));
			List<TCkCtCo2x> co2xList = dao.getByQuery(hqlCo2x, params);
			// convert to String accnIds
			List<String> accnIdStrLists = co2xList.stream().map(el -> el.getTCoreAccn().getAccnId())
					.collect(Collectors.toList());

			// filter accnsEntityList, if the accnId is not in the accnIdStrLists
			List<TCoreAccn> accns = accnsEntityList.stream().filter(el -> {
				return !accnIdStrLists.contains(el.getAccnId());
			}).collect(Collectors.toList());

			coreAccnList = accns.stream().map(el -> {
				CoreAccn accn = new CoreAccn(el);
				return accn;
			}).collect(Collectors.toList());
		}
		return coreAccnList;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtCo2x updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = principalUtilService.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtCo2x co2x = findById(id);
		if (co2x == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			co2x.setCo2xStatus(RecordStatus.ACTIVE.getCode());
		} else if ("deactive".equals(status)) {
			co2x.setCo2xStatus(RecordStatus.INACTIVE.getCode());
		}
		return update(co2x, principal);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtCo2x findByAccount(String accnId)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		if (accnId == null)
			throw new ParameterException("accnId null");

		String hql = "from TCkCtCo2x o where o.TCoreAccn.accnId = :accnId and o.co2xStatus in :status";
		Map<String, Object> params = new HashMap<>();
		params.put("accnId", accnId);
		params.put("status", Arrays.asList(RecordStatus.ACTIVE.getCode(), Status.NEW.getCode()));
		try {
			List<TCkCtCo2x> co2xList = dao.getByQuery(hql, params);
			if (co2xList != null && co2xList.size() > 0) {
				// expecting only one
				TCkCtCo2x entity = co2xList.get(0);
				Hibernate.initialize(entity.getTCoreAccn());
				CkCtCo2x dto = new CkCtCo2x(entity);
				dto.setTCoreAccn(new CoreAccn(entity.getTCoreAccn()));

				// then get the sso details to store in ssoUrl in this object so no need to do
				// separate call
				// TODO how to connect to sso
				String ssoUrl = sysParam.getValString(KEY_CO2_CONNECT_URL, null);
				dto.setSsoUrl(ssoUrl);
				return dto;

			}
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

		return null;
	}

}
