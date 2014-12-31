package com.tigo.cs.view;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.tigo.cs.api.facade.I18nAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@ManagedBean(name = "iBean")
@ApplicationScoped
public class I18nWebBean extends I18nAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public I18nWebBean() {
    }
}
