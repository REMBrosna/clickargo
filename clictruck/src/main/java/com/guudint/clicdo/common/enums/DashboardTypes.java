package com.guudint.clicdo.common.enums;

public enum DashboardTypes {

	TRUCK_JOBS("Trucking Jobs"),
	DRIVER_AVAILABILITY("Driver Availability"),
	BILLED_JOBS("Billed Jobs"),
	JOB_BILLING("Job Billing"),
	VERIFIED_JOBS("Verified Jobs"),
	APPROVED_JOBS("Approved Jobs"),
	PENDING_OUT_PAYMENTS("Pending Payments"),
	JOB_PAYMENTS("Job Payments"),
	SEQUENCE("Tax Sequence"),
	REPORTS("Tax Report"),
	INVOICES("Tax Invoices"),
	SUSPENSION("Account Suspension"),
	RESUMPTION("Account Resumption"),
	DOCUMENT_VERIFICATIONS("Document Verifications"),
	MOBILE_NEW("New"),
	MOBILE_PAUSED("Paused"),
	TRUCK_RENTAL("Rentals"),
	LEASE_APPLICATION("Lease Application"),
	TRUCK_TRACKING("Truck Tracking"),
	TRACKING("Tracking");

	private String desc;

	private DashboardTypes(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

}
