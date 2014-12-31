package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataClient;

/**
 * 
 * @author Miguel Zorrilla
 * 
 */
@ManagedBean(name = "crudMetaDataClientBean")
@ViewScoped
public class CrudMetaDataClientBean extends AbstractCrudMetaDataBean<DataClient> implements Serializable {

    private static final long serialVersionUID = -4929754475483702L;
    private static final Long COD_META = 1L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataClientBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_dataclient");
        setXlsReportName("rep_dataclient_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.clientCrudMetaData.Client"));
        }
        return metaLabel;
    }
}
