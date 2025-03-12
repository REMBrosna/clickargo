package com.guudint.clickargo.clictruck.common.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.common.dto.CkCtDept;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtDeptValidator implements IJobValidate<CkCtDept> {

	@Override
	public List<ValidationError> validateCreate(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		return validateMandatoryFields(dto);
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		return validateMandatoryFields(dto);
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtDept dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	// Private methods
	private List<ValidationError> validateMandatoryFields(CkCtDept dto) {
		List<ValidationError> invalidList = new ArrayList<>();

		if (dto.getTCoreAccn() == null)
			invalidList.add(new ValidationError("", "tcoreAccn.accnId", "Account cannot be empty"));

		if (StringUtils.isBlank(dto.getDeptName()))
			invalidList.add(new ValidationError("", "deptName", "Department cannot be empty"));

		if (dto.getDeptColor() == null)
			invalidList.add(new ValidationError("", "deptColor", "Department Color cannot be empty"));

		
		return invalidList;
	}

}
