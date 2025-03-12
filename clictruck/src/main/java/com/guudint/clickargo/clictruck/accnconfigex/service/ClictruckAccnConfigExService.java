package com.guudint.clickargo.clictruck.accnconfigex.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.admin.dto.CkAccnConfigExt;
import com.guudint.clickargo.admin.dto.CkAccnConfigTypesEnum;
import com.guudint.clickargo.admin.service.CkAccnConfigExtService;
import com.guudint.clickargo.clictruck.constant.CtConstant;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.master.dto.MstCountry;
import com.vcc.camelone.master.model.TMstCountry;

@Service("clictruckAccnConfigExService")
public class ClictruckAccnConfigExService extends CkAccnConfigExtService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(ClictruckAccnConfigExService.class);

	@Autowired
	@Qualifier("mstCountryService")
	private IEntityService<TMstCountry, String, MstCountry> mstCountryService;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public CKCountryConfig getCtryEnv() throws ParameterException, Exception {
		log.debug("getCtryEnv");
		try {

			String ckCtry = sysParam.getValString(ICkConstant.KEY_CLICKARGO_COUNTRY_CONFIG,
					"{\"country\":\"ID\",\"currency\":\"IDR\"}");
			CKCountryConfig ckCtryConfig = mapper.readValue(ckCtry, CKCountryConfig.class);
			return ckCtryConfig;

		} catch (Exception ex) {
			throw ex;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<String> getTabs(Principal principal) throws ParameterException, Exception {
		log.debug("getAccnConfigExtByType");
		List<String> tabsIdList = new ArrayList<>();
		try {
			if (principal == null)
				throw new ParameterException("param principal null");

			String ckCtry = sysParam.getValString(ICkConstant.KEY_CLICKARGO_COUNTRY_CONFIG,
					"{\"country\":\"ID\",\"currency\":\"IDR\"}");
			CKCountryConfig ckCtryConfig = mapper.readValue(ckCtry, CKCountryConfig.class);

			// if country is not Indonesia (default)
			if (!ckCtryConfig.getCountry().equalsIgnoreCase("ID")) {
				// get from dbAccnCnfgList

				List<CkAccnConfigExt> dbAccnCnfgList = getAccnConfigExt(CkAccnConfigTypesEnum.TABS, principal,
						ServiceTypes.CLICTRUCK, principal.getRoleList());
				if (dbAccnCnfgList != null) {
					dbAccnCnfgList.forEach(el -> {
						List<String> ids = Arrays.asList(el.getCaeValue().split(","));
						ids.forEach(el2 -> {
							if (!tabsIdList.contains(el2))
								tabsIdList.add(el2);
						});

					});
				} else {
					// if dbAccnCnfgList is empty, get from sysparam
					String defTabs = sysParam.getValString(CtConstant.KEY_CLICTRUCK_DEFAULT_TABS, null);
					if (defTabs != null) {
						List<String> ids = Arrays.asList(defTabs.split(","));
						ids.forEach(el2 -> {
							if (!tabsIdList.contains(el2))
								tabsIdList.add(el2);
						});
					}

				}
			}

			return tabsIdList;
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * Returns the job state filter as set in sysparam. This is not configured for
	 * specific accounts.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<String> getJobStateFilter() throws Exception {
		String ckCtry = sysParam.getValString(ICkConstant.KEY_CLICKARGO_COUNTRY_CONFIG,
				"{\"country\":\"ID\",\"currency\":\"IDR\"}");
		CKCountryConfig ckCtryConfig = mapper.readValue(ckCtry, CKCountryConfig.class);

		if (!ckCtryConfig.getCountry().equalsIgnoreCase("ID")) {
			String stateFilters = sysParam.getValString(CtConstant.KEY_CLICTRUCK_DEFAULT_STATE_FILTER, null);
			if (stateFilters != null) {
				List<String> stateFiltersList = Arrays.asList(stateFilters.split(","));
				return stateFiltersList;
			}
		}

		return new ArrayList<>();
	}

	/**
	 * Returns the lists of fields/fieldset that needs to be hidden from the UI.
	 * This is configured by account but if non set, this will be based on the
	 * country the clickargo is deployed as generic configuration for the entire
	 * application.
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<String> getFieldsToHide(Principal principal) throws ParameterException, Exception {
		
		List<String> roleList = null;
		
		if (principal == null) {
			// throw new ParameterException("principal is null");
			// principal is null in Payment transaction.
			//return null;
			roleList = new ArrayList<>();
		} else {
			roleList = principal.getRoleList();
		}

		List<String> fieldsToHide = new ArrayList<>();

		String ckCtry = sysParam.getValString(ICkConstant.KEY_CLICKARGO_COUNTRY_CONFIG,
				"{\"country\":\"ID\",\"currency\":\"IDR\"}");
		CKCountryConfig ckCtryConfig = mapper.readValue(ckCtry, CKCountryConfig.class);

		// default is ID. Only check from accn config ex if it's non ID
		if (!ckCtryConfig.getCountry().equalsIgnoreCase("ID")) {
			// Get from the accn config ex
			List<CkAccnConfigExt> dbAccnCnfgList = getAccnConfigExt(CkAccnConfigTypesEnum.HIDDEN_FIELDS, principal,
					ServiceTypes.CLICTRUCK, roleList);
			if (dbAccnCnfgList != null && dbAccnCnfgList.size() > 0) {
				dbAccnCnfgList.forEach(el -> {
					List<String> ids = Arrays.asList(el.getCaeValue().split(","));
					ids.forEach(el2 -> {
						if (!fieldsToHide.contains(el2))
							fieldsToHide.add(el2);
					});

				});
			} else {
				// if dbAccnCnfgList is empty, get from sysparam
				String defHiddenFields = sysParam.getValString(CtConstant.KEY_CLICTRUCK_DEFAULT_HIDE_FIELDS, null);
				if (defHiddenFields != null) {
					List<String> ids = Arrays.asList(defHiddenFields.split(","));
					ids.forEach(el2 -> {
						if (!fieldsToHide.contains(el2))
							fieldsToHide.add(el2);
					});
				}
			}
		}

		return fieldsToHide;
	}

}
