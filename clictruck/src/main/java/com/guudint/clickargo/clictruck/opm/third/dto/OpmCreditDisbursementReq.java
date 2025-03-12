package com.guudint.clickargo.clictruck.opm.third.dto;

import java.util.Date;

public class OpmCreditDisbursementReq extends OpmCreditReq {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////

	private String reference_number;
	private long loan_approved;
	private long provision_fee_amt;
	private long disbursement_amt;
	private Date loan_due_date;
	private String action;

	// Constructors
	///////////////
	public OpmCreditDisbursementReq() {
		
	}
	
	public OpmCreditDisbursementReq(int rowId) {
		super(rowId);
	}

	// Properties
	/////////////
	public String getReference_number() {
		return reference_number;
	}
	public void setReference_number(String reference_number) {
		this.reference_number = reference_number;
	}
	public long getLoan_approved() {
		return loan_approved;
	}
	public void setLoan_approved(long loan_approved) {
		this.loan_approved = loan_approved;
	}
	public long getProvision_fee_amt() {
		return provision_fee_amt;
	}
	public void setProvision_fee_amt(long provision_fee_amt) {
		this.provision_fee_amt = provision_fee_amt;
	}
	public long getDisbursement_amt() {
		return disbursement_amt;
	}
	public void setDisbursement_amt(long disbursement_amt) {
		this.disbursement_amt = disbursement_amt;
	}
	public Date getLoan_due_date() {
		return loan_due_date;
	}
	public void setLoan_due_date(Date loan_due_date) {
		this.loan_due_date = loan_due_date;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
}
