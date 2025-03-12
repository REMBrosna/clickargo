package com.guudint.clickargo.clictruck.master.dto;

public class MonitoringType {
	
	private String code;
	private String desc;
	
	public MonitoringType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MonitoringType(String code, String desc) {
		super();
		this.code = code;
		this.desc = desc;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
