package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.planexec.job.model.TCkCtJobTerm;
import com.vcc.camelone.common.dto.AbstractDTO;

public class CkCtJobTerm extends AbstractDTO<CkCtJobTerm, TCkCtJobTerm> {

	private static final long serialVersionUID = 8873347978261316846L;
	private String jtId;
	private CkCtJobTermReq TCkCtJobTermReq;
	private CkJobTruck TCkJobTruck;
	private Double jtJobDn;
	private Double jtJobPltfeeAmtCoff;
	private Double jtJobPltfeeAmtTo;
	private Character jtStatus;
	private Date jtDtCreate;
	private String jtUidCreate;
	private Date jtDtLupd;
	private String jtUidLupd;

	public CkCtJobTerm() {
	}

	public CkCtJobTerm(TCkCtJobTerm entity) {
		super(entity);
	}

	public CkCtJobTerm(String jtId, CkCtJobTermReq TCkCtJobTermReq, CkJobTruck TCkJobTruck) {
		this.jtId = jtId;
		this.TCkCtJobTermReq = TCkCtJobTermReq;
		this.TCkJobTruck = TCkJobTruck;
	}

	public CkCtJobTerm(String jtId, CkCtJobTermReq TCkCtJobTermReq, CkJobTruck TCkJobTruck, Double jtJobDn,
			Double jtJobPltfeeAmtCoff, Double jtJobPltfeeAmtTo, Character jtStatus, Date jtDtCreate, String jtUidCreate,
			Date jtDtLupd, String jtUidLupd) {
		this.jtId = jtId;
		this.TCkCtJobTermReq = TCkCtJobTermReq;
		this.TCkJobTruck = TCkJobTruck;
		this.jtJobDn = jtJobDn;
		this.jtJobPltfeeAmtCoff = jtJobPltfeeAmtCoff;
		this.jtJobPltfeeAmtTo = jtJobPltfeeAmtTo;
		this.jtStatus = jtStatus;
		this.jtDtCreate = jtDtCreate;
		this.jtUidCreate = jtUidCreate;
		this.jtDtLupd = jtDtLupd;
		this.jtUidLupd = jtUidLupd;
	}

	/**
	 * @return the jtId
	 */
	public String getJtId() {
		return jtId;
	}

	/**
	 * @param jtId the jtId to set
	 */
	public void setJtId(String jtId) {
		this.jtId = jtId;
	}

	/**
	 * @return the tCkCtJobTermReq
	 */
	public CkCtJobTermReq getTCkCtJobTermReq() {
		return TCkCtJobTermReq;
	}

	/**
	 * @param tCkCtJobTermReq the tCkCtJobTermReq to set
	 */
	public void setTCkCtJobTermReq(CkCtJobTermReq tCkCtJobTermReq) {
		TCkCtJobTermReq = tCkCtJobTermReq;
	}

	/**
	 * @return the tCkJobTruck
	 */
	public CkJobTruck getTCkJobTruck() {
		return TCkJobTruck;
	}

	/**
	 * @param tCkJobTruck the tCkJobTruck to set
	 */
	public void setTCkJobTruck(CkJobTruck tCkJobTruck) {
		TCkJobTruck = tCkJobTruck;
	}

	/**
	 * @return the jtJobDn
	 */
	public Double getJtJobDn() {
		return jtJobDn;
	}

	/**
	 * @param jtJobDn the jtJobDn to set
	 */
	public void setJtJobDn(Double jtJobDn) {
		this.jtJobDn = jtJobDn;
	}

	/**
	 * @return the jtJobPltfeeAmtCoff
	 */
	public Double getJtJobPltfeeAmtCoff() {
		return jtJobPltfeeAmtCoff;
	}

	/**
	 * @param jtJobPltfeeAmtCoff the jtJobPltfeeAmtCoff to set
	 */
	public void setJtJobPltfeeAmtCoff(Double jtJobPltfeeAmtCoff) {
		this.jtJobPltfeeAmtCoff = jtJobPltfeeAmtCoff;
	}

	/**
	 * @return the jtJobPltfeeAmtTo
	 */
	public Double getJtJobPltfeeAmtTo() {
		return jtJobPltfeeAmtTo;
	}

	/**
	 * @param jtJobPltfeeAmtTo the jtJobPltfeeAmtTo to set
	 */
	public void setJtJobPltfeeAmtTo(Double jtJobPltfeeAmtTo) {
		this.jtJobPltfeeAmtTo = jtJobPltfeeAmtTo;
	}

	/**
	 * @return the jtStatus
	 */
	public Character getJtStatus() {
		return jtStatus;
	}

	/**
	 * @param jtStatus the jtStatus to set
	 */
	public void setJtStatus(Character jtStatus) {
		this.jtStatus = jtStatus;
	}

	/**
	 * @return the jtDtCreate
	 */
	public Date getJtDtCreate() {
		return jtDtCreate;
	}

	/**
	 * @param jtDtCreate the jtDtCreate to set
	 */
	public void setJtDtCreate(Date jtDtCreate) {
		this.jtDtCreate = jtDtCreate;
	}

	/**
	 * @return the jtUidCreate
	 */
	public String getJtUidCreate() {
		return jtUidCreate;
	}

	/**
	 * @param jtUidCreate the jtUidCreate to set
	 */
	public void setJtUidCreate(String jtUidCreate) {
		this.jtUidCreate = jtUidCreate;
	}

	/**
	 * @return the jtDtLupd
	 */
	public Date getJtDtLupd() {
		return jtDtLupd;
	}

	/**
	 * @param jtDtLupd the jtDtLupd to set
	 */
	public void setJtDtLupd(Date jtDtLupd) {
		this.jtDtLupd = jtDtLupd;
	}

	/**
	 * @return the jtUidLupd
	 */
	public String getJtUidLupd() {
		return jtUidLupd;
	}

	/**
	 * @param jtUidLupd the jtUidLupd to set
	 */
	public void setJtUidLupd(String jtUidLupd) {
		this.jtUidLupd = jtUidLupd;
	}

	@Override
	public int compareTo(CkCtJobTerm o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
