package com.guudint.clickargo.clictruck.sage.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.sage.export.excel.service.SageExportExcelBookingService;
import com.guudint.clickargo.clictruck.sage.export.excel.service.SageExportExcelPayInService;
import com.guudint.clickargo.clictruck.sage.export.excel.service.SageExportExcelPayoutService;
import com.guudint.clickargo.clictruck.sage.export.excel.service.SageIntegrationService;
import com.guudint.clickargo.clictruck.sage.service.SageService;
import com.guudint.clickargo.clictruck.scheduler.id.SageImportScheduler;
import com.guudint.clickargo.sage.dto.CkCtMstSageIntStateEnum;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.common.service.ServiceStatus.STATUS;
import com.vcc.camelone.util.PrincipalUtilService;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clicktruck/sage/")
public class SageController extends AbstractPortalController {

	private static Logger log = Logger.getLogger(SageController.class);

	@Autowired
	private SageService sageService;

	@Autowired
	private SageExportExcelBookingService sageExportExcelBookingService;

	@Autowired
	private SageExportExcelPayoutService sageExportExcelPayoutService;

	@Autowired
	private SageExportExcelPayInService sageExportExcelPayinService;

	@Autowired
	private SageImportScheduler sageImportScheduler;

	@Autowired
	SageIntegrationService sageIntegrationService;

	@Autowired
	PrincipalUtilService principalUtilService;

	/**
	 * respond base64 string, file body
	 * 
	 * @param base64StrFilePath
	 * @return
	 */
	@RequestMapping(value = "/download/{base64StrFilePath}", method = RequestMethod.GET)
	public ResponseEntity<Object> getAttachmentByFilePath(@PathVariable String base64StrFilePath) {
		log.debug("getAttachmentByJobId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {
			if (StringUtils.isBlank(base64StrFilePath))
				throw new ParameterException("param entity null or empty");

			String base64FileBody = sageService.getFileBody(base64StrFilePath);

			return ResponseEntity.ok().body(base64FileBody);
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

	@RequestMapping(value = "/route/{sageId}/{action}", method = RequestMethod.GET)
	public ResponseEntity<Object> routeWorkflow(@PathVariable String sageId, @PathVariable String action) {
		log.debug("getAttachmentByJobId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			if (CkCtMstSageIntStateEnum.APPROVE.name().equalsIgnoreCase(action)) {
				sageIntegrationService.doApprove(sageId, principalUtilService.getPrincipal().getUserId());
			}

			if (CkCtMstSageIntStateEnum.COMPLETE.name().equalsIgnoreCase(action)) {
				sageIntegrationService.doAcknowledge(sageId, principalUtilService.getPrincipal().getUserId());
			}

			return ResponseEntity.ok().body("");
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

	/// Sage export excel begin

	@RequestMapping(value = "/exportSageBookingExcel/{beginDate}/{endDate}", method = RequestMethod.GET)
	public ResponseEntity<Object> exportSageBookingExcel(@PathVariable String beginDate, @PathVariable String endDate) {
		log.debug("getAttachmentByJobId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			sageExportExcelBookingService.exportSageExcel(this.getBeginDate(beginDate), this.getEndnDate(endDate));

			return ResponseEntity.ok().body("OK");
		} catch (Exception e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/exportSagePayoutExcel/{beginDate}/{endDate}", method = RequestMethod.GET)
	public ResponseEntity<Object> exportSagePayoutExcel(@PathVariable String beginDate, @PathVariable String endDate) {
		log.debug("getAttachmentByJobId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			sageExportExcelPayoutService.exportSageExcel(this.getBeginDate(beginDate), this.getEndnDate(endDate));

			return ResponseEntity.ok().body("OK");
		} catch (Exception e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/exportSagePayIntExcel/{beginDate}/{endDate}", method = RequestMethod.GET)
	public ResponseEntity<Object> exportSagePayInExcel(@PathVariable String beginDate, @PathVariable String endDate) {
		log.debug("getAttachmentByJobId");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			sageExportExcelPayinService.exportSageExcel(this.getBeginDate(beginDate), this.getEndnDate(endDate));

			return ResponseEntity.ok().body("OK");
		} catch (Exception e) {
			log.error("getEntityById", e);
			serviceStatus.setStatus(STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, e));
			return new ResponseEntity<Object>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/scanImport", method = RequestMethod.GET)
	public ResponseEntity<Object> scanImport() {
		log.debug("scanImport");
		ServiceStatus serviceStatus = new ServiceStatus();
		try {

			sageImportScheduler.doJob();

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
			// cal.add(Calendar.DAY_OF_YEAR, 1);
			cal.add(Calendar.MILLISECOND, -1);
			date = cal.getTime();

		} catch (ParseException e) {
			log.error("Fail to format " + dateStr, e);
			throw e;
		}
		return date;
	}

}
