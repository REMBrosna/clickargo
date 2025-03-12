package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstRentalType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstRentalType extends AbstractDTO<CkCtMstRentalType, TCkCtMstRentalType> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 5981086483134061725L;

	// Attributes
	/////////////
	private String rtId;
	private String rtName;
	private String rtDesc;
	private String rtDescOth;
	private Character rtStatus;
	private Date rtDtCreate;
	private String rtUidCreate;
	private Date rtDtLupd;
	private String rtUidLupd;
	
	// Constructors
	///////////////
	public CkCtMstRentalType() {
	}

	/**
	 * @param entity
	 */
	public CkCtMstRentalType(TCkCtMstRentalType entity) {
		super(entity);
	}

	/**
	 * @param rtId
	 * @param rtName
	 * @param rtDesc
	 * @param rtDescOth
	 * @param rtStatus
	 * @param rtDtCreate
	 * @param rtUidCreate
	 * @param rtDtLupd
	 * @param rtUidLupd
	 */
	public CkCtMstRentalType(String rtId, String rtName, String rtDesc, String rtDescOth, Character rtStatus,
			Date rtDtCreate, String rtUidCreate, Date rtDtLupd, String rtUidLupd) {
		super();
		this.rtId = rtId;
		this.rtName = rtName;
		this.rtDesc = rtDesc;
		this.rtDescOth = rtDescOth;
		this.rtStatus = rtStatus;
		this.rtDtCreate = rtDtCreate;
		this.rtUidCreate = rtUidCreate;
		this.rtDtLupd = rtDtLupd;
		this.rtUidLupd = rtUidLupd;
	}

	// Properties
	/////////////	
	/**
	 * @return the rtId
	 */
	public String getRtId() {
		return rtId;
	}

	/**
	 * @param rtId the rtId to set
	 */
	public void setRtId(String rtId) {
		this.rtId = rtId;
	}

	/**
	 * @return the rtName
	 */
	public String getRtName() {
		return rtName;
	}

	/**
	 * @param rtName the rtName to set
	 */
	public void setRtName(String rtName) {
		this.rtName = rtName;
	}

	/**
	 * @return the rtDesc
	 */
	public String getRtDesc() {
		return rtDesc;
	}

	/**
	 * @param rtDesc the rtDesc to set
	 */
	public void setRtDesc(String rtDesc) {
		this.rtDesc = rtDesc;
	}

	/**
	 * @return the rtDescOth
	 */
	public String getRtDescOth() {
		return rtDescOth;
	}

	/**
	 * @param rtDescOth the rtDescOth to set
	 */
	public void setRtDescOth(String rtDescOth) {
		this.rtDescOth = rtDescOth;
	}

	/**
	 * @return the rtStatus
	 */
	public Character getRtStatus() {
		return rtStatus;
	}

	/**
	 * @param rtStatus the rtStatus to set
	 */
	public void setRtStatus(Character rtStatus) {
		this.rtStatus = rtStatus;
	}

	/**
	 * @return the rtDtCreate
	 */
	public Date getRtDtCreate() {
		return rtDtCreate;
	}

	/**
	 * @param rtDtCreate the rtDtCreate to set
	 */
	public void setRtDtCreate(Date rtDtCreate) {
		this.rtDtCreate = rtDtCreate;
	}

	/**
	 * @return the rtUidCreate
	 */
	public String getRtUidCreate() {
		return rtUidCreate;
	}

	/**
	 * @param rtUidCreate the rtUidCreate to set
	 */
	public void setRtUidCreate(String rtUidCreate) {
		this.rtUidCreate = rtUidCreate;
	}

	/**
	 * @return the rtDtLupd
	 */
	public Date getRtDtLupd() {
		return rtDtLupd;
	}

	/**
	 * @param rtDtLupd the rtDtLupd to set
	 */
	public void setRtDtLupd(Date rtDtLupd) {
		this.rtDtLupd = rtDtLupd;
	}

	/**
	 * @return the rtUidLupd
	 */
	public String getRtUidLupd() {
		return rtUidLupd;
	}

	/**
	 * @param rtUidLupd the rtUidLupd to set
	 */
	public void setRtUidLupd(String rtUidLupd) {
		this.rtUidLupd = rtUidLupd;
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
	public int compareTo(CkCtMstRentalType o) {
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
