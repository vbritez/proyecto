package com.tigo.cs.domain.view;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.Searchable;

/**
 * The persistent class for the OT database table.
 * 
 */
@Entity
public class Ot extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @PrimarySortedField
    @Column(name = "SERVICEVALUE_COD")
    private Long servicevalueCod;

    @Column(name = "COD_SERVICE")
    private Long codService;

    @Column(name = "COD_CLIENT")
    private Long codClient;

    @Column(name = "COD_USERPHONE")
    private Long codUserphone;

    @Searchable(label = "ot.field.searchable.WorkingGroup")
    @Column(name = "NAME_CHR")
    private String nameChr;
    
    @Searchable(label = "ot.field.searchable.OtCode")
    @Column(name = "OT_CODE")
    private String otCode;

    @Column(name = "ACTIVITY_COD")
    private String activityCod;

    @Searchable(label = "ot.field.searchable.OtActivityDesc") 
    @Column(name = "ACTIVITY_DESCRIPTION")
    private String activityDescription;
    
    @Column(name = "ACTIVITY_DURATION")
    private String activityDuration;

//    @Searchable(label = "ot.field.searchable.OtClientCode") 
    @Column(name = "CLIENT_COD")
    private String clientCod;

    @Searchable(label = "ot.field.searchable.ClientDesc") 
    @Column(name = "CLIENT_DESCRIPTION")
    private String clientDescription;

    @Column(name = "ZONE_COD")
    private String zoneCod;

    @Searchable(label = "ot.field.searchable.ZoneDesc") 
    @Column(name = "ZONE_DESCRIPTION")
    private String zoneDescription;

    @Column(name = "STATUS_COD")
    private String statusCod;

    @Searchable(label = "ot.field.searchable.OTStateDesc") 
    @Column(name = "STATUS_DESCRIPTION")
    private String statusDescription;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Column(name = "ASSIGNED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedDate;
    
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Column(name = "ORDER_NUMBER")
    private Long orderNumber;
   
       
//    @Column(name = "COD_PARTNER")
//    private Long codPartner;
    
//    @Column(name = "PARTNER")
//    private String partner;

    public Ot() {
    }
    
    public Long getOtCod(){
    	return servicevalueCod;
    }
    public void setOtCod(Long otCod){
    	this.servicevalueCod = otCod;
    }

    public String getActivityCod() {
        return this.activityCod;
    }

    public void setActivityCod(String activityCod) {
        this.activityCod = activityCod;
    }

    public String getActivityDescription() {
        return this.activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getClientCod() {
        return this.clientCod;
    }

    public void setClientCod(String clientCod) {
        this.clientCod = clientCod;
    }

    public String getClientDescription() {
        return this.clientDescription;
    }

    public void setClientDescription(String clientDescription) {
        this.clientDescription = clientDescription;
    }

    public Long getCodClient() {
        return this.codClient;
    }

    public void setCodClient(Long codClient) {
        this.codClient = codClient;
    }

    public Long getCodService() {
        return this.codService;
    }

    public void setCodService(Long codService) {
        this.codService = codService;
    }

    public Long getCodUserphone() {
        return this.codUserphone;
    }

    public void setCodUserphone(Long codUserphone) {
        this.codUserphone = codUserphone;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getNameChr() {
        return this.nameChr;
    }

    public void setNameChr(String nameChr) {
        this.nameChr = nameChr;
    }

    public String getOtCode() {
        return this.otCode;
    }

    public void setOtCode(String otCode) {
        this.otCode = otCode;
    }

    public Long getServicevalueCod() {
        return this.servicevalueCod;
    }

    public void setServicevalueCod(Long servicevalueCod) {
        this.servicevalueCod = servicevalueCod;
    }

    public String getStatusCod() {
        return this.statusCod;
    }

    public void setStatusCod(String statusCod) {
        this.statusCod = statusCod;
    }

    public String getStatusDescription() {
        return this.statusDescription;
    }    

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public void setStateDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getZoneCod() {
        return this.zoneCod;
    }

    public void setZoneCod(String zoneCod) {
        this.zoneCod = zoneCod;
    }

    public String getZoneDescription() {
        return this.zoneDescription;
    }

    public void setZoneDescription(String zoneDescription) {
        this.zoneDescription = zoneDescription;
    }    

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }    

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
        

    public String getActivityDuration() {
        return activityDuration;
    }

    public void setActivityDuration(String activityDuration) {
        this.activityDuration = activityDuration;
    }  
    
    

//    public String getPartner() {
//        return partner;
//    }
//
//    public void setPartner(String partner) {
//        this.partner = partner;
//    }   
//
//    public Long getCodPartner() {
//        return codPartner;
//    }
//
//    public void setCodPartner(Long codPartner) {
//        this.codPartner = codPartner;
//    }
    
    public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
    public Long getPrimaryKey() {
        return getServicevalueCod();
    }

}