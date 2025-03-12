package com.guudint.clickargo.clictruck.planexec.job.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckAddAttr;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkJobTruckAddAttrDao extends GenericDao<TCkJobTruckAddAttr, String>{

	public void removeAdditionalAttributes(String jobTruckId) throws Exception;
	
	public List<TCkJobTruckAddAttr> getAdditionalAttributes(String jobTruckId) throws Exception;
}
