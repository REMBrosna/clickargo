package com.guudint.clickargo.clictruck.dto;

public enum JobSubType {
    LOCAL("LOCAL"),
    IMPORT("IMPORT"),
    EXPORT("EXPORT");
    private final String value;

    JobSubType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValidJobSubType(String value) {
        for (JobSubType filter : values()) {
            if (filter.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
