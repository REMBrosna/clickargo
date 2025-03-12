package com.guudint.clickargo.clictruck.admin.ratetable.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.ratetable.model.TCkCtRateTableRemark;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtRateTableRemark extends AbstractDTO<CkCtRateTableRemark, TCkCtRateTableRemark> {

	private static final long serialVersionUID = -6182186294549404784L;

	private String rtrId;
	private CkCtRateTable TCkCtRateTable;
	private char rtrType;
	private String rtrComment;
	private Character rtrStatus;
	private Date rtrDtCreate;
	private String rtrUidCreate;
	private Date rtrDtLupd;
	private String rtrUidLupd;

	public CkCtRateTableRemark() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CkCtRateTableRemark(TCkCtRateTableRemark entity) {
		super(entity);
		// TODO Auto-generated constructor stub
	}

	public CkCtRateTableRemark(String rtrId, CkCtRateTable TCkCtRateTable, char rtrType) {
		this.rtrId = rtrId;
		this.TCkCtRateTable = TCkCtRateTable;
		this.rtrType = rtrType;
	}

	public CkCtRateTableRemark(String rtrId, CkCtRateTable tCkCtRateTable, char rtrType, String rtrComment,
			Character rtrStatus, Date rtrDtCreate, String rtrUidCreate, Date rtrDtLupd, String rtrUidLupd) {
		super();
		this.rtrId = rtrId;
		TCkCtRateTable = tCkCtRateTable;
		this.rtrType = rtrType;
		this.rtrComment = rtrComment;
		this.rtrStatus = rtrStatus;
		this.rtrDtCreate = rtrDtCreate;
		this.rtrUidCreate = rtrUidCreate;
		this.rtrDtLupd = rtrDtLupd;
		this.rtrUidLupd = rtrUidLupd;
	}

	public String getRtrId() {
		return rtrId;
	}

	public void setRtrId(String rtrId) {
		this.rtrId = rtrId;
	}

	/**
	 * @return the tCkCtRateTable
	 */
	public CkCtRateTable getTCkCtRateTable() {
		return TCkCtRateTable;
	}

	/**
	 * @param tCkCtRateTable the tCkCtRateTable to set
	 */
	public void setTCkCtRateTable(CkCtRateTable tCkCtRateTable) {
		TCkCtRateTable = tCkCtRateTable;
	}

	public char getRtrType() {
		return rtrType;
	}

	public void setRtrType(char rtrType) {
		this.rtrType = rtrType;
	}

	public String getRtrComment() {
		return rtrComment;
	}

	public void setRtrComment(String rtrComment) {
		this.rtrComment = rtrComment;
	}

	public Character getRtrStatus() {
		return rtrStatus;
	}

	public void setRtrStatus(Character rtrStatus) {
		this.rtrStatus = rtrStatus;
	}

	public Date getRtrDtCreate() {
		return rtrDtCreate;
	}

	public void setRtrDtCreate(Date rtrDtCreate) {
		this.rtrDtCreate = rtrDtCreate;
	}

	public String getRtrUidCreate() {
		return rtrUidCreate;
	}

	public void setRtrUidCreate(String rtrUidCreate) {
		this.rtrUidCreate = rtrUidCreate;
	}

	public Date getRtrDtLupd() {
		return rtrDtLupd;
	}

	public void setRtrDtLupd(Date rtrDtLupd) {
		this.rtrDtLupd = rtrDtLupd;
	}

	public String getRtrUidLupd() {
		return rtrUidLupd;
	}

	public void setRtrUidLupd(String rtrUidLupd) {
		this.rtrUidLupd = rtrUidLupd;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int compareTo(CkCtRateTableRemark o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
