package com.guudint.clickargo.clictruck.planexec.trip.validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripLocation;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobEvent.JobEvent;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class TripValidator implements IJobValidate<CkCtTrip> {

	@Autowired
	MessageSource messageSource;

	// Autowired
	/////////////
	Locale locale = LocaleContextHolder.getLocale();

	// private String getMessage(String message) {
	// 	return messageSource.getMessage(message, null, locale);
	// }

	// private ValidationError newValidationError(String field, String message) {
	// 	return new ValidationError("", field, message);
	// }

	// Override Methods
	///////////////////
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateCreate(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateCreate(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");

		return validateFields(dto, principal, JobEvent.CREATE.getDesc());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateUpdate(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateUpdate(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");

		return validateFields(dto, principal, JobEvent.UPDATE.getDesc());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateSubmit(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateSubmit(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");

		return validateFields(dto, principal, JobEvent.SUBMIT.getDesc());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateReject(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateReject(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateCancel(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateCancel(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (null == dto)
			throw new ParameterException("param dto null");
		if (null == principal)
			throw new ParameterException("param principal null");

		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateDelete(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateDelete(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateConfirm(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateConfirm(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validatePay(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validatePay(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validatePaid(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validatePaid(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.guudint.clickargo.job.service.IJobValidate#validateComplete(com.vcc.camelone.common.dto.AbstractDTO,
	 *      com.vcc.camelone.cac.model.Principal)
	 * 
	 */
	@Override
	public List<ValidationError> validateComplete(CkCtTrip dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ValidationError> validateFields(CkCtTrip dto, Principal principal, String action) 
			throws ParameterException, ProcessingException {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		try {
			Date dtFrom = Optional.ofNullable(dto.getTCkCtTripLocationByTrFrom()).orElse(new CkCtTripLocation()).getTlocDtLoc();
			Date dtTo = Optional.ofNullable(dto.getTCkCtTripLocationByTrTo()).orElse(new CkCtTripLocation()).getTlocDtLoc();
			if(dtFrom != null && dtTo != null){
				if(dtTo.before(dtFrom)){
					errorList.add(new ValidationError("", "Submit.API.call", "To Date cannot be less than From Date"));
				}
			}
			return errorList;
		} catch (Exception ex) {
			throw new ProcessingException(ex);		
		}
	}
}
