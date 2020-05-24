package com.sap.bulletinboard.ads.models;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(updatable = false)
    private Timestamp createdAt;

    @Column(insertable = false)
    private Timestamp updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void setCreateAt() {
        this.setCreatedAt(now());
    }

    @PreUpdate
    protected void setUpdatedAt() {
        this.setUpdatedAt(now());
    }

    protected Timestamp now() {
        return new Timestamp((new Date()).getTime());
    }
}
