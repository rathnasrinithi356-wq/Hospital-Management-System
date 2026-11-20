package com.apex.diagnostics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "lab_state")
public class LabState {
    @Id private Long id = 1L;
    @Column(name = "payload", columnDefinition = "LONGTEXT", nullable = false) private String payload;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();

    protected LabState() { }
    public LabState(Long id, String payload) { this.id = id; this.payload = payload; this.updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getPayload() { return payload; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setPayload(String payload) { this.payload = payload; this.updatedAt = Instant.now(); }
    @PreUpdate public void touch() { this.updatedAt = Instant.now(); }
}
