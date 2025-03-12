package com.guudint.clickargo.clictruck.admin.rental.model;
// Generated 14 Feb, 2023 12:17:14 PM by Hibernate Tools 5.2.1.Final

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
import com.guudint.clickargo.clictruck.master.model.TCkCtMstRentalType;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.vcc.camelone.common.COAbstractEntity;

/**
 * TCkCtRentalVeh generated by hbm2java
 */
@Entity
@Table(name = "T_CK_CT_RENTAL_VEH")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler" })
public class TCkCtRentalVeh extends COAbstractEntity<TCkCtRentalVeh> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -7170847522598014565L;
	
	// Attributes
	/////////////
	private String rvId;
	private TCkCtMstRentalType TCkCtMstRentalType;
	private TCkCtMstVehType TCkCtMstVehType;
	private TCkCtRentalTable TCkCtRentalTable;
	private Short rvNumVeh;
	private BigDecimal rvRentalAmt;
	private Character rvStatus;
	private Date rvDtCreate;
	private String rvUidCreate;
	private Date rvDtLupd;
	private String rrUidLupd;

	// Constructors
	///////////////
	public TCkCtRentalVeh() {
	}

	public TCkCtRentalVeh(String rvId, TCkCtRentalTable TCkCtRentalTable) {
		this.rvId = rvId;
		this.TCkCtRentalTable = TCkCtRentalTable;
	}

	public TCkCtRentalVeh(String rvId, TCkCtMstRentalType TCkCtMstRentalType, TCkCtMstVehType TCkCtMstVehType,
			TCkCtRentalTable TCkCtRentalTable, Short rvNumVeh, BigDecimal rvRentalAmt, Character rvStatus,
			Date rvDtCreate, String rvUidCreate, Date rvDtLupd, String rrUidLupd) {
		this.rvId = rvId;
		this.TCkCtMstRentalType = TCkCtMstRentalType;
		this.TCkCtMstVehType = TCkCtMstVehType;
		this.TCkCtRentalTable = TCkCtRentalTable;
		this.rvNumVeh = rvNumVeh;
		this.rvRentalAmt = rvRentalAmt;
		this.rvStatus = rvStatus;
		this.rvDtCreate = rvDtCreate;
		this.rvUidCreate = rvUidCreate;
		this.rvDtLupd = rvDtLupd;
		this.rrUidLupd = rrUidLupd;
	}

	// Properties
	/////////////
	@Id
	@Column(name = "RV_ID", unique = true, nullable = false, length = 35)
	public String getRvId() {
		return this.rvId;
	}

	public void setRvId(String rvId) {
		this.rvId = rvId;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RV_RENTAL_TYPE")
	public TCkCtMstRentalType getTCkCtMstRentalType() {
		return this.TCkCtMstRentalType;
	}

	public void setTCkCtMstRentalType(TCkCtMstRentalType TCkCtMstRentalType) {
		this.TCkCtMstRentalType = TCkCtMstRentalType;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RV_VEH_TYPE")
	public TCkCtMstVehType getTCkCtMstVehType() {
		return this.TCkCtMstVehType;
	}

	public void setTCkCtMstVehType(TCkCtMstVehType TCkCtMstVehType) {
		this.TCkCtMstVehType = TCkCtMstVehType;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RV_RENTAL_TABLE", nullable = false)
	public TCkCtRentalTable getTCkCtRentalTable() {
		return this.TCkCtRentalTable;
	}

	public void setTCkCtRentalTable(TCkCtRentalTable TCkCtRentalTable) {
		this.TCkCtRentalTable = TCkCtRentalTable;
	}

	@Column(name = "RV_NUM_VEH")
	public Short getRvNumVeh() {
		return this.rvNumVeh;
	}

	public void setRvNumVeh(Short rvNumVeh) {
		this.rvNumVeh = rvNumVeh;
	}

	@Column(name = "RV_RENTAL_AMT", precision = 15)
	public BigDecimal getRvRentalAmt() {
		return this.rvRentalAmt;
	}

	public void setRvRentalAmt(BigDecimal rvRentalAmt) {
		this.rvRentalAmt = rvRentalAmt;
	}

	@Column(name = "RV_STATUS", length = 1)
	public Character getRvStatus() {
		return this.rvStatus;
	}

	public void setRvStatus(Character rvStatus) {
		this.rvStatus = rvStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RV_DT_CREATE", length = 19)
	@CreationTimestamp
	public Date getRvDtCreate() {
		return this.rvDtCreate;
	}

	public void setRvDtCreate(Date rvDtCreate) {
		this.rvDtCreate = rvDtCreate;
	}

	@Column(name = "RV_UID_CREATE", length = 35)
	public String getRvUidCreate() {
		return this.rvUidCreate;
	}

	public void setRvUidCreate(String rvUidCreate) {
		this.rvUidCreate = rvUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RV_DT_LUPD", length = 19)
	@UpdateTimestamp
	public Date getRvDtLupd() {
		return this.rvDtLupd;
	}

	public void setRvDtLupd(Date rvDtLupd) {
		this.rvDtLupd = rvDtLupd;
	}

	@Column(name = "RR_UID_LUPD", length = 35)
	public String getRrUidLupd() {
		return this.rrUidLupd;
	}

	public void setRrUidLupd(String rrUidLupd) {
		this.rrUidLupd = rrUidLupd;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(TCkCtRentalVeh o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
