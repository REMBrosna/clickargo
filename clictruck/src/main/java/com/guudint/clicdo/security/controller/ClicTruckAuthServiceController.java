package com.guudint.clicdo.security.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.cac.services.IAuthorize;
import com.vcc.camelone.cac.services.ICOSession;
import com.vcc.camelone.cac.services.PermExMenu;
import com.vcc.camelone.ccm.service.IPortalUser;
import com.vcc.camelone.ccm.service.impl.PortalUserService.LoginStatus;
import com.vcc.camelone.coas.security.JwtAuthResponse;
import com.vcc.camelone.coas.security.JwtUser;
import com.vcc.camelone.coas.security.authentication.UserCredentials;
import com.vcc.camelone.coas.security.authentication.utils.JwtUtil;
import com.vcc.camelone.coas.security.controller.impl.ServiceController;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

/**
 * ClicTruck auth service controller. When bypassing login from clickargo
 */
@RestController("clicTruckAuthController")
@RequestMapping(value = "/api/v1/clickargo/clictruck/auth")
public class ClicTruckAuthServiceController extends ServiceController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ClicTruckAuthServiceController.class);

	@Autowired
	@Qualifier("authorizeService")
	private IAuthorize authorizeService;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	@Autowired
	private IPortalUser portalUser;

	@Autowired
	@Qualifier("cacSessService")
	private ICOSession coSessionService;

	@Autowired
	@Qualifier("jwtUtil")
	private JwtUtil jwtUtil;

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

			String userId = null;

			if (portalUser.isEmailAddress(userCredentials.getId())) {
				userId = portalUser.findUserIdByEmail(userCredentials.getId()).getUsrUid();
			} else {
				userId = userCredentials.getId();
			}

			// Check username and password
			LoginStatus loginStatus = portalUser.authenticate(userId, userCredentials.getPassword(), request, response,
					'A', 'S');
			log.debug("LoginStatus= " + loginStatus);

			jwtAuthResponse.setLoginStatus(loginStatus);

			if (LoginStatus.AUTHORIZED_LOGIN == loginStatus || LoginStatus.AUTHORIZED_LOGIN_CHNG_PW_RQD.equals(loginStatus)) {

				// generate principal
				Principal principal = coSessionService.createPrincipal(userId, request, response);

				JwtUser jwtUser = jwtUtil.generateJwtUser(principal, getSessionExpireTime());

				jwtAuthResponse.setToken(jwtUtil.generateToken(jwtUser));
				jwtAuthResponse.setUser(jwtUser);
			} else {
				// login failed.
				Locale defaultLocale = LocaleUtils.toLocale("en_US");
				String errMsg = "";
				if (LoginStatus.ACCOUNT_INACTIVE == loginStatus || LoginStatus.ACCOUNT_SUSPENDED == loginStatus) {
					errMsg = messageSource.getMessage("message.login.inactive", new Object[] {}, defaultLocale);
				} else {
					// LoginStatus.UNAUTHORIZED
					errMsg = messageSource.getMessage("message.login.failure", new Object[] {}, defaultLocale);
				}
				jwtAuthResponse.setErr(errMsg);
			}
			/*-
			  Principal principal = sessionService.getPrincipal(request, response); 
			  if (null == principal) 
			  	principal = sessionService.createPrincipal(userCredentials.getId(), request, response);
			 */
		} catch (Exception ex) {
			log.error("login", ex);
			jwtAuthResponse.setErr(ex.getMessage());
		}
		log.debug("---: login :---");
		return jwtAuthResponse;
	}

	/**
	 * @param userId
	 * @return
	 */
	@GetMapping(value = "/menu")
	public ResponseEntity<Object> permMenu() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			Principal principal = ckSession.getPrincipal();
			if (principal == null)
				throw new ParameterException("principal is null");

			log.info("userId::" + principal.getUserId());
			List<PermExMenu> permExMenus = authorizeService.getPermMenu(principal, true);
			List<PermExMenu> sorted = permExMenus.stream().sorted(Comparator.comparing(PermExMenu::getSeq))
					.collect(Collectors.toList());

			ObjectMapper objMapper = new ObjectMapper();
			objMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
			objMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			objMapper.setSerializationInclusion(Include.NON_NULL);
			return ResponseEntity.ok(objMapper.writeValueAsString(sorted));

		} catch (Exception ex) {
			log.error("permUri", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
}
