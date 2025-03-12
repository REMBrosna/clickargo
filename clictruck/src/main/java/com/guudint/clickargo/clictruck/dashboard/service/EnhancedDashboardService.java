package com.guudint.clickargo.clictruck.dashboard.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clicdo.common.DashboardJson;
import com.guudint.clicdo.common.enums.DashboardTypes;
import com.guudint.clicdo.common.service.DashboardService;
import com.guudint.clickargo.admin.dto.CkAccnConfigExt;
import com.guudint.clickargo.admin.dto.CkAccnConfigTypesEnum;
import com.guudint.clickargo.admin.service.CkAccnConfigExtService;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.util.email.SysParam;

/**
 * This is an enhanced version of {@code DashboardService}. Dashboards will be
 * loaded from {@code T_CK_ACCN_EXT} to avoid/minimize adding multiple if/else
 * in the FE code.
 */
@Service
public class EnhancedDashboardService {

	@Autowired
	@Qualifier("ckAccnConfigExtService")
	private CkAccnConfigExtService accnConfigExtService;

	@Autowired
	private DashboardStatisticsServiceImpl dashboardStatisticsService;

	@Autowired
	private DashboardService dbService;

	@Autowired
	private SysParam sysParam;

	private ObjectMapper mapper = new ObjectMapper();

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<DashboardJson> getDashboardStats(Principal principal) throws Exception {
		if (null == principal)
			throw new ParameterException("param principal null");

		List<DashboardJson> dashboardBeanList = new ArrayList<>();

		// Check first which country clickargo is deployed
		String ckCtry = sysParam.getValString(ICkConstant.KEY_CLICKARGO_COUNTRY_CONFIG,
				"{\"country\":\"ID\",\"currency\":\"IDR\"}");
		CKCountryConfig ckCtryConfig = mapper.readValue(ckCtry, CKCountryConfig.class);

		if (ckCtryConfig.getCountry().equalsIgnoreCase("SG")) {
			List<CkAccnConfigExt> dbAccnCnfgList = accnConfigExtService.getAccnConfigExt(
					CkAccnConfigTypesEnum.DASHBOARD, principal, ServiceTypes.CLICTRUCK, principal.getRoleList());
			// query if dbAccnCnfgList is not empty
			if (dbAccnCnfgList != null && dbAccnCnfgList.size() > 0) {
				dashboardBeanList = doNonIDDashboard(dbAccnCnfgList, principal);
			} else {
				dashboardBeanList = doNonIDFromSysConfig(principal);

			}
		}

		// if dashboardBeanList is empty fall to the previous implementation
		if (dashboardBeanList.isEmpty()) {
			dashboardBeanList = dbService.getDashboardStats(principal);
		}

		return dashboardBeanList;
	}

	private List<DashboardJson> doNonIDDashboard(List<CkAccnConfigExt> dbAccnCnfgList, Principal principal)
			throws Exception {
		List<DashboardJson> dashboardBeanList = new ArrayList<>();
		// if it's SG
		List<DashboardTypes> dashTypeList = new ArrayList<>();
		for (CkAccnConfigExt dbCfg : dbAccnCnfgList) {
			// Iterate through the list and form the dashTypeList
			// Note that the dashboard type might be the same depending on the userRoles

			// Parse the dashboard json
			List<DashboardJson> dbJsonList = mapper.readValue(dbCfg.getCaeValue(),
					mapper.getTypeFactory().constructCollectionType(ArrayList.class, DashboardJson.class));
			dbJsonList.stream().forEach(dbJson -> {
				if (!dashTypeList.contains(DashboardTypes.valueOf(dbJson.getDbType()))) {
					dashTypeList.add(DashboardTypes.valueOf(dbJson.getDbType()));
					dbJson.setTitle(DashboardTypes.valueOf(dbJson.getDbType()).getDesc());
					// this is to ensure that only unique dashboard type is added in
					// dashboardBeanList
					dashboardBeanList.add(dbJson);
				}
			});

		}

		// go through the dashboard bean list and update the statistics
		dashboardBeanList.stream().forEach(dbEl -> {
			try {
				dashboardStatisticsService.doStastistics(dbEl, principal);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		if (dashboardBeanList.size() > 0) {
			IntStream.range(0, dashboardBeanList.size()).forEach(idx -> dashboardBeanList.get(idx).setId(idx));
		}
		return dashboardBeanList;
	}

	private List<DashboardJson> doNonIDFromSysConfig(Principal principal) throws Exception {
		List<DashboardJson> dashboardBeanList = new ArrayList<>();

		String accnType = Optional.ofNullable(principal.getCoreAccn().getTMstAccnType().getAtypId()).orElse(null);
		if (StringUtils.isBlank(accnType))
			throw new ParameterException("account type from principal null");

		String sysDefDashboard = null;
		sysDefDashboard = sysParam.getValString(CtConstant.KEY_CLICTRUCK_DEFAULT_DASHBOARD, null);
		if (StringUtils.isNotBlank(sysDefDashboard)) {
			List<DashboardJson> defDashboardList = mapper.readValue(sysDefDashboard,
					mapper.getTypeFactory().constructCollectionType(ArrayList.class, DashboardJson.class));
			// holder for the dashboard type
			List<DashboardTypes> dashTypeList = new ArrayList<>();
			// iterate through the dashboard and filter the unique ids
			defDashboardList.stream().forEach(dbJson -> {
				// check if the defDashboard is for what account type
				if (dbJson.getAccnType().equalsIgnoreCase(accnType)) {
					if (!dashTypeList.contains(DashboardTypes.valueOf(dbJson.getDbType()))) {
						dashTypeList.add(DashboardTypes.valueOf(dbJson.getDbType()));
						dbJson.setTitle(DashboardTypes.valueOf(dbJson.getDbType()).getDesc());
						// this is to ensure that only unique dashboard type is added in
						// dashboardBeanList
						dashboardBeanList.add(dbJson);
					}
				}

			});
		}

		if (!dashboardBeanList.isEmpty()) {
			// go through the dashboard bean list and update the statistics
			dashboardBeanList.stream().forEach(dbEl -> {
				try {
					dashboardStatisticsService.doStastistics(dbEl, principal);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			if (dashboardBeanList.size() > 0) {
				IntStream.range(0, dashboardBeanList.size()).forEach(idx -> dashboardBeanList.get(idx).setId(idx));
			}
		}

		return dashboardBeanList;
	}

}
