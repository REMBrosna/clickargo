package com.guudint.clickargo.clictruck.admin.account.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.guudint.clickargo.clictruck.admin.account.dto.AccountSuspend;
import com.guudint.clickargo.clictruck.admin.account.service.impl.AccountResumptionServiceImpl;
import com.guudint.clickargo.clictruck.admin.account.service.impl.AccountSuspendServiceImpl;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.controller.AbstractCkController;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@RequestMapping("/api/v1/clickargo/clictruck/account")
@CrossOrigin
public class AccountSuspendController extends AbstractCkController {
	
	private static Logger LOG = Logger.getLogger(AccountSuspendController.class);
	
	@Autowired
	AccountSuspendServiceImpl accountSuspendServiceImpl;
	
	@Autowired
	AccountResumptionServiceImpl accountResumptionServiceImpl;
	
	@GetMapping("/{entity}/list")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		LOG.debug("getEntitiesBy Controller");
		return super.getEntitiesBy(entity, params);
	}
	
	@GetMapping("/{entity}/{id}")
	public ResponseEntity<Object> getEntityById(@PathVariable String entity, @PathVariable String id) {
		LOG.debug("getEntityById Controller");
		if (StringUtils.isNotBlank(id) && StringUtils.equalsIgnoreCase(ICkConstant.DASH, id)) {
			return super.newEntity(entity);
		} else {
			return super.getEntityById(entity, id);
		}
	}
	
	@PutMapping("/{entity}/{id}")
	public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity,
			@PathVariable String id) {
		LOG.debug("updateEntity Controller");
		return super.updateEntity(object, entity, id);
	}
	
	@PutMapping("/{entity}")
	public ResponseEntity<?> updateStatus(@PathVariable String entity, @RequestBody AccountSuspend accountSuspend) throws Exception {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if(entity.equalsIgnoreCase("accnSuspend")) {
				serviceStatus.setData(accountSuspendServiceImpl.updateStatus(accountSuspend));
				serviceStatus.setStatus(STATUS.SUCCESS);
			} else if (entity.equalsIgnoreCase("accnResumption")) {
				serviceStatus.setData(accountResumptionServiceImpl.updateStatus(accountSuspend));
				serviceStatus.setStatus(STATUS.SUCCESS);
			}
			return ResponseEntity.ok().body(serviceStatus);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error(e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}
	
}
