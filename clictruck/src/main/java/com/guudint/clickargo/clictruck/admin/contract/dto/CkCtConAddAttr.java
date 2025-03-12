package com.guudint.clickargo.clictruck.admin.contract.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtConAddAttr;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtConAddAttr extends AbstractDTO<CkCtConAddAttr, TCkCtConAddAttr> {

	private static final long serialVersionUID = -6114801356625701380L;
	private String caaId;
	private CkCtAddAttrList TCkCtAddAttrList;
	private CkCtContract TCkCtContract;
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

	public CkCtConAddAttr() {
	}

	public CkCtConAddAttr(TCkCtConAddAttr entity) {
		super(entity);
	}

	public CkCtConAddAttr(String caaId, CkCtAddAttrList TCkCtAddAttrList, CkCtContract TCkCtContract, Character caaType,
			Short caaSeq, String caaLabel, String caaDescription, Character caaStatus, Date caaDtCreate,
			String caaUidCreate, Date caaDtLupd, String caaUidLupd) {
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

	public String getCaaId() {
		return caaId;
	}

	public void setCaaId(String caaId) {
		this.caaId = caaId;
	}

	public CkCtAddAttrList getTCkCtAddAttrList() {
		return TCkCtAddAttrList;
	}

	public void setTCkCtAddAttrList(CkCtAddAttrList tCkCtAddAttrList) {
		TCkCtAddAttrList = tCkCtAddAttrList;
	}

	public CkCtContract getTCkCtContract() {
		return TCkCtContract;
	}

	public void setTCkCtContract(CkCtContract tCkCtContract) {
		TCkCtContract = tCkCtContract;
	}

	public Character getCaaType() {
		return caaType;
	}

	public void setCaaType(Character caaType) {
		this.caaType = caaType;
	}

	public Short getCaaSeq() {
		return caaSeq;
	}

	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}

	public String getCaaLabel() {
		return caaLabel;
	}

	public void setCaaLabel(String caaLabel) {
		this.caaLabel = caaLabel;
	}

	public String getCaaDescription() {
		return caaDescription;
	}

	public void setCaaDescription(String caaDescription) {
		this.caaDescription = caaDescription;
	}

	public Character getCaaStatus() {
		return caaStatus;
	}

	public void setCaaStatus(Character caaStatus) {
		this.caaStatus = caaStatus;
	}

	public Date getCaaDtCreate() {
		return caaDtCreate;
	}

	public void setCaaDtCreate(Date caaDtCreate) {
		this.caaDtCreate = caaDtCreate;
	}

	public String getCaaUidCreate() {
		return caaUidCreate;
	}

	public void setCaaUidCreate(String caaUidCreate) {
		this.caaUidCreate = caaUidCreate;
	}

	public Date getCaaDtLupd() {
		return caaDtLupd;
	}

	public void setCaaDtLupd(Date caaDtLupd) {
		this.caaDtLupd = caaDtLupd;
	}

	public String getCaaUidLupd() {
		return caaUidLupd;
	}

	public void setCaaUidLupd(String caaUidLupd) {
		this.caaUidLupd = caaUidLupd;
	}

	@Override
	public int compareTo(CkCtConAddAttr o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	public Character getCaaRequired() {
		return caaRequired;
	}

	public void setCaaRequired(Character caaRequired) {
		this.caaRequired = caaRequired;
	}

}
