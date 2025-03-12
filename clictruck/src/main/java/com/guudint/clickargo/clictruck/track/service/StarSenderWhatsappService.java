package com.guudint.clickargo.clictruck.track.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.guudint.clickargo.clictruck.track.dao.CkCtTrackNotifyDao;
import com.guudint.clickargo.clictruck.track.dto.StarSenderWhatsappResponseDto;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackNotify;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.external.services.GatewayService;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.util.email.SysParam;

/**
 * Send whatsapp message.
 * https://starsender.online/api/sendText?tujuan=6596525690@s.whatsapp.net&message=Lunch
 * @author zhangji
 *
 */
@Service
@Deprecated
public class StarSenderWhatsappService extends GatewayService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(StarSenderWhatsappService.class);

	@Autowired
	SysParam sysParam;

	@Autowired
	CkCtTrackNotifyDao ckCtTrackNotifyDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public StarSenderWhatsappResponseDto sendWhatAppMsg(String whatsappmobileNumber, String msgBody, String jobId) throws ParameterException {
		
		log.info("whatsappmobileNumber:" + whatsappmobileNumber + " msgBody: " + msgBody);

		if (StringUtils.isBlank(whatsappmobileNumber)) {
			throw new ParameterException("Parameter mobileNumber is empty.");
		}
		
		if (StringUtils.isBlank(msgBody)) {
			throw new ParameterException("Parameter msgBody is empty.");
		}
		
		if(!StringUtils.isNumeric(whatsappmobileNumber)) {
			throw new ParameterException("mobileNumber is not number.");
		}

		TCkCtTrackNotify trackNotify = new TCkCtTrackNotify();
		trackNotify.setTnId(CkUtil.generateId(TCkCtTrackNotify.PREFIX_ID));
		trackNotify.setTnReceiver(whatsappmobileNumber);
		trackNotify.setTnMessage(msgBody); 
		trackNotify.setTnJobId(jobId);

		trackNotify.setTnStatus(Constant.ACTIVE_STATUS);
		trackNotify.setTnDtStart(new Date());
		trackNotify.setTnDtCreate(new Date());
		trackNotify.setTnUidCreate("sys");
		// msgBody, should have length limit.
		try {

			// get end point
			String reqUrl = this.getReqUrl(whatsappmobileNumber, msgBody);
			trackNotify.setTnReqUrl(reqUrl);

			// call end point
			String responsebody = this.callAPI(reqUrl, trackNotify);
			
			StarSenderWhatsappResponseDto starSender = objectMapper.readValue(responsebody, StarSenderWhatsappResponseDto.class);
			
			log.info(starSender);
			if( starSender != null ) {
				// response success or failed.
				trackNotify.setTnSendStatus(starSender.isStatus()?1:-1);
			} else {
				// no response
				trackNotify.setTnSendStatus(-2);
			}
			
			return starSender;

		} catch (Exception e) {

			log.error("Fail to send Whatsapp message: ", e);
			trackNotify.setTnRemark("Exception: " + e.getMessage());

		} finally {

			try {
				trackNotify.setTnStatus(Constant.ACTIVE_STATUS);
				trackNotify.setTnDtLupd(new Date());
				trackNotify.setTnDtEnd(new Date());

				ckCtTrackNotifyDao.add(trackNotify);

			} catch (Exception e) {
				log.error("Fail to save : ", e);
			}
		}
		return null;
	}

	// https://starsender.online/api/sendText?tujuan=6596525690@s.whatsapp.net&message=Lunch

	private String getReqUrl(String whatsappmobileNumber, String msgBody) throws Exception {

		// 1: URL from T_CORE_SYSPARAM;
		String url = sysParam.getValString("CLICTRUCK_STARSENDER_URL", "https://starsender.online/api/sendText");

		String endPoint = url + "?tujuan=" + whatsappmobileNumber + "@s.whatsapp.net&message=" + URLEncoder.encode(msgBody, StandardCharsets.UTF_8.toString());

		log.info("endPoint: " + endPoint);

		return endPoint;
	}

	private String callAPI(String url, TCkCtTrackNotify trackNotify) throws JsonParseException, JsonMappingException, IOException {

		String apikey = sysParam.getValString("CLICTRUCK_STARSENDER_APIKEY", "013f4232fe0517cb0c83e2a74626009d1a599f8f");
		
		Builder builder = super.getClient().target(url).request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("apikey", apikey);

		Response response = builder.post(null);

		String str = response.readEntity(String.class);
		
		trackNotify.setTnHttpStatus(response.getStatus() + "");
		trackNotify.setTnRspBody(str);
		
		log.info("response: " + str);

		return str;
	}

}
