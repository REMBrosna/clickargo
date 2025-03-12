package com.guudint.clickargo.clictruck.finacing.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.finacing.constant.CkCtToInvoiceConstant;
import com.guudint.clickargo.clictruck.finacing.dao.CkCtToInvoiceDao;
import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.guudint.clickargo.clictruck.finacing.model.TCkCtToInvoice;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtToInvoiceValidator implements IJobValidate<CkCtToInvoice> {

    @Autowired
    private CkCtToInvoiceDao ckCtToInvoiceDao;

    private static Logger LOG = Logger.getLogger(CkCtToInvoiceValidator.class);

    @Override
    public List<ValidationError> validateCancel(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCancel'");
    }

    @Override
    public List<ValidationError> validateComplete(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateComplete'");
    }

    @Override
    public List<ValidationError> validateConfirm(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateConfirm'");
    }

    @Override
    public List<ValidationError> validateCreate(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        List<ValidationError> invalidList = new ArrayList<>();
        invalidList.addAll(mandatoryValidation(ckCtToInvoice, principal));
        invalidList.addAll(uniqueValidation(ckCtToInvoice, principal));
        return invalidList;
    }

    @Override
    public List<ValidationError> validateDelete(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateDelete'");
    }

    @Override
    public List<ValidationError> validatePaid(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validatePaid'");
    }

    @Override
    public List<ValidationError> validatePay(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validatePay'");
    }

    @Override
    public List<ValidationError> validateReject(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateReject'");
    }

    @Override
    public List<ValidationError> validateSubmit(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateSubmit'");
    }

    @Override
    public List<ValidationError> validateUpdate(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException, ProcessingException {
        List<ValidationError> invalidList = new ArrayList<>();
        invalidList.addAll(mandatoryValidation(ckCtToInvoice, principal));
        return invalidList;
    }

    private List<ValidationError> mandatoryValidation(CkCtToInvoice ckCtToInvoice, Principal principal)
            throws ParameterException {
        if (principal == null) {
            throw new ParameterException("param principal null");
        }
        if (ckCtToInvoice == null) {
            throw new ParameterException("param invoice null");
        }
        if (ckCtToInvoice.getTCkCtTrip() == null) {
            throw new ParameterException("param trip null");
        }
        List<ValidationError> invalidList = new ArrayList<>();
        if (StringUtils.isBlank(ckCtToInvoice.getInvNo())) {
            invalidList.add(new ValidationError("", CkCtToInvoiceConstant.Column.INV_NO.substring(2),
                    "Invoice No. cannot be empty"));
        }
        if (ckCtToInvoice.getInvDtIssue() == null) {
            invalidList.add(new ValidationError("", CkCtToInvoiceConstant.Column.INV_DT_ISSUE.substring(2),
                    "Invoice Date cannot be empty"));
        }
        if (StringUtils.isBlank(ckCtToInvoice.getBase64File())) {
            invalidList.add(new ValidationError("", CkCtToInvoiceConstant.Column.INV_LOC.substring(2),
                    "Invoice File cannot be empty"));
        }
        return invalidList;
    }

    private List<ValidationError> uniqueValidation(CkCtToInvoice ckCtToInvoice, Principal principal) {
        List<ValidationError> invalidList = new ArrayList<>();
        try {
            List<TCkCtToInvoice> optCkCtToInvoice = ckCtToInvoiceDao
                    .findByTripId(ckCtToInvoice.getTCkCtTrip().getTrId());
            if (!optCkCtToInvoice.isEmpty()) {
                invalidList.add(new ValidationError("", CkCtToInvoiceConstant.Column.INV_NO.substring(2),
                        "This trip already has an invoice"));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return invalidList;
    }
}
