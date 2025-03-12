package com.guudint.clickargo.clictruck.dto;

public class LocationFields {

	private String locationFrom;
	private String locationTo;
	
	public LocationFields() {

	}

	public LocationFields(String locationFrom, String locationTo) {
		super();
		this.locationFrom = locationFrom;
		this.locationTo = locationTo;
	}

	public String getLocationFrom() {
		return locationFrom;
	}
	
	public void setLocationFrom(String locationFrom) {
		this.locationFrom = locationFrom;
	}
	
	public String getLocationTo() {
		return locationTo;
	}
	
	public void setLocationTo(String locationTo) {
		this.locationTo = locationTo;
	}
	
}
