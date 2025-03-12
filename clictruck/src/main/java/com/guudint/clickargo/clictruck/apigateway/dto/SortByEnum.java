package com.guudint.clickargo.clictruck.apigateway.dto;

public enum SortByEnum {
    CREATED_DATE("createdDate"),
    LAST_UPDATED_DATE("lastUpdatedDate"),
    JOB_NO("jobNo");

    private final String value;

    SortByEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValid(String value) {
        for (SortByEnum sortBy : values()) {
            if (sortBy.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
