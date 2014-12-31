package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.CourrierService;
import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class CourierServiceAPI<T extends CourrierService> extends AbstractServiceAPI<CourrierService> {

    protected CourierEvent tranType;

    protected enum CourierEvent implements ServiceEvent {

        DELIVERED(1, new ServiceProps("courier.name.Delivered", "courier.message.Delivered", ""));
        protected final int value;
        protected final ServiceProps props;

        private CourierEvent(int value, ServiceProps props) {
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

    public CourierServiceAPI() {
    }

    @Override
    public CourrierService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new CourrierService());
        }
        return super.getEntity();
    }

    @Override
    public CourrierService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new CourrierService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        ServiceValue header = treatHeader();
        treatDetails(header);

        Long ref = header.getServicevalueCod();

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());
        returnMessage = MessageFormat.format(returnMessage, ref);

        addRouteDetail(getUserphone(), getMessage().getCell());

        return returnMessage;
    }

    @Override
    protected ServiceValue treatHeader() {
        ServiceValue serviceValue = new ServiceValue();
        serviceValue.setService(getService());
        serviceValue.setUserphone(getUserphone());
        serviceValue.setMessage(getMessage());
        serviceValue.setRecorddateDat(validateDate());

        /*
         * asignamos los valores del servicio a la cabecera
         */

        serviceValue.setColumn1Chr(getEntity().getReceiverName());
        serviceValue.setColumn2Chr(getEntity().getObservation());
        serviceValue.setColumn3Chr(getEntity().getMotiveCode());

        serviceValue = getFacadeContainer().getServiceValueAPI().create(
                serviceValue);
        return serviceValue;

    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {

        List<ServiceValueDetail> details = new ArrayList<ServiceValueDetail>();
        for (OrderDetailService item : getEntity().getDetail()) {

            ServiceValueDetail itemDetail = new ServiceValueDetail();
            itemDetail.setServiceValue(serviceValue);
            itemDetail.setMessage(getMessage());
            itemDetail.setRecorddateDat(validateDate());
            itemDetail.setColumn1Chr(item.getProductCode());
            itemDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                    itemDetail);
            details.add(itemDetail);
        }

        return details;
    }

    @Override
    protected void assignServiceEvent() {
        tranType = CourierEvent.DELIVERED;
        getReturnEntity().setEvent("PR");
    }

}
