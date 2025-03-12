package com.guudint.clickargo.clictruck.admin.shell.service;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellInvoice;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellKiosk;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

import java.util.List;

public interface CkCtShellInvoiceService {

    void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException;
    List<TCkCtShellInvoice> getAllInvoice() throws ParameterException, EntityNotFoundException, ProcessingException;
    void save(TCkCtShellInvoice entity) throws ParameterException, ProcessingException;
    void update(TCkCtShellInvoice entity) throws ParameterException, ProcessingException;

}
