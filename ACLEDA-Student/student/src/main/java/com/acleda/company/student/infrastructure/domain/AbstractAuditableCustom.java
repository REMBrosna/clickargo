package com.acleda.company.student.infrastructure.domain;

import com.acleda.company.student.administrator.model.TAppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableCustom extends AbstractPersistableCustom implements Auditable<TAppUser, Long, Instant> {

    private static final long serialVersionUID = 141481953116476081L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby_id")
    private TAppUser createdBy;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lastmodifiedby_id")
    private TAppUser lastModifiedBy;

    @Column(name = "lastmodified_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Override
    public Optional<TAppUser> getCreatedBy() {
        return Optional.ofNullable(this.createdBy);
    }

    @Override
    public void setCreatedBy(final TAppUser createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Optional<Instant> getCreatedDate() {
        return null == this.createdDate ? Optional.empty() : Optional.of(this.createdDate.toInstant());
    }

    @Override
    public void setCreatedDate(final Instant createdDate) {
        this.createdDate = null == createdDate ? null : Date.from(createdDate);
    }

    @Override
    public Optional<TAppUser> getLastModifiedBy() {
        return Optional.ofNullable(this.lastModifiedBy);
    }

    @Override
    public void setLastModifiedBy(final TAppUser lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public Optional<Instant> getLastModifiedDate() {
        return null == this.lastModifiedDate ? Optional.empty() : Optional.of(this.lastModifiedDate.toInstant());
    }

    @Override
    public void setLastModifiedDate(final Instant lastModifiedDate) {
        this.lastModifiedDate = null == lastModifiedDate ? null : Date.from(lastModifiedDate);
    }

}
