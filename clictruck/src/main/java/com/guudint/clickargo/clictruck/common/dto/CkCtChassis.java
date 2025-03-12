package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtChassis;
import com.guudint.clickargo.clictruck.master.dto.CkCtMstChassisType;
import com.vcc.camelone.ccm.dto.CoreAccn;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtChassis extends AbstractDTO<CkCtChassis, TCkCtChassis>  {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -252547283359364002L;

	// Attributes
	/////////////
	private String chsId;
	private CoreAccn TCoreAccn;
	private String chsNo;
	private CkCtMstChassisType TCkCtMstChassisType;
	private Character chsStatus;
	private Date chsDtCreate;
	private String chsUidCreate;
	private Date chsDtLupd;
	private String chsUidLupd;
	
	// Constructors
	///////////////
	public CkCtChassis() {
	}

	public CkCtChassis(TCkCtChassis entity) {
		super(entity);
	}
	
	public CkCtChassis(String chsId, String chsNo, CoreAccn tCoreAccn,
			CkCtMstChassisType tCkCtMstChassisType, Character chsStatus, Date chsDtCreate, String chsUidCreate,
			Date chsDtLupd, String chsUidLupd) {
		super();
		this.chsId = chsId;
		TCoreAccn = tCoreAccn;
		this.chsNo = chsNo;
		TCkCtMstChassisType = tCkCtMstChassisType;
		this.chsStatus = chsStatus;
		this.chsDtCreate = chsDtCreate;
		this.chsUidCreate = chsUidCreate;
		this.chsDtLupd = chsDtLupd;
		this.chsUidLupd = chsUidLupd;
	}

	// Properties
	/////////////
	/**
	 * @return the chsId
	 */
	public String getChsId() {
		return chsId;
	}

	/**
	 * @param chsId the chsId to set
	 */
	public void setChsId(String chsId) {
		this.chsId = chsId;
	}

	/**
	 * @return the chsNo
	 */
	public String getChsNo() {
		return chsNo;
	}

	/**
	 * @param chsNo the chsNo to set
	 */
	public void setChsNo(String chsNo) {
		this.chsNo = chsNo;
	}
	
	/**
	 * @return the tCoreAccn
	 */
	public CoreAccn getTCoreAccn() {
		return TCoreAccn;
	}

	/**
	 * @param tCoreAccn the tCoreAccn to set
	 */
	public void setTCoreAccn(CoreAccn tCoreAccn) {
		TCoreAccn = tCoreAccn;
	}

	/**
	 * @return the tCkCtMstChassisType
	 */
	public CkCtMstChassisType getTCkCtMstChassisType() {
		return TCkCtMstChassisType;
	}

	/**
	 * @param tCkCtMstChassisType the tCkCtMstChassisType to set
	 */
	public void setTCkCtMstChassisType(CkCtMstChassisType tCkCtMstChassisType) {
		TCkCtMstChassisType = tCkCtMstChassisType;
	}

	/**
	 * @return the chsStatus
	 */
	public Character getChsStatus() {
		return chsStatus;
	}

	/**
	 * @param chsStatus the chsStatus to set
	 */
	public void setChsStatus(Character chsStatus) {
		this.chsStatus = chsStatus;
	}

	/**
	 * @return the chsDtCreate
	 */
	public Date getChsDtCreate() {
		return chsDtCreate;
	}

	/**
	 * @param chsDtCreate the chsDtCreate to set
	 */
	public void setChsDtCreate(Date chsDtCreate) {
		this.chsDtCreate = chsDtCreate;
	}

	/**
	 * @return the chsUidCreate
	 */
	public String getChsUidCreate() {
		return chsUidCreate;
	}

	/**
	 * @param chsUidCreate the chsUidCreate to set
	 */
	public void setChsUidCreate(String chsUidCreate) {
		this.chsUidCreate = chsUidCreate;
	}

	/**
	 * @return the chsDtLupd
	 */
	public Date getChsDtLupd() {
		return chsDtLupd;
	}

	/**
	 * @param chsDtLupd the chsDtLupd to set
	 */
	public void setChsDtLupd(Date chsDtLupd) {
		this.chsDtLupd = chsDtLupd;
	}

	/**
	 * @return the chsUidLupd
	 */
	public String getChsUidLupd() {
		return chsUidLupd;
	}

	/**
	 * @param chsUidLupd the chsUidLupd to set
	 */
	public void setChsUidLupd(String chsUidLupd) {
		this.chsUidLupd = chsUidLupd;
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(CkCtChassis o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
