package com.guudint.clickargo.clictruck.common.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guudint.clickargo.clictruck.common.constant.CkCtVehConstant;
import com.guudint.clickargo.clictruck.common.dao.CkCtChassisDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtDeptDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtDeptVehDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtVehDao;
import com.guudint.clickargo.clictruck.common.dao.impl.CkCtDeptVehDaoImpl;
import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.dto.CkCtVehExt;
import com.guudint.clickargo.clictruck.common.dto.CkCtVehExtId;
import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.service.TrackingTrucksCkCtVehService;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehState;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.planexec.job.dao.impl.CkJobTruckDaoImpl;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TrackingTrucksCkCtVehServiceImpl extends AbstractClickCargoEntityService<TCkCtVeh, String, CkCtVeh> implements ICkConstant, TrackingTrucksCkCtVehService {

	private static final Logger LOG = Logger.getLogger(TrackingTrucksCkCtVehServiceImpl.class);

	@Autowired
	@Qualifier("ckCtVehDao")
	private CkCtVehDao ckCtVehDao;

	@Autowired
	private CkCtVehExtServiceImpl ckCtVehExtServiceImpl;

	@Autowired
	private CkJobTruckServiceUtil jobTruckServiceUtil;

	@Autowired
	@Qualifier("ckCtDeptVehDao")
	private CkCtDeptVehDao deptVehDao;

	@Autowired
	@Qualifier("ckCtDeptVehDao")
	private CkCtDeptVehDao vehDeptDao;
	@Autowired
	@Qualifier("ckCtDeptDao")
	private CkCtDeptDao ckCtDeptDao;

	@Autowired
	private CkCtChassisDao ckCtChassisDao;
	@Autowired
	private CkCtDeptVehDaoImpl ckCtDeptVehDao;
	@Autowired
	private CkJobTruckDaoImpl ckJobTruckDao;
	public TrackingTrucksCkCtVehServiceImpl() {
		super(CkCtVehConstant.Table.NAME_DAO, CkCtVehConstant.Prefix.AUDIT_TAG, CkCtVehConstant.Table.NAME_ENTITY, CkCtVehConstant.Table.NAME);
	}
	@Override
	protected TCkCtVeh initEnity(TCkCtVeh tCkCtVeh) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtVeh != null) {
			Hibernate.initialize(tCkCtVeh.getTCkCtMstChassisType());
			Hibernate.initialize(tCkCtVeh.getTCkCtMstVehState());
			Hibernate.initialize(tCkCtVeh.getTCkCtMstVehType());
			// Hibernate.initialize(tCkCtVeh.getTCkJob());
			Hibernate.initialize(tCkCtVeh.getTCoreAccn());
		}
		return tCkCtVeh;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public List<CkCtVeh> filterBy(EntityFilterRequest filterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtVeh ckCtVeh = whereDto(filterRequest);
		if (ckCtVeh == null) {
			throw new ProcessingException("whereDto null");
		}
		List<CkCtVeh> ckCtVehs = new ArrayList<>();
		Principal principal = principalUtilService.getPrincipal();
		try {
			String orderByClause = formatOrderBy(filterRequest.getOrderBy().toString());
			String hqlQuery = null;
			if (principal.getCoreAccn().getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO_WJ.name())){
				hqlQuery = "SELECT o FROM TCkCtVeh o";
			}else {
				hqlQuery = "SELECT o FROM TCkCtVeh o, TCkJob tj, TCkJobTruck tjt";
			}

			String whereClause = this.getWhereClause(ckCtVeh, true);
			if (!StringUtils.isEmpty(whereClause)) {
				hqlQuery += " " + whereClause;
			}
			List<TCkCtVeh> tCkCtVehs = this.findEntitiesByAnd(ckCtVeh, hqlQuery, orderByClause, filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<String> vehIdList = new ArrayList<>();
			List<String> depIdList = new ArrayList<>();
			List<TCkCtVeh> modResult = tCkCtVehs;
			CkCtDept userDept = jobTruckServiceUtil.getPrincipalDepartment(principal);
			if (principal != null) {
				if (userDept != null) {
					List<TCkCtDeptVeh> deptVehList = deptVehDao.getVehiclesByAccnDept(userDept.getTCoreAccn().getAccnId());
					if (deptVehList != null && !deptVehList.isEmpty()) {
						for (TCkCtDeptVeh vd : deptVehList) {
							Hibernate.initialize(vd.getTCkCtVeh());
							if (!userDept.getDeptId().equalsIgnoreCase(vd.getTCkCtDept().getDeptId())) {
								vehIdList.add(vd.getTCkCtVeh().getVhId());
							} else {
								depIdList.add(vd.getTCkCtVeh().getVhId());
							}
						}
					}
					CoreAccn accn = principal.getCoreAccn();
					if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())
							|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())) {
						List<TCkJobTruck> listingByCoFf = ckJobTruckDao.findByCoFf(userDept.getDeptId(), accn.getAccnId());
						if (listingByCoFf != null) {
							modResult = listingByCoFf.stream()
									.map(TCkJobTruck::getTCkCtVeh)
									.filter(Objects::nonNull)
									.collect(Collectors.toList());
						}
					} else {
						modResult = tCkCtVehs.stream()
								.filter(el -> !vehIdList.contains(el.getVhId()))
								.collect(Collectors.toList());
					}
				} else {
					CoreAccn accn = principal.getCoreAccn();
					if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())
							|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())) {
						String noDep = "nonDep";
						List<TCkJobTruck> listingByCoFf = ckJobTruckDao.findByCoFf(noDep, accn.getAccnId());
						if (listingByCoFf != null) {
							modResult = listingByCoFf.stream()
									.map(TCkJobTruck::getTCkCtVeh)
									.filter(Objects::nonNull)
									.collect(Collectors.toList());
						}
					}else {
						modResult = tCkCtVehs.stream()
								.filter(Objects::nonNull)
								.collect(Collectors.toList());
					}
				}
			}
			for (TCkCtVeh tCkCtVeh : modResult) {
				CkCtVeh dto = dtoFromEntity(tCkCtVeh, false);
				if (userDept != null) {
					if (depIdList.stream().anyMatch(el -> el.equalsIgnoreCase(dto.getVhId()))) {
						dto.setColorCode(userDept.getDeptColor().toString());
					} else {
						dto.setColorCode(null);
					}
				}
				if (dto != null) {
					ckCtVehs.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtVehs;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public List<TCkCtVeh> findEntitiesByAnd(CkCtVeh dto, String selectClause, String orderByClause, int limit, int offset) throws ParameterException, ProcessingException {
		LOG.debug("findEntitiesByAnd");

		try {
			if (null == dto) {
				throw new ParameterException("param dto null");
			} else if (StringUtils.isEmpty(selectClause)) {
				throw new ParameterException("param selectClause null or empty");
			} else if (StringUtils.isEmpty(orderByClause)) {
				throw new ParameterException("param orderByClause null or empty");
			} else {
				HashMap<String, Object> parameters = this.getParameters(dto);
				String hqlQuery = selectClause + " " + orderByClause;
				List<TCkCtVeh> entities = this.dao.getByQuery(hqlQuery, parameters, limit, offset);
				for (TCkCtVeh entity : entities) {
					this.initEnity(entity);
				}
				return entities;
			}
		} catch (ProcessingException | ParameterException ex) {
			LOG.error("findEntitiesByAnd", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("findEntitiesByAnd", ex);
			throw new ProcessingException(ex);
		}
	}


	protected String formatOrderBy(String attribute) throws Exception {
		attribute = Optional.ofNullable(attribute).orElse("");
		attribute = attribute.replace("tckCtMstChassisType", "TCkCtMstChassisType").replace("tckCtMstVehState", "TCkCtMstVehState").replace("tckCtMstVehType", "TCkCtMstVehType");
		return attribute;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtVeh dtoFromEntity(TCkCtVeh tCkCtVeh) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtVeh == null) {
			throw new ParameterException("param entity null");
		}
		CkCtVeh ckCtVeh = new CkCtVeh(tCkCtVeh);
		if (tCkCtVeh.getTCkCtMstChassisType() != null) {
			ckCtVeh.setTCkCtMstChassisType(new CkCtMstChassisType(tCkCtVeh.getTCkCtMstChassisType()));
		}
		if (tCkCtVeh.getTCkCtMstVehState() != null) {
			ckCtVeh.setTCkCtMstVehState(new CkCtMstVehState(tCkCtVeh.getTCkCtMstVehState()));
		}
		if (tCkCtVeh.getTCkCtMstVehType() != null) {
			ckCtVeh.setTCkCtMstVehType(new CkCtMstVehType(tCkCtVeh.getTCkCtMstVehType()));
		}
		if (tCkCtVeh.getTCoreAccn() != null) {
			ckCtVeh.setTCoreAccn(new CoreAccn(tCkCtVeh.getTCoreAccn()));
		}
		if (tCkCtVeh.getVhPhotoLoc() != null) {
			try {
				String base64 = FileUtil.toBase64(tCkCtVeh.getVhPhotoLoc());
				ckCtVeh.setBase64File(base64);
			} catch (IOException e) {
				LOG.error(e);
			}
		}

		this.retrieveChassisSize(tCkCtVeh, ckCtVeh);

		try {
			// Check if there is an existing vehExt maintenance for this vehicle
			CkCtVehExt maintenance = ckCtVehExtServiceImpl.findVehExtByVehAndKey(ckCtVeh.getVhId(),
					"VEHICLE_MAINTENANCE");
			// just init if null to be used in fe
			// just init if null to be used in fe
			CkCtVehExt newMnt = new CkCtVehExt();
			CkCtVehExtId mntpId = new CkCtVehExtId();
			mntpId.setVextId(ckCtVeh.getVhId());
			newMnt.setId(mntpId);
			ckCtVeh.setMaintenance(maintenance == null ? newMnt : maintenance);

			// Check if there is an existing vehExt expiry for this vehicle
			CkCtVehExt expiry = ckCtVehExtServiceImpl.findVehExtByVehAndKey(ckCtVeh.getVhId(), "EXP_");
			// just init if null to be used in fe
			CkCtVehExt newExp = new CkCtVehExt();
			CkCtVehExtId expId = new CkCtVehExtId();
			expId.setVextId(ckCtVeh.getVhId());
			newExp.setId(expId);
			ckCtVeh.setExpiry(expiry == null ? newExp : expiry);

			// retrieve the department if there is
			TCkCtDeptVeh deptVeh = vehDeptDao.getVehicleDeptByVeh(tCkCtVeh.getVhId());
			if (deptVeh != null) {
				Hibernate.initialize(deptVeh.getTCkCtDept());
				ckCtVeh.setDepartment(deptVeh.getTCkCtDept().getDeptName());
			}
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

		return ckCtVeh;
	}


	protected CkCtVeh dtoFromEntity(TCkCtVeh tCkCtVeh, boolean withData)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtVeh == null) {
			throw new ParameterException("param entity null");
		}
		CkCtVeh ckCtVeh = new CkCtVeh(tCkCtVeh);
		if (tCkCtVeh.getTCkCtMstChassisType() != null) {
			ckCtVeh.setTCkCtMstChassisType(new CkCtMstChassisType(tCkCtVeh.getTCkCtMstChassisType()));
		}
		if (tCkCtVeh.getTCkCtMstVehState() != null) {
			ckCtVeh.setTCkCtMstVehState(new CkCtMstVehState(tCkCtVeh.getTCkCtMstVehState()));
		}
		if (tCkCtVeh.getTCkCtMstVehType() != null) {
			ckCtVeh.setTCkCtMstVehType(new CkCtMstVehType(tCkCtVeh.getTCkCtMstVehType()));
		}
		if (tCkCtVeh.getTCoreAccn() != null) {
			ckCtVeh.setTCoreAccn(new CoreAccn(tCkCtVeh.getTCoreAccn()));
		}

		if (withData) {
			if (tCkCtVeh.getVhPhotoLoc() != null) {
				try {
					String base64 = FileUtil.toBase64(tCkCtVeh.getVhPhotoLoc());
					ckCtVeh.setBase64File(base64);
				} catch (IOException e) {
					LOG.error(e);
				}
			}
		}

		return ckCtVeh;
	}

	@Override
	protected TCkCtVeh entityFromDTO(CkCtVeh ckCtVeh) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtVeh == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtVeh tCkCtVeh = new TCkCtVeh(ckCtVeh);
		if (ckCtVeh.getTCkCtMstChassisType() != null) {
			tCkCtVeh.setTCkCtMstChassisType(ckCtVeh.getTCkCtMstChassisType().toEntity(new TCkCtMstChassisType()));
		}
		if (ckCtVeh.getTCkCtMstVehState() != null) {
			tCkCtVeh.setTCkCtMstVehState(ckCtVeh.getTCkCtMstVehState().toEntity(new TCkCtMstVehState()));
		}
		if (ckCtVeh.getTCkCtMstVehType() != null) {
			tCkCtVeh.setTCkCtMstVehType(ckCtVeh.getTCkCtMstVehType().toEntity(new TCkCtMstVehType()));
		}
		if (ckCtVeh.getTCoreAccn() != null) {
			tCkCtVeh.setTCoreAccn(ckCtVeh.getTCoreAccn().toEntity(new TCoreAccn()));
		}
		return tCkCtVeh;
	}
	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtVeh ckCtVeh)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckCtVeh == null) {
			throw new ParameterException("dto param null");
		}
		if (ckCtVeh.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return ckCtVeh.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtVeh ckCtVeh) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (ckCtVeh == null)
			throw new ParameterException("param dto null");
		try {
			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal is null");

			SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
			HashMap<String, Object> parameters = new HashMap<>();

			CoreAccn accn = principal.getCoreAccn();
			// only Trucking Operator should be able to see the list
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name()) ||
					accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO_WJ.name()) ||
					accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF_CO.name())) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_COMPANY, accn.getAccnId());
			}
//			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name()) || accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())) {
//				parameters.put(CkCtVehConstant.ColumnParam.VH_COMPANY, accn.getAccnId());
//			}
			if (StringUtils.isNotBlank(ckCtVeh.getVhPlateNo())) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_PLATE_NO, "%" + ckCtVeh.getVhPlateNo() + "%");
			}
			if (ckCtVeh.getTCkCtMstVehType() != null) {
				if (ckCtVeh.getTCkCtMstVehType().getVhtyId() != null) {
					parameters.put(CkCtVehConstant.ColumnParam.VH_TYPE, ckCtVeh.getTCkCtMstVehType().getVhtyId());
				}
				if (ckCtVeh.getTCkCtMstVehType().getVhtyName() != null) {
					parameters.put("vhtyName", "%" + ckCtVeh.getTCkCtMstVehType().getVhtyName() + "%");
				}
			}
			if (StringUtils.isNotBlank(ckCtVeh.getVhId())) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_ID, ckCtVeh.getVhId());
			}
			if (ckCtVeh.getVhClass() != null) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_CLASS, ckCtVeh.getVhClass());
			}
			if (ckCtVeh.getVhVolume() != null) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_VOLUME, ckCtVeh.getVhVolume());
			}
			if (ckCtVeh.getVhWeight() != null) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_WEIGHT, ckCtVeh.getVhWeight());
			}
			if (ckCtVeh.getVhDtCreate() != null) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_DT_CREATE, sdf.format(ckCtVeh.getVhDtCreate()));
			}
			if (ckCtVeh.getVhDtLupd() != null) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_DT_LUPD, sdf.format(ckCtVeh.getVhDtLupd()));
			}

			Optional<CkCtMstVehState> opVehState = Optional.ofNullable(ckCtVeh.getTCkCtMstVehState());
			if (opVehState.isPresent()) {
				if (!StringUtils.isBlank(opVehState.get().getVhstId())) {
					parameters.put(CkCtVehConstant.ColumnParam.VH_STATE, opVehState.get().getVhstId());
				}
			}

			if (ckCtVeh.getVhStatus() != null) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_STATUS, 'A');
			}
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())) {
				CkCtDept userDept = jobTruckServiceUtil.getPrincipalDepartment(principal);
				if (userDept != null) {
					parameters.put("deptId", userDept.getDeptId());
				}
				parameters.put("accnId", accn.getAccnId());
			}
			if (!accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO_WJ.name())){
				parameters.put("jobStates", JobStates.ONGOING.name());
			}
			parameters.put("validStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.DEACTIVATE.getCode()));

			return parameters;
		} catch (Exception e) {
			LOG.error("getParameters", e);
			throw new ProcessingException(e);
		}
	}

	@Override
	protected String getWhereClause(CkCtVeh ckCtVeh, boolean wherePrinted) throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		try {
			String EQUAL = " = :", CONTAIN = " like :";
			if (ckCtVeh == null) {
				throw new ParameterException("param dto null");
			}
			StringBuilder condition = new StringBuilder();

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null) {
				throw new ProcessingException("principal is null");
			}
			CoreAccn accn = principal.getCoreAccn();
			// if ACC_TYPE_TO_WJ show all active truck
			if (!accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO_WJ.name())){
			condition.append(getOperator(false)).append(" tj.TCkMstJobState.jbstId = :jobStates");
			}
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF_CO.name())) {
				condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_COMPANY).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_COMPANY);
				wherePrinted = true;
			}
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO_WJ.name())){
				condition.append(getOperator(false)).append(CkCtVehConstant.Column.VH_COMPANY).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_COMPANY);
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(ckCtVeh.getVhPlateNo())) {
				condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_PLATE_NO).append(CONTAIN).append(CkCtVehConstant.ColumnParam.VH_PLATE_NO);
				wherePrinted = true;
			}
			if (ckCtVeh.getTCkCtMstVehType() != null) {
				if (ckCtVeh.getTCkCtMstVehType().getVhtyId() != null) {
					condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_TYPE).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_TYPE);
					wherePrinted = true;
				}
				if (ckCtVeh.getTCkCtMstVehType().getVhtyName() != null) {
					condition.append(getOperator(wherePrinted)).append(" o.vhtyName LIKE :vhtyName");
					wherePrinted = true;
				}
			}
			if (StringUtils.isNotBlank(ckCtVeh.getVhId())) {
				condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_ID).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_ID);
				wherePrinted = true;
			}
			if (ckCtVeh.getVhClass() != null) {
				condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_CLASS).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_CLASS);
				wherePrinted = true;
			}
			if (ckCtVeh.getVhVolume() != null) {
				condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_VOLUME).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_VOLUME);
				wherePrinted = true;
			}
			if (ckCtVeh.getVhWeight() != null) {
				condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_WEIGHT).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_WEIGHT);
				wherePrinted = true;
			}
			if (ckCtVeh.getVhDtCreate() != null) {
				condition.append(getOperator(wherePrinted)).append("DATE_FORMAT(").append(CkCtVehConstant.Column.VH_DT_CREATE).append(",'").append(DateFormat.MySql.D_M_Y).append("')").append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_DT_CREATE);
				wherePrinted = true;
			}
			if (ckCtVeh.getVhDtLupd() != null) {
				condition.append(getOperator(wherePrinted)).append("DATE_FORMAT(").append(CkCtVehConstant.Column.VH_DT_LUPD).append(",'").append(DateFormat.MySql.D_M_Y).append("')").append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_DT_LUPD);
				wherePrinted = true;
			}
			Optional<CkCtMstVehState> opVehState = Optional.ofNullable(ckCtVeh.getTCkCtMstVehState());
			if (opVehState.isPresent()) {
				if (!StringUtils.isBlank(opVehState.get().getVhstId())) {
					condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_STATE).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_STATE);
					wherePrinted = true;
				}
			}
			if (ckCtVeh.getVhStatus() != null) {
				condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_STATUS).append(EQUAL).append(CkCtVehConstant.ColumnParam.VH_STATUS);
				wherePrinted = true;
			}
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())) {
				// Add deptId condition based on the logic in findByCoFf
				CkCtDept userDept = jobTruckServiceUtil.getPrincipalDepartment(principal);
				if (Objects.isNull(userDept)) {
					condition.append(getOperator(wherePrinted)).append(" tjt.TCkCtDeptByJobCoDepartment.deptId IS NULL AND tjt.TCkCtVeh.vhId IS NOT NULL AND (tjt.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR tjt.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId)");
				} else {
					condition.append(getOperator(wherePrinted)).append(" (tjt.TCkCtDeptByJobCoDepartment.deptId = :deptId OR tjt.TCkCtDeptByJobCoDepartment.deptId IS NULL) AND tjt.TCkCtVeh.vhId IS NOT NULL AND (tjt.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR tjt.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId)");
				}
			}
			if (!accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO_WJ.name())) {
				// Ensure joining conditions are correct
				condition.append(getOperator(wherePrinted)).append(" tj.jobId = tjt.TCkJob.jobId AND tjt.TCkCtVeh.vhId = o.vhId");
			}
			// Add final condition
			condition.append(getOperator(wherePrinted)).append(CkCtVehConstant.Column.VH_STATUS).append(" IN :validStatus");
			return condition.toString();
		} catch (Exception e) {
			LOG.error("getWhereClause", e);
			throw new ProcessingException(e);
		}
	}
	@Override
	protected CkCtVeh whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtVeh ckCtVeh = new CkCtVeh();
		CkCtMstChassisType ckCtMstChassisType = new CkCtMstChassisType();
		CkCtMstVehState ckCtMstVehState = new CkCtMstVehState();
		CkCtMstVehType ckCtMstVehType = new CkCtMstVehType();
		// CkJob ckJob = new CkJob();
		CoreAccn coreAccn = new CoreAccn();
		ckCtVeh.setTCkCtMstChassisType(ckCtMstChassisType);
		ckCtVeh.setTCkCtMstVehState(ckCtMstVehState);
		ckCtVeh.setTCkCtMstVehType(ckCtMstVehType);
		// ckCtVeh.setTCkJob(ckJob);
		ckCtVeh.setTCoreAccn(coreAccn);
		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}
			String attribute = "o." + entityWhere.getAttribute();
			if (CkCtVehConstant.Column.VH_COMPANY.equalsIgnoreCase(attribute)) {
				coreAccn.setAccnId(entityWhere.getValue());
			} else if (CkCtVehConstant.Column.VH_ID.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhId(entityWhere.getValue());
			} else if (CkCtVehConstant.Column.VH_STATUS.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhStatus((entityWhere.getValue() == null) ? null : entityWhere.getValue().charAt(0));
			} else if (CkCtVehConstant.Column.VH_PLATE_NO.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhPlateNo(entityWhere.getValue());
			} else if (CkCtVehConstant.Column.VH_TYPE.equalsIgnoreCase(attribute)) {
				ckCtMstVehType.setVhtyId(entityWhere.getValue());
			} else if ("TCkCtMstVehType.vhtyName".equalsIgnoreCase(attribute)) {
				ckCtMstVehType.setVhtyName(entityWhere.getValue());
			} else if (CkCtVehConstant.Column.VH_CLASS.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhClass(Byte.parseByte(entityWhere.getValue()));
			} else if (CkCtVehConstant.Column.VH_VOLUME.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhVolume(Integer.parseInt(entityWhere.getValue()));
			} else if (CkCtVehConstant.Column.VH_WEIGHT.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhWeight(Integer.parseInt(entityWhere.getValue()));
			} else if (CkCtVehConstant.Column.VH_STATE.equalsIgnoreCase(attribute)) {
				ckCtVeh.getTCkCtMstVehState().setVhstId(entityWhere.getValue());

			} else if (CkCtVehConstant.Column.VH_DT_CREATE.equalsIgnoreCase(attribute)) {
				try {
					ckCtVeh.setVhDtCreate(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtVehConstant.Column.VH_DT_LUPD.equalsIgnoreCase(attribute)) {
				try {
					ckCtVeh.setVhDtLupd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			}
		}
		return ckCtVeh;
	}

	@Override
	protected CkCtVeh preSaveUpdateDTO(TCkCtVeh tCkCtVeh, CkCtVeh ckCtVeh)
			throws ParameterException, ProcessingException {
		if (tCkCtVeh == null)
			throw new ParameterException("param storedEntity null");
		if (ckCtVeh == null)
			throw new ParameterException("param dto null");

		if (StringUtils.isNotBlank(ckCtVeh.getBase64File()) && StringUtils.isNotBlank(ckCtVeh.getVhPhotoName())) {
			try {
				byte[] data = Base64.getDecoder().decode(ckCtVeh.getBase64File());
				String basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
				if (StringUtils.isBlank(basePath)) {
					throw new ProcessingException("basePath is not configured");
				}
				String filePath = FileUtil.saveAttachment(basePath.concat(ckCtVeh.getVhPhotoName()), data);
				ckCtVeh.setVhPhotoLoc(filePath);
			} catch (Exception e) {
				LOG.error(e);
			}
		}
		ckCtVeh.setVhUidCreate(tCkCtVeh.getVhUidCreate());
		ckCtVeh.setVhDtCreate(tCkCtVeh.getVhDtCreate());
		return ckCtVeh;
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub
	}

	@Override
	protected String entityKeyFromDTO(CkCtVeh ckCtVeh) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtVeh updateEntity(ACTION action, TCkCtVeh tCkCtVeh, Principal principal, Date date) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtVeh updateEntityStatus(TCkCtVeh tCkCtVeh, char c) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected void preSaveValidation(CkCtVeh arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtVeh arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkCtVeh setCoreMstLocale(CoreMstLocale arg0, CkCtVeh arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}
	@Override
	public CkCtVeh findById(String s) throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	public CkCtVeh deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		return null;
	}

	@Override
	public CkCtVeh newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<CkCtMstVehType> getVehTypeByCompany(String companyId) throws Exception {
		List<CkCtMstVehType> result = new ArrayList<>();
		List<TCkCtVeh> list = ckCtVehDao.findVehTypeByCompany(companyId);
		if (list != null && list.size() > 0) {
			list.forEach(e -> {
				Hibernate.initialize(e.getTCkCtMstVehType());
				CkCtMstVehType vhType = new CkCtMstVehType(e.getTCkCtMstVehType());
				if (!result.contains(vhType)) {
					result.add(vhType);
				}
			});
		}

		return result;
	}
	private void retrieveChassisSize(TCkCtVeh tCkCtVeh, CkCtVeh ckCtVeh) {
		if (null != tCkCtVeh.getVhChassisNo() && null != tCkCtVeh.getTCkCtMstChassisType()
				&& null != tCkCtVeh.getTCoreAccn()) {
			try {
				List<TCkCtChassis> chassis = ckCtChassisDao.findExistingChassis(tCkCtVeh.getVhChassisNo(),
						tCkCtVeh.getTCkCtMstChassisType().getChtyId(), tCkCtVeh.getTCoreAccn().getAccnId());
				if (null != chassis && !ObjectUtils.isEmpty(chassis))
					tCkCtVeh.setVhChassisNo(chassis.get(0).getChsNo());
				else {
					JsonObject root = this.getAsJson(tCkCtVeh.getVhChassisNo());
					if (null == root) {
						// Chassis Size: For existing records that is not JSON, create a JSON object
						JSONObject chassisJson = new JSONObject();
						chassisJson.put("vhChassisNo", "OTHERS");
						chassisJson.put("vhChassisNoOth", ckCtVeh.getVhChassisNo());
						ckCtVeh.setVhChassisNoOth(ckCtVeh.getVhChassisNo());
						ckCtVeh.setVhChassisNo(chassisJson.toString());
					} else {
						// Chassis Size: For existing records that is JSON, extract values
						JsonElement vhChassisNo = root.get("vhChassisNo");
						JsonElement vhChassisNoOth = root.get("vhChassisNoOth");
						ckCtVeh.setVhChassisNo(vhChassisNo.getAsString());
						ckCtVeh.setVhChassisNoOth(vhChassisNoOth.getAsString());
					}
				}

			} catch (Exception ex) {
				LOG.error("ckCtChassisDao findDuplicateChassis ", ex);
			}
		} else {
			tCkCtVeh.setVhChassisNo(null);
		}
	}
	public JsonObject getAsJson(String json) {
		JsonObject root = null;
		try {
			root = JsonParser.parseString(json).getAsJsonObject();
		} catch (Exception ex) {
			return null;
		}
		return root;
	}
}
