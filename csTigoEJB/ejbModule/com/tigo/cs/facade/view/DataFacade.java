package com.tigo.cs.facade.view;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.DataAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class DataFacade extends DataAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public DataFacade() {
    }

}
