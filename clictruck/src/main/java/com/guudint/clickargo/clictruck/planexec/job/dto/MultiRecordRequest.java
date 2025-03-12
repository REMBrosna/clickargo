package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.util.ArrayList;
import java.util.List;

import com.guudint.clickargo.common.enums.JobActions;

public class MultiRecordRequest {

	private JobActions action;
	private String accType, role;
	private List<String> id;
	private String remarks;

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

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
