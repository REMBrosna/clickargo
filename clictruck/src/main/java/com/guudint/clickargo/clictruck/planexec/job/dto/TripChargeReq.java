package com.guudint.clickargo.clictruck.planexec.job.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TripChargeReq {

	private String toAccn, coFfAccn, currency, locFrom, locTo, vehType;

	public TripChargeReq() {

	}

	public TripChargeReq(String toAccn, String coFfAccn, String currency, String locFrom, String locTo,
			String vehType) {
		super();
		this.toAccn = toAccn;
		this.coFfAccn = coFfAccn;
		this.currency = currency;
		this.locFrom = locFrom;
		this.locTo = locTo;
		this.vehType = vehType;
	}

	public String getToAccn() {
		return toAccn;
	}

	public void setToAccn(String toAccn) {
		this.toAccn = toAccn;
	}

	public String getCoFfAccn() {
		return coFfAccn;
	}

	public void setCoFfAccn(String coFfAccn) {
		this.coFfAccn = coFfAccn;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLocFrom() {
		return locFrom;
	}

	public void setLocFrom(String locFrom) {
		this.locFrom = locFrom;
	}

	public String getLocTo() {
		return locTo;
	}

	public void setLocTo(String locTo) {
		this.locTo = locTo;
	}

	public String getVehType() {
		return vehType;
	}

	public void setVehType(String vehType) {
		this.vehType = vehType;
	}

}
