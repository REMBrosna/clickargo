package com.guudint.clickargo.clictruck.planexec.job.validator;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstTripAttachType;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTripAttach;
import com.guudint.clickargo.clictruck.planexec.trip.mobile.service.TripMobileService.TripAttachTypeEnum;
import com.guudint.clickargo.clictruck.planexec.trip.service.impl.CkCtTripDoService;
import com.guudint.clickargo.common.model.ValidationError;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class TripAttachValidator {

	Locale locale = LocaleContextHolder.getLocale();
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	private CkCtTripDoService ckCtTripDoService;
	
	public List<ValidationError> validateCreate(CkCtTripAttach dto, Principal principal)
			throws ParameterException, ProcessingException {

		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(this.validateMandatoryFields(dto, principal));

		return invalidList;
	}
	
	/**
	 * @param dto
	 * @return
	 * @throws ParameterException 
	 * @throws ProcessingException 
	 */
	private List<ValidationError> validateMandatoryFields(CkCtTripAttach dto, Principal principal) throws ParameterException, ProcessingException {
		
		if (principal == null)
			throw new ParameterException("param principal null");
		if (dto == null)
			throw new ParameterException("param dto null");
		
		try {
			List<ValidationError> errorList = new ArrayList<>();
			
			Optional<CkCtMstTripAttachType> opCkMstAccnAttType = Optional.ofNullable(dto.getTCkCtMstTripAttachType());
			Optional<CkCtTrip> opCkCtTrip = Optional.ofNullable(dto.getTCkCtTrip());
			Optional<String> opAtName = Optional.ofNullable(dto.getAtName());
			Optional<byte[]> opAtLocData = Optional.ofNullable(dto.getAtLocData());
			boolean allowRequired = opCkMstAccnAttType.isPresent() &&
					Objects.nonNull(opCkMstAccnAttType.get().getAtypId()) &&
					(TripAttachTypeEnum.PHOTO.name().equalsIgnoreCase(opCkMstAccnAttType.get().getAtypId().toString()) || TripAttachTypeEnum.DOCUMENT.name().equalsIgnoreCase(opCkMstAccnAttType.get().getAtypId().toString()));


			// Validate Trip Attach Type
			if (opCkMstAccnAttType.isPresent()) {
				if (StringUtils.isBlank(opCkMstAccnAttType.get().getAtypId()))
					errorList.add(new ValidationError("", "TCkCtMstTripAttachType.atypId", getMessage("valid.tripattachtype.atypId")));
			} else {
				errorList.add(new ValidationError("", "TCkCtMstTripAttachType.atypId", getMessage("valid.tripattachtype.atypId")));
			}

			// Validate Trip
			if (opCkCtTrip.isPresent()) {
				if (StringUtils.isBlank(opCkCtTrip.get().getTrId()))
					errorList.add(new ValidationError("", "TCkCtTrip.trId", getMessage("valid.trip.trId")));
			} else {
				errorList.add(new ValidationError("", "TCkCtTrip.trId", getMessage("valid.trip.trId")));
			}

			// Validate File Name
			if (opAtName.isPresent()) {
				if (StringUtils.isBlank(opAtName.get()) && allowRequired)
					errorList.add(new ValidationError("", "atName", getMessage("valid.tripattach.atName")));
			} else if (allowRequired){
				errorList.add(new ValidationError("", "atName", getMessage("valid.tripattach.atName")));
			}

			// Validate File Date
			if (opAtLocData.isPresent()) {
				if (ObjectUtils.isEmpty(opAtLocData.get()) && allowRequired)
					errorList.add(new ValidationError("", "atName", getMessage("valid.tripattach.atName")));
			} else if (allowRequired){
				errorList.add(new ValidationError("", "atName", getMessage("valid.tripattach.atName")));
			}

			// Validate File Format for Photo Pick-up, Drop-off and Signature
			if (opCkMstAccnAttType.isPresent() && opAtLocData.isPresent()) {
				if ((opCkMstAccnAttType.get().getAtypId().equalsIgnoreCase(TripAttachTypeEnum.PHOTO_PICKUP.name()) ||
						opCkMstAccnAttType.get().getAtypId().equalsIgnoreCase(TripAttachTypeEnum.PHOTO_DROPOFF.name()) ||
						opCkMstAccnAttType.get().getAtypId().equalsIgnoreCase(TripAttachTypeEnum.SIGNATURE.name())) &&
						!ckCtTripDoService.isMimeTypeAllowed(opAtLocData.get(), true, false))
					errorList.add(new ValidationError("", "atName", getMessage("valid.tripattachtype.format")));
			}
			
			return errorList;
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	private String getMessage(String message) {
		return messageSource.getMessage(message, null, locale);
	}
}
