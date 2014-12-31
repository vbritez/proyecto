package com.tigo.cs.facade;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ClientFeatureAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.domain.ClientFeature;

@Stateless
@DependsOn("EJBFacadeContainer")
public class ClientFeatureFacade extends ClientFeatureAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public ClientFeatureFacade() {
    }

}
