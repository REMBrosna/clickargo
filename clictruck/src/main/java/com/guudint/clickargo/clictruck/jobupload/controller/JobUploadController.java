package com.guudint.clickargo.clictruck.jobupload.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guudint.clickargo.clictruck.jobupload.dao.CkJobUploadDao;
import com.guudint.clickargo.clictruck.jobupload.dto.CkJobUpload;
import com.guudint.clickargo.clictruck.jobupload.model.TCkJobUpload;
import com.guudint.clickargo.clictruck.jobupload.service.JobUploadService;
import com.vcc.camelone.cac.model.Principal;
import com.vcc.camelone.common.attach.dto.CoreAttach;
import com.vcc.camelone.common.exception.ProcessingException;
import com.vcc.camelone.common.service.ServiceError;
import com.vcc.camelone.common.service.ServiceStatus;
import com.vcc.camelone.util.PrincipalUtilService;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/clickargo/clictruck/jobUpload")
public class JobUploadController {

	@Autowired
	private PrincipalUtilService principalUtilService;
	@Autowired
	private JobUploadService jobUploadService;
	@Autowired
	private CkJobUploadDao ckJobUploadDao;

	@PostMapping
	public ResponseEntity<?> uploadExcel(@RequestBody CoreAttach coreAttach) {

		try {
			TCkJobUpload jobUpload = jobUploadService.uploadExcel(principalUtilService.getPrincipal(),
					coreAttach.getAttData(), coreAttach.getAttName());

			return ResponseEntity.ok().body(jobUpload);

		} catch (Exception e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping(value = "/downloadExcelTemplate")
	public ResponseEntity<?> downloadExcelExample() {

		try {

			Principal principal = principalUtilService.getPrincipal();
			if (principal == null) {
				throw new ProcessingException("principal is null");
			}
			
			String base64 = jobUploadService.downloadExcelExample(principal.getUserAccnId());

			return ResponseEntity.ok().body(base64);

		} catch (Exception e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/list")
	public ResponseEntity<?> uploadList() {

		try {
			List<TCkJobUpload> jobUploadList = ckJobUploadDao
					.findByAccn(principalUtilService.getPrincipal().getUserAccnId());

			List<CkJobUpload> jobUploadDtoList = jobUploadList.stream().map(up -> new CkJobUpload(up))
					.collect(Collectors.toList());

			return ResponseEntity.ok().body(jobUploadDtoList);

		} catch (Exception e) {
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatus(ServiceStatus.STATUS.EXCEPTION);
			serviceStatus.setErr(new ServiceError(-100, ExceptionUtils.getStackTrace(e)));
			return new ResponseEntity<>(serviceStatus, HttpStatus.BAD_REQUEST);
		}

	}
}
