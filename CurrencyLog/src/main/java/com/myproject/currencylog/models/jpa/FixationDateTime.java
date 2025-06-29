package com.myproject.currencylog.models.jpa;

import java.time.LocalDateTime;

public interface FixationDateTime {
    LocalDateTime getCreated();
    LocalDateTime getUpdated();
    void setCreated(LocalDateTime created);
    void setUpdated(LocalDateTime updated);
}
