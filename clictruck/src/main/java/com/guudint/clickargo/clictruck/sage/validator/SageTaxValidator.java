package com.guudint.clickargo.clictruck.sage.validator;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.guudint.clickargo.clictruck.sage.dto.CkCtSageTax;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class SageTaxValidator implements IJobValidate<CkCtSageTax> {

	@Override
	public List<ValidationError> validateCreate(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		return mandatoryValidation(ckCtSageTax, principal);
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		return mandatoryValidation(ckCtSageTax, principal);
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtSageTax ckCtSageTax, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<ValidationError> mandatoryValidation(CkCtSageTax ckCtSageTax, Principal principal) throws ParameterException {
        if (principal == null) {
            throw new ParameterException("param principal null");
        }
        if (ckCtSageTax == null) {
            throw new ParameterException("param dto null");
        }
        List<ValidationError> invalidList = new ArrayList<>();
        if (ckCtSageTax.getStRangeBegin() < 0L) {
            invalidList.add(new ValidationError("", "stRangeBegin",
                    "Range Begin must be positive number"));
        }
        
        if (Long.toString(ckCtSageTax.getStRangeBegin()).length() > 8) {
            invalidList.add(new ValidationError("", "stRangeBegin",
                    "Range Begin must be 8 digit number"));
        }
        
        if (ckCtSageTax.getStRangeEnd() < 0L) {
            invalidList.add(new ValidationError("", "stRangeEnd",
                    "Range End must be positive number"));
        }
        
        if (Long.toString(ckCtSageTax.getStRangeEnd()).length() > 8) {
            invalidList.add(new ValidationError("", "stRangeEnd",
                    "Range End must be 8 digit number"));
        }
        
        if (ckCtSageTax.getStRangeEnd() <= ckCtSageTax.getStRangeBegin()) {
            invalidList.add(new ValidationError("", "stRangeEnd",
                    "Range End must be greater than Range Begin"));
        }
        
//        if (ckCtSageTax.getStRangeCurrent() < 0L) {
//            invalidList.add(new ValidationError("", "stRangeCurrent",
//                    "Range Current must be positive number"));
//        }
//        
//        if (Long.toString(ckCtSageTax.getStRangeCurrent()).length() > 8) {
//            invalidList.add(new ValidationError("", "stRangeCurrent",
//                    "Range Current must be 8 digit number"));
//        }
//        
//        if (ckCtSageTax.getStRangeCurrent() < ckCtSageTax.getStRangeBegin()) {
//            invalidList.add(new ValidationError("", "stRangeCurrent",
//                    "Range Current must be equal to or greater than Range Begin"));
//        }
        
        String pattern = "\\d{3}\\.\\d{3}[-\\.]\\d{2}\\."; // pattern for example data : 010.011.23. or 010.011-23.
        if (StringUtils.isBlank(ckCtSageTax.getStPrefix()) || !ckCtSageTax.getStPrefix().matches(pattern)) {
            invalidList.add(new ValidationError("", "stPrefix",
                    "Prefix must follow this format (xxx.xxx-xx.)"));
        }
        
        return invalidList;
    }
}

