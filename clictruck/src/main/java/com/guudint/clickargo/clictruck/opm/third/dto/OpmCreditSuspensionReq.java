package com.guudint.clickargo.clictruck.opm.third.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OpmCreditSuspensionReq extends OpmCreditReq {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////
	private long facility_limit;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date suspend_date;
	
	private String action;

	// Constructors
	///////////////
	public OpmCreditSuspensionReq() {
		
	}
	
	public OpmCreditSuspensionReq(int rowId) {
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
	public Date getSuspend_date() {
		return suspend_date;
	}
	public void setSuspend_date(Date suspend_date) {
		this.suspend_date = suspend_date;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

}
