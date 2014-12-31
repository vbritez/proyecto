package com.tigo.cs.facade.ncenter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.tigo.cs.api.facade.ws.NCenterWSAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Singleton
public class NCenterWSFacade extends NCenterWSAPI {

    private static final long serialVersionUID = 2662934059995005712L;
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    @PostConstruct
    public void postConstruct() {
        super.postConstruct();
    }


    public NCenterWSFacade() {
    }
}
