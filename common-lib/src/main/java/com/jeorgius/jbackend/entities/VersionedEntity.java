package com.jeorgius.jbackend.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class VersionedEntity<ID> {
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
    @Column(name = "deleted")
    private boolean deleted = false;

    public abstract ID getId();

    public abstract void setId(ID id);
}
