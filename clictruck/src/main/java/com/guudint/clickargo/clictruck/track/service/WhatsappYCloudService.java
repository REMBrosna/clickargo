package com.guudint.clickargo.clictruck.track.service;

import com.guudint.clickargo.clictruck.track.dao.CkCtTrackNotifyDao;
import com.guudint.clickargo.clictruck.track.dto.StarSenderWhatsappResponseDto;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackNotify;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.external.services.GatewayService;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.common.dao.GenericDao;
import com.vcc.camelone.common.exception.ParameterException;
import com.vcc.camelone.config.model.TCoreSysparam;
import com.vcc.camelone.util.PrincipalUtilService;
import com.vcc.camelone.util.email.SysParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

@Service
public class WhatsappYCloudService extends GatewayService {

	private static final Logger log = Logger.getLogger(WhatsappYCloudService.class);

	@Autowired
	private SysParam sysParam;
	@Autowired
	@Qualifier("coreSysparamDao")
	protected GenericDao<TCoreSysparam, String> sysParamDao;

	@Autowired
	private CkCtTrackNotifyDao ckCtTrackNotifyDao;
	
	@Autowired
	protected PrincipalUtilService principalUtilService;
	
	// Don't rollback transaction even if sending the whatsapp message fails.
	@Transactional(propagation = Propagation.REQUIRES_NEW )
	public void sendYCloudWhatAppMsg(String clasQuinContactNo, String whatsappMobileNumber, ArrayList<String> msgBody, String jobId, String templateName) throws ParameterException {
		log.info("whatsappMobileNumber: " + whatsappMobileNumber + " msgBody: " + msgBody);

		if (StringUtils.isBlank(whatsappMobileNumber)) {
			throw new ParameterException("Parameter mobileNumber is empty.");
		}
		if (msgBody == null || msgBody.size() == 0) {
			throw new ParameterException("Parameter msgBody is empty.");
		}
		
		whatsappMobileNumber = whatsappMobileNumber.replaceAll("\\+", "");
		
		if (!StringUtils.isNumeric(whatsappMobileNumber)) {
			throw new ParameterException("mobileNumber is not a number: " + whatsappMobileNumber);
		}


		TCkCtTrackNotify trackNotify = new TCkCtTrackNotify();
		trackNotify.setTnId(CkUtil.generateId(TCkCtTrackNotify.PREFIX_ID));
		trackNotify.setTnReceiver(whatsappMobileNumber);
		trackNotify.setTnMessage(msgBody.toString());
		trackNotify.setTnJobId(jobId);
		trackNotify.setTnStatus(Constant.ACTIVE_STATUS);
		trackNotify.setTnDtStart(new Date());
		trackNotify.setTnDtCreate(new Date());
		trackNotify.setTnUidCreate("sys");

		try {
			String reqUrl = getReqUrl();
			trackNotify.setTnReqUrl(reqUrl);

			String responseBody = postAuthorization(clasQuinContactNo, reqUrl, msgBody,trackNotify, templateName);
			log.info("WhatsAppYCloud responseBody : " + responseBody);
		} catch (Exception e) {
			log.error("Failed to send WhatsApp message: ", e);
			trackNotify.setTnRemark("Exception: " + e.getMessage());
		} finally {
			try {
				trackNotify.setTnStatus(Constant.ACTIVE_STATUS);
				trackNotify.setTnDtLupd(new Date());
				trackNotify.setTnDtEnd(new Date());

				ckCtTrackNotifyDao.add(trackNotify);

			} catch (Exception e) {
				log.error("Failed to save: ", e);
			}
		}
	}

	private String getReqUrl() throws Exception {
		String baseUrl = sysParam.getValString("CLICTRUCK_STARSENDER_URL", "https://api.ycloud.com/v2/whatsapp/messages/sendDirectly");
		log.info("endPoint: " + baseUrl);
		return baseUrl;
	}

	public String postAuthorization(String clasquinPhoneNum, String url, ArrayList<String> msgBody, TCkCtTrackNotify trackNotify, String templateName) throws Exception {
		TCoreSysparam coreApikey = sysParamDao.find("CLICTRUCK_WHATAPP_YCLOUD_APIKEY");
		TCoreSysparam coreJsonBody = sysParamDao.find("CLICTRUCK_WHATAPP_FORM");
		String apikey = coreApikey.getSysVal();
		String jsonBody = coreJsonBody.getSysVal();

		if (jsonBody == null || jsonBody.isEmpty()) {
			throw new RuntimeException("The WhatsApp JSON body is missing or empty.");
		}
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> templateMap = objectMapper.readValue(jsonBody, new TypeReference<Map<String, Object>>() {});
		@SuppressWarnings("unchecked")
		Map<String, Object> template = (Map<String, Object>) templateMap.get("template");
		template.put("name", templateName);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> components = (List<Map<String, Object>>) template.get("components");
		Map<String, Object> bodyComponent = components.get(0);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> parameters = (List<Map<String, Object>>) bodyComponent.get("parameters");

		// Map msgBody to the parameters
		for (int i = 0; i < msgBody.size(); i++) {
			Map<String, Object> param;
			if (i < parameters.size()) {
				param = parameters.get(i);
			} else {
				param = new HashMap<>();
				parameters.add(param);
			}
			param.put("type", "text");
			param.put("text", msgBody.get(i));
		}

		//get default phone number
		String from = null;
		if (clasquinPhoneNum != null){
			from = clasquinPhoneNum;
		}else {
			from = (String) templateMap.get("from");
		}
		templateMap.put("from", from);
		templateMap.put("to", trackNotify.getTnReceiver());
		templateMap.put("template", template);

		// Convert the updated map back to JSON string
		String paramJson = objectMapper.writeValueAsString(templateMap);

		// Create the HTTP client and send the request
		WebTarget target = super.getClient().target(url);
		Response response = target.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header("X-API-Key", apikey)
				.post(Entity.entity(paramJson, MediaType.APPLICATION_JSON));

		String responseStr = response.readEntity(String.class);
		trackNotify.setTnHttpStatus(String.valueOf(response.getStatus()));
		trackNotify.setTnRspBody(responseStr);
		log.info("Body Json What App: " + paramJson);
		log.info("response: " + responseStr);
		return responseStr;
	}
}
