package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.TruckJobNoSessionService;
import com.guudint.clickargo.common.service.impl.CKEncryptionUtil;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;

/**
 * Controller for non-session job truck updates. Example: A link from email
 * notification to go to the page and approve/reject. TODO To convert this to an
 * entity controller in future when other modules require the same
 * functionality.
 */
@RequestMapping(value = "/api/v1/clickargo/clictruck/job/ns/")
@CrossOrigin
@Controller
public class TruckJobNoSessionController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TruckJobNoSessionController.class);

	@Autowired
	private TruckJobNoSessionService jobTruckNoSessionService;

	private ObjectMapper objectMapper = new ObjectMapper();

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * This is for overriding the getByEntity with encryption with key
	 * accnId+userId. This is applicable for non-session view.
	 */
	@GetMapping("truck/view/{id}/{encDate}")
	public ResponseEntity<Object> getEntityByIdDate(@PathVariable String id, @PathVariable String encDate) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			String decJobId = CKEncryptionUtil.decrypt(id, "000");
			String decDateStr = CKEncryptionUtil.decrypt(encDate, decJobId);
			Date validDate = sdf.parse(decDateStr);
			Calendar now = Calendar.getInstance();
			// If current date is after decDateStr, it's not valid anymore
			if (now.getTime().compareTo(validDate) >= 1) {
				Map<String, Object> validateErrParam = new HashMap<>();
				validateErrParam.put("invalid-link", "The link is no longer valid!");
				throw new ValidationException(objectMapper.writeValueAsString(validateErrParam));
			}

			return ResponseEntity.ok(jobTruckNoSessionService.findJobTruck(id));

		} catch (ValidationException ex) {
			log.error("getEntityByIdDate", ex);
			serviceStatus.setData(null);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("getEntityByIdDate", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	@PutMapping("truck/{id}/{encAccnId}/{encRoles}")
	public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String id,
			@PathVariable String encAccnId, @PathVariable String encRoles) {
		log.debug("updateEntity");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			CkJobTruck dto = objectMapper.readValue(object, CkJobTruck.class);
			CkJobTruck entity = jobTruckNoSessionService.update(dto, encAccnId, encRoles);
			if (entity == null) {
				serviceStatus.setData(null);
				throw new Exception("entity null or empty");
			}
			return ResponseEntity.ok(entity);
		} catch (PathNotFoundException ex) {
			log.error("updateEntity", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (ValidationException ex) {
			log.error("updateEntity", ex);
			serviceStatus.setData(object);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("updateEntity", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
}
