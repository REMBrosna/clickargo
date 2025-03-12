package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;
import com.vcc.camelone.ccm.dto.CoreAccn;

public class CkJobTruckApproved extends CkJobTruck {

	private static final long serialVersionUID = -8683505367256911043L;

	private CoreAccn invoiceFromAccn;
	private BigDecimal charges;
	private BigDecimal reimbursements;
	private BigDecimal platformFees;
	private Date billingDate;
	private Date paymentDueDate;
	private Date approvedDate;
	private String approvedBy;
	private Date acknowledgedDate;
	private String acknowledgedBy;
	private String paymentState;
	private boolean forInboundCs;
	private boolean opmOnly;

	// Holder for the accounts associated to customer service
	private List<CoreAccn> accnForCsList;

	public CkJobTruckApproved() {
		super();
	}

	public CkJobTruckApproved(TCkJobTruck entity) {
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

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getPaymentState() {
		return paymentState;
	}

	public void setPaymentState(String paymentState) {
		this.paymentState = paymentState;
	}

	public boolean isForInboundCs() {
		return forInboundCs;
	}

	public void setForInboundCs(boolean forInboundCs) {
		this.forInboundCs = forInboundCs;
	}

	public Date getAcknowledgedDate() {
		return acknowledgedDate;
	}

	public void setAcknowledgedDate(Date acknowledgedDate) {
		this.acknowledgedDate = acknowledgedDate;
	}

	public String getAcknowledgedBy() {
		return acknowledgedBy;
	}

	public void setAcknowledgedBy(String acknowledgedBy) {
		this.acknowledgedBy = acknowledgedBy;
	}

	public List<CoreAccn> getAccnForCsList() {
		return accnForCsList;
	}

	public void setAccnForCsList(List<CoreAccn> accnForCsList) {
		this.accnForCsList = accnForCsList;
	}

	/**
	 * @return the opmOnly
	 */
	public boolean isOpmOnly() {
		return opmOnly;
	}

	/**
	 * @param opmOnly the opmOnly to set
	 */
	public void setOpmOnly(boolean opmOnly) {
		this.opmOnly = opmOnly;
	}

}
