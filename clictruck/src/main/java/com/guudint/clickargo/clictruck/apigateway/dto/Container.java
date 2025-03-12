package com.guudint.clickargo.clictruck.apigateway.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class Container implements Serializable {

    private String number;
    private String sealNumber;
    private String type;
    private String weight;
    private String weightUOM;
    private String volume;
    private String volumeUOM;
    private String size;
    private String noOfPacks;
    private String commodity;

    public Container() {
    }

    public Container(String number, String sealNumber, String type, String weight, String weightUOM, String volume, String volumeUOM, String size, String noOfPacks, String commodity) {
        this.number = number;
        this.sealNumber = sealNumber;
        this.type = type;
        this.weight = weight;
        this.weightUOM = weightUOM;
        this.volume = volume;
        this.volumeUOM = volumeUOM;
        this.size = size;
        this.noOfPacks = noOfPacks;
        this.commodity = commodity;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSealNumber() {
        return sealNumber;
    }

    public void setSealNumber(String sealNumber) {
        this.sealNumber = sealNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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

    public String getVolumeUOM() {
        return volumeUOM;
    }

    public void setVolumeUOM(String volumeUOM) {
        this.volumeUOM = volumeUOM;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getNoOfPacks() {
        return noOfPacks;
    }

    public void setNoOfPacks(String noOfPacks) {
        this.noOfPacks = noOfPacks;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }
}
