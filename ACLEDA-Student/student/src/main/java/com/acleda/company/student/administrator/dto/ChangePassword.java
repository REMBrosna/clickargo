package com.acleda.company.student.administrator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassword {
    private boolean forceChangePwd = false;
    private String username;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
    private String token;

}
