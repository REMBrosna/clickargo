package com.guudint.clickargo.clictruck.admin.shell.service;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellTxn;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

import java.util.List;

public interface CkCtShellTxnService {

    void updateStatus(String id, char status)
            throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;
    void update(TCkCtShellTxn entity) throws ParameterException, ProcessingException;
    void save(TCkCtShellTxn entity) throws ParameterException, ProcessingException;
    List<TCkCtShellTxn> getAllShellTxn() throws ParameterException, ProcessingException;
}
