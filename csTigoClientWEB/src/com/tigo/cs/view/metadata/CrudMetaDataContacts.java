package com.tigo.cs.view.metadata;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.view.DataContacts;

@ManagedBean(name = "crudMetaDataContactsBean")
@ViewScoped
public class CrudMetaDataContacts extends AbstractCrudMetaDataBean<DataContacts> implements Serializable {

    private static final long serialVersionUID = 4421848163376876801L;
    private static final Long COD_META = 16L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataContacts() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_datacontacts");
        setXlsReportName("rep_datacontacts_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.contactsCrudMetaData.Contacts"));
        }
        return metaLabel; 
    }

}