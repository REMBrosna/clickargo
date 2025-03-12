package com.guudint.clickargo.clictruck.track.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guudint.clickargo.clictruck.common.service.impl.CkCtLocationServiceImpl;
import com.vcc.camelone.util.email.SysParam;

/**
 * Call Google API to get GPS 
 * @author 
 *
 */
@Service
public class TrackTraceCoordinateService {

	private static final Logger log = Logger.getLogger(CkCtLocationServiceImpl.class);

	@Autowired
	SysParam sysParam;

	/**
	 * 
	 * @param address: like: 2 Tanjong Katong Road #05-01 Paya Lebar Quarter, PLQ, 3, Singapore
	 * @return : [1.316146, 103.8934519]
	 */
	public String fetchCoordinate(String address) {

		log.info("FetchCoordinate " + address);

		try {

			String apiKey = sysParam.getValString("CLICTRUCK_GOOGLE_MAP_KEY", null);

			if (null == apiKey) {
				return null;
			}

			String encodeAddress = URLEncoder.encode(address.replaceAll(" ", "+"), "UTF-8");
			
			String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address="
					+ encodeAddress + "&key=" + URLEncoder.encode(apiKey, "UTF-8");

			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			JSONObject json = new JSONObject(response.toString());

			log.info(json.toString());

			if (json.getString("status").equals("OK")) {
				JSONArray results = json.getJSONArray("results");
				JSONObject result = results.getJSONObject(0);
				JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
				double latitude = location.getDouble("lat");
				double longitude = location.getDouble("lng");

				double[] latLong = new double[] { latitude, longitude };

				return Arrays.toString(latLong);
			} else {
				log.error("Fail to find geocode: " + address);
			}
		} catch (IOException e) {
			log.error("", e);
			return null;
		}
		return null;
	}

}
