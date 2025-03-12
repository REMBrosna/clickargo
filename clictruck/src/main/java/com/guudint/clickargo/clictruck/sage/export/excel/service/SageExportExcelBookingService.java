package com.guudint.clickargo.clictruck.sage.export.excel.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.sage.dao.CkSageBookingDao;
import com.guudint.clickargo.clictruck.sage.dto.CkSageBooking;
import com.guudint.clickargo.clictruck.sage.model.VCkSageBooking;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.model.TCkMstServiceType;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntStateEnum;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntTypeEnum;
import com.guudint.clickargo.sage.model.TCkCtMstSageIntState;
import com.guudint.clickargo.sage.model.TCkCtMstSageIntType;
import com.guudint.clickargo.sage.model.TCkSageIntegration;

@Service
public class SageExportExcelBookingService extends SageExportExcelService<CkSageBooking> {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(SageExportExcelBookingService.class);

	@Autowired
	protected CkSageBookingDao sageBookingDao;

	@Override
	protected List<CkSageBooking> fetchRecords(Date beginDate, Date endDate) {

		try {
			List<VCkSageBooking> bookingList = sageBookingDao.findByDate(beginDate, endDate);

			List<CkSageBooking> svcSubList = bookingList.stream().map(o -> new CkSageBooking(o))
					.collect(Collectors.toList());

			return svcSubList;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected String getFileName(Date beginDate, Date endDate) {

		String bDate = yyyyMMddSDF.format(beginDate);
		String eDate = yyyyMMddSDF.format(endDate);

		return CkCtMstSageIntTypeEnum.BOOKING.getFileName() + "_" + bDate + "_" + eDate + ".xls";
	}
	
	@Override
	protected TCkSageIntegration createTCkSageIntegration(Date beginDate, Date endDate, int noRecords, String locExport) {
		
		TCkSageIntegration sageInte = super.createTCkSageIntegration(beginDate, endDate, noRecords, locExport);
		
		sageInte.setTCkCtMstSageIntState(new TCkCtMstSageIntState(CkCtMstSageIntStateEnum.SUBMITTED.name(), null));
		sageInte.setTCkCtMstSageIntType(new TCkCtMstSageIntType(CkCtMstSageIntTypeEnum.BOOKING.name(), null));
		sageInte.setTCkMstServiceType(new TCkMstServiceType(ServiceTypes.CLICTRUCK.getId(), null));
		
		return sageInte;
	}

	@Override
	protected byte[] generateExcelFile(List<CkSageBooking> dtoList) throws IOException {

		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFSheet sheet = wb.createSheet("Booking");

		HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		style.setWrapText(true);

		sheet.setColumnWidth(0, 256 * 15);
		sheet.setColumnWidth(1, 256 * 10);
		sheet.setColumnWidth(2, 256 * 20);
		sheet.setColumnWidth(3, 256 * 20);
		sheet.setColumnWidth(4, 256 * 15);
		
		sheet.setColumnWidth(5, 256 * 20);
		sheet.setColumnWidth(4, 256 * 15);
		sheet.setColumnWidth(4, 256 * 25);
		sheet.setColumnWidth(4, 256 * 15);
		sheet.setColumnWidth(4, 256 * 10);

		sheet.setColumnWidth(4, 256 * 15);
		sheet.setColumnWidth(4, 256 * 15);
		sheet.setColumnWidth(4, 256 * 15);
		sheet.setColumnWidth(4, 256 * 15);
		sheet.setColumnWidth(4, 256 * 15);
		sheet.setColumnWidth(4, 256 * 15);
		
		int rowid = 0;
		int cellid = 0;

		HSSFRow row = sheet.createRow(rowid);
		row.setHeightInPoints(25);
		row.setRowStyle(style);
		

		HSSFCell cell = row.createCell(cellid++);
		cell.setCellValue("service");
		cell = row.createCell(cellid++);
		cell.setCellValue("type");
		cell = row.createCell(cellid++);
		cell.setCellValue("reference");
		cell = row.createCell(cellid++);
		cell.setCellValue("dateTime");
		cell = row.createCell(cellid++);
		cell.setCellValue("typeid");
		
		
		cell = row.createCell(cellid++);
		cell.setCellValue("Cust./ProvicerId");
		cell = row.createCell(cellid++);
		cell.setCellValue("tranType");
		cell = row.createCell(cellid++);
		cell.setCellValue("DocNo");
		cell = row.createCell(cellid++);
		cell.setCellValue("issuedDate");
		cell = row.createCell(cellid++);
		cell.setCellValue("ccy");
		
		cell = row.createCell(cellid++);
		cell.setCellValue("amount");
		cell = row.createCell(cellid++);
		cell.setCellValue("vat");
		cell = row.createCell(cellid++);
		cell.setCellValue("duty");
		cell = row.createCell(cellid++);
		cell.setCellValue("terms");
		cell = row.createCell(cellid++);
		cell.setCellValue("taxNo");
		cell = row.createCell(cellid++);
		cell.setCellValue("total");

		row.setRowStyle(style);

		rowid++;

		for (CkSageBooking booking : (List<CkSageBooking>)dtoList) {

			cellid = 0;
			row = sheet.createRow(rowid);
			rowid++;

			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getService());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getSageType());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getReference());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getDatetime());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getTypeId());
			
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getSageId());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getTranType());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getDocNo());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getIssuedDate());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getCcy());

			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getAmount());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getVat());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getDuty());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getTerms());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getTaxNo());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getTotal());

		}
		//byte[] excelBody = wb.getBytes();
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		wb.write(outByteStream);
		byte[] excelBody = outByteStream.toByteArray();
		
		wb.close();
		
		return excelBody;
	}

}
