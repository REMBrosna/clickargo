package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.planexec.job.dto.MultiAssignDrvVeh;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiRecordRequest;
import com.guudint.clickargo.clictruck.planexec.job.dto.MultiRecordResponse;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckService;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

/**
 * This controller is for the multi-select action.
 */
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/jobs")
@CrossOrigin
public class TruckJobsController {

	@Autowired
	private CkJobTruckService ckJobTruckService;
	
	@Autowired
	protected ICkSession ckSession;

	@PostMapping
	public ResponseEntity<?> multiRecord(@RequestBody MultiRecordRequest request) {
		MultiRecordResponse response = ckJobTruckService.multiRecord(request);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("/multi-assign")
	public ResponseEntity<?> multiAssign(@RequestBody MultiAssignDrvVeh request)
			throws ParameterException, ProcessingException, ValidationException, EntityNotFoundException, Exception {

		Principal principal = ckSession.getPrincipal();
		if (principal == null)
			throw new ProcessingException("principal is null");

		CoreAccn coreAccn = Optional.ofNullable(principal.getCoreAccn()).orElse(new CoreAccn());
		MultiRecordResponse response = new MultiRecordResponse();
		response.setAccType(request.getAccType());
		response.setAction(request.getAction());
		response.setRole(request.getRole());
		response.getId().addAll(request.getId());

		ServiceStatus serviceStatus = new ServiceStatus();
		if ('S' == coreAccn.getAccnStatus()) {
			response.setSuspended(true);
			return ResponseEntity.badRequest().body(response);
		} else {
			try {
				if (request.getAction() == JobActions.ASSIGN) {
					ckJobTruckService.multiAssignDrvVeh(request, principal);
				}
				response.getSuccess().addAll(request.getId());
				response.setSuspended(false);
				response.setNoSuccess(response.getSuccess().size());
				response.setNoFailed(response.getFailed().size());
				return ResponseEntity.ok().body(response);
			} catch (ValidationException e) {
				serviceStatus.setErr(new ServiceError(500, e.getMessage()));
				serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(serviceStatus);
			} catch (ParameterException | EntityNotFoundException | ProcessingException e) {
				serviceStatus.setErr(new ServiceError(500, e.getMessage()));
				serviceStatus.setStatus(STATUS.EXCEPTION);
				return ResponseEntity.badRequest().body(serviceStatus);
			}
		}
	}
			
	@GetMapping("/action")
	public ResponseEntity<?> getActions(@RequestParam String accnType, @RequestParam String role,
			@RequestParam String jobId) {
		List<String> actions = ckJobTruckService.getActions(accnType, role, jobId);
		Map<String, Object> response = new HashMap<>();
		response.put("actions", actions);
		return ResponseEntity.ok().body(response);
	}
}
