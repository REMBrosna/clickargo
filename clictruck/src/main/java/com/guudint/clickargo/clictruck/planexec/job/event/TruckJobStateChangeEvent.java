package com.guudint.clickargo.clictruck.planexec.job.event;

import org.springframework.context.ApplicationEvent;

import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.common.enums.JobActions;
import com.vcc.camelone.cac.model.Principal;

public class TruckJobStateChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = -4372777417926306052L;

	private JobActions actions;
	private CkJobTruck dto;
	private Principal principal;

	public TruckJobStateChangeEvent(Object source, JobActions actions, CkJobTruck dto, Principal principal) {
		super(source);
		this.actions = actions;
		this.dto = dto;
		this.principal = principal;
	}

	public JobActions getActions() {
		return actions;
	}

	public void setActions(JobActions actions) {
		this.actions = actions;
	}

	public CkJobTruck getDto() {
		return dto;
	}

	public void setDto(CkJobTruck dto) {
		this.dto = dto;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

}
