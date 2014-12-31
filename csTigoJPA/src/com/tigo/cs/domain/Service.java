package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;
import com.tigo.cs.domain.ussd.UssdServiceCorresp;

@Entity
@Table(name = "service")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@NamedQueries({ @NamedQuery(name = "Service.findByServiceCod",
        query = "SELECT s FROM Service s WHERE s.serviceCod = :serviceCod"), @NamedQuery(
        name = "Service.findAllAvailable",
        query = "SELECT s FROM Service s WHERE s.serviceCod > 0 AND s.serviceCod < 99") })
public class Service implements Serializable {

    private static final long serialVersionUID = -1978406118022155873L;
    @Id
    @SequenceGenerator(name = "serviceGen", sequenceName = "service_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "serviceGen")
    @Basic(optional = false)
    @Column(name = "service_cod")
    @Searchable(label = "entity.service.searchable.serviceCod")
    private Long serviceCod;
    @Basic(optional = false)
    @Column(name = "description_chr")
    @Searchable(label = "entity.service.searchable.descriptionChr")
    @PrimarySortedField
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "dispatcher_chr")
    private String dispatcherChr;
    @OneToMany(
            mappedBy = "service",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<ServiceColumn> serviceColumnCollection;
    @OneToMany(
            mappedBy = "service",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<ServiceValue> serviceValueCollection;
    @OneToMany(
            mappedBy = "service",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Collection<ServiceFunctionality> serviceFunctionality;
    @ManyToMany
    @JoinTable(name = "service_functionality", joinColumns = { @JoinColumn(
            name = "cod_service") }, inverseJoinColumns = { @JoinColumn(
            name = "cod_functionality") })
    @OrderBy("functionalityCod ASC")
    private Set<Functionality> functionalitySet;
    @OneToMany(mappedBy = "service")
    private List<Screenclient> screenClientList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "service")
    private Collection<MessageNotSent> messageNotSentList;
    // bi-directional many-to-one association to UssdServiceCorresp
    @OneToMany(mappedBy = "service")
    private List<UssdServiceCorresp> ussdServiceCorresps;

    // bi-directional many-to-one association to ClientGoal
    @OneToMany(mappedBy = "service")
    private List<ClientGoal> clientGoals;

    @OneToMany(mappedBy = "service")
    private List<Message> messageList;

    public Service() {
    }

    public Service(Long serviceCod) {
        this.serviceCod = serviceCod;
    }

    public Service(Long serviceCod, String descriptionChr) {
        this.serviceCod = serviceCod;
        this.descriptionChr = descriptionChr;
    }

    public Long getServiceCod() {
        return serviceCod;
    }

    public void setServiceCod(Long serviceCod) {
        this.serviceCod = serviceCod;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Set<Functionality> getFunctionalitySet() {
        return functionalitySet;
    }

    public void setFunctionalitySet(Set<Functionality> functionalitySet) {
        this.functionalitySet = functionalitySet;
    }

    public Collection<ServiceFunctionality> getServiceFunctionality() {
        return serviceFunctionality;
    }

    public void setServiceFunctionality(Collection<ServiceFunctionality> serviceFunctionality) {
        this.serviceFunctionality = serviceFunctionality;
    }

    public Collection<ServiceColumn> getServiceColumnCollection() {
        return serviceColumnCollection;
    }

    public void setServiceColumnCollection(Collection<ServiceColumn> serviceColumnCollection) {
        this.serviceColumnCollection = serviceColumnCollection;
    }

    public Collection<ServiceValue> getServiceValueCollection() {
        return serviceValueCollection;
    }

    public void setServiceValueCollection(Collection<ServiceValue> serviceValueCollection) {
        this.serviceValueCollection = serviceValueCollection;
    }

    public List<Screenclient> getScreenClientList() {
        return screenClientList;
    }

    public void setScreenClientList(List<Screenclient> screenClientList) {
        this.screenClientList = screenClientList;
    }

    public Collection<MessageNotSent> getMessageNotSentList() {
        return messageNotSentList;
    }

    public void setMessageNotSentList(Collection<MessageNotSent> messageNotSentList) {
        this.messageNotSentList = messageNotSentList;
    }

    public String getDispatcherChr() {
        return dispatcherChr;
    }

    public void setDispatcherChr(String dispatcherChr) {
        this.dispatcherChr = dispatcherChr;
    }

    public List<UssdServiceCorresp> getUssdServiceCorresps() {
        return ussdServiceCorresps;
    }

    public void setUssdServiceCorresps(List<UssdServiceCorresp> ussdServiceCorresps) {
        this.ussdServiceCorresps = ussdServiceCorresps;
    }

    public List<ClientGoal> getClientGoals() {
        return clientGoals;
    }

    public void setClientGoals(List<ClientGoal> clientGoals) {
        this.clientGoals = clientGoals;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Service other = (Service) obj;
        if (this.serviceCod != other.serviceCod
            && (this.serviceCod == null || !this.serviceCod.equals(other.serviceCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash
            + (this.serviceCod != null ? this.serviceCod.hashCode() : 0);
        hash = 17
            * hash
            + (this.descriptionChr != null ? this.descriptionChr.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descriptionChr.concat(" (Cod.: ").concat(serviceCod.toString()).concat(
                ")");
    }
}
