package com.tigo.cs.ws.data;

import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * 
 * @author Viviana Britez
 */
@XStreamAlias("ot")
public class OTCab {
    long id;
    String nroTelefono;
    String usuarioTelefono;
    String otCode;
    String activityCod;
    String clientCod;
    String zoneCod;
    String createdDate;
    String assignedDate;
    String statusCod;
    String endDate;
    @XStreamImplicit(itemFieldName = "detalle")
    List<OTDet> detalles;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNroTelefono() {
        return nroTelefono;
    }
    public void setNroTelefono(String nroTelefono) {
        this.nroTelefono = nroTelefono;
    }
    public String getUsuarioTelefono() {
        return usuarioTelefono;
    }
    public void setUsuarioTelefono(String usuarioTelefono) {
        this.usuarioTelefono = usuarioTelefono;
    }
    public String getOtCode() {
        return otCode;
    }
    public void setOtCode(String otCode) {
        this.otCode = otCode;
    }
    public String getActivityCod() {
        return activityCod;
    }
    public void setActivityCod(String activityCod) {
        this.activityCod = activityCod;
    }
    public String getClientCod() {
        return clientCod;
    }
    public void setClientCod(String clientCod) {
        this.clientCod = clientCod;
    }
    public String getZoneCod() {
        return zoneCod;
    }
    public void setZoneCod(String zoneCod) {
        this.zoneCod = zoneCod;
    }
    
    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public String getAssignedDate() {
        return assignedDate;
    }
    public void setAssignedDate(String assignedDate) {
        this.assignedDate = assignedDate;
    }
    public String getStatusCod() {
        return statusCod;
    }
    public void setStatusCod(String statusCod) {
        this.statusCod = statusCod;
    }
   
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public List<OTDet> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<OTDet> detalles) {
        this.detalles = detalles;
    }
}
