package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstLocationType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstLocationType extends AbstractDTO<CkCtMstLocationType, TCkCtMstLocationType> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -3982893783651609685L;

	// Attributes
	/////////////
	private String lctyId;
	private String lctyName;
	private String lctyDesc;
	private String lctyDescOth;
	private Character lctyStatus;
	private Date lctyDtCreate;
	private String lctyUidCreate;
	private Date lctyDtLupd;
	private String lctyUidLupd;

	// Constructors
	///////////////
	public CkCtMstLocationType() {
	}

	/**
	 * @param entity
	 */
	public CkCtMstLocationType(TCkCtMstLocationType entity) {
		super(entity);
	}

	/**
	 * @param lctyId
	 * @param lctyName
	 * @param lctyDesc
	 * @param lctyDescOth
	 * @param lctyStatus
	 * @param lctyDtCreate
	 * @param lctyUidCreate
	 * @param lctyDtLupd
	 * @param lctyUidLupd
	 */
	public CkCtMstLocationType(String lctyId, String lctyName, String lctyDesc, String lctyDescOth,
			Character lctyStatus, Date lctyDtCreate, String lctyUidCreate, Date lctyDtLupd, String lctyUidLupd) {
		super();
		this.lctyId = lctyId;
		this.lctyName = lctyName;
		this.lctyDesc = lctyDesc;
		this.lctyDescOth = lctyDescOth;
		this.lctyStatus = lctyStatus;
		this.lctyDtCreate = lctyDtCreate;
		this.lctyUidCreate = lctyUidCreate;
		this.lctyDtLupd = lctyDtLupd;
		this.lctyUidLupd = lctyUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the lctyId
	 */
	public String getLctyId() {
		return lctyId;
	}

	/**
	 * @param lctyId the lctyId to set
	 */
	public void setLctyId(String lctyId) {
		this.lctyId = lctyId;
	}

	/**
	 * @return the lctyName
	 */
	public String getLctyName() {
		return lctyName;
	}

	/**
	 * @param lctyName the lctyName to set
	 */
	public void setLctyName(String lctyName) {
		this.lctyName = lctyName;
	}

	/**
	 * @return the lctyDesc
	 */
	public String getLctyDesc() {
		return lctyDesc;
	}

	/**
	 * @param lctyDesc the lctyDesc to set
	 */
	public void setLctyDesc(String lctyDesc) {
		this.lctyDesc = lctyDesc;
	}

	/**
	 * @return the lctyDescOth
	 */
	public String getLctyDescOth() {
		return lctyDescOth;
	}

	/**
	 * @param lctyDescOth the lctyDescOth to set
	 */
	public void setLctyDescOth(String lctyDescOth) {
		this.lctyDescOth = lctyDescOth;
	}

	/**
	 * @return the lctyStatus
	 */
	public Character getLctyStatus() {
		return lctyStatus;
	}

	/**
	 * @param lctyStatus the lctyStatus to set
	 */
	public void setLctyStatus(Character lctyStatus) {
		this.lctyStatus = lctyStatus;
	}

	/**
	 * @return the lctyDtCreate
	 */
	public Date getLctyDtCreate() {
		return lctyDtCreate;
	}

	/**
	 * @param lctyDtCreate the lctyDtCreate to set
	 */
	public void setLctyDtCreate(Date lctyDtCreate) {
		this.lctyDtCreate = lctyDtCreate;
	}

	/**
	 * @return the lctyUidCreate
	 */
	public String getLctyUidCreate() {
		return lctyUidCreate;
	}

	/**
	 * @param lctyUidCreate the lctyUidCreate to set
	 */
	public void setLctyUidCreate(String lctyUidCreate) {
		this.lctyUidCreate = lctyUidCreate;
	}

	/**
	 * @return the lctyDtLupd
	 */
	public Date getLctyDtLupd() {
		return lctyDtLupd;
	}

	/**
	 * @param lctyDtLupd the lctyDtLupd to set
	 */
	public void setLctyDtLupd(Date lctyDtLupd) {
		this.lctyDtLupd = lctyDtLupd;
	}

	/**
	 * @return the lctyUidLupd
	 */
	public String getLctyUidLupd() {
		return lctyUidLupd;
	}

	/**
	 * @param lctyUidLupd the lctyUidLupd to set
	 */
	public void setLctyUidLupd(String lctyUidLupd) {
		this.lctyUidLupd = lctyUidLupd;
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
	public int compareTo(CkCtMstLocationType o) {
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
