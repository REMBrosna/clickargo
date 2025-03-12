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
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;
import com.guudint.clickargo.clictruck.common.service.AbstractTemplateService;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;

/**
 * Driver implementation for download/upload template. 
 */
public class DriverTemplateServiceImpl extends AbstractTemplateService {

	@Autowired
	@Qualifier("ckCtDrvService")
	private IEntityService<TCkCtDrv, String, CkCtDrv> ckCtDrvService;
	
	@Override
	public byte[] download() throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// create file excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {

			XSSFSheet sheet = workbook.createSheet("Details");

			String[] headers = { 
					"Driver Name", "Driver User ID", "Driver Password",
					"Driver License Number", "Driver License Expiry (YYYY-MM-DD)",
					"Driver Email (ex : xxx@email.com)", "Driver Phone (with city number min 8 char, ex : +62xxx)"
					};
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

		Date now = new Date();

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
				
				CkCtDrv dto = new CkCtDrv();
				dto.setTCoreAccn(principal.getCoreAccn());
				dto.setDrvDtCreate(now);
				dto.setDrvDtLupd(now);
				dto.setDrvUidLupd(principal.getUserId());
				dto.setDrvUidCreate(principal.getUserId());
				dto.setDrvStatus(RecordStatus.ACTIVE.getCode());
				
				// Iterate through columns of the row
				Iterator<Cell> cellItr = row.cellIterator();
				try {

					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();

						if (cell.getColumnIndex() == 0)
							dto.setDrvName(cell.getStringCellValue());
						if (cell.getColumnIndex() == 1) 
							dto.setDrvMobileId(cell.getStringCellValue());
						if (cell.getColumnIndex() == 2) 
							dto.setDrvMobilePassword(cell.getStringCellValue());
						if (cell.getColumnIndex() == 3) {
							if (cell.getCellType() == CellType.NUMERIC) {
								dto.setDrvLicenseNo(String.valueOf(cell.getNumericCellValue()));
							} else if (cell.getCellType() == CellType.STRING) {
								dto.setDrvLicenseNo(cell.getStringCellValue());
							}
						}
						if (cell.getColumnIndex() == 4)
							dto.setDrvLicenseExpiry(cell.getDateCellValue());
						if (cell.getColumnIndex() == 5) 
							dto.setDrvEmail(cell.getStringCellValue());
						if (cell.getColumnIndex() == 6) 
							dto.setDrvPhone(cell.getStringCellValue());
					}

					ckCtDrvService.add(dto, principalUtilService.getPrincipal());
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
