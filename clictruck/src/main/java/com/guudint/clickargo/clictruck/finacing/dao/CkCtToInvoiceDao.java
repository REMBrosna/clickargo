package com.guudint.clickargo.clictruck.finacing.dao;

import java.util.List;
import java.util.Optional;

import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtToInvoiceDao extends GenericDao<TCkCtToInvoice, String> {
    
    List<TCkCtToInvoice> findByTripId(String tripId) throws Exception;

    Optional<TCkCtToInvoice> findByTripIdAndInvNo(String tripId, String invNo) throws Exception;

    List<TCkCtToInvoice> findByJobId(String jobId) throws Exception;
}
