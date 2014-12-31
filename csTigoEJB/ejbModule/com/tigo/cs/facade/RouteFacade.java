package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.RouteAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@DependsOn("EJBFacadeContainer")
@Stateless
public class RouteFacade extends RouteAPI {
    
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public RouteFacade() {
    }
}
