package com.acleda.company.student.configuration.token.payload.response;

import lombok.*;

import java.util.Date;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenResponse {
    private String jti;
    private MockUser user;
    private String accessToken;
    private String refreshToken;
    private Date expiredAt;
    private String tokenType;
}
