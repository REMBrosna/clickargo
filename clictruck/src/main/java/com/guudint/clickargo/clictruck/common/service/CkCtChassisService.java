package com.guudint.clickargo.clictruck.common.service;

import com.guudint.clickargo.clictruck.common.dto.CkCtChassis;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface CkCtChassisService {
	
	CkCtChassis updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;
}
