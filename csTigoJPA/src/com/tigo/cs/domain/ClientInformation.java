package com.tigo.cs.domain;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;
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
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@Entity
@Table(name = "client_information")
public class ClientInformation implements Serializable {

    private static final long serialVersionUID = 8188984907831836455L;
    @Id
    @SequenceGenerator(name = "clientInformationGen", sequenceName = "client_information_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientInformationGen")
    @Basic(optional = false)
    @Column(name = "client_information_cod")
    @Searchable(label = "entity.client.clientInformation.searchable.clientInformationCod")
    private Long clientInformationCod;
    @NotNull(message = "{entity.clientInformation.constraint.infordateDat.NotNull.}")
    @Column(name = "infodate_dat")
    @Temporal(TemporalType.TIMESTAMP)
    @PrimarySortedField(ascending = false)
    @Searchable(label = "entity.client.clientInformation.searchable.infordateDat")
    private Date infodateDat;
    @NotEmpty(message = "{entity.clientInformation.constraint.informationChr.NotEmpty}")
    @Column(name = "information_chr")
    @Searchable(label = "entity.client.clientInformation.searchable.informationChr")
    private String informationChr;
    @JoinColumn(name = "cod_useradmin", referencedColumnName = "useradmin_cod")
    @ManyToOne(optional = false)
    private Useradmin useradmin;
    @JoinColumn(name = "cod_client", referencedColumnName = "client_cod")
    @ManyToOne(optional = false)
    private Client client;

    public ClientInformation() {
    }

    public ClientInformation(Long clientInformationCod) {
        this.clientInformationCod = clientInformationCod;
    }

    public ClientInformation(Long clientInformationCod, Date infodateDat, String informationChr) {
        this.clientInformationCod = clientInformationCod;
        this.infodateDat = infodateDat;
        this.informationChr = informationChr;
    }

    public Long getClientInformationCod() {
        return clientInformationCod;
    }

    public void setClientInformationCod(Long clientInformationCod) {
        this.clientInformationCod = clientInformationCod;
    }

    public Date getInfodateDat() {
        return infodateDat;
    }

    public void setInfodateDat(Date infodateDat) {
        this.infodateDat = infodateDat;
    }

    public String getInformationChr() {
        return informationChr;
    }

    public void setInformationChr(String informationChr) {
        this.informationChr = informationChr;
    }

    public Useradmin getUseradmin() {
        return useradmin;
    }

    public void setUseradmin(Useradmin useradmin) {
        this.useradmin = useradmin;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientInformation other = (ClientInformation) obj;
        if (this.clientInformationCod != other.clientInformationCod && (this.clientInformationCod == null || !this.clientInformationCod.equals(other.clientInformationCod))) {
            return false;
        }
        if (this.infodateDat != other.infodateDat && (this.infodateDat == null || !this.infodateDat.equals(other.infodateDat))) {
            return false;
        }
        if ((this.informationChr == null) ? (other.informationChr != null) : !this.informationChr.equals(other.informationChr)) {
            return false;
        }
        if (this.useradmin != other.useradmin && (this.useradmin == null || !this.useradmin.equals(other.useradmin))) {
            return false;
        }
        if (this.client != other.client && (this.client == null || !this.client.equals(other.client))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.clientInformationCod != null ? this.clientInformationCod.hashCode() : 0);
        hash = 97 * hash + (this.infodateDat != null ? this.infodateDat.hashCode() : 0);
        hash = 97 * hash + (this.informationChr != null ? this.informationChr.hashCode() : 0);
        hash = 97 * hash + (this.useradmin != null ? this.useradmin.hashCode() : 0);
        hash = 97 * hash + (this.client != null ? this.client.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getInformationChr().concat(" (Cod.: ").concat(getClientInformationCod().toString()).concat(")");
    }
}
