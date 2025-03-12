package com.guudint.clicdo.common.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clicdo.common.service.DashboardService;
import com.guudint.clickargo.clictruck.dashboard.service.EnhancedDashboardService;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/dashboard")
public class DashboardController extends AbstractPortalController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(DashboardController.class);

	@Autowired
	private DashboardService dbService;

	@Autowired
	private EnhancedDashboardService enhancedDbService;

	@GetMapping("")
	public ResponseEntity<Object> getDashboard() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

//			return ResponseEntity.ok(dbService.getDashboardStats(getPrincipal()));
			return ResponseEntity.ok(enhancedDbService.getDashboardStats(getPrincipal()));
		} catch (Exception e) {
			log.error("getDashboard", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/docbillverification")
	public ResponseEntity<Object> getDashboardForDocBillVerification() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(dbService.getDocBillingVerificationStats(getPrincipal()));
		} catch (Exception e) {
			log.error("getDashboard", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/taxmodules")
	public ResponseEntity<Object> getDashboardTaxModules() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(dbService.getDashboardStatsTaxModules(getPrincipal()));
		} catch (Exception e) {
			log.error("getDashboard", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/accnSuspend")
	public ResponseEntity<Object> getDashboardAccountSuspension() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(dbService.getDashboardAccountSuspension(getPrincipal()));
		} catch (Exception e) {
			log.error("getDashboard", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/outboundPayment")
	public ResponseEntity<Object> getDashboardOutboundPayment() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(dbService.getDashboardOutboundPayment(getPrincipal()));
		} catch (Exception e) {
			log.error("getDashboard", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}
}
