package com.myproject.currencylog.models.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractBaseFixationDateTimeEntity
        extends AbstractBaseEntity implements FixationDateTime {

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;

    @Override
    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public LocalDateTime getUpdated() {
        return updated;
    }

    @Override
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
}