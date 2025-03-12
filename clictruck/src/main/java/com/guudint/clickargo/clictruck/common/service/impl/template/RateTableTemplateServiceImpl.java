package com.guudint.clickargo.clictruck.common.service.impl.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContract;
import com.guudint.clickargo.clictruck.admin.contract.service.CkCtContractService;
import com.guudint.clickargo.clictruck.admin.ratetable.dto.CkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTable;
import com.guudint.clickargo.clictruck.admin.ratetable.service.ICkCtRateTableService;
import com.guudint.clickargo.clictruck.common.service.AbstractTemplateService;
import com.guudint.clickargo.common.CKCountryConfig;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;
import com.vcc.camelone.master.dto.MstCurrency;

/**
 * Rate table implementation for download/upload template. 
 */
public class RateTableTemplateServiceImpl extends AbstractTemplateService {

	@Autowired
	private ICkCtRateTableService rateTableService;

	@Autowired
	@Qualifier("ccmAccnService")
	private IEntityService<TCoreAccn, String, CoreAccn> ccmAccnService;

	@Autowired
	@Qualifier("ckCtRateTableService")
	private IEntityService<TCkCtRateTable, String, CkCtRateTable> ckCtRateTableService;

	@Autowired
	private CkCtContractService contractService;

	@Override
	public byte[] download() throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// create file excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {

			XSSFSheet sheet = workbook.createSheet("Details");

			String[] headers = { "Name", "Description", "CO/FF Account ID (Please refer to Accounts sheet)",
					"Start Date (YYYY-MM-DD)", "End Date (YYYY-MM-DD)" };
			Row headerRow = sheet.createRow(0);

			CellStyle boldCellStyle = workbook.createCellStyle();
			Font boldFont = workbook.createFont();
			boldFont.setBold(true);
			boldCellStyle.setFont(boldFont);

			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(boldCellStyle);
				sheet.setColumnWidth(i, 6400);
			}

			// Create sheet for the accounts that principal has in contract with and no rate
			// table setup yet.
			XSSFSheet sheet2 = workbook.createSheet("Accounts");
			Row sheet2HeaderRow0 = sheet2.createRow(0);
			// Note:
			Cell cell0 = sheet2HeaderRow0.createCell(0);
			cell0.setCellValue("These are lists of accounts that don't have rate table setup yet!");

			String[] hdrSheet2 = { "ID", "Account Name" };
			Row sheet2HeaderRow = sheet2.createRow(1);

			for (int i = 0; i < hdrSheet2.length; i++) {
				Cell cell = sheet2HeaderRow.createCell(i);
				cell.setCellValue(hdrSheet2[i]);
				cell.setCellStyle(boldCellStyle);
				sheet2.setColumnWidth(i, 6400);
			}

			List<CoreAccn> accnList = rateTableService.loadAccnsRateTableByContract(principalUtilService.getPrincipal(),
					true);

			if (accnList != null && accnList.size() > 0) {
				int rowIdx = 2;
				for (CoreAccn accn : accnList) {
					Row row = sheet2.createRow(rowIdx++);
					row.createCell(0).setCellValue(accn.getAccnId());
					row.createCell(1).setCellValue(accn.getAccnName());
				}
			}

			workbook.write(bos);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			try {
				bos.close();
				workbook.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bos.toByteArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void upload(byte[] data) throws ValidationException, Exception {
		if (data == null)
			throw new ParameterException("param data null or empty");

		Principal principal = principalUtilService.getPrincipal();
		if (principal == null)
			throw new ProcessingException("principal null");

		CKCountryConfig ctryConfig = getCountryConfig();
		MstCurrency tMstCurrency = new MstCurrency();
		tMstCurrency.setCcyCode(ctryConfig.getCurrency());

		Date now = new Date();

		//
		List<String> errors = new ArrayList<>();

		ByteArrayInputStream input = new ByteArrayInputStream(data);
		XSSFWorkbook wb = new XSSFWorkbook(input);

		try {

			// Only process the first sheet
			XSSFSheet ws = wb.getSheetAt(0);

			// Iterate through each rows in the ws
			Iterator<Row> rowItr = ws.iterator();
			while (rowItr.hasNext()) {
				Row row = rowItr.next();

				// ignore first row as this is the header
				if (row.getRowNum() == 0)
					continue;

				CkCtRateTable dto = new CkCtRateTable();
				dto.setTCoreAccnByRtCompany(principal.getCoreAccn());
				dto.setTMstCurrency(tMstCurrency);
				dto.setRtDtCreate(now);
				dto.setRtDtLupd(now);
				dto.setRtUidLupd(principal.getUserId());
				dto.setRtUidCreate(principal.getUserId());
				dto.setRtStatus(RecordStatus.ACTIVE.getCode());

				// Iterate through columns of the row
				Iterator<Cell> cellItr = row.cellIterator();
				try {

					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();

						if (cell.getColumnIndex() == 0)
							dto.setRtName(cell.getStringCellValue());
						if (cell.getColumnIndex() == 1)
							dto.setRtDescription(cell.getStringCellValue());
						if (cell.getColumnIndex() == 2) {
							// find for the account
							CoreAccn coffAccn = ccmAccnService.findById(cell.getStringCellValue());
							if (coffAccn != null) {
								dto.setTCoreAccnByRtCoFf(coffAccn);
								// check if the coFfAccn set has contract with the TO in principal
								CkCtContract ctrct = contractService.getContractByAccounts(
										principal.getCoreAccn().getAccnId(), coffAccn.getAccnId());
								if (ctrct == null) {
									Map<String, String> errMap = new HashMap<String, String>();
									errMap.put("noContract", coffAccn.getAccnId() + " has no existing contract with "
											+ principal.getCoreAccn().getAccnName());
									throw new ValidationException(mapper.writeValueAsString(errMap));
								}

							}

						}

						if (cell.getColumnIndex() == 3)
							dto.setRtDtStart(cell.getDateCellValue());

						if (cell.getColumnIndex() == 4)
							dto.setRtDtEnd(cell.getDateCellValue());

					}

					ckCtRateTableService.add(dto, principalUtilService.getPrincipal());
				} catch (ValidationException e) {

					Map<String, String> vErrMap = mapper.readValue(e.getMessage(), HashMap.class);
					StringBuilder strErr = new StringBuilder(row.getRowNum() + " - ");
					for (Map.Entry<String, String> entry : vErrMap.entrySet()) {
						strErr.append(entry.getValue());
						errors.add(strErr.toString());
					}

				}

			}

			if (!errors.isEmpty())
				throw new ValidationException(mapper.writeValueAsString(errors));

		} catch (ValidationException ex) {
			throw ex;

		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				input.close();
				wb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
