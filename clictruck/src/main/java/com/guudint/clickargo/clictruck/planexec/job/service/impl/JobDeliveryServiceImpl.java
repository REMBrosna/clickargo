package com.guudint.clickargo.clictruck.planexec.job.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.guudint.clickargo.clictruck.track.service.WhatsappYCloudService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.planexec.job.service.IJobDeliveryService;
import com.guudint.clickargo.clictruck.track.dto.DistanceMatrixDto;
import com.guudint.clickargo.clictruck.track.dto.ImeiLatestStatusDto;
import com.guudint.clickargo.clictruck.track.dto.StarSenderWhatsappResponseDto;
import com.guudint.clickargo.clictruck.track.service.StarSenderWhatsappService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceDistanceMatrixService;
import com.guudint.clickargo.clictruck.track.service.TrackTraceLatestStatusService;
import com.vcc.camelone.common.exception.ParameterException;

@Service
public class JobDeliveryServiceImpl implements IJobDeliveryService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(JobDeliveryServiceImpl.class);

	@Autowired
	TrackTraceLatestStatusService trackTraceLatestStatusService;

	@Autowired
	TrackTraceDistanceMatrixService trackTraceDistanceMatrixService;

	@Autowired
	StarSenderWhatsappService starSenderWhatsappService;
	@Autowired
	private WhatsappYCloudService whatsappYCloudService;
	@Override
	public List<ImeiLatestStatusDto> getImeiLocation(String iMei, String jobTruckId) throws Exception {

		try {
			return trackTraceLatestStatusService.getImeiLocation(iMei, jobTruckId);

		} catch (Exception e) {
			log.error("Fail to find latest location.", e);
			throw e;
		}
	}

	@Override
	public DistanceMatrixDto getDistanceMatrix(String origin, String destination, String jobId) throws Exception {

		try {
			return trackTraceDistanceMatrixService.getDistanceMatrix(origin, destination, jobId);

		} catch (Exception e) {
			log.error("Fail to compute distance matirx.", e);
			throw e;
		}
	}

	@Override
	public StarSenderWhatsappResponseDto sendWhatAppMsg(String whatsappmobileNumber, String msgBody, String jobId)
			throws ParameterException {

		try {
			return starSenderWhatsappService.sendWhatAppMsg(whatsappmobileNumber, msgBody, jobId);

		} catch (Exception e) {
			log.error("Fail to send Whatsapp message.", e);
			throw e;
		}
	}
	@Override
	public void sendYCloudWhatAppMsg(String clasQuinContactNo, String whatsappMobileNumber, ArrayList<String> msgBody, String jobId, String templateName) throws ParameterException {
		try {
			// Attempt using whatsappYCloudService
            whatsappYCloudService.sendYCloudWhatAppMsg(clasQuinContactNo, whatsappMobileNumber, msgBody, jobId, templateName);
        } catch (Exception e) {
			log.error("Failed to send Whatsapp message using WhatsappYCloudService. : " + e.getMessage());
			// throw e;
		}
	}


}
