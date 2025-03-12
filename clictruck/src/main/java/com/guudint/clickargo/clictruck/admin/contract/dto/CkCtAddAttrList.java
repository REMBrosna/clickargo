package com.guudint.clickargo.clictruck.admin.contract.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtAddAttrList;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtAddAttrList extends AbstractDTO<CkCtAddAttrList, TCkCtAddAttrList> {

	private static final long serialVersionUID = 6221536129155686102L;
	private String aalId;
	private String aalList;
	private String aalDescription;
	private Character aalStatus;
	private Date aalDtCreate;
	private String aalUidCreate;
	private Date aalDtLupd;
	private String aalUidLupd;

	public CkCtAddAttrList() {
	}

	public CkCtAddAttrList(TCkCtAddAttrList entity) {
		super(entity);
	}

	public CkCtAddAttrList(String aalId, String aalList, String aalDescription, Character aalStatus, Date aalDtCreate,
			String aalUidCreate, Date aalDtLupd, String aalUidLupd) {
		this.aalId = aalId;
		this.aalList = aalList;
		this.aalDescription = aalDescription;
		this.aalStatus = aalStatus;
		this.aalDtCreate = aalDtCreate;
		this.aalUidCreate = aalUidCreate;
		this.aalDtLupd = aalDtLupd;
		this.aalUidLupd = aalUidLupd;
	}

	public String getAalId() {
		return aalId;
	}

	public void setAalId(String aalId) {
		this.aalId = aalId;
	}

	public String getAalList() {
		return aalList;
	}

	public void setAalList(String aalList) {
		this.aalList = aalList;
	}

	public String getAalDescription() {
		return aalDescription;
	}

	public void setAalDescription(String aalDescription) {
		this.aalDescription = aalDescription;
	}

	public Character getAalStatus() {
		return aalStatus;
	}

	public void setAalStatus(Character aalStatus) {
		this.aalStatus = aalStatus;
	}

	public Date getAalDtCreate() {
		return aalDtCreate;
	}

	public void setAalDtCreate(Date aalDtCreate) {
		this.aalDtCreate = aalDtCreate;
	}

	public String getAalUidCreate() {
		return aalUidCreate;
	}

	public void setAalUidCreate(String aalUidCreate) {
		this.aalUidCreate = aalUidCreate;
	}

	public Date getAalDtLupd() {
		return aalDtLupd;
	}

	public void setAalDtLupd(Date aalDtLupd) {
		this.aalDtLupd = aalDtLupd;
	}

	public String getAalUidLupd() {
		return aalUidLupd;
	}

	public void setAalUidLupd(String aalUidLupd) {
		this.aalUidLupd = aalUidLupd;
	}

	@Override
	public int compareTo(CkCtAddAttrList o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
