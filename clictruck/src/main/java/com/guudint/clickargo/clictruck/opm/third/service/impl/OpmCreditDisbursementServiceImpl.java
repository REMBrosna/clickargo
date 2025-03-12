package com.guudint.clickargo.clictruck.opm.third.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.guudint.clickargo.clictruck.finacing.dto.JobPaymentStates;
import com.guudint.clickargo.clictruck.finacing.service.IPlatformInvoiceService;
import com.guudint.clickargo.clictruck.opm.OpmConstants;
import com.guudint.clickargo.clictruck.opm.OpmConstants.Opm_Validation;
import com.guudint.clickargo.clictruck.opm.OpmException;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditDisbursementReq;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtPayment;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPayment;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtPlatformInvoice;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.TCkAccnOpm;
import com.guudint.clickargo.master.enums.AccountTypes;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.master.enums.JournalTxnType;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.model.TCkMstPaymentType;
import com.guudint.clickargo.master.model.TCkMstServiceType;
import com.guudint.clickargo.payment.enums.PaymentTypes;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.guudint.clickargo.payment.service.impl.CkPaymentTxnService;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.master.model.TMstAccnType;
import com.vcc.camelone.master.model.TMstCurrency;

@Service
public class OpmCreditDisbursementServiceImpl extends OpmProcessService<OpmCreditDisbursementReq> {

	private static Logger log = Logger.getLogger(OpmCreditDisbursementServiceImpl.class);

	@Override
	public List<OpmCreditDisbursementReq> parseFile(String disburesementFile, Map<Integer, OpmException> errsMap)
			throws OpmException {

		List<OpmCreditDisbursementReq> crList = new ArrayList<OpmCreditDisbursementReq>();

		try (FileInputStream file = new FileInputStream(new File(disburesementFile));
				Workbook workbook = new XSSFWorkbook(file);) {

			formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

			Sheet sheet = workbook.getSheetAt(0);
			// DataFormatter dataFormatter = new DataFormatter();
			for (int rowId = 1; rowId < sheet.getPhysicalNumberOfRows(); rowId++) {

				try {
					Row row = sheet.getRow(rowId);
					OpmCreditDisbursementReq cr = new OpmCreditDisbursementReq(rowId);

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
					long loanApproved = super.getLongFromCell(cell, "loan_approved");
					cr.setLoan_approved(loanApproved);

					// 3
					cellId++;
					cell = row.getCell(cellId);
					long provisionFeeAmt = super.getLongFromCell(cell, "provision_fee_amt");
					cr.setProvision_fee_amt(provisionFeeAmt);

					// 4
					cellId++;
					cell = row.getCell(cellId);
					long disbursementAmt = super.getLongFromCell(cell, "disbursement_amt");
					cr.setDisbursement_amt(disbursementAmt);

					// 5
					cellId++;
					cell = row.getCell(cellId);
					Date expiryDate = null;
					try {
						expiryDate = super.getDateFromCell(cell);
						if (null == expiryDate) {
							throw new OpmException();
						}
					} catch (Exception e) {
						throw new OpmException(Opm_Validation.NOT_DATE, "Expiry Date");
					}
					cr.setLoan_due_date(expiryDate);

					// 6
					cellId++;
					cell = row.getCell(cellId);
					String action = super.getStringFromCell(cell);
					if (StringUtils.isBlank(action)) {

						throw new OpmException(Opm_Validation.NULL, "action");

					} else if (!OpmConstants.OPM_ACTION_DISBURSE.equalsIgnoreCase(action)) {

						throw new OpmException(Opm_Validation.ACTION_SHOULD_BE, OpmConstants.OPM_ACTION_DISBURSE);
					}
					cr.setAction(action);

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
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}
		return crList;
	}

	@Override
	protected int getErrorCodeColumn() {
		return 7;
	}

	@Override
	public void validateReq(OpmCreditDisbursementReq req, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		try {
			super.validateReq(req, financer, errMap);

			// Check if the t_ck_accn_opm is found
			TCoreAccn accn = super.findTCoreAccn(req.getTax_no());
			TCkAccnOpm accnOpm = ckCtAccnOpmDao.find(accn.getAccnId());
			if (accnOpm == null) {
				throw new OpmException(Opm_Validation.NO_APPROVED_CREDIT, "Approved OPM Credit");
			} else {
				if (accnOpm.getCaoStatus() != RecordStatus.ACTIVE.getCode())
					throw new OpmException(Opm_Validation.EXPIRED_SUSPENDED_TERMINATED_CREDIT, "OPM Credit");
			}

			// 2
			if (req.getProvision_fee_amt() < 0) {
				throw new OpmException(Opm_Validation.LESS_THAN_ZERO, "loan_approved");
			}
			if (req.getLoan_approved() < 0) {
				throw new OpmException(Opm_Validation.LESS_THAN_ZERO, "provision_fee_amt");
			}
			if (req.getDisbursement_amt() < 0) {
				throw new OpmException(Opm_Validation.LESS_THAN_ZERO, "disbursement_amt");
			}

			Date now = new Date();

			// 3
			TCkJobTruck jobTruckEntity = ckJobTruckDao.find(req.getReference_number());
			if (null == jobTruckEntity) {
				// do something.
				throw new OpmException(Opm_Validation.NOT_FIND, "reference_number");

			} else if (JobPaymentStates.PAID.name().equalsIgnoreCase(jobTruckEntity.getJobOutPaymentState())) {
				// already paid
				// duplicate put excel to SFTP,
				throw new OpmException(Opm_Validation.DISBURSEMENT_REFERENCE_NUMBER_ALREADY, req.getReference_number());
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

			// 5 If loan_approved is not equal to job's total trip charge + reimbursement
			// (if applicable)

			if (req.getLoan_approved() != jobTruckEntity.computeTotalAmt().longValue()) {
				throw new OpmException(Opm_Validation.AMOUNT_NOT_CORRECT, "loan_approved");
			}

			// 6
			BigDecimal provisionFeeAmt = opmUtilizeService.computePlatFormFeeFromBank(jobTruckEntity);
			if (req.getProvision_fee_amt() != provisionFeeAmt.longValue()) {
				throw new OpmException(Opm_Validation.AMOUNT_NOT_CORRECT, "provision_fee_amt");
			}

			// 7 If disbursement_amt is not equal to the loan_approved - provisionFeeAmt
			BigDecimal disbursementAmt = jobTruckEntity.computeTotalAmt().subtract(provisionFeeAmt);
			if (req.getDisbursement_amt() != disbursementAmt.longValue()) {
				throw new OpmException(Opm_Validation.AMOUNT_NOT_CORRECT, "disbursementAmt");
			}

			// 8 If loan_due_date < today
			if (req.getLoan_due_date().before(now)) {
				throw new OpmException(Opm_Validation.LESS_TODAY, "loan_due_date");
			}

		} catch (OpmException e) {
			throw e;
		} catch (Exception e) {
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}

	}

	@Override
	@Transactional
	public void process(OpmCreditDisbursementReq disbursementReq, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		try {
			this.validateReq(disbursementReq, financer, errMap);

			// 1: find account by tax_no
			TCoreAccn accn = this.findTCoreAccn(disbursementReq.getTax_no());

			// 2: check T_CK_ACCN
			this.findTckAccn(accn.getAccnId(), disbursementReq.getTax_no(), financer);

			// update T_CK_CREDIT_FINANCE_JOURNAL and T_CK_CREDIT_FINANCE_SUMMARY
			CkJobTruck jobTruck = ckJobTruckService.findById(disbursementReq.getReference_number());

			// check amount
			// BigDecimal totalAmt = jobTruck.computeTotalAmt();
			//

			opmService.convertResever2utilizeOpmJobTruckCredit(JournalTxnType.JOB_PAYMENT_APPROVE, jobTruck);

			// TO Accn
			this.processJobAfterDisbursement(Arrays.asList(jobTruck.getJobId()),
					jobTruck.getTCoreAccnByJobPartyTo().toEntity(new TCoreAccn()), new Date());

		} catch (OpmException e) {
			throw e;
		} catch (Exception e) {
			log.error("Fail to process OPM Credit Approve. ", e);
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}
	}

	@Override
	public String getJobTruckIdList(List<OpmCreditDisbursementReq> reqList) throws Exception {

		String jobIdList = reqList.stream().map(req -> req.getReference_number()).distinct()
				.collect(Collectors.joining(","));

		return jobIdList;
	}

	/**
	 * Process job after paid to TO
	 * 
	 * @throws Exception
	 */
	private void processJobAfterDisbursement(List<String> jobIds, TCoreAccn toAccn, Date paidDate) throws Exception {
		//
		TCkPaymentTxn paymentTxn = this.createPaymentTxn4OPM(jobIds, toAccn, paidDate);
		//
		toPayoutService.updateFundsTransferResultSuccessfulByPaymentTxn(paymentTxn, paidDate);
	}

	private TCkPaymentTxn createPaymentTxn4OPM(List<String> jobIds, TCoreAccn toAccn, Date payDate) throws Exception {

		Date now = new Date();

		TMstCurrency currency = new TMstCurrency(Currencies.IDR.getCode(), "", ' ');

		TCkPaymentTxn txn = new TCkPaymentTxn();
		txn.setPtxId(CkUtil.generateIdSynch(CkPaymentTxnService.PREFIX_DO_TXN));
		txn.setPtxSvcRef(StringUtils.join(jobIds, ","));

		txn.setTMstCurrency(currency);
		txn.setTCkMstPaymentType(new TCkMstPaymentType(PaymentTypes.OPM.getId()));
		txn.setTCkMstServiceType(new TCkMstServiceType(ServiceTypes.CLICTRUCK.getId()));

		// payer is the one who executes the pay
		txn.setTCoreAccnByPtxPayer(new TCoreAccn("GLI", null, ' ', null));
		txn.setTCoreAccnByPtxPayee(toAccn);

		txn.setPtxDtCreate(now);
		txn.setPtxPaymentState(JobPaymentStates.PAID.name());

		txn.setPtxMerchantBank("-");
		txn.setPtxPayeeBankAccn("-");
		txn.setPtxPayeeBankAccnName("-");

		txn.setPtxStatus(RecordStatus.ACTIVE.getCode());
		txn.setPtxUidCreate("sys");
		txn.setPtxDtLupd(now);
		txn.setPtxUidLupd("sys");
		txn.setPtxDtDue(payDate);

		ckPaymentTxnDao.add(txn);

		BigDecimal ptxAmt = BigDecimal.ZERO;

		for (String jobId : jobIds) {
			TCkCtPayment ctPymnt = new TCkCtPayment(CkUtil.generateIdSynch(CkCtPayment.PREFIX_SP_CT_PYMNT));

			List<TCkCtPlatformInvoice> invList = ckCtPlatformInvoiceDao.findByJobIdAndInvTo(jobId,
					AccountTypes.ACC_TYPE_TO.name());

			TCkCtPlatformInvoice inv = invList.get(0);
			

			ctPymnt.setTCkPaymentTxn(txn);

			ctPymnt.setTMstCurrency(currency);
			ctPymnt.setCtpRef(inv.getInvId());
			ctPymnt.setCtpJob(jobId);
			ctPymnt.setCtpItem(String.format(IPlatformInvoiceService.PF_ITEM_DESC, jobId));
			ctPymnt.setCtpAttach(inv.getInvLoc());
			ctPymnt.setCtpQty((short) 1);
			ctPymnt.setCtpAmount(inv.getInvTotal());

			ctPymnt.setCtpStatus(RecordStatus.ACTIVE.getCode());
			ctPymnt.setCtpState(JobPaymentStates.PAID.getAltCode());
			ctPymnt.setCtpDtCreate(now);
			ctPymnt.setCtpUidCreate("sys");

			ckCtPaymentDao.add(ctPymnt);

			ptxAmt = ptxAmt.add(ctPymnt.getCtpAmount());
		}

		txn.setPtxAmount(ptxAmt);

		return txn;
	}

}
