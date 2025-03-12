package com.guudint.clickargo.clictruck.admin.ratetable.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTripRateDao extends GenericDao<TCkCtTripRate, String> {

	List<TCkCtTripRate> findByCoFfAndLocFromTo(String coFf, String locFrom, String locTo) throws Exception;
	
    List<TCkCtTripRate> findByRateTableIdLocFromToAndTruckType(String rateTableId, String locFrom, String locTo, String vehType) throws Exception;

	TCkCtTripRate findByTripRateTableAndFromToVehType(String toAccn, String coFfAccn, String currency, String locFrom,
			String locTo, String vehType) throws Exception;
	

	List<TCkCtTripRate> findByTripRateTableAndFromToVehType(String toAccn, String coFfAccn, String currency, String locFrom,
			String locTo, String vehType, List<Character> inStatus, String trType) throws Exception;
	
	TCkCtTripRate findByTripRateTableAndFromToVehType(String toAccn, String coFfAccn, String currency, String locFrom,
			String locTo, String vehType, List<Character> inStatus, Integer seqNo, String tripRateParentId) throws Exception;
}
