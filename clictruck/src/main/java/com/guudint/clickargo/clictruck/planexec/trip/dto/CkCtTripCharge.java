package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripCharge;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripCharge extends AbstractDTO<CkCtTripCharge, TCkCtTripCharge> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 3876336283281338458L;

	// Attributes
	/////////////
	private String tcId;
	private char tcIsOpen;
	private BigDecimal tcPrice;
	private BigDecimal tcPlatformFee;
	private BigDecimal tcGovtTax;
	private BigDecimal tcWitholdTax;
	private Character tcStatus;
	private Date tcDtCreate;
	private String tcUidCreate;
	private Date tcDtLupd;
	private String tcUidLupd;
	
	// Constructors
	///////////////
	public CkCtTripCharge() {
	}

	public CkCtTripCharge(TCkCtTripCharge entity) {
		super(entity);
	}

	/**
	 * @param tcId
	 * @param tcIsOpen
	 * @param tcPrice
	 * @param tcPlatformFee
	 * @param tcGovtTax
	 * @param tcWitholdTax
	 * @param tcStatus
	 * @param tcDtCreate
	 * @param tcUidCreate
	 * @param tcDtLupd
	 * @param tcUidLupd
	 */
	public CkCtTripCharge(String tcId, char tcIsOpen, BigDecimal tcPrice, BigDecimal tcPlatformFee,
			BigDecimal tcGovtTax, BigDecimal tcWitholdTax, Character tcStatus, Date tcDtCreate, String tcUidCreate,
			Date tcDtLupd, String tcUidLupd) {
		super();
		this.tcId = tcId;
		this.tcIsOpen = tcIsOpen;
		this.tcPrice = tcPrice;
		this.tcPlatformFee = tcPlatformFee;
		this.tcGovtTax = tcGovtTax;
		this.tcWitholdTax = tcWitholdTax;
		this.tcStatus = tcStatus;
		this.tcDtCreate = tcDtCreate;
		this.tcUidCreate = tcUidCreate;
		this.tcDtLupd = tcDtLupd;
		this.tcUidLupd = tcUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the tcId
	 */
	public String getTcId() {
		return tcId;
	}

	/**
	 * @param tcId the tcId to set
	 */
	public void setTcId(String tcId) {
		this.tcId = tcId;
	}

	/**
	 * @return the tcIsOpen
	 */
	public char getTcIsOpen() {
		return tcIsOpen;
	}

	/**
	 * @param tcIsOpen the tcIsOpen to set
	 */
	public void setTcIsOpen(char tcIsOpen) {
		this.tcIsOpen = tcIsOpen;
	}

	/**
	 * @return the tcPrice
	 */
	public BigDecimal getTcPrice() {
		return tcPrice;
	}

	/**
	 * @param tcPrice the tcPrice to set
	 */
	public void setTcPrice(BigDecimal tcPrice) {
		this.tcPrice = tcPrice;
	}

	/**
	 * @return the tcPlatformFee
	 */
	public BigDecimal getTcPlatformFee() {
		return tcPlatformFee;
	}

	/**
	 * @param tcPlatformFee the tcPlatformFee to set
	 */
	public void setTcPlatformFee(BigDecimal tcPlatformFee) {
		this.tcPlatformFee = tcPlatformFee;
	}

	/**
	 * @return the tcGovtTax
	 */
	public BigDecimal getTcGovtTax() {
		return tcGovtTax;
	}

	/**
	 * @param tcGovtTax the tcGovtTax to set
	 */
	public void setTcGovtTax(BigDecimal tcGovtTax) {
		this.tcGovtTax = tcGovtTax;
	}

	/**
	 * @return the tcWitholdTax
	 */
	public BigDecimal getTcWitholdTax() {
		return tcWitholdTax;
	}

	/**
	 * @param tcWitholdTax the tcWitholdTax to set
	 */
	public void setTcWitholdTax(BigDecimal tcWitholdTax) {
		this.tcWitholdTax = tcWitholdTax;
	}

	/**
	 * @return the tcStatus
	 */
	public Character getTcStatus() {
		return tcStatus;
	}

	/**
	 * @param tcStatus the tcStatus to set
	 */
	public void setTcStatus(Character tcStatus) {
		this.tcStatus = tcStatus;
	}

	/**
	 * @return the tcDtCreate
	 */
	public Date getTcDtCreate() {
		return tcDtCreate;
	}

	/**
	 * @param tcDtCreate the tcDtCreate to set
	 */
	public void setTcDtCreate(Date tcDtCreate) {
		this.tcDtCreate = tcDtCreate;
	}

	/**
	 * @return the tcUidCreate
	 */
	public String getTcUidCreate() {
		return tcUidCreate;
	}

	/**
	 * @param tcUidCreate the tcUidCreate to set
	 */
	public void setTcUidCreate(String tcUidCreate) {
		this.tcUidCreate = tcUidCreate;
	}

	/**
	 * @return the tcDtLupd
	 */
	public Date getTcDtLupd() {
		return tcDtLupd;
	}

	/**
	 * @param tcDtLupd the tcDtLupd to set
	 */
	public void setTcDtLupd(Date tcDtLupd) {
		this.tcDtLupd = tcDtLupd;
	}

	/**
	 * @return the tcUidLupd
	 */
	public String getTcUidLupd() {
		return tcUidLupd;
	}

	/**
	 * @param tcUidLupd the tcUidLupd to set
	 */
	public void setTcUidLupd(String tcUidLupd) {
		this.tcUidLupd = tcUidLupd;
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
	public int compareTo(CkCtTripCharge o) {
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
