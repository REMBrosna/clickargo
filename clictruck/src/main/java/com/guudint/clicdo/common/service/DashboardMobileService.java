package com.guudint.clicdo.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clicdo.common.DashboardMobileJson;
import com.guudint.clicdo.common.enums.DashboardTypes;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.master.enums.JobStates;
import com.guudint.clickargo.master.enums.Roles;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;

@Service
public class DashboardMobileService {
	private static Logger log = Logger.getLogger(DashboardMobileService.class);

	@Autowired
	private CkJobTruckDao ckJobTruckDao;
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<DashboardMobileJson> getDashboardStats(Principal principal) throws ParameterException, Exception {
		if (null == principal)
			throw new ParameterException("param principal null");

		List<DashboardMobileJson> dashboardBeanList = new ArrayList<>();
		List<DashboardTypes> dashTypeList = new ArrayList<>();
		List<String> roles = Optional.ofNullable(principal.getRoleList()).orElse(null);

		if (!roles.isEmpty()) {
			for (String role : roles) {
				if (role.equals(Roles.DRIVER.name())) {
					dashTypeList.add(DashboardTypes.MOBILE_NEW);
					dashTypeList.add(DashboardTypes.MOBILE_PAUSED);
				}
			}
		}

		if (!dashTypeList.isEmpty() && roles.contains(Roles.DRIVER.name())) {
			for (DashboardTypes d : dashTypeList) {
				DashboardMobileJson db = new DashboardMobileJson(d.name(), d.getDesc());
				db.setAccnType(Roles.DRIVER.name());
				dashboardBeanList.add(db);
			}
		}

		dashboardBeanList.stream().forEach(dbEl -> {
			try {
				if (dbEl.getAccnType().equalsIgnoreCase(Roles.DRIVER.name())) {

					mobileTrip(dbEl, principal.getUserId());
				}

			} catch (Exception ex) {
				log.error("error in statistics", ex);
				ex.printStackTrace();
			}
		});

		if (dashboardBeanList.size() > 0) {
			IntStream.range(0, dashboardBeanList.size()).forEach(idx -> dashboardBeanList.get(idx).setId(idx));
		}
		
		log.info("getMobileDashboardStats " + principal.getUserId() + "\n" + (new ObjectMapper()).writeValueAsString(dashboardBeanList));

		return dashboardBeanList;

	}

	private void mobileTrip(DashboardMobileJson dbEl, String drvMobileId) throws Exception {

		if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.MOBILE_NEW.name())) {
			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
			hqlStat.append(" WHERE o.TCkJob.TCkMstJobState.jbstId = :jobState")
					.append(" AND o.TCkCtDrv.drvMobileId = :drvMobileId ");

			Map<String, Object> params = new HashMap<>();
			params.put("jobState", JobStates.ASG.name());
			params.put("drvMobileId", drvMobileId);

			Integer count = ckJobTruckDao.count(hqlStat.toString(), params);
			dbEl.setCount(count);

		} else if (dbEl.getDbType().equalsIgnoreCase(DashboardTypes.MOBILE_PAUSED.name())) {
			StringBuilder hqlStat = new StringBuilder("SELECT COUNT(o) FROM TCkJobTruck o ");
			hqlStat.append(" WHERE o.TCkJob.TCkMstJobState.jbstId = :jobState")
					.append(" AND o.TCkCtDrv.drvMobileId = :drvMobileId ");

			Map<String, Object> params = new HashMap<>();
			params.put("jobState", JobStates.PAUSED.name());
			params.put("drvMobileId", drvMobileId);

			Integer count = ckJobTruckDao.count(hqlStat.toString(), params);
			dbEl.setCount(count);
		}

	}

}
