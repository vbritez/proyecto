package com.tigo.cs.api.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.enumerates.MetaNames;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryDAService extends BaseServiceBean {

    private static final long serialVersionUID = -6637765036852725702L;
    @MetaColumn(metaname = MetaNames.CLIENT)
    private String clientCode;
    @MetaColumn(metaname = MetaNames.DELIVERER)
    private String dealer;
    private String trayType;
    private String location;
    private String quantity;

    public InventoryDAService() {
        super();
        setServiceCod(8);
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getTrayType() {
        return trayType;
    }

    public void setTrayType(String trayType) {
        this.trayType = trayType;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
