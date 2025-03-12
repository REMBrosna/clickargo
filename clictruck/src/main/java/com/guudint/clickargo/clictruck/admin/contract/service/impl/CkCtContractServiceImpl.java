package com.guudint.clickargo.clictruck.admin.contract.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.contract.constant.CkCtContractChargeConstant;
import com.guudint.clickargo.clictruck.admin.contract.constant.CkCtContractConstant;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractChargeService;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractService;
import com.guudint.clickargo.clictruck.admin.contract.validator.ContractValidator;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.finacing.service.IPaymentService;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnOpmDao;
import com.guudint.clickargo.common.model.TCkAccnOpm;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkCsAccnService;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
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
import com.vcc.camelone.master.dto.MstBank;
import com.vcc.camelone.master.dto.MstCurrency;
import com.vcc.camelone.master.model.TMstBank;
import com.vcc.camelone.master.model.TMstCurrency;

public class CkCtContractServiceImpl extends AbstractClickCargoEntityService<TCkCtContract, String, CkCtContract>
		implements ICkConstant, CkCtContractService {

	private static Logger LOG = Logger.getLogger(CkCtContractServiceImpl.class);

	@Autowired
	private GenericDao<TCkCtContractCharge, String> ckCtContractChargeDao;

	@Autowired
	private CkCtContractChargeService ckCtContractChargeService;

	@Autowired
	protected ICkCsAccnService ckCsAccnService;
	@Autowired
	private ContractValidator contractValidator;

	@Autowired
	private CkAccnOpmDao accnOpmDao;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	public CkCtContractServiceImpl() {
		super(CkCtContractConstant.Table.NAME_DAO, CkCtContractConstant.Prefix.AUDIT_TAG,
				CkCtContractConstant.Table.NAME_ENTITY, CkCtContractConstant.Table.NAME);
	}

	@Override
	public CkCtContract deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("deleteByid");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		LOG.debug("deleteById -> id:" + id);
		try {
			TCkCtContract tCkCtContract = dao.find(id);
			if (tCkCtContract != null) {
				dao.remove(tCkCtContract);
				return dtoFromEntity(tCkCtContract);
			}
		} catch (Exception e) {
			LOG.error("deleteById", e);
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtContract> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("filterBy");

		List<CkCtContract> ckCtContracts = new ArrayList<>();
		try {

			if (filterRequest == null) {
				throw new ParameterException("param filterRequest null");
			}
			CkCtContract ckCtContract = whereDto(filterRequest);
			if (ckCtContract == null) {
				throw new ProcessingException("whereDto null");
			}

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal null");
			boolean isSpOpAdmin = principal.getRoleList().stream()
					.anyMatch(e -> Arrays.asList(Roles.SP_OP_ADMIN.name()).contains(e));

			/*-
			if (principal.getCoreAccn().getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_SP.name())) {

				if (isSpOpAdmin && ckCtContract.isForCsView()) {
					List<CoreAccn> accnsForCs = ckCsAccnService.getAssociatedAccounts(principal.getUserId(),
							ServiceTypes.CLICTRUCK);
					ckCtContract.setAccnForCsList(accnsForCs);
					// return if no accounts for cs
					if (accnsForCs != null && accnsForCs.isEmpty())
						return new ArrayList<CkCtContract>();
				}
			}
			*/

			filterRequest.setTotalRecords(countByAnd(ckCtContract));

			String orderByClause = formatOrderBy(filterRequest.getOrderBy().toString());
			List<TCkCtContract> tCkCtContracts = findEntitiesByAnd(ckCtContract,
					"from " + CkCtContractConstant.Table.NAME_ENTITY + " o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());

			for (TCkCtContract tCkCtContract : tCkCtContracts) {
				CkCtContract dto = dtoFromEntity(tCkCtContract);
				if (dto != null) {
					if (isSpOpAdmin && ckCtContract.isForCsView()) {
						// editable by the sp_op_admin
						dto.setEditable(true);
					}
					ckCtContracts.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtContracts;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CoreAccn> getTruckOperatorsByCoFf(Principal principal) throws ParameterException, Exception {
		LOG.debug("getTruckOperatorsByCoFf");
		try {

			if (principal == null)
				throw new ParameterException("param principal empty or null");
			List<CoreAccn> truckOpsList = new ArrayList<>();
			SimpleDateFormat tfDate = new SimpleDateFormat("yyyy-MM-dd");
			String hql = "from TCkCtContract o WHERE o.TCoreAccnByConCoFf.accnId = :accnId AND o.conStatus = :conStatus AND "
					+ ":now BETWEEN DATE_FORMAT(o.conDtStart, '%Y-%m-%d') AND DATE_FORMAT(o.conDtEnd, '%Y-%m-%d')";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accnId", principal.getCoreAccn().getAccnId());
			params.put("conStatus", RecordStatus.ACTIVE.getCode());
			params.put("now", tfDate.format(Calendar.getInstance().getTime()));
			List<TCkCtContract> listContract = dao.getByQuery(hql, params);

			if (listContract != null && listContract.size() > 0) {
				for (TCkCtContract c : listContract) {
					Hibernate.initialize(c.getTCoreAccnByConTo());
					TCoreAccn accn = c.getTCoreAccnByConTo();
					truckOpsList.add(new CoreAccn(accn));
				}
			}

			return truckOpsList.stream()
					.collect(Collectors.toConcurrentMap(CoreAccn::getAccnId, Function.identity(), (p, q) -> p)).values()
					.stream().sorted(Comparator.comparing(CoreAccn::getAccnName)).collect(Collectors.toList());

		} catch (Exception ex) {
			LOG.error("getTruckOperatorsByCoFf", ex);
			throw ex;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CoreAccn> getTruckOperatorsByCoFf(Principal principal, boolean isWithOpm)
			throws ParameterException, Exception {
		LOG.debug("getTruckOperatorsByCoFf");
		try {

			if (principal == null)
				throw new ParameterException("param principal empty or null");

			if (!isWithOpm)
				return getTruckOperatorsByCoFf(principal);

			List<CoreAccn> truckOpsList = new ArrayList<>();
			SimpleDateFormat tfDate = new SimpleDateFormat("yyyy-MM-dd");
			String hql = "from TCkCtContract o WHERE o.TCoreAccnByConCoFf.accnId = :accnId AND o.conStatus = :conStatus AND "
					+ ":now BETWEEN DATE_FORMAT(o.conDtStart, '%Y-%m-%d') AND DATE_FORMAT(o.conDtEnd, '%Y-%m-%d')";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("accnId", principal.getCoreAccn().getAccnId());
			params.put("conStatus", RecordStatus.ACTIVE.getCode());
			params.put("now", tfDate.format(Calendar.getInstance().getTime()));
			List<TCkCtContract> listContract = dao.getByQuery(hql, params);

			if (listContract != null && listContract.size() > 0) {
				for (TCkCtContract c : listContract) {
					Hibernate.initialize(c.getTCoreAccnByConTo());
					TCoreAccn accnTO = c.getTCoreAccnByConTo();
					// Check if CO is in opm financing
					boolean isCoOpm = accnOpmDao.findByAccnId(principal.getCoreAccn().getAccnId(),
							RecordStatus.ACTIVE.getCode()) == null ? false : true;

					if (isCoOpm) {
						// Check if TO is in opm, do not include in the list
						boolean isToOpm = accnOpmDao.findByAccnId(accnTO.getAccnId(),
								RecordStatus.ACTIVE.getCode()) == null ? false : true;
						if (!isToOpm)
							truckOpsList.add(new CoreAccn(accnTO));

					} else {
						// If CO is not in opm financing, check if the TO has active opm
						TCkAccnOpm accnOpm = accnOpmDao.findByAccnId(accnTO.getAccnId());
						if (accnOpm != null) {
							if (accnOpm.getCaoStatus() == RecordStatus.ACTIVE.getCode())
								truckOpsList.add(new CoreAccn(accnTO));
						} else {
							// if no opm account is found, add it still
							truckOpsList.add(new CoreAccn(accnTO));
						}

					}

				}
			}

			return truckOpsList.stream()
					.collect(Collectors.toConcurrentMap(CoreAccn::getAccnId, Function.identity(), (p, q) -> p)).values()
					.stream().sorted(Comparator.comparing(CoreAccn::getAccnName)).collect(Collectors.toList());

		} catch (Exception ex) {
			LOG.error("getTruckOperatorsByCoFf", ex);
			throw ex;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public CkCtContract findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtContract entity = dao.find(id);
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

	private String formatOrderBy(String attribute) throws Exception {
		return Optional.ofNullable(attribute).orElse("");
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected void initBusinessValidator() {

	}

	@Override
	protected CkCtContract dtoFromEntity(TCkCtContract tCkCtContract) throws ParameterException, ProcessingException {
		LOG.info("dtoFromEntity");
		if (tCkCtContract == null) {
			throw new ParameterException("param entity null");
		}

		BigDecimal vat = null;
		try {
			String vatSysParam = getSysParam(IPaymentService.KEY_CLICTRUCK_VAT_PERCENTAGE);

			if (StringUtils.isNotBlank(vatSysParam)) {
				vat = new BigDecimal(vatSysParam);
			}
		} catch (Exception ex) {
			LOG.error(ex);
		}

		CkCtContract ckCtContract = new CkCtContract(tCkCtContract);
		if (tCkCtContract.getTCkCtContractChargeByConChargeTo() != null) {
			ckCtContract.setTCkCtContractChargeByConChargeTo(
					new CkCtContractCharge(tCkCtContract.getTCkCtContractChargeByConChargeTo()));
			ckCtContract.getTCkCtContractChargeByConChargeTo().setConcAddtaxAmt(vat);
			ckCtContract.getTCkCtContractChargeByConChargeTo().setConcAddtaxType('P');
		}
		if (tCkCtContract.getTCkCtContractChargeByConChargeCoFf() != null) {
			ckCtContract.setTCkCtContractChargeByConChargeCoFf(
					new CkCtContractCharge(tCkCtContract.getTCkCtContractChargeByConChargeCoFf()));
			ckCtContract.getTCkCtContractChargeByConChargeCoFf().setConcAddtaxAmt(vat);
			ckCtContract.getTCkCtContractChargeByConChargeCoFf().setConcAddtaxType('P');
		}
		if (tCkCtContract.getTCoreAccnByConCoFf() != null) {
			ckCtContract.setTCoreAccnByConCoFf(new CoreAccn(tCkCtContract.getTCoreAccnByConCoFf()));
		}
		if (tCkCtContract.getTCoreAccnByConTo() != null) {
			ckCtContract.setTCoreAccnByConTo(new CoreAccn(tCkCtContract.getTCoreAccnByConTo()));
		}
		if (tCkCtContract.getTMstCurrency() != null) {
			ckCtContract.setTMstCurrency(new MstCurrency(tCkCtContract.getTMstCurrency()));
		}

		if (tCkCtContract.getTCkCtContractChargeByConOpm() != null) {
			ckCtContract.setTCkCtContractChargeByConOpm(
					new CkCtContractCharge(tCkCtContract.getTCkCtContractChargeByConOpm()));
		}

		if (tCkCtContract.getTMstBank() != null) {
			ckCtContract.setTMstBank(new MstBank(tCkCtContract.getTMstBank()));
		}
		return ckCtContract;
	}

	@Override
	protected TCkCtContract entityFromDTO(CkCtContract ckCtContract) throws ParameterException, ProcessingException {
		LOG.info("entityFromDTO");
		if (ckCtContract == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtContract tCkCtContract = new TCkCtContract(ckCtContract);
		if (ckCtContract.getTCkCtContractChargeByConChargeCoFf() != null) {
			tCkCtContract.setTCkCtContractChargeByConChargeCoFf(
					ckCtContract.getTCkCtContractChargeByConChargeCoFf().toEntity(new TCkCtContractCharge()));
		}
		if (ckCtContract.getTCkCtContractChargeByConChargeTo() != null) {
			tCkCtContract.setTCkCtContractChargeByConChargeTo(
					ckCtContract.getTCkCtContractChargeByConChargeTo().toEntity(new TCkCtContractCharge()));
		}
		if (ckCtContract.getTCoreAccnByConCoFf() != null) {
			tCkCtContract.setTCoreAccnByConCoFf(ckCtContract.getTCoreAccnByConCoFf().toEntity(new TCoreAccn()));
		}
		if (ckCtContract.getTCoreAccnByConTo() != null) {
			tCkCtContract.setTCoreAccnByConTo(ckCtContract.getTCoreAccnByConTo().toEntity(new TCoreAccn()));
		}
		if (ckCtContract.getTMstCurrency() != null) {
			tCkCtContract.setTMstCurrency(ckCtContract.getTMstCurrency().toEntity(new TMstCurrency()));
		}

		if (ckCtContract.getTCkCtContractChargeByConOpm() != null) {
			tCkCtContract.setTCkCtContractChargeByConOpm(
					ckCtContract.getTCkCtContractChargeByConOpm().toEntity(new TCkCtContractCharge()));
		}

		if (ckCtContract.getTMstBank() != null) {
			tCkCtContract.setTMstBank(ckCtContract.getTMstBank().toEntity(new TMstBank()));
		}
		return tCkCtContract;
	}

	@Override
	protected String entityKeyFromDTO(CkCtContract ckCtContract) throws ParameterException, ProcessingException {
		LOG.info("entityKeyFromDTO");
		if (ckCtContract == null) {
			throw new ParameterException("param dto null");
		}
		return ckCtContract.getConId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtContract ckCtContract)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckCtContract == null) {
			throw new ParameterException("dto param null");
		}
		if (ckCtContract.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return ckCtContract.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtContract ckCtContract)
			throws ParameterException, ProcessingException {
		LOG.info("getParameters");
		if (ckCtContract == null) {
			throw new ParameterException("param dto null");
		}

		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("principal is null");

		CoreAccn accn = principal.getCoreAccn();
		if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
				|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())) {
			parameters.put(CkCtContractConstant.ColumnParam.CON_CO_FF_ID, accn.getAccnId());
		} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {
			parameters.put(CkCtContractConstant.ColumnParam.CON_TO_ID, accn.getAccnId());
		} else {
			// for Service Provider
			/*-
			boolean isSpOpAdmin = principal.getRoleList().stream()
					.anyMatch(e -> Arrays.asList(Roles.SP_OP_ADMIN.name()).contains(e));
			if (isSpOpAdmin && ckCtContract.isForCsView()) {
				if (ckCtContract.getAccnForCsList() != null && ckCtContract.getAccnForCsList().size() > 0) {
					parameters.put("coFfToAccnIds", ckCtContract.getAccnForCsList().stream().map(e -> e.getAccnId())
							.collect(Collectors.toList()));
				}
			}
			*/
		}

		if (StringUtils.isNotBlank(ckCtContract.getConId())) {
			parameters.put(CkCtContractConstant.ColumnParam.CON_ID, ckCtContract.getConId());
		}
		Optional<CoreAccn> opCoreAccnByConTo = Optional.ofNullable(ckCtContract.getTCoreAccnByConTo());
		if (opCoreAccnByConTo.isPresent()) {
			if (StringUtils.isNotBlank(ckCtContract.getTCoreAccnByConTo().getAccnId())) {
				parameters.put(CkCtContractConstant.ColumnParam.CON_TO_ID,
						ckCtContract.getTCoreAccnByConTo().getAccnId());
			}
			if (StringUtils.isNotBlank(ckCtContract.getTCoreAccnByConTo().getAccnName())) {
				parameters.put(CkCtContractConstant.ColumnParam.CON_TO_NAME,
						"%" + ckCtContract.getTCoreAccnByConTo().getAccnName() + "%");
			}
		}

		Optional<CoreAccn> opCoreAccnByConCoFf = Optional.ofNullable(ckCtContract.getTCoreAccnByConCoFf());
		if (opCoreAccnByConCoFf.isPresent()) {
			if (StringUtils.isNotBlank(ckCtContract.getTCoreAccnByConCoFf().getAccnId())) {
				parameters.put(CkCtContractConstant.ColumnParam.CON_CO_FF_ID,
						ckCtContract.getTCoreAccnByConCoFf().getAccnId());
			}
			if (StringUtils.isNotBlank(ckCtContract.getTCoreAccnByConCoFf().getAccnName())) {
				parameters.put(CkCtContractConstant.ColumnParam.CON_CO_FF_NAME,
						"%" + ckCtContract.getTCoreAccnByConCoFf().getAccnName() + "%");
			}
		}
		if (ckCtContract.getConDtStart() != null) {
			parameters.put(CkCtContractConstant.ColumnParam.CON_DT_START, sdf.format(ckCtContract.getConDtStart()));
		}
		if (ckCtContract.getConDtEnd() != null) {
			parameters.put(CkCtContractConstant.ColumnParam.CON_DT_END, sdf.format(ckCtContract.getConDtEnd()));
		}
		if (ckCtContract.getConDtCreate() != null) {
			parameters.put(CkCtContractConstant.ColumnParam.CON_DT_CREATE, sdf.format(ckCtContract.getConDtCreate()));
		}
		if (ckCtContract.getConDtLupd() != null) {
			parameters.put(CkCtContractConstant.ColumnParam.CON_DT_LUPD, sdf.format(ckCtContract.getConDtLupd()));
		}

		if (ckCtContract.getConStatus() != null) {
			parameters.put(CkCtContractConstant.ColumnParam.CON_STATUS, ckCtContract.getConStatus());
		}

		parameters.put("validStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.DEACTIVATE.getCode()));

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtContract ckCtContract, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.info("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (null == ckCtContract) {
			throw new ParameterException("param dto null");
		}

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("principal is null");

		StringBuffer searchStatement = new StringBuffer();
		CoreAccn accn = principal.getCoreAccn();
		if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_FF.name())
				|| accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_CO.name())) {
			searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_CO_FF_ID + EQUAL
					+ CkCtContractConstant.ColumnParam.CON_CO_FF_ID);
			wherePrinted = true;
		} else if (accn.getTMstAccnType().getAtypId().equals(AccountTypes.ACC_TYPE_TO.name())) {
			searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_TO_ID + EQUAL
					+ CkCtContractConstant.ColumnParam.CON_TO_ID);
			wherePrinted = true;
		} else {
			// for Service Provider
			/*
			boolean isSpOpAdmin = principal.getRoleList().stream()
					.anyMatch(e -> Arrays.asList(Roles.SP_OP_ADMIN.name()).contains(e));
			if (isSpOpAdmin && ckCtContract.isForCsView()) {
				if (ckCtContract.getAccnForCsList() != null && ckCtContract.getAccnForCsList().size() > 0) {
					searchStatement.append(getOperator(wherePrinted)
							+ " (o.TCoreAccnByConCoFf.accnId in (:coFfToAccnIds) OR o.TCoreAccnByConTo.accnId in (:coFfToAccnIds)) ");
					wherePrinted = true;
				}
			}
			*/
		}

		if (StringUtils.isNotBlank(ckCtContract.getConId())) {
			searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_ID + EQUAL
					+ CkCtContractConstant.ColumnParam.CON_ID);
			wherePrinted = true;
		}
		Optional<CoreAccn> opCoreAccnByConTo = Optional.ofNullable(ckCtContract.getTCoreAccnByConTo());
		if (opCoreAccnByConTo.isPresent()) {
			if (StringUtils.isNotBlank(ckCtContract.getTCoreAccnByConTo().getAccnId())) {
				searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_TO_ID + EQUAL
						+ CkCtContractConstant.ColumnParam.CON_TO_ID);
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(ckCtContract.getTCoreAccnByConTo().getAccnName())) {
				searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_TO_NAME + CONTAIN
						+ CkCtContractConstant.ColumnParam.CON_TO_NAME);
				wherePrinted = true;
			}
		}
		Optional<CoreAccn> opCoreAccnByConCoFf = Optional.ofNullable(ckCtContract.getTCoreAccnByConCoFf());
		if (opCoreAccnByConCoFf.isPresent()) {
			if (StringUtils.isNotBlank(ckCtContract.getTCoreAccnByConCoFf().getAccnId())) {
				searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_CO_FF_ID + EQUAL
						+ CkCtContractConstant.ColumnParam.CON_CO_FF_ID);
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(ckCtContract.getTCoreAccnByConCoFf().getAccnName())) {
				searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_CO_FF_NAME + CONTAIN
						+ CkCtContractConstant.ColumnParam.CON_CO_FF_NAME);
				wherePrinted = true;
			}
		}
		if (ckCtContract.getConDtStart() != null) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtContractConstant.Column.CON_DT_START
					+ ",'" + DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtContractConstant.ColumnParam.CON_DT_START);
			wherePrinted = true;
		}
		if (ckCtContract.getConDtEnd() != null) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtContractConstant.Column.CON_DT_END
					+ ",'" + DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtContractConstant.ColumnParam.CON_DT_END);
			wherePrinted = true;
		}
		if (ckCtContract.getConDtCreate() != null) {
			searchStatement.append(
					getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtContractConstant.Column.CON_DT_CREATE + ",'"
							+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtContractConstant.ColumnParam.CON_DT_CREATE);
			wherePrinted = true;
		}
		if (ckCtContract.getConDtLupd() != null) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtContractConstant.Column.CON_DT_LUPD
					+ ",'" + DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtContractConstant.ColumnParam.CON_DT_LUPD);
			wherePrinted = true;
		}

		if (ckCtContract.getConStatus() != null) {
			searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_STATUS + EQUAL
					+ CkCtContractConstant.ColumnParam.CON_STATUS);
			wherePrinted = true;
		}

		searchStatement.append(getOperator(wherePrinted) + CkCtContractConstant.Column.CON_STATUS + " IN :validStatus");

		return searchStatement.toString();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	protected TCkCtContract initEnity(TCkCtContract tCkCtContract) throws ParameterException, ProcessingException {
		LOG.info("initEnity");
		if (tCkCtContract != null) {
			Hibernate.initialize(tCkCtContract.getTCkCtContractChargeByConChargeCoFf());
			Hibernate.initialize(tCkCtContract.getTCkCtContractChargeByConChargeTo());
			Hibernate.initialize(tCkCtContract.getTCoreAccnByConCoFf());
			Hibernate.initialize(tCkCtContract.getTCoreAccnByConTo());
			Hibernate.initialize(tCkCtContract.getTMstCurrency());
			if (tCkCtContract.getTMstBank() != null)
				Hibernate.initialize(tCkCtContract.getTMstBank());
			if (tCkCtContract.getTCkCtContractChargeByConOpm() != null)
				Hibernate.initialize(tCkCtContract.getTCkCtContractChargeByConOpm());
		}
		return tCkCtContract;
	}

	@Override
	protected CkCtContract preSaveUpdateDTO(TCkCtContract tCkCtContract, CkCtContract ckCtContract)
			throws ParameterException, ProcessingException {
		LOG.info("preSaveUpdateDTO");
		if (tCkCtContract == null) {
			throw new ParameterException("param entity null");
		}
		if (ckCtContract == null) {
			throw new ParameterException("param dto null");
		}
		ckCtContract.setConDtCreate(tCkCtContract.getConDtCreate());
		ckCtContract.setConUidCreate(tCkCtContract.getConUidCreate());
		ckCtContract.getTCkCtContractChargeByConChargeCoFf()
				.setConcDtCreate(tCkCtContract.getTCkCtContractChargeByConChargeCoFf().getConcDtCreate());
		ckCtContract.getTCkCtContractChargeByConChargeTo()
				.setConcUidCreate(tCkCtContract.getTCkCtContractChargeByConChargeTo().getConcUidCreate());
		try {
			ckCtContractChargeService.update(ckCtContract.getTCkCtContractChargeByConChargeCoFf());
			ckCtContractChargeService.update(ckCtContract.getTCkCtContractChargeByConChargeTo());
		} catch (Exception e) {
			LOG.error("Error update contract charge", e);
		}
		return ckCtContract;
	}

	@Override
	protected void preSaveValidation(CkCtContract arg0, Principal arg1) throws ParameterException, ProcessingException {

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtContract arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkCtContract setCoreMstLocale(CoreMstLocale arg0, CkCtContract arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	protected TCkCtContract updateEntity(ACTION action, TCkCtContract tCkCtContract, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.info("updateEntity");
		if (tCkCtContract == null) {
			throw new ParameterException("param entity null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (date == null) {
			throw new ParameterException("param date null");
		}
		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			tCkCtContract.setConUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			tCkCtContract.setConDtCreate(date);
			tCkCtContract.setConDtLupd(date);
			tCkCtContract.setConUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		case MODIFY:
			tCkCtContract.setConDtLupd(date);
			tCkCtContract.setConUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;
		default:
			break;
		}
		return tCkCtContract;
	}

	@Override
	protected TCkCtContract updateEntityStatus(TCkCtContract tCkCtContract, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtContract == null)
			throw new ParameterException("entity param null");

		tCkCtContract.setConStatus(status);
		return tCkCtContract;
	}

	@Override
	protected CkCtContract whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.info("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtContract ckCtContract = new CkCtContract();
		CoreAccn conTo = new CoreAccn();
		CoreAccn conCoFf = new CoreAccn();
		ckCtContract.setTCoreAccnByConTo(conTo);
		ckCtContract.setTCoreAccnByConCoFf(conCoFf);
		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere == null) {
				continue;
			}
			String attribute = "o." + entityWhere.getAttribute();
			if (CkCtContractConstant.Column.CON_ID.equalsIgnoreCase(attribute)) {
				ckCtContract.setConId(entityWhere.getValue());
			} else if (CkCtContractConstant.Column.CON_TO_ID.equalsIgnoreCase(attribute)) {
				conTo.setAccnId(entityWhere.getValue());
			} else if (CkCtContractConstant.Column.CON_TO_NAME.equalsIgnoreCase(attribute)) {
				conTo.setAccnName(entityWhere.getValue());
			} else if (CkCtContractConstant.Column.CON_CO_FF_ID.equalsIgnoreCase(attribute)) {
				conCoFf.setAccnId(entityWhere.getValue());
			} else if (CkCtContractConstant.Column.CON_CO_FF_NAME.equalsIgnoreCase(attribute)) {
				conCoFf.setAccnName(entityWhere.getValue());
			} else if (CkCtContractConstant.Column.CON_DT_START.equalsIgnoreCase(attribute)) {
				try {
					ckCtContract.setConDtStart(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtContractConstant.Column.CON_DT_END.equalsIgnoreCase(attribute)) {
				try {
					ckCtContract.setConDtEnd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtContractConstant.Column.CON_DT_LUPD.equalsIgnoreCase(attribute)) {
				try {
					ckCtContract.setConDtLupd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtContractConstant.Column.CON_DT_CREATE.equalsIgnoreCase(attribute)) {
				try {
					ckCtContract.setConDtCreate(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtContractConstant.Column.CON_STATUS.equalsIgnoreCase(attribute)) {
				ckCtContract.setConStatus((entityWhere.getValue() == null) ? null : entityWhere.getValue().charAt(0));
			}

			if (attribute.equalsIgnoreCase("o.forCsView")) {
				ckCtContract.setForCsView(Boolean.valueOf(entityWhere.getValue()));
			}

		}
		return ckCtContract;
	}

	@Override
	public CkCtContract updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtContract ckCtContract = findById(id);
		if (ckCtContract == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			ckCtContract.setConStatus(RecordStatus.ACTIVE.getCode());
		} else if ("deactive".equals(status)) {
			ckCtContract.setConStatus(RecordStatus.INACTIVE.getCode());
		}
		update(ckCtContract, principal);
		return ckCtContract;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtContract getContractByAccounts(String toAccn, String coffAccn)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		try {
			LOG.info("getByContractByAccounts");
			if (StringUtils.isBlank(toAccn))
				throw new ParameterException("param toAccn null or empty");
			if (StringUtils.isBlank(coffAccn))
				throw new ParameterException("param coffAccn null or empty");

			// start and end date should still be valid, otherwise don't return anything
			String hql = "from TCkCtContract o where o.TCoreAccnByConTo.accnId=:toAccn and o.TCoreAccnByConCoFf.accnId=:coffAccn "
					+ "and o.conStatus=:status and now() >= o.conDtStart and now() <= o.conDtEnd";
			Map<String, Object> params = new HashMap<>();
			params.put("toAccn", toAccn);
			params.put("coffAccn", coffAccn);
			params.put("status", RecordStatus.ACTIVE.getCode());

			List<TCkCtContract> listContract = dao.getByQuery(hql, params);
			if (listContract != null && listContract.size() > 0) {
				// expecting only one active contract
				TCkCtContract contract = listContract.get(0);
				initEnity(contract);
				CkCtContract dto = dtoFromEntity(contract);
				return dto;
			}

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

		return null;

	}

	@Override
	@Transactional
	public CkCtContract getContractByAccounts(String toAccn, String coffAccn, Date startDt, Date endDt,
			List<Character> listStatus)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			LOG.info("getByContractByAccounts");
			if (StringUtils.isBlank(toAccn))
				throw new ParameterException("param toAccn null or empty");
			if (StringUtils.isBlank(coffAccn))
				throw new ParameterException("param coffAccn null or empty");

			// start and end date should still be valid, otherwise don't return anything
			Map<String, Object> params = new HashMap<>();
			StringBuilder hql = new StringBuilder(
					"from TCkCtContract o where o.TCoreAccnByConTo.accnId=:toAccn and o.TCoreAccnByConCoFf.accnId=:coffAccn ");

			params.put("toAccn", toAccn);
			params.put("coffAccn", coffAccn);

			if (listStatus != null) {
				hql.append(" and o.conStatus in (:status) ");
				params.put("status", listStatus);
			}

			if (startDt != null) {
				hql.append("  and :startDt >= DATE_FORMAT(o.conDtStart, '%Y-%m-%d') ");
				params.put("startDt", sdf.format(startDt));
			}

			if (endDt != null) {
				hql.append(" and :endDt <= DATE_FORMAT(o.conDtEnd, '%Y-%m-%d') ");
				params.put("endDt", sdf.format(endDt));
			}

			List<TCkCtContract> listContract = dao.getByQuery(hql.toString(), params);
			if (listContract != null && listContract.size() > 0) {
				// expecting only one active contract
				TCkCtContract contract = listContract.get(0);
				initEnity(contract);
				CkCtContract dto = dtoFromEntity(contract);
				return dto;
			}

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtContract> getContracts(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {

		List<CkCtContract> contractList = new ArrayList<>();
		if (principal == null)
			throw new ParameterException("param principal null");

		Optional<MstAccnType> opAccnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType());
		if (!opAccnType.isPresent())
			throw new ProcessingException("account type null or invalid");

		StringBuilder hql = new StringBuilder("from TCkCtContract o where o.conStatus=:status ");
		if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
			hql.append(" and o.TCoreAccnByConTo.accnId = :accnId");
		} else if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())
				|| opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {
			hql.append(" and o.TCoreAccnByConCoFf.accnId = :accnId");
		}

		Map<String, Object> params = new HashMap<>();
		params.put("status", RecordStatus.ACTIVE.getCode());
		params.put("accnId", principal.getCoreAccn().getAccnId());
		List<TCkCtContract> list = dao.getByQuery(hql.toString(), params);
		if (list != null && list.size() > 0) {
			for (TCkCtContract c : list) {
				Hibernate.initialize(c.getTCoreAccnByConTo());
				Hibernate.initialize(c.getTCoreAccnByConCoFf());
				CkCtContract ctrct = new CkCtContract(c);
				ctrct.setTCoreAccnByConTo(new CoreAccn(c.getTCoreAccnByConTo()));
				ctrct.setTCoreAccnByConCoFf(new CoreAccn(c.getTCoreAccnByConCoFf()));
				contractList.add(ctrct);
			}
		}
		return contractList;
	}

	@Override
	public CkCtContract add(CkCtContract ckCtContract, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (ckCtContract == null) {
			throw new ParameterException("param dto null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		ckCtContract.setTCoreAccnByConCoFf(ckCtContract.getTCoreAccnByConCoFf());
		ckCtContract.setTCoreAccnByConTo(ckCtContract.getTCoreAccnByConTo());
		ckCtContract.setConId(CkUtil.generateId(CkCtContractConstant.Prefix.PREFIX_CK_CT_CONTRACT));
		ckCtContract.setConStatus(RecordStatus.ACTIVE.getCode());
		Optional<String> optUserId = Optional.ofNullable(principal.getUserId());
		CkCtContractCharge chargeTo = ckCtContract.getTCkCtContractChargeByConChargeTo();
		chargeTo.setConcId(CkUtil.generateId(CkCtContractChargeConstant.Prefix.PREFIX_CK_CT_CONTRACT_CHARGE));
		chargeTo.setConcUidCreate(optUserId.isPresent() ? optUserId.get() : Constant.DEFAULT_USR);
		chargeTo.setConcUidLupd(optUserId.isPresent() ? optUserId.get() : Constant.DEFAULT_USR);
		CkCtContractCharge chargeCoFf = ckCtContract.getTCkCtContractChargeByConChargeCoFf();
		chargeCoFf.setConcId(CkUtil.generateId(CkCtContractChargeConstant.Prefix.PREFIX_CK_CT_CONTRACT_CHARGE));
		chargeCoFf.setConcUidCreate(optUserId.isPresent() ? optUserId.get() : Constant.DEFAULT_USR);
		chargeCoFf.setConcUidLupd(optUserId.isPresent() ? optUserId.get() : Constant.DEFAULT_USR);
		ckCtContract.setTCkCtContractChargeByConChargeCoFf(chargeCoFf);
		ckCtContract.setTCkCtContractChargeByConChargeTo(chargeTo);
		try {
			TCkCtContractCharge tCkCtContractChargeTo = chargeTo.toEntity(new TCkCtContractCharge());
			TCkCtContractCharge tCkCtContractChargeCoFf = chargeCoFf.toEntity(new TCkCtContractCharge());
			ckCtContractChargeDao.add(tCkCtContractChargeTo);
			ckCtContractChargeDao.add(tCkCtContractChargeCoFf);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ProcessingException(e);
		}
		return super.add(ckCtContract, principal);
	}

	@Override
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtContract ckCtContract = (CkCtContract) object;
		List<ValidationError> validationErrors = contractValidator.validateCreate(ckCtContract, principal);
		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return add(ckCtContract, principal);
		}
	}

	@Override
	public Object updateObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtContract ckCtContract = (CkCtContract) object;
		List<ValidationError> validationErrors = contractValidator.validateUpdate(ckCtContract, principal);
		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return super.updateObj(ckCtContract, principal);
		}
	}

	@Override
	public CkCtContract newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtContract ckCtContract = new CkCtContract();
		DateUtil dateUtil = new DateUtil(new Date());
		ckCtContract.setTCkCtContractChargeByConChargeTo(new CkCtContractCharge());
		ckCtContract.setTCkCtContractChargeByConChargeCoFf(new CkCtContractCharge());
		ckCtContract.setTCoreAccnByConTo(new CoreAccn());
		ckCtContract.setTCoreAccnByConCoFf(new CoreAccn());
		ckCtContract.setConDtCreate(dateUtil.toDate(dateUtil.getDateOnly()));
		MstCurrency mstCurrency = new MstCurrency();
		mstCurrency.setCcyCode("IDR");
		ckCtContract.setTMstCurrency(mstCurrency);
		return ckCtContract;
	}

	public CkCtContract copyContractReqToContract(CkCtContractReq ctReq, CkCtContract contract) throws Exception {

		CkCtContract dto = new CkCtContract();
		dto.setTCkCtContractChargeByConChargeTo(new CkCtContractCharge());
		dto.setTCkCtContractChargeByConChargeCoFf(new CkCtContractCharge());
		dto.setTCoreAccnByConTo(new CoreAccn());
		dto.setTCoreAccnByConCoFf(new CoreAccn());
		dto.setTMstCurrency(new MstCurrency());

		if (contract != null) {
			// copy to new bean, we don't update the parameters as we will need the ids for
			// update
			contract.copyBeanProperties(dto);
		}

		dto.setConPaytermTo(ctReq.getCrPaytermTo());
		dto.setConPaytermCoFf(ctReq.getCrPaytermCoFf());
		dto.setConDescription(ctReq.getCrDescription());
		dto.setConDtStart(ctReq.getCrDtStart());
		dto.setConDtEnd(ctReq.getCrDtEnd());

		// get the details
		ctReq.getTCkCtContractChargeByCrChargeTo().copyBeanProperties(dto.getTCkCtContractChargeByConChargeTo());
		ctReq.getTCkCtContractChargeByCrChargeCoFf().copyBeanProperties(dto.getTCkCtContractChargeByConChargeCoFf());
		ctReq.getTCoreAccnByCrTo().copyBeanProperties(dto.getTCoreAccnByConTo());
		ctReq.getTCoreAccnByCrCoFf().copyBeanProperties(dto.getTCoreAccnByConCoFf());
		ctReq.getTMstCurrency().copyBeanProperties(dto.getTMstCurrency());

		// opm
		if (dto.getTCkCtContractChargeByConOpm() == null) {
			dto.setTCkCtContractChargeByConOpm(new CkCtContractCharge());
		}

		if (ctReq.getTCkCtContractChargeByCrChargeOpm() != null) {
			ctReq.getTCkCtContractChargeByCrChargeOpm().copyBeanProperties(dto.getTCkCtContractChargeByConOpm());
		}

		if (dto.getTMstBank() == null) {
			dto.setTMstBank(new MstBank());
		}
		
		if(ctReq.getTMstBank() != null) {
			ctReq.getTMstBank().copyBeanProperties(dto.getTMstBank());
		}
		
		dto.setConFinanceModel(ctReq.getCrFinanceModel());
		
		return dto;

	}

	/**
	 * This method retrieves all active contracts that will expire on a give date.
	 * 
	 * @param dueDate
	 * @return
	 */
	public List<CkCtContract> getAllContractsToExpire(Date dueDate) {

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

		String hql = "FROM TCkCtContract o WHERE DATE_FORMAT(o.conDtEnd, '%d/%m/%Y') = :conDtEnd AND o.conStatus = :conStatus";
		Map<String, Object> params = new HashMap<>();
		params.put("conDtEnd", sdfDate.format(dueDate));
		params.put("conStatus", RecordStatus.ACTIVE.getCode());

		try {
			List<CkCtContract> ckContractsToExpire = new ArrayList<CkCtContract>();
			List<TCkCtContract> tckContractsToExpire = super.getDao().getByQuery(hql, params);
			for (TCkCtContract contract : tckContractsToExpire) {
				CkCtContract ckCtContract = new CkCtContract(contract);
				ckCtContract.setTCoreAccnByConCoFf(new CoreAccn(contract.getTCoreAccnByConCoFf()));
				ckCtContract.setTCoreAccnByConTo(new CoreAccn(contract.getTCoreAccnByConTo()));
				ckContractsToExpire.add(ckCtContract);
			}
			return ckContractsToExpire;
		} catch (Exception ex) {
			LOG.error("getAllContractsToExpire ", ex);
		}

		return null;
	}

}
