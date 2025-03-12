package com.guudint.clickargo.clictruck.planexec.job.service.impl;

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
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTermReq;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTermReq;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.enums.WorkflowTypeEnum;
import com.guudint.clickargo.common.event.ApproveEvent;
import com.guudint.clickargo.common.event.RejectEvent;
import com.guudint.clickargo.common.event.SubmitEvent;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.guudint.clickargo.job.service.IJobValidate;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
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

public class CkJobTermReqService extends AbstractJobService<CkCtJobTermReq, TCkCtJobTermReq, String>
		implements ICkConstant {

	private static Logger LOG = Logger.getLogger(CkJobTermReqService.class);

	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;
	
	@Autowired
	private CkJobTermReqWorkflowService ckJobTermReqWorkflowService;
	
	@Autowired
	protected SessionFactory sessionFactory;
    
    @Autowired
    IJobValidate<CkCtJobTermReq> termReqValidate;


	public CkJobTermReqService() {
		super("ckCtJobTermReqDao", "Job Termination", "TCkCtJobTermReq",
				"T_CK_CT_JOB_TERM_REQ");
	}

	@Override
	public CkCtJobTermReq deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		//return updateStatus(id, "delete");
		return null;
		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtJobTermReq> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtJobTermReq ckCtVeh = whereDto(filterRequest);
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
		List<CkCtJobTermReq> ckCtVehs = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtJobTermReq> tCkCtVehs = findEntitiesByAnd(ckCtVeh, "from TCkCtJobTermReq o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtJobTermReq tCkCtVeh : tCkCtVehs) {
				CkCtJobTermReq dto = dtoFromEntity(tCkCtVeh, false);
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtJobTermReq updateJob(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		try {

			if (null != dto.getAction()) {
				

				// validation
				List<ValidationError> validationErrors = null;
				
				JobActions jobAction = dto.getAction();
				switch (jobAction) {
				case SUBMIT:
					validationErrors = termReqValidate.validateSubmit(dto, principal);
					break;
				case APPROVE:
				case REJECT:
					validationErrors = termReqValidate.validateComplete(dto, principal);
					break;
				default:
					break;
				}
				
				if (null != validationErrors && !validationErrors.isEmpty())
					throw new ValidationException(validationErrorMap(validationErrors));
				

				CkCtJobTermReq updatedDto 
					= ckJobTermReqWorkflowService.moveState(ckJobTermReqWorkflowService.convert2FormActionByJobAction(dto.getAction()), dto, principal, ServiceTypes.CLICTRUCK);
				
				LOG.info("New State" + updatedDto.getJtrState());
				
				dto.setJtrState(updatedDto.getJtrState());
				
				this.publishPostEvents(dto);
				
				return updatedDto;
			} else {

				// just to update the user and lupd
				return super.update(dto, principal);
			}

		} catch (ValidationException ex) {
			throw new ValidationException(ex.getMessage());
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtJobTermReq findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtJobTermReq tCkCtVeh = dao.find(id);
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
	protected void _auditError(JobEvent jobEvent, CkCtJobTermReq ckCtVeh, Exception ex, Principal principal) {

	}

	@Override
	protected void _auditEvent(JobEvent jobEvent, CkCtJobTermReq ckJobTermReq, Principal principal) {
		LOG.debug("_auditEvent");
		try {
			if (ckJobTermReq == null) {
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
			coreAuditLog.setAudtReckey(StringUtils.isEmpty(ckJobTermReq.getJtrId() ) ? DASH : ckJobTermReq.getJtrId());
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
	protected CkCtJobTermReq _cancelJob(CkCtJobTermReq ckCtVeh, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTermReq _completeJob(CkCtJobTermReq arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTermReq _confirmJob(CkCtJobTermReq arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTermReq _createJob(CkCtJobTermReq ckCtVeh, CkJob ckJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {
		if (ckCtVeh == null) {
			throw new ParameterException("param dto null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		// TODO
		// compute debit note and platform fee.
		// fetch JobTruck

		return add(ckCtVeh, principal);
	}

	@Override
	protected AbstractJobEvent<CkCtJobTermReq> _getJobEvent(JobEvent arg0, CkCtJobTermReq arg1, Principal arg2) {
		return null;
	}

	@Override
	protected CkCtJobTermReq _newJob(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtJobTermReq ckCtVeh = new CkCtJobTermReq();

		return ckCtVeh;
	}

	@Override
	protected CkCtJobTermReq _paidJob(CkCtJobTermReq arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTermReq _payJob(CkCtJobTermReq arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTermReq _rejectJob(CkCtJobTermReq arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTermReq _submitJob(CkCtJobTermReq arg0, Principal arg1)
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
	protected CkCtJobTermReq dtoFromEntity(TCkCtJobTermReq tCkCtJobTermReq) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtJobTermReq == null) {
			throw new ParameterException("param entity null");
		}
		CkCtJobTermReq ckCtVeh = new CkCtJobTermReq(tCkCtJobTermReq);

		/*-
		if (tCkCtJobTermReq.getTCkCtMstTrackDeviceState() != null) {
			ckCtVeh.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(tCkCtJobTermReq.getTCkCtMstTrackDeviceState()));
		}
		if (tCkCtJobTermReq.getTCkCtVeh() != null) {
			
			ckCtVeh.setTCkCtVeh(new CkCtVeh(tCkCtJobTermReq.getTCkCtVeh()));
			
			if (tCkCtJobTermReq.getTCkCtVeh().getTCkCtMstVehType() != null) {
				ckCtVeh.getTCkCtVeh().setTCkCtMstVehType(new CkCtMstVehType(tCkCtJobTermReq.getTCkCtVeh().getTCkCtMstVehType()));
			}
		}
		*/
		if (tCkCtJobTermReq.getTCoreAccn() != null) {
			ckCtVeh.setTCoreAccn(new CoreAccn(tCkCtJobTermReq.getTCoreAccn()));
		}
		return ckCtVeh;
	}

	protected CkCtJobTermReq dtoFromEntity(TCkCtJobTermReq tCkCtJobTermReq, boolean withData)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtJobTermReq == null) {
			throw new ParameterException("param entity null");
		}

		return this.dtoFromEntity(tCkCtJobTermReq);
	}

	@Override
	protected TCkCtJobTermReq entityFromDTO(CkCtJobTermReq ckCtVehDevice) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtVehDevice == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtJobTermReq tCkCtJobTermReq = ckCtVehDevice.toEntity(new TCkCtJobTermReq());

		/*-
		if (ckCtVehDevice.getTCkCtMstTrackDeviceState() != null) {
			tCkCtJobTermReq.setTCkCtMstTrackDeviceState(ckCtVehDevice.getTCkCtMstTrackDeviceState().toEntity(new TCkCtMstTrackDeviceState()));
		}

		if (ckCtVehDevice.getTCkCtVeh() != null) {
			
			tCkCtJobTermReq.setTCkCtVeh(ckCtVehDevice.getTCkCtVeh().toEntity(new TCkCtVeh()));
			
			if (ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType() != null) {
				tCkCtJobTermReq.getTCkCtVeh().setTCkCtMstVehType(ckCtVehDevice.getTCkCtVeh().getTCkCtMstVehType().toEntity(new TCkCtMstVehType()));
			}
		}
		*/
		if (ckCtVehDevice.getTCoreAccn() != null) {
			tCkCtJobTermReq.setTCoreAccn(ckCtVehDevice.getTCoreAccn().toEntity(new TCoreAccn()));
		}
		
		return tCkCtJobTermReq;
	}

	@Override
	protected String entityKeyFromDTO(CkCtJobTermReq ckCtVeh) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtVeh == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtVeh.getJtrId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtJobTermReq ckCtVeh)
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
	protected HashMap<String, Object> getParameters(CkCtJobTermReq ckctJobTermReq) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (ckctJobTermReq == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();

		Principal principal = ckSession.getPrincipal();

			boolean isL1 = principal.getRoleList().stream().anyMatch(el -> Arrays
					.asList(Roles.SP_L1.name()).contains(el));
			
			boolean isFnHd = principal.getRoleList().stream().anyMatch(el -> Arrays
					.asList(Roles.SP_FIN_HD.name()).contains(el));
			
			List<String> statusList = null;


			if (StringUtils.isNotBlank(ckctJobTermReq.getJtrId())) {
				parameters.put("jtrId", "%" + ckctJobTermReq.getJtrId() + "%");
			}

			if (ckctJobTermReq.getTCoreAccn()!= null && StringUtils.isNotBlank(ckctJobTermReq.getTCoreAccn().getAccnId())) {
				parameters.put("accnId", "%" + ckctJobTermReq.getTCoreAccn().getAccnId() + "%");
			}

			if (ckctJobTermReq.getTCoreAccn()!= null && StringUtils.isNotBlank(ckctJobTermReq.getTCoreAccn().getAccnName())) {
				parameters.put("accnName", "%" + ckctJobTermReq.getTCoreAccn().getAccnName() + "%");
			}
			
			// 4 dates
			if (ckctJobTermReq.getJtrDtCreate()!= null ) {
				parameters.put("jtrDtCreate",  sdf.format(ckctJobTermReq.getJtrDtCreate()));
			}
			
			if (ckctJobTermReq.getJtrDtLupd()!= null ) {

				parameters.put("jtrDtLupd",  sdf.format(ckctJobTermReq.getJtrDtLupd()));
			}
			
			if (ckctJobTermReq.getHistory() != null) {
				
				// History
				if(HISTORY.equalsIgnoreCase(ckctJobTermReq.getHistory() )) {
					// History;
					statusList = Arrays.asList(JobStates.APP.name(), JobStates.REJ.name());
				}else {
					if( isL1 ) {
						statusList = Arrays.asList(JobStates.NEW.name(), JobStates.SUB.name() );
					} else if(isFnHd) {
						statusList = Arrays.asList(JobStates.SUB.name() );
					}
				}
				
				parameters.put("historyId",statusList);
			}
		
		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtJobTermReq ckCtJobTermReq, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (ckCtJobTermReq == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();
		
		wherePrinted = true;
		condition.append(" where o.jtrStatus = 'A' ");

		if (StringUtils.isNotBlank(ckCtJobTermReq.getJtrId())) {
			condition.append(getOperator(wherePrinted) + " o.jtrId like :jtrId");
			wherePrinted = true;
		}

		if (ckCtJobTermReq.getTCoreAccn()!= null && StringUtils.isNotBlank(ckCtJobTermReq.getTCoreAccn().getAccnId())) {
			condition.append(getOperator(wherePrinted) + " o.TCoreAccn.accnId like :accnId");
			wherePrinted = true;
		}

		if (ckCtJobTermReq.getTCoreAccn()!= null && StringUtils.isNotBlank(ckCtJobTermReq.getTCoreAccn().getAccnName())) {
			condition.append(getOperator(wherePrinted) + " o.TCoreAccn.accnName like :accnName");
			wherePrinted = true;
		}

		if (ckCtJobTermReq.getJtrDtCreate()!= null ) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jtrDtCreate,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "jtrDtCreate");
			wherePrinted = true;
		}
		
		if (ckCtJobTermReq.getJtrDtLupd()!= null ) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(o.jtrDtLupd,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "jtrDtLupd");
			wherePrinted = true;
		}
		
		// History
		if (ckCtJobTermReq.getHistory() != null) {
			// display SUBMITTED
			condition.append(getOperator(wherePrinted) + " o.jtrState in :historyId" );
			wherePrinted = true;
		} 
		return condition.toString();
	}

	@Override
	protected TCkCtJobTermReq initEnity(TCkCtJobTermReq tCkCtJobTermReq) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtJobTermReq != null) {
			/*-
			Hibernate.initialize(tCkCtJobTermReq.getTCkCtMstTrackDeviceState());
			if(null != tCkCtJobTermReq.getTCkCtVeh() ) {
				Hibernate.initialize(tCkCtJobTermReq.getTCkCtVeh());
			}
			if(null != tCkCtJobTermReq.getTCkCtVeh() && tCkCtJobTermReq.getTCkCtVeh().getTCkCtMstVehType() != null) {
				Hibernate.initialize(tCkCtJobTermReq.getTCkCtVeh().getTCkCtMstVehType());
			}
			Hibernate.initialize(tCkCtJobTermReq.getTCkCtVeh());
			*/
			Hibernate.initialize(tCkCtJobTermReq.getTCoreAccn());
		}
		return tCkCtJobTermReq;
	}

	@Override
	protected CkCtJobTermReq preSaveUpdateDTO(TCkCtJobTermReq tCkCtJobTermReq, CkCtJobTermReq ckCtJobTermReq)
			throws ParameterException, ProcessingException {
		if (tCkCtJobTermReq == null)
			throw new ParameterException("param storedEntity null");
		if (ckCtJobTermReq == null)
			throw new ParameterException("param dto null");

		ckCtJobTermReq.setJtrUidCreate(tCkCtJobTermReq.getJtrUidCreate());
		ckCtJobTermReq.setJtrDtCreate(tCkCtJobTermReq.getJtrDtCreate());
		ckCtJobTermReq.setJtrStatus(Constant.ACTIVE_STATUS);
		
		return ckCtJobTermReq;
	}

	@Override
	protected void preSaveValidation(CkCtJobTermReq arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtJobTermReq arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkCtJobTermReq setCoreMstLocale(CoreMstLocale arg0, CkCtJobTermReq arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtJobTermReq updateEntity(ACTION action, TCkCtJobTermReq tCkCtJobTermReq, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtJobTermReq == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			
			if(StringUtils.isBlank(tCkCtJobTermReq.getJtrId())) {
				tCkCtJobTermReq.setJtrId(CkUtil.generateId(TCkCtJobTermReq.PREFIX_ID));
			}
			tCkCtJobTermReq.setJtrUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			tCkCtJobTermReq.setJtrDtCreate(date);
			tCkCtJobTermReq.setJtrDtLupd(date);
			tCkCtJobTermReq.setJtrUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			
			tCkCtJobTermReq.setJtrStatus(Constant.ACTIVE_STATUS);
			tCkCtJobTermReq.setJtrState(JobStates.NEW.name());
			
			break;

		case MODIFY:
			tCkCtJobTermReq.setJtrDtLupd(date);
			tCkCtJobTermReq.setJtrUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		default:
			break;
		}
		return tCkCtJobTermReq;
	}

	@Override
	protected TCkCtJobTermReq updateEntityStatus(TCkCtJobTermReq tCkCtJobTermReq, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtJobTermReq == null) {
			throw new ParameterException("entity param null");
		}
		tCkCtJobTermReq.setJtrStatus(status);
		return tCkCtJobTermReq;
	}

	@Override
	protected CkCtJobTermReq whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtJobTermReq ckCtJobTermReq = new CkCtJobTermReq();
		ckCtJobTermReq.setTCoreAccn( new CoreAccn());
		//ckCtTrackDevice.setTCkCtVeh( new CkCtVeh());

		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}
			if ("jtrId".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtJobTermReq.setJtrId(entityWhere.getValue());
			} else if ("TcoreAccn.accnId".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtJobTermReq.getTCoreAccn().setAccnId(entityWhere.getValue());
			} else if ("TcoreAccn.accnName".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtJobTermReq.getTCoreAccn().setAccnName(entityWhere.getValue());
			} else if ("jtrDtCreate".equalsIgnoreCase(entityWhere.getAttribute())) {
				try {
					ckCtJobTermReq.setJtrDtCreate(sdf.parse(entityWhere.getValue()));
				} catch (Exception e) {
					LOG.error(e);
				}
			} else if ("jtrDtLupd".equalsIgnoreCase(entityWhere.getAttribute())) {
				try {
					ckCtJobTermReq.setJtrDtLupd(sdf.parse(entityWhere.getValue()));
				} catch (Exception e) {
					LOG.error(e);
				}
			} 

			// history toggle
			if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
				ckCtJobTermReq.setHistory(entityWhere.getValue());
			}
		}
		
		return ckCtJobTermReq;
	}

	/**
	 * Update T_CK_CT_JOB_TERM_REQ amount by T_CK_CT_JOB_TERM
	 * @param jtrId
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void computeAmount(String jtrId) throws Exception {
		LOG.info("compute amount: " + jtrId);
		
		String nativeSQL = "update T_CK_CT_JOB_TERM_REQ jtr, ( select tr.JT_REQ, count(*) jobs, sum(JT_JOB_PLTFEE_AMT_COFF + JT_JOB_PLTFEE_AMT_TO) sumInv,\n"
				+ "	 sum(tr.JT_JOB_DN) sumDn from T_CK_CT_JOB_TERM tr  group by tr.JT_REQ) t\n"
				+ " set jtr.JTR_NO_JOBS = jobs, JTR_JOBS_PLTFEE_AMT = sumInv, JTR_JOBS_DN_AMT = sumDn\n"
				+ " where jtr.JTR_ID = t.JT_REQ\n"
				+ "	 and jtr.JTR_ID = '"+ jtrId +"'";
		
		sessionFactory.getCurrentSession().createSQLQuery(nativeSQL).executeUpdate();
	}


	
	private void publishPostEvents(CkCtJobTermReq dto) throws Exception {

		ApplicationEvent appEvent = null;
		
		FormActions formAction = ckJobTermReqWorkflowService.convert2FormActionByJobAction(dto.getAction());
		
		if (formAction == FormActions.SUBMIT) {
			appEvent = new SubmitEvent<TCkCtJobTermReq, CkCtJobTermReq>(this, WorkflowTypeEnum.JOB_TERM, dto);

		} else if (formAction == FormActions.APPROVE) {
			appEvent = new ApproveEvent<TCkCtJobTermReq, CkCtJobTermReq>(this, WorkflowTypeEnum.JOB_TERM, dto);

		} else if (formAction == FormActions.REJECT) {
			appEvent = new RejectEvent<TCkCtJobTermReq, CkCtJobTermReq>(this, WorkflowTypeEnum.JOB_TERM, dto);

		} 
		
		eventPublisher.publishEvent(appEvent);
	}
}
