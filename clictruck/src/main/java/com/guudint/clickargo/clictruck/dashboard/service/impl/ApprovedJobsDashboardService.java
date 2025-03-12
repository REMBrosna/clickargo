package com.guudint.clickargo.clictruck.dashboard.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clicdo.common.DashboardJson;
import com.guudint.clicdo.common.enums.DashboardStatus;
import com.guudint.clickargo.clictruck.dashboard.service.IDashboardStatisticsService;
import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;

public class ApprovedJobsDashboardService implements IDashboardStatisticsService {

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;

	@Override
	public DashboardJson getStatistics(DashboardJson dbJson, Principal principal) throws Exception {
		if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())
				|| dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
			hqlStat.append(" WHERE o.jobStatus=:jobStatus")
					.append(" AND o.TCkJob.TCkMstJobState.jbstId not in (:excludeStates) ");
			hqlStat.append(" AND o.TCoreAccnByJobPartyCoFf.accnId = :accnId");

			Map<String, Object> params = new HashMap<>();
			params.put("jobStatus", RecordStatus.ACTIVE.getCode());
			params.put("excludeStates", Arrays.asList(JobStates.CAN.name(), JobStates.REJ.name(), JobStates.DEL.name(),
					JobStates.REJ.name()));
			params.put("accnId", principal.getCoreAccn().getAccnId());

			hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");
			params.put("activeStates", Arrays.asList(JobStates.APP.name(), JobStates.APP_BILL.name()));

			dbJson.getTransStatistic().put(DashboardStatus.APPROVED.name(),
					ckJobTruckDao.count(hqlStat.toString(), params));
		} else if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_SP.name())) {
			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
			hqlStat.append(" WHERE o.jobStatus=:jobStatus")
					.append(" AND o.TCkJob.TCkMstJobState.jbstId not in (:excludeStates) ");

			Map<String, Object> params = new HashMap<>();
			params.put("jobStatus", RecordStatus.ACTIVE.getCode());
			params.put("excludeStates", Arrays.asList(JobStates.CAN.name(), JobStates.REJ.name(), JobStates.DEL.name(),
					JobStates.REJ.name()));

			// not filtered by account for GLI since it is not captured from tckjobtruck

			hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");
			hqlStat.append(" and o.jobOutPaymentState in (:outPayStates)");

			params.put("activeStates", Arrays.asList(JobStates.APP.name(), JobStates.APP_BILL.name()));
			params.put("outPayStates", Arrays.asList(JobPaymentStates.NEW.name()));

			dbJson.getTransStatistic().put(DashboardStatus.APPROVED.name(),
					ckJobTruckDao.count(hqlStat.toString(), params));
		}

		return dbJson;
	}

}
