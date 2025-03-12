package com.guudint.clickargo.clictruck.common.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.guudint.clickargo.clictruck.common.dto.CkCtAccnInqReq;
import com.guudint.clickargo.clictruck.common.service.impl.CkCtAccnInqReqServiceImpl;
import com.guudint.clickargo.controller.AbstractCkController;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@RequestMapping(value = "/api/v1/clickargo/clictruck/inquiry")
@CrossOrigin
public class AccnInquiryController extends AbstractCkController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(AccnInquiryController.class);

	@Autowired
	private CkCtAccnInqReqServiceImpl accnInqReqService;

	@RequestMapping(value = "/{entity}", method = RequestMethod.POST)
	public ResponseEntity<Object> createEntity(@PathVariable String entity, @RequestBody String object) {
		log.debug("createEntity");
		if (entity.equalsIgnoreCase("accn")) {
			//this is implemented differently as the submission don't have principal
			return submitAccnInquiryRequest(entity, object);

		}
		return super.createEntity(entity, object);

	}

	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> getAccnInquiry(@PathVariable String entity, @PathVariable String id) {
		return super.getEntityById(entity, id);
	}
	
	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity,
			@PathVariable String id) {
		log.debug("updateEntity");
		return super.updateEntity(object, entity, id);
	}
	
	@RequestMapping(value = "/{entity}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
		log.debug("deleteEntityById");
		
		try {
			if (entity.equalsIgnoreCase("accn")) {
				return null;
//				return ResponseEntity.ok(ckAccnAttService.deleteAttachment(id, getPrincipal()));
			} else {
				return super.deleteEntityById(entity, id);
			}
		} catch (Exception e) {
            log.error("deleteEntityById", e);
            ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
        }
	}

	@GetMapping(value = "/{entity}/list")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		log.debug("getEntitiesBy");
		return super.getEntitiesBy(entity, params);
	}

	// Helper Methods
	//////////////
	private ResponseEntity<Object> submitAccnInquiryRequest(String entity, String object) {
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			Class<?> entityClass = Class.forName(entityDTOs.get(entity));
			Object dto = objectMapper.readValue(object, entityClass);
			AbstractDTO<?, ?> entityDto = (AbstractDTO<?, ?>) dto;
			log.debug(entityDto.toJson());

			CkCtAccnInqReq req = (CkCtAccnInqReq) entityDto;
			return ResponseEntity.ok(accnInqReqService.createAccnInquiryRequest(req));
		} catch (Exception e) {
			log.error("submitAccnInquiryRequest", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}
}
