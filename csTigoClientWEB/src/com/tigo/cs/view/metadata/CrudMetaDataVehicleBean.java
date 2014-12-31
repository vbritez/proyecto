package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataVehicle;

/**
 * 
 * @author Miguel Zorrilla
 */
@ManagedBean(name = "crudMetaDataVehicleBean")
@ViewScoped
public class CrudMetaDataVehicleBean extends AbstractCrudMetaDataBean<DataVehicle> implements Serializable {

    private static final long serialVersionUID = -9188440122523148185L;
    private static final Long COD_META = 8L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataVehicleBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_datavehicle");
        setXlsReportName("rep_datavehicle_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.vehicleCrudMetaData.Vehicle"));
        }
        return metaLabel;
    }
}
