package com.guudint.clickargo.clictruck.admin.contract.model;
// Generated 21 Feb, 2023 3:06:01 PM by Hibernate Tools 5.2.1.Final

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtContractCharge;
import com.vcc.camelone.common.COAbstractEntity;

/**
 * TCkCtContractCharge generated by hbm2java
 */
@Entity
@Table(name = "T_CK_CT_CONTRACT_CHARGE")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler" })
public class TCkCtContractCharge extends COAbstractEntity<TCkCtContractCharge> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 909031759605335258L;
	
	// Attributes
	/////////////
	private String concId;
	private BigDecimal concPltfeeAmt;
	private Character concPltfeeType;
	private BigDecimal concAddtaxAmt;
	private Character concAddtaxType;
	private BigDecimal concWhtaxAmt;
	private Character concWhtaxType;
	private Character concStatus;
	private Date concDtCreate;
	private String concUidCreate;
	private Date concDtLupd;
	private String concUidLupd;

	// Constructors
	///////////////
	public TCkCtContractCharge() {
	}

	public TCkCtContractCharge(CkCtContractCharge ckCtContractCharge) {
		BeanUtils.copyProperties(ckCtContractCharge, this);
	}

	public TCkCtContractCharge(String concId) {
		this.concId = concId;
	}

	public TCkCtContractCharge(String concId, BigDecimal concPltfeeAmt, Character concPltfeeType,
			BigDecimal concAddtaxAmt, Character concAddtaxType, BigDecimal concWhtaxAmt, Character concWhtaxType,
			Character concStatus, Date concDtCreate, String concUidCreate, Date concDtLupd, String concUidLupd) {
		this.concId = concId;
		this.concPltfeeAmt = concPltfeeAmt;
		this.concPltfeeType = concPltfeeType;
		this.concAddtaxAmt = concAddtaxAmt;
		this.concAddtaxType = concAddtaxType;
		this.concWhtaxAmt = concWhtaxAmt;
		this.concWhtaxType = concWhtaxType;
		this.concStatus = concStatus;
		this.concDtCreate = concDtCreate;
		this.concUidCreate = concUidCreate;
		this.concDtLupd = concDtLupd;
		this.concUidLupd = concUidLupd;
	}

	// Properties
	/////////////
	@Id
	@Column(name = "CONC_ID", unique = true, nullable = false, length = 35)
	public String getConcId() {
		return this.concId;
	}

	public void setConcId(String concId) {
		this.concId = concId;
	}

	@Column(name = "CONC_PLTFEE_AMT", precision = 15)
	public BigDecimal getConcPltfeeAmt() {
		return this.concPltfeeAmt;
	}

	public void setConcPltfeeAmt(BigDecimal concPltfeeAmt) {
		this.concPltfeeAmt = concPltfeeAmt;
	}

	@Column(name = "CONC_PLTFEE_TYPE", length = 1)
	public Character getConcPltfeeType() {
		return this.concPltfeeType;
	}

	public void setConcPltfeeType(Character concPltfeeType) {
		this.concPltfeeType = concPltfeeType;
	}

	@Column(name = "CONC_ADDTAX_AMT", precision = 15)
	public BigDecimal getConcAddtaxAmt() {
		return this.concAddtaxAmt;
	}

	public void setConcAddtaxAmt(BigDecimal concAddtaxAmt) {
		this.concAddtaxAmt = concAddtaxAmt;
	}

	@Column(name = "CONC_ADDTAX_TYPE", length = 1)
	public Character getConcAddtaxType() {
		return this.concAddtaxType;
	}

	public void setConcAddtaxType(Character concAddtaxType) {
		this.concAddtaxType = concAddtaxType;
	}

	@Column(name = "CONC_WHTAX_AMT", precision = 15)
	public BigDecimal getConcWhtaxAmt() {
		return this.concWhtaxAmt;
	}

	public void setConcWhtaxAmt(BigDecimal concWhtaxAmt) {
		this.concWhtaxAmt = concWhtaxAmt;
	}

	@Column(name = "CONC_WHTAX_TYPE", length = 1)
	public Character getConcWhtaxType() {
		return this.concWhtaxType;
	}

	public void setConcWhtaxType(Character concWhtaxType) {
		this.concWhtaxType = concWhtaxType;
	}

	@Column(name = "CONC_STATUS", length = 1)
	public Character getConcStatus() {
		return this.concStatus;
	}

	public void setConcStatus(Character concStatus) {
		this.concStatus = concStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONC_DT_CREATE", length = 19)
	@CreationTimestamp
	public Date getConcDtCreate() {
		return this.concDtCreate;
	}

	public void setConcDtCreate(Date concDtCreate) {
		this.concDtCreate = concDtCreate;
	}

	@Column(name = "CONC_UID_CREATE", length = 35)
	public String getConcUidCreate() {
		return this.concUidCreate;
	}

	public void setConcUidCreate(String concUidCreate) {
		this.concUidCreate = concUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONC_DT_LUPD", length = 19)
	@UpdateTimestamp
	public Date getConcDtLupd() {
		return this.concDtLupd;
	}

	public void setConcDtLupd(Date concDtLupd) {
		this.concDtLupd = concDtLupd;
	}

	@Column(name = "CONC_UID_LUPD", length = 35)
	public String getConcUidLupd() {
		return this.concUidLupd;
	}

	public void setConcUidLupd(String concUidLupd) {
		this.concUidLupd = concUidLupd;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(TCkCtContractCharge o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
