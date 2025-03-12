package com.guudint.clickargo.clictruck.admin.ratetable.service.impl;

import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.service.ITripRateStateService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public class TripRateStateServiceImpl implements ITripRateStateService {

	@Override
	public CkCtTripRate verify(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CkCtTripRate approve(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CkCtTripRate delete(CkCtTripRate dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
