package com.guudint.clickargo.clictruck.planexec.job.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckAddAttrDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckAddAttr;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.common.dao.impl.GenericDaoImpl;

public class CkJobTruckAddAttrDaoImpl extends GenericDaoImpl<TCkJobTruckAddAttr, String>
		implements CkJobTruckAddAttrDao {

	@Override
	public void removeAdditionalAttributes(String jobTruckId) throws Exception {

		List<TCkJobTruckAddAttr> list = getAdditionalAttributes(jobTruckId);
		for(TCkJobTruckAddAttr entity : list) {
			remove(entity);
		}
//		String hql = "delete from TCkJobTruckAddAttr o where o.TCkJobTruck.jobId=:jobTruckId";
//		Map<String, Object> params = new HashMap<>();
//		params.put("jobTruckId", jobTruckId);
//		this.executeUpdate(hql, params);

	}

	@Override
	public List<TCkJobTruckAddAttr> getAdditionalAttributes(String jobTruckId) throws Exception {

		String hql = "from TCkJobTruckAddAttr o where o.TCkJobTruck.jobId = :jobTruckId and o.jaaStatus = :status";
		Map<String, Object> params = new HashMap<>();
		params.put("jobTruckId", jobTruckId);
		params.put("status", RecordStatus.ACTIVE.getCode());
		return getByQuery(hql, params);
	}

}
