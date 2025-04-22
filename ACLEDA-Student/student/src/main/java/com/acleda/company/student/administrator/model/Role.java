package com.acleda.company.student.administrator.model;

import com.acleda.company.student.infrastructure.domain.AbstractPersistableCustom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "T_APP_ROLE", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "unq_name")})
public class Role extends AbstractPersistableCustom implements Serializable {

    @Column(name = "NAME", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 500)
    private String description;
}
