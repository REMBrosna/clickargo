package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.service.util.ClickargoAccnService;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtDebitNote;
import com.guudint.clickargo.clictruck.finacing.service.IDebitNoteService;
import com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTerm;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTermReq;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTerm;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTermReq;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.enums.WorkflowTypeEnum;
import com.guudint.clickargo.common.event.ApproveEvent;
import com.guudint.clickargo.common.event.RejectEvent;
import com.guudint.clickargo.common.event.SubmitEvent;
import com.guudint.clickargo.common.event.VerifyEvent;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.job.dto.CkJob;
import com.guudint.clickargo.job.event.AbstractJobEvent;
import com.guudint.clickargo.job.service.AbstractJobService;
import com.guudint.clickargo.master.dto.CkMstJobState;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
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
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.locale.dto.CoreMstLocale;

public class CkJobTermService extends AbstractJobService<CkCtJobTerm, TCkCtJobTerm, String> implements ICkConstant {

	private static Logger LOG = Logger.getLogger(CkJobTermService.class);

	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	protected ClickargoAccnService clickargoAccnService;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	private IEntityService<TCkJobTruck, String, CkJobTruck> ckCtJobTruckService;

	@Autowired
	private IDebitNoteService debitNoteService;

	@Autowired
	private IPlatformInvoiceService platformInvoiceService;

	@Autowired
	private CkCtPlatformInvoiceDao platformInvoiceDao;

	@Autowired
	private CkJobTermReqService jobTermReqService;

	public CkJobTermService() {
		super("ckCtJobTermDao", "Job Termination item", "TCkCtJobTerm", "T_CK_CT_JOB_TERM");
	}

	@Override
	public CkCtJobTerm deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		// return updateStatus(id, "delete");
		return null;

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtJobTerm delete(CkCtJobTerm dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		try {
			this.dao.remove(new TCkCtJobTerm(dto.getJtId(), null, null));

			jobTermReqService.computeAmount(dto.getTCkCtJobTermReq().getJtrId());

		} catch (Exception e) {
			LOG.error("Fail to delete ", e);
			throw new ProcessingException(e);
		}

		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtJobTerm> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtJobTerm ckCtVeh = whereDto(filterRequest);
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
		List<CkCtJobTerm> ckCtVehs = new ArrayList<>();
		try {
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtJobTerm> tCkCtVehs = findEntitiesByAnd(ckCtVeh, "from TCkCtJobTerm o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtJobTerm tCkCtVeh : tCkCtVehs) {
				CkCtJobTerm dto = dtoFromEntity(tCkCtVeh, false);
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
	public CkCtJobTerm findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtJobTerm tCkCtVeh = dao.find(id);
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
	protected void _auditError(JobEvent jobEvent, CkCtJobTerm ckCtVeh, Exception ex, Principal principal) {

	}

	@Override
	protected void _auditEvent(JobEvent jobEvent, CkCtJobTerm ckJobTerm, Principal principal) {
		LOG.debug("_auditEvent");
		try {
			if (ckJobTerm == null) {
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
			coreAuditLog.setAudtReckey(StringUtils.isEmpty(ckJobTerm.getJtId()) ? DASH : ckJobTerm.getJtId());
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
	protected CkCtJobTerm _cancelJob(CkCtJobTerm ckCtVeh, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTerm _completeJob(CkCtJobTerm arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTerm _confirmJob(CkCtJobTerm arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtJobTerm _createJob(CkCtJobTerm jobTerm, CkJob ckJob, Principal principal)
			throws ParameterException, ValidationException, ProcessingException, Exception {
		if (jobTerm == null) {
			throw new ParameterException("param dto null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		// TODO
		// compute debit note and platform fee.
		// fetch JobTruck
		CkJobTruck jobTruck = ckCtJobTruckService.findById(jobTerm.getTCkJobTruck().getJobId());

		TCkCtDebitNote dn = new TCkCtDebitNote();
		debitNoteService.computeDebitNoteAmount(dn, jobTruck, false);
		jobTerm.setJtJobDn(dn.getDnTotal().doubleValue());

		List<TCkCtPlatformInvoice> invList = platformInvoiceDao.findByJobId(jobTruck.getJobId());
		TCkCtPlatformInvoice coInv = null;
		TCkCtPlatformInvoice toInv = null;
		if (null != invList && invList.size() > 0) {
			// CO
			coInv = invList.stream().filter(inv -> jobTruck.getTCoreAccnByJobPartyCoFf().getAccnId()
					.equalsIgnoreCase(inv.getTCoreAccnByInvTo().getAccnId())).findFirst().orElse(null);
			// TO
			toInv = invList.stream().filter(inv -> jobTruck.getTCoreAccnByJobPartyTo().getAccnId()
					.equalsIgnoreCase(inv.getTCoreAccnByInvTo().getAccnId())).findFirst().orElse(null);
		}
		// compute invoice
		if (null == coInv) {
			coInv = platformInvoiceService.computePlatformFee4COff(jobTruck);
		}
		if (null == toInv) {
			toInv = platformInvoiceService.computePlatformFee4TO(jobTruck);
		}

		if (null != coInv)
			jobTerm.setJtJobPltfeeAmtCoff(coInv.getInvTotal().doubleValue());

		if (null != toInv)
			jobTerm.setJtJobPltfeeAmtTo(toInv.getInvTotal().doubleValue());

		CkCtJobTerm jobTermClone = add(jobTerm, principal);
		jobTermReqService.computeAmount(jobTermClone.getTCkCtJobTermReq().getJtrId());

		return jobTermClone;
	}

	@Override
	protected AbstractJobEvent<CkCtJobTerm> _getJobEvent(JobEvent arg0, CkCtJobTerm arg1, Principal arg2) {
		return null;
	}

	@Override
	protected CkCtJobTerm _newJob(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtJobTerm ckCtVeh = new CkCtJobTerm();

		return ckCtVeh;
	}

	@Override
	protected CkCtJobTerm _paidJob(CkCtJobTerm arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTerm _payJob(CkCtJobTerm arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTerm _rejectJob(CkCtJobTerm arg0, Principal arg1)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		return null;
	}

	@Override
	protected CkCtJobTerm _submitJob(CkCtJobTerm arg0, Principal arg1)
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
				.replace("tckCtVeh", "TCkCtVeh").replace("tckCtMstVehType", "TCkCtMstVehType")
				.replace("tcoreAccn", "TCoreAccn");
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
	protected CkCtJobTerm dtoFromEntity(TCkCtJobTerm tCkCtJobTerm) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtJobTerm == null) {
			throw new ParameterException("param entity null");
		}
		CkCtJobTerm ckCtVeh = new CkCtJobTerm(tCkCtJobTerm);

		if (tCkCtJobTerm.getTCkCtJobTermReq() != null) {
			ckCtVeh.setTCkCtJobTermReq(new CkCtJobTermReq(tCkCtJobTerm.getTCkCtJobTermReq()));
		}

		if (tCkCtJobTerm.getTCkJobTruck() != null) {
			ckCtVeh.setTCkJobTruck(new CkJobTruck(tCkCtJobTerm.getTCkJobTruck()));

			if (tCkCtJobTerm.getTCkJobTruck().getTCkJob() != null) {
				ckCtVeh.getTCkJobTruck().setTCkJob(new CkJob(tCkCtJobTerm.getTCkJobTruck().getTCkJob()));

				if (tCkCtJobTerm.getTCkJobTruck().getTCkJob().getTCkMstJobState() != null) {
					ckCtVeh.getTCkJobTruck().getTCkJob().setTCkMstJobState(
							new CkMstJobState(tCkCtJobTerm.getTCkJobTruck().getTCkJob().getTCkMstJobState()));
				}
			}

		}

		return ckCtVeh;
	}

	protected CkCtJobTerm dtoFromEntity(TCkCtJobTerm tCkCtJobTerm, boolean withData)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtJobTerm == null) {
			throw new ParameterException("param entity null");
		}

		return this.dtoFromEntity(tCkCtJobTerm);
	}

	@Override
	protected TCkCtJobTerm entityFromDTO(CkCtJobTerm ckCtVehDevice) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtVehDevice == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtJobTerm tCkCtJobTerm = ckCtVehDevice.toEntity(new TCkCtJobTerm());

		if (ckCtVehDevice.getTCkCtJobTermReq() != null) {
			tCkCtJobTerm.setTCkCtJobTermReq(ckCtVehDevice.getTCkCtJobTermReq().toEntity(new TCkCtJobTermReq()));
		}

		if (ckCtVehDevice.getTCkJobTruck() != null) {

			tCkCtJobTerm.setTCkJobTruck(ckCtVehDevice.getTCkJobTruck().toEntity(new TCkJobTruck()));

		}

		return tCkCtJobTerm;
	}

	@Override
	protected String entityKeyFromDTO(CkCtJobTerm ckCtVeh) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtVeh == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtVeh.getJtId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtJobTerm ckCtVeh)
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
	protected HashMap<String, Object> getParameters(CkCtJobTerm jobTerm)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (jobTerm == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();

		List<String> statusList = null;

		if (StringUtils.isNotBlank(jobTerm.getTCkCtJobTermReq().getJtrId())) {
			parameters.put("jtrId", "%" + jobTerm.getTCkCtJobTermReq().getJtrId() + "%");
		}
		/*-
		if (StringUtils.isNotBlank(ckCtVehDevice.getTdVehPlateNo())) {
			parameters.put("tdVehPlateNo", "%" + ckCtVehDevice.getTdVehPlateNo() + "%");
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
		
			parameters.put("tdDtActivate",  sdf.format(ckCtVehDevice.getTdDtActivate()));
		
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
			statusList = Arrays.asList(CkCtJobTermEnum.DEACTIVATE.name());
		}else {
			statusList = Arrays.asList(CkCtJobTermEnum.ACTIVATE.name(), CkCtJobTermEnum.NEW.name());
		}
		
		parameters.put("historyId",statusList);
		*/

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtJobTerm jobTerm, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (jobTerm == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();

		if (StringUtils.isNotBlank(jobTerm.getTCkCtJobTermReq().getJtrId())) {
			condition.append(getOperator(wherePrinted) + " o.TCkCtJobTermReq.jtrId like :jtrId");
			wherePrinted = true;
		}

		/*-
		if (StringUtils.isNotBlank(ckCtVehDevice.getTdVehPlateNo())) {
			condition.append(getOperator(wherePrinted) + " o.tdVehPlateNo like :tdVehPlateNo");
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
		if (ckCtVehDevice.getTdDtActivate()!= null ) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(o.tdDtActivate,'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "tdDtActivate");
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
		*/
		return condition.toString();
	}

	@Override
	protected TCkCtJobTerm initEnity(TCkCtJobTerm tCkCtJobTerm) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtJobTerm != null) {
			/*-
			Hibernate.initialize(tCkCtJobTerm.getTCkCtMstTrackDeviceState());
			if(null != tCkCtJobTerm.getTCkCtVeh() ) {
				Hibernate.initialize(tCkCtJobTerm.getTCkCtVeh());
			}
			if(null != tCkCtJobTerm.getTCkCtVeh() && tCkCtJobTerm.getTCkCtVeh().getTCkCtMstVehType() != null) {
				Hibernate.initialize(tCkCtJobTerm.getTCkCtVeh().getTCkCtMstVehType());
			}
			Hibernate.initialize(tCkCtJobTerm.getTCkCtVeh());
			*/
		}
		return tCkCtJobTerm;
	}

	@Override
	protected CkCtJobTerm preSaveUpdateDTO(TCkCtJobTerm tCkCtJobTerm, CkCtJobTerm ckCtJobTerm)
			throws ParameterException, ProcessingException {
		if (tCkCtJobTerm == null)
			throw new ParameterException("param storedEntity null");
		if (ckCtJobTerm == null)
			throw new ParameterException("param dto null");

		ckCtJobTerm.setJtUidCreate(tCkCtJobTerm.getJtUidCreate());
		ckCtJobTerm.setJtDtCreate(tCkCtJobTerm.getJtDtCreate());

		return ckCtJobTerm;
	}

	@Override
	protected void preSaveValidation(CkCtJobTerm arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtJobTerm arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkCtJobTerm setCoreMstLocale(CoreMstLocale arg0, CkCtJobTerm arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtJobTerm updateEntity(ACTION action, TCkCtJobTerm tCkCtJobTerm, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtJobTerm == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:

			if (StringUtils.isBlank(tCkCtJobTerm.getJtId())) {
				tCkCtJobTerm.setJtId(CkUtil.generateId(TCkCtJobTerm.PREFIX_ID));
			}
			tCkCtJobTerm.setJtUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			tCkCtJobTerm.setJtDtCreate(date);
			tCkCtJobTerm.setJtDtLupd(date);
			tCkCtJobTerm.setJtUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);

			tCkCtJobTerm.setJtStatus(Constant.ACTIVE_STATUS);

			break;

		case MODIFY:
			tCkCtJobTerm.setJtDtLupd(date);
			tCkCtJobTerm.setJtUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		default:
			break;
		}
		return tCkCtJobTerm;
	}

	@Override
	protected TCkCtJobTerm updateEntityStatus(TCkCtJobTerm tCkCtJobTerm, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtJobTerm == null) {
			throw new ParameterException("entity param null");
		}
		tCkCtJobTerm.setJtStatus(status);
		return tCkCtJobTerm;
	}

	@Override
	protected CkCtJobTerm whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtJobTerm jobTerm = new CkCtJobTerm();
		jobTerm.setTCkCtJobTermReq(new CkCtJobTermReq());

		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}

			if ("TCkCtJobTermReq.jtrId".equalsIgnoreCase(entityWhere.getAttribute())) {
				jobTerm.getTCkCtJobTermReq().setJtrId(entityWhere.getValue());

			}
			/*-
			else if ("tdVehPlateNo".equalsIgnoreCase(entityWhere.getAttribute())) {
				ckCtTrackDevice.setTdVehPlateNo(entityWhere.getValue());
				
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
					ckCtTrackDevice.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(CkCtJobTermEnum.ACTIVATE.name()));
				} else {
					ckCtTrackDevice.setTCkCtMstTrackDeviceState(new CkCtMstTrackDeviceState(CkCtJobTermEnum.DEACTIVATE.name()));
				}
				/
			}
			 */
		}

		return jobTerm;
	}

}
