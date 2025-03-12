package com.guudint.clickargo.clictruck.admin.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.admin.account.service.impl.AccountFfCoServiceImpl;
import com.guudint.clickargo.clictruck.admin.ratetable.service.ICkCtRateTableService;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtCo2xServiceImpl;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;
import com.vcc.camelone.util.PrincipalUtilService;

@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/selectOptions")
@CrossOrigin
public class ClictruckSelectController {

	private static Logger LOG = Logger.getLogger(AdministratorController.class);

	@Autowired
	private ICkCtRateTableService rateTableService;

	@Autowired
	private AccountFfCoServiceImpl accountFfCoService;

	@Autowired
	private CkCtCo2xServiceImpl co2xServiceImpl;

	@Autowired
	private PrincipalUtilService principalUtilService;

	@RequestMapping(value = "/truckOperators", method = RequestMethod.GET)
	public ResponseEntity<Object> loadTruckingOperators() {
		LOG.debug("loadTruckingOperators");

		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ParameterException("principal null");

			return ResponseEntity.ok(rateTableService.loadOperators(principal));
		} catch (PathNotFoundException ex) {
			LOG.error("loadTruckingOperators", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("loadTruckingOperators", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getCoFfFilteredRtContracts", method = RequestMethod.GET)
	public ResponseEntity<Object> loadFilteredCoFfAccnWithContracts() {
		LOG.debug("loadTruckingOperators");

		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ParameterException("principal null");

			return ResponseEntity.ok(rateTableService.loadAccnsRateTableByContract(principal, true));
		} catch (PathNotFoundException ex) {
			LOG.error("loadFilteredCoFfAccnWithContracts", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("loadFilteredCoFfAccnWithContracts", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getFfCoFilteredByFf", method = RequestMethod.GET)
	public ResponseEntity<Object> getFfCoFilteredByFf() {
		LOG.debug("getFfCoFilteredByFf");

		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ParameterException("principal null");

			return ResponseEntity.ok(accountFfCoService.getFfCoAccn(principal.getUserAccnId()));
		} catch (PathNotFoundException ex) {
			LOG.error("loadFilteredCoFfAccnWithContracts", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("loadFilteredCoFfAccnWithContracts", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/co2xAccns", method = RequestMethod.GET)
	public ResponseEntity<Object> getFilteredAccountsForCO2x(@RequestParam("isFilter") String isFilter) {
		LOG.debug("getFilteredAccountsForCO2x");

		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null)
				throw new ParameterException("principal null");

			return ResponseEntity.ok(co2xServiceImpl.listEligibleAccounts(isFilter));
		} catch (PathNotFoundException ex) {
			LOG.error("getFilteredAccountsForCO2x", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("getFilteredAccountsForCO2x", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

}
