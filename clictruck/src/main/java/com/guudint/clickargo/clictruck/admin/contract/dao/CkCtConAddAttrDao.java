package com.guudint.clickargo.clictruck.admin.contract.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtConAddAttr;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtConAddAttrDao extends GenericDao<TCkCtConAddAttr, String> {

	public List<TCkCtConAddAttr> getAdditionalAttributesByContract(String contractId) throws Exception;
}
