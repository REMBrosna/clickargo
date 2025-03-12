package com.guudint.clickargo.clictruck.admin.shell.service;

import com.guudint.clickargo.clictruck.admin.shell.dto.CkCtShellCardTruck;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

import java.util.List;

public interface CkCtShellCardTruckService {

	CkCtShellCardTruck updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

	List<String> getAllAssignedCards() throws EntityNotFoundException;
	List<String> getAllAssignedTrucks() throws EntityNotFoundException;
}
