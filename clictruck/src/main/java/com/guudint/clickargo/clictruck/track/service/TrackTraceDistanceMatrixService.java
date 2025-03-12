package com.guudint.clickargo.clictruck.track.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

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
import com.guudint.clickargo.clictruck.track.dao.CkCtTrackDistanceDao;
import com.guudint.clickargo.clictruck.track.dto.DistanceMatrixDto;
import com.guudint.clickargo.clictruck.track.dto.DistanceMatrixDto.Element;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackDistance;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.external.services.GatewayService;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.util.email.SysParam;

/**
 * Call https://maps.googleapis.com/maps/api/distancematrix/json to get estimate
 * distance and time to destination
 * 
 * @author
 *
 */
@Service
public class TrackTraceDistanceMatrixService extends GatewayService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TrackTraceDistanceMatrixService.class);
	private static final String TRANSPORTATION_MODE = "driving";

	@Autowired
	SysParam sysParam;

	@Autowired
	CkCtTrackDistanceDao ckCtTrackDistanceDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public DistanceMatrixDto getDistanceMatrix(String origin, String destination, String jobId) {

		log.info("origin:" + origin + "  destination:" + destination);

		if (StringUtils.isBlank(origin)) {
			log.error("Parameter origin is empty.");
		}
		if (StringUtils.isBlank(destination)) {
			log.error("Parameter destination is empty.");
		}

		TCkCtTrackDistance trackDistance = new TCkCtTrackDistance();
		trackDistance.setTdId(CkUtil.generateId(TCkCtTrackDistance.PREFIX_ID));
		trackDistance.setTdOrigins(origin);
		trackDistance.setTdDestination(destination);
		trackDistance.setTdTransportaionMode(TRANSPORTATION_MODE);
		trackDistance.setTdJobId(jobId);

		trackDistance.setTdStatus(Constant.ACTIVE_STATUS);
		trackDistance.setTdDtStart(new Date());
		trackDistance.setTdDtCreate(new Date());
		trackDistance.setTdUidCreate("sys");

		try {

			// get end point
			String reqUrl = this.getReqUrl(origin, destination, "imperial", TRANSPORTATION_MODE, trackDistance);

			// call end point
			String responsebody = this.callAPI(reqUrl, trackDistance);

			DistanceMatrixDto distinanceMatrix = objectMapper.readValue(responsebody, DistanceMatrixDto.class);
			
			Optional.of(distinanceMatrix).map( d -> d.getRows()).ifPresent(
					rows -> {
						if(rows != null && rows.size()> 0 ) {
							ArrayList<Element> elementList = rows.get(0).getElements();
							if(elementList != null && elementList.size() >0 ) {
								trackDistance.setTdDistance(Optional.of(elementList.get(0)).map( Element::getDistance).map( d -> d.getValue()).orElse(null));
								trackDistance.setTdDuration(Optional.of(elementList.get(0)).map( Element::getDuration).map( d -> d.getValue()).orElse(null));
							}
						}
					});;

			log.info(distinanceMatrix);
			return distinanceMatrix;

		} catch (Exception e) {

			log.error("Fail to get Track and Trace Distance and Duration: ", e);
			trackDistance.setTdRemark("Exception: " + e.getMessage());

		} finally {
			try {
				trackDistance.setTdStatus(Constant.ACTIVE_STATUS);
				trackDistance.setTdDtLupd(new Date());
				trackDistance.setTdDtEnd(new Date());

				ckCtTrackDistanceDao.add(trackDistance);

			} catch (Exception e) {
				log.error("Fail to save : ", e);
			}
		}
		return null;
	}

	// https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyAWR5l1cNa6HP-iixR3Rb9gg5qKHljOvG4&origins=New
	// York City NY| Miami&destinations=Washington DC| San Francisco
	// &units=imperial&mode=driving

	private String getReqUrl(String origin, String destination, String units, String mode,
							 TCkCtTrackDistance trackDistance) throws Exception {

		// 1: URL from T_CORE_SYSPARAM;
		String url = sysParam.getValString("CLICTRUCK_TRACK_DISTINCAT_MATRIX_URL",
				"https://maps.googleapis.com/maps/api/distancematrix/json");

		String apiKey = sysParam.getValString("CLICTRUCK_GOOGLE_MAP_KEY", null);

		// Remove brackets if they are present
		origin = origin.replace("[", "").replace("]", "").trim();
		destination = destination.replace("[", "").replace("]", "").trim();

		// Encode the coordinates
		String encodedOrigin = URLEncoder.encode(origin, StandardCharsets.UTF_8.toString());
		String encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8.toString());

		String parameters = String.format("&units=%s&mode=%s&origins=%s&destinations=%s", units, mode,
				encodedOrigin, encodedDestination);

		String endPoint = url + "?key=" + apiKey + parameters;

		trackDistance.setTdReqUrl(parameters);
		log.info("endPoint: " + endPoint);
		return endPoint;
	}


	private String callAPI(String url, TCkCtTrackDistance trackDistance)
			throws JsonParseException, JsonMappingException, IOException {

		Builder builder = super.getClient().target(url).request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

		Response response = builder.get();
		String str = response.readEntity(String.class);

		trackDistance.setTdHttpStatus(response.getStatus() + "");
		trackDistance.setTdRspBody(str);
		log.info("response: " + str);

		return str;
	}

}
