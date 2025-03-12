package com.guudint.clickargo.clictruck.sage.controller;

import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.guudint.clickargo.clictruck.sage.service.SageTaxService;
import com.guudint.clickargo.clictruck.tax.service.TaxInvoiceService;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.service.ICkSession;
import com.guudint.clickargo.controller.AbstractCkController;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/clickargo/clictruck/")
public class SageTaxController extends AbstractCkController {

	private static Logger LOG = Logger.getLogger(SageTaxController.class);

	@Autowired
	private SageTaxService sageTaxService;
	
	@Autowired
	private TaxInvoiceService taxInvoiceService;
	
	@Autowired
	protected ICkSession ckSession;

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

	@DeleteMapping("/{entity}/{id}")
	public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
		LOG.debug("deleteEntityById Controller");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			Principal principal = ckSession.getPrincipal();
			if (principal == null) {
				throw new ParameterException("principal is null");
			}
			serviceStatus.setData(sageTaxService.deleteById(id, principal));				
			serviceStatus.setStatus(STATUS.SUCCESS);
			return ResponseEntity.ok().body(serviceStatus);
		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
			LOG.error(e);
			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
			serviceStatus.setStatus(STATUS.EXCEPTION);
			return ResponseEntity.badRequest().body(serviceStatus);
		}
	}

	@GetMapping("/{entity}/list")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		LOG.debug("getEntitiesBy Controller");
		return super.getEntitiesBy(entity, params);
	}
	
	@PostMapping("/upload/{entity}")
    public ResponseEntity<Object> handleTaxInvoiceUpload(@RequestParam("file") MultipartFile file) throws Exception{
		LOG.debug("upload tax invoice");
        ServiceStatus serviceStatus = new ServiceStatus();
        Principal principal = getPrincipal();
		if (null == principal) {
			throw new ProcessingException("principal is null");
		}
		
		try {
			String savePath = taxInvoiceService.uploadTaxInvoice(file, principal);
			if (savePath != null) {
				return ResponseEntity.ok(savePath);
             } else {
            	 LOG.error("upload tax invoice");
      			serviceStatus.setStatus(STATUS.EXCEPTION);
      			serviceStatus.setErr(new ServiceError(-100, "Error uploading file"));
      			return new ResponseEntity<Object>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
             }
        }catch(Exception e) {
        	LOG.error("upload tax invoice");
 			serviceStatus.setStatus(STATUS.EXCEPTION);
 			serviceStatus.setErr(new ServiceError(-100, e));
 			return new ResponseEntity<Object>(serviceStatus, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    } 
	
//	@PutMapping("/{entity}/{id}/{status}")
//	public ResponseEntity<?> updateStatus(@PathVariable String entity, @PathVariable String id,
//			@PathVariable String status) {
//		ServiceStatus serviceStatus = new ServiceStatus();
//		try {
//			serviceStatus.setData(sageTaxService.updateStatus(id, status));				
//			serviceStatus.setStatus(STATUS.SUCCESS);
//			return ResponseEntity.ok().body(serviceStatus);
//		} catch (ParameterException | EntityNotFoundException | ProcessingException | ValidationException e) {
//			LOG.error(e);
//			serviceStatus.setErr(new ServiceError(-100, e.getMessage()));
//			serviceStatus.setStatus(STATUS.EXCEPTION);
//			return ResponseEntity.badRequest().body(serviceStatus);
//		}
//	}
	
}
