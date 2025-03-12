package com.guudint.clickargo.clictruck.opm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.opm.dao.CkOpmDao;
import com.guudint.clickargo.clictruck.opm.dao.CkOpmSummaryDao;
import com.guudint.clickargo.clictruck.opm.dto.CkOpmJournal;
import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmSummary;
import com.guudint.clickargo.clictruck.opm.service.IOpmValidator;
import com.guudint.clickargo.common.model.ValidationError;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class OpmValidator implements IOpmValidator {

	private static Logger log = LogManager.getLogger(OpmValidator.class);

	@Autowired
	private CkOpmDao ckOpmDao;

	@Autowired
	private CkOpmSummaryDao ckOpmSummaryDao;

	@Override
	public List<ValidationError> validateOpmReserve(CkOpmJournal opmJournal)
			throws ParameterException, ProcessingException, Exception {

		log.debug("validateOpmReserve");
		try {
			if (opmJournal == null)
				throw new ParameterException("param opmJournal null");

			List<ValidationError> validationErrors = new ArrayList<>();

			TCkOpm entity = ckOpmDao.getByServiceTypeAndAccnAndCcy(opmJournal.getTCkMstServiceType(),
					opmJournal.getTCoreAccn(), opmJournal.getTMstCurrency());
			if (entity == null) {
				validationErrors.add(new ValidationError("", "validation", "credit-not-found"));
				return validationErrors;
			}

			if (opmJournal.getOpmjReserve().doubleValue() > entity.getOpmAmt().doubleValue()) {
				validationErrors.add(new ValidationError("", "validation", "insufficient-credit"));
				return validationErrors;
			}

			TCkOpmSummary summary = ckOpmSummaryDao.getByServiceTypeAndAccnAndCcy(opmJournal.getTCkMstServiceType(),
					opmJournal.getTCoreAccn(), opmJournal.getTMstCurrency());
			if (summary == null) {
				validationErrors.add(new ValidationError("", "validation", "credit-summary-not-found"));
				return validationErrors;
			}

			if (opmJournal.getOpmjReserve().doubleValue() > summary.getOpmsBalance().doubleValue()) {
				validationErrors.add(new ValidationError("", "validation", "insufficient-balance"));
				return validationErrors;
			}

			return null;
		} catch (Exception ex) {
			throw ex;
		}
	}

	@Override
	public List<ValidationError> validateOpmReverse(CkOpmJournal opmJournal)
			throws ParameterException, ProcessingException, Exception {
		log.debug("validateReverse");
		try {
			List<ValidationError> validationErrors = new ArrayList<>();
			TCkOpm entity = ckOpmDao.getByServiceTypeAndAccnAndCcy(opmJournal.getTCkMstServiceType(),
					opmJournal.getTCoreAccn(), opmJournal.getTMstCurrency());
			if (entity == null) {
				validationErrors.add(new ValidationError("", "validation", "credit-not-found"));
				return validationErrors;
			}

			TCkOpmSummary summary = ckOpmSummaryDao.getByServiceTypeAndAccnAndCcy(opmJournal.getTCkMstServiceType(),
					opmJournal.getTCoreAccn(), opmJournal.getTMstCurrency());
			if (summary == null) {
				validationErrors.add(new ValidationError("", "validation", "credit-summary-not-found"));
				return validationErrors;
			}

			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<ValidationError> validateOpmUtilize(CkOpmJournal opmJournal)
			throws ParameterException, ProcessingException, Exception {
		log.debug("validateUtilize");
		try {
			List<ValidationError> validationErrors = new ArrayList<>();
			TCkOpm entity = ckOpmDao.getByServiceTypeAndAccnAndCcy(opmJournal.getTCkMstServiceType(),
					opmJournal.getTCoreAccn(), opmJournal.getTMstCurrency());
			if (entity == null) {
				validationErrors.add(new ValidationError("", "validation", "credit-not-found"));
				return validationErrors;
			}

			TCkOpmSummary summary = ckOpmSummaryDao.getByServiceTypeAndAccnAndCcy(opmJournal.getTCkMstServiceType(),
					opmJournal.getTCoreAccn(), opmJournal.getTMstCurrency());
			if (summary == null) {
				validationErrors.add(new ValidationError("", "validation", "credit-summary-not-found"));
				return validationErrors;
			}

			if (summary.getOpmsBalance().doubleValue() < 0) {
				validationErrors.add(new ValidationError("", "validation", "insufficient-balance"));
				return validationErrors;
			}

			return null;
		} catch (Exception e) {
			throw e;
		}
	}

}
