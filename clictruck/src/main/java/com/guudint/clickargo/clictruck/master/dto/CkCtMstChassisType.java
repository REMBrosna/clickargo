package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstChassisType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstChassisType extends AbstractDTO<CkCtMstChassisType, TCkCtMstChassisType> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -7824783600602296126L;

	// Attributes
	/////////////
	private String chtyId;
	private String chtyName;
	private String chtyDesc;
	private String chtyDescOth;
	private Character chtyStatus;
	private Date chtyDtCreate;
	private String chtyUidCreate;
	private Date chtyDtLupd;
	private String chtyUidLupd;
	
	// Constructors
	///////////////
	public CkCtMstChassisType() {
	}

	/**
	 * @param entity
	 */
	public CkCtMstChassisType(TCkCtMstChassisType entity) {
		super(entity);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param chtyId
	 * @param chtyName
	 * @param chtyDesc
	 * @param chtyDescOth
	 * @param chtyStatus
	 * @param chtyDtCreate
	 * @param chtyUidCreate
	 * @param chtyDtLupd
	 * @param chtyUidLupd
	 */
	public CkCtMstChassisType(String chtyId, String chtyName, String chtyDesc, String chtyDescOth, Character chtyStatus,
			Date chtyDtCreate, String chtyUidCreate, Date chtyDtLupd, String chtyUidLupd) {
		super();
		this.chtyId = chtyId;
		this.chtyName = chtyName;
		this.chtyDesc = chtyDesc;
		this.chtyDescOth = chtyDescOth;
		this.chtyStatus = chtyStatus;
		this.chtyDtCreate = chtyDtCreate;
		this.chtyUidCreate = chtyUidCreate;
		this.chtyDtLupd = chtyDtLupd;
		this.chtyUidLupd = chtyUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the chtyId
	 */
	public String getChtyId() {
		return chtyId;
	}

	/**
	 * @param chtyId the chtyId to set
	 */
	public void setChtyId(String chtyId) {
		this.chtyId = chtyId;
	}

	/**
	 * @return the chtyName
	 */
	public String getChtyName() {
		return chtyName;
	}

	/**
	 * @param chtyName the chtyName to set
	 */
	public void setChtyName(String chtyName) {
		this.chtyName = chtyName;
	}

	/**
	 * @return the chtyDesc
	 */
	public String getChtyDesc() {
		return chtyDesc;
	}

	/**
	 * @param chtyDesc the chtyDesc to set
	 */
	public void setChtyDesc(String chtyDesc) {
		this.chtyDesc = chtyDesc;
	}

	/**
	 * @return the chtyDescOth
	 */
	public String getChtyDescOth() {
		return chtyDescOth;
	}

	/**
	 * @param chtyDescOth the chtyDescOth to set
	 */
	public void setChtyDescOth(String chtyDescOth) {
		this.chtyDescOth = chtyDescOth;
	}

	/**
	 * @return the chtyStatus
	 */
	public Character getChtyStatus() {
		return chtyStatus;
	}

	/**
	 * @param chtyStatus the chtyStatus to set
	 */
	public void setChtyStatus(Character chtyStatus) {
		this.chtyStatus = chtyStatus;
	}

	/**
	 * @return the chtyDtCreate
	 */
	public Date getChtyDtCreate() {
		return chtyDtCreate;
	}

	/**
	 * @param chtyDtCreate the chtyDtCreate to set
	 */
	public void setChtyDtCreate(Date chtyDtCreate) {
		this.chtyDtCreate = chtyDtCreate;
	}

	/**
	 * @return the chtyUidCreate
	 */
	public String getChtyUidCreate() {
		return chtyUidCreate;
	}

	/**
	 * @param chtyUidCreate the chtyUidCreate to set
	 */
	public void setChtyUidCreate(String chtyUidCreate) {
		this.chtyUidCreate = chtyUidCreate;
	}

	/**
	 * @return the chtyDtLupd
	 */
	public Date getChtyDtLupd() {
		return chtyDtLupd;
	}

	/**
	 * @param chtyDtLupd the chtyDtLupd to set
	 */
	public void setChtyDtLupd(Date chtyDtLupd) {
		this.chtyDtLupd = chtyDtLupd;
	}

	/**
	 * @return the chtyUidLupd
	 */
	public String getChtyUidLupd() {
		return chtyUidLupd;
	}

	/**
	 * @param chtyUidLupd the chtyUidLupd to set
	 */
	public void setChtyUidLupd(String chtyUidLupd) {
		this.chtyUidLupd = chtyUidLupd;
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
	public int compareTo(CkCtMstChassisType o) {
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
