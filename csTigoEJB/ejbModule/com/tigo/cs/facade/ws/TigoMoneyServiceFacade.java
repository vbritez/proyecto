package com.tigo.cs.facade.ws;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.facade.ws.TigoMoneyWSServiceAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class TigoMoneyServiceFacade extends TigoMoneyWSServiceAPI {

    private static final long serialVersionUID = 220739272480434861L;

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @PostConstruct
    protected void init() throws InvalidOperationException {
        super.postConstruct();
    }

}
