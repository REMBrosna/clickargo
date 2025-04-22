package com.acleda.company.student.configuration.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JwtTokenHeader {
    @JsonProperty("alg")
    private String algorithm;
    @JsonProperty("type")
    private String type;

    public JwtTokenHeader() {
        this.algorithm = "SHA512withECDSA";
        this.type = "JWT";
    }

    public JwtTokenHeader(String algorithm, String type) {
        this.algorithm = algorithm;
        this.type = type;
    }
}
