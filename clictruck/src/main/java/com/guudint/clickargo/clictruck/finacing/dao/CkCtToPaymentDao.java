package com.guudint.clickargo.clictruck.finacing.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.finacing.model.TCkCtToPayment;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtToPaymentDao extends GenericDao<TCkCtToPayment, String> {

    List<TCkCtToPayment> findByTopReference(String topReference) throws Exception;

    List<TCkCtToPayment> findByAccnTo(String accnId) throws Exception;
}
