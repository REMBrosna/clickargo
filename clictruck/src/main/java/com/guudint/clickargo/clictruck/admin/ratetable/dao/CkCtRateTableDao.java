package com.guudint.clickargo.clictruck.admin.ratetable.dao;

import java.util.Optional;

import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtRateTableDao extends GenericDao<TCkCtRateTable, String> {

    Optional<TCkCtRateTable> findByNameAndCompany(String name, String companyId) throws Exception;
    Optional<TCkCtRateTable> findById(String id) throws Exception;
}
