package com.guudint.clickargo.clictruck.admin.ratetable.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.guudint.clickargo.clictruck.admin.ratetable.constant.CkCtTripConstant;
import com.guudint.clickargo.clictruck.admin.ratetable.dao.CkCtTripRateDao;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtTripRate;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtTripRate;
import com.guudint.clickargo.clictruck.common.dao.CkCtLocationDao;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;

@Component
public class TripRateValidator implements IJobValidate<CkCtTripRate> {
	
	private static Logger LOG = Logger.getLogger(TripRateValidator.class);

	@Autowired
	private CkCtLocationDao ckCtLocationDao;
	
	@Autowired
	private CkCtTripRateDao ckCtTripRateDao;
	
	private String ACTION_CREATE = "CREATE";
	private String ACTION_UPDATE = "UPDATE";

	@Override
	public List<ValidationError> validateCancel(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ValidationError> validateCreate(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(uniqueValidation(ckCkTripRate, ACTION_CREATE));
		return invalidList;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ValidationError> validateUpdate(CkCtTripRate ckCkTripRate, Principal principal)
			throws ParameterException, ProcessingException {
		List<ValidationError> invalidList = new ArrayList<>();
		invalidList.addAll(uniqueValidation(ckCkTripRate, ACTION_UPDATE));
		return invalidList;
	}
	
	private List<ValidationError> uniqueValidation(CkCtTripRate ckCkTripRate, String action) {
		List<ValidationError> invalidList = new ArrayList<>();
		try {
			Optional<TCkCtLocation> optTCkCtLocFrom = ckCtLocationDao
					.findById(ckCkTripRate.getTCkCtLocationByTrLocFrom().getLocId());
			Optional<TCkCtLocation> optTCkCtLocTo = ckCtLocationDao
					.findById(ckCkTripRate.getTCkCtLocationByTrLocTo().getLocId());
			List<TCkCtTripRate> optTCkCtTripRate = ckCtTripRateDao.findByRateTableIdLocFromToAndTruckType(
					ckCkTripRate.getTCkCtRateTable().getRtId(), ckCkTripRate.getTCkCtLocationByTrLocFrom().getLocId(),
					ckCkTripRate.getTCkCtLocationByTrLocTo().getLocId(), ckCkTripRate.getTCkCtMstVehType().getVhtyId());
			
			if (optTCkCtLocFrom.isPresent() && optTCkCtLocTo.isPresent()) {
				if(optTCkCtLocFrom.get().getLocName().equalsIgnoreCase(optTCkCtLocTo.get().getLocName())) {
					invalidList.add(new ValidationError("", CkCtTripConstant.Column.TR_LOC_FROM.substring(2),
							"Location From and Location To cannot be the same"));
					invalidList.add(new ValidationError("", CkCtTripConstant.Column.TR_LOC_TO.substring(2),
							"Location From and Location To cannot be the same"));
				}
			}
			
			if (!optTCkCtTripRate.isEmpty()) {
				if (action.equalsIgnoreCase(ACTION_CREATE)) {
					invalidList.add(new ValidationError("", CkCtTripConstant.Column.TR_CHARGE.substring(2),
							"Trip Rate Charge already exists"));
				}
			}
			
		} catch (Exception e) {
			LOG.error(e);
		}
		return invalidList;
	}


}
