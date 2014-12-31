package com.tigo.cs.domain.view;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tigo.cs.commons.jpa.Order;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

@Entity
@Table(name = "DATA_TICKET_CSI_USER")
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class DataTicketCSIUser extends Data implements Serializable {

    private static final long serialVersionUID = 3591544795608123333L;

    @Column(name = "NOMBRE", length = 255)
    @Searchable(label = "metadata.contacts.field.Name")
    @PrimarySortedField
    @Order(1)
    private String nombre;

    @Column(name = "USUARIO_LDAP", length = 255)
    @Searchable(label = "metadata.contacts.field.LastName")
    @Order(2)
    private String usuarioLdap;

    @Column(name = "USUARIO_CSI", length = 255)
    @Searchable(label = "metadata.contacts.field.PhoneNumber")
    @Order(3)
    private String usuarioCsi;

    @Column(name = "CONTRASENA_CSI", length = 255)
    @Searchable(label = "metadata.contacts.field.Cellphone")
    @Order(4)
    private String contrasenaCsi;

    @Column(name = "EMAIL", length = 255)
    @Searchable(label = "metadata.contacts.field.Address")
    @Order(5)
    private String email;

    @Column(name = "CELULAR", length = 255)
    @Searchable(label = "metadata.contacts.field.Address")
    @Order(5)
    private String celular;

    public DataTicketCSIUser() {
        dataPK = new DataPK();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuarioLdap() {
        return usuarioLdap;
    }

    public void setUsuarioLdap(String usuarioLdap) {
        this.usuarioLdap = usuarioLdap;
    }

    public String getUsuarioCsi() {
        return usuarioCsi;
    }

    public void setUsuarioCsi(String usuarioCsi) {
        this.usuarioCsi = usuarioCsi;
    }

    public String getContrasenaCsi() {
        return contrasenaCsi;
    }

    public void setContrasenaCsi(String contrasenaCsi) {
        this.contrasenaCsi = contrasenaCsi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    @Override
    public String toString() {
        return "DataContacts [dataPK=" + dataPK + "]";
    }
}
