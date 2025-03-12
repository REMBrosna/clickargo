package com.guudint.clickargo.clictruck.admin.shell.model;
// Generated 6 May 2024, 2:09:45 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vcc.camelone.common.COAbstractEntity;

/**
 * TCkCtShellCard generated by hbm2java
 */
@Entity
@Table(name = "T_CK_CT_SHELL_CARD")
public class TCkCtShellCard extends COAbstractEntity<TCkCtShellCard> {

	private static final long serialVersionUID = 6817689248842950068L;
	private String scId;
	private Date scDtExpiry;
	private Character scStatus;
	private Date scDtCreate;
	private String scUidCreate;
	private Date scDtLupd;
	private String scUidLupd;

	public TCkCtShellCard() {
	}

	public TCkCtShellCard(String scId) {
		this.scId = scId;
	}

	public TCkCtShellCard(String scId, Date scDtExpiry, Character scStatus, Date scDtCreate, String scUidCreate,
			Date scDtLupd, String scUidLupd) {
		this.scId = scId;
		this.scDtExpiry = scDtExpiry;
		this.scStatus = scStatus;
		this.scDtCreate = scDtCreate;
		this.scUidCreate = scUidCreate;
		this.scDtLupd = scDtLupd;
		this.scUidLupd = scUidLupd;
	}

	@Id

	@Column(name = "SC_ID", unique = true, nullable = false, length = 35)
	public String getScId() {
		return this.scId;
	}

	public void setScId(String scId) {
		this.scId = scId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SC_DT_EXPIRY", length = 19)
	public Date getScDtExpiry() {
		return this.scDtExpiry;
	}

	public void setScDtExpiry(Date scDtExpiry) {
		this.scDtExpiry = scDtExpiry;
	}

	@Column(name = "SC_STATUS", length = 1)
	public Character getScStatus() {
		return this.scStatus;
	}

	public void setScStatus(Character scStatus) {
		this.scStatus = scStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SC_DT_CREATE", length = 19)
	public Date getScDtCreate() {
		return this.scDtCreate;
	}

	public void setScDtCreate(Date scDtCreate) {
		this.scDtCreate = scDtCreate;
	}

	@Column(name = "SC_UID_CREATE", length = 35)
	public String getScUidCreate() {
		return this.scUidCreate;
	}

	public void setScUidCreate(String scUidCreate) {
		this.scUidCreate = scUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SC_DT_LUPD", length = 19)
	public Date getScDtLupd() {
		return this.scDtLupd;
	}

	public void setScDtLupd(Date scDtLupd) {
		this.scDtLupd = scDtLupd;
	}

	@Column(name = "SC_UID_LUPD", length = 35)
	public String getScUidLupd() {
		return this.scUidLupd;
	}

	public void setScUidLupd(String scUidLupd) {
		this.scUidLupd = scUidLupd;
	}

	@Override
	public int compareTo(TCkCtShellCard o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
