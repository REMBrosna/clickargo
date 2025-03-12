package com.guudint.clickargo.clictruck.sage.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.sage.dto.CkCtSage;
import com.guudint.clickargo.clictruck.sage.model.TCkCtSage;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.vcc.camelone.cac.model.Principal;
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

public class SageEntityServiceImpl extends AbstractJobService<CkCtSage, TCkCtSage, String>
		implements ICkConstant {

	private static Logger LOG = Logger.getLogger(SageEntityServiceImpl.class);

	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;



	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	public SageEntityServiceImpl() {
		super("ckCtSageDao", "CKCT SAGE", "TCkCtSage","T_CK_CT_SAGE");
	}

	@Override
	public CkCtSage deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		return null;
	}

	@Override
	public List<CkCtSage> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtSage CkCtSage = whereDto(filterRequest);
		if (CkCtSage == null) {
			throw new ProcessingException("whereDto null");
		}
		filterRequest.setTotalRecords(countByAnd(CkCtSage));
		List<CkCtSage> CkCtSages = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtSage> tCkCtSages = findEntitiesByAnd(CkCtSage, "from TCkCtSage o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtSage tCkCtSage : tCkCtSages) {
				CkCtSage dto = dtoFromEntity(tCkCtSage, false);
				if (dto != null) {
					CkCtSages.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return CkCtSages;
	}

	@Override
	public CkCtSage findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtSage tCkCtSage = dao.find(id);
			if (tCkCtSage == null) {
				throw new EntityNotFoundException("id:" + id);
			}
			initEnity(tCkCtSage);
			return dtoFromEntity(tCkCtSage);
		} catch (Exception e) {
			LOG.error("findById", e);
		}
		return null;
	}

	@Override
	protected void _auditError(JobEvent jobEvent, CkCtSage CkCtSage, Exception ex, Principal principal) {

	}

	@Override
	protected void _auditEvent(JobEvent jobEvent, CkCtSage CkCtSage, Principal principal) {
		LOG.debug("_auditEvent");
		try {
			if (CkCtSage == null) {
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
			coreAuditLog.setAudtReckey(StringUtils.isEmpty(CkCtSage.getSageId() ) ? DASH : CkCtSage.getSageId());
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
	protected CkCtSage _cancelJob(CkCtSage CkCtSage, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtSage _completeJob(CkCtSage arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtSage _confirmJob(CkCtSage arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtSage _createJob(CkCtSage CkCtSage, CkJob ckJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {

		return null;
	}

	@Override
	protected AbstractJobEvent<CkCtSage> _getJobEvent(JobEvent arg0, CkCtSage arg1, Principal arg2) {
		return null;
	}

	@Override
	protected CkCtSage _newJob(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {

		return null;
	}

	@Override
	protected CkCtSage _paidJob(CkCtSage arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtSage _payJob(CkCtSage arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtSage _rejectJob(CkCtSage arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtSage _submitJob(CkCtSage arg0, Principal arg1)
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
	protected CkCtSage dtoFromEntity(TCkCtSage tCkCtSage) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtSage == null) {
			throw new ParameterException("param entity null");
		}
		CkCtSage CkCtSage = new CkCtSage(tCkCtSage);
		
		return CkCtSage;
	}

	protected CkCtSage dtoFromEntity(TCkCtSage tCkCtSage, boolean withData)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtSage == null) {
			throw new ParameterException("param entity null");
		}
		CkCtSage CkCtSage = new CkCtSage(tCkCtSage);

		return CkCtSage;
	}

	@Override
	protected TCkCtSage entityFromDTO(CkCtSage ckCtSage) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtSage == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtSage tCkCtSage = new TCkCtSage();
		BeanUtils.copyProperties(ckCtSage, tCkCtSage);
		return tCkCtSage;
	}

	@Override
	protected String entityKeyFromDTO(CkCtSage ckCtSage) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtSage == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtSage.getSageId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtSage CkCtSage)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (CkCtSage == null) {
			throw new ParameterException("dto param null");
		}
		if (CkCtSage.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return CkCtSage.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtSage ckCtSage) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (ckCtSage == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();

		if (StringUtils.isNotBlank(ckCtSage.getSageBatchNo())) {
			parameters.put("sageBatchNo", ckCtSage.getSageBatchNo());
		}
		
		if (ckCtSage.getSageDtStart() != null) {
			parameters.put("sageDtStart", sdf.format(ckCtSage.getSageDtStart()));
		}
		if (ckCtSage.getSageDtEnd() != null) {
			parameters.put("sageDtEnd", sdf.format(ckCtSage.getSageDtEnd()));
		}
		if ( null != ckCtSage.getSageStatus()) {
			parameters.put("sageStatus", ckCtSage.getSageStatus());
		}

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtSage CkCtSage, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (CkCtSage == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();

		if (StringUtils.isNotBlank(CkCtSage.getSageBatchNo())) {
			condition.append(getOperator(wherePrinted) + " sageBatchNo " + CONTAIN
					+ ":sageBatchNo");
			wherePrinted = true;
		}

		if (CkCtSage.getSageDtStart() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(sageDtStart,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "sageDtStart");
			wherePrinted = true;
		}
		if (CkCtSage.getSageDtEnd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(sageDtEnd,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "sageDtEnd");
			wherePrinted = true;
		}
		if (CkCtSage.getSageStatus() != null) {
			
			condition.append(getOperator(wherePrinted) + " sageStatus " + CONTAIN
					+ "sageStatus");
			wherePrinted = true;
		}


		return condition.toString();
	}

	@Override
	protected TCkCtSage initEnity(TCkCtSage tCkCtSage) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtSage != null) {

		}
		return tCkCtSage;
	}

	@Override
	protected CkCtSage preSaveUpdateDTO(TCkCtSage tCkCtSage, CkCtSage CkCtSage)
			throws ParameterException, ProcessingException {

		return CkCtSage;
	}

	@Override
	protected void preSaveValidation(CkCtSage arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtSage arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkCtSage setCoreMstLocale(CoreMstLocale arg0, CkCtSage arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtSage updateEntity(ACTION action, TCkCtSage tCkCtSage, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtSage == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			break;

		case MODIFY:
			break;

		default:
			break;
		}
		return tCkCtSage;
	}

	@Override
	protected TCkCtSage updateEntityStatus(TCkCtSage tCkCtSage, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtSage == null) {
			throw new ParameterException("entity param null");
		}
		return tCkCtSage;
	}

	@Override
	protected CkCtSage whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtSage ckCtSage = new CkCtSage();

		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}
			String attribute = "o." + entityWhere.getAttribute();
			if ("sageBatchNo".equalsIgnoreCase(attribute)) {
				ckCtSage.setSageId(entityWhere.getValue());
			} else if ("sageDtStart".equalsIgnoreCase(attribute)) {
				try {
					ckCtSage.setSageDtStart(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if ("sageDtEnd".equalsIgnoreCase(attribute)) {
				try {
					ckCtSage.setSageDtEnd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			}
			
			// history toggle
			if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
				ckCtSage.setSageStatus(entityWhere.getValue().charAt(0)); // D: download
			} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
				ckCtSage.setSageStatus(entityWhere.getValue().charAt(0)); // A: Active
			}
			
		}
		return ckCtSage;
	}

}
