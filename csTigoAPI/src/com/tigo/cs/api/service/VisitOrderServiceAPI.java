package com.tigo.cs.api.service;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.entities.OrderService;
import com.tigo.cs.api.entities.VisitOrderService;
import com.tigo.cs.api.entities.VisitService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.service.OrderServiceAPI.OrderEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class VisitOrderServiceAPI<T extends VisitOrderService> extends AbstractServiceAPI<VisitOrderService> {

    private Double totalPedido;
    private Double totalProducto;
    protected VisitorderEvent tranType;
    private ServiceValueDetail svd = null;
    private String totalVisita;

    protected enum VisitorderEvent implements ServiceEvent {

        IN(1, new ServiceProps("visitorder.name.In", "visitorder.message.In", "")),
        OUT(2, new ServiceProps("visitorder.name.Out", "visitorder.message.Out", "")),
        ORDER_REGISTRATION(3, new ServiceProps("visitorder.name.Registration", "visitorder.message.Registration", ""));
        protected final int value;
        protected final ServiceProps props;

        private VisitorderEvent(int value, ServiceProps props) {
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

    public VisitOrderServiceAPI() {

    }

    @Override
    public VisitOrderService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new VisitOrderService());
            super.getEntity().setVisit(new VisitService());
            super.getEntity().setOrder(new OrderService());
        }
        return super.getEntity();
    }

    @Override
    public VisitOrderService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new VisitOrderService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        if (tranType.equals(VisitorderEvent.OUT)) {
            addRouteDetail(getUserphone(), getMessage().getCell());
        }
        svd = getFacadeContainer().getServiceValueDetailAPI().getServiceValueDetailList(
                getUserphone(), getService(),
                " AND s.column1Chr in ('ENT','SAL') ");

        String total = "0";
        Long ref = null;
        String duracion = "";
        String client = "";
        switch (tranType) {
        case OUT:
            getEntity().getVisit().setClientCode(svd.getColumn2Chr());

        case IN:
            ServiceValue serviceValue = treatHeader();
            List<ServiceValueDetail> svdList = treatDetails(serviceValue);

            client = getMetaForIntegrationValue(
                    getEntity().getVisit().getClientCode(), MetaNames.CLIENT);
            duracion = svdList.get(0).getColumn7Chr() != null ? svdList.get(0).getColumn7Chr() : "";
            total = totalVisita != null ? totalVisita : "0";
            ref = svdList.get(0).getServicevaluedetailCod();

            break;
        case ORDER_REGISTRATION:
            svdList = createOrders();

            client = getMetaForIntegrationValue(svd.getColumn2Chr(),
                    MetaNames.CLIENT);
            ref = svdList.get(0).getServiceValue().getServicevalueCod();
            total = totalPedido != null ? String.valueOf(totalPedido.intValue()) : "0";

            returnMessage = MessageFormat.format(tranType.getSuccessMessage(),
                    String.valueOf(totalPedido.intValue()), ref);
            break;
        }

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());
        returnMessage = MessageFormat.format(returnMessage, client, duracion,
                total, ref);

        return returnMessage;
    }

    @Override
    protected void validate() throws InvalidOperationException {
        super.validate();

        if (!tranType.equals(OrderEvent.ORDER_REGISTRATION)) {
            svd = getFacadeContainer().getServiceValueDetailAPI().getServiceValueDetailList(
                    getUserphone(), getService(),
                    " AND s.column1Chr in ('ENT','SAL') ");

            String lastEvent = null;
            String currentEvent = null;
            String lastClient = null;
            currentEvent = getEntity().getVisit().getEvent();

            if (svd != null) {
                lastEvent = svd.getColumn1Chr();
                lastClient = svd.getColumn2Chr();
                lastClient = getMetaForIntegrationValue(lastClient,
                        MetaNames.CLIENT);
            }

            if (svd != null && lastEvent.equals("ENT")
                && (currentEvent.equals("ENT"))) {
                throw new InvalidOperationException(MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "visit.InvalidOperationPendingVisit"),
                        lastClient));
            } else if (svd != null && lastEvent.equals("SAL")
                && currentEvent.equals("SAL")) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "visit.InvalidOperationNoPendingVisit"));
            }
            if ((svd == null) && currentEvent.equals("SAL")) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "visit.InvalidOperationNoPendingVisit"));
            }
        }
    }

    protected List<ServiceValueDetail> createOrders() throws InvalidOperationException {
        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        ServiceValueDetail serviceValueDetail;

        if (svd != null && svd.getColumn1Chr().equals("ENT")) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String fechaRegistro = df.format(svd.getRecorddateDat());
            String fechaHoy = df.format(Calendar.getInstance().getTime());
            if (fechaRegistro.equals(fechaHoy)) {
                ServiceValue serviceValue = new ServiceValue();

                serviceValue.setService(getService());
                serviceValue.setUserphone(getUserphone());
                serviceValue.setMessage(getMessage());
                serviceValue.setRecorddateDat(validateDate());
                serviceValue.setColumn1Chr("SD");
                serviceValue.setColumn2Chr(getEntity().getOrder().getInvoiceType());
                serviceValue.setColumn3Chr(getEntity().getOrder().getPriceList());
                serviceValue.setColumn4Chr(svd.getServiceValue().getServicevalueCod().toString());
                serviceValue.setColumn5Chr(svd.getServicevaluedetailCod().toString());

                totalPedido = 0.0;
                for (OrderDetailService detail : getEntity().getOrder().getDetail()) {
                    totalProducto = 0.0;

                    serviceValueDetail = new ServiceValueDetail();
                    serviceValueDetail.setServiceValue(serviceValue);
                    serviceValueDetail.setMessage(getMessage());
                    serviceValueDetail.setRecorddateDat(validateDate());
                    serviceValueDetail.setColumn1Chr(detail.getProductCode());
                    serviceValueDetail.setColumn2Chr(detail.getQuantity());
                    serviceValueDetail.setColumn3Chr(detail.getDiscount());
                    serviceValueDetail.setColumn4Chr(detail.getUnitPrice());

                    Double cantidad = Double.parseDouble(detail.getQuantity());
                    Double descuento = Double.parseDouble(detail.getDiscount());
                    Double precioUnitario = Double.parseDouble(detail.getUnitPrice());

                    /*
                     * mantenemos los valores Double para el caso de considerar
                     * valores decimales, pero se almacenan solo enteros.
                     */
                    totalProducto = ((precioUnitario * cantidad) - ((precioUnitario * cantidad)
                        * descuento / 100.0));
                    totalPedido += totalProducto;
                    serviceValueDetail.setColumn5Chr(Integer.toString(totalProducto.intValue()));
                    svdList.add(serviceValueDetail);

                }

                /*
                 * agregamos el total del pedido y persistimos los datos
                 */

                /*
                 * tambien mantenemos el valor Double para el caso de considerar
                 * valores decimales, pero se almacenan solo enteros
                 */

                serviceValue.setColumn6Chr(Integer.toString(totalPedido.intValue()));
                serviceValue = getFacadeContainer().getServiceValueAPI().create(
                        serviceValue);

                /*
                 * persistimos los detalles del pedido
                 */
                for (ServiceValueDetail svd : svdList) {
                    getFacadeContainer().getServiceValueDetailAPI().create(svd);
                }

            } else {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "visitorder.InvalidOperationYestedayPendingVisit"));
            }
        } else {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "visitorder.InvalidOperationNoOpenedVisit"));
        }

        return svdList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ServiceValue treatHeader() {
        ServiceValue serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentServiceValue(
                getUserphone(), getService());
        if (serviceValue == null) {
            serviceValue = new ServiceValue();
            serviceValue.setService(getService());
            serviceValue.setUserphone(getUserphone());
            serviceValue.setMessage(getMessage());
            serviceValue.setRecorddateDat(validateDate());
            serviceValue.setColumn1Chr("APERTURA");
            serviceValue = getFacadeContainer().getServiceValueAPI().create(
                    serviceValue);
        }
        return serviceValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {

        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        ServiceValueDetail serviceValueDetail = new ServiceValueDetail();

        serviceValueDetail.setServiceValue(serviceValue);
        serviceValueDetail.setMessage(getMessage());
        serviceValueDetail.setRecorddateDat(validateDate());
        serviceValueDetail.setColumn1Chr(getEntity().getVisit().getEvent());
        serviceValueDetail.setColumn2Chr(getEntity().getVisit().getClientCode());
        serviceValueDetail.setColumn3Chr(getEntity().getVisit().getMotiveCode());
        serviceValueDetail.setColumn4Chr(getEntity().getVisit().getObservation());
        /*
         * si el registro anterior es una entrada almacenamos la referencia al
         * codigo de este en la columna 5 del regsitro de salida
         */
        if (svd != null && svd.getColumn1Chr().equals("ENT")) {
            totalVisita = getFacadeContainer().getServiceValueAPI().getVisitOrderTotal(
                    getUserphone(), getService(), serviceValue);
            serviceValueDetail.setColumn5Chr(svd.getServicevaluedetailCod().toString());
            serviceValueDetail.setColumn6Chr(totalVisita);
            serviceValueDetail.setColumn7Chr(DateUtil.getPeriod(
                    svd.getRecorddateDat().getTime(), new Date().getTime()));
        }
        svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                serviceValueDetail));

        return svdList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void assignServiceEvent() {

        String discriminator = getEntity().getVisit().getEvent();

        if (discriminator.equals("ENT")) {
            tranType = VisitorderEvent.IN;
            getReturnEntity().setEvent("ENT");
        } else if (discriminator.equals("SAL")) {
            tranType = VisitorderEvent.OUT;
            getReturnEntity().setEvent("SAL");
        } else {
            tranType = VisitorderEvent.ORDER_REGISTRATION;
            getReturnEntity().setEvent("REGISTER");
        }
    }

}
