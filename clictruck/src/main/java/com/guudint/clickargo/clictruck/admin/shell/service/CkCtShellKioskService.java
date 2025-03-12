package com.guudint.clickargo.clictruck.admin.shell.service;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellKiosk;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

import java.util.List;

public interface CkCtShellKioskService {

    void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException;
    List<TCkCtShellKiosk> getAllKiosks() throws ParameterException, EntityNotFoundException, ProcessingException;
    void removeUnfoundKiosks(List<String> unfoundIds) throws ParameterException, EntityNotFoundException, ProcessingException;
    void save(TCkCtShellKiosk entity) throws ParameterException, ProcessingException;
    void update(TCkCtShellKiosk entity) throws ParameterException, ProcessingException;

}
