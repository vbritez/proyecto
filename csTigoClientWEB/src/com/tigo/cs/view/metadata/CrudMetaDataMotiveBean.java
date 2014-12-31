package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataMotive;

/**
 * 
 * @author Miguel Zorrilla
 */
@ManagedBean(name = "crudMetaDataMotiveBean")
@ViewScoped
public class CrudMetaDataMotiveBean extends AbstractCrudMetaDataBean<DataMotive> implements Serializable {

    private static final long serialVersionUID = -8906377144695353814L;
    private static final Long COD_META = 3L; // MOTIVO
    private static final Long COD_MEMBER = 1L; // DESCRIPCION

    public CrudMetaDataMotiveBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_datamotive");
        setXlsReportName("rep_datamotive_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.motiveCrudMetaData.Motive"));
        }
        return metaLabel;
    }
}
