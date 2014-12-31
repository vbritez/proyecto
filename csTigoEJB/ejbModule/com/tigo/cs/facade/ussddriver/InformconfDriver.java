package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.InformconfService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.InformconfServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.INFORMCONF)
public class InformconfDriver extends InformconfServiceAPI<InformconfService> implements DriverInterface {

    private static final String INFORMCONF_CEDULA = "INFORMCONF_CEDULA";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
        getEntity().setCedula(getNodeValue(INFORMCONF_CEDULA));

    }

}
