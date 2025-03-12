package com.guudint.clickargo.clictruck.common.service.impl.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.common.dto.CkCtChassis;
import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.guudint.clickargo.clictruck.common.service.AbstractTemplateService;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstChassisTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.service.impl.CkCtMstChassisTypeServiceImpl;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;

/**
 * Chassis implementation for download/upload template. 
 */
public class ChassisTemplateServiceImpl extends AbstractTemplateService {

	@Autowired
	@Qualifier("ckCtChassisService")
	private IEntityService<TCkCtChassis, String, CkCtChassis> ckCtChassisService;
	
	@Autowired
	private CkCtMstChassisTypeServiceImpl ckCtMstChassisTypeServiceImpl;
	
	@Autowired
	private CkCtMstChassisTypeDao ckCtMstChassisTypeDao;
	
	@Override
	public byte[] download() throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// create file excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {

			XSSFSheet sheet = workbook.createSheet("Details");

			String[] headers = { "Chassis Size (Please refer to Chassis Size sheet)", "Chassis Number" };
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
			
			// Sheet Chassis Size
			XSSFSheet sheetChassisType = workbook.createSheet("Chassis Size");
			Row sheetChassisTypeHeaderRow0 = sheetChassisType.createRow(0);
			// Add Note
			Cell cellChassisType0 = sheetChassisTypeHeaderRow0.createCell(0);
			cellChassisType0.setCellValue(
					"This is a list of available chassis sizes. Please choose using the Chassis Type ID!");

			String[] hdrSheetChassisType = { "Chassis Type ID", "Chassis Type Name" };
			Row sheetChassisTypeHeaderRow = sheetChassisType.createRow(1);

			for (int i = 0; i < hdrSheetChassisType.length; i++) {
				Cell cell = sheetChassisTypeHeaderRow.createCell(i);
				cell.setCellValue(hdrSheetChassisType[i]);
				cell.setCellStyle(boldCellStyle);
				sheetChassisType.setColumnWidth(i, 6400);
			}
			
			List<CkCtMstChassisType> chassisTypeList = ckCtMstChassisTypeServiceImpl.listAll();

			if (chassisTypeList != null && chassisTypeList.size() > 0) {
				int rowIdx = 2;
				for (CkCtMstChassisType chassisType : chassisTypeList) {
					Row row = sheetChassisType.createRow(rowIdx++);
					row.createCell(0).setCellValue(chassisType.getChtyId());
					row.createCell(1).setCellValue(chassisType.getChtyName());
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
				
				CkCtChassis dto = new CkCtChassis();
				dto.setTCoreAccn(principal.getCoreAccn());
				dto.setChsDtCreate(now);
				dto.setChsDtLupd(now);
				dto.setChsUidLupd(principal.getUserId());
				dto.setChsUidCreate(principal.getUserId());
				dto.setChsStatus(RecordStatus.ACTIVE.getCode());
				
				// Iterate through columns of the row
				Iterator<Cell> cellItr = row.cellIterator();
				try {
					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();
						if (cell.getColumnIndex() == 0) {
							Optional<TCkCtMstChassisType> opTCkCtMstChassisType = Optional.ofNullable(ckCtMstChassisTypeDao.find(cell.getStringCellValue()));
							dto.setTCkCtMstChassisType(new CkCtMstChassisType(opTCkCtMstChassisType.get()));
						}
						if (cell.getColumnIndex() == 1) {
							String chasisNo = "";
							if (cell.getCellType() == CellType.NUMERIC) {
								chasisNo = String.valueOf((int) cell.getNumericCellValue());
							} else if (cell.getCellType() == CellType.STRING) {
								chasisNo = cell.getStringCellValue();
							}
							dto.setChsNo(chasisNo);
						}
					}
					ckCtChassisService.add(dto, principalUtilService.getPrincipal());
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
