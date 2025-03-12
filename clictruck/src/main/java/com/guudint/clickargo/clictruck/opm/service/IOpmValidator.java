package com.guudint.clickargo.clictruck.opm.service;

import java.util.List;

import com.guudint.clickargo.clictruck.opm.dto.CkOpmJournal;
import com.guudint.clickargo.common.model.ValidationError;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

public interface IOpmValidator {

	public List<ValidationError> validateOpmReserve(CkOpmJournal opmJournal)
			throws ParameterException, ProcessingException, Exception;

	public List<ValidationError> validateOpmReverse(CkOpmJournal opmJournal)
			throws ParameterException, ProcessingException, Exception;

	public List<ValidationError> validateOpmUtilize(CkOpmJournal opmJournal)
			throws ParameterException, ProcessingException, Exception;

}
