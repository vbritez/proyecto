package com.tigo.cs.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "auditory_action")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "AuditoryAction.findByAuditoryActionId",
        query = "SELECT a FROM AuditoryAction a WHERE a.auditoryActionId = :auditoryActionId") })
public class AuditoryAction implements Serializable {

    private static final long serialVersionUID = -4202979607186616834L;
    @Id
    @Basic(optional = false)
    @Column(name = "auditory_action_id")
    private Integer auditoryActionId;
    @Basic(optional = false)
    @Column(name = "abrev")
    private String abrev;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Column(name = "message")
    private String message;
    @Basic(optional = false)
    @Column(name = "logable")
    private Boolean logable;
    @Basic(optional = false)
    @Column(name = "trapable")
    private Boolean trapable;
    @Basic(optional = false)
    @Column(name = "auditable")
    private Boolean auditable;

    public AuditoryAction() {
    }

    public AuditoryAction(Integer auditoryActionId) {
        this.auditoryActionId = auditoryActionId;
    }

    public AuditoryAction(Integer auditoryActionId, String abrev,
            String description, String message, Boolean log, Boolean trap,
            Boolean audit) {
        this.auditoryActionId = auditoryActionId;
        this.abrev = abrev;
        this.description = description;
        this.message = message;
        this.logable = log;
        this.trapable = trap;
        this.auditable = audit;
    }

    public Integer getAuditoryActionId() {
        return auditoryActionId;
    }

    public void setAuditoryActionId(Integer auditoryActionId) {
        this.auditoryActionId = auditoryActionId;
    }

    public String getAbrev() {
        return abrev;
    }

    public void setAbrev(String abrev) {
        this.abrev = abrev;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getAuditable() {
        return auditable;
    }

    public void setAuditable(Boolean auditable) {
        this.auditable = auditable;
    }

    public Boolean getLogable() {
        return logable;
    }

    public void setLogable(Boolean logable) {
        this.logable = logable;
    }

    public Boolean getTrapable() {
        return trapable;
    }

    public void setTrapable(Boolean trapable) {
        this.trapable = trapable;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuditoryAction other = (AuditoryAction) obj;
        if (this.auditoryActionId != other.auditoryActionId
            && (this.auditoryActionId == null || !this.auditoryActionId.equals(other.auditoryActionId))) {
            return false;
        }
        if ((this.abrev == null) ? (other.abrev != null) : !this.abrev.equals(other.abrev)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : !this.message.equals(other.message)) {
            return false;
        }
        if (this.logable != other.logable
            && (this.logable == null || !this.logable.equals(other.logable))) {
            return false;
        }
        if (this.trapable != other.trapable
            && (this.trapable == null || !this.trapable.equals(other.trapable))) {
            return false;
        }
        if (this.auditable != other.auditable
            && (this.auditable == null || !this.auditable.equals(other.auditable))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23
            * hash
            + (this.auditoryActionId != null ? this.auditoryActionId.hashCode() : 0);
        hash = 23 * hash + (this.abrev != null ? this.abrev.hashCode() : 0);
        hash = 23 * hash
            + (this.description != null ? this.description.hashCode() : 0);
        hash = 23 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 23 * hash + (this.logable != null ? this.logable.hashCode() : 0);
        hash = 23 * hash
            + (this.trapable != null ? this.trapable.hashCode() : 0);
        hash = 23 * hash
            + (this.auditable != null ? this.auditable.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "AuditoryAction[auditoryActionId=" + auditoryActionId + "]";
    }
}
