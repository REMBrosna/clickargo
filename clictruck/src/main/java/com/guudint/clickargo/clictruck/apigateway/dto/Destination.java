package com.guudint.clickargo.clictruck.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class Destination {
    @JsonProperty("location_name")
    private String locationName;
    private String name;
    @JsonProperty("building_name")
    private String buildingName;
    @JsonProperty("unit_number")
    private String unitNumber;
    @JsonProperty("postal_code")
    private String postalCode;
    private String datetime;
    private String remark;
    private ArrayList<Cargo> cargos;

    public Destination() {
    }


    public Destination(String locationName, String name, String buildingName, String unitNumber, String postalCode, String datetime, String remark, ArrayList<Cargo> cargos) {
        this.locationName = locationName;
        this.name = name;
        this.buildingName = buildingName;
        this.unitNumber = unitNumber;
        this.postalCode = postalCode;
        this.datetime = datetime;
        this.remark = remark;
        this.cargos = cargos;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ArrayList<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(ArrayList<Cargo> cargos) {
        this.cargos = cargos;
    }
}
