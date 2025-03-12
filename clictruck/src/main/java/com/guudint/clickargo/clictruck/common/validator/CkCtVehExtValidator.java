package com.guudint.clickargo.clictruck.common.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.common.dto.CkCtVehExt;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtVehExtValidator implements IJobValidate<CkCtVehExt> {

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(CkCtVehExtValidator.class);

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_PATTERN = "^(?!0)[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";

	@Override
	public List<ValidationError> validateCancel(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateCreate(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return mandatoryValidation(ckCtVehExt, principal);
	}

	@Override
	public List<ValidationError> validateDelete(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException, ProcessingException {
		return mandatoryValidation(ckCtVehExt, principal);
	}

	private List<ValidationError> mandatoryValidation(CkCtVehExt ckCtVehExt, Principal principal)
			throws ParameterException {
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (ckCtVehExt == null) {
			throw new ParameterException("param dto null");
		}
		List<ValidationError> invalidList = new ArrayList<>();

		String type = ckCtVehExt.getExtType() + "_";

		if (ckCtVehExt.getVextNotify() == null) {
			invalidList.add(new ValidationError("", type + "vextNotify", "Notify By cannot be empty"));
		}

		if (ckCtVehExt.getVextNotify() != null) {

			if (StringUtils.isBlank(ckCtVehExt.getVextNotifyEmail())
					& StringUtils.isBlank(ckCtVehExt.getVextNotifyWhatsapp())) {
				invalidList.add(new ValidationError("", type + "notifyBy", "Notification Method cannot be empty"));
			}

			if (StringUtils.isNotBlank(ckCtVehExt.getVextNotifyEmail())) {
				Pattern pattern = Pattern.compile(EMAIL_PATTERN);
				Matcher matcher = pattern.matcher(ckCtVehExt.getVextNotifyEmail());
				if (!matcher.matches()) {
					invalidList.add(new ValidationError("", type + "vextNotifyEmail",
							"Vehicle Ext Notify Email invalid"));
				}
			}

			if (StringUtils.isNotBlank(ckCtVehExt.getVextNotifyWhatsapp())) {
				Pattern pattern = Pattern.compile(PHONE_PATTERN);
				Matcher matcher = pattern.matcher(ckCtVehExt.getVextNotifyWhatsapp());
				if (!matcher.matches()) {

					invalidList.add(new ValidationError("", type + "vextNotifyWhatsapp",
							"Vehicle Ext Notify Whatsapp invalid"));
				}
			}
		}

		if (ckCtVehExt.getVextMonitorMthd() == null) {
			invalidList.add(new ValidationError("", type+"vextMonitorMthd", "Monitor Method cannot be empty"));
		}

		if (ckCtVehExt.getVextMonitorMthd() != null && StringUtils.isBlank(ckCtVehExt.getVextMonitorValue())) {
			invalidList.add(new ValidationError("", type+"vextMonitorValue", "Monitor Method Value cannot be empty"));
		}

		return invalidList;
	}
}
