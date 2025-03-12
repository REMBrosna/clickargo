package com.guudint.clickargo.clictruck.opm.third.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.guudint.clickargo.clictruck.opm.model.TCkOpm;
import com.guudint.clickargo.clictruck.opm.OpmException;
import com.guudint.clickargo.clictruck.opm.third.dto.OpmCreditTerminationReq;
import com.guudint.clickargo.common.RecordStatus;
import com.guudint.clickargo.common.model.TCkAccnOpm;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.exception.ParameterException;

@Service
public class OpmTerminationServiceImpl extends OpmProcessService<OpmCreditTerminationReq> {

	private static Logger log = Logger.getLogger(OpmTerminationServiceImpl.class);

	@Override
	public List<OpmCreditTerminationReq> parseFile(String terminationFile, Map<Integer, OpmException> errsMap)
			throws OpmException {

		List<OpmCreditTerminationReq> crList = new ArrayList<OpmCreditTerminationReq>();

		try (FileInputStream file = new FileInputStream(new File(terminationFile));
				Workbook workbook = new XSSFWorkbook(file);) {

			formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = workbook.getSheetAt(0);

			// DataFormatter dataFormatter = new DataFormatter();
			for (int rowId = 1; rowId < sheet.getPhysicalNumberOfRows(); rowId++) {

				try {
					Row row = sheet.getRow(rowId);

					OpmCreditTerminationReq cr = new OpmCreditTerminationReq(rowId);

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
					Date closeDate = null;
					try {
						closeDate = super.getDateFromCell(cell);
						if (null == closeDate) {
							throw new OpmException();
						}
					} catch (Exception e) {
						throw new OpmException(Opm_Validation.NOT_DATE, "Close Date");
					}
					cr.setClose_date(closeDate);

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
	public void validateReq(OpmCreditTerminationReq req, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		super.validateReq(req, financer, errMap);

	}

	@Override
	@Transactional
	public void process(OpmCreditTerminationReq terminationReq, String financer, Map<Integer, OpmException> errMap)
			throws OpmException {

		try {
			this.validateReq(terminationReq, financer, errMap);

			if (null == terminationReq)
				throw new ParameterException("terminationReq is null");

			if (StringUtils.isBlank(terminationReq.getTax_no()))
				throw new ParameterException("terminationReq.taxNo is null");

			// 1: find account by tax_no
			TCoreAccn accn = this.findTCoreAccn(terminationReq.getTax_no());
			if (accn == null)
				throw new OpmException(Opm_Validation.NOT_FIND, terminationReq.getTax_no());

			// 2: check T_CK_ACCN
			this.findTckAccn(accn.getAccnId(), terminationReq.getTax_no(), financer);

			// 3: update TCkAccn
			TCkAccnOpm accnOpm = ckCtAccnOpmDao.find(accn.getAccnId());

			if (accnOpm == null) {
				throw new OpmException(Opm_Validation.NOT_FIND, "Approved OPM account");
			} else if (accnOpm != null && accnOpm.getCaoStatus() == RecordStatus.INACTIVE.getCode()) {
				throw new OpmException(Opm_Validation.CREDIT_TERMINATED_ALREADY);
			}

			if (terminationReq.getFacility_limit() > 0
					&& terminationReq.getFacility_limit() != accnOpm.getCaoCreditLimit().longValue()) {

				throw new OpmException(Opm_Validation.AMOUNT_NOT_CORRECT, "facility_limit");
			}

			// close_date <= today, update status immediately
			if (terminationReq.getClose_date().compareTo(new Date()) <= 0) {
				// update status when today after closeDay
				accnOpm.setCaoStatus(RecordStatus.INACTIVE.getCode());
				super.insertAccnAuditLog(accn.getAccnId(), "Terminate Account due to OPM");
			}

			accnOpm.setCaoDtClose(terminationReq.getClose_date());
			accnOpm.setCaoDtLupd(new Date());
			accnOpm.setCaoUidLupd("sys");

			ckCtAccnOpmDao.saveOrUpdate(accnOpm);
			//TCkAccnOpm updatedAccnOpm = opmUpdateAccnStatusService.updateOpmAccnStatusByAccn(accnOpm);

			if (accnOpm.getCaoStatus() == RecordStatus.INACTIVE.getCode()) {
				// 3: update TckOpm
				TCkOpm ckOpm = ckOpmDao.findByAccnIdStatus(accn.getAccnId(), Arrays.asList(RecordStatus.ACTIVE.getCode()));
				if (ckOpm == null) {
					// if no active, search for suspended
					ckOpm = ckOpmDao.findByAccnIdStatus(accn.getAccnId(), Arrays.asList(RecordStatus.SUSPENDED.getCode()));
				}

				if (ckOpm != null) {
					ckOpm.setOpmDtLupd(new Date());
					ckOpm.setOpmUidLupd("sys");
					ckOpm.setOpmStatus(RecordStatus.INACTIVE.getCode());
					ckOpmDao.saveOrUpdate(ckOpm);
				}
			}

		} catch (OpmException e) {
			throw e;
		} catch (Exception e) {
			log.error("Fail to process OPM Credit Approve. ", e);
			throw new OpmException(Opm_Validation.UNKNOW.getCode(), e.getMessage());
		}
	}
}
