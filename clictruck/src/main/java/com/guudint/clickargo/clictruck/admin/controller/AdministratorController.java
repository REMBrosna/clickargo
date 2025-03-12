package com.guudint.clickargo.clictruck.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.guudint.clickargo.clictruck.common.dto.NotificationTemplateName;
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

import com.guudint.clickargo.clictruck.admin.contract.service.impl.CkCtContractServiceImpl;
import com.guudint.clickargo.clictruck.admin.dto.Parties;
import com.guudint.clickargo.clictruck.admin.ratetable.service.ICkCtRateTableService;
import com.guudint.clickargo.clictruck.admin.service.ClicTruckAccnService;
import com.guudint.clickargo.clictruck.admin.shell.service.impl.CkCtShellCardTruckServiceImpl;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.service.CkCtChassisService;
import com.guudint.clickargo.clictruck.common.service.CkCtCo2xService;
import com.guudint.clickargo.clictruck.common.service.CkCtDeptService;
import com.guudint.clickargo.clictruck.common.service.CkCtDrvService;
import com.guudint.clickargo.clictruck.common.service.CkCtLocationService;
import com.guudint.clickargo.clictruck.common.service.CkCtVehExtService;
import com.guudint.clickargo.clictruck.common.service.CkCtVehMlogService;
import com.guudint.clickargo.clictruck.common.service.CkCtVehService;
import com.guudint.clickargo.clictruck.common.service.CkCtVehTrackService;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtRentalAppServiceImpl;
import com.guudint.clickargo.clictruck.util.ObjectUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.controller.AbstractCkController;
import com.guudint.clickargo.controller.CustomSerializerProvider;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.controller.entity.EntityFilterResponse;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.PermissionException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;
import com.vcc.camelone.util.crypto.AESEcbEncryption;

@RequestMapping("/api/v1/clickargo/clictruck/administrator")
@CrossOrigin
public class AdministratorController extends AbstractCkController {

	private static final Logger LOG = Logger.getLogger(AdministratorController.class);

	@Autowired
	private CkCtVehService ckCtVehService;

	@Autowired
	private CkCtVehTrackService ckCtVehTrackService;

	@Autowired
	private CkCtDrvService ckCtDrvService;

	@Autowired
	private CkCtLocationService ckCtLocationService;

	@Autowired
	private ICkCtRateTableService ckCtRateTableService;

	@Autowired
	private ClicTruckAccnService accnService;

	@Autowired
	private CkCtContractServiceImpl ckCtContractServiceImpl;

	@Autowired
	private CkCtChassisService ckCtChassisService;

	@Autowired
	private CkCtVehExtService ckCtVehExtService;

	@Autowired
	private CkCtVehMlogService ckCtVehMlogService;

	@Autowired
	private CkCtRentalAppServiceImpl ckCtRentalAppService;

	@Autowired
	private CkCtCo2xService co2xService;

	@Autowired
	private CkCtDeptService deptService;

	@Autowired
	private CkCtShellCardTruckServiceImpl ckCtShellCardTruckService;

	@PostConstruct
	public void configureObjectMapper() {
		objectMapper.setSerializerProvider(new CustomSerializerProvider());
	}

	@PostMapping("/{entity}")
	public ResponseEntity<Object> createEntity(@PathVariable String entity, @RequestBody String requestBody) {
		LOG.debug("createEntity Controller");
		return super.createEntity(entity, requestBody);
	}

	@GetMapping("/{entity}/{id}")
	public ResponseEntity<Object> getEntityById(@PathVariable String entity, @PathVariable String id) {
		LOG.debug("getEntityById Controller");
		if (StringUtils.isNotBlank(id) && StringUtils.equalsIgnoreCase(ICkConstant.DASH, id)) {
			return super.newEntity(entity);
		} else if (entity.equalsIgnoreCase("driver")) {
			// In the case of driver, display password only inside the form
			try {
				return ResponseEntity.ok(ckCtDrvService.findById(id, 'Y'));
			} catch (Exception ex) {
				LOG.error(" Driver getEntityById ", ex);
			}
		} else {
			return super.getEntityById(entity, id);
		}
		return null;
	}

	@GetMapping("/vehExt/{id}")
	public ResponseEntity<Object> getEntityByIdNew(@PathVariable String id) {
		LOG.debug("getEntityById");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			id = this.decryptId(id);
			Optional<Object> opEntity = Optional.ofNullable(ckCtVehExtService.findVehExtById(id));
			if (!opEntity.isPresent()) {
				serviceStatus.setData((Object) null);
				throw new Exception("entity null or empty");
			} else {
				return ResponseEntity.ok(opEntity.get());
			}
		} catch (PathNotFoundException ex) {
			LOG.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (PermissionException ex) {
			LOG.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.PERMISSION_FAILED);
			serviceStatus.setErr(new ServiceError(403, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.FORBIDDEN);
		} catch (ValidationException ex) {
			LOG.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			LOG.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	private String decryptId(String id) {
		try {
			Principal principal = this.getPrincipal();
			String key = principal.getUserAccnId() + principal.getUserId();
			key = StringUtils.rightPad(key, 32, "0");
			return AESEcbEncryption.decrypt(id, key);
		} catch (Exception var4) {
			LOG.error(var4.getMessage());
			return id;
		}
	}

	@PutMapping("/{entity}/{id}")
	public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity,
			@PathVariable String id) {
		LOG.debug("updateEntity Controller");
		return super.updateEntity(object, entity, id);
	}

	@DeleteMapping("/{entity}/{id}")
	public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
		LOG.debug("deleteEntityById Controller");
		return super.deleteEntityById(entity, id);
	}

	@GetMapping("/{entity}/list")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		LOG.debug("getEntitiesBy Controller");
		return super.getEntitiesBy(entity, params);
	}

	@PutMapping("/{entity}/{id}/{status}")
	public ResponseEntity<?> updateStatus(@PathVariable String entity, @PathVariable String id,
			@PathVariable String status) {
		ServiceStatus serviceStatus = new ServiceStatus();

		// TODO: How to add security checking? Anyone call this URL?

		try {
			switch (entity) {
			case "vehicle":
				serviceStatus.setData(ckCtVehService.updateStatus(id, status));
				break;
			case "trackDevice":
				serviceStatus.setData(ckCtVehTrackService.updateStatus(id, status));
				break;
			case "driver":
				serviceStatus.setData(ckCtDrvService.updateStatus(id, status));
				break;
			case "location":
				serviceStatus.setData(ckCtLocationService.updateStatus(id, status));
				break;
			case "ratetable":
				serviceStatus.setData(ckCtRateTableService.updateStatus(id, status));
				break;
			case "contract":
				serviceStatus.setData(ckCtRateTableService.updateStatus(id, status));
				break;
			case "chassis":
				serviceStatus.setData(ckCtChassisService.updateStatus(id, status));
				break;
			case "vehExt":
				serviceStatus.setData(ckCtVehExtService.updateStatus(id, status));
				break;
			case "vehMlog":
				serviceStatus.setData(ckCtVehMlogService.updateStatus(id, status));
				break;
			case "rentalApp":
				serviceStatus.setData(ckCtRentalAppService.updateStatus(id, status));
				break;
			case "co2x":
				serviceStatus.setData(co2xService.updateStatus(id, status));
				break;
			case "department":
				serviceStatus.setData(deptService.updateStatus(id, status));
				break;
			case "shellCardTruck":
				serviceStatus.setData(ckCtShellCardTruckService.updateStatus(id, status));
				break;
			default:
				break;
			}
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok().body(serviceStatus);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error(e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}catch (Exception e){
			LOG.error("", e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}

	@GetMapping("/parties/list")
	public ResponseEntity<?> getParties(@RequestParam Map<String, Object> params) {
		Parties parties = ObjectUtil.mapToObject(params, Parties.class);
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			serviceStatus.setData(accnService.getParties(parties));
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok().body(serviceStatus);
		} catch (Exception e) {
			LOG.error(e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}

	@GetMapping(value = "/truckoperators")
	public ResponseEntity<Object> getTruckOperatorsByCoFf() {
		LOG.debug("getTruckOperatorsByCoFf");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			return ResponseEntity.ok(ckCtContractServiceImpl.getTruckOperatorsByCoFf(getPrincipal(), true));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/associated-vehicle/list")
	public ResponseEntity<Object> getAssociatedVehList(@RequestParam String drvId, @RequestParam String vehType)
			throws Exception {
		LOG.debug("getAssociatedVehList Controller");

		try {
			List<CkCtVeh> data = ckCtVehService.associatedVehicle(drvId, vehType);
			EntityFilterResponse filterResponse = new EntityFilterResponse();
			filterResponse.setiTotalRecords(data.size());
			filterResponse.setiTotalDisplayRecords(data.size());
			filterResponse.setAaData((ArrayList) data);
			return ResponseEntity.ok(filterResponse);
		} catch (Exception e) {
			LOG.error("", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}

	}

	@GetMapping("/rental-params/providers")
	public ResponseEntity<Object> getRentalProviders() throws Exception {
		LOG.debug("getRentalProviders Controller");

		try {
			String providers = ckCtRentalAppService.getRentalProviders();
			return ResponseEntity.ok(providers);
		} catch (Exception e) {
			LOG.error("getRentalProviders", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}

	@GetMapping("/truck/image/{code}/{fileName}")
	public ResponseEntity<Object> getRentalTruckImage(@PathVariable String code, @PathVariable String fileName)
			throws Exception {
		LOG.debug("getRentalTruckImage Controller");
		try {
			String providers = ckCtRentalAppService.getBase64Str(code, fileName);
			return ResponseEntity.ok(providers);
		} catch (Exception e) {
			LOG.error("getRentalTruckImage", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}

	@GetMapping("/driver/resetPassword/{drvId}")
	public ResponseEntity<Object> resetDriverPassword(@PathVariable String drvId) throws Exception {
		LOG.debug("resetDriverPassword Controller");
		try {

			String rst = ckCtDrvService.resetDriverPassword(drvId, NotificationTemplateName.RESET_PASSWORD.name());

			return ResponseEntity.ok(rst);
		} catch (Exception e) {
			LOG.error("getRentalTruckImage", e);
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
}
