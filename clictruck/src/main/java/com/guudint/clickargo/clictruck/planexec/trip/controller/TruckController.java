package com.guudint.clickargo.clictruck.planexec.trip.controller;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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

import com.guudint.clickargo.controller.AbstractCkController;
import com.guudint.clickargo.controller.CustomSerializerProvider;

@RequestMapping(value = "/api/v1/clickargo/clictruck/truck/")
@CrossOrigin
public class TruckController extends AbstractCkController {
	
	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TruckController.class);
	
	/**
	 * 
	 * (non-Javadoc)
	 *
	 * @see com.guudint.clickargo.controller.AbstractCkController#configureObjectMapper()
	 *
	 */
	@PostConstruct
	public void configureObjectMapper() {
		objectMapper.setSerializerProvider(new CustomSerializerProvider());
	}
	
	@GetMapping("{entity}")
	public ResponseEntity<Object> newEntity(@PathVariable String entity){
		return super.newEntity(entity);
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.AbstractEntityController#createEntity(java.lang.String,
	 *      java.lang.String)
	 *
	 */
	@PostMapping("{entity}")
	public ResponseEntity<Object> createEntity(@PathVariable String entity, @RequestBody String object) {
		log.debug("createEntity");
		return super.createEntity(entity, object);
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.AbstractEntityController#
	 * getEntityById(java.lang.String, java.lang.String)
	 *
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
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.AbstractEntityController#
	 * updateEntity(java.lang.String, java.lang.String, java.lang.String)
	 *
	 */
	@PutMapping("{entity}/{id}")
	public ResponseEntity<Object> updateEntity(@RequestBody String object, @PathVariable String entity,
			@PathVariable String id) {
		log.debug("updateEntity");
		return super.updateEntity(object, entity, id);
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.AbstractEntityController#
	 * deleteEntityById(java.lang.String, java.lang.String)
	 *
	 */
	@DeleteMapping("{entity}/{id}")
	public ResponseEntity<Object> deleteEntityById(@PathVariable String entity, @PathVariable String id) {
		log.debug("getEntityById");
		return super.deleteEntityById(entity, id);
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.controller.entity.AbstractEntityController#getEntitiesBy(java.lang.String,
	 *      java.util.Map)
	 *
	 */
	@GetMapping("{entity}/list")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		log.debug("getEntitiesBy");
		return super.getEntitiesBy(entity, params);
	}

}
