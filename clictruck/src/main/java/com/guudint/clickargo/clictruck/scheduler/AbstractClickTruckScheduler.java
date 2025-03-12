package com.guudint.clickargo.clictruck.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.accnconfigex.service.ClictruckAccnConfigExService;
import com.guudint.clickargo.common.CKCountryConfig;
import com.vcc.camelone.scheduler.service.AbstractJob;

@Service
public abstract class AbstractClickTruckScheduler extends AbstractJob {

	@Autowired
	ClictruckAccnConfigExService accnConfigExService;

	public boolean isSingapore() throws Exception {

		try {
			CKCountryConfig countryConfig = accnConfigExService.getCtryEnv();

			return "SG".equalsIgnoreCase(countryConfig.getCountry());

		} catch (Exception e) {
			throw e;
		}
	}

	public boolean isIndonesia() throws Exception {

		try {
			CKCountryConfig countryConfig = accnConfigExService.getCtryEnv();

			return "ID".equalsIgnoreCase(countryConfig.getCountry());

		} catch (Exception e) {
			throw e;
		}
	}
}
