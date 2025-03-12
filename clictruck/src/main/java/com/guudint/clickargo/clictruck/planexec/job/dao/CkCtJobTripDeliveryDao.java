package com.guudint.clickargo.clictruck.planexec.job.dao;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTripDelivery;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtJobTripDeliveryDao extends GenericDao<TCkCtJobTripDelivery, String> {
	
	TCkCtJobTripDelivery findByJobId(String jobId) throws Exception;

}
