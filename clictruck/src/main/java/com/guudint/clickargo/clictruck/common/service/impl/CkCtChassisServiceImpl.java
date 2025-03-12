package com.guudint.clickargo.clictruck.common.service.impl;

import java.text.SimpleDateFormat;
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

import com.guudint.clickargo.clictruck.common.dto.CkCtChassis;
import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.guudint.clickargo.clictruck.common.service.CkCtChassisService;
import com.guudint.clickargo.clictruck.common.validator.CkCtChassisValidator;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
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
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.master.model.TMstAccnType;

public class CkCtChassisServiceImpl extends AbstractClickCargoEntityService<TCkCtChassis, String, CkCtChassis> implements ICkConstant, CkCtChassisService {
	
	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(CkCtChassisServiceImpl.class);
	private static String AUDIT = "CHASSIS";
	private static String TABLE = "T_CK_CT_CHASSIS";
	private static String CHASSIS_PREFIX = "CHS";

	// Constructor
	//////////////
	public CkCtChassisServiceImpl() {
		super("ckCtChassisDao", AUDIT, TCkCtChassis.class.getName(), TABLE);
	}

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;
	
	@Autowired
	protected GenericDao<TCkCtChassis, String> ckCtChassisDao;
	
	@Autowired
	private CkCtChassisValidator ckCtChassisValidator;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtChassis findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		log.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtChassis entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);
			this.initEnity(entity);

			return this.dtoFromEntity(entity);
		} catch (ParameterException | EntityNotFoundException ex) {
			log.error("findById ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("findById ", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtChassis deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		log.debug("deleteById");

		Date now = Calendar.getInstance().getTime();
		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");
			if (null == principal)
				throw new ParameterException("param prinicipal null");

			TCkCtChassis entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtChassis dto = dtoFromEntity(entity);
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<CkCtChassis> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		log.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtChassis dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));

			String selectClause = "from TCkCtChassis o ";
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtChassis> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtChassis> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x);
				} catch (ParameterException | ProcessingException e) {
					log.error("filterBy ", e);
				}
				return null;

			}).collect(Collectors.toList());

			return dtos;
		} catch (ParameterException | ProcessingException ex) {
			log.error("filterBy ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("filterBy ", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtChassis initEnity(TCkCtChassis entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkCtMstChassisType());
			Hibernate.initialize(entity.getTCoreAccn());
			Hibernate.initialize(entity.getTCoreAccn().getTMstAccnType());
		}
		return entity;
	}

	@Override
	protected TCkCtChassis entityFromDTO(CkCtChassis dto) throws ParameterException, ProcessingException {
		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtChassis entity = new TCkCtChassis();
			entity = dto.toEntity(entity);

			Optional<CoreAccn> opCoreAccn = Optional.ofNullable(dto.getTCoreAccn());
			entity.setTCoreAccn(opCoreAccn.isPresent() ? opCoreAccn.get().toEntity(new TCoreAccn()) : null);
			
			if (opCoreAccn.isPresent()) {
				Optional<MstAccnType> opMstAccnType = Optional.ofNullable(dto.getTCoreAccn().getTMstAccnType());
				entity.getTCoreAccn().setTMstAccnType(opMstAccnType.isPresent() ? opMstAccnType.get().toEntity(new TMstAccnType()) : null);
			}
			
			Optional<CkCtMstChassisType> opMstChassisType = Optional.ofNullable(dto.getTCkCtMstChassisType());
			entity.setTCkCtMstChassisType(opMstChassisType.isPresent() ? opMstChassisType.get().toEntity(new TCkCtMstChassisType()) : null);
			
			return entity;
		} catch (ParameterException ex) {
			log.error("entityFromDTO ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("entityFromDTO ", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtChassis dtoFromEntity(TCkCtChassis entity) throws ParameterException, ProcessingException {
		log.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtChassis dto = new CkCtChassis(entity);

			Optional<TCoreAccn> opCoreAccn = Optional.ofNullable(entity.getTCoreAccn());
			dto.setTCoreAccn(new CoreAccn(opCoreAccn.get()));
			
			if (opCoreAccn.isPresent()) {
				Optional<TMstAccnType> opMstAccnType = Optional.ofNullable(entity.getTCoreAccn().getTMstAccnType());
				dto.getTCoreAccn().setTMstAccnType(opMstAccnType.isPresent() ? new MstAccnType(opMstAccnType.get()) : null);
			}
			
			Optional<TCkCtMstChassisType> opMstChassisType = Optional.ofNullable(entity.getTCkCtMstChassisType());
			dto.setTCkCtMstChassisType(new CkCtMstChassisType(opMstChassisType.get()));

			return dto;
		} catch (ParameterException ex) {
			log.error("dtoFromEntity ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("dtoFromEntity ", ex);
			throw new ProcessingException(ex);
		}
	}
	
	@Override
	protected String entityKeyFromDTO(CkCtChassis dto) throws ParameterException, ProcessingException {
		log.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return dto.getChsId();
		} catch (ParameterException ex) {
			log.error("entityKeyFromDTO ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("entityKeyFromDTO ", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtChassis updateEntity(ACTION attriubte, TCkCtChassis entity, Principal principal, Date date)
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
				entity.setChsUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setChsDtCreate(date);
				entity.setChsUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setChsDtLupd(date);
				break;

			case MODIFY:
				entity.setChsUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setChsDtLupd(date);
				break;

			default:
				break;
			}

			return entity;
		} catch (ParameterException ex) {
			log.error("updateEntity ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("updateEntity ", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtChassis updateEntityStatus(TCkCtChassis entity, char status)
			throws ParameterException, ProcessingException {
		log.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setChsStatus(status);
			return entity;
		} catch (ParameterException ex) {
			log.error("updateEntityStatus ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("updateEntityStatus ", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtChassis preSaveUpdateDTO(TCkCtChassis storedEntity, CkCtChassis dto)
			throws ParameterException, ProcessingException {
		log.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setChsUidCreate(storedEntity.getChsUidCreate());
			dto.setChsDtCreate(storedEntity.getChsDtCreate());

			return dto;
		} catch (ParameterException ex) {
			log.error("preSaveUpdateDTO ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("preSaveUpdateDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected void preSaveValidation(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtChassis dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		log.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			StringBuffer searchStatement = new StringBuffer();
			
			// Display based on account
			searchStatement.append(getOperator(wherePrinted) + "o.TCoreAccn.accnId = :loggedInAccn");
			wherePrinted = true;
			
			if (StringUtils.isNotBlank(dto.getChsId())) {
				searchStatement.append(getOperator(wherePrinted) + "o.chsId = :chsId");
				wherePrinted = true;
			}

			if (StringUtils.isNotBlank(dto.getChsNo())) {
				searchStatement.append(getOperator(wherePrinted) + "o.chsNo LIKE :chsNo");
				wherePrinted = true;
			}
			
			Optional<CoreAccn> opCoreAccn = Optional.of(dto.getTCoreAccn());
			if (opCoreAccn.isPresent()) {
				if (StringUtils.isNotBlank(opCoreAccn.get().getAccnId())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCoreAccn.accnId = :accnId");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opCoreAccn.get().getAccnName())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCoreAccn.accnName LIKE :accnName");
					wherePrinted = true;
				}
			}
			
			Optional<CkCtMstChassisType> opMstChassisType = Optional.of(dto.getTCkCtMstChassisType());
			if (opMstChassisType.isPresent()) {
				if (StringUtils.isNotBlank(opMstChassisType.get().getChtyId())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCkCtMstChassisType.chtyId = :chtyId");
					wherePrinted = true;
				}
				if (StringUtils.isNotBlank(opMstChassisType.get().getChtyName())) {
					searchStatement.append(getOperator(wherePrinted))
							.append("o.TCkCtMstChassisType.chtyName LIKE :chtyName");
					wherePrinted = true;
				}
			}

			if (dto.getChsStatus() != null && Character.isAlphabetic(dto.getChsStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.chsStatus = :chsStatus");
				wherePrinted = true;
			}
			
			Optional<Date> opChsDtCreate = Optional.ofNullable(dto.getChsDtCreate());
			if (opChsDtCreate.isPresent()) {
				searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.chsDtCreate,'%d/%m/%Y') = :chsDtCreate");
				wherePrinted = true;
			}
			
			Optional<Date> opChsDtLupd = Optional.ofNullable(dto.getChsDtLupd());
			if (opChsDtLupd.isPresent()) {
				searchStatement.append(getOperator(wherePrinted) + "DATE_FORMAT(o.chsDtLupd,'%d/%m/%Y') = :chsDtLupd");
				wherePrinted = true;
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

	@Override
	protected HashMap<String, Object> getParameters(CkCtChassis dto) throws ParameterException, ProcessingException {
		log.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();
			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
			
			Principal principal = ckSession.getPrincipal();
			if (principal == null)
				throw new ProcessingException("principal is null");

			// Display based on account
			CoreAccn accn = principal.getCoreAccn();
			parameters.put("loggedInAccn", accn.getAccnId());
			
			if (StringUtils.isNotBlank(dto.getChsId()))
				parameters.put("chsId", dto.getChsId());

			if (StringUtils.isNotBlank(dto.getChsNo()))
				parameters.put("chsNo", "%" + dto.getChsNo() + "%");

			Optional<CoreAccn> opCoreAccn = Optional.ofNullable(dto.getTCoreAccn());
			if (opCoreAccn.isPresent() && StringUtils.isNotBlank(dto.getTCoreAccn().getAccnId())) {
				parameters.put("accnId", dto.getTCoreAccn().getAccnId());
			}
			if (opCoreAccn.isPresent() && StringUtils.isNotBlank(dto.getTCoreAccn().getAccnName())) {
				parameters.put("accnName", "%" + dto.getTCoreAccn().getAccnName() + "%");
			}
			
			Optional<CkCtMstChassisType> opCkCtMstChassisType = Optional.ofNullable(dto.getTCkCtMstChassisType());
			if (opCkCtMstChassisType.isPresent() && StringUtils.isNotBlank(dto.getTCkCtMstChassisType().getChtyId())) {
				parameters.put("chtyId", dto.getTCkCtMstChassisType().getChtyId());
			}
			if (opCkCtMstChassisType.isPresent() && StringUtils.isNotBlank(dto.getTCkCtMstChassisType().getChtyName())) {
				parameters.put("chtyName", "%" + dto.getTCkCtMstChassisType().getChtyName() + "%");
			}

			if (dto.getChsStatus() != null && Character.isAlphabetic(dto.getChsStatus()))
				parameters.put("chsStatus", dto.getChsStatus());

			Optional<Date> opChsDtCreate = Optional.ofNullable(dto.getChsDtCreate());
			if (opChsDtCreate.isPresent() && null != opChsDtCreate.get())
				parameters.put("chsDtCreate", sdfDate.format(opChsDtCreate.get()));
			
			Optional<Date> opChsDtLupd = Optional.ofNullable(dto.getChsDtLupd());
			if (opChsDtLupd.isPresent() && null != opChsDtLupd.get())
				parameters.put("chsDtLupd", sdfDate.format(opChsDtLupd.get()));
			
			return parameters;
		} catch (ParameterException ex) {
			log.error("getParameters ", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getParameters ", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtChassis whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		log.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtChassis dto = new CkCtChassis();
			CoreAccn coreAccn = new CoreAccn();
			CkCtMstChassisType mstChassisType = new CkCtMstChassisType();

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
			
			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("chsId"))
					dto.setChsId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("chsNo"))
					dto.setChsNo(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnId"))
					coreAccn.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCoreAccn.accnName"))
					coreAccn.setAccnName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtMstChassisType.chtyId"))
					mstChassisType.setChtyId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtMstChassisType.chtyName"))
					mstChassisType.setChtyName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("chsStatus"))
					dto.setChsStatus(opValue.get().charAt(0));
				if (entityWhere.getAttribute().equalsIgnoreCase("chsDtCreate"))
					dto.setChsDtCreate(sdfDate.parse(opValue.get()));
				if (entityWhere.getAttribute().equalsIgnoreCase("chsDtLupd"))
					dto.setChsDtLupd(sdfDate.parse(opValue.get()));
			}

			dto.setTCoreAccn(coreAccn);
			dto.setTCkCtMstChassisType(mstChassisType);
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
	protected CoreMstLocale getCoreMstLocale(CkCtChassis dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtChassis setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtChassis dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

    private EntityOrderBy formatOrderByObj(EntityOrderBy orderBy) {
        if (orderBy == null) {
            return null;
        }
        if (StringUtils.isEmpty(orderBy.getAttribute())) {
            return null;
        }
        String newAttr = orderBy.getAttribute();
        newAttr = newAttr.replaceAll("tckCtMstChassisType", "TCkCtMstChassisType")
        		.replaceAll("tcoreAccn", "TCoreAccn");
        orderBy.setAttribute(newAttr);
        return orderBy;
    }
    
    @Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtChassis updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		log.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtChassis ckCtChassis = findById(id);
		if (ckCtChassis == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			ckCtChassis.setChsStatus(RecordStatus.ACTIVE.getCode());
		} else if ("deactive".equals(status)) {
			ckCtChassis.setChsStatus(RecordStatus.INACTIVE.getCode());
		}
		update(ckCtChassis, principal);
		return ckCtChassis;
	}
    
	@Override
	public CkCtChassis newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtChassis chassis = new CkCtChassis();
		CoreAccn coreAccn = principal.getCoreAccn();
		chassis.setChsId(CkUtil.generateId(CHASSIS_PREFIX));
		chassis.setChsStatus(RecordStatus.ACTIVE.getCode());
		chassis.setChsDtCreate(new Date());
		chassis.setChsUidCreate(principal.getUserId());
		chassis.setChsDtLupd(new Date());
		chassis.setChsUidLupd(principal.getUserId());
		chassis.setTCoreAccn(coreAccn);
		return chassis;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtChassis chassis = (CkCtChassis) object;
		List<ValidationError> validationErrors = ckCtChassisValidator.validateCreate(chassis, principal);
		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return add(chassis, principal);
		}
	}
	
	@Override
	public CkCtChassis add(CkCtChassis dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (null == dto)
			throw new ParameterException("param dto null;");
		if (null == principal)
			throw new ParameterException("param principal null");
		try {
			dto.setChsId(CkUtil.generateId(CHASSIS_PREFIX));
			dto.setChsStatus(RecordStatus.ACTIVE.getCode());
			dto.setTCoreAccn(principal.getCoreAccn());
			return super.add(dto, principal);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ProcessingException(e);
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
	
}
