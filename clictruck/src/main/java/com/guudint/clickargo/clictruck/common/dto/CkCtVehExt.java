package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.guudint.clickargo.clictruck.common.model.TCkCtVehExt;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtVehExt extends AbstractDTO<CkCtVehExt, TCkCtVehExt> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 7608850623786062520L;

	// Attributes
	/////////////
	private CkCtVehExtId id;
	private CkCtVeh TCkCtVeh;
	private String vextValue;
	private Character vextMonitorMthd;
	private String vextMonitorValue;
	private Character vextNotify;
	private String vextNotifyEmail;
	private String vextNotifyWhatsapp;
	private String vextRemarks;
	private Character vextStatus;
	private Date vextDtCreate;
	private String vextUidCreate;
	private Date vextDtLupd;
	private String vextUidLupd;

	// place holder just to differentiate if it's maintenance or expiry; one use if for validation
	private String extType; // MNT | EXP

	// Constructors
	///////////////
	public CkCtVehExt() {
		// TODO Auto-generated constructor stub
	}

	public CkCtVehExt(TCkCtVehExt entity) {
		super(entity);
	}

	public CkCtVehExt(CkCtVehExtId id, CkCtVeh TCkCtVeh) {
		this.id = id;
		this.TCkCtVeh = TCkCtVeh;
	}

	public CkCtVehExt(CkCtVehExtId id, CkCtVeh tCkCtVeh, String vextValue, Character vextMonitorMthd,
			String vextMonitorValue, Character vextNotify, String vextNotifyEmail, String vextNotifyWhatsapp,
			String vextRemarks, Character vextStatus, Date vextDtCreate, String vextUidCreate, Date vextDtLupd,
			String vextUidLupd) {
		super();
		this.id = id;
		TCkCtVeh = tCkCtVeh;
		this.vextValue = vextValue;
		this.vextMonitorMthd = vextMonitorMthd;
		this.vextMonitorValue = vextMonitorValue;
		this.vextNotify = vextNotify;
		this.vextNotifyEmail = vextNotifyEmail;
		this.vextNotifyWhatsapp = vextNotifyWhatsapp;
		this.vextRemarks = vextRemarks;
		this.vextStatus = vextStatus;
		this.vextDtCreate = vextDtCreate;
		this.vextUidCreate = vextUidCreate;
		this.vextDtLupd = vextDtLupd;
		this.vextUidLupd = vextUidLupd;
	}

	public CkCtVehExtId getId() {
		return id;
	}

	public void setId(CkCtVehExtId ckCtVehExtId) {
		this.id = ckCtVehExtId;
	}

	@JsonIgnore
	public CkCtVeh getTCkCtVeh() {
		return TCkCtVeh;
	}

	public void setTCkCtVeh(CkCtVeh tCkCtVeh) {
		TCkCtVeh = tCkCtVeh;
	}

	public String getVextValue() {
		return vextValue;
	}

	public void setVextValue(String vextValue) {
		this.vextValue = vextValue;
	}

	public Character getVextMonitorMthd() {
		return vextMonitorMthd;
	}

	public void setVextMonitorMthd(Character vextMonitorMthd) {
		this.vextMonitorMthd = vextMonitorMthd;
	}

	public String getVextMonitorValue() {
		return vextMonitorValue;
	}

	public void setVextMonitorValue(String vextMonitorValue) {
		this.vextMonitorValue = vextMonitorValue;
	}

	public Character getVextNotify() {
		return vextNotify;
	}

	public void setVextNotify(Character vextNotify) {
		this.vextNotify = vextNotify;
	}

	public String getVextNotifyEmail() {
		return vextNotifyEmail;
	}

	public void setVextNotifyEmail(String vextNotifyEmail) {
		this.vextNotifyEmail = vextNotifyEmail;
	}

	public String getVextNotifyWhatsapp() {
		return vextNotifyWhatsapp;
	}

	public void setVextNotifyWhatsapp(String vextNotifyWhatsapp) {
		this.vextNotifyWhatsapp = vextNotifyWhatsapp;
	}

	public String getVextRemarks() {
		return vextRemarks;
	}

	public void setVextRemarks(String vextRemarks) {
		this.vextRemarks = vextRemarks;
	}

	public Character getVextStatus() {
		return vextStatus;
	}

	public void setVextStatus(Character vextStatus) {
		this.vextStatus = vextStatus;
	}

	public Date getVextDtCreate() {
		return vextDtCreate;
	}

	public void setVextDtCreate(Date vextDtCreate) {
		this.vextDtCreate = vextDtCreate;
	}

	public String getVextUidCreate() {
		return vextUidCreate;
	}

	public void setVextUidCreate(String vextUidCreate) {
		this.vextUidCreate = vextUidCreate;
	}

	public Date getVextDtLupd() {
		return vextDtLupd;
	}

	public void setVextDtLupd(Date vextDtLupd) {
		this.vextDtLupd = vextDtLupd;
	}

	public String getVextUidLupd() {
		return vextUidLupd;
	}

	public void setVextUidLupd(String vextUidLupd) {
		this.vextUidLupd = vextUidLupd;
	}

	@Override
	public int compareTo(CkCtVehExt o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the extType
	 */
	public String getExtType() {
		return extType;
	}

	/**
	 * @param extType the extType to set
	 */
	public void setExtType(String extType) {
		this.extType = extType;
	}

}
