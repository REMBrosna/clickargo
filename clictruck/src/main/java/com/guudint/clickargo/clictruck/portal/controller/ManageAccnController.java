package com.guudint.clickargo.clictruck.portal.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.admin.dto.ForgotPassword;
import com.guudint.clickargo.clictruck.portal.service.ManageAccnService;
import com.guudint.clickargo.controller.CustomSerializerProvider;
import com.vcc.camelone.ccm.dto.PortalAccn;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;

/*
 * This is a PortEDI implementation of the user listing. C1 core did not check
 * the principal to filter the listing by the current user's account ID. This
 * this instead of passing the account from the frontEnd to do the filtering.
 * Just to be consistent that principal checks are done in backend.
 */
@RequestMapping(value = "/api/v1/clickargo/clictruck/manageaccn")
@CrossOrigin
@RestController
@Deprecated
public class ManageAccnController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ManageAccnController.class);

	@Autowired
	private ManageAccnService manageAccnService;

	protected ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void configureObjectMapper() {
		objectMapper.setSerializerProvider(new CustomSerializerProvider());
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public ResponseEntity<Object> newAccn(@RequestBody PortalAccn portalAccn) {
		log.debug("newAccn");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			
			PortalAccn opEntity = manageAccnService.createNewAccn(portalAccn);
			manageAccnService.createAccnAdmin(opEntity.getAccnDetails());
			return ResponseEntity.ok(objectMapper.writeValueAsString(opEntity));

		} catch (Exception ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/{accnId}", method = RequestMethod.PUT)
	public ResponseEntity<Object> updateAccn(@RequestBody  PortalAccn portalAccn, @PathVariable String accnId) {
		log.debug("updateAccn");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			PortalAccn opEntity = manageAccnService.updateAccn(accnId, portalAccn);
			return ResponseEntity.ok(objectMapper.writeValueAsString(opEntity));

		} catch (Exception ex) {
			log.error("updateUserStatus", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<Object> getEntitiesBy(@RequestParam Map<String, String> params) {
		log.debug("getEntitiesBy");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			Optional<Object> opEntity = manageAccnService.getEntitiesByProxy(params);
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
			//manageUserService.forgotPassword(dto.getEmail());

			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("message", "Password reset request was sent successfully. Please check your email to reset your password");
			objectMapping.put("timestamp", new Date());
			return ResponseEntity.ok(objectMapping);
		} catch (Exception ex) {
			log.error("forgetPassword", ex);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

}
