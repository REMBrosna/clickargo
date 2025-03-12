package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstCargoType;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoFm;
import com.guudint.clickargo.master.dto.CkMstCntType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripCargoFm extends AbstractDTO<CkCtTripCargoFm, TCkCtTripCargoFm> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 6123109566262338112L;

	// Attributes
	/////////////
	private String cgId;
	private CkCtMstCargoType TCkCtMstCargoType;
	private CkCtTrip TCkCtTrip;
	private CkMstCntType TCkMstCntType;
	private String cgCntNo;
	private String cgCntSealNo;
	private Character cgCntFullLoad;
	private String cgCntFullLoadStr;
	private String cgCargoDesc;
	private String cgCargoSpecialInstn;
	private Character cgStatus;
	private Date cgDtCreate;
	private String cgUidCreate;
	private Date cgDtLupd;
	private String cgUidLupd;
	
	// Constructors
	///////////////
	public CkCtTripCargoFm() {
	}
	
	public CkCtTripCargoFm(TCkCtTripCargoFm entity) {
		super(entity);
	}

	/**
	 * @param cgId
	 * @param tCkCtMstCargoType
	 * @param tCkCtTrip
	 * @param tCkMstCntType
	 * @param cgCntNo
	 * @param cgCntSealNo
	 * @param cgCntFullLoad
	 * @param cgCargoDesc
	 * @param cgCargoSpecialInstn
	 * @param cgStatus
	 * @param cgDtCreate
	 * @param cgUidCreate
	 * @param cgDtLupd
	 * @param cgUidLupd
	 */
	public CkCtTripCargoFm(String cgId, CkCtMstCargoType tCkCtMstCargoType, CkCtTrip tCkCtTrip,
			CkMstCntType tCkMstCntType, String cgCntNo, String cgCntSealNo, Character cgCntFullLoad, String cgCargoDesc,
			String cgCargoSpecialInstn, Character cgStatus, Date cgDtCreate, String cgUidCreate, Date cgDtLupd,
			String cgUidLupd) {
		super();
		this.cgId = cgId;
		TCkCtMstCargoType = tCkCtMstCargoType;
		TCkCtTrip = tCkCtTrip;
		TCkMstCntType = tCkMstCntType;
		this.cgCntNo = cgCntNo;
		this.cgCntSealNo = cgCntSealNo;
		this.cgCntFullLoad = cgCntFullLoad;
		this.cgCargoDesc = cgCargoDesc;
		this.cgCargoSpecialInstn = cgCargoSpecialInstn;
		this.cgStatus = cgStatus;
		this.cgDtCreate = cgDtCreate;
		this.cgUidCreate = cgUidCreate;
		this.cgDtLupd = cgDtLupd;
		this.cgUidLupd = cgUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the cgId
	 */
	public String getCgId() {
		return cgId;
	}

	/**
	 * @param cgId the cgId to set
	 */
	public void setCgId(String cgId) {
		this.cgId = cgId;
	}

	/**
	 * @return the tCkCtMstCargoType
	 */
	public CkCtMstCargoType getTCkCtMstCargoType() {
		return TCkCtMstCargoType;
	}

	/**
	 * @param tCkCtMstCargoType the tCkCtMstCargoType to set
	 */
	public void setTCkCtMstCargoType(CkCtMstCargoType tCkCtMstCargoType) {
		TCkCtMstCargoType = tCkCtMstCargoType;
	}

	/**
	 * @return the tCkCtTrip
	 */
	public CkCtTrip getTCkCtTrip() {
		return TCkCtTrip;
	}

	/**
	 * @param tCkCtTrip the tCkCtTrip to set
	 */
	public void setTCkCtTrip(CkCtTrip tCkCtTrip) {
		TCkCtTrip = tCkCtTrip;
	}

	/**
	 * @return the tCkMstCntType
	 */
	public CkMstCntType getTCkMstCntType() {
		return TCkMstCntType;
	}

	/**
	 * @param tCkMstCntType the tCkMstCntType to set
	 */
	public void setTCkMstCntType(CkMstCntType tCkMstCntType) {
		TCkMstCntType = tCkMstCntType;
	}

	/**
	 * @return the cgCntNo
	 */
	public String getCgCntNo() {
		return cgCntNo;
	}

	/**
	 * @param cgCntNo the cgCntNo to set
	 */
	public void setCgCntNo(String cgCntNo) {
		this.cgCntNo = cgCntNo;
	}

	/**
	 * @return the cgCntSealNo
	 */
	public String getCgCntSealNo() {
		return cgCntSealNo;
	}

	/**
	 * @param cgCntSealNo the cgCntSealNo to set
	 */
	public void setCgCntSealNo(String cgCntSealNo) {
		this.cgCntSealNo = cgCntSealNo;
	}

	/**
	 * @return the cgCntFullLoad
	 */
	public Character getCgCntFullLoad() {
		return cgCntFullLoad;
	}

	/**
	 * @param cgCntFullLoad the cgCntFullLoad to set
	 */
	public void setCgCntFullLoad(Character cgCntFullLoad) {
		this.cgCntFullLoad = cgCntFullLoad;
	}

	/**
	 * @return the cgCargoDesc
	 */
	public String getCgCargoDesc() {
		return cgCargoDesc;
	}

	/**
	 * @param cgCargoDesc the cgCargoDesc to set
	 */
	public void setCgCargoDesc(String cgCargoDesc) {
		this.cgCargoDesc = cgCargoDesc;
	}

	/**
	 * @return the cgCargoSpecialInstn
	 */
	public String getCgCargoSpecialInstn() {
		return cgCargoSpecialInstn;
	}

	/**
	 * @param cgCargoSpecialInstn the cgCargoSpecialInstn to set
	 */
	public void setCgCargoSpecialInstn(String cgCargoSpecialInstn) {
		this.cgCargoSpecialInstn = cgCargoSpecialInstn;
	}

	/**
	 * @return the cgStatus
	 */
	public Character getCgStatus() {
		return cgStatus;
	}

	/**
	 * @param cgStatus the cgStatus to set
	 */
	public void setCgStatus(Character cgStatus) {
		this.cgStatus = cgStatus;
	}

	/**
	 * @return the cgDtCreate
	 */
	public Date getCgDtCreate() {
		return cgDtCreate;
	}

	/**
	 * @param cgDtCreate the cgDtCreate to set
	 */
	public void setCgDtCreate(Date cgDtCreate) {
		this.cgDtCreate = cgDtCreate;
	}

	/**
	 * @return the cgUidCreate
	 */
	public String getCgUidCreate() {
		return cgUidCreate;
	}

	/**
	 * @param cgUidCreate the cgUidCreate to set
	 */
	public void setCgUidCreate(String cgUidCreate) {
		this.cgUidCreate = cgUidCreate;
	}

	/**
	 * @return the cgDtLupd
	 */
	public Date getCgDtLupd() {
		return cgDtLupd;
	}

	/**
	 * @param cgDtLupd the cgDtLupd to set
	 */
	public void setCgDtLupd(Date cgDtLupd) {
		this.cgDtLupd = cgDtLupd;
	}

	/**
	 * @return the cgUidLupd
	 */
	public String getCgUidLupd() {
		return cgUidLupd;
	}

	/**
	 * @param cgUidLupd the cgUidLupd to set
	 */
	public void setCgUidLupd(String cgUidLupd) {
		this.cgUidLupd = cgUidLupd;
	}

	public String getCgCntFullLoadStr() {
		return cgCntFullLoadStr;
	}

	public void setCgCntFullLoadStr(String cgCntFullLoadStr) {
		this.cgCntFullLoadStr = cgCntFullLoadStr;
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
	public int compareTo(CkCtTripCargoFm o) {
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
	
}
