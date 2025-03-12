package com.guudint.clickargo.clictruck.admin.account.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.account.dto.AccountOpm;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnOpmDao;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAddress;
import com.vcc.camelone.ccm.dto.CoreContact;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.model.embed.TCoreAddress;
import com.vcc.camelone.ccm.model.embed.TCoreContact;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.master.dto.MstAccnType;
import com.vcc.camelone.master.dto.MstCountry;
import com.vcc.camelone.master.model.TMstAccnType;
import com.vcc.camelone.master.model.TMstCountry;

/**
 * This is an extension of the AccnService from clibr. Implementation for
 * clictruck specific account processes.
 */
public class CkAccountExtOpmServiceImpl extends AbstractClickCargoEntityService<TCoreAccn, String, AccountOpm> {

	private static Logger log = Logger.getLogger(CkAccountExtOpmServiceImpl.class);
	private static String auditTag = "ACCN";
	private static String tableName = "T_CORE_ACCN";

	@Autowired
	private CkAccnOpmDao accnOpmDao;

	public CkAccountExtOpmServiceImpl() {
		super("coreAccDao", auditTag, TCoreAccn.class.getName(), tableName);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<AccountOpm> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {

		log.debug("filterBy");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			AccountOpm dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("whereDto null");

			filterRequest.setTotalRecords(super.countByAnd(dto));

			String selectClause = "from TCoreAccn o ";
			String orderByClause = filterRequest.getOrderBy().toString();
			List<TCoreAccn> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());

			List<AccountOpm> dtos = entities.stream().map(x -> {
				try {
					return dtoFromEntity(x);
				} catch (ParameterException e) {
					log.error("filterBy", e);
				} catch (ProcessingException e) {
					log.error("filterBy", e);
				}
				return null;
			}).collect(Collectors.toList());

			List<AccountOpm> filteredList = new ArrayList<>();
			for (AccountOpm accn : dtos) {
				boolean isOpm = accnOpmDao.findByAccnId(accn.getAccnId(), RecordStatus.ACTIVE.getCode()) == null ? false
						: true;
				if (isOpm) {
					filteredList.add(accn);
				}
			}

			return filteredList;
		} catch (ParameterException | ProcessingException ex) {
			log.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("filterBy", ex);
			throw new ProcessingException(ex);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public AccountOpm findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {

		// TODO Auto-generated method stub
		log.debug("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCoreAccn entity = dao.find(id);
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
	public AccountOpm newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountOpm deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		// TODO Auto-generated method stub
		return null;
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
	protected TCoreAccn initEnity(TCoreAccn entity) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		if (null != entity) {
			Hibernate.initialize(entity.getTMstAccnType());
			Hibernate.initialize(entity.getAccnAddr());
			Hibernate.initialize(entity.getAccnContact());
		}
		return entity;
	}

	@Override
	protected TCoreAccn entityFromDTO(AccountOpm dto) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("entityFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCoreAccn entity = new TCoreAccn();
			entity = dto.toEntity(entity);
			// no deep copy with BeansUtil
			Optional<MstAccnType> opAccnType = Optional.ofNullable(dto.getTMstAccnType());
			entity.setTMstAccnType(opAccnType.isPresent() ? opAccnType.get().toEntity(new TMstAccnType()) : null);

			Optional<CoreAddress> opCoreAddr = Optional.ofNullable(dto.getAccnAddr());
			entity.setAccnAddr(opCoreAddr.isPresent() ? opCoreAddr.get().toEntity(new TCoreAddress()) : null);

			if (opCoreAddr.isPresent()) {
				Optional<MstCountry> opMstAddr = Optional.ofNullable(dto.getAccnAddr().getAddrCtry());
				entity.getAccnAddr()
						.setAddrCtry(opMstAddr.isPresent() ? opMstAddr.get().toEntity(new TMstCountry()) : null);
			}

			Optional<CoreContact> opCoreContact = Optional.ofNullable(dto.getAccnContact());
			entity.setAccnContact(opCoreContact.isPresent() ? opCoreContact.get().toEntity(new TCoreContact()) : null);

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
	protected AccountOpm dtoFromEntity(TCoreAccn entity) throws ParameterException, ProcessingException {
		log.debug("dtoFromEntity");

		try {
			if (null == entity)
				throw new ParameterException("param entity null");

			AccountOpm dto = new AccountOpm(entity);
			// no deep copy with BeansUtil
			Optional<TMstAccnType> opAccnType = Optional.ofNullable(entity.getTMstAccnType());
			dto.setTMstAccnType(opAccnType.isPresent() ? new MstAccnType(opAccnType.get()) : null);
			Optional<TCoreAddress> opCoreAddr = Optional.ofNullable(entity.getAccnAddr());
			dto.setAccnAddr(opCoreAddr.isPresent() ? new CoreAddress(opCoreAddr.get()) : null);

			if (opCoreAddr.isPresent()) {
				Optional<TMstCountry> opsCtryAddr = Optional.ofNullable(entity.getAccnAddr().getAddrCtry());
				dto.getAccnAddr().setAddrCtry(opsCtryAddr.isPresent() ? new MstCountry(opsCtryAddr.get()) : null);
			}

			Optional<TCoreContact> opCoreContact = Optional.ofNullable(entity.getAccnContact());
			dto.setAccnContact(opCoreContact.isPresent() ? new CoreContact(opCoreContact.get()) : null);

			boolean isOpm = accnOpmDao.findByAccnId(dto.getAccnId(), RecordStatus.ACTIVE.getCode()) == null ? false
					: true;

			dto.setAccountOpm(isOpm);
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
	protected String entityKeyFromDTO(AccountOpm dto) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("entityKeyFromDTO");

		try {
			if (null == dto)
				throw new ParameterException("dto param null");

			return dto.getAccnId();
		} catch (ParameterException ex) {
			log.error("entityKeyFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("entityKeyFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected TCoreAccn updateEntity(ACTION attriubte, TCoreAccn entity, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCoreAccn updateEntityStatus(TCoreAccn entity, char status)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AccountOpm preSaveUpdateDTO(TCoreAccn storedEntity, AccountOpm dto)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void preSaveValidation(AccountOpm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(AccountOpm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(AccountOpm dto, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("getWhereClause");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			StringBuffer searchStatement = new StringBuffer();

			if (!StringUtils.isEmpty(dto.getAccnId())) {
				searchStatement.append(getOperator(wherePrinted) + "o.accnId LIKE :accnId");
				wherePrinted = true;
			}
			if (!StringUtils.isEmpty(dto.getAccnName())) {
				searchStatement.append(getOperator(wherePrinted) + "o.accnName LIKE :accnName");
				wherePrinted = true;
			}
			Optional<MstAccnType> opMstAccnType = Optional.ofNullable(dto.getTMstAccnType());
			if (opMstAccnType.isPresent() && !StringUtils.isEmpty(opMstAccnType.get().getAtypDescription())) {
				searchStatement
						.append(getOperator(wherePrinted) + "o.TMstAccnType.atypDescription LIKE :atypDescription");
				wherePrinted = true;
			}

			if (opMstAccnType.isPresent() && !StringUtils.isEmpty(opMstAccnType.get().getAtypId())) {
				if (opMstAccnType.get().getAtypId().contains(",")) {
					searchStatement.append(getOperator(wherePrinted) + "o.TMstAccnType.atypId in (:atypId)");
				} else {
					searchStatement.append(getOperator(wherePrinted) + "o.TMstAccnType.atypId LIKE :atypId");
				}
				wherePrinted = true;
			}

			if (Character.isAlphabetic(dto.getAccnStatus())) {
				searchStatement.append(getOperator(wherePrinted) + "o.accnStatus = :accnStatus");
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
	protected HashMap<String, Object> getParameters(AccountOpm dto) throws ParameterException, ProcessingException {
		log.debug("getParameters");

		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			if (!StringUtils.isEmpty(dto.getAccnId()))
				parameters.put("accnId", "%" + dto.getAccnId() + "%");

			if (!StringUtils.isEmpty(dto.getAccnName()))
				parameters.put("accnName", "%" + dto.getAccnName() + "%");

			Optional<MstAccnType> opMstAccnType = Optional.ofNullable(dto.getTMstAccnType());
			if (opMstAccnType.isPresent() && !StringUtils.isEmpty(opMstAccnType.get().getAtypDescription()))
				parameters.put("atypDescription", "%" + (opMstAccnType.get().getAtypDescription()) + "%");
			if (opMstAccnType.isPresent() && !StringUtils.isEmpty(opMstAccnType.get().getAtypId())) {
				if (opMstAccnType.get().getAtypId().contains(",")) {
					List<String> convertedList = Stream.of(opMstAccnType.get().getAtypId().split(",", -1))
							.collect(Collectors.toList());
					parameters.put("atypId", convertedList);
				} else {
					parameters.put("atypId", "%" + (opMstAccnType.get().getAtypId()) + "%");
				}
			}

			if (Character.isAlphabetic(dto.getAccnStatus())) {
				parameters.put("accnStatus", dto.getAccnStatus());
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

	@Override
	protected AccountOpm whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		log.debug("whereDto");

		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			AccountOpm dto = new AccountOpm();
			MstAccnType mstAcnnType = new MstAccnType();
			dto.setTMstAccnType(mstAcnnType);
			for (EntityWhere entityWhere : filterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;
				if (entityWhere.getAttribute().equalsIgnoreCase("accnId"))
					dto.setAccnId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("accnName"))
					dto.setAccnName(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("atypDescription"))
					mstAcnnType.setAtypDescription(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("TMstAccnType.atypId"))
					mstAcnnType.setAtypId(opValue.get());
				if (entityWhere.getAttribute().equalsIgnoreCase("accnStatus"))
					dto.setAccnStatus(opValue.get().charAt(0));
			}

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
	protected CoreMstLocale getCoreMstLocale(AccountOpm dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AccountOpm setCoreMstLocale(CoreMstLocale coreMstLocale, AccountOpm dto)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

}
