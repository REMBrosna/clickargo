package com.guudint.clickargo.clictruck.opm.service;

import com.guudint.clickargo.clictruck.opm.dto.CkOpm;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public interface IOpmDashboardService {

	CkOpm find(CkOpm dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception;

}