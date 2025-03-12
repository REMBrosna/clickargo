package com.guudint.clickargo.clictruck.opm.third.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.clictruck.opm.model.TCkOpmSummary;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditApprovalReq;
import com.guudint.clickargo.clictruck.util.ExcelPOIUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.TCkAccn;
import com.guudint.clickargo.common.model.TCkAccnOpm;
import com.guudint.clickargo.master.enums.CreditState;
import com.guudint.clickargo.master.enums.Currencies;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.model.TCkMstCreditState;
import com.guudint.clickargo.master.model.TCkMstServiceType;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.master.model.TMstCurrency;

@Service
public class OpmCreditApproveServiceImpl extends OpmProcessService<OpmCreditApprovalReq> {

	private static Logger log = Logger.getLogger(OpmCreditApproveServiceImpl.class);

	@Override
	public List<OpmCreditApprovalReq> parseFile(String approveFile, Map<Integer, OpmException> errsMap)
			throws OpmException {

		List<OpmCreditApprovalReq> crList = new ArrayList<OpmCreditApprovalReq>();

		try (FileInputStream file = new FileInputStream(new File(approveFile));
				Workbook workbook = new XSSFWorkbook(file);) {

			formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

			Sheet sheet = workbook.getSheetAt(0);

			// DataFormatter dataFormatter = new DataFormatter();
			for (int rowId = 1; rowId < sheet.getPhysicalNumberOfRows(); rowId++) {

				try {
					Row row = sheet.getRow(rowId);

					if (ExcelPOIUtil.isRowEmpty(row)) {
						continue;
					}
					OpmCreditApprovalReq cr = new OpmCreditApprovalReq(rowId);

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
					long facilitLimit = super.getLongFromCell(cell, "Facilit Limit");
					cr.setFacility_limit(facilitLimit);

					// 2
					cellId++;
					cell = row.getCell(cellId);
					Date approvalDate = null;
					try {
						approvalDate = super.getDateFromCell(cell);
						if (null == approvalDate) {
							throw new OpmException();
						}
					} catch (Exception e) {
						throw new OpmException(Opm_Validation.NOT_DATE, "Approval Date");
					}
					cr.setApproval_date(approvalDate);

					// 3
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
					cr.setExpiry_date(expiryDate);

					crList.add(cr);
				} catch (OpmException e) {
					log.error("", e);
					errsMap.put(rowId, e);
					// throw e;
				} catch (Exception e) {
					log.error("", e);
					// throw new OpmException("-1", e.getMessage());
					errsMap.put(rowId, new OpmException(e));
				}
			}
		} catch (Exception e) {
			log.error("", e);
			throw new OpmException(e);
		}
		return crList;
	}

	@Override
	protected int getErrorCodeColumn() {
		return 4;
	}

	@Override
	public void validateReq(OpmCreditApprovalReq approvalReq, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		try {

			super.validateReq(approvalReq, financer, errMap);

			// reject if facility limit <= 0
			if (approvalReq.getFacility_limit() <= 0) {
				throw new OpmException(Opm_Validation.LESS_THAN_ZERO, "Facility Limit");
			}

			// reject if facility limit > MAX set in sysparam
			if (approvalReq.getFacility_limit() > getOpmMaxLimit())
				throw new OpmException(Opm_Validation.GREATER_THAN_MAX, "Facility Limit");

			// reject if expiry date < approval date
			if (approvalReq.getExpiry_date().before(approvalReq.getApproval_date())) {
				throw new OpmException(Opm_Validation.EXPIRY_LESS_APPROVAL_DATE, "Expiry Date");
			}

		} catch (OpmException e) {
			throw e;
		} catch (Exception e) {
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}

	}

//	public static void main(String[] args) {
//		Calendar cal = Calendar.getInstance();
//		Date now = cal.getTime();
//
//		cal.add(Calendar.DATE, -2);
//		Date previous = cal.getTime();
//
//		System.out.println(now + " - " + previous);
//		System.out.println("Now after Previous: " + now.after(previous));
//		System.out.print("Now before Prevous: " + now.before(previous));
//	}

	@Override
	@Transactional
	public void process(OpmCreditApprovalReq approvalReq, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		try {

			this.validateReq(approvalReq, financer, errMap);

			Date now = Calendar.getInstance().getTime();

			// 1: find account by tax_no
			TCoreAccn accn = super.findTCoreAccn(approvalReq.getTax_no());

			// 2: check T_CK_ACCN
			TCkAccn ckAccn = super.findTckAccn(accn.getAccnId(), approvalReq.getTax_no(), financer);

			// 3: Check if there is an existing t_ck_accn_opm
			TCkAccnOpm accnOpm = ckCtAccnOpmDao.find(accn.getAccnId());

			if (null == accnOpm) {
				accnOpm = new TCkAccnOpm(accn.getAccnId());
				accnOpm.setTCoreAccn(accn);

				// 3.1 Check if the approval date > today
				if (approvalReq.getApproval_date().after(now)) {
					// actually DRAFT
					accnOpm.setCaoStatus(RecordStatus.DEACTIVATE.getCode());
				} else {
					accnOpm.setCaoStatus(RecordStatus.ACTIVE.getCode());
				}

				accnOpm.setCaoType(ckAccn.getCaccnFinancingType()); // OC or OT
				accnOpm.setCaoFinancer(financer);
				accnOpm.setCaoDtCreate(new Date());
				accnOpm.setCaoUidCreate("sys");
			} else {
				// 3.2
				if (RecordStatus.ACTIVE.getCode() != accnOpm.getCaoStatus()) {
					accnOpm.setCaoDtLupd(new Date());
					accnOpm.setCaoUidLupd("sys");
				} else {
					// Can not approve twice
					throw new OpmException(Opm_Validation.CREDIT_APPROVED_ALREADY);
				}
			}

			accnOpm.setCaoDtApprove(approvalReq.getApproval_date());
			accnOpm.setCaoCreditLimit(BigDecimal.valueOf(approvalReq.getFacility_limit()));
			accnOpm.setCaoDtExpiry(approvalReq.getExpiry_date());

			accnOpm.setCaoDtClose(null);
			accnOpm.setCaoDtSuspend(null);
			accnOpm.setCaoDtUnsuspend(null);

			ckCtAccnOpmDao.saveOrUpdate(accnOpm);
			opmUpdateAccnStatusService.updateOpmAccnStatusByAccn(accnOpm);

			// 4: insert or update T_CK_OPM table
			TCkOpm ckOpm = ckOpmDao.findByAccnIdStatus(accn.getAccnId(),
					Arrays.asList(RecordStatus.ACTIVE.getCode(), RecordStatus.DEACTIVATE.getCode()));
			if (null == ckOpm) {
				ckOpm = new TCkOpm(CkUtil.generateId(TCkOpm.PREFIX_ID));

				ckOpm.setTCoreAccn(accn);
				ckOpm.setTCkMstCreditState(new TCkMstCreditState(CreditState.APPROVED.name(), null));
				ckOpm.setTCkMstServiceType(new TCkMstServiceType(ServiceTypes.CLICTRUCK.name()));
				ckOpm.setTMstCurrency(new TMstCurrency(Currencies.IDR.getCode(), "", ' '));

				ckOpm.setOpmFinancer(financer);

				// based the status of the tckopm to the accnopm.
				// so it won't spill if the user submit or accept
				// the scheduler should update tckopm as well
				ckOpm.setOpmStatus(accnOpm.getCaoStatus());
				ckOpm.setOpmDtCreate(new Date());
				ckOpm.setOpmUidCreate("sys");
			} else {
				ckOpm.setOpmStatus(accnOpm.getCaoStatus());
				ckOpm.setOpmDtLupd(new Date());
				ckOpm.setOpmUidLupd("sys");

			}
			ckOpm.setOpmDtStart(approvalReq.getApproval_date());
			ckOpm.setOpmDtApprove(approvalReq.getApproval_date());
			ckOpm.setOpmAmt(BigDecimal.valueOf(approvalReq.getFacility_limit()));
			ckOpm.setOpmDtEnd(approvalReq.getExpiry_date());

			ckOpmDao.saveOrUpdate(ckOpm);

			// 5: insert or update T_CK_OPM_SUMMARY table
			TCkOpmSummary opmSummary = ckOpmSummaryDao.findByAccnId(accn.getAccnId());
			if (null == opmSummary) {
				opmSummary = new TCkOpmSummary(CkUtil.generateId(TCkOpmSummary.PREFIX_ID));

				opmSummary.setTCoreAccn(accn);
				opmSummary.setTCkMstServiceType(new TCkMstServiceType(ServiceTypes.CLICTRUCK.name()));
				opmSummary.setTMstCurrency(new TMstCurrency(Currencies.IDR.getCode(), "", ' '));

				opmSummary.setOpmsReserve(BigDecimal.ZERO);
				opmSummary.setOpmsUtilized(BigDecimal.ZERO);
				opmSummary.setOpmsBalance(BigDecimal.valueOf(approvalReq.getFacility_limit()));

				opmSummary.setOpmsStatus(ckOpm.getOpmStatus());
				opmSummary.setOpmsDtCreate(new Date());
				opmSummary.setOpmsUidCreate("sys");
			} else {
				opmSummary.setOpmsStatus(ckOpm.getOpmStatus());
				opmSummary.setOpmsDtLupd(new Date());
				opmSummary.setOpmsUidLupd("sys");
			}

			opmSummary.setOpmsAmt(BigDecimal.valueOf(approvalReq.getFacility_limit()));

			ckOpmSummaryDao.saveOrUpdate(opmSummary);

		} catch (OpmException e) {
			throw e;
		} catch (Exception e) {
			log.error("Fail to process OPM Credit Approve. ", e);
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}
	}
}
