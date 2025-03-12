package com.guudint.clickargo.clictruck.planexec.job.event;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.job.event.AbstractJobEvent;

public class TruckJobSubmitEvent extends AbstractJobEvent<CkJobTruck>{

	private static final long serialVersionUID = -5988819342399056501L;

	public TruckJobSubmitEvent(Object source, JobEvent jobEvent, CkJobTruck dto) {
		super(source, jobEvent, dto);
	}

}
