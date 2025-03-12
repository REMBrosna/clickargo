package com.guudint.clickargo.clictruck.common.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.common.model.TCkCtDeptUsr;
import com.vcc.camelone.ccm.dto.CoreUsr;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtDeptUsr extends AbstractDTO<CkCtDeptUsr, TCkCtDeptUsr> {

	private static final long serialVersionUID = -3640117207663831042L;
	private String duId;
	private CkCtDept TCkCtDept;
	private CoreUsr TCoreUsr;
	private Character duStatus;
	private Date duDtCreate;
	private String duUidCreate;
	private Date duDtLupd;
	private String duUidLupd;

	public CkCtDeptUsr() {
	}

	public CkCtDeptUsr(TCkCtDeptUsr entity) {
		super(entity);
	}

	public CkCtDeptUsr(String duId, CkCtDept TCkCtDept, CoreUsr TCoreUsr, Character duStatus, Date duDtCreate,
			String duUidCreate, Date duDtLupd, String duUidLupd) {
		this.duId = duId;
		this.TCkCtDept = TCkCtDept;
		this.TCoreUsr = TCoreUsr;
		this.duStatus = duStatus;
		this.duDtCreate = duDtCreate;
		this.duUidCreate = duUidCreate;
		this.duDtLupd = duDtLupd;
		this.duUidLupd = duUidLupd;
	}

	/**
	 * @return the duId
	 */
	public String getDuId() {
		return duId;
	}

	/**
	 * @param duId the duId to set
	 */
	public void setDuId(String duId) {
		this.duId = duId;
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
	 * @return the tCoreUsr
	 */
	public CoreUsr getTCoreUsr() {
		return TCoreUsr;
	}

	/**
	 * @param tCoreUsr the tCoreUsr to set
	 */
	public void setTCoreUsr(CoreUsr tCoreUsr) {
		TCoreUsr = tCoreUsr;
	}

	/**
	 * @return the duStatus
	 */
	public Character getDuStatus() {
		return duStatus;
	}

	/**
	 * @param duStatus the duStatus to set
	 */
	public void setDuStatus(Character duStatus) {
		this.duStatus = duStatus;
	}

	/**
	 * @return the duDtCreate
	 */
	public Date getDuDtCreate() {
		return duDtCreate;
	}

	/**
	 * @param duDtCreate the duDtCreate to set
	 */
	public void setDuDtCreate(Date duDtCreate) {
		this.duDtCreate = duDtCreate;
	}

	/**
	 * @return the duUidCreate
	 */
	public String getDuUidCreate() {
		return duUidCreate;
	}

	/**
	 * @param duUidCreate the duUidCreate to set
	 */
	public void setDuUidCreate(String duUidCreate) {
		this.duUidCreate = duUidCreate;
	}

	/**
	 * @return the duDtLupd
	 */
	public Date getDuDtLupd() {
		return duDtLupd;
	}

	/**
	 * @param duDtLupd the duDtLupd to set
	 */
	public void setDuDtLupd(Date duDtLupd) {
		this.duDtLupd = duDtLupd;
	}

	/**
	 * @return the duUidLupd
	 */
	public String getDuUidLupd() {
		return duUidLupd;
	}

	/**
	 * @param duUidLupd the duUidLupd to set
	 */
	public void setDuUidLupd(String duUidLupd) {
		this.duUidLupd = duUidLupd;
	}

	@Override
	public int compareTo(CkCtDeptUsr o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
