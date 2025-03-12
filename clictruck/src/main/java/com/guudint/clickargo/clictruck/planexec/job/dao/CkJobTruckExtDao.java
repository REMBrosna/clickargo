/**
 * 
 */
package com.guudint.clickargo.clictruck.planexec.job.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.vcc.camelone.common.dao.GenericDao;


public interface CkJobTruckExtDao extends GenericDao<TCkJobTruckExt, String> {

	//List<TCkJobTruckExt> findByJobId(String jobId) throws Exception;
	
	List<TCkJobTruckExt> findAllByJobTruckId(String jobTruckId) throws Exception;
	
	List<TCkJobTruckExt> findAllByJobId(String jobId) throws Exception;
	
	void deleteByJobId(String jobId) throws Exception;
	TCkJobTruckExt findByJobIdContainEpodId(String jobId) throws Exception;

}
