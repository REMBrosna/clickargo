package com.guudint.clickargo.clictruck.finacing.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.finacing.service.impl.CkCtToInvoiceServiceImpl;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtPlatformInvoiceDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/invoice")
public class TruckInvoiceMiscController {

	private static final Logger LOG = Logger.getLogger(TruckPdfRegenController.class);

	@Autowired
	private CkCtPlatformInvoiceDao ckCtPlatformInvoiceDao;
	
	@Autowired
	private CkCtToInvoiceServiceImpl ckCtToInvoiceServiceImpl;
	
	@GetMapping(value = "/statistic/{accnId}")
	public ResponseEntity<Object> getOutstandingInv(@PathVariable String accnId, @RequestParam String invStatus) {
		
		try {

			if( StringUtils.isBlank(invStatus))
				throw new ParameterException("InvStatus is null");
			
			return ResponseEntity.ok(this.getInv(accnId, invStatus));

		} catch (Exception e) {
			LOG.error("regenerateInvoices", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	private Map<String, Object> getInv(String accnId, String invStatus) throws Exception {

		
		List<String> statusList = Arrays.asList(invStatus.split(","));
		
		List<TCkCtPlatformInvoice> invList = ckCtPlatformInvoiceDao.findByAccnIdAndStatus(accnId, statusList);
		
		int totalInv = invList.size();
		BigDecimal sumAmount = invList.stream().map(TCkCtPlatformInvoice::getInvAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
		Date earliestDate = invList.stream().map(TCkCtPlatformInvoice::getInvDtDue).min(Comparator.comparing(Date::getTime)).orElse(null);

		Map<String, Object> rst = new HashMap<>();
		
		rst.put("totalInv", totalInv);
		rst.put("sumAmount", sumAmount);
		rst.put("earlistDate", earliestDate);
		
		return rst;
	}
	
	@GetMapping("/multitrip-invoice")
	@Transactional
	public ResponseEntity<Object> getListInvoiceByJobId(@RequestParam String jobId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			serviceStatus.setStatus(ServiceStatus.STATUS.SUCCESS);
			serviceStatus.setData(ckCtToInvoiceServiceImpl.getListInvoiceByJobId(jobId));
			return ResponseEntity.ok(serviceStatus);
		} catch (Exception e) {
			LOG.error("getListInvoiceByJobId", e);
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
}
