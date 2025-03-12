package com.guudint.clickargo.clictruck.common.dto;

public class EventCallbackNotification {
	
	private String imei;
	private Integer distance;
	private String dateTime;
	private Long time;

	public EventCallbackNotification() {
	}

	public EventCallbackNotification(String imei, Integer distance, String dateTime, Long time) {
		super();
		this.imei = imei;
		this.distance = distance;
		this.dateTime = dateTime;
		this.time = time;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
	
}
