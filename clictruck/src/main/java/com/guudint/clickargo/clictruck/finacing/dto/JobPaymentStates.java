package com.guudint.clickargo.clictruck.finacing.dto;

public enum JobPaymentStates {
	NEW("NEW"),
	PAYING("PYG"),
	PENDING("PND"),
	PAID("PAD"),
	VERIFIED("VERIFIED"),
	APPROVED("APPROVED"),
	CANCELLED("CAN"),
	FAILED("FAL"),
	TERMINATED("TERMINATED");
	
	private String altCode;
	
	JobPaymentStates(String altCode) {
		this.altCode = altCode;
	}
	
	public String getAltCode() {
		return this.altCode;
	}
	
	
}
