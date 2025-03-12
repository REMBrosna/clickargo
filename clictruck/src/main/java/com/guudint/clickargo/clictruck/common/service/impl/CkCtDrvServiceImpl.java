package com.guudint.clickargo.clictruck.common.service.impl;

import com.guudint.clickargo.admin.event.ClickargoPostUserUpdateEvent;
import com.guudint.clickargo.clictruck.common.constant.CkCtDrvConstant;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.DriverStates;
import com.guudint.clickargo.clictruck.common.dto.NotificationTemplateName;
import com.guudint.clickargo.clictruck.common.event.AccnDriverEvent;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.service.CkCtDrvService;
import com.guudint.clickargo.clictruck.common.validator.CkCtDrvValidator;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.clictruck.constant.DateFormat;
import com.guudint.clickargo.clictruck.notification.model.TCkCtAlert;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.track.service.WhatsappYCloudService;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.clictruck.util.FileUtil;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.dao.CkAccnDao;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.util.RandomNumberPasswordGenerator;
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
import com.vcc.camelone.util.crypto.PasswordEncryptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CkCtDrvServiceImpl extends AbstractClickCargoEntityService<TCkCtDrv, String, CkCtDrv>
		implements ICkConstant, CkCtDrvService {

	private static Logger LOG = Logger.getLogger(CkCtDrvServiceImpl.class);

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;
	@Autowired
	private CkCtDrvValidator ckCtDrvValidator;

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;
	@Autowired
	private WhatsappYCloudService whatsappYCloudService;
	@Autowired
	@Qualifier("ckCtAlertDao")
	private GenericDao<TCkCtAlert, String> ckCtAlertDao;

	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private CkAccnDao ckAccnDao;

	public CkCtDrvServiceImpl() {
		super(CkCtDrvConstant.Table.NAME_DAO, CkCtDrvConstant.Prefix.AUDIT_TAG, CkCtDrvConstant.Table.NAME_ENTITY,
				CkCtDrvConstant.Table.NAME);
	}

	@Override
	public CkCtDrv deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		LOG.debug("deleteById -> id:" + id);
		try {
			TCkCtDrv tCkCtDrv = dao.find(id);
			if (tCkCtDrv != null) {
				dao.remove(tCkCtDrv);
				return dtoFromEntity(tCkCtDrv);
			}
		} catch (Exception e) {
			LOG.error("deleteById", e);
		}
		return null;
	}

	@Override
	public List<CkCtDrv> filterBy(EntityFilterRequest filterRequest)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("filterBy");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		CkCtDrv ckCtDrv = whereDto(filterRequest);
		if (ckCtDrv == null) {
			throw new ProcessingException("whereDto null result");
		}
		filterRequest.setTotalRecords(countByAnd(ckCtDrv));
		List<CkCtDrv> ckCtDrvs = new ArrayList<>();
		try {
			String orderClause = formatOrderBy(filterRequest.getOrderBy().toString());
			List<TCkCtDrv> tCkCtDrvs = findEntitiesByAnd(ckCtDrv, "from TCkCtDrv o ", orderClause,
					filterRequest.getDisplayLength(), filterRequest.getDisplayStart());
			for (TCkCtDrv tCkCtDrv : tCkCtDrvs) {
				CkCtDrv dto = dtoFromEntity(tCkCtDrv, false);
				if (dto != null) {
					ckCtDrvs.add(dto);
				}
			}
		} catch (Exception e) {
			LOG.error("filterBy", e);
		}
		return ckCtDrvs;
	}

	@Override
	public CkCtDrv findById(String id) throws ParameterException, EntityNotFoundException, ProcessingException {
		LOG.debug("findById");
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		try {
			TCkCtDrv tCkCtDrv = dao.find(id);
			if (tCkCtDrv == null) {
				throw new EntityNotFoundException("findById -> id:" + id);
			}
			initEnity(tCkCtDrv);
			return dtoFromEntity(tCkCtDrv);
		} catch (Exception e) {
			LOG.error("findById" + e);
		}
		return null;
	}

	protected String formatOrderBy(String attribute) throws Exception {
		attribute = Optional.ofNullable(attribute).orElse("");
		return attribute;
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected void initBusinessValidator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'initBusinessValidator'");
	}

	protected CkCtDrv dtoFromEntity(TCkCtDrv tCkCtDrv, boolean withData)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtDrv == null) {
			throw new ParameterException("param entity null");
		}
		CkCtDrv ckCtDrv = new CkCtDrv(tCkCtDrv);
		if (tCkCtDrv.getTCoreAccn() != null) {
			ckCtDrv.setTCoreAccn(new CoreAccn(tCkCtDrv.getTCoreAccn()));
		}

		if (withData) {
			if (tCkCtDrv.getDrvLicensePhotoLoc() != null) {
				try {
					String base64 = FileUtil.toBase64(tCkCtDrv.getDrvLicensePhotoLoc());
					ckCtDrv.setBase64File(base64);
				} catch (IOException e) {
					LOG.error(e);
				}
			}
		}
		return ckCtDrv;
	}

	protected CkCtDrv dtoFromEntity(TCkCtDrv tCkCtDrv) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtDrv == null) {
			throw new ParameterException("param entity null");
		}
		CkCtDrv ckCtDrv = new CkCtDrv(tCkCtDrv);
		if (tCkCtDrv.getTCoreAccn() != null) {
			ckCtDrv.setTCoreAccn(new CoreAccn(tCkCtDrv.getTCoreAccn()));
		}

		if (tCkCtDrv.getDrvLicensePhotoLoc() != null) {
			try {
				String base64 = FileUtil.toBase64(tCkCtDrv.getDrvLicensePhotoLoc());
				ckCtDrv.setBase64File(base64);
			} catch (IOException e) {
				LOG.error(e);
			}
		}
		return ckCtDrv;
	}

	@Override
	protected TCkCtDrv entityFromDTO(CkCtDrv ckCtDrv) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		if (ckCtDrv == null) {
			throw new ParameterException("param entity null");
		}
		TCkCtDrv tCkCtDrv = new TCkCtDrv(ckCtDrv);
		if (ckCtDrv.getTCoreAccn() != null) {
			tCkCtDrv.setTCoreAccn(ckCtDrv.getTCoreAccn().toEntity(new TCoreAccn()));
		}
		return tCkCtDrv;
	}

	@Override
	protected String entityKeyFromDTO(CkCtDrv ckCtDrv) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		if (ckCtDrv == null) {
			throw new ParameterException("dto param null");
		}
		return ckCtDrv.getDrvId();
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtDrv ckCtDrv)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		if (ckCtDrv == null) {
			throw new ParameterException("dto param null");
		}
		if (ckCtDrv.getCoreMstLocale() == null) {
			throw new ProcessingException("coreMstLocale null");
		}
		return ckCtDrv.getCoreMstLocale();
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtDrv ckCtDrv) throws ParameterException, ProcessingException {
		LOG.debug("getParameters");
		if (ckCtDrv == null) {
			throw new ParameterException("param dto null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		HashMap<String, Object> parameters = new HashMap<>();
		if (ckCtDrv.getTCoreAccn() != null) {
			if (ckCtDrv.getTCoreAccn().getAccnId() != null) {
				parameters.put(CkCtDrvConstant.ColumnParam.DRV_COMPANY, ckCtDrv.getTCoreAccn().getAccnId());
			}
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvId())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_ID, ckCtDrv.getDrvId());
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvName())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_NAME, "%" + ckCtDrv.getDrvName() + "%");
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvLicenseNo())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_LICENSE_NO, "%" + ckCtDrv.getDrvLicenseNo() + "%");
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvMobileId())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_MOBILE_ID, "%" + ckCtDrv.getDrvMobileId() + "%");
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvEmail())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_EMAIL, "%" + ckCtDrv.getDrvEmail() + "%");
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvPhone())) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_PHONE, "%" + ckCtDrv.getDrvPhone() + "%");
		}
		if (ckCtDrv.getDrvDtCreate() != null) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_DT_CREATE, sdf.format(ckCtDrv.getDrvDtCreate()));
		}
		if (ckCtDrv.getDrvDtLupd() != null) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_DT_LUPD, sdf.format(ckCtDrv.getDrvDtLupd()));
		}

		if (ckCtDrv.getDrvState() != null) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_STATE, ckCtDrv.getDrvState());
		}

		if (ckCtDrv.getDrvStatus() != null) {
			parameters.put(CkCtDrvConstant.ColumnParam.DRV_STATUS, ckCtDrv.getDrvStatus());
		}

		parameters.put("validStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.DEACTIVATE.getCode(),
				// [Manage Driver] After Deactive Driver records goes away instead of Inactive
				RecordStatus.INACTIVE.getCode(), RecordStatus.SUSPENDED.getCode()));

		return parameters;
	}

	@Override
	protected String getWhereClause(CkCtDrv ckCtDrv, boolean wherePrinted)
			throws ParameterException, ProcessingException {
		LOG.debug("getWhereClause");
		String EQUAL = " = :", CONTAIN = " like :";
		if (ckCtDrv == null) {
			throw new ParameterException("param dto null");
		}
		StringBuffer condition = new StringBuffer();
		if (ckCtDrv.getTCoreAccn() != null) {
			if (ckCtDrv.getTCoreAccn().getAccnId() != null) {
				condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_COMPANY + EQUAL
						+ CkCtDrvConstant.ColumnParam.DRV_COMPANY);
				wherePrinted = true;
			}
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvId())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_ID + EQUAL
					+ CkCtDrvConstant.ColumnParam.DRV_ID);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvName())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_NAME + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_NAME);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvLicenseNo())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_LICENSE_NO + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_LICENSE_NO);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvMobileId())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_MOBILE_ID + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_MOBILE_ID);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvEmail())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_EMAIL + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_EMAIL);
			wherePrinted = true;
		}
		if (StringUtils.isNotBlank(ckCtDrv.getDrvPhone())) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_PHONE + CONTAIN
					+ CkCtDrvConstant.ColumnParam.DRV_PHONE);
			wherePrinted = true;
		}
		if (ckCtDrv.getDrvDtCreate() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtDrvConstant.Column.DRV_DT_CREATE + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtDrvConstant.ColumnParam.DRV_DT_CREATE);
			wherePrinted = true;
		}
		if (ckCtDrv.getDrvDtLupd() != null) {
			condition.append(getOperator(wherePrinted) + "DATE_FORMAT(" + CkCtDrvConstant.Column.DRV_DT_LUPD + ",'"
					+ DateFormat.MySql.D_M_Y + "')" + EQUAL + CkCtDrvConstant.ColumnParam.DRV_DT_LUPD);
			wherePrinted = true;
		}

		if (ckCtDrv.getDrvState() != null) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_STATE + EQUAL
					+ CkCtDrvConstant.ColumnParam.DRV_STATE);
			wherePrinted = true;
		}

		if (ckCtDrv.getDrvStatus() != null) {
			condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_STATUS + EQUAL
					+ CkCtDrvConstant.ColumnParam.DRV_STATUS);
			wherePrinted = true;
		}

		condition.append(getOperator(wherePrinted) + CkCtDrvConstant.Column.DRV_STATUS + " IN :validStatus");

		return condition.toString();
	}

	@Override
	protected TCkCtDrv initEnity(TCkCtDrv tCkCtDrv) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtDrv != null) {
			Hibernate.initialize(tCkCtDrv.getTCoreAccn());
		}
		return tCkCtDrv;
	}

	@Override
	protected CkCtDrv preSaveUpdateDTO(TCkCtDrv tCkCtDrv, CkCtDrv ckCtDrv)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");

		if (tCkCtDrv == null)
			throw new ParameterException("param storedEntity null");
		if (ckCtDrv == null)
			throw new ParameterException("param dto null");
		/*-
		if (StringUtils.isNotBlank(ckCtDrv.getBase64File())
				&& StringUtils.isNotBlank(ckCtDrv.getDrvLicensePhotoName())) {
			try {
				byte[] data = Base64.getDecoder().decode(ckCtDrv.getBase64File());
				String basePath;
				basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
				if (StringUtils.isBlank(basePath)) {
					throw new ProcessingException("basePath is not configured");
				}
				String filePath = FileUtil.saveAttachment(basePath.concat(ckCtDrv.getDrvLicensePhotoName()), data);
				ckCtDrv.setDrvLicensePhotoLoc(filePath);
			} catch (Exception e) {
				LOG.error(e);
			}
		}

		if (StringUtils.isNotBlank(ckCtDrv.getDrvMobileId())
				&& StringUtils.isNotBlank(ckCtDrv.getDrvMobilePassword())) {
			try {
		//				boolean isPwdChanged = drvDao.isPasswordChanged(ckCtDrv.getDrvMobilePassword());
				if (ckCtDrv.isDrvEditPassword()) {
					String encryptedPassword = PasswordEncryptor.encrypt(ckCtDrv.getDrvMobileId(),
							ckCtDrv.getDrvMobilePassword());
					ckCtDrv.setDrvMobilePassword(encryptedPassword);
				}
			} catch (Exception ex) {
				throw new ProcessingException(ex);
			}
		}
		*/
		ckCtDrv.setDrvUidCreate(tCkCtDrv.getDrvUidCreate());
		ckCtDrv.setDrvDtCreate(tCkCtDrv.getDrvDtCreate());
		return ckCtDrv;
	}

	@Override
	protected void preSaveValidation(CkCtDrv arg0, Principal arg1) throws ParameterException, ProcessingException {

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtDrv arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	protected CkCtDrv setCoreMstLocale(CoreMstLocale arg0, CkCtDrv arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		return null;
	}

	@Override
	protected TCkCtDrv updateEntity(ACTION action, TCkCtDrv tCkCtDrv, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtDrv == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");
		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
			case CREATE:
				tCkCtDrv.setDrvUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				tCkCtDrv.setDrvDtCreate(date);
				tCkCtDrv.setDrvDtLupd(date);
				tCkCtDrv.setDrvUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				break;

			case MODIFY:
				tCkCtDrv.setDrvDtLupd(date);
				tCkCtDrv.setDrvUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
				break;

			default:
				break;
		}
		return tCkCtDrv;
	}

	@Override
	protected TCkCtDrv updateEntityStatus(TCkCtDrv tCkCtDrv, char status)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntityStatus");
		if (tCkCtDrv == null) {
			throw new ParameterException("entity param null");
		}
		tCkCtDrv.setDrvStatus(status);
		return tCkCtDrv;
	}

	@Override
	protected CkCtDrv whereDto(EntityFilterRequest filterRequest) throws ParameterException, ProcessingException {
		LOG.debug("whereDto");
		if (filterRequest == null) {
			throw new ParameterException("param filterRequest null");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.Java.DD_MM_YYYY);
		CkCtDrv ckCtDrv = new CkCtDrv();
		CoreAccn coreAccn = new CoreAccn();
		ckCtDrv.setTCoreAccn(coreAccn);
		for (EntityWhere entityWhere : filterRequest.getWhereList()) {
			if (entityWhere == null) {
				continue;
			}
			String attribute = "o." + entityWhere.getAttribute();
			if (CkCtDrvConstant.Column.DRV_COMPANY.equalsIgnoreCase(attribute)) {
				coreAccn.setAccnId(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_ID.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvId(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_STATUS.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvStatus((entityWhere.getValue() == null) ? null : entityWhere.getValue().charAt(0));
			} else if (CkCtDrvConstant.Column.DRV_NAME.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvName(entityWhere.getValue());
			}
			else if (CkCtDrvConstant.Column.DRV_LICENSE_NO.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvLicenseNo(entityWhere.getValue());
			}
			else if (CkCtDrvConstant.Column.DRV_MOBILE_ID.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvMobileId(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_EMAIL.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvEmail(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_PHONE.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvPhone(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_STATE.equalsIgnoreCase(attribute)) {
				ckCtDrv.setDrvState(entityWhere.getValue());
			} else if (CkCtDrvConstant.Column.DRV_DT_CREATE.equalsIgnoreCase(attribute)) {
				try {
					ckCtDrv.setDrvDtCreate(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			} else if (CkCtDrvConstant.Column.DRV_DT_LUPD.equalsIgnoreCase(attribute)) {
				try {
					ckCtDrv.setDrvDtLupd(sdf.parse(entityWhere.getValue()));
				} catch (ParseException e) {
					LOG.error(e);
				}
			}
		}
		return ckCtDrv;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public CkCtDrv updateStatus(String id, String status)
			throws Exception {
		LOG.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtDrv ckCtDrv = findById(id);
		if (ckCtDrv == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			ckCtDrv.setDrvStatus(RecordStatus.ACTIVE.getCode());
			deepDeleteApp(id, RecordStatus.ACTIVE.getCode());
		} else if ("deactive".equals(status)) {
			checkAvailJobByDrv(ckCtDrv);
			ckCtDrv.setDrvStatus(RecordStatus.INACTIVE.getCode());
			deepDeleteApp(id, RecordStatus.INACTIVE.getCode());
		}
		update(ckCtDrv, principal);
		return ckCtDrv;
	}

	private void deepDeleteApp(String key, Character status) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("vehId", key);
		param.put("status", status);
		String updateAlertStatus = "UPDATE TCkCtAlert SET altStatus = :status WHERE altReferId = :vehId AND altStatus = 'A'";
		ckCtAlertDao.executeUpdate(updateAlertStatus, param);
	}


	private void checkAvailJobByDrv(CkCtDrv ckCtDrv) throws ProcessingException {
		if (ckCtDrv.getDrvState().equalsIgnoreCase(DriverStates.ASSIGNED.name())) {
			throw new ProcessingException(ckCtDrv.getDrvName() + " (" + ckCtDrv.getDrvId() + ") still has a job");
		}
	}

	@Override
	public CkCtDrv newObj(Principal principal) throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtDrv ckCtDrv = new CkCtDrv();
		ckCtDrv.setTCoreAccn(principal.getCoreAccn());
		DateUtil dateUtil = new DateUtil(new Date());
		ckCtDrv.setDrvDtCreate(dateUtil.getDate());
		ckCtDrv.setDrvLicenseExpiry(new DateUtil().getDefaultEndDate());
		ckCtDrv.setDrvState(DriverStates.UNASSIGNED.name());
		ckCtDrv.setDrvEditPassword(true);
		return ckCtDrv;
	}

	@Override
	public CkCtDrv add(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (ckCtDrv == null) {
			throw new ParameterException("param dto null");
		}
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		try {
			ckCtDrv.setTCoreAccn(principal.getCoreAccn());
			ckCtDrv.setDrvId(CkUtil.generateId(CkCtDrvConstant.Prefix.PREFIX_CK_CT_DRV));
			ckCtDrv.setDrvStatus(RecordStatus.ACTIVE.getCode());
			if (StringUtils.isNotBlank(ckCtDrv.getBase64File())
					&& StringUtils.isNotBlank(ckCtDrv.getDrvLicensePhotoName())) {
				byte[] data = Base64.getDecoder().decode(ckCtDrv.getBase64File());
				String basePath = getSysParam(CtConstant.KEY_ATTCH_BASE_LOCATION);
				if (StringUtils.isBlank(basePath)) {
					throw new ProcessingException("basePath is not configured");
				}
				String filePath = FileUtil.saveAttachment(basePath.concat(ckCtDrv.getDrvLicensePhotoName()), data);
				ckCtDrv.setDrvLicensePhotoLoc(filePath);
			}

			CkCtDrv newCkCtDrv = super.add(ckCtDrv, principal);
			Boolean isSubscribed = ckAccnDao.findByAccnIdSubscribed(ckCtDrv.getTCoreAccn().getAccnId(), RecordStatus.ACTIVE.getCode(), null);
			this.newDriverPassword(newCkCtDrv.getDrvId(), isSubscribed);

			return newCkCtDrv;

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ProcessingException(e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtDrv ckCtDrv = (CkCtDrv) object;
		List<ValidationError> validationErrors = ckCtDrvValidator.validateCreate(ckCtDrv, principal);
		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return add(ckCtDrv, principal);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public Object updateObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {

		CkCtDrv ckCtDrv = (CkCtDrv) object;
		List<ValidationError> validationErrors = ckCtDrvValidator.validateUpdate(ckCtDrv, principal);

		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {
			return super.update(ckCtDrv, principal);
		}
	}

	@Override
	public boolean isDriverFree(String id, boolean isMobileJob, List<String> validStates)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		// If there is still at least one record that the driver is assigned to with the
		// jobstate in ASG, ONGOING and PAUSED
		// driver is not considered free (mobile enabled job only)
		String hql = "from TCkJobTruck o where o.TCkCtDrv.drvId=:driverRecId "
				+ " and o.jobMobileEnabled= :mobileEnabled " + " and o.TCkJob.TCkMstJobState.jbstId in (:jobStates)";
		Map<String, Object> params = new HashMap<>();
		params.put("driverRecId", id);
		params.put("jobStates", validStates);
		params.put("mobileEnabled", isMobileJob ? 'Y' : 'N');
		List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hql, params);
		if (list != null && list.size() > 0) {
			return false;
		}
		return true;
	}

	// In the case of driver, display password only inside the form
	public CkCtDrv findById(String id, Character showPassword)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception {
		LOG.debug("findById");
		try {
			TCkCtDrv tCkCtDrv = dao.find(id);
			if (tCkCtDrv == null) {
				throw new EntityNotFoundException("findById -> id:" + id);
			}
			initEnity(tCkCtDrv);
			return this.dtoFromEntity(tCkCtDrv, showPassword);
		} catch (Exception e) {
			LOG.error("findById" + e);
		}
		return null;
	}

	public String resetDriverPassword(String drvId, String action) throws Exception {

		TCkCtDrv tCkCtDrv;
		try {
			tCkCtDrv = dao.find(drvId);

			String pwd = RandomNumberPasswordGenerator.generateRandomPassword();

			String encryptedPassword = this.encryptDriverPwd(pwd);
			tCkCtDrv.setDrvMobilePassword(encryptedPassword);
			tCkCtDrv.setDrvDtLupd(new Date());

			Boolean isSubscribed = ckAccnDao.findByAccnIdSubscribed(tCkCtDrv.getTCoreAccn().getAccnId(), RecordStatus.ACTIVE.getCode(), null);
			if (isSubscribed){
				this.sendWhatsapYcloudForgetPassword(tCkCtDrv.getDrvPhone(), tCkCtDrv.getDrvMobileId(), pwd);
			}else {
				if (action.equalsIgnoreCase(NotificationTemplateName.RESET_PASSWORD.getDesc())){
					eventPublisher.publishEvent(new AccnDriverEvent(this, tCkCtDrv, AccnDriverEvent.AccnDrvEventAction.RESET_PASSWORD, pwd));
				}else if (action.equalsIgnoreCase(NotificationTemplateName.FORGOT_PASSWORD.getDesc())) {
					eventPublisher.publishEvent(new AccnDriverEvent(this, tCkCtDrv, AccnDriverEvent.AccnDrvEventAction.FORGOT_PASSWORD, pwd));
				}

			}
			this.dao.saveOrUpdate(tCkCtDrv);

			return "Password successfully updated and sent to " + tCkCtDrv.getDrvPhone();

		} catch (Exception e) {
			LOG.error("resetDriverPassword", e);
			//throw e;
			return "Fail to reset password: " + e.getMessage();
		}
	}

	public String encryptDriverPwd(String pwd) {
		return PasswordEncryptor.encrypt("", pwd);
	}

	public void sendWhatsapYcloudForgetPassword(String mobileNumber, String driverName, String password)
			throws Exception {

		LOG.info("sendWhatsapYcloudForgetPassword: " + mobileNumber + "  " + driverName + "  " + password);

		try {
			// Arrays.asList(driverName, password)
			ArrayList<String> list = new ArrayList<>();
			list.add(driverName);
			list.add(password);

			whatsappYCloudService.sendYCloudWhatAppMsg(null, mobileNumber, list, null,
					NotificationTemplateName.RESET_PASSWORD.getDesc());

		} catch (Exception e) {
			LOG.error("enterExitTimeOfLocation", e);
			throw e;
		}
	}

	public void newDriverPassword(String drvId, Boolean isSubscribed) throws Exception {

		TCkCtDrv tCkCtDrv;
		try {
			tCkCtDrv = dao.find(drvId);

			String pwd = RandomNumberPasswordGenerator.generateRandomPassword();

			String encryptedPassword = this.encryptDriverPwd(pwd);
			tCkCtDrv.setDrvMobilePassword(encryptedPassword);
			tCkCtDrv.setDrvDtLupd(new Date());
			if (isSubscribed){
				this.sendWhatsappYCloudNewDriverPassword(tCkCtDrv.getDrvName(), tCkCtDrv.getDrvEmail(), pwd, tCkCtDrv.getDrvPhone());
			}else {
				eventPublisher.publishEvent(new AccnDriverEvent(this, tCkCtDrv, AccnDriverEvent.AccnDrvEventAction.NEW_DRIVER, pwd));
			}
			this.dao.saveOrUpdate(tCkCtDrv);

		} catch (Exception e) {
			LOG.error("newDriverPassword", e);
			throw e;
		}
	}

	public void sendWhatsappYCloudNewDriverPassword(String drvName, String email, String password, String mobileNumber)
			throws Exception {

		LOG.info("sendWhatsapYcloudForgetPassword: " + drvName + "  " + email + "  " + password);

		try {
			// Arrays.asList(driverName, password)
			ArrayList<String> list = new ArrayList<>();
			list.add(drvName);
			list.add(email);
			list.add(password);

			whatsappYCloudService.sendYCloudWhatAppMsg(null, mobileNumber, list, null,
					NotificationTemplateName.NEW_DRIVER.getDesc());

		} catch (Exception e) {
			LOG.error("sendWhatsappYCloudNewDriverPassword", e);
			throw e;
		}
	}

	/**
	 * This method shows password inside Driver Management form
	 *
	 * @param tCkCtDrv
	 * @param showPassword
	 * @return
	 * @throws ParameterException
	 * @throws ProcessingException
	 */
	protected CkCtDrv dtoFromEntity(TCkCtDrv tCkCtDrv, Character showPassword)
			throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		if (tCkCtDrv == null) {
			throw new ParameterException("param entity null");
		}
		CkCtDrv ckCtDrv = new CkCtDrv(tCkCtDrv);
		if (tCkCtDrv.getTCoreAccn() != null) {
			ckCtDrv.setTCoreAccn(new CoreAccn(tCkCtDrv.getTCoreAccn()));
		}

		boolean show = showPassword != null && showPassword == 'Y';
		ckCtDrv.setDrvMobilePassword(show ? ckCtDrv.getDrvMobilePassword() : null);

		if (tCkCtDrv.getDrvLicensePhotoLoc() != null) {
			try {
				String base64 = FileUtil.toBase64(tCkCtDrv.getDrvLicensePhotoLoc());
				ckCtDrv.setBase64File(base64);
			} catch (IOException e) {
				LOG.error(e);
			}
		}
		return ckCtDrv;
	}

}