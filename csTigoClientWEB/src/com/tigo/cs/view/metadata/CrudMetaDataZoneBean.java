package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataZone;

@ManagedBean(name = "crudMetaDataZoneBean")
@ViewScoped
public class CrudMetaDataZoneBean extends AbstractCrudMetaDataBean<DataZone> implements Serializable {

    private static final long serialVersionUID = 5115799678462742077L;
    private static final Long COD_META = 13L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataZoneBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_datazone");
        setXlsReportName("rep_datazone_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.zoneCrudMetaData.Zone"));
        }
        return metaLabel; 
    }

}