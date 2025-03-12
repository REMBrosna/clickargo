package com.guudint.clickargo.clictruck.common.dto;

import com.guudint.clickargo.clictruck.common.model.TCkCtDrv;

public class CkCtDrvAvail extends CkCtDrv {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 6306775072643131036L;

	// Attributes
	/////////////
	private Integer jobsAllocated;
	private Integer jobsRemaining;
	private Integer jobsCompleted;

	// Constructors
	///////////////
	public CkCtDrvAvail() {
	}
	
	public CkCtDrvAvail(TCkCtDrv entity) {
		super(entity);
	}

	public CkCtDrvAvail(Integer jobsAllocated, Integer jobsRemaining, Integer jobsCompleted) {
		super();
		this.jobsAllocated = jobsAllocated;
		this.jobsRemaining = jobsRemaining;
		this.jobsCompleted = jobsCompleted;
	}

	public Integer getJobsAllocated() {
		return jobsAllocated;
	}

	public void setJobsAllocated(Integer jobsAllocated) {
		this.jobsAllocated = jobsAllocated;
	}

	public Integer getJobsRemaining() {
		return jobsRemaining;
	}

	public void setJobsRemaining(Integer jobsRemaining) {
		this.jobsRemaining = jobsRemaining;
	}

	public Integer getJobsCompleted() {
		return jobsCompleted;
	}

	public void setJobsCompleted(Integer jobsCompleted) {
		this.jobsCompleted = jobsCompleted;
	}
}
