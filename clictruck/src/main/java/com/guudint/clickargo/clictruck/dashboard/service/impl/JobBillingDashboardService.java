package com.guudint.clickargo.clictruck.dashboard.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clicdo.common.DashboardJson;
import com.guudint.clicdo.common.enums.DashboardStatus;
import com.guudint.clickargo.clictruck.dashboard.service.IDashboardStatisticsService;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;

public class JobBillingDashboardService implements IDashboardStatisticsService {

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@SuppressWarnings("rawtypes")
	@Override
	public DashboardJson getStatistics(DashboardJson dbJson, Principal principal) throws Exception {
		StringBuilder hqlStat = new StringBuilder();
		hqlStat.append("SELECT SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:overStates) AND o.jobIsFinanced in ('F','E')  THEN 1 ELSE 0 END) as OVER_STATE, ");
		hqlStat.append(
				" SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:underStates) THEN 1 ELSE 0 END) as UNDER_STATE ");
		hqlStat.append(" FROM TCkJobTruck o ");
		hqlStat.append(" WHERE o.jobStatus=:jobStatus")
				.append(" AND o.TCkJob.TCkMstJobState.jbstId in (:includeStates) ");
		Map<String, Object> params = new HashMap<>();
		params.put("jobStatus", RecordStatus.ACTIVE.getCode());
		hqlStat.append(" AND o.TCkJob.TCoreAccnByJobToAccn.accnId = :accnId ");
		params.put("accnId", principal.getCoreAccn().getAccnId());
		

		// jobs for billing
		params.put("overStates", Arrays.asList(JobStates.DLV.name()));
		params.put("underStates", Arrays.asList(JobStates.BILLED.name()));
		params.put("includeStates", Arrays.asList(JobStates.DLV.name(), JobStates.BILLED.name()));
		List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hqlStat.toString(), params);
		if (list != null && list.size() > 0) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Object[] obj = (Object[]) it.next();
				dbJson.getTransStatistic().put(DashboardStatus.READY.name(),
						obj[0] == null ? 0 : Integer.valueOf(String.valueOf(obj[0])));
				dbJson.getTransStatistic().put(DashboardStatus.SUBMITTED.name(),
						obj[1] == null ? 0 : Integer.valueOf(String.valueOf(obj[1])));

			}
		}
		
		return dbJson;
	
	}

}
