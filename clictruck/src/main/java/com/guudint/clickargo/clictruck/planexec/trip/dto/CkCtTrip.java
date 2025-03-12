package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTrip;
import com.vcc.camelone.common.dto.AbstractDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CkCtTrip extends AbstractDTO<CkCtTrip, TCkCtTrip> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -7838445706301250300L;

	// Attributes
	/////////////
	private String trId;
	private CkCtTripCharge TCkCtTripCharge;
	private CkCtTripLocation TCkCtTripLocationByTrTo;
	private CkCtTripLocation TCkCtTripLocationByTrFrom;
	private CkCtTripLocation TCkCtTripLocationByTrDepot;
	private CkJobTruck TCkJobTruck;
	private String trFromAddr;
	private String trToAddr;
	private String trDepoAddr;
	private Character trChargeOpen;
	private Integer trSeq;
	
	private Character trStatus;
	private Date trDtCreate;
	private String trUidCreate;
	private Date trDtLupd;
	private String trUidLupd;

	private List<CkCtTripCargoFm> tckCtTripCargoFmList;
	private List<CkCtTripCargoMm> tripCargoMmList;
	private BigDecimal totalTripOpenPrice;
	private BigDecimal totalTripPrice;
	private BigDecimal totalReimbursementCharge;

	private CkCtTripDoAttach unsignedDo;
	private CkCtTripDoAttach signedDo;
	private CkCtTripDo tckCtTripDo;
	
	
	// Constructors
	///////////////
	public CkCtTrip() {
	}

	public CkCtTrip(TCkCtTrip entity) {
		super(entity);
	}

	/**
	 * 
	 * @param trId
	 * @param tCkCtTripCharge
	 * @param tCkCtTripLocationByTrTo
	 * @param tCkCtTripLocationByTrFrom
	 * @param tCkJobTruck
	 * @param tCkCtTripLocationByTrDepot
	 * @param trFromAddr
	 * @param trToAddr
	 * @param trDepoAddr
	 * @param trChargeOpen
	 * @param trStatus
	 * @param trDtCreate
	 * @param trUidCreate
	 * @param trDtLupd
	 * @param trUidLupd
	 */
	public CkCtTrip(String trId, CkCtTripCharge tCkCtTripCharge, CkCtTripLocation tCkCtTripLocationByTrTo,
			CkCtTripLocation tCkCtTripLocationByTrFrom, CkCtTripLocation tCkCtTripLocationByTrDepot,
			CkJobTruck tCkJobTruck, String trDepo, String trFromAddr, String trToAddr, String trDepoAddr,
			Character trChargeOpen, Character trStatus, Date trDtCreate, String trUidCreate, Date trDtLupd,
			String trUidLupd) {
		super();
		this.trId = trId;
		TCkCtTripCharge = tCkCtTripCharge;
		TCkCtTripLocationByTrTo = tCkCtTripLocationByTrTo;
		TCkCtTripLocationByTrFrom = tCkCtTripLocationByTrFrom;
		TCkCtTripLocationByTrDepot = tCkCtTripLocationByTrDepot;
		TCkJobTruck = tCkJobTruck;
		this.trFromAddr = trFromAddr;
		this.trToAddr = trToAddr;
		this.trDepoAddr = trDepoAddr;
		this.trChargeOpen = trChargeOpen;
		this.trStatus = trStatus;
		this.trDtCreate = trDtCreate;
		this.trUidCreate = trUidCreate;
		this.trDtLupd = trDtLupd;
		this.trUidLupd = trUidLupd;
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
	public int compareTo(CkCtTrip o) {
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
	
	// Properties
	/////////////
	/**
	 * @return the trId
	 */
	public String getTrId() {
		return trId;
	}

	/**
	 * @param trId the trId to set
	 */
	public void setTrId(String trId) {
		this.trId = trId;
	}

	/**
	 * @return the tCkCtTripCharge
	 */
	public CkCtTripCharge getTCkCtTripCharge() {
		return TCkCtTripCharge;
	}

	/**
	 * @param tCkCtTripCharge the tCkCtTripCharge to set
	 */
	public void setTCkCtTripCharge(CkCtTripCharge tCkCtTripCharge) {
		TCkCtTripCharge = tCkCtTripCharge;
	}

	/**
	 * @return the tCkCtTripLocationByTrTo
	 */
	public CkCtTripLocation getTCkCtTripLocationByTrTo() {
		return TCkCtTripLocationByTrTo;
	}

	/**
	 * @param tCkCtTripLocationByTrTo the tCkCtTripLocationByTrTo to set
	 */
	public void setTCkCtTripLocationByTrTo(CkCtTripLocation tCkCtTripLocationByTrTo) {
		TCkCtTripLocationByTrTo = tCkCtTripLocationByTrTo;
	}

	/**
	 * @return the tCkCtTripLocationByTrFrom
	 */
	public CkCtTripLocation getTCkCtTripLocationByTrFrom() {
		return TCkCtTripLocationByTrFrom;
	}

	/**
	 * @param tCkCtTripLocationByTrFrom the tCkCtTripLocationByTrFrom to set
	 */
	public void setTCkCtTripLocationByTrFrom(CkCtTripLocation tCkCtTripLocationByTrFrom) {
		TCkCtTripLocationByTrFrom = tCkCtTripLocationByTrFrom;
	}

	/**
	 * @return the tCkJobTruck
	 */
	public CkJobTruck getTCkJobTruck() {
		return TCkJobTruck;
	}

	/**
	 * @param tCkJobTruck the tCkJobTruck to set
	 */
	public void setTCkJobTruck(CkJobTruck tCkJobTruck) {
		TCkJobTruck = tCkJobTruck;
	}

	/**
	 * @return the tCkCtTripLocationByTrDepot
	 */
	public CkCtTripLocation getTCkCtTripLocationByTrDepot() {
		return TCkCtTripLocationByTrDepot;
	}

	/**
	 * @param tCkCtTripLocationByTrDepot the tCkCtTripLocationByTrDepot to set
	 */
	public void setTCkCtTripLocationByTrDepot(CkCtTripLocation tCkCtTripLocationByTrDepot) {
		TCkCtTripLocationByTrDepot = tCkCtTripLocationByTrDepot;
	}

	/**
	 * @return the trFromAddr
	 */
	public String getTrFromAddr() {
		return trFromAddr;
	}

	/**
	 * @param trFromAddr the trFromAddr to set
	 */
	public void setTrFromAddr(String trFromAddr) {
		this.trFromAddr = trFromAddr;
	}

	/**
	 * @return the trToAddr
	 */
	public String getTrToAddr() {
		return trToAddr;
	}

	/**
	 * @param trToAddr the trToAddr to set
	 */
	public void setTrToAddr(String trToAddr) {
		this.trToAddr = trToAddr;
	}

	/**
	 * @return the trDepoAddr
	 */
	public String getTrDepoAddr() {
		return trDepoAddr;
	}

	/**
	 * @param trDepoAddr the trDepoAddr to set
	 */
	public void setTrDepoAddr(String trDepoAddr) {
		this.trDepoAddr = trDepoAddr;
	}

	/**
	 * @return the trChargeOpen
	 */
	public Character getTrChargeOpen() {
		return trChargeOpen;
	}

	/**
	 * @param trChargeOpen the trChargeOpen to set
	 */
	public void setTrChargeOpen(Character trChargeOpen) {
		this.trChargeOpen = trChargeOpen;
	}

	/**
	 * @return the trStatus
	 */
	public Character getTrStatus() {
		return trStatus;
	}

	/**
	 * @param trStatus the trStatus to set
	 */
	public void setTrStatus(Character trStatus) {
		this.trStatus = trStatus;
	}

	/**
	 * @return the trDtCreate
	 */
	public Date getTrDtCreate() {
		return trDtCreate;
	}

	/**
	 * @param trDtCreate the trDtCreate to set
	 */
	public void setTrDtCreate(Date trDtCreate) {
		this.trDtCreate = trDtCreate;
	}

	/**
	 * @return the trUidCreate
	 */
	public String getTrUidCreate() {
		return trUidCreate;
	}

	/**
	 * @param trUidCreate the trUidCreate to set
	 */
	public void setTrUidCreate(String trUidCreate) {
		this.trUidCreate = trUidCreate;
	}

	/**
	 * @return the trDtLupd
	 */
	public Date getTrDtLupd() {
		return trDtLupd;
	}

	/**
	 * @param trDtLupd the trDtLupd to set
	 */
	public void setTrDtLupd(Date trDtLupd) {
		this.trDtLupd = trDtLupd;
	}

	/**
	 * @return the trUidLupd
	 */
	public String getTrUidLupd() {
		return trUidLupd;
	}

	/**
	 * @param trUidLupd the trUidLupd to set
	 */
	public void setTrUidLupd(String trUidLupd) {
		this.trUidLupd = trUidLupd;
	}

	/**
	 * @return the tckCtTripCargoFmList
	 */
	public List<CkCtTripCargoFm> getTckCtTripCargoFmList() {
		return tckCtTripCargoFmList;
	}

	/**
	 * @param tckCtTripCargoFmList the tckCtTripCargoFmList to set
	 */
	public void setTckCtTripCargoFmList(List<CkCtTripCargoFm> tckCtTripCargoFmList) {
		this.tckCtTripCargoFmList = tckCtTripCargoFmList;
	}

	/**
	 * @return the totalTripOpenPrice
	 */
	public BigDecimal getTotalTripOpenPrice() {
		return totalTripOpenPrice;
	}

	/**
	 * @param totalTripOpenPrice the totalTripOpenPrice to set
	 */
	public void setTotalTripOpenPrice(BigDecimal totalTripOpenPrice) {
		this.totalTripOpenPrice = totalTripOpenPrice;
	}

	/**
	 * @return the totalTripPrice
	 */
	public BigDecimal getTotalTripPrice() {
		return totalTripPrice;
	}

	/**
	 * @param totalTripPrice the totalTripPrice to set
	 */
	public void setTotalTripPrice(BigDecimal totalTripPrice) {
		this.totalTripPrice = totalTripPrice;
	}

	public List<CkCtTripCargoMm> getTripCargoMmList() {
		return tripCargoMmList;
	}

	public void setTripCargoMmList(List<CkCtTripCargoMm> tripCargoMmList) {
		this.tripCargoMmList = tripCargoMmList;
	}

	/**
	 * @return the unsignedDo
	 */
	public CkCtTripDoAttach getUnsignedDo() {
		return unsignedDo;
	}

	/**
	 * @param unsignedDo the unsignedDo to set
	 */
	public void setUnsignedDo(CkCtTripDoAttach unsignedDo) {
		this.unsignedDo = unsignedDo;
	}

	/**
	 * @return the signedDo
	 */
	public CkCtTripDoAttach getSignedDo() {
		return signedDo;
	}

	/**
	 * @param signedDo the signedDo to set
	 */
	public void setSignedDo(CkCtTripDoAttach signedDo) {
		this.signedDo = signedDo;
	}

	/**
	 * @return the tckCtTripDo
	 */
	public CkCtTripDo getTckCtTripDo() {
		return tckCtTripDo;
	}

	/**
	 * @param tckCtTripDo the tckCtTripDo to set
	 */
	public void setTckCtTripDo(CkCtTripDo tckCtTripDo) {
		this.tckCtTripDo = tckCtTripDo;
	}

	/**
	 * @return the totalReimbursementCharge
	 */
	public BigDecimal getTotalReimbursementCharge() {
		return totalReimbursementCharge;
	}

	/**
	 * @param totalReimbursementCharge the totalReimbursementCharge to set
	 */
	public void setTotalReimbursementCharge(BigDecimal totalReimbursementCharge) {
		this.totalReimbursementCharge = totalReimbursementCharge;
	}

	public Integer getTrSeq() {
		return trSeq;
	}

	public void setTrSeq(Integer trSeq) {
		this.trSeq = trSeq;
	}

}