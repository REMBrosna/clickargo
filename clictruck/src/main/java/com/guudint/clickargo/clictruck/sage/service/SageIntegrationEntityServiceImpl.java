package com.guudint.clickargo.clictruck.sage.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntState;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntStateEnum;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntType;
import com.guudint.clickargo.sage.dto.CkSageIntegration;
import com.guudint.clickargo.sage.model.TCkSageIntegration;
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

// Move to clicCommon
@Deprecated
public class SageIntegrationEntityServiceImpl extends AbstractJobService<CkSageIntegration, TCkSageIntegration, String>
		implements ICkConstant {

	private static Logger LOG = Logger.getLogger(SageIntegrationEntityServiceImpl.class);

	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;



	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	public SageIntegrationEntityServiceImpl() {
		super("ckSageIntegrationDao", "CKCT SAGE INTEGRATION", "TCkSageIntegration","T_CK_SAGE_INTEGRATION");
	}

	@Override
	public CkSageIntegration deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		return null;
	}

	@Override
	public List<CkSageIntegration> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkSageIntegration CkSageIntegration = whereDto(filterRequest);
		if (CkSageIntegration == null) {
			throw new ProcessingException("whereDto null");
		}
		filterRequest.setTotalRecords(countByAnd(CkSageIntegration));
		List<CkSageIntegration> CkSageIntegrations = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkSageIntegration> tCkSageIntegrations = findEntitiesByAnd(CkSageIntegration, "from TCkSageIntegration o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkSageIntegration tCkSageIntegration : tCkSageIntegrations) {
				CkSageIntegration dto = dtoFromEntity(tCkSageIntegration, false);
				if (dto != null) {
					CkSageIntegrations.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return CkSageIntegrations;
	}

	@Override
	public CkSageIntegration findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkSageIntegration tCkSageIntegration = dao.find(id);
			if (tCkSageIntegration == null) {
				throw new EntityNotFoundException("id:" + id);
			}
			initEnity(tCkSageIntegration);
			return dtoFromEntity(tCkSageIntegration);
		} catch (Exception e) {
			LOG.error("findById", e);
		}
		return null;
	}

	@Override
	protected void _auditError(JobEvent jobEvent, CkSageIntegration CkSageIntegration, Exception ex, Principal principal) {

	}

	@Override
	protected void _auditEvent(JobEvent jobEvent, CkSageIntegration CkSageIntegration, Principal principal) {
		LOG.debug("_auditEvent");
		try {
			if (CkSageIntegration == null) {
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
			coreAuditLog.setAudtReckey(StringUtils.isEmpty(CkSageIntegration.getSintId() ) ? DASH : CkSageIntegration.getSintId());
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
	protected CkSageIntegration _cancelJob(CkSageIntegration CkSageIntegration, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkSageIntegration _completeJob(CkSageIntegration arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkSageIntegration _confirmJob(CkSageIntegration arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkSageIntegration _createJob(CkSageIntegration CkSageIntegration, CkJob ckJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {

		return null;
	}

	@Override
	protected AbstractJobEvent<CkSageIntegration> _getJobEvent(JobEvent arg0, CkSageIntegration arg1, Principal arg2) {
		return null;
	}

	@Override
	protected CkSageIntegration _newJob(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {

		return null;
	}

	@Override
	protected CkSageIntegration _paidJob(CkSageIntegration arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkSageIntegration _payJob(CkSageIntegration arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkSageIntegration _rejectJob(CkSageIntegration arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkSageIntegration _submitJob(CkSageIntegration arg0, Principal arg1)
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
		//attribute = attribute.replace("tckCtMstChassisType", "TCkCtMstChassisType")
		//		.replace("tckCtMstVehState", "TCkCtMstVehState").replace("tckCtMstVehType", "TCkCtMstVehType");
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
	protected CkSageIntegration dtoFromEntity(TCkSageIntegration tCkSageIntegration) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkSageIntegration == null) {
			throw new ParameterException("param entity null");
		}
		CkSageIntegration CkSageIntegration = new CkSageIntegration(tCkSageIntegration);
		
		if(tCkSageIntegration.getTCkCtMstSageIntState() != null) {
			CkSageIntegration.setTCkCtMstSageIntState( new CkCtMstSageIntState(tCkSageIntegration.getTCkCtMstSageIntState().getSisId()));
		}
		
		if(tCkSageIntegration.getTCkCtMstSageIntType() != null) {
			CkSageIntegration.setTCkCtMstSageIntType( new CkCtMstSageIntType(tCkSageIntegration.getTCkCtMstSageIntType().getSitId() ));
		}
		
		return CkSageIntegration;
	}

	protected CkSageIntegration dtoFromEntity(TCkSageIntegration tCkSageIntegration, boolean withData)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkSageIntegration == null) {
			throw new ParameterException("param entity null");
		}
		CkSageIntegration CkSageIntegration = new CkSageIntegration(tCkSageIntegration);
		
		if(tCkSageIntegration.getTCkCtMstSageIntState() != null) {
			CkSageIntegration.setTCkCtMstSageIntState( new CkCtMstSageIntState(tCkSageIntegration.getTCkCtMstSageIntState().getSisId()));
		}
		
		if(tCkSageIntegration.getTCkCtMstSageIntType() != null) {
			CkSageIntegration.setTCkCtMstSageIntType( new CkCtMstSageIntType(tCkSageIntegration.getTCkCtMstSageIntType().getSitId() ));
		}

		return CkSageIntegration;
	}

	@Override
	protected TCkSageIntegration entityFromDTO(CkSageIntegration ckCtSage) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtSage == null) {
			throw new ParameterException("param dto null");
		}
		TCkSageIntegration tCkSageIntegration = new TCkSageIntegration();
		BeanUtils.copyProperties(ckCtSage, tCkSageIntegration);
		return tCkSageIntegration;
	}

	@Override
	protected String entityKeyFromDTO(CkSageIntegration ckCtSage) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtSage == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtSage.getSintId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkSageIntegration CkSageIntegration)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (CkSageIntegration == null) {
			throw new ParameterException("dto param null");
		}
		if (CkSageIntegration.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return CkSageIntegration.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkSageIntegration ckCtSage) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (ckCtSage == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();
/*
		if (StringUtils.isNotBlank(ckCtSage.getSageBatchNo())) {
			parameters.put("sageBatchNo", ckCtSage.getSageBatchNo());
		}
		
		if (ckCtSage.getSageDtStart() != null) {
			parameters.put("sageDtStart", sdf.format(ckCtSage.getSageDtStart()));
		}
		if (ckCtSage.getSageDtEnd() != null) {
			parameters.put("sageDtEnd", sdf.format(ckCtSage.getSageDtEnd()));
		}
*/
		if ( null != ckCtSage.getTCkCtMstSageIntState() ) {
			
			List<String> statusList = null;
			
			if(CkCtMstSageIntStateEnum.SUBMITTED.name().equals(ckCtSage.getTCkCtMstSageIntState().getSisId())) {
				// submit;
				statusList = Arrays.asList(CkCtMstSageIntStateEnum.SUBMITTED.name(), CkCtMstSageIntStateEnum.ERROR.name());
			}else {
				statusList = Arrays.asList(CkCtMstSageIntStateEnum.COMPLETE.name());
			}
			
			parameters.put("sisId",statusList);
		} 
		return parameters;
	}

	@Override
	protected String getWhereClause(CkSageIntegration CkSageIntegration, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (CkSageIntegration == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();
/*
		if (StringUtils.isNotBlank(CkSageIntegration.getSageBatchNo())) {
			condition.append(getOperator(wherePrinted) + " sageBatchNo " + CONTAIN
					+ ":sageBatchNo");
			wherePrinted = true;
		}

		if (CkSageIntegration.getSageDtStart() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(sageDtStart,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "sageDtStart");
			wherePrinted = true;
		}
		if (CkSageIntegration.getSageDtEnd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(sageDtEnd,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "sageDtEnd");
			wherePrinted = true;
		}
*/
		if (CkSageIntegration.getTCkCtMstSageIntState()!= null) {
			// display SUBMITTED
			condition.append(getOperator(wherePrinted) + " TCkCtMstSageIntState.sisId in :sisId" );
			wherePrinted = true;
		} 

		return condition.toString();
	}

	@Override
	protected TCkSageIntegration initEnity(TCkSageIntegration tCkSageIntegration) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkSageIntegration != null) {

		}
		return tCkSageIntegration;
	}

	@Override
	protected CkSageIntegration preSaveUpdateDTO(TCkSageIntegration tCkSageIntegration, CkSageIntegration CkSageIntegration)
			throws ParameterException, ProcessingException {

		return CkSageIntegration;
	}

	@Override
	protected void preSaveValidation(CkSageIntegration arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
	}

	@Override
	protected ServiceStatus preUpdateValidation(CkSageIntegration arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkSageIntegration setCoreMstLocale(CoreMstLocale arg0, CkSageIntegration arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	protected TCkSageIntegration updateEntity(ACTION action, TCkSageIntegration tCkSageIntegration, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkSageIntegration == null)
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
		return tCkSageIntegration;
	}

	@Override
	protected TCkSageIntegration updateEntityStatus(TCkSageIntegration tCkSageIntegration, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkSageIntegration == null) {
			throw new ParameterException("entity param null");
		}
		return tCkSageIntegration;
	}

	@Override
	protected CkSageIntegration whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkSageIntegration ckCtSage = new CkSageIntegration();

		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}

			String attribute = "o." + entityWhere.getAttribute();			
			/*
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
			
			*/
			// history toggle
			if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
				
				if(!HISTORY.equals(entityWhere.getValue())) {
					// not history
					ckCtSage.setTCkCtMstSageIntState( new CkCtMstSageIntState(CkCtMstSageIntStateEnum.SUBMITTED.name()));
				} else {
					ckCtSage.setTCkCtMstSageIntState( new CkCtMstSageIntState(CkCtMstSageIntStateEnum.COMPLETE.name()));
				}
			}
		}
		return ckCtSage;
	}

}
