package com.guudint.clicdo.common.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.guudint.clickargo.clictruck.attachments.service.ICtAttachmentService;
import com.guudint.clickargo.common.service.ICkSession;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.master.controller.PathNotFoundException;

@RequestMapping(value = "/api/v1/clickargo/clictruck/attach/")
@CrossOrigin
public class ClicTruckAttchController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ClicTruckAttchController.class);

	protected HashMap<String, String> attachServices;

	@Autowired
	protected ApplicationContext applicationContext;

	@Autowired
	@Qualifier("ckSessionService")
	private ICkSession ckSession;

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.IEntityServiceController#getEntityById(java.lang.String,
	 *      java.lang.String)
	 */
	@RequestMapping(value = "{entity}/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> getAttachment(@PathVariable String entity, @PathVariable String id) {
		log.debug("getAttachment");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isEmpty(entity))
				throw new ParameterException("param entity null or empty");
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");
			if (!attachServices.containsKey(entity))
				throw new PathNotFoundException("entity not mapped: " + entity);

			Object bean = applicationContext.getBean(attachServices.get(entity));
			ICtAttachmentService<?> service = (ICtAttachmentService<?>) bean;

			String base64Str = service.getAttachment(id);
			if (StringUtils.isBlank(base64Str)) {
				serviceStatus.setData(null);
				throw new Exception("Attachment not available.");
			}

			return ResponseEntity.ok(base64Str);
		} catch (PathNotFoundException ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "byJobId/{entity}/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> getAttachmentByJobId(@PathVariable String entity, @PathVariable String id) {
		log.debug("getAttachmentByJobId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if (StringUtils.isEmpty(entity))
				throw new ParameterException("param entity null or empty");
			if (StringUtils.isEmpty(id))
				throw new ParameterException("param id null or empty");
			if (!attachServices.containsKey(entity))
				throw new PathNotFoundException("entity not mapped: " + entity);

			Object bean = applicationContext.getBean(attachServices.get(entity));
			ICtAttachmentService<?> service = (ICtAttachmentService<?>) bean;
			Map<String, Object> attachment = service.getAttachmentByJobId(id, ckSession.getPrincipal());
			return ResponseEntity.ok().body(attachment);
		} catch (ParameterException e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (PathNotFoundException e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}


	@RequestMapping(value = "byParam/{entity}/{param}", method = RequestMethod.GET)
	public ResponseEntity<?> getAttachment2(@PathVariable String entity, @PathVariable String param){
		log.debug("getAttachment2");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (StringUtils.isEmpty(entity))
				throw new ParameterException("param entity null or empty");
			if (StringUtils.isEmpty(param))
				throw new ParameterException("param id null or empty");
			if (!attachServices.containsKey(entity))
				throw new PathNotFoundException("entity not mapped: " + entity);

			Object bean = applicationContext.getBean(attachServices.get(entity));
			ICtAttachmentService<?> service = (ICtAttachmentService<?>) bean;
			return ResponseEntity.ok(service.getAttachment2(param));
		} catch (PathNotFoundException ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (ParameterException ex) {
			log.error("getEntityById", ex);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, ex));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	public HashMap<String, String> getAttachServices() {
		return attachServices;
	}

	public void setAttachServices(HashMap<String, String> attachServices) {
		this.attachServices = attachServices;
	}

}
