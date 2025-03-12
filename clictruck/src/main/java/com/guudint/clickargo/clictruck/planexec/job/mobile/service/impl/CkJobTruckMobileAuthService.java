package com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.guudint.clickargo.clictruck.common.dto.NotificationTemplateName;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.admin.dto.ChangeMobilePassword;
import com.guudint.clickargo.clictruck.common.dao.CkCtDrvDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.service.CkCtDrvService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Roles;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.cac.services.impl.SessionService;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreUsrReset;
import com.vcc.camelone.ccm.service.impl.PortalUserService;
import com.vcc.camelone.ccm.service.impl.PortalUserService.LoginStatus;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.SysParamService;
import com.vcc.camelone.constant.SysParamConstant;
import com.vcc.camelone.core.model.TCoreSession;
import com.vcc.camelone.master.dto.MstAccnType;

@Service
public class CkJobTruckMobileAuthService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(PortalUserService.class);
	private final String CLICTRUCK_MOBILE_LOGIN_RETRIES = "CLICTRUCK_MOBILE_LOGIN_RETRIES";


	@Autowired
	GenericDao<TCoreAuditlog, String> auditLogDao;

	@Autowired
	SessionService sessionService;

	@Autowired
	SysParamService sysParamService;

	@Autowired
	@Qualifier("coreSessionDao")
	private GenericDao<TCoreSession, String> coreSessionDao;

	@Autowired
	@Qualifier("ckCtDrvDao")
	private GenericDao<TCkCtDrv, String> ckCtDrvDao;

	@Autowired
	private CkCtDrvDao drvDao;
	@Autowired
	private CkCtDrvService ckCtDrvService;

	@Autowired
	@Qualifier("coreUsrResetDao")
	private GenericDao<TCoreUsrReset, String> coreUsrResetDao;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	protected ICkSession ckSession;


	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public LoginStatus authenticate(String drvId, String usrPassword, HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("authenticate");

		try {
			String proceed = "on";// request.getParameter("proceed");

			TCkCtDrv tDrv = drvDao.findByMobileUserId(drvId, Arrays.asList(RecordStatus.ACTIVE.getCode(),
					RecordStatus.INACTIVE.getCode(), RecordStatus.DEACTIVATE.getCode(), RecordStatus.SUSPENDED.getCode()));

			if (tDrv != null && tDrv.getDrvStatus() == 'A'
					&& (tDrv.getTCoreAccn() != null && tDrv.getTCoreAccn().getAccnStatus() == 'A')) {

				String encryptedPassword = ckCtDrvService.encryptDriverPwd(usrPassword);
				if (encryptedPassword.equalsIgnoreCase(tDrv.getDrvMobilePassword())) {
					// password correct
					Boolean isConLoginEnabled = Boolean.TRUE;
					String concurrentLogin = sysParamService.getValString(SysParamConstant.CONCURRENT_LOGIN_ENABLE);
					if (concurrentLogin != null && !concurrentLogin.trim().isEmpty() && concurrentLogin.equals("N"))
						isConLoginEnabled = Boolean.FALSE;

					if (isConLoginEnabled && sessionService.isExistingSession(drvId)) {
						// if Y = proceed, continue
						String timeTokillSession = sysParamService.getValString(SysParamConstant.CONCURRENT_LOGIN_TIME);
						TCoreSession session = sessionService.getCoreSessionByUserId(drvId);
						LocalDateTime dateTime = LocalDateTime.ofInstant(session.getSessTimeLastaccess().toInstant(),
								ZoneId.systemDefault());
						LocalDateTime dateTime2 = LocalDateTime.now();
						long diffInMin = java.time.Duration.between(dateTime, dateTime2).toMinutes();
						if (timeTokillSession.isEmpty() || diffInMin <= Long.valueOf(timeTokillSession)) {
							if (!"on".equalsIgnoreCase(proceed)) {
								request.getSession().setAttribute(LoginStatus.USER_SESSION_EXISTING.toString(),
										new Boolean(true));
								return LoginStatus.USER_SESSION_EXISTING;
							} else {
								sessionService.removeExistingSessions(drvId);
							}
						} else {
							sessionService.removeExistingSessions(drvId);
						}
					}

					tDrv.setDrvLoginFailure((short) 0);
					drvDao.update(tDrv);

					java.util.Date date = new java.util.Date();
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);

					// Number of pswd valid days loading from configuration
					int maxNumPswdValidDays = 0;
					String maxNumOfPwdValid = sysParamService.getValString(SysParamConstant.MAX_NUM_PSWD_VALID_DAYS);
					if (maxNumOfPwdValid != null && !maxNumOfPwdValid.trim().isEmpty())
						maxNumPswdValidDays = Integer.valueOf(maxNumOfPwdValid);
					else
						maxNumPswdValidDays = Constant.MAX_NUM_PSWD_VALID_DAYS;

					cal.add(Calendar.DATE, -maxNumPswdValidDays);
					// java.util.Date pwLstUpdate = foundUsr.getUsrDtPwdLupd();
					java.util.Date pwLstUpdate = tDrv.getDrvDtLupd(); // not correct
					java.util.Date beforeConfigDays = cal.getTime();

					log.debug("Date validation on pw pwLstUpdate:" + pwLstUpdate + " beforeConfigDays:"
							+ beforeConfigDays);

					// Audit Login Activity
					audit(tDrv.getDrvId(), Constant.LOG_EVNT_ADM_USER_LOGIN, tDrv.getDrvId(), tDrv.getDrvName(), null,
							null);

					return LoginStatus.AUTHORIZED_LOGIN;

				} else {
					// password NOT correct
					tDrv.setDrvLoginFailure(tDrv.getDrvLoginFailure() + 1);
					drvDao.update(tDrv);
					return LoginStatus.UNAUTHENTICATED;
				}
			}

			else if (tDrv != null && tDrv.getDrvStatus() == 'S') {
				return LoginStatus.ACCOUNT_SUSPENDED;
			}

			else if (tDrv != null && (tDrv.getDrvStatus() == 'I'
					|| (tDrv.getTCoreAccn() != null && tDrv.getTCoreAccn().getAccnStatus() == 'I'))) {
				return LoginStatus.ACCOUNT_INACTIVE;
				
			} else {
				return LoginStatus.UNAUTHENTICATED;
			}
		} catch (Exception ex) {
			log.error("authorizeLogin", ex);
			return LoginStatus.UNAUTHENTICATED;
		}
	}

	@Transactional
	public Principal createPrincipal(String drvId, HttpServletRequest request, HttpServletResponse response)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("createPrincipal");

		Date now = Calendar.getInstance().getTime();
		ByteArrayOutputStream bos = null;
		try {
			if (null == drvId)
				throw new ParameterException("param drvId null or empty");
			if (null == request)
				throw new ParameterException("param request null");
			if (null == response)
				throw new ParameterException("param response null");

			// Hibernate.initialize(tDrv.getTCoreAccn());

//			TCkCtDrv tDrv = drvDao.find(drvId);
			TCkCtDrv tDrv = drvDao.findByMobileUserId(drvId);

			if (null == tDrv)
				throw new ParameterException("Fail to find driver: " + drvId);

			String sessionId = request.getSession().getId();
			Principal principal = this.newPrincipal(tDrv, sessionId, request);
			if (null == principal)
				throw new ProcessingException("newPrincipal null");

			TCoreSession coreSession = new TCoreSession();
			coreSession.setSessSid(sessionId);
			coreSession.setSessUid(tDrv.getDrvId());
			coreSession.setSessTimeLogin(now);
			coreSession.setSessTimeLastaccess(now);
			coreSession.setSessAccnid(tDrv.getTCoreAccn().getAccnId());
			coreSession.setSessTimePrincipalCache(now);

			bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(principal);
			out.flush();
			coreSession.setSessObj(bos.toByteArray());

			coreSessionDao.saveOrUpdate(coreSession);

			return principal;
		} catch (ParameterException ex) {
			log.error("createPrincipal", ex);
			throw ex;
		} catch (Exception ex) {
			log.error("createPrincipal", ex);
			throw new ProcessingException(ex);
		} finally {
			try {
				if (null != bos)
					bos.close();
			} catch (IOException ex) {
				log.error("createPrincipal", ex);
			}
		}
	}

	/**
	 * @param coreUsr
	 * @param sessionId
	 * @param request
	 * @param response
	 * @return
	 * @throws ParameterException
	 * @throws ProcessingException
	 */
	private Principal newPrincipal(TCkCtDrv tDrv, String sessionId, HttpServletRequest request)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		log.debug("newPrincipal");

		try {
			if (null == tDrv)
				throw new ParameterException("param tDrv null");
			if (StringUtils.isEmpty(sessionId))
				throw new ParameterException("param sessionId null or empty");
			if (null == request)
				throw new ParameterException("param request null");

			log.debug("tDrv: " + tDrv);

			Principal principal = new Principal();
			principal.setSessionId(sessionId);
			principal.setUserId(tDrv.getDrvMobileId());
			principal.setUserName(tDrv.getDrvName());
//			principal.setUserEmail(tDrv.getDrvEmail());
			principal.setAppsCode(ServiceTypes.CLICTRUCK.getAppsCode());

			principal.setUserAccnId(tDrv.getTCoreAccn().getAccnId());
			principal.setUri("/");

			log.info("context url" + principal.getContextUrl());

			principal.setRoleList(Arrays.asList(Roles.DRIVER.name()));
			CoreAccn accnDrvr = new CoreAccn(tDrv.getTCoreAccn());
			MstAccnType accnType = new MstAccnType();
			accnType.setAtypId(AccountTypes.ACC_TYPE_TO.name());
			accnDrvr.setTMstAccnType(accnType);
			principal.setCoreAccn(accnDrvr);

			return principal;
		} catch (Exception ex) {
			log.error("newPrincipal", ex);
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * @param email
	 * @throws Exception
	 */
	@Transactional
	public String forgotPassword(String mobileId, String phone) throws Exception {
		
		if (mobileId == null || mobileId.isEmpty())
			throw new EntityNotFoundException("User Id cannot empty");
		
		TCkCtDrv tDrv = drvDao.findByMobileUserId(mobileId);
		if (tDrv == null)
			throw new EntityNotFoundException("User Id does not exist");
		
		if (phone == null || phone.isEmpty())
			throw new EntityNotFoundException("Phone Number cannot empty");
		
		if (!isValidPhoneNumber(phone))
			throw new EntityNotFoundException("Mobile phone numbers must use country code");
		
		TCkCtDrv tCkCtDrv = getUserByPhone(mobileId, phone);
		if (tCkCtDrv == null)
			throw new EntityNotFoundException("user with phone does not exist. please try again");
		
		String msg = ckCtDrvService.resetDriverPassword(tCkCtDrv.getDrvId(), NotificationTemplateName.FORGOT_PASSWORD.name());
		return msg;
/*-
		CkCtDrv ckCtDrv = new CkCtDrv();
		BeanUtils.copyProperties(tCkCtDrv, ckCtDrv);

		String randomPassword = PasswordGenerator.generatePassword(5);
		String token = PasswordEncryptor.encrypt(ckCtDrv.getDrvMobileId(), RESET_PASSWORD+randomPassword);
		
		tCkCtDrv.setDrvMobilePassword(token);
		drvDao.saveOrUpdate(tCkCtDrv);
		String msgBody = "UserId : "+mobileId+" password : "+RESET_PASSWORD+randomPassword;
		String phoneNumber = phone.replaceAll("\\+", "");
		ArrayList<String> text = new ArrayList<>();
		text.add(tDrv.getDrvName());
		text.add(msgBody);
		whatsappYCloudService.sendYCloudWhatAppMsg(null, phoneNumber, text,null, YCloudTemplateName.FORGOT_PASSWORD.getDesc());
		return msgBody;
*/
	}
	
	public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "\\+\\d{1,}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

	/// Helper Methods
	///////////////////////
	private TCkCtDrv getUserByPhone(String mobileId, String phone) throws EntityNotFoundException {
		try {
			phone = phone.replaceAll("\\D+", "");
			if(phone.length() < 8) {
				throw new Exception("Phone number length is not correct!");
			}
			Map<String, Object> param = new HashMap<>();
			param.put("drvPhone", "%" + phone);
			param.put("drvMobileId", mobileId);
			
			String sql = "SELECT o FROM TCkCtDrv o WHERE o.drvPhone like :drvPhone AND o.drvMobileId = :drvMobileId ";
			List<TCkCtDrv> tCkCtDrvs = ckCtDrvDao.getByQuery(sql, param);
			if (tCkCtDrvs == null || tCkCtDrvs.size() == 0) {
				throw new Exception("Not match " + phone + " with id " + mobileId);
			} else {
				TCkCtDrv ckCtDrv = tCkCtDrvs.get(0);
				if (ckCtDrv.getDrvStatus() != Constant.ACTIVE_STATUS) {
					throw new Exception(phone + " is locked. Please contact administrator.");
				}
				return tCkCtDrvs.get(0);
			}
		} catch (Exception ex) {
			log.error("getUserByPhone", ex);
			throw new EntityNotFoundException(ex.getMessage());
		}
	}

	/**
	 * Copy from PortalUserService
	 * 
	 * @param recKey
	 * @param auditv
	 * @param userId
	 * @param userName
	 * @param auditRemarks
	 * @param auditParamKey
	 * @param auditParams
	 * @throws Exception
	 */
	private void audit(String recKey, String auditv, String userId, String userName, String auditRemarks,
			String auditParamKey, String... auditParams) throws Exception {

		Calendar now = Calendar.getInstance();

		if (StringUtils.isNotEmpty(userId)) {

			TCoreAuditlog TCoreAuditlog = new TCoreAuditlog(null, auditv, now.getTime(), userId, recKey);
			TCoreAuditlog.setAudtRemarks(auditRemarks);
			TCoreAuditlog.setAudtUname(userName);

			auditLogDao.add(TCoreAuditlog);

		} else {
			log.error("Unable to obtain principal user for " + auditv + " with message '" + auditRemarks + "' at "
					+ now.getTime() + " in " + recKey);
		}

	}

	/**
	 * @param dto
	 * @throws Exception
	 */
	public void changePw(ChangeMobilePassword dto) throws Exception {

		Principal principal = ckSession.getPrincipal();
		if (null == principal)
			throw new ProcessingException("principal is null");

		if (dto == null)
			throw new ParameterException("param dto null");

		TCkCtDrv tCkCtDrv = getDrvById(principal.getUserId());
		if (tCkCtDrv == null)
			throw new EntityNotFoundException("user with driver id does not exist. please try again");

		String oldPassword = ckCtDrvService.encryptDriverPwd(dto.getOldPass());

		if (tCkCtDrv.getDrvMobilePassword().equalsIgnoreCase(oldPassword)) {
			if (isValidasiString(dto.getNewPass())) {
				String newPassword = ckCtDrvService.encryptDriverPwd(dto.getNewPass());
				
				tCkCtDrv.setDrvMobilePassword(newPassword);
				ckCtDrvDao.saveOrUpdate(tCkCtDrv);
			} else {
				throw new ProcessingException(
						"Password must be at least 6 characters, containing uppercase letter, lowercase letter, number and special character.");
			}
		} else {
			throw new ProcessingException("Your existing password does not match");
		}

	}

	/**
	 * @param drvId
	 * @return
	 * @throws EntityNotFoundException
	 */
	private TCkCtDrv getDrvById(String mobileId) throws EntityNotFoundException {
		try {
			Map<String, Object> param = new HashMap<>();
			param.put("drvMobileId", mobileId);
			String sql = "SELECT o FROM TCkCtDrv o WHERE o.drvMobileId = :drvMobileId";
			List<TCkCtDrv> tCkCtDrvs = ckCtDrvDao.getByQuery(sql, param);
			if (tCkCtDrvs == null || tCkCtDrvs.size() == 0) {
				throw new Exception("Fail to find driverId " + mobileId);
			} else {
				TCkCtDrv ckCtDrv = tCkCtDrvs.get(0);
				if (ckCtDrv.getDrvStatus() != Constant.ACTIVE_STATUS) {
					throw new Exception(mobileId + " is locked. Please contact administrator.");
				}
				return tCkCtDrvs.get(0);
			}
		} catch (Exception ex) {
			log.error("getDrvById", ex);
			throw new EntityNotFoundException(ex.getMessage());
		}
	}

	/**
	 * @for isValidasiString
	 * @param inputString
	 * @return
	 */
	private boolean isValidasiString(String inputString) {
		String regex = "^(?=.*[0-9])(?=.*[@#$%^&+=!])(?=\\S+$).{6,999}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(inputString);
		return matcher.matches();
	}

	@Transactional
	public boolean maxLoginFailure(String mobileId) throws Exception {
		Optional<TCkCtDrv> opTCkCtDrv = Optional.ofNullable(drvDao.findByMobileUserId(mobileId));
		Optional<Integer> loginRetries = Optional
				.ofNullable(Integer.valueOf(sysParamService.getValString(CLICTRUCK_MOBILE_LOGIN_RETRIES, "5")));
		if (opTCkCtDrv.isPresent() && loginRetries.isPresent()
				&& opTCkCtDrv.get().getDrvLoginFailure() >= loginRetries.get()) {
			opTCkCtDrv.get().setDrvStatus(RecordStatus.SUSPENDED.getCode());
			ckCtDrvDao.update(opTCkCtDrv.get());
			return true;
		}
		return false;
	}
	
}
