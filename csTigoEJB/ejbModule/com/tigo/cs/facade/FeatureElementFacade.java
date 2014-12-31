package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.FeatureElementAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@DependsOn("EJBFacadeContainer")
@Stateless
public class FeatureElementFacade extends FeatureElementAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public FeatureElementFacade() {
    }
}
