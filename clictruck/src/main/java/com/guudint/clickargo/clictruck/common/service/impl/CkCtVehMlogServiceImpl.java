package com.guudint.clickargo.clictruck.common.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dao.CkCtVehMlogDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.dto.CkCtVehMlog;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehMlog;
import com.guudint.clickargo.clictruck.common.service.CkCtVehMlogService;
import com.guudint.clickargo.clictruck.common.validator.CkCtVehMlogValidator;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehState;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;

public class CkCtVehMlogServiceImpl extends AbstractClickCargoEntityService<TCkCtVehMlog, String, CkCtVehMlog>
		implements CkCtVehMlogService {

	private static Logger LOG = Logger.getLogger(CkCtVehMlogServiceImpl.class);
	private final static String PREFIX_CK_CT_MLOG = "CKCTVEHMLOG";

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	private CkCtVehMlogValidator ckCtVehMlogValidator;
	
	@Autowired
	private CkCtVehMlogDao ckCtVehMlogDao;

	public CkCtVehMlogServiceImpl() {
		super("ckCtVehMlogDao", "VEH MLOG", TCkCtVehMlog.class.getName(), "T_CK_CT_VEH_MLOG");
		// TODO Auto-generated constructor stub
	}

	@Override
	public CkCtVehMlog newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtVehMlog ckCtVehMlog = new CkCtVehMlog();
		ckCtVehMlog.setTCkCtVeh(new CkCtVeh());
		return ckCtVehMlog;
	}

	@Override
	public CkCtVehMlog add(CkCtVehMlog dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (null == dto)
			throw new ParameterException("param dto null;");
		if (null == principal)
			throw new ParameterException("param principal null");

		dto.setVmlId(CkUtil.generateId(PREFIX_CK_CT_MLOG));
		dto.setVmlStatus(RecordStatus.ACTIVE.getCode());

		List<ValidationError> validationErrors = ckCtVehMlogValidator.validateCreate(dto, principal);
		if (null != validationErrors && !validationErrors.isEmpty())
			throw new ValidationException(validationErrorMap(validationErrors));

		Optional<TCkCtVehMlog> opTCkCtVehMlog = ckCtVehMlogDao.findByVehId(dto.getTCkCtVeh().getVhId());
		if (opTCkCtVehMlog.isPresent()) {
			throw new ProcessingException(
					"There is a existing active maintenance record with vehicle id : " + dto.getTCkCtVeh().getVhId());
		}

		return super.add(dto, principal);
	}

	@Override
	public CkCtVehMlog deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		return updateStatus(id, "delete");
	}

	@Transactional
	@Override
	public List<CkCtVehMlog> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtVehMlog ckCtVehMlog = whereDto(filterRequest);
		if (ckCtVehMlog == null) {
			throw new ProcessingException("whereDto null");
		}
		filterRequest.setTotalRecords(countByAnd(ckCtVehMlog));
		List<CkCtVehMlog> ckCtVehMlogs = new ArrayList<>();
		try {
			String orderByClause = formatOrderBy(filterRequest.getOrderBy().toString());
			List<TCkCtVehMlog> tCkCtVehMlogs = findEntitiesByAnd(ckCtVehMlog, "from TCkCtVehMlog o ", orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtVehMlog tCkCtVehMlog : tCkCtVehMlogs) {
				CkCtVehMlog dto = dtoFromEntity(tCkCtVehMlog);
				if (dto != null) {
					ckCtVehMlogs.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtVehMlogs;
	}

	protected String formatOrderBy(String attribute) throws Exception {
		attribute = Optional.ofNullable(attribute).orElse("");
		attribute = attribute.replace("tckCtVeh", "TCkCtVeh").replace("tCkCtVeh", "TCkCtVeh");
		return attribute;
	}

	@Transactional
	@Override
	public CkCtVehMlog findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		try {
			TCkCtVehMlog tCkCtVehMlog = dao.find(id);
			if (tCkCtVehMlog == null) {
				throw new EntityNotFoundException("id:" + id);
			}
			initEnity(tCkCtVehMlog);
			return dtoFromEntity(tCkCtVehMlog);
		} catch (Exception e) {
			LOG.error("findById", e);
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtVehMlog updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtVehMlog ckCtVehMlog = findById(id);
		if (ckCtVehMlog == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			ckCtVehMlog.setVmlStatus(RecordStatus.ACTIVE.getCode());
		} else if ("deactive".equals(status)) {
			ckCtVehMlog.setVmlStatus(RecordStatus.DEACTIVATE.getCode());
		} else if ("delete".equals(status)) {
			ckCtVehMlog.setVmlStatus(RecordStatus.INACTIVE.getCode());
		} else if ("close".equals(status)) {
			ckCtVehMlog.setVmlStatus(RecordStatus.CLOSED.getCode());
		}
		return update(ckCtVehMlog, principal);
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
	protected CkCtVehMlog dtoFromEntity(TCkCtVehMlog entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		try {
			if (entity == null) {
				throw new ParameterException("param entity null");
			} else {
				CkCtVehMlog dto = new CkCtVehMlog(entity);
				Optional<TCkCtVeh> opCkCtVeh = Optional.ofNullable(entity.getTCkCtVeh());
				if (opCkCtVeh.isPresent()) {
					dto.setTCkCtVeh(new CkCtVeh(opCkCtVeh.get()));
					Optional<TCkCtMstChassisType> opMstChassisType = Optional
							.ofNullable(opCkCtVeh.get().getTCkCtMstChassisType());
					dto.getTCkCtVeh().setTCkCtMstChassisType(
							opMstChassisType.isPresent() ? new CkCtMstChassisType(opMstChassisType.get()) : null);
					Optional<TCkCtMstVehState> opMstVehState = Optional
							.ofNullable(opCkCtVeh.get().getTCkCtMstVehState());
					dto.getTCkCtVeh().setTCkCtMstVehState(
							opMstVehState.isPresent() ? new CkCtMstVehState(opMstVehState.get()) : null);
					Optional<TCkCtMstVehType> opMstVehType = Optional.ofNullable(opCkCtVeh.get().getTCkCtMstVehType());
					dto.getTCkCtVeh().setTCkCtMstVehType(
							opMstVehType.isPresent() ? new CkCtMstVehType(opMstVehType.get()) : null);
					Optional<TCoreAccn> opCoreAccn = Optional.ofNullable(opCkCtVeh.get().getTCoreAccn());
					dto.getTCkCtVeh().setTCoreAccn(opCoreAccn.isPresent() ? new CoreAccn(opCoreAccn.get()) : null);

				}

				return dto;
			}
		} catch (ParameterException ex) {
			LOG.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtVehMlog entityFromDTO(CkCtVehMlog dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtVehMlog entity = new TCkCtVehMlog();
			entity = dto.toEntity(entity);

			Optional<CkCtVeh> opCkCtVeh = Optional.ofNullable(dto.getTCkCtVeh());
			if (opCkCtVeh.isPresent()) {
				entity.setTCkCtVeh(opCkCtVeh.get().toEntity(new TCkCtVeh()));
				Optional<CkCtMstChassisType> opMstChassisType = Optional
						.ofNullable(opCkCtVeh.get().getTCkCtMstChassisType());
				entity.getTCkCtVeh()
						.setTCkCtMstChassisType(opMstChassisType.isPresent()
								? opMstChassisType.get().toEntity(new TCkCtMstChassisType())
								: null);
				Optional<CkCtMstVehState> opMstVehState = Optional.ofNullable(opCkCtVeh.get().getTCkCtMstVehState());
				entity.getTCkCtVeh().setTCkCtMstVehState(
						opMstVehState.isPresent() ? opMstVehState.get().toEntity(new TCkCtMstVehState()) : null);
				Optional<CkCtMstVehType> opMstVehType = Optional.ofNullable(opCkCtVeh.get().getTCkCtMstVehType());
				entity.getTCkCtVeh().setTCkCtMstVehType(
						opMstVehType.isPresent() ? opMstVehType.get().toEntity(new TCkCtMstVehType()) : null);
				Optional<CoreAccn> opCoreAccn = Optional.ofNullable(opCkCtVeh.get().getTCoreAccn());
				entity.getTCkCtVeh()
						.setTCoreAccn(opCoreAccn.isPresent() ? opCoreAccn.get().toEntity(new TCoreAccn()) : null);

			}

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
	protected String entityKeyFromDTO(CkCtVehMlog ckCtVehMlog) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtVehMlog == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtVehMlog.getVmlId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtVehMlog arg0)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtVehMlog ckCtVehMlog) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		
		if (ckCtVehMlog == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();
		if (StringUtils.isNotBlank(ckCtVehMlog.getVmlId())) {
			parameters.put("vmlId", "%" + ckCtVehMlog.getVmlId() + "%");
		}
		if (ckCtVehMlog.getTCkCtVeh() != null) {
			if (ckCtVehMlog.getTCkCtVeh().getVhId() != null) {
				parameters.put("vhId", ckCtVehMlog.getTCkCtVeh().getVhId());
			}
		}
		if (ckCtVehMlog.getVmlDtStart() != null) {
			parameters.put("vmlDtStart", sdf.format(ckCtVehMlog.getVmlDtStart()));
		}
		if (ckCtVehMlog.getVmlDtEnd() != null) {
			parameters.put("vmlDtEnd", sdf.format(ckCtVehMlog.getVmlDtEnd()));
		}
		
		if (ckCtVehMlog.getVmlCost() != null) {
		    parameters.put("vmlCost", ckCtVehMlog.getVmlCost());
		}
		
		if (StringUtils.isNotBlank(ckCtVehMlog.getVmlRemarks())) {
			parameters.put("vmlRemarks", "%" + ckCtVehMlog.getVmlRemarks() + "%");
		}
		
		if (ckCtVehMlog.getVmlStatus() != null) {
			parameters.put("vmlStatus", ckCtVehMlog.getVmlStatus());
		} else {
			parameters.put("validStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.INACTIVE.getCode(), RecordStatus.CLOSED.getCode()));
		}
		
		if (ckCtVehMlog.getVmlDtCreate() != null) {
			parameters.put("vmlDtCreate", sdf.format(ckCtVehMlog.getVmlDtCreate()));
		}
		if (ckCtVehMlog.getVmlDtLupd() != null) {
			parameters.put("vmlDtLupd", sdf.format(ckCtVehMlog.getVmlDtLupd()));
		}

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtVehMlog ckCtVehMlog, boolean wherePrinted) throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :", IN = " in :";
		if (ckCtVehMlog == null) {
			throw new ParameterException("param dto null");
		}
		
		StringBuffer condition = new StringBuffer();
		if (StringUtils.isNotBlank(ckCtVehMlog.getVmlId())) {
			condition.append(getOperator(wherePrinted) + "o.vmlId" + CONTAIN
					+ "vmlId");
			wherePrinted = true;
		}
		if (ckCtVehMlog.getTCkCtVeh() != null) {
			if (ckCtVehMlog.getTCkCtVeh().getVhId() != null) {
				condition.append(getOperator(wherePrinted) + "o.TCkCtVeh.vhId" + CONTAIN
						+ "vhId");
				wherePrinted = true;
			}
		}
		if (ckCtVehMlog.getVmlDtStart() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.vmlDtStart" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "vmlDtStart");
			wherePrinted = true;
		}
		if (ckCtVehMlog.getVmlDtEnd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.vmlDtEnd" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "vmlDtEnd");
			wherePrinted = true;
		}
		if (ckCtVehMlog.getVmlCost() != null) {
			condition.append(getOperator(wherePrinted) + "o.vmlCost" + EQUAL
					+ "vmlCost");
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtVehMlog.getVmlRemarks())) {
			condition.append(getOperator(wherePrinted) + "o.vmlRemarks" + CONTAIN
					+ "vmlRemarks");
			wherePrinted = true;
		}
		if (ckCtVehMlog.getVmlStatus() != null && !Character.isWhitespace(ckCtVehMlog.getVmlStatus())) {
		    condition.append(getOperator(wherePrinted) + "o.vmlStatus" + EQUAL + "vmlStatus");
		    wherePrinted = true;
		} else {
		    condition.append(getOperator(wherePrinted) + "o.vmlStatus" + IN + "validStatus");
		    wherePrinted = true;
		}
		
		if (ckCtVehMlog.getVmlDtCreate() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.vmlDtCreate" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "vmlDtCreate");
			wherePrinted = true;
		}
		if (ckCtVehMlog.getVmlDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + "o.vmlDtLupd" + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + "o.vmlDtLupd");
			wherePrinted = true;
		}

		return condition.toString();
	}

	@Override
	protected TCkCtVehMlog initEnity(TCkCtVehMlog tCkCtVehMlog) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtVehMlog != null) {
			Hibernate.initialize(tCkCtVehMlog.getTCkCtVeh());
			Hibernate.initialize(tCkCtVehMlog.getTCkCtVeh().getTCkCtMstChassisType());
			Hibernate.initialize(tCkCtVehMlog.getTCkCtVeh().getTCkCtMstVehState());
			Hibernate.initialize(tCkCtVehMlog.getTCkCtVeh().getTCkCtMstVehType());
			Hibernate.initialize(tCkCtVehMlog.getTCkCtVeh().getTCoreAccn());
		}
		return tCkCtVehMlog;
	}

	@Override
	protected CkCtVehMlog preSaveUpdateDTO(TCkCtVehMlog storedEntity, CkCtVehMlog dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");
		if (null == storedEntity)
			throw new ParameterException("param storedEntity null");
		if (null == dto)
			throw new ParameterException("param dto null");

		dto.setVmlUidCreate(storedEntity.getVmlUidCreate());
		dto.setVmlDtCreate(storedEntity.getVmlDtCreate());

		return dto;
	}

	@Override
	protected void preSaveValidation(CkCtVehMlog arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtVehMlog arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtVehMlog setCoreMstLocale(CoreMstLocale arg0, CkCtVehMlog arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkCtVehMlog updateEntity(ACTION action, TCkCtVehMlog tCkCtVehMlog, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtVehMlog == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			tCkCtVehMlog.setVmlUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			tCkCtVehMlog.setVmlDtCreate(date);
			tCkCtVehMlog.setVmlDtLupd(date);
			tCkCtVehMlog.setVmlUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		case MODIFY:
			tCkCtVehMlog.setVmlDtLupd(date);
			tCkCtVehMlog.setVmlUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		default:
			break;
		}
		return tCkCtVehMlog;
	}

	@Override
	protected TCkCtVehMlog updateEntityStatus(TCkCtVehMlog tCkCtVehMlog, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtVehMlog == null) {
			throw new ParameterException("entity param null");
		}
		tCkCtVehMlog.setVmlStatus(status);
		return tCkCtVehMlog;
	}

	@Override
	protected CkCtVehMlog whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtVehMlog ckCtVehMlog = new CkCtVehMlog();
		CkCtVeh ckCtVeh = new CkCtVeh();
		ckCtVehMlog.setTCkCtVeh(ckCtVeh);
		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere.getValue() == null) {
				continue;
			}
			String attribute = "o." + entityWhere.getAttribute();
			if ("o.vmlId".equalsIgnoreCase(attribute)) {
				ckCtVehMlog.setVmlId(entityWhere.getValue());
			} else if ("o.TCkCtVeh.vhId".equalsIgnoreCase(attribute)) {
				ckCtVeh.setVhId(entityWhere.getValue());
			} else if ("o.vmlDtStart".equalsIgnoreCase(attribute)) {
				try {
					ckCtVehMlog.setVmlDtStart(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if ("o.vmlDtEnd".equalsIgnoreCase(attribute)) {
				try {
					ckCtVehMlog.setVmlDtEnd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if ("o.vmlCost".equalsIgnoreCase(attribute)) {
				ckCtVehMlog.setVmlCost(new BigDecimal(entityWhere.getValue()));
			} else if ("o.vmlRemarks".equalsIgnoreCase(attribute)) {
				ckCtVehMlog.setVmlRemarks(entityWhere.getValue());
			} else if ("o.vmlStatus".equalsIgnoreCase(attribute)) {
				ckCtVehMlog.setVmlStatus(entityWhere.getValue().charAt(0));
			} else if ("o.vmlDtCreate".equalsIgnoreCase(attribute)) {
				try {
					ckCtVehMlog.setVmlDtCreate(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if ("o.vmlDtLupd".equalsIgnoreCase(attribute)) {
				try {
					ckCtVehMlog.setVmlDtLupd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			}
		}
		return ckCtVehMlog;
	}

}
