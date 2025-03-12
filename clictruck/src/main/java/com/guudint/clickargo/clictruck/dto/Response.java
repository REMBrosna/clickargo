package com.guudint.clickargo.clictruck.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.guudint.clickargo.clictruck.util.ObjectUtil;

public class Response {

    @JsonFormat(timezone = "GMT+7")
    private Date timestamp;
    private int status;
    private String error, success;
    private ArrayList<Object> data;

    public List<Object> getData() {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        return this.data;
    }

    public Date getTimestamp() {
        if (this.timestamp == null) {
            this.timestamp = new Date();
        }
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuccess() {
        return this.success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setData(ArrayList<Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return ObjectUtil.jsonToString(this);
    }
}
