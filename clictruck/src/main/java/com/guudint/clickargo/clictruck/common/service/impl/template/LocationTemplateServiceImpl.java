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
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.common.service.AbstractTemplateService;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstLocationTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstLocationType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstLocationType;
import com.guudint.clickargo.clictruck.master.service.impl.CkCtMstLocationTypeServiceImpl;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;

/**
 * Location implementation for download/upload template. 
 */
public class LocationTemplateServiceImpl extends AbstractTemplateService {

	@Autowired
	@Qualifier("ckCtLocationService")
	private IEntityService<TCkCtLocation, String, CkCtLocation> ckCtLocationService;
	
	@Autowired
	private CkCtMstLocationTypeServiceImpl ckCtMstLocationTypeServiceImpl;
	
	@Autowired
	private CkCtMstLocationTypeDao ckCtMstLocationTypeDao;
	
	@Override
	public byte[] download() throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// create file excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {

			XSSFSheet sheet = workbook.createSheet("Details");

			String[] headers = { 
					"Location Name", "Location Type (Please refer to Location Type sheet)", "Address", "Remarks",
					"Start Date (YYYY-MM-DD)", "End Date (YYYY-MM-DD)"
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
			
			// Sheet Location Type
			XSSFSheet sheetLocType = workbook.createSheet("Location Type");
			Row sheetLocTypeHeaderRow0 = sheetLocType.createRow(0);
			Cell cellLocType = sheetLocTypeHeaderRow0.createCell(0);
			cellLocType.setCellValue("Location Type");
			cellLocType.setCellStyle(boldCellStyle);
			sheetLocType.setColumnWidth(0, 6400);

			List<CkCtMstLocationType> tCkCtMstLocationTypes = ckCtMstLocationTypeServiceImpl.listAll(); 
			if (tCkCtMstLocationTypes != null && tCkCtMstLocationTypes.size() > 0) {
				int rowIdx = 1;
				for (CkCtMstLocationType locType : tCkCtMstLocationTypes) {
					Row row = sheetLocType.createRow(rowIdx++);
					row.createCell(0).setCellValue(locType.getLctyId());
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
				
				CkCtLocation dto = new CkCtLocation();
				dto.setTCoreAccn(principal.getCoreAccn());
				dto.setLocDtCreate(now);
				dto.setLocDtLupd(now);
				dto.setLocUidLupd(principal.getUserId());
				dto.setLocUidCreate(principal.getUserId());
				dto.setLocStatus(RecordStatus.ACTIVE.getCode());
				
				// Iterate through columns of the row
				Iterator<Cell> cellItr = row.cellIterator();
				try {

					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();

						if (cell.getColumnIndex() == 0)
							dto.setLocName(cell.getStringCellValue());
						if (cell.getColumnIndex() == 1) {
							Optional<TCkCtMstLocationType> opTCkCtMstLocationType = Optional
									.ofNullable(ckCtMstLocationTypeDao.find(cell.getStringCellValue()));
							if (opTCkCtMstLocationType.isPresent()) {
								dto.setTCkCtMstLocationType(new CkCtMstLocationType(opTCkCtMstLocationType.get()));
							}
						}
						if (cell.getColumnIndex() == 2) 
							dto.setLocAddress(cell.getStringCellValue());
						if (cell.getColumnIndex() == 3) 
							dto.setLocRemarks(cell.getStringCellValue());
						if (cell.getColumnIndex() == 4)
							dto.setLocDtStart(cell.getDateCellValue());
						if (cell.getColumnIndex() == 5)
							dto.setLocDtEnd(cell.getDateCellValue());
					}
					ckCtLocationService.add(dto, principalUtilService.getPrincipal());
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
