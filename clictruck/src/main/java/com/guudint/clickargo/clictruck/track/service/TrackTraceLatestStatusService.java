package com.guudint.clickargo.clictruck.track.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.guudint.clickargo.clictruck.track.dao.CkCtTrackImeiStatusDao;
import com.guudint.clickargo.clictruck.track.dto.ImeiLatestStatusDto;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackImeiStatus;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.external.services.GatewayService;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.util.email.SysParam;

/**
 * Call https://ext.logistics.myascents.net/api/3rdparty/unit/lateststatus to
 * get iMei current location
 * 
 * @author
 *
 */
@Service
public class TrackTraceLatestStatusService extends GatewayService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TrackTraceLatestStatusService.class);

	@Autowired
	SysParam sysParam;

	@Autowired
	CkCtTrackImeiStatusDao ckCtTrackImeiStatusDao;

	/**
	 * 
	 * @param iMei
	 * @param jobTruckId
	 * @return : [{"uid":"860896050372135","name":"GBL8760A-0372135","dateTime":"2023-11-22T06:03:37","time":1700633017,"lat":1.351368,"lng":103.970808,"alt":-4.0}]
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public List<ImeiLatestStatusDto> getImeiLocation(String iMei, String jobTruckId) {

		log.info("iMei:" + iMei + " jobTruckId: " + jobTruckId);

		if (StringUtils.isBlank(iMei)) {
			log.error("Parameter iMei is empty.");
		}

		TCkCtTrackImeiStatus trackImeiStatus = new TCkCtTrackImeiStatus();
		trackImeiStatus.setTisId(CkUtil.generateId(TCkCtTrackImeiStatus.PREFIX_ID));
		trackImeiStatus.setTisImei(iMei);
		trackImeiStatus.setTisJobId(jobTruckId);

		trackImeiStatus.setTisStatus(Constant.ACTIVE_STATUS);
		trackImeiStatus.setTisDtStart(new Date());
		trackImeiStatus.setTisDtCreate(new Date());
		trackImeiStatus.setTisUidCreate("sys");

		try {

			// get end point
			String reqUrl = this.getReqUrl(iMei, trackImeiStatus);

			// call end point
			String responsebody = this.callAPI(reqUrl, trackImeiStatus);

			List<ImeiLatestStatusDto> locList = objectMapper.readValue(responsebody,
					new TypeReference<List<ImeiLatestStatusDto>>() {
					});

			if (locList != null && locList.size() > 0) {
				trackImeiStatus.setTisGps(locList.get(0).getGPS());
			}

			log.info(locList);

			return locList;

		} catch (Exception e) {

			log.error("Fail to get Track and Trace latest location: ", e);
			trackImeiStatus.setTisRemark("Exception: " + e.getMessage());

		} finally {
			try {
				trackImeiStatus.setTisStatus(Constant.ACTIVE_STATUS);
				trackImeiStatus.setTisDtLupd(new Date());
				trackImeiStatus.setTisDtEnd(new Date());

				ckCtTrackImeiStatusDao.add(trackImeiStatus);

			} catch (Exception e) {
				log.error("Fail to save : ", e);
			}
		}
		return null;
	}

	// https://ext.logistics.myascents.net/api/3rdparty/unit/lateststatus?key=$2a$11$oo7.I606K/DVjUdZi88UzeFu.tLQiwTChnty4KATKbf3Dq0T8s2Ii&iMei=860896050372135

	private String getReqUrl(String iMei, TCkCtTrackImeiStatus trackImeiStatus) throws Exception {

		// 1: URL from T_CORE_SYSPARAM;
		String url = sysParam.getValString("CLICTRUCK_TRACK_LATESTSTATUS_URL", "");
		String parameters = "&ident=" + iMei;
		String endPoint = url + parameters;

		trackImeiStatus.setTisReqUrl(parameters);
		log.info("endPoint: " + endPoint);

		return endPoint;
	}

	private String callAPI(String url, TCkCtTrackImeiStatus trackImeiStatus)
			throws JsonParseException, JsonMappingException, IOException {

		Builder builder = super.getClient().target(url).request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

		Response response = builder.get();
		String str = response.readEntity(String.class);

		trackImeiStatus.setTisHttpStatus(response.getStatus() + "");
		trackImeiStatus.setTisRspBody(str);
//		log.info("response status: " + response.getStatus() + " response body : " + str);

		return str;
	}

}
