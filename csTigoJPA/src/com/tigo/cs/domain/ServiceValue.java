package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "service_value")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(
        name = "ServiceValue.findByServicevalueCod",
        query = "SELECT s FROM ServiceValue s WHERE s.servicevalueCod = :servicevalueCod AND s.enabledChr = true "), @NamedQuery(
        name = "ServiceValue.findMaxServicevalue",
        query = "SELECT MAX(s.servicevalueCod) FROM ServiceValue s WHERE s.userphone = :userphone AND s.service.serviceCod = :service  AND s.enabledChr = true"), @NamedQuery(
        name = "ServiceValue.findMaxNextServicevalue",
        query = "SELECT MAX(s.servicevalueCod) FROM ServiceValue s WHERE s.userphone = :userphone AND s.service.serviceCod = :service AND s.servicevalueCod > :servicevalueCod  AND s.enabledChr = true"), @NamedQuery(
        name = "ServiceValue.findMaxServicevalueToday",
        query = "SELECT MAX(s.servicevalueCod) FROM ServiceValue s WHERE s.userphone = :userphone AND s.service.serviceCod = :service  AND s.enabledChr = true AND s.recorddateDat = :recorddateDat"), @NamedQuery(
        name = "ServiceValue.findMaxNextServicevalueToday",
        query = "SELECT MAX(s.servicevalueCod) FROM ServiceValue s WHERE s.userphone = :userphone AND s.service.serviceCod = :service AND s.servicevalueCod > :servicevalueCod  AND s.enabledChr = true AND s.recorddateDat = :recorddateDat") })
public class ServiceValue implements Serializable {

    private static final long serialVersionUID = 2296499481766045650L;
    @Id
    @SequenceGenerator(name = "serviceValueGen",
            sequenceName = "service_value_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "serviceValueGen")
    @Basic(optional = false)
    @Column(name = "servicevalue_cod")
    @Searchable(label = "entity.ticketscsi.searchable.ServiceValueCod")
    private Long servicevalueCod;
    @JoinColumn(name = "cod_service", referencedColumnName = "service_cod")
    @ManyToOne(optional = false)
    private Service service;
    @JoinColumn(name = "cod_userphone", referencedColumnName = "userphone_cod")
    @ManyToOne(optional = false)
    private Userphone userphone;
    @JoinColumn(name = "cod_message", referencedColumnName = "message_cod")
    @ManyToOne
    private Message message;
    @Column(name = "column1_chr")
    @Searchable(label = "entity.ticketscsi.searchable.Area")
    private String column1Chr;
    @Column(name = "column2_chr")
    @Searchable(label = "entity.ticketscsi.searchable.Type")
    private String column2Chr;
    @Column(name = "column3_chr")
    @Searchable(label = "entity.ticketscsi.searchable.Description")
    private String column3Chr;
    @Searchable(label = "entity.ticketscsi.searchable.TicketCode")
    @Column(name = "column4_chr")
    private String column4Chr;
    @Column(name = "column5_chr")
    private String column5Chr;
    @Column(name = "column6_chr")
    private String column6Chr;
    @Column(name = "column7_chr")
    private String column7Chr;
    @Column(name = "column8_chr")
    private String column8Chr;
    @Column(name = "column9_chr")
    private String column9Chr;
    @Column(name = "column10_chr")
    private String column10Chr;

    @Column(name = "ENABLED_CHR")
    private Boolean enabledChr;

    @PrimarySortedField
    @Basic(optional = false)
    @Column(name = "recorddate_dat")
    @Temporal(TemporalType.DATE)
    private Date recorddateDat;
    @OneToMany(
            mappedBy = "serviceValue",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @OrderBy("recorddateDat")
    private Collection<ServiceValueDetail> serviceValueDetailCollection;

    @Transient
    private Long totalRecorrido;

    public ServiceValue() {
    }

    public ServiceValue(Long servicevalueCod) {
        this.servicevalueCod = servicevalueCod;
    }

    public Long getServicevalueCod() {
        return servicevalueCod;
    }

    public void setServicevalueCod(Long servicevalueCod) {
        this.servicevalueCod = servicevalueCod;
    }

    public Boolean getEnabledChr() {
        return enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Userphone getUserphone() {
        return userphone;
    }

    public void setUserphone(Userphone userphone) {
        this.userphone = userphone;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getColumn1Chr() {
        return column1Chr;
    }

    public void setColumn1Chr(String column1Chr) {
        this.column1Chr = column1Chr;
    }

    public String getColumn2Chr() {
        return column2Chr;
    }

    public void setColumn2Chr(String column2Chr) {
        this.column2Chr = column2Chr;
    }

    public String getColumn3Chr() {
        return column3Chr;
    }

    public void setColumn3Chr(String column3Chr) {
        this.column3Chr = column3Chr;
    }

    public String getColumn4Chr() {
        return column4Chr;
    }

    public void setColumn4Chr(String column4Chr) {
        this.column4Chr = column4Chr;
    }

    public String getColumn5Chr() {
        return column5Chr;
    }

    public void setColumn5Chr(String column5Chr) {
        this.column5Chr = column5Chr;
    }

    public String getColumn6Chr() {
        return column6Chr;
    }

    public void setColumn6Chr(String column6Chr) {
        this.column6Chr = column6Chr;
    }

    public String getColumn7Chr() {
        return column7Chr;
    }

    public void setColumn7Chr(String column7Chr) {
        this.column7Chr = column7Chr;
    }

    public String getColumn8Chr() {
        return column8Chr;
    }

    public void setColumn8Chr(String column8Chr) {
        this.column8Chr = column8Chr;
    }

    public String getColumn9Chr() {
        return column9Chr;
    }

    public void setColumn9Chr(String column9Chr) {
        this.column9Chr = column9Chr;
    }

    public String getColumn10Chr() {
        return column10Chr;
    }

    public void setColumn10Chr(String column10Chr) {
        this.column10Chr = column10Chr;
    }

    public Date getRecorddateDat() {
        return recorddateDat;
    }

    public void setRecorddateDat(Date recorddateDat) {
        this.recorddateDat = recorddateDat;
    }

    public Collection<ServiceValueDetail> getServiceValueDetailCollection() {
        return serviceValueDetailCollection;
    }

    public void setServiceValueDetailCollection(Collection<ServiceValueDetail> serviceValueDetailCollection) {
        this.serviceValueDetailCollection = serviceValueDetailCollection;
    }

    // @PrePersist
    // protected void addCreatedDate() {
    // if (recorddateDat == null) {
    // setRecorddateDat(new Date());
    // }
    // }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServiceValue other = (ServiceValue) obj;
        if (this.servicevalueCod != other.servicevalueCod
            && (this.servicevalueCod == null || !this.servicevalueCod.equals(other.servicevalueCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17
            * hash
            + (this.servicevalueCod != null ? this.servicevalueCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ServiceValue[servicevalueCod=" + servicevalueCod + "]";
    }

    public Long getTotalRecorrido() {
        return totalRecorrido;
    }

    public void setTotalRecorrido(Long totalRecorrido) {
        this.totalRecorrido = totalRecorrido;
    }
}
