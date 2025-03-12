package com.guudint.clickargo.clictruck.track.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.guudint.clickargo.clictruck.planexec.job.dao.CkJobTruckDao;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.dao.CkCtTripDao;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.guudint.clickargo.clictruck.track.dao.CkCtTrackLocDao;
import com.guudint.clickargo.clictruck.track.dao.CkCtTrackLocItemDao;
import com.guudint.clickargo.clictruck.track.dto.EntryExitTimeDto;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackLoc;
import com.guudint.clickargo.clictruck.track.model.TCkCtTrackLocItem;
import com.guudint.clickargo.clictruck.util.NumberUtil;
import com.guudint.clickargo.common.CkUtil;
import com.guudint.clickargo.common.model.TCkRecordDate;
import com.guudint.clickargo.external.services.GatewayService;
import com.vcc.camelone.ccm.util.Constant;
import com.vcc.camelone.util.email.SysParam;

/**
 * Call https://ext.logistics.myascents.net/api/3rdparty/unit/enterexit to get
 * the enter and exit times for a location based of GPS long-lat coordinates
 * 
 * @author
 *
 */
@Service
public class TrackTraceEnterExitLocService extends GatewayService {

	// Static Attributes
	////////////////////
	private static Logger log = Logger.getLogger(TrackTraceEnterExitLocService.class);

	@Autowired
	private CkJobTruckDao ckJobTruckDao;

	@Autowired
	SysParam sysParam;

	@Autowired
	CkCtTripDao CkCtTripDao;

	@Autowired
	CkCtTrackLocDao ckCtTrackLocDao;

	@Autowired
	private CkCtTrackLocItemDao ckCtTrackLocItemDao;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public void getEnterExitTimeOfLocation(String jobTruckId) {

		if (StringUtils.isBlank(jobTruckId)) {
			log.error("Parameter jobTruckId is empty.");
		}

		TCkCtTrackLoc trackLoc = new TCkCtTrackLoc();
		trackLoc.setTlId(CkUtil.generateId(TCkCtTrackLoc.PREFIX_ID));

		trackLoc.setTlStatus(Constant.ACTIVE_STATUS);
		trackLoc.setTlDtCreate(new Date());
		trackLoc.setTlUidCreate("sys");

		try {

			// get end point
			String reqUrl = this.getReqUrl(jobTruckId, trackLoc);

			if (reqUrl != null) {
				// call end point
				// Map<"1.62998668,103.688384", Map<"10",
				String responsebody = this.callAPI(reqUrl);
				trackLoc.setTlRspBody(responsebody);

				Map<String, Map<String, EntryExitTimeDto>> map = objectMapper.readValue(responsebody,
						new TypeReference<Map<String, Map<String, EntryExitTimeDto>>>() {
						});

				trackLoc = this.setTrackLocItem(map, trackLoc);

				trackLoc.setTlStatus(Constant.ACTIVE_STATUS);
				trackLoc.setTlDtLupd(new Date());
			}

		} catch (Exception e) {

			log.error("Fail to get Track and Trace Location enter and exit time: " + e.getMessage());
			trackLoc.setTlRemark("Exception: " + e.getMessage());

		} finally {
			try {
				ckCtTrackLocDao.add(trackLoc);
				List<TCkCtTrackLocItem> ckCtTrackLocItems = trackLoc.getTCkCtTrackLocItems();

				if (ckCtTrackLocItems != null && ckCtTrackLocItems.size() > 0) {
					for (TCkCtTrackLocItem item : ckCtTrackLocItems) {
						ckCtTrackLocItemDao.add(item);
					}
				}
			} catch (Exception e) {
				log.error("Fail to save : ", e);
			}
		}
	}

	// http://URL/location/enterexit?imei=860896050449388&start=1694966401&end=1695052799&r=10,30,100&loc=1.49620117,103.70765293&loc=1.62998668,103.688384

	private String getReqUrl(String jobTruckId, TCkCtTrackLoc trackLoc) throws Exception {

		String iMei, startTimeStr, endTimeStr;
		String locFrom = null, locTo = null, locDepot = null;
		String radius = "10,30,100";

		// 1: URL from T_CORE_SYSPARAM;
		String url = sysParam.getValString("CLICTRUCK_TRACK_TIME_URL", "");

		// 2: job information
		TCkJobTruck job = ckJobTruckDao.find(jobTruckId);
		trackLoc.setTCkJobTruck(job);

		if (job.getTCkCtVeh() != null) {
			iMei = job.getTCkCtVeh().getVhGpsImei();

			if (StringUtils.isBlank(iMei)) {
				throw new Exception("iMei is null for: " + jobTruckId + " vhId: " + job.getTCkCtVeh().getVhId());
			}

			TCkRecordDate recordDate = job.getTCkJob().getTCkRecordDate();

			Date startTime = recordDate.getRcdDtStart();
			Date endTime = job.getJobDtDelivery(); // delivered time.
			if (null == endTime) {
				endTime = new Date();
			}
			startTimeStr = startTime.getTime() / 1000 + "";
			endTimeStr = endTime.getTime() / 1000 + "";

			// startTimeStr = "1694966400";
			// endTimeStr = "1695027600";

			// 2: trip information
			List<TCkCtTrip> tripList = CkCtTripDao.findByJobId(jobTruckId);

			if (tripList == null || tripList.size() == 0) {
				throw new Exception("Fail to find trip by job truck id: " + jobTruckId);
			}

			TCkCtTrip trip = tripList.get(0);
			if (trip.getTCkCtTripLocationByTrFrom() != null) {
				locFrom = trip.getTCkCtTripLocationByTrFrom().getTCkCtLocation().getLocGps();
			}
			if (trip.getTCkCtTripLocationByTrTo() != null) {
				locTo = trip.getTCkCtTripLocationByTrTo().getTCkCtLocation().getLocGps();
			}
			if (trip.getTCkCtTripLocationByTrDepot() != null) {
				locDepot = trip.getTCkCtTripLocationByTrDepot().getTCkCtLocation().getLocGps();
			}

			// remove [,] and blank character.
			if (!StringUtils.isBlank(locFrom))
				locFrom = locFrom.replace("[", "").replace("]", "").replace(" ", "");

			if (!StringUtils.isBlank(locTo))
				locTo = locTo.replace("[", "").replace("]", "").replace(" ", "");

			if (!StringUtils.isBlank(locDepot))
				locDepot = locDepot.replace("[", "").replace("]", "").replace(" ", "");

			String urlParam = String.format("&imei=%s&start=%s&end=%s&r=%s", iMei, startTimeStr, endTimeStr, radius);
			if (!StringUtils.isBlank(locFrom))
				urlParam = urlParam + "&loc=" + locFrom;
			if (!StringUtils.isBlank(locTo))
				urlParam = urlParam + "&loc=" + locTo;
			if (!StringUtils.isBlank(locDepot))
				urlParam = urlParam + "&loc=" + locDepot;

			String endPoint = url + urlParam;

			// set TCkCtTrackLoc
			trackLoc.setTlImei(iMei);
			trackLoc.setTlDtStart(startTime);
			trackLoc.setTlDtEnd(endTime);
			trackLoc.setTlReqUrl(urlParam);

			log.info("endPoint: " + endPoint);
			return endPoint;
		}

		return null;

	}

	private String callAPI(String url) throws JsonParseException, JsonMappingException, IOException {

		Builder builder = super.getClient().target(url).request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

		Response response = builder.get();

		String str = response.readEntity(String.class);
		log.info("response: " + str);

		return str;
	}

	private TCkCtTrackLoc setTrackLocItem(Map<String, Map<String, EntryExitTimeDto>> map, TCkCtTrackLoc trackLoc) {

		List<TCkCtTrackLocItem> itemList = new ArrayList<TCkCtTrackLocItem>(0);

		// consume response
		for (Map.Entry<String, Map<String, EntryExitTimeDto>> e1 : map.entrySet()) {
			log.info(" " + e1.getKey() + " " + e1.getValue());

			for (Map.Entry<String, EntryExitTimeDto> e2 : e1.getValue().entrySet()) {
				log.info("\t " + e2.getKey() + " " + e2.getValue());

				TCkCtTrackLocItem tlItem = new TCkCtTrackLocItem();
				tlItem.setTliId(CkUtil.generateId(TCkCtTrackLoc.PREFIX_ID));
				tlItem.setTCkCtTrackLoc(trackLoc);
				tlItem.setTCkCtLocation(null);
				tlItem.setTliGps(e1.getKey());

				tlItem.setTliRadius(NumberUtil.toInteger(e2.getKey()));
				tlItem.setTliDtEnter(new Date(e2.getValue().getEnter() * 1000));
				tlItem.setTliDtExit(new Date(e2.getValue().getExit() * 1000));

				tlItem.setTliStatus(Constant.ACTIVE_STATUS);
				tlItem.setTliDtCreate(new Date());
				tlItem.setTliUidCreate("sys");

				itemList.add(tlItem);
			}
		}

		trackLoc.setTCkCtTrackLocItems(itemList);

		return trackLoc;
	}
}
