package com.guudint.clickargo.clictruck.apigateway.dto;

import org.apache.commons.lang3.StringUtils;

public enum JobStatesEnum {
    ACP("ACCEPTED"),
    ASG("ASSIGNED"),
    CAN("CANCELLED"),
    DLV("DELIVERED"),
    NEW("NEW"),
    ONGOING("ONGOING"),
    PAUSED("PAUSED"),
    REJ("REJECTED"),
    SUB("SUBMITTED"),
    ALL("ALL");

    private final String desc;

    JobStatesEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static String fromStateName(String state) {
        for (JobStatesEnum jobState : values()) {
            if (jobState.name().equalsIgnoreCase(state)) {
                return jobState.getDesc();
            }
        }
        return null;
    }
    public static String fromStateDesc(String state) {
        for (JobStatesEnum jobState : values()) {
            if (jobState.getDesc().equalsIgnoreCase(state)) {
                return jobState.name();
            }
        }
        return null;
    }
    public static boolean isValid(String value) {
        if (StringUtils.isBlank(value)) {
            return false; //still return true when blank
        }
        for (JobStatesEnum state : values()) {
            if (state.getDesc().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}

