package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.CollectionInvoiceService;
import com.tigo.cs.api.entities.CollectionPaymentService;
import com.tigo.cs.api.entities.CollectionService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.CollectionServiceAPI;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.COBRANZA)
public class CollectionDriver extends CollectionServiceAPI<CollectionService> implements DriverInterface {

    private static final String COBRANZA_COD_CLIENTE = "COBRANZA-COD-CLIENTE";
    private static final String COBRANZA_NRO_RECIBO = "COBRANZA-NRO-RECIBO";
    private static final String COBRANZA_FACTURA_NUMERO_ = "COBRANZA-FACTURA-NUMERO-";
    private static final String COBRANZA_FACTURA_MONTO_ = "COBRANZA-FACTURA-MONTO-";
    private static final String COBRANZA_EFECTIVO_MONTO_ = "COBRANZA-EFECTIVO-MONTO-";
    private static final String COBRANZA_CHEQUE_MONTO_ = "COBRANZA-CHEQUE-MONTO-";
    private static final String COBRANZA_CHEQUE_BANCO_ = "COBRANZA-CHEQUE-BANCO-";
    private static final String COBRANZA_CHEQUE_NRO_CHEQUE_ = "COBRANZA-CHEQUE-NRO-CHEQUE-";
    private static final String COBRANZA_CHEQUE_FECHA_CHEQUE_ = "COBRANZA-CHEQUE-FECHA-CHEQUE-";
    private static final String COBRANZA_CHEQUE_VENC_CHEQUE_ = "COBRANZA-CHEQUE-VENC-CHEQUE-";
    private static final String COBRANZA_CONFIRMACION_REGISTRO = "COBRANZA-CONFIRMACION-REGISTRO";
    private static final String COBRANZA_FACTURA_OPCIONES = "COBRANZA-FACTURA-OPCIONES";
    private static final String COBRANZA_ACTIVO_OPCIONES = "COBRANZA-ACTIVO-OPCIONES";
    private static final String COBRANZA_OPCIONES = "COBRANZA-OPCIONES";
    private static final String COBRANZA_OPCIONES_FACTURA = "COBRANZA-OPCIONES-FACTURA";
    private static final String COBRANZA_OPCIONES_ACTIVO = "COBRANZA-OPCIONES-ACTIVO";
    

    @EJB
    protected FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
        String option = getNodeValue(COBRANZA_OPCIONES);
        String operation;
        
        if (option != null && option.equals(COBRANZA_OPCIONES_FACTURA)){
            operation = getNodeValue(COBRANZA_FACTURA_OPCIONES);
        }else if (option != null && option.equals(COBRANZA_OPCIONES_ACTIVO)){
            operation = getNodeValue(COBRANZA_ACTIVO_OPCIONES);
        }else{
            operation = null;
        }
        
        if (operation != null && operation.equals(COBRANZA_CONFIRMACION_REGISTRO)) {
            getEntity().setEvent("OPEN");
        }
        else{
            getEntity().setEvent("CLOSE");
        }
    }

    @Override
    public void convertToBean() {
        getEntity().setClientCode(getNodeValue(COBRANZA_COD_CLIENTE));
        getEntity().setReceiptNumber(getNodeValue(COBRANZA_NRO_RECIBO));
        
        getEntity().setInvoices(new ArrayList<CollectionInvoiceService>());
        getEntity().setPayments(new ArrayList<CollectionPaymentService>());

        if (getNodeValue(COBRANZA_FACTURA_NUMERO_.concat("1")) != null) {
            for (int i = 1; i <= 5; i++) {
                if (getNodeValue(COBRANZA_FACTURA_NUMERO_.concat(Integer.toString(i))) != null) {
                    CollectionInvoiceService invoice = new CollectionInvoiceService();

                    invoice.setInvoiceNumber(getNodeValue(COBRANZA_FACTURA_NUMERO_.concat(Integer.toString(i))));
                    invoice.setInvoiceAmmount(getNodeValue(COBRANZA_FACTURA_MONTO_.concat(Integer.toString(i))));

                    getEntity().getInvoices().add(invoice);
                } else {
                    break;
                }
            }
        }

        if (getNodeValue(COBRANZA_EFECTIVO_MONTO_.concat("1")) != null) {
            for (int i = 1; i <= 5; i++) {
                if (getNodeValue(COBRANZA_EFECTIVO_MONTO_.concat(Integer.toString(i))) != null) {
                    CollectionPaymentService payment = new CollectionPaymentService();
                    payment.setCollectionType("1");
                    payment.setCollectionTypeAmmount(getNodeValue(COBRANZA_EFECTIVO_MONTO_.concat(Integer.toString(i))));
                    getEntity().getPayments().add(payment);
                } else {
                    break;
                }

            }
        }

        if (getNodeValue(COBRANZA_CHEQUE_MONTO_.concat("1")) != null) {
            for (int i = 1; i <= 5; i++) {
                if (getNodeValue(COBRANZA_CHEQUE_MONTO_.concat(Integer.toString(i))) != null) {
//                    getEntity().setPayments(new ArrayList<CollectionPaymentService>());
                    CollectionPaymentService payment = new CollectionPaymentService();
                    payment.setCollectionType("2");

                    payment.setCollectionTypeAmmount(getNodeValue(COBRANZA_CHEQUE_MONTO_.concat(Integer.toString(i))));
                    payment.setBankCode(getNodeValue(COBRANZA_CHEQUE_BANCO_.concat(Integer.toString(i))));
                    payment.setCheckNumber(getNodeValue(COBRANZA_CHEQUE_NRO_CHEQUE_.concat(Integer.toString(i))));
                    payment.setCheckDate(getNodeValue(COBRANZA_CHEQUE_FECHA_CHEQUE_.concat(Integer.toString(i))));
                    payment.setCheckExpirationDate(getNodeValue(COBRANZA_CHEQUE_VENC_CHEQUE_.concat(Integer.toString(i))));
                    getEntity().getPayments().add(payment);
                } else {
                    break;
                }
            }
        }

    }

}
