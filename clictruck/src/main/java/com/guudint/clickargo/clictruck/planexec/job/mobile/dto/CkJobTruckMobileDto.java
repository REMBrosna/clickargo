package com.guudint.clickargo.clictruck.planexec.job.mobile.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.guudint.clickargo.clictruck.planexec.job.dto.CkJobTruck;
import com.guudint.clickargo.clictruck.planexec.job.model.TCkJobTruck;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class CkJobTruckMobileDto extends CkJobTruck {

	private static final long serialVersionUID = -3719173881243181512L;

	public static final String NOTIF_TMPLT_DELIVERING = "CKT_NTL_0052";
	public static final String NOTIF_TMPLT_30MINS_ARRIVAL = "CKT_NTL_0053";

	private boolean isGreen = false;
	private boolean isOrange = false;
	private boolean isRed = false;

	// This is just to store data so that frontend don't need to do anythhing as
	// mostly it is just readonly.
	private String shipmentType;
	private String jobState;
	private String jobId;

	// TODO to change in multiple trip in future
	private List<CkJobMTripDto> trips;

	private List<CkJobMTruckAddAttrDto> addAttrDto;

	public CkJobTruckMobileDto() {

	}

	public CkJobTruckMobileDto(TCkJobTruck entity) {
		super(entity);
	}

	public boolean isGreen() {
		return isGreen;
	}

	public void setGreen(boolean isGreen) {
		this.isGreen = isGreen;
	}

	public boolean isOrange() {
		return isOrange;
	}

	public void setOrange(boolean isOrange) {
		this.isOrange = isOrange;
	}

	public boolean isRed() {
		return isRed;
	}

	public void setRed(boolean isRed) {
		this.isRed = isRed;
	}

	public String getShipmentType() {
		return shipmentType;
	}

	public void setShipmentType(String shipmentType) {
		this.shipmentType = shipmentType;
	}

	public String getJobState() {
		return jobState;
	}

	public void setJobState(String jobState) {
		this.jobState = jobState;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public List<CkJobMTripDto> getTrips() {
		return trips;
	}

	public void setTrip(List<CkJobMTripDto> trips) {
		this.trips = trips;
	}

	public List<CkJobMTruckAddAttrDto> getAddAttrDto() {
		return addAttrDto;
	}

	public void setAddAttrDto(List<CkJobMTruckAddAttrDto> addAttrDto) {
		this.addAttrDto = addAttrDto;
	}

}
