package com.guudint.clickargo.clictruck.common.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.guudint.clickargo.clictruck.common.dao.CkCtChassisDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtChassis;
import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class CkCtChassisValidator implements IJobValidate<CkCtChassis> {

	private static Logger LOG = Logger.getLogger(CkCtChassisValidator.class);

	@Autowired
	private CkCtChassisDao ckCtChassisDao;

	@Override
	public List<ValidationError> validateCancel(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = true)
	public List<ValidationError> validateCreate(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(mandatoryValidation(dto, principal));
		invalidList.addAll(uniqueValidation(dto));
		return invalidList;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = true)
	public List<ValidationError> validateUpdate(CkCtChassis dto, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	/**
	 * 
	 * @param dto
	 * @param principal
	 * @return
	 * @throws ParameterException
	 */
	private List<ValidationError> mandatoryValidation(CkCtChassis dto, Principal principal) throws ParameterException {
		
		if (principal == null) {
			throw new ParameterException("param principal null");
		}
		if (dto == null) {
			throw new ParameterException("param dto null");
		}
		
		List<ValidationError> invalidList = new ArrayList<>();
		Optional<CkCtMstChassisType> opMstChassisType = Optional.ofNullable(dto.getTCkCtMstChassisType());
		
		if (opMstChassisType.isPresent()) {
			if (StringUtils.isBlank(opMstChassisType.get().getChtyId()))
				invalidList.add(new ValidationError("", "TCkCtMstChassisType.chtyId", "Size cannot be empty"));
		} else {
			invalidList.add(new ValidationError("", "TCkCtMstChassisType.chtyId", "Size cannot be empty"));
		}
		
		if (StringUtils.isBlank(dto.getChsNo())) {
			invalidList.add(new ValidationError("", "chsNo", "Number cannot be empty"));
		}
		
		return invalidList;
	}

	/**
	 * 
	 * @param dto
	 * @return
	 */
	private List<ValidationError> uniqueValidation(CkCtChassis dto) {
		List<ValidationError> invalidList = new ArrayList<>();
		List<TCkCtChassis> chassisList = new ArrayList<TCkCtChassis>();
		try {
			Optional<CkCtMstChassisType> opMstChassisType = Optional.ofNullable(dto.getTCkCtMstChassisType());
			if (opMstChassisType.isPresent() && 
					StringUtils.isNotBlank(opMstChassisType.get().getChtyId()) && StringUtils.isNotBlank(dto.getChsNo())) {
				chassisList = ckCtChassisDao.findExistingChassis(dto.getChsNo(), opMstChassisType.get().getChtyId(), dto.getTCoreAccn().getAccnId());
				if (null != chassisList && !ObjectUtils.isEmpty(chassisList)) 
					invalidList.add(new ValidationError("", "chsNo", "Number already exists for the selected Size under this account" ));
			}

		} catch (Exception e) {
			LOG.error(e);
		}
		return invalidList;
	}

}
