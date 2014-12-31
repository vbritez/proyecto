package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.CourrierService;
import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.CourierServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.COURRIER)
public class CourrierDriver extends CourierServiceAPI<CourrierService> implements DriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    private static final String COURRIER_NOMBRE_RECEPTOR = "COURRIER_NOMBRE_RECEPTOR";
    private static final String COURRIER_OBSERVACION = "COURRIER_OBSERVACION";
    private static final String COURRIER_MOTIVO = "COURRIER_MOTIVO";
    private static final String COURRIER_ITEM_ = "COURRIER_ITEM_";

    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
        getEntity().setReceiverName(getNodeValue(COURRIER_NOMBRE_RECEPTOR));
        getEntity().setObservation(getNodeValue(COURRIER_OBSERVACION));
        getEntity().setMotiveCode(getNodeValue(COURRIER_MOTIVO));

        List<OrderDetailService> detailList = new ArrayList<OrderDetailService>();
        for (int i = 1; i <= 5; i++) {
            if (getNodeValue(COURRIER_ITEM_.concat(Integer.toString(i))) != null) {
                OrderDetailService detail = new OrderDetailService();
                detail.setProductCode(getNodeValue(COURRIER_ITEM_.concat(Integer.toString(i))));
                detailList.add(detail);
            }
        }
        getEntity().setDetail(detailList);
    }

}
