package com.guudint.clickargo.clictruck.common.dto;
// Generated 29 Aug 2023, 4:49:59 pm by Hibernate Tools 4.3.6.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtTrackDevice;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstTrackDeviceState;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;
import com.vcc.camelone.master.dto.MstAccnType;


public class CkCtTrackDevice extends AbstractDTO<CkCtTrackDevice, TCkCtTrackDevice>{

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;
	
	private String tdId;
	private CkCtMstTrackDeviceState TCkCtMstTrackDeviceState;
	private CkCtVeh TCkCtVeh;
	private CoreAccn TCoreAccn;
	private MstAccnType TMstAccnType;
	private String tdVehPlateNo;
	private String tdGpsImei;
	private Date tdDtActivattion;
	private Date tdDtActivate;
	private Date tdDtDeactivate;
	private String tdUidActivate;
	private String tdUidDeactivate;
	private Character tdStatus;
	private Date tdDtCreate;
	private String tdUidCreate;
	private Date tdDtLupd;
	private String tdUidLupd;
	
	private String history;

	public CkCtTrackDevice() {
	}

	public CkCtTrackDevice(TCkCtTrackDevice entity) {
		super(entity);
	}

	public CkCtTrackDevice(String tdId, CkCtMstTrackDeviceState TCkCtMstTrackDeviceState) {
		this.tdId = tdId;
		this.TCkCtMstTrackDeviceState = TCkCtMstTrackDeviceState;
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
	public int compareTo(CkCtTrackDevice o) {
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
	
	public String getTdId() {
		return this.tdId;
	}

	public void setTdId(String tdId) {
		this.tdId = tdId;
	}

	public CkCtMstTrackDeviceState getTCkCtMstTrackDeviceState() {
		return this.TCkCtMstTrackDeviceState;
	}

	public void setTCkCtMstTrackDeviceState(CkCtMstTrackDeviceState TCkCtMstTrackDeviceState) {
		this.TCkCtMstTrackDeviceState = TCkCtMstTrackDeviceState;
	}

	public CkCtVeh getTCkCtVeh() {
		return this.TCkCtVeh;
	}

	public void setTCkCtVeh(CkCtVeh TCkCtVeh) {
		this.TCkCtVeh = TCkCtVeh;
	}

	public CoreAccn getTCoreAccn() {
		return this.TCoreAccn;
	}

	public void setTCoreAccn(CoreAccn TCoreAccn) {
		this.TCoreAccn = TCoreAccn;
	}

	public MstAccnType getTMstAccnType() {
		return this.TMstAccnType;
	}

	public void setTMstAccnType(MstAccnType TMstAccnType) {
		this.TMstAccnType = TMstAccnType;
	}

	public String getTdVehPlateNo() {
		return this.tdVehPlateNo;
	}

	public void setTdVehPlateNo(String tdVehPlateNo) {
		this.tdVehPlateNo = tdVehPlateNo;
	}

	public String getTdGpsImei() {
		return this.tdGpsImei;
	}

	public void setTdGpsImei(String tdGpsImei) {
		this.tdGpsImei = tdGpsImei;
	}

	public Date getTdDtActivattion() {
		return this.tdDtActivattion;
	}

	public void setTdDtActivattion(Date tdDtActivattion) {
		this.tdDtActivattion = tdDtActivattion;
	}

	public Date getTdDtActivate() {
		return this.tdDtActivate;
	}

	public void setTdDtActivate(Date tdDtActivate) {
		this.tdDtActivate = tdDtActivate;
	}

	public Date getTdDtDeactivate() {
		return this.tdDtDeactivate;
	}

	public void setTdDtDeactivate(Date tdDtDeactivate) {
		this.tdDtDeactivate = tdDtDeactivate;
	}

	public String getTdUidActivate() {
		return this.tdUidActivate;
	}

	public void setTdUidActivate(String tdUidActivate) {
		this.tdUidActivate = tdUidActivate;
	}

	public String getTdUidDeactivate() {
		return this.tdUidDeactivate;
	}

	public void setTdUidDeactivate(String tdUidDeactivate) {
		this.tdUidDeactivate = tdUidDeactivate;
	}

	public Character getTdStatus() {
		return this.tdStatus;
	}

	public void setTdStatus(Character tdStatus) {
		this.tdStatus = tdStatus;
	}

	public Date getTdDtCreate() {
		return this.tdDtCreate;
	}

	public void setTdDtCreate(Date tdDtCreate) {
		this.tdDtCreate = tdDtCreate;
	}

	public String getTdUidCreate() {
		return this.tdUidCreate;
	}

	public void setTdUidCreate(String tdUidCreate) {
		this.tdUidCreate = tdUidCreate;
	}

	public Date getTdDtLupd() {
		return this.tdDtLupd;
	}

	public void setTdDtLupd(Date tdDtLupd) {
		this.tdDtLupd = tdDtLupd;
	}

	public String getTdUidLupd() {
		return this.tdUidLupd;
	}

	public void setTdUidLupd(String tdUidLupd) {
		this.tdUidLupd = tdUidLupd;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

}
