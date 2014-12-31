package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.InventoryService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class InventoryStdServiceAPI<T extends InventoryService> extends AbstractServiceAPI<InventoryService> {

    protected InventoryEvent tranType;

    protected enum InventoryEvent implements ServiceEvent {

        REGISTER(1, new ServiceProps("inventorystd.name.Register", "inventorystd.message.Register", ""));
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

    public InventoryStdServiceAPI() {
    }

    @Override
    public InventoryService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new InventoryService());
        }
        return super.getEntity();
    }

    @Override
    public InventoryService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new InventoryService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        Long ref = null;

        ServiceValue serviceValue = treatHeader();
        treatDetails(serviceValue);

        ref = serviceValue.getServicevalueCod();

        String deposit = getMetaForIntegrationValue(
                getEntity().getDepositCode(), MetaNames.DEPOSIT);

        String product = getMetaForIntegrationValue(
                getEntity().getProductCode(), MetaNames.PRODUCT);

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());

        returnMessage = MessageFormat.format(returnMessage, deposit, product,
                ref);
        addRouteDetail(getUserphone(), getMessage().getCell());

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
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
        serviceValueDetail.setServiceValue(serviceValue);
        serviceValueDetail.setMessage(getMessage());
        serviceValueDetail.setRecorddateDat(validateDate());
        serviceValueDetail.setColumn1Chr(getEntity().getDepositCode());
        serviceValueDetail.setColumn2Chr(getEntity().getProductCode());
        serviceValueDetail.setColumn3Chr(getEntity().getQuantity());
        svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                serviceValueDetail));
        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        tranType = InventoryEvent.REGISTER;
        getReturnEntity().setEvent("REGISTER");
    }

}
