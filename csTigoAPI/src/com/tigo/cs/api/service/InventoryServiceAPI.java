package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.InventoryDAService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class InventoryServiceAPI<T extends InventoryDAService> extends AbstractServiceAPI<InventoryDAService> {

    protected InventoryEvent tranType;

    protected enum InventoryEvent implements ServiceEvent {

        REGISTER(1, new ServiceProps("inventory.name.Register", "inventory.message.Register", ""));
        protected final int value;
        protected final ServiceProps props;

        private InventoryEvent(int value, ServiceProps props) {
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

    public InventoryServiceAPI() {
    }

    @Override
    public InventoryDAService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new InventoryDAService());
        }
        return super.getEntity();
    }

    @Override
    public InventoryDAService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new InventoryDAService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        Long ref = null;
        getFacadeContainer().getServiceValueDetailAPI().getServiceValueDetailList(
                getUserphone(), getService(), " ");
        switch (tranType) {
        case REGISTER:
            ServiceValue serviceValue = treatHeader();
            treatDetails(serviceValue);

            String client = getMetaForIntegrationValue(
                    getEntity().getClientCode(), MetaNames.CLIENT);

            ref = serviceValue.getServicevalueCod();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());
            returnMessage = MessageFormat.format(returnMessage, client, ref);
            break;
        }

        return returnMessage;
    }

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
            serviceValue = getFacadeContainer().getServiceValueAPI().create(
                    serviceValue);
        }
        return serviceValue;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) throws InvalidOperationException {
        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
        serviceValueDetail.setServiceValue(serviceValue);
        serviceValueDetail.setRecorddateDat(validateDate());
        serviceValueDetail.setMessage(getMessage());
        serviceValueDetail.setColumn1Chr(getEntity().getClientCode());
        serviceValueDetail.setColumn2Chr(getEntity().getDealer());
        serviceValueDetail.setColumn3Chr(getEntity().getTrayType());
        serviceValueDetail.setColumn4Chr(getEntity().getLocation());
        serviceValueDetail.setColumn5Chr(getMsgElement(5));
        svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                serviceValueDetail));
        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        tranType = InventoryEvent.REGISTER;
    }

}
