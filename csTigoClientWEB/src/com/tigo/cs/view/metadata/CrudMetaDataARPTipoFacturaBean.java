package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataArpTipoFactura;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CrudMetaDataARPTipoFacturaBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "crudMetaDataARPTipoFacturaBean")
@ViewScoped
public class CrudMetaDataARPTipoFacturaBean extends AbstractCrudMetaDataBean<DataArpTipoFactura> implements Serializable {

    private static final long serialVersionUID = -497591370794300316L;
    private static final Long COD_META = 6L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataARPTipoFacturaBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_dataarp");
        setXlsReportName("rep_dataarp_xls");

    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.ARPInvoiceTypeCrudMetaData"));
        }
        return metaLabel;
    }

}
