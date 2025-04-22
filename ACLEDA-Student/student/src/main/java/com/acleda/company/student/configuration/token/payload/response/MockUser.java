package com.acleda.company.student.configuration.token.payload.response;

import com.acleda.company.student.administrator.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MockUser {
    private Long id;
    private String username;
    private String email;
    private Date issuedAt;
    private Date expiredAt;
    private Collection<String> authorities;
    private Set<Role> roles;
    private String groupPosition;
    private String displayName;
}
