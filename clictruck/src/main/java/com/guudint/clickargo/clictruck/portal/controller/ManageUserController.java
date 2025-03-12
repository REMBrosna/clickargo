package com.guudint.clickargo.clictruck.portal.controller;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.admin.dto.ChangePassword;
import com.guudint.clickargo.admin.dto.ClickargoUser;
import com.guudint.clickargo.admin.dto.ForgotPassword;
import com.guudint.clickargo.admin.service.ClickargoManageUserService;
import com.guudint.clickargo.clictruck.portal.service.CkUserUtilService;
import com.guudint.clickargo.controller.CustomSerializerProvider;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.PortalUser;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.PermissionException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.core.model.TCoreSession;
import com.vcc.camelone.master.controller.PathNotFoundException;

@RequestMapping(value = "/api/v1/clickargo/clictruck/manageusr")
@CrossOrigin
@RestController
public class ManageUserController extends AbstractPortalController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ManageUserController.class);

	@Autowired
	private ClickargoManageUserService clickargoUserService;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	private CkUserUtilService ckUserUtilService;

	protected ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void configureObjectMapper() {
		objectMapper.setSerializerProvider(new CustomSerializerProvider());
	}

	@GetMapping(value = "/new")
	public ResponseEntity<Object> newUser() {
		log.debug("newUser");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			ClickargoUser opEntity = clickargoUserService.getNewUser();
			return ResponseEntity.ok(objectMapper.writeValueAsString(opEntity));

		} catch (PermissionException ex) {
			log.error("newUser", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("newUser", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/{usrId}")
	public ResponseEntity<Object> getPortalUser(@PathVariable String usrId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isEmpty(usrId))
				throw new Exception("param usrId empty or null");

			Principal principal = getPrincipal();
			if (null == principal)
				throw new ProcessingException("principal is null");

			String encryptedUsrId = usrId.equalsIgnoreCase("profile") ? principal.getUserId()
					: new String(Base64.getDecoder().decode(usrId));

			if (encryptedUsrId.equals("0") || encryptedUsrId.equals("-")) {
				return newUser();
			}

			ClickargoUser portalUser = clickargoUserService.getUser(encryptedUsrId);
			if (null == portalUser)
				throw new Exception("protalUser not found: " + encryptedUsrId);

			// enhancement for managing department
			portalUser.getCoreUsr().setUsrDept(ckUserUtilService.getUserDepartment(portalUser.getCoreUsr()));

			List<TCoreSession> userSessions = clickargoUserService.getCoreSessionByUser(encryptedUsrId);
			if (null != userSessions && userSessions.size() > 0)
				portalUser.setLoggedIn(true);

			return ResponseEntity.ok(portalUser);

		} catch (PermissionException ex) {
			log.error("getPortalUser", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (PathNotFoundException ex) {
			log.error("getPortalUser", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("getPortalUser", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/check-id-availability")
	public ResponseEntity<Object> checkUserIdAvailability(@RequestParam String usrId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			ClickargoUser portalUser = null;
			// If method getUser throw error EntityNotFoundException mean user not exit,
			try {
				if (StringUtils.isNotBlank(usrId))
					portalUser = clickargoUserService.getUser(usrId);
			} catch (Exception ex) {
				Map<String, Object> response = new HashMap<>();
				response.put("available", true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			if (portalUser != null)
				throw new Exception("User already exists with id: " + usrId);
			return null;
		} catch (PermissionException ex) {
			log.error("checkUserIdAvailability", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("checkUserIdAvailability", ex);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		}
	}

	@GetMapping(value = "/check-email-availability")
	public ResponseEntity<Object> checkEmailAvailability(@RequestParam String usrId, @RequestParam String email) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isEmpty(usrId))
				throw new Exception("param usrId empty or null");

			if (StringUtils.isEmpty(email))
				throw new Exception("param usrId empty or null");

			ClickargoUser portalUser;

			try {
				portalUser = clickargoUserService.getByIdAndEmail(usrId, email);
			} catch (Exception ex) {
				Map<String, Object> response = new HashMap<>();
				response.put("available", true);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			if (portalUser != null && !portalUser.getCoreUsr().getUsrUid().equalsIgnoreCase(usrId))
				throw new Exception("Email already exists with id: " + email);
			return null;
		} catch (PermissionException ex) {
			log.error("checkEmailAvailability", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("checkEmailAvailability", ex);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		}
	}

	@PostMapping(value = "/create")
	public ResponseEntity<Object> createUser(@RequestBody ClickargoUser portalUser) {
		log.debug("createUser");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			ClickargoUser opEntity = clickargoUserService.createUser(portalUser);
			return ResponseEntity.ok(objectMapper.writeValueAsString(opEntity));

		} catch (PermissionException ex) {
			log.error("createUser", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("createUser", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(value = "/update/{action}/{usrId}")
	public ResponseEntity<Object> updateUserStatus(@PathVariable String action, @PathVariable String usrId) {
		log.debug("updateUserStatus");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isBlank(action))
				throw new ParameterException("param action is null or empty");
			if (StringUtils.isBlank(usrId))
				throw new ParameterException("param usr Id is null or empty");

			String encryptedUsrId = new String(Base64.getDecoder().decode(usrId));

			ClickargoUser opEntity = clickargoUserService.updateUserStatus(action, encryptedUsrId);

			// PORTEDI-1921 to force logout the user.
//			Optional<CoreUsr> opCoreUsr = Optional.of(opEntity.getCoreUsr());
//			if (opCoreUsr.isPresent() && opCoreUsr.get().getUsrStatus() == RecordStatus.INACTIVE.getCode()
//					&& action.equalsIgnoreCase(UserAction.DEACTIVATE.getAction())) {
//				LogoutEvent event = new LogoutEvent(this, encryptedUsrId, Action.FORCE_LOGOUT);
//				eventPublisher.publishEvent(event);
//			}

			return ResponseEntity.ok(objectMapper.writeValueAsString(opEntity));

		} catch (PermissionException ex) {
			log.error("updateUserStatus", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("updateUserStatus", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(value = "/{action}/{usrId}")
	public ResponseEntity<Object> updateUser(@PathVariable String action, @PathVariable String usrId,
			@RequestBody ClickargoUser portalUser) {
		log.debug("updateUser");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isBlank(action))
				throw new ParameterException("param action is null or empty");
			if (StringUtils.isBlank(usrId))
				throw new ParameterException("param usrId is null or empty");

			String encryptedUsrId = new String(Base64.getDecoder().decode(usrId));

//			boolean isRolesChanged = false;
//			if (action.equals(UserAction.SAVE.getAction())) {
//				PortalUser dbPortalUser = clickargoUserService.getUser(encryptedUsrId);
//				if (dbPortalUser != null && dbPortalUser.getHoldRoleList() != null
//						&& portalUser.getHoldRoleList() != null
//						&& dbPortalUser.getHoldRoleList().size() != portalUser.getHoldRoleList().size()) {
//					isRolesChanged = true;
//				}
//			}

			ClickargoUser opEntity = clickargoUserService.updateUserByAction(action, encryptedUsrId, portalUser);
			// To force logout the user.
//			Optional<CoreUsr> opCoreUsr = Optional.of(opEntity.getCoreUsr());
//			if ((opCoreUsr.isPresent() && opCoreUsr.get().getUsrStatus() == RecordStatus.INACTIVE.getCode()
//					&& action.equalsIgnoreCase(UserAction.DEACTIVATE.getAction())) || isRolesChanged) {
//
//				LogoutEvent event = new LogoutEvent(this, encryptedUsrId, Action.FORCE_LOGOUT);
//				eventPublisher.publishEvent(event);
//
//			}

//			if (isRolesChanged) {
//				LogoutEvent event = new LogoutEvent(this, encryptedUsrId, Action.FORCE_LOGOUT);
//				eventPublisher.publishEvent(event);
//			}
//			
			return ResponseEntity.ok(objectMapper.writeValueAsString(opEntity));

		} catch (PermissionException ex) {
			log.error("updateUser", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("updateUser", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/list")
	public ResponseEntity<Object> getEntitiesBy(@RequestParam Map<String, String> params) {
		log.debug("getEntitiesBy");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			Optional<Object> opEntity = clickargoUserService.getEntitiesByProxy(params);
			return ResponseEntity.ok(opEntity.get());
		} catch (PathNotFoundException ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/forgotpwd")
	public ResponseEntity<Object> forgetPassword(@RequestBody ForgotPassword dto) {
		log.debug("forgetPassword");
		try {
			clickargoUserService.forgotPassword(dto.getEmail());

			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("message",
					"Password reset request was sent successfully. Please check your email to reset your password");
			objectMapping.put("timestamp", new Date());
			return ResponseEntity.ok(objectMapping);
		} catch (Exception ex) {
			log.error("forgetPassword", ex);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/chgpwd")
	public ResponseEntity<Object> changePassword(@RequestBody ChangePassword dto) {
		log.debug("changePassword");
		try {
			clickargoUserService.changePassword(dto);

			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.SUCCESS);
			serviceStatus.setData("Password has been changed successfully");
			return ResponseEntity.ok(serviceStatus);
		} catch (Exception ex) {
			log.error("changePassword", ex);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, "Fail to change password: " + ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Update User
	 * 
	 * @param object
	 * @param entity
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Object> updatePortalUser(@RequestBody PortalUser portalUser, @PathVariable String id) {

		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			PortalUser portalUserUpdated = clickargoUserService.updateUserProfile(portalUser);

			return ResponseEntity.ok(portalUserUpdated);

		} catch (PathNotFoundException ex) {
			log.error("createEntity", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("createEntity", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/fetchRoles/{accId}")
	public ResponseEntity<Object> findRolesByAccId(@PathVariable String accId) {
		log.debug("findRolesByAccId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(objectMapper.writeValueAsString(clickargoUserService.findRolesByAccId(accId)));
		} catch (PermissionException ex) {
			log.error("findRolesByAccId", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("findRolesByAccId", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

}
