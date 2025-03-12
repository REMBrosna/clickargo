package com.guudint.clickargo.clictruck.common.dto;

public enum NotificationTemplateName {

    VEHICLE_MAINTENANCE("vehicle_maintenance"),
    VEHICLE_INSURANCE_EXPIRED("vehicle_insurance_expired"),
    DRIVER_LICENSE_EXPIRED("driver_license_expired"),
    CARGO_DELIVERED("cargo_delivered"),
    RESET_PASSWORD("reset_password"),
    NEW_DRIVER("new_driver"),
    FORGOT_PASSWORD("forgot_password"),
    E_NEW_DRIVER("CK_NTL_0003"),
    E_RESET_PASSWORD("CK_NTL_0002"),
    E_FORGOT_PASSWORD("CK_NTL_0001"),
    CARGO_PICKED_UP("cargo_picked_up"),
    CARGO_1_HOUR("cargo_1_hr"),
    CARGO_30_MIN("cargo_30_min"),
    VEHICLE_TOTAL_DISTANCE_TRAVELED("vehicle_total_distance_traveled"),
    VEHICLE_PARKING_CERT_EXPIRED("vehicle_parking_cert_expired");
    private String desc;
    NotificationTemplateName(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
