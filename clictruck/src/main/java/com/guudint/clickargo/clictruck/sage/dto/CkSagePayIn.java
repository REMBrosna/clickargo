package com.guudint.clickargo.clictruck.sage.dto;
// Generated 3 Aug 2023, 7:23:57 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.sage.model.VCkSagePayIn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkSagePayIn extends AbstractDTO<CkSagePayIn, VCkSagePayIn> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;
	
	private String service;
	private String sageType;
	private String reference;
	private String datetime;
	private Long payTotal;
	private String cunsumerId;
	private String docType;
	private String docNo;
	private String issuedDate;
	private String ccy;
	private Long amount;
	private Long vat;
	private Long duty;
	private String terms;
	private String taxNo;
	private Long total;
	private Date ptxDtPaid;

	// Constructors
	///////////////
	public CkSagePayIn() {
	}

	public CkSagePayIn(VCkSagePayIn entity) {
		super(entity);
	}

	
	// Override Methods
	///////////////////
	@Override
	public int compareTo(CkSagePayIn arg0) {
		return 0;
	}

	@Override
	public void init() {

	}

	public String getDocNo() {
		return this.docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	
	public String getService() {
		return this.service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getSageType() {
		return this.sageType;
	}

	public void setSageType(String sageType) {
		this.sageType = sageType;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDatetime() {
		return this.datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public Long getPayTotal() {
		return this.payTotal;
	}

	public void setPayTotal(Long payTotal) {
		this.payTotal = payTotal;
	}

	public String getCunsumerId() {
		return this.cunsumerId;
	}

	public void setCunsumerId(String cunsumerId) {
		this.cunsumerId = cunsumerId;
	}

	public String getDocType() {
		return this.docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getIssuedDate() {
		return this.issuedDate;
	}

	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}

	public String getCcy() {
		return this.ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public Long getAmount() {
		return this.amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getVat() {
		return this.vat;
	}

	public void setVat(Long vat) {
		this.vat = vat;
	}

	public Long getDuty() {
		return this.duty;
	}

	public void setDuty(Long duty) {
		this.duty = duty;
	}

	public String getTerms() {
		return this.terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getTaxNo() {
		return this.taxNo;
	}

	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}

	public Long getTotal() {
		return this.total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Date getPtxDtPaid() {
		return this.ptxDtPaid;
	}

	public void setPtxDtPaid(Date ptxDtPaid) {
		this.ptxDtPaid = ptxDtPaid;
	}

}
