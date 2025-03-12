package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTripDoAttachDao extends GenericDao<TCkCtTripDoAttach, String> {

    List<TCkCtTripDoAttach> findByTripId(String tripId) throws Exception;
    
    List<TCkCtTripDoAttach> findByJobId(String jobTruckId) throws Exception;
}
