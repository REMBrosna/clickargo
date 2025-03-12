package com.guudint.clickargo.clictruck.track.dto;

import java.io.Serializable;

public class ImeiLatestStatusDto implements Serializable {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;

	// Attributes
	/////////////
	public String uid;
	public String name;
	public String dateTime;
	public int time;
	public double lat;
	public double lng;
	public double alt;
	public boolean ignition;

	// Constructor
	public ImeiLatestStatusDto() {
		super();
	}

	@Override
	public String toString() {
		return "ImeiLatestStatusDto{" +
				"uid='" + uid + '\'' +
				", name='" + name + '\'' +
				", dateTime='" + dateTime + '\'' +
				", time=" + time +
				", lat=" + lat +
				", lng=" + lng +
				", alt=" + alt +
				", ignition=" + ignition +
				'}';
	}

	///
	public String getGPS() {
		return lat + "," + lng;
	}

	// Properties
	/////////////
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
	}

	public boolean isIgnition() {
		return ignition;
	}

	public void setIgnition(boolean ignition) {
		this.ignition = ignition;
	}
}
