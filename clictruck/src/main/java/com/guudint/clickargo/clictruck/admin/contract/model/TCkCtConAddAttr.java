package com.guudint.clickargo.clictruck.admin.contract.model;
// Generated 22 Feb 2024, 10:53:23 am by Hibernate Tools 4.3.6.Final

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

import com.vcc.camelone.common.COAbstractEntity;

/**
 * TCkCtConAddAttr generated by hbm2java
 */
@Entity
@Table(name = "T_CK_CT_CON_ADD_ATTR")
public class TCkCtConAddAttr extends COAbstractEntity<TCkCtConAddAttr> {

	private static final long serialVersionUID = -9110110156104286383L;
	private String caaId;
	private TCkCtAddAttrList TCkCtAddAttrList;
	private TCkCtContract TCkCtContract;
	private Character caaType;
	private Character caaRequired;
	private Short caaSeq;
	private String caaLabel;
	private String caaDescription;
	private Character caaStatus;
	private Date caaDtCreate;
	private String caaUidCreate;
	private Date caaDtLupd;
	private String caaUidLupd;

	public TCkCtConAddAttr() {
	}

	public TCkCtConAddAttr(String caaId, TCkCtContract TCkCtContract) {
		this.caaId = caaId;
		this.TCkCtContract = TCkCtContract;
	}

	public TCkCtConAddAttr(String caaId, TCkCtAddAttrList TCkCtAddAttrList, TCkCtContract TCkCtContract,
			Character caaType, Short caaSeq, String caaLabel, String caaDescription, Character caaStatus,
			Date caaDtCreate, String caaUidCreate, Date caaDtLupd, String caaUidLupd) {
		this.caaId = caaId;
		this.TCkCtAddAttrList = TCkCtAddAttrList;
		this.TCkCtContract = TCkCtContract;
		this.caaType = caaType;
		this.caaSeq = caaSeq;
		this.caaLabel = caaLabel;
		this.caaDescription = caaDescription;
		this.caaStatus = caaStatus;
		this.caaDtCreate = caaDtCreate;
		this.caaUidCreate = caaUidCreate;
		this.caaDtLupd = caaDtLupd;
		this.caaUidLupd = caaUidLupd;
	}

	@Id

	@Column(name = "CAA_ID", unique = true, nullable = false, length = 35)
	public String getCaaId() {
		return this.caaId;
	}

	public void setCaaId(String caaId) {
		this.caaId = caaId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAA_REF_LIST")
	public TCkCtAddAttrList getTCkCtAddAttrList() {
		return this.TCkCtAddAttrList;
	}

	public void setTCkCtAddAttrList(TCkCtAddAttrList TCkCtAddAttrList) {
		this.TCkCtAddAttrList = TCkCtAddAttrList;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAA_CON", nullable = false)
	public TCkCtContract getTCkCtContract() {
		return this.TCkCtContract;
	}

	public void setTCkCtContract(TCkCtContract TCkCtContract) {
		this.TCkCtContract = TCkCtContract;
	}

	@Column(name = "CAA_TYPE", length = 1)
	public Character getCaaType() {
		return this.caaType;
	}

	public void setCaaType(Character caaType) {
		this.caaType = caaType;
	}

	@Column(name = "CAA_SEQ")
	public Short getCaaSeq() {
		return this.caaSeq;
	}

	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}

	@Column(name = "CAA_LABEL", length = 1024)
	public String getCaaLabel() {
		return this.caaLabel;
	}

	public void setCaaLabel(String caaLabel) {
		this.caaLabel = caaLabel;
	}

	@Column(name = "CAA_DESCRIPTION", length = 1024)
	public String getCaaDescription() {
		return this.caaDescription;
	}

	public void setCaaDescription(String caaDescription) {
		this.caaDescription = caaDescription;
	}

	@Column(name = "CAA_STATUS", length = 1)
	public Character getCaaStatus() {
		return this.caaStatus;
	}

	public void setCaaStatus(Character caaStatus) {
		this.caaStatus = caaStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CAA_DT_CREATE", length = 19)
	public Date getCaaDtCreate() {
		return this.caaDtCreate;
	}

	public void setCaaDtCreate(Date caaDtCreate) {
		this.caaDtCreate = caaDtCreate;
	}

	@Column(name = "CAA_UID_CREATE", length = 35)
	public String getCaaUidCreate() {
		return this.caaUidCreate;
	}

	public void setCaaUidCreate(String caaUidCreate) {
		this.caaUidCreate = caaUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CAA_DT_LUPD", length = 19)
	public Date getCaaDtLupd() {
		return this.caaDtLupd;
	}

	public void setCaaDtLupd(Date caaDtLupd) {
		this.caaDtLupd = caaDtLupd;
	}

	@Column(name = "CAA_UID_LUPD", length = 35)
	public String getCaaUidLupd() {
		return this.caaUidLupd;
	}

	public void setCaaUidLupd(String caaUidLupd) {
		this.caaUidLupd = caaUidLupd;
	}

	@Column(name = "CAA_REQUIRED", length = 1)
	public Character getCaaRequired() {
		return caaRequired;
	}

	public void setCaaRequired(Character caaRequired) {
		this.caaRequired = caaRequired;
	}

	@Override
	public int compareTo(TCkCtConAddAttr o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
