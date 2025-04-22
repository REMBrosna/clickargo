package com.acleda.company.student.configuration.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtTokenBody {
    @JsonProperty("sub")
    private String username;

    @JsonProperty("iat")
    private Date issuedAt;

    @JsonProperty("exp")
    private Date expiredAt;

    private String fullName;
    private Long id;
    private String email;

    private List<String> roles;
}
