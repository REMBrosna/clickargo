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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dao.CkCtDeptUsrDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtDeptVehDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtVehDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtDept;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptUsr;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.service.CkCtDeptService;
import com.guudint.clickargo.clictruck.common.validator.CkCtDeptValidator;
import com.guudint.clickargo.clictruck.portal.service.CkUserUtilService;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.dto.MstAccnType;

public class CkCtDeptServiceImpl extends AbstractClickCargoEntityService<TCkCtDept, String, CkCtDept>
		implements CkCtDeptService {

	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(CkCtDeptServiceImpl.class);
	private static String AUDIT = "DEPARTMENT";
	private static String TABLE = "T_CK_CT_DEPT";
	private static String DEPT_PREFIX = "DPT";

	@Autowired
	private CkCtDeptValidator validator;

	@Autowired
	private GenericDao<TCoreUsr, String> coreUserDao;

	@Autowired
	private CkCtVehDao vehDao;

	@Autowired
	private CkCtDeptUsrDao deptUsrDao;

	@Autowired
	private CkCtDeptVehDao deptVehDao;

	@Autowired
	@Qualifier("ckCtDeptUsrDao")
	private GenericDao<TCkCtDeptUsr, String> ckCtDeptUsrDao;

	@Autowired
	@Qualifier("ckCtDeptVehDao")
	private GenericDao<TCkCtDeptVeh, String> ckCtDeptVehDao;

	@Autowired
	@Qualifier("ccmAccnService")
	private IEntityService<TCoreAccn, String, CoreAccn> accnService;

	@Autowired
	private CkUserUtilService ckUserUtilService;

	public CkCtDeptServiceImpl() {
		super("ckCtDeptDao", AUDIT, TCkCtDept.class.getName(), TABLE);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtDept newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (principal == null)
			throw new ParameterException("param principal null");

		CkCtDept dept = new CkCtDept();
		// set to the principal account as only respective TO/CO account can create
		// department
		dept.setTCoreAccn(principal.getCoreAccn());

		try {

			CoreAccn accn = principal.getCoreAccn();
			MstAccnType accnType = accn.getTMstAccnType();
			if (Arrays.asList(AccountTypes.ACC_TYPE_CO.name(), AccountTypes.ACC_TYPE_FF_CO.name(),
					AccountTypes.ACC_TYPE_TO.name(),AccountTypes.ACC_TYPE_FF.name(), AccountTypes.ACC_TYPE_TO_WJ.name()).contains(accnType.getAtypId())) {
				loadFilterUsers(dept, accn);
			}

			// Retrieve the vehicles, applies only to CO
			if (accnType.getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
				loadFilterVehicles(dept, accn);
			}

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
		return dept;
	}

	// Overriding to add validation
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtDept dto = (CkCtDept) object;
		List<ValidationError> validationErrors = validator.validateCreate(dto, principal);

		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return add(dto, principal);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtDept add(CkCtDept dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		dto.setDeptId(CkUtil.generateId(DEPT_PREFIX));
		dto.setTCoreAccn(dto.getTCoreAccn());
		dto.setDeptStatus(RecordStatus.ACTIVE.getCode());

		try {
			// Check if deptUsers is filled in, otherwise do not process
			if (dto.getDeptUsers() != null && dto.getDeptUsers().size() > 0) {
				// save to t_ck_ct_dept_usr
				insertDeptUsers(dto, dto.getDeptUsers(), principal);
			}

			// Check if deptVeh is filled in, otherwise do not process
			if (dto.getDeptVehs() != null && dto.getDeptVehs().size() > 0) {
				// save to T_ck_ct_veh
				insertDeptVehs(dto, dto.getDeptVehs(), principal);
			}
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

		return super.add(dto, principal);
	}

	// Overriding this with update validation
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object updateObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtDept dto = (CkCtDept) object;
		List<ValidationError> validationErrors = validator.validateUpdate(dto, principal);
		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {

			try {
				// Check if deptUsers is filled in, otherwise do not process
				if (dto.getDeptUsers() != null && dto.getDeptUsers().size() > 0) {
					// save to t_ck_ct_dept_usr
					insertDeptUsers(dto, dto.getDeptUsers(), principal);
				} else {
					// clear all the users under this department if it's empty
					clearDeptUsers(dto);
				}

				// Check if deptVeh is filled in, otherwise do not process
				if (dto.getDeptVehs() != null && dto.getDeptVehs().size() > 0) {
					// save to T_ck_ct_veh
					insertDeptVehs(dto, dto.getDeptVehs(), principal);
				} else {
					clearDeptVehs(dto);
				}
			} catch (Exception ex) {
				throw new ProcessingException(ex);
			}

			return update(dto, principal);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtDept findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtDept entity = dao.find(id);
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
	public CkCtDept deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		LOG.debug("deleteById -> id:" + id);
		try {
			TCkCtDept entity = dao.find(id);
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
	public CkCtDept delete(CkCtDept dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (dto == null)
			throw new ParameterException("param dto null");
		try {

			TCkCtDept entity = dao.find(dto.getDeptId());
			if (entity == null)
				throw new EntityNotFoundException("key: " + dto.getDeptId());

			this.updateEntityStatus(entity, 'D');
			this.updateEntity(ACTION.DELETE, entity, principal, new Date());
			this.deepDeleteDep(dto.getDeptId());

			dao.update(entity);

			audit(principal, dto.getDeptId(), ACTION.DELETE.toString());
			return dtoFromEntity(entity);

		} catch (Exception ex) {
			LOG.error("delete", ex);
			throw new ProcessingException(ex);

		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtDept> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtDept dept = whereDto(filterRequest);
		if (dept == null) {
			throw new ProcessingException("whereDto null result");
		}
		filterRequest.setTotalRecords(countByAnd(dept));
		List<CkCtDept> deptDtoList = new ArrayList<>();
		try {
			String orderClause = filterRequest.getOrderBy().toString();
			List<TCkCtDept> deptEntityList = findEntitiesByAnd(dept, "from TCkCtDept o ", orderClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtDept entity : deptEntityList) {
				CkCtDept dto = dtoFromEntity(entity);
				if (dto != null) {
					deptDtoList.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return deptDtoList;
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
	protected TCkCtDept initEnity(TCkCtDept entity) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (entity != null) {
			Hibernate.initialize(entity.getTCoreAccn());
		}
		return entity;
	}

	@Override
	protected TCkCtDept entityFromDTO(CkCtDept dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (dto == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtDept entity = new TCkCtDept();
		dto.copyBeanProperties(entity);
		if (dto.getTCoreAccn() != null) {
			entity.setTCoreAccn(dto.getTCoreAccn().toEntity(new TCoreAccn()));
		}
		return entity;
	}

	@Override
	protected CkCtDept dtoFromEntity(TCkCtDept entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (entity == null) {
			throw new ParameterException("param entity null");
		}
		CkCtDept dto = new CkCtDept(entity);
		if (entity.getTCoreAccn() != null) {
			dto.setTCoreAccn(new CoreAccn(entity.getTCoreAccn()));
		}

		try {
			if (entity.getTCoreAccn() != null) {
				CoreAccn accn = accnService.findById(entity.getTCoreAccn().getAccnId());
				// Get the account type
				MstAccnType accnType = accn.getTMstAccnType();
				//if (Arrays.asList(AccountTypes.ACC_TYPE_CO.name(),AccountTypes.ACC_TYPE_FF.name(), AccountTypes.ACC_TYPE_FF_CO.name(), AccountTypes.ACC_TYPE_TO.name()).contains(accnType.getAtypId())) {
				if(true) {
					loadFilterUsers(dto, accn);
				}

				// Retrieve the vehicles, applies only to TO, TO_WJ
				//if (accnType.getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
				if (Arrays.asList(AccountTypes.ACC_TYPE_TO.name(), AccountTypes.ACC_TYPE_TO_WJ.name()).contains(accnType.getAtypId())) {
					loadFilterVehicles(dto, accn);
				}

			}

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

		return dto;
	}

	@Override
	protected String entityKeyFromDTO(CkCtDept dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (dto == null) {
			throw new ParameterException("dto param null");
		}
		return dto.getDeptId();
	}

	@Override
	protected TCkCtDept updateEntity(ACTION action, TCkCtDept entity, Principal principal, Date date)
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
			entity.setDeptUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			entity.setDeptDtCreate(date);
			entity.setDeptUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			entity.setDeptDtLupd(date);
			break;

		case MODIFY:
			entity.setDeptUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			entity.setDeptDtLupd(date);
			break;

		default:
			break;
		}
		return entity;
	}

	@Override
	protected TCkCtDept updateEntityStatus(TCkCtDept entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (entity == null) {
			throw new ParameterException("entity param null");
		}
		entity.setDeptStatus(status);
		return entity;
	}

	@Override
	protected CkCtDept preSaveUpdateDTO(TCkCtDept storedEntity, CkCtDept dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		if (storedEntity == null)
			throw new ParameterException("param storedEntity null");
		if (dto == null)
			throw new ParameterException("param dto null");
		dto.setDeptUidCreate(storedEntity.getDeptUidCreate());
		dto.setDeptDtCreate(storedEntity.getDeptDtCreate());

		return dto;
	}

	@Override
	protected void preSaveValidation(CkCtDept dto, Principal principal) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected String getWhereClause(CkCtDept dto, boolean wherePrinted) throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		if (dto == null)
			throw new ParameterException("param dto null");

		StringBuilder condition = new StringBuilder();

		condition.append(getOperator(wherePrinted) + " o.TCoreAccn.accnId =  :accnId");
		wherePrinted = true;

		condition.append(getOperator(wherePrinted) + " o.deptStatus in :status");
		wherePrinted = true;

		if (StringUtils.isNotBlank(dto.getDeptName())) {
			condition.append(getOperator(wherePrinted) + "o.deptName like :deptName");
			wherePrinted = true;
		}

		if (StringUtils.isNotBlank(dto.getDeptDesc())) {
			condition.append(getOperator(wherePrinted) + " o.deptDesc like :deptDesc");
			wherePrinted = true;
		}

		if (dto.getDeptDtCreate() != null) {
			condition.append(getOperator(wherePrinted) + " DATE_FORMAT(o.deptDtCreate,'%d/%m/%Y') = :deptDtCreate");
			wherePrinted = true;
		}

		if (dto.getDeptDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + " DATE_FORMAT(o.deptDtLupd,'%d/%m/%Y') = :deptDtLupd");
			wherePrinted = true;
		}

		if (dto.getDeptStatus() != null) {
			condition.append(getOperator(wherePrinted) + " o.deptStatus = :deptStatus");
			wherePrinted = true;
		}

		if (dto.getDeptColor() != null) {
			condition.append(getOperator(wherePrinted) + " o.deptColor = :deptColor");
			wherePrinted = true;
		}

		return condition.toString();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected HashMap<String, Object> getParameters(CkCtDept dto) throws ParameterException, ProcessingException {

		LOG.debug("getParameters");

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
		if (dto == null)
			throw new ParameterException("param dto null");

		HashMap<String, Object> parameters = new HashMap<>();

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("principal null");

		parameters.put("accnId", principal.getCoreAccn().getAccnId());

		if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase("history")) {
			parameters.put("status", Arrays.asList(RecordStatus.INACTIVE.getCode(), RecordStatus.SUSPENDED.getCode(), RecordStatus.DEACTIVATE.getCode()));
		} else {
			parameters.put("status", Arrays.asList(RecordStatus.ACTIVE.getCode(), 'N'));
		}

		if (dto.getDeptStatus() != null)
			parameters.put("deptStatus", dto.getDeptStatus());

		if (dto.getDeptColor() != null)
			parameters.put("deptColor", dto.getDeptColor());

		if (StringUtils.isNotBlank(dto.getDeptName()))
			parameters.put("deptName", "%" + dto.getDeptName() + "%");

		if (StringUtils.isNotBlank(dto.getDeptDesc()))
			parameters.put("deptDesc", "%" + dto.getDeptDesc() + "%");

		if (dto.getDeptDtCreate() != null)
			parameters.put("deptDtCreate", sdfDate.format(dto.getDeptDtCreate()));

		if (dto.getDeptDtLupd() != null)
			parameters.put("deptDtLupd", sdfDate.format(dto.getDeptDtLupd()));

		return parameters;
	}

	@Override
	protected CkCtDept whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
			CkCtDept dto = new CkCtDept();
			CoreAccn accn = new CoreAccn();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnId"))
					accn.setAccnId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnName"))
					accn.setAccnName(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("deptName"))
					dto.setDeptName(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("deptDesc"))
					dto.setDeptDesc(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("deptDtCreate"))
					dto.setDeptDtCreate(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("deptDtLupd"))
					dto.setDeptDtLupd(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("deptStatus"))
					dto.setDeptStatus(opValue.get().charAt(0));

				if (entityWhere.getAttribute().equalsIgnoreCase("deptColor"))
					dto.setDeptColor(opValue.get().charAt(0));

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
	protected CoreMstLocale getCoreMstLocale(CkCtDept dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtDept setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtDept dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	// Helper Methods
	//////////////////

	/**
	 * Loads the users that belongs to this account and filter it further if the
	 * user is already added in t_ck_ct_dept_usr
	 */
	private CkCtDept loadFilterUsers(CkCtDept dto, CoreAccn accn) throws Exception {
		// Retrieve the users, applies to both CO & TO
		String hql = "from TCoreUsr o where o.TCoreAccn.accnId = :accnId and o.usrStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("accnId", accn.getAccnId());
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCoreUsr> usrEntityList = coreUserDao.getByQuery(hql, params);
		List<CoreUsr> accnUsersList = usrEntityList.stream().map(el -> {
			CoreUsr usr = new CoreUsr(el);
			return usr;

		}).collect(Collectors.toList());

		List<CoreUsr> deptUsersList = new ArrayList<>();
		if (StringUtils.isNotBlank(dto.getDeptId())) {
			List<TCkCtDeptUsr> deptUsers = deptUsrDao.getUsersByDept(dto.getDeptId());
			if (deptUsers != null && deptUsers.size() > 0) {
				deptUsersList = deptUsers.stream().map(el -> {
					Hibernate.initialize(el.getTCoreUsr());
					CoreUsr usr = new CoreUsr(el.getTCoreUsr());
					return usr;
				}).collect(Collectors.toList());
			}
		}

		// Do not include in the list if the user is already in the other department
		List<TCkCtDeptUsr> allDeptUsers = deptUsrDao.getUsersByAccnDept(accn.getAccnId());
		List<CoreUsr> allUsers = allDeptUsers.stream().map(el -> {
			Hibernate.initialize(el.getTCoreUsr());
			CoreUsr usr = new CoreUsr(el.getTCoreUsr());
			return usr;
		}).collect(Collectors.toList());

		// Filter accnUsersList by checking against usersInDeptSet. If user exist in
		// usersInDeptSet do not include in accnUsersList
		List<CoreUsr> nonDeptUsersList = new ArrayList<>();
		for (CoreUsr usr : accnUsersList) {
			boolean isDeptUser = allUsers.stream().map(CoreUsr::getUsrUid)
					.anyMatch(uid -> uid.equalsIgnoreCase(usr.getUsrUid()));
			if (!isDeptUser) {
				nonDeptUsersList.add(usr);
			}
		}

		dto.setAccnUsers(nonDeptUsersList);
		dto.setDeptUsers(deptUsersList);
		return dto;

	}

	/**
	 * Loads the users that belongs to this account and filter it further if the
	 * user is already added in t_ck_ct_dept_usr
	 */
	private CkCtDept loadFilterVehicles(CkCtDept dto, CoreAccn accn) throws Exception {
		// Retrieve all active vehicles that belongs to TO
		List<TCkCtVeh> vehList = vehDao.findVehTypeByCompany(accn.getAccnId());
		List<CkCtVeh> accnVehList = vehList.stream().map(el -> {
			return new CkCtVeh(el);
		}).collect(Collectors.toList());

		List<CkCtVeh> deptVehsList = new ArrayList<>();
		if (StringUtils.isNotBlank(dto.getDeptId())) {
			List<TCkCtDeptVeh> deptVehs = deptVehDao.getVehiclesByDept(dto.getDeptId());
			if (deptVehs != null && deptVehs.size() > 0) {
				deptVehsList = deptVehs.stream().map(el -> {
					Hibernate.initialize(el.getTCkCtVeh());
					CkCtVeh veh = new CkCtVeh(el.getTCkCtVeh());
					return veh;
				}).collect(Collectors.toList());
			}
		}

		// Do not include in the list if the vehicle is already in the other department
		List<TCkCtDeptVeh> deptAllVehs = deptVehDao.getVehiclesByAccnDept(accn.getAccnId());
		List<CkCtVeh> allVehs = deptAllVehs.stream().map(el -> {
			Hibernate.initialize(el.getTCkCtVeh());
			CkCtVeh veh = new CkCtVeh(el.getTCkCtVeh());
			return veh;
		}).collect(Collectors.toList());

		// Filter accnUsersList by checking against usersInDeptSet. If user exist in
		// usersInDeptSet do not include in accnUsersList
		List<CkCtVeh> nonDeptVehsList = new ArrayList<>();
		for (CkCtVeh veh : accnVehList) {
			boolean isDeptVeh = allVehs.stream().map(CkCtVeh::getVhId)
					.anyMatch(vId -> vId.equalsIgnoreCase(veh.getVhId()));
			if (!isDeptVeh) {
				nonDeptVehsList.add(veh);
			}
		}

		dto.setAccnVehs(nonDeptVehsList);
		dto.setDeptVehs(deptVehsList);
		return dto;

	}

	private void clearDeptUsers(CkCtDept dept) throws Exception {
		// Delete all users first that belongs to this department
		String hql = "delete from TCkCtDeptUsr o where o.TCkCtDept.deptId=:deptId";
		Map<String, Object> params = new HashMap<>();
		params.put("deptId", dept.getDeptId());
		ckCtDeptUsrDao.executeUpdate(hql, params);
	}

	private void clearDeptVehs(CkCtDept dept) throws Exception {
		// Delete all users first that belongs to this department
		String hql = "delete from TCkCtDeptVeh o where o.TCkCtDept.deptId=:deptId";
		Map<String, Object> params = new HashMap<>();
		params.put("deptId", dept.getDeptId());
		ckCtDeptVehDao.executeUpdate(hql, params);
	}

	private void insertDeptUsers(CkCtDept dept, List<CoreUsr> deptUsers, Principal principal) throws Exception {
		// Delete all users first that belongs to this department
		String hql = "delete from TCkCtDeptUsr o where o.TCkCtDept.deptId=:deptId";
		Map<String, Object> params = new HashMap<>();
		params.put("deptId", dept.getDeptId());
		ckCtDeptUsrDao.executeUpdate(hql, params);

		// then insert
		for (CoreUsr usr : deptUsers) {
			TCkCtDeptUsr usrDept = new TCkCtDeptUsr();
			usrDept.setDuId(CkUtil.generateRecordId());

			TCoreUsr usrEntity = new TCoreUsr();
			usr.copyBeanProperties(usrEntity);
			usrDept.setTCoreUsr(usrEntity);

			TCkCtDept deptEntity = new TCkCtDept();
			dept.copyBeanProperties(deptEntity);
			usrDept.setTCkCtDept(deptEntity);

			usrDept.setDuStatus(RecordStatus.ACTIVE.getCode());
			usrDept.setDuUidCreate(principal.getUserId());
			usrDept.setDuDtCreate(new Date());
			ckCtDeptUsrDao.saveOrUpdate(usrDept);

			// update user department in tcoreusr
			ckUserUtilService.updateUserDepartment(usr, dept, principal);

		}
	}

	private void insertDeptVehs(CkCtDept dept, List<CkCtVeh> deptVehs, Principal principal) throws Exception {
		// Delete all users first that belongs to this department
		String hql = "delete from TCkCtDeptVeh o where o.TCkCtDept.deptId=:deptId";
		Map<String, Object> params = new HashMap<>();
		params.put("deptId", dept.getDeptId());
		ckCtDeptVehDao.executeUpdate(hql, params);

		// then insert
		for (CkCtVeh veh : deptVehs) {
			TCkCtDeptVeh vehDept = new TCkCtDeptVeh();
			vehDept.setDvId(CkUtil.generateRecordId());

			TCkCtVeh vehEntity = new TCkCtVeh();
			veh.copyBeanProperties(vehEntity);
			vehDept.setTCkCtVeh(vehEntity);

			TCkCtDept deptEntity = new TCkCtDept();
			dept.copyBeanProperties(deptEntity);
			vehDept.setTCkCtDept(deptEntity);

			vehDept.setDvStatus(RecordStatus.ACTIVE.getCode());
			vehDept.setDvUidCreate(principal.getUserId());
			vehDept.setDvDtCreate(new Date());
			ckCtDeptVehDao.saveOrUpdate(vehDept);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtDept updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = principalUtilService.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtDept co2x = findById(id);
		if (co2x == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		switch (status) {
			case "active":
				co2x.setDeptStatus(RecordStatus.ACTIVE.getCode());
                try {
                    this.activeUserDep(id);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
			case "deactive":
				co2x.setDeptStatus(RecordStatus.INACTIVE.getCode());
                try {
					this.deActiveUserDep(id);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
			default:
				throw new IllegalArgumentException("Invalid status: " + status);
		}
		return update(co2x, principal);
	}
	private void deepDeleteDep(String key) throws Exception {
		executeUpdateWithDeptId(key, "UPDATE TCkCtDeptUsr o SET o.duStatus ='D' WHERE o.TCkCtDept.deptId = :deptId");
		executeUpdateWithDeptId(key, "UPDATE TCkCtDeptVeh o SET o.dvStatus ='D' WHERE o.TCkCtDept.deptId = :deptId");
	}

	private void activeUserDep(String key) throws Exception {
		executeUpdateWithDeptId(key, "UPDATE TCkCtDeptUsr o SET o.duStatus ='A' WHERE o.TCkCtDept.deptId = :deptId");
		executeUpdateWithDeptId(key, "UPDATE TCkCtDeptVeh o SET o.dvStatus ='A' WHERE o.TCkCtDept.deptId = :deptId");
	}

	private void deActiveUserDep(String key) throws Exception {
		executeUpdateWithDeptId(key, "UPDATE TCkCtDeptUsr o SET o.duStatus ='I' WHERE o.TCkCtDept.deptId = :deptId");
		executeUpdateWithDeptId(key, "UPDATE TCkCtDeptVeh o SET o.dvStatus ='I' WHERE o.TCkCtDept.deptId = :deptId");
	}

	private void executeUpdateWithDeptId(String deptId, String query) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("deptId", deptId);
		dao.executeUpdate(query, param);
	}

}
