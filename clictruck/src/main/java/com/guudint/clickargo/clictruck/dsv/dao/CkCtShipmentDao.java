package com.guudint.clickargo.clictruck.dsv.dao;

import java.util.List;

import com.guudint.clickargo.clictruck.dsv.model.TCkCtShipment;
import com.guudint.clickargo.master.enums.JobStates;
import com.vcc.camelone.common.dao.GenericDao;

public interface CkCtShipmentDao extends GenericDao<TCkCtShipment, String> {
	
	List<TCkCtShipment> fetchByShipmentId(String shipmentId, String... shipmentStatus) throws Exception;
	
	List<TCkCtShipment> fetchByJobId(String jobParentId) throws Exception;
	
	List<TCkCtShipment> fetchUnprocessedJob(JobStates jobStates) throws Exception;
	
	////// Used for patch data
	List<TCkCtShipment> fetchDtStatusMessagePush2SftpIsNullAndStatusMessageIsNotNull() throws Exception;


}
