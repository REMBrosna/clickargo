package com.guudint.clickargo.clictruck.planexec.job.mobile.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.guudint.clickargo.clictruck.admin.dto.ChangeMobileDriverLang;
import com.guudint.clickargo.clictruck.planexec.job.controller.AbstractTruckJobListController;
import com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl.CkJobTruckASGListService;
import com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl.CkJobTruckMobileLangService;
import com.guudint.clickargo.clictruck.planexec.job.mobile.service.impl.CkJobTruckPAUListService;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

/**
 * Listing controller for mobile apps.
 */
@CrossOrigin
@RequestMapping(value = "/api/v1/clickargo/clictruck/mobile/")
public class TruckJobMobileListController extends AbstractTruckJobListController {

	private static Logger log = Logger.getLogger(TruckJobMobileListController.class);

	@Autowired
	private CkJobTruckASGListService ckJobTruckASGListService;

	@Autowired
	private CkJobTruckPAUListService ckJobTruckPAUListService;

	@Autowired
	private CkJobTruckMobileLangService ckJobTruckMobileLangService;

	@GetMapping("{entity}")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		log.debug("getEntitiesBy");
		return super.listEntitiesBy(entity, params);
	}

	@GetMapping("{entity}/checkJobStatus")
	public ResponseEntity<Object> getCheckJobStatus(@PathVariable String entity) {
		log.debug("getCheckJobStatus");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if (entity.equalsIgnoreCase(JobStates.ASG.getDesc())) {
				serviceStatus.setData(ckJobTruckASGListService.checkStatusJobTruck());
			} else if (entity.equalsIgnoreCase(JobStates.PAUSED.getDesc())) {
				serviceStatus.setData(ckJobTruckPAUListService.checkStatusJobTruck());
			}
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok(serviceStatus);
		} catch (Exception ex) {
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("startJob/{jobId}")
	public ResponseEntity<Object> startJob(@PathVariable String jobId, @RequestParam String action) throws Exception {
		log.debug("startJob");
		try {
			Map<String, Object> objectMapping = new HashMap<>();
			if (action.equalsIgnoreCase("START")) {
				objectMapping.put("data", ckJobTruckASGListService.startAssignJob(jobId));
			} else if (action.equalsIgnoreCase("PAUSE")) {
				objectMapping.put("data", ckJobTruckPAUListService.resumePauseJob(jobId));
			}
			objectMapping.put("status", STATUS.SUCCESS);
			return ResponseEntity.ok(objectMapping);
		} catch (Exception ex) {
			log.error("updateJob", ex);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("job/{entity}")
	public ResponseEntity<Object> getListJobDriver(@PathVariable String entity,
			@RequestParam Map<String, String> params) {
		log.debug("getListJobDriver");
		return super.listEntitiesBy(entity, params);
	}

	@PutMapping("driver/lang")
	public synchronized ResponseEntity<Object> driverLang(@RequestBody ChangeMobileDriverLang dto) {

		log.debug("driverLang");
		try {
			ckJobTruckMobileLangService.driverLang(dto);

			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("message", "Language successfully to change");
			objectMapping.put("timestamp", new Date());
			return ResponseEntity.ok(objectMapping);
		} catch (Exception ex) {
			log.error("driverLang", ex);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("driver/lang")
	public ResponseEntity<Object> getLanguageDriv(@RequestParam String drivMobileId) throws Exception {
		log.debug("getLanguageDriv");

		try {
			return ResponseEntity.ok(ckJobTruckMobileLangService.getLanguageDriv(drivMobileId));
		} catch (Exception ex) {
			log.error("getLanguageDriv", ex);
			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}

	}

}
