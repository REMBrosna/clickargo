package com.guudint.clickargo.clictruck.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.admin.shell.dto.response.ShellCredentialConfigDto;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.config.model.TCoreSysparam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.common.service.CkCtVehService;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/vehicle")
@CrossOrigin
public class VehicleController {

	@Autowired
	private CkCtVehService ckCtVehService;

	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> coreSysparamDao;

	@GetMapping("/veh-type/{companyId}")
	public ResponseEntity<Object> getVehTypeByCompanyId(@PathVariable String companyId) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			serviceStatus.setStatus(STATUS.SUCCESS);
			serviceStatus.setData(ckCtVehService.getVehTypeByCompany(companyId));
			return ResponseEntity.ok(serviceStatus);
		} catch (PathNotFoundException ex) {
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/location/history/{ident}/{start}/{end}")
	public ResponseEntity<Object> getVehTypeByCompanyId(@PathVariable String ident, @PathVariable String start, @PathVariable String end) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			ObjectMapper mapper = new ObjectMapper();
			serviceStatus.setStatus(STATUS.SUCCESS);
			String key = getSysParam("CLICTRUCK_TRUCK_LOCATION_HISTORY");
			HashMap apiValue = mapper.readValue(key, HashMap.class);
			RestTemplate restTemplate = new RestTemplate();
			String url = String.format("%s?key=%s&ident=%s&start=%s&end=%s", apiValue.get("apiUrl"), apiValue.get("apiKey"), ident, start, end);
			return ResponseEntity.ok(restTemplate.getForObject(url, String.class));
		} catch (Exception ex) {
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
		}
		return ResponseEntity.ok(new ArrayList<>());
	}

	protected String getSysParam(String key) throws Exception {
		if (StringUtils.isBlank(key))
			throw new ParameterException("param key null or empty");
		TCoreSysparam sysParam = coreSysparamDao.find(key);
		if (sysParam != null) {
			return sysParam.getSysVal();
		}
		throw new EntityNotFoundException("sys param config " + key + " not set");
	}
}
