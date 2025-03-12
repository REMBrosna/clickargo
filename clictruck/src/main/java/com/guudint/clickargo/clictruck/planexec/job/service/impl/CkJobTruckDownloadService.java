package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.vcc.camelone.common.controller.entity.EntityFilterRequest;
import com.vcc.camelone.common.controller.entity.EntityOrderBy;
import com.vcc.camelone.common.controller.entity.EntityWhere;
import com.vcc.camelone.common.controller.entity.EntityOrderBy.ORDERED;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.master.controller.PathNotFoundException;

import io.jsonwebtoken.lang.Collections;

/**
 * Arianto
 */
public class CkJobTruckDownloadService {
	
	private static Logger LOG = Logger.getLogger(CkJobTruckDownloadService.class);
	
	@Autowired
	@Qualifier("ckCtTripDao")
	private GenericDao<TCkCtTrip, String> ckCtTripDao;
	
	@Autowired
	private CkJobTruckService ckJobTruckService;
	
	/**
	 * @param entity
	 * @param params
	 * @return
	 * @throws ParameterException
	 * @throws PathNotFoundException
	 * @throws ProcessingException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public Workbook getDownloadData(String entity, Map<String, String> params) throws ParameterException, PathNotFoundException, ProcessingException{
		LOG.debug("Download excel");
		
		try {
			Optional<List<CkJobTruck>> ckJobTruck = Optional.ofNullable(getDataFilter(params));
		
			// create file excel	
			XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("Data");

			
			//create header
			String[] headersBold = {"Job ID", "Job Type", "Trucking Operator", "Delivery Date", "Shipment Ref", "Pick Up",
					"Last Drop Off", "Trip Charge", "Reimbursement", "Total Trip Charge", "Invoice Number"}; 
			Row headerRow = sheet.createRow(0);
			CellStyle boldCellStyle = workbook.createCellStyle();
		    Font boldFont = workbook.createFont();
		    boldFont.setBold(true);
		    boldCellStyle.setFont(boldFont);

		    
		    for (int i = 0; i < headersBold.length; i++) {
		    	Cell cell = headerRow.createCell(i);
		        cell.setCellValue(headersBold[i]);
		        cell.setCellStyle(boldCellStyle);

			}
		    
		    int rowNum = 1;
		    for (CkJobTruck cellValue : ckJobTruck.get()) {
		    	
	    		Row row = sheet.createRow(rowNum++);
	    		//Job ID
		        row.createCell(0).setCellValue(cellValue.getJobId());
		        
		        //Job Type
		        if (cellValue.getTCkJob().getTCkMstShipmentType() != null) {
		        	row.createCell(1).setCellValue(cellValue.getTCkJob().getTCkMstShipmentType().getShtName());
				}else {
					row.createCell(1).setCellValue("null");
				}
		        
		        //Trucking Operator
		        if (cellValue.getTCkJob().getTCoreAccnByJobToAccn() != null) {
		        	row.createCell(2).setCellValue(cellValue.getTCkJob().getTCoreAccnByJobToAccn().getAccnName());
				}else {
					row.createCell(2).setCellValue("null");
				}
		        
		        //Delivery Date
		        row.createCell(3).setCellValue(cellValue.getJobDtDelivery());
		        
		        //Shipment Ref
		        row.createCell(4).setCellValue(cellValue.getJobShipmentRef());
		        
		        //Pick Up
		        if (cellValue.getPickUp() != null) {
		        	row.createCell(5).setCellValue(cellValue.getPickUp().getTlocLocName());
				}else {
					row.createCell(5).setCellValue("null");
				}
	        	
	        	//Last Drop Off
		        if (cellValue.getLastDrop() != null) {
		        	row.createCell(6).setCellValue(cellValue.getLastDrop().getTlocLocName());
				}else {
					row.createCell(6).setCellValue("null");
				}
		        
		        //Trip Charge
		        if(cellValue.getTckCtTripList() != null) {
			        row.createCell(7).setCellValue(cellValue.getTckCtTripList().get(0).getTCkCtTripCharge().getTcPrice().doubleValue());
		        }else {
			        row.createCell(7).setCellValue("null");
		        }
		        
		        //Reimbursement
		        if (cellValue.getJobTotalReimbursements() != null) {
		        	row.createCell(8).setCellValue(cellValue.getJobTotalReimbursements().doubleValue());
				}else {
					row.createCell(8).setCellValue("0");
				}
		        
		        //Total Trip Charge
		        if (cellValue.getJobTotalCharge() != null) {
		        	row.createCell(9).setCellValue(cellValue.getJobTotalCharge().doubleValue());
				}else {
					row.createCell(9).setCellValue("0");
				}
		        
		        //Invoice Number
		        if (cellValue.getTCkCtToInvoice() != null) {
		        	row.createCell(10).setCellValue(cellValue.getTCkCtToInvoice().getInvNo());
				}else {
					row.createCell(10).setCellValue("null");
				}
		        
		    	
			}
	        return workbook;
		} catch (ParameterException | PathNotFoundException | ProcessingException ex) {
			LOG.error("download data", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("download data", ex);
			throw new ProcessingException(ex);
		}
	}
	
	/**
	 * @param params
	 * @return
	 * @throws ParameterException
	 * @throws PathNotFoundException
	 * @throws ProcessingException
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	public List<CkJobTruck> getDataFilter(Map<String, String> params) throws ParameterException, PathNotFoundException, ProcessingException{
		try {

			if (Collections.isEmpty(params))
				throw new ParameterException("param params null or empty");
			EntityFilterRequest filterRequest = new EntityFilterRequest();
			filterRequest.setDisplayStart(
					params.containsKey("iDisplayStart") ? Integer.valueOf(params.get("iDisplayStart")).intValue() : -1);
			filterRequest.setDisplayLength(
					params.containsKey("iDisplayLength") ? Integer.valueOf(params.get("iDisplayLength")).intValue()
							: -1);
			ArrayList<EntityWhere> whereList = new ArrayList<>();
			List<String> searches = params.keySet().stream().filter(x -> x.contains("sSearch")).
					collect(Collectors.toList());
			for (int nIndex = 0; nIndex < searches.size(); nIndex++) {
				String searchParam = params.get("sSearch_" + String.valueOf(nIndex));
				String valueParam = params.get("mDataProp_" + String.valueOf(nIndex));
				whereList.add(new EntityWhere(valueParam, searchParam));
				
			}
			filterRequest.setWhereList(whereList);
			// order by parameters extraction
			Optional<String> opSortAttribute = Optional.ofNullable(params.get("mDataProp_0"));
			Optional<String> opSortOrder = Optional.ofNullable(params.get("sSortDir_0"));
			if (opSortAttribute.isPresent() && opSortOrder.isPresent()) {
				EntityOrderBy orderBy = new EntityOrderBy();
				orderBy.setAttribute("jobId");
				orderBy.setOrdered(opSortOrder.get().equalsIgnoreCase("desc") ? ORDERED.DESC : ORDERED.ASC);
				filterRequest.setOrderBy(orderBy);
			}

			if (!filterRequest.isValid())
				throw new ProcessingException("Invalid request: " + filterRequest.toJson());

			List<CkJobTruck> en = ckJobTruckService.filterBy(filterRequest);
			
			return en;
		} catch (ParameterException | PathNotFoundException | ProcessingException ex) {
			LOG.error("Download Data", ex);
			throw ex;
		} catch (Exception ex) {
			LOG.error("Download Data", ex);
			throw new ProcessingException(ex);
		}
		
	}

}
