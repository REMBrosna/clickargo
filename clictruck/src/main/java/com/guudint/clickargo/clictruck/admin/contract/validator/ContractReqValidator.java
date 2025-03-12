package com.guudint.clickargo.clictruck.admin.contract.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.guudint.clickargo.clictruck.admin.contract.dao.CkCtContractReqDao;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractReq;
import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtContractReq;
import com.guudint.clickargo.common.model.ValidationError;
import com.guudint.clickargo.job.service.IJobValidate;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.master.dto.MstCurrency;

@Component
public class ContractReqValidator implements IJobValidate<CkCtContractReq> {

	private static Logger LOG = Logger.getLogger(ContractReqValidator.class);
	private static final String KEY_CONTRACT_MONTHS_INTERVAL = "CLICTRUCK_CONTRACT_MONTHS_INTERVAL";

	@Autowired
	private CkCtContractReqDao contractReqDao;

	@Autowired
	@Qualifier("coreSysparamDao")
	private GenericDao<TCoreSysparam, String> coreSysparamDao;

	@Override
	public List<ValidationError> validateCreate(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {

		if (dto == null)
			throw new ParameterException("param dto null");

		List<ValidationError> invalidList = new ArrayList<>();

		invalidList.addAll(uniqueValidation(dto));
		invalidList.addAll(mandatoryFieldValidation(dto, false));
		
		return invalidList;
	}

	@Override
	public List<ValidationError> validateUpdate(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		if (dto == null)
			throw new ParameterException("param dto null");

		List<ValidationError> invalidList = new ArrayList<>();

		invalidList.addAll(uniqueValidation(dto));
		invalidList.addAll(mandatoryFieldValidation(dto, dto.getAction() == null ? false : true));
		return invalidList;
	}

	@Override
	public List<ValidationError> validateSubmit(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateReject(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateCancel(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateDelete(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateConfirm(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePay(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validatePaid(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ValidationError> validateComplete(CkCtContractReq dto, Principal principal)
			throws ParameterException, ProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ValidationError> mandatoryFieldValidation(CkCtContractReq dto, boolean isSubmit) {

		List<ValidationError> invalidList = new ArrayList<>();

		if (StringUtils.isBlank(dto.getCrName()))
			invalidList.add(new ValidationError("crName", "crName", "Contract Name cannot be empty"));

		// check if the cr name does not exist yet
		

		if (isSubmit) {
			MstCurrency curr = Optional.ofNullable(dto.getTMstCurrency()).orElse(null);
			/*
			if (curr == null || (curr != null && StringUtils.isBlank(curr.getCcyCode()))) {
				invalidList.add(
						new ValidationError("TMstCurrency.ccyCode", "TMstCurrency.ccyCode", "Currency is required"));
			}
			*/

			if (dto.getCrDtStart() == null) {
				invalidList.add(new ValidationError("crDtStart", "crDtStart", "Start Date cannot be empty"));
			}
			if (dto.getCrDtEnd() == null) {
				invalidList.add(new ValidationError("crDtEnd", "crDtEnd", "End Date cannot be empty"));
			}

			if (dto.getCrDtStart() != null && dto.getCrDtEnd() != null && dto.getCrDtStart().after(dto.getCrDtEnd())) {
				invalidList.add(new ValidationError("crDtStart", "crDtStart", "Start Date cannot be after End Date"));
			}

			// check that start date should be 6 months before end date
			if (dto.getCrDtStart() != null && dto.getCrDtEnd() != null) {
				Calendar cStart = Calendar.getInstance();
				cStart.setTime(dto.getCrDtStart());
				Integer monthsInterval = Integer.valueOf(getSysParam(KEY_CONTRACT_MONTHS_INTERVAL, "6"));
				cStart.add(Calendar.MONTH, monthsInterval);
				// if end date is before the cStart + 6 months, then throw validation error
				if (dto.getCrDtEnd().before(cStart.getTime())) {
					invalidList.add(new ValidationError("crDtEnd", "crDtEnd",
							"End date should be " + monthsInterval + " months after the start date."));
				}
			}

			CoreAccn toAccn = Optional.ofNullable(dto.getTCoreAccnByCrTo()).orElse(new CoreAccn());
			if (StringUtils.isBlank(toAccn.getAccnId())) {
				invalidList.add(new ValidationError("TCoreAccnByCrTo.accnId", "TCoreAccnByCrTo.accnId",
						"Truck Operator is required"));
			}

			CoreAccn coFfAccn = Optional.ofNullable(dto.getTCoreAccnByCrCoFf()).orElse(new CoreAccn());
			if (StringUtils.isBlank(coFfAccn.getAccnId())) {
				invalidList.add(new ValidationError("TCoreAccnByCrCoFf.accnId", "TCoreAccnByCrCoFf.accnId",
						"CO/FF is required"));
			}
			/*
			Integer payTermsTo = dto.getCrPaytermTo();
			if (payTermsTo == null || payTermsTo < 1)
				invalidList.add(new ValidationError("crPaytermTo", "crPaytermTo", "TO Payment Terms required"));

			Integer payTermsCoFf = dto.getCrPaytermCoFf();
			if (payTermsCoFf == null || payTermsCoFf < 1)
				invalidList.add(new ValidationError("crPaytermCoFf", "crPaytermCoFf", "CO/FF Payment Terms required"));

			CkCtContractCharge toCharge = Optional.ofNullable(dto.getTCkCtContractChargeByCrChargeTo())
					.orElse(new CkCtContractCharge());
			if (toCharge.getConcPltfeeAmt() == null || toCharge.getConcPltfeeAmt().compareTo(BigDecimal.ZERO) < 1) {
				invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeTo.concPltfeeAmt",
						"TCkCtContractChargeByCrChargeTo.concPltfeeAmt", "Platform Fee TO cannot be empty"));
			}

			if (toCharge.getConcPltfeeType() == null) {
				invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeTo.concPltfeeType",
						"TCkCtContractChargeByCrChargeTo.concPltfeeType", "Required"));
			}

			if (dto.isAdditionalTaxTo()) {
				if (toCharge.getConcAddtaxAmt() == null || toCharge.getConcAddtaxAmt().compareTo(BigDecimal.ZERO) < 1)
					invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeTo.concAddtaxAmt",
							"TCkCtContractChargeByCrChargeTo.concAddtaxAmt",
							"Additional Tax Amount TO cannot be empty"));

				if (toCharge.getConcAddtaxType() == null)
					invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeTo.concAddtaxType",
							"TCkCtContractChargeByCrChargeTo.concAddtaxType", "Required"));

			}

			if (dto.isWitholdTaxTo()) {
				if (toCharge.getConcWhtaxAmt() == null || toCharge.getConcWhtaxAmt().compareTo(BigDecimal.ZERO) < 1)
					invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeTo.concWhtaxAmt",
							"TCkCtContractChargeByCrChargeTo.concWhtaxAmt",
							"Withholding Tax Amount TO cannot be empty"));
				if (toCharge.getConcWhtaxType() == null)
					invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeTo.concWhtaxType",
							"TCkCtContractChargeByCrChargeTo.concWhtaxType", "Required"));
			}

			CkCtContractCharge coFfCharge = Optional.ofNullable(dto.getTCkCtContractChargeByCrChargeCoFf())
					.orElse(new CkCtContractCharge());
			if (coFfCharge.getConcPltfeeAmt() == null || coFfCharge.getConcPltfeeAmt().compareTo(BigDecimal.ZERO) < 1) {
				invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeCoFf.concPltfeeAmt",
						"TCkCtContractChargeByCrChargeCoFf.concPltfeeAmt", "Platform Fee CO/FF cannot be empty"));
			}

			if (coFfCharge.getConcPltfeeType() == null) {
				invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeCoFf.concPltfeeType",
						"TCkCtContractChargeByCrChargeCoFf.concPltfeeType", "Required"));
			}

			if (dto.isAdditionalTaxCoFf()) {
				if (coFfCharge.getConcAddtaxAmt() == null
						|| coFfCharge.getConcAddtaxAmt().compareTo(BigDecimal.ZERO) < 1)
					invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeCoFf.concAddtaxAmt",
							"TCkCtContractChargeByCrChargeCoFf.concAddtaxAmt",
							"Additional Tax Amount CO/FF cannot be empty"));

				if (coFfCharge.getConcAddtaxType() == null)
					invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeCoFf.concAddtaxType",
							"TCkCtContractChargeByCrChargeCoFf.concAddtaxType", "Required"));
			}

			if (dto.isWitholdTaxCoFf()) {
				if (coFfCharge.getConcWhtaxAmt() == null || coFfCharge.getConcWhtaxAmt().compareTo(BigDecimal.ZERO) < 1)
					invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeCoFf.concWhtaxAmt",
							"TCkCtContractChargeByCrChargeCoFf.concWhtaxAmt",
							"Withholding Tax Amount CO/FF cannot be empty"));

				if (coFfCharge.getConcWhtaxType() == null)
					invalidList.add(new ValidationError("TCkCtContractChargeByCrChargeCoFf.concWhtaxType",
							"TCkCtContractChargeByCrChargeCoFf.concWhtaxType", "Required"));
			}

			if (StringUtils.isBlank(dto.getCrCommentRequestor())) {
				invalidList.add(new ValidationError("crCommentRequestor", "crCommentRequestor", "Required"));
			}
			*/
		}

		return invalidList;
	}

	private List<ValidationError> uniqueValidation(CkCtContractReq dto) {
		List<ValidationError> invalidList = new ArrayList<>();
		try {
			Optional<TCkCtContractReq> crEntity = contractReqDao.findByName(dto.getCrName());
			if (crEntity.isPresent() && !crEntity.get().getCrName().equals(dto.getCrName())) {
				invalidList.add(new ValidationError("", "crName", "Contract Request Name already exists"));
			}
		} catch (Exception e) {
			LOG.error(e);
		}
		return invalidList;
	}
	

	private String getSysParam(String key, String defValue) {
		LOG.debug("getSysParam");

		try {
			TCoreSysparam sysParam = coreSysparamDao.find(key);
			if (sysParam == null)
				return defValue;

			return sysParam.getSysVal();
		} catch (Exception ex) {
			LOG.error("getSysParam", ex);
		}

		return defValue;

	}
}
