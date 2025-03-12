package com.guudint.clickargo.clictruck.planexec.trip.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstReimbursementType;
import com.guudint.clickargo.clictruck.planexec.trip.constant.CkCtTripReimbursementConstant;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripReimbursement;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class TripReimbursementValidator implements IJobValidate<CkCtTripReimbursement> {

    @Override
    public List<ValidationError> validateCancel(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateComplete(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateConfirm(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateCreate(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        List<ValidationError> invalidList = new ArrayList<>();
        invalidList.addAll(mandatoryValidation(ckCtTripReimbursement, principal));
        return invalidList;
    }

    @Override
    public List<ValidationError> validateDelete(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validatePaid(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validatePay(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateReject(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateSubmit(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateUpdate(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException, ProcessingException {
        List<ValidationError> invalidList = new ArrayList<>();
        invalidList.addAll(mandatoryValidation(ckCtTripReimbursement, principal));
        return invalidList;
    }

    private List<ValidationError> mandatoryValidation(CkCtTripReimbursement ckCtTripReimbursement, Principal principal)
            throws ParameterException {
        if (principal == null) {
            throw new ParameterException("param principal null");
        }
        if (ckCtTripReimbursement == null) {
            throw new ParameterException("param dto null");
        }
        List<ValidationError> invalidList = new ArrayList<>();
        CkCtMstReimbursementType ckCtMstReimbursementType = Optional
                .ofNullable(ckCtTripReimbursement.getTCkCtMstReimbursementType())
                .orElse(new CkCtMstReimbursementType());
        if (StringUtils.isBlank(ckCtMstReimbursementType.getRbtypId())) {
            invalidList.add(new ValidationError("", CkCtTripReimbursementConstant.Column.TR_TYPE.substring(2),
                    "Reimbursement Type cannot be empty"));
        }
        if (StringUtils.isBlank(ckCtTripReimbursement.getBase64File())) {
            invalidList.add(new ValidationError("", "base64File", "Receipt File cannot be empty"));
        }
        return invalidList;
    }
}
