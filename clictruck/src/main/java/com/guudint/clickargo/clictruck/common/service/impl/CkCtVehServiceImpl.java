package com.guudint.clickargo.clictruck.common.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guudint.clickargo.clictruck.common.constant.CkCtVehConstant;
import com.guudint.clickargo.clictruck.common.dao.*;
import com.guudint.clickargo.clictruck.common.dao.impl.CkCtDeptVehDaoImpl;
import com.guudint.clickargo.clictruck.common.dto.*;
import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.service.CkCtVehService;
import com.guudint.clickargo.clictruck.common.validator.CkCtVehValidator;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehState;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.common.*;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
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
import com.vcc.camelone.master.dto.MstAccnType;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
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

public class CkCtVehServiceImpl extends AbstractClickCargoEntityService<TCkCtVeh, String, CkCtVeh>
		implements ICkConstant, CkCtVehService {

	private static Logger LOG = Logger.getLogger(CkCtVehServiceImpl.class);

	@Autowired
	private CkCtVehDao ckCtVehDao;

	@Autowired
	private CkCtVehValidator ckCtVehValidator;

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Autowired
	private CkJobTruckDao ckJobTruckDaoImpl;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	private CkCtDrvDao ckCtDrvDao;

	@Autowired
	private CkCtChassisDao ckCtChassisDao;

	@Autowired
	private CkCtVehExtDao ckCtVehExtDao;

	@Autowired
	private CkCtVehExtServiceImpl ckCtVehExtServiceImpl;

	@Autowired
	private CkJobTruckServiceUtil jobTruckServiceUtil;

	@Autowired
	private CkCtDeptVehDao deptVehDao;

	@Autowired
	private CkCtDeptVehDao vehDeptDao;
	@Autowired
	private CkCtDeptVehDaoImpl ckCtDeptVehDao;
	@Autowired
	@Qualifier("ckCtAlertDao")
	private GenericDao<TCkCtAlert, String> ckCtAlertDao;

	public CkCtVehServiceImpl() {
		super(CkCtVehConstant.Table.NAME_DAO, CkCtVehConstant.Prefix.AUDIT_TAG, CkCtVehConstant.Table.NAME_ENTITY,
				CkCtVehConstant.Table.NAME);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtVeh deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
        try {
            return updateStatus(id, "delete");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtVeh> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		if (filterRequest == null) {
			throw new ParameterException("Parameter 'filterRequest' is null");
		}

		CkCtVeh ckCtVeh = whereDto(filterRequest);
		if (ckCtVeh == null) {
			throw new ProcessingException("whereDto returned null");
		}

		filterRequest.setTotalRecords(countByAnd(ckCtVeh));
		List<CkCtVeh> ckCtVehs = new ArrayList<>();

		try {
			String orderByClause = formatOrderBy(filterRequest.getOrderBy().toString());
			List<TCkCtVeh> tCkCtVehs = findEntitiesByAnd(ckCtVeh, "from TCkCtVeh o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			Principal principal = principalUtilService.getPrincipal();

			List<TCkCtVeh> modResult = tCkCtVehs;

			if (ckCtVeh.getDepartment() != null){
				if (ckCtVeh.getDepartment().equalsIgnoreCase("Y")){
					if (principal != null) {
						CkCtDept userDept = jobTruckServiceUtil.getPrincipalDepartment(principal);
						if (userDept != null) {
							// Has Department:
							// User views all trucks within the same department
							// User can also view trucks without department
							List<TCkCtDeptVeh> deptVehList = deptVehDao.getVehiclesByDept(userDept.getDeptId());
							List<TCkCtVeh> inDeptVehList = deptVehList.stream().map( depVeh -> depVeh.getTCkCtVeh()).collect(Collectors.toList());

							List<TCkCtVeh> noDeptVeh = ckCtVehDao.findVehNotInDepartment(principal.getUserAccnId());

							List<TCkCtVeh> mregedVehList = new ArrayList<>(inDeptVehList);
							mregedVehList.addAll(noDeptVeh);

							modResult = tCkCtVehs.stream().filter(mregedVehList::contains).collect(Collectors.toList());

						} else {
							// No Department: User views all trucks

						}
					}
				}
			}
			for (TCkCtVeh tCkCtVeh : modResult) {
				CkCtVeh dto = dtoFromEntity(tCkCtVeh, false);
				if (dto != null) {
					ckCtVehs.add(dto);
				}
			}

			this.addDeptmentInfoToVehicle(ckCtVehs, principal.getUserAccnId());

		} catch (Exception e) {
			LOG.error("Exception in filterBy: ", e);
		}
		return ckCtVehs;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtVeh findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtVeh tCkCtVeh = dao.find(id);
			if (tCkCtVeh == null) {
				throw new EntityNotFoundException("id:" + id);
			}
			initEnity(tCkCtVeh);
			return dtoFromEntity(tCkCtVeh);
		} catch (Exception e) {
			LOG.error("findById", e);
		}
		return null;
	}

	@Override
	public CkCtVeh newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtVeh ckCtVeh = new CkCtVeh();
		ckCtVeh.setTCkCtMstChassisType(new CkCtMstChassisType());
		CkCtMstVehState state = new CkCtMstVehState();
		state.setVhstId(CkCtVehConstant.State.UNASSIGNED);
		ckCtVeh.setTCkCtMstVehState(state);
		ckCtVeh.setTCkCtMstVehType(new CkCtMstVehType());
		ckCtVeh.setTCoreAccn(principal.getCoreAccn());

		// Initialize for maintenance and expiry object
//		CkCtVehExt maintenance = new CkCtVehExt();
//		CkCtVehExtId mntId = new CkCtVehExtId();
//		maintenance.setId(mntId);
//
//		CkCtVehExt expiry = new CkCtVehExt();
//		CkCtVehExtId expId = new CkCtVehExtId();
//		expiry.setId(expId);
//
//		ckCtVeh.setMaintenance(maintenance);
//		ckCtVeh.setExpiry(expiry);
		return ckCtVeh;
	}

	@Override
	public CkCtVeh add(CkCtVeh dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (dto == null) throw new ParameterException("param dto null;");
		if (principal == null) throw new ParameterException("param principal null");

		try {
			List<ValidationError> validationErrors = ckCtVehValidator.validateCreate(dto, principal);
			dto.setVhId(CkUtil.generateId(CkCtVehConstant.Prefix.PREFIX_CK_CT_VEH));
			dto.setVhStatus(RecordStatus.ACTIVE.getCode());
			dto.setTCoreAccn(principal.getCoreAccn());

			if (StringUtils.isNotBlank(dto.getBase64File()) && StringUtils.isNotBlank(dto.getVhPhotoName())) {
				byte[] data = Base64.getDecoder().decode(dto.getBase64File());
				String basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
				if (StringUtils.isBlank(basePath)) {
					throw new ProcessingException("basePath is not configured");
				}
				String filePath = FileUtil.saveAttachment(basePath.concat(dto.getVhPhotoName()), data);
				dto.setVhPhotoLoc(filePath);
			}
			if (!validationErrors.isEmpty()) {
				throw new ValidationException(this.validationErrorMap(validationErrors));
			} else {
				super.add(dto, principal);
			}

//			processVehExt(dto, principal, dto.getExpiry());
//			processVehExt(dto, principal, dto.getMaintenance());

			return dto;
		} catch (ParameterException | ProcessingException | ValidationException | EntityNotFoundException e) {
			LOG.error("Error in add method: ", e);
			throw e;
		} catch (Exception e) {
			LOG.error("Unexpected error: ", e);
			throw new ProcessingException(e);
		}
	}
	private void processVehExt(CkCtVeh dto, Principal principal, CkCtVehExt vehExt) throws ProcessingException, ValidationException, ParameterException, EntityNotFoundException {
		if (vehExt != null) {
			CkCtVehExt ckCtVehExt = new CkCtVehExt();
			BeanUtils.copyProperties(vehExt, ckCtVehExt);

			CkCtVehExtId id = new CkCtVehExtId();
			id.setVextId(dto.getVhId());
			id.setVextParam(vehExt.getId().getVextParam());

			ckCtVehExt.setId(id);
			ckCtVehExt.setTCkCtVeh(dto);

			ckCtVehExtServiceImpl.addObj(ckCtVehExt, principal);
		}
	}

	protected String formatOrderBy(String attribute) throws Exception {
		attribute = Optional.ofNullable(attribute).orElse("");
		attribute = attribute.replace("tckCtMstChassisType", "TCkCtMstChassisType")
				.replace("tckCtMstVehState", "TCkCtMstVehState").replace("tckCtMstVehType", "TCkCtMstVehType");
		return attribute;
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

//		try {
//			// Check if there is an existing vehExt maintenance for this vehicle
//			CkCtVehExt maintenance = ckCtVehExtServiceImpl.findVehExtByVehAndKey(ckCtVeh.getVhId(),
//					"VEHICLE_MAINTENANCE");
//			// just init if null to be used in fe
//			// just init if null to be used in fe
//			CkCtVehExt newMnt = new CkCtVehExt();
//			CkCtVehExtId mntpId = new CkCtVehExtId();
//			mntpId.setVextId(ckCtVeh.getVhId());
//			newMnt.setId(mntpId);
//			ckCtVeh.setMaintenance(maintenance == null ? newMnt : maintenance);
//
//			// Check if there is an existing vehExt expiry for this vehicle
//			CkCtVehExt expiry = ckCtVehExtServiceImpl.findVehExtByVehAndKey(ckCtVeh.getVhId(), "EXP_");
//			// just init if null to be used in fe
//			CkCtVehExt newExp = new CkCtVehExt();
//			CkCtVehExtId expId = new CkCtVehExtId();
//			expId.setVextId(ckCtVeh.getVhId());
//			newExp.setId(expId);
//			ckCtVeh.setExpiry(expiry == null ? newExp : expiry);
//
//			// retrieve the department if there is
//			TCkCtDeptVeh deptVeh = vehDeptDao.getVehicleDeptByVeh(tCkCtVeh.getVhId());
//			if (deptVeh != null) {
//				Hibernate.initialize(deptVeh.getTCkCtDept());
//				ckCtVeh.setDepartment(deptVeh.getTCkCtDept().getDeptName());
//			}
//		} catch (Exception ex) {
//			throw new ProcessingException(ex);
//		}

		return ckCtVeh;
	}

	/**
	 * @param tCkCtVeh
	 * @param ckCtVeh
	 */
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
		// If empty or blank, set to null
		if (StringUtils.isEmpty(ckCtVeh.getVhChassisNo())) {
			ckCtVeh.setVhChassisNo(null);
			// If, OTHERS selected, create a JSON object.
		} else if (ckCtVeh.getVhChassisNo().equalsIgnoreCase("OTHERS")) {
			JSONObject chassisJson = new JSONObject();
			chassisJson.put("vhChassisNo", "OTHERS");
			chassisJson.put("vhChassisNoOth",
					StringUtils.isEmpty(ckCtVeh.getVhChassisNoOth()) ? null : ckCtVeh.getVhChassisNoOth());
			tCkCtVeh.setVhChassisNo(chassisJson.toString());
		}
		return tCkCtVeh;
	}

	@Override
	protected String entityKeyFromDTO(CkCtVeh ckCtVeh) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtVeh == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtVeh.getVhId();
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

		Principal principal = principalUtilService.getPrincipal();

		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();

		if(principal != null) {
			CoreAccn accn = principal.getCoreAccn();
			// only Trucking Operator should be able to see the list
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())
					|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO_WJ.name()) ) {
				parameters.put(CkCtVehConstant.ColumnParam.VH_COMPANY, accn.getAccnId());
			}
		}

		if (StringUtils.isNotBlank(ckCtVeh.getVhPlateNo())) {
			parameters.put(CkCtVehConstant.ColumnParam.VH_PLATE_NO, "%" + ckCtVeh.getVhPlateNo() + "%");
		}
		if (Objects.nonNull(ckCtVeh.getTCkCtMstVehType())) {
			if (StringUtils.isNotBlank(ckCtVeh.getTCkCtMstVehType().getVhtyId())) {
				parameters.put("vhtyId", ckCtVeh.getTCkCtMstVehType().getVhtyId());
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
			parameters.put(CkCtVehConstant.ColumnParam.VH_STATUS, ckCtVeh.getVhStatus());
		}

		parameters.put("validStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.DEACTIVATE.getCode(), RecordStatus.INACTIVE.getCode()));

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtVeh ckCtVeh, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (ckCtVeh == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();

		Principal principal = principalUtilService.getPrincipal();

		if(principal != null) {
			CoreAccn accn = principal.getCoreAccn();
			// only Trucking Operator should be able to see the list
			if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name()) ||
					accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO_WJ .name())) {

				condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_COMPANY + EQUAL
						+ CkCtVehConstant.ColumnParam.VH_COMPANY);
				wherePrinted = true;
			} else if ( ! accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {
				// not TO and TO_WJ, can not view truck at all
				condition.append(getOperator(wherePrinted) + " 1 = 0 " );
				wherePrinted = true;
			}
		}

		if (StringUtils.isNotBlank(ckCtVeh.getVhPlateNo())) {
			condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_PLATE_NO + CONTAIN
					+ CkCtVehConstant.ColumnParam.VH_PLATE_NO);
			wherePrinted = true;
		}
		if (Objects.nonNull(ckCtVeh.getTCkCtMstVehType())) {
			if (StringUtils.isNotBlank(ckCtVeh.getTCkCtMstVehType().getVhtyId())) {
				condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_TYPE + EQUAL
						+ "vhtyId");
				wherePrinted = true;
			}
		}
		if (StringUtils.isNotBlank(ckCtVeh.getVhId())) {
			condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_ID + EQUAL
					+ CkCtVehConstant.ColumnParam.VH_ID);
			wherePrinted = true;
		}
		if (ckCtVeh.getVhClass() != null) {
			condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_CLASS + EQUAL
					+ CkCtVehConstant.ColumnParam.VH_CLASS);
			wherePrinted = true;
		}
		if (ckCtVeh.getVhVolume() != null) {
			condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_VOLUME + EQUAL
					+ CkCtVehConstant.ColumnParam.VH_VOLUME);
			wherePrinted = true;
		}
		if (ckCtVeh.getVhWeight() != null) {
			condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_WEIGHT + EQUAL
					+ CkCtVehConstant.ColumnParam.VH_WEIGHT);
			wherePrinted = true;
		}
		if (ckCtVeh.getVhDtCreate() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtVehConstant.Column.VH_DT_CREATE + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtVehConstant.ColumnParam.VH_DT_CREATE);
			wherePrinted = true;
		}
		if (ckCtVeh.getVhDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtVehConstant.Column.VH_DT_LUPD + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtVehConstant.ColumnParam.VH_DT_LUPD);
			wherePrinted = true;
		}

		Optional<CkCtMstVehState> opVehState = Optional.ofNullable(ckCtVeh.getTCkCtMstVehState());
		if (opVehState.isPresent()) {
			if (!StringUtils.isBlank(opVehState.get().getVhstId())) {
				condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_STATE + EQUAL
						+ CkCtVehConstant.ColumnParam.VH_STATE);
				wherePrinted = true;
			}
		}

		if (ckCtVeh.getVhStatus() != null) {
			condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_STATUS + EQUAL
					+ CkCtVehConstant.ColumnParam.VH_STATUS);
			wherePrinted = true;
		}

		condition.append(getOperator(wherePrinted) + CkCtVehConstant.Column.VH_STATUS + " IN :validStatus");

		return condition.toString();
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
	protected TCkCtVeh updateEntity(ACTION action, TCkCtVeh tCkCtVeh, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtVeh == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			tCkCtVeh.setVhUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			tCkCtVeh.setVhDtCreate(date);
			tCkCtVeh.setVhDtLupd(date);
			tCkCtVeh.setVhUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		case MODIFY:
			tCkCtVeh.setVhDtLupd(date);
			tCkCtVeh.setVhUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		default:
			break;
		}
		return tCkCtVeh;
	}

	@Override
	protected TCkCtVeh updateEntityStatus(TCkCtVeh tCkCtVeh, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtVeh == null) {
			throw new ParameterException("entity param null");
		}
		tCkCtVeh.setVhStatus(status);
		return tCkCtVeh;
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
			} else if ("o.tckCtMstVehType.vhtyName".equalsIgnoreCase(attribute)) {
				ckCtMstVehType.setVhtyId(entityWhere.getValue());
			} else if (CkCtVehConstant.Column.VH_CLASS.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhClass(Byte.parseByte(entityWhere.getValue()));
			} else if (CkCtVehConstant.Column.VH_VOLUME.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhVolume(Integer.parseInt(entityWhere.getValue()));
			} else if (CkCtVehConstant.Column.VH_WEIGHT.equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhWeight(Integer.parseInt(entityWhere.getValue()));
			}  else if (CkCtVehConstant.Column.VH_DT_CREATE.equalsIgnoreCase(attribute)) {
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
			}else if (entityWhere.getAttribute().equalsIgnoreCase("department")){
				ckCtVeh.setDepartment("Y");
			} else if (CkCtVehConstant.Column.VH_COMPANY_ACCN_TYPE.equalsIgnoreCase(attribute)){
				MstAccnType mstAccnType = new MstAccnType();
				mstAccnType.setAtypId(entityWhere.getValue());
				coreAccn.setTMstAccnType(mstAccnType);
			}
		}
		return ckCtVeh;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtVeh updateStatus(String id, String status)
			throws Exception {
		LOG.info("updateStatus");

		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("Principal is null");
		}

		CkCtVeh ckCtVeh = findById(id);
		if (ckCtVeh == null) {
			throw new EntityNotFoundException("Vehicle with id: " + id + " not found");
		}

		switch (status.toLowerCase()) {
			case "deactive":
				ckCtVeh.setVhStatus(RecordStatusNew.DEACTIVE.getCode());
				deepDeleteApp(id, RecordStatusNew.DEACTIVE.getCode());
				break;
			case "delete":
				ckCtVeh.setVhStatus(RecordStatusNew.DELETE.getCode());
				deepDeleteApp(id, RecordStatusNew.DELETE.getCode());
				break;
			case "active":
				ckCtVeh.setVhStatus(RecordStatus.ACTIVE.getCode());
				deepDeleteApp(id, RecordStatus.ACTIVE.getCode());
				break;
			default:
				throw new ParameterException("Invalid status: " + status);
		}
		return update(ckCtVeh, principal);
	}
	private void deepDeleteApp(String key, Character status) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("vehId", key);
		param.put("status", status);
		String updateAlertStatus = "UPDATE TCkCtAlert SET altStatus = :status WHERE altReferId = :vehId AND altStatus = 'A'";
		ckCtAlertDao.executeUpdate(updateAlertStatus, param);
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

//	@Override
//	public Object addObj(Object object, Principal principal)
//			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
//		CkCtVeh ckCtVeh = (CkCtVeh) object;
//		CkCtVehExt expiry = ckCtVeh.getExpiry();
//		CkCtVehExt maintenance = ckCtVeh.getMaintenance();
//
//		// Validate the main object
//		List<ValidationError> validationErrors = ckCtVehValidator.validateCreate(ckCtVeh, principal);
//
//		// Validate and add expiry extension if present
//		if (expiry == null) {
//			validateVehExt(expiry, validationErrors, "EXP_");
//		}
//
//		// Validate and add maintenance extension if present
//		if (maintenance == null) {
//			validateVehExt(maintenance, validationErrors, "MNT_");
//		}
//
//		// Check for validation errors
//		if (!validationErrors.isEmpty()) {
//			throw new ValidationException(this.validationErrorMap(validationErrors));
//		}
//
//		// Add the main object
//		return add(ckCtVeh, principal);
//	}
	private void validateVehExt(CkCtVehExt vehExt, List<ValidationError> validationErrors, String prefix) {
		if (vehExt == null){
			vehExt = new CkCtVehExt();
			if (vehExt.getVextMonitorMthd() == null) {
				validationErrors.add(new ValidationError("", prefix + "vextMonitorMthd", "Monitor method cannot be empty"));
			}
			if (vehExt.getVextMonitorValue() == null) {
				validationErrors.add(new ValidationError("", prefix + "vextMonitorValue", "Monitor Value cannot be empty"));
			}
			if (vehExt.getVextNotifyEmail() == null) {
				validationErrors.add(new ValidationError("", prefix + "notifyBy", "Notify by cannot be empty"));
			}
			if (vehExt.getVextNotify() == null) {
				validationErrors.add(new ValidationError("", prefix + "vextNotify", "Notify method cannot be empty"));
			}
		}

	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object updateObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtVeh ckCtVeh = (CkCtVeh) object;
		// If empty or blank, set to null
		if (StringUtils.isEmpty(ckCtVeh.getVhChassisNo())) {
			ckCtVeh.setVhChassisNo(null);
			// If, OTHERS selected, create a JSON object.
		} else if (ckCtVeh.getVhChassisNo().equalsIgnoreCase("OTHERS")) {
			JSONObject chassisJson = new JSONObject();
			chassisJson.put("vhChassisNo", "OTHERS");
			chassisJson.put("vhChassisNoOth",
					StringUtils.isEmpty(ckCtVeh.getVhChassisNoOth()) ? null : ckCtVeh.getVhChassisNoOth());
			ckCtVeh.setVhChassisNo(chassisJson.toString());
		}
//		CkCtVehExt expiry = ckCtVeh.getExpiry();
//		CkCtVehExt maintenance = ckCtVeh.getMaintenance();
		List<ValidationError> validationErrors = ckCtVehValidator.validateUpdate(ckCtVeh, principal);
		// Validate and add expiry extension if present
//		if (expiry == null) {
//			validateVehExt(expiry, validationErrors, "EXP_");
//		}
//
//		// Validate and add maintenance extension if present
//		if (maintenance == null) {
//			validateVehExt(maintenance, validationErrors, "MNT_");
//		}

		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			ckCtVeh = super.update(ckCtVeh, principal);
		}

		// process for extension if have
//		if (ckCtVeh.getMaintenance() != null) {
//			ckCtVeh.getMaintenance().getId().setVextId(ckCtVeh.getVhId());
//			ckCtVeh.getMaintenance().setTCkCtVeh(ckCtVeh);
//			ckCtVeh.getMaintenance().setExtType("MNT");
//			ckCtVeh.setMaintenance((CkCtVehExt) ckCtVehExtServiceImpl.updateObj(ckCtVeh.getMaintenance(), principal));
//		}
//
//		if (ckCtVeh.getExpiry() != null) {
//			ckCtVeh.getExpiry().getId().setVextId(ckCtVeh.getVhId());
//			ckCtVeh.getExpiry().setTCkCtVeh(ckCtVeh);
//			ckCtVeh.getExpiry().setExtType("EXP");
//			ckCtVeh.setExpiry((CkCtVehExt) ckCtVehExtServiceImpl.updateObj(ckCtVeh.getExpiry(), principal));
//		}

		return ckCtVeh;
	}

	@Override
	public boolean isVehicleFree(String id, boolean isMobileJob, List<String> validStates)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		// If there is still at least one record that the driver is assigned to with the
		// jobstate = ASG
		// driver is not considered free (mobile enabled job only)
		String hql = "from TCkJobTruck o where o.TCkCtVeh.vhId=:vehRecId " + " and o.jobMobileEnabled= :mobileEnabled "
				+ " and o.TCkJob.TCkMstJobState.jbstId in (:jobStates)";
		Map<String, Object> params = new HashMap<>();
		params.put("vehRecId", id);
		params.put("jobStates", validStates);
		params.put("mobileEnabled", isMobileJob ? 'Y' : 'N');
		List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hql, params);
		if (list != null && list.size() > 0) {
			return false;
		}
		return true;
	}

	@Transactional
	public List<CkCtVeh> associatedVehicle(String drvId, String vehTypeId) throws Exception {
		LOG.info("associatedVehicle");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}

		List<CkCtVeh> listCkCtVeh = new ArrayList<>();

		// get vehicle on TCkCtVeh with state UNASSIGNED, COMPANY ID and VEH TYPE
		List<TCkCtVeh> ckCtVehs = ckCtVehDao.findVehTypeByCompany(principal.getUserAccnId(),
				Arrays.asList(VehStates.UNASSIGNED.name()));
		if (!ckCtVehs.isEmpty()) {
			for (TCkCtVeh tCkCtVeh : ckCtVehs) {
				// removed vehicle type, because vehicle type is not set on job submission for
				// xml
				listCkCtVeh.add(dtoFromEntity(tCkCtVeh));
			}
		}

		// load the vehicles associated to the driver for jobs that are not yet
		// started/ONGOING
		Optional<TCkCtDrv> opCkCtDrv = Optional.ofNullable(ckCtDrvDao.find(drvId));
		if (opCkCtDrv.isPresent()) {
			// get vehicle on TCkJobTruck with drvId and vehTypeId that are not yet in
			// ONGOING
			List<TCkJobTruck> listJobTruck = ckJobTruckDaoImpl.findByDrvMobileId(opCkCtDrv.get().getDrvMobileId(),
					Arrays.asList(JobStates.ASG.name()));
			if (!listJobTruck.isEmpty()) {
				for (TCkJobTruck tCkJobTruck : listJobTruck) {
					if (tCkJobTruck.getTCkCtMstVehType() != null
							&& tCkJobTruck.getTCkCtMstVehType().getVhtyId().equalsIgnoreCase(vehTypeId)) {
						if (tCkJobTruck.getTCkCtVeh() != null) {
							Optional<CkCtVeh> opFoundVeh = listCkCtVeh.stream().filter(itm -> StringUtils
									.equalsIgnoreCase(itm.getVhId(), tCkJobTruck.getTCkCtVeh().getVhId())).findFirst();
							if (!opFoundVeh.isPresent())
								listCkCtVeh.add(dtoFromEntity(tCkJobTruck.getTCkCtVeh()));
						}
					}
				}
			}
		}

		return listCkCtVeh;
	}

	/**
	 *
	 * @param json
	 * @return
	 */
	public JsonObject getAsJson(String json) {
		JsonObject root = null;
		try {
			root = JsonParser.parseString(json).getAsJsonObject();
		} catch (Exception ex) {
			return null;
		}
		return root;
	}

	private void addDeptmentInfoToVehicle(List<CkCtVeh> ckCtVehs, String accnId) throws Exception {

		if( ckCtVehs == null || ckCtVehs.size() == 0) {
			return;
		}

		List<TCkCtDeptVeh> deptVehList = ckCtDeptVehDao.getVehiclesByAccnDept(accnId);

		for(CkCtVeh veh: ckCtVehs) {
			deptVehList.forEach( dv -> {
				if( veh.getVhId().equalsIgnoreCase(dv.getTCkCtVeh().getVhId())) {
					veh.setDepartment(dv.getTCkCtDept().getDeptName());
					veh.setTCkCtDept(new CkCtDept(dv.getTCkCtDept()));
				}
			});
		}
	}
}
