package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataGuard;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CrudMetaDataGuardBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "crudMetaDataGuardBean")
@ViewScoped
public class CrudMetaDataGuardBean extends AbstractCrudMetaDataBean<DataGuard> implements Serializable {

    private static final long serialVersionUID = 2765575061701097148L;
    private static final Long COD_META = 4L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataGuardBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_dataguard");
        setXlsReportName("rep_dataguard_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.guarCrudMetaData.Guard"));
        }
        return metaLabel;
    }
}
