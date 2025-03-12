package com.guudint.clickargo.clictruck.sage.dto;
// Generated 3 Aug 2023, 7:23:57 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.sage.model.VCkSagePayOut;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkSagePayOut extends AbstractDTO<CkSagePayOut, VCkSagePayOut> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;

	private String service;
	private String sageType;
	private String reference;
	private String datetime;
	private Long payTotal;
	private String providerId;
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
	public CkSagePayOut() {
	}

	public CkSagePayOut(VCkSagePayOut entity) {
		super(entity);
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(CkSagePayOut arg0) {
		return 0;
	}

	@Override
	public void init() {

	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getSageType() {
		return sageType;
	}

	public void setSageType(String sageType) {
		this.sageType = sageType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public Long getPayTotal() {
		return payTotal;
	}

	public void setPayTotal(Long payTotal) {
		this.payTotal = payTotal;
	}


	public String getProviderId() {
		return this.providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getVat() {
		return vat;
	}

	public void setVat(Long vat) {
		this.vat = vat;
	}

	public Long getDuty() {
		return duty;
	}

	public void setDuty(Long duty) {
		this.duty = duty;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getTaxNo() {
		return taxNo;
	}

	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Date getPtxDtPaid() {
		return ptxDtPaid;
	}

	public void setPtxDtPaid(Date ptxDtPaid) {
		this.ptxDtPaid = ptxDtPaid;
	}


}
