package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDo;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTripDoDao extends GenericDao<TCkCtTripDo, String> {
 
    List<TCkCtTripDo> findByTripId(String tripId) throws Exception;
}
