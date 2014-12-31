package com.tigo.cs.api.service;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.entities.OrderService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class OrderServiceAPI<T extends OrderService> extends AbstractServiceAPI<OrderService> {

    protected OrderEvent tranType;
    protected Double totalPedido;
    protected Double total;
    private ServiceValue currentServiceValue;

    protected enum OrderEvent implements ServiceEvent {

        ORDER_REGISTRATION(1, new ServiceProps("order.name.Register", "order.message.Register", "")),
        ORDER_REGISTRATION_REST(2, new ServiceProps("order.name.Register", "order.message.Register", "")),
        ORDER_SESSION_INIT(3, new ServiceProps("order.name.Init", "order.message.Init", "")),
        ORDER_SESSION_FINISH(4, new ServiceProps("order.name.Finish", "order.message.End", ""));
        protected final int value;
        protected final ServiceProps props;

        private OrderEvent(int value, ServiceProps props) {
            this.value = value;
            this.props = props;
        }

        @Override
        public String getSuccessMessage() {
            return props.getSuccesMessage();
        }

        @Override
        public String getNoMatchMessage() {
            return props.getNoMatchMessage();
        }

        @Override
        public String getDescription() {
            return props.getDescription();
        }
    }

    public OrderServiceAPI() {
    }

    @Override
    public OrderService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new OrderService());
        }
        return super.getEntity();
    }

    @Override
    public OrderService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new OrderService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        String client = "";
        Long ref = null;

        switch (tranType) {
        case ORDER_REGISTRATION_REST:
        case ORDER_REGISTRATION:
            ServiceValue serviceValue = treatHeader();

            currentServiceValue = serviceValue != null ? serviceValue : currentServiceValue;
            getEntity().setClientCode(currentServiceValue.getColumn1Chr());

            treatDetails(currentServiceValue);

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());
            client = getMetaForIntegrationValue(getEntity().getClientCode(),
                    MetaNames.CLIENT);
            ref = currentServiceValue.getServicevalueCod();

            returnMessage = MessageFormat.format(returnMessage, client,
                    currentServiceValue.getColumn4Chr(), ref);
            break;

        case ORDER_SESSION_INIT:
            currentServiceValue = treatHeaderIni();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());
            client = getMetaForIntegrationValue(getEntity().getClientCode(),
                    MetaNames.CLIENT);
            ref = currentServiceValue.getServicevalueCod();

            returnMessage = MessageFormat.format(returnMessage, client, ref);

            break;

        case ORDER_SESSION_FINISH:
            currentServiceValue = treatHeaderEnd();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());
            client = getMetaForIntegrationValue(
                    currentServiceValue.getColumn1Chr(), MetaNames.CLIENT);
            ref = currentServiceValue.getServicevalueCod();

            returnMessage = MessageFormat.format(returnMessage, client,
                    currentServiceValue.getColumn4Chr(), ref);

            break;

        }
        addRouteDetail(getUserphone(), getMessage().getCell());
        return returnMessage;
    }

    private ServiceValue treatHeaderIni() {
        // EN CASO DE QUE SEA UN PEDIDO DE AGUA BES O PEDIDO EXTENDIDO
        ServiceValue serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentServiceValue(
                getUserphone(), getService());
        if (serviceValue == null) {
            serviceValue = new ServiceValue();
            serviceValue.setService(getService());
            serviceValue.setUserphone(getUserphone());
            serviceValue.setMessage(getMessage());
            serviceValue.setRecorddateDat(validateDate());
            serviceValue.setColumn1Chr(getEntity().getClientCode());
            serviceValue.setColumn2Chr(getEntity().getInvoiceType());
            serviceValue.setColumn3Chr(getEntity().getPriceList());
            serviceValue.setColumn4Chr("0");
            serviceValue.setColumn6Chr(getEntity().getObservation());

            serviceValue.setColumn9Chr("0"); // Estado de Pedido Iniciado
            serviceValue = getFacadeContainer().getServiceValueAPI().create(
                    serviceValue);
        }
        return serviceValue;
    }

    private ServiceValue treatHeaderEnd() {
        ServiceValue serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentServiceValue(
                getUserphone(), getService());
        try {
            /*
             * asignamos el valor en el campo observacion
             */
            if (getEntity().getObservation() != null) {
                serviceValue.setColumn6Chr(getEntity().getObservation());
            }
            serviceValue.setColumn9Chr("1"); // Estado de Pedido Finalizado
            serviceValue = getFacadeContainer().getServiceValueAPI().edit(
                    serviceValue);
            Collection<ServiceValueDetail> svds = serviceValue.getServiceValueDetailCollection();

            total = 0.0;
            for (ServiceValueDetail svd : svds) {
                try {
                    total += Double.parseDouble(svd.getColumn5Chr());
                } catch (Exception e) {
                    /*
                     * no hay total del detalle
                     */
                }
            }
            serviceValue.setColumn4Chr(total.toString());
            getFacadeContainer().getServiceValueAPI().edit(serviceValue);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(),
                    getCellphoneNumber().toString(),
                    "Error al cerrar el pedido", e);
        }
        return serviceValue;
    }

    @Override
    protected ServiceValue treatHeader() {

        // EN CASO DE QUE SEA UN PEDIDO NORMAL CON O SIN OBSERVACION, SIN EVENTO
        ServiceValue serviceValue = null;
        if (getEntity().getEvent() == null
            || tranType.equals(OrderEvent.ORDER_REGISTRATION_REST)) { // si es
                                                                      // un
                                                                      // pedido
                                                                      // o
                                                                      // pedido
                                                                      // con
            // obs
            serviceValue = new ServiceValue();
            serviceValue.setService(getService());
            serviceValue.setUserphone(getUserphone());
            serviceValue.setMessage(getMessage());
            serviceValue.setRecorddateDat(validateDate());
            serviceValue.setColumn1Chr(getEntity().getClientCode());
            serviceValue.setColumn2Chr(getEntity().getInvoiceType());
            serviceValue.setColumn3Chr(getEntity().getPriceList());
            serviceValue.setColumn6Chr(getEntity().getObservation());
            getFacadeContainer().getServiceValueAPI().create(serviceValue);
        }
        return serviceValue;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) throws InvalidOperationException {
        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        ServiceValueDetail serviceValueDetail;
        OrderService entityOrder = getEntity();
        /*
         * por cada producto del pedido recorremos asignamos sus valores en la
         * lista de detalles
         * 
         * el primer elemento de los detalles se refiere a la lista de precio y
         * lo dejamos de lado en este proceso
         */
        /*
         * inicializamos el monto total del pedido actual
         */
        totalPedido = 0.0;
        total = 0.0;

        for (OrderDetailService subDetail : entityOrder.getDetail()) {
            try {
                /*
                 * ej. con el primer producto del pedido subDetalle[0] = 22020
                 * subDetalle[1] = 20 subDetalle[2] = 10 subDetalle[3] = 1500
                 */
                serviceValueDetail = new ServiceValueDetail();
                serviceValueDetail.setServiceValue(serviceValue);
                serviceValueDetail.setMessage(getMessage());
                serviceValueDetail.setRecorddateDat(validateDate());
                /*
                 * subDetalle[0] : codigo del producto subDetalle[1] : cantidad
                 * subDetalle[2] : descuento subDetalle[3] : precio unitario
                 */
                try {
                    Double cantidad = Double.parseDouble(subDetail.getQuantity());
                    Double descuento = Double.parseDouble(subDetail.getDiscount());
                    Double precioUnitario = Double.parseDouble(subDetail.getUnitPrice());
                    total = (precioUnitario * cantidad)
                        - ((precioUnitario * cantidad) * descuento / 100.0);
                    totalPedido += total;
                    serviceValueDetail.setColumn5Chr(total.toString());
                } catch (NumberFormatException ex) {
                    // NO se pudo calcular el monto del pedido
                } catch (Exception ex) {
                    // NO se pudo calcular el monto del pedido
                }
                serviceValueDetail.setColumn1Chr(subDetail.getProductCode());
                /*
                 * cantidad
                 */
                serviceValueDetail.setColumn2Chr(subDetail.getQuantity());
                /*
                 * descuento
                 */
                serviceValueDetail.setColumn3Chr(subDetail.getDiscount());
                /*
                 * precio unitario
                 */
                serviceValueDetail.setColumn4Chr(subDetail.getUnitPrice());

                /*
                 * agregamos los detalles a la lista pero aun no es persistida
                 */
                svdList.add(serviceValueDetail);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException(getFacadeContainer().getI18nAPI().iValue(
                        "error.WrongMessageDetailStructure"), e);
            } catch (Exception ex) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "error.DetailErrorDueInternalError"));
            }
        }

        if (svdList != null && svdList.size() > 0) {
            /*
             * agregamos al pedido el monto total del mismo
             */
            if (totalPedido != null) {
                DecimalFormat df = new DecimalFormat("#,###.##");
                serviceValue.setColumn4Chr(df.format(totalPedido));
            }

            /*
             * recien aqui se persisten los datos de la cabecera
             */
            getFacadeContainer().getServiceValueAPI().edit(serviceValue);

            /*
             * se persisten los datos del detalle
             */
            for (ServiceValueDetail svd : svdList) {
                getFacadeContainer().getServiceValueDetailAPI().create(svd);
            }
        }
        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        if (getEntity().getEvent() != null
            && getEntity().getEvent().equals("INI")) {
            tranType = OrderEvent.ORDER_SESSION_INIT;
        } else if (getEntity().getEvent() != null
            && getEntity().getEvent().equals("FIN")) {
            tranType = OrderEvent.ORDER_SESSION_FINISH;
        } else if (getEntity().getEvent() != null
            && getEntity().getEvent().equals("END")) {
            tranType = OrderEvent.ORDER_SESSION_FINISH;
        } else if (getEntity().getEvent() != null
            && getEntity().getEvent().equals("REGISTER")) {
            tranType = OrderEvent.ORDER_REGISTRATION_REST;
        } else {
            tranType = OrderEvent.ORDER_REGISTRATION;
        }
        getReturnEntity().setEvent(getEntity().getEvent());
    }

    @Override
    public void validate() throws InvalidOperationException {
        super.validate();

        currentServiceValue = getFacadeContainer().getServiceValueAPI().getCurrentServiceValue(
                getUserphone(), getService());

        /*
         * tiene una session abierta actualmente, verificar cual es el evento
         * que se esta tratando actualmente
         */
        if (currentServiceValue != null) {
            if (tranType.equals(OrderEvent.ORDER_SESSION_INIT)) {
                String msjPattern = getFacadeContainer().getI18nAPI().iValue(
                        "order.AlreadyOpenedSession");

                String clientName = getMetaForIntegrationValue(
                        currentServiceValue.getColumn1Chr(), MetaNames.CLIENT);

                returnMessage = MessageFormat.format(msjPattern, clientName);

                throw new InvalidOperationException(returnMessage);

            }
        }
        /*
         * en caso de que no tenga una sesion abierta
         */
        else {
            if (getEntity().getEvent() != null) { // solo si es pedido con
                                                  // sesion
                if (tranType.equals(OrderEvent.ORDER_REGISTRATION)) {
                    throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                            "order.CannotRegisterOrEnd"));
                } else if (tranType.equals(OrderEvent.ORDER_SESSION_FINISH)) {
                    ServiceValue lastServiceValue = getFacadeContainer().getServiceValueAPI().getLastServiceValue(
                            getUserphone(), getService());
                    if (lastServiceValue.getColumn9Chr() != null
                        && lastServiceValue.getColumn9Chr().equals("1")) {
                        throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                                "order.AlreadyClosedSession"));
                    } else {
                        throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                                "order.CannotRegisterOrEnd"));
                    }
                }
            }
        }

    }

}
