package com.tigo.cs.facade.ws;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ws.RetrievePoliceBaseInfoAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class RetrievePoliceBaseInfoFacade extends RetrievePoliceBaseInfoAPI {

    private static final long serialVersionUID = 1L;
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
