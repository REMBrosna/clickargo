package com.acleda.company.student.configuration.token.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokenRequest {
    @JsonProperty("username")
    @NotEmpty(message = "Username is mandatory")
    private String username;
    @JsonProperty("password")
    @NotEmpty(message = "Password is mandatory")
    private String password;
}
