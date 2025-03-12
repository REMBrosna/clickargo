package com.guudint.clickargo.clictruck.planexec.trip.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripCargoMmDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoMm;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkCtTripCargoMmDaoImpl extends GenericDaoImpl<TCkCtTripCargoMm, String> implements CkCtTripCargoMmDao {

	@Transactional
	public List<TCkCtTripCargoMm> findTripCargoFmmsByTripId(String id) throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("trId", id);
		parameters.put("cgStatus", RecordStatus.ACTIVE.getCode());

		String hql = "FROM TCkCtTripCargoMm o WHERE o.TCkCtTrip.trId = :trId AND o.cgStatus = :cgStatus";
		return this.getByQuery(hql, parameters);
	}

	@Transactional
	public List<TCkCtTripCargoMm> findTripCargoFmmsByJobId(String jobId) throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("jobId", jobId);
		parameters.put("cgStatus", RecordStatus.ACTIVE.getCode());

		String hql = "FROM TCkCtTripCargoMm o WHERE o.TCkCtTrip.TCkJobTruck.jobId = :jobId AND o.cgStatus = :cgStatus";
		return this.getByQuery(hql, parameters);

	}
}
