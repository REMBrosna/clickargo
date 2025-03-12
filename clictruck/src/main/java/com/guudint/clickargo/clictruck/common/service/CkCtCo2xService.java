package com.guudint.clickargo.clictruck.common.service;

import com.guudint.clickargo.clictruck.common.dto.CkCtCo2x;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface CkCtCo2xService {

	CkCtCo2x updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

	CkCtCo2x findByAccount(String accnId)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

}
	