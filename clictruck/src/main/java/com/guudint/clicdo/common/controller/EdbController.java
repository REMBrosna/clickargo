package com.guudint.clicdo.common.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.guudint.clickargo.controller.AbstractCkController;

/**
 * Use this controller for new dashboard related listing and/or details (if required). 
 */
@RequestMapping(value = "/api/v1/clickargo/clictruck/edb/")
@CrossOrigin
public class EdbController extends AbstractCkController {

	private static Logger LOG = Logger.getLogger(EdbController.class);
	
	@GetMapping("/{entity}/list")
	public ResponseEntity<Object> getEntitiesBy(@PathVariable String entity, @RequestParam Map<String, String> params) {
		LOG.debug("getEntitiesBy Controller");
		return super.getEntitiesBy(entity, params);
	}
}
