package com.guudint.clickargo.clictruck.admin.ratetable.service.impl;

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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clicservice.service.ICkWorkflowService;
import com.guudint.clickargo.clictruck.admin.ratetable.constant.CkCtRateConstant.TripType;
import com.guudint.clickargo.clictruck.admin.ratetable.dao.CkCtTripRateDao;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.service.ICkCtTripRateService;
import com.guudint.clickargo.clictruck.admin.ratetable.service.impl.TripRateWorkflowServiceImpl.TripRateStatus;
import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtLocationServiceImpl;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstLocationType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.planexec.job.dto.TripChargeReq;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.master.enums.FormActions;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.COException;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ErrorCodes;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;

public class CkCtTripRateServiceImpl extends AbstractClickCargoEntityService<TCkCtTripRate, String, CkCtTripRate>
		implements ICkCtTripRateService {

	private static Logger LOG = Logger.getLogger(CkCtTripRateServiceImpl.class);
	private static String AUDIT_TAG = "CKCT TRIP RATE";
	private static String TABLE_NAME = "T_CK_CT_TRIP_RATE";
	private static final String PREFIX_KEY = "CKCTRT";

	// Attributes
	/////////////

	@Autowired
	private CkCtTripRateDao ckCtTripRateDao;

	@Autowired
	private CkCtRateTableServiceImpl rateTableService;

	@Autowired
	private CkCtLocationServiceImpl locationService;

	@Autowired
	private GenericDao<TCkCtMstVehType, String> vehTypeDao;

	@Autowired
	@Qualifier("tripRateWorkflowService")
	private ICkWorkflowService<TCkCtTripRate, CkCtTripRate> tripRateWorkflowService;

	public CkCtTripRateServiceImpl() {
		super("ckCtTripRateDao", AUDIT_TAG, TCkCtTripRate.class.getName(), TABLE_NAME);
	}

	@Override
	public CkCtTripRate newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return new CkCtTripRate();
	}

	@Override
	public CkCtTripRate add(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		checkForDuplicates(dto, principal);

		// check if it's single trip
		if (StringUtils.isNotBlank(dto.getTrType())
				&& StringUtils.equalsIgnoreCase(dto.getTrType(), TripType.S.name())) {
			dto.setTrId(CkUtil.generateId(PREFIX_KEY));
			dto.setTrStatus(TripRateStatus.NEW.getStatusCode());
			dto.setTrType(TripType.S.name());
			dto.setTrCharge(dto.getTCkCtTripRates().get(0).getTrCharge());
			return super.add(dto, principal);
		} else {
			// Holder for the charge to be set in the parent
			BigDecimal totalCharge = null;
			// Get the main from/to and save it as the parent
			dto.setTrId(CkUtil.generateId(PREFIX_KEY));
			dto.setTrStatus(TripRateStatus.NEW.getStatusCode());
			dto.setTrType(TripType.M.name());
			dto.setTrCharge(dto.getTCkCtTripRates().get(0).getTrCharge());
			dto.setTrSeq(0);
			CkCtTripRate parent = super.add(dto, principal);

			// Save the children
			int seq = 1;
			for (CkCtTripRate childRate : dto.getTCkCtTripRates()) {
				childRate.setTrId(CkUtil.generateId(PREFIX_KEY));
				childRate.setTrStatus(TripRateStatus.NEW.getStatusCode());
				childRate.setTrType(TripType.C.name());
				childRate.setTrSeq(seq);
				childRate.setTCkCtRateTable(dto.getTCkCtRateTable());
				childRate.setTCkCtMstVehType(dto.getTCkCtMstVehType());
				childRate.setTCkCtTripRate(parent);
				if (parent.getTCkCtTripRates() == null)
					parent.setTCkCtTripRates(new ArrayList<>());

				if (totalCharge == null)
					totalCharge = childRate.getTrCharge();
				parent.getTCkCtTripRates().add(super.add(childRate, principal));
				seq++;
			}

			parent.setTrCharge(totalCharge);
			super.update(parent, principal);

			return parent;
		}

	}

	@Override
	public CkCtTripRate update(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		checkForDuplicates(dto, principal);

		return super.update(dto, principal);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripRate findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTripRate entity = dao.find(id);
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
	public CkCtTripRate deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtTripRate entity = dao.find(id);
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtTripRate delete(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		LOG.debug("delete");

		Date now = Calendar.getInstance().getTime();
		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			if (null == principal)
				throw new ParameterException("param principal null");

			String key = this.entityKeyFromDTO(dto); // abstract callback
			if (null == key)
				throw new ProcessingException("entityKeyFromDTO");

			TCkCtTripRate storedEntity = this.getDao().find(key);
			if (null == storedEntity)
				throw new EntityNotFoundException("key: " + key.toString());

			this.updateEntityStatus(storedEntity, 'I'); // abstract callback
			this.updateEntity(ACTION.MODIFY, storedEntity, principal, now); // abstract callback
			this.dao.update(storedEntity);

			// Delete children trip rate
			if(storedEntity.getTCkCtTripRates() != null && storedEntity.getTCkCtTripRates().size() > 0) {
				for(TCkCtTripRate childTripRate : storedEntity.getTCkCtTripRates()) {
					
					this.updateEntityStatus(childTripRate, 'I'); // abstract callback
					this.updateEntity(ACTION.MODIFY, childTripRate, principal, now); // abstract callback
					this.dao.update(childTripRate);
				}
			}
			
			// Audit
			audit(principal, key, ACTION.DEACTIVATE.toString());

			return dto;
		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("delete", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("delete", ex);
			COException.create(COException.ERROR, ErrorCodes.ERR_GEN_DATABASE, ErrorCodes.MSG_GEN_DATABASE,
					"delete operation failed for: " + moduleName, ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtTripRate> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripRate dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(this.countByAnd(dto));

			String selectClause = "from TCkCtTripRate o ";
			String orderByClause = this.formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtTripRate> entities = this.findEntitiesByAnd(dto, selectClause, orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtTripRate> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x, true);
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
		// TODO Auto-generated method stub
		return LOG;
	}

	@Override
	protected TCkCtTripRate initEnity(TCkCtTripRate entity) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (null != entity) {
			Hibernate.initialize(entity.getTCkCtLocationByTrLocFrom());
			Optional.ofNullable(entity.getTCkCtLocationByTrLocFrom().getTCkCtMstLocationType())
					.ifPresent(e -> Hibernate.initialize(e));
			Hibernate.initialize(entity.getTCkCtLocationByTrLocTo());
			Optional.ofNullable(entity.getTCkCtLocationByTrLocTo().getTCkCtMstLocationType())
					.ifPresent(e -> Hibernate.initialize(e));
			Hibernate.initialize(entity.getTCkCtRateTable());
			if (entity.getTCkCtRateTable() != null) {
				Hibernate.initialize(entity.getTCkCtRateTable().getTCoreAccnByRtCoFf());
				Hibernate.initialize(entity.getTCkCtRateTable().getTCoreAccnByRtCompany());
			}
			Hibernate.initialize(entity.getTCkCtMstVehType());
			Hibernate.initialize(entity.getTCkCtTripRates());
		}
		return entity;
	}

	@Override
	protected TCkCtTripRate entityFromDTO(CkCtTripRate dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtTripRate entity = new TCkCtTripRate();
			entity = dto.toEntity(entity);

			Optional<CkCtLocation> opCkCtLocationByTrLocFrom = Optional.ofNullable(dto.getTCkCtLocationByTrLocFrom());
			entity.setTCkCtLocationByTrLocFrom(opCkCtLocationByTrLocFrom.isPresent()
					? opCkCtLocationByTrLocFrom.get().toEntity(new TCkCtLocation())
					: null);

			Optional<CkCtMstVehType> opCkCtMstVehType = Optional.ofNullable(dto.getTCkCtMstVehType());
			entity.setTCkCtMstVehType(
					opCkCtMstVehType.isPresent() ? opCkCtMstVehType.get().toEntity(new TCkCtMstVehType()) : null);

			Optional<CkCtLocation> opCkCtLocationByTrLocTo = Optional.ofNullable(dto.getTCkCtLocationByTrLocTo());
			entity.setTCkCtLocationByTrLocTo(
					opCkCtLocationByTrLocTo.isPresent() ? opCkCtLocationByTrLocTo.get().toEntity(new TCkCtLocation())
							: null);

			Optional<CkCtRateTable> opCkCtRateTable = Optional.ofNullable(dto.getTCkCtRateTable());
			entity.setTCkCtRateTable(
					opCkCtRateTable.isPresent() ? opCkCtRateTable.get().toEntity(new TCkCtRateTable()) : null);

			// Parent TripRate
			Optional<CkCtTripRate> opParentTripRate = Optional.ofNullable(dto.getTCkCtTripRate());
			entity.setTCkCtTripRate(
					opParentTripRate.isPresent() ? opParentTripRate.get().toEntity(new TCkCtTripRate()) : null);

			Optional<List<CkCtTripRate>> opListTripRates = Optional.ofNullable(dto.getTCkCtTripRates());
			List<TCkCtTripRate> children = new ArrayList<>();

			opListTripRates.ifPresent(tripRates -> {
				tripRates.stream().forEach(eTr -> {
					TCkCtTripRate obj = new TCkCtTripRate();
					eTr.toEntity(obj);
					obj.setTCkCtLocationByTrLocFrom(eTr.getTCkCtLocationByTrLocFrom().toEntity(new TCkCtLocation()));
					obj.setTCkCtLocationByTrLocTo(eTr.getTCkCtLocationByTrLocTo().toEntity(new TCkCtLocation()));
					obj.setTCkCtMstVehType(
							opCkCtMstVehType.isPresent() ? opCkCtMstVehType.get().toEntity(new TCkCtMstVehType())
									: null);
					obj.setTCkCtRateTable(
							opCkCtRateTable.isPresent() ? opCkCtRateTable.get().toEntity(new TCkCtRateTable()) : null);

					children.add(obj);
				});
			});
			entity.setTCkCtTripRates(children);

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
	protected CkCtTripRate dtoFromEntity(TCkCtTripRate entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		return this.dtoFromEntity(entity, true);
	}

	protected CkCtTripRate dtoFromEntity(TCkCtTripRate entity, boolean isIncludeChildTrip)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtTripRate dto = new CkCtTripRate(entity);

			TCkCtLocation opCkCtLocationByTrLocFromE = entity.getTCkCtLocationByTrLocFrom();
			if (null != opCkCtLocationByTrLocFromE) {
				CkCtLocation opCkCtLocationByTrLocFrom = new CkCtLocation(opCkCtLocationByTrLocFromE);
				Optional.ofNullable(opCkCtLocationByTrLocFromE.getTCkCtMstLocationType())
						.ifPresent(e -> opCkCtLocationByTrLocFrom.setTCkCtMstLocationType(new CkCtMstLocationType(e)));
				dto.setTCkCtLocationByTrLocFrom(opCkCtLocationByTrLocFrom);
			}

			Optional<TCkCtMstVehType> opCkCtMstVehType = Optional.ofNullable(entity.getTCkCtMstVehType());
			dto.setTCkCtMstVehType(opCkCtMstVehType.isPresent() ? new CkCtMstVehType(opCkCtMstVehType.get()) : null);

			TCkCtLocation opCkCtLocationByTrLocToE = entity.getTCkCtLocationByTrLocTo();
			if (null != opCkCtLocationByTrLocToE) {
				CkCtLocation opCkCtLocationByTrLocTo = new CkCtLocation(opCkCtLocationByTrLocToE);
				Optional.ofNullable(opCkCtLocationByTrLocToE.getTCkCtMstLocationType())
						.ifPresent(e -> opCkCtLocationByTrLocTo.setTCkCtMstLocationType(new CkCtMstLocationType(e)));
				dto.setTCkCtLocationByTrLocTo(opCkCtLocationByTrLocTo);
			}

			Optional<TCkCtRateTable> opCkCtRateTable = Optional.ofNullable(entity.getTCkCtRateTable());
			dto.setTCkCtRateTable(opCkCtRateTable.isPresent() ? new CkCtRateTable(opCkCtRateTable.get()) : null);
			if (opCkCtRateTable.isPresent()) {
				dto.getTCkCtRateTable()
						.setTCoreAccnByRtCoFf(new CoreAccn(entity.getTCkCtRateTable().getTCoreAccnByRtCoFf()));
				dto.getTCkCtRateTable()
						.setTCoreAccnByRtCompany(new CoreAccn(entity.getTCkCtRateTable().getTCoreAccnByRtCompany()));
			}

			Optional<TCkCtTripRate> opParentTripRate = Optional.ofNullable(entity.getTCkCtTripRate());
			dto.setTCkCtTripRate(opParentTripRate.isPresent() ? new CkCtTripRate(opParentTripRate.get()) : null);

			//
			if (isIncludeChildTrip && entity.getTCkCtTripRates() != null && entity.getTCkCtTripRates().size() > 0) {
				List<CkCtTripRate> rateList = new ArrayList<>();
				for (TCkCtTripRate subEntity : entity.getTCkCtTripRates()) {
					CkCtTripRate rate = this.dtoFromEntity(subEntity, false);
					rateList.add(rate);
				}
				dto.setTCkCtTripRates(rateList);
			}

			return dto;
		} catch (ParameterException ex) {
			LOG.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected String entityKeyFromDTO(CkCtTripRate dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return dto.getTrId();
		} catch (ParameterException ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtTripRate updateEntity(ACTION attribute, TCkCtTripRate entity, Principal principal, Date date)
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
			switch (attribute) {
			case CREATE:
				entity.setTrUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				entity.setTrDtCreate(date);
				entity.setTrDtLupd(date);
				entity.setTrUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				break;

			case MODIFY:
				entity.setTrDtLupd(date);
				entity.setTrUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
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
	protected TCkCtTripRate updateEntityStatus(TCkCtTripRate entity, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setTrStatus('D');
			return entity;
		} catch (ParameterException ex) {
			LOG.error("updateEntityStatus", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("updateEntityStatus", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtTripRate preSaveUpdateDTO(TCkCtTripRate storedEntity, CkCtTripRate dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setTrUidCreate(storedEntity.getTrUidCreate());
			dto.setTrDtCreate(storedEntity.getTrDtCreate());
			return dto;
		} catch (ParameterException ex) {
			LOG.error("preSaveUpdateDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("preSaveUpdateDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected void preSaveValidation(CkCtTripRate dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtTripRate dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtTripRate dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			StringBuffer searchStatement = new StringBuffer();

			if (StringUtils.isNotBlank(dto.getTrId())) {
				searchStatement.append(getOperator(wherePrinted) + "o.trId = :trId");
				wherePrinted = true;
			}

			Optional<Date> opCkTrDtCreate = Optional.ofNullable(dto.getTrDtCreate());
			if (opCkTrDtCreate.isPresent()) {
				searchStatement.append(getOperator(wherePrinted))
						.append("DATE_FORMAT(o.trDtCreate,'%d/%m/%Y') = :trDtCreate");
				wherePrinted = true;
			}
			Optional<Date> opCkTrDtLupd = Optional.ofNullable(dto.getTrDtLupd());
			if (opCkTrDtLupd.isPresent()) {
				searchStatement.append(getOperator(wherePrinted))
						.append("DATE_FORMAT(o.trDtLupd,'%d/%m/%Y') = :trDtLupd");
				wherePrinted = true;
			}

			Optional<CkCtMstVehType> opCkCtMstVehType = Optional.ofNullable(dto.getTCkCtMstVehType());
			if (opCkCtMstVehType.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtMstVehType.get().getVhtyId())) {
					searchStatement.append(getOperator(wherePrinted) + "o.TCkCtMstVehType.vhtyId = :vhtyId");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opCkCtMstVehType.get().getVhtyName())) {
					searchStatement.append(getOperator(wherePrinted) + "o.TCkCtMstVehType.vhtyName = :vhtyName");
					wherePrinted = true;
				}
			}

			Optional<CkCtLocation> opkCtLocationByTrLocFrom = Optional.ofNullable(dto.getTCkCtLocationByTrLocFrom());
			if (opkCtLocationByTrLocFrom.isPresent()) {
				if (StringUtils.isNotBlank(opkCtLocationByTrLocFrom.get().getLocName())) {
					searchStatement
							.append(getOperator(wherePrinted) + "o.TCkCtLocationByTrLocFrom.locName LIKE :fromLocName");
					wherePrinted = true;
				}
				Optional<CkCtMstLocationType> opCkCtLocTypeFrom = opkCtLocationByTrLocFrom
						.map(CkCtLocation::getTCkCtMstLocationType);
				if (opCkCtLocTypeFrom.isPresent()) {
					if (StringUtils.isNotBlank(opCkCtLocTypeFrom.get().getLctyName())) {
						searchStatement.append(getOperator(wherePrinted)
								+ "o.TCkCtLocationByTrLocFrom.TCkCtMstLocationType.lctyName = :fromLocType");
						wherePrinted = true;
					}
				}
			}

			Optional<CkCtLocation> opkCtLocationByTrLocTo = Optional.ofNullable(dto.getTCkCtLocationByTrLocTo());
			if (opkCtLocationByTrLocTo.isPresent()) {
				if (StringUtils.isNotBlank(opkCtLocationByTrLocTo.get().getLocName())) {
					searchStatement
							.append(getOperator(wherePrinted) + "o.TCkCtLocationByTrLocTo.locName LIKE :toLocName");
					wherePrinted = true;
				}
				Optional<CkCtMstLocationType> opCkCtLocTypeTo = opkCtLocationByTrLocTo
						.map(CkCtLocation::getTCkCtMstLocationType);
				if (opCkCtLocTypeTo.isPresent()) {
					if (StringUtils.isNotBlank(opCkCtLocTypeTo.get().getLctyName())) {
						searchStatement.append(getOperator(wherePrinted)
								+ "o.TCkCtLocationByTrLocTo.TCkCtMstLocationType.lctyName = :toLocType");
						wherePrinted = true;
					}
				}
			}

			Optional<CkCtRateTable> opCkCtRateTable = Optional.ofNullable(dto.getTCkCtRateTable());
			if (opCkCtRateTable.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtRateTable.get().getRtId())) {
					searchStatement.append(getOperator(wherePrinted) + "o.TCkCtRateTable.rtId = :rtId");
					wherePrinted = true;
				}
				if (opCkCtRateTable.get().getRtStatus() != null) {
					searchStatement.append(getOperator(wherePrinted) + "o.TCkCtRateTable.rtStatus = :rtStatus");
					wherePrinted = true;
				}

				Optional<CoreAccn> opRtCompany = Optional.ofNullable(opCkCtRateTable.get().getTCoreAccnByRtCompany());
				if (opRtCompany.isPresent()) {
					if (StringUtils.isNotBlank(opRtCompany.get().getAccnId())) {
						searchStatement.append(
								getOperator(wherePrinted) + "o.TCkCtRateTable.TCoreAccnByRtCompany.accnId = :rtAccnId");
						wherePrinted = true;
					}
					if (StringUtils.isNotBlank(opRtCompany.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted)
								+ "o.TCkCtRateTable.TCoreAccnByRtCompany.accnId like :rtAccnName");
						wherePrinted = true;
					}
				}

				Optional<CoreAccn> opRtCoff = Optional.ofNullable(opCkCtRateTable.get().getTCoreAccnByRtCoFf());
				if (opRtCoff.isPresent()) {
					if (StringUtils.isNotBlank(opRtCoff.get().getAccnId())) {
						searchStatement.append(getOperator(wherePrinted)
								+ "o.TCkCtRateTable.TCoreAccnByRtCoFf.accnId = :rtCoffAccnId");
						wherePrinted = true;
					}
					if (StringUtils.isNotBlank(opRtCoff.get().getAccnName())) {
						searchStatement.append(getOperator(wherePrinted)
								+ "o.TCkCtRateTable.TCoreAccnByRtCoFf.accnId like :rtCoffAccnName");
						wherePrinted = true;
					}
				}

				// https://jira.vcargocloud.com/browse/CT-212 - add checking that the associated
				// rate table is not expired
				searchStatement.append(getOperator(wherePrinted)
						+ " NOW() BETWEEN o.TCkCtRateTable.rtDtStart and o.TCkCtRateTable.rtDtEnd");
				wherePrinted = true;
			}

			// Added filter for trCharge
			if (dto.getTrCharge() != null && dto.getTrCharge().compareTo(BigDecimal.ZERO) > 0) {
				searchStatement.append(getOperator(wherePrinted)).append("o.trCharge =  :trCharge");
				wherePrinted = true;
			}

			searchStatement.append(getOperator(wherePrinted) + "o.trStatus in (:trStatus)");
			searchStatement.append(getOperator(wherePrinted) + "o.trType in :trType");
			searchStatement.append(getOperator(wherePrinted) + "o.trType in :trTypeFilter");

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
	protected HashMap<String, Object> getParameters(CkCtTripRate dto) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			if (StringUtils.isNotBlank(dto.getTrId()))
				parameters.put("trId", dto.getTrId());

			Principal principal = principalUtilService.getPrincipal();
			if (null == principal)
				throw new ProcessingException("principa null");

			Optional<List<String>> opAuthRoles = Optional.ofNullable(principal.getRoleList());
			if (!opAuthRoles.isPresent())
				throw new ProcessingException("principal roles null or empty");

			if (opAuthRoles.get().contains(Roles.SP_L1.name())) {
				parameters.put("trStatus", Arrays.asList(TripRateStatus.SUB.getStatusCode()));

			} else if (opAuthRoles.get().contains(Roles.SP_FIN_HD.name())) {
				parameters.put("trStatus", Arrays.asList(TripRateStatus.VER.getStatusCode()));

			} else {
				parameters.put("trStatus",
						Arrays.asList(TripRateStatus.NEW.getStatusCode(), TripRateStatus.APP.getStatusCode(),
								TripRateStatus.SUB.getStatusCode(), TripRateStatus.VER.getStatusCode(),
								TripRateStatus.INACTIVE.getStatusCode()));
			}

			Optional<Date> opCkTrDtCreate = Optional.ofNullable(dto.getTrDtCreate());
			if (opCkTrDtCreate.isPresent())
				parameters.put("trDtCreate", sdfDate.format(opCkTrDtCreate.get()));
			Optional<Date> opCkTrDtLupd = Optional.ofNullable(dto.getTrDtLupd());
			if (opCkTrDtLupd.isPresent())
				parameters.put("trDtLupd", sdfDate.format(opCkTrDtLupd.get()));

			Optional<CkCtMstVehType> opCkCtMstVehType = Optional.ofNullable(dto.getTCkCtMstVehType());
			if (opCkCtMstVehType.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtMstVehType.get().getVhtyId()))
					parameters.put("vhtyId", opCkCtMstVehType.get().getVhtyId());
				if (StringUtils.isNotBlank(opCkCtMstVehType.get().getVhtyName()))
					parameters.put("vhtyName", "%" + opCkCtMstVehType.get().getVhtyName() + "%");
			}

			Optional<CkCtLocation> opCkCtLocationByTrLocFrom = Optional.ofNullable(dto.getTCkCtLocationByTrLocFrom());
			if (opCkCtLocationByTrLocFrom.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtLocationByTrLocFrom.get().getLocName()))
					parameters.put("fromLocName", "%" + opCkCtLocationByTrLocFrom.get().getLocName() + "%");
			}
			if (opCkCtLocationByTrLocFrom.isPresent()) {
				Optional<CkCtMstLocationType> opCkCtLocTypeFrom = opCkCtLocationByTrLocFrom
						.map(CkCtLocation::getTCkCtMstLocationType);
				if (opCkCtLocTypeFrom.isPresent()) {
					if (StringUtils.isNotBlank(opCkCtLocTypeFrom.get().getLctyName()))
						parameters.put("fromLocType", opCkCtLocTypeFrom.get().getLctyName());
				}
			}

			Optional<CkCtLocation> opCkCtLocationByTrLocTo = Optional.ofNullable(dto.getTCkCtLocationByTrLocTo());
			if (opCkCtLocationByTrLocTo.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtLocationByTrLocTo.get().getLocName()))
					parameters.put("toLocName", "%" + opCkCtLocationByTrLocTo.get().getLocName() + "%");
			}
			if (opCkCtLocationByTrLocTo.isPresent()) {
				Optional<CkCtMstLocationType> opCkCtLocTypeTo = opCkCtLocationByTrLocTo
						.map(CkCtLocation::getTCkCtMstLocationType);
				if (opCkCtLocTypeTo.isPresent()) {
					if (StringUtils.isNotBlank(opCkCtLocTypeTo.get().getLctyName()))
						parameters.put("toLocType", opCkCtLocTypeTo.get().getLctyName());
				}
			}

			Optional<CkCtRateTable> opCkCtRateTable = Optional.ofNullable(dto.getTCkCtRateTable());
			if (opCkCtRateTable.isPresent()) {
				if (StringUtils.isNotBlank(opCkCtRateTable.get().getRtId()))
					parameters.put("rtId", opCkCtRateTable.get().getRtId());
				if (opCkCtRateTable.get().getRtStatus() != null)
					parameters.put("rtStatus", opCkCtRateTable.get().getRtStatus());
				Optional<CoreAccn> opRtCompany = Optional.ofNullable(opCkCtRateTable.get().getTCoreAccnByRtCompany());
				if (opRtCompany.isPresent()) {
					if (StringUtils.isNotBlank(opRtCompany.get().getAccnId())) {
						parameters.put("rtAccnId", opRtCompany.get().getAccnId());
					}
					if (StringUtils.isNotBlank(opRtCompany.get().getAccnName())) {
						parameters.put("rtAccnName", "%" + opRtCompany.get().getAccnName() + "%");
					}
				}

				Optional<CoreAccn> opRtCoff = Optional.ofNullable(opCkCtRateTable.get().getTCoreAccnByRtCoFf());
				if (opRtCoff.isPresent()) {
					if (StringUtils.isNotBlank(opRtCoff.get().getAccnId())) {
						parameters.put("rtCoffAccnId", opRtCoff.get().getAccnId());
					}
					if (StringUtils.isNotBlank(opRtCoff.get().getAccnName())) {
						parameters.put("rtCoffAccnName", "%" + opRtCoff.get().getAccnName() + "%");
					}
				}
			}

			// Added filter for trCharge
			if (dto.getTrCharge() != null && dto.getTrCharge().compareTo(BigDecimal.ZERO) > 0) {
				parameters.put("trCharge", dto.getTrCharge());
			}

			// Added filter for trCharge
			if (dto.getTrType() != null) {
				parameters.put("trType", Arrays.asList(dto.getTrType()));
			} else {
				parameters.put("trType", Arrays.asList("S", "M"));
			}

			if (dto.getTrTypeFilter() != null) {
				parameters.put("trTypeFilter", Arrays.asList(dto.getTrTypeFilter().split(",")));
			} else {
				parameters.put("trTypeFilter", Arrays.asList("S", "M"));
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
	protected CkCtTripRate whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");

		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtTripRate dto = new CkCtTripRate();

			CkCtLocation ckCtLocationByTrLocTo = new CkCtLocation();
			CkCtMstLocationType ckCtLocationTypeByTrLocTo = new CkCtMstLocationType();
			CkCtLocation ckCtLocationByTrLocFrom = new CkCtLocation();
			CkCtMstLocationType ckCtLocationTypeByTrLocFrom = new CkCtMstLocationType();
			CkCtMstVehType ckCtMstVehType = new CkCtMstVehType();
			CkCtRateTable ckCtRateTable = new CkCtRateTable();

			CoreAccn rtCompanyAccn = new CoreAccn();
			CoreAccn rtCoFfAccn = new CoreAccn();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				String attribute = entityWhere.getAttribute();
				if (attribute.equalsIgnoreCase("trId"))
					dto.setTrId(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtRateTable.rtId"))
					ckCtRateTable.setRtId(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtRateTable.rtStatus"))
					ckCtRateTable.setRtStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
				else if (attribute.equalsIgnoreCase("TCkCtRateTable.TCoreAccnByRtCompany.accnId"))
					rtCompanyAccn.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtRateTable.TCoreAccnByRtCompany.accnName"))
					rtCompanyAccn.setAccnName(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtRateTable.TCoreAccnByRtCoFf.accnId"))
					rtCoFfAccn.setAccnId(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtRateTable.TCoreAccnByRtCoFf.accnName"))
					rtCoFfAccn.setAccnName(opValue.get());
				else if (attribute.equalsIgnoreCase("trStatus"))
					dto.setTrStatus((opValue.get() == null) ? null : opValue.get().charAt(0));
				else if (attribute.equalsIgnoreCase("trDtCreate"))
					dto.setTrDtCreate(sdfDate.parse(opValue.get()));
				else if (attribute.equalsIgnoreCase("trDtLupd"))
					dto.setTrDtLupd(sdfDate.parse(opValue.get()));

				else if (attribute.equalsIgnoreCase("TCkCtLocationByTrLocFrom.locName"))
					ckCtLocationByTrLocFrom.setLocName(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtLocationByTrLocTo.locName"))
					ckCtLocationByTrLocTo.setLocName(opValue.get());

				else if (attribute.equalsIgnoreCase("TCkCtLocationByTrLocFrom.TCkCtMstLocationType.lctyName"))
					ckCtLocationTypeByTrLocFrom.setLctyName(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtLocationByTrLocTo.TCkCtMstLocationType.lctyName"))
					ckCtLocationTypeByTrLocTo.setLctyName(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtMstVehType.vhtyName"))
					ckCtMstVehType.setVhtyName(opValue.get());
				else if (attribute.equalsIgnoreCase("TCkCtMstVehType.vhtyId"))
					ckCtMstVehType.setVhtyId(opValue.get());

				// Added filter for trCharge
				else if (entityWhere.getAttribute().equalsIgnoreCase("trCharge")) {
					BigDecimal charge = new BigDecimal(opValue.get());
					dto.setTrCharge(charge);
				} else if (entityWhere.getAttribute().equalsIgnoreCase("trType")) {
					dto.setTrType(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase("trTypeDesc")) {
					dto.setTrType(opValue.get());
				} else if (entityWhere.getAttribute().equalsIgnoreCase("trTypeFilter")) {
					dto.setTrTypeFilter(opValue.get());
				}

			}

			ckCtRateTable.setTCoreAccnByRtCompany(rtCompanyAccn);
			ckCtRateTable.setTCoreAccnByRtCoFf(rtCoFfAccn);
			dto.setTCkCtMstVehType(ckCtMstVehType);
			ckCtLocationByTrLocFrom.setTCkCtMstLocationType(ckCtLocationTypeByTrLocFrom);
			ckCtLocationByTrLocTo.setTCkCtMstLocationType(ckCtLocationTypeByTrLocTo);
			dto.setTCkCtLocationByTrLocFrom(ckCtLocationByTrLocFrom);
			dto.setTCkCtLocationByTrLocTo(ckCtLocationByTrLocTo);
			dto.setTCkCtRateTable(ckCtRateTable);
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
	protected CoreMstLocale getCoreMstLocale(CkCtTripRate dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("getCoreMstLocale");

		try {
			if (null == dto) {
				throw new ParameterException("param dto null");
			}
			CoreMstLocale coreMstLocale = dto.getCoreMstLocale();
			if (null == coreMstLocale) {
				throw new ProcessingException("coreMstLocale null");
			}

			return coreMstLocale;
		} catch (ParameterException | ProcessingException ex) {
			LOG.error("getCoreMstLocale", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getCoreMstLocale", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtTripRate setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtTripRate dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Optional<CkCtTripRate> getByTripRateTableAndFromToVehTypeCurr(TripChargeReq tripChargeReq) throws Exception {
		if (tripChargeReq == null)
			throw new ParameterException("param tripChargeReq null");

		TCkCtTripRate tripRateE = ckCtTripRateDao.findByTripRateTableAndFromToVehType(tripChargeReq.getToAccn(),
				tripChargeReq.getCoFfAccn(), tripChargeReq.getCurrency(), tripChargeReq.getLocFrom(),
				tripChargeReq.getLocTo(), tripChargeReq.getVehType());

		if (tripRateE == null)
			return Optional.empty();
		return Optional.ofNullable(dtoFromEntity(tripRateE));

	}

	/**
	 * Process the workflow of trip rate by rate table.
	 */
	public void processTripRate(List<CkCtTripRate> listTripRateDto, JobActions action, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		try {

			if (listTripRateDto == null)
				throw new ParameterException("param listTripRateDto null");

			if (action == null)
				throw new ParameterException("param action is null");

			if (principal == null)
				throw new ParameterException("param principal null");

			switch (action) {
			case SUBMIT:
				for (CkCtTripRate tr : listTripRateDto) {
					tr = tripRateWorkflowService.moveState(FormActions.SUBMIT, tr, principal, ServiceTypes.CLICTRUCK);
				}
				break;
			case VERIFY:
				for (CkCtTripRate tr : listTripRateDto) {
					tr = tripRateWorkflowService.moveState(FormActions.VERIFY, tr, principal, ServiceTypes.CLICTRUCK);
				}
				break;
			case APPROVE:
				for (CkCtTripRate tr : listTripRateDto) {
					tr = tripRateWorkflowService.moveState(FormActions.APPROVE, tr, principal, ServiceTypes.CLICTRUCK);
				}
				break;
			case REJECT:
				for (CkCtTripRate tr : listTripRateDto) {
					tr = tripRateWorkflowService.moveState(FormActions.REJECT, tr, principal, ServiceTypes.CLICTRUCK);
				}
				break;
			case ACTIVATE: {
				for (CkCtTripRate tr : listTripRateDto) {
					tr = tripRateWorkflowService.moveState(FormActions.ACTIVATE, tr, principal, ServiceTypes.CLICTRUCK);
				}
				break;
			}
			case DEACTIVATE: {
				for (CkCtTripRate tr : listTripRateDto) {
					tr = tripRateWorkflowService.moveState(FormActions.DEACTIVATE, tr, principal,
							ServiceTypes.CLICTRUCK);
				}
				break;
			}
			default:
				break;
			}

		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
	}

	@Override
	public Optional<CkCtTripRate> getByCoFfAndLocFromTo(TripChargeReq tripChargeReq) throws Exception {
		List<TCkCtTripRate> tckCkCtTripRates = ckCtTripRateDao.findByCoFfAndLocFromTo(tripChargeReq.getCoFfAccn(),
				tripChargeReq.getLocFrom(), tripChargeReq.getLocTo());
		if (!tckCkCtTripRates.isEmpty()) {
			CkCtTripRate ckCtTripRate = new CkCtTripRate(tckCkCtTripRates.get(0));
			return Optional.ofNullable(ckCtTripRate);
		}
		return Optional.ofNullable(null);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtTripRate> getByRateTable(CkCtRateTable rateTableDto) throws Exception {
		if (rateTableDto == null)
			throw new ParameterException("param rateTableDto null");

		List<CkCtTripRate> tripRatesList = new ArrayList<>();
		String hql = "from TCkCtTripRate o where o.TCkCtRateTable.rtId = :rateTableId";
		Map<String, Object> params = new HashMap<>();
		params.put("rateTableId", rateTableDto.getRtId());

		List<TCkCtTripRate> list = dao.getByQuery(hql, params);
		if (list != null && list.size() > 0) {
			for (TCkCtTripRate rt : list) {
				Hibernate.initialize(rt.getTCkCtRateTable());
				CkCtTripRate dto = new CkCtTripRate(rt);
				dto.setTCkCtRateTable(new CkCtRateTable(rt.getTCkCtRateTable()));
				tripRatesList.add(dto);
			}
		}

		return tripRatesList;
	}

	// Helper Methods
	////////////////////
	private void checkForDuplicates(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		try {
			// Validate that truck and to/from are unique
			// Retrieve the rate table details to get some information such as the
			// coff/currency
			CkCtRateTable rateTableDto = rateTableService.findById(dto.getTCkCtRateTable().getRtId());

			if (rateTableDto == null)
				throw new EntityNotFoundException("rateTable " + dto.getTCkCtRateTable().getRtId() + " not found.");

			// With the change in the T_CK_CT_TRIP_RATE table, dto is expecting a lists of
			// tripRates instead of one due to changes in the UI as well.
			// trType = S should only have one element in TCkCtTripRates list
			List<CkCtTripRate> tripRates = dto.getTCkCtTripRates();

			if (tripRates != null && tripRates.size() > 0) {
				if (StringUtils.isNotBlank(dto.getTrType())
						&& StringUtils.equalsIgnoreCase(TripType.S.name(), dto.getTrType())) {
					validateSingleTripRate(rateTableDto, dto, principal);
				} else {
					// for multiple-trip
					validateMultiTripRate(rateTableDto, dto, principal);
				}

			}

		} catch (

		ValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	private EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) {
		if (orderBy == null) {
			return null;
		}
		if (StringUtils.isEmpty(orderBy.getAttribute())) {
			return null;
		}
		String newAttr = orderBy.getAttribute();
		newAttr = newAttr.replaceAll("tckCtLocationByTrLocTo", "TCkCtLocationByTrLocTo")
				.replaceAll("tckCtLocationByTrLocFrom", "TCkCtLocationByTrLocFrom")
				.replaceAll("tckCtMstLocationType", "TCkCtMstLocationType")
				.replaceAll("tckCtMstVehType", "TCkCtMstVehType").replaceAll("tckCtRateTable", "TCkCtRateTable");
		orderBy.setAttribute(newAttr);
		return orderBy;
	}

	private void validateSingleTripRate(CkCtRateTable rateTableDto, CkCtTripRate dto, Principal principal)
			throws Exception, ValidationException {

		List<TCkCtTripRate> tripRateEList = ckCtTripRateDao
				.findByTripRateTableAndFromToVehType(principal.getCoreAccn().getAccnId(),
						rateTableDto.getTCoreAccnByRtCoFf().getAccnId(), rateTableDto.getTMstCurrency().getCcyCode(),
						dto.getTCkCtLocationByTrLocFrom().getLocId(), dto.getTCkCtLocationByTrLocTo().getLocId(),
						dto.getTCkCtMstVehType().getVhtyId(),
						Arrays.asList(TripRateStatus.NEW.getStatusCode(), TripRateStatus.APP.getStatusCode(),
								TripRateStatus.SUB.getStatusCode(), TripRateStatus.VER.getStatusCode()),
						TripType.S.name());

		if (tripRateEList != null && tripRateEList.size() > 0) {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> validateErrParam = new HashMap<>();

			CkCtLocation from = locationService.findById(dto.getTCkCtLocationByTrLocFrom().getLocId());
			if (from == null)
				throw new EntityNotFoundException(
						"location " + dto.getTCkCtLocationByTrLocFrom().getLocId() + " not found");

			CkCtLocation to = locationService.findById(dto.getTCkCtLocationByTrLocTo().getLocId());
			if (to == null)
				throw new EntityNotFoundException(
						"location " + dto.getTCkCtLocationByTrLocTo().getLocId() + " not found");

			TCkCtMstVehType vehType = vehTypeDao.find(dto.getTCkCtMstVehType().getVhtyId());
			if (vehType == null)
				throw new EntityNotFoundException(
						"vehicle type " + dto.getTCkCtMstVehType().getVhtyId() + " not found");

			validateErrParam.put("rate-trip-duplicate",
					StringUtils.join(Arrays.asList(from.getLocName(), to.getLocName(), vehType.getVhtyName()), " - "));

			throw new ValidationException(mapper.writeValueAsString(validateErrParam));
		}
	}

	private void validateMultiTripRate(CkCtRateTable rateTableDto, CkCtTripRate dto, Principal principal)
			throws ValidationException, Exception {


		// Get the parent from/to first
		List<TCkCtTripRate> tripRateEList = ckCtTripRateDao
				.findByTripRateTableAndFromToVehType(principal.getCoreAccn().getAccnId(),
						rateTableDto.getTCoreAccnByRtCoFf().getAccnId(), rateTableDto.getTMstCurrency().getCcyCode(),
						dto.getTCkCtLocationByTrLocFrom().getLocId(), dto.getTCkCtLocationByTrLocTo().getLocId(),
						dto.getTCkCtMstVehType().getVhtyId(),
						Arrays.asList(TripRateStatus.NEW.getStatusCode(), TripRateStatus.APP.getStatusCode(),
								TripRateStatus.SUB.getStatusCode(), TripRateStatus.VER.getStatusCode()),
						TripType.M.name());

		// There is an existing multi-trip
		if (tripRateEList != null && tripRateEList.size() > 0) {
			
			for (TCkCtTripRate tripRateEParent : tripRateEList) {
				
				if (dto.getTCkCtTripRates().size() != tripRateEParent.getTCkCtTripRates().size()) {
					return;
				}

				List<String> duplicates = new ArrayList<>();

				tripRateEParent.getTCkCtTripRates().sort( (a, b) -> a.getTrSeq() - b.getTrSeq());
				// Get the children and check for duplicates
				for (int i = 0; i < dto.getTCkCtTripRates().size(); i++) {

					CkCtTripRate childTr = dto.getTCkCtTripRates().get(i);
					childTr.setTrSeq(i + 1); // from 1
					// if same from/to and seqNo from the same tripRateParent, add in the duplicates
					// list
					TCkCtTripRate childTrE = tripRateEParent.getTCkCtTripRates().get(i);
					if (childTr.equalsLocAndSeq(childTrE)) {
						duplicates.add(childTr.getTCkCtLocationByTrLocFrom().getLocName() + " - "
								+ childTr.getTCkCtLocationByTrLocTo().getLocName() + "- sequence: "
								+ childTr.getTrSeq());
					}
				}
				// If duplicates is not empty, throw the validation error
				if (duplicates.size() == dto.getTCkCtTripRates().size()) {
					ObjectMapper mapper = new ObjectMapper();
					Map<String, Object> validateErrParam = new HashMap<>();

					validateErrParam.put("rate-trip-duplicate", StringUtils.join(duplicates, "\n"));
					throw new ValidationException(mapper.writeValueAsString(validateErrParam));
				}
			}
		}
		/*-
		if (tripRateEParent != null) {
			// Get the children and check for duplicates
			for (CkCtTripRate childTr : dto.getTCkCtTripRates()) {
				TCkCtTripRate childTrE = ckCtTripRateDao.findByTripRateTableAndFromToVehType(
						principal.getCoreAccn().getAccnId(), rateTableDto.getTCoreAccnByRtCoFf().getAccnId(),
						rateTableDto.getTMstCurrency().getCcyCode(), dto.getTCkCtLocationByTrLocFrom().getLocId(),
						dto.getTCkCtLocationByTrLocTo().getLocId(), dto.getTCkCtMstVehType().getVhtyId(),
						Arrays.asList(TripRateStatus.NEW.getStatusCode(), TripRateStatus.APP.getStatusCode(),
								TripRateStatus.SUB.getStatusCode(), TripRateStatus.VER.getStatusCode()),
						childTr.getTrSeq(), dto.getTrId());
				// if same from/to and seqNo from the same tripRateParent, add in the duplicates
				// list
				if (childTrE != null) {
					duplicates.add(childTrE.getTCkCtLocationByTrLocFrom().getLocName() + " - "
							+ childTrE.getTCkCtLocationByTrLocTo().getLocName() + "-"
							+ childTrE.getTCkCtMstVehType().getVhtyName());
				}
				// otherwise just ignore and continue
		
			}
		}
		*/

	}
}
