package com.guudint.clickargo.clictruck.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class ShipmentContract {
    private String jobId;
    private String contractId;
    private String shipmentRef;
    private String customerRef;
    private String loading;
    private String jobSubType;
    private Date bookingDate;
    private Date planDate;
    private String truckType;

    private Location pickUp;
    private List<Location> dropOffs;
    private String status;

    // Getters and Setters

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getShipmentRef() {
        return shipmentRef;
    }

    public void setShipmentRef(String shipmentRef) {
        this.shipmentRef = shipmentRef;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public String getLoading() {
        return loading;
    }

    public void setLoading(String loading) {
        this.loading = loading;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Date getPlanDate() {
        return planDate;
    }

    public void setPlanDate(Date planDate) {
        this.planDate = planDate;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public Location getPickUp() {
        return pickUp;
    }

    public void setPickUp(Location pickUp) {
        this.pickUp = pickUp;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobSubType() {
        return jobSubType;
    }

    public void setJobSubType(String jobSubType) {
        this.jobSubType = jobSubType;
    }

    public List<Location> getDropOffs() {
        return dropOffs;
    }

    public void setDropOffs(List<Location> dropOffs) {
        this.dropOffs = dropOffs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Location {

        @JsonProperty("locationName")
        private String locationName;

        @JsonProperty("locationDetails")
        private String locationDetails;

        @JsonProperty("datetime")
        private Date datetime;

        @JsonProperty("mobileNo")
        private String mobileNo;

        @JsonProperty("remark")
        private String remark;

        @JsonProperty("driverComment")
        private String driverComment;

        // Getters and Setters

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public String getLocationDetails() {
            return locationDetails;
        }

        public void setLocationDetails(String locationDetails) {
            this.locationDetails = locationDetails;
        }

        public Date getDatetime() {
            return datetime;
        }

        public void setDatetime(Date datetime) {
            this.datetime = datetime;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getDriverComment() {
            return driverComment;
        }

        public void setDriverComment(String driverComment) {
            this.driverComment = driverComment;
        }
    }
}
