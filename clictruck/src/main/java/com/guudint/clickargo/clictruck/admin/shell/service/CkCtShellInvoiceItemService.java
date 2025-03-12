package com.guudint.clickargo.clictruck.admin.shell.service;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellInvoice;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellInvoiceItem;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

import java.util.List;

public interface CkCtShellInvoiceItemService {

    void updateStatus(String id, char status) throws ParameterException, EntityNotFoundException, ProcessingException;
    void save(TCkCtShellInvoiceItem entity) throws ParameterException, ProcessingException;
    void update(TCkCtShellInvoiceItem entity) throws ParameterException, ProcessingException;

}
