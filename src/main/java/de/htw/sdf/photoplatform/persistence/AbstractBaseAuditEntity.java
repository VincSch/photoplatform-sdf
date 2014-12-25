/*
 *
 * Copyright (C) 2014
 *
 */

package de.htw.sdf.photoplatform.persistence;

import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Superclass for all entities defining audit fields.
 *
 * @author <a href="mailto:s0541962@htw-berlin.de">Vincent Schwarzer</a>
 */
@MappedSuperclass
public abstract class AbstractBaseAuditEntity extends AbstractBaseEntity {

    private static final long serialVersionUID = -3699932723300487164L;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // TODO this should be "User" entity
    // createdBy reference user email
    @Email
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // TODO this should be "User" entity
    // updatedBy reference user email
    @Email
    @Column(name = "UPDATED_BY")
    private String updatedBy;

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the updatedBy
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Sets createdAt before insert.
     */
    @PrePersist
    public void setCreationDate() {
        this.createdAt = new Date();
    }

    /**
     * Sets updatedAt before update.
     */
    @PreUpdate
    public void setChangeDate() {
        this.updatedAt = new Date();
    }
}
