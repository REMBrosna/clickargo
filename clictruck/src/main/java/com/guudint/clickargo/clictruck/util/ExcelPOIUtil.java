package com.guudint.clickargo.clictruck.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelPOIUtil {

	public static boolean isSheetEmpty(Sheet sheet) {

		if (sheet == null) {
			return true;
		}
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			if (!isRowEmpty(sheet.getRow(i))) {
				// not blank
				return false;
			}
		}
		return true;
	}

	public static boolean isRowEmpty(Row row) {
		if (row == null) {
			return true;
		}

		for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
			/*-
			Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
			if (cell != null && cell.getCellType() != CellType.BLANK) {
				return false;
			}
			*/
			Cell cell = row.getCell(i);
			if (StringUtils.isNotBlank(getCellValueAsString(cell))) {
				return false;
			}
		}
		return true;
	}

	public synchronized static String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			} else {
				return getStringFromNum(cell.getNumericCellValue());
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return null;
		default:
			return null;
		}
	}

	public synchronized static Object getCellValueAsStringOrDate(Cell cell) {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				return getStringFromNum(cell.getNumericCellValue());
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return null;
		default:
			return null;
		}
	}
	
	private static String getStringFromNum(double d) {
		try {
			return new Double(d).longValue() + "";
		} catch (Exception e) {

		}
		return String.valueOf(d);
	}

	public static boolean isXlsFormat(byte[] excelData) throws IOException {
		byte[] xlsMagicNumber = new byte[] { (byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0, (byte) 0xA1,
				(byte) 0xB1, (byte) 0x1A, (byte) 0xE1 };
		for (int i = 0; i < xlsMagicNumber.length; i++) {
			if (xlsMagicNumber[i] != excelData[i]) {
				return false;
			}
		}
		return true;
	}

	public static boolean isXlsxFormat(byte[] excelData) throws IOException {
		byte[] xlsxMagicNumber = new byte[] { (byte) 0x50, (byte) 0x4B, (byte) 0x03, (byte) 0x04 };
		for (int i = 0; i < xlsxMagicNumber.length; i++) {
			if (xlsxMagicNumber[i] != excelData[i]) {
				return false;
			}
		}
		return true;
	}
}
