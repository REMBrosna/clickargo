package com.guudint.clickargo.clictruck.common.dto;

public enum NotificationTypeName {
    WHATSAPP("WhatsApp"),
    SMS("SMS"),
    EMAIL("Email");

    private String desc;
    NotificationTypeName(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
