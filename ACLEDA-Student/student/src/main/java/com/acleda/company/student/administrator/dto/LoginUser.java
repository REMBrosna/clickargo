package com.acleda.company.student.administrator.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUser {
    private String username;
    private String password;
}
