package com.guudint.clickargo.clictruck.common.dto;

public enum VehExtParamEnum {
	VEHICLE_MAINTENANCE("VEHICLE_MAINTENANCE"),
	EXP_DRIVER_LICENSE("Driver's License"),
	EXP_VPC_EXPIRY("VPC"),
	EXP_INSURANCE("Insurance");
	
	private String desc;
	
	private VehExtParamEnum(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
