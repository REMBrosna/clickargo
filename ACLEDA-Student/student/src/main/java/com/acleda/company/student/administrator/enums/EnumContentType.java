package com.acleda.company.student.administrator.enums;

public enum EnumContentType {

    TEXT("TEXT", "TEXT"),
    HTML("HTML", "HTML");

    private final String desc;
    private final String postDesc;

    EnumContentType(String desc, String postDesc) {
        this.desc = desc;
        this.postDesc = postDesc;
    }

    public String getDesc() {
        return desc;
    }

    public String getPostDesc() {
        return postDesc;
    }
}
