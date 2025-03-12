package com.guudint.clickargo.clictruck.apigateway.dto;

import java.io.Serializable;

/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class ServiceResponse implements Serializable {
    private Object data;

    public ServiceResponse() {
    }

    public ServiceResponse(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}

