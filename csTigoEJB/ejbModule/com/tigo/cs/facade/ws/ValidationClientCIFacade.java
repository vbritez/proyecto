package com.tigo.cs.facade.ws;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ws.ValidationClientCIAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class ValidationClientCIFacade extends ValidationClientCIAPI {

    private static final long serialVersionUID = 1L;
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }
}
