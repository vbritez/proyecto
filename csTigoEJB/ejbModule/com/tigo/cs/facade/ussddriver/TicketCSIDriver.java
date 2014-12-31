package com.tigo.cs.facade.ussddriver;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.TicketCSIService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.TicketCSIServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.TICKET_CSI)
public class TicketCSIDriver extends TicketCSIServiceAPI<TicketCSIService> implements DriverInterface {

    private static final String SC_CSI = "SC_CSI";
    private static final String DESCRIPTION = "DESCRIPTION";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    protected void assignEvent() {
        tranType = TicketCSIEvent.REGISTER;
    }

    @Override
    public void convertToBean() {
        getEntity().setTicketArea(getNodeValue(SC_CSI));
        getEntity().setTicketCategory(getNodeValue(getEntity().getTicketArea()));
        getEntity().setDescription(getNodeValue(DESCRIPTION));
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

}
