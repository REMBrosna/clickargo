package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstPartyType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstPartyType extends AbstractDTO<CkCtMstPartyType, TCkCtMstPartyType> {

	private static final long serialVersionUID = -8106602510797667924L;
	private String ptId;
	private String ptName;
	private String ptDesc;
	private String ptDescOth;
	private Character ptStatus;
	private Date ptDtCreate;
	private String ptUidCreate;
	private Date ptDtLupd;
	private String ptUidLupd;

	public CkCtMstPartyType() {
	}

	public CkCtMstPartyType(String ptId, String ptName) {
		this.ptId = ptId;
		this.ptName = ptName;
	}

	public CkCtMstPartyType(TCkCtMstPartyType entity) {
		super(entity);
	}

	public CkCtMstPartyType(String ptId, String ptName, String ptDesc, String ptDescOth, Character ptStatus,
			Date ptDtCreate, String ptUidCreate, Date ptDtLupd, String ptUidLupd) {
		this.ptId = ptId;
		this.ptName = ptName;
		this.ptDesc = ptDesc;
		this.ptDescOth = ptDescOth;
		this.ptStatus = ptStatus;
		this.ptDtCreate = ptDtCreate;
		this.ptUidCreate = ptUidCreate;
		this.ptDtLupd = ptDtLupd;
		this.ptUidLupd = ptUidLupd;
	}

	public String getPtId() {
		return ptId;
	}

	public void setPtId(String ptId) {
		this.ptId = ptId;
	}

	public String getPtName() {
		return ptName;
	}

	public void setPtName(String ptName) {
		this.ptName = ptName;
	}

	public String getPtDesc() {
		return ptDesc;
	}

	public void setPtDesc(String ptDesc) {
		this.ptDesc = ptDesc;
	}

	public String getPtDescOth() {
		return ptDescOth;
	}

	public void setPtDescOth(String ptDescOth) {
		this.ptDescOth = ptDescOth;
	}

	public Character getPtStatus() {
		return ptStatus;
	}

	public void setPtStatus(Character ptStatus) {
		this.ptStatus = ptStatus;
	}

	public Date getPtDtCreate() {
		return ptDtCreate;
	}

	public void setPtDtCreate(Date ptDtCreate) {
		this.ptDtCreate = ptDtCreate;
	}

	public String getPtUidCreate() {
		return ptUidCreate;
	}

	public void setPtUidCreate(String ptUidCreate) {
		this.ptUidCreate = ptUidCreate;
	}

	public Date getPtDtLupd() {
		return ptDtLupd;
	}

	public void setPtDtLupd(Date ptDtLupd) {
		this.ptDtLupd = ptDtLupd;
	}

	public String getPtUidLupd() {
		return ptUidLupd;
	}

	public void setPtUidLupd(String ptUidLupd) {
		this.ptUidLupd = ptUidLupd;
	}

	@Override
	public int compareTo(CkCtMstPartyType o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
