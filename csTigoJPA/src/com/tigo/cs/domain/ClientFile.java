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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "client_file")
@NamedQueries({ @NamedQuery(
        name = "ClientFile.findByClientFile",
        query = "SELECT c FROM ClientFile c WHERE c.client = :client AND c.filenameChr = :fileName") })
public class ClientFile implements Serializable {

    private static final long serialVersionUID = 2796726813953822366L;
    @Id
    @SequenceGenerator(name = "clientFileGen",
            sequenceName = "CLIENT_FILE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "clientFileGen")
    @Basic(optional = false)
    @Column(name = "CLIENT_FILE_COD")
    @Searchable(label = "entity.clientFile.searchable.clientFileCod")
    private Long clientFileCod;
    @Basic(optional = false)
    @Column(name = "FILENAME_CHR")
    @Searchable(label = "entity.clientFile.searchable.filenameChr")
    private String filenameChr;
    @Column(name = "DESCRIPTION_CHR")
    @Searchable(label = "entity.clientFile.searchable.descriptionChr")
    private String descriptionChr;
    @Basic(optional = false)
    @Column(name = "FILETYPE_CHR")
    private String filetypeChr;
    @Basic(optional = false)
    @Column(name = "UPLOADDATE_DAT")
    @Temporal(TemporalType.TIMESTAMP)
    @PrimarySortedField(ascending = false)
    private Date uploaddateDat;
    @Basic(optional = false)
    @Lob
    @Column(name = "FILE_BYT")
    private byte[] fileByt;
    @JoinColumn(name = "COD_USERADMIN", referencedColumnName = "USERADMIN_COD")
    @ManyToOne
    private Useradmin useradmin;
    @JoinColumn(name = "COD_USERWEB", referencedColumnName = "USERWEB_COD")
    @ManyToOne
    private Userweb userweb;
    @JoinColumn(name = "COD_CLIENT", referencedColumnName = "CLIENT_COD")
    @ManyToOne(optional = false)
    private Client client;

    public ClientFile() {
    }

    public ClientFile(Long clientFileCod) {
        this.clientFileCod = clientFileCod;
    }

    public ClientFile(Long clientFileCod, String filenameChr,
            String filetypeChr, Date uploaddateDat, byte[] fileByt) {
        this.clientFileCod = clientFileCod;
        this.filenameChr = filenameChr;
        this.filetypeChr = filetypeChr;
        this.uploaddateDat = uploaddateDat;
        this.fileByt = fileByt;
    }

    public Long getClientFileCod() {
        return clientFileCod;
    }

    public void setClientFileCod(Long clientFileCod) {
        this.clientFileCod = clientFileCod;
    }

    public String getFilenameChr() {
        return filenameChr;
    }

    public void setFilenameChr(String filenameChr) {
        this.filenameChr = filenameChr;
    }

    public String getDescriptionChr() {
        return descriptionChr;
    }

    public void setDescriptionChr(String descriptionChr) {
        this.descriptionChr = descriptionChr;
    }

    public String getFiletypeChr() {
        return filetypeChr;
    }

    public void setFiletypeChr(String filetypeChr) {
        this.filetypeChr = filetypeChr;
    }

    public Date getUploaddateDat() {
        return uploaddateDat;
    }

    public void setUploaddateDat(Date uploaddateDat) {
        this.uploaddateDat = uploaddateDat;
    }

    public byte[] getFileByt() {
        return fileByt;
    }

    public void setFileByt(byte[] fileByt) {
        this.fileByt = fileByt;
    }

    public Useradmin getUseradmin() {
        return useradmin;
    }

    public void setUseradmin(Useradmin useradmin) {
        this.useradmin = useradmin;
    }

    public Userweb getUserweb() {
        return userweb;
    }

    public void setUserweb(Userweb userweb) {
        this.userweb = userweb;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @PrePersist
    public void setUploadedDate() {
        this.uploaddateDat = new Date();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientFile other = (ClientFile) obj;
        if (this.clientFileCod != other.clientFileCod
            && (this.clientFileCod == null || !this.clientFileCod.equals(other.clientFileCod))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash
            + (this.clientFileCod != null ? this.clientFileCod.hashCode() : 0);

        return hash;
    }

    @Override
    public String toString() {
        return "ClientFile[clientFileCod=" + clientFileCod + "]";
    }
}
