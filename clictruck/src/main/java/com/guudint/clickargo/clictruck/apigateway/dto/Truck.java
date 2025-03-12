package com.guudint.clickargo.clictruck.apigateway.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
@JsonPropertyOrder({
        "id", "companyId", "type", "licenseNo", "class", "length", "width", "height",
        "sizeUOM", "maxWeight", "weightUOM", "volume", "remark", "dept", "chasis"
})
public class Truck implements Serializable {
    private String id;
    private String companyId;
    private String type;
    private String licenseNo;
    @JsonProperty("class")
    private byte clazz;
    private String length;
    private String width;
    private String height;
    private String sizeUOM;
    private String maxWeight;
    private String weightUOM;
    private String volume;
    private String remark;
    private String dept;
    private Chasis chasis;

    // Default Constructor
    public Truck() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public byte getClazz() {
        return clazz;
    }

    public void setClazz(byte clazz) {
        this.clazz = clazz;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSizeUOM() {
        return sizeUOM;
    }

    public void setSizeUOM(String sizeUOM) {
        this.sizeUOM = sizeUOM;
    }

    public String getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(String maxWeight) {
        this.maxWeight = maxWeight;
    }

    public String getWeightUOM() {
        return weightUOM;
    }

    public void setWeightUOM(String weightUOM) {
        this.weightUOM = weightUOM;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public Chasis getChasis() {
        return chasis;
    }

    public void setChasis(Chasis chasis) {
        this.chasis = chasis;
    }
}

