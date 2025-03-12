package com.guudint.clickargo.clictruck.admin.ratetable.service;

import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface ITripRateStateService {
	
	public CkCtTripRate delete(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public CkCtTripRate verify(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public CkCtTripRate approve(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

}
