package com.guudint.clickargo.clictruck.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class LocationDetails implements Serializable {
    @JsonProperty("locationName")
    private String locationName;

    @JsonProperty("locationDetails")
    private String locationDetails;

    @JsonProperty("datetime")
    private String datetime;

    @JsonProperty("mobileNo")
    private String mobileNo;

    @JsonProperty("remark")
    private String remark;

    @JsonProperty("driverComment")
    private String driverComment;

    @JsonProperty("cargos")
    private List<Cargo> cargos;

    public LocationDetails() {
    }

    public LocationDetails(String locationName, String locationDetails, String datetime, String mobileNo, String remark, String driverComment, List<Cargo> cargos) {
        this.locationName = locationName;
        this.locationDetails = locationDetails;
        this.datetime = datetime;
        this.mobileNo = mobileNo;
        this.remark = remark;
        this.driverComment = driverComment;
        this.cargos = cargos;
    }

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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
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

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }
}
