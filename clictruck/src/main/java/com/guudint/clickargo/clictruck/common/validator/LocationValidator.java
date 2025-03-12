package com.guudint.clickargo.clictruck.common.validator;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.constant.CkCtLocationConstant;
import com.guudint.clickargo.clictruck.common.dao.CkCtLocationDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstLocationType;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class LocationValidator implements IJobValidate<CkCtLocation> {

	private static Logger LOG = Logger.getLogger(LocationValidator.class);
	private static final SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy/MM/dd");
	@Autowired
	private CkCtLocationDao ckCtLocationDao;

	@Override
	public List<ValidationError> validateCancel(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ValidationError> validateCreate(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(mandatoryValidation(ckCtLocation, principal));
		invalidList.addAll(uniqueValidation(ckCtLocation));
		invalidList.addAll(dateValidation(ckCtLocation));
		return invalidList;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ValidationError> validateUpdate(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();

		// Check for null ckCtLocation
		if (ckCtLocation == null) {
			throw new ParameterException("CkCtLocation cannot be null");
		}
		try {
			LocalDate todayDate = LocalDate.now();

			Date locDtStart = ckCtLocation.getLocDtStart();
			Date locDtEnd = ckCtLocation.getLocDtEnd();

			if (locDtStart != null) {
				LocalDate startDate = locDtStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				if (startDate.isBefore(todayDate)) {
					invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_DT_START.substring(2),
							"Start Date cannot be less than today"));
				}
			}

			if (locDtStart != null && locDtEnd != null) {
				LocalDate startDate = locDtStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate endDate = locDtEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				if (endDate.isBefore(startDate)) {
					invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_DT_END.substring(2),
							"End Date cannot be less than Start Date"));
				}
			}

			invalidList.addAll(mandatoryValidation(ckCtLocation, principal));
			invalidList.addAll(uniqueValidation(ckCtLocation));
		} catch (Exception e) {
			LOG.error("Validation error for CkCtLocation {}: {}" + e.getMessage());
			throw new ProcessingException(e.getMessage());
		}
		return invalidList;
	}

	private List<ValidationError> mandatoryValidation(CkCtLocation ckCtLocation, Principal principal)
			throws ParameterException {
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (ckCtLocation == null) {
			throw new ParameterException("param dto null");
		}
		List<ValidationError> invalidList = new ArrayList<>();
		if (StringUtils.isBlank(ckCtLocation.getLocName())) {
			invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_NAME.substring(2),
					"Location Name cannot be empty"));
		}
		CkCtMstLocationType ckCtMstLocationType = Optional.ofNullable(ckCtLocation.getTCkCtMstLocationType())
				.orElse(new CkCtMstLocationType());
		if (StringUtils.isBlank(ckCtMstLocationType.getLctyId())) {
			invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_TYPE.substring(2),
					"Location Type cannot be empty"));
		} 
		if (ckCtLocation.getLocDtStart() == null) {
			invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_DT_START.substring(2),
					"Location Start Date cannot be empty"));
		}
		if (ckCtLocation.getLocDtEnd() == null) {
			invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_DT_END.substring(2),
					"Location End Date cannot be empty"));
		}
		return invalidList;
	}

	private List<ValidationError> uniqueValidation(CkCtLocation ckCtLocation) {
		List<ValidationError> invalidList = new ArrayList<>();
		try {
			Optional<TCkCtLocation> optTCkCtLoc = ckCtLocationDao.findByNameAndCompany(ckCtLocation.getLocName(), ckCtLocation.getTCoreAccn().getAccnId());
			if (optTCkCtLoc.isPresent() && !optTCkCtLoc.get().getLocId().equals(ckCtLocation.getLocId())) {
				invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_NAME.substring(2),
						"Location Name already exists"));
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return invalidList;
	}

	private List<ValidationError> dateValidation(CkCtLocation ckCtLocation) {
		List<ValidationError> invalidList = new ArrayList<>();
		DateUtil startDate = new DateUtil(ckCtLocation.getLocDtStart());
		DateUtil nowDate = new DateUtil(new Date());
		DateUtil endDate = new DateUtil(ckCtLocation.getLocDtEnd());
		if (startDate.getDate() != null) {
			if (startDate.getDateOnly().isBefore(nowDate.getDateOnly())) {
				invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_DT_START.substring(2),
						"Start Date cannot be less than today"));
			}
			if (endDate.getDateOnly().isBefore(startDate.getDateOnly())) {
				invalidList.add(new ValidationError("", CkCtLocationConstant.Column.LOC_DT_END.substring(2),
						"End Date cannot be less than Start Date"));
			}
		}
		
		return invalidList;
	}
}