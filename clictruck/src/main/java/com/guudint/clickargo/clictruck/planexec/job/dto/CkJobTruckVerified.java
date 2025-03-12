package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.vcc.camelone.ccm.dto.CoreAccn;

public class CkJobTruckVerified extends CkJobTruck {

	private static final long serialVersionUID = -8683505367256911043L;

	private CoreAccn invoiceFromAccn;
	private BigDecimal charges;
	private BigDecimal reimbursements;
	private Date billingDate;
	private Date verifiedDate;
	private String verifiedBy;
	
	public CkJobTruckVerified() {
		super();
	}
	public CkJobTruckVerified(TCkJobTruck entity) {
		super(entity);
	}

	public CoreAccn getInvoiceFromAccn() {
		return invoiceFromAccn;
	}

	public void setInvoiceFromAccn(CoreAccn invoiceFromAccn) {
		this.invoiceFromAccn = invoiceFromAccn;
	}

	public BigDecimal getCharges() {
		return charges;
	}

	public void setCharges(BigDecimal charges) {
		this.charges = charges;
	}

	public BigDecimal getReimbursements() {
		return reimbursements;
	}

	public void setReimbursements(BigDecimal reimbursements) {
		this.reimbursements = reimbursements;
	}

	public Date getBillingDate() {
		return billingDate;
	}

	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}

	public Date getVerifiedDate() {
		return verifiedDate;
	}

	public void setVerifiedDate(Date verifiedDate) {
		this.verifiedDate = verifiedDate;
	}

	public String getVerifiedBy() {
		return verifiedBy;
	}

	public void setVerifiedBy(String verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

}
