package com.guudint.clickargo.clictruck.planexec.job.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTerm;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class JotTermValidator implements IJobValidate<CkCtJobTerm > {

	@Override
	public List<ValidationError> validateCreate(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtJobTerm dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}


}
