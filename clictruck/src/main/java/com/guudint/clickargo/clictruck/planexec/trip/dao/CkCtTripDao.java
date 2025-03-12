package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTripDao extends GenericDao<TCkCtTrip, String> {

    List<TCkCtTrip> findByJobId(String jobId) throws Exception;
}
