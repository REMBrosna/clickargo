package com.guudint.clickargo.clictruck.apigateway.dto;

import java.io.Serializable;

public class Chasis implements Serializable {
    private String size;
    private String number;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
