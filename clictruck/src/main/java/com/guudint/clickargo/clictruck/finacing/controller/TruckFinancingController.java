package com.guudint.clickargo.clictruck.finacing.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.finacing.service.FinancingService;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.master.dto.MstBank;

/**
 * Financing related controller.
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/financing")
public class TruckFinancingController {

	@Autowired
	private FinancingService financeService;

	@GetMapping(value = "/financers")
	public ResponseEntity<Object> getFinances() {

		try {

			return ResponseEntity.ok(financeService.getFinancers());

		} catch (Exception e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/bank/{bankId}")
	public ResponseEntity<Object> getBankUrl(@PathVariable String bankId) {

		ServiceStatus serviceStatus = new ServiceStatus();

		try {

			MstBank bank = financeService.getBankDetails(bankId);
			if (bank != null) {
				serviceStatus.setStatus(ServiceStatus.STATUS.SUCCESS);
				serviceStatus.setData(bank.getBankUrl());
			} else {
				serviceStatus.setStatus(ServiceStatus.STATUS.SUCCESS);
				serviceStatus.setData("-");
			}

			return ResponseEntity.ok(serviceStatus);

		} catch (Exception e) {
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

}
