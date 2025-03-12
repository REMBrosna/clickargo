package com.guudint.clickargo.clictruck.admin.ratetable.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractService;
import com.guudint.clickargo.clictruck.admin.ratetable.constant.CkCtRateConstant;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTableRemark;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.service.ICkCtRateTableService;
import com.guudint.clickargo.clictruck.admin.ratetable.service.impl.TripRateWorkflowServiceImpl.TripRateStatus;
import com.guudint.clickargo.clictruck.admin.ratetable.validator.RateTableValidator;
import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.RecordStatusNew;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.enums.WorkflowTypeEnum;
import com.guudint.clickargo.common.event.ApproveEvent;
import com.guudint.clickargo.common.event.RejectEvent;
import com.guudint.clickargo.common.event.SubmitEvent;
import com.guudint.clickargo.common.event.VerifyEvent;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.master.enums.Roles;
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
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.master.dto.MstCurrency;
import com.vcc.camelone.master.model.TMstCurrency;

public class CkCtRateTableServiceImpl extends AbstractClickCargoEntityService<TCkCtRateTable, String, CkCtRateTable>
		implements ICkCtRateTableService {

	private static Logger LOG = Logger.getLogger(CkCtRateTableServiceImpl.class);
	private static String AUDIT_TAG = "CKCT RATE TABLE";
	private static String TABLE_NAME = "T_CK_CT_RATE_TABLE";
	private static final String PREFIX_KEY = "CKRT";

	public CkCtRateTableServiceImpl() {
		super("ckCtRateTableDao", AUDIT_TAG, TCkCtRateTable.class.getName(), TABLE_NAME);
	}

	@Autowired
	private CkCtContractService contractService;

	@Autowired
	private CkCtTripRateServiceImpl tripRateServiceImpl;
	
	@Autowired
	private CkCtRateTableRemarkServiceImpl rateTableRemarkService;
	
	@Autowired
	private RateTableValidator rateTableValidator;

	@Override
	public CkCtRateTable newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtRateTable ckCtRateTable = new CkCtRateTable();
		CoreAccn tCoreAccnByRtCompany = principal.getCoreAccn();
		MstCurrency tMstCurrency = new MstCurrency();
		tMstCurrency.setCcyCode(Currencies.IDR.getCode());
		ckCtRateTable.setTCoreAccnByRtCompany(tCoreAccnByRtCompany);
		ckCtRateTable.setTMstCurrency(tMstCurrency);
		DateUtil dateUtil = new DateUtil(new Date());
		ckCtRateTable.setRtDtEnd(dateUtil.getDefaultEndDate());
		ckCtRateTable.setRtDtCreate(dateUtil.getDate());
		return ckCtRateTable;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtRateTable findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtRateTable tCkCtRateTable = dao.find(id);
			if (tCkCtRateTable == null) {
				throw new EntityNotFoundException("findById -> id:" + id);
			}
			initEnity(tCkCtRateTable);
			return dtoFromEntity(tCkCtRateTable);
		} catch (Exception e) {
			LOG.error("findById" + e);
		}
		return null;
	}

	@Override
	public CkCtRateTable add(CkCtRateTable dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (null == dto)
			throw new ParameterException("param dto null;");
		if (null == principal)
			throw new ParameterException("param principal null");

		dto.setTCoreAccnByRtCompany(principal.getCoreAccn());
		dto.setRtId(CkUtil.generateId(PREFIX_KEY));
		dto.setRtStatus(RecordStatus.ACTIVE.getCode());
		
		List<ValidationError> validationErrors = rateTableValidator.validateCreate(dto, principal);
		if (null != validationErrors && !validationErrors.isEmpty())
			throw new ValidationException(validationErrorMap(validationErrors));

		return super.add(dto, principal);
	}

	@Override
	public CkCtRateTable update(CkCtRateTable dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		try {
			Map<String, Object> params = new HashMap<>();
			
			// Retrieve all the trip rates associated to this rate table depending on the
			// action
			StringBuilder hql = new StringBuilder("from TCkCtTripRate o where o.TCkCtRateTable.rtId = :rateTableId ");

			params.put("rateTableId", dto.getRtId());

			char actionType = 'A';
			if (null != dto.getAction()) {

				hql.append(" and o.trStatus in :status");

				switch (dto.getAction()) {
				case SUBMIT:
					// if submit the status should be N
					params.put("status", Arrays.asList(TripRateStatus.NEW.getStatusCode()));
					break;
				case VERIFY:
					// if verify the status should be S
					actionType = 'V';
					params.put("status", Arrays.asList(TripRateStatus.SUB.getStatusCode()));
					break;
				case APPROVE:
					// if approve the status should be V
					params.put("status", Arrays.asList(TripRateStatus.VER.getStatusCode()));
					break;
				case ACTIVATE:
					// only activate those that are in I
					params.put("status", Arrays.asList(TripRateStatus.INACTIVE.getStatusCode()));
					break;
				case DEACTIVATE:
					// only deactivate those that are in A
					params.put("status", Arrays.asList(TripRateStatus.APP.getStatusCode()));
					break;
				case REJECT:
					// only deactivate those that are in A
					actionType = 'R';
					boolean isL1 = principal.getRoleList().stream()
							.anyMatch(el -> Arrays.asList(Roles.SP_L1.name()).contains(el));
					boolean isFnHd = principal.getRoleList().stream()
							.anyMatch(el -> Arrays.asList(Roles.SP_FIN_HD.name()).contains(el));

					if (isL1) {
						// reject only those that are in sub
						params.put("status", Arrays.asList(TripRateStatus.SUB.getStatusCode()));
					} else if (isFnHd) {
						// reject only those that are in ver
						params.put("status", Arrays.asList(TripRateStatus.VER.getStatusCode()));
					}

				default:
					break;
				}
				
				//If remarks not empty, save to t_ck_ct_rate_table_remark
				if(StringUtils.isNotBlank(dto.getActionRemarks())) {
					CkCtRateTableRemark remarkDto = new CkCtRateTableRemark();
					remarkDto.setTCkCtRateTable(dto);
					remarkDto.setRtrType(actionType);
					remarkDto.setRtrComment(dto.getActionRemarks());
					remarkDto.setRtrId(CkUtil.generateId("RTRMK"));
					rateTableRemarkService.add(remarkDto, principal);
				}

				List<CkCtTripRate> listTripRatesDto = new ArrayList<>();
				List<TCkCtTripRate> tripRates = tripRateServiceImpl.getDao().getByQuery(hql.toString(), params);
				if (tripRates != null && tripRates.size() > 0) {
					for (TCkCtTripRate tr : tripRates) {
						Hibernate.initialize(tr.getTCkCtLocationByTrLocTo());
						Hibernate.initialize(tr.getTCkCtLocationByTrLocFrom());
						Hibernate.initialize(tr.getTCkCtRateTable());
						Hibernate.initialize(tr.getTCkCtMstVehType());
						CkCtTripRate trDto = new CkCtTripRate(tr);
						// set the action from ratetable
						trDto.setAction(dto.getAction());
						trDto.setTCkCtLocationByTrLocFrom(new CkCtLocation(tr.getTCkCtLocationByTrLocFrom()));
						trDto.setTCkCtLocationByTrLocTo(new CkCtLocation(tr.getTCkCtLocationByTrLocTo()));
						trDto.setTCkCtRateTable(new CkCtRateTable(tr.getTCkCtRateTable()));
						trDto.setTCkCtMstVehType(new CkCtMstVehType(tr.getTCkCtMstVehType()));
						listTripRatesDto.add(trDto);
					}
				}

				// Pass to trip rate service to process the records
				tripRateServiceImpl.processTripRate(listTripRatesDto, dto.getAction(), principal);
				
				if (Stream.of(JobActions.DEACTIVATE, JobActions.ACTIVATE)
				        .noneMatch(action -> action.equals(dto.getAction()))) {
				    publishPostEvents(dto);
				}
				
			}

			// just to update the user and lupd
			return super.update(dto, principal);

		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
	}

	@Override
	public CkCtRateTable deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		LOG.debug("deleteById -> id:" + id);
		try {
			TCkCtRateTable tCkCtRateTable = dao.find(id);
			if (tCkCtRateTable != null) {
				dao.remove(tCkCtRateTable);
				return dtoFromEntity(tCkCtRateTable);
			}
		} catch (Exception e) {
			LOG.error("deleteById", e);
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtRateTable> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtRateTable ckCtRateTable = whereDto(filterRequest);
		if (ckCtRateTable == null) {
			throw new ProcessingException("whereDto null result");
		}
		filterRequest.setTotalRecords(countByAnd(ckCtRateTable));
		List<CkCtRateTable> ckCtRateTables = new ArrayList<>();
		try {
			String orderClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtRateTable> tCkCtRateTables = findEntitiesByAnd(ckCtRateTable, "from TCkCtRateTable o ",
					orderClause, filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtRateTable tCkCtRateTable : tCkCtRateTables) {
				CkCtRateTable dto = dtoFromEntity(tCkCtRateTable);
				if (dto != null) {
					ckCtRateTables.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtRateTables;
	}

	@Override
	public CkCtRateTable updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = principalUtilService.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtRateTable ckCtRateTable = findById(id);
		if (ckCtRateTable == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			ckCtRateTable.setAction(JobActions.ACTIVATE);
			ckCtRateTable.setRtStatus(RecordStatusNew.ACTIVE.getCode());
		} else if ("deactive".equals(status)) {
			ckCtRateTable.setAction(JobActions.DEACTIVATE);
			ckCtRateTable.setRtStatus(RecordStatusNew.DEACTIVE.getCode());
		}
		update(ckCtRateTable, principal);
		return ckCtRateTable;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<TruckOperatorOptions> loadOperators(Principal principal)
			throws ParameterException, EntityNotFoundException, Exception {

		try {

			if (principal == null)
				throw new ParameterException("param principal null");

			StringBuilder strHql = new StringBuilder("from TCkCtRateTable o where o.rtStatus=:rtStatus");

			Map<String, Object> params = new HashMap<>();
			params.put("rtStatus", RecordStatus.ACTIVE.getCode());

			Optional<CoreAccn> opAccn = Optional.ofNullable(principal.getCoreAccn());
			if (opAccn.isPresent()) {
				Optional<MstAccnType> opAccnType = Optional.ofNullable(opAccn.get().getTMstAccnType());
				if (opAccnType.isPresent()) {

					params.put("accnId", principal.getCoreAccn().getAccnId());
					if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
						strHql.append(" and o.TCoreAccnByRtCompany.accnId=:accnId");
					} else if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())
							|| opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
						strHql.append(" and o.TCoreAccnByRtCoFf.accnId=:accnId");
					}

				}
			}

			List<TCkCtRateTable> list = dao.getByQuery(strHql.toString(), params);
			List<TruckOperatorOptions> optionsList = new ArrayList<>();
			list.stream().forEach(e -> {
				Hibernate.initialize(e.getTCoreAccnByRtCoFf());
				Hibernate.initialize(e.getTCoreAccnByRtCompany());
				Hibernate.initialize(e.getTMstCurrency());

				if (e.getTCoreAccnByRtCompany() != null) {
					optionsList.add(new TruckOperatorOptions(e.getTCoreAccnByRtCompany().getAccnId(),
							e.getTCoreAccnByRtCompany().getAccnName()));
				}
			});

			// Filter unique
			List<TruckOperatorOptions> unique = optionsList.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(
									() -> new TreeSet<>(Comparator.comparing(TruckOperatorOptions::getValue))),
							ArrayList::new));

			return unique;

		} catch (Exception ex) {
			throw ex;
		}

	}

	@Override
	public CkCtRateTable getRateTableByAccounts(CoreAccn rtComAccn, CoreAccn coFfAccn, Currencies currency)
			throws ParameterException, EntityNotFoundException, Exception {
		try {

			if (rtComAccn == null)
				throw new ParameterException("param rtComAccn null");
			if (coFfAccn == null)
				throw new ParameterException("param coFfAccn null");
			if (currency == null)
				throw new ParameterException("param currency null");

			Map<String, Object> params = new HashMap<>();
			params.put("rtCompany", rtComAccn.getAccnId());
			params.put("rtCoFf", coFfAccn.getAccnId());
			params.put("rtStatus", RecordStatus.ACTIVE.getCode());
			params.put("currency", currency.getCode());
			String hql = "FROM TCkCtRateTable o WHERE o.TCoreAccnByRtCompany.accnId = :rtCompany "
					+ "AND o.TCoreAccnByRtCoFf.accnId = :rtCoFf AND o.rtStatus = :rtStatus"
					+ " and o.TMstCurrency.ccyCode=:currency";
			List<TCkCtRateTable> result = dao.getByQuery(hql, params);
			if (result != null) {
				// expecting only one
				return dtoFromEntity(result.get(0));
			}

		} catch (Exception ex) {
			throw ex;
		}

		return null;
	}

	@Override
	public List<CoreAccn> loadAccnsRateTableByContract(Principal principal, boolean isFilterByRateTableExistence)
			throws ParameterException, EntityNotFoundException, Exception {
		if (principal == null)
			throw new ParameterException("param principal null");

		Optional<MstAccnType> opAccnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType());
		if (!opAccnType.isPresent())
			throw new ProcessingException("account type null or invalid");

		boolean isTo = opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name());
		List<CoreAccn> listAccn = new ArrayList<>();
		List<CkCtContract> listContracts = contractService.getContracts(principal);
		if (listContracts != null && listContracts.size() > 0) {
			for (CkCtContract c : listContracts) {
				if (isTo) {
					listAccn.add(c.getTCoreAccnByConCoFf());
				} else {
					listAccn.add(c.getTCoreAccnByConTo());
				}
			}
		}

		if (!listAccn.isEmpty() && isFilterByRateTableExistence) {
			List<TruckOperatorOptions> rateTableList = loadActiveRateTableByAccount(principal);
			return listAccn.stream().filter(e -> {
				TruckOperatorOptions found = rateTableList.stream()
						.filter(rt -> rt.getValue().equalsIgnoreCase(e.getAccnId())).findAny().orElse(null);
				return (found == null);
			}).collect(Collectors.toList());

		}
		return listAccn;
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return LOG;
	}

	@Override
	protected TCkCtRateTable initEnity(TCkCtRateTable entity) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (null != entity) {
			Hibernate.initialize(entity.getTCoreAccnByRtCompany());
			Hibernate.initialize(entity.getTCoreAccnByRtCoFf());
			Hibernate.initialize(entity.getTMstCurrency());
		}
		return entity;
	}

	@Override
	protected TCkCtRateTable entityFromDTO(CkCtRateTable dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtRateTable entity = new TCkCtRateTable();
			entity = dto.toEntity(entity);

			Optional<CoreAccn> opCoreAccnByRtCompany = Optional.ofNullable(dto.getTCoreAccnByRtCompany());
			entity.setTCoreAccnByRtCompany(
					opCoreAccnByRtCompany.isPresent() ? opCoreAccnByRtCompany.get().toEntity(new TCoreAccn()) : null);

			Optional<CoreAccn> opCoreAccnByRtCoFf = Optional.ofNullable(dto.getTCoreAccnByRtCoFf());
			entity.setTCoreAccnByRtCoFf(
					opCoreAccnByRtCoFf.isPresent() ? opCoreAccnByRtCoFf.get().toEntity(new TCoreAccn()) : null);

			Optional<MstCurrency> opMstCurrency = Optional.ofNullable(dto.getTMstCurrency());
			entity.setTMstCurrency(opMstCurrency.isPresent() ? opMstCurrency.get().toEntity(new TMstCurrency()) : null);

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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtRateTable dtoFromEntity(TCkCtRateTable entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (null == entity)
			throw new ParameterException("param entity null");

		CkCtRateTable dto = new CkCtRateTable(entity);

		Optional<TCoreAccn> opCoreAccnByRtCompany = Optional.ofNullable(entity.getTCoreAccnByRtCompany());
		dto.setTCoreAccnByRtCompany(
				opCoreAccnByRtCompany.isPresent() ? new CoreAccn(opCoreAccnByRtCompany.get()) : null);

		Optional<TCoreAccn> opCoreAccnByRtCoFf = Optional.ofNullable(entity.getTCoreAccnByRtCoFf());
		dto.setTCoreAccnByRtCoFf(opCoreAccnByRtCoFf.isPresent() ? new CoreAccn(opCoreAccnByRtCoFf.get()) : null);

		Optional<TMstCurrency> opMstCurrency = Optional.ofNullable(entity.getTMstCurrency());
		dto.setTMstCurrency(opMstCurrency.isPresent() ? new MstCurrency(opMstCurrency.get()) : null);

		// Check if at least one has N (for submission), S (for verification), V (for
		// approval)
		try {
			List<CkCtTripRate> tripRateList = tripRateServiceImpl.getByRateTable(dto);
			dto.setHasNewTripRate(
					tripRateList.stream().anyMatch(e -> e.getTrStatus() == TripRateStatus.NEW.getStatusCode()));

		} catch (Exception e) {
			throw new ProcessingException(e);
		}
		return dto;
	}

	@Override
	protected String entityKeyFromDTO(CkCtRateTable dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (null == dto)
			throw new ParameterException("dto param null");

		return dto.getRtId();
	}

	@Override
	protected TCkCtRateTable updateEntity(ACTION attribute, TCkCtRateTable entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (null == entity)
			throw new ParameterException("param entity null");
		if (null == principal)
			throw new ParameterException("param principal null");
		if (null == date)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (attribute) {
		case CREATE:
			entity.setRtUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			entity.setRtDtCreate(date);
			entity.setRtDtLupd(date);
			entity.setRtUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);	
			break;

		case MODIFY:
			entity.setRtDtLupd(date);
			entity.setRtUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		default:
			break;
		}

		return entity;
	}

	@Override
	protected TCkCtRateTable updateEntityStatus(TCkCtRateTable entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (null == entity)
			throw new ParameterException("entity param null");

		entity.setRtStatus(status);
		return entity;
	}

	@Override
	protected CkCtRateTable preSaveUpdateDTO(TCkCtRateTable storedEntity, CkCtRateTable dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");
		if (null == storedEntity)
			throw new ParameterException("param storedEntity null");
		if (null == dto)
			throw new ParameterException("param dto null");

		dto.setRtUidCreate(storedEntity.getRtUidCreate());
		dto.setRtDtCreate(storedEntity.getRtDtCreate());
		return dto;
	}

	@Override
	protected void preSaveValidation(CkCtRateTable dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtRateTable dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtRateTable dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :", NOT_EQUAL = " != :";
		if (null == dto)
			throw new ParameterException("param dto null");

		StringBuffer searchStatement = new StringBuffer();

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("param principal null");

		Optional<MstAccnType> opAccnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType());
		if (opAccnType.isPresent() && StringUtils.isNotBlank(opAccnType.get().getAtypId())) {
			if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccnByRtCompany.accnId = :accnId");
				wherePrinted = true;
			} else if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())
					|| opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccnByRtCoFf.accnId = :accnId");
				wherePrinted = true;
			}
		}

		if (StringUtils.isNotBlank(dto.getRtName())) {
			searchStatement.append(getOperator(wherePrinted) + CkCtRateConstant.Column.RT_NAME + CONTAIN
					+ CkCtRateConstant.ColumnParam.RT_NAME);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(dto.getRtId())) {
			searchStatement.append(getOperator(wherePrinted) + CkCtRateConstant.Column.RT_ID + EQUAL
					+ CkCtRateConstant.ColumnParam.RT_ID);
			wherePrinted = true;
		}

		Optional<CoreAccn> opCoreAccnByRtCompany = Optional.ofNullable(dto.getTCoreAccnByRtCompany());
		if (opCoreAccnByRtCompany.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccnByRtCompany.get().getAccnId())) {
				searchStatement.append(getOperator(wherePrinted) + CkCtRateConstant.Column.RT_COMPANY_ID + EQUAL
						+ CkCtRateConstant.ColumnParam.RT_COMPANY_ID);
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(opCoreAccnByRtCompany.get().getAccnName())) {
				searchStatement.append(getOperator(wherePrinted) + CkCtRateConstant.Column.RT_COMPANY_NAME + CONTAIN
						+ CkCtRateConstant.ColumnParam.RT_COMPANY_NAME);
				wherePrinted = true;
			}
		}
		Optional<CoreAccn> opCoreAccnByRtCoFf = Optional.ofNullable(dto.getTCoreAccnByRtCoFf());
		if (opCoreAccnByRtCoFf.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccnByRtCoFf.get().getAccnId())) {
				searchStatement.append(getOperator(wherePrinted) + CkCtRateConstant.Column.RT_CO_FF_ID + EQUAL
						+ CkCtRateConstant.ColumnParam.RT_CO_FF_ID);
				wherePrinted = true;
			}
			if (StringUtils.isNotBlank(opCoreAccnByRtCoFf.get().getAccnName())) {
				searchStatement.append(getOperator(wherePrinted) + CkCtRateConstant.Column.RT_CO_FF_NAME + CONTAIN
						+ CkCtRateConstant.ColumnParam.RT_CO_FF_NAME);
				wherePrinted = true;
			}
		}
		Optional<Date> opRtDtStart = Optional.ofNullable(dto.getRtDtStart());
		if (opRtDtStart.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtRateConstant.Column.RT_DT_START
					+ ",'" + DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtRateConstant.ColumnParam.RT_DT_START);
			wherePrinted = true;
		}
		Optional<Date> opRtDtEnd = Optional.ofNullable(dto.getRtDtEnd());
		if (opRtDtEnd.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtRateConstant.Column.RT_DT_END + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtRateConstant.ColumnParam.RT_DT_END);
			wherePrinted = true;
		}
		Optional<Date> opRtDtCreate = Optional.ofNullable(dto.getRtDtCreate());
		if (opRtDtCreate.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtRateConstant.Column.RT_DT_CREATE
					+ ",'" + DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtRateConstant.ColumnParam.RT_DT_CREATE);
			wherePrinted = true;
		}
		Optional<Date> opRtDtLupd = Optional.ofNullable(dto.getRtDtLupd());
		if (opRtDtLupd.isPresent()) {
			searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtRateConstant.Column.RT_DT_LUPD
					+ ",'" + DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtRateConstant.ColumnParam.RT_DT_LUPD);
			wherePrinted = true;
		}

		if (dto.getRtStatus() == null) {
			searchStatement.append(getOperator(wherePrinted) + CkCtRateConstant.Column.RT_STATUS + NOT_EQUAL
					+ CkCtRateConstant.ColumnParam.RT_STATUS);
		} else {
			searchStatement.append(getOperator(wherePrinted) + CkCtRateConstant.Column.RT_STATUS + EQUAL
					+ CkCtRateConstant.ColumnParam.RT_STATUS);
		}

		return searchStatement.toString();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtRateTable dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		if (null == dto)
			throw new ParameterException("param dto null");

		SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<String, Object>();

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("param principal null");
		
		parameters.put("accnId", principal.getCoreAccn().getAccnId());
		
		if (StringUtils.isNotBlank(dto.getRtName()))
			parameters.put(CkCtRateConstant.ColumnParam.RT_NAME, "%" + dto.getRtName() + "%");

		if (dto.getRtStatus() == null) {
			parameters.put(CkCtRateConstant.ColumnParam.RT_STATUS, RecordStatusNew.DELETE.getCode());
		} else {
			parameters.put(CkCtRateConstant.ColumnParam.RT_STATUS, dto.getRtStatus());
		}

		Optional<CoreAccn> opCoreAccnByRtCompany = Optional.ofNullable(dto.getTCoreAccnByRtCompany());
		if (opCoreAccnByRtCompany.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccnByRtCompany.get().getAccnId()))
				parameters.put(CkCtRateConstant.ColumnParam.RT_COMPANY_ID, opCoreAccnByRtCompany.get().getAccnId());
			if (StringUtils.isNotBlank(opCoreAccnByRtCompany.get().getAccnName()))
				parameters.put(CkCtRateConstant.ColumnParam.RT_COMPANY_NAME,
						"%" + opCoreAccnByRtCompany.get().getAccnName() + "%");
		}
		Optional<CoreAccn> opCoreAccnByRtCoFf = Optional.ofNullable(dto.getTCoreAccnByRtCoFf());
		if (opCoreAccnByRtCoFf.isPresent()) {
			if (StringUtils.isNotBlank(opCoreAccnByRtCoFf.get().getAccnId()))
				parameters.put(CkCtRateConstant.ColumnParam.RT_CO_FF_ID, opCoreAccnByRtCoFf.get().getAccnId());
			if (StringUtils.isNotBlank(opCoreAccnByRtCoFf.get().getAccnName()))
				parameters.put(CkCtRateConstant.ColumnParam.RT_CO_FF_NAME,
						"%" + opCoreAccnByRtCoFf.get().getAccnName() + "%");
		}
		if (StringUtils.isNotBlank(dto.getRtId())) {
			parameters.put(CkCtRateConstant.ColumnParam.RT_ID, dto.getRtId());
		}
		Optional<Date> opRtDtStart = Optional.ofNullable(dto.getRtDtStart());
		if (opRtDtStart.isPresent() && null != opRtDtStart.get())
			parameters.put(CkCtRateConstant.ColumnParam.RT_DT_START, sdfDate.format(opRtDtStart.get()));
		Optional<Date> opRtDtEnd = Optional.ofNullable(dto.getRtDtEnd());
		if (opRtDtEnd.isPresent() && null != opRtDtEnd.get())
			parameters.put(CkCtRateConstant.ColumnParam.RT_DT_END, sdfDate.format(opRtDtEnd.get()));
		Optional<Date> opRtDtCreate = Optional.ofNullable(dto.getRtDtCreate());
		if (opRtDtCreate.isPresent() && null != opRtDtCreate.get())
			parameters.put(CkCtRateConstant.ColumnParam.RT_DT_CREATE, sdfDate.format(opRtDtCreate.get()));
		Optional<Date> opRtDtLupd = Optional.ofNullable(dto.getRtDtLupd());
		if (opRtDtLupd.isPresent() && null != opRtDtLupd.get())
			parameters.put(CkCtRateConstant.ColumnParam.RT_DT_LUPD, sdfDate.format(opRtDtLupd.get()));

		return parameters;
	}

	@Override
	protected CkCtRateTable whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			SimpleDateFormat sdfDate = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);

			CkCtRateTable dto = new CkCtRateTable();
			CoreAccn coreAccnByRtCompany = new CoreAccn();
			CoreAccn coreAccnByRtCoFf = new CoreAccn();
			MstCurrency mstCurrency = new MstCurrency();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				String attribute = "o." + entityWhere.getAttribute();
				if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_NAME))
					dto.setRtName(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_STATUS))
					dto.setRtStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_ID))
					dto.setRtId(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_COMPANY_ID))
					coreAccnByRtCompany.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_COMPANY_NAME))
					coreAccnByRtCompany.setAccnName(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_CO_FF_ID))
					coreAccnByRtCoFf.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_CO_FF_NAME))
					coreAccnByRtCoFf.setAccnName(opValue.get());

				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_DT_START))
					dto.setRtDtStart(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_DT_END))
					dto.setRtDtEnd(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_DT_CREATE))
					dto.setRtDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase(CkCtRateConstant.Column.RT_DT_LUPD))
					dto.setRtDtLupd(sdfDate.parse(opValue.get()));
			}

			dto.setTCoreAccnByRtCompany(coreAccnByRtCompany);
			dto.setTCoreAccnByRtCoFf(coreAccnByRtCoFf);
			dto.setTMstCurrency(mstCurrency);

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
	protected CoreMstLocale getCoreMstLocale(CkCtRateTable dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("getCoreMstLocale");

		if (null == dto) {
			throw new ParameterException("param dto null");
		}
		CoreMstLocale coreMstLocale = dto.getCoreMstLocale();
		if (null == coreMstLocale) {
			throw new ProcessingException("coreMstLocale null");
		}
		return coreMstLocale;
	}

	@Override
	protected CkCtRateTable setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtRateTable dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	public class TruckOperatorOptions {
		private String value;
		private String desc;

		public TruckOperatorOptions(String value, String desc) {
			super();
			this.value = value;
			this.desc = desc;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final TruckOperatorOptions other = (TruckOperatorOptions) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}

	// Helper Methods
	/////////////////////
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	private List<TruckOperatorOptions> loadActiveRateTableByAccount(Principal principal)
			throws ParameterException, EntityNotFoundException, Exception {

		try {

			if (principal == null)
				throw new ParameterException("param principal null");

			StringBuilder strHql = new StringBuilder("from TCkCtRateTable o where o.rtStatus=:rtStatus");

			Map<String, Object> params = new HashMap<>();
			params.put("rtStatus", RecordStatus.ACTIVE.getCode());

			boolean isTo = false;
			Optional<CoreAccn> opAccn = Optional.ofNullable(principal.getCoreAccn());
			if (opAccn.isPresent()) {
				Optional<MstAccnType> opAccnType = Optional.ofNullable(opAccn.get().getTMstAccnType());
				if (opAccnType.isPresent()) {

					params.put("accnId", principal.getCoreAccn().getAccnId());
					if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
						isTo = true;
						strHql.append(" and o.TCoreAccnByRtCompany.accnId=:accnId");
					} else if (opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())
							|| opAccnType.get().getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
						strHql.append(" and o.TCoreAccnByRtCoFf.accnId=:accnId");
					}

				}
			}

			List<TCkCtRateTable> list = dao.getByQuery(strHql.toString(), params);
			List<TruckOperatorOptions> accnList = new ArrayList<>();
			for (TCkCtRateTable e : list) {
				Hibernate.initialize(e.getTCoreAccnByRtCoFf());
				Hibernate.initialize(e.getTCoreAccnByRtCompany());
				Hibernate.initialize(e.getTMstCurrency());

				if (isTo) {
					// load the co/ff
					if (e.getTCoreAccnByRtCoFf() != null)
						accnList.add(new TruckOperatorOptions(e.getTCoreAccnByRtCoFf().getAccnId(),
								e.getTCoreAccnByRtCoFf().getAccnName()));

				} else {
					// load trucking operator
					if (e.getTCoreAccnByRtCompany() != null)
						accnList.add(new TruckOperatorOptions(e.getTCoreAccnByRtCompany().getAccnId(),
								e.getTCoreAccnByRtCompany().getAccnName()));
				}
			}

			// Filter unique
			List<TruckOperatorOptions> unique = accnList.stream()
					.collect(Collectors.collectingAndThen(
							Collectors.toCollection(
									() -> new TreeSet<>(Comparator.comparing(TruckOperatorOptions::getValue))),
							ArrayList::new));

			return unique;

		} catch (Exception ex) {
			throw ex;
		}

	}
	
	private void publishPostEvents(CkCtRateTable dto) throws Exception {

		ApplicationEvent appEvent = null;
		
		if (dto.getAction() == JobActions.SUBMIT) {
			appEvent = new SubmitEvent<TCkCtRateTable, CkCtRateTable>(this, WorkflowTypeEnum.RATE_TABLE, dto);

		} else if (dto.getAction() == JobActions.VERIFY) {
			appEvent = new VerifyEvent<TCkCtRateTable, CkCtRateTable>(this, WorkflowTypeEnum.RATE_TABLE, dto);

		} else if (dto.getAction() == JobActions.APPROVE) {
			appEvent = new ApproveEvent<TCkCtRateTable, CkCtRateTable>(this, WorkflowTypeEnum.RATE_TABLE, dto);

		} else if (dto.getAction() == JobActions.REJECT) {
			appEvent = new RejectEvent<TCkCtRateTable, CkCtRateTable>(this, WorkflowTypeEnum.RATE_TABLE, dto);

		} 
		
		eventPublisher.publishEvent(appEvent);
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
		newAttr = newAttr.replaceAll("tcoreAccnByRtCompany", "TCoreAccnByRtCompany")
				.replaceAll("tcoreAccnByRtCoFf", "TCoreAccnByRtCoFf")
				.replaceAll("tmstCurrency", "TMstCurrency");
		orderBy.setAttribute(newAttr);
		return orderBy;
	}
	
}
