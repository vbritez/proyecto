package com.tigo.cs.facade.ws;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.ws.ValidacionClienteBCCSNacServiceAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
@DependsOn("EJBFacadeContainer")
public class ValidacionClienteBCCSNacServiceFacade extends ValidacionClienteBCCSNacServiceAPI {

    private static final long serialVersionUID = 4420429824642207895L;
    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

}
