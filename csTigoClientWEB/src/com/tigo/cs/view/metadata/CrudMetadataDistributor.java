package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataDeliveryman;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: CrudMetadataDistributor.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "crudMetadataDistributor")
@ViewScoped
public class CrudMetadataDistributor extends AbstractCrudMetaDataBean<DataDeliveryman> implements Serializable {

    private static final long serialVersionUID = -5785505467004503101L;
    private static final Long COD_META = 5L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetadataDistributor() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_datadelivery");
        setXlsReportName("rep_datadelivery_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.distributorCrudMetaData.Distributor"));
        }
        return metaLabel;
    }
}
