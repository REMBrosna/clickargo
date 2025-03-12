package com.guudint.clickargo.clictruck.dsv.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.dsv.service.IDsvService;
import com.guudint.clickargo.clictruck.dsv.service.impl.DSVProcesAfterDeliverdService;
import com.guudint.clickargo.clictruck.scheduler.sg.DSVAsProcessAfterDeliverScheduler;
import com.guudint.clickargo.clictruck.scheduler.sg.DSVLoadXMLFileScheduler;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/dsv")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class DsvController extends AbstractPortalController {

	private static Logger log = Logger.getLogger(DsvController.class);

	@Autowired
	private DSVLoadXMLFileScheduler dSVLoadXMLFileScheduler;

	@Autowired
	private DSVAsProcessAfterDeliverScheduler dSVProcesAfterDeliverScheduler;

	@Autowired
	private DSVProcesAfterDeliverdService procesAfterDeliverdService;

	@Autowired
	private IDsvService dsvService;

	/**
	 * Run scheduler job to load XML files;
	 * 
	 * @return
	 */
	@RequestMapping(value = "/loadFromDSV")
	public ResponseEntity<Object> loadFromDSV() {

		try {
			dSVLoadXMLFileScheduler.doJob();

			return ResponseEntity.ok("Done!");

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/auxiliaryRefreshJobTruckExt/{shId}")
	public ResponseEntity<Object> auxiliaryRefreshJobTruckExt(@PathVariable String shId) {

		try {
			dsvService.auxiliaryRefreshJobTruckExt(shId);

			return ResponseEntity.ok("Done!");

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}

	/**
	 * Run scheduler to process after job is delivered.
	 * 
	 * @param jobTruckId
	 * @return
	 */
	@RequestMapping(value = "/jobDelivered")
	public ResponseEntity<Object> afterDsvJobIsDelivered() {

		try {

			dSVProcesAfterDeliverScheduler.doJob();

			return ResponseEntity.ok("");

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}
///////////////////// status Message PDF, ePOD,  /////////////
	
	
	@RequestMapping(value = "/afterDsvJobIsDelivered/{shId}")
	public ResponseEntity<Object> afterDsvJobIsDelivered(@PathVariable String shId) {

		try {

			procesAfterDeliverdService.afterDsvJobIsDelivered(shId);

			return ResponseEntity.ok("");

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}

	///////////////////// status Message PDF file ////////////////////////////////////////////////////
	
	@RequestMapping(value = "/processStatusMessage/{shId}")
	public ResponseEntity<Object> generateStatusMessage(@PathVariable String shId) {
		try {
			
			procesAfterDeliverdService.processStatusMessage(shId);
			
			return ResponseEntity.ok("");
		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}
	}

	@RequestMapping(value = "/generateStatusXMLfile/{shId}")
	public ResponseEntity<Object> generateStatusXMLfile(@PathVariable String shId) {

		try {
			procesAfterDeliverdService.generateStatusXMLfile(shId);
			return ResponseEntity.ok("");
		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}
	}

	@RequestMapping(value = "/pushStatusXMLfile2SFTP/{shId}")
	public ResponseEntity<Object> pushStatusXMLfile2SFTP(@PathVariable String shId) {

		try {
			procesAfterDeliverdService.pushStatusXMLfile2SFTP(shId);
			return ResponseEntity.ok("");
		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}
	}

	///////////////////// ePOD PDF file /////////////////////////////////////////////////////////////////
	
	
	@RequestMapping(value = "/processEpod/{shId}")
	public ResponseEntity<Object> processEpod(@PathVariable String shId) {

		try {

			procesAfterDeliverdService.processEpod(shId);

			return ResponseEntity.ok(shId);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}

	@RequestMapping(value = "/createEpodFile/{shId}")
	public ResponseEntity<Object> createEpodFile(@PathVariable String shId) {

		try {

			procesAfterDeliverdService.createEpodFile(shId);

			return ResponseEntity.ok(shId);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}

	@RequestMapping(value = "/sendPodPdf/{shId}")
	public ResponseEntity<Object> sendPodPdf(@PathVariable String shId) {

		try {

			procesAfterDeliverdService.sendEpodEmail(shId);

			return ResponseEntity.ok(shId);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}
	
	///////////////////// Photo PDF file //////////////////////////////////////////////////////////////////////////////

	@RequestMapping(value = "/processPhoto/{shId}")
	public ResponseEntity<Object> processPhoto(@PathVariable String shId) {

		try {

			procesAfterDeliverdService.processPhoto(shId);

			return ResponseEntity.ok(shId);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}

	@RequestMapping(value = "/createPhotoFile/{shId}")
	public ResponseEntity<Object> createPhotoFile(@PathVariable String shId) {

		try {

			procesAfterDeliverdService.createPhotoFile(shId);

			return ResponseEntity.ok(shId);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}

	@RequestMapping(value = "/sendPhotoEmail/{shId}")
	public ResponseEntity<Object> sendPhotoEmail(@PathVariable String shId) {

		try {

			procesAfterDeliverdService.sendPhotoEmail(shId);

			return ResponseEntity.ok(shId);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);

			return ResponseEntity.ok(e.getMessage());
		}
	}
}
