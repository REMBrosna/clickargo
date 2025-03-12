
package com.guudint.clickargo.clictruck.auxiliary;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.opm.service.impl.OpmCreditDisbursementEmail2FinanceService;
import com.guudint.clickargo.clictruck.opm.service.impl.OpmSftpService;
import com.guudint.clickargo.clictruck.opm.service.impl.OpmUpdateAccnStatusService;
import com.guudint.clickargo.clictruck.opm.service.impl.OpmUtilizeService;
import com.guudint.clickargo.clictruck.scheduler.id.opm.OpmCreditDisbursementEmail2Finance;
import com.guudint.clickargo.clictruck.scheduler.id.opm.OpmLoadCsvFileScheduler;
import com.guudint.clickargo.clictruck.scheduler.id.opm.OpmPushUtilize2BankScheduler;
import com.guudint.clickargo.clictruck.scheduler.id.opm.OpmUpdateAccnStatusScheduler;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/auxiliary/opm/")
public class AuxiliaryOpmController extends AbstractPortalController {

	private static Logger log = Logger.getLogger(AuxiliaryOpmController.class);

	@Autowired
	private OpmPushUtilize2BankScheduler opmPushUtilize2BankScheduler;

	@Autowired
	private OpmLoadCsvFileScheduler opmLoadCsvFileScheduler;

	@Autowired
	private OpmSftpService opmSftpService;
	
	@Autowired
	OpmUtilizeService opmUtilizeService;

	@Autowired
	OpmUpdateAccnStatusScheduler opmUpdateAccnStatusScheduler;
	
	@Autowired
	private OpmUpdateAccnStatusService opmUpdateAccnStatusService;

	@Autowired
	OpmCreditDisbursementEmail2Finance opmCreditDisbursementEmail2Finance;
	
	@Autowired
	OpmCreditDisbursementEmail2FinanceService opmCreditDisbursementEmail2FinanceService;
	
	@RequestMapping(value = "/opmPushUtilize2BankScheduler")
	public ResponseEntity<Object> opmPushUtilize2BankScheduler() {

		try {
			opmPushUtilize2BankScheduler.doJob();

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error("opmPushUtilize2BankScheduler", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/pushUtilizeFile2Bank/{dateStr}")
	public ResponseEntity<Object> pushUtilizeFile2Bank(@PathVariable String dateStr) {

		try {
			Date processDate = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
					
			opmUtilizeService.pushUtilizeFile2Bank(processDate);

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error("opmPushUtilize2BankScheduler", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/opmLoadCsvFileScheduler")
	public ResponseEntity<Object> opmLoadCsvFileScheduler() {

		try {
			opmLoadCsvFileScheduler.doJob();

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error("opmLoadCsvFileScheduler", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/processByOpmSftpLogId/{opmSftpLogid}")
	public ResponseEntity<Object> processByOpmSftpLogId(@PathVariable String opmSftpLogid) {

		try {
			opmSftpService.processByOpmSftpLogId(opmSftpLogid);

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error("processByOpmSftpLogId", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/updateOpmAccnStatus")
	public ResponseEntity<Object> updateOpmAccnStatus() {

		try {
			opmUpdateAccnStatusScheduler.doJob();

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error("updateOpmAccnStatus", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/updateOpmAccnStatus/{accnId}")
	public ResponseEntity<Object> updateOpmAccnStatus(@PathVariable String accnId) {

		try {
			opmUpdateAccnStatusService.updateOpmAccnStatusByAccnId(accnId);

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error("updateOpmAccnStatus", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/opmCreditDisbursementEmail2Finance")
	public ResponseEntity<Object> opmCreditDisbursementEmail2Finance() {

		try {
			opmCreditDisbursementEmail2Finance.doJob();

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error("opmPushUtilize2BankScheduler", e);
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}
	@RequestMapping(value = "/opmCreditDisbursementEmail2Finance/{dateStr}")
	public ResponseEntity<Object> opmCreditDisbursementEmail2Finance(@PathVariable String dateStr) {

		try {
			Date processDate = new SimpleDateFormat("yyyyMMdd").parse(dateStr);

			String jobResult = opmCreditDisbursementEmail2FinanceService.sendCdEmail2Finance(processDate);

			return ResponseEntity.ok(jobResult);

		} catch (Exception e) {
			log.error("opmPushUtilize2BankScheduler", e);
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}

	}
	


}

