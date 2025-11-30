package com.subscription.core.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

/**
 * Base entity class providing auditing capabilities for creation and modification tracking.
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity {
    
    @CreatedDate
    @Column(name = "created_on", nullable = false, updatable = false)
    private ZonedDateTime createdOn;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    
    @LastModifiedDate
    @Column(name = "modified_on")
    private ZonedDateTime modifiedOn;
    
    @LastModifiedBy
    @Column(name = "modified_by")
    private String modifiedBy;
}
