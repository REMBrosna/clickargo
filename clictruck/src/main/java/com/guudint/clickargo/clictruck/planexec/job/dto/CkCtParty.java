package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstPartyType;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtParty;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtParty extends AbstractDTO<CkCtParty, TCkCtParty> {

	private static final long serialVersionUID = 6967491143813807084L;
	private String ptyId;
	private TCkCtMstPartyType TCkCtMstPartyType;
	private String ptyName;
	private String ptyNameOth;
	private String ptyAddr1;
	private String ptyAddr2;
	private String ptyAddr3;
	private String ptyPcode;
	private String ptyCity;
	private String ptyCtycode;
	private Character ptyStatus;
	private Date ptyDtCreate;
	private String ptyUidCreate;
	private Date ptyDtLupd;
	private String ptyUidLupd;

	public CkCtParty() {
	}

	public CkCtParty(TCkCtParty entity) {
		super(entity);
	}

	public CkCtParty(String ptyId, TCkCtMstPartyType TCkCtMstPartyType) {
		this.ptyId = ptyId;
		this.TCkCtMstPartyType = TCkCtMstPartyType;
	}

	public CkCtParty(String ptyId, TCkCtMstPartyType TCkCtMstPartyType, String ptyName, String ptyNameOth,
			String ptyAddr1, String ptyAddr2, String ptyAddr3, String ptyPcode, String ptyCity, String ptyCtycode,
			Character ptyStatus, Date ptyDtCreate, String ptyUidCreate, Date ptyDtLupd, String ptyUidLupd) {
		this.ptyId = ptyId;
		this.TCkCtMstPartyType = TCkCtMstPartyType;
		this.ptyName = ptyName;
		this.ptyNameOth = ptyNameOth;
		this.ptyAddr1 = ptyAddr1;
		this.ptyAddr2 = ptyAddr2;
		this.ptyAddr3 = ptyAddr3;
		this.ptyPcode = ptyPcode;
		this.ptyCity = ptyCity;
		this.ptyCtycode = ptyCtycode;
		this.ptyStatus = ptyStatus;
		this.ptyDtCreate = ptyDtCreate;
		this.ptyUidCreate = ptyUidCreate;
		this.ptyDtLupd = ptyDtLupd;
		this.ptyUidLupd = ptyUidLupd;
	}

	public String getPtyId() {
		return ptyId;
	}

	public void setPtyId(String ptyId) {
		this.ptyId = ptyId;
	}

	public TCkCtMstPartyType getTCkCtMstPartyType() {
		return TCkCtMstPartyType;
	}

	public void setTCkCtMstPartyType(TCkCtMstPartyType tCkCtMstPartyType) {
		TCkCtMstPartyType = tCkCtMstPartyType;
	}

	public String getPtyName() {
		return ptyName;
	}

	public void setPtyName(String ptyName) {
		this.ptyName = ptyName;
	}

	public String getPtyNameOth() {
		return ptyNameOth;
	}

	public void setPtyNameOth(String ptyNameOth) {
		this.ptyNameOth = ptyNameOth;
	}

	public String getPtyAddr1() {
		return ptyAddr1;
	}

	public void setPtyAddr1(String ptyAddr1) {
		this.ptyAddr1 = ptyAddr1;
	}

	public String getPtyAddr2() {
		return ptyAddr2;
	}

	public void setPtyAddr2(String ptyAddr2) {
		this.ptyAddr2 = ptyAddr2;
	}

	public String getPtyAddr3() {
		return ptyAddr3;
	}

	public void setPtyAddr3(String ptyAddr3) {
		this.ptyAddr3 = ptyAddr3;
	}

	public String getPtyPcode() {
		return ptyPcode;
	}

	public void setPtyPcode(String ptyPcode) {
		this.ptyPcode = ptyPcode;
	}

	public String getPtyCity() {
		return ptyCity;
	}

	public void setPtyCity(String ptyCity) {
		this.ptyCity = ptyCity;
	}

	public String getPtyCtycode() {
		return ptyCtycode;
	}

	public void setPtyCtycode(String ptyCtycode) {
		this.ptyCtycode = ptyCtycode;
	}

	public Character getPtyStatus() {
		return ptyStatus;
	}

	public void setPtyStatus(Character ptyStatus) {
		this.ptyStatus = ptyStatus;
	}

	public Date getPtyDtCreate() {
		return ptyDtCreate;
	}

	public void setPtyDtCreate(Date ptyDtCreate) {
		this.ptyDtCreate = ptyDtCreate;
	}

	public String getPtyUidCreate() {
		return ptyUidCreate;
	}

	public void setPtyUidCreate(String ptyUidCreate) {
		this.ptyUidCreate = ptyUidCreate;
	}

	public Date getPtyDtLupd() {
		return ptyDtLupd;
	}

	public void setPtyDtLupd(Date ptyDtLupd) {
		this.ptyDtLupd = ptyDtLupd;
	}

	public String getPtyUidLupd() {
		return ptyUidLupd;
	}

	public void setPtyUidLupd(String ptyUidLupd) {
		this.ptyUidLupd = ptyUidLupd;
	}

	@Override
	public int compareTo(CkCtParty o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
