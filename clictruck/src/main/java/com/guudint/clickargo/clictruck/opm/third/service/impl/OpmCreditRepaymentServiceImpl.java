package com.guudint.clickargo.clictruck.opm.third.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.opm.OpmConstants.Opm_Validation;
import com.guudint.clickargo.clictruck.opm.OpmException;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmJournal;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditRepaymentReq;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.TCkAccnOpm;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.master.model.TMstAccnType;

@Service
public class OpmCreditRepaymentServiceImpl extends OpmProcessService<OpmCreditRepaymentReq> {

	private static Logger log = Logger.getLogger(OpmCreditRepaymentServiceImpl.class);

	@Override
	public List<OpmCreditRepaymentReq> parseFile(String repaymentFile, Map<Integer, OpmException> errsMap)
			throws OpmException {

		List<OpmCreditRepaymentReq> crList = new ArrayList<OpmCreditRepaymentReq>();

		try (FileInputStream file = new FileInputStream(new File(repaymentFile));
				Workbook workbook = new XSSFWorkbook(file);) {

			formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

			Sheet sheet = workbook.getSheetAt(0);
			// DataFormatter dataFormatter = new DataFormatter();
			for (int rowId = 1; rowId < sheet.getPhysicalNumberOfRows(); rowId++) {

				try {
					Row row = sheet.getRow(rowId);
					OpmCreditRepaymentReq cr = new OpmCreditRepaymentReq(rowId);

					int cellId = 0;

					// 0
					Cell cell = row.getCell(cellId);
					String taxNo = super.getStringFromCell(cell);
					if (StringUtils.isBlank(taxNo)) {
						throw new OpmException(Opm_Validation.NULL, "Tax NO");
					}
					cr.setTax_no(taxNo);

					// 1
					cellId++;
					cell = row.getCell(cellId);
					String referenceNum = super.getStringFromCell(cell);
					if (StringUtils.isBlank(referenceNum)) {
						throw new OpmException(Opm_Validation.NULL, "reference_number");
					}
					cr.setReference_number(referenceNum);

					// 2
					cellId++;
					cell = row.getCell(cellId);
					long loanApproved = super.getLongFromCell(cell, "principal_paid");
					cr.setPrincipal_paid(loanApproved);

					// 3
					cellId++;
					cell = row.getCell(cellId);
					long lateFee = super.getLongFromCell(cell, 0, "late_fee");
					cr.setLate_fee(lateFee);

					// 4
					cellId++;
					cell = row.getCell(cellId);
					Date paymentDate = null;
					try {
						paymentDate = super.getDateFromCell(cell);
						if (null == paymentDate) {
							throw new OpmException();
						}
					} catch (Exception e) {
						throw new OpmException(Opm_Validation.NOT_DATE, "payment_date");
					}
					cr.setPayment_date(paymentDate);

					// 5
					cellId++;
					cell = row.getCell(cellId);
					long loadOutstandAmt = super.getLongFromCell(cell, 0, "loan_outstanding_amt");
					cr.setLoan_outstanding_amt(loadOutstandAmt);

					crList.add(cr);
				} catch (OpmException e) {
					log.error("", e);
					errsMap.put(rowId, e);
				} catch (Exception e) {
					log.error("", e);
					errsMap.put(rowId, new OpmException(e));
				}
			}
		} catch (Exception e) {
			log.error("", e);
			throw new OpmException("-1", e.getMessage());
		}
		return crList;
	}

	@Override
	protected int getErrorCodeColumn() {
		return 6;
	}

	@Override
	public void validateReq(OpmCreditRepaymentReq req, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		try {
			super.validateReq(req, financer, errMap);

			// 1.Check if the t_ck_accn_opm is found
			TCoreAccn accn = super.findTCoreAccn(req.getTax_no());
			TCkAccnOpm accnOpm = ckCtAccnOpmDao.find(accn.getAccnId());
			if (accnOpm == null) {
				throw new OpmException(Opm_Validation.NO_APPROVED_CREDIT, "Approved OPM Credit");
			} else {
				if (accnOpm.getCaoStatus() != RecordStatus.ACTIVE.getCode())
					throw new OpmException(Opm_Validation.EXPIRED_SUSPENDED_TERMINATED_CREDIT, "OPM Credit");
			}

			if (req.getPrincipal_paid() < 0) {
				throw new OpmException(Opm_Validation.LESS_THAN_ZERO, "principal_paid");
			}

			if (req.getLate_fee() < 0) {
				throw new OpmException(Opm_Validation.LESS_THAN_ZERO, "late_fee");
			}

			// if loan_oustanding_amt < 0
			if (req.getLoan_outstanding_amt() < 0) {
				throw new OpmException(Opm_Validation.LESS_THAN_ZERO, "loan_outstanding_amt");
			}

			//
			TCkJobTruck jobTruckEntity = ckJobTruckDao.find(req.getReference_number());
			if (null == jobTruckEntity) {
				// do something.
				throw new OpmException(Opm_Validation.NOT_FIND, "reference_number");
			}

			// 4 If reference_no for job not belonging to tax_no. The tax_no should be
			// either of the TO or CO
			TMstAccnType accnType = Optional.ofNullable(accn.getTMstAccnType()).get();
			if ((accnType.getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_CO.name())
					|| accnType.getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_FF.name()))
					&& !jobTruckEntity.getTCoreAccnByJobPartyCoFf().getAccnCoyRegn()
							.equalsIgnoreCase(req.getTax_no())) {
				throw new OpmException(Opm_Validation.REF_NO_INVALID, "reference_number");

			} else if (accnType.getAtypId().equalsIgnoreCase(AccountTypes.ACC_TYPE_TO.name())
					&& !jobTruckEntity.getTCoreAccnByJobPartyTo().getAccnCoyRegn().equalsIgnoreCase(req.getTax_no())) {
				throw new OpmException(Opm_Validation.REF_NO_INVALID, "reference_number");

			}

			// If principal_paid <> job's total trip charge
			if (req.getPrincipal_paid() != jobTruckEntity.computeTotalAmt().longValue()) {
				throw new OpmException(Opm_Validation.AMOUNT_NOT_CORRECT, "principal_paid");
			}

			Date now = new Date();
			// Check if payment_date < today
			if (now.before(req.getPayment_date())) {
				throw new OpmException(Opm_Validation.AFTER_TODAY, "payment_date");
			}

		} catch (OpmException e) {
			throw e;
		} catch (Exception e) {
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}
	}

	@Override
	@Transactional
	public void process(OpmCreditRepaymentReq repaymentReq, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		try {

			this.validateReq(repaymentReq, financer, errMap);

			// 1: find account by tax_no
			TCoreAccn accn = this.findTCoreAccn(repaymentReq.getTax_no());

			// 2: check T_CK_ACCN
			this.findTckAccn(accn.getAccnId(), repaymentReq.getTax_no(), financer);

			// reset Credit limit
			CkJobTruck jobTruck = ckJobTruckService.findById(repaymentReq.getReference_number());

			if (null == jobTruck) {
				// do something.
				throw new OpmException(Opm_Validation.NOT_FIND, "reference_number");
			} else {

				List<TCkOpmJournal> journalPayList = ckOpmJournalDao.findByJobTruckIdAndJournalType(jobTruck.getJobId(),
						JournalTxnType.JOB_PAYMENT);

				if (null != journalPayList && journalPayList.size() > 0) {
					throw new OpmException(Opm_Validation.REPAY_REFERENCE_NUMBER_ALREADY,
							repaymentReq.getReference_number());
				}
			}
			opmService.reverseOpmUtilized(JournalTxnType.JOB_PAYMENT, jobTruck, null);

		} catch (OpmException e) {
			throw e;
		} catch (Exception e) {
			log.error("Fail to process OPM Credit Approve. ", e);
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}
	}

	@Override
	public String getJobTruckIdList(List<OpmCreditRepaymentReq> reqList) throws Exception {

		String jobIdList = reqList.stream().map(req -> req.getReference_number()).distinct()
				.collect(Collectors.joining(","));

		return jobIdList;
	}

}
