package com.guudint.clickargo.clictruck.common.validator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.common.constant.CkCtDrvConstant;
import com.guudint.clickargo.clictruck.common.dao.CkCtDrvDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.common.ICkConstant;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtDrvValidator implements IJobValidate<CkCtDrv> {

	private static Logger LOG = Logger.getLogger(CkCtDrvValidator.class);

	private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{6,}$";
	private static final int PHONE_MIN_LENGTH = 8;
	
	@Autowired
	private CkCtDrvDao ckCtDrvDao;

	@Override
	public List<ValidationError> validateCancel(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = true)
	public List<ValidationError> validateCreate(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(mandatoryValidation(ckCtDrv, principal));
		invalidList.addAll(uniqueValidation(ckCtDrv));
		invalidList.addAll(phoneNumberFormatValidation(ckCtDrv.getDrvPhone()));

		try {
			TCkCtDrv tCkCtDrvMobileId = ckCtDrvDao.findByMobileUserId(ckCtDrv.getDrvMobileId());
			if (tCkCtDrvMobileId != null
					&& StringUtils.equalsIgnoreCase(tCkCtDrvMobileId.getDrvMobileId(), ckCtDrv.getDrvMobileId())) {
				invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_MOBILE_ID.substring(2),
						"User ID already exists"));
			}
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}

		return invalidList;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = true)
	public List<ValidationError> validateUpdate(CkCtDrv ckCtDrv, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(mandatoryValidation(ckCtDrv, principal));
		invalidList.addAll(uniqueValidation(ckCtDrv));
		// Check duplicate mobile user ID
		try {
			TCkCtDrv tckCtDrv = ckCtDrvDao.findByMobileUserId(ckCtDrv.getDrvMobileId());
			if (null != tckCtDrv && !tckCtDrv.getDrvMobileId().equalsIgnoreCase(ckCtDrv.getDrvMobileId())) {
				invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_MOBILE_ID.substring(2),
						"User ID already exists"));
			}
		} catch (Exception ex) {
			throw new ProcessingException(ex);
		}
		
		invalidList.addAll(phoneNumberFormatValidation(ckCtDrv.getDrvPhone()));
		return invalidList;
	}

	private List<ValidationError> mandatoryValidation(CkCtDrv ckCtDrv, Principal principal) throws ParameterException {
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (ckCtDrv == null) {
			throw new ParameterException("param dto null");
		}
		List<ValidationError> invalidList = new ArrayList<>();
		if (StringUtils.isBlank(ckCtDrv.getDrvName())) {
			invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_NAME.substring(2),
					"Driver Name cannot be empty"));
		}
		if (StringUtils.isBlank(ckCtDrv.getDrvLicenseNo())) {
			invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_LICENSE_NO.substring(2),
					"Driver License Number cannot be empty"));
		}
		if (StringUtils.isBlank(ckCtDrv.getDrvMobileId())) {
			invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_MOBILE_ID.substring(2),
					"User Id cannot be empty"));
		}
		/*
		if (StringUtils.isBlank(ckCtDrv.getDrvMobilePassword())) {
			invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_MOBILE_PASSWORD.substring(2),
					"Password cannot be empty"));
		} else {
			if (ckCtDrv.isDrvEditPassword()) {
				Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
				Matcher matcher = pattern.matcher(ckCtDrv.getDrvMobilePassword());
				if (!matcher.matches()) {
					invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_MOBILE_PASSWORD.substring(2),
							"Password must be at least 6 characters, containing uppercase letter, lowercase letter, number and special character."));
				}
			}
		}
		*/
		
		if (StringUtils.isBlank(ckCtDrv.getDrvEmail())) {
			invalidList.add(
					new ValidationError("", CkCtDrvConstant.Column.DRV_EMAIL.substring(2), "Email cannot be empty"));
		}
		if (StringUtils.isBlank(ckCtDrv.getDrvPhone())) {
			invalidList.add(
					new ValidationError("", CkCtDrvConstant.Column.DRV_PHONE.substring(2), "Phone cannot be empty"));
		}
		return invalidList;
	}

	private List<ValidationError> uniqueValidation(CkCtDrv ckCtDrv) {
		List<ValidationError> invalidList = new ArrayList<>();
		try {
			Optional<TCkCtDrv> optTCkCtDrv = ckCtDrvDao.findByLicenseNo(ckCtDrv.getDrvLicenseNo());
			if (optTCkCtDrv.isPresent() && !optTCkCtDrv.get().getDrvId().equals(ckCtDrv.getDrvId())) {
				invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_LICENSE_NO.substring(2),
						"License Number already exists"));
			}
			
			// Nina: Removed this validation as driver may not have email
			// https://jira.vcargocloud.com/browse/CT2SG-225 Allow duplicated email to be used for Driver account.
            /*
			optTCkCtDrv = ckCtDrvDao.findByEmail(ckCtDrv.getDrvEmail());
            if (optTCkCtDrv.isPresent() && !optTCkCtDrv.get().getDrvId().equals(ckCtDrv.getDrvId())) {
                invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_EMAIL.substring(2),
                        "Email already exists"));
            }
            */
		} catch (Exception e) {
			LOG.error(e);
		}
		return invalidList;
	}

	private List<ValidationError> expiryLicenseValidation(CkCtDrv ckCtDrv) {
		List<ValidationError> invalidList = new ArrayList<>();
		Date createdDate = null;
		if (StringUtils.isBlank(ckCtDrv.getDrvId())) {
			createdDate = ckCtDrv.getDrvDtCreate();
		} else {
			try {
				TCkCtDrv tCkCtDrv = ckCtDrvDao.find(ckCtDrv.getDrvId());
				createdDate = tCkCtDrv.getDrvDtCreate();
			} catch (Exception e) {
				LOG.error(e);
			}
		}
		DateUtil expiryLicense = new DateUtil(ckCtDrv.getDrvLicenseExpiry());
		DateUtil minExpiry = new DateUtil(createdDate);
		minExpiry.add(Calendar.MONTH, 6);
		if (expiryLicense.getDateOnly().isBefore(minExpiry.getDateOnly())) {
			invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_LICENSE_EXPIRY.substring(2),
					"License Expiry Date should be greater then 6 months from created date"));
		}
		return invalidList;
	}

	private List<ValidationError> phoneNumberFormatValidation(String phoneNumber) {
		String regex = ICkConstant.PHONE_PATTERN;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phoneNumber.trim());
		List<ValidationError> invalidList = new ArrayList<>();
		if (StringUtils.isBlank(phoneNumber)) {
			invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_PHONE.substring(2),
					"Phone cannot be empty."));
		} else {
			if (phoneNumber.length() < PHONE_MIN_LENGTH) {
				invalidList.add(
						new ValidationError("", CkCtDrvConstant.Column.DRV_PHONE.substring(2), "Phone must contain at least 8 characters"));
			} else if (!matcher.matches()) {
				invalidList.add(new ValidationError("", CkCtDrvConstant.Column.DRV_PHONE.substring(2),
						"Invalid Phone Number format. Please include +Country Code."));
			}
		}

		return invalidList;
	}
}
