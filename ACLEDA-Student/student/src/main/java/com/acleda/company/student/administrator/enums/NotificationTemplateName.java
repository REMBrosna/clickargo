package com.acleda.company.student.administrator.enums;

public enum NotificationTemplateName {

    RESET_PASSWORD("reset_password"),
    FORGOT_PASSWORD("forgot_password");
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
