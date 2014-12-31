package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataDeposit;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CrudMetaDataDepositBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "crudMetaDataDepositBean")
@ViewScoped
public class CrudMetaDataDepositBean extends AbstractCrudMetaDataBean<DataDeposit> implements Serializable {

    private static final long serialVersionUID = 4421848163376876801L;
    private static final Long COD_META = 10L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataDepositBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_datadeposit");
        setXlsReportName("rep_datadeposit_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.depositCrudMetaData.Deposit"));
        }
        return metaLabel;
    }

}
