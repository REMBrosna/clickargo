package com.guudint.clickargo.clictruck.admin.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.guudint.clickargo.clictruck.common.dto.CkCtCo2x;
import com.guudint.clickargo.clictruck.common.service.CkCtCo2xService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.util.PrincipalUtilService;

/**
 * Use this controller for other admin related api such as checking if account
 * has co2x feature, etc.
 */
@RequestMapping("/api/v1/clickargo/clictruck/admin/misc")
@CrossOrigin
@Controller
public class AdminMiscController {

	private static Logger LOG = Logger.getLogger(AdminMiscController.class);

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	private CkCtCo2xService co2xService;

	@GetMapping("/co2x/check")
	public ResponseEntity<Object> checkCo2xEligibility() {
		LOG.debug("checkCo2xEligibility");

		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			Principal principal = principalUtilService.getPrincipal();
			
			CkCtCo2x co2x = co2xService.findByAccount(principal.getUserAccnId());

			if (co2x == null) {
				serviceStatus.setData(null);
			} else {
				serviceStatus.setData(co2x);
			}

			
			return ResponseEntity.ok(serviceStatus);

		} catch (Exception ex) {
			LOG.error("checkCo2xEligibility", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
}
