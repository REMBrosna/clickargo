package com.guudint.clickargo.clictruck.planexec.job.mobile.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clicdo.security.controller.ClicTruckAuthServiceController;
import com.guudint.clickargo.clictruck.admin.dto.ChangeMobilePassword;
import com.guudint.clickargo.clictruck.admin.dto.ForgotMobilePassword;
import com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl.CkJobTruckMobileAuthService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.cac.services.ICOSession;
import com.vcc.camelone.ccm.service.impl.PortalUserService.LoginStatus;
import com.vcc.camelone.coas.security.JwtAuthResponse;
import com.vcc.camelone.coas.security.JwtUser;
import com.vcc.camelone.coas.security.authentication.UserCredentials;
import com.vcc.camelone.coas.security.authentication.utils.JwtUtil;
import com.vcc.camelone.coas.security.controller.impl.ServiceController;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.util.email.SysParam;

/**
 * Mobile authentication controller
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/mobile/auth")
public class TruckJobMobileAuthController extends ServiceController{

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ClicTruckAuthServiceController.class);

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private CkJobTruckMobileAuthService mobileAuthService;

	@Autowired
	@Qualifier("cacSessService")
	private ICOSession coSessionService;

	@Autowired
	private SysParam sysParam;

	@Override
	@PostMapping(value = "/login", headers = "Accept=application/json")
	public JwtAuthResponse login(@RequestBody UserCredentials userCredentials, HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		log.debug("login");

		JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
		try {
			if (null == userCredentials || StringUtils.isEmpty(userCredentials.getId())
					|| StringUtils.isEmpty(userCredentials.getPassword()))
				throw new Exception("param null");
			
			String driverId = userCredentials.getId();

			// Check username and password
			LoginStatus loginStatus = mobileAuthService.authenticate(userCredentials.getId(), userCredentials.getPassword(), request, response);
			log.debug("LoginStatus= " + loginStatus);

			jwtAuthResponse.setLoginStatus(loginStatus);

			if (LoginStatus.AUTHORIZED_LOGIN == loginStatus) {

				// generate principal
				Principal principal = mobileAuthService.createPrincipal(driverId, request, response);

				JwtUser jwtUser = jwtUtil.generateJwtUser(principal, this.getSessionExpireTime());

				jwtAuthResponse.setToken(jwtUtil.generateToken(jwtUser));
				jwtAuthResponse.setUser(jwtUser);
			} else {
				// login failed.
				Locale defaultLocale = LocaleUtils.toLocale("en_US");
				String errMsg = "";
				if (LoginStatus.ACCOUNT_INACTIVE == loginStatus) {
					errMsg = messageSource.getMessage("message.login.inactive", new Object[] {}, defaultLocale);
				} else if(LoginStatus.ACCOUNT_SUSPENDED == loginStatus) {
					errMsg = messageSource.getMessage("message.login.suspend", new Object[] {}, defaultLocale);
				} else {
					// LoginStatus.UNAUTHORIZED
					boolean maxRetriesLogin = mobileAuthService.maxLoginFailure(driverId);
					if(maxRetriesLogin) {
						errMsg = messageSource.getMessage("message.max.retries.login", new Object[] {}, defaultLocale);
					} else {
						errMsg = messageSource.getMessage("message.login.failure", new Object[] {}, defaultLocale); 
					}
				}
				jwtAuthResponse.setErr(errMsg);
			}
		} catch (Exception ex) {
			log.error("login", ex);
			jwtAuthResponse.setErr(ex.getMessage());
		}
		log.debug("---: login :---");
		return jwtAuthResponse;
	}
	
	/**
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping("/forgotpwd")
	public ResponseEntity<Object> forgetPassword(@RequestBody ForgotMobilePassword dto) {
		log.debug("forgetPassword");
		try {
			String newPw = mobileAuthService.forgotPassword(dto.getDrvMobileId(), dto.getDrvPhone());

			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("message",
					"Password reset request was sent successfully. Please check your email or whatsapp message ");
			objectMapping.put("timestamp", new Date());
			//objectMapping.put("Reset Password", newPw);
			
			return ResponseEntity.ok(objectMapping);
		} catch (Exception ex) {
			log.error("forgetPassword", ex);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping("/changePwd")
	public synchronized ResponseEntity<Object> changePw(@RequestBody ChangeMobilePassword dto) {
		
		log.debug("changePw");
		try {
			mobileAuthService.changePw(dto);

			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("message", "Password successfully to change");
			objectMapping.put("timestamp", new Date());
			return ResponseEntity.ok(objectMapping);
		} catch (Exception ex) {
			log.error("changePw", ex);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	protected long getSessionExpireTime() {

		int sessionTimeout = sysParam.getValInteger("SESSION_MAX_LIFETIME_DRIVER", 8 * 60);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, sessionTimeout);
		return cal.getTimeInMillis();
	}
}
