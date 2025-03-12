package com.guudint.clickargo.clictruck.opm.third.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OpmCreditUnSuspensionReq extends OpmCreditReq {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////
	private long facility_limit;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date unsuspend_date;
	private String action;

	// Constructors
	///////////////
	public OpmCreditUnSuspensionReq() {
		
	}
	
	public OpmCreditUnSuspensionReq(int rowId) {
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
	public Date getUnsuspend_date() {
		return unsuspend_date;
	}
	public void setUnsuspend_date(Date unsuspend_date) {
		this.unsuspend_date = unsuspend_date;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

}
