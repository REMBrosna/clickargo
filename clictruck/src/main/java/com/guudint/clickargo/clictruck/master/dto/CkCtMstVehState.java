package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.clictruck.master.model.TCkCtMstVehState;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstVehState extends AbstractDTO<CkCtMstVehState, TCkCtMstVehState> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 2289557547340932809L;

	// Attributes
	/////////////
	private String vhstId;
	private String vhstName;
	private String vhstDesc;
	private String vhstDescOth;
	private Character vhstStatus;
	private Date vhstDtCreate;
	private String vhstUidCreate;
	private Date vhstDtLupd;
	private String vhstUidLupd;
	private Set<CkCtVeh> TCkCtVehs = new HashSet<CkCtVeh>(0);
	
	// Constructors
	///////////////
	public CkCtMstVehState() {
	}

	/**
	 * @param entity
	 */
	public CkCtMstVehState(TCkCtMstVehState entity) {
		super(entity);
	}

	/**
	 * @param vhstId
	 * @param vhstName
	 * @param vhstDesc
	 * @param vhstDescOth
	 * @param vhstStatus
	 * @param vhstDtCreate
	 * @param vhstUidCreate
	 * @param vhstDtLupd
	 * @param vhstUidLupd
	 * @param tCkCtVehs
	 */
	public CkCtMstVehState(String vhstId, String vhstName, String vhstDesc, String vhstDescOth, Character vhstStatus,
			Date vhstDtCreate, String vhstUidCreate, Date vhstDtLupd, String vhstUidLupd, Set<CkCtVeh> tCkCtVehs) {
		super();
		this.vhstId = vhstId;
		this.vhstName = vhstName;
		this.vhstDesc = vhstDesc;
		this.vhstDescOth = vhstDescOth;
		this.vhstStatus = vhstStatus;
		this.vhstDtCreate = vhstDtCreate;
		this.vhstUidCreate = vhstUidCreate;
		this.vhstDtLupd = vhstDtLupd;
		this.vhstUidLupd = vhstUidLupd;
		TCkCtVehs = tCkCtVehs;
	}

	// Properties
	/////////////
	/**
	 * @return the vhstId
	 */
	public String getVhstId() {
		return vhstId;
	}

	/**
	 * @param vhstId the vhstId to set
	 */
	public void setVhstId(String vhstId) {
		this.vhstId = vhstId;
	}

	/**
	 * @return the vhstName
	 */
	public String getVhstName() {
		return vhstName;
	}

	/**
	 * @param vhstName the vhstName to set
	 */
	public void setVhstName(String vhstName) {
		this.vhstName = vhstName;
	}

	/**
	 * @return the vhstDesc
	 */
	public String getVhstDesc() {
		return vhstDesc;
	}

	/**
	 * @param vhstDesc the vhstDesc to set
	 */
	public void setVhstDesc(String vhstDesc) {
		this.vhstDesc = vhstDesc;
	}

	/**
	 * @return the vhstDescOth
	 */
	public String getVhstDescOth() {
		return vhstDescOth;
	}

	/**
	 * @param vhstDescOth the vhstDescOth to set
	 */
	public void setVhstDescOth(String vhstDescOth) {
		this.vhstDescOth = vhstDescOth;
	}

	/**
	 * @return the vhstStatus
	 */
	public Character getVhstStatus() {
		return vhstStatus;
	}

	/**
	 * @param vhstStatus the vhstStatus to set
	 */
	public void setVhstStatus(Character vhstStatus) {
		this.vhstStatus = vhstStatus;
	}

	/**
	 * @return the vhstDtCreate
	 */
	public Date getVhstDtCreate() {
		return vhstDtCreate;
	}

	/**
	 * @param vhstDtCreate the vhstDtCreate to set
	 */
	public void setVhstDtCreate(Date vhstDtCreate) {
		this.vhstDtCreate = vhstDtCreate;
	}

	/**
	 * @return the vhstUidCreate
	 */
	public String getVhstUidCreate() {
		return vhstUidCreate;
	}

	/**
	 * @param vhstUidCreate the vhstUidCreate to set
	 */
	public void setVhstUidCreate(String vhstUidCreate) {
		this.vhstUidCreate = vhstUidCreate;
	}

	/**
	 * @return the vhstDtLupd
	 */
	public Date getVhstDtLupd() {
		return vhstDtLupd;
	}

	/**
	 * @param vhstDtLupd the vhstDtLupd to set
	 */
	public void setVhstDtLupd(Date vhstDtLupd) {
		this.vhstDtLupd = vhstDtLupd;
	}

	/**
	 * @return the vhstUidLupd
	 */
	public String getVhstUidLupd() {
		return vhstUidLupd;
	}

	/**
	 * @param vhstUidLupd the vhstUidLupd to set
	 */
	public void setVhstUidLupd(String vhstUidLupd) {
		this.vhstUidLupd = vhstUidLupd;
	}

	/**
	 * @return the tCkCtVehs
	 */
	public Set<CkCtVeh> getTCkCtVehs() {
		return TCkCtVehs;
	}

	/**
	 * @param tCkCtVehs the tCkCtVehs to set
	 */
	public void setTCkCtVehs(Set<CkCtVeh> tCkCtVehs) {
		TCkCtVehs = tCkCtVehs;
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
	public int compareTo(CkCtMstVehState o) {
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
