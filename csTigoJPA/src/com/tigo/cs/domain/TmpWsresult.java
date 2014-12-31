package com.tigo.cs.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tmp_wsresult")
@NamedQueries({ @NamedQuery(
        name = "TmpWsresult.findByParameters",
        query = "SELECT t FROM TmpWsresult t WHERE t.codClient = :codClient AND t.sessionId = :sessionId AND t.dataType = :dataType AND t.dataId = :dataId") })
public class TmpWsresult implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "tmpWsresultGen",
            sequenceName = "TMP_WSRESULT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "tmpWsresultGen")
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "COD_CLIENT")
    private Long codClient;
    @Basic(optional = false)
    @Column(name = "SESSION_ID")
    private String sessionId;
    @Basic(optional = false)
    @Column(name = "DATA_TYPE")
    private String dataType;
    @Basic(optional = false)
    @Column(name = "DATA_ID")
    private String dataId;
    @Basic(optional = false)
    @Column(name = "DATA_C")
    private String dataC;

    public TmpWsresult() {
    }

    public TmpWsresult(Long id) {
        this.id = id;
    }

    public TmpWsresult(Long id, Long codClient, String sessionId,
            String dataType, String dataId, String dataC) {
        this.id = id;
        this.codClient = codClient;
        this.sessionId = sessionId;
        this.dataType = dataType;
        this.dataId = dataId;
        this.dataC = dataC;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodClient() {
        return codClient;
    }

    public void setCodClient(Long codClient) {
        this.codClient = codClient;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataC() {
        return dataC;
    }

    public void setDataC(String dataC) {
        this.dataC = dataC;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TmpWsresult other = (TmpWsresult) obj;
        if (this.id != other.id
            && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "TmpWsresult[id=" + id + "]";
    }
}
