package com.guudint.clickargo.clictruck.master.dao;


import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomWeight;
import com.vcc.camelone.common.dao.GenericDao;


public interface CkCtMstUomWeightDao extends GenericDao<TCkCtMstUomWeight, String> {
    TCkCtMstUomWeight getWeightUomByDesc(String desc) throws Exception;

}
