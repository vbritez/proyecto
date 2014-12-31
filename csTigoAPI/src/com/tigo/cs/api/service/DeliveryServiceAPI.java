package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.DeliveryDetailService;
import com.tigo.cs.api.entities.DeliveryService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class DeliveryServiceAPI<T extends DeliveryService> extends AbstractServiceAPI<DeliveryService> {

    private DeliveryEvent tranType;

    protected enum DeliveryEvent implements ServiceEvent {

        DELIVERY_REGISTRATION(1, new ServiceProps("delivery.name.Register", "delivery.message.Register", ""));
        protected final int value;
        protected final ServiceProps serviceProps;

        private DeliveryEvent(int value, ServiceProps serviceProps) {
            this.value = value;
            this.serviceProps = serviceProps;
        }

        @Override
        public String getSuccessMessage() {
            return serviceProps.getSuccesMessage();
        }

        @Override
        public String getNoMatchMessage() {
            return serviceProps.getNoMatchMessage();
        }

        @Override
        public String getDescription() {
            return serviceProps.getDescription();
        }
    }

    public DeliveryServiceAPI() {
    }

    @Override
    public DeliveryService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new DeliveryService());
        }
        return super.getEntity();
    }

    @Override
    public DeliveryService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new DeliveryService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        ServiceValue serviceValue = treatHeader();
        treatDetails(serviceValue);

        String client = getMetaForIntegrationValue(getEntity().getClientCode(),
                MetaNames.CLIENT);
        Long ref = serviceValue.getServicevalueCod();

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());
        returnMessage = MessageFormat.format(returnMessage, client, ref);

        addRouteDetail(getUserphone(), getMessage().getCell());
        return returnMessage;

    }

    @Override
    protected ServiceValue treatHeader() {

        ServiceValue serviceValue = null;
        serviceValue = new ServiceValue();
        serviceValue.setService(getService());
        serviceValue.setUserphone(getUserphone());
        serviceValue.setMessage(getMessage());
        serviceValue.setRecorddateDat(validateDate());
        serviceValue.setColumn1Chr(getEntity().getClientCode());
        serviceValue.setColumn2Chr(getEntity().getObservation());
        serviceValue = getFacadeContainer().getServiceValueAPI().create(
                serviceValue);
        return serviceValue;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        for (DeliveryDetailService detail : getEntity().getDetails()) {

            ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
            serviceValueDetail.setRecorddateDat(validateDate());
            serviceValueDetail.setServiceValue(serviceValue);
            serviceValueDetail.setMessage(getMessage());
            serviceValueDetail.setColumn1Chr(detail.getRemissionNumber());
            svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                    serviceValueDetail));
        }
        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        tranType = DeliveryEvent.DELIVERY_REGISTRATION;
    }

}
