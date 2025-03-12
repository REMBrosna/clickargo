package com.guudint.clickargo.clictruck.master.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstLocationType;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtMstLocationTypeDao extends GenericDao<TCkCtMstLocationType, String> {

    List<TCkCtMstLocationType> findByLctyStatus(Character state) throws Exception;
}
