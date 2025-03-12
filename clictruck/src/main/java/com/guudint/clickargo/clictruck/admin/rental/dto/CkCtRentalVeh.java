package com.guudint.clickargo.clictruck.admin.rental.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.guudint.clickargo.clictruck.admin.rental.model.TCkCtRentalVeh;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstRentalType;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstVehType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtRentalVeh extends AbstractDTO<CkCtRentalVeh, TCkCtRentalVeh> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -7526399395260451239L;

	// Attributes
	/////////////
	private String rvId;
	private CkCtMstRentalType ckCtMstRentalType;
	private CkCtMstVehType ckCtMstVehType;
	private CkCtRentalTable ckCtRentalTable;
	private Short rvNumVeh;
	private BigDecimal rvRentalAmt;
	private Character rvStatus;
	private Date rvDtCreate;
	private String rvUidCreate;
	private Date rvDtLupd;
	private String rrUidLupd;
	
	// Constructors
	///////////////
	public CkCtRentalVeh() {
	}

	/**
	 * @param entity
	 */
	public CkCtRentalVeh(TCkCtRentalVeh entity) {
		super(entity);
	}
	
	/**
	 * @param rvId
	 * @param ckCtMstRentalType
	 * @param ckCtMstVehType
	 * @param ckCtRentalTable
	 * @param rvNumVeh
	 * @param rvRentalAmt
	 * @param rvStatus
	 * @param rvDtCreate
	 * @param rvUidCreate
	 * @param rvDtLupd
	 * @param rrUidLupd
	 */
	public CkCtRentalVeh(String rvId, CkCtMstRentalType ckCtMstRentalType, CkCtMstVehType ckCtMstVehType,
			CkCtRentalTable ckCtRentalTable, Short rvNumVeh, BigDecimal rvRentalAmt, Character rvStatus,
			Date rvDtCreate, String rvUidCreate, Date rvDtLupd, String rrUidLupd) {
		super();
		this.rvId = rvId;
		this.ckCtMstRentalType = ckCtMstRentalType;
		this.ckCtMstVehType = ckCtMstVehType;
		this.ckCtRentalTable = ckCtRentalTable;
		this.rvNumVeh = rvNumVeh;
		this.rvRentalAmt = rvRentalAmt;
		this.rvStatus = rvStatus;
		this.rvDtCreate = rvDtCreate;
		this.rvUidCreate = rvUidCreate;
		this.rvDtLupd = rvDtLupd;
		this.rrUidLupd = rrUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the rvId
	 */
	public String getRvId() {
		return rvId;
	}

	/**
	 * @param rvId the rvId to set
	 */
	public void setRvId(String rvId) {
		this.rvId = rvId;
	}

	/**
	 * @return the ckCtMstRentalType
	 */
	public CkCtMstRentalType getCkCtMstRentalType() {
		return ckCtMstRentalType;
	}

	/**
	 * @param ckCtMstRentalType the ckCtMstRentalType to set
	 */
	public void setCkCtMstRentalType(CkCtMstRentalType ckCtMstRentalType) {
		this.ckCtMstRentalType = ckCtMstRentalType;
	}

	/**
	 * @return the ckCtMstVehType
	 */
	public CkCtMstVehType getCkCtMstVehType() {
		return ckCtMstVehType;
	}

	/**
	 * @param ckCtMstVehType the ckCtMstVehType to set
	 */
	public void setCkCtMstVehType(CkCtMstVehType ckCtMstVehType) {
		this.ckCtMstVehType = ckCtMstVehType;
	}

	/**
	 * @return the ckCtRentalTable
	 */
	public CkCtRentalTable getCkCtRentalTable() {
		return ckCtRentalTable;
	}

	/**
	 * @param ckCtRentalTable the ckCtRentalTable to set
	 */
	public void setCkCtRentalTable(CkCtRentalTable ckCtRentalTable) {
		this.ckCtRentalTable = ckCtRentalTable;
	}

	/**
	 * @return the rvNumVeh
	 */
	public Short getRvNumVeh() {
		return rvNumVeh;
	}

	/**
	 * @param rvNumVeh the rvNumVeh to set
	 */
	public void setRvNumVeh(Short rvNumVeh) {
		this.rvNumVeh = rvNumVeh;
	}

	/**
	 * @return the rvRentalAmt
	 */
	public BigDecimal getRvRentalAmt() {
		return rvRentalAmt;
	}

	/**
	 * @param rvRentalAmt the rvRentalAmt to set
	 */
	public void setRvRentalAmt(BigDecimal rvRentalAmt) {
		this.rvRentalAmt = rvRentalAmt;
	}

	/**
	 * @return the rvStatus
	 */
	public Character getRvStatus() {
		return rvStatus;
	}

	/**
	 * @param rvStatus the rvStatus to set
	 */
	public void setRvStatus(Character rvStatus) {
		this.rvStatus = rvStatus;
	}

	/**
	 * @return the rvDtCreate
	 */
	public Date getRvDtCreate() {
		return rvDtCreate;
	}

	/**
	 * @param rvDtCreate the rvDtCreate to set
	 */
	public void setRvDtCreate(Date rvDtCreate) {
		this.rvDtCreate = rvDtCreate;
	}

	/**
	 * @return the rvUidCreate
	 */
	public String getRvUidCreate() {
		return rvUidCreate;
	}

	/**
	 * @param rvUidCreate the rvUidCreate to set
	 */
	public void setRvUidCreate(String rvUidCreate) {
		this.rvUidCreate = rvUidCreate;
	}

	/**
	 * @return the rvDtLupd
	 */
	public Date getRvDtLupd() {
		return rvDtLupd;
	}

	/**
	 * @param rvDtLupd the rvDtLupd to set
	 */
	public void setRvDtLupd(Date rvDtLupd) {
		this.rvDtLupd = rvDtLupd;
	}

	/**
	 * @return the rrUidLupd
	 */
	public String getRrUidLupd() {
		return rrUidLupd;
	}

	/**
	 * @param rrUidLupd the rrUidLupd to set
	 */
	public void setRrUidLupd(String rrUidLupd) {
		this.rrUidLupd = rrUidLupd;
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
	public int compareTo(CkCtRentalVeh o) {
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
