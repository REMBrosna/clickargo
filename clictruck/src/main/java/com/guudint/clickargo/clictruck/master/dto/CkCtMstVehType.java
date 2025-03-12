package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstVehType extends AbstractDTO<CkCtMstVehType, TCkCtMstVehType> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 3599848974256614592L;

	// Attributes
	/////////////
	private String vhtyId;
	private String vhtyName;
	private String vhtyDesc;
	private String vhtyDescOth;
	private Character vhtyStatus;
	private Date vhtyDtCreate;
	private String vhtyUidCreate;
	private Date vhtyDtLupd;
	private String vhtyUidLupd;

	// Constructors
	///////////////
	public CkCtMstVehType() {
	}
	
	public CkCtMstVehType(String vhtyId) {
		this.vhtyId = vhtyId;
	}

	/**
	 * @param entity
	 */
	public CkCtMstVehType(TCkCtMstVehType entity) {
		super(entity);
	}

	/**
	 * @param vhtyId
	 * @param vhtyName
	 * @param vhtyDesc
	 * @param vhtyDescOth
	 * @param vhtyStatus
	 * @param vhtyDtCreate
	 * @param vhtyUidCreate
	 * @param vhtyDtLupd
	 * @param vhtyUidLupd
	 */
	public CkCtMstVehType(String vhtyId, String vhtyName, String vhtyDesc, String vhtyDescOth, Character vhtyStatus,
			Date vhtyDtCreate, String vhtyUidCreate, Date vhtyDtLupd, String vhtyUidLupd) {
		super();
		this.vhtyId = vhtyId;
		this.vhtyName = vhtyName;
		this.vhtyDesc = vhtyDesc;
		this.vhtyDescOth = vhtyDescOth;
		this.vhtyStatus = vhtyStatus;
		this.vhtyDtCreate = vhtyDtCreate;
		this.vhtyUidCreate = vhtyUidCreate;
		this.vhtyDtLupd = vhtyDtLupd;
		this.vhtyUidLupd = vhtyUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the vhtyId
	 */
	public String getVhtyId() {
		return vhtyId;
	}

	/**
	 * @param vhtyId the vhtyId to set
	 */
	public void setVhtyId(String vhtyId) {
		this.vhtyId = vhtyId;
	}

	/**
	 * @return the vhtyName
	 */
	public String getVhtyName() {
		return vhtyName;
	}

	/**
	 * @param vhtyName the vhtyName to set
	 */
	public void setVhtyName(String vhtyName) {
		this.vhtyName = vhtyName;
	}

	/**
	 * @return the vhtyDesc
	 */
	public String getVhtyDesc() {
		return vhtyDesc;
	}

	/**
	 * @param vhtyDesc the vhtyDesc to set
	 */
	public void setVhtyDesc(String vhtyDesc) {
		this.vhtyDesc = vhtyDesc;
	}

	/**
	 * @return the vhtyDescOth
	 */
	public String getVhtyDescOth() {
		return vhtyDescOth;
	}

	/**
	 * @param vhtyDescOth the vhtyDescOth to set
	 */
	public void setVhtyDescOth(String vhtyDescOth) {
		this.vhtyDescOth = vhtyDescOth;
	}

	/**
	 * @return the vhtyStatus
	 */
	public Character getVhtyStatus() {
		return vhtyStatus;
	}

	/**
	 * @param vhtyStatus the vhtyStatus to set
	 */
	public void setVhtyStatus(Character vhtyStatus) {
		this.vhtyStatus = vhtyStatus;
	}

	/**
	 * @return the vhtyDtCreate
	 */
	public Date getVhtyDtCreate() {
		return vhtyDtCreate;
	}

	/**
	 * @param vhtyDtCreate the vhtyDtCreate to set
	 */
	public void setVhtyDtCreate(Date vhtyDtCreate) {
		this.vhtyDtCreate = vhtyDtCreate;
	}

	/**
	 * @return the vhtyUidCreate
	 */
	public String getVhtyUidCreate() {
		return vhtyUidCreate;
	}

	/**
	 * @param vhtyUidCreate the vhtyUidCreate to set
	 */
	public void setVhtyUidCreate(String vhtyUidCreate) {
		this.vhtyUidCreate = vhtyUidCreate;
	}

	/**
	 * @return the vhtyDtLupd
	 */
	public Date getVhtyDtLupd() {
		return vhtyDtLupd;
	}

	/**
	 * @param vhtyDtLupd the vhtyDtLupd to set
	 */
	public void setVhtyDtLupd(Date vhtyDtLupd) {
		this.vhtyDtLupd = vhtyDtLupd;
	}

	/**
	 * @return the vhtyUidLupd
	 */
	public String getVhtyUidLupd() {
		return vhtyUidLupd;
	}

	/**
	 * @param vhtyUidLupd the vhtyUidLupd to set
	 */
	public void setVhtyUidLupd(String vhtyUidLupd) {
		this.vhtyUidLupd = vhtyUidLupd;
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
	public int compareTo(CkCtMstVehType o) {
		return o.getVhtyId().equals(this.getVhtyId()) && o.getVhtyName().equals(this.getVhtyName()) ? 0 : 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		CkCtMstVehType ckCtMstVehType = (CkCtMstVehType) obj;
		return ckCtMstVehType.getVhtyId().equals(this.vhtyId);
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
