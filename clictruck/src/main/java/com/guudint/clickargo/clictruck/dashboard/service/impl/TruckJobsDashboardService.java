package com.guudint.clickargo.clictruck.dashboard.service.impl;

import com.guudint.clicdo.common.DashboardJson;
import com.guudint.clicdo.common.enums.DashboardStatus;
import com.guudint.clicdo.common.enums.DashboardTypes;
import com.guudint.clickargo.clictruck.common.dao.CkCtDeptVehDao;
import com.guudint.clickargo.clictruck.common.dao.CkCtVehDao;
import com.guudint.clickargo.clictruck.common.dao.impl.CkCtDeptVehDaoImpl;
import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.clictruck.common.model.TCkCtDeptVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.dashboard.service.IDashboardStatisticsService;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.service.impl.CkJobTruckServiceUtil;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.util.PrincipalUtilService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import sun.security.ssl.HandshakeInStream;

import java.util.*;
import java.util.stream.Collectors;

public class TruckJobsDashboardService implements IDashboardStatisticsService {

	@Autowired
	@Qualifier("ckJobTruckDao")
	private GenericDao<TCkJobTruck, String> ckJobTruckDao;
	@Autowired
	private CkJobTruckServiceUtil jobTruckServiceUtil;
	@Autowired
	protected PrincipalUtilService principalUtilService;
	@Autowired
	@Qualifier("ckCtDeptVehDao")
	private CkCtDeptVehDao deptVehDao;
	@Autowired
	private CkCtDeptVehDaoImpl ckCtDeptVehDao;
	@Autowired
	private CkCtVehDao ckCtVehDao;
	@SuppressWarnings("rawtypes")
	@Override
	public DashboardJson getStatistics(DashboardJson dbJson, Principal principal) throws Exception {
		if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name()) || dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {

			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
			hqlStat.append(" WHERE o.jobStatus=:jobStatus")
					.append(" AND o.TCkJob.TCkMstJobState.jbstId not in (:excludeStates) ");
			Map<String, Object> params = new HashMap<>();
			params.put("jobStatus", RecordStatus.ACTIVE.getCode());
			params.put("excludeStates", Arrays.asList(JobStates.CAN.name(), JobStates.REJ.name(), JobStates.DEL.name(),
					JobStates.REJ.name()));

			// check if FF or TO, for filtering the account
			if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name())) {
				hqlStat.append(
						" AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId) ");
			} else if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())) {
				hqlStat.append(
						" AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobCoAccn.accnId = :accnId) ");

			}
			params.put("accnId", principal.getCoreAccn().getAccnId());
			hqlStat.append(" and o.TCkJob.TCkMstJobState.jbstId IN (:activeStates)");
			params.put("activeStates", Arrays.asList(JobStates.NEW.name(), JobStates.SUB.name(), JobStates.ACP.name(),
					JobStates.ASG.name(), JobStates.ONGOING.name()));
			if (dbJson.getDbType().equalsIgnoreCase(DashboardTypes.TRACKING.getDesc())) {
				Map<String, Object> objectMap = new HashMap<>();
				objectMap.put("jobStatus", RecordStatus.ACTIVE.getCode());
				objectMap.put("underStates", JobStates.ONGOING.name());
				objectMap.put("accnId", principal.getCoreAccn().getAccnId());
				CkCtDept userDept = jobTruckServiceUtil.getPrincipalDepartment(principalUtilService.getPrincipal());
				String trackingRecords = null;
				if (userDept != null){
					objectMap.put("deptId", userDept.getDeptId());
					 trackingRecords = "SELECT COUNT(o) FROM TCkJobTruck o WHERE o.jobStatus=:jobStatus " +
							 "AND (o.TCkCtDeptByJobCoDepartment.deptId = :deptId OR o.TCkCtDeptByJobCoDepartment.deptId is null) " +
							 "AND o.TCkJob.TCkMstJobState.jbstId = :underStates AND o.TCkCtVeh.vhId IS NOT NULL " +
							 "AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId)" ;
				}else {
					 trackingRecords = "SELECT COUNT(o) FROM TCkJobTruck o WHERE o.jobStatus=:jobStatus " +
							 "AND o.TCkCtDeptByJobCoDepartment.deptId is null AND o.TCkJob.TCkMstJobState.jbstId = :underStates " +
							 "AND o.TCkCtVeh.vhId IS NOT NULL AND (o.TCkJob.TCoreAccnByJobOwnerAccn.accnId = :accnId OR o.TCkJob.TCoreAccnByJobFfAccn.accnId = :accnId)" ;
				}
				int count = ckJobTruckDao.count(trackingRecords, objectMap);
				dbJson.getTransStatistic().put(DashboardStatus.ONGOING.name(), count);
			} else {
				dbJson.getTransStatistic().put(DashboardStatus.ACTIVE.name(),
						ckJobTruckDao.count(hqlStat.toString(), params));
			}
		} else if (dbJson.getAccnType().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())) {

			// This is for TO.
			// Updated script for stats for job_billing that it should only count for
			// financed/extended finance
			StringBuilder hqlStat = new StringBuilder();

			hqlStat.append(
					"SELECT SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:overStates) THEN 1 ELSE 0 END) as OVER_STATE, ");

			hqlStat.append(
					" SUM(CASE WHEN o.TCkJob.TCkMstJobState.jbstId IN (:underStates) THEN 1 ELSE 0 END) as UNDER_STATE ");
			hqlStat.append(" FROM TCkJobTruck o ");
			hqlStat.append(" WHERE o.jobStatus=:jobStatus")
					.append(" AND o.TCkJob.TCkMstJobState.jbstId in (:includeStates) ");
			Map<String, Object> params = new HashMap<>();
			params.put("jobStatus", RecordStatus.ACTIVE.getCode());

			// check if FF or TO, for filtering the account
			hqlStat.append(" AND o.TCkJob.TCoreAccnByJobToAccn.accnId = :accnId ");

			params.put("accnId", principal.getCoreAccn().getAccnId());
			params.put("overStates", Arrays.asList(JobStates.SUB.name(), JobStates.ACP.name(), JobStates.ASG.name()));
			params.put("underStates", Arrays.asList(JobStates.ONGOING.name()));
			params.put("includeStates", Arrays.asList(JobStates.SUB.name(), JobStates.ACP.name(), JobStates.ASG.name(),
					JobStates.ONGOING.name(), JobStates.DLV.name()));

			List<TCkJobTruck> list = ckJobTruckDao.getByQuery(hqlStat.toString(), params);
			if (list != null && list.size() > 0) {
				Iterator it = list.iterator();
				while (it.hasNext()) {
					Object[] obj = (Object[]) it.next();
					// Check if dbJson.getDbType() is not equal to TRACKING before putting the NEW status
					if (!dbJson.getDbType().equalsIgnoreCase(DashboardTypes.TRACKING.getDesc())) {
						dbJson.getTransStatistic().put(DashboardStatus.NEW.name(), NumberUtil.toInteger(obj[0]));
					}
					dbJson.getTransStatistic().put(DashboardStatus.ONGOING.name(), NumberUtil.toInteger(obj[1]));
					// Check if dbJson.getDbType() is equal to TRACKING
					if (dbJson.getDbType().equalsIgnoreCase(DashboardTypes.TRACKING.getDesc())) {
						Map<String, Object> objectMap = new HashMap<>();
						objectMap.put("validStatus", RecordStatus.ACTIVE.getCode());
						objectMap.put("underStates", JobStates.ONGOING.name());
						objectMap.put("vhCompany", principal.getCoreAccn().getAccnId());
						String trackingQuery = "SELECT o FROM TCkCtVeh o, TCkJob tj, TCkJobTruck tjt " +
								"WHERE tj.jobId = tjt.TCkJob.jobId " +
								"AND tjt.TCkCtVeh.vhId = o.vhId AND tj.TCkMstJobState.jbstId = :underStates " +
								"AND o.TCoreAccn.accnId = :vhCompany AND  tjt.TCkCtVeh.vhId IS NOT NULL AND o.vhStatus IN :validStatus";
						List<TCkCtVeh> tCkCtVehs = ckCtVehDao.getByQuery(trackingQuery, objectMap);
						List<String> vehIdList = new ArrayList<>();
						List<TCkCtVeh> modResult = tCkCtVehs;
						CkCtDept userDept = jobTruckServiceUtil.getPrincipalDepartment(principalUtilService.getPrincipal());
							if (principal != null) {
								if (userDept != null) {
									List<TCkCtDeptVeh> deptVehList = deptVehDao.getVehiclesByAccnDept(userDept.getTCoreAccn().getAccnId());
									if (deptVehList != null && !deptVehList.isEmpty()) {
										for (TCkCtDeptVeh vd : deptVehList) {
											Hibernate.initialize(vd.getTCkCtVeh());
											if (!userDept.getDeptId().equalsIgnoreCase(vd.getTCkCtDept().getDeptId())) {
												vehIdList.add(vd.getTCkCtVeh().getVhId());
											}
										}
									}
									modResult = tCkCtVehs.stream()
											.filter(el -> !vehIdList.contains(el.getVhId()))
											.collect(Collectors.toList());
								} else {
									List<TCkCtDeptVeh> deptVehList = ckCtDeptVehDao.getAll();
									modResult = tCkCtVehs.stream()
											.filter(el -> el.getVhId() == null || deptVehList.stream().noneMatch(deptVeh -> deptVeh.getTCkCtVeh().getVhId().equals(el.getVhId())))
											.collect(Collectors.toList());
								}
								dbJson.getTransStatistic().put(DashboardStatus.ONGOING.name(), modResult.size());
							}
						}
					}
				}
			}
		return dbJson;
	}
}
