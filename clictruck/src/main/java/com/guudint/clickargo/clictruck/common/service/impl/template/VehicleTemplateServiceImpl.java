package com.guudint.clickargo.clictruck.common.service.impl.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guudint.clickargo.clictruck.common.dao.CkCtChassisDao;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.guudint.clickargo.clictruck.common.model.TCkCtVeh;
import com.guudint.clickargo.clictruck.common.service.AbstractTemplateService;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstChassisTypeDao;
import com.guudint.clickargo.clictruck.master.dao.CkCtMstVehTypeDao;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.guudint.clickargo.clictruck.master.service.impl.CkCtMstChassisTypeServiceImpl;
import com.guudint.clickargo.clictruck.master.service.impl.CkCtMstVehTypeServiceImpl;
import com.guudint.clickargo.common.RecordStatus;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.exception.ValidationException;
import com.vcc.camelone.common.service.entity.IEntityService;

/**
 * Vehicle implementation for download/upload template. 
 */
public class VehicleTemplateServiceImpl extends AbstractTemplateService {

	@Autowired
	@Qualifier("ckCtVehService")
	private IEntityService<TCkCtVeh, String, CkCtVeh> ckCtVehService;
	
	@Autowired
	private CkCtMstVehTypeServiceImpl ckCtMstVehTypeServiceImpl;
	
	@Autowired
	private CkCtMstVehTypeDao ckCtMstVehTypeDao;
	
	@Autowired
	private CkCtMstChassisTypeServiceImpl ckCtMstChassisTypeServiceImpl;
	
	@Autowired
	private CkCtMstChassisTypeDao ckCtMstChassisTypeDao;
	
	@Autowired
	private CkCtChassisDao chassisDao;
	
	@Override
	public byte[] download() throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// create file excel
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {

			XSSFSheet sheet = workbook.createSheet("Details");

			String[] headers = { 
					"Plate Number", "Vehicle Type ID (Please refer to Vehicle Type sheet)", 
					"Vehicle Class (Please refer to Vehicle Class sheet)", 
					"Dimension Length", "Dimension Width", "Dimension Height", "Max Weight (Kg)","Volume (CBM)", 
					"Chassis Type ID (Please refer to Chassis Type sheet)", "Chassis Number (Please refer to Chassis Number sheet)",
					"Maintenance (Y/N)", "Remark"
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
			
			// Sheet Vehicle Type
			XSSFSheet sheetVehType = workbook.createSheet("Vehicle Type");
			Row sheetVehTypeHeaderRow0 = sheetVehType.createRow(0);
			// Add Note
			Cell cellVehType0 = sheetVehTypeHeaderRow0.createCell(0);
			cellVehType0.setCellValue("This is a list of available vehicle types. Please choose using the Vehicle Type ID!");

			String[] hdrSheetVehType = { "Vehicle Type ID", "Vehicle Type Name" };
			Row sheetVehTypeHeaderRow = sheetVehType.createRow(1);

			for (int i = 0; i < hdrSheetVehType.length; i++) {
				Cell cell = sheetVehTypeHeaderRow.createCell(i);
				cell.setCellValue(hdrSheetVehType[i]);
				cell.setCellStyle(boldCellStyle);
				sheetVehType.setColumnWidth(i, 6400);
			}

			List<CkCtMstVehType> vehTypeList = ckCtMstVehTypeServiceImpl.listAll();

			if (vehTypeList != null && vehTypeList.size() > 0) {
				int rowIdx = 2;
				for (CkCtMstVehType vehType : vehTypeList) {
					Row row = sheetVehType.createRow(rowIdx++);
					row.createCell(0).setCellValue(vehType.getVhtyId());
					row.createCell(1).setCellValue(vehType.getVhtyName());
				}
			}
			
			// Sheet Vehicle Class
			XSSFSheet sheetVehClass = workbook.createSheet("Vehicle Class");
			Row sheetVehClassHeaderRow0 = sheetVehClass.createRow(0);
			Cell cellVehClass = sheetVehClassHeaderRow0.createCell(0);
			cellVehClass.setCellValue("Vehicle Class");
			cellVehClass.setCellStyle(boldCellStyle);
			sheetVehClass.setColumnWidth(0, 6400);

			List<Integer> vehClassList = Arrays.asList(1, 2, 3);

			if (vehClassList != null && vehClassList.size() > 0) {
				int rowIdx = 1;
				for (Integer vehClass : vehClassList) {
					Row row = sheetVehClass.createRow(rowIdx++);
					row.createCell(0).setCellValue(vehClass);
				}
			}
			
			// Sheet Chassis Type
			XSSFSheet sheetChassisType = workbook.createSheet("Chassis Type");
			Row sheetChassisTypeHeaderRow0 = sheetChassisType.createRow(0);
			// Add Note
			Cell cellChassisType0 = sheetChassisTypeHeaderRow0.createCell(0);
			cellChassisType0.setCellValue("This is a list of available chassis types. Please choose using the Chassis Type ID!");

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
			
			// Sheet Chassis Number
			XSSFSheet sheetChassisNumber = workbook.createSheet("Chassis Number");
			Row sheetChassisNumberHeaderRow0 = sheetChassisNumber.createRow(0);
			// Add Note
			Cell cellChassisNumber0 = sheetChassisNumberHeaderRow0.createCell(0);
			cellChassisNumber0.setCellValue(
					"This is a list of available chassis numbers. Please choose using the Chassis Number!");

			Row sheetChassisNumberHeaderRow = sheetChassisNumber.createRow(1);
			Cell cell = sheetChassisNumberHeaderRow.createCell(0);
			cell.setCellValue("Chassis Number");
			cell.setCellStyle(boldCellStyle);
			sheetChassisNumber.setColumnWidth(0, 6400);

			List<TCkCtChassis> ckCtChassisList = chassisDao.findChassisByCompany(principalUtilService.getPrincipal().getCoreAccn().getAccnId());
			if (ckCtChassisList != null && ckCtChassisList.size() > 0) {
				int rowIdx = 2;
				for (TCkCtChassis chassisType : ckCtChassisList) {
					Row row = sheetChassisNumber.createRow(rowIdx++);
					row.createCell(0).setCellValue(chassisType.getChsNo());
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
				
				CkCtVeh dto = new CkCtVeh();
				dto.setTCoreAccn(principal.getCoreAccn());
				dto.setVhDtCreate(now);
				dto.setVhDtLupd(now);
				dto.setVhUidLupd(principal.getUserId());
				dto.setVhUidCreate(principal.getUserId());
				dto.setVhStatus(RecordStatus.ACTIVE.getCode());
				
				Optional<TCkCtMstChassisType> opTCkCtMstChassisType = Optional.empty();

				// Iterate through columns of the row
				Iterator<Cell> cellItr = row.cellIterator();
				try {

					while (cellItr.hasNext()) {
						Cell cell = cellItr.next();

						if (cell.getColumnIndex() == 0)
							dto.setVhPlateNo(cell.getStringCellValue());
						if (cell.getColumnIndex() == 1) {
							Optional<TCkCtMstVehType> opTCkCtMstVehType = Optional.ofNullable(ckCtMstVehTypeDao.find(cell.getStringCellValue()));
							if(opTCkCtMstVehType!=null) {
								CkCtMstVehType ckCtMstVehType = new CkCtMstVehType(opTCkCtMstVehType.get());
								dto.setTCkCtMstVehType(ckCtMstVehType);
							}
						}
						if (cell.getColumnIndex() == 2) 
							dto.setVhClass((byte) cell.getNumericCellValue());
						if (cell.getColumnIndex() == 3) 
							dto.setVhLength((short) cell.getNumericCellValue());
						if (cell.getColumnIndex() == 4) 
							dto.setVhWidth((short) cell.getNumericCellValue());
						if (cell.getColumnIndex() == 5) 
							dto.setVhHeight((short) cell.getNumericCellValue());
						if (cell.getColumnIndex() == 6) 
							dto.setVhWeight((int) cell.getNumericCellValue());
						if (cell.getColumnIndex() == 7) 
							dto.setVhVolume((int) cell.getNumericCellValue());	
						 if (cell.getColumnIndex() == 8)
							 opTCkCtMstChassisType = Optional.ofNullable(ckCtMstChassisTypeDao.find(cell.getStringCellValue()));
						if (cell.getColumnIndex() == 9) {
						    String chasisNo = "";
						    if (cell.getCellType() == CellType.NUMERIC) {
						        chasisNo = String.valueOf((int) cell.getNumericCellValue());
						    } else if (cell.getCellType() == CellType.STRING) {
						        chasisNo = cell.getStringCellValue();
						    }
							Optional<TCkCtChassis> opTCkCtChassis = Optional.ofNullable(chassisDao.findChassisByChsNo(chasisNo, principal.getCoreAccn().getAccnId()));
							if(opTCkCtChassis.isPresent()) {
								dto.setVhChassisNo(opTCkCtChassis.get().getChsNo());
								dto.setTCkCtMstChassisType(new CkCtMstChassisType(opTCkCtChassis.get().getTCkCtMstChassisType()));
							} else {			
								JSONObject chassisJson = new JSONObject();
								chassisJson.put("vhChassisNo", "OTHERS");
								chassisJson.put("vhChassisNoOth", chasisNo);
								dto.setVhChassisNoOth(chasisNo);
								dto.setVhChassisNo(chassisJson.toString());
								if (opTCkCtMstChassisType != null) {
									dto.setTCkCtMstChassisType(new CkCtMstChassisType(opTCkCtMstChassisType.get()));
								}	
							}
						}
						if (cell.getColumnIndex() == 10)
							dto.setVhIsMaintenance(cell.getStringCellValue().charAt(0));
						if (cell.getColumnIndex() == 11)
							dto.setVhRemarks(cell.getStringCellValue());

					}

					ckCtVehService.add(dto, principalUtilService.getPrincipal());
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
