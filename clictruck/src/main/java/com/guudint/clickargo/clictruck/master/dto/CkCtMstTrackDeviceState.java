package com.guudint.clickargo.clictruck.master.dto;
// Generated 29 Aug 2023, 4:36:48 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstTrackDeviceState;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstTrackDeviceState extends AbstractDTO<CkCtMstTrackDeviceState, TCkCtMstTrackDeviceState> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;

	private String tdsId;
	private String tdsName;
	private String tdsDesc;
	private String tdsDescOth;
	private Character tdsStatus;
	private Date tdsDtCreate;
	private String tdsUidCreate;
	private Date tdsDtLupd;
	private String tdsUidLupd;

	// Constructors
	///////////////
	public CkCtMstTrackDeviceState() {
	}

	/**
	 * @param entity
	 */
	public CkCtMstTrackDeviceState(TCkCtMstTrackDeviceState entity) {
		super(entity);
	}

	public CkCtMstTrackDeviceState(String tdsId) {
		this.tdsId = tdsId;
	}

	public CkCtMstTrackDeviceState(String tdsId, String tdsName, String tdsDesc, String tdsDescOth, Character tdsStatus,
			Date tdsDtCreate, String tdsUidCreate, Date tdsDtLupd, String tdsUidLupd) {
		this.tdsId = tdsId;
		this.tdsName = tdsName;
		this.tdsDesc = tdsDesc;
		this.tdsDescOth = tdsDescOth;
		this.tdsStatus = tdsStatus;
		this.tdsDtCreate = tdsDtCreate;
		this.tdsUidCreate = tdsUidCreate;
		this.tdsDtLupd = tdsDtLupd;
		this.tdsUidLupd = tdsUidLupd;
	}

	// Override Methods
	///////////////////
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 */
	@Override
	public int compareTo(CkCtMstTrackDeviceState o) {
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.COAbstractEntity#init()
	 * 
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	public String getTdsId() {
		return this.tdsId;
	}

	public void setTdsId(String tdsId) {
		this.tdsId = tdsId;
	}

	public String getTdsName() {
		return this.tdsName;
	}

	public void setTdsName(String tdsName) {
		this.tdsName = tdsName;
	}

	public String getTdsDesc() {
		return this.tdsDesc;
	}

	public void setTdsDesc(String tdsDesc) {
		this.tdsDesc = tdsDesc;
	}

	public String getTdsDescOth() {
		return this.tdsDescOth;
	}

	public void setTdsDescOth(String tdsDescOth) {
		this.tdsDescOth = tdsDescOth;
	}

	public Character getTdsStatus() {
		return this.tdsStatus;
	}

	public void setTdsStatus(Character tdsStatus) {
		this.tdsStatus = tdsStatus;
	}

	public Date getTdsDtCreate() {
		return this.tdsDtCreate;
	}

	public void setTdsDtCreate(Date tdsDtCreate) {
		this.tdsDtCreate = tdsDtCreate;
	}

	public String getTdsUidCreate() {
		return this.tdsUidCreate;
	}

	public void setTdsUidCreate(String tdsUidCreate) {
		this.tdsUidCreate = tdsUidCreate;
	}

	public Date getTdsDtLupd() {
		return this.tdsDtLupd;
	}

	public void setTdsDtLupd(Date tdsDtLupd) {
		this.tdsDtLupd = tdsDtLupd;
	}

	public String getTdsUidLupd() {
		return this.tdsUidLupd;
	}

	public void setTdsUidLupd(String tdsUidLupd) {
		this.tdsUidLupd = tdsUidLupd;
	}

}
