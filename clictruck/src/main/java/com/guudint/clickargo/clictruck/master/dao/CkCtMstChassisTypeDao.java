package com.guudint.clickargo.clictruck.master.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtMstChassisTypeDao extends GenericDao<TCkCtMstChassisType, String> {

    List<TCkCtMstChassisType> findByChtyStatus(Character status) throws Exception;
}
