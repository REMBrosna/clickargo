package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.math.BigDecimal;
import java.util.List;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripReimbursement;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTripReimbursementDao extends GenericDao<TCkCtTripReimbursement, String> {

    List<TCkCtTripReimbursement> findByTripIdAndStatus(String tripId, Character status) throws Exception;

    BigDecimal sumTotalByTripIdAndStatus(String tripId, Character status) throws Exception;
}
