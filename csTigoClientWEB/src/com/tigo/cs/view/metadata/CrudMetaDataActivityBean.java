package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataActivity;

@ManagedBean(name = "crudMetaDataActivityBean")
@ViewScoped
public class CrudMetaDataActivityBean extends AbstractCrudMetaDataBean<DataActivity> implements Serializable {

    private static final long serialVersionUID = -369508588682186969L;
    private static final Long COD_META = 11L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataActivityBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_dataactivity");
        setXlsReportName("rep_dataactivity_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.activityCrudMetaData.Activity"));
        }
        return metaLabel; 
    }

}