package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataBank;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CrudMetaDataBankBean.java 91 2011-11-28 20:16:12Z javier.kovacs
 *          $
 */
@ManagedBean(name = "crudMetaDataBankBean")
@ViewScoped
public class CrudMetaDataBankBean extends AbstractCrudMetaDataBean<DataBank> implements Serializable {

    private static final long serialVersionUID = -5296666309412997259L;
    private static final Long COD_META = 9L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataBankBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_databank");
        setXlsReportName("rep_databank_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.bankCrudMetaData.Bank"));
        }
        return metaLabel;
    }

}
