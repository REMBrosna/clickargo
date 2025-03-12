package com.guudint.clickargo.clictruck.apigateway.dao;

import com.guudint.clickargo.clictruck.apigateway.model.TCkCtVendorCompany;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtVendorCompanyDao extends GenericDao<TCkCtVendorCompany, String> {
    TCkCtVendorCompany hasVendorAuthorization(String accnId, String vendorId) throws Exception;
}
