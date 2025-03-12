package com.guudint.clickargo.clictruck.finacing.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.finacing.service.impl.TruckRegenPdfMiscService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.JobTruckStateServiceImpl;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.util.PrincipalUtilService;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/regenerate")
public class TruckPdfRegenController {

	private static final Logger LOG = Logger.getLogger(TruckPdfRegenController.class);

	@Autowired
	private TruckRegenPdfMiscService pdfMiscService;

	@Autowired
	private JobTruckStateServiceImpl jobTruckStateServiceImpl;

	@Autowired
	private PrincipalUtilService principalUtilService;

	@RequestMapping(value = "/invoices", method = RequestMethod.GET)
	public ResponseEntity<Object> regenerateInvoices() {
		try {

			pdfMiscService.regeneratePdf();
			return ResponseEntity.ok("success");

		} catch (Exception e) {
			LOG.error("regenerateInvoices", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/createInvoices/{jobTruckId}", method = RequestMethod.GET)
	public ResponseEntity<Object> regenerateInvoicesData(@PathVariable String jobTruckId) {
		try {
			CoreAccn gliAccn = new CoreAccn();
			gliAccn.setAccnId("GLI");

			Principal principal = principalUtilService.getPrincipal();

			jobTruckStateServiceImpl.createPlatformFees(jobTruckId, gliAccn, principal);

			return ResponseEntity.ok("success");

		} catch (Exception e) {
			LOG.error("regenerateInvoices", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

}
