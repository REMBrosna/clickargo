package com.acleda.company.student.administrator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupPosition {
    ADMIN("ROLE_ADMIN", "Administrator"),
    STUDENT("ROLE_STUDENT", "Student");

    private final String code;
    private final String value;
}
