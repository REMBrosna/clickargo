package com.acleda.company.student.administrator.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponse {
    private String accessToken;
    private long expiry;
}
