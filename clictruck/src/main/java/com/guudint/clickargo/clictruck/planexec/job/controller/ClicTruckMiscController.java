package com.guudint.clickargo.clictruck.planexec.job.controller;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.guudint.clickargo.clictruck.planexec.job.service.impl.ClicTruckMiscService;
import com.guudint.clickargo.master.enums.AttachmentTypes;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;

/**
 * Misc controller for entity related apis that cannot be catered from other
 * existing associated tables.
 * 
 * TODO can make this generic for other entities
 */
@RequestMapping(value = "/api/v1/clickargo/clictruck/misc")
@CrossOrigin
@Controller
public class ClicTruckMiscController extends AbstractPortalController {
	// Static Attributes
	////////////////////
	private static Logger LOG = Logger.getLogger(ClicTruckMiscController.class);

	@Autowired
	private ClicTruckMiscService clicTruckMiscService;

	/**
	 * Load the truck operators associated with this principal cargo owner or
	 * freight forwarder.
	 */
	@GetMapping(value = "/truckoperators")
	public ResponseEntity<Object> getTruckOperatorsByCoFf() {
		LOG.debug("getTruckOperatorsByCoFf");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(clicTruckMiscService.getTruckOperatorsByCoFf(getPrincipal()));
		} catch (PathNotFoundException ex) {
			LOG.error("getTruckOperatorsByCoFf", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("getTruckOperatorsByCoFf", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Load the trucks with jobs.
	 */
	@GetMapping(value = "/towithjobs")
	public ResponseEntity<Object> getTruckingOperatorsWithJobs() {
		LOG.debug("getTruckOperatorsByCoFf");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(clicTruckMiscService.getTruckingOperatorsWithPayables());
		} catch (PathNotFoundException ex) {
			LOG.error("getTruckingOperatorsWithJobs", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("getTruckingOperatorsWithJobs", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Load the trip locations based on the Truck operator
	 */
	@GetMapping(value = "/locations/{accnId}")
	public ResponseEntity<Object> getLocationsByTruckOperator(@PathVariable String accnId) {
		LOG.debug("getLocationsByTruckOperator");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isEmpty(accnId))
				throw new Exception("param accnId empty or null");

			return ResponseEntity.ok(clicTruckMiscService.getLocationsByTruckOperator(getPrincipal(), accnId));
		} catch (PathNotFoundException ex) {
			LOG.error("getLocationsByTruckOperator", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("getLocationsByTruckOperator", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Gets the attachment by the parent job ID.
	 */
	@RequestMapping(value = "/job/{parentJobId}/{attType}", method = RequestMethod.GET)
	public ResponseEntity<Object> viewUploadedBL(@PathVariable String parentJobId, @PathVariable String attType) {
		LOG.debug("viewUploadedBL");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (!EnumUtils.isValidEnum(AttachmentTypes.class, attType))
				throw new ParameterException("attType is not valid");

			return ResponseEntity.ok(clicTruckMiscService.getAttachment(AttachmentTypes.valueOf(attType), parentJobId));
		} catch (PathNotFoundException ex) {
			LOG.error("viewUploadedBL", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("viewUploadedBL", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

}
