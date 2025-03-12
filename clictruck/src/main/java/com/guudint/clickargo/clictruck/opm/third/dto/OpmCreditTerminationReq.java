package com.guudint.clickargo.clictruck.opm.third.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OpmCreditTerminationReq extends OpmCreditReq {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////
	private long facility_limit;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date close_date;
	
	private String action;

	// Constructors
	///////////////
	public OpmCreditTerminationReq() {
		
	}
	
	public OpmCreditTerminationReq(int rowId) {
		super(rowId);
	}

	// Properties
	/////////////

	public long getFacility_limit() {
		return facility_limit;
	}
	public void setFacility_limit(long facility_limit) {
		this.facility_limit = facility_limit;
	}
	public Date getClose_date() {
		return close_date;
	}
	public void setClose_date(Date close_date) {
		this.close_date = close_date;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

}