package com.guudint.clickargo.clictruck.dsv.dto;

public enum DsvMstShipmentStateEnum {

	JOB_CREATED("JOB_CREATED"), 
	JOB_UPDATED("JOB_UPDATED"), 
	FAIL_TO_CREATE_JOB("FAIL_TO_CREATE_JOB"), 
	NEED_NOT_CREATE_JOB("NEED_NOT_CREATE_JOB"), 
	NEED_NOT_CREATE_JOB_MIGRATION("NEED_NOT_CREATE_JOB_MIGRATION"), 
	CONTENT_ERROR("CONTENT_ERROR"), 
	CONTENT_EMPTY("CONTENT_EMPTY");
	
	private String desc;

	private DsvMstShipmentStateEnum(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
