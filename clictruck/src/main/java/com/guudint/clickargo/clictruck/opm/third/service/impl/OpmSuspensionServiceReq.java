package com.guudint.clickargo.clictruck.opm.third.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
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

import com.guudint.clickargo.clictruck.opm.OpmException;
import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.clictruck.opm.OpmConstants.Opm_Validation;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditSuspensionReq;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.TCkAccnOpm;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.exception.ParameterException;

@Service
public class OpmSuspensionServiceReq extends OpmProcessService<OpmCreditSuspensionReq> {

	private static Logger log = Logger.getLogger(OpmSuspensionServiceReq.class);

	@Override
	public List<OpmCreditSuspensionReq> parseFile(String suspensionFile, Map<Integer, OpmException> errsMap)
			throws OpmException {

		List<OpmCreditSuspensionReq> crList = new ArrayList<OpmCreditSuspensionReq>();

		try (FileInputStream file = new FileInputStream(new File(suspensionFile));
				Workbook workbook = new XSSFWorkbook(file);) {

			formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = workbook.getSheetAt(0);

			// DataFormatter dataFormatter = new DataFormatter();
			for (int rowId = 1; rowId < sheet.getPhysicalNumberOfRows(); rowId++) {

				try {
					Row row = sheet.getRow(rowId);

					OpmCreditSuspensionReq cr = new OpmCreditSuspensionReq(rowId);

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
					long facilitLimit = super.getLongFromCell(cell, 0, "Facilit Limit");
					cr.setFacility_limit(facilitLimit);

					// 2
					cellId++;
					cell = row.getCell(cellId);
					Date suspendDate = null;
					try {
						suspendDate = super.getDateFromCell(cell);
						if (null == suspendDate) {
							throw new OpmException();
						}
					} catch (Exception e) {
						throw new OpmException(Opm_Validation.NOT_DATE, "Suspend Date");
					}
					cr.setSuspend_date(suspendDate);

					crList.add(cr);
				} catch (Exception e) {
					log.error("", e);
					// throw new OpmException("-1", e.getMessage());
					errsMap.put(rowId, new OpmException("-1", e.getMessage()));
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
		return 3;
	}

	@Override
	public void validateReq(OpmCreditSuspensionReq req, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		super.validateReq(req, financer, errMap);

	}

	@Override
	@Transactional
	public void process(OpmCreditSuspensionReq suspensionReq, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		try {
			super.validateReq(suspensionReq, financer, errMap);

			if (null == suspensionReq)
				throw new ParameterException("suspensionReq is null");

			if (StringUtils.isBlank(suspensionReq.getTax_no()))
				throw new ParameterException("suspensionReq.taxNo is null");

			// insert or update T_CK_CT_CONTRACT_FINANCE
			// 1: find account by tax_no
			TCoreAccn accn = coreAccnDao.findAccnByUen(suspensionReq.getTax_no());
			if (null == accn) {
				throw new OpmException(Opm_Validation.NOT_FIND, suspensionReq.getTax_no());
			}

			// 2: update TCkAccnOpm
			TCkAccnOpm accnOpm = ckCtAccnOpmDao.find(accn.getAccnId());

			if (accnOpm == null || (accnOpm != null && accnOpm.getCaoStatus() != RecordStatus.ACTIVE.getCode())) {
				throw new OpmException(Opm_Validation.NOT_FIND, "Approved OPM account");
			}

			if (suspensionReq.getFacility_limit() > 0
					&& suspensionReq.getFacility_limit() != accnOpm.getCaoCreditLimit().longValue()) {

				throw new OpmException(Opm_Validation.AMOUNT_NOT_CORRECT, "facility_limit");
			}

			// if suspend_date <= today, updat status immediately
			if (suspensionReq.getSuspend_date().compareTo(new Date()) <= 0) {
				accnOpm.setCaoStatus(RecordStatus.SUSPENDED.getCode());
				super.insertAccnAuditLog(accn.getAccnId(), "Suspended Account due to OPM");
			}

			accnOpm.setCaoDtSuspend(suspensionReq.getSuspend_date());

			accnOpm.setCaoDtLupd(new Date());
			accnOpm.setCaoUidLupd("sys");

			ckCtAccnOpmDao.saveOrUpdate(accnOpm);
			
			// TCkAccnOpm updatedAccnOpm = opmUpdateAccnStatusService.updateOpmAccnStatusByAccn(accnOpm);

			if (accnOpm.getCaoStatus() == RecordStatus.SUSPENDED.getCode()) {
				// Retrieve the t_ck_opm
				TCkOpm ckOpm = ckOpmDao.findByAccnId(accn.getAccnId());
				if (ckOpm == null) {
					throw new Exception(
							"Fail to find OPM credit: " + accn.getAccnId() + " " + suspensionReq.getTax_no());
				}
				// 3. Uupdate the status to suspended
				ckOpm.setOpmDtLupd(new Date());
				ckOpm.setOpmUidLupd("sys");
				ckOpm.setOpmStatus(RecordStatus.SUSPENDED.getCode());
				ckOpmDao.saveOrUpdate(ckOpm);
			}

		} catch (OpmException e) {
			throw e;
		} catch (Exception e) {
			log.error("Fail to process OPM Credit Approve. ", e);
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}
	}

}
