package com.guudint.clickargo.clictruck.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.common.service.ITemplateService;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;

@RequestMapping(value = "/api/v1/clickargo/clictruck/template/")
@CrossOrigin
public class TemplateController {

	@Autowired
	protected ApplicationContext applicationContext;

	private HashMap<String, String> templateServices;

	private ObjectMapper objectMapper = new ObjectMapper();

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TemplateController.class);

	@GetMapping("download/{entity}")
	public ResponseEntity<Object> downloadTemplate(@PathVariable String entity) {

		ServiceStatus serviceStatus = new ServiceStatus();
		log.debug("downloadTemplate");
		try {
			if (StringUtils.isEmpty(entity))
				throw new ParameterException("param entity null or empty");
			if (!templateServices.containsKey(entity))
				throw new PathNotFoundException("entity not mapped: " + entity);

			Object bean = applicationContext.getBean(templateServices.get(entity));
			ITemplateService service = (ITemplateService) bean;

			String base64Str = Base64Utils.encodeToString(service.download());
			serviceStatus.setData(base64Str);
			return ResponseEntity.ok(serviceStatus);

		} catch (ParameterException | PathNotFoundException ex) {
			log.error("downloadTemplate", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	@SuppressWarnings("unchecked")
	@PostMapping("upload/{entity}")
	public ResponseEntity<Object> uploadTemplate(@PathVariable String entity, @RequestBody String objData) {

		ServiceStatus serviceStatus = new ServiceStatus();
		log.debug("uploadTemplate");
		try {
			if (StringUtils.isEmpty(entity))
				throw new ParameterException("param entity null or empty");
			if (!templateServices.containsKey(entity))
				throw new PathNotFoundException("entity not mapped: " + entity);

			Map<String, Object> data = objectMapper.readValue(objData, HashMap.class);
			byte[] fileData = Base64Utils.decodeFromString((String) data.get("data"));

			Object bean = applicationContext.getBean(templateServices.get(entity));
			ITemplateService service = (ITemplateService) bean;

			service.upload(fileData);
			return ResponseEntity.ok(serviceStatus);

		} catch (ParameterException | PathNotFoundException ex) {
			log.error("uploadTemplate", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (ValidationException ex) {
			log.error("uploadTemplate", ex);
			serviceStatus.setData(objData);
			serviceStatus.setStatus(STATUS.VALIDATION_FAILED);
			serviceStatus.setErr(new ServiceError(-500, ex.getMessage()));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	public HashMap<String, String> getTemplateServices() {
		return templateServices;
	}

	public void setTemplateServices(HashMap<String, String> templateServices) {
		this.templateServices = templateServices;
	}

}
