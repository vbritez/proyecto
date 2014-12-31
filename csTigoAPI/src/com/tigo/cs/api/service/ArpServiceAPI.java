package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.ARPEnrollmentService;
import com.tigo.cs.api.entities.ARPGuideService;
import com.tigo.cs.api.entities.ARPService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class ArpServiceAPI<T extends ARPService> extends AbstractServiceAPI<ARPService> {

    protected ArpEvent tranType;

    protected enum ArpEvent implements ServiceEvent {

        REGISTER(1, new ServiceProps("arp.name.Register", "arp.message.Register", ""));
        protected final int value;
        protected final ServiceProps props;

        private ArpEvent(int value, ServiceProps props) {
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

    public ArpServiceAPI() {
    }

    @Override
    public ARPService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new ARPService());
        }
        return super.getEntity();
    }

    @Override
    public ARPService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new ARPService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        ServiceValue sv = treatHeader();
        treatDetails(sv);

        Long ref = sv.getServicevalueCod();

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());

        returnMessage = MessageFormat.format(returnMessage, ref);

        return returnMessage;
    }

    @Override
    protected ServiceValue treatHeader() {

        ServiceValue serviceValue = new ServiceValue();
        serviceValue.setService(getService());
        serviceValue.setUserphone(getUserphone());
        serviceValue.setMessage(getMessage());
        serviceValue.setRecorddateDat(validateDate());

        serviceValue.setColumn1Chr(getEntity().getInvoiceNumber());
        serviceValue.setColumn2Chr(getEntity().getInvoiceTypeCode());
        return serviceValue;

    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        ServiceValueDetail serviceValueDetail;

        Double montoTotal = 0.0;
        for (ARPGuideService guide : getEntity().getGuideList()) {
            serviceValueDetail = new ServiceValueDetail();
            serviceValueDetail.setServiceValue(serviceValue);
            serviceValueDetail.setMessage(getMessage());
            serviceValueDetail.setRecorddateDat(validateDate());

            serviceValueDetail.setColumn1Chr("GC");
            serviceValueDetail.setColumn2Chr(guide.getGuide());
            serviceValueDetail.setColumn3Chr(guide.getDimension());
            serviceValueDetail.setColumn4Chr(guide.getAmmount());
            try {
                montoTotal += Double.parseDouble(guide.getAmmount());
            } catch (NumberFormatException ex) {
            }

            svdList.add(serviceValueDetail);
        }

        for (ARPEnrollmentService enrollment : getEntity().getEnrollmentList()) {
            serviceValueDetail = new ServiceValueDetail();
            serviceValueDetail.setServiceValue(serviceValue);
            serviceValueDetail.setMessage(getMessage());
            serviceValueDetail.setRecorddateDat(validateDate());
            serviceValueDetail.setColumn1Chr("M");
            serviceValueDetail.setColumn2Chr(enrollment.getEnrollment());
            svdList.add(serviceValueDetail);
        }

        serviceValue.setColumn3Chr(String.valueOf(montoTotal.intValue()));
        getFacadeContainer().getServiceValueAPI().create(serviceValue);

        for (ServiceValueDetail svd : svdList) {
            getFacadeContainer().getServiceValueDetailAPI().create(svd);
        }

        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        tranType = ArpEvent.REGISTER;
        getReturnEntity().setEvent("REGISTER");
    }

}
