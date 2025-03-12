package com.guudint.clickargo.clictruck.sage.export.dto;

public class CkCtSageExportBookingJson extends CkCtSageExportJson{

	public ConsumerProviderBooking consumer;
	public ConsumerProviderBooking provider;

	// Constructor
	public CkCtSageExportBookingJson() {
		super();
	}

	public CkCtSageExportBookingJson(String service, String type, String reference, String dateTime,
			ConsumerProviderBooking consumer, ConsumerProviderBooking provider) {
		super();

		this.service = service;
		this.type = type;
		this.reference = reference;
		this.dateTime = dateTime;
		this.consumer = consumer;
		this.provider = provider;
	}

	@Override
	public String toString() {
		return "CkCtSageExportBookingJson [service=" + service + ", type=" + type + ", reference=" + reference
				+ ", dateTime=" + dateTime + ", consumer=" + consumer + ", provider=" + provider + "]";
	}

}
