package com.tigo.cs.api.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MetadataCrudService extends BaseServiceBean {

    private static final long serialVersionUID = 89350767031495345L;
    private String code;
    private String value;
    private Integer metaCode;
    private Integer memberCode;
    private String activityToOpen;
    private Long mapMarkCod;
    private String iconCodeSelected;
    private List<MetadataCrudService> responseList;

    public MetadataCrudService() {
        super();
        setServiceCod(99);
        setRequiresLocation(false);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getMetaCode() {
        return metaCode;
    }

    public void setMetaCode(Integer metaCode) {
        this.metaCode = metaCode;
    }
    
    

    @Override
    public String toString() {
        return super.toString();
    }

    public List<MetadataCrudService> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<MetadataCrudService> responseList) {
        this.responseList = responseList;
    }

    public Integer getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(Integer memberCode) {
        this.memberCode = memberCode;
    }

    public String getActivityToOpen() {
        return activityToOpen;
    }

    public void setActivityToOpen(String activityToOpen) {
        this.activityToOpen = activityToOpen;
    }

    public Long getMapMarkCod() {
        return mapMarkCod;
    }

    public void setMapMarkCod(Long mapMarkCod) {
        this.mapMarkCod = mapMarkCod;
    }

	public String getIconCodeSelected() {
		return iconCodeSelected;
	}

	public void setIconCodeSelected(String iconCodeSelected) {
		this.iconCodeSelected = iconCodeSelected;
	}
    
    
}
