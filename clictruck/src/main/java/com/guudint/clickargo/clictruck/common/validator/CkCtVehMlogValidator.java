package com.guudint.clickargo.clictruck.common.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.dto.CkCtVehMlog;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtVehMlogValidator implements IJobValidate<CkCtVehMlog> {

	private static Logger LOG = Logger.getLogger(CkCtVehMlogValidator.class);

	@Override
	public List<ValidationError> validateCreate(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		return mandatoryValidation(dto, principal);
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		  return mandatoryValidation(dto, principal);
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtVehMlog dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ValidationError> mandatoryValidation(CkCtVehMlog dto, Principal principal)
			throws ParameterException {
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (dto == null) {
			throw new ParameterException("param dto null");
		}
		List<ValidationError> invalidList = new ArrayList<>();

		CkCtVeh ckCtVeh = Optional.ofNullable(dto.getTCkCtVeh()).orElse(new CkCtVeh());
		if (StringUtils.isBlank(ckCtVeh.getVhId())) {
			invalidList.add(new ValidationError("", "o.TCkCtVeh.vhId".replaceFirst("o.", ""),
					"Vehicle cannot be empty"));
		}
		return invalidList;
	}

}
