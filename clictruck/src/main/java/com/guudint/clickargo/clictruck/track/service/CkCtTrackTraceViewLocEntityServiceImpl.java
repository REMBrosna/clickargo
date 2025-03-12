package com.guudint.clickargo.clictruck.track.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.track.dto.CkCtTrackLocDto;
import com.guudint.clickargo.clictruck.track.model.VCkCtTrackLoc;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
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
import com.vcc.camelone.util.email.SysParam;

public class CkCtTrackTraceViewLocEntityServiceImpl extends AbstractJobService<CkCtTrackLocDto, VCkCtTrackLoc, String>
		implements ICkConstant {

	private static Logger LOG = Logger.getLogger(CkCtTrackTraceViewLocEntityServiceImpl.class);

	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	private SysParam sysParam;

	public CkCtTrackTraceViewLocEntityServiceImpl() {
		super("ckCtViewTrackLocDao", "Tack and Track View", "VCkCtTrackLoc", "V_CK_CT_TRACK_LOC");
	}

	@Override
	public CkCtTrackLocDto deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		return null;
	}

	@Override
	@Transactional
	public List<CkCtTrackLocDto> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtTrackLocDto CkCtTrackLocDto = whereDto(filterRequest);
		if (CkCtTrackLocDto == null) {
			throw new ProcessingException("whereDto null");
		}
		filterRequest.setTotalRecords(countByAnd(CkCtTrackLocDto));
		List<CkCtTrackLocDto> CkCtTrackLocDtos = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<VCkCtTrackLoc> tCkCtTrackLocDtos = findEntitiesByAnd(CkCtTrackLocDto, "from VCkCtTrackLoc o ",
					orderByClause, filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (VCkCtTrackLoc tCkCtTrackLocDto : tCkCtTrackLocDtos) {
				CkCtTrackLocDto dto = dtoFromEntity(tCkCtTrackLocDto);
				if (dto != null) {
					CkCtTrackLocDtos.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return CkCtTrackLocDtos;
	}

	@Override
	public CkCtTrackLocDto findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			VCkCtTrackLoc tCkCtTrackLocDto = dao.find(id);
			if (tCkCtTrackLocDto == null) {
				throw new EntityNotFoundException("id:" + id);
			}
			initEnity(tCkCtTrackLocDto);
			return dtoFromEntity(tCkCtTrackLocDto);
		} catch (Exception e) {
			LOG.error("findById", e);
		}
		return null;
	}

	@Override
	protected void _auditError(JobEvent jobEvent, CkCtTrackLocDto CkSageIntegration, Exception ex,
			Principal principal) {

	}

	@Override
	protected void _auditEvent(JobEvent jobEvent, CkCtTrackLocDto CkSageIntegration, Principal principal) {
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
			coreAuditLog.setAudtReckey(
					StringUtils.isEmpty(CkSageIntegration.getTrJob()) ? DASH : CkSageIntegration.getTrJob());
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
	protected Class<?>[] _validateGroupClass(JobEvent arg0) {
		return null;
	}

	@Override
	protected String formatOrderBy(String attribute) throws Exception {
		attribute = Optional.ofNullable(attribute).orElse("");
		// attribute = attribute.replace("tckCtMstChassisType", "TCkCtMstChassisType")
		// .replace("tckCtMstVehState", "TCkCtMstVehState").replace("tckCtMstVehType",
		// "TCkCtMstVehType");
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

	protected CkCtTrackLocDto dtoFromEntity(VCkCtTrackLoc vCkCtTrackEnterExit)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (vCkCtTrackEnterExit == null) {
			throw new ParameterException("param entity null");
		}
		CkCtTrackLocDto ckCtTrackEnterExit = new CkCtTrackLocDto(vCkCtTrackEnterExit);

		if (vCkCtTrackEnterExit.getTCoreAccnCO() != null) {
			ckCtTrackEnterExit.setTCoreAccnCO( new CoreAccn(vCkCtTrackEnterExit.getTCoreAccnCO()));
		}
		if (vCkCtTrackEnterExit.getTCoreAccnTO() != null) {
			ckCtTrackEnterExit.setTCoreAccnTO( new CoreAccn(vCkCtTrackEnterExit.getTCoreAccnTO()));
		}
		
		/// some logic
		int timeGapMinute = sysParam.getValInteger("CLICTRUCK_TRACK_ENTER_TIME_GAP", 120); // default 60 minutes
		ckCtTrackEnterExit.setTimeGapMinute(timeGapMinute);
		///

		return ckCtTrackEnterExit;
	}

	@Override
	protected String entityKeyFromDTO(CkCtTrackLocDto ckCtSage) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtSage == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtSage.getTrJob();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtTrackLocDto CkSageIntegration)
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
	protected HashMap<String, Object> getParameters(CkCtTrackLocDto ckCtSage)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (ckCtSage == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();
		/*
		 * if (StringUtils.isNotBlank(ckCtSage.getSageBatchNo())) {
		 * parameters.put("sageBatchNo", ckCtSage.getSageBatchNo()); }
		 * 
		 * if (ckCtSage.getSageDtStart() != null) { parameters.put("sageDtStart",
		 * sdf.format(ckCtSage.getSageDtStart())); } if (ckCtSage.getSageDtEnd() !=
		 * null) { parameters.put("sageDtEnd", sdf.format(ckCtSage.getSageDtEnd())); }
		 */

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtTrackLocDto ckCtTrackLocDto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (ckCtTrackLocDto == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();
		/*
		 * if (StringUtils.isNotBlank(CkSageIntegration.getSageBatchNo())) {
		 * condition.append(getOperator(wherePrinted) + " sageBatchNo " + CONTAIN +
		 * ":sageBatchNo"); wherePrinted = true; }
		 * 
		 * if (CkSageIntegration.getSageDtStart() != null) {
		 * condition.append(getOperator(wherePrinted) + "DATE_FORMAT(sageDtStart,'" +
		 * DateFormat.MySql.D_M_Y + "')" + EQUAL + "sageDtStart"); wherePrinted = true;
		 * } if (CkSageIntegration.getSageDtEnd() != null) {
		 * condition.append(getOperator(wherePrinted) + "DATE_FORMAT(sageDtEnd,'" +
		 * DateFormat.MySql.D_M_Y + "')" + EQUAL + "sageDtEnd"); wherePrinted = true; }
		 */

		return condition.toString();
	}

	@Override
	protected VCkCtTrackLoc initEnity(VCkCtTrackLoc vCkCtTrackEnterExitTime)
			throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (vCkCtTrackEnterExitTime != null) {
			Hibernate.initialize(vCkCtTrackEnterExitTime.getTCoreAccnCO());
			Hibernate.initialize(vCkCtTrackEnterExitTime.getTCoreAccnTO());
		}
		return vCkCtTrackEnterExitTime;
	}

	@Override
	protected VCkCtTrackLoc updateEntity(ACTION action, VCkCtTrackLoc tCkCtTrackLocDto, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtTrackLocDto == null)
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
		return tCkCtTrackLocDto;
	}

	@Override
	protected VCkCtTrackLoc updateEntityStatus(VCkCtTrackLoc tCkCtTrackLocDto, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtTrackLocDto == null) {
			throw new ParameterException("entity param null");
		}
		return tCkCtTrackLocDto;
	}

	@Override
	protected CkCtTrackLocDto whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtTrackLocDto ckCtSage = new CkCtTrackLocDto();

		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}

			String attribute = "o." + entityWhere.getAttribute();
			/*
			 * if ("sageBatchNo".equalsIgnoreCase(attribute)) {
			 * ckCtSage.setSageId(entityWhere.getValue()); } else if
			 * ("sageDtStart".equalsIgnoreCase(attribute)) { try {
			 * ckCtSage.setSageDtStart(sdf.parse(entityWhere.getValue())); } catch
			 * (ParseException e) { LOG.error(e); } } else if
			 * ("sageDtEnd".equalsIgnoreCase(attribute)) { try {
			 * ckCtSage.setSageDtEnd(sdf.parse(entityWhere.getValue())); } catch
			 * (ParseException e) { LOG.error(e); } }
			 * 
			 */
			// history toggle
			if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {

				if (!HISTORY.equals(entityWhere.getValue())) {
					// not history
					// ckCtSage.setTCkCtMstSageIntState( new
					// CkCtMstSageIntState(CkCtMstSageIntStateEnum.SUBMITTED.name()));
				} else {
					// ckCtSage.setTCkCtMstSageIntState( new
					// CkCtMstSageIntState(CkCtMstSageIntStateEnum.SUCCESS.name()));
				}
			}
		}
		return ckCtSage;
	}

	@Override
	protected AbstractJobEvent<CkCtTrackLocDto> _getJobEvent(JobEvent jobEvent, CkCtTrackLocDto dto,
			Principal principal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _newJob(Principal p)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _createJob(CkCtTrackLocDto dto, CkJob parentJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _submitJob(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _rejectJob(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _cancelJob(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _confirmJob(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _payJob(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _paidJob(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto _completeJob(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected VCkCtTrackLoc entityFromDTO(CkCtTrackLocDto dto) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto preSaveUpdateDTO(VCkCtTrackLoc storedEntity, CkCtTrackLocDto dto)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preSaveValidation(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtTrackLocDto setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtTrackLocDto dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

}
