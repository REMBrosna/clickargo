package com.guudint.clickargo.clictruck.admin.shell.service.impl;

import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellCardTruck;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCardTruck;
import com.guudint.clickargo.clictruck.admin.shell.service.CkCtShellCardTruckService;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
import com.guudint.clickargo.clictruck.planexec.job.event.TruckJobStateChangeEvent;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.common.service.impl.CkNotificationUtilService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.can.device.NotificationParam;
import com.vcc.camelone.can.service.impl.NotificationService;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.*;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.core.model.TCoreApps;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.util.email.SysParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.Email;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CkCtShellCardTruckServiceImpl extends AbstractClickCargoEntityService<TCkCtShellCardTruck, String, CkCtShellCardTruck> implements ICkConstant, CkCtShellCardTruckService {

	private static final Logger LOG = Logger.getLogger(CkCtShellCardTruckServiceImpl.class);
	private static final String AUDIT = "SHELL_CARD_TRUCK";
	private static final String TABLE = "T_CK_CT_SHELL_CARD_TRUCK";
	private static final String PREFIX = "SCT";

	public static char STATUS_ACTIVE = 'A';
	public static char STATUS_INACTIVE = 'I';
	public static char STATUS_EXPIRE = 'E';
	public static char STATUS_TERMINATE = 'T';

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	public CkCtShellCardTruckServiceImpl() {
		super("ckCtShellCardTruckDao", AUDIT, TCkCtShellCardTruck.class.getName(), TABLE);
	}

	@Override
	public CkCtShellCardTruck updateStatus(String id, String status) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");

		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("Principal is null");
		}

		CkCtShellCardTruck ckCtShellCardTruck = findById(id);
		if (ckCtShellCardTruck == null) {
			throw new EntityNotFoundException("ID::" + id);
		}

		char ctStatus = status.charAt(0);
		if (ctStatus == STATUS_INACTIVE || ctStatus == STATUS_EXPIRE || ctStatus == STATUS_ACTIVE || ctStatus == STATUS_TERMINATE) {
			ckCtShellCardTruck.setCtStatus(ctStatus);
		}

		update(ckCtShellCardTruck, principal);
		return ckCtShellCardTruck;
	}

	@Override
	protected void initBusinessValidator() {

	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public CkCtShellCardTruck newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtShellCardTruck initEnity(TCkCtShellCardTruck entity) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtShellCardTruck entityFromDTO(CkCtShellCardTruck ckCtShellCardTruck) throws ParameterException, ProcessingException {
		LOG.info("entityFromDTO: CkCtShellCardTruck");
		if (ckCtShellCardTruck == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtShellCardTruck entity = new TCkCtShellCardTruck();
		TCoreAccn tCoreAccn = new TCoreAccn();
		TCkCtVeh tCkCtVeh = new TCkCtVeh();
		TCkCtShellCard tCkCtShellCard = new TCkCtShellCard();
		if (Objects.nonNull(ckCtShellCardTruck.getTCoreAccn()) && Objects.nonNull(ckCtShellCardTruck.getTCoreAccn().getAccnId())) {
			tCoreAccn.setAccnId(ckCtShellCardTruck.getTCoreAccn().getAccnId());
			entity.setTCoreAccn(tCoreAccn);
		}
		if (Objects.nonNull(ckCtShellCardTruck.getTCkCtVeh()) && Objects.nonNull(ckCtShellCardTruck.getTCkCtVeh().getVhId())) {
			tCkCtVeh.setVhId(ckCtShellCardTruck.getTCkCtVeh().getVhId());
			entity.setTCkCtVeh(tCkCtVeh);
		}
		if (Objects.nonNull(ckCtShellCardTruck.getTCkCtShellCard()) && Objects.nonNull(ckCtShellCardTruck.getTCkCtShellCard().getScId())) {
			tCkCtShellCard.setScId(ckCtShellCardTruck.getTCkCtShellCard().getScId());
			entity.setTCkCtShellCard(tCkCtShellCard);
		}
		BeanUtils.copyProperties(ckCtShellCardTruck, entity);
		return entity;
	}

	@Override
	@Transactional
	protected CkCtShellCardTruck dtoFromEntity(TCkCtShellCardTruck entity) throws ParameterException, ProcessingException {
		LOG.info("dtoFromEntity: TCkCtShellCardTruck");
		if (entity == null) {
			throw new ParameterException("param entity null");
		}
		CkCtShellCardTruck dto = new CkCtShellCardTruck();
		TCoreAccn tCoreAccn = new TCoreAccn();
		TCkCtVeh tCkCtVeh = new TCkCtVeh();
		CkCtShellCard ckCtShellCard = new CkCtShellCard();
		TCkCtMstVehType tCkCtMstVehType = new TCkCtMstVehType();
		if (Objects.nonNull(entity.getTCoreAccn()) && Objects.nonNull(entity.getTCoreAccn().getAccnId())) {
			tCoreAccn.setAccnId(entity.getTCoreAccn().getAccnId());
			tCoreAccn.setAccnName(entity.getTCoreAccn().getAccnName());
			dto.setTCoreAccn(tCoreAccn);
		}
		if (Objects.nonNull(entity.getTCkCtVeh()) && Objects.nonNull(entity.getTCkCtVeh().getVhId())) {
			tCkCtVeh.setVhId(entity.getTCkCtVeh().getVhId());
			tCkCtVeh.setVhPlateNo(entity.getTCkCtVeh().getVhPlateNo());
			tCkCtMstVehType.setVhtyId(Objects.nonNull(entity.getTCkCtVeh().getTCkCtMstVehType()) ? entity.getTCkCtVeh().getTCkCtMstVehType().getVhtyId() : "");
			tCkCtMstVehType.setVhtyName(Objects.nonNull(entity.getTCkCtVeh().getTCkCtMstVehType()) ? entity.getTCkCtVeh().getTCkCtMstVehType().getVhtyName(): "");
			tCkCtVeh.setTCkCtMstVehType(tCkCtMstVehType);
			dto.setTCkCtVeh(tCkCtVeh);
		}
		if (Objects.nonNull(entity.getTCkCtShellCard()) && Objects.nonNull(entity.getTCkCtShellCard().getScId())) {
			BeanUtils.copyProperties(entity.getTCkCtShellCard(), ckCtShellCard);
			dto.setTCkCtShellCard(ckCtShellCard);
		}

		dto.setCtId(entity.getCtId());
		dto.setCtUidCreate(entity.getCtUidCreate());
		dto.setCtDtCreate(entity.getCtDtCreate());
		dto.setCtDtLupd(entity.getCtDtLupd());
		dto.setCtUidLupd(entity.getCtUidLupd());
		dto.setCtStatus(entity.getCtStatus());

  		return dto;
	}

	@Override
	protected String entityKeyFromDTO(CkCtShellCardTruck ckCtShellCardTruck) throws ParameterException, ProcessingException {
		LOG.info("entityKeyFromDTO");
		if (ckCtShellCardTruck == null) {
			throw new ParameterException("param dto null");
		}
		return ckCtShellCardTruck.getCtId();
	}

	@Override
	protected TCkCtShellCardTruck updateEntity(ACTION action, TCkCtShellCardTruck entity, Principal principal, Date date) throws ParameterException, ProcessingException {
		if (entity == null) {
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
				entity.setCtId(CkUtil.generateId(PREFIX));
				entity.setCtUidCreate(opUserId.orElse(Constant.DEFAULT_USR));
				entity.setCtDtCreate(date);
				entity.setCtDtLupd(date);
				entity.setCtUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
				entity.setCtStatus(STATUS_ACTIVE);
				break;

			case MODIFY:
				entity.setCtDtLupd(date);
				entity.setCtUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
				break;
			default:
				break;
		}
		return entity;
	}

	@Override
	protected TCkCtShellCardTruck updateEntityStatus(TCkCtShellCardTruck entity, char status) throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (entity == null)
			throw new ParameterException("entity param null");

		entity.setCtStatus(status);
		return entity;
	}

	@Override
	protected CkCtShellCardTruck preSaveUpdateDTO(TCkCtShellCardTruck entity, CkCtShellCardTruck ckCtShellCardTruck) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected void preSaveValidation(CkCtShellCardTruck ckCtShellCardTruck, Principal principal) throws ParameterException, ProcessingException {

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtShellCardTruck ckCtShellCardTruck, Principal principal) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected String getWhereClause(CkCtShellCardTruck dto, boolean wherePrinted) throws ParameterException, ProcessingException {
		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			String EQUAL = " = :", CONTAIN = " like :";
			StringBuilder searchStatement = new StringBuilder();
			if (!StringUtils.isEmpty(dto.getCtId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.ctId LIKE :ctId");
				wherePrinted = true;
			}

			if (dto.getCtStatus() != null) {
				searchStatement.append(getOperator(wherePrinted)).append("o.ctStatus").append(CONTAIN).append("ctStatus");
				wherePrinted = true;
			}

			searchStatement.append(getOperator(wherePrinted)).append("o.ctStatus").append(" IN :validStatus");


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
	protected HashMap<String, Object> getParameters(CkCtShellCardTruck dto) throws ParameterException, ProcessingException {
		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			if (!StringUtils.isEmpty(dto.getCtId())){
				parameters.put("ctId", "%" + dto.getCtId() + "%");
			}
			if (dto.getCtStatus() != null) {
				parameters.put("ctStatus", dto.getCtStatus());
				parameters.put("validStatus", dto.getCtStatus());
			} else if (dto.getHistory() != null) {
				if (dto.getHistory().equalsIgnoreCase("default")) {
					parameters.put("validStatus", Arrays.asList(STATUS_ACTIVE));
				} else if (dto.getHistory().equalsIgnoreCase("history")) {
					parameters.put("validStatus", Arrays.asList(STATUS_EXPIRE, STATUS_INACTIVE, STATUS_TERMINATE));
				}
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
	protected CkCtShellCardTruck whereDto(EntityFilterRequest entityFilterRequest) throws ParameterException, ProcessingException {
		LOG.info("whereDto");
		if (entityFilterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		try {

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
			CkCtShellCardTruck dto = new CkCtShellCardTruck();
			for (EntityWhere entityWhere : entityFilterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if ("ctId".equalsIgnoreCase(entityWhere.getAttribute())) {
					dto.setCtId(opValue.get());
				}

				if ("ctStatus".equalsIgnoreCase(entityWhere.getAttribute())) {
					dto.setCtStatus(opValue.get().charAt(0));
				}

				if ("history".equalsIgnoreCase(entityWhere.getAttribute())) {
					dto.setHistory(opValue.get());
				}
			}
			return dto;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtShellCardTruck ckCtShellCardTruck) throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckCtShellCardTruck == null) {
			throw new ParameterException("dto param null");
		}
		if (ckCtShellCardTruck.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return ckCtShellCardTruck.getCoreMstLocale();
	}

	@Override
	protected CkCtShellCardTruck setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtShellCardTruck ckCtShellCardTruck) throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	public CkCtShellCardTruck findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtShellCardTruck entity = dao.find(id);
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
	public CkCtShellCardTruck deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		return null;
	}

	@Override
	public List<CkCtShellCardTruck> filterBy(EntityFilterRequest filterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			List<TCkCtShellCardTruck> assignedTrucks = this.getExpiredCardAssigned();
			if (assignedTrucks != null && !assignedTrucks.isEmpty()) {
				for (TCkCtShellCardTruck truck : assignedTrucks) {
					if (truck != null && truck.getTCkCtShellCard() != null && truck.getTCkCtShellCard().getScId() != null) {
						if (this.isExpired(truck.getTCkCtShellCard().getScDtExpiry())) {
							this.updateStatus(truck.getCtId(), "E");
						}
					}
				}
			}

			CkCtShellCardTruck dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("dto from filter is null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "FROM TCkCtShellCardTruck o";

			String orderByClause = filterRequest.getOrderBy().toString();
			List<TCkCtShellCardTruck> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());

			return entities.stream().map((x) -> {
				try {
					return this.dtoFromEntity(x);
				} catch (ParameterException | ProcessingException var3) {
					LOG.error("filterBy", var3);
				}
				return null;
			}).collect(Collectors.toList());

		} catch (ParameterException | ProcessingException ex) {
			LOG.error("filterBy", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("filterBy", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	public List<String> getAllAssignedCards() throws EntityNotFoundException  {
		LOG.info("getAllAssignedCards");
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("status", 'A');
			String hql = "FROM TCkCtShellCardTruck o WHERE o.ctStatus=:status";
			List<TCkCtShellCardTruck> entity = dao.getByQuery(hql, parameters);
			if(Objects.nonNull(entity) && !entity.isEmpty()){
				return entity.stream()
						.filter(Objects::nonNull)
						.map(val -> val.getTCkCtShellCard().getScId())
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("getAllAssignedCards", e);
		}
		return null;
	}

	@Override
	public List<String> getAllAssignedTrucks() throws EntityNotFoundException {
		LOG.info("getAllAssignedTrucks");
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("status", 'A');
			String hql = "FROM TCkCtShellCardTruck o WHERE o.ctStatus=:status";
			List<TCkCtShellCardTruck> entity = dao.getByQuery(hql, parameters);
			if(Objects.nonNull(entity) && !entity.isEmpty()){
				return entity.stream()
						.filter(Objects::nonNull)
						.map(val -> val.getTCkCtVeh().getVhId())
						.filter(Objects::nonNull)
						.collect(Collectors.toList());
			}
		} catch (Exception e) {
			LOG.error("getAllAssignedTrucks", e);
		}
		return null;
	}

	public TCkCtShellCardTruck findByCard(String id, String accnId) throws ParameterException, EntityNotFoundException {
		LOG.info("findById");
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("status", 'A');
			parameters.put("scId", id);
			parameters.put("accnId", accnId);
			String hql = "FROM TCkCtShellCardTruck o WHERE o.TCkCtShellCard.scId=:scId AND o.TCoreAccn.accnId=:accnId  AND o.ctStatus=:status";
			List<TCkCtShellCardTruck> entity = dao.getByQuery(hql, parameters);
			if (Objects.nonNull(entity) && !entity.isEmpty()){
				return entity.get(0);
			}
		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("findById", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("findById", ex);
		}
		return null;
	}

	public TCkCtShellCardTruck findByCard(String id) throws ParameterException, EntityNotFoundException {
		LOG.info("findById");
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("status", 'A');
			parameters.put("scId", id);
			String hql = "FROM TCkCtShellCardTruck o WHERE o.TCkCtShellCard.scId=:scId AND o.ctStatus=:status";
			List<TCkCtShellCardTruck> entity = dao.getByQuery(hql, parameters);
			if (Objects.nonNull(entity) && !entity.isEmpty()){
				return entity.get(0);
			}
		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("findById", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("findById", ex);
		}
		return null;
	}

	public List<TCkCtShellCardTruck> getExpiredCardAssigned() throws ParameterException, EntityNotFoundException {
		LOG.info("getExpiredCardAssigned");
		try {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("status", 'E');
			String hql = "FROM TCkCtShellCardTruck o WHERE o.ctStatus != :status";
			List<TCkCtShellCardTruck> entity = dao.getByQuery(hql, parameters);
			if (Objects.nonNull(entity) && !entity.isEmpty()){
				return entity;
			}
		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("getExpiredCardAssigned", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getExpiredCardAssigned", ex);
		}
		return null;
	}

	public boolean isExpired(Date expireData) {
		if (expireData == null) {
			throw new IllegalArgumentException("Expiration date is not set.");
		}
		Date currentDate = new Date();
		return expireData.before(currentDate);
	}
}
