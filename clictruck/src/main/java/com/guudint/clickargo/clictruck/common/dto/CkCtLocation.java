package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtLocation;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstLocationType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtLocation extends AbstractDTO<CkCtLocation, TCkCtLocation> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -5275964103902549912L;

	// Attributes
	/////////////
	private String locId;
	private CkCtMstLocationType TCkCtMstLocationType;
	private CoreAccn TCoreAccn;
	private String locName;
	private String locAddress;
	private Date locDtStart;
	private Date locDtEnd;
	private String locRemarks;
	private String locGps;
	private Character locStatus;
	private Date locDtCreate;
	private String locUidCreate;
	private Date locDtLupd;
	private String locUidLupd;

	// Constructors
	///////////////
	public CkCtLocation() {
	}

	public CkCtLocation(TCkCtLocation entity) {
		super(entity);
	}

	public CkCtLocation(String locId) {
		this.locId = locId;
	}

	/**
	 * @param locId
	 * @param tCkCtMstLocationType
	 * @param tCoreAccn
	 * @param locName
	 * @param locAddress
	 * @param locDtStart
	 * @param locDtEnd
	 * @param locRemarks
	 * @param locStatus
	 * @param locDtCreate
	 * @param locUidCreate
	 * @param locDtLupd
	 * @param locUidLupd
	 */
	public CkCtLocation(String locId, CkCtMstLocationType tCkCtMstLocationType, CoreAccn tCoreAccn, String locName,
			String locAddress, Date locDtStart, Date locDtEnd, String locRemarks, String locGps, Character locStatus,
			Date locDtCreate, String locUidCreate, Date locDtLupd,
			String locUidLupd) {
		super();
		this.locId = locId;
		TCkCtMstLocationType = tCkCtMstLocationType;
		TCoreAccn = tCoreAccn;
		this.locName = locName;
		this.locAddress = locAddress;
		this.locDtStart = locDtStart;
		this.locDtEnd = locDtEnd;
		this.locRemarks = locRemarks;
		this.locGps = locGps;
		this.locStatus = locStatus;
		this.locDtCreate = locDtCreate;
		this.locUidCreate = locUidCreate;
		this.locDtLupd = locDtLupd;
		this.locUidLupd = locUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the locId
	 */
	public String getLocId() {
		return locId;
	}

	/**
	 * @param locId the locId to set
	 */
	public void setLocId(String locId) {
		this.locId = locId;
	}

	/**
	 * @return the tCkCtMstLocationType
	 */
	public CkCtMstLocationType getTCkCtMstLocationType() {
		return TCkCtMstLocationType;
	}

	/**
	 * @param tCkCtMstLocationType the tCkCtMstLocationType to set
	 */
	public void setTCkCtMstLocationType(CkCtMstLocationType tCkCtMstLocationType) {
		TCkCtMstLocationType = tCkCtMstLocationType;
	}

	/**
	 * @return the tCoreAccn
	 */
	public CoreAccn getTCoreAccn() {
		return TCoreAccn;
	}

	/**
	 * @param tCoreAccn the tCoreAccn to set
	 */
	public void setTCoreAccn(CoreAccn tCoreAccn) {
		TCoreAccn = tCoreAccn;
	}

	/**
	 * @return the locName
	 */
	public String getLocName() {
		return locName;
	}

	/**
	 * @param locName the locName to set
	 */
	public void setLocName(String locName) {
		this.locName = locName;
	}

	/**
	 * @return the locAddress
	 */
	public String getLocAddress() {
		return locAddress;
	}

	/**
	 * @param locAddress the locAddress to set
	 */
	public void setLocAddress(String locAddress) {
		this.locAddress = locAddress;
	}

	/**
	 * @return the locDtStart
	 */
	public Date getLocDtStart() {
		return locDtStart;
	}

	/**
	 * @param locDtStart the locDtStart to set
	 */
	public void setLocDtStart(Date locDtStart) {
		this.locDtStart = locDtStart;
	}

	/**
	 * @return the locDtEnd
	 */
	public Date getLocDtEnd() {
		return locDtEnd;
	}

	/**
	 * @param locDtEnd the locDtEnd to set
	 */
	public void setLocDtEnd(Date locDtEnd) {
		this.locDtEnd = locDtEnd;
	}

	/**
	 * @return the locRemarks
	 */
	public String getLocRemarks() {
		return locRemarks;
	}

	/**
	 * @param locRemarks the locRemarks to set
	 */
	public void setLocRemarks(String locRemarks) {
		this.locRemarks = locRemarks;
	}

	public String getLocGps() {
		return this.locGps;
	}

	public void setLocGps(String locGps) {
		this.locGps = locGps;
	}

	/**
	 * @return the locStatus
	 */
	public Character getLocStatus() {
		return locStatus;
	}

	/**
	 * @param locStatus the locStatus to set
	 */
	public void setLocStatus(Character locStatus) {
		this.locStatus = locStatus;
	}

	/**
	 * @return the locDtCreate
	 */
	public Date getLocDtCreate() {
		return locDtCreate;
	}

	/**
	 * @param locDtCreate the locDtCreate to set
	 */
	public void setLocDtCreate(Date locDtCreate) {
		this.locDtCreate = locDtCreate;
	}

	/**
	 * @return the locUidCreate
	 */
	public String getLocUidCreate() {
		return locUidCreate;
	}

	/**
	 * @param locUidCreate the locUidCreate to set
	 */
	public void setLocUidCreate(String locUidCreate) {
		this.locUidCreate = locUidCreate;
	}

	/**
	 * @return the locDtLupd
	 */
	public Date getLocDtLupd() {
		return locDtLupd;
	}

	/**
	 * @param locDtLupd the locDtLupd to set
	 */
	public void setLocDtLupd(Date locDtLupd) {
		this.locDtLupd = locDtLupd;
	}

	/**
	 * @return the locUidLupd
	 */
	public String getLocUidLupd() {
		return locUidLupd;
	}

	/**
	 * @param locUidLupd the locUidLupd to set
	 */
	public void setLocUidLupd(String locUidLupd) {
		this.locUidLupd = locUidLupd;
	}

	// Override Methods
	///////////////////
	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 *
	 */
	@Override
	public int compareTo(CkCtLocation o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.vcc.camelone.common.COAbstractEntity#init()
	 *
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
