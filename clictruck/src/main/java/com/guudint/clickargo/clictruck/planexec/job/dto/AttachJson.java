package com.guudint.clickargo.clictruck.planexec.job.dto;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guudint.clickargo.clictruck.planexec.trip.dto.CkCtTrip;
import com.vcc.camelone.common.COAbstractEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachJson extends COAbstractEntity<AttachJson> {

	private static final long serialVersionUID = 1L;

	// Attributes
	////////////////////
	private String id;
	private String tripId;
	private CkCtTrip TCkCtTrip;
	private String locFrom;
	private String locTo;
	private String fileType;
	private String fileName;
	private char status;
	private Date createdAt;
	private int seq;
	
	// Constructors
	///////////////
	public AttachJson() {

	}

	// Static Methods
	///////////////////
	public static AttachJson convertFromJson(String jsonStr)
			throws JsonParseException, JsonMappingException, IOException {

		return (new ObjectMapper()).readValue(jsonStr, AttachJson.class);
	}

	// Override Methods
	///////////////////
	@Override
	public int compareTo(AttachJson arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the tCkCtTrip
	 */
	@JsonProperty("TCkCtTrip")
	public CkCtTrip getTCkCtTrip() {
		return TCkCtTrip;
	}

	/**
	 * @param tCkCtTrip the tCkCtTrip to set
	 */
	public void setTCkCtTrip(CkCtTrip tCkCtTrip) {
		TCkCtTrip = tCkCtTrip;
	}

	/**
	 * @return the locFrom
	 */
	public String getLocFrom() {
		return locFrom;
	}

	/**
	 * @param locFrom the locFrom to set
	 */
	public void setLocFrom(String locFrom) {
		this.locFrom = locFrom;
	}

	/**
	 * @return the locTo
	 */
	public String getLocTo() {
		return locTo;
	}

	/**
	 * @param locTo the locTo to set
	 */
	public void setLocTo(String locTo) {
		this.locTo = locTo;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the status
	 */
	public char getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(char status) {
		this.status = status;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the seq
	 */
	public int getSeq() {
		return seq;
	}

	/**
	 * @param seq the seq to set
	 */
	public void setSeq(int seq) {
		this.seq = seq;
	}

}
