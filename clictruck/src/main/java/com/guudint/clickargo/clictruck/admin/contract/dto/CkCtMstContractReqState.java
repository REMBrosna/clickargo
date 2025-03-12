package com.guudint.clickargo.clictruck.admin.contract.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.contract.model.TCkCtMstContractReqState;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtMstContractReqState extends AbstractDTO<CkCtMstContractReqState, TCkCtMstContractReqState> {

	private static final long serialVersionUID = 8049974586827527304L;
	private String stId;
	private String stName;
	private String stDesc;
	private String stDescOth;
	private Character stStatus;
	private Date stDtCreate;
	private String stUidCreate;
	private Date stDtLupd;
	private String stUidLupd;

	public CkCtMstContractReqState() {
	}

	public CkCtMstContractReqState(TCkCtMstContractReqState entity) {
		super(entity);
	}

	public CkCtMstContractReqState(String stId) {
		this.stId = stId;
	}

	public CkCtMstContractReqState(String stId, String stName, String stDesc, String stDescOth, Character stStatus,
			Date stDtCreate, String stUidCreate, Date stDtLupd, String stUidLupd) {
		this.stId = stId;
		this.stName = stName;
		this.stDesc = stDesc;
		this.stDescOth = stDescOth;
		this.stStatus = stStatus;
		this.stDtCreate = stDtCreate;
		this.stUidCreate = stUidCreate;
		this.stDtLupd = stDtLupd;
		this.stUidLupd = stUidLupd;
	}

	/**
	 * @return the stId
	 */
	public String getStId() {
		return stId;
	}

	/**
	 * @param stId the stId to set
	 */
	public void setStId(String stId) {
		this.stId = stId;
	}

	/**
	 * @return the stName
	 */
	public String getStName() {
		return stName;
	}

	/**
	 * @param stName the stName to set
	 */
	public void setStName(String stName) {
		this.stName = stName;
	}

	/**
	 * @return the stDesc
	 */
	public String getStDesc() {
		return stDesc;
	}

	/**
	 * @param stDesc the stDesc to set
	 */
	public void setStDesc(String stDesc) {
		this.stDesc = stDesc;
	}

	/**
	 * @return the stDescOth
	 */
	public String getStDescOth() {
		return stDescOth;
	}

	/**
	 * @param stDescOth the stDescOth to set
	 */
	public void setStDescOth(String stDescOth) {
		this.stDescOth = stDescOth;
	}

	/**
	 * @return the stStatus
	 */
	public Character getStStatus() {
		return stStatus;
	}

	/**
	 * @param stStatus the stStatus to set
	 */
	public void setStStatus(Character stStatus) {
		this.stStatus = stStatus;
	}

	/**
	 * @return the stDtCreate
	 */
	public Date getStDtCreate() {
		return stDtCreate;
	}

	/**
	 * @param stDtCreate the stDtCreate to set
	 */
	public void setStDtCreate(Date stDtCreate) {
		this.stDtCreate = stDtCreate;
	}

	/**
	 * @return the stUidCreate
	 */
	public String getStUidCreate() {
		return stUidCreate;
	}

	/**
	 * @param stUidCreate the stUidCreate to set
	 */
	public void setStUidCreate(String stUidCreate) {
		this.stUidCreate = stUidCreate;
	}

	/**
	 * @return the stDtLupd
	 */
	public Date getStDtLupd() {
		return stDtLupd;
	}

	/**
	 * @param stDtLupd the stDtLupd to set
	 */
	public void setStDtLupd(Date stDtLupd) {
		this.stDtLupd = stDtLupd;
	}

	/**
	 * @return the stUidLupd
	 */
	public String getStUidLupd() {
		return stUidLupd;
	}

	/**
	 * @param stUidLupd the stUidLupd to set
	 */
	public void setStUidLupd(String stUidLupd) {
		this.stUidLupd = stUidLupd;
	}

	@Override
	public int compareTo(CkCtMstContractReqState o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	
}
