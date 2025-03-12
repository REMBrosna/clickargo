package com.guudint.clickargo.clictruck.opm.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.finacing.constant.FinancingConstants.FinancingTypes;
import com.guudint.clickargo.clictruck.finacing.service.impl.TruckJobCreditServiceImpl;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmJournalDao;
import com.guudint.clickargo.clictruck.opm.dto.CkOpmJournal;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmJournal;
import com.guudint.clickargo.clictruck.opm.service.IOpmService;
import com.guudint.clickargo.clictruck.opm.service.IOpmValidator;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.master.dto.CkMstJournalTxnType;
import com.guudint.clickargo.master.dto.CkMstServiceType;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.model.TCkMstJournalTxnType;
import com.guudint.clickargo.master.model.TCkMstServiceType;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.master.dto.MstCurrency;
import com.vcc.camelone.master.model.TMstCurrency;
import com.vcc.camelone.util.PrincipalUtilService;

@Service
public class OpmServiceImpl implements IOpmService {

	private static Logger LOG = Logger.getLogger(TruckJobCreditServiceImpl.class);

	@Autowired
	private CkOpmJournalDao opmJournalDao;

	@Autowired
	private IOpmValidator opmValidator;

	@Autowired
	private PrincipalUtilService principalUtilService;

	/**
	 * When CO submit job or reimbursement
	 */
	@Override
	public void reserveOpmJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, BigDecimal amount,
			Principal principal) throws Exception {
		LOG.info("reserveJobTruckCredit");
		try {
			if (journalTxnType == null)
				throw new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				throw new ParameterException("param jobTruck null");
			if (amount == null)
				throw new ParameterException("param amount null");
			if (principal == null)
				throw new ParameterException("param principal null");

			LOG.info("Total Trip Charge to Reserve: " + amount);

			// if it's OT, submit should not log a journal, it should log only during accept
			if ( FinancingTypes.OT.name().equalsIgnoreCase(jobTruck.getJobFinanceOpt())
					&& journalTxnType == JournalTxnType.JOB_SUBMIT) {
				return;
			}

			CkOpmJournal opmJournal = new CkOpmJournal();

			opmJournal = this.init(opmJournal, journalTxnType, jobTruck, principal);

			opmJournal.setOpmjTxnRef(jobTruck.getJobId());
			opmJournal.setOpmjReserve(amount);

			opmJournal.setOpmjUtilized(BigDecimal.ZERO);

			List<ValidationError> validationErrors = opmValidator.validateOpmReserve(opmJournal);
			if (null != validationErrors && !validationErrors.isEmpty())
				throw new ValidationException(validationErrorMap(validationErrors));

			TCkOpmJournal opmJournalE = entityFromDto(opmJournal);
			opmJournalDao.add(opmJournalE);

		} catch (Exception ex) {
			LOG.error("reserveJobTruckCredit", ex);
			throw ex;
		}
	}

	/**
	 * When CO withdraw job.
	 */
	@Override
	public void reverseOpmJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws Exception {
		LOG.info("reverseJobTruckCredit");
		try {

			if (journalTxnType == null)
				new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				new ParameterException("param jobTruck null");

			CkOpmJournal opmJournal = new CkOpmJournal();
			opmJournal = this.init(opmJournal, journalTxnType, jobTruck, principal);

			double amount = 0.0;
			if (journalTxnType == JournalTxnType.JOB_SUBMIT_REIMBURSEMENT) {
				amount = jobTruck.getJobTotalReimbursements() == null ? 0.0
						: jobTruck.getJobTotalReimbursements().doubleValue();
			} else {
				amount = (jobTruck.getJobTotalCharge() == null ? 0.0 : jobTruck.getJobTotalCharge().doubleValue())
						+ (jobTruck.getJobTotalReimbursements() == null ? 0.0
								: jobTruck.getJobTotalReimbursements().doubleValue());
			}
			opmJournal.setOpmjReserve(new BigDecimal(amount).multiply(new BigDecimal(-1)));
			opmJournal.setOpmjUtilized(BigDecimal.ZERO);

			List<ValidationError> validationErrors = opmValidator.validateOpmReverse(opmJournal);
			if (null != validationErrors && !validationErrors.isEmpty())
				throw new ValidationException(validationErrorMap(validationErrors));

			TCkOpmJournal opmJournalE = entityFromDto(opmJournal);
			opmJournalDao.add(opmJournalE);

		} catch (Exception ex) {
			LOG.error("reverseJobTruckCredit", ex);
			throw ex;
		}

	}

	@Override
	public void utilizeOpmJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws Exception {
		LOG.info("utilizeJobTruckCredit");
		try {
			if (journalTxnType == null)
				new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				new ParameterException("param jobTruck null");

			BigDecimal totalAmount = jobTruck.computeTotalAmt();

			LOG.info("Total charge to utilize: " + totalAmount);

			CkOpmJournal opmJournal = new CkOpmJournal();
			opmJournal = this.init(opmJournal, journalTxnType, jobTruck, principal);

			opmJournal.setOpmjUtilized(totalAmount);
			opmJournal.setOpmjReserve(BigDecimal.ZERO);

			List<ValidationError> validationErrors = opmValidator.validateOpmUtilize(opmJournal);
			if (null != validationErrors && !validationErrors.isEmpty())
				throw new ValidationException(validationErrorMap(validationErrors));

			TCkOpmJournal opmJournalE = entityFromDto(opmJournal);
			opmJournalDao.add(opmJournalE);

		} catch (Exception ex) {
			LOG.error("utilizeJobTruckCredit", ex);
			throw ex;
		}
	}

	/**
	 * When GLI approve Job
	 */
	@Override
	public void convertResever2utilizeOpmJobTruckCredit(JournalTxnType journalTxnType, CkJobTruck jobTruck) throws Exception {
		LOG.info("utilizeJobTruckCredit");
		try {
			if (journalTxnType == null)
				new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				new ParameterException("param jobTruck null");

			CkOpmJournal omJournal = new CkOpmJournal();

			BigDecimal totalAmount = jobTruck.computeTotalAmt();

			LOG.info("Total convertResever2utilizeOpmJobTruckCredit: " + totalAmount);

			omJournal = this.init(omJournal, journalTxnType, jobTruck, null);

			omJournal.setOpmjReserve(totalAmount.multiply(new BigDecimal(-1)));
			omJournal.setOpmjUtilized(totalAmount);

			TCkOpmJournal opmJournalE = entityFromDto(omJournal);
			opmJournalDao.add(opmJournalE);

		} catch (Exception ex) {
			LOG.error("utilizeJobTruckCredit", ex);
			throw ex;
		}

	}

	/**
	 * When CO paid to GLI
	 */
	@Override
	public void reverseOpmUtilized(JournalTxnType journalTxnType, CkJobTruck jobTruck, Principal principal)
			throws Exception {
		LOG.info("utilizeJobTruckCredit");
		try {
			if (journalTxnType == null)
				new ParameterException("param journalTxnType null");
			if (jobTruck == null)
				new ParameterException("param jobTruck null");

			BigDecimal totalAmount = jobTruck.computeTotalAmt();

			LOG.info("Total charge to utilize: " + totalAmount);

			CkOpmJournal opmJournal = new CkOpmJournal();
			opmJournal = this.init(opmJournal, journalTxnType, jobTruck, principal);

			opmJournal.setOpmjId(CkUtil.generateId());
			opmJournal.setOpmjReserve(BigDecimal.ZERO);
			opmJournal.setOpmjUtilized(totalAmount.multiply(new BigDecimal(-1)));
			opmJournal.setOpmjStatus(Constant.ACTIVE_STATUS);

			TCkOpmJournal opmJournalE = entityFromDto(opmJournal);
			opmJournalDao.add(opmJournalE);

		} catch (Exception ex) {
			LOG.error("utilizeJobTruckCredit", ex);
			throw ex;
		}

	}

	//// Helper Methods
	private CkOpmJournal init(CkOpmJournal opmJournal, JournalTxnType journalTxnType, CkJobTruck jobTruck,
			Principal principal) {

		opmJournal.setOpmjId(CkUtil.generateId());
		opmJournal.setOpmjStatus(Constant.ACTIVE_STATUS);

		// This is important to distinct OC or OT
		if (FinancingTypes.OC.name().equalsIgnoreCase(jobTruck.getJobFinanceOpt())) {
			opmJournal.setTCoreAccn(jobTruck.getTCoreAccnByJobPartyCoFf());
		} else {
			// OT
			opmJournal.setTCoreAccn(jobTruck.getTCoreAccnByJobPartyTo());
		}
		opmJournal.setOpmjTxnRef(jobTruck.getJobId());
		opmJournal.setTCkMstServiceType(
				new CkMstServiceType(ServiceTypes.CLICTRUCK.name(), ServiceTypes.CLICTRUCK.name()));
		opmJournal.setTCkMstJournalTxnType(new CkMstJournalTxnType(journalTxnType.name(), journalTxnType.getDesc()));
		MstCurrency ccy = new MstCurrency();
		ccy.setCcyCode(Currencies.IDR.getCode());
		opmJournal.setTMstCurrency(ccy);

		Date now = Calendar.getInstance().getTime();
		opmJournal.setOpmjDtCreate(now);
		opmJournal.setOpmjUidCreate(principal == null ? "sys" : principal.getUserId());
		opmJournal.setOpmjUidLupd(principal == null ? "sys" : principal.getUserId());
		opmJournal.setOpmjDtLupd(now);

		return opmJournal;
	}

	private TCkOpmJournal entityFromDto(CkOpmJournal dto) throws Exception {
		if (dto == null)
			throw new ParameterException("param dto null");

		TCkOpmJournal entity = new TCkOpmJournal();
		dto.copyBeanProperties(entity);

		TCkMstJournalTxnType journalTxnTypeE = new TCkMstJournalTxnType();
		dto.getTCkMstJournalTxnType().copyBeanProperties(journalTxnTypeE);

		TCkMstServiceType serviceTypeE = new TCkMstServiceType();
		dto.getTCkMstServiceType().copyBeanProperties(serviceTypeE);

		TCoreAccn accnE = new TCoreAccn();
		dto.getTCoreAccn().copyBeanProperties(accnE);

		TMstCurrency ccyE = new TMstCurrency();
		dto.getTMstCurrency().copyBeanProperties(ccyE);

		entity.setTCkMstJournalTxnType(journalTxnTypeE);
		entity.setTCkMstServiceType(serviceTypeE);
		entity.setTCoreAccn(accnE);
		entity.setTMstCurrency(ccyE);
		return entity;

	}

	private String validationErrorMap(List<ValidationError> validationErrors)
			throws ParameterException, ProcessingException, Exception {
		LOG.debug("validationMap");

		if (null == validationErrors)
			throw new ParameterException("param errros null");

		if (validationErrors.isEmpty())
			throw new ProcessingException("param errros empty");

		String json;
		try {
			Map<String, String> validationMap = new HashMap<>();
			validationErrors.stream().forEach(ve -> {
				LOG.error(ve.getErrorType() + " " + ve.getErrorDescription() + "  " + ve);
				validationMap.put(ve.getErrorType().toString(), ve.getErrorDescription());
			});

			json = (new ObjectMapper()).writeValueAsString(validationMap);

		} catch (Exception e) {
			LOG.error("errorMap", e);
			throw e;
		}
		return json;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkOpmJournal> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		LOG.debug("filterBy");
		if (null == filterRequest)
			throw new ParameterException("param filterRequest null");

		CkOpmJournal dto = whereDto(filterRequest);
		if (null == dto)
			throw new ProcessingException("param dto null");

		filterRequest.setTotalRecords(countByAnd(dto));
		List<CkOpmJournal> dtos = new ArrayList<>();
		String orderClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
		List<TCkOpmJournal> query = findEntitiesByAnd(dto, "from TCkOpmJournal o ", orderClause,
				filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
		for (TCkOpmJournal entity : query) {
			CkOpmJournal newDto = dtoFromEntity(entity);
			if (null != newDto)
				dtos.add(newDto);
		}
		return dtos;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public int countByAnd(CkOpmJournal dto)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		LOG.debug("countByAnd");
		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			String whereClause = getWhereClause(dto, false);
			HashMap<String, Object> parameters = getParameters(dto);
			int count = 0;
			if (StringUtils.isNotEmpty(whereClause) && null != parameters && parameters.size() > 0) {
				count = opmJournalDao.count(
						"SELECT COUNT(o) FROM " + TCkOpmJournal.class.getName() + " o" + whereClause, parameters);
			} else {
				count = opmJournalDao.count("SELECT COUNT(o) FROM " + TCkOpmJournal.class.getName() + " o");
			}
			return count;
		} catch (ParameterException | EntityNotFoundException e) {
			LOG.error("countByAnd", (Throwable) e);
			throw e;
		} catch (Exception e) {
			LOG.error("countByAnd", e);
			throw e;
		}
	}

	/////////// Helper Methods
	///////////////////////////////

	private CkOpmJournal dtoFromEntity(TCkOpmJournal entity) throws ParameterException, ProcessingException, Exception {
		LOG.debug("dtoFromEntity");
		if (null == entity)
			throw new ParameterException("param entity null");

		CkOpmJournal dto = new CkOpmJournal(entity);
		Optional.ofNullable(entity.getTCkMstServiceType())
				.ifPresent(x -> dto.setTCkMstServiceType(new CkMstServiceType(x)));
		Optional.ofNullable(entity.getTCoreAccn()).ifPresent(x -> dto.setTCoreAccn(new CoreAccn(x)));
		Optional.ofNullable(entity.getTCkMstJournalTxnType())
				.ifPresent(x -> dto.setTCkMstJournalTxnType(new CkMstJournalTxnType(x)));
		Optional.ofNullable(entity.getTMstCurrency()).ifPresent(x -> dto.setTMstCurrency(new MstCurrency(x)));

		return dto;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	private List<TCkOpmJournal> findEntitiesByAnd(CkOpmJournal dto, String selectClause, String orderByClause,
			int limit, int offset) throws ParameterException, ProcessingException, Exception {
		LOG.debug("findEntitesByAnd");
		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			if (StringUtils.isEmpty(selectClause))
				throw new ParameterException("param selectClause null or empty");

			if (StringUtils.isEmpty(orderByClause))
				throw new ParameterException("param orderByClause null or empty");

			String whereClause = getWhereClause(dto, false);
			HashMap<String, Object> parameters = getParameters(dto);
			String hqlQuery = StringUtils.isEmpty(whereClause) ? (selectClause + orderByClause)
					: (selectClause + whereClause + orderByClause);
			List<TCkOpmJournal> entities = opmJournalDao.getByQuery(hqlQuery, parameters, limit, offset);
			for (TCkOpmJournal entity : entities)
				initEnity(entity);
			return entities;
		} catch (ParameterException | ProcessingException e) {
			LOG.error("findEntitiesByAnd", (Throwable) e);
			throw e;
		} catch (Exception e) {
			LOG.error("findEntitiesByAnd", e);
			throw e;
		}
	}

	private TCkOpmJournal initEnity(TCkOpmJournal entity) throws ParameterException, ProcessingException, Exception {
		LOG.debug("initEnity");
		if (null != entity) {
			Optional.ofNullable(entity.getTCkMstServiceType()).ifPresent(x -> Hibernate.initialize(x));
			Optional.ofNullable(entity.getTCoreAccn()).ifPresent(x -> Hibernate.initialize(x));
			Optional.ofNullable(entity.getTCkMstJournalTxnType()).ifPresent(x -> Hibernate.initialize(x));
			Optional.ofNullable(entity.getTMstCurrency()).ifPresent(x -> Hibernate.initialize(x));
		}
		return entity;
	}

	protected EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) throws Exception {
		if (orderBy == null) {
			return null;
		} else if (StringUtils.isEmpty(orderBy.getAttribute())) {
			return null;
		} else {
			String newAttr = formatOrderBy(orderBy.getAttribute());
			if (StringUtils.isEmpty(newAttr)) {
				return orderBy;
			} else {
				orderBy.setAttribute(newAttr);
				return orderBy;
			}
		}
	}

	private String formatOrderBy(String attributes) throws ParameterException, ProcessingException, Exception {
		LOG.debug("formatOrderBy");
		String attribute = attributes;

		if (StringUtils.contains(attribute, "tckMstServiceType"))
			attribute = attribute.replace("tckMstServiceType", "TCkMstServiceType");

		if (StringUtils.contains(attribute, "tcoreAccn"))
			attribute = attribute.replace("tcoreAccn", "TCoreAccn");

		if (StringUtils.contains(attribute, "tckMstJournalTxnType"))
			attribute = attribute.replace("tckMstJournalTxnType", "TCkMstJournalTxnType");

		if (StringUtils.contains(attribute, "tmstCurrency"))
			attribute = attribute.replace("tmstCurrency", "TMstCurrency");

		return attribute;
	}

	private String getWhereClause(CkOpmJournal dto, boolean wherePrinted)
			throws ParameterException, ProcessingException, Exception {
		LOG.debug("getWhereClause");
		if (null == dto)
			throw new ParameterException("param dto null");

		StringBuffer condition = new StringBuffer();

		Optional<String> opmjId = Optional.ofNullable(dto.getOpmjId());
		if (opmjId.isPresent()) {
			condition.append(getOperator(wherePrinted)).append("o.opmjId = :opmjId");
			wherePrinted = true;
		}

		Optional<String> opmjTxnRef = Optional.ofNullable(dto.getOpmjTxnRef());
		if (opmjTxnRef.isPresent()) {
			condition.append(getOperator(wherePrinted)).append("o.opmjTxnRef LIKE :opmjTxnRef");
			wherePrinted = true;
		}

		Optional<BigDecimal> opmjReserve = Optional.ofNullable(dto.getOpmjReserve());
		if (opmjReserve.isPresent()) {
			condition.append(getOperator(wherePrinted)).append("o.opmjReserve = :opmjReserve");
			wherePrinted = true;
		}

		Optional<BigDecimal> opmjUtilized = Optional.ofNullable(dto.getOpmjUtilized());
		if (opmjUtilized.isPresent()) {
			condition.append(getOperator(wherePrinted)).append("o.opmjUtilized = :opmjUtilized");
			wherePrinted = true;
		}

		Optional<CkMstServiceType> opCkMstServiceType = Optional.ofNullable(dto.getTCkMstServiceType());
		if (opCkMstServiceType.isPresent()) {
			Optional<String> opSvctId = Optional.ofNullable(dto.getTCkMstServiceType())
					.map(CkMstServiceType::getSvctId);
			if (opSvctId.isPresent()) {
				condition.append(getOperator(wherePrinted)).append("o.TCkMstServiceType.svctId = :svctId");
				wherePrinted = true;
			}
		}

		Optional<MstCurrency> opMstCurrency = Optional.ofNullable(dto.getTMstCurrency());
		if (opMstCurrency.isPresent()) {
			Optional<String> opCcyCode = Optional.ofNullable(dto.getTMstCurrency()).map(MstCurrency::getCcyCode);
			if (opCcyCode.isPresent()) {
				condition.append(getOperator(wherePrinted)).append("o.TMstCurrency.ccyCode = :ccyCode");
				wherePrinted = true;
			}
		}

		Optional<CkMstJournalTxnType> opCkMstJournalTxnType = Optional.ofNullable(dto.getTCkMstJournalTxnType());
		if (opCkMstJournalTxnType.isPresent()) {
			Optional<String> opJttId = Optional.ofNullable(dto.getTCkMstJournalTxnType())
					.map(CkMstJournalTxnType::getJttId);
			if (opJttId.isPresent()) {
				condition.append(getOperator(wherePrinted)).append("o.TCkMstJournalTxnType.jttId = :jttId");
				wherePrinted = true;
			}
		}

		condition.append(getOperator(wherePrinted)).append("o.TCoreAccn.accnId = :accnId");
		wherePrinted = true;
		return condition.toString();
	}

	protected HashMap<String, Object> getParameters(CkOpmJournal dto)
			throws ParameterException, ProcessingException, Exception {
		LOG.debug("getParameters");
		if (null == dto)
			throw new ParameterException("param dto null");

		Principal principal = principalUtilService.getPrincipal();
		if (null == principal)
			throw new ParameterException("param principal null");
		CoreAccn coreAccn = principal.getCoreAccn();

		HashMap<String, Object> parameters = new HashMap<>();

		Optional<String> opmjId = Optional.ofNullable(dto.getOpmjId());
		if (opmjId.isPresent()) {
			parameters.put("opmjId", opmjId.get());
		}

		Optional<CkMstServiceType> opCkMstServiceType = Optional.ofNullable(dto.getTCkMstServiceType());
		if (opCkMstServiceType.isPresent()) {
			Optional<String> opSvctId = Optional.ofNullable(dto.getTCkMstServiceType())
					.map(CkMstServiceType::getSvctId);
			if (opSvctId.isPresent()) {
				parameters.put("svctId", opSvctId.get());
			}
		}

		parameters.put("accnId", coreAccn.getAccnId());

		Optional<String> opmjTxnRef = Optional.ofNullable(dto.getOpmjTxnRef());
		if (opmjTxnRef.isPresent()) {
			parameters.put("opmjTxnRef", "%" + opmjTxnRef.get() + "%");
		}

		Optional<BigDecimal> opmjReserve = Optional.ofNullable(dto.getOpmjReserve());
		if (opmjReserve.isPresent()) {
			parameters.put("opmjReserve", opmjReserve.get());
		}

		Optional<BigDecimal> opmjUtilized = Optional.ofNullable(dto.getOpmjUtilized());
		if (opmjUtilized.isPresent()) {
			parameters.put("opmjUtilized", opmjUtilized.get());
		}

		Optional<CkMstJournalTxnType> opCkMstJournalTxnType = Optional.ofNullable(dto.getTCkMstJournalTxnType());
		if (opCkMstJournalTxnType.isPresent()) {
			Optional<String> opJttId = Optional.ofNullable(dto.getTCkMstJournalTxnType())
					.map(CkMstJournalTxnType::getJttId);
			if (opJttId.isPresent()) {
				parameters.put("jttId", opJttId.get());
			}
		}

		Optional<MstCurrency> opMstCurrency = Optional.ofNullable(dto.getTMstCurrency());
		if (opMstCurrency.isPresent()) {
			Optional<String> opCcyCode = Optional.ofNullable(dto.getTMstCurrency()).map(MstCurrency::getCcyCode);
			if (opCcyCode.isPresent()) {
				parameters.put("ccyCode", opCcyCode.get());
			}
		}
		return parameters;
	}

	private String getOperator(boolean whereprinted) {
		return whereprinted ? " AND " : " WHERE ";
	}

	private CkOpmJournal whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException, Exception {
		LOG.debug("whereDto");
		CkOpmJournal dto = new CkOpmJournal();
		CkMstServiceType ckMstServiceType = new CkMstServiceType();
		CoreAccn coreAccn = new CoreAccn();
		CkMstJournalTxnType ckMstJournalTxnType = new CkMstJournalTxnType();
		MstCurrency mstCurrency = new MstCurrency();

		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
			if (!opValue.isPresent())
				continue;

			if (entityWhere.getAttribute().equalsIgnoreCase("opmjId"))
				dto.setOpmjId(opValue.get());

			if (entityWhere.getAttribute().equalsIgnoreCase("opmjTxnRef"))
				dto.setOpmjTxnRef(opValue.get());

			if (entityWhere.getAttribute().equalsIgnoreCase("opmjReserve"))
				dto.setOpmjReserve(new BigDecimal(opValue.get()));

			if (entityWhere.getAttribute().equalsIgnoreCase("opmjUtilized"))
				dto.setOpmjUtilized(new BigDecimal(opValue.get()));

			if (entityWhere.getAttribute().equalsIgnoreCase("TCkMstServiceType.svctId"))
				ckMstServiceType.setSvctId(opValue.get());

			if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnId"))
				coreAccn.setAccnId(opValue.get());

			if (entityWhere.getAttribute().equalsIgnoreCase("TCkMstJournalTxnType.jttId"))
				ckMstJournalTxnType.setJttId(opValue.get());

			if (entityWhere.getAttribute().equalsIgnoreCase("TMstCurrency.ccyCode"))
				mstCurrency.setCcyCode(opValue.get());
		}
		dto.setTCkMstServiceType(ckMstServiceType);
		dto.setTCoreAccn(coreAccn);
		dto.setTCkMstJournalTxnType(ckMstJournalTxnType);
		dto.setTMstCurrency(mstCurrency);
		return dto;
	}

}
