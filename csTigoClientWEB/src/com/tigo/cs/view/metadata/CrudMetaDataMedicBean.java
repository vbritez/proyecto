package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataMedic;

/**
 * 
 * @author Miguel Zorrilla
 */
@ManagedBean(name = "crudMetaDataMedicBean")
@ViewScoped
public class CrudMetaDataMedicBean extends AbstractCrudMetaDataBean<DataMedic> implements Serializable {

    private static final long serialVersionUID = 1277474674526106831L;
    private static final Long COD_META = 15L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataMedicBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_datamedic");
        setXlsReportName("rep_datamedic_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.guarCrudMetaData.Medic"));
        }
        return metaLabel;
    }
}
