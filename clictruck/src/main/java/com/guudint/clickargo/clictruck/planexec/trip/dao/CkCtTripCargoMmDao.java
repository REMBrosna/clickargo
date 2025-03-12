package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoMm;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTripCargoMmDao extends GenericDao<TCkCtTripCargoMm, String> {

	public List<TCkCtTripCargoMm> findTripCargoFmmsByTripId(String id) throws Exception;

	public List<TCkCtTripCargoMm> findTripCargoFmmsByJobId(String jobId) throws Exception;
}
