package com.guudint.clickargo.clictruck.common.service;

import com.guudint.clickargo.clictruck.common.dto.CkCtVehExt;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface CkCtVehExtService {

	CkCtVehExt updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

	CkCtVehExt findVehExtById(String id)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

	CkCtVehExt findVehExtByVehAndKey(String vehId, String key)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

}
