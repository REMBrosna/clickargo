package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstCargoType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstUomSize;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstUomVolume;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstUomWeight;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCargoMm;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripCargoMm extends AbstractDTO<CkCtTripCargoMm, TCkCtTripCargoMm> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1054412083953437063L;
	
	// Attributes
	/////////////
	private String cgId;
	private CkCtMstCargoType TCkCtMstCargoType;
	private CkCtTrip TCkCtTrip;
	private String cgCargoDesc;
	private String cgCargoSpecialInstn;
	private Double cgCargoQty;
	private String cgCargoMarksNo;
	private Double cgCargoLength;
	private Double cgCargoWidth;
	private Double cgCargoHeight;
	private Double cgCargoWeight;
	private Double cgCargoVolume;
	private Character cgStatus;
	private Character cgPickupStatus;
	private Character cgDropOffStatus;
	private Date cgDtCreate;
	private String cgUidCreate;
	private Date cgDtLupd;
	private String cgUidLupd;

	private CkCtMstUomSize TCkCtMstUomSize;
	private CkCtMstUomVolume TCkCtMstUomVolume;
	private CkCtMstUomWeight TCkCtMstUomWeight;
	private String cgCargoQtyUom;

	// Constructors
	///////////////
	public CkCtTripCargoMm() {
	}

	public CkCtTripCargoMm(TCkCtTripCargoMm entity) {
		super(entity);
	}

	/**
	 * @param cgId
	 * @param tCkCtMstCargoType
	 * @param tCkCtTrip
	 * @param cgCargoDesc
	 * @param cgCargoSpecialInstn
	 * @param cgCargoQty
	 * @param cgCargoMarksNo
	 * @param cgCargoLength
	 * @param cgCargoWidth
	 * @param cgCargoHeight
	 * @param cgCargoWeight
	 * @param cgCargoVolume
	 * @param cgStatus
	 * @param cgDtCreate
	 * @param cgUidCreate
	 * @param cgDtLupd
	 * @param cgUidLupd
	 */
	public CkCtTripCargoMm(String cgId, CkCtMstCargoType tCkCtMstCargoType, CkCtTrip tCkCtTrip, String cgCargoDesc,
			String cgCargoSpecialInstn, Double cgCargoQty, String cgCargoMarksNo, Double cgCargoLength,
						   Double cgCargoWidth, Double cgCargoHeight, Double cgCargoWeight, Double cgCargoVolume, Character cgStatus,
			Date cgDtCreate, String cgUidCreate, Date cgDtLupd, String cgUidLupd, Character cgPickupStatus, Character cgDropOffStatus) {
		super();
		this.cgId = cgId;
		TCkCtMstCargoType = tCkCtMstCargoType;
		TCkCtTrip = tCkCtTrip;
		this.cgCargoDesc = cgCargoDesc;
		this.cgCargoSpecialInstn = cgCargoSpecialInstn;
		this.cgCargoQty = cgCargoQty;
		this.cgCargoMarksNo = cgCargoMarksNo;
		this.cgCargoLength = cgCargoLength;
		this.cgCargoWidth = cgCargoWidth;
		this.cgCargoHeight = cgCargoHeight;
		this.cgCargoWeight = cgCargoWeight;
		this.cgCargoVolume = cgCargoVolume;
		this.cgStatus = cgStatus;
		this.cgPickupStatus = cgPickupStatus;
		this.cgDropOffStatus = cgDropOffStatus;
		this.cgDtCreate = cgDtCreate;
		this.cgUidCreate = cgUidCreate;
		this.cgDtLupd = cgDtLupd;
		this.cgUidLupd = cgUidLupd;
	}

	public CkCtTripCargoMm(CkCtMstUomSize TCkCtMstUomSize, CkCtMstUomVolume TCkCtMstUomVolume, CkCtMstUomWeight TCkCtMstUomWeight, String cgCargoQtyUom) {
		this.TCkCtMstUomSize = TCkCtMstUomSize;
		this.TCkCtMstUomVolume = TCkCtMstUomVolume;
		this.TCkCtMstUomWeight = TCkCtMstUomWeight;
		this.cgCargoQtyUom = cgCargoQtyUom;
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
	 * @return the cgCargoQty
	 */
	public Double getCgCargoQty() {
		return cgCargoQty;
	}

	/**
	 * @param cgCargoQty the cgCargoQty to set
	 */
	public void setCgCargoQty(Double cgCargoQty) {
		this.cgCargoQty = cgCargoQty;
	}

	/**
	 * @return the cgCargoMarksNo
	 */
	public String getCgCargoMarksNo() {
		return cgCargoMarksNo;
	}

	/**
	 * @param cgCargoMarksNo the cgCargoMarksNo to set
	 */
	public void setCgCargoMarksNo(String cgCargoMarksNo) {
		this.cgCargoMarksNo = cgCargoMarksNo;
	}

	/**
	 * @return the cgCargoLength
	 */
	public Double getCgCargoLength() {
		return cgCargoLength;
	}

	/**
	 * @param cgCargoLength the cgCargoLength to set
	 */
	public void setCgCargoLength(Double cgCargoLength) {
		this.cgCargoLength = cgCargoLength;
	}

	/**
	 * @return the cgCargoWidth
	 */
	public Double getCgCargoWidth() {
		return cgCargoWidth;
	}

	/**
	 * @param cgCargoWidth the cgCargoWidth to set
	 */
	public void setCgCargoWidth(Double cgCargoWidth) {
		this.cgCargoWidth = cgCargoWidth;
	}

	/**
	 * @return the cgCargoHeight
	 */
	public Double getCgCargoHeight() {
		return cgCargoHeight;
	}

	/**
	 * @param cgCargoHeight the cgCargoHeight to set
	 */
	public void setCgCargoHeight(Double cgCargoHeight) {
		this.cgCargoHeight = cgCargoHeight;
	}

	/**
	 * @return the cgCargoWeight
	 */
	public Double getCgCargoWeight() {
		return cgCargoWeight;
	}

	/**
	 * @param cgCargoWeight the cgCargoWeight to set
	 */
	public void setCgCargoWeight(Double cgCargoWeight) {
		this.cgCargoWeight = cgCargoWeight;
	}

	/**
	 * @return the cgCargoVolume
	 */
	public Double getCgCargoVolume() {
		return cgCargoVolume;
	}

	/**
	 * @param cgCargoVolume the cgCargoVolume to set
	 */
	public void setCgCargoVolume(Double cgCargoVolume) {
		this.cgCargoVolume = cgCargoVolume;
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

	public Character getCgPickupStatus() {
		return cgPickupStatus;
	}

	public void setCgPickupStatus(Character cgPickupStatus) {
		this.cgPickupStatus = cgPickupStatus;
	}

	public Character getCgDropOffStatus() {
		return cgDropOffStatus;
	}

	public void setCgDropOffStatus(Character cgDropOffStatus) {
		this.cgDropOffStatus = cgDropOffStatus;
	}

	public CkCtMstUomSize getTCkCtMstUomSize() {
		return TCkCtMstUomSize;
	}

	public void setTCkCtMstUomSize(CkCtMstUomSize TCkCtMstUomSize) {
		this.TCkCtMstUomSize = TCkCtMstUomSize;
	}

	public CkCtMstUomVolume getTCkCtMstUomVolume() {
		return TCkCtMstUomVolume;
	}

	public void setTCkCtMstUomVolume(CkCtMstUomVolume TCkCtMstUomVolume) {
		this.TCkCtMstUomVolume = TCkCtMstUomVolume;
	}

	public CkCtMstUomWeight getTCkCtMstUomWeight() {
		return TCkCtMstUomWeight;
	}

	public void setTCkCtMstUomWeight(CkCtMstUomWeight TCkCtMstUomWeight) {
		this.TCkCtMstUomWeight = TCkCtMstUomWeight;
	}

	public String getCgCargoQtyUom() {
		return cgCargoQtyUom;
	}

	public void setCgCargoQtyUom(String cgCargoQtyUom) {
		this.cgCargoQtyUom = cgCargoQtyUom;
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
	public int compareTo(CkCtTripCargoMm o) {
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
