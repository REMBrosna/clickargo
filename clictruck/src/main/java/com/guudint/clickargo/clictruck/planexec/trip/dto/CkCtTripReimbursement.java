package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstReimbursementType;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripReimbursement;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripReimbursement extends AbstractDTO<CkCtTripReimbursement, TCkCtTripReimbursement> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -5336373614028185436L;

	// Attributes
	/////////////
	private String trId;
	private CkCtMstReimbursementType TCkCtMstReimbursementType;
	private CkCtTrip TCkCtTrip;
	private String trReceiptName;
	private String trReceiptLoc;
	private String trRemarks;
	private BigDecimal trPrice;
	private BigDecimal trTax;
	private BigDecimal trTotal;
	private Character trStatus;
	private Date trDtCreate;
	private String trUidCreate;
	private Date trDtLupd;
	private String trUidLupd;
	private String base64File;

	// Constructors
	///////////////
	public CkCtTripReimbursement() {
		super();
	}

	public CkCtTripReimbursement(TCkCtTripReimbursement entity) {
		super(entity);
	}

	/**
	 * @param trId
	 * @param tCkCtMstReimbursementType
	 * @param tCkCtTrip
	 * @param trReceiptName
	 * @param trReceiptLoc
	 * @param trRemarks
	 * @param trPrice
	 * @param trTax
	 * @param trTotal
	 * @param trStatus
	 * @param trDtCreate
	 * @param trUidCreate
	 * @param trDtLupd
	 * @param trUidLupd
	 */
	public CkCtTripReimbursement(String trId, CkCtMstReimbursementType tCkCtMstReimbursementType, CkCtTrip tCkCtTrip,
			String trReceiptName, String trReceiptLoc, String trRemarks, BigDecimal trPrice, BigDecimal trTax,
			BigDecimal trTotal, Character trStatus, Date trDtCreate, String trUidCreate, Date trDtLupd,
			String trUidLupd) {
		super();
		this.trId = trId;
		TCkCtMstReimbursementType = tCkCtMstReimbursementType;
		TCkCtTrip = tCkCtTrip;
		this.trReceiptName = trReceiptName;
		this.trReceiptLoc = trReceiptLoc;
		this.trRemarks = trRemarks;
		this.trPrice = trPrice;
		this.trTax = trTax;
		this.trTotal = trTotal;
		this.trStatus = trStatus;
		this.trDtCreate = trDtCreate;
		this.trUidCreate = trUidCreate;
		this.trDtLupd = trDtLupd;
		this.trUidLupd = trUidLupd;
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
	 * @return the tCkCtMstReimbursementType
	 */
	public CkCtMstReimbursementType getTCkCtMstReimbursementType() {
		return TCkCtMstReimbursementType;
	}

	/**
	 * @param tCkCtMstReimbursementType the tCkCtMstReimbursementType to set
	 */
	public void setTCkCtMstReimbursementType(CkCtMstReimbursementType tCkCtMstReimbursementType) {
		TCkCtMstReimbursementType = tCkCtMstReimbursementType;
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
	 * @return the trReceiptName
	 */
	public String getTrReceiptName() {
		return trReceiptName;
	}

	/**
	 * @param trReceiptName the trReceiptName to set
	 */
	public void setTrReceiptName(String trReceiptName) {
		this.trReceiptName = trReceiptName;
	}

	/**
	 * @return the trReceiptLoc
	 */
	public String getTrReceiptLoc() {
		return trReceiptLoc;
	}

	/**
	 * @param trReceiptLoc the trReceiptLoc to set
	 */
	public void setTrReceiptLoc(String trReceiptLoc) {
		this.trReceiptLoc = trReceiptLoc;
	}

	/**
	 * @return the trRemarks
	 */
	public String getTrRemarks() {
		return trRemarks;
	}

	/**
	 * @param trRemarks the trRemarks to set
	 */
	public void setTrRemarks(String trRemarks) {
		this.trRemarks = trRemarks;
	}

	/**
	 * @return the trPrice
	 */
	public BigDecimal getTrPrice() {
		return trPrice;
	}

	/**
	 * @param trPrice the trPrice to set
	 */
	public void setTrPrice(BigDecimal trPrice) {
		this.trPrice = trPrice;
	}

	/**
	 * @return the trTax
	 */
	public BigDecimal getTrTax() {
		return trTax;
	}

	/**
	 * @param trTax the trTax to set
	 */
	public void setTrTax(BigDecimal trTax) {
		this.trTax = trTax;
	}

	/**
	 * @return the trTotal
	 */
	public BigDecimal getTrTotal() {
		return trTotal;
	}

	/**
	 * @param trTotal the trTotal to set
	 */
	public void setTrTotal(BigDecimal trTotal) {
		this.trTotal = trTotal;
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

	public String getBase64File() {
		return base64File;
	}

	public void setBase64File(String base64File) {
		this.base64File = base64File;
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
	public int compareTo(CkCtTripReimbursement o) {
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
