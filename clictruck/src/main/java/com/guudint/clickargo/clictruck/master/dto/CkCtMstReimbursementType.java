package com.guudint.clickargo.clictruck.master.dto;
// Generated 23 Feb, 2023 4:00:19 PM by Hibernate Tools 5.2.1.Final

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstReimbursementType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstReimbursementType extends AbstractDTO<CkCtMstReimbursementType, TCkCtMstReimbursementType> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -3614733653209625966L;
	
	// Attributes
	/////////////
	private String rbtypId;
	private String rbtypName;
	private String rbtypDesc;
	private String rbtypDescOth;
	private Character rbtypStatus;
	private Date rbtypDtCreate;
	private String rbtypUidCreate;
	private Date rbtypDtLupd;
	private String rbtypUidLupd;
	
	// Constructors
	///////////////
	public CkCtMstReimbursementType() {
	}

	public CkCtMstReimbursementType(TCkCtMstReimbursementType entity) {
		super(entity);
	}

	/**
	 * @param rbtypId
	 * @param rbtypName
	 * @param rbtypDesc
	 * @param rbtypDescOth
	 * @param rbtypStatus
	 * @param rbtypDtCreate
	 * @param rbtypUidCreate
	 * @param rbtypDtLupd
	 * @param rbtypUidLupd
	 */
	public CkCtMstReimbursementType(String rbtypId, String rbtypName, String rbtypDesc, String rbtypDescOth,
			Character rbtypStatus, Date rbtypDtCreate, String rbtypUidCreate, Date rbtypDtLupd, String rbtypUidLupd) {
		super();
		this.rbtypId = rbtypId;
		this.rbtypName = rbtypName;
		this.rbtypDesc = rbtypDesc;
		this.rbtypDescOth = rbtypDescOth;
		this.rbtypStatus = rbtypStatus;
		this.rbtypDtCreate = rbtypDtCreate;
		this.rbtypUidCreate = rbtypUidCreate;
		this.rbtypDtLupd = rbtypDtLupd;
		this.rbtypUidLupd = rbtypUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the rbtypId
	 */
	public String getRbtypId() {
		return rbtypId;
	}

	/**
	 * @param rbtypId the rbtypId to set
	 */
	public void setRbtypId(String rbtypId) {
		this.rbtypId = rbtypId;
	}

	/**
	 * @return the rbtypName
	 */
	public String getRbtypName() {
		return rbtypName;
	}

	/**
	 * @param rbtypName the rbtypName to set
	 */
	public void setRbtypName(String rbtypName) {
		this.rbtypName = rbtypName;
	}

	/**
	 * @return the rbtypDesc
	 */
	public String getRbtypDesc() {
		return rbtypDesc;
	}

	/**
	 * @param rbtypDesc the rbtypDesc to set
	 */
	public void setRbtypDesc(String rbtypDesc) {
		this.rbtypDesc = rbtypDesc;
	}

	/**
	 * @return the rbtypDescOth
	 */
	public String getRbtypDescOth() {
		return rbtypDescOth;
	}

	/**
	 * @param rbtypDescOth the rbtypDescOth to set
	 */
	public void setRbtypDescOth(String rbtypDescOth) {
		this.rbtypDescOth = rbtypDescOth;
	}

	/**
	 * @return the rbtypStatus
	 */
	public Character getRbtypStatus() {
		return rbtypStatus;
	}

	/**
	 * @param rbtypStatus the rbtypStatus to set
	 */
	public void setRbtypStatus(Character rbtypStatus) {
		this.rbtypStatus = rbtypStatus;
	}

	/**
	 * @return the rbtypDtCreate
	 */
	public Date getRbtypDtCreate() {
		return rbtypDtCreate;
	}

	/**
	 * @param rbtypDtCreate the rbtypDtCreate to set
	 */
	public void setRbtypDtCreate(Date rbtypDtCreate) {
		this.rbtypDtCreate = rbtypDtCreate;
	}

	/**
	 * @return the rbtypUidCreate
	 */
	public String getRbtypUidCreate() {
		return rbtypUidCreate;
	}

	/**
	 * @param rbtypUidCreate the rbtypUidCreate to set
	 */
	public void setRbtypUidCreate(String rbtypUidCreate) {
		this.rbtypUidCreate = rbtypUidCreate;
	}

	/**
	 * @return the rbtypDtLupd
	 */
	public Date getRbtypDtLupd() {
		return rbtypDtLupd;
	}

	/**
	 * @param rbtypDtLupd the rbtypDtLupd to set
	 */
	public void setRbtypDtLupd(Date rbtypDtLupd) {
		this.rbtypDtLupd = rbtypDtLupd;
	}

	/**
	 * @return the rbtypUidLupd
	 */
	public String getRbtypUidLupd() {
		return rbtypUidLupd;
	}

	/**
	 * @param rbtypUidLupd the rbtypUidLupd to set
	 */
	public void setRbtypUidLupd(String rbtypUidLupd) {
		this.rbtypUidLupd = rbtypUidLupd;
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
	public int compareTo(CkCtMstReimbursementType o) {
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
