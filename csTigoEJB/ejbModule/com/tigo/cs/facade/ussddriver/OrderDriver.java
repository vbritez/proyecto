package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.entities.OrderService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.OrderServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.PEDIDO)
public class OrderDriver extends OrderServiceAPI<OrderService> implements DriverInterface {

    @EJB
    private FacadeContainer ussdFacadeContainer;

    private static final String PEDIDO_CLIENTE = "PEDIDO-CLIENTE";
    private static final String PEDIDO_COND_VENTA = "PEDIDO-COND-VENTA";
    private static final String PEDIDO_LISTA_PRECIO = "PEDIDO-LISTA-PRECIO";
    private static final String PEDIDO_OBSERVACION = "PEDIDO-OBSERVACION";
    private static final String PEDIDO_PRODUCTO = "PEDIDO-PRODUCTO";
    private static final String PEDIDO_CANTIDAD = "PEDIDO-CANTIDAD";
    private static final String PEDIDO_PRECIO = "PEDIDO-PRECIO";
    private static final String PEDIDO_DESCUENTO = "PEDIDO-DESCUENTO";

    @Override
    public void convertToBean() {
        getEntity().setClientCode(getNotNullNodeValueFrom(PEDIDO_CLIENTE));
        getEntity().setInvoiceType(getNotNullNodeValueFrom(PEDIDO_COND_VENTA));
        getEntity().setPriceList(getNotNullNodeValueFrom(PEDIDO_LISTA_PRECIO));
        getEntity().setObservation(getNotNullNodeValueFrom(PEDIDO_OBSERVACION));

        getEntity().setDetail(new ArrayList<OrderDetailService>());

        for (int i = 1; i < 6; i++) {
            if (getNodeValue(PEDIDO_PRODUCTO + i) != null) {
                OrderDetailService det = new OrderDetailService();
                det.setProductCode(getNodeValue(PEDIDO_PRODUCTO + i));
                det.setQuantity(getNodeValue(PEDIDO_CANTIDAD + i));
                det.setUnitPrice(getNodeValue(PEDIDO_PRECIO + i));
                det.setDiscount(getNodeValue(PEDIDO_DESCUENTO + i));
                ((OrderService) getEntity()).getDetail().add(det);
            }
        }
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    @Override
    protected void assignEvent() {
        getEntity().setEvent(null);
    }

}
