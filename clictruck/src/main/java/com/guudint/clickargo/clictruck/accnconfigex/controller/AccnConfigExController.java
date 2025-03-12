package com.guudint.clickargo.clictruck.accnconfigex.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.accnconfigex.service.ClictruckAccnConfigExService;
import com.guudint.clickargo.clictruck.dashboard.service.EnhancedDashboardService;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/accnconfig")
public class AccnConfigExController extends AbstractPortalController {

	// Static Attributes
	////////////////////
	static Logger log = Logger.getLogger(AccnConfigExController.class);

	@Autowired
	private EnhancedDashboardService eDbService;

	@Autowired
	@Qualifier("clictruckAccnConfigExService")
	private ClictruckAccnConfigExService clictruckAccnConfigExtService;

	@GetMapping("/edashboard")
	public ResponseEntity<Object> getDashboard() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(eDbService.getDashboardStats(getPrincipal()));
		} catch (Exception e) {
			log.error("getDashboard", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Endpoint to check if the account has credit limit set up before retrieving
	 * the credit details.
	 */
	@GetMapping("/ecredit")
	public ResponseEntity<Object> getCreditDetails() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity
					.ok(clictruckAccnConfigExtService.isCreditApplicable(ServiceTypes.CLICTRUCK, getPrincipal()));
		} catch (Exception e) {
			log.error("getCreditDetails", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Endpoint to retrieve the tabs that can only be displayed based on the country
	 * it is deployed or configuration.
	 */
	@GetMapping("/tabs")
	public ResponseEntity<Object> getTabs() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(clictruckAccnConfigExtService.getTabs(getPrincipal()));
		} catch (Exception e) {
			log.error("getTabs", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Endpoint to retrieve job state filters to be used in datatable truck listing.
	 */
	@GetMapping("/truckstatefilter")
	public ResponseEntity<Object> getTruckStateFilter() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(clictruckAccnConfigExtService.getJobStateFilter());
		} catch (Exception e) {
			log.error("getTabs", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Endpoint to retrieve job state filters to be used in datatable truck listing.
	 */
	@GetMapping("/env")
	public ResponseEntity<Object> getAppEnvironmentDeployed() {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(clictruckAccnConfigExtService.getCtryEnv());
		} catch (Exception e) {
			log.error("getAppEnvironmentDeployed", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

}
