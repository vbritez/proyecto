package com.tigo.cs.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.facade.WnClienteAPI;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.domain.WnCliente;
import com.tigo.cs.domain.WnOperadora;

@Stateless
public class WnClienteFacade extends WnClienteAPI {

    @EJB
    protected FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    public WnClienteFacade() {
    }

    public List<WnCliente> findByOperadora(WnOperadora wnOperadora) {
        finderParams = prepareParams();
        finderParams.put("wnOperadora", wnOperadora);
        return findListWithNamedQuery("WnCliente.findByWnOperadora",
                finderParams);
    }
}
