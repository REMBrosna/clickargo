package com.guudint.clickargo.clictruck.opm.third.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OpmCreditApprovalReq extends OpmCreditReq {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////
	private long facility_limit;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date approval_date;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date expiry_date;

	// Constructors
	///////////////
	public OpmCreditApprovalReq() {
		
	}
	
	public OpmCreditApprovalReq(int rowId) {
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

	public Date getApproval_date() {
		return approval_date;
	}

	public void setApproval_date(Date approval_date) {
		this.approval_date = approval_date;
	}

	public Date getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}

}
