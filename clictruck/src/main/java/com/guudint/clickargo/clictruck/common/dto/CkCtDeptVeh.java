package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtDeptVeh;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtDeptVeh extends AbstractDTO<CkCtDeptVeh, TCkCtDeptVeh> {

	private static final long serialVersionUID = 2163294964480997214L;
	private String dvId;
	private CkCtDept TCkCtDept;
	private CkCtVeh TCkCtVeh;
	private Character dvStatus;
	private Date dvDtCreate;
	private String dvUidCreate;
	private Date dvDtLupd;
	private String dvUidLupd;

	public CkCtDeptVeh() {
	}

	public CkCtDeptVeh(String dvId) {
		this.dvId = dvId;
	}

	public CkCtDeptVeh(String dvId, CkCtDept TCkCtDept, CkCtVeh TCkCtVeh, Character dvStatus, Date dvDtCreate,
			String dvUidCreate, Date dvDtLupd, String dvUidLupd) {
		this.dvId = dvId;
		this.TCkCtDept = TCkCtDept;
		this.TCkCtVeh = TCkCtVeh;
		this.dvStatus = dvStatus;
		this.dvDtCreate = dvDtCreate;
		this.dvUidCreate = dvUidCreate;
		this.dvDtLupd = dvDtLupd;
		this.dvUidLupd = dvUidLupd;
	}

	/**
	 * @return the dvId
	 */
	public String getDvId() {
		return dvId;
	}

	/**
	 * @param dvId the dvId to set
	 */
	public void setDvId(String dvId) {
		this.dvId = dvId;
	}

	/**
	 * @return the tCkCtDept
	 */
	public CkCtDept getTCkCtDept() {
		return TCkCtDept;
	}

	/**
	 * @param tCkCtDept the tCkCtDept to set
	 */
	public void setTCkCtDept(CkCtDept tCkCtDept) {
		TCkCtDept = tCkCtDept;
	}

	/**
	 * @return the tCkCtVeh
	 */
	public CkCtVeh getTCkCtVeh() {
		return TCkCtVeh;
	}

	/**
	 * @param tCkCtVeh the tCkCtVeh to set
	 */
	public void setTCkCtVeh(CkCtVeh tCkCtVeh) {
		TCkCtVeh = tCkCtVeh;
	}

	/**
	 * @return the dvStatus
	 */
	public Character getDvStatus() {
		return dvStatus;
	}

	/**
	 * @param dvStatus the dvStatus to set
	 */
	public void setDvStatus(Character dvStatus) {
		this.dvStatus = dvStatus;
	}

	/**
	 * @return the dvDtCreate
	 */
	public Date getDvDtCreate() {
		return dvDtCreate;
	}

	/**
	 * @param dvDtCreate the dvDtCreate to set
	 */
	public void setDvDtCreate(Date dvDtCreate) {
		this.dvDtCreate = dvDtCreate;
	}

	/**
	 * @return the dvUidCreate
	 */
	public String getDvUidCreate() {
		return dvUidCreate;
	}

	/**
	 * @param dvUidCreate the dvUidCreate to set
	 */
	public void setDvUidCreate(String dvUidCreate) {
		this.dvUidCreate = dvUidCreate;
	}

	/**
	 * @return the dvDtLupd
	 */
	public Date getDvDtLupd() {
		return dvDtLupd;
	}

	/**
	 * @param dvDtLupd the dvDtLupd to set
	 */
	public void setDvDtLupd(Date dvDtLupd) {
		this.dvDtLupd = dvDtLupd;
	}

	/**
	 * @return the dvUidLupd
	 */
	public String getDvUidLupd() {
		return dvUidLupd;
	}

	/**
	 * @param dvUidLupd the dvUidLupd to set
	 */
	public void setDvUidLupd(String dvUidLupd) {
		this.dvUidLupd = dvUidLupd;
	}

	@Override
	public int compareTo(CkCtDeptVeh o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
