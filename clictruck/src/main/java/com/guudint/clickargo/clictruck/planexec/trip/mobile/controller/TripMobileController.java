package com.guudint.clickargo.clictruck.planexec.trip.mobile.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guudint.clickargo.clictruck.planexec.job.mobile.dto.CkMTripCargoDetails;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoMm;
import com.vcc.camelone.common.dao.GenericDao;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.dto.MobileTripCargo;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.dto.TripAttachment;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.CkJobTruckTripMobileService;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService.TripAttachTypeEnum;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

/**
 * Controller for trip related actions such as Start, Deliver, DropOff, etc. 
 */
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/mobile")
@CrossOrigin
public class TripMobileController {

	private static Logger LOG = Logger.getLogger(TripMobileController.class);

	@Autowired
	private TripMobileService tripMobileService;
	
	@Autowired
	private CkJobTruckTripMobileService ckJobTruckTripMobileService;
	
	@Autowired
	protected ICkSession ckSession;

	@Autowired
	private GenericDao<TCkCtTripCargoMm, String> ckCtTripCargoMmDao;
	
	@PostMapping("/trip/location/attach")
	public ResponseEntity<Object> attachment(@RequestBody TripAttachment dto) throws Exception {
		LOG.debug("attachment");
		
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			switch (dto.getAction()) {
			case "UPLOAD":
				serviceStatus.setData(tripMobileService.uploadTripAttachment(dto));
				break;
			case "CANCEL":
				tripMobileService.removeTripAttachment(dto);
				break;
			default:
				break;
			}
            serviceStatus.setStatus(STATUS.SUCCESS);
            return ResponseEntity.ok().body(serviceStatus);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error("", e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
	
	@GetMapping("/trip/location/attach")
	public ResponseEntity<Object> downloadTripAttachment(@RequestParam String tripId, @RequestParam(required = false) String type) throws Exception {
		LOG.debug("downloadAttachment");
		LOG.info("downloadAttachment");
		
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			

			if (null != type && type.equalsIgnoreCase("pickup")) {
				type = TripAttachTypeEnum.PHOTO_PICKUP.name();
			} 
			
			serviceStatus.setData(tripMobileService.downloadTripAttachment(tripId, type, true));
			serviceStatus.setStatus(STATUS.SUCCESS);
            return ResponseEntity.ok().body(serviceStatus);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error("", e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
	
	
	@PutMapping("/trip")
	public ResponseEntity<Object> updateTripCargo(@RequestBody MobileTripCargo dto) throws Exception {
		LOG.debug("updateTripCargo");
		try {
			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("data", tripMobileService.updateTrip(dto));
			return ResponseEntity.ok(objectMapping);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error("", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
	
	@PostMapping("/trip/confirmation")
	public ResponseEntity<Object> confirmTrip(@RequestBody TripAttachment dto) throws Exception {
		LOG.debug("confirmTrip");
		try {
			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("data", tripMobileService.confirmationTripDropOff(dto));
			objectMapping.put("timestamp", new Date());
			return ResponseEntity.ok(objectMapping);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error("", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
	
	@GetMapping("/trip/ongoing")
	public ResponseEntity<Object> getTripOngoing() throws Exception {
		LOG.debug("getTripOngoing");
		try {
			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("data", tripMobileService.onGoingJob());
			return ResponseEntity.ok(objectMapping);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error("", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
	
	@PutMapping("/trip/location")
	public synchronized ResponseEntity<Object> tripLocationRemarks(@RequestBody CkCtTripLocation dto) {
		LOG.debug("tripLocationRemarks");
		try {
			tripMobileService.tripLocationRemarks(dto);
			Map<String, Object> objectMapping = new HashMap<>();
			objectMapping.put("status", STATUS.SUCCESS);
			objectMapping.put("message", "Remark Active Ongoing Job");
			objectMapping.put("timestamp", new Date());
			return ResponseEntity.ok(objectMapping);
		} catch (Exception ex) {
			LOG.error("tripLocationRemarks", ex);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/trip/location")
	public ResponseEntity<Object> getRemarks (@RequestParam String tripId) throws Exception{
		LOG.debug("getRemarks");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			serviceStatus.setData(ckJobTruckTripMobileService.getRemarks(tripId));
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok(serviceStatus);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error("", e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
	
	
	@GetMapping("/trip/cargo")
	public ResponseEntity<Object> getCargoDetails (@RequestParam String tripId) throws Exception{
		LOG.debug("getCargoDetails");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			serviceStatus.setData(ckJobTruckTripMobileService.listCargoDetail(tripId));
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok(serviceStatus);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error("", e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
	
	@GetMapping("/downloadDsv/{tripId}")
	public ResponseEntity<Object> downloadDsv(@PathVariable String tripId) throws Exception{
	    LOG.debug("downloadDsv");
	    ServiceStatus serviceStatus = new ServiceStatus();
	    try {
	    	serviceStatus.setData(ckJobTruckTripMobileService.downloadDsv(tripId));
	    	serviceStatus.setStatus(STATUS.SUCCESS);
	        return ResponseEntity.ok(serviceStatus);
	    } catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
	        serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
	    }
	}
	
	@GetMapping("/downloadJobEpod/{jobId}")
	public ResponseEntity<Object> downloadJobEpod(@PathVariable String jobId) throws Exception{
	    LOG.debug("downloadDsv");
	    ServiceStatus serviceStatus = new ServiceStatus();
	    try {
	    	serviceStatus.setData(ckJobTruckTripMobileService.downloadDOAttachByJob(jobId));
	    	serviceStatus.setStatus(STATUS.SUCCESS);
	        return ResponseEntity.ok(serviceStatus);
	    } catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
	        serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
	    }
	}
	
	@GetMapping("/job/trip/location/attach")
	public ResponseEntity<Object> downloadTripsAttachmentByJob(@RequestParam String jobId,@RequestParam(required = false) String type) throws Exception {
		LOG.debug("downloadTripsAttachmentByJob");
		
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			
			if (null != type && type.equalsIgnoreCase("pickup")) {
				type = TripAttachTypeEnum.PHOTO_PICKUP.name();
			} 
			serviceStatus.setData(tripMobileService.downloadTripAttachment(jobId, type, false));
			serviceStatus.setStatus(STATUS.SUCCESS);
            return ResponseEntity.ok().body(serviceStatus);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error("", e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}

	@PutMapping("/job/trip/cargos/checklist")
	public ResponseEntity<ServiceStatus> updateCargoCheckList(@RequestBody List<CkMTripCargoDetails> dtoList) throws Exception {
		LOG.debug("updateCargoCheckList");

		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if (dtoList != null && !dtoList.isEmpty()) {
				for (CkMTripCargoDetails cargoMm : dtoList) {
					TCkCtTripCargoMm tCkCtTripCargoMm = ckCtTripCargoMmDao.find(cargoMm.getCnCgId());
					if (tCkCtTripCargoMm != null) {
						tCkCtTripCargoMm.setCgPickupStatus(cargoMm.getCgPickupStatus());
						tCkCtTripCargoMm.setCgDropOffStatus(cargoMm.getCgDropOffStatus());
						ckCtTripCargoMmDao.saveOrUpdate(tCkCtTripCargoMm);
					}
				}
				serviceStatus.setStatus(STATUS.SUCCESS);
			}
		} catch (ValidationException | EntityNotFoundException | ProcessingException | ParameterException e) {
			LOG.error("Error updating cargo checklist", e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceStatus);
		}

		return ResponseEntity.ok(serviceStatus);
	}
}