package com.guudint.clickargo.clictruck.planexec.trip.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.master.dto.CkCtMstTripAttachType;
import com.guudint.clickargo.clictruck.planexec.trip.model.TCkCtTripAttach;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtTripAttach extends AbstractDTO<CkCtTripAttach, TCkCtTripAttach> {

	private static final long serialVersionUID = -2935663438020938099L;
	private String atId;
	private CkCtMstTripAttachType TCkCtMstTripAttachType;
	private CkCtTrip TCkCtTrip;
	private String atName;
	private String atLoc;
	private String atSource;
	private String atComment;
	private Character atStatus;
	private Date atDtCreate;
	private String atUidCreate;
	private Date atDtLupd;
	private String atUidLupd;
	
	private byte[] atLocData;

	public CkCtTripAttach() {
	}

	public CkCtTripAttach(String atId) {
		this.atId = atId;
	}

	public CkCtTripAttach(TCkCtTripAttach entity) {
		super(entity);
	}

	public CkCtTripAttach(String atId, CkCtMstTripAttachType TCkCtMstTripAttachType, CkCtTrip TCkCtTrip,
			String atName, String atLoc, Character atStatus, Date atDtCreate, String atUidCreate, Date atDtLupd,
			String atUidLupd, String atComment) {
		this.atId = atId;
		this.TCkCtMstTripAttachType = TCkCtMstTripAttachType;
		this.TCkCtTrip = TCkCtTrip;
		this.atName = atName;
		this.atLoc = atLoc;
		this.atStatus = atStatus;
		this.atDtCreate = atDtCreate;
		this.atUidCreate = atUidCreate;
		this.atDtLupd = atDtLupd;
		this.atUidLupd = atUidLupd;
		this.atComment = atComment;
	}

	/**
	 * @return the atId
	 */
	public String getAtId() {
		return atId;
	}

	/**
	 * @param atId the atId to set
	 */
	public void setAtId(String atId) {
		this.atId = atId;
	}

	/**
	 * @return the tCkCtMstTripAttachType
	 */
	public CkCtMstTripAttachType getTCkCtMstTripAttachType() {
		return TCkCtMstTripAttachType;
	}

	/**
	 * @param tCkCtMstTripAttachType the tCkCtMstTripAttachType to set
	 */
	public void setTCkCtMstTripAttachType(CkCtMstTripAttachType tCkCtMstTripAttachType) {
		TCkCtMstTripAttachType = tCkCtMstTripAttachType;
	}

	/**
	 * @return the tCkCtTrip
	 */
	public CkCtTrip getTCkCtTrip() {
		return TCkCtTrip;
	}

	/**
	 * @param tCkCtTrip the tCkCtTrip to set
	 */
	public void setTCkCtTrip(CkCtTrip tCkCtTrip) {
		TCkCtTrip = tCkCtTrip;
	}

	/**
	 * @return the atName
	 */
	public String getAtName() {
		return atName;
	}

	/**
	 * @param atName the atName to set
	 */
	public void setAtName(String atName) {
		this.atName = atName;
	}

	/**
	 * @return the atLoc
	 */
	public String getAtLoc() {
		return atLoc;
	}

	/**
	 * @param atLoc the atLoc to set
	 */
	public void setAtLoc(String atLoc) {
		this.atLoc = atLoc;
	}

	public String getAtSource() {
		return this.atSource;
	}

	public void setAtSource(String atSource) {
		this.atSource = atSource;
	}

	/**
	 * @return the atStatus
	 */
	public Character getAtStatus() {
		return atStatus;
	}

	/**
	 * @param atStatus the atStatus to set
	 */
	public void setAtStatus(Character atStatus) {
		this.atStatus = atStatus;
	}

	/**
	 * @return the atDtCreate
	 */
	public Date getAtDtCreate() {
		return atDtCreate;
	}

	/**
	 * @param atDtCreate the atDtCreate to set
	 */
	public void setAtDtCreate(Date atDtCreate) {
		this.atDtCreate = atDtCreate;
	}

	/**
	 * @return the atUidCreate
	 */
	public String getAtUidCreate() {
		return atUidCreate;
	}

	/**
	 * @param atUidCreate the atUidCreate to set
	 */
	public void setAtUidCreate(String atUidCreate) {
		this.atUidCreate = atUidCreate;
	}

	/**
	 * @return the atDtLupd
	 */
	public Date getAtDtLupd() {
		return atDtLupd;
	}

	/**
	 * @param atDtLupd the atDtLupd to set
	 */
	public void setAtDtLupd(Date atDtLupd) {
		this.atDtLupd = atDtLupd;
	}

	/**
	 * @return the atUidLupd
	 */
	public String getAtUidLupd() {
		return atUidLupd;
	}

	/**
	 * @param atUidLupd the atUidLupd to set
	 */
	public void setAtUidLupd(String atUidLupd) {
		this.atUidLupd = atUidLupd;
	}

	/**
	 * @return the atLocData
	 */
	public byte[] getAtLocData() {
		return atLocData;
	}

	/**
	 * @param atLocData the atLocData to set
	 */
	public void setAtLocData(byte[] atLocData) {
		this.atLocData = atLocData;
	}

	public String getAtComment() {
		return atComment;
	}

	public void setAtComment(String atComment) {
		this.atComment = atComment;
	}

	@Override
	public int compareTo(CkCtTripAttach o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
