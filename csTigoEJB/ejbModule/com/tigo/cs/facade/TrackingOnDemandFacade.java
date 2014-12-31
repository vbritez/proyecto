package com.tigo.cs.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ws.TrackingOnDemandAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;


@Stateless
public class TrackingOnDemandFacade extends TrackingOnDemandAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public TrackingOnDemandFacade() {
    }
}
