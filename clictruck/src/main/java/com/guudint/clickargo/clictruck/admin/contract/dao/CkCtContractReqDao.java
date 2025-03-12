package com.guudint.clickargo.clictruck.admin.contract.dao;

import java.util.Optional;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractReq;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtContractReqDao extends GenericDao<TCkCtContractReq, String> {
	
	Optional<TCkCtContractReq> findByName(String name) throws Exception;
}
