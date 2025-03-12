package com.guudint.clickargo.clictruck.common.model;

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
import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.vcc.camelone.ccm.model.TCoreAccn;
import com.vcc.camelone.common.COAbstractEntity;

@Entity
@Table(name = "T_CK_CT_CHASSIS")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler" })
public class TCkCtChassis extends COAbstractEntity<TCkCtChassis> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 2512198062674571878L;

	// Attributes
	/////////////
	private String chsId;
	private TCoreAccn TCoreAccn;
	private String chsNo;
	private TCkCtMstChassisType TCkCtMstChassisType;
	private Character chsStatus;
	private Date chsDtCreate;
	private String chsUidCreate;
	private Date chsDtLupd;
	private String chsUidLupd;
	
	// Constructors
	///////////////
	public TCkCtChassis() {
	}
	
	public TCkCtChassis(String chsId) {
		this.chsId = chsId;
	}

	public TCkCtChassis(String chsId, TCoreAccn TCoreAccn, String chsNo, Character chsStatus,
			Date chsDtCreate, String chsUidCreate, Date chsDtLupd, String chsUidLupd) {
		this.chsId = chsId;
		this.TCoreAccn = TCoreAccn;
		this.chsNo = chsNo;
		this.chsStatus = chsStatus;
		this.chsDtCreate = chsDtCreate;
		this.chsUidCreate = chsUidCreate;
		this.chsDtLupd = chsDtLupd;
		this.chsUidLupd = chsUidLupd;
	}
	
	// Properties
	/////////////
	@Id
	@Column(name = "CHS_ID", unique = true, nullable = false, length = 35)
	public String getChsId() {
		return this.chsId;
	}

	public void setChsId(String chsId) {
		this.chsId = chsId;
	}

	@Column(name = "CHS_NO", length = 256)
	public String getChsNo() {
		return this.chsNo;
	}

	public void setChsNo(String chsNo) {
		this.chsNo = chsNo;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CHS_COMPANY")
	public TCoreAccn getTCoreAccn() {
		return this.TCoreAccn;
	}

	public void setTCoreAccn(TCoreAccn TCoreAccn) {
		this.TCoreAccn = TCoreAccn;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CHS_CHASSIS_TYPE")
	public TCkCtMstChassisType getTCkCtMstChassisType() {
		return this.TCkCtMstChassisType;
	}

	public void setTCkCtMstChassisType(TCkCtMstChassisType tCkCtMstChassisType) {
		this.TCkCtMstChassisType = tCkCtMstChassisType;
	}

	@Column(name = "CHS_STATUS", length = 1)
	public Character getChsStatus() {
		return this.chsStatus;
	}

	public void setChsStatus(Character chsStatus) {
		this.chsStatus = chsStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CHS_DT_CREATE", length = 19)
	@CreationTimestamp
	public Date getChsDtCreate() {
		return this.chsDtCreate;
	}

	public void setChsDtCreate(Date chsDtCreate) {
		this.chsDtCreate = chsDtCreate;
	}

	@Column(name = "CHS_UID_CREATE", length = 35)
	public String getChsUidCreate() {
		return this.chsUidCreate;
	}

	public void setChsUidCreate(String chsUidCreate) {
		this.chsUidCreate = chsUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CHS_DT_LUPD", length = 19)
	@UpdateTimestamp
	public Date getChsDtLupd() {
		return this.chsDtLupd;
	}

	public void setChsDtLupd(Date chsDtLupd) {
		this.chsDtLupd = chsDtLupd;
	}

	@Column(name = "CHS_UID_LUPD", length = 35)
	public String getChsUidLupd() {
		return this.chsUidLupd;
	}

	public void setChsUidLupd(String chsUidLupd) {
		this.chsUidLupd = chsUidLupd;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(TCkCtChassis o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
