package com.guudint.clickargo.clictruck.dashboard.service;

import com.guudint.clicdo.common.DashboardJson;
import com.vcc.camelone.cac.model.Principal;

public interface IDashboardStatisticsService {

	public DashboardJson getStatistics(DashboardJson dbJson, Principal principal) throws Exception;
}
