package com.guudint.clickargo.clictruck.master.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtMstVehTypeDao extends GenericDao<TCkCtMstVehType, String> {

    List<TCkCtMstVehType> findByVhtyStatus(Character status) throws Exception;
    List<TCkCtMstVehType> findByVhtyName(String vhtyName) throws Exception;
}
