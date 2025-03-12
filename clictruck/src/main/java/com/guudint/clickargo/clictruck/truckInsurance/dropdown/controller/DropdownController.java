package com.guudint.clickargo.clictruck.truckInsurance.dropdown.controller;

import com.guudint.clickargo.clictruck.truckInsurance.dropdown.service.DropdownService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/dropdown")
public class DropdownController {
    private static final Logger log = Logger.getLogger(DropdownController.class);

    @Autowired
    private DropdownService dropdownService;

    @RequestMapping(value = "/ckCtVehList", method = RequestMethod.GET)
    public ResponseEntity<Object> getCkCtVehicles() {
        log.info("getCkCtVehicles");
        return dropdownService.getCkCtVehicles();
    }
    @RequestMapping(value = "/alert/{module}", method = RequestMethod.GET)
    public ResponseEntity<Object> agencyList(@PathVariable String module) {
        log.info("agencyList");
        return dropdownService.getAlert(module);
    }
    @RequestMapping(value = "/alert/{module}/{altName}", method = RequestMethod.GET)
    public ResponseEntity<Object> agencyList(@PathVariable String module, @PathVariable String altName) {
        log.info("agencyList");
        return dropdownService.getAlertType(module, altName);
    }
    @RequestMapping(value = "/alert/{module}/{altName}/{altNotificationType}", method = RequestMethod.GET)
    public ResponseEntity<Object> agencyList(@PathVariable String module, @PathVariable String altName,@PathVariable String altNotificationType) {
        log.info("agencyList");
        return dropdownService.getConditionType(module, altName, altNotificationType);
    }

}

