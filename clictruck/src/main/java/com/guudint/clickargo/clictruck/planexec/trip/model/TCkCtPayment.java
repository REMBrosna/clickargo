package com.guudint.clickargo.clictruck.planexec.trip.model;
// Generated 05 15, 23 10:36:37 PM by Hibernate Tools 5.2.1.Final

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.guudint.clickargo.payment.model.TCkPaymentTxn;
import com.vcc.camelone.common.COAbstractEntity;
import com.vcc.camelone.master.model.TMstCurrency;

/**
 * TCkCtPayment generated by hbm2java
 */
@Entity
@Table(name = "T_CK_CT_PAYMENT")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler" })
public class TCkCtPayment extends COAbstractEntity<TCkCtPayment> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -6277051374356984098L;
	
	// Attributes
	/////////////
	private String ctpId;
	private TCkPaymentTxn TCkPaymentTxn;
	private TMstCurrency TMstCurrency;
	private String ctpJob;
	private String ctpItem;
	private Short ctpQty;
	private BigDecimal ctpAmount;
	private String ctpRef;
	private String ctpAttach;
	private String ctpState;
	private Character ctpStatus;
	private Date ctpDtCreate;
	private String ctpUidCreate;
	private Date ctpDtLupd;
	private String ctpUidLupd;

	// Constructors
	///////////////
	public TCkCtPayment() {
	}

	public TCkCtPayment(String ctpId) {
		this.ctpId = ctpId;
	}

	public TCkCtPayment(String ctpId, TCkPaymentTxn TCkPaymentTxn, TMstCurrency TMstCurrency, String ctpJob,
			String ctpItem, Short ctpQty, BigDecimal ctpAmount, String ctpRef, String ctpAttach, String ctpState,
			Character ctpStatus, Date ctpDtCreate, String ctpUidCreate, Date ctpDtLupd, String ctpUidLupd) {
		this.ctpId = ctpId;
		this.TCkPaymentTxn = TCkPaymentTxn;
		this.TMstCurrency = TMstCurrency;
		this.ctpJob = ctpJob;
		this.ctpItem = ctpItem;
		this.ctpQty = ctpQty;
		this.ctpAmount = ctpAmount;
		this.ctpRef = ctpRef;
		this.ctpAttach = ctpAttach;
		this.ctpState = ctpState;
		this.ctpStatus = ctpStatus;
		this.ctpDtCreate = ctpDtCreate;
		this.ctpUidCreate = ctpUidCreate;
		this.ctpDtLupd = ctpDtLupd;
		this.ctpUidLupd = ctpUidLupd;
	}

	// Properties
	/////////////
	@Id
	@Column(name = "CTP_ID", unique = true, nullable = false, length = 35)
	public String getCtpId() {
		return this.ctpId;
	}

	public void setCtpId(String ctpId) {
		this.ctpId = ctpId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTP_PAYMENT_TXN")
	public TCkPaymentTxn getTCkPaymentTxn() {
		return this.TCkPaymentTxn;
	}

	public void setTCkPaymentTxn(TCkPaymentTxn TCkPaymentTxn) {
		this.TCkPaymentTxn = TCkPaymentTxn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTP_CCY")
	public TMstCurrency getTMstCurrency() {
		return this.TMstCurrency;
	}

	public void setTMstCurrency(TMstCurrency TMstCurrency) {
		this.TMstCurrency = TMstCurrency;
	}

	@Column(name = "CTP_JOB", length = 35)
	public String getCtpJob() {
		return this.ctpJob;
	}

	public void setCtpJob(String ctpJob) {
		this.ctpJob = ctpJob;
	}

	@Column(name = "CTP_ITEM", length = 16777215)
	public String getCtpItem() {
		return this.ctpItem;
	}

	public void setCtpItem(String ctpItem) {
		this.ctpItem = ctpItem;
	}

	@Column(name = "CTP_QTY")
	public Short getCtpQty() {
		return this.ctpQty;
	}

	public void setCtpQty(Short ctpQty) {
		this.ctpQty = ctpQty;
	}

	@Column(name = "CTP_AMOUNT", precision = 15)
	public BigDecimal getCtpAmount() {
		return this.ctpAmount;
	}

	public void setCtpAmount(BigDecimal ctpAmount) {
		this.ctpAmount = ctpAmount;
	}

	@Column(name = "CTP_REF", length = 16777215)
	public String getCtpRef() {
		return this.ctpRef;
	}

	public void setCtpRef(String ctpRef) {
		this.ctpRef = ctpRef;
	}

	@Column(name = "CTP_ATTACH", length = 1024)
	public String getCtpAttach() {
		return this.ctpAttach;
	}

	public void setCtpAttach(String ctpAttach) {
		this.ctpAttach = ctpAttach;
	}

	@Column(name = "CTP_STATE", length = 3)
	public String getCtpState() {
		return this.ctpState;
	}

	public void setCtpState(String ctpState) {
		this.ctpState = ctpState;
	}

	@Column(name = "CTP_STATUS", length = 1)
	public Character getCtpStatus() {
		return this.ctpStatus;
	}

	public void setCtpStatus(Character ctpStatus) {
		this.ctpStatus = ctpStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CTP_DT_CREATE", length = 19)
	@CreationTimestamp
	public Date getCtpDtCreate() {
		return this.ctpDtCreate;
	}

	public void setCtpDtCreate(Date ctpDtCreate) {
		this.ctpDtCreate = ctpDtCreate;
	}

	@Column(name = "CTP_UID_CREATE", length = 35)
	public String getCtpUidCreate() {
		return this.ctpUidCreate;
	}

	public void setCtpUidCreate(String ctpUidCreate) {
		this.ctpUidCreate = ctpUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CTP_DT_LUPD", length = 19)
	@UpdateTimestamp
	public Date getCtpDtLupd() {
		return this.ctpDtLupd;
	}

	public void setCtpDtLupd(Date ctpDtLupd) {
		this.ctpDtLupd = ctpDtLupd;
	}

	@Column(name = "CTP_UID_LUPD", length = 35)
	public String getCtpUidLupd() {
		return this.ctpUidLupd;
	}

	public void setCtpUidLupd(String ctpUidLupd) {
		this.ctpUidLupd = ctpUidLupd;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(TCkCtPayment o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
