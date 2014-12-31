package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.tigo.cs.api.facade.I18nAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Singleton
@DependsOn("EJBFacadeContainer")
public class I18nBean extends I18nAPI {
    
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public I18nBean() {
    }

}
