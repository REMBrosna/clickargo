package com.acleda.company.student.infrastructure.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;
import java.io.Serializable;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractPersistableCustom implements Persistable<Long>, Serializable {

    private static final long serialVersionUID = 9181640245194392646L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
    @Override
    @Transient // DATAJPA-622
    @JsonIgnore
    public boolean isNew() {
        return null == this.id;
    }
}
