package com.guudint.clickargo.clictruck.admin.ratetable.service;

import java.util.Optional;

import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.guudint.clickargo.clictruck.planexec.job.dto.TripChargeReq;

public interface ICkCtTripRateService {

	Optional<CkCtTripRate> getByCoFfAndLocFromTo(TripChargeReq tripChargeReq) throws Exception;

	/**
	 * Retrieves the trip rate by TO and CO/FF rate table, From/To location, Vehicle
	 * Type and Currency}
	 */
	Optional<CkCtTripRate> getByTripRateTableAndFromToVehTypeCurr(TripChargeReq tripChargeReq) throws Exception;

}
