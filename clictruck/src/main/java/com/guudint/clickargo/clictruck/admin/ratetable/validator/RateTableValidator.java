package com.guudint.clickargo.clictruck.admin.ratetable.validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.admin.ratetable.constant.CkCtRateConstant;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTable;
import com.guudint.clickargo.clictruck.util.DateUtil;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class RateTableValidator implements IJobValidate<CkCtRateTable> {

	@Override
	public List<ValidationError> validateCancel(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
				return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
				return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
				return null;
	}

	@Override
	public List<ValidationError> validateCreate(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(mandatoryValidation(ckCtRateTable, principal));
		invalidList.addAll(dateValidation(ckCtRateTable));
		return invalidList;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
				return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
				return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
				return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
				return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
				return null;
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(mandatoryValidation(ckCtRateTable, principal));
		invalidList.addAll(dateValidation(ckCtRateTable));
		return invalidList;
	}

	private List<ValidationError> mandatoryValidation(CkCtRateTable ckCtRateTable, Principal principal)
			throws ParameterException {
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (ckCtRateTable == null) {
			throw new ParameterException("param dto null");
		}
		List<ValidationError> invalidList = new ArrayList<>();
		if (StringUtils.isBlank(ckCtRateTable.getRtName())) {
			invalidList.add(new ValidationError("", CkCtRateConstant.Column.RT_NAME.substring(2),
					"Rate Table Name cannot be empty"));
		}
		CoreAccn coreAccn = Optional.ofNullable(ckCtRateTable.getTCoreAccnByRtCoFf()).orElse(new CoreAccn());
		if (StringUtils.isBlank(coreAccn.getAccnId())) {
			invalidList.add(new ValidationError("", CkCtRateConstant.Column.RT_CO_FF_ID.substring(2),
					"Forwader cannot be empty"));
		}
		if (ckCtRateTable.getRtDtStart() == null) {
			invalidList.add(new ValidationError("", CkCtRateConstant.Column.RT_DT_START.substring(2),
					"Start Date cannot be empty"));
		}
		if (ckCtRateTable.getRtDtEnd() == null) {
			invalidList.add(new ValidationError("", CkCtRateConstant.Column.RT_DT_END.substring(2),
					"End Date cannot be empty"));
		}
		return invalidList;
	}

	private List<ValidationError> dateValidation(CkCtRateTable ckCtRateTable) {
		List<ValidationError> invalidList = new ArrayList<>();
		DateUtil startDate = new DateUtil(ckCtRateTable.getRtDtStart());
		DateUtil nowDate = new DateUtil(new Date());
		if (startDate.getDateOnly().isBefore(nowDate.getDateOnly())) {
			invalidList.add(new ValidationError("", CkCtRateConstant.Column.RT_DT_START.substring(2),
					"Start Date cannot be less than today"));
		}
		return invalidList;
	}
}
