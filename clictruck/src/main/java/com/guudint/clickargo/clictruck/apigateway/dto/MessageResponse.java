package com.guudint.clickargo.clictruck.apigateway.dto;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class MessageResponse {
    private String message;
    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
