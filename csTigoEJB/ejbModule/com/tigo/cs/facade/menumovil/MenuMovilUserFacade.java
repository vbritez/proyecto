package com.tigo.cs.facade.menumovil;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.MenuMovilUserAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@DependsOn("EJBFacadeContainer")
@Stateless
public class MenuMovilUserFacade extends MenuMovilUserAPI {

    private static final long serialVersionUID = 5167564040050859650L;
    @EJB
    protected FacadeContainer facadeContainer;

    public MenuMovilUserFacade() {
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
