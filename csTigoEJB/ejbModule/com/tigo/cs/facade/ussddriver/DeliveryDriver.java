package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.DeliveryDetailService;
import com.tigo.cs.api.entities.DeliveryService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.DeliveryServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.DELIVERY)
public class DeliveryDriver extends DeliveryServiceAPI<DeliveryService> implements DriverInterface {

    private static final String ENTREGA_CODIGO_CLIENTE = "ENTREGA-CODIGO-CLIENTE";
    private static final String ENTREGA_OBSERVACION = "ENTREGA-OBSERVACION";
    private static final String ENTREGA_REMISION_ = "ENTREGA-REMISION-";

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
        getEntity().setClientCode(getNodeValue(ENTREGA_CODIGO_CLIENTE));
        getEntity().setObservation(getNodeValue(ENTREGA_OBSERVACION));
        getEntity().setDetails(new ArrayList<DeliveryDetailService>());
        for (int i = 1; i <= 5; i++) {
            if (getNodeValue(ENTREGA_REMISION_.concat(Integer.toString(i))) != null) {
                if (getEntity().getDetails() == null) {
                    getEntity().setDetails(
                            new ArrayList<DeliveryDetailService>());
                }
                DeliveryDetailService detail = new DeliveryDetailService();

                detail.setRemissionNumber(getNodeValue(ENTREGA_REMISION_.concat(Integer.toString(i))));
                getEntity().getDetails().add(detail);
            }
        }
    }

}
