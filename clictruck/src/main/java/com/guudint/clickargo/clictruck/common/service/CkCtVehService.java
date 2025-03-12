package com.guudint.clickargo.clictruck.common.service;

import java.util.List;

import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface CkCtVehService {

	CkCtVeh updateStatus(String id, String status)
            throws Exception;

	List<CkCtMstVehType> getVehTypeByCompany(String companyId) throws Exception;

	boolean isVehicleFree(String id, boolean isMobileJob, List<String> validStates)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception;
	
	List<CkCtVeh> associatedVehicle(String drvId, String vehTypeId) throws Exception;

}
