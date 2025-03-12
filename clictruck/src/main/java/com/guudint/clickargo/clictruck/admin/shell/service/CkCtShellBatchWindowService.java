package com.guudint.clickargo.clictruck.admin.shell.service;

import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;

public interface CkCtShellBatchWindowService {

    void updateStatus(String id, char status)
            throws ParameterException, EntityNotFoundException, ProcessingException, ValidationException;
}
