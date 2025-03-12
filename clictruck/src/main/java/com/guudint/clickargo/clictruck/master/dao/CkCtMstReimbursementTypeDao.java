package com.guudint.clickargo.clictruck.master.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstReimbursementType;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtMstReimbursementTypeDao extends GenericDao<TCkCtMstReimbursementType, String> {

    List<TCkCtMstReimbursementType> findByRbtyStatus(Character status) throws Exception;
}
