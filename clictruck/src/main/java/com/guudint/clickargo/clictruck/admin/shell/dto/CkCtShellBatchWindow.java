package com.guudint.clickargo.clictruck.admin.shell.dto;

import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellBatchWindow;
import com.guudint.clickargo.clictruck.admin.shell.model.TCkCtShellCard;
import com.vcc.camelone.common.dto.AbstractDTO;

import java.util.Date;

public class CkCtShellBatchWindow extends AbstractDTO<CkCtShellBatchWindow, TCkCtShellBatchWindow> {

	private String sbId;
	private String sbName;
	private Date sbDtWindow;
	private char sbStatus;
	private Date sbDtCreate;
	private String sbUidCreate;
	private Date sbDtLupd;
	private String sbUidLupd;

	public CkCtShellBatchWindow() {
	}

	public CkCtShellBatchWindow(TCkCtShellBatchWindow entity) {
		super(entity);
	}

	public CkCtShellBatchWindow(String sbId, String sbName, Date sbDtWindow, char sbStatus, Date sbDtCreate, String sbUidCreate, Date sbDtLupd, String sbUidLupd) {
		this.sbId = sbId;
		this.sbName = sbName;
		this.sbDtWindow = sbDtWindow;
		this.sbStatus = sbStatus;
		this.sbDtCreate = sbDtCreate;
		this.sbUidCreate = sbUidCreate;
		this.sbDtLupd = sbDtLupd;
		this.sbUidLupd = sbUidLupd;
	}

	public String getSbId() {
		return sbId;
	}

	public void setSbId(String sbId) {
		this.sbId = sbId;
	}

	public String getSbName() {
		return sbName;
	}

	public void setSbName(String sbName) {
		this.sbName = sbName;
	}

	public Date getSbDtWindow() {
		return sbDtWindow;
	}

	public void setSbDtWindow(Date sbDtWindow) {
		this.sbDtWindow = sbDtWindow;
	}

	public char getSbStatus() {
		return sbStatus;
	}

	public void setSbStatus(char sbStatus) {
		this.sbStatus = sbStatus;
	}

	public Date getSbDtCreate() {
		return sbDtCreate;
	}

	public void setSbDtCreate(Date sbDtCreate) {
		this.sbDtCreate = sbDtCreate;
	}

	public String getSbUidCreate() {
		return sbUidCreate;
	}

	public void setSbUidCreate(String sbUidCreate) {
		this.sbUidCreate = sbUidCreate;
	}

	public Date getSbDtLupd() {
		return sbDtLupd;
	}

	public void setSbDtLupd(Date sbDtLupd) {
		this.sbDtLupd = sbDtLupd;
	}

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
	public int compareTo(CkCtShellBatchWindow o) {
		return 0;
	}
}
