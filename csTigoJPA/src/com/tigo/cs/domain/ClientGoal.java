package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "CLIENT_GOAL")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ClientGoal implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "CLIENT_GOAL_GENERATOR",
            sequenceName = "CLIENT_GOAL_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "CLIENT_GOAL_GENERATOR")
    @Column(name = "CLIENT_GOAL_COD")
    private Long clientGoalCod;

    @Column(name = "DAY_FROM_NUM")
    private Integer dayFrom;

    @Column(name = "DAY_TO_NUM")
    private Integer dayTo;

    @Column(name = "DESCRIPTION_CHR")
    private String description;

    @Column(name = "VALIDITY_DATE_FROM_DAT")
    @Temporal(TemporalType.DATE)
    private Date validityDateFrom;

    @Column(name = "VALIDITY_DATE_TO_DAT")
    @Temporal(TemporalType.DATE)
    private Date validityDateTo;

    @Column(name = "GOAL_NUM")
    private Integer goal;

    // bi-directional many-to-one association to Client
    @ManyToOne
    @JoinColumn(name = "COD_CLIENT")
    private Client client;

    // bi-directional many-to-one association to Service
    @ManyToOne
    @JoinColumn(name = "COD_SERVICE")
    private Service service;

    // bi-directional many-to-many association to Userphone
    @ManyToMany
    @JoinTable(name = "CLIENT_GOAL_USERPHONE", joinColumns = { @JoinColumn(
            name = "COD_CLIENT_GOAL") }, inverseJoinColumns = { @JoinColumn(
            name = "COD_USERPHONE") })
    private List<Userphone> userphones;

    public ClientGoal() {
    }

    public Long getClientGoalCod() {
        return this.clientGoalCod;
    }

    public void setClientGoalCod(Long clientGoalCod) {
        this.clientGoalCod = clientGoalCod;
    }

    public Integer getDayFrom() {
        return this.dayFrom;
    }

    public void setDayFrom(Integer dayFrom) {
        this.dayFrom = dayFrom;
    }

    public Integer getDayTo() {
        return this.dayTo;
    }

    public void setDayTo(Integer dayTo) {
        this.dayTo = dayTo;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getValidityDateFrom() {
        return this.validityDateFrom;
    }

    public void setValidityDateFrom(Date validityDateFrom) {
        this.validityDateFrom = validityDateFrom;
    }

    public Date getValidityDateTo() {
        return this.validityDateTo;
    }

    public void setValidityDateTo(Date validityDateTo) {
        this.validityDateTo = validityDateTo;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Userphone> getUserphones() {
        return this.userphones;
    }

    public void setUserphones(List<Userphone> userphones) {
        this.userphones = userphones;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((clientGoalCod == null) ? 0 : clientGoalCod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientGoal other = (ClientGoal) obj;
        if (clientGoalCod == null) {
            if (other.clientGoalCod != null)
                return false;
        } else if (!clientGoalCod.equals(other.clientGoalCod))
            return false;
        return true;
    }

}