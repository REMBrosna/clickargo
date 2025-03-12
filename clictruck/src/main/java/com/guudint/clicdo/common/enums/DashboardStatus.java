package com.guudint.clicdo.common.enums;

public enum DashboardStatus {

	SUBMITTED("Submitted"), 
	PENDING_VERIFICATION("Pending Verification"), 
	ACTIVE("Active"), 
	NEW("New Jobs"),
	ONGOING("Ongoing Jobs"),
	READY("Ready"),
	VERIFIED("Pending Approval"),
	APPROVED("Pending Payment"),
	PAID("Paid"),
	USED("Used"),
	TOTAL("Total"),
	READY_REPORT("Ready Report"),
	WITHOUT_PDF("Without PDF"),
	ACTIVE_ACCN("Active Accounts"),
	SUSPEND_ACCN("Suspended Accounts"),
	PENDING_DOC_VERIFY("Pending"),
	DOC_VERIFIED("Verified");
	
	private String desc;

	private DashboardStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

}
