package com.guudint.clickargo.clictruck.admin.contract.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.admin.contract.constant.CkCtContractConstant;
import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractDao;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContract;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class ContractValidator implements IJobValidate<CkCtContract> {

    private static Logger LOG = Logger.getLogger(ContractValidator.class);

    @Autowired
    private CkCtContractDao ckCtContractDao;

    @Override
    public List<ValidationError> validateCancel(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateComplete(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateConfirm(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateCreate(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        List<ValidationError> invalidList = new ArrayList<>();
        invalidList.addAll(mandatoryValidation(ckCtContract, principal));
        invalidList.addAll(uniqueValidation(ckCtContract));
        return invalidList;
    }

    @Override
    public List<ValidationError> validateDelete(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validatePaid(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validatePay(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateReject(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateSubmit(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateUpdate(CkCtContract ckCtContract, Principal principal)
            throws ParameterException, ProcessingException {
        List<ValidationError> invalidList = new ArrayList<>();
        invalidList.addAll(mandatoryValidation(ckCtContract, principal));
        invalidList.addAll(uniqueValidation(ckCtContract));
        return invalidList;
    }

    private List<ValidationError> mandatoryValidation(CkCtContract ckCtContract, Principal principal)
            throws ParameterException {
        if (principal == null) {
            throw new ParameterException("param principal null");
        }
        if (ckCtContract == null) {
            throw new ParameterException("param dto null");
        }
        List<ValidationError> invalidList = new ArrayList<>();
        if (StringUtils.isBlank(ckCtContract.getConName())) {
            invalidList.add(new ValidationError("", CkCtContractConstant.Column.CON_NAME.substring(2),
                    "Contract Name cannot be empty"));
        }
        CkCtContractCharge ckCtContractChargeCoFf = Optional
                .ofNullable(ckCtContract.getTCkCtContractChargeByConChargeCoFf())
                .orElse(new CkCtContractCharge());
        if (ckCtContractChargeCoFf.getConcPltfeeAmt() == null) {
            invalidList.add(
                    new ValidationError("", CkCtContractConstant.Column.CON_CHARGE_CO_FF_PLATFORM_FEE.substring(2),
                            "Platform Fee CO/FF cannot be empty"));
        }
        if (ckCtContractChargeCoFf.getConcAddtaxAmt() == null && ckCtContract.isAdditionalTaxCoFf()) {
            invalidList.add(
                    new ValidationError("", CkCtContractConstant.Column.CON_CHARGE_CO_FF_ADDITIONAL_TAX.substring(2),
                            "Additional Tax CO/FF cannot be empty"));
        }
        if (ckCtContractChargeCoFf.getConcWhtaxAmt() == null && ckCtContract.isWitholdTaxCoFf()) {
            invalidList.add(
                    new ValidationError("", CkCtContractConstant.Column.CON_CHARGE_CO_FF_ADDITIONAL_TAX.substring(2),
                            "Withold Tax CO/FF cannot be empty"));
        }
        CkCtContractCharge ckCtContractChargeTo = Optional
                .ofNullable(ckCtContract.getTCkCtContractChargeByConChargeTo())
                .orElse(new CkCtContractCharge());
        BigDecimal platFormFeeAmt = Optional.ofNullable(ckCtContractChargeTo.getConcPltfeeAmt())
                .orElse(BigDecimal.ZERO);
        if (platFormFeeAmt.doubleValue() < 1) {
            invalidList.add(
                    new ValidationError("", CkCtContractConstant.Column.CON_CHARGE_TO_PLATFORM_FEE.substring(2),
                            "Platform Fee TO cannot be less than 1"));
        }
        BigDecimal addTaxAmt = Optional.ofNullable(ckCtContractChargeTo.getConcAddtaxAmt()).orElse(BigDecimal.ZERO);
        if (ckCtContract.isAdditionalTaxTo() && addTaxAmt.doubleValue() < 1) {
            invalidList
                    .add(new ValidationError("",
                            CkCtContractConstant.Column.CON_CHARGE_TO_ADDITIONAL_TAX.substring(2),
                            "Additional Tax TO cannot be less than 1"));
        }
        BigDecimal concWhtaxAmt = Optional.ofNullable(ckCtContractChargeTo.getConcWhtaxAmt()).orElse(BigDecimal.ZERO);
        if (concWhtaxAmt.doubleValue() < 1 && ckCtContract.isWitholdTaxTo()) {
            invalidList
                    .add(new ValidationError("", CkCtContractConstant.Column.CON_CHARGE_TO_ADDITIONAL_TAX.substring(2),
                            "Withold Tax TO cannot less than 1"));
        }
        if (ckCtContract.getConDtStart() == null) {
            invalidList.add(
                    new ValidationError("", CkCtContractConstant.Column.CON_DT_START.substring(2),
                            "Start Date cannot be empty"));
        }
        if (ckCtContract.getConDtEnd() == null) {
            invalidList
                    .add(new ValidationError("", CkCtContractConstant.Column.CON_DT_END.substring(2),
                            "Start Date cannot be empty"));
        }
        CoreAccn accnTo = Optional.ofNullable(ckCtContract.getTCoreAccnByConTo()).orElse(new CoreAccn());
        if (StringUtils.isBlank(accnTo.getAccnId())) {
            invalidList.add(
                    new ValidationError("", CkCtContractConstant.Column.CON_TO_ID.substring(2),
                            "Truck Operator cannot be empty"));
        }
        CoreAccn accnCoFf = Optional.ofNullable(ckCtContract.getTCoreAccnByConCoFf()).orElse(new CoreAccn());
        if (StringUtils.isBlank(accnCoFf.getAccnId())) {
            invalidList.add(new ValidationError("", CkCtContractConstant.Column.CON_CO_FF_ID.substring(2),
                    "CO/FF cannot be empty"));
        }
        return invalidList;
    }

    private List<ValidationError> uniqueValidation(CkCtContract ckCtContract) {
        List<ValidationError> invalidList = new ArrayList<>();
        try {
            Optional<TCkCtContract> optTCkCtContract = ckCtContractDao.findByName(ckCtContract.getConName());
            if (optTCkCtContract.isPresent() && !optTCkCtContract.get().getConId().equals(ckCtContract.getConId())) {
                invalidList.add(new ValidationError("", CkCtContractConstant.Column.CON_NAME.substring(2),
                        "Contract Name already exists"));
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return invalidList;
    }
}
