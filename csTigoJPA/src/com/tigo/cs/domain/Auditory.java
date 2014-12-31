package com.tigo.cs.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "auditory")
public class Auditory implements Serializable {

    private static final long serialVersionUID = 1362012472729802322L;
    @Id
    @SequenceGenerator(name = "auditoryGen", sequenceName = "auditory_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "auditoryGen")
    @Basic(optional = false)
    @Column(name = "auditory_cod")
    private Long auditoryCod;
    @NotNull(message = "{entity.auditory.constraint.auditdateDat.NotNull}",
            groups = { TemporalType.class })
    @Column(name = "auditdate_dat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date auditdateDat;
    @Column(name = "action_chr")
    @NotEmpty(message = "{entity.auditory.constraint.auditdateDat.NotEmpty}",
            groups = { Date.class })
    private String actionChr;
    @Column(name = "description_chr")
    private String descriptionChr;
    @JoinColumn(name = "cod_userweb", referencedColumnName = "userweb_cod")
    @ManyToOne
    private Userweb userweb;
    @JoinColumn(name = "cod_useradmin", referencedColumnName = "useradmin_cod")
    @ManyToOne
    private Useradmin useradmin;
    @JoinColumn(name = "cod_screen", referencedColumnName = "screen_cod")
    @ManyToOne
    private Screen screen;
    @JoinColumn(name = "cod_screenclient",
            referencedColumnName = "screenclient_cod")
    @ManyToOne
    private Screenclient screenClient;
    @Column(name = "sessionid")
    private String sessionId;
    @Column(name = "ipaddress")
    private String ipAddress;

    public Auditory() {
    }

    public Auditory(Long auditoryCod) {
        this.auditoryCod = auditoryCod;
    }

    public Auditory(Long auditoryCod, Timestamp auditdateDat, String actionChr) {
        this.auditoryCod = auditoryCod;
        this.auditdateDat = auditdateDat;
        this.actionChr = actionChr;
    }

    public Long getAuditoryCod() {
        return auditoryCod;
    }

    public void setAuditoryCod(Long auditoryCod) {
        this.auditoryCod = auditoryCod;
    }

    public Date getAuditdateDat() {
        return auditdateDat;
    }

    public void setAuditdateDat(Date auditdateDat) {
        this.auditdateDat = auditdateDat;
    }

    public String getActionChr() {
        return actionChr;
    }

    public void setActionChr(String actionChr) {
        this.actionChr = actionChr;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public Userweb getUserweb() {
        return userweb;
    }

    public void setUserweb(Userweb userweb) {
        this.userweb = userweb;
    }

    public Useradmin getUseradmin() {
        return useradmin;
    }

    public void setUseradmin(Useradmin useradmin) {
        this.useradmin = useradmin;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @PrePersist
    protected void prePersist() {
        setAuditdateDat(new Date());
    }

    public Screenclient getScreenClient() {
        return screenClient;
    }

    public void setScreenClient(Screenclient screenClient) {
        this.screenClient = screenClient;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((auditoryCod == null) ? 0 : auditoryCod.hashCode());
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
        Auditory other = (Auditory) obj;
        if (auditoryCod == null) {
            if (other.auditoryCod != null)
                return false;
        } else if (!auditoryCod.equals(other.auditoryCod))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getActionChr().concat(" (Cod.: ").concat(
                getAuditoryCod().toString()).concat(")");
    }
}
