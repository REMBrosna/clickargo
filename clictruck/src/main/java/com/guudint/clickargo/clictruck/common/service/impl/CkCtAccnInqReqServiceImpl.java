package com.guudint.clickargo.clictruck.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq;
import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq.ReqAction;
import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq.ReqState;
import com.guudint.clickargo.clictruck.common.listener.AccnInquiryEvent;
import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReq;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.manageaccn.service.CkManageAccnService;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsr;
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
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.util.PrincipalUtilService;

public class CkCtAccnInqReqServiceImpl
		extends AbstractClickCargoEntityService<TCkCtAccnInqReq, String, CkCtAccnInqReq> {

	private static Logger log = Logger.getLogger(CkCtAccnInqReqServiceImpl.class);

	private static String AUDIT_TAG = "ACCOUNT INQUIRY REQUEST";
	private static String TABLE_NAME = "T_CK_CT_ACCN_INQ_REQ";
	protected static String HISTORY = "history";
	protected static String DEFAULT = "default";

	@Autowired
	private CkManageAccnService ckManageAccnService;

	@Autowired
	@Qualifier("ckCtAccnInqReqDao")
	private GenericDao<TCkCtAccnInqReq, String> ckCtAccnInqReqDao;

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	@Qualifier("coreUserDao")
	private GenericDao<TCoreUsr, String> coreUserDao;

	public CkCtAccnInqReqServiceImpl() {
		super("ckCtAccnInqReqDao", AUDIT_TAG, TCkCtAccnInqReq.class.getName(), TABLE_NAME);
	}

	public String createAccnInquiryRequest(CkCtAccnInqReq req) throws ParameterException, Exception {
		log.debug("createAccnInquiryRequest");
		try {
			if (req == null)
				throw new ParameterException("param req null");
			if (StringUtils.isBlank(req.getAccnRegTaxNo()))
				throw new ParameterException("param accnRegTaxNo null or empty");

			// Find the account based on the company registration/tax no.
			TCoreAccn accn = ckManageAccnService.getAccountDetailsByRegNo(req.getAccnRegTaxNo());
			if (accn == null) {
				// just return ok, no need to inform requestor that the account exists or not
				return "ok";
			}

			Date now = Calendar.getInstance().getTime();
			// create the ID
			TCkCtAccnInqReq reqEntity = new TCkCtAccnInqReq();
			req.copyBeanProperties(reqEntity);
			reqEntity.setAirId(CkUtil.generateId("AIR"));
			reqEntity.setTCoreAccn(accn);
			reqEntity.setAirReqState(ReqState.PENDING.name());
			reqEntity.setAirStatus(RecordStatus.ACTIVE.getCode());
			reqEntity.setAirDtCreate(now);
			reqEntity.setAirDtLupd(now);
			reqEntity.setAirUidCreate("SYS");
			reqEntity.setAirUidLupd("SYS");
			this.dao.add(reqEntity);

			createaAudit(reqEntity, now, "SUBMITTED", null);

			return "ok";

		} catch (Exception ex) {
			log.error("createAccnInquiryRequest", ex);
			throw ex;
		}
	}

	@Override
	public CkCtAccnInqReq update(CkCtAccnInqReq dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		// move the status to IN-PROGRESS
		try {
			if (dto.getAction() == ReqAction.SAVE) {
				dto.setAirReqState(ReqState.INPROGRESS.name());
				dto = super.update(dto, principal, false);

			} else {
				dto.setAirDtProcessed(new Date());
				TCoreUsr usr = coreUserDao.find(principal.getUserId());
				dto.setTCoreUsr(usr == null ? null : new CoreUsr(usr));
				dto.setAirReqState(ReqState.COMPLETED.name());

				dto = super.update(dto, principal, false);
				audit(principal, dto.getAirId(), "RESPONDED");
				// Publish a listener here to save to notification and to send
				eventPublisher.publishEvent(new AccnInquiryEvent(this, dto));
			}

			return dto;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtAccnInqReq> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtAccnInqReq dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));

			String selectClause = "from TCkCtAccnInqReq o ";
			String orderByClause = filterRequest.getOrderBy().toString();
			List<TCkCtAccnInqReq> entities = this.findEntitiesByAnd(dto, selectClause, orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtAccnInqReq> dtos = entities.stream().map(x -> {
				try {
					CkCtAccnInqReq accn = dtoFromEntity(x);
					return accn;
				} catch (ParameterException e) {
					log.error("filterBy", e);
				} catch (ProcessingException e) {
					log.error("filterBy", e);
				}
				return null;
			}).collect(Collectors.toList());

			return dtos;
		} catch (ParameterException | ProcessingException ex) {
			log.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("filterBy", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtAccnInqReq whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		log.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtAccnInqReq dto = new CkCtAccnInqReq();

			CoreAccn accn = new CoreAccn();
			CoreUsr usr = new CoreUsr();

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnId"))
					accn.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnName"))
					accn.setAccnName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreUsr.usrUid"))
					usr.setUsrUid(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreUsr.usrName"))
					usr.setUsrName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("airEmailReq"))
					dto.setAirEmailReq(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("airReqState"))
					dto.setAirReqState(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("airStatus"))
					dto.setAirStatus(opValue.get().charAt(0));

				if (entityWhere.getAttribute().equalsIgnoreCase("airDtCreate"))
					dto.setAirDtCreate(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("airDtLupd"))
					dto.setAirDtLupd(sdfDate.parse(opValue.get()));

				if (entityWhere.getAttribute().equalsIgnoreCase("airUidCreate"))
					dto.setAirUidCreate(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("airUidLupd"))
					dto.setAirUidLupd(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("airDtProcessed"))
					dto.setAirDtProcessed(sdfDate.parse(opValue.get()));

				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}

			}

			dto.setTCoreAccn(accn);
			dto.setTCoreUsr(usr);
			return dto;
		} catch (ParameterException ex) {
			log.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	public CkCtAccnInqReq dtoFromEntity(TCkCtAccnInqReq entity) throws ParameterException, ProcessingException {
		log.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtAccnInqReq dto = new CkCtAccnInqReq(entity);

			Optional<TCoreAccn> opCoreAccn = Optional.ofNullable(entity.getTCoreAccn());
			dto.setTCoreAccn(opCoreAccn.isPresent() ? new CoreAccn(opCoreAccn.get()) : null);

			Optional<TCoreUsr> opCoreUsr = Optional.ofNullable(entity.getTCoreUsr());
			dto.setTCoreUsr(opCoreUsr.isPresent() ? new CoreUsr(opCoreUsr.get()) : null);

			return dto;
		} catch (ParameterException ex) {
			log.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	protected String getWhereClause(CkCtAccnInqReq dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal null");

			StringBuffer searchStatement = new StringBuffer();

			CoreAccn accn = principal.getCoreAccn();
			Optional<MstAccnType> opAccnType = Optional.ofNullable(accn.getTMstAccnType());
			// only proceed if it's SP
			if (opAccnType.isPresent() && opAccnType.get().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {

				// Exclude Registration Rejected from filter
				searchStatement.append(getOperator(wherePrinted)).append("o.airStatus = :airStatus");
				wherePrinted = true;

				searchStatement.append(getOperator(wherePrinted) + "o.airReqState IN (:airReqState)");
				wherePrinted = true;

				Optional<CoreAccn> opCoreAccn = Optional.ofNullable(dto.getTCoreAccn());
				if (opCoreAccn.isPresent()) {
					if (StringUtils.isNotBlank(opCoreAccn.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccn.accnId = :accnId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opCoreAccn.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccn.accnName LIKE :accnName");
						wherePrinted = true;
					}

				}

				Optional<CoreUsr> opCoreUsr = Optional.ofNullable(dto.getTCoreUsr());
				if (opCoreUsr.isPresent()) {
					if (StringUtils.isNotBlank(opCoreUsr.get().getUsrUid())) {
						searchStatement.append(getOperator(wherePrinted) + "o.TCoreUsr.usrUid = :usrUid");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opCoreUsr.get().getUsrName())) {
						searchStatement.append(getOperator(wherePrinted) + "o.TCoreUsr.usrName LIKE :usrName");
						wherePrinted = true;
					}
				}

				if (StringUtils.isNotBlank(dto.getAirEmailReq())) {
					searchStatement.append(getOperator(wherePrinted) + "o.airEmailReq LIKE :airEmailReq");
					wherePrinted = true;
				}

				if (StringUtils.isNotBlank(dto.getAirRemarks())) {
					searchStatement.append(getOperator(wherePrinted) + "o.airRemarks = :airRemarks");
					wherePrinted = true;
				}

				if (null != dto.getAirDtCreate()) {
					searchStatement
							.append(getOperator(wherePrinted) + "DATE_FORMAT(o.airDtCreate,'%d/%m/%Y') = :airDtCreate");
					wherePrinted = true;
				}

				if (null != dto.getAirDtLupd()) {
					searchStatement
							.append(getOperator(wherePrinted) + "DATE_FORMAT(o.airDtLupd,'%d/%m/%Y') = :airDtLupd");
					wherePrinted = true;
				}

				if (null != dto.getAirDtProcessed()) {
					searchStatement.append(
							getOperator(wherePrinted) + "DATE_FORMAT(o.airDtProcessed,'%d/%m/%Y') = :airDtProcessed");
					wherePrinted = true;
				}

				if (!StringUtils.isEmpty(dto.getAirUidCreate())) {
					searchStatement.append(getOperator(wherePrinted) + "o.airUidCreate LIKE :airUidCreate");
					wherePrinted = true;
				}

				if (!StringUtils.isEmpty(dto.getAirUidLupd())) {
					searchStatement.append(getOperator(wherePrinted) + "o.airUidLupd LIKE :airUidLupd");
					wherePrinted = true;
				}

			}

			return searchStatement.toString();
		} catch (ParameterException ex) {
			log.error("getWhereClause", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getWhereClause", ex);
			throw new ProcessingException(ex);
		}
	}

	protected HashMap<String, Object> getParameters(CkCtAccnInqReq dto) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
			HashMap<String, Object> parameters = new HashMap<String, Object>();

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal null");

			CoreAccn accn = principal.getCoreAccn();
			Optional<MstAccnType> opAccnType = Optional.ofNullable(accn.getTMstAccnType());
			// only proceed if it's SP
			if (opAccnType.isPresent() && opAccnType.get().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {

				parameters.put("airStatus", RecordStatus.ACTIVE.getCode());

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(DEFAULT)) {
					parameters.put("airReqState", Arrays.asList(ReqState.PENDING.name(), ReqState.INPROGRESS.name()));
				} else {
					parameters.put("airReqState", Arrays.asList(ReqState.COMPLETED.name()));
				}

				Optional<CoreAccn> opCoreAccn = Optional.ofNullable(dto.getTCoreAccn());
				if (opCoreAccn.isPresent()) {
					if (StringUtils.isNotBlank(opCoreAccn.get().getAccnId()))
						parameters.put("accnId", opCoreAccn.get().getAccnId());

					if (StringUtils.isNotBlank(opCoreAccn.get().getAccnName()))
						parameters.put("accnName", "%" + opCoreAccn.get().getAccnName() + "%");

				}

				Optional<CoreUsr> opCoreUsr = Optional.ofNullable(dto.getTCoreUsr());
				if (opCoreUsr.isPresent()) {
					if (StringUtils.isNotBlank(opCoreUsr.get().getUsrUid()))
						parameters.put("usrUid", opCoreUsr.get().getUsrUid());

					if (StringUtils.isNotBlank(opCoreUsr.get().getUsrName())) {
						parameters.put("usrName", "%" + opCoreUsr.get().getUsrName() + "%");
					}
				}

				if (StringUtils.isNotBlank(dto.getAirEmailReq()))
					parameters.put("airEmailReq", "%" + dto.getAirEmailReq() + "%");

				if (StringUtils.isNotBlank(dto.getAirRemarks()))
					parameters.put("airRemarks", "%" + dto.getAirRemarks() + "%");

				if (null != dto.getAirDtCreate())
					parameters.put("airDtCreate", sdfDate.format(dto.getAirDtCreate()));

				if (null != dto.getAirDtLupd())
					parameters.put("airDtLupd", sdfDate.format(dto.getAirDtLupd()));

				if (null != dto.getAirDtProcessed())
					parameters.put("airDtProcessed", sdfDate.format(dto.getAirDtProcessed()));

				if (!StringUtils.isEmpty(dto.getAirUidCreate()))
					parameters.put("airUidCreate", "%" + dto.getAirUidCreate() + "%");

				if (!StringUtils.isEmpty(dto.getAirUidLupd()))
					parameters.put("airUidLupd", "%" + dto.getAirUidLupd() + "%");

			}

			return parameters;
		} catch (ParameterException ex) {
			log.error("getParameters", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getParameters", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtAccnInqReq initEnity(TCkCtAccnInqReq entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCoreAccn());
			Hibernate.initialize(entity.getTCoreUsr());
		}
		return entity;
	}

	protected String getOperator(boolean whereprinted) {
		return whereprinted ? " AND " : " WHERE ";
	}

	@Override
	public CkCtAccnInqReq newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtAccnInqReq findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		log.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtAccnInqReq entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);
			this.initEnity(entity);

			return this.dtoFromEntity(entity);
		} catch (ParameterException | EntityNotFoundException ex) {
			log.error("findById", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("findById", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	public CkCtAccnInqReq deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		log.debug("deleteById");

		Date now = Calendar.getInstance().getTime();
		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");
			if (null == principal)
				throw new ParameterException("param prinicipal null");

			TCkCtAccnInqReq entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtAccnInqReq dto = dtoFromEntity(entity);
			this.delete(dto, principal);
			return dto;
		} catch (ParameterException | EntityNotFoundException ex) {
			log.error("deleteById", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("deleteById", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	protected TCkCtAccnInqReq entityFromDTO(CkCtAccnInqReq dto) throws ParameterException, ProcessingException {
		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtAccnInqReq entity = new TCkCtAccnInqReq();
			entity = dto.toEntity(entity);

			Optional<CoreAccn> opAccn = Optional.ofNullable(dto.getTCoreAccn());
			entity.setTCoreAccn(opAccn.isPresent() ? opAccn.get().toEntity(new TCoreAccn()) : null);

			Optional<CoreUsr> opUsr = Optional.ofNullable(dto.getTCoreUsr());
			entity.setTCoreUsr(opUsr.isPresent() ? opUsr.get().toEntity(new TCoreUsr()) : null);

			return entity;
		} catch (ParameterException ex) {
			log.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("entityFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected String entityKeyFromDTO(CkCtAccnInqReq dto) throws ParameterException, ProcessingException {
		log.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return dto.getAirId();
		} catch (ParameterException ex) {
			log.error("entityKeyFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("entityKeyFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtAccnInqReq updateEntity(ACTION attriubte, TCkCtAccnInqReq entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		log.debug("updateEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");
			if (null == principal)
				throw new ParameterException("param principal null");
			if (null == date)
				throw new ParameterException("param date null");

			Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
			switch (attriubte) {
			case CREATE:
				entity.setAirUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAirDtCreate(date);
				entity.setAirUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAirDtLupd(date);
				break;

			case MODIFY:
				entity.setAirUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAirDtLupd(date);
				break;

			default:
				break;
			}

			return entity;
		} catch (ParameterException ex) {
			log.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("updateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtAccnInqReq updateEntityStatus(TCkCtAccnInqReq entity, char status)
			throws ParameterException, ProcessingException {
		log.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setAirStatus(status);
			return entity;
		} catch (ParameterException ex) {
			log.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("updateEntityStatus", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtAccnInqReq preSaveUpdateDTO(TCkCtAccnInqReq storedEntity, CkCtAccnInqReq dto)
			throws ParameterException, ProcessingException {
		log.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setAirUidCreate(storedEntity.getAirUidCreate());
			dto.setAirDtCreate(storedEntity.getAirDtCreate());

			return dto;
		} catch (ParameterException ex) {
			log.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("preSaveUpdateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected void preSaveValidation(CkCtAccnInqReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtAccnInqReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtAccnInqReq dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtAccnInqReq setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtAccnInqReq dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	private void createaAudit(TCkCtAccnInqReq reqEntity, Date now, String action, Principal principal)
			throws Exception {
		TCoreAuditlog tCoreAuditlog = new TCoreAuditlog(null, this.moduleName + " " + action, now, "sys",
				reqEntity.getAirId());
		tCoreAuditlog.setAudtRemoteIp(getLocalAddress());
		tCoreAuditlog.setAudtUname(principal == null ? "sys" : principal.getUserId());
		tCoreAuditlog.setAudtRemarks(action);
		tCoreAuditlog.setAudtReckey(reqEntity.getAirId());
		tCoreAuditlog.setAudtParam1(
				StringUtils.isEmpty(entityName) ? "" : entityName.substring(entityName.lastIndexOf(".") + 1));
		auditLogDao.add(tCoreAuditlog);
	}
}
