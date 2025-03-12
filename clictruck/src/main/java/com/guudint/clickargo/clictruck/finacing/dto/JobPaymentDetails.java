package com.guudint.clickargo.clictruck.finacing.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class JobPaymentDetails implements Serializable {

	private static final long serialVersionUID = 8630243484317642836L;

	public static enum JobPaymentPdfType {
		PLATFORM_FEE, DEBIT_NOTE;
	}

	private List<InvoiceDetails> invoiceDetails;
	private String paymentRef;
	private Date billingDate;
	private Date paidDate;
	private double totalIdr;
	private String vaIdr;
	private String bankAccnName;

	public List<InvoiceDetails> getInvoiceDetails() {
		return invoiceDetails;
	}

	public void setInvoiceDetails(List<InvoiceDetails> invoiceDetails) {
		this.invoiceDetails = invoiceDetails;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public Date getBillingDate() {
		return billingDate;
	}

	public void setBillingDate(Date billingDate) {
		this.billingDate = billingDate;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public double getTotalIdr() {
		return totalIdr;
	}

	public void setTotalIdr(double totalIdr) {
		this.totalIdr = totalIdr;
	}

	public String getVaIdr() {
		return vaIdr;
	}

	public void setVaIdr(String vaIdr) {
		this.vaIdr = vaIdr;
	}

	public String getBankAccnName() {
		return bankAccnName;
	}

	public void setBankAccnName(String bankAccnName) {
		this.bankAccnName = bankAccnName;
	}

	public static class InvoiceDetails {
		private String id;
		private int seq;
		private String jobId;
		private int qty;
		private String invType;
		private String invNo;
		private String invCurrency;
		private BigDecimal invAmt;
		private String invDesc;
		private String jobParentId;
		private String fileLocation;

		public InvoiceDetails() {
		}

		public InvoiceDetails(String id, int seq, String jobId, int qty, String invType, String invNo,
				String invCurrency, BigDecimal invAmt, String invDesc, String jobParentId) {
			super();
			this.id = id;
			this.seq = seq;
			this.jobId = jobId;
			this.qty = qty;
			this.invType = invType;
			this.invNo = invNo;
			this.invCurrency = invCurrency;
			this.invAmt = invAmt;
			this.invDesc = invDesc;
			this.jobParentId = jobParentId;
		}

		public String getFileLocation() {
			return fileLocation;
		}

		public void setFileLocation(String fileLocation) {
			this.fileLocation = fileLocation;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public int getSeq() {
			return seq;
		}

		public void setSeq(int seq) {
			this.seq = seq;
		}

		public String getJobId() {
			return jobId;
		}

		public void setJobId(String jobId) {
			this.jobId = jobId;
		}

		public int getQty() {
			return qty;
		}

		public void setQty(int qty) {
			this.qty = qty;
		}

		public String getInvType() {
			return invType;
		}

		public void setInvType(String invType) {
			this.invType = invType;
		}

		public String getInvCurrency() {
			return invCurrency;
		}

		public void setInvCurrency(String invCurrency) {
			this.invCurrency = invCurrency;
		}

		public BigDecimal getInvAmt() {
			return invAmt;
		}

		public void setInvAmt(BigDecimal invAmt) {
			this.invAmt = invAmt;
		}

		public String getInvDesc() {
			return invDesc;
		}

		public void setInvDesc(String invDesc) {
			this.invDesc = invDesc;
		}

		public String getJobParentId() {
			return jobParentId;
		}

		public void setJobParentId(String jobParentId) {
			this.jobParentId = jobParentId;
		}

		public String getInvNo() {
			return invNo;
		}

		public void setInvNo(String invNo) {
			this.invNo = invNo;
		}

	}
}
