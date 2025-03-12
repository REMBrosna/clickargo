/**
 * 
 */
package com.guudint.clickargo.clictruck.planexec.job.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckExtDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckExt;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;


@Service
@Transactional
public class CkJobTruckExtDaoImpl extends GenericDaoImpl<TCkJobTruckExt, String> implements CkJobTruckExtDao {
	private static final String EPOD_ID = "epodId";

	/*
	@Override
	public List<TCkJobTruckExt> findByJobId(String jobId) throws Exception {
	    String hql = "SELECT o.TCkJobTruck.TCkJob.jobId, o.jextKey, o.jextVal, MAX(o.jextDtCreate) AS latestDtCreate "
	            + "FROM TCkJobTruckExt o "
	            + "WHERE o.TCkJobTruck.TCkJob.jobId = :jobId "
	            + "GROUP BY o.TCkJobTruck.TCkJob.jobId, o.jextKey, o.jextVal "
	            + "ORDER BY latestDtCreate DESC";
	    Map<String, Object> params = new HashMap<>();
	    params.put("jobId", jobId);
	    return getByQuery(hql, params);
	}
	*/
	
	@Override
	public List<TCkJobTruckExt> findAllByJobTruckId(String jobTruckId) throws Exception {

	    String hql =  "FROM TCkJobTruckExt o "
	            + " WHERE o.TCkJobTruck.jobId = :jobId ";
	    
	    Map<String, Object> params = new HashMap<>();
	    params.put("jobId", jobTruckId);
	    return getByQuery(hql, params);
	}
	
	@Override
	public List<TCkJobTruckExt> findAllByJobId(String jobId) throws Exception {

	    String hql =  "FROM TCkJobTruckExt o "
	            + " WHERE o.TCkJobTruck.TCkJob.jobId = :jobId ";
	    
	    Map<String, Object> params = new HashMap<>();
	    params.put("jobId", jobId);
	    return getByQuery(hql, params);
	}
	/*-
	@Override
	public List<TCkJobTruckExt> findAllByJobId(String jobId) throws Exception {
		
        DetachedCriteria criteria = DetachedCriteria.forClass(TCkJobTruckExt.class);
        criteria.add(Restrictions.eq("TCkJobTruck.TCkJob.jobId", jobId));
        return getByCriteria(criteria);
	}
	 */
	
	@Override
	public void deleteByJobId(String jobId) throws Exception {
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("jobId", jobId);
		super.executeUpdate("DELETE FROM TCkJobTruckExt o WHERE o.TCkJobTruck.jobId = :jobId)", parameters);
	}

	public TCkJobTruckExt findByJobIdContainEpodId(String jobId) throws Exception {
		String hql = "FROM TCkJobTruckExt o "
				+ "WHERE o.TCkJobTruck.jobId = :jobId AND o.jextKey = :epodId";
		Map<String, Object> params = new HashMap<>();
		params.put("jobId", jobId);
		params.put("epodId", EPOD_ID);
				List<TCkJobTruckExt> jobTruckExtList = this.getByQuery(hql, params);
		return jobTruckExtList.isEmpty() ? null : jobTruckExtList.get(0);
	}
}
