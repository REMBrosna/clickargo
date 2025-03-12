package com.guudint.clickargo.clictruck.planexec.job.mobile.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * This holds the mobile trip properties.
 */
public class CkJobMTripDto implements Serializable {

	private static final long serialVersionUID = 7286399908599833247L;
	
	private String id;
	private int seqNo;
	private String fromLocName;
	private String fromLocAddr;
	private String fromLocRemarks;
	private String cargoRecipient;
	private String toLocName;
	private String toLocAddr;
	private String toLocRemarks;
	private Date estPickupTime;
	private Date estDropOffTime;
	private Date jobStartTime;
	private Date pickedUpTime;
	private Date deliverStartTime;
	private Date jobFinishTime;
	private Character status;
	private String comment;

	// To location comment
	private String dropOffComments;

	// Comments/Instructions
	private String commentsInstruction;

	private List<CkMTripCargoDetails> cargos;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromLocName() {
		return fromLocName;
	}

	public void setFromLocName(String fromLocName) {
		this.fromLocName = fromLocName;
	}

	public String getFromLocAddr() {
		return fromLocAddr;
	}

	public void setFromLocAddr(String fromLocAddr) {
		this.fromLocAddr = fromLocAddr;
	}

	public String getToLocName() {
		return toLocName;
	}

	public void setToLocName(String toLocName) {
		this.toLocName = toLocName;
	}

	public String getToLocAddr() {
		return toLocAddr;
	}

	public void setToLocAddr(String toLocAddr) {
		this.toLocAddr = toLocAddr;
	}

	public Date getEstPickupTime() {
		return estPickupTime;
	}

	public void setEstPickupTime(Date estPickupTime) {
		this.estPickupTime = estPickupTime;
	}

	public Date getEstDropOffTime() {
		return estDropOffTime;
	}

	public void setEstDropOffTime(Date estDropOffTime) {
		this.estDropOffTime = estDropOffTime;
	}

	public Date getJobStartTime() {
		return jobStartTime;
	}

	public void setJobStartTime(Date jobStartTime) {
		this.jobStartTime = jobStartTime;
	}

	public Date getPickedUpTime() {
		return pickedUpTime;
	}

	public void setPickedUpTime(Date pickedUpTime) {
		this.pickedUpTime = pickedUpTime;
	}

	public Date getDeliverStartTime() {
		return deliverStartTime;
	}

	public void setDeliverStartTime(Date deliverStartTime) {
		this.deliverStartTime = deliverStartTime;
	}

	public Date getJobFinishTime() {
		return jobFinishTime;
	}

	public void setJobFinishTime(Date jobFinishTime) {
		this.jobFinishTime = jobFinishTime;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDropOffComments() {
		return dropOffComments;
	}

	public void setDropOffComments(String dropOffComments) {
		this.dropOffComments = dropOffComments;
	}

	public String getFromLocRemarks() {
		return fromLocRemarks;
	}

	public void setFromLocRemarks(String fromLocRemarks) {
		this.fromLocRemarks = fromLocRemarks;
	}

	public String getToLocRemarks() {
		return toLocRemarks;
	}

	public void setToLocRemarks(String toLocRemarks) {
		this.toLocRemarks = toLocRemarks;
	}

	public String getCommentsInstruction() {
		return commentsInstruction;
	}

	public void setCommentsInstruction(String commentsInstruction) {
		this.commentsInstruction = commentsInstruction;
	}

	public String getCargoRecipient() {
		return cargoRecipient;
	}

	public void setCargoRecipient(String cargoRecipient) {
		this.cargoRecipient = cargoRecipient;
	}

	/**
	 * @return the cargos
	 */
	public List<CkMTripCargoDetails> getCargos() {
		return cargos;
	}

	/**
	 * @param cargos the cargos to set
	 */
	public void setCargos(List<CkMTripCargoDetails> cargos) {
		this.cargos = cargos;
	}

	/**
	 * @return the seqNo
	 */
	public int getSeqNo() {
		return seqNo;
	}

	/**
	 * @param seqNo the seqNo to set
	 */
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	
	

}
