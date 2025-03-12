package com.guudint.clickargo.clictruck.common.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.common.dto.CkCtCo2x;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtCo2xValidator implements IJobValidate<CkCtCo2x> {

	@Override
	public List<ValidationError> validateCreate(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		return validateMandatoryFields(dto);

	}

	@Override
	public List<ValidationError> validateUpdate(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		return validateMandatoryFields(dto);
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtCo2x dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ValidationError> validateMandatoryFields(CkCtCo2x dto) {
		List<ValidationError> invalidList = new ArrayList<>();

		if (dto.getTCoreAccn() == null)
			invalidList.add(new ValidationError("", "tcoreAccn.accnId", "Account cannot be empty"));

		if (StringUtils.isBlank(dto.getCo2xCoyId()))
			invalidList.add(new ValidationError("", "co2xCoyId", "Company ID cannot be empty"));

		if (dto.getCo2xDtExpiry() == null)
			invalidList.add(new ValidationError("", "co2xDtExpiry", "Expiry Date cannot be empty"));

		if (StringUtils.isBlank(dto.getCo2xUid()))
			invalidList.add(new ValidationError("", "co2xUid", "User ID cannot be empty"));

		if (StringUtils.isBlank(dto.getCo2xPwd()))
			invalidList.add(new ValidationError("", "co2xPwd", "Password cannot be empty"));

		return invalidList;
	}
}
