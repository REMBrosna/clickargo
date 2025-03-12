package com.guudint.clickargo.clictruck.master.dao;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstUomSize;
import com.vcc.camelone.common.dao.GenericDao;
public interface CkCtMstUomSizeDao extends GenericDao<TCkCtMstUomSize, String> {
    TCkCtMstUomSize getSizeUomByDesc(String desc) throws Exception;
}
