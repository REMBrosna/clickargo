package com.guudint.clickargo.clictruck.thirdparty.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.thirdparty.service.ThridPartyService;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/thirdparty")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ThridPartyController extends AbstractPortalController {

	private static Logger log = Logger.getLogger(ThridPartyController.class);

	@Autowired
	ThridPartyService hridPartyService;

	/**
	 * Run scheduler job to load XML files;
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list/{accnId}")
	public ResponseEntity<Object> fetchThirdPartyByAccnId(@PathVariable String accnId) {

		try {

			if("-".equalsIgnoreCase(accnId)) {
				accnId = super.getPrincipal().getUserAccnId();
			}

			return ResponseEntity.ok(hridPartyService.getThirdpartList(accnId));

		} catch (Exception e) {
			log.error("fetchThirdPartyByAccnId", e);
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	 
}
