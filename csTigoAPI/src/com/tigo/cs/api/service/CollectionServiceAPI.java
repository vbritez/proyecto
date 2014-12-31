package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.CollectionInvoiceService;
import com.tigo.cs.api.entities.CollectionPaymentService;
import com.tigo.cs.api.entities.CollectionService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class CollectionServiceAPI<T extends CollectionService> extends AbstractServiceAPI<CollectionService> {

    protected CollectionEvent tranType;
    private ServiceValue openCollection;

    protected enum CollectionEvent implements ServiceEvent {

        COLLECTION_REGISTRATION(1, new ServiceProps("collection.name.Register", "collection.message.Register", "")),
        COLLECTION_OPEN_REGISTRATION(2, new ServiceProps("collection.name.Open", "collection.message.Open", "")),
        COLLECTION_CLOSE(2, new ServiceProps("collection.name.Close", "collection.message.Close", ""));
        protected final int value;
        protected final ServiceProps props;

        private CollectionEvent(int value, ServiceProps props) {
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

    public CollectionServiceAPI() {
    }

    @Override
    public CollectionService getEntity() {

        if (super.getEntity() == null) {
            setEntity(new CollectionService());
            super.getEntity().setInvoices(
                    new ArrayList<CollectionInvoiceService>());
            super.getEntity().setPayments(
                    new ArrayList<CollectionPaymentService>());
            openCollection = null;
        }
        return super.getEntity();
    }

    @Override
    public CollectionService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new CollectionService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        String client = null;
        Long ref = null;

        openCollection = treatHeader();
        treatDetails(openCollection);

        switch (tranType) {
        case COLLECTION_CLOSE:
            openCollection = getFacadeContainer().getServiceValueAPI().closeOpenCollection(
                    openCollection);
            break;
        case COLLECTION_OPEN_REGISTRATION:
        case COLLECTION_REGISTRATION:
            break;
        }

        addRouteDetail(getUserphone(), getMessage().getCell());

        client = getMetaForIntegrationValue(getEntity().getClientCode(),
                MetaNames.CLIENT);
        ref = openCollection.getServicevalueCod();

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());
        returnMessage = MessageFormat.format(returnMessage, client, ref);
        return returnMessage;

    }

    @Override
    protected void validate() throws InvalidOperationException {
        super.validate();

        openCollection = getFacadeContainer().getServiceValueAPI().getOpenCollection(
                getUserphone(), getService());

        /*
         * Se verifica que si es un cierre de cobranza que provenga de Android,
         * se debe setear el cod cliente y recibo, porque desde android se envia
         * vacio
         */
        if (getEntity().getAndroid()
            && (tranType.equals(CollectionEvent.COLLECTION_CLOSE)
                || tranType.equals(CollectionEvent.COLLECTION_OPEN_REGISTRATION) || tranType.equals(CollectionEvent.COLLECTION_REGISTRATION))
            && openCollection != null) {
            getEntity().setClientCode(openCollection.getColumn1Chr());
            getEntity().setReceiptNumber(openCollection.getColumn2Chr());
        }

        String codCliente = getEntity().getClientCode();
        String nroRecibo = getEntity().getReceiptNumber();

        if (tranType.equals(CollectionEvent.COLLECTION_OPEN_REGISTRATION)
            && openCollection != null) {
            String msj = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "collection.message.InvalidOperationMustCloseLastCollection"),
                    codCliente, nroRecibo);
            throw new InvalidOperationException(msj);
        }

        if ((codCliente == null || codCliente.isEmpty())
            && (nroRecibo == null || nroRecibo.isEmpty())) {
            if (openCollection == null) {
                String msj = getFacadeContainer().getI18nAPI().iValue(
                        "collection.message.MustOpenCollection");
                throw new InvalidOperationException(msj);
            }
        } else {
            if (openCollection != null
                && (openCollection.getColumn1Chr().trim().compareTo(codCliente) != 0 || openCollection.getColumn2Chr().trim().compareTo(
                        nroRecibo) != 0)) {
                String msj = getFacadeContainer().getI18nAPI().iValue(
                        "collection.message.InvalidOperationMustCloseLastCollection");
                throw new InvalidOperationException(MessageFormat.format(msj,
                        openCollection.getColumn1Chr(),
                        openCollection.getColumn2Chr()));
            }
        }

        if (tranType.equals(CollectionEvent.COLLECTION_CLOSE)
            && openCollection != null
            && (openCollection.getColumn1Chr().trim().compareTo(codCliente) != 0 || openCollection.getColumn2Chr().trim().compareTo(
                    nroRecibo) != 0)) {
            String msj = getFacadeContainer().getI18nAPI().iValue(
                    "collection.message.InvalidOperationTransactionToCloseDoesNotCorrespondToAnOpen");
            throw new InvalidOperationException(MessageFormat.format(msj,
                    openCollection.getColumn1Chr(),
                    openCollection.getColumn2Chr()));
        }

    }

    @Override
    protected ServiceValue treatHeader() {
        if (openCollection == null) {
            openCollection = new ServiceValue();
            openCollection.setService(getService());
            openCollection.setUserphone(getUserphone());
            openCollection.setMessage(getMessage());
            openCollection.setRecorddateDat(validateDate());
            openCollection.setColumn1Chr(getEntity().getClientCode());
            openCollection.setColumn2Chr(getEntity().getReceiptNumber());
            if (tranType.equals(CollectionEvent.COLLECTION_OPEN_REGISTRATION)) {
                openCollection.setColumn3Chr("1");
            }
            openCollection = getFacadeContainer().getServiceValueAPI().create(
                    openCollection);
        }
        return openCollection;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {

        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();

        if (getEntity().getInvoices() != null) {
            for (CollectionInvoiceService invoice : getEntity().getInvoices()) {

                if (invoice.getInvoiceNumber() == null) {
                    continue;
                }
                ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
                serviceValueDetail.setServiceValue(serviceValue);
                serviceValueDetail.setMessage(getMessage());
                serviceValueDetail.setRecorddateDat(validateDate());
                serviceValueDetail.setColumn1Chr("INVOICE");
                serviceValueDetail.setColumn2Chr(invoice.getInvoiceNumber());
                serviceValueDetail.setColumn3Chr(invoice.getInvoiceAmmount());

                svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                        serviceValueDetail));
            }
        }

        if (getEntity().getPayments() != null) {
            for (CollectionPaymentService payment : getEntity().getPayments()) {

                if (payment.getCollectionTypeAmmount() == null) {
                    continue;
                }

                ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
                serviceValueDetail.setServiceValue(serviceValue);
                serviceValueDetail.setMessage(getMessage());
                serviceValueDetail.setRecorddateDat(validateDate());
                serviceValueDetail.setColumn1Chr("PAYMENT");
                serviceValueDetail.setColumn2Chr(payment.getCollectionType());
                serviceValueDetail.setColumn3Chr(payment.getCollectionTypeAmmount());
                serviceValueDetail.setColumn4Chr(payment.getBankCode());
                serviceValueDetail.setColumn5Chr(payment.getCheckNumber());
                serviceValueDetail.setColumn6Chr(payment.getCheckDate());
                serviceValueDetail.setColumn7Chr(payment.getCheckExpirationDate());
                serviceValueDetail.setColumn8Chr(payment.getObservation());

                svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                        serviceValueDetail));
            }
        }

        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        String discriminator = getEntity().getEvent();
        if (discriminator != null && discriminator.equals("OPEN")) {
            getReturnEntity().setEvent("OPEN");
            tranType = CollectionEvent.COLLECTION_OPEN_REGISTRATION;
        } else if (discriminator != null && discriminator.equals("CLOSE")) {
            getReturnEntity().setEvent("CLOSE");
            tranType = CollectionEvent.COLLECTION_CLOSE;
        } else {
            getReturnEntity().setEvent("REG");
            tranType = CollectionEvent.COLLECTION_REGISTRATION;
        }

    }
}
