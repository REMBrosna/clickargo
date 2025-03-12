package com.guudint.clickargo.clictruck.planexec.job.mobile.controller;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.guudint.clickargo.controller.AbstractCkController;

/**
 * This controller for the mobile app endpoint.
 */
@RequestMapping(value = "/api/v1/clickargo/clictruck/mobile/job/")
@CrossOrigin
public class TruckJobMobileController extends AbstractCkController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TruckJobMobileController.class);

	@GetMapping("{entity}/{id}")
	public ResponseEntity<Object> getEntityById(@PathVariable String entity, @PathVariable String id) {
		log.debug("getEntityById");
		return super.getEntityById(entity, id);

	}

	/**
	 * PUT operation endpoint to update a clicdo job entity, i.e.
	 * /api/ck/doi/job/co/da604367-d2e8-47d3-85bb-84b99fd44d37,
	 * /api/ck/doi/job/ff/60835e7f-3658-4814-9125-9864d015e582
	 *
	 * @param object business model representation of the entity
	 * @param entity i.e. jobDoiCo, taskDoiFf
	 * @param id     entity identifier, i.e. a604367-d2e8-47d3-85bb-84b99fd44d37
	 * @return
	 */
	@PutMapping("{entity}/{id}")
	public synchronized ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity,
			@PathVariable String id) {
		log.debug("updateEntity");
		return super.updateEntity(object, entity, id);
	}
}
