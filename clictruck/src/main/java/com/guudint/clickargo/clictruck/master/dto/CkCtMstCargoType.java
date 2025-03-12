package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstCargoType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstCargoType extends AbstractDTO<CkCtMstCargoType, TCkCtMstCargoType> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -5101186786226068322L;

	// Attributes
	/////////////
	private String crtypId;
	private String crtypName;
	private String crtypDesc;
	private String crtypDescOth;
	private int crtypSeq;
	private Character crtypStatus;
	private Date crtypDtCreate;
	private String crtypUidCreate;
	private Date crtypDtLupd;
	private String crtypUidLupd;
	
	// Constructors
	///////////////
	public CkCtMstCargoType() {
	}

	public CkCtMstCargoType(TCkCtMstCargoType entity) {
		super(entity);
	}

	public CkCtMstCargoType(String crtypId, String crtypName) {
		this.crtypId = crtypId;
		this.crtypName = crtypName;
	}

	/**
	 * @param crtypId
	 * @param crtypName
	 * @param crtypDesc
	 * @param crtypDescOth
	 * @param crtypStatus
	 * @param crtypDtCreate
	 * @param crtypUidCreate
	 * @param crtypDtLupd
	 * @param crtypUidLupd
	 */
	public CkCtMstCargoType(String crtypId, String crtypName, String crtypDesc, String crtypDescOth, int crtypSeq,
			Character crtypStatus, Date crtypDtCreate, String crtypUidCreate, Date crtypDtLupd, String crtypUidLupd) {
		super();
		this.crtypId = crtypId;
		this.crtypName = crtypName;
		this.crtypDesc = crtypDesc;
		this.crtypDescOth = crtypDescOth;
		this.crtypSeq = crtypSeq;
		this.crtypStatus = crtypStatus;
		this.crtypDtCreate = crtypDtCreate;
		this.crtypUidCreate = crtypUidCreate;
		this.crtypDtLupd = crtypDtLupd;
		this.crtypUidLupd = crtypUidLupd;
	}
	
	// Properties
	/////////////
	/**
	 * @return the crtypId
	 */
	public String getCrtypId() {
		return crtypId;
	}

	/**
	 * @param crtypId the crtypId to set
	 */
	public void setCrtypId(String crtypId) {
		this.crtypId = crtypId;
	}

	/**
	 * @return the crtypName
	 */
	public String getCrtypName() {
		return crtypName;
	}

	/**
	 * @param crtypName the crtypName to set
	 */
	public void setCrtypName(String crtypName) {
		this.crtypName = crtypName;
	}

	/**
	 * @return the crtypDesc
	 */
	public String getCrtypDesc() {
		return crtypDesc;
	}

	/**
	 * @param crtypDesc the crtypDesc to set
	 */
	public void setCrtypDesc(String crtypDesc) {
		this.crtypDesc = crtypDesc;
	}

	/**
	 * @return the crtypDescOth
	 */
	public String getCrtypDescOth() {
		return crtypDescOth;
	}

	/**
	 * @param crtypDescOth the crtypDescOth to set
	 */
	public void setCrtypDescOth(String crtypDescOth) {
		this.crtypDescOth = crtypDescOth;
	}

	public int getCrtypSeq() {
		return crtypSeq;
	}

	public void setCrtypSeq(int crtypSeq) {
		this.crtypSeq = crtypSeq;
	}

	/**
	 * @return the crtypStatus
	 */
	public Character getCrtypStatus() {
		return crtypStatus;
	}

	/**
	 * @param crtypStatus the crtypStatus to set
	 */
	public void setCrtypStatus(Character crtypStatus) {
		this.crtypStatus = crtypStatus;
	}

	/**
	 * @return the crtypDtCreate
	 */
	public Date getCrtypDtCreate() {
		return crtypDtCreate;
	}

	/**
	 * @param crtypDtCreate the crtypDtCreate to set
	 */
	public void setCrtypDtCreate(Date crtypDtCreate) {
		this.crtypDtCreate = crtypDtCreate;
	}

	/**
	 * @return the crtypUidCreate
	 */
	public String getCrtypUidCreate() {
		return crtypUidCreate;
	}

	/**
	 * @param crtypUidCreate the crtypUidCreate to set
	 */
	public void setCrtypUidCreate(String crtypUidCreate) {
		this.crtypUidCreate = crtypUidCreate;
	}

	/**
	 * @return the crtypDtLupd
	 */
	public Date getCrtypDtLupd() {
		return crtypDtLupd;
	}

	/**
	 * @param crtypDtLupd the crtypDtLupd to set
	 */
	public void setCrtypDtLupd(Date crtypDtLupd) {
		this.crtypDtLupd = crtypDtLupd;
	}

	/**
	 * @return the crtypUidLupd
	 */
	public String getCrtypUidLupd() {
		return crtypUidLupd;
	}

	/**
	 * @param crtypUidLupd the crtypUidLupd to set
	 */
	public void setCrtypUidLupd(String crtypUidLupd) {
		this.crtypUidLupd = crtypUidLupd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 */
	@Override
	public int compareTo(CkCtMstCargoType o) {
		// TODO Auto-generated method stub
		return 0;
	}

	// Override Methods
	///////////////////
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
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 * 
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

}
