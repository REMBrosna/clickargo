package com.guudint.clickargo.clictruck.truckInsurance.controller;

import com.guudint.clickargo.clictruck.truckInsurance.dto.Company;
import com.guudint.clickargo.clictruck.truckInsurance.service.InsuranceService;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/v2.0/clicTruck")
@CrossOrigin
@RestController
public class InsuranceController {
    @Autowired
    private InsuranceService insuranceService;
    private static final Logger log = Logger.getLogger(InsuranceController.class);
    @RequestMapping(value = "/insurance/sg/assue", method = RequestMethod.POST)
    public ResponseEntity<Object> createInsuranceAssue(@RequestBody Company company) {
        log.debug("createInsuranceAssue");
        ServiceStatus serviceStatus = new ServiceStatus();
        try {
            insuranceService.pushInsurance(company);
            serviceStatus.setStatus(ServiceStatus.STATUS.SUCCESS);
            return new ResponseEntity<>(serviceStatus, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("createInsuranceAssue", ex);
            serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
            serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
            return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
    }
    @RequestMapping(value = "/insurance/sg/assue/form", method = RequestMethod.GET)
    public ResponseEntity<Object> getInsuranceAssueForm() {
        log.debug("getInsuranceAssueForm");
        ServiceStatus serviceStatus = new ServiceStatus();
        try {
            return insuranceService.getInsuranceForm();
        } catch (Exception ex) {
            log.error("getInsuranceAssueForm", ex);
            serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
            serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
            return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
    }
}
