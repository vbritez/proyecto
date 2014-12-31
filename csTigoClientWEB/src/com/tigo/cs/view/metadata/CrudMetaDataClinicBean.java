package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataClinic;

/**
 * 
 * @author Miguel Maciel
 */
@ManagedBean(name = "crudMetaDataClinicBean")
@ViewScoped
public class CrudMetaDataClinicBean extends AbstractCrudMetaDataBean<DataClinic> implements Serializable {

    private static final long serialVersionUID = 1277474674526106831L;
    private static final Long COD_META = 14L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataClinicBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_dataclinic");
        setXlsReportName("rep_dataclinic_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.clientCrudMetaData.Local"));
        }
        return metaLabel;
    }
}
