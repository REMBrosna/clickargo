package com.guudint.clickargo.clictruck.auxiliary;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.common.dao.CkCtLocationDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.track.service.TrackTraceCoordinateService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceEnterExitLocService;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/auxiliary/gps")
public class AuxiliaryGpsController extends AbstractPortalController {

	private static Logger log = Logger.getLogger(AuxiliaryGpsController.class);


	@Autowired
	private CkCtLocationDao ckCtLocationDao;

	@Autowired
	TrackTraceCoordinateService coordinateService;

	@Autowired
	TrackTraceEnterExitLocService trackTraceService;

	@Autowired
	private CkJobTruckServiceUtil ckJobTruckUtilService;

	@RequestMapping(value = "/initGPS")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public ResponseEntity<Object> initGPS() {

		try {
			this.initLocGpsImplement();

		} catch (Exception e) {
			log.error("encryptPwd", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/initTripLocGPS/{jobTruckId}")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public ResponseEntity<Object> initTripLocGPS(@PathVariable String jobTruckId) {

		try {
			ckJobTruckUtilService.updateTripLocGPS(jobTruckId);

		} catch (Exception e) {
			log.error("encryptPwd", e);
		}

		return ResponseEntity.ok("OK");
	}
	
	@RequestMapping(value = "/fetchCoordinate/{address}")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public ResponseEntity<Object> fetchCoordinate(@PathVariable String address) {

		try {
			String gps = coordinateService.fetchCoordinate(address);

			return ResponseEntity.ok(gps);
		} catch (Exception e) {
			log.error("encryptPwd", e);
			return ResponseEntity.badRequest().body(e);
		}

	}

	@RequestMapping(value = "/enterExitTimeOfLocation/{jobTruckId}")
	public ResponseEntity<Object> enterExitTimeOfLocation(@PathVariable String jobTruckId) {

		try {
			trackTraceService.getEnterExitTimeOfLocation(jobTruckId);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
		}

		return ResponseEntity.ok("OK");
	}
		
	private void initLocGpsImplement() throws Exception {

		List<TCkCtLocation> locList = ckCtLocationDao.findByGPSisNull();

		for (TCkCtLocation loc : locList) {
			String addressOrName = null;
			
			if (StringUtils.isNotBlank(loc.getLocAddress())) {
				addressOrName = loc.getLocAddress();
			} else if (StringUtils.isNotBlank(loc.getLocName())) {
				addressOrName = loc.getLocName();
			} 
			String gps = coordinateService.fetchCoordinate(addressOrName);

			if (StringUtils.isNotBlank(gps)) {
				loc.setLocGps(gps);
				ckCtLocationDao.saveOrUpdate(loc);
			}
		}

	}

}
