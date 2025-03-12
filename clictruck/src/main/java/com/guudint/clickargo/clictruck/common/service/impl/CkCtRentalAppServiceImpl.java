package com.guudint.clickargo.clictruck.common.service.impl;

import com.guudint.clickargo.clictruck.common.dto.*;
import com.guudint.clickargo.clictruck.common.model.TCkCtRentalApp;
import com.guudint.clickargo.clictruck.common.service.CkCtRentalAppService;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.planexec.job.event.ClicTruckNotifTemplates;
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
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CkCtRentalAppServiceImpl extends AbstractClickCargoEntityService<TCkCtRentalApp, String, CkCtRentalApp> implements ICkConstant, CkCtRentalAppService {

	private static final Logger LOG = Logger.getLogger(CkCtRentalAppServiceImpl.class);
	private static final String AUDIT = "RENTAL_APP";
	private static final String TABLE = "T_CK_CT_RENTAL_APP";

	public static char STATUS_NEW = 'N';
	public static char STATUS_S_APPROVE = 'S';
	public static char STATUS_R_REJECT = 'R';

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	protected CkNotificationUtilService notificationUtilService;

	@Autowired
	protected NotificationService notificationService;

	public CkCtRentalAppServiceImpl() {
		super("ckCtRentalAppDao", AUDIT, TCkCtRentalApp.class.getName(), TABLE);
	}

	@Override
	public CkCtRentalApp updateStatus(String id, String status) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");

		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("Principal is null");
		}

		CkCtRentalApp ckCtRentalApp = findById(id);
		if (ckCtRentalApp == null) {
			throw new EntityNotFoundException("ID::" + id);
		}

		char vrStatus = status.charAt(0);
		if (vrStatus == 'S' || vrStatus == 'R') {
			ckCtRentalApp.setVrStatus(vrStatus);
		}

		update(ckCtRentalApp, principal);
		return ckCtRentalApp;
	}

	@Override
	protected void initBusinessValidator() {

	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	public CkCtRentalApp newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtRentalApp initEnity(TCkCtRentalApp tCkCtRentalApp) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtRentalApp entityFromDTO(CkCtRentalApp ckCtRentalApp) throws ParameterException, ProcessingException {
		LOG.info("entityFromDTO: CkCtRentalApp");
		if (ckCtRentalApp == null) {
			throw new ParameterException("param dto null");
		}
		TCkCtRentalApp rentalAppEntity = new TCkCtRentalApp();
		TCoreAccn tCoreAccn = new TCoreAccn();
		if (ckCtRentalApp.getAccn() != null) {
			tCoreAccn.setAccnId(ckCtRentalApp.getAccn());
			rentalAppEntity.setVrAccn(tCoreAccn);
			rentalAppEntity.setVrUidCreate(ckCtRentalApp.getAccn());
		}
		rentalAppEntity.setVrCtEmail(ckCtRentalApp.getEmail() != null ? ckCtRentalApp.getEmail() : "");
		rentalAppEntity.setVrCtMobile(ckCtRentalApp.getContact() != null ? ckCtRentalApp.getContact() : "");
		rentalAppEntity.setVrLease(ckCtRentalApp.getLease() != null ? ckCtRentalApp.getLease() : 0);
		rentalAppEntity.setVrQty(ckCtRentalApp.getQuantity() != null ? ckCtRentalApp.getQuantity() : 0);
		rentalAppEntity.setVrPrice(ckCtRentalApp.getPrice() != null ? ckCtRentalApp.getPrice() : BigDecimal.ZERO);
		rentalAppEntity.setVrCtName(ckCtRentalApp.getName() != null ? ckCtRentalApp.getName() : "");
		rentalAppEntity.setVrTruck(ckCtRentalApp.getTruck() != null ? ckCtRentalApp.getTruck() : "");
		rentalAppEntity.setVrProvider(ckCtRentalApp.getProvider() != null ? ckCtRentalApp.getProvider() : "");
		BeanUtils.copyProperties(ckCtRentalApp, rentalAppEntity);
		return rentalAppEntity;
	}

	@Override
	protected CkCtRentalApp dtoFromEntity(TCkCtRentalApp tCkCtRentalApp) throws ParameterException, ProcessingException {
		LOG.info("dtoFromEntity: TCkCtRentalApp");
		if (tCkCtRentalApp == null) {
			throw new ParameterException("param entity null");
		}
		CkCtRentalApp rentalAppDto = new CkCtRentalApp();
		rentalAppDto.setEmail(tCkCtRentalApp.getVrCtEmail() != null ? tCkCtRentalApp.getVrCtEmail() : "");
		rentalAppDto.setName(tCkCtRentalApp.getVrAccn() != null ? tCkCtRentalApp.getVrAccn().getAccnName() : "");
		rentalAppDto.setQuantity(tCkCtRentalApp.getVrQty() != null ? tCkCtRentalApp.getVrQty() : 0);
		rentalAppDto.setAccn(tCkCtRentalApp.getVrAccn() != null ? tCkCtRentalApp.getVrAccn().getAccnId() : "");
		rentalAppDto.setCompany(tCkCtRentalApp.getVrAccn() != null ? tCkCtRentalApp.getVrAccn().getAccnName() : "");
		rentalAppDto.setLease(tCkCtRentalApp.getVrLease() != null ? tCkCtRentalApp.getVrLease() : 0);
		rentalAppDto.setContact(tCkCtRentalApp.getVrCtMobile() != null ? tCkCtRentalApp.getVrCtMobile() : "");
		rentalAppDto.setTruck(tCkCtRentalApp.getVrTruck() != null ? tCkCtRentalApp.getVrTruck() : "");
		rentalAppDto.setPrice(tCkCtRentalApp.getVrPrice() != null ? tCkCtRentalApp.getVrPrice() : BigDecimal.ZERO);
		rentalAppDto.setProvider(tCkCtRentalApp.getVrProvider() != null ? tCkCtRentalApp.getVrProvider() : "");
		BeanUtils.copyProperties(tCkCtRentalApp, rentalAppDto);
		return rentalAppDto;
	}

	@Override
	protected String entityKeyFromDTO(CkCtRentalApp ckCtRentalApp) throws ParameterException, ProcessingException {
		LOG.info("entityKeyFromDTO");
		if (ckCtRentalApp == null) {
			throw new ParameterException("param dto null");
		}
		return ckCtRentalApp.getVrId();
	}

	@Override
	protected TCkCtRentalApp updateEntity(ACTION action, TCkCtRentalApp entity, Principal principal, Date date) throws ParameterException, ProcessingException {
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
		String id = CkUtil.generateId("REN");

		switch (action) {
			case CREATE:
				entity.setVrId(id);
				entity.setVrUidCreate(opUserId.orElse(Constant.DEFAULT_USR));
				entity.setVrDtCreate(date);
				entity.setVrDtLupd(date);
				entity.setVrUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
				entity.setVrStatus('N');
				this.sendEmailNotification(Objects.nonNull(principal.getUserName()) ? principal.getUserName() : "",entity.getVrCtEmail(),principal.getCoreAccn().getAccnName());
				break;

			case MODIFY:
				entity.setVrDtLupd(date);
				entity.setVrUidLupd(opUserId.orElse(Constant.DEFAULT_USR));
				break;
			default:
				break;
		}
		return entity;
	}

	@Override
	protected TCkCtRentalApp updateEntityStatus(TCkCtRentalApp tCkCtRentalApp, char status) throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtRentalApp == null)
			throw new ParameterException("entity param null");

		tCkCtRentalApp.setVrStatus(status);
		return tCkCtRentalApp;
	}

	@Override
	protected CkCtRentalApp preSaveUpdateDTO(TCkCtRentalApp tCkCtRentalApp, CkCtRentalApp ckCtRentalApp) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected void preSaveValidation(CkCtRentalApp ckCtRentalApp, Principal principal) throws ParameterException, ProcessingException {

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtRentalApp ckCtRentalApp, Principal principal) throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected String getWhereClause(CkCtRentalApp dto, boolean wherePrinted) throws ParameterException, ProcessingException {
		try {
			if (null == dto)
				throw new ParameterException("param dto null");
			String EQUAL = " = :", CONTAIN = " like :";
			StringBuilder searchStatement = new StringBuilder();
			if (!StringUtils.isEmpty(dto.getVrId())) {
				searchStatement.append(getOperator(wherePrinted)).append("o.vrId LIKE :vrId");
				wherePrinted = true;
			}

			if (dto.getVrStatus() != null) {
				searchStatement.append(getOperator(wherePrinted) + "o.vrStatus" + CONTAIN + "vrStatus");
				wherePrinted = true;
			}

			if (dto.getAccn() != null) {
				searchStatement.append(getOperator(wherePrinted)).append("o.vrAccn.accnId").append(EQUAL).append("accnId");
				wherePrinted = true;
			}

			searchStatement.append(getOperator(wherePrinted) + "o.vrStatus" + " IN :validStatus");

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
	protected HashMap<String, Object> getParameters(CkCtRentalApp dto) throws ParameterException, ProcessingException {
		try {
			if (null == dto)
				throw new ParameterException("param dto null");

			HashMap<String, Object> parameters = new HashMap<String, Object>();

			if (!StringUtils.isEmpty(dto.getVrId())){
				parameters.put("vrId", "%" + dto.getVrId() + "%");
			}
			if (dto.getAccn() != null) {
				parameters.put("accnId", dto.getAccn());
			}
			if (dto.getVrStatus() != null) {
				parameters.put("vrStatus", dto.getVrStatus());
				parameters.put("validStatus", dto.getVrStatus());
			} else if (dto.getHistory() != null) {
				if (dto.getHistory().equalsIgnoreCase("default")) {
					parameters.put("validStatus", Collections.singletonList(STATUS_NEW));
				} else if (dto.getHistory().equalsIgnoreCase("history")) {
					parameters.put("validStatus", Arrays.asList(STATUS_R_REJECT, STATUS_S_APPROVE));
				} else if (dto.getHistory().equalsIgnoreCase("all")) {
					parameters.put("validStatus", Arrays.asList(STATUS_R_REJECT, STATUS_S_APPROVE, STATUS_NEW));
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
	protected CkCtRentalApp whereDto(EntityFilterRequest entityFilterRequest) throws ParameterException, ProcessingException {
		LOG.info("whereDto");
		if (entityFilterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		try {

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
			CkCtRentalApp dto = new CkCtRentalApp();
			for (EntityWhere entityWhere : entityFilterRequest.getWhereList()) {
				Optional<String> opValue = Optional.ofNullable(entityWhere.getValue());
				if (!opValue.isPresent())
					continue;

				if ("vrId".equalsIgnoreCase(entityWhere.getAttribute())) {
					dto.setVrId(opValue.get());
				}

				if ("vrStatus".equalsIgnoreCase(entityWhere.getAttribute())) {
					dto.setVrStatus(opValue.get().charAt(0));
				}

				if ("history".equalsIgnoreCase(entityWhere.getAttribute())) {
					dto.setHistory(opValue.get());
				}
				if ("accnId".equalsIgnoreCase(entityWhere.getAttribute())) {
					dto.setAccn(opValue.get());
				}
			}
			return dto;
		} catch (Exception ex) {
			LOG.error("whereDto", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtRentalApp ckCtRentalApp) throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckCtRentalApp == null) {
			throw new ParameterException("dto param null");
		}
		if (ckCtRentalApp.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return ckCtRentalApp.getCoreMstLocale();
	}

	@Override
	protected CkCtRentalApp setCoreMstLocale(CoreMstLocale coreMstLocale, CkCtRentalApp ckCtRentalApp) throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	public CkCtRentalApp findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("findById");

		try {
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");

			TCkCtRentalApp entity = dao.find(id);
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
	public CkCtRentalApp deleteById(String s, Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		return null;
	}

	@Override
	public List<CkCtRentalApp> filterBy(EntityFilterRequest filterRequest) throws ParameterException, EntityNotFoundException, ProcessingException {
		try {
			if (null == filterRequest)
				throw new ParameterException("param filterRequest null");

			CkCtRentalApp dto = this.whereDto(filterRequest);
			if (null == dto)
				throw new ProcessingException("dto from filter is null");

			filterRequest.setTotalRecords(super.countByAnd(dto));
			String selectClause = "from TCkCtRentalApp o";

			String orderByClause = filterRequest.getOrderBy().toString();
			List<TCkCtRentalApp> entities = super.findEntitiesByAnd(dto, selectClause, orderByClause,
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

	public String getRentalProviders() throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.info("getRentalProviders");
		try {
			String sysVal = coreSysparamDao.find(CtConstant.KEY_TRUCKS_RENTAL_PROVIDERS_PATH).getSysVal();
			return new String(Files.readAllBytes(Paths.get(sysVal+"/truck_providers.json")));
		} catch (ParameterException | EntityNotFoundException ex) {
			LOG.error("getRentalProviders", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("getRentalProviders", ex);
			throw new ProcessingException(ex);
		}
	}

	private void sendEmailNotification(String msg, String recipients, String userName) throws ProcessingException {
		LOG.info("sendEmailNotification");
		try {
			NotificationParam param = new NotificationParam();
			param.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());
			param.setTemplateId(ClicTruckNotifTemplates.TO_TRUCK_PROVIDER_SUBMITTED.getId());
			HashMap<String, String> mailContent = new HashMap<>();
			mailContent.put(":jobNumber", msg);
			mailContent.put(":sp_details", userName);
			param.setContentFeilds(mailContent);
			param.setRecipients(new ArrayList<>(Arrays.asList(recipients.split(","))));

			notificationUtilService.saveNotificationLog(param.toJson(), null, true);
			notificationService.notifySyn(param);

		} catch (Exception e) {
			LOG.error("Fail to rental request email notification", e);
			throw new ProcessingException(e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public String getBase64Str(String code, String file) throws Exception {
		LOG.info("Truck getBase64Str");
		if (StringUtils.isNotBlank(code)) {
			byte[] decodedBytes = Base64.getDecoder().decode(file);
			String fileName = new String(decodedBytes);

			byte[] bytes = Files.readAllBytes(Paths.get("/home/vcc/appAttachments/clictruck/truckProviders/"+code+"/"+fileName));
			String base64Str = Base64.getEncoder().encodeToString(bytes);

			if (StringUtils.isBlank(base64Str)) {
				throw new Exception("Truck getBase64Str image not set.");
			}
			return base64Str;
		}
		return null;
	}
}
