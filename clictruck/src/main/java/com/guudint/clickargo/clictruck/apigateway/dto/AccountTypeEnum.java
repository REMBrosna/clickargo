package com.guudint.clickargo.clictruck.apigateway.dto;

public enum AccountTypeEnum {

    ACC_TYPE_TO("TO"),
    ACC_TYPE_CO("CO"),
    ACC_TYPE_FF("FF"),
    ACC_TYPE_API("API");

    private final String desc;

    AccountTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
    public static String getValueAccountType(String value) {
        for (AccountTypeEnum filter : values()) {
            if (filter.name().equalsIgnoreCase(value)) {
                return filter.getDesc();
            }
        }
        return null;
    }
}
