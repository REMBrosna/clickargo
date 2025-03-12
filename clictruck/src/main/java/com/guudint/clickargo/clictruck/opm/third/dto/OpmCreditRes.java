package com.guudint.clickargo.clictruck.opm.third.dto;

import java.io.Serializable;

public class OpmCreditRes implements Serializable {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////
	private String err_code;
	private String err_msg;
	// Constructors
	///////////////

	// Properties
	/////////////
	public String getErr_code() {
		return err_code;
	}
	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}
	public String getErr_msg() {
		return err_msg;
	}
	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}
	
}
