package com.tigo.cs.facade.ws;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ws.LbsGwServiceAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class LbsGwServiceFacade extends LbsGwServiceAPI implements Serializable {

    private static final long serialVersionUID = 6547633429093172258L;
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    public LbsGwServiceFacade() {

    }

}
