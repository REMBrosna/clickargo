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

import com.guudint.clickargo.clictruck.sage.dao.CkSagePayInDao;
import com.guudint.clickargo.clictruck.sage.dto.CkSagePayIn;
import com.guudint.clickargo.clictruck.sage.model.VCkSagePayIn;
import com.guudint.clickargo.master.enums.ServiceTypes;
import com.guudint.clickargo.master.model.TCkMstServiceType;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntStateEnum;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntTypeEnum;
import com.guudint.clickargo.sage.model.TCkCtMstSageIntState;
import com.guudint.clickargo.sage.model.TCkCtMstSageIntType;
import com.guudint.clickargo.sage.model.TCkSageIntegration;

@Service
public class SageExportExcelPayInService extends SageExportExcelService<CkSagePayIn> {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(SageExportExcelPayInService.class);

	@Autowired
	protected CkSagePayInDao sagePayInDao;

	@Override
	protected List<CkSagePayIn> fetchRecords(Date beginDate, Date endDate) {

		try {
			List<VCkSagePayIn> bookingList = sagePayInDao.findByDate(beginDate, endDate);
			
			log.info("bookingList.size() :" + bookingList.size());

			List<CkSagePayIn> svcSubList = bookingList.stream().map(o -> new CkSagePayIn(o))
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

		return CkCtMstSageIntTypeEnum.PAYMENT_IN.getFileName() + "_" + bDate + "_" + eDate + ".xls";
	}
	
	@Override
	protected TCkSageIntegration createTCkSageIntegration(Date beginDate, Date endDate, int noRecords, String locExport) {
		
		TCkSageIntegration sageInte = super.createTCkSageIntegration(beginDate, endDate, noRecords, locExport);
		
		sageInte.setTCkCtMstSageIntState(new TCkCtMstSageIntState(CkCtMstSageIntStateEnum.SUBMITTED.name(), null));
		sageInte.setTCkCtMstSageIntType(new TCkCtMstSageIntType(CkCtMstSageIntTypeEnum.PAYMENT_IN.name(), null));
		sageInte.setTCkMstServiceType(new TCkMstServiceType(ServiceTypes.CLICTRUCK.getId(), null));
		
		return sageInte;
	}

	@Override
	protected byte[] generateExcelFile(List<CkSagePayIn> dtoList) throws IOException {

		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFSheet sheet = wb.createSheet("PayOut");

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
		cell.setCellValue("payTotal");
		
		
		cell = row.createCell(cellid++);
		cell.setCellValue("CunsumerId");
		cell = row.createCell(cellid++);
		cell.setCellValue("docType");
		cell = row.createCell(cellid++);
		cell.setCellValue("DocumentNo");
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
		cell.setCellValue("invoiceNo");

		row.setRowStyle(style);

		rowid++;

		for (CkSagePayIn booking : dtoList) {

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
			cell.setCellValue(booking.getDatetime() );
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getPayTotal());
			
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getCunsumerId());
			cell = row.createCell(cellid++);
			cell.setCellValue(booking.getDocType());
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
