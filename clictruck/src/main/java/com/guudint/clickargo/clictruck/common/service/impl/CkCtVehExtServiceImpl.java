package com.guudint.clickargo.clictruck.common.service.impl;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

import com.guudint.clickargo.clictruck.common.dao.CkCtVehExtDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.dto.CkCtVehExt;
import com.guudint.clickargo.clictruck.common.dto.CkCtVehExtId;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehExt;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehExtId;
import com.guudint.clickargo.clictruck.common.service.CkCtVehExtService;
import com.guudint.clickargo.clictruck.common.validator.CkCtVehExtValidator;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehState;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehState;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.common.AbstractClickCargoEntityService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.locale.dto.CoreMstLocale;
import com.vcc.camelone.util.email.SysParam;

public class CkCtVehExtServiceImpl extends AbstractClickCargoEntityService<TCkCtVehExt, TCkCtVehExtId, CkCtVehExt>
		implements CkCtVehExtService {

	private static Logger LOG = Logger.getLogger(CkCtVehExtServiceImpl.class);

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	private CkCtVehExtValidator ckCtVehExtValidator;

	@Autowired
	private CkCtVehExtDao vehExtDao;

	@Autowired
	SysParam sysParam;

	public CkCtVehExtServiceImpl() {
		super("ckCtVehExtDao", "VEH EXT", TCkCtVehExt.class.getName(), "T_CK_CT_VEH_EXT");
	}

	@Override
	public CkCtVehExt newObj(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		CkCtVehExt ckCtVehExt = new CkCtVehExt();
		ckCtVehExt.setTCkCtVeh(new CkCtVeh());

		CkCtVehExtId ckCtVehExtId = new CkCtVehExtId();
		ckCtVehExt.setId(ckCtVehExtId);

		return ckCtVehExt;
	}

	@Override
	public Object addObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtVehExt ckCtVehExt = (CkCtVehExt) object;

		List<ValidationError> validationErrors = ckCtVehExtValidator.validateCreate(ckCtVehExt, principal);
		if (!validationErrors.isEmpty()) {
			throw new ValidationException(this.validationErrorMap(validationErrors));
		} else {

			if (ckCtVehExt.getVextMonitorMthd().equals('O')) {
				ckCtVehExt.setVextNotify('N');
			}

			if (ckCtVehExt.getVextNotifyWhatsapp() != null) {
				String phoneNumber = ckCtVehExt.getVextNotifyWhatsapp().replaceAll("^\\+", "");
				ckCtVehExt.setVextNotifyWhatsapp(phoneNumber);
			}

			ckCtVehExt.setVextStatus(RecordStatus.ACTIVE.getCode());
			return add(ckCtVehExt, principal);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Object updateObj(Object object, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		CkCtVehExt ckCtVehExtUpdate = (CkCtVehExt) object;
		String id = ckCtVehExtUpdate.getId().getVextId() + ":" + ckCtVehExtUpdate.getId().getVextParam();

		CkCtVehExt ckCtVehExtExist = findVehExtById(id);
		if (ckCtVehExtExist == null) {
			// create instead
			return addObj(object, principal);
		} else {
			List<ValidationError> validationErrors = ckCtVehExtValidator.validateUpdate(ckCtVehExtUpdate, principal);
			if (!validationErrors.isEmpty()) {
				throw new ValidationException(this.validationErrorMap(validationErrors));
			}

			// changed Monitory By from Days Before ('D') to Distance ('O') -> call API
			// Odometer Notification Register
			if (ckCtVehExtExist.getVextMonitorMthd().equals('D') && ckCtVehExtUpdate.getVextMonitorMthd().equals('O')) {
				odometerNotificationDeregister(entityFromDTO(ckCtVehExtUpdate));
				odometerNotificationRegister(entityFromDTO(ckCtVehExtUpdate));
				ckCtVehExtUpdate.setVextNotify('N');
				ckCtVehExtUpdate.setVextMonitorValue(null);
			}

			// changed Distance value -> call API Odometer Notification Register
			if (ckCtVehExtExist.getVextMonitorMthd().equals('O') && ckCtVehExtUpdate.getVextMonitorMthd().equals('O')
					&& !ckCtVehExtExist.getVextMonitorValue().equals(ckCtVehExtUpdate.getVextMonitorValue())) {
				odometerNotificationDeregister(entityFromDTO(ckCtVehExtUpdate));
				odometerNotificationRegister(entityFromDTO(ckCtVehExtUpdate));
				ckCtVehExtUpdate.setVextNotify('N');
			}

			// changed Monitory By from Distance ('O') to Days Before ('D') -> call API
			// Odometer Notification Deregister
			if (ckCtVehExtExist.getVextMonitorMthd().equals('O') && ckCtVehExtUpdate.getVextMonitorMthd().equals('D')) {
				odometerNotificationDeregister(entityFromDTO(ckCtVehExtUpdate));
				ckCtVehExtUpdate.setVextNotify('Y');
			}

			return super.update(ckCtVehExtUpdate, principal);
		}

	}

	@Override
	public CkCtVehExt deleteById(String id, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		if (StringUtils.isEmpty(id)) {
			throw new ParameterException("param id null or empty");
		}
		return updateStatus(id, "delete");
	}

	@Override
	public List<CkCtVehExt> filterBy(EntityFilterRequest arg0)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CkCtVehExt findById(String arg0) throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public CkCtVehExt findVehExtById(String id)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.debug("findVehExtById");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		try {
			if (StringUtils.isEmpty(id)) {
				throw new ParameterException("param id null or empty");
			} else {
				String[] idParts = id.split(":");
				if (idParts.length != 2) {
					throw new ParameterException("id not formulated " + id);
				} else {
					TCkCtVehExtId entityId = new TCkCtVehExtId();
					entityId.setVextId(idParts[0]);
					entityId.setVextParam(idParts[1]);
					TCkCtVehExt entity = dao.find(entityId);
					if (entity != null) {
						initEnity(entity);
						return dtoFromEntity(entity);
					}
				}
			}
		} catch (EntityNotFoundException | ParameterException ex) {
			LOG.error("findVehExtById", ex);
			throw ex;
		} catch (ValidationException ex) {
			LOG.error("findVehExtById", ex);
			throw new ValidationException(ex);
		} catch (Exception ex) {
			LOG.error("findVehExtById", ex);
			throw new ProcessingException(ex);
		}

		return null;
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtVehExt updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		LOG.info("updateStatus");
		Principal principal = ckSession.getPrincipal();
		if (principal == null) {
			throw new ParameterException("principal is null");
		}
		CkCtVehExt ckCtVehExt = findVehExtById(id);
		if (ckCtVehExt == null) {
			throw new EntityNotFoundException("id::" + id);
		}
		if ("active".equals(status)) {
			ckCtVehExt.setVextStatus(RecordStatus.ACTIVE.getCode());
		} else if ("deactive".equals(status)) {
			ckCtVehExt.setVextStatus(RecordStatus.DEACTIVATE.getCode());
		} else if ("delete".equals(status)) {
			ckCtVehExt.setVextStatus(RecordStatus.INACTIVE.getCode());
		}
		return update(ckCtVehExt, principal);
	}

	@Override
	protected CkCtVehExt dtoFromEntity(TCkCtVehExt entity) throws ParameterException, ProcessingException {
		LOG.debug("dtoFromEntity");
		try {
			if (entity == null) {
				throw new ParameterException("param entity null");
			} else {
				CkCtVehExt dto = new CkCtVehExt(entity);
				if (entity.getId() != null) {
					CkCtVehExtId ckCtVehExtId = new CkCtVehExtId();
					ckCtVehExtId.setVextId(entity.getTCkCtVeh().getVhId());
					ckCtVehExtId.setVextParam(entity.getId().getVextParam());
					dto.setId(ckCtVehExtId);
				}
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
	protected TCkCtVehExt entityFromDTO(CkCtVehExt dto) throws ParameterException, ProcessingException {
		LOG.debug("entityFromDTO");
		try {
			if (null == dto)
				throw new ParameterException("dto dto null");

			TCkCtVehExt entity = new TCkCtVehExt();
			entity = dto.toEntity(entity);

			Optional<CkCtVehExtId> opId = Optional.ofNullable(dto.getId());
			entity.setId(opId.isPresent() ? (TCkCtVehExtId) ((CkCtVehExtId) opId.get()).toEntity(new TCkCtVehExtId())
					: null);

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
	protected TCkCtVehExtId entityKeyFromDTO(CkCtVehExt dto) throws ParameterException, ProcessingException {
		LOG.debug("entityKeyFromDTO");
		try {
			if (dto == null) {
				throw new ParameterException("dto param null");
			} else {
				return dto.getId().toEntity(new TCkCtVehExtId());
			}
		} catch (ParameterException ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("entityKeyFromDTO", ex);
			throw new ProcessingException(ex);
		}
	}

	@Override
	protected CoreMstLocale getCoreMstLocale(CkCtVehExt arg0)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HashMap<String, Object> getParameters(CkCtVehExt arg0) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getWhereClause(CkCtVehExt arg0, boolean arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkCtVehExt initEnity(TCkCtVehExt tCkCtVehExt) throws ParameterException, ProcessingException {
		LOG.debug("initEnity");
		if (tCkCtVehExt != null) {
			Hibernate.initialize(tCkCtVehExt.getTCkCtVeh());
		}
		return tCkCtVehExt;
	}

	@Override
	protected CkCtVehExt preSaveUpdateDTO(TCkCtVehExt storedEntity, CkCtVehExt dto)
			throws ParameterException, ProcessingException {
		LOG.debug("preSaveUpdateDTO");
		if (null == storedEntity)
			throw new ParameterException("param storedEntity null");
		if (null == dto)
			throw new ParameterException("param dto null");

		dto.setVextUidCreate(storedEntity.getVextUidCreate());
		dto.setVextDtCreate(storedEntity.getVextDtCreate());

		return dto;
	}

	@Override
	protected void preSaveValidation(CkCtVehExt arg0, Principal arg1) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub

	}

	@Override
	protected ServiceStatus preUpdateValidation(CkCtVehExt arg0, Principal arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtVehExt setCoreMstLocale(CoreMstLocale arg0, CkCtVehExt arg1)
			throws ParameterException, EntityNotFoundException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TCkCtVehExt updateEntity(ACTION action, TCkCtVehExt tCkCtVehExt, Principal principal, Date date)
			throws ParameterException, ProcessingException {
		LOG.debug("updateEntity");
		if (tCkCtVehExt == null)
			throw new ParameterException("param entity null");
		if (principal == null)
			throw new ParameterException("param principal null");
		if (date == null)
			throw new ParameterException("param date null");

		Optional<String> opUserId = Optional.ofNullable(principal.getUserId());
		switch (action) {
		case CREATE:
			tCkCtVehExt.setVextUidCreate(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			tCkCtVehExt.setVextDtCreate(date);
			tCkCtVehExt.setVextDtLupd(date);
			tCkCtVehExt.setVextUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);

			if (tCkCtVehExt.getVextMonitorMthd().equals('O')) {
				// call API Odometer Notification Register
				odometerNotificationRegister(tCkCtVehExt);
			}

			break;

		case MODIFY:
			tCkCtVehExt.setVextDtLupd(date);
			tCkCtVehExt.setVextUidLupd(opUserId.isPresent() ? opUserId.get() : Constant.DEFAULT_USR);
			break;

		default:
			break;
		}
		return tCkCtVehExt;
	}

	// POST
	// https://ext.logistics.myascents.net/api/3rdparty/unit/odometer/notification?key=%242a%2411%24oo7.I606K%2FDVjUdZi88UzeFu.tLQiwTChnty4KATKbf3Dq0T8s2Ii
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void odometerNotificationRegister(TCkCtVehExt tCkCtVehExt) throws ProcessingException {
		LOG.debug("odometerNotificationRegister");
		if (tCkCtVehExt.getTCkCtVeh() != null && tCkCtVehExt.getTCkCtVeh().getVhGpsImei() != null
				&& tCkCtVehExt.getVextValue() != null) {

			try {
				String endpointUrl = sysParam.getValString("CLICTRUCK_MYASCENTS_ODOMETER_REGISTER_URL",
						"https://ext.logistics.myascents.net/api/3rdparty/unit/odometer/notification?key=%242a%2411%24oo7.I606K%2FDVjUdZi88UzeFu.tLQiwTChnty4KATKbf3Dq0T8s2Ii");
				URL url = new URL(endpointUrl);
				LOG.info(url.toString());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");

				String jsonInputString = "{ \"imei\": \"" + tCkCtVehExt.getTCkCtVeh().getVhGpsImei()
						+ "\", \"distance\": " + tCkCtVehExt.getVextMonitorValue() + " }";
				LOG.info(jsonInputString);

				try (OutputStream os = connection.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
					connection.connect();
				}

				int responseCode = connection.getResponseCode();
				System.out.println("Response Code: " + responseCode);

				connection.disconnect();

			} catch (Exception e) {
				e.printStackTrace();
				throw new ProcessingException(e);
			}
		}
	}

	// DELETE
	// https://ext.logistics.myascents.net/api/3rdparty/unit/odometer/notification?key=%242a%2411%24oo7.I606K%2FDVjUdZi88UzeFu.tLQiwTChnty4KATKbf3Dq0T8s2Ii&imei=FEA01BA30126
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void odometerNotificationDeregister(TCkCtVehExt tCkCtVehExt) throws ProcessingException {
		LOG.debug("odometerNotificationDeregister");
		if (tCkCtVehExt.getTCkCtVeh() != null && tCkCtVehExt.getTCkCtVeh().getVhGpsImei() != null
				&& tCkCtVehExt.getVextValue() != null) {

			try {
				String endpointUrl = sysParam.getValString("CLICTRUCK_MYASCENTS_ODOMETER_DEREGISTER_URL",
						"https://ext.logistics.myascents.net/api/3rdparty/unit/odometer/notification?key=%242a%2411%24oo7.I606K%2FDVjUdZi88UzeFu.tLQiwTChnty4KATKbf3Dq0T8s2Ii&imei=")
						+ tCkCtVehExt.getTCkCtVeh().getVhGpsImei();
				URL url = new URL(endpointUrl);
				LOG.info(url.toString());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("DELETE");
				connection.setDoOutput(false);

				int responseCode = connection.getResponseCode();
				System.out.println("Response Code: " + responseCode);

				connection.disconnect();

			} catch (Exception e) {
				e.printStackTrace();
				throw new ProcessingException(e);
			}
		}
	}

	@Override
	protected TCkCtVehExt updateEntityStatus(TCkCtVehExt arg0, char arg1)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CkCtVehExt whereDto(EntityFilterRequest arg0) throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CkCtVehExt findVehExtByVehAndKey(String vehId, String key)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException {
		try {

			if (StringUtils.isBlank(vehId))
				throw new ParameterException("param vehId null or empty");
			if (StringUtils.isBlank(key))
				throw new ParameterException("param key null or empty");

			TCkCtVehExt vehExtEntity = vehExtDao.findByVehIdAndKey(vehId, key);
			if (vehExtEntity != null) {
				Hibernate.initialize(vehExtEntity.getTCkCtVeh());
				Hibernate.initialize(vehExtEntity.getId());
				CkCtVehExt vehExtDto = new CkCtVehExt(vehExtEntity);
				vehExtDto.setId(new CkCtVehExtId(vehExtEntity.getId()));
				vehExtDto.setTCkCtVeh(new CkCtVeh(vehExtEntity.getTCkCtVeh()));
				return vehExtDto;
			}
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

		return null;
	}

}
