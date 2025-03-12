
package com.guudint.clickargo.clictruck.auxiliary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.admin.contract.scheduler.ContractUpdateScheduler;
import com.guudint.clickargo.clictruck.common.dto.NotificationTemplateName;
import com.guudint.clickargo.clictruck.dsv.pod.PodServiceImpl;
import com.guudint.clickargo.clictruck.jobupload.service.JobUploadService;
import com.guudint.clickargo.clictruck.jobupload.service.SendEpodService;
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripDoAttach;
import com.guudint.clickargo.clictruck.scheduler.sg.SendEpodScheduler;
import com.guudint.clickargo.clictruck.track.dto.DistanceMatrixDto;
import com.guudint.clickargo.clictruck.track.dto.ImeiLatestStatusDto;
import com.guudint.clickargo.clictruck.track.dto.StarSenderWhatsappResponseDto;
import com.guudint.clickargo.clictruck.track.service.StarSenderWhatsappService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceCoordinateService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceDistanceMatrixService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceEnterExitLocService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceLatestStatusService;
import com.guudint.clickargo.clictruck.track.service.WhatsappYCloudService;
import com.guudint.clickargo.common.scheduler.ClickargoNotificationScheduler;
import com.guudint.clickargo.credit.scheduler.CreditRequestScheduler;
import com.vcc.camelone.ccm.dao.CoreUserDao;
import com.vcc.camelone.common.controller.entity.AbstractPortalController;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/auxiliary/")
public class AuxiliaryController extends AbstractPortalController {

	private static Logger log = Logger.getLogger(AuxiliaryController.class);

	@Autowired
	private InitVAService initVAService;

	@Autowired
	private CoreUserDao coreUserDao;

	@Autowired
	TrackTraceCoordinateService coordinateService;

	@Autowired
	TrackTraceEnterExitLocService trackTraceService;

	@Autowired
	private ContractUpdateScheduler contractUpdateScheduler;

	@Autowired
	private TrackTraceLatestStatusService trackTraceLatestStatusService;

	@Autowired
	private StarSenderWhatsappService starSenderWhatsappService;
	@Autowired
	private WhatsappYCloudService whatsappYCloudService;

	@Autowired
	private TrackTraceDistanceMatrixService trackTraceDistanceMatrixService;

	@Autowired
	private CreditRequestScheduler creditRequestScheduler;

	@Autowired
	private ClickargoNotificationScheduler notificationScheduler;

	@Autowired
	private PodServiceImpl podService;

	@Autowired
	private SendEpodScheduler sendEpodScheduler;

	@Autowired
	private SendEpodService sendEpodService;
	@Autowired
	private CkJobTruckDao jobTruckDao;
	
	@Autowired
	private JobUploadService jobUploadService;
	
	/*
	 * initial Virtual Account NO
	 */
	@RequestMapping(value = "/initVirtualAccountNO")
	public ResponseEntity<Object> initVirtualAccountNO() {

		try {
			// initVAService.initVA();

		} catch (Exception e) {
			log.error("initVirtualAccountNO", e);
		}

		return ResponseEntity.ok("OK");
	}

	@RequestMapping(value = "/executeContractUpdate")
	public ResponseEntity<Object> executeContractUpdate() throws ParameterException, Exception {

		contractUpdateScheduler.doJob();

		return ResponseEntity.ok("OK");
	}

	@RequestMapping(value = "/executeCreditUpdate")
	public ResponseEntity<Object> executeCreditUpdate(
			@RequestParam(value = "startDate", required = false) String startDate)
			throws ParameterException, Exception {
		Date date = null;
		if (null != startDate) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			date = sdf.parse(startDate);
		} else {
			date = new Date();
		}

		creditRequestScheduler.activeCreditLimit(date);

		return ResponseEntity.ok("OK");
	}

	@RequestMapping(value = "/latestStauts/{iMei}/{jobId}")
	public ResponseEntity<Object> latestStauts(@PathVariable String iMei, @PathVariable String jobId) {

		try {
			List<ImeiLatestStatusDto> rst = trackTraceLatestStatusService.getImeiLocation(iMei, jobId);
			return ResponseEntity.ok(rst);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}

	}

	@RequestMapping(value = "/sendWhatsap/{mobileNumber}/{msgBody}")
	public ResponseEntity<Object> sendWhatsap(@PathVariable String mobileNumber, @PathVariable String msgBody) {

		try {
			StarSenderWhatsappResponseDto rst = starSenderWhatsappService.sendWhatAppMsg(mobileNumber, msgBody,
					"jobId");

			return ResponseEntity.ok(rst);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}

	}

	@RequestMapping(value = "/sendWhatsap/ycloud/{mobileNumber}/{driverName}/{password}")
	public ResponseEntity<Object> sendWhatsapYcloudForgetPassword(@PathVariable String mobileNumber
			, @PathVariable String driverName, @PathVariable String password) {

		try {
			// Arrays.asList(driverName, password)
			ArrayList<String> list = new ArrayList<>();
			list.add(driverName);
			list.add(password);
			
			whatsappYCloudService.sendYCloudWhatAppMsg(null, mobileNumber, 
					list, null, NotificationTemplateName.FORGOT_PASSWORD.getDesc());

			return ResponseEntity.ok("ycloud");

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}

	}
	
	@RequestMapping(value = "/getDistanceMatrix/{origin}/{destination}")
	public ResponseEntity<Object> getDistanceMatrix(@PathVariable String origin, @PathVariable String destination) {

		try {
			DistanceMatrixDto disinctMatrix = trackTraceDistanceMatrixService.getDistanceMatrix(origin, destination,
					"jobId");

			return ResponseEntity.ok(disinctMatrix);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}

	}

	@RequestMapping(value = "/sendEmailNotify")
	public ResponseEntity<Object> sendEmailNotify() throws Exception {

		try {

			notificationScheduler.doJob();

			return ResponseEntity.ok("");

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}

	}

	@RequestMapping(value = "/sendEpodEmail/{jobId}")
	@Transactional
	public ResponseEntity<Object> epod(@PathVariable String jobId) {

		try {
			String rst = sendEpodService.sendEpodEmail(jobTruckDao.find(jobId));

			return ResponseEntity.ok(rst);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}
	}

	@Deprecated
	@RequestMapping(value = "/epod/{tripId}")
	public ResponseEntity<Object> sendEpodEmail(@PathVariable String tripId) {

		try {
			String epodFilePath = podService.generateShipmentReport(tripId);

			return ResponseEntity.ok(epodFilePath);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}
	}

	@RequestMapping(value = "/epod/jobId/{jobId}")
	public ResponseEntity<Object> refreshEpodFile(@PathVariable String jobId) {

		try {
			List<TCkCtTripDoAttach> doAttachList = sendEpodService.refreshEpodFile(jobId);

			return ResponseEntity.ok(doAttachList);

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}
	}
	@RequestMapping(value = "/sendEpodScheduler")
	public ResponseEntity<Object> sendEpodScheduler() {

		try {
			sendEpodScheduler.doJob();

			return ResponseEntity.ok("");

		} catch (Exception e) {
			log.error("enterExitTimeOfLocation", e);
			return ResponseEntity.ok(e.getMessage());
		}

	}

	/**
	 * 
	 * @param accnId
	 * @return
	 */
	@GetMapping(value = "/downloadExcelTemplate/{accnId}")
	public ResponseEntity<?> downloadExcelExample(@PathVariable String accnId) {

		try {

			String base64 = jobUploadService.downloadExcelExample(accnId);

			return ResponseEntity.ok().body(base64);

		} catch (Exception e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

}
