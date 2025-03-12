package com.guudint.clickargo.clictruck.apigateway.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class Token {
    private String plainTextToken;
    private String expiresAt;

    public String getPlainTextToken() {
        return plainTextToken;
    }

    public void setPlainTextToken(String plainTextToken) {
        this.plainTextToken = plainTextToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}
