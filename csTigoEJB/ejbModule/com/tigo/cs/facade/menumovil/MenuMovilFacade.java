package com.tigo.cs.facade.menumovil;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.tigo.cs.api.facade.MenuMovilAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Singleton
public class MenuMovilFacade extends MenuMovilAPI{

    private static final long serialVersionUID = -7119436957709693887L;
    @EJB
    protected FacadeContainer facadeContainer;

    public MenuMovilFacade() {
    }

    @Override
    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
