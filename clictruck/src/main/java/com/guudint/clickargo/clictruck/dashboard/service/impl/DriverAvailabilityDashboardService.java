package com.guudint.clickargo.clictruck.dashboard.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clicdo.common.DashboardJson;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.clictruck.common.dto.DriverStates;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.dashboard.service.IDashboardStatisticsService;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;

public class DriverAvailabilityDashboardService implements IDashboardStatisticsService {

	@Autowired
	@Qualifier("ckCtDrvDao")
	private GenericDao<TCkCtDrv, String> ckCtDrvDao;

	@Autowired
	private CkCtContractDao ckCtContractDao;

	@Override
	public DashboardJson getStatistics(DashboardJson dbJson, Principal principal) throws Exception {
		if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())
				|| dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {

			List<TCkCtContract> listTCkCtContract = ckCtContractDao
					.findValidContractByCoFf(principal.getCoreAccn().getAccnId());

			if (listTCkCtContract != null && listTCkCtContract.size() > 0) {
				List<String> listTCoreAccnByConTo = listTCkCtContract.stream()
						.map(tCkCtContract -> tCkCtContract.getTCoreAccnByConTo().getAccnId())
						.collect(Collectors.toList());

				StringBuilder hqlStat = new StringBuilder();
				hqlStat.append("SELECT COUNT(CASE WHEN o.drvState IN ('ASSIGNED') THEN o.drvId END) AS ASSIGNED,"
						+ "COUNT(CASE WHEN o.drvState IN ('UNASSIGNED') THEN o.drvId END) AS UNASSIGNED "
						+ "FROM TCkCtDrv o " 
						+ "WHERE o.TCoreAccn.accnId IN :accnId " 
						+ "AND o.drvStatus IN :drvStatus "
						+ "AND o.drvState IN :drvState ");
				Map<String, Object> params = new HashMap<>();
				params.put("accnId", listTCoreAccnByConTo);
				params.put("drvStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.SUSPENDED.getCode()));
				params.put("drvState", Arrays.asList(DriverStates.ASSIGNED.name(), DriverStates.UNASSIGNED.name()));

				List<TCkCtDrv> list = ckCtDrvDao.getByQuery(hqlStat.toString(), params);
				if (list != null && list.size() > 0) {
					Iterator it = list.iterator();
					while (it.hasNext()) {
						Object[] obj = (Object[]) it.next();
						dbJson.getTransStatistic().put(DriverStates.ASSIGNED.name(), NumberUtil.toInteger(obj[0]));
						dbJson.getTransStatistic().put(DriverStates.UNASSIGNED.name(), NumberUtil.toInteger(obj[1]));
					}
				}
			}

		} else if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {
			StringBuilder hqlStat = new StringBuilder();
			hqlStat.append("SELECT COUNT(CASE WHEN o.drvState IN ('ASSIGNED') THEN o.drvId END) AS ASSIGNED,"
					+ "COUNT(CASE WHEN o.drvState IN ('UNASSIGNED') THEN o.drvId END) AS UNASSIGNED "
					+ "FROM TCkCtDrv o " 
					+ "WHERE o.TCoreAccn.accnId IN :accnId " 
					+ "AND o.drvStatus IN :drvStatus "
					+ "AND o.drvState IN :drvState ");
			Map<String, Object> params = new HashMap<>();
			params.put("accnId", principal.getCoreAccn().getAccnId());
			params.put("drvStatus", Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.SUSPENDED.getCode()));
			params.put("drvState", Arrays.asList(DriverStates.ASSIGNED.name(), DriverStates.UNASSIGNED.name()));

			List<TCkCtDrv> list = ckCtDrvDao.getByQuery(hqlStat.toString(), params);
			if (list != null && list.size() > 0) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					dbJson.getTransStatistic().put(DriverStates.ASSIGNED.name(), NumberUtil.toInteger(obj[0]));
					dbJson.getTransStatistic().put(DriverStates.UNASSIGNED.name(), NumberUtil.toInteger(obj[1]));
				}
			}
		}
		return dbJson;
	}
}
