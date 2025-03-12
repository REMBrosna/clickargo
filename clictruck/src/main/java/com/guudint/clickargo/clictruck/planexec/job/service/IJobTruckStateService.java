package com.guudint.clickargo.clictruck.planexec.job.service;

import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface IJobTruckStateService<D> {

	public D cloneJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D withdrawJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D rejectJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D acceptJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D assignJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D startJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D stopJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D billJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D terminateJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	/**
	 * This is called when either COFF/GLI rejects the billing submitted by TO. 
	 */
	public D rejectJobPayment(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D verifyJobPayment(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	/**
	 * This is called when CO approves the billing submitted by TO. 
	 */
	public D acknowledgeJobPayment(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;
	
	/**
	 * This is called when GLI verifieds the billing documents submitted by TO. 
	 */
	public D approveJobPayment(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

	public D deleteJob(D dto, Principal principal)
			throws ParameterException, EntityNotFoundException, ValidationException, ProcessingException, Exception;

}
