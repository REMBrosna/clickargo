package com.guudint.clickargo.clictruck.finacing.service;

import java.util.Optional;

import com.guudint.clickargo.clictruck.finacing.dto.CkCtToInvoice;
import com.vcc.camelone.common.exception.EntityNotFoundException;
import com.vcc.camelone.common.exception.ParameterException;

public interface CkCtToInvoiceService {

	Optional<CkCtToInvoice> getByTripId(String tripId) throws ParameterException, EntityNotFoundException, Exception;
}
