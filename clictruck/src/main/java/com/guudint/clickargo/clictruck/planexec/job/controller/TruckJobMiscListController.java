package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Non payment listing controller. (e.g. reject remarks, etc) 
 */
@CrossOrigin
@RequestMapping(value = "/api/v1/clickargo/clictruck/list/misc/job/")
public class TruckJobMiscListController extends AbstractTruckJobListController {

	@Override
	@GetMapping("{entity}/list")
	public ResponseEntity<Object> listEntitiesBy(@PathVariable String entity,
			@RequestParam Map<String, String> params) {
		return super.listEntitiesBy(entity, params);
	}
}
