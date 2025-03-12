package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkAttachJson;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.ClicTruckMiscService;
import com.guudint.clickargo.common.enums.JobActions;
import com.guudint.clickargo.controller.AbstractCkController;
import com.guudint.clickargo.controller.CustomSerializerProvider;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

/**
 * This controller is for the trucking related jobs. 
 */
@RequestMapping(value = "/api/v1/clickargo/clictruck/job/")
@CrossOrigin
public class TruckJobController extends AbstractCkController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TruckJobController.class);

	@Autowired
	private ClicTruckMiscService jobTruckMiscService;

	@PostConstruct
	public void configureObjectMapper() {
		objectMapper.setSerializerProvider(new CustomSerializerProvider());
	}

	/**
	 * POST operation endpoint to create a clicdo job entity, i.e. /api/ck/doi/co,
	 * /api/ck/doi/ff
	 *
	 * @param entity i.e. jobDoiCo, taskDoiFf
	 * @param object request body payload
	 * @return
	 */
	@PostMapping("{entity}")
	public ResponseEntity<Object> createEntity(@PathVariable String entity, @RequestBody String object) {
		log.debug("createEntity");
		return super.createEntity(entity, object);
	}

	/**
	 * GET operation endpoint to retrieve a clicdo job entity by identifier,i.e.
	 * /api/ck/doi/job/co/da604367-d2e8-47d3-85bb-84b99fd44d37,
	 * /api/ck/doi/job/ff/60835e7f-3658-4814-9125-9864d015e582
	 *
	 * @param entity i.e. jobDoiCo, taskDoiFf
	 * @param id     entity identifier, i.e da604367-d2e8-47d3-85bb-84b99fd44d37
	 * @return
	 */
	@GetMapping("{entity}/{id}")
	public ResponseEntity<Object> getEntityById(@PathVariable String entity, @PathVariable String id) {
		log.debug("getEntityById");
		if (StringUtils.isNotBlank(id) && StringUtils.equalsIgnoreCase("-", id)) {
			return super.newEntity(entity);
		} else {
			return super.getEntityById(entity, id);
		}

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
	public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity,
			@PathVariable String id) {
		log.debug("updateEntity");
		return super.updateEntity(object, entity, id);
	}

	/**
	 * DELETE operation endpoint to remove/delete a clicdo entity, i.e.
	 * /api/ck/doi/job/co/da604367-d2e8-47d3-85bb-84b99fd44d37,
	 * /api/ck/doi/job/ff/60835e7f-3658-4814-9125-9864d015e582
	 *
	 * @param entity i.e. jobDoiCo, taskDoiFf
	 * @param id     entity identifier, i.e. a604367-d2e8-47d3-85bb-84b99fd44d37
	 * @return
	 */
	@DeleteMapping("{entity}/{id}")
	public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
		log.debug("getEntityById");
		return super.deleteEntityById(entity, id);
	}

	/**
	 * GET operation endpoint to retrieve a collection of clicdo entities by
	 * criteria/params,i.e.
	 * /api/ck/doi/job/co/list?param1=value1&param2=value2&param3=value3
	 * /api/ck/doi/job/ff/list?param1=value1&param2=value2&param3=value3
	 *
	 * @param entity i.e. jobDoiCo, taskDoiFf
	 * @param params criteria to retrieve specific collection of entities
	 * @return
	 */
	@GetMapping("{entity}/list")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		log.debug("getEntitiesBy");
		return super.getEntitiesBy(entity, params);
	}

	@PutMapping("{entity}/{id}/reject")
	public synchronized ResponseEntity<Object> updateReject(@RequestBody String remarks, @PathVariable String entity,
			@PathVariable String id) {
		log.debug("updateEntity");
		return jobTruckMiscService.updateJobTruckByAction(JobActions.REJECT, id, remarks);
	}

	/**
	 * This methods retrieves the list of trip attachment associated with a truck job
	 * @param jobId
	 * @return
	 */
	@GetMapping(value = "{entity}/{jobId}/tripList")
	public ResponseEntity<Object> getTripList(@PathVariable String jobId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			List<CkAttachJson> setJson = jobTruckMiscService.getTripListByJobId(jobId);
			return ResponseEntity.ok(setJson);

		} catch (Exception ex) {
			log.error("getTripAttachments ", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
}
