package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TerportService extends BaseServiceBean {

    private static final long serialVersionUID = -6250820892294187426L;
    private String username;
    private String password;

    @MetaColumn(metaname = MetaNames.MANAGER)
    private String manager;
    @MetaColumn(metaname = MetaNames.OPERATOR)
    private String operator;
    @MetaColumn(metaname = MetaNames.MACHINE)
    private String machine;

    private String container;
    private String ubication;
    private String registrationNumber;

    public TerportService() {
        super();
        setServiceCod(24);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
