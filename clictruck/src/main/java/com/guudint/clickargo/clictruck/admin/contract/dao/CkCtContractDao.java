package com.guudint.clickargo.clictruck.admin.contract.dao;

import java.util.List;
import java.util.Optional;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtContractDao extends GenericDao<TCkCtContract, String> {
	
    Optional<TCkCtContract> findByName(String name) throws Exception;
    
    List<TCkCtContract> findValidContract (String ConCoFf, String ConTo) throws Exception;
    
    List<TCkCtContract> findNotValidContract () throws Exception;
    
    List<TCkCtContract> findValidContractByCoFf (String ConCoFf) throws Exception;
    
    List<TCkCtContract> findValidContractByTo (String toAccnId) throws Exception;
    Optional<TCkCtContract> findByConId(String id) throws Exception;
}
