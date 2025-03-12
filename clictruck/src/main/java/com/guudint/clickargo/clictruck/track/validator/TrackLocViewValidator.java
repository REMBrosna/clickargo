package com.guudint.clickargo.clictruck.track.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.track.dto.CkCtTrackLocDto;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class TrackLocViewValidator implements IJobValidate<CkCtTrackLocDto> {

	@Override
	public List<ValidationError> validateCreate(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtTrackLocDto dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}


}
