package com.guudint.clickargo.clictruck.common.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq;
import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReqDocs;
import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReq;
import com.guudint.clickargo.clictruck.common.model.TCkCtAccnInqReqDocs;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkFileUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.service.ICkMstRecords;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.dto.MstAttType;
import com.vcc.camelone.master.model.TMstAttType;
import com.vcc.camelone.util.bean.COBeansUtil;

public class CkCtAccnInqReqDocsServiceImpl
		extends AbstractClickCargoEntityService<TCkCtAccnInqReqDocs, String, CkCtAccnInqReqDocs> {

	private static Logger log = Logger.getLogger(CkCtAccnInqReqDocsServiceImpl.class);
	private static String AUDIT_TAG = "ACCOUNT INQUIRY REQUEST DOCS";
	private static String TABLE_NAME = "T_CK_CT_ACCN_INQ_REQ_DOCS";

	@Autowired
	@Qualifier("ckCtAccnInqReqDao")
	private GenericDao<TCkCtAccnInqReq, String> ckCtAccnInqReqDao;

	@Autowired
	protected ICkMstRecords clicMasterRecrodsSvc;

	@Autowired
	@Qualifier("mAttTypeDao")
	private GenericDao<TMstAttType, String> mAttTypeDao;
	
	@Autowired
	protected CkFileUtil ckFileUtil;

	public CkCtAccnInqReqDocsServiceImpl() {
		super("ckCtAccnInqReqDocsDao", AUDIT_TAG, TCkCtAccnInqReqDocs.class.getName(), TABLE_NAME);
	}

	@Override
	public CkCtAccnInqReqDocs newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtAccnInqReqDocs reqDocs = (CkCtAccnInqReqDocs) object;

		reqDocs.setAirdId(CkUtil.generateId("AIRD"));
		reqDocs.setAirdStatus(RecordStatus.ACTIVE.getCode());

		try {
			String fileLocation = ckFileUtil.saveAttachment(reqDocs.getTCkCtAccnInqReq().getAirId() ,reqDocs.getAirdFilename(), reqDocs.getData());
			if (StringUtils.isNotBlank(fileLocation))
				reqDocs.setAirdDoc(fileLocation);
		} catch (Exception ex) {
			log.error("addObj", ex);
		}

		reqDocs = add(reqDocs, principal);

		// add audit for the accn inquiry req
		String docName = "DOCUMENT";
		try {
			TMstAttType attype = mAttTypeDao.find(reqDocs.getTMstAttType().getMattId());
			if (attype != null)
				docName = attype.getMattName();
		} catch (Exception ex) {
			log.error("error finding attachment type", ex);
		}

		audit(principal, reqDocs.getTCkCtAccnInqReq().getAirId(), docName + " UPLOADED");
		return reqDocs;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtAccnInqReqDocs findById(String id)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		log.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtAccnInqReqDocs entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);
			this.initEnity(entity);

			return this.dtoFromEntity(entity, true);
		} catch (ParameterException | EntityNotFoundException ex) {
			log.error("findById", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("findById", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtAccnInqReqDocs deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		log.debug("deleteById");

		Date now = Calendar.getInstance().getTime();
		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");
			if (null == principal)
				throw new ParameterException("param prinicipal null");

			TCkCtAccnInqReqDocs entity = dao.find(id);
			if (null == entity)
				throw new EntityNotFoundException("id: " + id);

			this.updateEntityStatus(entity, RecordStatus.INACTIVE.getCode());
			this.updateEntity(ACTION.MODIFY, entity, principal, now);

			CkCtAccnInqReqDocs dto = dtoFromEntity(entity);
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
	public List<CkCtAccnInqReqDocs> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		log.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtAccnInqReqDocs dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));

			String selectClause = "from TCkCtAccnInqReqDocs o ";
			String orderByClause = formatOrderByObj(filterRequest.getOrderBy()).toString();
			List<TCkCtAccnInqReqDocs> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			List<CkCtAccnInqReqDocs> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x);
				} catch (ParameterException | ProcessingException e) {
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
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		return log;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected TCkCtAccnInqReqDocs initEnity(TCkCtAccnInqReqDocs entity) throws ParameterException, ProcessingException {
		if (null != entity) {
			Hibernate.initialize(entity.getTCkCtAccnInqReq());
			Hibernate.initialize(entity.getTMstAttType());
		}

		return entity;
	}

	@Override
	protected TCkCtAccnInqReqDocs entityFromDTO(CkCtAccnInqReqDocs dto) throws ParameterException, ProcessingException {
		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtAccnInqReqDocs entity = new TCkCtAccnInqReqDocs();
			entity = dto.toEntity(entity);

			Optional<CkCtAccnInqReq> opAccnInqReq = Optional.ofNullable(dto.getTCkCtAccnInqReq());
			entity.setTCkCtAccnInqReq(
					opAccnInqReq.isPresent() ? opAccnInqReq.get().toEntity(new TCkCtAccnInqReq()) : null);

			Optional<MstAttType> opMstAttType = Optional.ofNullable(dto.getTMstAttType());
			entity.setTMstAttType(opMstAttType.isPresent() ? opMstAttType.get().toEntity(new TMstAttType()) : null);

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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtAccnInqReqDocs dtoFromEntity(TCkCtAccnInqReqDocs entity)
			throws ParameterException, ProcessingException {
		log.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtAccnInqReqDocs dto = new CkCtAccnInqReqDocs(entity);

			Optional<TCkCtAccnInqReq> opAccnInqReq = Optional.ofNullable(entity.getTCkCtAccnInqReq());

			if (opAccnInqReq.isPresent())
				dto.setTCkCtAccnInqReq(new CkCtAccnInqReq(opAccnInqReq.get()));

			Optional<TMstAttType> opMstAttType = Optional.ofNullable(entity.getTMstAttType());
			dto.setTMstAttType(new MstAttType(opMstAttType.get()));

			return dto;
		} catch (ParameterException ex) {
			log.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	protected CkCtAccnInqReqDocs dtoFromEntity(TCkCtAccnInqReqDocs entity, boolean isWithData)
			throws ParameterException, ProcessingException {
		log.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			CkCtAccnInqReqDocs dto = new CkCtAccnInqReqDocs(entity);

			Optional<TCkCtAccnInqReq> opAccnInqReq = Optional.ofNullable(entity.getTCkCtAccnInqReq());

			if (opAccnInqReq.isPresent())
				dto.setTCkCtAccnInqReq(new CkCtAccnInqReq(opAccnInqReq.get()));

			Optional<TMstAttType> opMstAttType = Optional.ofNullable(entity.getTMstAttType());
			dto.setTMstAttType(new MstAttType(opMstAttType.get()));

			if (isWithData) {
				if (!StringUtils.isBlank(dto.getAirdDoc())) {
					File file = new File(dto.getAirdDoc());
					dto.setData(Files.readAllBytes(file.toPath()));
				}
			}

			return dto;
		} catch (ParameterException ex) {
			log.error("entityFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("dtoFromEntity", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected String entityKeyFromDTO(CkCtAccnInqReqDocs dto) throws ParameterException, ProcessingException {
		log.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return dto.getAirdId();
		} catch (ParameterException ex) {
			log.error("entityKeyFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("entityKeyFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCkCtAccnInqReqDocs updateEntity(ACTION attriubte, TCkCtAccnInqReqDocs entity, Principal principal,
			Date date) throws ParameterException, ProcessingException {
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
				entity.setAirdUidCreate(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAirdDtCreate(date);
				entity.setAirdUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAirdDtLupd(date);
				break;

			case MODIFY:
				entity.setAirdUidLupd(opUserId.isPresent() ? opUserId.get() : "SYS");
				entity.setAirdDtLupd(date);
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
	protected TCkCtAccnInqReqDocs updateEntityStatus(TCkCtAccnInqReqDocs entity, char status)
			throws ParameterException, ProcessingException {
		log.debug("updateEntityStatus");

		try {
			if (null == entity)
				throw new ParameterException("entity param null");

			entity.setAirdStatus(status);
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
	protected CkCtAccnInqReqDocs preSaveUpdateDTO(TCkCtAccnInqReqDocs storedEntity, CkCtAccnInqReqDocs dto)
			throws ParameterException, ProcessingException {
		log.debug("preSaveUpdateDTO");

		try {
			if (null == storedEntity)
				throw new ParameterException("param storedEntity null");
			if (null == dto)
				throw new ParameterException("param dto null");

			dto.setAirdUidCreate(storedEntity.getAirdUidCreate());
			dto.setAirdDtCreate(storedEntity.getAirdDtCreate());

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
	protected void preSaveValidation(CkCtAccnInqReqDocs dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtAccnInqReqDocs dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtAccnInqReqDocs dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		log.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			StringBuffer searchStatement = new StringBuffer();
			if (StringUtils.isNotBlank(dto.getAirdId())) {
				searchStatement.append(getOperator(wherePrinted) + "o.airdId LIKE :airdId");
				wherePrinted = true;
			}

			if (StringUtils.isNotBlank(dto.getAirdFilename())) {
				searchStatement.append(getOperator(wherePrinted) + "o.airdFilename LIKE :airdFilename");
				wherePrinted = true;
			}

			Optional<MstAttType> opMstAttType = Optional.ofNullable(dto.getTMstAttType());
			if (opMstAttType.isPresent() && StringUtils.isNotBlank(opMstAttType.get().getMattName())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TMstAttType.mattName LIKE :svcAttTypeName");
				wherePrinted = true;
			}

			if (dto.getAirdStatus() != null && Character.isAlphabetic(dto.getAirdStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.airdStatus = :airdStatus");
				wherePrinted = true;
			}

			Optional<CkCtAccnInqReq> opAccnInqReq = Optional.ofNullable(dto.getTCkCtAccnInqReq());
			if (opAccnInqReq.isPresent() && StringUtils.isNotBlank(opAccnInqReq.get().getAirId())) {
				searchStatement.append(getOperator(wherePrinted) + "o.TCkCtAccnInqReq.airId = :airId");
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
	protected HashMap<String, Object> getParameters(CkCtAccnInqReqDocs dto)
			throws ParameterException, ProcessingException {
		log.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			if (StringUtils.isNotBlank(dto.getAirdId()))
				parameters.put("airdId", "%" + dto.getAirdId() + "%");

			if (StringUtils.isNotBlank(dto.getAirdFilename()))
				parameters.put("airdFilename", "%" + dto.getAirdFilename() + "%");

			Optional<MstAttType> opMstAttType = Optional.ofNullable(dto.getTMstAttType());
			if (opMstAttType.isPresent() && StringUtils.isNotBlank(dto.getTMstAttType().getMattName())) {
				parameters.put("svcAttTypeName", "%" + dto.getTMstAttType().getMattName() + "%");
			}

			Optional<CkCtAccnInqReq> opAccnInqReq = Optional.ofNullable(dto.getTCkCtAccnInqReq());
			if (opAccnInqReq.isPresent() && StringUtils.isNotBlank(opAccnInqReq.get().getAirId())) {
				parameters.put("airId", opAccnInqReq.get().getAirId());
			}

			if (dto.getAirdStatus() != null && Character.isAlphabetic(dto.getAirdStatus()))
				parameters.put("airdStatus", dto.getAirdStatus());

			return parameters;
		} catch (ParameterException ex) {
			log.error("getParameters", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("getParameters", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CkCtAccnInqReqDocs whereDto(EntityFilterRequest filterRequest)
			throws ParameterException, ProcessingException {
		log.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtAccnInqReqDocs dto = new CkCtAccnInqReqDocs();
			CkCtAccnInqReq accnInqReq = new CkCtAccnInqReq();
			MstAttType mstAttType = new MstAttType();

			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if (entityWhere.getAttribute().equalsIgnoreCase("airdId"))
					dto.setAirdId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("airdFilename"))
					dto.setAirdFilename(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TMstAttType.mattName"))
					mstAttType.setMattName(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("TCkCtAccnInqReq.airId"))
					accnInqReq.setAirId(opValue.get());

				if (entityWhere.getAttribute().equalsIgnoreCase("airdStatus"))
					dto.setAirdStatus(opValue.get().charAt(0));

			}

			dto.setTMstAttType(mstAttType);
			dto.setTCkCtAccnInqReq(accnInqReq);
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
	protected CoreMstLocale getCoreMstLocale(CkCtAccnInqReqDocs dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtAccnInqReqDocs setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtAccnInqReqDocs dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	//// Private Methods
	/////////////////////

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtAccnInqReqDocs createAttachment(CkCtAccnInqReqDocs accnReqDocs, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		log.debug("createAttachment");

		if (null == accnReqDocs)
			throw new ParameterException("param accnReqDocs null");
		if (null == principal)
			throw new ParameterException("principal null");

		Optional<String> opAccnInqReqId = Optional.ofNullable(accnReqDocs.getTCkCtAccnInqReq().getAirId());
		if (!opAccnInqReqId.isPresent())
			throw new ProcessingException("opAccnInqReqId null or empty");

		TCkCtAccnInqReq accnInqReqE = ckCtAccnInqReqDao.find(opAccnInqReqId.get());
		if (null == accnInqReqE)
			throw new ProcessingException("accn inquiry request not found: " + opAccnInqReqId.get());

		TCkCtAccnInqReqDocs doc = new TCkCtAccnInqReqDocs();

		BeanUtils.copyProperties(accnReqDocs, doc, COBeansUtil.getNullPropertyNames(accnReqDocs));
		doc.setTCkCtAccnInqReq(accnInqReqE);

		Optional<String> opMstAttachTypeId = Optional.ofNullable(accnReqDocs.getTMstAttType().getMattId());
		if (!opMstAttachTypeId.isPresent())
			throw new ProcessingException("jobAttach.TMstAttType.mattId not found");
		if (StringUtils.isEmpty(opMstAttachTypeId.get()))
			throw new ProcessingException("jobAttach.TMstAttType.mattId null or empty");

		HashMap<String, Object> hmMstAttachTypes = clicMasterRecrodsSvc.getRecords("mstAttachType");
		if (!hmMstAttachTypes.containsKey(opMstAttachTypeId.get()))
			throw new ProcessingException("attachment master type not found: " + opMstAttachTypeId.get());

		doc.setTMstAttType((TMstAttType) hmMstAttachTypes.get((opMstAttachTypeId.get())));

		String fileLocation = this.saveAttachment(accnReqDocs.getAirdFilename(), accnReqDocs.getData());
		if (StringUtils.isNotBlank(fileLocation))
			doc.setAirdDoc(fileLocation);

		this.dao.saveOrUpdate(doc);

		accnReqDocs.setAirdDoc(fileLocation);
		return accnReqDocs;

	};


	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");

		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}

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
		newAttr = newAttr.replaceAll("tmstAttType", "TMstAttType").replaceAll("tckCtAccnInqReq", "TCkCtAccnInqReq");
		orderBy.setAttribute(newAttr);
		return orderBy;
	}

}
