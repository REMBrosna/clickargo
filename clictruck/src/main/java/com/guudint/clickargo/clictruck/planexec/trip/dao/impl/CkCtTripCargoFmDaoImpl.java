package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripCargoFmDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoFm;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtTripCargoFmDaoImpl extends GenericDaoImpl<TCkCtTripCargoFm, String> implements CkCtTripCargoFmDao {

	public List<TCkCtTripCargoFm> findTripCargoFmsByTripId(String id) throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("trId", id);
		parameters.put("cgStatus", RecordStatus.ACTIVE.getCode());

		String hql = "FROM TCkCtTripCargoFm o WHERE o.TCkCtTrip.trId = :trId AND o.cgStatus = :cgStatus";
		return this.getByQuery(hql, parameters);

	}

	public List<TCkCtTripCargoFm> findTripCargoFmsByJobId(String jobId) throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("jobId", jobId);
		parameters.put("cgStatus", RecordStatus.ACTIVE.getCode());

		String hql = "FROM TCkCtTripCargoFm o WHERE o.TCkCtTrip.TCkJobTruck.jobId = :jobId AND o.cgStatus = :cgStatus";
		return this.getByQuery(hql, parameters);

	}

}
