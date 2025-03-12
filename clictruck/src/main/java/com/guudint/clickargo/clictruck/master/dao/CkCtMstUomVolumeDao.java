package com.guudint.clickargo.clictruck.master.dao;


import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomVolume;
import com.vcc.camelone.common.dao.GenericDao;


public interface CkCtMstUomVolumeDao extends GenericDao<TCkCtMstUomVolume, String> {
    TCkCtMstUomVolume getVolumeUomByDesc(String desc) throws Exception;
}
