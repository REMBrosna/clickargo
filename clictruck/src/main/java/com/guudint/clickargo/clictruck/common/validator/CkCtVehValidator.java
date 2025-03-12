package com.guudint.clickargo.clictruck.common.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.common.constant.CkCtVehConstant;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtVehValidator implements IJobValidate<CkCtVeh> {

    private static Logger LOG = Logger.getLogger(CkCtVehValidator.class);

    @Autowired
    private GenericDao<TCkCtVeh, String> ckCtVehDao;

    @Override
    public List<ValidationError> validateCancel(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateComplete(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateConfirm(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateCreate(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return mandatoryValidation(ckCtVeh, principal);
    }

    @Override
    public List<ValidationError> validateDelete(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validatePaid(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validatePay(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateReject(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateSubmit(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        return null;
    }

    @Override
    public List<ValidationError> validateUpdate(CkCtVeh ckCtVeh, Principal principal)
            throws ParameterException, ProcessingException {
        List<ValidationError> invalidList = new ArrayList<>();
        invalidList.addAll(mandatoryValidation(ckCtVeh, principal));
        TCkCtVeh tCkCtVeh = null;
        try {
            tCkCtVeh = ckCtVehDao.find(ckCtVeh.getVhId());
        } catch (Exception e) {
            LOG.error(e);
        }
        if (tCkCtVeh != null) {
            if(ckCtVeh.getVhIsMaintenance() !=null){
                if (Character.compare('Y', ckCtVeh.getVhIsMaintenance()) == 0) {
                    if (CkCtVehConstant.State.ASSIGNED.equals(tCkCtVeh.getTCkCtMstVehState().getVhstId())) {
                        invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_STATE.replaceFirst("o.", ""),
                                "Truck cannot be set to maintenance, status assigned"));
                    }
                }
                if (Character.compare(RecordStatus.INACTIVE.getCode(), ckCtVeh.getVhStatus()) == 0) {
                    if (CkCtVehConstant.State.ASSIGNED.equals(tCkCtVeh.getTCkCtMstVehState().getVhstId())) {
                        invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_STATUS.replaceFirst("o.", ""),
                                "Truck cannot be set to inactive, status assigned"));
                    }
                }
            }
        }
        return invalidList;
    }

    private List<ValidationError> mandatoryValidation(CkCtVeh ckCtVeh, Principal principal) throws ParameterException {
        if (principal == null) {
            throw new ParameterException("param principal null");
        }
        if (ckCtVeh == null) {
            throw new ParameterException("param dto null");
        }
        List<ValidationError> invalidList = new ArrayList<>();
        CkCtMstVehType ckCtMstVehType = Optional.ofNullable(ckCtVeh.getTCkCtMstVehType()).orElse(new CkCtMstVehType());
        if (StringUtils.isBlank(ckCtMstVehType.getVhtyId())) {
            invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_TYPE.replaceFirst("o.", ""),
                    "Truck Type cannot be empty"));
        }
        if (StringUtils.isBlank(ckCtVeh.getVhPlateNo())) {
            invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_PLATE_NO.replaceFirst("o.", ""),
                    "Plate Number cannot be empty"));
        }
        if (Optional.ofNullable(ckCtVeh.getVhLength()).orElse(new Short("0")).intValue() == 0) {
            invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_LENGTH.replaceFirst("o.", ""),
                    "Length cannot be empty"));
        }
        if (Optional.ofNullable(ckCtVeh.getVhWidth()).orElse(new Short("0")).intValue() == 0) {
            invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_WIDTH.replaceFirst("o.", ""),
                    "Width cannot be empty"));
        }
        if (Optional.ofNullable(ckCtVeh.getVhHeight()).orElse(new Short("0")).intValue() == 0) {
            invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_HEIGHT.replaceFirst("o.", ""),
                    "Height cannot be empty"));
        }
        if (Optional.ofNullable(ckCtVeh.getVhWeight()).orElse(0).intValue() == 0) {
            invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_WEIGHT.replaceFirst("o.", ""),
                    "Weight cannot be empty"));
        }
        if (Optional.ofNullable(ckCtVeh.getVhVolume()).orElse(0).intValue() == 0) {
            invalidList.add(new ValidationError("", CkCtVehConstant.Column.VH_VOLUME.replaceFirst("o.", ""),
                    "Volume cannot be empty"));
        }
        if (StringUtils.isNotBlank(ckCtVeh.getVhChassisNo()) && ckCtVeh.getVhChassisNo().contains("OTHERS")
        		&& StringUtils.isBlank(ckCtVeh.getVhChassisNoOth())) {
        	invalidList.add(new ValidationError("", "vhChassisNoOth", "Please specify Number (Others)"));
        }
        return invalidList;
    }
}
