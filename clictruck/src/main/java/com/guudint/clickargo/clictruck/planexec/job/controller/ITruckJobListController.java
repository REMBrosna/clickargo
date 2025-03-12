package com.guudint.clickargo.clictruck.planexec.job.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface ITruckJobListController {

	/**
	 * Get the filter entities for the table listing
	 * 
	 * @param entity
	 * @param params
	 * @return
	 */
	public ResponseEntity<Object> listEntitiesBy(@PathVariable String entity,
			@RequestParam Map<String, String> params);
}
