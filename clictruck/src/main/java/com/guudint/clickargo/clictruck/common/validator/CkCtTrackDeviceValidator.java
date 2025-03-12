package com.guudint.clickargo.clictruck.common.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.common.dto.CkCtTrackDevice;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtTrackDeviceValidator implements IJobValidate<CkCtTrackDevice> {

	@Override
	public List<ValidationError> validateCreate(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return mandatoryValidation(dto, principal);
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {
		
		return mandatoryValidation(dto, principal);
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtTrackDevice dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	private List<ValidationError> mandatoryValidation(CkCtTrackDevice dto, Principal principal)
			throws ParameterException {
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (dto == null) {
			throw new ParameterException("param dto null");
		}
		List<ValidationError> invalidList = new ArrayList<>();
		if (StringUtils.isBlank(dto.getTdGpsImei())) {
			invalidList.add(new ValidationError("", "tdGpsImei", "IMEI cannot be empty"));
		}
		if ( dto.getTCkCtVeh() == null || StringUtils.isBlank(dto.getTCkCtVeh().getVhId())) {
			invalidList.add(new ValidationError("", "TCkCtVeh.vhId", "Vehicle cannot be empty"));
		}
		if ( dto.getTCoreAccn() == null || StringUtils.isBlank(dto.getTCoreAccn().getAccnId())) {
			invalidList.add(new ValidationError("", "TCoreAccn.accnId", "Account cannot be empty"));
		}
		return invalidList;
	}
}
