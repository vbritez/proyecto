package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataProduct;

/**
 * 
 * @author Miguel Zorrilla

 */
@ManagedBean(name = "crudMetaDataProductBean")
@ViewScoped
public class CrudMetaDataProductBean extends AbstractCrudMetaDataBean<DataProduct> implements Serializable {

    private static final long serialVersionUID = 1277474674526106831L;
    private static final Long COD_META = 2L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataProductBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_dataproduct");
        setXlsReportName("rep_dataproduct_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.productCrudMetaData.Product"));
        }
        return metaLabel;
    }
}
