package com.guudint.clickargo.clictruck.admin.contract.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clicservice.service.ICkWorkflowService;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtMstContractReqState;
import com.guudint.clickargo.clictruck.admin.contract.dto.ContractReqStateEnum;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtMstContractReqState;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractService;
import com.guudint.clickargo.clictruck.admin.contract.validator.ContractReqValidator;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.enums.WorkflowTypeEnum;
import com.guudint.clickargo.common.event.ApproveEvent;
import com.guudint.clickargo.common.event.RejectEvent;
import com.guudint.clickargo.common.event.SubmitEvent;
import com.guudint.clickargo.common.event.VerifyEvent;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
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
import com.vcc.camelone.master.dto.MstBank;
import com.vcc.camelone.master.dto.MstCurrency;
import com.vcc.camelone.master.model.TMstBank;
import com.vcc.camelone.master.model.TMstCurrency;

public class CkCtContractRequestServiceImpl
		extends AbstractClickCargoEntityService<TCkCtContractReq, String, CkCtContractReq> {

////////////////////
	private static Logger LOG = Logger.getLogger(CkCtContractRequestServiceImpl.class);
	private static final String PREFIX_KEY = "CRR";
	private static final String PREFIX_CR_REQ_KEY = "CRCHG";
	private static String AUDIT_TAG = "CK CONTRACT REQUEST";
	private static String TABLE_NAME = "T_CK_CT_CONTRACT_REQ";
	private static String HISTORY = "history";

	@Autowired
	private ContractReqValidator contractReqValidator;

	@Autowired
	private GenericDao<TCkCtContractCharge, String> ckCtContractChargeDao;

	@Autowired
	private CkCtContractService contractService;

	@Autowired
	@Qualifier("ccmAccnService")
	private IEntityService<TCoreAccn, String, CoreAccn> ccmAccnService;

	@Autowired
	@Qualifier("ckCtContractService")
	private IEntityService<TCkCtContract, String, CkCtContract> ckCtContractService;

	@Autowired
	@Qualifier("ckCtContractDao")
	private GenericDao<TCkCtContract, String> ckCtContractDao;

	@Autowired
	@Qualifier("contractReqWorkflowService")
	private ICkWorkflowService<TCkCtContractReq, CkCtContractReq> contractReqWorkflowService;

	public CkCtContractRequestServiceImpl() {
		super("ckCtContractReqDao", AUDIT_TAG, TCkCtContractReq.class.getName(), TABLE_NAME);
	}

	private SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public CkCtContractReq newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtContractReq cReq = new CkCtContractReq();
		cReq.setTCkCtContractChargeByCrChargeTo(new CkCtContractCharge());
		cReq.setTCkCtContractChargeByCrChargeCoFf(new CkCtContractCharge());
		cReq.setTCkCtContractChargeByCrChargeOpm(new CkCtContractCharge());
		cReq.setTCoreAccnByCrTo(new CoreAccn());
		cReq.setTCoreAccnByCrCoFf(new CoreAccn());
		cReq.setTMstBank(new MstBank());
		cReq.setTMstCurrency(new MstCurrency());
		cReq.setTCkCtMstContractReqState(new CkCtMstContractReqState(ContractReqStateEnum.NEW_REQ.getCode()));
		//Get the value from sysparam
		try {
			String vatSysParam = getSysParam(IPaymentService.KEY_CLICTRUCK_VAT_PERCENTAGE);
			if(StringUtils.isNotBlank(vatSysParam)) {
				double vat = Double.valueOf(vatSysParam);
				cReq.getTCkCtContractChargeByCrChargeCoFf().setConcAddtaxAmt(new BigDecimal(vat));
				cReq.getTCkCtContractChargeByCrChargeCoFf().setConcAddtaxType('P');
				cReq.getTCkCtContractChargeByCrChargeTo().setConcAddtaxAmt(new BigDecimal(vat));
				cReq.getTCkCtContractChargeByCrChargeTo().setConcAddtaxType('P');
			}
		} catch(Exception ex) {
			LOG.error(ex);
		}
		
		return cReq;

	}

	@Override
	public CkCtContractReq add(CkCtContractReq dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		try {

			List<ValidationError> validationErrors = contractReqValidator.validateCreate(dto, principal);

			if (!validationErrors.isEmpty()) {
				throw new ValidationException(this.validationErrorMap(validationErrors));
			} else {

				// validate first if the co/ff - to mapping already exist
				validateIfContractReqAllowed(dto);

				// update the req state if it's a NEW_REQ or NEW_UPDATE
				updateRequestStateByContract(dto);

				dto.setCrId(CkUtil.generateId(PREFIX_KEY));
				dto.setCrStatus(RecordStatus.ACTIVE.getCode());
				
				MstCurrency currency = new MstCurrency();
				currency.setCcyCode(Currencies.SGD.name());
				dto.setTMstCurrency(currency);

				// Save for contract first
				Optional<CkCtContractCharge> opToCharge = Optional.ofNullable(dto.getTCkCtContractChargeByCrChargeTo());
				if (opToCharge.isPresent()) {
					opToCharge.get().setConcId(CkUtil.generateId(PREFIX_CR_REQ_KEY));
					TCkCtContractCharge toChargeE = new TCkCtContractCharge();
					opToCharge.get().toEntity(toChargeE);
					ckCtContractChargeDao.add(toChargeE);
				}

				Optional<CkCtContractCharge> opCoFfCharge = Optional
						.ofNullable(dto.getTCkCtContractChargeByCrChargeCoFf());
				if (opCoFfCharge.isPresent()) {
					opCoFfCharge.get().setConcId(CkUtil.generateId(PREFIX_CR_REQ_KEY));
					TCkCtContractCharge coFfChargeE = new TCkCtContractCharge();
					opCoFfCharge.get().toEntity(coFfChargeE);
					ckCtContractChargeDao.add(coFfChargeE);
				}

				Optional<CkCtContractCharge> opOpmCharge = Optional
						.ofNullable(dto.getTCkCtContractChargeByCrChargeOpm());
				if (opOpmCharge.isPresent()) {
					opOpmCharge.get().setConcId(CkUtil.generateId(PREFIX_CR_REQ_KEY));
					TCkCtContractCharge opmChargeE = new TCkCtContractCharge();
					opOpmCharge.get().toEntity(opmChargeE);
					ckCtContractChargeDao.add(opmChargeE);
				}

				return super.add(dto, principal);
			}

		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtContractReq update(CkCtContractReq dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		try {

			if (null != dto.getAction() && dto.getAction() != FormActions.DELETE) {
				List<ValidationError> validationErrors = contractReqValidator.validateUpdate(dto, principal);
				if (!validationErrors.isEmpty()) {
					throw new ValidationException(this.validationErrorMap(validationErrors));
				}
			}

			// Update contract charges
			Optional<CkCtContractCharge> opToCharge = Optional.ofNullable(dto.getTCkCtContractChargeByCrChargeTo());
			if (opToCharge.isPresent() && StringUtils.isNotBlank(opToCharge.get().getConcId())) {
				TCkCtContractCharge toChargeE = ckCtContractChargeDao.find(opToCharge.get().getConcId());
				opToCharge.get().toEntity(toChargeE);
				ckCtContractChargeDao.update(toChargeE);
			}

			Optional<CkCtContractCharge> opCoFfCharge = Optional.ofNullable(dto.getTCkCtContractChargeByCrChargeCoFf());
			if (opCoFfCharge.isPresent()) {
				TCkCtContractCharge coFfChargeE = ckCtContractChargeDao.find(opCoFfCharge.get().getConcId());
				opCoFfCharge.get().toEntity(coFfChargeE);
				ckCtContractChargeDao.update(coFfChargeE);
			}

			dto.setCrStatus(RecordStatus.ACTIVE.getCode());

			if (null != dto.getAction()) {
				contractReqWorkflowService.moveState(dto.getAction(), dto, principal, ServiceTypes.CLICTRUCK);
				if (dto.getAction() == FormActions.SUBMIT) {
					dto.setCrUidRequestor(principal.getUserName());
					dto.setCrDtSubmit(new Date());
				} else if (dto.getAction() == FormActions.APPROVE || dto.getAction() == FormActions.REJECT) {
					dto.setCrUidApprover(principal.getUserName());
					dto.setCrDtApproveReject(new Date());
				}

				publishPostEvents(dto);

			} else {
				// if it is only update/save
				updateRequestStateByContract(dto);
			}

			// reset action
			dto.setAction(null);
			return super.update(dto, principal, false);

		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtContractReq findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			// Due to the update can be initiated from the contract management, search for
			// the id
			// first in TCkCtContract, if it's found then get the details from the contract
			try {
				// used this so as not to throw exception that will result in rollback
				TCkCtContract contract = ckCtContractDao.find(id);
				if (contract != null) {
					CkCtContract contractDto = ckCtContractService.findById(id);
					return copyContractToRequest(contractDto);
				}
			} catch (Exception e) {
				LOG.error("No contract found: " + id);
			}

			TCkCtContractReq entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);
			this.initEnity(entity);

			return this.dtoFromEntity(entity);

		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("findById", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("findById", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	public CkCtContractReq deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.debug("deleteById");

		Date now = Calendar.getInstance().getTime();
		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");
			if (null == principal)
				throw new ParameterException("param prinicipal null");

			String[] idParts = id.split(":");
			if (idParts.length != 2)
				throw new ParameterException("id not formulated " + id);

			TCkCtContractReq entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtContractReq dto = dtoFromEntity(entity);
			this.delete(dto, principal);
			return dto;
		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("deleteById", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("deleteById", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtContractReq> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtContractReq dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkCtContractReq o ";
			String orderByClauses = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtContractReq> entities = this.findEntitiesByAnd(dto, selectClause, orderByClauses,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtContractReq> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x);
				} catch (ParameterException | ProcessingException e) {
					LOG.error("filterBy", e);
				}
				return null;
			}).collect(Collectors.toList());

			return dtos;
		} catch (ParameterException | ProcessingException ex) {
			LOG.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("filterBy", ex);
			throw new ProcessingException(ex);
		}
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
	protected TCkCtContractReq initEnity(TCkCtContractReq entity) throws ParameterException, ProcessingException {
		LOG.info("initEnity");
		if (entity != null) {
			Hibernate.initialize(entity.getTCkCtContractChargeByCrChargeTo());
			Hibernate.initialize(entity.getTCkCtContractChargeByCrChargeCoFf());
			Hibernate.initialize(entity.getTCkCtContractChargeByCrChargeOpm());
			Hibernate.initialize(entity.getTCoreAccnByCrTo());
			Hibernate.initialize(entity.getTCoreAccnByCrCoFf());
			Hibernate.initialize(entity.getTMstBank());
			Hibernate.initialize(entity.getTMstCurrency());
			Hibernate.initialize(entity.getTCkCtMstContractReqState());
		}

		return entity;
	}

	@Override
	protected TCkCtContractReq entityFromDTO(CkCtContractReq dto) throws ParameterException, ProcessingException {
		LOG.info("entityFromDTO");
		try {

			if (dto == null)
				throw new ParameterException("param dto null");

			TCkCtContractReq entity = new TCkCtContractReq();
			dto.toEntity(entity);
			Optional<CkCtContractCharge> opChargeTo = Optional.ofNullable(dto.getTCkCtContractChargeByCrChargeTo());
			if (opChargeTo.isPresent())
				entity.setTCkCtContractChargeByCrChargeTo(opChargeTo.get().toEntity(new TCkCtContractCharge()));

			Optional<CkCtContractCharge> opChargeCoFf = Optional.ofNullable(dto.getTCkCtContractChargeByCrChargeCoFf());
			if (opChargeCoFf.isPresent())
				entity.setTCkCtContractChargeByCrChargeCoFf(opChargeCoFf.get().toEntity(new TCkCtContractCharge()));

			Optional<CkCtMstContractReqState> opState = Optional.ofNullable(dto.getTCkCtMstContractReqState());
			if (opState.isPresent())
				entity.setTCkCtMstContractReqState(opState.get().toEntity(new TCkCtMstContractReqState()));

			Optional<CoreAccn> opAccnTo = Optional.ofNullable(dto.getTCoreAccnByCrTo());
			if (opAccnTo.isPresent() && StringUtils.isNotBlank(opAccnTo.get().getAccnId()))
				entity.setTCoreAccnByCrTo(opAccnTo.get().toEntity(new TCoreAccn()));

			Optional<CoreAccn> opAccnCoFf = Optional.ofNullable(dto.getTCoreAccnByCrCoFf());
			if (opAccnCoFf.isPresent() && StringUtils.isNotBlank(opAccnCoFf.get().getAccnId()))
				entity.setTCoreAccnByCrCoFf(opAccnCoFf.get().toEntity(new TCoreAccn()));

			Optional<MstCurrency> opMstCurr = Optional.ofNullable(dto.getTMstCurrency());
			if (opMstCurr.isPresent() && StringUtils.isNotBlank(opMstCurr.get().getCcyCode()))
				entity.setTMstCurrency(opMstCurr.get().toEntity(new TMstCurrency()));

			Optional<CkCtContractCharge> opChargeOpm = Optional.ofNullable(dto.getTCkCtContractChargeByCrChargeOpm());
			if (opChargeOpm.isPresent())
				entity.setTCkCtContractChargeByCrChargeOpm(opChargeOpm.get().toEntity(new TCkCtContractCharge()));

			Optional<MstBank> opMstBank = Optional.ofNullable(dto.getTMstBank());
			if (opMstBank.isPresent() && StringUtils.isNotBlank(opMstBank.get().getBankId()))
				entity.setTMstBank(opMstBank.get().toEntity(new TMstBank()));

			return entity;
		} catch (ParameterException ex) {
			LOG.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityFromDTO", ex);
			throw new ProcessingException(ex);
		}

	}

	@Override
	protected CkCtContractReq dtoFromEntity(TCkCtContractReq entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {

			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtContractReq dto = new CkCtContractReq(entity);

			Optional<TCkCtContractCharge> opChargeToEntity = Optional
					.ofNullable(entity.getTCkCtContractChargeByCrChargeTo());
			if (opChargeToEntity.isPresent()) {
				dto.setTCkCtContractChargeByCrChargeTo(new CkCtContractCharge(opChargeToEntity.get()));
				if (dto.getTCkCtContractChargeByCrChargeTo().getConcAddtaxAmt() != null
						&& dto.getTCkCtContractChargeByCrChargeTo().getConcAddtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
					dto.setAdditionalTaxTo(true);
				}

				if (dto.getTCkCtContractChargeByCrChargeTo().getConcWhtaxAmt() != null
						&& dto.getTCkCtContractChargeByCrChargeTo().getConcWhtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
					dto.setWitholdTaxTo(true);
				}
			}

			Optional<TCkCtContractCharge> opChargeCoFfEntity = Optional
					.ofNullable(entity.getTCkCtContractChargeByCrChargeCoFf());
			if (opChargeCoFfEntity.isPresent()) {
				dto.setTCkCtContractChargeByCrChargeCoFf(new CkCtContractCharge(opChargeCoFfEntity.get()));
				if (dto.getTCkCtContractChargeByCrChargeCoFf().getConcAddtaxAmt() != null && dto
						.getTCkCtContractChargeByCrChargeCoFf().getConcAddtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
					dto.setAdditionalTaxCoFf(true);
				}

				if (dto.getTCkCtContractChargeByCrChargeCoFf().getConcWhtaxAmt() != null && dto
						.getTCkCtContractChargeByCrChargeCoFf().getConcWhtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
					dto.setWitholdTaxCoFf(true);
				}
			}

			Optional<TCkCtContractCharge> opChargeOpmEntity = Optional
					.ofNullable(entity.getTCkCtContractChargeByCrChargeOpm());
			if (opChargeOpmEntity.isPresent()) {
				dto.setTCkCtContractChargeByCrChargeOpm(new CkCtContractCharge(opChargeOpmEntity.get()));
				if (dto.getTCkCtContractChargeByCrChargeOpm().getConcAddtaxAmt() != null && dto
						.getTCkCtContractChargeByCrChargeOpm().getConcAddtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
					dto.setAdditionalTaxOpm(true);
				}

				if (dto.getTCkCtContractChargeByCrChargeOpm().getConcWhtaxAmt() != null
						&& dto.getTCkCtContractChargeByCrChargeOpm().getConcWhtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
					dto.setWitholdTaxCoFf(true);
				}
			}

			Optional<TCkCtMstContractReqState> opStateEntity = Optional
					.ofNullable(entity.getTCkCtMstContractReqState());
			if (opStateEntity.isPresent())
				dto.setTCkCtMstContractReqState(new CkCtMstContractReqState(opStateEntity.get()));

			Optional<TCoreAccn> opAccnToEntity = Optional.ofNullable(entity.getTCoreAccnByCrTo());
			if (opAccnToEntity.isPresent())
				dto.setTCoreAccnByCrTo(new CoreAccn(opAccnToEntity.get()));

			Optional<TCoreAccn> opAccnCoFfEntity = Optional.ofNullable(entity.getTCoreAccnByCrCoFf());
			if (opAccnCoFfEntity.isPresent())
				dto.setTCoreAccnByCrCoFf(new CoreAccn(opAccnCoFfEntity.get()));

			Optional<TMstBank> opBankEntity = Optional.ofNullable(entity.getTMstBank());
			if (opBankEntity.isPresent())
				dto.setTMstBank(new MstBank(opBankEntity.get()));

			Optional<TMstCurrency> opMstCurrEntity = Optional.ofNullable(entity.getTMstCurrency());
			if (opMstCurrEntity.isPresent())
				dto.setTMstCurrency(new MstCurrency(opMstCurrEntity.get()));

			return dto;

		} catch (ParameterException ex) {
			LOG.error("dtoFromEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected String entityKeyFromDTO(CkCtContractReq dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return null == dto.getCrId() ? null : dto.getCrId();
		} catch (ParameterException ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtContractReq updateEntity(ACTION attriubte, TCkCtContractReq entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");

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
				entity.setCrUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCrDtCreate(date);
				entity.setCrUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCrDtLupd(date);
				break;

			case MODIFY:
				entity.setCrUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setCrDtLupd(date);
				break;

			default:
				break;
			}

			return entity;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("updateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtContractReq updateEntityStatus(TCkCtContractReq entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setCrStatus(status);
			return entity;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("updateEntityStatus", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtContractReq preSaveUpdateDTO(TCkCtContractReq storedEntity, CkCtContractReq dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setCrUidCreate(storedEntity.getCrUidCreate());
			dto.setCrDtCreate(storedEntity.getCrDtCreate());

			return dto;
		} catch (ParameterException ex) {
			LOG.error("updateEntity", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("preSaveUpdateEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected void preSaveValidation(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtContractReq dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
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

				searchStatement.append(getOperator(wherePrinted))
						.append("o.TCkCtMstContractReqState.stId IN :contractReqStates");
				wherePrinted = true;

				if (StringUtils.isNotBlank(dto.getCrId())) {
					searchStatement.append(getOperator(wherePrinted)).append("o.crId like :crId");
					wherePrinted = true;
				}

				if (StringUtils.isNotBlank(dto.getCrName())) {
					searchStatement.append(getOperator(wherePrinted)).append("o.crName like :crName");
					wherePrinted = true;
				}

				Optional<CoreAccn> opAccnTo = Optional.ofNullable(dto.getTCoreAccnByCrTo());
				if (opAccnTo.isPresent()) {
					if (StringUtils.isNotBlank(opAccnTo.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCoreAccnByCrTo.accnId = :toAccnId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opAccnTo.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCoreAccnByCrTo.accnName like :toAccnName");
						wherePrinted = true;
					}
				}

				Optional<CoreAccn> opAccnCoFf = Optional.ofNullable(dto.getTCoreAccnByCrCoFf());
				if (opAccnCoFf.isPresent()) {
					if (StringUtils.isNotBlank(opAccnCoFf.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCoreAccnByCrCoFf.accnId = :coFfAccnId");
						wherePrinted = true;
					}

					if (StringUtils.isNotBlank(opAccnCoFf.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted))
								.append("o.TCoreAccnByCrCoFf.accnName like :coFfAccnName");
						wherePrinted = true;
					}
				}

				Optional<Date> opStartDate = Optional.ofNullable(dto.getCrDtStart());
				if (opStartDate.isPresent() && null != opStartDate.get()) {
					searchStatement
							.append(getOperator(wherePrinted) + "DATE_FORMAT(o.crDtStart,'%d/%m/%Y') = :crDtStart");
					wherePrinted = true;
				}

				Optional<Date> opEndDate = Optional.ofNullable(dto.getCrDtEnd());
				if (opEndDate.isPresent() && null != opEndDate.get()) {
					searchStatement
							.append(getOperator(wherePrinted) + "DATE_FORMAT(o.crDtStart,'%d/%m/%Y') = :crDtEnd");
					wherePrinted = true;
				}

				Optional<Date> opCreateDate = Optional.ofNullable(dto.getCrDtCreate());
				if (opCreateDate.isPresent() && null != opCreateDate.get()) {
					searchStatement
							.append(getOperator(wherePrinted) + "DATE_FORMAT(o.crDtCreate,'%d/%m/%Y') = :crDtCreate");
					wherePrinted = true;
				}

				Optional<Date> opLupdDate = Optional.ofNullable(dto.getCrDtLupd());
				if (opLupdDate.isPresent() && null != opLupdDate.get()) {
					searchStatement
							.append(getOperator(wherePrinted) + "DATE_FORMAT(o.crDtLupd,'%d/%m/%Y') = :crDtLupd");
					wherePrinted = true;
				}

				Optional<CkCtMstContractReqState> opState = Optional.ofNullable(dto.getTCkCtMstContractReqState());
				if (opState.isPresent() && null != opState.get() && StringUtils.isNotBlank(opState.get().getStId())) {
					searchStatement.append(getOperator(wherePrinted) + "o.TCkCtMstContractReqState.stId = :state");
					wherePrinted = true;
				}

			}

			return searchStatement.toString();
		} catch (ParameterException ex) {
			LOG.error("getWhereClause", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getWhereClause", ex);
			throw new ProcessingException(ex);
		}

	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtContractReq dto)
			throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal null");

			CoreAccn accn = principal.getCoreAccn();
			Optional<MstAccnType> opAccnType = Optional.ofNullable(accn.getTMstAccnType());
			// only proceed if it's SP
			if (opAccnType.isPresent() && opAccnType.get().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {

				dto.setL1(
						principal.getRoleList().stream().anyMatch(e -> Arrays.asList(Roles.SP_L1.name()).contains(e)));
				dto.setFinance(principal.getRoleList().stream()
						.anyMatch(e -> Arrays.asList(Roles.SP_FIN_HD.name()).contains(e)));

				if (dto.getHistory() != null && dto.getHistory().equalsIgnoreCase(HISTORY)) {
					List<String> reqStates = new ArrayList<>();
					reqStates.add(ContractReqStateEnum.EXPIRED.name());
					reqStates.add(ContractReqStateEnum.NEW_REJECTED.name());
					reqStates.add(ContractReqStateEnum.UPDATE_REJECTED.name());
					reqStates.add(ContractReqStateEnum.EXPORTED.name());
					if (dto.isL1()) {
						reqStates.add(ContractReqStateEnum.DELETED.name());
					}
					parameters.put("contractReqStates", reqStates);

				} else {
					List<String> reqStates = new ArrayList<>();
					reqStates.add(ContractReqStateEnum.NEW_SUBMITTED.name());
					reqStates.add(ContractReqStateEnum.UPDATE_SUBMITTED.name());
					reqStates.add(ContractReqStateEnum.UPDATE_APPROVED.name());
					reqStates.add(ContractReqStateEnum.NEW_APPROVED.name());
					reqStates.add(ContractReqStateEnum.RENEWAL_APPROVED.name());
					reqStates.add(ContractReqStateEnum.RENEWAL_SUBMITTED.name());
					if (dto.isL1()) {
						reqStates.add(ContractReqStateEnum.NEW_REQ.name());
						reqStates.add(ContractReqStateEnum.NEW_UPDATE.name());
						reqStates.add(ContractReqStateEnum.RENEWAL_REQ.name());
					}
					parameters.put("contractReqStates", reqStates);
				}

				if (StringUtils.isNotBlank(dto.getCrId()))
					parameters.put("crId", "%" + dto.getCrId() + "%");

				if (StringUtils.isNotBlank(dto.getCrName()))
					parameters.put("crName", "%" + dto.getCrName() + "%");

				Optional<CoreAccn> opAccnTo = Optional.ofNullable(dto.getTCoreAccnByCrTo());
				if (opAccnTo.isPresent()) {
					if (StringUtils.isNotBlank(opAccnTo.get().getAccnId()))
						parameters.put("toAccnId", opAccnTo.get().getAccnId());

					if (StringUtils.isNotBlank(opAccnTo.get().getAccnName()))
						parameters.put("toAccnName", "%" + opAccnTo.get().getAccnName() + "%");
				}

				Optional<CoreAccn> opAccnCoFf = Optional.ofNullable(dto.getTCoreAccnByCrCoFf());
				if (opAccnCoFf.isPresent()) {
					if (StringUtils.isNotBlank(opAccnCoFf.get().getAccnId()))
						parameters.put("coFfAccnId", opAccnCoFf.get().getAccnId());

					if (StringUtils.isNotBlank(opAccnCoFf.get().getAccnName()))
						parameters.put("coFfAccnName", "%" + opAccnCoFf.get().getAccnName() + "%");
				}

				Optional<Date> opStartDate = Optional.ofNullable(dto.getCrDtStart());
				if (opStartDate.isPresent() && null != opStartDate.get())
					parameters.put("crDtStart", sdfDate.format(opStartDate.get()));

				Optional<Date> opEndDate = Optional.ofNullable(dto.getCrDtEnd());
				if (opEndDate.isPresent() && null != opEndDate.get())
					parameters.put("crDtEnd", sdfDate.format(opEndDate.get()));

				Optional<Date> opCreateDate = Optional.ofNullable(dto.getCrDtCreate());
				if (opCreateDate.isPresent() && null != opCreateDate.get())
					parameters.put("crDtCreate", sdfDate.format(opCreateDate.get()));

				Optional<Date> opLupdDate = Optional.ofNullable(dto.getCrDtLupd());
				if (opLupdDate.isPresent() && null != opLupdDate.get())
					parameters.put("crDtLupd", sdfDate.format(opLupdDate.get()));

				Optional<CkCtMstContractReqState> opState = Optional.ofNullable(dto.getTCkCtMstContractReqState());
				if (opState.isPresent() && null != opState.get() && StringUtils.isNotBlank(opState.get().getStId()))
					parameters.put("state", opState.get().getStId());

			}

			return parameters;
		} catch (ParameterException ex) {
			LOG.error("getParameters", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getParameters", ex);
			throw new ProcessingException(ex);
		}

	}

	@Override
	protected CkCtContractReq whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtContractReq dto = new CkCtContractReq();
			CoreAccn accnTo = new CoreAccn();
			CoreAccn accnCoFf = new CoreAccn();
			CkCtMstContractReqState state = new CkCtMstContractReqState();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("crId"))
					dto.setCrId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("crName"))
					dto.setCrName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccnByCrTo.accnId"))
					accnTo.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccnByCrTo.accnName"))
					accnTo.setAccnName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccnByCrCoFf.accnId"))
					accnCoFf.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccnByCrCoFf.accnName"))
					accnCoFf.setAccnName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("crDtStart"))
					dto.setCrDtStart(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("crDtEnd"))
					dto.setCrDtEnd(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("crDtCreate"))
					dto.setCrDtCreate(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("crDtLupd"))
					dto.setCrDtLupd(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtMstContractReqState.stId"))
					state.setStId(opValue.get());

				// history toggle
				if (entityWhere.getAttribute().equalsIgnoreCase(HISTORY)) {
					dto.setHistory(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase(DEFAULT)) {
					dto.setHistory(opValue.get());
				}

			}

			dto.setTCoreAccnByCrTo(accnTo);
			dto.setTCoreAccnByCrCoFf(accnCoFf);
			dto.setTCkCtMstContractReqState(state);

			return dto;
		} catch (ParameterException ex) {
			LOG.error("whereDto", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}

	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtContractReq dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtContractReq setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtContractReq dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtContractReq> getContractRequestsByDateAndState(Date startDt, List<String> reqStates)
			throws Exception {

		List<CkCtContractReq> reqList = new ArrayList<>();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		String hql = "from TCkCtContractReq o where DATE_FORMAT(o.crDtStart,'%Y-%m-%d') = :startDt "
				+ " and o.TCkCtMstContractReqState.stId in (:reqState) and o.crStatus=:status";
		Map<String, Object> params = new HashMap<>();
		params.put("startDt", sdfDate.format(startDt));
		params.put("reqState", reqStates);
		params.put("status", RecordStatus.ACTIVE.getCode());
		List<TCkCtContractReq> list = dao.getByQuery(hql, params);
		if (list != null && list.size() > 0) {
			for (TCkCtContractReq req : list) {
				initEnity(req);
				CkCtContractReq dto = dtoFromEntity(req);
				reqList.add(dto);
			}
		}

		return reqList;
	}

	///// Helper Methods
	///////////////////////
	private void updateRequestStateByContract(CkCtContractReq dto) throws Exception {
		Optional<CoreAccn> toAccn = Optional.ofNullable(dto.getTCoreAccnByCrTo());
		Optional<CoreAccn> coFfAccn = Optional.ofNullable(dto.getTCoreAccnByCrCoFf());

		// Check if there is an existing valid to date contract between to & coff
		CkCtContract contract = null;
		if (toAccn.isPresent() && StringUtils.isNotBlank(toAccn.get().getAccnId()) && coFfAccn.isPresent()
				&& StringUtils.isNotBlank(coFfAccn.get().getAccnId()) && dto.getCrDtStart() != null
				&& dto.getCrDtEnd() != null) {
			contract = contractService.getContractByAccounts(dto.getTCoreAccnByCrTo().getAccnId(),
					dto.getTCoreAccnByCrCoFf().getAccnId(), dto.getCrDtStart(), dto.getCrDtEnd(),
					Arrays.asList(RecordStatus.ACTIVE.getCode()));
		}

		// if above returns contract, that means there is still a valid contract and the
		// req start/end date is still within
		// that contract validity date, then it is considered and update request.
		// Otherwise it will be a new request
		if (contract != null) {
			CkCtMstContractReqState updateReqState = new CkCtMstContractReqState(
					ContractReqStateEnum.NEW_UPDATE.getCode());
			dto.setTCkCtMstContractReqState(updateReqState);
		} else {
			// check if there is a co/ff - to contract existing regardless of the start/end
			// date
			contract = contractService.getContractByAccounts(dto.getTCoreAccnByCrTo().getAccnId(),
					dto.getTCoreAccnByCrCoFf().getAccnId(), null, null, null);

			if (contract == null) {
				CkCtMstContractReqState updateReqState = new CkCtMstContractReqState(
						ContractReqStateEnum.NEW_REQ.getCode());
				dto.setTCkCtMstContractReqState(updateReqState);
			} else {
				CkCtMstContractReqState updateReqState = new CkCtMstContractReqState(
						ContractReqStateEnum.RENEWAL_REQ.getCode());
				dto.setTCkCtMstContractReqState(updateReqState);
			}

		}

	}

	private CkCtContractReq copyContractToRequest(CkCtContract contract) {
		CkCtContractReq ctReq = new CkCtContractReq();
		ctReq.setCrPaytermTo(contract.getConPaytermTo());
		ctReq.setCrPaytermCoFf(contract.getConPaytermCoFf());
		ctReq.setCrName(contract.getConName());
		ctReq.setCrDescription(contract.getConDescription());
		ctReq.setCrDtStart(contract.getConDtStart());
		ctReq.setCrDtEnd(contract.getConDtEnd());

		// get the details
		CkCtContractCharge reqToCharge = new CkCtContractCharge();
		contract.getTCkCtContractChargeByConChargeTo().copyBeanProperties(reqToCharge);
		ctReq.setTCkCtContractChargeByCrChargeTo(reqToCharge);
		if (reqToCharge.getConcAddtaxAmt() != null && reqToCharge.getConcAddtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
			ctReq.setAdditionalTaxTo(true);
		}

		if (reqToCharge.getConcWhtaxAmt() != null && reqToCharge.getConcWhtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
			ctReq.setWitholdTaxTo(true);
		}

		CkCtContractCharge reqCoFfCharge = new CkCtContractCharge();
		contract.getTCkCtContractChargeByConChargeCoFf().copyBeanProperties(reqCoFfCharge);
		ctReq.setTCkCtContractChargeByCrChargeCoFf(reqCoFfCharge);

		if (reqCoFfCharge.getConcAddtaxAmt() != null
				&& reqCoFfCharge.getConcAddtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
			ctReq.setAdditionalTaxCoFf(true);
		}

		if (reqCoFfCharge.getConcWhtaxAmt() != null && reqCoFfCharge.getConcWhtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
			ctReq.setWitholdTaxCoFf(true);
		}

		// Opm contract charge
		CkCtContractCharge reqOpmCharge = new CkCtContractCharge();
		if (contract.getTCkCtContractChargeByConOpm() != null) {
			contract.getTCkCtContractChargeByConOpm().copyBeanProperties(reqOpmCharge);
		}

		ctReq.setTCkCtContractChargeByCrChargeOpm(reqOpmCharge);

		if (reqOpmCharge.getConcAddtaxAmt() != null && reqOpmCharge.getConcAddtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
			ctReq.setAdditionalTaxOpm(true);
		}

		if (reqOpmCharge.getConcWhtaxType() != null && reqOpmCharge.getConcWhtaxAmt().compareTo(BigDecimal.ZERO) > 0) {
			ctReq.setAdditionalTaxOpm(true);
		}

		CoreAccn reqToAccn = new CoreAccn();
		contract.getTCoreAccnByConTo().copyBeanProperties(reqToAccn);
		ctReq.setTCoreAccnByCrTo(reqToAccn);

		CoreAccn reqCoFfAccn = new CoreAccn();
		contract.getTCoreAccnByConCoFf().copyBeanProperties(reqCoFfAccn);
		ctReq.setTCoreAccnByCrCoFf(reqCoFfAccn);

		MstBank reqBank = new MstBank();
		if (contract.getTMstBank() != null)
			contract.getTMstBank().copyBeanProperties(reqBank);
		ctReq.setTMstBank(reqBank);

		MstCurrency reqMstCurr = new MstCurrency();
		contract.getTMstCurrency().copyBeanProperties(reqMstCurr);
		ctReq.setTMstCurrency(reqMstCurr);

		ctReq.setTCkCtMstContractReqState(new CkCtMstContractReqState(ContractReqStateEnum.NEW_UPDATE.name()));
		return ctReq;

	}

	/**
	 * To check that the contract between co/ff - to has only one request
	 */
	private void validateIfContractReqAllowed(CkCtContractReq reqDto) throws Exception {
		if (reqDto == null)
			throw new ParameterException("param reqDto null");

		if (reqDto.getTCoreAccnByCrCoFf() != null && reqDto.getTCoreAccnByCrTo() != null) {
			String hql = "from TCkCtContractReq o where o.TCoreAccnByCrTo.accnId = :toAccnId "
					+ "and o.TCoreAccnByCrCoFf.accnId=:coFfAccnId and o.crStatus = :status"
					+ " and o.TCkCtMstContractReqState.stId not in (:excludeReqState)";
			Map<String, Object> params = new HashMap<>();
			params.put("toAccnId", reqDto.getTCoreAccnByCrTo().getAccnId());
			params.put("coFfAccnId", reqDto.getTCoreAccnByCrCoFf().getAccnId());
			params.put("status", RecordStatus.ACTIVE.getCode());
			// update approve is added in the lists of req state to check because
			params.put("excludeReqState", Arrays.asList(ContractReqStateEnum.DELETED.name(),
					ContractReqStateEnum.UPDATE_REJECTED.name(), ContractReqStateEnum.NEW_REJECTED.name()));

			List<TCkCtContractReq> list = dao.getByQuery(hql, params);
			if (list != null && list.size() > 0) {
				CoreAccn coFfAccn = ccmAccnService.findById(reqDto.getTCoreAccnByCrCoFf().getAccnId());
				CoreAccn toAccn = ccmAccnService.findById(reqDto.getTCoreAccnByCrTo().getAccnId());

				Map<String, Object> validateErrParam = new HashMap<>();
				ObjectMapper mapper = new ObjectMapper();
				validateErrParam.put("contract-req-not-allowed", "At least one contract request is allowed for "
						+ StringUtils.join(Arrays.asList(coFfAccn.getAccnName(), toAccn.getAccnName()), " - "));
				throw new ValidationException(mapper.writeValueAsString(validateErrParam));
			}

		}

	}

	private void publishPostEvents(CkCtContractReq dto) throws Exception {

		ApplicationEvent appEvent = null;

		if (dto.getAction() == FormActions.SUBMIT) {
			appEvent = new SubmitEvent<TCkCtContractReq, CkCtContractReq>(this, WorkflowTypeEnum.CONTRACT_REQ, dto);

		} else if (dto.getAction() == FormActions.VERIFY) {
			appEvent = new VerifyEvent<TCkCtContractReq, CkCtContractReq>(this, WorkflowTypeEnum.CONTRACT_REQ, dto);

		} else if (dto.getAction() == FormActions.APPROVE) {
			appEvent = new ApproveEvent<TCkCtContractReq, CkCtContractReq>(this, WorkflowTypeEnum.CONTRACT_REQ, dto);

		} else if (dto.getAction() == FormActions.REJECT) {
			appEvent = new RejectEvent<TCkCtContractReq, CkCtContractReq>(this, WorkflowTypeEnum.CONTRACT_REQ, dto);

		}

		if (appEvent != null) {
			eventPublisher.publishEvent(appEvent);
		}

	}

	/**
	 * 
	 * @param orderBy
	 * @return
	 */
	private EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) {
		if (orderBy == null) {
			return null;
		}
		if (StringUtils.isEmpty(orderBy.getAttribute())) {
			return null;
		}
		String newAttr = orderBy.getAttribute();
		newAttr = newAttr.replaceAll("tcoreAccnByCrTo", "TCoreAccnByCrTo")
				.replaceAll("tcoreAccnByCrCoFf", "TCoreAccnByCrCoFf")
				.replaceAll("tckCtMstContractReqState", "TCkCtMstContractReqState");
		orderBy.setAttribute(newAttr);
		return orderBy;
	}

}
