package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.CollectionPaymentService;
import com.tigo.cs.api.entities.CollectionService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.CollectionServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.COBRANZA)
public class MiddleCollectionDriver extends CollectionServiceAPI<CollectionService> implements DriverInterface {

    private static final String COBRANZA_COD_CLIENTE = "COBRANZA-COD-CLIENTE";
    private static final String COBRANZA_NRO_RECIBO = "COBRANZA-NRO-RECIBO";
    private static final String COBRANZA_FACTURA_MONTO = "COBRANZA-FACTURA-MONTO";
    private static final String COBRANZA_ACTIVO_OPCIONES = "COBRANZA-ACTIVO-OPCIONES";
    private static final String COBRANZA_ACTIVO_EFECTIVO = "COBRANZA-ACTIVO-EFECTIVO";
    private static final String COBRANZA_CHEQUE_BANCO = "COBRANZA-CHEQUE-BANCO";
    private static final String COBRANZA_CHEQUE_NRO_CHEQUE = "COBRANZA-CHEQUE-NRO-CHEQUE";
    private static final String COBRANZA_OBSERVACION = "COBRANZA-OBSERVACION";

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
        getEntity().setClientCode(getNodeValue(COBRANZA_COD_CLIENTE));
        getEntity().setReceiptNumber(getNodeValue(COBRANZA_NRO_RECIBO));
        
        CollectionPaymentService payment = new CollectionPaymentService();
        payment.setCollectionType(getNodeValue(COBRANZA_ACTIVO_OPCIONES).equals(
                COBRANZA_ACTIVO_EFECTIVO) ? "1" : "2");
        payment.setCollectionTypeAmmount(getNodeValue(COBRANZA_FACTURA_MONTO));
        payment.setBankCode(getNodeValue(COBRANZA_CHEQUE_BANCO));
        payment.setCheckNumber(getNodeValue(COBRANZA_CHEQUE_NRO_CHEQUE));
        payment.setObservation(getNodeValue(COBRANZA_OBSERVACION));
        
        getEntity().setPayments(new ArrayList<CollectionPaymentService>());
        getEntity().getPayments().add(payment);

    }

}
