package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.TrackingConfigurationAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@DependsOn("EJBFacadeContainer")
@Stateless
public class TrackingConfigurationFacade extends TrackingConfigurationAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public TrackingConfigurationFacade() {
    }

}
