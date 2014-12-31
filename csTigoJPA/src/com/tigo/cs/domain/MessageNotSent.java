package com.tigo.cs.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "message_not_sent")
public class MessageNotSent implements Serializable {

    private static final long serialVersionUID = 414743714843355824L;
    @Id
    @SequenceGenerator(name = "messageNotSentGen",
            sequenceName = "MESSAGE_NOT_SENT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "messageNotSentGen")
    @Basic(optional = false)
    @Column(name = "MESSAGE_NOT_SENT_COD")
    private Long messageNotSentCod;
    @Basic(optional = false)
    @Column(name = "DESTINATION_CHR")
    private String destinationChr;
    @Basic(optional = false)
    @Column(name = "MESSAGEOUT_CHR")
    private String messageoutChr;
    @Basic(optional = false)
    @Column(name = "DATEOUT_DAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateoutDat;
    @Basic(optional = false)
    @Column(name = "SENT_CHR")
    private String sentChr;
    @JoinColumn(name = "COD_SERVICE", referencedColumnName = "SERVICE_COD")
    @ManyToOne(optional = false)
    private Service service;
    @JoinColumn(name = "COD_MESSAGE", referencedColumnName = "MESSAGE_COD")
    @ManyToOne(optional = false)
    private Message message;

    public MessageNotSent() {
    }

    public MessageNotSent(Long messageNotSentCod) {
        this.messageNotSentCod = messageNotSentCod;
    }

    public MessageNotSent(Long messageNotSentCod, String destinationChr,
            String messageoutChr, Date dateoutDat, String sentChr) {
        this.messageNotSentCod = messageNotSentCod;
        this.destinationChr = destinationChr;
        this.messageoutChr = messageoutChr;
        this.dateoutDat = dateoutDat;
        this.sentChr = sentChr;
    }

    public Long getMessageNotSentCod() {
        return messageNotSentCod;
    }

    public void setMessageNotSentCod(Long messageNotSentCod) {
        this.messageNotSentCod = messageNotSentCod;
    }

    public String getDestinationChr() {
        return destinationChr;
    }

    public void setDestinationChr(String destinationChr) {
        this.destinationChr = destinationChr;
    }

    public String getMessageoutChr() {
        return messageoutChr;
    }

    public void setMessageoutChr(String messageoutChr) {
        this.messageoutChr = messageoutChr;
    }

    public Date getDateoutDat() {
        return dateoutDat;
    }

    public void setDateoutDat(Date dateoutDat) {
        this.dateoutDat = dateoutDat;
    }

    public String getSentChr() {
        return sentChr;
    }

    public void setSentChr(String sentChr) {
        this.sentChr = sentChr;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MessageNotSent other = (MessageNotSent) obj;
        if (this.messageNotSentCod != other.messageNotSentCod
            && (this.messageNotSentCod == null || !this.messageNotSentCod.equals(other.messageNotSentCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47
            * hash
            + (this.messageNotSentCod != null ? this.messageNotSentCod.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MessageNotSent[messageNotSentCod=" + messageNotSentCod + "]";
    }
}
