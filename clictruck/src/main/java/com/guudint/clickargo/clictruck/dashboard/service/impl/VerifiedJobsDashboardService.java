package com.guudint.clickargo.clictruck.dashboard.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clicdo.common.DashboardJson;
import com.guudint.clicdo.common.enums.DashboardStatus;
import com.guudint.clickargo.clictruck.dashboard.service.IDashboardStatisticsService;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;

public class VerifiedJobsDashboardService implements IDashboardStatisticsService {

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Override
	public DashboardJson getStatistics(DashboardJson dbJson, Principal principal) throws Exception {
		StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
		hqlStat.append(" WHERE o.jobStatus=:jobStatus")
				.append(" AND o.TCkJob.TCkMstJobState.jbstId not in (:excludeStates) ");
		Map<String, Object> params = new HashMap<>();
		params.put("jobStatus", RecordStatus.ACTIVE.getCode());
		params.put("excludeStates",
				Arrays.asList(JobStates.CAN.name(), JobStates.REJ.name(), JobStates.DEL.name(), JobStates.REJ.name()));

		// check if FF or TO, for filtering the account
		if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {
			hqlStat.append(
					" AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId) ");
		} else if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
			hqlStat.append(
					" AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobCoAccn.accnId = :accnId) ");

		}

		params.put("accnId", principal.getCoreAccn().getAccnId());
		hqlStat.append(" AND o.TCoreAccnByJobPartyCoFf.accnId = :accnId");
		hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");

		params.put("activeStates", Arrays.asList(JobStates.VER.name(), JobStates.VER_BILL.name()));
		dbJson.getTransStatistic().put(DashboardStatus.VERIFIED.name(),
				ckJobTruckDao.count(hqlStat.toString(), params));
		return dbJson;
	}

}
