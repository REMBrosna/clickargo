package com.guudint.clickargo.clictruck.planexec.trip.mobile.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripAttachment {
	private String action;
	private String typeData;
	private String truckJobId;
	private String tlocCargoRec;
	private List<HashMap<String, String>> listData;
	private Map<String, List<Map<String, String>>> imageData;
	// Associated tripId of this attachment.
	private String tripId;
	// Place holder for comments associated to the attachment
	private String comment;

	// Flag to determine if to update the job. This is for multi-dropoff
	private boolean updateTruckJob;
	private boolean multiDrop;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTypeData() {
		return typeData;
	}

	public void setTypeData(String typeData) {
		this.typeData = typeData;
	}

	public List<HashMap<String, String>> getListData() {
		return listData;
	}

	public void setListData(List<HashMap<String, String>> listData) {
		this.listData = listData;
	}

	public Map<String, List<Map<String, String>>> getImageData() {
		return imageData;
	}

	public void setImageData(Map<String, List<Map<String, String>>> imageData) {
		this.imageData = imageData;
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
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
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
	 * @return the updateTruckJob
	 */
	public boolean isUpdateTruckJob() {
		return updateTruckJob;
	}

	/**
	 * @param updateTruckJob the updateTruckJob to set
	 */
	public void setUpdateTruckJob(boolean updateTruckJob) {
		this.updateTruckJob = updateTruckJob;
	}

	/**
	 * @return the multiDrop
	 */
	public boolean isMultiDrop() {
		return multiDrop;
	}

	/**
	 * @param multiDrop the multiDrop to set
	 */
	public void setMultiDrop(boolean multiDrop) {
		this.multiDrop = multiDrop;
	}

	public String getTlocCargoRec() {
		return tlocCargoRec;
	}

	public void setTlocCargoRec(String tlocCargoRec) {
		this.tlocCargoRec = tlocCargoRec;
	}
}