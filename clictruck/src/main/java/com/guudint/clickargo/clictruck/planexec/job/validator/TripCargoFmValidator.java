package com.guudint.clickargo.clictruck.planexec.job.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripCargoFm;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class TripCargoFmValidator implements IJobValidate<CkCtTripCargoFm> {

	@Override
	public List<ValidationError> validateCancel(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateCreate(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtTripCargoFm ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

}
