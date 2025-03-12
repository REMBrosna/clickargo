package com.guudint.clickargo.clictruck.dto;

public enum JobLoading {
    FTL("FTL"),
    LTL("LTL");
    private final String value;

    JobLoading(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public static boolean isValidLoading(String value) {
        for (JobLoading filter : values()) {
            if (filter.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
