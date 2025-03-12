package com.guudint.clickargo.clictruck.apigateway.dto;

import java.io.Serializable;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class Cargo implements Serializable {
    private String id;
    private String type;
    private String volume;
    private String volumeUOM;
    private String weight;
    private String weightUOM;
    private String length;
    private String width;
    private String height;
    private String sizeUOM;
    private String quantity;
    private String quantityUOM;
    private String marksAndNo;
    private String description;
    private String specialInstruction;

    public Cargo() {
    }

    public Cargo(String id, String type, String volume, String volumeUOM, String weight, String weightUOM, String length, String width, String height, String sizeUOM, String quantity, String quantityUOM, String marksAndNo, String description, String specialInstruction) {
        this.id = id;
        this.type = type;
        this.volume = volume;
        this.volumeUOM = volumeUOM;
        this.weight = weight;
        this.weightUOM = weightUOM;
        this.length = length;
        this.width = width;
        this.height = height;
        this.sizeUOM = sizeUOM;
        this.quantity = quantity;
        this.quantityUOM = quantityUOM;
        this.marksAndNo = marksAndNo;
        this.description = description;
        this.specialInstruction = specialInstruction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantityUOM() {
        return quantityUOM;
    }

    public void setQuantityUOM(String quantityUOM) {
        this.quantityUOM = quantityUOM;
    }

    public String getVolumeUOM() {
        return volumeUOM;
    }

    public void setVolumeUOM(String volumeUOM) {
        this.volumeUOM = volumeUOM;
    }

    public String getMarksAndNo() {
        return marksAndNo;
    }

    public void setMarksAndNo(String marksAndNo) {
        this.marksAndNo = marksAndNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }
}
