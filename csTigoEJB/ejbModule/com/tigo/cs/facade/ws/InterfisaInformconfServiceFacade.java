package com.tigo.cs.facade.ws;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ws.InterfisaInformconfServiceAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class InterfisaInformconfServiceFacade extends InterfisaInformconfServiceAPI {

    @EJB
    protected FacadeContainer facadeContainer;

    private static final long serialVersionUID = -5137747365035867803L;

    public InterfisaInformconfServiceFacade() {
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

}
