package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the WS_CLIENT database table.
 * 
 */
@Entity
@Table(name = "WS_CLIENT")
@NamedQueries({ @NamedQuery(name = "WsClient.findByConsumerId",
        query = "SELECT w FROM WsClient w WHERE w.consumerIdChr = :consumerId") })
public class WsClient implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "WS_CLIENT_WSCLIENTCOD_GENERATOR",
            sequenceName = "WS_CLIENT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "WS_CLIENT_WSCLIENTCOD_GENERATOR")
    @Column(name = "WS_CLIENT_COD")
    private Long wsClientCod;

    @Column(name = "CONSUMER_DESC_CHR")
    private String consumerDescChr;

    @Column(name = "CONSUMER_ID_CHR")
    private String consumerIdChr;

    @Column(name = "CONSUMER_PWD_CHR")
    private String consumerPwdChr;

    @Column(name = "ENABLED_CHR")
    private Boolean enabledChr;

    @Column(name = "OP_EXISTSCI_ALLOWED_CHR")
    private Boolean opExistsciAllowedChr;

    @Column(name = "OP_GETCI_ALLOWED_CHR")
    private Boolean opGetciAllowedChr;

    @Column(name = "OP_REGISTERCI_ALLOWED_CHR")
    private Boolean opRegisterciAllowedChr;

    @Column(name = "OP_UPDATECI_ALLOWED_CHR")
    private Boolean opUpdateciAllowedChr;

    @OneToMany(
            mappedBy = "wsClient",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private List<Subscriber> subscriberList;

    public WsClient() {
    }

    public Long getWsClientCod() {
        return this.wsClientCod;
    }

    public void setWsClientCod(Long wsClientCod) {
        this.wsClientCod = wsClientCod;
    }

    public String getConsumerDescChr() {
        return this.consumerDescChr;
    }

    public void setConsumerDescChr(String consumerDescChr) {
        this.consumerDescChr = consumerDescChr;
    }

    public String getConsumerIdChr() {
        return this.consumerIdChr;
    }

    public void setConsumerIdChr(String consumerIdChr) {
        this.consumerIdChr = consumerIdChr;
    }

    public String getConsumerPwdChr() {
        return this.consumerPwdChr;
    }

    public void setConsumerPwdChr(String consumerPwdChr) {
        this.consumerPwdChr = consumerPwdChr;
    }

    public Boolean getEnabledChr() {
        return this.enabledChr;
    }

    public void setEnabledChr(Boolean enabledChr) {
        this.enabledChr = enabledChr;
    }

    public Boolean getOpExistsciAllowedChr() {
        return this.opExistsciAllowedChr;
    }

    public void setOpExistsciAllowedChr(Boolean opExistsciAllowedChr) {
        this.opExistsciAllowedChr = opExistsciAllowedChr;
    }

    public Boolean getOpGetciAllowedChr() {
        return this.opGetciAllowedChr;
    }

    public void setOpGetciAllowedChr(Boolean opGetciAllowedChr) {
        this.opGetciAllowedChr = opGetciAllowedChr;
    }

    public Boolean getOpRegisterciAllowedChr() {
        return this.opRegisterciAllowedChr;
    }

    public void setOpRegisterciAllowedChr(Boolean opRegisterciAllowedChr) {
        this.opRegisterciAllowedChr = opRegisterciAllowedChr;
    }

    public Boolean getOpUpdateciAllowedChr() {
        return opUpdateciAllowedChr;
    }

    public void setOpUpdateciAllowedChr(Boolean opUpdateciAllowedChr) {
        this.opUpdateciAllowedChr = opUpdateciAllowedChr;
    }

    public List<Subscriber> getSubscriberList() {
        return subscriberList;
    }

    public void setSubscriberList(List<Subscriber> subscriberList) {
        this.subscriberList = subscriberList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((wsClientCod == null) ? 0 : wsClientCod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WsClient other = (WsClient) obj;
        if (wsClientCod == null) {
            if (other.wsClientCod != null) {
                return false;
            }
        } else if (!wsClientCod.equals(other.wsClientCod)) {
            return false;
        }
        return true;
    }

}