package com.guudint.clickargo.clictruck.planexec.job.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.planexec.job.dao.CkCtJobTermDao;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkCtJobTermReq;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTerm;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class JotTermReqValidator implements IJobValidate<CkCtJobTermReq > {
	
	@Autowired
	CkCtJobTermDao jobTermDao;


	private ValidationError newValidationError(String field, String message) {
		return new ValidationError("", field, message);
	}
	
	@Override
	public List<ValidationError> validateCreate(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		List<ValidationError> errorList = new ArrayList<ValidationError>();
		
		if(dto.getTCoreAccn() == null || StringUtils.isBlank(dto.getTCoreAccn().getAccnId())) {

			errorList.add(newValidationError("tcoreAccn.accnId",
					"Account is required"));
		}
		return errorList;
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		List<ValidationError> errorList = new ArrayList<ValidationError>();
		
		List<TCkCtJobTerm> termList;
		try {
			termList = jobTermDao.findByReqId(dto.getJtrId());

			if(termList == null || termList.size() == 0) {

				errorList.add(newValidationError("TCkCtJobTerms",
						"job terms is empty"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		if(StringUtils.isBlank(dto.getJtrCommentRequestor() )) {

			errorList.add(newValidationError("jtrCommentRequestor",
					"Comment is required"));
		}
		
		return errorList;
	}

	@Override
	public List<ValidationError> validateReject(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtJobTermReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		List<ValidationError> errorList = new ArrayList<ValidationError>();
		
		if(StringUtils.isBlank(dto.getJtrCommentApprover() )) {

			errorList.add(newValidationError("jtrCommentApprover",
					"Comment is required"));
		}
		
		return errorList;
	}

}
