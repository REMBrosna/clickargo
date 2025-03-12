package com.guudint.clickargo.clictruck.common.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.common.ICkConstant;
import com.vcc.camelone.util.PrincipalUtilService;
import com.vcc.camelone.util.email.SysParam;

public abstract class AbstractTemplateService implements ITemplateService {

	@Autowired
	protected PrincipalUtilService principalUtilService;

	protected ObjectMapper mapper = new ObjectMapper();

	@Autowired
	protected SysParam sysParam;

	protected CKCountryConfig getCountryConfig() throws Exception {
		String ckCtry = sysParam.getValString(ICkConstant.KEY_CLICKARGO_COUNTRY_CONFIG,
				"{\"country\":\"ID\",\"currency\":\"IDR\"}");
		CKCountryConfig ckCtryConfig = mapper.readValue(ckCtry, CKCountryConfig.class);
		return ckCtryConfig;
	}

	public PrincipalUtilService getPrincipalUtilService() {
		return principalUtilService;
	}

	public void setPrincipalUtilService(PrincipalUtilService principalUtilService) {
		this.principalUtilService = principalUtilService;
	}

}
