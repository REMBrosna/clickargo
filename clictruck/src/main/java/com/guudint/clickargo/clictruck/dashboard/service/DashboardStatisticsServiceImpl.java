package com.guudint.clickargo.clictruck.dashboard.service;

import com.guudint.clicdo.common.DashboardJson;
import com.vcc.camelone.cac.model.Principal;

public class DashboardStatisticsServiceImpl extends AbstractDashboardStatisticsService {

	public void doStastistics(DashboardJson dbJson, Principal principal) throws Exception {

		Object bean = applicationContext.getBean(statTypeService.get(dbJson.getDbType()));
		IDashboardStatisticsService service = (IDashboardStatisticsService) bean;
		dbJson = service.getStatistics(dbJson, principal);
	}
}
