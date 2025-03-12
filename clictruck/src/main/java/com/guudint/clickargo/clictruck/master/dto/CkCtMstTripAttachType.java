package com.guudint.clickargo.clictruck.master.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.model.TCkCtMstTripAttachType;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstTripAttachType extends AbstractDTO<CkCtMstTripAttachType, TCkCtMstTripAttachType> {

	private static final long serialVersionUID = 5550532389339469224L;
	private String atypId;
	private String atypName;
	private String atypDesc;
	private String atypDescOth;
	private Character atypStatus;
	private Date atypDtCreate;
	private String atypUidCreate;
	private Date atypDtLupd;
	private String atypUidLupd;

	public CkCtMstTripAttachType() {
	}

	public CkCtMstTripAttachType(TCkCtMstTripAttachType entity) {
		super(entity);
	}

	public CkCtMstTripAttachType(String atypId, String atypName) {
		this.atypId = atypId;
		this.atypName = atypName;
	}

	/**
	 * @return the atypId
	 */
	public String getAtypId() {
		return atypId;
	}

	/**
	 * @param atypId the atypId to set
	 */
	public void setAtypId(String atypId) {
		this.atypId = atypId;
	}

	/**
	 * @return the atypName
	 */
	public String getAtypName() {
		return atypName;
	}

	/**
	 * @param atypName the atypName to set
	 */
	public void setAtypName(String atypName) {
		this.atypName = atypName;
	}

	/**
	 * @return the atypDesc
	 */
	public String getAtypDesc() {
		return atypDesc;
	}

	/**
	 * @param atypDesc the atypDesc to set
	 */
	public void setAtypDesc(String atypDesc) {
		this.atypDesc = atypDesc;
	}

	/**
	 * @return the atypDescOth
	 */
	public String getAtypDescOth() {
		return atypDescOth;
	}

	/**
	 * @param atypDescOth the atypDescOth to set
	 */
	public void setAtypDescOth(String atypDescOth) {
		this.atypDescOth = atypDescOth;
	}

	/**
	 * @return the atypStatus
	 */
	public Character getAtypStatus() {
		return atypStatus;
	}

	/**
	 * @param atypStatus the atypStatus to set
	 */
	public void setAtypStatus(Character atypStatus) {
		this.atypStatus = atypStatus;
	}

	/**
	 * @return the atypDtCreate
	 */
	public Date getAtypDtCreate() {
		return atypDtCreate;
	}

	/**
	 * @param atypDtCreate the atypDtCreate to set
	 */
	public void setAtypDtCreate(Date atypDtCreate) {
		this.atypDtCreate = atypDtCreate;
	}

	/**
	 * @return the atypUidCreate
	 */
	public String getAtypUidCreate() {
		return atypUidCreate;
	}

	/**
	 * @param atypUidCreate the atypUidCreate to set
	 */
	public void setAtypUidCreate(String atypUidCreate) {
		this.atypUidCreate = atypUidCreate;
	}

	/**
	 * @return the atypDtLupd
	 */
	public Date getAtypDtLupd() {
		return atypDtLupd;
	}

	/**
	 * @param atypDtLupd the atypDtLupd to set
	 */
	public void setAtypDtLupd(Date atypDtLupd) {
		this.atypDtLupd = atypDtLupd;
	}

	/**
	 * @return the atypUidLupd
	 */
	public String getAtypUidLupd() {
		return atypUidLupd;
	}

	/**
	 * @param atypUidLupd the atypUidLupd to set
	 */
	public void setAtypUidLupd(String atypUidLupd) {
		this.atypUidLupd = atypUidLupd;
	}

	@Override
	public int compareTo(CkCtMstTripAttachType o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
