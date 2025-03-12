package com.guudint.clickargo.clictruck.opm.third.dto;

import java.util.Date;

public class OpmCreditRepaymentReq extends OpmCreditReq {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;

	// Attributes
	/////////////

	private String reference_number;
	private long principal_paid;
	private long late_fee;
	private Date payment_date;
	private long loan_outstanding_amt;
	private String action;

	// Constructors
	///////////////
	public OpmCreditRepaymentReq() {
		
	}
	
	public OpmCreditRepaymentReq(int rowId) {
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
	public long getPrincipal_paid() {
		return principal_paid;
	}
	public void setPrincipal_paid(long principal_paid) {
		this.principal_paid = principal_paid;
	}
	public long getLate_fee() {
		return late_fee;
	}
	public void setLate_fee(long late_fee) {
		this.late_fee = late_fee;
	}
	public Date getPayment_date() {
		return payment_date;
	}
	public void setPayment_date(Date payment_date) {
		this.payment_date = payment_date;
	}
	public long getLoan_outstanding_amt() {
		return loan_outstanding_amt;
	}
	public void setLoan_outstanding_amt(long loan_outstanding_amt) {
		this.loan_outstanding_amt = loan_outstanding_amt;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

}
