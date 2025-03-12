package com.guudint.clickargo.clictruck.opm.third.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.opm.OpmException;
import com.guudint.clickargo.clictruck.opm.service.IOpmProcessService;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditApprovalReq;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditDisbursementReq;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditRepaymentReq;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditRes;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditSuspensionReq;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditTerminationReq;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditUnSuspensionReq;

@RequestMapping("/api/v1/clickargo/truck/opm")
@CrossOrigin
@RestController
public class OpmController {

	private static Logger log = Logger.getLogger(OpmController.class);
	
	@Autowired
	IOpmProcessService<OpmCreditApprovalReq> opmApproveService;
	@Autowired
	IOpmProcessService<OpmCreditDisbursementReq> opmDisbursementService;
	@Autowired
	IOpmProcessService<OpmCreditRepaymentReq> opmRepaymentService;
	@Autowired
	IOpmProcessService<OpmCreditTerminationReq> opmTerminationService;
	@Autowired
	IOpmProcessService<OpmCreditSuspensionReq> opmSuspensionService;
	@Autowired
	IOpmProcessService<OpmCreditUnSuspensionReq> opmUnsuspensionService;
	
	private static String financer = "OCBC";
	
	Map<Integer, OpmException> errMap = new HashMap<>();

	@PostMapping("/approval")
	public ResponseEntity<OpmCreditRes> creditApproval(@RequestBody OpmCreditApprovalReq approvalReq) {

		try {
			opmApproveService.process(approvalReq, financer, errMap);

		} catch (Exception e) {
			log.error("creditApproval:", e);
			OpmCreditRes response = new OpmCreditRes();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping("/disbursement")
	public ResponseEntity<OpmCreditRes> creditDisbursement(@RequestBody OpmCreditDisbursementReq disbursementReq) {

		try {
			opmDisbursementService.process(disbursementReq, financer, errMap);

		} catch (Exception e) {
			log.error("creditDisbursement:", e);
			OpmCreditRes response = new OpmCreditRes();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping("/repayment")
	public ResponseEntity<OpmCreditRes> creditRepayment(@RequestBody OpmCreditRepaymentReq repaymentReq) {

		try {
			opmRepaymentService.process(repaymentReq, financer, errMap);

		} catch (Exception e) {
			log.error("creditRepayment:", e);
			OpmCreditRes response = new OpmCreditRes();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping("/termination")
	public ResponseEntity<OpmCreditRes> creditTermination(@RequestBody OpmCreditTerminationReq terminationReq) {

		try {
			opmTerminationService.process(terminationReq, financer, errMap);

		} catch (Exception e) {
			log.error("creditTermination:", e);
			OpmCreditRes response = new OpmCreditRes();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping("/suspension")
	public ResponseEntity<OpmCreditRes> creditSuspension(@RequestBody OpmCreditSuspensionReq suspensionReq) {

		try {
			opmSuspensionService.process(suspensionReq, financer, errMap);

		} catch (Exception e) {
			log.error("creditSuspension:", e);
			OpmCreditRes response = new OpmCreditRes();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		return ResponseEntity.ok().body(null);
	}
	
	@PostMapping("/unsuspension")
	public ResponseEntity<OpmCreditRes> creditUnsuspension(@RequestBody OpmCreditUnSuspensionReq unsuspensionReq) {

		try {
			opmUnsuspensionService.process(unsuspensionReq, financer, errMap);

		} catch (Exception e) {
			log.error("creditUnsuspension:", e);
			OpmCreditRes response = new OpmCreditRes();
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
		}
		return ResponseEntity.ok().body(null);
	}
}
