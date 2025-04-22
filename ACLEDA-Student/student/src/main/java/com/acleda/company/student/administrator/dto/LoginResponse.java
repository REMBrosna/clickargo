package com.acleda.company.student.administrator.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String fullName;
    private String username;
    private String email;
    private String token;
    private long expiresIn;
}
