package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.InterfisaService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class InterfisaServiceAPI<T extends InterfisaService> extends AbstractServiceAPI<InterfisaService> {

    protected InterfisaEvent tranType;

    protected enum InterfisaEvent implements ServiceEvent {

        INFORMCONF(1, new ServiceProps("interfisa.name.Informconf", "interfisa.message.Informconf", ""));
        protected final int value;
        protected final ServiceProps props;

        private InterfisaEvent(int value, ServiceProps props) {
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

    public InterfisaServiceAPI() {
    }

    @Override
    public InterfisaService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new InterfisaService());
        }
        return super.getEntity();
    }

    @Override
    public InterfisaService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new InterfisaService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        String response = getFacadeContainer().getInterfisaInformconfServiceAPI().sendQuery(
                getEntity().getDocument(),
                CellPhoneNumberUtil.correctMsisdnToLocalFormat(getEntity().getMsisdn()));

        ServiceValue serviceValue = treatHeader();
        List<ServiceValueDetail> svdList = treatDetails(serviceValue);

        Long ref = svdList.get(0).getServicevaluedetailCod();

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());
        returnMessage = MessageFormat.format(returnMessage, response, ref);

        return returnMessage;
    }

    @Override
    protected void validate() throws InvalidOperationException, OperationNotAllowedException {
        super.validate();

        if (getEntity().getDocument() == null
            || getEntity().getDocument().trim().length() <= 0) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "interfisa.NoValidDocument"));
        }

    }

    @Override
    protected ServiceValue treatHeader() throws InvalidOperationException {
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
        serviceValueDetail.setMessage(getMessage());
        serviceValueDetail.setRecorddateDat(validateDate());
        serviceValueDetail.setColumn1Chr(getEntity().getEvent());
        serviceValueDetail.setColumn2Chr(getEntity().getDocument());
        svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                serviceValueDetail));

        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        String discriminator = getEntity().getEvent();
        if (discriminator.equals("INFORMCONF")) {
            tranType = InterfisaEvent.INFORMCONF;
        }
    }

}
