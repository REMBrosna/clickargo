package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.ArrayList;
import java.util.List;

import com.guudint.clickargo.clictruck.common.dto.CkCtDrv;
import com.guudint.clickargo.clictruck.common.dto.CkCtVeh;
import com.guudint.clickargo.common.enums.JobActions;

public class MultiAssignDrvVeh {
	private JobActions action;
	private String accType, role;
	private List<String> id;
	private CkCtDrv ckCtDrv;
	private CkCtVeh ckCtVeh;

	public JobActions getAction() {
		return action;
	}

	public void setAction(JobActions action) {
		this.action = action;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getId() {
		if (id == null) {
			id = new ArrayList<>();
		}
		return id;
	}

	public void setId(List<String> id) {
		this.id = id;
	}

	public CkCtDrv getCkCtDrv() {
		return ckCtDrv;
	}

	public void setCkCtDrv(CkCtDrv ckCtDrv) {
		this.ckCtDrv = ckCtDrv;
	}

	public CkCtVeh getCkCtVeh() {
		return ckCtVeh;
	}

	public void setCkCtVeh(CkCtVeh ckCtVeh) {
		this.ckCtVeh = ckCtVeh;
	}

	
}
