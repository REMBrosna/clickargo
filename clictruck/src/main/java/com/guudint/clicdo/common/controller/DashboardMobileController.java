package com.guudint.clicdo.common.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clicdo.common.service.DashboardMobileService;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/dashboardMobile")
public class DashboardMobileController extends AbstractPortalController {

	private static Logger log = Logger.getLogger(DashboardMobileController.class);

	@Autowired
	private DashboardMobileService dbMService;

	@GetMapping("")
	public ResponseEntity<Object> getDashboardMobile() {
		log.debug("getDashboardMobile");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			return ResponseEntity.ok(dbMService.getDashboardStats(getPrincipal()));
		} catch (Exception e) {
			log.error("getDashboard", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

}
