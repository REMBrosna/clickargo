package com.guudint.clickargo.clictruck.admin.contract.service;

import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface CkCtContractService {

	CkCtContract updateStatus(String id, String status)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

	CkCtContract getContractByAccounts(String toAccn, String coffAccn)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

	CkCtContract getContractByAccounts(String toAccn, String coffAccn, Date startDt, Date endDt, List<Character> listStatus)
			throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;

	/**
	 * Returns the lists of contract that the {@code principal} account ID has be it
	 * with co/ff or trucking operator.
	 */
	List<CkCtContract> getContracts(Principal principal)
			throws ParameterException, EntityNotFoundException, ProcessingException, Exception;
}
