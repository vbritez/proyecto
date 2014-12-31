package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.entities.VisitOrderService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.VisitOrderServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.VISITA_PEDIDO)
public class VisitOrderDriver extends VisitOrderServiceAPI<VisitOrderService> implements DriverInterface {

    private static final String VISITA = "VISITA";

    private static final String VISITAPEDIDO_IN_CODCLIENTE = "VISITAPEDIDO-IN-CODCLIENTE";
    private static final String VISITAPEDIDO_IN_OBS = "VISITAPEDIDO-IN-OBS";

    private static final String VISITAPEDIDO_FIN_OBS = "VISITAPEDIDO-FIN-OBS";
    private static final String VISITAPEDIDO_FIN_MOTIVO = "VISITAPEDIDO-FIN-MOTIVO";

    private static final String PEDIDO_COND_VENTA = "VISITAPEDIDO-COND-VENTA";
    private static final String PEDIDO_LISTA_PRECIO = "VISITAPEDIDO-LISTA-PRECIO";
    private static final String PEDIDO_PRODUCTO = "VISITAPEDIDO-PRODUCTO";
    private static final String PEDIDO_CANTIDAD = "VISITAPEDIDO-CANTIDAD";
    private static final String PEDIDO_PRECIO = "VISITAPEDIDO-PRECIO";
    private static final String PEDIDO_DESCUENTO = "VISITAPEDIDO-DESCUENTO";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
        String discriminator = getNodeValue(VISITA);
        if (discriminator != null) {
            if (discriminator.equals("VISITAPEDIDO-INICIO")) {
                getEntity().getVisit().setEvent("ENT");
            } else if (discriminator.equals("VISITAPEDIDO-FIN")) {
                getEntity().getVisit().setEvent("SAL");
            }
        }else
            getEntity().getVisit().setEvent("REGISTRO");
    }

    @Override
    public void convertToBean() {
        getEntity().getVisit().setClientCode(
                getNotNullNodeValueFrom(VISITAPEDIDO_IN_CODCLIENTE));
        getEntity().getVisit().setMotiveCode(
                getNotNullNodeValueFrom(VISITAPEDIDO_FIN_MOTIVO));
        getEntity().getVisit().setObservation(
                getNotNullNodeValueFrom(VISITAPEDIDO_IN_OBS,
                        VISITAPEDIDO_FIN_OBS));
        getEntity().getOrder().setInvoiceType(
                getNotNullNodeValueFrom(PEDIDO_COND_VENTA));
        getEntity().getOrder().setPriceList(
                getNotNullNodeValueFrom(PEDIDO_LISTA_PRECIO));
        if (tranType.equals(VisitorderEvent.ORDER_REGISTRATION)) {
            for (int i = 1; i < 6; i++) {
                if (getNodeValue(PEDIDO_PRODUCTO + i) != null) {
                    if (getEntity().getOrder().getDetail() == null) {
                        getEntity().getOrder().setDetail(
                                new ArrayList<OrderDetailService>());
                    }
                    OrderDetailService detail = new OrderDetailService();
                    detail.setProductCode(getNodeValue(PEDIDO_PRODUCTO + i));
                    detail.setQuantity(getNodeValue(PEDIDO_CANTIDAD + i));
                    detail.setUnitPrice(getNodeValue(PEDIDO_PRECIO + i));
                    detail.setDiscount(getNodeValue(PEDIDO_DESCUENTO + i));
                    getEntity().getOrder().getDetail().add(detail);
                } else {
                    break;
                }
            }
        }
    }

}
