package com.guudint.clickargo.clictruck.planexec.job.mobile.dto;

import java.io.Serializable;

public class CkJobMTruckAddAttrDto implements Serializable {

	private static final long serialVersionUID = -7968452379043733509L;
	private String label;
	private String value;

	public CkJobMTruckAddAttrDto() {
	}

	public CkJobMTruckAddAttrDto(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
