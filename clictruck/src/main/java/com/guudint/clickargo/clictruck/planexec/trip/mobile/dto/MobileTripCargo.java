package com.guudint.clickargo.clictruck.planexec.trip.mobile.dto;

import java.util.List;

import com.guudint.clickargo.master.enums.FormActions;

public class MobileTripCargo {

	public static final String PREFIX_ID = "CTAT";

	private FormActions action;
//	private CkJobTruckMobileDto ckJobTruck;
	// Associated tripId of this trip cargo
	private String tripId;
	private String photoComment;
	private String truckJobId;

	// flag to determine if it's multi-drop off,
	private boolean multiDropOff;
	// place holder for other dropoff trip location
	private List<String> dropOffTrips;

	public FormActions getAction() {
		return action;
	}

	public void setAction(FormActions action) {
		this.action = action;
	}

	/**
	 * @return the truckJobId
	 */
	public String getTruckJobId() {
		return truckJobId;
	}

	/**
	 * @param truckJobId the truckJobId to set
	 */
	public void setTruckJobId(String truckJobId) {
		this.truckJobId = truckJobId;
	}

	/**
	 * @return the tripId
	 */
	public String getTripId() {
		return tripId;
	}

	/**
	 * @param tripId the tripId to set
	 */
	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	/**
	 * @return the photoComment
	 */
	public String getPhotoComment() {
		return photoComment;
	}

	/**
	 * @param photoComment the photoComment to set
	 */
	public void setPhotoComment(String photoComment) {
		this.photoComment = photoComment;
	}

	/**
	 * @return the multiDropOff
	 */
	public boolean isMultiDropOff() {
		return multiDropOff;
	}

	/**
	 * @param multiDropOff the multiDropOff to set
	 */
	public void setMultiDropOff(boolean multiDropOff) {
		this.multiDropOff = multiDropOff;
	}

	/**
	 * @return the dropOffTrips
	 */
	public List<String> getDropOffTrips() {
		return dropOffTrips;
	}

	/**
	 * @param dropOffTrips the dropOffTrips to set
	 */
	public void setDropOffTrips(List<String> dropOffTrips) {
		this.dropOffTrips = dropOffTrips;
	}


}