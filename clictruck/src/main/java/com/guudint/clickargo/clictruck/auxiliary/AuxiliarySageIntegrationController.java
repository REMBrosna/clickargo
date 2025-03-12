package com.guudint.clickargo.clictruck.auxiliary;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.sage.export.service.SageExportJsonBookingService;
import com.guudint.clickargo.clictruck.sage.export.service.SageExportJsonInService;
import com.guudint.clickargo.clictruck.sage.export.service.SageExportJsonOutService;
import com.guudint.clickargo.clictruck.sage.service.SageService;
import com.vcc.camelone.common.attach.dto.CoreAttach;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/auxiliary/sage/")
public class AuxiliarySageIntegrationController extends AbstractPortalController {

	private static Logger log = Logger.getLogger(AuxiliarySageIntegrationController.class);


	@Autowired
	private SageExportJsonBookingService sageExportJsonBookingService;

	@Autowired
	private SageExportJsonOutService sageExportJsonOutService;

	@Autowired
	private SageExportJsonInService sageExportJsonInService;


	@Autowired
	private SageService sageService;

	/**
	 * /export/20230522/20230526
	 * @param begainDate
	 * @param endDate
	 */
	@RequestMapping(value = "/export/{begainDate}/{endDate}")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void generateExcelReport(@PathVariable String begainDate, @PathVariable String endDate) {

		log.info("downloadSummaryReport():... " + begainDate + "  " + endDate);
		try {
			// AR Report 08 Jun 2023 10_48_49.xls
			// AR Report 08 Jun 2023 13_39_29.xls
			String xlsFileName = sageService.getFileName(new Date());

			Date bDate = this.getBeginDate(begainDate);
			Date eDate = this.getEndnDate(endDate);
			
			byte[] data = sageService.getExcelReport(bDate, eDate, xlsFileName);

			httpResponse.setHeader("Content-Disposition", "attachment; filename=" + xlsFileName);

			ServletOutputStream servletOutputStream = httpResponse.getOutputStream();

			servletOutputStream.write(data);

			httpResponse.setHeader("Content-Length", String.valueOf(data.length));
			httpResponse.setHeader("Content-Type", "application/xls");

			servletOutputStream.close();

		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * respond base64 string, file body 
	 * @param base64StrFilePath
	 * @return
	 */
	@RequestMapping(value = "/downloadById/{sageId}", method = RequestMethod.GET)
	public ResponseEntity<Object> getAttachmentByJobId(@PathVariable String sageId) {
		log.debug("getAttachmentByJobId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			CoreAttach coreAttach = sageService.getFileBodyBySageId(sageId, true);
			
			return ResponseEntity.ok().body(coreAttach);
		} catch (ParameterException e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-500, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_GATEWAY);
		} catch (Exception e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	} 
	
	/**
	 * respond base64 string, file body 
	 * @param base64StrFilePath
	 * @return
	 */
	@RequestMapping(value = "/exportSageBookingJson/{date}", method = RequestMethod.GET)
	public ResponseEntity<Object> exportSageJson(@PathVariable String date) {
		log.debug("getAttachmentByJobId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			
			sageExportJsonBookingService.exportSageJson(this.getBeginDate(date));
			
			return ResponseEntity.ok().body("OK");
		} catch (Exception e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	} 
	
	/**
	 * respond base64 string, file body 
	 * @param base64StrFilePath
	 * @return
	 */
	@RequestMapping(value = "/sageExportJsonInService/{date}", method = RequestMethod.GET)
	public ResponseEntity<Object> sageExportJsonInService(@PathVariable String date) {
		log.debug("sageExportJsonOutService");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			
			sageExportJsonInService.exportSageJson(this.getBeginDate(date));
			
			return ResponseEntity.ok().body("OK");
		} catch (Exception e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	} 
	
	/**
	 * respond base64 string, file body 
	 * @param base64StrFilePath
	 * @return
	 */
	@RequestMapping(value = "/sageExportJsonOutService/{date}", method = RequestMethod.GET)
	public ResponseEntity<Object> sageExportJsonOutService(@PathVariable String date) {
		log.debug("sageExportJsonOutService");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			
			sageExportJsonOutService.exportSageJson(this.getBeginDate(date));
			
			return ResponseEntity.ok().body("OK");
		} catch (Exception e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	} 

	/// Sage export excel End
	
	/**
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private Date getBeginDate(String dateStr) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			log.error("Fail to format " + dateStr, e);
			throw e;
		}
		return date;
	}

	/**
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	private Date getEndnDate(String dateStr) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date;
		try {
			date = dateFormat.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			//cal.add(Calendar.DAY_OF_YEAR, 1);
			cal.add(Calendar.MILLISECOND, -1);
			date = cal.getTime();
			
		} catch (ParseException e) {
			log.error("Fail to format " + dateStr, e);
			throw e;
		}
		return date;
	}
}
