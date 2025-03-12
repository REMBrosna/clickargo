package com.guudint.clickargo.clictruck.master.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstCargoType;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtMstCargoTypeDao extends GenericDao<TCkCtMstCargoType, String>{
    
    List<TCkCtMstCargoType> findByCrtypStatus(Character status) throws Exception;
}
