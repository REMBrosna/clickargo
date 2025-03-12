package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoFm;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTripCargoFmDao extends GenericDao<TCkCtTripCargoFm, String> {

	public List<TCkCtTripCargoFm> findTripCargoFmsByTripId(String id) throws Exception;
	
	public List<TCkCtTripCargoFm> findTripCargoFmsByJobId(String jobId) throws Exception;

}
