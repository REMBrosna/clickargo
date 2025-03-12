package com.guudint.clickargo.clictruck.auxiliary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.admin.contract.scheduler.ContractUpdateScheduler;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/auxiliary/contract/")
public class AuxiliaryContractController {

	private static Logger log = Logger.getLogger(AuxiliaryContractController.class);
	
	SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	ContractUpdateScheduler contractUpdateScheduler;
	
	@RequestMapping(value = "/ContractUpdateScheduler")
	public ResponseEntity<Object> contractUpdateScheduler() {

		try {
			contractUpdateScheduler.doJob();

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error(" AuxiliaryContractController contractUpdateScheduler", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/executeContractUpdate/{dateStr}")
	public ResponseEntity<Object> executeContractUpdate(@PathVariable String dateStr) {

		try {
			Date date = sdfDate.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			contractUpdateScheduler.executeContractUpdate(cal);

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error(" AuxiliaryContractController contractUpdateScheduler", e);
		}

		return ResponseEntity.ok("OK");
	}

}
