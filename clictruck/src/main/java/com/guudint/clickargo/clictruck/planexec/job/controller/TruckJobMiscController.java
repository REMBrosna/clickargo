package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCardTruck;
import com.vcc.camelone.common.audit.model.TCoreAuditlog;
import com.vcc.camelone.common.dao.GenericDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.admin.contract.dto.ContractDto;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.service.CkCtToInvoiceService;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTerm;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTerm;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckAddtlAttrService;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.JobTruckStateServiceImpl;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripService;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.ShipmentTypes;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.PermissionException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.common.service.entity.AbstractEntityService;
import com.vcc.camelone.master.controller.PathNotFoundException;
import com.vcc.camelone.util.PrincipalUtilService;

@RequestMapping(value = "/api/v1/clickargo/clictruck/job/truck")
@CrossOrigin
@RestController
public class TruckJobMiscController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TruckJobMiscController.class);
	@Autowired
	private CkJobTruckServiceUtil jobTruckUtilService;
	@Autowired
	private CkCtToInvoiceService ckCtToInvoiceService;

	@Autowired
	private CkCtTripService tripService;

	@Autowired
	private AbstractEntityService<TCkCtJobTerm, String, CkCtJobTerm> jobTermService;

	@Autowired
	private PrincipalUtilService principalUtilService;

	@Autowired
	private CkJobTruckAddtlAttrService addtlAttrService;

	@Autowired
	private JobTruckStateServiceImpl jobTruckStateServiceImpl;

	@Autowired
	private GenericDao<TCoreAuditlog, String> auditLogDao;

	/**
	 * Custom endpoint to initialize a trucking job based on shipment type (export,
	 * import, domestic)
	 */
	@GetMapping("new/{shipmentType}")
	public ResponseEntity<Object> initTruckJob(@PathVariable String shipmentType) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isEmpty(shipmentType))
				throw new ParameterException("param appType null or empty");

			if (ShipmentTypes.getByName(shipmentType) == null)
				throw new ParameterException("param shipmentType " + shipmentType + " is not valid");

			return ResponseEntity.ok(jobTruckUtilService.initTruckJob(shipmentType));

		} catch (PathNotFoundException ex) {
			log.error("initTruckJob", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (PermissionException ex) {
			log.error("initTruckJob", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("initTruckJob", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * This endpoint is applicable per trip.
	 */
	@GetMapping("/details/invoice/{tripId}")
	public ResponseEntity<?> initJobInvoice(@PathVariable String tripId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			Optional<CkCtToInvoice> optCkCtToInvoice = ckCtToInvoiceService.getByTripId(tripId);
			if (optCkCtToInvoice.isPresent()) {
				return ResponseEntity.ok(optCkCtToInvoice.get());
			}

			serviceStatus.setErr(new ServiceError(404, "No Data Trips"));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(serviceStatus);
		} catch (PathNotFoundException ex) {
			log.error("initJobInvoice", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (PermissionException ex) {
			log.error("initJobInvoice", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (Exception ex) {
			log.error("initJobInvoice", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * This endpoint is applicable per trip.
	 */
	@GetMapping("/trips/{jobId}")
	public ResponseEntity<?> getTrips(@PathVariable String jobId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			List<CkCtTrip> trips = tripService.findTripsByTruckJobAndStatus(jobId,
					Arrays.asList(RecordStatus.ACTIVE.getCode()));

			return ResponseEntity.ok(trips);

		} catch (Exception ex) {
			log.error("initJobInvoice", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/jobTerms")
	public ResponseEntity<Object> createEntity(@RequestBody List<CkCtJobTerm> jobTerms) {
		log.debug("createEntity");

		ServiceStatus serviceStatus = new ServiceStatus();

		try {

			if (jobTerms == null || jobTerms.size() == 0)
				throw new ParameterException("param jobTerms null or empty");

			if (principalUtilService.getPrincipal() == null) {
				throw new Exception("Please login first!");
			}

			List<CkCtJobTerm> rst = jobTerms.stream().map(jt -> {
				try {
					return jobTermService.add(jt, principalUtilService.getPrincipal());
				} catch (Exception e) {
					log.error("", e);
					return null;
				}
			}).collect(Collectors.toList());

			return ResponseEntity.ok(rst);

		} catch (Exception ex) {
			log.error("initJobInvoice", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * To load the additional fields configured by contract.
	 */
	@PostMapping(value = "/addtl/attr")
	public ResponseEntity<Object> getAdditionalFields(@RequestBody ContractDto dto) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (dto == null)
				throw new ProcessingException("param dto is null");

			return ResponseEntity
					.ok(addtlAttrService.getAdditionalFieldsByContract(dto.getToAccnId(), dto.getCoAccnId()));

		} catch (Exception ex) {
			log.error("getAdditionalFields", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * https://jira.vcargocloud.com/browse/CT2SG-226 Allow TO to modify the driver
	 * and truck for jobs that are Assigned, On-going, Paused statuses.
	 * 
	 * @param map
	 * @return
	 */
	@PostMapping(value = "/updateDriverTruck")
	public ResponseEntity<Object> getAdditionalFields(@RequestBody CkJobTruck ckJobTruck) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (ckJobTruck == null)
				throw new ProcessingException("param ckJobTruck is null");

			return ResponseEntity.ok(jobTruckUtilService.updateTruckDriver(ckJobTruck));

		} catch (Exception ex) {
			log.error("getAdditionalFields", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/splitJob/{jobTruckId}")
	public ResponseEntity<Object> splitJob(@PathVariable String jobTruckId, @RequestParam Integer num) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (num == null)
				throw new ProcessingException("param num is null");
			jobTruckStateServiceImpl.splitJob(jobTruckId, num, principalUtilService.getPrincipal());
			
			return ResponseEntity.ok("");

		} catch (Exception ex) {
			log.error("splitJob", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/job/audit/{audtId}")
	public ResponseEntity<?> getJobAudit(@PathVariable String audtId,@RequestParam String params) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			ObjectMapper objectMapper = new ObjectMapper();
			List<String> tripIds = objectMapper.readValue(params, new TypeReference<List<String>>() {});

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("audtReckey", audtId);
			parameters.put("tripIds", tripIds);
			String hql = "FROM TCoreAuditlog o WHERE o.audtReckey =:audtReckey OR o.audtReckey IN (:tripIds) ORDER BY o.audtTimestamp ASC ";
			List<TCoreAuditlog> entity = auditLogDao.getByQuery(hql, parameters);

			return ResponseEntity.ok(entity);

		} catch (Exception ex) {
			log.error("getJobAudit", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
}
