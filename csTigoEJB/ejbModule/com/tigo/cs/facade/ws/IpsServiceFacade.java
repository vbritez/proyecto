package com.tigo.cs.facade.ws;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ws.IpsServiceAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class IpsServiceFacade extends IpsServiceAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    private static final long serialVersionUID = -5137747365035867803L;

    public IpsServiceFacade() {
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
}
