package com.guudint.clickargo.clictruck.finacing.model;
// default package

// Generated 11 Jun 2023, 11:22:04 am by Hibernate Tools 4.3.6.Final

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vcc.camelone.common.COAbstractEntity;
import com.vcc.camelone.master.model.TMstCurrency;

/**
 * TCkCtDebitNoteItem generated by hbm2java
 */
@Entity
@Table(name = "T_CK_CT_DEBIT_NOTE_ITEM")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler" })
public class TCkCtDebitNoteItem extends COAbstractEntity<TCkCtDebitNoteItem> {

	private static final long serialVersionUID = 1225020911040382574L;
	private String itmId;
	private TCkCtDebitNote TCkCtDebitNote;
	private Short itmSno;
	private String itmItem;
	private Short itmQty;
	private TMstCurrency TMstCurrency;
	private BigDecimal itmUnitPrice;
	private BigDecimal itmAmount;
	private String itmRef;
	private Character itmStatus;
	private Date itmDtCreate;
	private String itmUidCreate;
	private Date itmDtLupd;
	private String itmUidLupd;

	public TCkCtDebitNoteItem() {
	}

	public TCkCtDebitNoteItem(String itmId) {
		this.itmId = itmId;
	}

	public TCkCtDebitNoteItem(String itmId, TCkCtDebitNote TCkCtDebitNote, Short itmSno, String itmItem, Short itmQty,
			TMstCurrency TMstCurrency, BigDecimal itmUnitPrice, BigDecimal itmAmount, String itmRef,
			Character itmStatus, Date itmDtCreate, String itmUidCreate, Date itmDtLupd, String itmUidLupd) {
		this.itmId = itmId;
		this.TCkCtDebitNote = TCkCtDebitNote;
		this.itmSno = itmSno;
		this.itmItem = itmItem;
		this.itmQty = itmQty;
		this.TMstCurrency = TMstCurrency;
		this.itmUnitPrice = itmUnitPrice;
		this.itmAmount = itmAmount;
		this.itmRef = itmRef;
		this.itmStatus = itmStatus;
		this.itmDtCreate = itmDtCreate;
		this.itmUidCreate = itmUidCreate;
		this.itmDtLupd = itmDtLupd;
		this.itmUidLupd = itmUidLupd;
	}

	@Id

	@Column(name = "ITM_ID", unique = true, nullable = false, length = 35)
	public String getItmId() {
		return this.itmId;
	}

	public void setItmId(String itmId) {
		this.itmId = itmId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITM_DEBIT_NOTE")
	public TCkCtDebitNote getTCkCtDebitNote() {
		return this.TCkCtDebitNote;
	}

	public void setTCkCtDebitNote(TCkCtDebitNote TCkCtDebitNote) {
		this.TCkCtDebitNote = TCkCtDebitNote;
	}

	@Column(name = "ITM_SNO")
	public Short getItmSno() {
		return this.itmSno;
	}

	public void setItmSno(Short itmSno) {
		this.itmSno = itmSno;
	}

	@Column(name = "ITM_ITEM", length = 16777215)
	public String getItmItem() {
		return this.itmItem;
	}

	public void setItmItem(String itmItem) {
		this.itmItem = itmItem;
	}

	@Column(name = "ITM_QTY")
	public Short getItmQty() {
		return this.itmQty;
	}

	public void setItmQty(Short itmQty) {
		this.itmQty = itmQty;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITM_CCY")
	public TMstCurrency getTMstCurrency() {
		return TMstCurrency;
	}

	public void setTMstCurrency(TMstCurrency tMstCurrency) {
		TMstCurrency = tMstCurrency;
	}

	@Column(name = "ITM_UNIT_PRICE", precision = 15)
	public BigDecimal getItmUnitPrice() {
		return this.itmUnitPrice;
	}

	public void setItmUnitPrice(BigDecimal itmUnitPrice) {
		this.itmUnitPrice = itmUnitPrice;
	}

	@Column(name = "ITM_AMOUNT", precision = 15)
	public BigDecimal getItmAmount() {
		return this.itmAmount;
	}

	public void setItmAmount(BigDecimal itmAmount) {
		this.itmAmount = itmAmount;
	}

	@Column(name = "ITM_REF", length = 16777215)
	public String getItmRef() {
		return this.itmRef;
	}

	public void setItmRef(String itmRef) {
		this.itmRef = itmRef;
	}

	@Column(name = "ITM_STATUS", length = 1)
	public Character getItmStatus() {
		return this.itmStatus;
	}

	public void setItmStatus(Character itmStatus) {
		this.itmStatus = itmStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ITM_DT_CREATE", length = 19)
	public Date getItmDtCreate() {
		return this.itmDtCreate;
	}

	public void setItmDtCreate(Date itmDtCreate) {
		this.itmDtCreate = itmDtCreate;
	}

	@Column(name = "ITM_UID_CREATE", length = 35)
	public String getItmUidCreate() {
		return this.itmUidCreate;
	}

	public void setItmUidCreate(String itmUidCreate) {
		this.itmUidCreate = itmUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ITM_DT_LUPD", length = 19)
	public Date getItmDtLupd() {
		return this.itmDtLupd;
	}

	public void setItmDtLupd(Date itmDtLupd) {
		this.itmDtLupd = itmDtLupd;
	}

	@Column(name = "ITM_UID_LUPD", length = 35)
	public String getItmUidLupd() {
		return this.itmUidLupd;
	}

	public void setItmUidLupd(String itmUidLupd) {
		this.itmUidLupd = itmUidLupd;
	}

	@Override
	public int compareTo(TCkCtDebitNoteItem o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
