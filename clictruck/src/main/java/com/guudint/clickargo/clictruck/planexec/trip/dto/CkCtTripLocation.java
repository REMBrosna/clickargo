package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.dto.CkCtLocation;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripLocation;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripLocation extends AbstractDTO<CkCtTripLocation, TCkCtTripLocation> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 7640296136372205126L;

	// Attributes
	/////////////
	private String tlocId;
	private CkCtLocation TCkCtLocation;
	private String tlocLocName;
	private String tlocLocAddress;
	private String tlocLocGps;
	private Date tlocDtLoc;
	private String tlocRemarks;
	private String tlocCargoRec;
	private String tlocMobileNo;
	private String tlocSpecialInstn;
	private String tlocComment;
	private Date tlocDtStart;
	private Date tlocDtReach;
	private Date tlocDtEnd;
	private Character tlocIsDeviated;
	private String tlocDeviationComment;
	private Character tlocStatus;
	private String tlocName;
	private Date tlocDtCreate;
	private String tlocUidCreate;
	private Date tlocDtLupd;
	private String tlocUidLupd;

	// Constructors
	///////////////
	public CkCtTripLocation() {
	}

	public CkCtTripLocation(TCkCtTripLocation entity) {
		super(entity);
	}

	/**
	 * @param tlocId
	 * @param tCkCtLocation
	 * @param tlocLocName
	 * @param tlocLocAddress
	 * @param tlocLocGps
	 * @param tlocDtLoc
	 * @param tlocRemarks
	 * @param tlocMobileNo
	 * @param tlocStatus
	 * @param tlocDtCreate
	 * @param tlocUidCreate
	 * @param tlocDtLupd
	 * @param tlocUidLupd
	 */
	public CkCtTripLocation(String tlocId, CkCtLocation tCkCtLocation, String tlocLocName, String tlocLocAddress,
			String tlocLocGps, Date tlocDtLoc, String tlocRemarks, String tlocCargoRec, String tlocMobileNo, Character tlocStatus,
			String tlocName, Date tlocDtCreate, String tlocUidCreate, Date tlocDtLupd, String tlocUidLupd) {
		super();
		this.tlocId = tlocId;
		TCkCtLocation = tCkCtLocation;
		this.tlocLocName = tlocLocName;
		this.tlocLocAddress = tlocLocAddress;
		this.tlocLocGps = tlocLocGps;
		this.tlocDtLoc = tlocDtLoc;
		this.tlocRemarks = tlocRemarks;
		this.tlocCargoRec = tlocCargoRec;
		this.tlocMobileNo = tlocMobileNo;
		this.tlocStatus = tlocStatus;
		this.tlocName = tlocName;
		this.tlocDtCreate = tlocDtCreate;
		this.tlocUidCreate = tlocUidCreate;
		this.tlocDtLupd = tlocDtLupd;
		this.tlocUidLupd = tlocUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the tlocId
	 */
	public String getTlocId() {
		return tlocId;
	}

	/**
	 * @param tlocId the tlocId to set
	 */
	public void setTlocId(String tlocId) {
		this.tlocId = tlocId;
	}

	/**
	 * @return the tCkCtLocation
	 */
	public CkCtLocation getTCkCtLocation() {
		return TCkCtLocation;
	}

	/**
	 * @param tCkCtLocation the tCkCtLocation to set
	 */
	public void setTCkCtLocation(CkCtLocation tCkCtLocation) {
		TCkCtLocation = tCkCtLocation;
	}

	/**
	 * @return the tlocLocName
	 */
	public String getTlocLocName() {
		return tlocLocName;
	}

	/**
	 * @param tlocLocName the tlocLocName to set
	 */
	public void setTlocLocName(String tlocLocName) {
		this.tlocLocName = tlocLocName;
	}

	/**
	 * @return the tlocLocAddress
	 */
	public String getTlocLocAddress() {
		return tlocLocAddress;
	}

	/**
	 * @param tlocLocAddress the tlocLocAddress to set
	 */
	public void setTlocLocAddress(String tlocLocAddress) {
		this.tlocLocAddress = tlocLocAddress;
	}

	/**
	 * @return the tlocLocGps
	 */
	public String getTlocLocGps() {
		return tlocLocGps;
	}

	/**
	 * @param tlocLocGps the tlocLocGps to set
	 */
	public void setTlocLocGps(String tlocLocGps) {
		this.tlocLocGps = tlocLocGps;
	}

	/**
	 * @return the tlocDtLoc
	 */
	public Date getTlocDtLoc() {
		return tlocDtLoc;
	}

	/**
	 * @param tlocDtLoc the tlocDtLoc to set
	 */
	public void setTlocDtLoc(Date tlocDtLoc) {
		this.tlocDtLoc = tlocDtLoc;
	}

	/**
	 * @return the tlocRemarks
	 */
	public String getTlocRemarks() {
		return tlocRemarks;
	}

	/**
	 * @param tlocRemarks the tlocRemarks to set
	 */
	public void setTlocRemarks(String tlocRemarks) {
		this.tlocRemarks = tlocRemarks;
	}

	/**
	 * @return the tlocMobileNo
	 */
	public String getTlocMobileNo() {
		return tlocMobileNo;
	}

	/**
	 * @param tlocMobileNo the tlocMobileNo to set
	 */
	public void setTlocMobileNo(String tlocMobileNo) {
		this.tlocMobileNo = tlocMobileNo;
	}

	/**
	 * @return the tlocStatus
	 */
	public Character getTlocStatus() {
		return tlocStatus;
	}

	/**
	 * @param tlocStatus the tlocStatus to set
	 */
	public void setTlocStatus(Character tlocStatus) {
		this.tlocStatus = tlocStatus;
	}
	
	public String getTlocName() {
		return tlocName;
	}

	public void setTlocName(String tlocName) {
		this.tlocName = tlocName;
	}

	/**
	 * @return the tlocDtCreate
	 */
	public Date getTlocDtCreate() {
		return tlocDtCreate;
	}

	/**
	 * @param tlocDtCreate the tlocDtCreate to set
	 */
	public void setTlocDtCreate(Date tlocDtCreate) {
		this.tlocDtCreate = tlocDtCreate;
	}

	/**
	 * @return the tlocUidCreate
	 */
	public String getTlocUidCreate() {
		return tlocUidCreate;
	}

	/**
	 * @param tlocUidCreate the tlocUidCreate to set
	 */
	public void setTlocUidCreate(String tlocUidCreate) {
		this.tlocUidCreate = tlocUidCreate;
	}

	/**
	 * @return the tlocDtLupd
	 */
	public Date getTlocDtLupd() {
		return tlocDtLupd;
	}

	/**
	 * @param tlocDtLupd the tlocDtLupd to set
	 */
	public void setTlocDtLupd(Date tlocDtLupd) {
		this.tlocDtLupd = tlocDtLupd;
	}

	/**
	 * @return the tlocUidLupd
	 */
	public String getTlocUidLupd() {
		return tlocUidLupd;
	}

	/**
	 * @param tlocUidLupd the tlocUidLupd to set
	 */
	public void setTlocUidLupd(String tlocUidLupd) {
		this.tlocUidLupd = tlocUidLupd;
	}

	// Override Methods
	///////////////////
	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 */
	@Override
	public int compareTo(CkCtTripLocation o) {
		// TODO Auto-generated method stub
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

	/**
	 * @return the tlocSpecialInstn
	 */
	public String getTlocSpecialInstn() {
		return tlocSpecialInstn;
	}

	/**
	 * @param tlocSpecialInstn the tlocSpecialInstn to set
	 */
	public void setTlocSpecialInstn(String tlocSpecialInstn) {
		this.tlocSpecialInstn = tlocSpecialInstn;
	}

	/**
	 * @return the tlocDtStart
	 */
	public Date getTlocDtStart() {
		return tlocDtStart;
	}

	/**
	 * @param tlocDtStart the tlocDtStart to set
	 */
	public void setTlocDtStart(Date tlocDtStart) {
		this.tlocDtStart = tlocDtStart;
	}

	/**
	 * @return the tlocDtReach
	 */
	public Date getTlocDtReach() {
		return tlocDtReach;
	}

	/**
	 * @param tlocDtReach the tlocDtReach to set
	 */
	public void setTlocDtReach(Date tlocDtReach) {
		this.tlocDtReach = tlocDtReach;
	}

	/**
	 * @return the tlocDtEnd
	 */
	public Date getTlocDtEnd() {
		return tlocDtEnd;
	}

	/**
	 * @param tlocDtEnd the tlocDtEnd to set
	 */
	public void setTlocDtEnd(Date tlocDtEnd) {
		this.tlocDtEnd = tlocDtEnd;
	}

	/**
	 * @return the tlocComment
	 */
	public String getTlocComment() {
		return tlocComment;
	}

	/**
	 * @param tlocComment the tlocComment to set
	 */
	public void setTlocComment(String tlocComment) {
		this.tlocComment = tlocComment;
	}

	/**
	 * @return the tlocIsDeviated
	 */
	public Character getTlocIsDeviated() {
		return tlocIsDeviated;
	}

	/**
	 * @param tlocIsDeviated the tlocIsDeviated to set
	 */
	public void setTlocIsDeviated(Character tlocIsDeviated) {
		this.tlocIsDeviated = tlocIsDeviated;
	}

	/**
	 * @return the tlocDeviationComment
	 */
	public String getTlocDeviationComment() {
		return tlocDeviationComment;
	}

	/**
	 * @param tlocDeviationComment the tlocDeviationComment to set
	 */
	public void setTlocDeviationComment(String tlocDeviationComment) {
		this.tlocDeviationComment = tlocDeviationComment;
	}

	public String getTlocCargoRec() {
		return tlocCargoRec;
	}

	public void setTlocCargoRec(String tlocCargoRec) {
		this.tlocCargoRec = tlocCargoRec;
	}
}
