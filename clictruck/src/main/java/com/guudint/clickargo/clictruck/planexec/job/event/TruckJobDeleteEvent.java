package com.guudint.clickargo.clictruck.planexec.job.event;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.job.event.AbstractJobEvent;

public class TruckJobDeleteEvent extends AbstractJobEvent<CkJobTruck> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 9048244929738148689L;

	// Constructors
	///////////////
	/**
	 * @param source
	 * @param eventType
	 */
	public TruckJobDeleteEvent(Object source, JobEvent jobEvent, CkJobTruck dto) {
		super(source);
		this.jobEvent = jobEvent;
		this.dto = dto;
	}		

}
