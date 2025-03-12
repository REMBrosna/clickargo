package com.guudint.clickargo.clictruck.admin.shell.model;

import com.vcc.camelone.common.COAbstractEntity;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "T_CK_CT_SHELL_BATCH_WINDOW")
public class TCkCtShellBatchWindow extends COAbstractEntity<TCkCtShellBatchWindow> {

	private String sbId;
	private String sbName;
	private Date sbDtWindow;
	private char sbStatus;
	private Date sbDtCreate;
	private String sbUidCreate;
	private Date sbDtLupd;
	private String sbUidLupd;

	public TCkCtShellBatchWindow() {
	}

	public TCkCtShellBatchWindow(String sbId, String sbName, Date sbDtWindow, char sbStatus, Date sbDtCreate, String sbUidCreate, Date sbDtLupd, String sbUidLupd) {
		this.sbId = sbId;
		this.sbName = sbName;
		this.sbDtWindow = sbDtWindow;
		this.sbStatus = sbStatus;
		this.sbDtCreate = sbDtCreate;
		this.sbUidCreate = sbUidCreate;
		this.sbDtLupd = sbDtLupd;
		this.sbUidLupd = sbUidLupd;
	}

	@Id
	@Column(name = "SB_ID", unique = true, nullable = false, length = 35)
	public String getSbId() {
		return sbId;
	}

	public void setSbId(String sbId) {
		this.sbId = sbId;
	}

	@Column(name = "SB_NAME", length = 1024)
	public String getSbName() {
		return sbName;
	}

	public void setSbName(String sbName) {
		this.sbName = sbName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SB_DT_WINDOW", length = 35)
	public Date getSbDtWindow() {
		return sbDtWindow;
	}

	public void setSbDtWindow(Date sbDtWindow) {
		this.sbDtWindow = sbDtWindow;
	}

	@Column(name = "SB_STATUS", length = 1)
	public char getSbStatus() {
		return sbStatus;
	}

	public void setSbStatus(char sbStatus) {
		this.sbStatus = sbStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SB_DT_CREATE", length = 19)
	public Date getSbDtCreate() {
		return sbDtCreate;
	}

	public void setSbDtCreate(Date sbDtCreate) {
		this.sbDtCreate = sbDtCreate;
	}

	@Column(name = "SB_UID_CREATE", length = 35)
	public String getSbUidCreate() {
		return sbUidCreate;
	}

	public void setSbUidCreate(String sbUidCreate) {
		this.sbUidCreate = sbUidCreate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SB_DT_LUPD", length = 19)
	public Date getSbDtLupd() {
		return sbDtLupd;
	}

	public void setSbDtLupd(Date sbDtLupd) {
		this.sbDtLupd = sbDtLupd;
	}

	@Column(name = "SB_UID_LUPD", length = 35)
	public String getSbUidLupd() {
		return sbUidLupd;
	}

	public void setSbUidLupd(String sbUidLupd) {
		this.sbUidLupd = sbUidLupd;
	}

	@Override
	public void init() {

	}

	@Override
	public int compareTo(TCkCtShellBatchWindow o) {
		return 0;
	}
}
