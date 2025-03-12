package com.guudint.clickargo.clictruck.notification.controller;

import com.guudint.clickargo.clictruck.planexec.trip.controller.TruckController;
import com.guudint.clickargo.controller.AbstractCkController;
import com.guudint.clickargo.controller.CustomSerializerProvider;
import com.vcc.camelone.event.PortalCacheTimeStampEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Map;


@CrossOrigin
@RestController
@RequestMapping(value = "/api/v2/clickargo/master")
public class AlertServiceController extends AbstractCkController {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TruckController.class);

	/**
	 *
	 * (non-Javadoc)
	 *
	 * @see AbstractCkController#configureObjectMapper()
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
	 * @see com.vcc.camelone.common.controller.entity.AbstractEntityController#createEntity(String,
	 *      String)
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
	 * @see com.vcc.camelone.common.controller.entity.AbstractEntityController#getEntitiesBy(String,
	 *      Map)
	 *
	 */
	@GetMapping("{entity}/list")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		log.debug("getEntitiesBy");
		return super.getEntitiesBy(entity, params);
	}
	@RequestMapping(value = { "{entity}/{id}/{action}" }, method = { RequestMethod.PUT })
	public ResponseEntity<Object> updateEntityStatus(@RequestBody String object, @PathVariable String entity,
													 @PathVariable String id, @PathVariable String action) {
		log.info("updateEntityStatus");
		this.applicationContext.publishEvent(new PortalCacheTimeStampEvent(this, "master", entity));
		return super.updateEntityStatus(object, entity, id, action);
	}


}
