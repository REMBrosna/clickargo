package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.vcc.camelone.ccm.dto.CoreAccn;

public class CkJobTruckAcknowledged extends CkJobTruck {

	private static final long serialVersionUID = -8683505367256911043L;

	private CoreAccn invoiceFromAccn;
	private BigDecimal charges;
	private BigDecimal reimbursements;
	private BigDecimal platformFees;
	private Date billingDate;
	private Date paymentDueDate;
	private Date acknowledgedDate;
	private String acknowledgedBy;
	private String paymentState;

	public CkJobTruckAcknowledged() {
		super();
	}

	public CkJobTruckAcknowledged(TCkJobTruck entity) {
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

	public BigDecimal getPlatformFees() {
		return platformFees;
	}

	public void setPlatformFees(BigDecimal platformFees) {
		this.platformFees = platformFees;
	}

	public Date getPaymentDueDate() {
		return paymentDueDate;
	}

	public void setPaymentDueDate(Date paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

	/**
	 * @return the acknowledgedDate
	 */
	public Date getAcknowledgedDate() {
		return acknowledgedDate;
	}

	/**
	 * @param acknowledgedDate the acknowledgedDate to set
	 */
	public void setAcknowledgedDate(Date acknowledgedDate) {
		this.acknowledgedDate = acknowledgedDate;
	}

	/**
	 * @return the acknowledgedBy
	 */
	public String getAcknowledgedBy() {
		return acknowledgedBy;
	}

	/**
	 * @param acknowledgedBy the acknowledgedBy to set
	 */
	public void setAcknowledgedBy(String acknowledgedBy) {
		this.acknowledgedBy = acknowledgedBy;
	}

	public String getPaymentState() {
		return paymentState;
	}

	public void setPaymentState(String paymentState) {
		this.paymentState = paymentState;
	}

}
