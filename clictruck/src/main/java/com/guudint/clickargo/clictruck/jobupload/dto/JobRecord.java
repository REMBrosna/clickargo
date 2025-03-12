package com.guudint.clickargo.clictruck.jobupload.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobRecord implements Serializable {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = 1L;
	
	private int rowId; // from 1;
	
	private String contractId;
	private String jobSubType;
	private String country;
	private String startLoc;
	private String startLocAddress;
	private String endLoc;
	private String endLocAddress;
	private Date dateOfDelivery;
	private Date timeOfDelivery;

	private Date planDate;
	private Date bookingDate;
	private String loading;
	private String emailNotification;
	private String description;
	private String paymentMethod;
	private String CargoType;
	private String CargoWeight;
	private String cargoTruckType;
	private String cargoOwner;

	private String linkingNumber;
	private String driverName;
	private String truckPlateNo;
	private String dropoffRemark;
	private String shipmentRefNo;
	private String jobCustomerRef;
	private String fromLocDateTime;
	private String fromLocMobileNumber;
	private String fromLocRemarks;

	private String toLocDateTime;
	private String toLocCargoRec;
	private String toLocMobileNumber;
	private String toLocRemark;

	// Cargo Details
	private Double cgCargoLength;
	private Double cgCargoWidth;
	private Double cgCargoHeight;
	private String cgCargoSizeUom;
	private Double cgCargoQty;
	private String cargoQtyUom;
	private Double cgCargoWeight;
	private String cgCargoWeightUom;
	private String cgCargoMarksNo;
	private Double cgCargoVolume;
	private String cgCargoVolumeUom;
	private String cgCargoDesc;
	private String cgCargoSpecialInstn;

	
	private JobRecord parentJobRecord = null; // for Muliti-Drop;
	
	private Map<String, Object> extendAttrs = new HashMap<>();
	private List<JobRecord> multiDrops = new ArrayList<>();

	@Override
	public String toString() {
		return "JobRecord [rowId=" + rowId + ", contractId=" + contractId + ", country=" + country + ", startLoc="
				+ startLoc + ", endLoc=" + endLoc + ", dateOfDelivery=" + dateOfDelivery + ", timeOfDelivery="
				+ timeOfDelivery + ", planDate=" + planDate
				+ ", bookingDate=" + bookingDate + ", loading=" + loading + ", emailNotification=" + emailNotification
				+ ", description=" + description + ", paymentMethod=" + paymentMethod + ", CargoType=" + CargoType
				+ ", CargoWeight=" + CargoWeight + ", cargoTruckType=" + cargoTruckType + ", linkingNumber="
				+ linkingNumber + ", driverName=" + driverName + ", truckPlateNo=" + truckPlateNo + ", parentJobRecord=" + parentJobRecord
				+ ", multiDrops=" + multiDrops + "]";
	}

	//////////////////
	public Date computeScheduleDateTime() {

        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(this.getDateOfDelivery());

        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(this.getTimeOfDelivery());

        Calendar scheduleCal = Calendar.getInstance();
        scheduleCal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
        scheduleCal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
        scheduleCal.set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH));


        scheduleCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        scheduleCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        scheduleCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        scheduleCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));

        return scheduleCal.getTime();
	}

	//////////////////////////
	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStartLoc() {
		return startLoc;
	}

	public void setStartLoc(String startLoc) {
		this.startLoc = startLoc;
	}

	public String getEndLoc() {
		return endLoc;
	}

	public void setEndLoc(String endLoc) {
		this.endLoc = endLoc;
	}

	public Date getDateOfDelivery() {
		return dateOfDelivery;
	}

	public void setDateOfDelivery(Date dateOfDelivery) {
		this.dateOfDelivery = dateOfDelivery;
	}

	public Date getTimeOfDelivery() {
		return timeOfDelivery;
	}

	public void setTimeOfDelivery(Date timeOfDelivery) {
		this.timeOfDelivery = timeOfDelivery;
	}

	public Date getPlanDate() {
		return planDate;
	}

	public void setPlanDate(Date planDate) {
		this.planDate = planDate;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getLoading() {
		return loading;
	}

	public void setLoading(String loading) {
		this.loading = loading;
	}

	public String getEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(String emailNotification) {
		this.emailNotification = emailNotification;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCargoType() {
		return CargoType;
	}

	public void setCargoType(String cargoType) {
		CargoType = cargoType;
	}

	public String getCargoWeight() {
		return CargoWeight;
	}

	public void setCargoWeight(String cargoWeight) {
		CargoWeight = cargoWeight;
	}

	public String getCargoTruckType() {
		return cargoTruckType;
	}

	public void setCargoTruckType(String cargoTruckType) {
		this.cargoTruckType = cargoTruckType;
	}

	public String getLinkingNumber() {
		return linkingNumber;
	}

	public void setLinkingNumber(String linkingNumber) {
		this.linkingNumber = linkingNumber;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getTruckPlateNo() {
		return truckPlateNo;
	}

	public void setTruckPlateNo(String truckPlateNo) {
		this.truckPlateNo = truckPlateNo;
	}

	public List<JobRecord> getMultiDrops() {
		return multiDrops;
	}

	public void setMultiDrops(List<JobRecord> multiDrops) {
		this.multiDrops = multiDrops;
	}

	public JobRecord getParentJobRecord() {
		return parentJobRecord;
	}

	public void setParentJobRecord(JobRecord parentJobRecord) {
		this.parentJobRecord = parentJobRecord;
	}

	public Map<String, Object> getExtendAttrs() {
		return extendAttrs;
	}

	public void setExtendAttrs(Map<String, Object> extendAttrs) {
		this.extendAttrs = extendAttrs;
	}

	public String getDropoffRemark() {
		return dropoffRemark;
	}

	public void setDropoffRemark(String dropoffRemark) {
		this.dropoffRemark = dropoffRemark;
	}

	public String getShipmentRefNo() {
		return shipmentRefNo;
	}

	public void setShipmentRefNo(String shipmentRefNo) {
		this.shipmentRefNo = shipmentRefNo;
	}

	public Double getCgCargoLength() {
		return cgCargoLength;
	}

	public void setCgCargoLength(Double cgCargoLength) {
		this.cgCargoLength = cgCargoLength;
	}

	public Double getCgCargoWidth() {
		return cgCargoWidth;
	}

	public void setCgCargoWidth(Double cgCargoWidth) {
		this.cgCargoWidth = cgCargoWidth;
	}

	public Double getCgCargoHeight() {
		return cgCargoHeight;
	}

	public void setCgCargoHeight(Double cgCargoHeight) {
		this.cgCargoHeight = cgCargoHeight;
	}

	public Double getCgCargoQty() {
		return cgCargoQty;
	}

	public void setCgCargoQty(Double cgCargoQty) {
		this.cgCargoQty = cgCargoQty;
	}

	public Double getCgCargoWeight() {
		return cgCargoWeight;
	}

	public void setCgCargoWeight(Double cgCargoWeight) {
		this.cgCargoWeight = cgCargoWeight;
	}

	public String getCgCargoMarksNo() {
		return cgCargoMarksNo;
	}

	public void setCgCargoMarksNo(String cgCargoMarksNo) {
		this.cgCargoMarksNo = cgCargoMarksNo;
	}

	public Double getCgCargoVolume() {
		return cgCargoVolume;
	}

	public void setCgCargoVolume(Double cgCargoVolume) {
		this.cgCargoVolume = cgCargoVolume;
	}

	public String getCgCargoDesc() {
		return cgCargoDesc;
	}

	public void setCgCargoDesc(String cgCargoDesc) {
		this.cgCargoDesc = cgCargoDesc;
	}

	public String getCgCargoSpecialInstn() {
		return cgCargoSpecialInstn;
	}

	public void setCgCargoSpecialInstn(String cgCargoSpecialInstn) {
		this.cgCargoSpecialInstn = cgCargoSpecialInstn;
	}

	public String getJobCustomerRef() {
		return jobCustomerRef;
	}

	public void setJobCustomerRef(String jobCustomerRef) {
		this.jobCustomerRef = jobCustomerRef;
	}

	public String getFromLocDateTime() {
		return fromLocDateTime;
	}

	public void setFromLocDateTime(String fromLocDateTime) {
		this.fromLocDateTime = fromLocDateTime;
	}

	public String getFromLocMobileNumber() {
		return fromLocMobileNumber;
	}

	public void setFromLocMobileNumber(String fromLocMobileNumber) {
		this.fromLocMobileNumber = fromLocMobileNumber;
	}

	public String getFromLocRemarks() {
		return fromLocRemarks;
	}

	public void setFromLocRemarks(String fromLocRemarks) {
		this.fromLocRemarks = fromLocRemarks;
	}

	public String getToLocDateTime() {
		return toLocDateTime;
	}

	public void setToLocDateTime(String toLocDateTime) {
		this.toLocDateTime = toLocDateTime;
	}

	public String getToLocMobileNumber() {
		return toLocMobileNumber;
	}

	public void setToLocMobileNumber(String toLocMobileNumber) {
		this.toLocMobileNumber = toLocMobileNumber;
	}

	public String getToLocRemark() {
		return toLocRemark;
	}

	public void setToLocRemark(String toLocRemark) {
		this.toLocRemark = toLocRemark;
	}

	public String getStartLocAddress() {
		return startLocAddress;
	}

	public void setStartLocAddress(String startLocAddress) {
		this.startLocAddress = startLocAddress;
	}

	public String getEndLocAddress() {
		return endLocAddress;
	}

	public void setEndLocAddress(String endLocAddress) {
		this.endLocAddress = endLocAddress;
	}

	public String getCargoOwner() {
		return cargoOwner;
	}

	public void setCargoOwner(String cargoOwner) {
		this.cargoOwner = cargoOwner;
	}

	public String getToLocCargoRec() {
		return toLocCargoRec;
	}

	public void setToLocCargoRec(String toLocCargoRec) {
		this.toLocCargoRec = toLocCargoRec;
	}

	public String getJobSubType() {
		return jobSubType;
	}

	public void setJobSubType(String jobSubType) {
		this.jobSubType = jobSubType;
	}

	public String getCgCargoSizeUom() {
		return cgCargoSizeUom;
	}

	public void setCgCargoSizeUom(String cgCargoSizeUom) {
		this.cgCargoSizeUom = cgCargoSizeUom;
	}

	public String getCargoQtyUom() {
		return cargoQtyUom;
	}

	public void setCargoQtyUom(String cargoQtyUom) {
		this.cargoQtyUom = cargoQtyUom;
	}

	public String getCgCargoWeightUom() {
		return cgCargoWeightUom;
	}

	public void setCgCargoWeightUom(String cgCargoWeightUom) {
		this.cgCargoWeightUom = cgCargoWeightUom;
	}

	public String getCgCargoVolumeUom() {
		return cgCargoVolumeUom;
	}

	public void setCgCargoVolumeUom(String cgCargoVolumeUom) {
		this.cgCargoVolumeUom = cgCargoVolumeUom;
	}
}
