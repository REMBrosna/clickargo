package com.guudint.clickargo.clictruck.apigateway.auth.services;

import com.guudint.clickargo.clictruck.apigateway.dto.*;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.cac.services.ICOSession;
import com.vcc.camelone.cac.services.impl.SessionService;
import com.vcc.camelone.ccm.model.TCoreUsr;
import com.vcc.camelone.ccm.service.IPortalUser;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.coas.security.JwtUser;
import com.vcc.camelone.coas.security.authentication.utils.JwtUtil;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.SysParamService;
import com.vcc.camelone.config.dao.CoreSysparamDao;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.core.model.TCoreSession;
import com.vcc.camelone.util.crypto.PasswordEncryptor;
import com.vcc.camelone.util.email.SysParam;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import static com.guudint.clickargo.clictruck.apigateway.auth.services.ApiGatewayAuthService.LoginStatus.*;
import static com.guudint.clickargo.clictruck.apigateway.dto.AccountTypeEnum.getValueAccountType;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@Service
public class ApiGatewayAuthService {
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(ApiGatewayAuthService.class);
    @Autowired
    private GenericDao<TCoreUsr, String> coreUserDao;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private GenericDao<TCoreAuditlog, String> auditLogDao;
    @Autowired
    @Qualifier("cacSessService")
    private ICOSession coSessionService;
    @Autowired
    protected MessageSource messageSource;
    @Autowired
    private IPortalUser portalUser;
    @Autowired
    @Qualifier("jwtUtil")
    private JwtUtil jwtUtil;
    @Autowired
    private SysParam sysParam;
    @Autowired
    private CoreSysparamDao coreSysparamDao;
    public enum LoginStatus {UNAUTHENTICATED, INCORRECT_PASSWORD, UNAUTHORIZED, AUTHORIZED_LOGIN,
        AUTHORIZED_LOGIN_CHNG_PW_RQD, ACCOUNT_SUSPENDED, ACCOUNT_INACTIVE, USER_SESSION_EXISTING;
    }

    public ResponseEntity<?> handleLogin(UserCredentials userCredentials, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Starting user authentication for user ID: {}", userCredentials.getUser());

        log.debug("Validating user credentials");
        validateCredentials(userCredentials);

        // Fetch user details
        TCoreUsr foundUsr = getUserIdOrEmail(userCredentials.getUser());
        String errMsg;
        if (foundUsr == null) {
            errMsg = messageSource.getMessage("message.login.user.invalid", null, LocaleUtils.toLocale("en_US"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(errMsg));
        }

        // Map user status to login status
        ApiGatewayAuthService.LoginStatus userStatus = mapUserStatusToLoginStatus(foundUsr);
        if (userStatus != ApiGatewayAuthService.LoginStatus.AUTHORIZED_LOGIN) {
            return handleLoginFailure(userStatus);
        }

        // Validate password
        String encryptedPassword = PasswordEncryptor.encrypt(foundUsr.getUsrUid(), userCredentials.getPassword());
        if (!encryptedPassword.equals(foundUsr.getUsrPwd())) {
            return handleInvalidPassword(foundUsr);
        }

        // Handle concurrent login
        if (isConcurrentLoginEnabled() && sessionService.isExistingSession(foundUsr.getUsrUid())) {
            handleConcurrentLogin(foundUsr, request);
        }

        // Reset invalid login count and check password expiry
        resetInvalidLoginCount(foundUsr);
        if (isPasswordExpired(foundUsr)) {
            return handleLoginFailure(ApiGatewayAuthService.LoginStatus.AUTHORIZED_LOGIN_CHNG_PW_RQD);
        }

        auditLogin(foundUsr);

        // Generate JWT and return user info
        Principal principal = coSessionService.createPrincipal(foundUsr.getUsrUid(), request, response);
        JwtUser jwtUser = jwtUtil.generateJwtUser(principal, getSessionExpireTime());
        Token token = createToken(jwtUser);
        User user = getUser(jwtUser, token);

        return ResponseEntity.ok().body(user);
    }

    private Token createToken(JwtUser jwtUser) {
        log.debug("Creating token for JWT user: " + jwtUser);
        Token token = new Token();
        token.setPlainTextToken(jwtUtil.generateToken(jwtUser));
        token.setExpiresAt(String.valueOf(jwtUser.getExp()));
        log.debug("Token created successfully");
        return token;
    }
    protected long getSessionExpireTime() throws Exception {
        log.debug("Calculating session expiration time");
        int sessionTimeout = Integer.parseInt(getSysParam("SESSION_MAX_LIFETIME", String.valueOf(120)));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, sessionTimeout);
        log.debug("Session will expire at: " + cal.getTimeInMillis());
        return cal.getTimeInMillis();
    }
    private static User getUser(JwtUser jwtUser, Token token) {
        log.debug("Populating User with JWT user data");
        User user = new User();
        user.setId(jwtUser.getId());
        user.setName(jwtUser.getName());
        user.setEmail(jwtUser.getEmail());
        user.setToken(token);

        log.debug("Populating Company with JWT user company data");
        Company company = new Company();
        company.setId(jwtUser.getCoreAccn().getAccnId());
        company.setName(jwtUser.getCoreAccn().getAccnName());
        company.setType(getValueAccountType(jwtUser.getCoreAccn().getTMstAccnType().getAtypId()));
        user.setCompany(company);

        log.debug("User populated successfully");
        return user;
    }
    private void validateCredentials(UserCredentials userCredentials) throws Exception {
        log.debug("Validating credentials: " + userCredentials);
        if (userCredentials == null ||
                StringUtils.isBlank(userCredentials.getUser()) ||
                StringUtils.isBlank(userCredentials.getPassword())) {
            log.error("Invalid login credentials");
            throw new UsernameNotFoundException("Invalid login credentials");
        }
        //Checking for validated email address
        portalUser.isEmailAddress(userCredentials.getUser());

    }
    private ApiGatewayAuthService.LoginStatus mapUserStatusToLoginStatus(TCoreUsr user) {
        if (user.getUsrStatus() == 'S') {
            return ApiGatewayAuthService.LoginStatus.ACCOUNT_SUSPENDED;
        } else if (user.getUsrStatus() == 'I' ||
                (user.getTCoreAccn() != null && user.getTCoreAccn().getAccnStatus() == 'I')) {
            return ApiGatewayAuthService.LoginStatus.ACCOUNT_INACTIVE;
        }
        return ApiGatewayAuthService.LoginStatus.AUTHORIZED_LOGIN;
    }
    private TCoreUsr getUserIdOrEmail(String usrUidOrEmail) throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usrUidOrEmail", usrUidOrEmail);
        List<TCoreUsr> users = coreUserDao.getByQuery("FROM TCoreUsr u WHERE u.usrUid=:usrUidOrEmail OR u.usrContact.contactEmail=:usrUidOrEmail", parameters);
        return users.isEmpty() ? null : users.get(0);
    }

    private ResponseEntity<?> handleInvalidPassword(TCoreUsr user) throws Exception {
        int maxAttempts = Integer.parseInt(getSysParam("MAX_LOGIN_ATTEMPTS_CONFIG", String.valueOf(Constant.MAX_LOGIN_ATTEMPTS)));
        user.setUsrLoginInvcnt(user.getUsrLoginInvcnt() + 1);
        coreUserDao.update(user);
        String errMsg;
        if (user.getUsrLoginInvcnt() >= maxAttempts) {
            errMsg = messageSource.getMessage("message.login.suspend", null, LocaleUtils.toLocale("en_US"));
            user.setUsrStatus(Constant.SUSPENDED_STATUS);
            coreUserDao.update(user);
            audit(user.getUsrUid(), Constant.LOG_EVNT_ADM_ACCOUNT_SUSPENDED_DUE_TO_INCORRECT_PW, user.getUsrUid(), user.getUsrName(), null, null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(errMsg));
        }else {
            errMsg = messageSource.getMessage("message.login.password.incorrect", null, LocaleUtils.toLocale("en_US"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(errMsg));
    }

    private boolean isConcurrentLoginEnabled() throws Exception {
        return !"N".equalsIgnoreCase(sysParamService.getValString("CONCURRENT_LOGIN_ENABLE"));
    }

    private void handleConcurrentLogin(TCoreUsr user, HttpServletRequest request) throws Exception {
        String timeToKillSession = sysParamService.getValString("CONCURRENT_LOGIN_TIME");
        TCoreSession session = sessionService.getCoreSessionByUserId(user.getUsrUid());
        long sessionAgeInMinutes = Duration.between(
                LocalDateTime.ofInstant(session.getSessTimeLastaccess().toInstant(), ZoneId.systemDefault()),
                LocalDateTime.now()
        ).toMinutes();

        if (!timeToKillSession.isEmpty() && sessionAgeInMinutes > Long.parseLong(timeToKillSession)) {
            sessionService.removeExistingSessions(user.getUsrUid());
        } else {
            request.getSession().setAttribute(USER_SESSION_EXISTING.toString(), true);
        }
    }

    private void resetInvalidLoginCount(TCoreUsr user) throws Exception {
        user.setUsrLoginInvcnt(0);
        coreUserDao.update(user);
    }

    private boolean isPasswordExpired(TCoreUsr user) throws Exception {
        int maxValidDays = Integer.parseInt(getSysParam("MAX_NUM_PSWD_VALID_DAYS", String.valueOf(Constant.MAX_NUM_PSWD_VALID_DAYS)));
        LocalDateTime passwordLastUpdated = user.getUsrDtPwdLupd().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return passwordLastUpdated.isBefore(LocalDateTime.now().minusDays(maxValidDays)) ||
                "Y".equalsIgnoreCase(user.getUsrPwdForce());
    }
    private void auditLogin(TCoreUsr user) throws Exception {
        audit(user.getUsrUid(), Constant.LOG_EVNT_ADM_USER_LOGIN, user.getUsrUid(), user.getUsrName(), null, null);
    }
    private void audit(String recKey, String auditv, String userId, String userName, String auditRemarks, String auditParamKey, String... auditParams) throws Exception {
        Calendar now = Calendar.getInstance();
        if (StringUtils.isNotEmpty(userId)) {
            TCoreAuditlog auditLog = new TCoreAuditlog(null, auditv, now.getTime(), userId, recKey);
            auditLog.setAudtRemarks(auditRemarks);
            auditLog.setAudtUname(userName);
            this.auditLogDao.add(auditLog);
        } else {
            log.error("Unable to obtain principal user for " + auditv + " with message '" + auditRemarks + "' at " + now.getTime() + " in " + recKey);
        }
    }
    protected String getSysParam(String key, String defValue) throws Exception {
        if (StringUtils.isBlank(key))
            throw new ParameterException("param key null or empty");

        TCoreSysparam sysParam = coreSysparamDao.find(key);
        if (sysParam == null)
            return defValue;

        return sysParam.getSysVal();

    }
    //Login Validation API GATEWAY
    private ResponseEntity<MessageResponse> handleLoginFailure(ApiGatewayAuthService.LoginStatus loginStatus) {
        String errMsg;
        HttpStatus httpStatus;

        switch (loginStatus) {
            case ACCOUNT_INACTIVE:
                errMsg = messageSource.getMessage("message.login.inactive", null, LocaleUtils.toLocale("en_US"));
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            case ACCOUNT_SUSPENDED:
                errMsg = messageSource.getMessage("message.login.suspend", null, LocaleUtils.toLocale("en_US"));
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            case INCORRECT_PASSWORD:
                errMsg = messageSource.getMessage("message.login.password.incorrect", null, LocaleUtils.toLocale("en_US"));
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
            default:
                errMsg = messageSource.getMessage("message.login.user.invalid", null, LocaleUtils.toLocale("en_US"));
                httpStatus = HttpStatus.UNAUTHORIZED;
                break;
        }
        return ResponseEntity.status(httpStatus).body(new MessageResponse(errMsg));
    }
}