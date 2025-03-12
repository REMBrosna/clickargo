package com.guudint.clickargo.clictruck.report.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.report.service.ClicTruckReportMiscService;

@RequestMapping(value = "/api/app/report/misc")
@CrossOrigin
@RestController
public class ClicTruckReportMiscController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ClicTruckReportMiscController.class);
	
	@Autowired
	private ClicTruckReportMiscService truckReportMiscService;
	
	@RequestMapping(value = "/accn/assoc/{accnType}", method = RequestMethod.GET)
	public ResponseEntity<Object> listReportGroup(@PathVariable String accnType) {
		log.info("listReportGroup");
		return truckReportMiscService.getAccnAssocAppType(accnType);
	}
}
