package com.guudint.clickargo.clictruck.opm.third.dto;

import java.io.Serializable;

public abstract class OpmCreditReq implements Serializable {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////
	private int rowId; // excel file rowId;
	private String tax_no;

	// Constructors
	///////////////
	public OpmCreditReq() {
	}
	public OpmCreditReq(int rowId) {
		this.rowId = rowId;
	}

	// Properties
	/////////////
	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public String getTax_no() {
		return tax_no;
	}

	public void setTax_no(String tax_no) {
		this.tax_no = tax_no;
	}

}
