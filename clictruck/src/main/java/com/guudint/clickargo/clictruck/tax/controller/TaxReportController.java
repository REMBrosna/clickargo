package com.guudint.clickargo.clictruck.tax.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.tax.service.TaxInvoiceService;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@RestController
@RequestMapping("/api/v1/clickargo/clictruck/taxReport")
public class TaxReportController {

    @Autowired
    private TaxInvoiceService taxInvoiceService;

    private static Logger LOG = Logger.getLogger(TaxReportController.class);

    @GetMapping
    public ResponseEntity<?> generateTaxReport(@RequestParam String date) {
        ServiceStatus serviceStatus = new ServiceStatus();
        try {
            taxInvoiceService.generateTaxReport(date);
            serviceStatus.setStatus(STATUS.SUCCESS);
            return ResponseEntity.ok().body(serviceStatus);
        } catch (Exception e) {
            LOG.error(e);
            serviceStatus.setErr(new ServiceError(-100, e));
            return ResponseEntity.badRequest().body(serviceStatus);
        }
    }
}
