package com.guudint.clickargo.clictruck.master.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstTripAttachType;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtMstTripAttachTypeDao extends GenericDao<TCkCtMstTripAttachType, String> {

	List<TCkCtMstTripAttachType> findByAtStatus(Character status) throws Exception;
}
