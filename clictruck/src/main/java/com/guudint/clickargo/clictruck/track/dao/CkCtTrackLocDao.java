package com.guudint.clickargo.clictruck.track.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.track.model.TCkCtTrackLoc;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtTrackLocDao extends GenericDao<TCkCtTrackLoc, String> {
	
	List<TCkCtTrackLoc> findByJobId(String jobTruckId) throws Exception;

}
