package com.guudint.clickargo.clictruck.planexec.job.event;


import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.job.event.AbstractJobEvent;

public class TruckJobCreateEvent extends AbstractJobEvent<CkJobTruck> {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -7236816614328868694L;

	// Constructors
	///////////////
	/**
	 * @param source
	 * @param eventType
	 */
	public TruckJobCreateEvent(Object source, JobEvent jobEvent, CkJobTruck dto) {
		super(source);
		this.jobEvent = jobEvent;
		this.dto = dto;
	}		

}
