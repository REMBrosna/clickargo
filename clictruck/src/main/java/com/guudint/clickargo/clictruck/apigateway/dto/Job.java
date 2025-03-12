package com.guudint.clickargo.clictruck.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class Job implements Serializable {
    private String jobId;
    private String status;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contractId;
    private Contract contract;
    private String planDate;
    private String bookingDate;
    private String deliveredDate;
    private String shipmentRef;
    private String customerRef;
    private String loading;
    private String jobSubType;
    private String truckType;
    private LocationDetails pickUp;

    private List<LocationDetails> destinations;
    private JobDetails details;
    private String epodId;
    private Driver driver;
    private Truck truck;
    private String rejReason;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String accnId;

    public Job() {
    }

    public Job(String jobId, String status, String contractId, Contract contract, String planDate, String bookingDate, String deliveredDate, String shipmentRef, String loading, String jobSubType, String truckType, LocationDetails pickUp, String customerRef, List<LocationDetails> destinations, JobDetails details, String epodId, Driver driver, Truck truck, String rejReason) {
        this.jobId = jobId;
        this.status = status;
        this.contractId = contractId;
        this.contract = contract;
        this.planDate = planDate;
        this.bookingDate = bookingDate;
        this.deliveredDate = deliveredDate;
        this.shipmentRef = shipmentRef;
        this.loading = loading;
        this.jobSubType = jobSubType;
        this.truckType = truckType;
        this.pickUp = pickUp;
        this.customerRef = customerRef;
        this.destinations = destinations;
        this.details = details;
        this.epodId = epodId;
        this.driver = driver;
        this.truck = truck;
        this.rejReason = rejReason;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(String deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public String getShipmentRef() {
        return shipmentRef;
    }

    public void setShipmentRef(String shipmentRef) {
        this.shipmentRef = shipmentRef;
    }
    public String getLoading() {
        return loading;
    }

    public void setLoading(String loading) {
        this.loading = loading;
    }

    public String getJobSubType() {
        return jobSubType;
    }

    public void setJobSubType(String jobSubType) {
        this.jobSubType = jobSubType;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public LocationDetails getPickUp() {
        return pickUp;
    }

    public void setPickUp(LocationDetails pickUp) {
        this.pickUp = pickUp;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public List<LocationDetails> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<LocationDetails> destinations) {
        this.destinations = destinations;
    }

    public JobDetails getDetails() {
        return details;
    }

    public void setDetails(JobDetails details) {
        this.details = details;
    }

    public String getEpodId() {
        return epodId;
    }

    public void setEpodId(String epodId) {
        this.epodId = epodId;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public String getRejReason() {
        return rejReason;
    }

    public void setRejReason(String rejReason) {
        this.rejReason = rejReason;
    }

    public String getAccnId() {
        return accnId;
    }

    public void setAccnId(String accnId) {
        this.accnId = accnId;
    }
}
