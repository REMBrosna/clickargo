package com.guudint.clickargo.clictruck.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.common.dto.CkCtTrackDevice;
import com.guudint.clickargo.clictruck.common.dto.CkCtTrackDeviceEnum;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtTrackDevice;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.service.CkCtVehTrackService;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstTrackDeviceState;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstTrackDeviceState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.guudint.clickargo.master.enums.AccountTypes;
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
import com.vcc.camelone.locale.dto.CoreMstLocale;

public class CkCtTrackDeviceServiceImpl extends AbstractJobService<CkCtTrackDevice, TCkCtTrackDevice, String>
		implements ICkConstant, CkCtVehTrackService {

	private static Logger LOG = Logger.getLogger(CkCtTrackDeviceServiceImpl.class);

	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;
	
	@Autowired
	private CkCtVehServiceImpl ckCtVehServiceImpl;

	public CkCtTrackDeviceServiceImpl() {
		super("ckCtTrackDeviceDao", "CkCt Track Device", "TCkCtTrackDevice",
				"T_CK_CT_TRACK_DEVICE");
	}

	@Override
	public CkCtTrackDevice deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		return updateStatus(id, "delete");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtTrackDevice> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtTrackDevice ckCtVeh = whereDto(filterRequest);
		if (ckCtVeh == null) {
			throw new ProcessingException("whereDto null");
		}

		//
		try {
			CoreAccn coreAccn = clickargoAccnService.getAccountByType(AccountTypes.ACC_TYPE_SP);

			Principal principal = ckSession.getPrincipal();

			if (!coreAccn.getAccnId().equalsIgnoreCase(principal.getCoreAccn().getAccnId())) {
				throw new ProcessingException("Only GLI can access");
			}
		} catch (Exception e1) {

			// throw new ProcessingException(e1);
		}
		//
		filterRequest.setTotalRecords(countByAnd(ckCtVeh));
		List<CkCtTrackDevice> ckCtVehs = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTrackDevice> tCkCtVehs = findEntitiesByAnd(ckCtVeh, "from TCkCtTrackDevice o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtTrackDevice tCkCtVeh : tCkCtVehs) {
				CkCtTrackDevice dto = dtoFromEntity(tCkCtVeh, false);
				if (dto != null) {
					ckCtVehs.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtVehs;
	}

	@Override
	public CkCtTrackDevice findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtTrackDevice tCkCtVeh = dao.find(id);
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
	protected void _auditError(JobEvent jobEvent, CkCtTrackDevice ckCtVeh, Exception ex, Principal principal) {

	}

	@Override
	protected void _auditEvent(JobEvent jobEvent, CkCtTrackDevice ckCtVeh, Principal principal) {
		LOG.debug("_auditEvent");
		try {
			if (ckCtVeh == null) {
				throw new ParameterException("param dto null");
			}
			if (principal == null) {
				throw new ParameterException("param principal null");
			}
			Date now = new Date();
			TCoreAuditlog coreAuditLog = new TCoreAuditlog();
			coreAuditLog.setAudtId(String.valueOf(System.currentTimeMillis()));
			coreAuditLog.setAudtEvent(jobEvent.getDesc());
			coreAuditLog.setAudtTimestamp(now);
			Optional<String> opAccnId = Optional.ofNullable(principal.getCoreAccn().getAccnId());
			coreAuditLog.setAudtAccnid(opAccnId.isPresent() ? opAccnId.get() : DASH);
			coreAuditLog.setAudtUid(StringUtils.isEmpty(principal.getUserId()) ? DASH : principal.getUserId());
			coreAuditLog.setAudtUname(StringUtils.isEmpty(principal.getUserName()) ? DASH : principal.getUserName());
			coreAuditLog.setAudtRemoteIp(DASH);
			coreAuditLog.setAudtReckey(StringUtils.isEmpty(ckCtVeh.getTdId()) ? DASH : ckCtVeh.getTdId());
			coreAuditLog.setAudtParam1(DASH);
			coreAuditLog.setAudtParam2(DASH);
			coreAuditLog.setAudtParam3(DASH);
			coreAuditLog.setAudtRemarks(DASH);
			auditLogDao.add(coreAuditLog);
		} catch (Exception e) {
			LOG.error("_auditEvent", e);
		}
	}

	@Override
	protected CkCtTrackDevice _cancelJob(CkCtTrackDevice ckCtVeh, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtTrackDevice _completeJob(CkCtTrackDevice arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtTrackDevice _confirmJob(CkCtTrackDevice arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtTrackDevice _createJob(CkCtTrackDevice ckCtVeh, CkJob ckJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {
		if (ckCtVeh == null) {
			throw new ParameterException("param dto null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		// TODO
		// add Track Device here
		// not add CkCtTrackDevice, add TCkCtTrackDevice

		return add(ckCtVeh, principal);
	}

	@Override
	protected AbstractJobEvent<CkCtTrackDevice> _getJobEvent(JobEvent arg0, CkCtTrackDevice arg1, Principal arg2) {
		return null;
	}

	@Override
	protected CkCtTrackDevice _newJob(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtTrackDevice ckCtVeh = new CkCtTrackDevice();

		return ckCtVeh;
	}

	@Override
	protected CkCtTrackDevice _paidJob(CkCtTrackDevice arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtTrackDevice _payJob(CkCtTrackDevice arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtTrackDevice _rejectJob(CkCtTrackDevice arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtTrackDevice _submitJob(CkCtTrackDevice arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected Class<?>[] _validateGroupClass(JobEvent arg0) {
		return null;
	}

	@Override
	protected String formatOrderBy(String attribute) throws Exception {
		attribute = Optional.ofNullable(attribute).orElse("");
		attribute = attribute.replace("tckCtMstTrackDeviceState", "TCkCtMstTrackDeviceState")
				.replace("tckCtVeh", "TCkCtVeh").replace("tckCtMstVehType", "TCkCtMstVehType").replace("tcoreAccn", "TCoreAccn");
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
	protected CkCtTrackDevice dtoFromEntity(TCkCtTrackDevice tCkCtTrackDevice) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtTrackDevice == null) {
			throw new ParameterException("param entity null");
		}
		CkCtTrackDevice ckCtVeh = new CkCtTrackDevice(tCkCtTrackDevice);

		if (tCkCtTrackDevice.getTCkCtMstTrackDeviceState() != null) {
			ckCtVeh.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(tCkCtTrackDevice.getTCkCtMstTrackDeviceState()));
		}
		if (tCkCtTrackDevice.getTCkCtVeh() != null) {
			
			ckCtVeh.setTCkCtVeh(new CkCtVeh(tCkCtTrackDevice.getTCkCtVeh()));
			
			if (tCkCtTrackDevice.getTCkCtVeh().getTCkCtMstVehType() != null) {
				ckCtVeh.getTCkCtVeh().setTCkCtMstVehType(new CkCtMstVehType(tCkCtTrackDevice.getTCkCtVeh().getTCkCtMstVehType()));
			}
		}
		if (tCkCtTrackDevice.getTCoreAccn() != null) {
			ckCtVeh.setTCoreAccn(new CoreAccn(tCkCtTrackDevice.getTCoreAccn()));
		}
		return ckCtVeh;
	}

	protected CkCtTrackDevice dtoFromEntity(TCkCtTrackDevice tCkCtTrackDevice, boolean withData)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtTrackDevice == null) {
			throw new ParameterException("param entity null");
		}

		return this.dtoFromEntity(tCkCtTrackDevice);
	}

	@Override
	protected TCkCtTrackDevice entityFromDTO(CkCtTrackDevice ckCtVehDevice) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtVehDevice == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtTrackDevice tCkCtTrackDevice = new TCkCtTrackDevice(ckCtVehDevice);

		if (ckCtVehDevice.getTCkCtMstTrackDeviceState() != null) {
			tCkCtTrackDevice.setTCkCtMstTrackDeviceState(ckCtVehDevice.getTCkCtMstTrackDeviceState().toEntity(new TCkCtMstTrackDeviceState()));
		}

		if (ckCtVehDevice.getTCkCtVeh() != null) {
			
			tCkCtTrackDevice.setTCkCtVeh(ckCtVehDevice.getTCkCtVeh().toEntity(new TCkCtVeh()));
			
			if (ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType() != null) {
				tCkCtTrackDevice.getTCkCtVeh().setTCkCtMstVehType(ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType().toEntity(new TCkCtMstVehType()));
			}
		}
		
		if (ckCtVehDevice.getTCoreAccn() != null) {
			tCkCtTrackDevice.setTCoreAccn(ckCtVehDevice.getTCoreAccn().toEntity(new TCoreAccn()));
		}
		
		return tCkCtTrackDevice;
	}

	@Override
	protected String entityKeyFromDTO(CkCtTrackDevice ckCtVeh) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtVeh == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtVeh.getTdId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtTrackDevice ckCtVeh)
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
	protected HashMap<String, Object> getParameters(CkCtTrackDevice ckCtVehDevice) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (ckCtVehDevice == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();
		

			
			List<String> statusList = null;

			if (StringUtils.isNotBlank(ckCtVehDevice.getTdGpsImei())) {
				parameters.put("tdGpsImei", "%" + ckCtVehDevice.getTdGpsImei() + "%");
			}
			if (StringUtils.isNotBlank(ckCtVehDevice.getTCkCtVeh().getVhPlateNo())) {
				parameters.put("tdVehPlateNo", "%" + ckCtVehDevice.getTCkCtVeh().getVhPlateNo() + "%");
			}
			if (ckCtVehDevice.getTCoreAccn()!= null && StringUtils.isNotBlank(ckCtVehDevice.getTCoreAccn().getAccnName())) {
				parameters.put("accnName", "%" + ckCtVehDevice.getTCoreAccn().getAccnName() + "%");
			}
			
			if (ckCtVehDevice.getTCkCtVeh()!= null && ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType()!= null 
					&& StringUtils.isNotBlank(ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType().getVhtyId() )) {
				
				parameters.put("vhtyName", "%" + ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType().getVhtyId() + "%");
			}
			
			// 4 dates
			if (ckCtVehDevice.getTdDtCreate()!= null ) {
				parameters.put("tdDtCreate",  sdf.format(ckCtVehDevice.getTdDtCreate()));
			}
			if (ckCtVehDevice.getTdDtLupd()!= null ) {

				parameters.put("tdDtLupd",  sdf.format(ckCtVehDevice.getTdDtLupd()));

			}
			
			if (ckCtVehDevice.getTdDtActivate()!= null ) {
				parameters.put("tdDtDeactivate",  sdf.format(ckCtVehDevice.getTdDtActivate()));
			}
			
			if (ckCtVehDevice.getTdDtActivattion()!= null ) {
				parameters.put("tdDtActivattion",  sdf.format(ckCtVehDevice.getTdDtActivattion()));
			}
			
			if (ckCtVehDevice.getTdDtDeactivate()!= null ) {

				parameters.put("tdDtDeactivate",  sdf.format(ckCtVehDevice.getTdDtDeactivate()));

			}
			if (ckCtVehDevice.getTCkCtMstTrackDeviceState()!= null) {

				parameters.put("tdsName",  ckCtVehDevice.getTCkCtMstTrackDeviceState().getTdsId());
			}
			
			// History
			if(HISTORY.equalsIgnoreCase(ckCtVehDevice.getHistory() )) {
				// History;
				statusList = Arrays.asList(CkCtTrackDeviceEnum.DEACTIVATE.name());
			}else {
				statusList = Arrays.asList(CkCtTrackDeviceEnum.ACTIVATE.name(), CkCtTrackDeviceEnum.NEW.name());
			}
			
			parameters.put("historyId",statusList);

		
		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtTrackDevice ckCtVehDevice, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (ckCtVehDevice == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();
		

		if (StringUtils.isNotBlank(ckCtVehDevice.getTdGpsImei())) {
			condition.append(getOperator(wherePrinted) + " o.tdGpsImei like :tdGpsImei");
			wherePrinted = true;
		}
		
		if (StringUtils.isNotBlank(ckCtVehDevice.getTCkCtVeh().getVhPlateNo())) {
			condition.append(getOperator(wherePrinted) + " o.TCkCtVeh.vhPlateNo like :tdVehPlateNo");
			wherePrinted = true;
		}
		
		if (ckCtVehDevice.getTCkCtVeh()!= null && ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType()!= null 
				&& StringUtils.isNotBlank(ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType().getVhtyId() )) {
			condition.append(getOperator(wherePrinted) + " o.TCkCtVeh.TCkCtMstVehType.vhtyName like :vhtyName");
			wherePrinted = true;
		}
		
		if (ckCtVehDevice.getTCoreAccn()!= null && StringUtils.isNotBlank(ckCtVehDevice.getTCoreAccn().getAccnName())) {
			condition.append(getOperator(wherePrinted) + " o.TCoreAccn.accnName like :accnName");
			wherePrinted = true;
		}
		// 4 dates
		if (ckCtVehDevice.getTdDtCreate()!= null ) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(o.tdDtCreate,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "tdDtCreate");
			wherePrinted = true;
		}
		if (ckCtVehDevice.getTdDtLupd()!= null ) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(o.tdDtLupd,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "tdDtLupd");
			wherePrinted = true;
		}
		if (ckCtVehDevice.getTdDtActivate() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(o.tdDtActivate,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "tdDtActivate");
			wherePrinted = true;
		}
		if (ckCtVehDevice.getTdDtActivattion() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(o.tdDtActivattion,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "tdDtActivattion");
			wherePrinted = true;
		}
		if (ckCtVehDevice.getTdDtDeactivate()!= null ) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(o.tdDtDeactivate,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "tdDtDeactivate");
			wherePrinted = true;
		}
		if (ckCtVehDevice.getTCkCtMstTrackDeviceState()!= null) {
			condition.append(getOperator(wherePrinted) + " o.TCkCtMstTrackDeviceState.tdsName like :tdsName");
			wherePrinted = true;
		}
		
		// History
		if (ckCtVehDevice.getHistory() != null) {
			// display SUBMITTED
			condition.append(getOperator(wherePrinted) + " o.TCkCtMstTrackDeviceState.tdsId in :historyId" );
			wherePrinted = true;
		} 
		
		return condition.toString();
	}

	@Override
	protected TCkCtTrackDevice initEnity(TCkCtTrackDevice tCkCtTrackDevice) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtTrackDevice != null) {
			Hibernate.initialize(tCkCtTrackDevice.getTCkCtMstTrackDeviceState());
			if(null != tCkCtTrackDevice.getTCkCtVeh() ) {
				Hibernate.initialize(tCkCtTrackDevice.getTCkCtVeh());
			}
			if(null != tCkCtTrackDevice.getTCkCtVeh() && tCkCtTrackDevice.getTCkCtVeh().getTCkCtMstVehType() != null) {
				Hibernate.initialize(tCkCtTrackDevice.getTCkCtVeh().getTCkCtMstVehType());
			}
			Hibernate.initialize(tCkCtTrackDevice.getTCkCtVeh());
			Hibernate.initialize(tCkCtTrackDevice.getTCoreAccn());
		}
		return tCkCtTrackDevice;
	}

	@Override
	protected CkCtTrackDevice preSaveUpdateDTO(TCkCtTrackDevice tCkCtTrackDevice, CkCtTrackDevice ckCtVeh)
			throws ParameterException, ProcessingException {
		if (tCkCtTrackDevice == null)
			throw new ParameterException("param storedEntity null");
		if (ckCtVeh == null)
			throw new ParameterException("param dto null");

		ckCtVeh.setTdUidCreate(tCkCtTrackDevice.getTdUidCreate());
		ckCtVeh.setTdDtCreate(tCkCtTrackDevice.getTdDtCreate());
		
		return ckCtVeh;
	}

	@Override
	protected void preSaveValidation(CkCtTrackDevice arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtTrackDevice arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkCtTrackDevice setCoreMstLocale(CoreMstLocale arg0, CkCtTrackDevice arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtTrackDevice updateEntity(ACTION action, TCkCtTrackDevice tCkCtTrackDevice, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtTrackDevice == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			
			if(StringUtils.isBlank(tCkCtTrackDevice.getTdId())) {
				tCkCtTrackDevice.setTdId(CkUtil.generateId(TCkCtTrackDevice.PREFIX_ID));
			}
			tCkCtTrackDevice.setTdUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			tCkCtTrackDevice.setTdDtCreate(date);
			tCkCtTrackDevice.setTdDtLupd(date);
			tCkCtTrackDevice.setTdUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		case MODIFY:
			tCkCtTrackDevice.setTdDtLupd(date);
			tCkCtTrackDevice.setTdUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		default:
			break;
		}
		return tCkCtTrackDevice;
	}

	@Override
	protected TCkCtTrackDevice updateEntityStatus(TCkCtTrackDevice tCkCtTrackDevice, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtTrackDevice == null) {
			throw new ParameterException("entity param null");
		}
		tCkCtTrackDevice.setTdStatus(status);
		return tCkCtTrackDevice;
	}

	@Override
	protected CkCtTrackDevice whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtTrackDevice ckCtTrackDevice = new CkCtTrackDevice();
		ckCtTrackDevice.setTCoreAccn( new CoreAccn());
		ckCtTrackDevice.setTCkCtVeh( new CkCtVeh());

		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}
			
			if ("tdGpsImei".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtTrackDevice.setTdGpsImei(entityWhere.getValue());
				
				
			} else if ("TCkCtVeh.vhPlateNo".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtTrackDevice.getTCkCtVeh().setVhPlateNo(entityWhere.getValue());
				
			} else if ("TcoreAccn.accnName".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtTrackDevice.getTCoreAccn().setAccnName(entityWhere.getValue());
				
			} else if ("TCkCtVeh.tckCtMstVehType.vhtyName".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtTrackDevice.getTCkCtVeh().setTCkCtMstVehType(new CkCtMstVehType(entityWhere.getValue()));
				
			} else if ("tdDtCreate".equalsIgnoreCase(entityWhere.getAttribute())) {
				try {
					ckCtTrackDevice.setTdDtCreate(sdf.parse(entityWhere.getValue()));
				} catch (Exception e) {
					LOG.error(e);
				}
			} else if ("tdDtLupd".equalsIgnoreCase(entityWhere.getAttribute())) {
				try {
					ckCtTrackDevice.setTdDtLupd(sdf.parse(entityWhere.getValue()));
				} catch (Exception e) {
					LOG.error(e);
				}
			} else if ("tdDtActivate".equalsIgnoreCase(entityWhere.getAttribute())) {
				try {
					ckCtTrackDevice.setTdDtActivate(sdf.parse(entityWhere.getValue()));
				} catch (Exception e) {
					LOG.error(e);
				}
			} else if ("tdDtActivattion".equalsIgnoreCase(entityWhere.getAttribute())) {
				try {
					ckCtTrackDevice.setTdDtActivattion(sdf.parse(entityWhere.getValue()));
				} catch (Exception e) {
					LOG.error(e);
				}
			} else if ("tdDtDeactivate".equalsIgnoreCase(entityWhere.getAttribute())) {
				try {
					ckCtTrackDevice.setTdDtDeactivate(sdf.parse(entityWhere.getValue()));
				} catch (Exception e) {
					LOG.error(e);
				}
			} else if ("TCkCtMstTrackDeviceState.tdsName".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtTrackDevice.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(entityWhere.getValue()));
			}
	

			// history toggle
			if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
				
				ckCtTrackDevice.setHistory(entityWhere.getValue());
				/*-
				if(!HISTORY.equals(entityWhere.getValue())) {
					// not history
					ckCtTrackDevice.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(CkCtTrackDeviceEnum.ACTIVATE.name()));
				} else {
					ckCtTrackDevice.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(CkCtTrackDeviceEnum.DEACTIVATE.name()));
				}
				*/
			}
		}
		
		return ckCtTrackDevice;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTrackDevice updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		
		CkCtTrackDevice ckCtVehDevice = findById(id);
		
		if (ckCtVehDevice == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		
		if(null == ckCtVehDevice.getTCkCtVeh() || StringUtils.isBlank(ckCtVehDevice.getTCkCtVeh().getVhId())) {
			throw new EntityNotFoundException("veh id is null" );
		}
		
		if (CkCtTrackDeviceEnum.ACTIVATE.name().equalsIgnoreCase(status)) {
			ckCtVehDevice.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(CkCtTrackDeviceEnum.ACTIVATE.name()));
			updateVehchle(ckCtVehDevice.getTCkCtVeh().getVhId(), ckCtVehDevice.getTdGpsImei(), principal);
			ckCtVehDevice.setTdDtActivattion(new Date());
		} else if (CkCtTrackDeviceEnum.DEACTIVATE.name().equalsIgnoreCase(status)) {
			ckCtVehDevice.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(CkCtTrackDeviceEnum.DEACTIVATE.name()));
			updateVehchle(ckCtVehDevice.getTCkCtVeh().getVhId(), null, principal);
			ckCtVehDevice.setTdDtDeactivate(new Date());
		} 
		return update(ckCtVehDevice, principal);
	}

	private void updateVehchle(String vechId, String gpsImei, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		
		CkCtVeh ckCtVeh = ckCtVehServiceImpl.find(new CkCtVeh(vechId));
		ckCtVeh.setVhGpsImei(gpsImei);
		ckCtVehServiceImpl.update(ckCtVeh, principal);
		
	}


}
