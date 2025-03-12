package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.Date;

import com.guudint.clickargo.clictruck.admin.contract.dto.CkCtConAddAttr;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruckAddAttr;
import com.vcc.camelone.common.dto.AbstractDTO;
public class CkJobTruckAddAttr extends AbstractDTO<CkJobTruckAddAttr, TCkJobTruckAddAttr> {

	private static final long serialVersionUID = 5148715924892711118L;
	private String jaaId;
	private CkCtConAddAttr TCkCtConAddAttr;
	private CkJobTruck TCkJobTruck;
	private String jaaValue;
	private Character jaaStatus;
	private Date jaaDtCreate;
	private String jaaUidCreate;
	private Date jaaDtLupd;
	private String jaaUidLupd;

	public CkJobTruckAddAttr() {
	}

	public CkJobTruckAddAttr(TCkJobTruckAddAttr entity) {
		super(entity);
	}

	public CkJobTruckAddAttr(String jaaId, CkCtConAddAttr TCkCtConAddAttr, CkJobTruck TCkJobTruck, String jaaValue,
			Character jaaStatus, Date jaaDtCreate, String jaaUidCreate, Date jaaDtLupd, String jaaUidLupd) {
		this.jaaId = jaaId;
		this.TCkCtConAddAttr = TCkCtConAddAttr;
		this.TCkJobTruck = TCkJobTruck;
		this.jaaValue = jaaValue;
		this.jaaStatus = jaaStatus;
		this.jaaDtCreate = jaaDtCreate;
		this.jaaUidCreate = jaaUidCreate;
		this.jaaDtLupd = jaaDtLupd;
		this.jaaUidLupd = jaaUidLupd;
	}

	public String getJaaId() {
		return jaaId;
	}

	public void setJaaId(String jaaId) {
		this.jaaId = jaaId;
	}

	public CkCtConAddAttr getTCkCtConAddAttr() {
		return TCkCtConAddAttr;
	}

	public void setTCkCtConAddAttr(CkCtConAddAttr tCkCtConAddAttr) {
		TCkCtConAddAttr = tCkCtConAddAttr;
	}

	public CkJobTruck getTCkJobTruck() {
		return TCkJobTruck;
	}

	public void setTCkJobTruck(CkJobTruck tCkJobTruck) {
		TCkJobTruck = tCkJobTruck;
	}

	public String getJaaValue() {
		return jaaValue;
	}

	public void setJaaValue(String jaaValue) {
		this.jaaValue = jaaValue;
	}

	public Character getJaaStatus() {
		return jaaStatus;
	}

	public void setJaaStatus(Character jaaStatus) {
		this.jaaStatus = jaaStatus;
	}

	public Date getJaaDtCreate() {
		return jaaDtCreate;
	}

	public void setJaaDtCreate(Date jaaDtCreate) {
		this.jaaDtCreate = jaaDtCreate;
	}

	public String getJaaUidCreate() {
		return jaaUidCreate;
	}

	public void setJaaUidCreate(String jaaUidCreate) {
		this.jaaUidCreate = jaaUidCreate;
	}

	public Date getJaaDtLupd() {
		return jaaDtLupd;
	}

	public void setJaaDtLupd(Date jaaDtLupd) {
		this.jaaDtLupd = jaaDtLupd;
	}

	public String getJaaUidLupd() {
		return jaaUidLupd;
	}

	public void setJaaUidLupd(String jaaUidLupd) {
		this.jaaUidLupd = jaaUidLupd;
	}

	@Override
	public int compareTo(CkJobTruckAddAttr o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}


}
