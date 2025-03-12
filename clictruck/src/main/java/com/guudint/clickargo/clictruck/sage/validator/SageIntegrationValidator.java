package com.guudint.clickargo.clictruck.sage.validator;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.guudint.clickargo.sage.dto.CkSageIntegration;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class SageIntegrationValidator implements IJobValidate<CkSageIntegration> {

	@Autowired
	MessageSource messageSource;

	// Autowired
	/////////////
	Locale locale = LocaleContextHolder.getLocale();

	@Override
	public List<ValidationError> validateCreate(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateUpdate(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkSageIntegration dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
