package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

/**
 * The persistent class for the SUBSCRIBER database table.
 * 
 */
@Entity
@Table(name = "subscriber")
@NamedQueries({ @NamedQuery(name = "Subscriber.findByIdentification",
        query = "SELECT s FROM Subscriber s WHERE s.ciChr = :ci") })
public class Subscriber implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 333988398908403391L;

    @Id
    @SequenceGenerator(name = "SUBSCRIBER_SUBSCRIBERCOD_GENERATOR",
            sequenceName = "SUBSCRIBER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "SUBSCRIBER_SUBSCRIBERCOD_GENERATOR")
    @Column(name = "SUBSCRIBER_COD")
    private Long subscriberCod;

    @Column(name = "ADDRESS_CHR")
    private String addressChr;

    @Lob()
    @Column(name = "BACK_PHOTO_BYT")
    private byte[] backPhotoByt;

    @Column(name = "BIRTHDATE_DAT")
    @Temporal(TemporalType.DATE)
    private Date birthdateDat;

    @Column(name = "CI_CHR")
    private String ciChr;

    @Column(name = "CITY_CHR")
    private String cityChr;

    @Lob()
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "FRONT_PHOTO_BYT")
    @Type(type = "org.hibernate.type.PrimitiveByteArrayBlobType")
    private byte[] frontPhotoByt;

    @Column(name = "SOURCE_CHR")
    private String sourceChr;

    @Column(name = "REGISTRATION_CHANNEL_CHR")
    private String registrationChannelChr;

    @Column(name = "INSERTED_ON_DAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date insertedOnDat;

    @Column(name = "UPDATED_ON_DAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOnDat;

    @JoinColumn(name = "COD_WSCLIENT", referencedColumnName = "WS_CLIENT_COD")
    @ManyToOne
    private WsClient wsClient;

    public Subscriber() {
    }

    public Long getSubscriberCod() {
        return this.subscriberCod;
    }

    public void setSubscriberCod(Long subscriberCod) {
        this.subscriberCod = subscriberCod;
    }

    public String getAddressChr() {
        return this.addressChr;
    }

    public void setAddressChr(String addressChr) {
        this.addressChr = addressChr;
    }

    public byte[] getBackPhotoByt() {
        return this.backPhotoByt;
    }

    public void setBackPhotoByt(byte[] backPhotoByt) {
        this.backPhotoByt = backPhotoByt;
    }

    public Date getBirthdateDat() {
        return this.birthdateDat;
    }

    public void setBirthdateDat(Date birthdateDat) {
        this.birthdateDat = birthdateDat;
    }

    public String getCiChr() {
        return this.ciChr;
    }

    public void setCiChr(String ciChr) {
        this.ciChr = ciChr;
    }

    public String getCityChr() {
        return this.cityChr;
    }

    public void setCityChr(String cityChr) {
        this.cityChr = cityChr;
    }

    public byte[] getFrontPhotoByt() {
        return this.frontPhotoByt;
    }

    public void setFrontPhotoByt(byte[] frontPhotoByt) {
        this.frontPhotoByt = frontPhotoByt;
    }

    public String getSourceChr() {
        return sourceChr;
    }

    public String getRegistrationChannelChr() {
        return registrationChannelChr;
    }

    public Date getInsertedOnDat() {
        return insertedOnDat;
    }

    public Date getUpdatedOnDat() {
        return updatedOnDat;
    }

    public void setSourceChr(String sourceChr) {
        this.sourceChr = sourceChr;
    }

    public void setRegistrationChannelChr(String registrationChannelChr) {
        this.registrationChannelChr = registrationChannelChr;
    }

    public void setInsertedOnDat(Date insertedOnDat) {
        this.insertedOnDat = insertedOnDat;
    }

    public void setUpdatedOnDat(Date updatedOnDat) {
        this.updatedOnDat = updatedOnDat;
    }

    public WsClient getWsClient() {
        return wsClient;
    }

    public void setWsClient(WsClient wsClient) {
        this.wsClient = wsClient;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((subscriberCod == null) ? 0 : subscriberCod.hashCode());
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
        Subscriber other = (Subscriber) obj;
        if (subscriberCod == null) {
            if (other.subscriberCod != null) {
                return false;
            }
        } else if (!subscriberCod.equals(other.subscriberCod)) {
            return false;
        }
        return true;
    }

}