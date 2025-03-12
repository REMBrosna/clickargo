package com.guudint.clickargo.clictruck.planexec.trip.dao;

import java.util.List;
import java.util.Optional;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtPaymentDao extends GenericDao<TCkCtPayment, String> {
 
    Optional<TCkCtPayment> findByJobId(String jobId) throws Exception;

    List<TCkCtPayment> findByPtxId(String ptxId) throws Exception;
}
