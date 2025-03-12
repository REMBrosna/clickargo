package com.guudint.clickargo.clictruck.dashboard.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class AbstractDashboardStatisticsService{

	@Autowired
	protected ApplicationContext applicationContext;
	protected HashMap<String, String> statTypeService;
	
	public HashMap<String, String> getStatTypeService() {
		return statTypeService;
	}
	public void setStatTypeService(HashMap<String, String> statTypeService) {
		this.statTypeService = statTypeService;
	}


}
