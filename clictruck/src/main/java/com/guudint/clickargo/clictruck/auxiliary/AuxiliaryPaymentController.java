package com.guudint.clickargo.clictruck.auxiliary;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.finacing.service.ITruckOperatorPayoutService;
import com.guudint.clickargo.clictruck.scheduler.id.TruckOperatorPayoutScheduler;
import com.vcc.camelone.common.exception.ParameterException;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/auxiliary/payment/")
public class AuxiliaryPaymentController {

	@Autowired
	private TruckOperatorPayoutScheduler truckOperatorPayoutScheduler;
	
	@Autowired
	private ITruckOperatorPayoutService toPayoutService;

	@RequestMapping(value = "/executeFundsTransfer")
	public ResponseEntity<Object> executeFundsTransfer() throws ParameterException, Exception {

		truckOperatorPayoutScheduler.doJob();

		return ResponseEntity.ok("OK");
	}

	@RequestMapping(value = "/updateFundsTransferResult/{tCkCtToPaymentId}")
	public ResponseEntity<Object> updateFundsTransferResult(@PathVariable String tCkCtToPaymentId,
			@RequestParam(value = "paymentResult", required = false) Boolean paymentResult,
			@RequestParam(value = "paymentDate", required = false) Boolean paymentDate)
			throws ParameterException, Exception {
		
		if(null == paymentResult) {
			// default is true;
			paymentResult = true;
		}

		// Maybe need to update payment date to any date before.
		Date now = new Date();
		
		toPayoutService.updateFundsTransferResult(paymentResult, tCkCtToPaymentId, now);

		return ResponseEntity.ok("OK");
	}
}
