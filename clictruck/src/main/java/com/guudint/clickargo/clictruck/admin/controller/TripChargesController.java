package com.guudint.clickargo.clictruck.admin.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.service.ICkCtTripRateService;
import com.guudint.clickargo.clictruck.planexec.job.dto.TripChargeReq;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripReimbursementService;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/tripcharges")
@CrossOrigin
public class TripChargesController {

	@Autowired
	private ICkCtTripRateService ckCtTripRateService;
	@Autowired
	private CkCtTripReimbursementService ckCtTripReimbursementService;

	@RequestMapping(value = "/calculation", method = RequestMethod.POST)
	public ResponseEntity<?> getTripCharge(@RequestBody String reqBody) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			ObjectMapper mapper = new ObjectMapper();
			TripChargeReq tripChargeReq = mapper.readValue(reqBody, TripChargeReq.class);
			Optional<CkCtTripRate> optTripRate = ckCtTripRateService.getByCoFfAndLocFromTo(tripChargeReq);
			if (optTripRate.isPresent()) {
				serviceStatus.setData(optTripRate.get());
			}
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok().body(serviceStatus);
		} catch (Exception e) {
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}

	@RequestMapping(value = "/calculation/total/{trId}", method = RequestMethod.GET)
	public ResponseEntity<?> getTotal(@PathVariable String trId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		Map<String, Object> map;
		try {
			map = ckCtTripReimbursementService.calculateTotalCharges(trId);
			serviceStatus.setData(map);
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok().body(serviceStatus);
		} catch (ProcessingException e) {
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
}
