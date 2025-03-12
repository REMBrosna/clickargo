package com.guudint.clickargo.clictruck.tax.validator;

import java.util.List;
import org.springframework.stereotype.Component;
import com.guudint.clickargo.tax.dto.CkTaxInvoice;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class TaxInvoiceValidator implements IJobValidate<CkTaxInvoice> {

	@Override
	public List<ValidationError> validateCreate(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateUpdate(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkTaxInvoice ckTaxInvoice, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

}
