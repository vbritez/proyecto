package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.List;

import com.tigo.cs.api.entities.TicketCSIService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class TicketCSIServiceAPI<T extends TicketCSIService> extends AbstractServiceAPI<TicketCSIService> {

    protected TicketCSIEvent tranType;

    protected enum TicketCSIEvent implements ServiceEvent {

        REGISTER(1, new ServiceProps("ticketcsi.name.Register", "ticketcsi.message.Register", ""));
        protected final int value;
        protected final ServiceProps props;

        private TicketCSIEvent(int value, ServiceProps props) {
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

    @Override
    public TicketCSIService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new TicketCSIService());
        }
        return super.getEntity();
    }

    @Override
    public TicketCSIService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new TicketCSIService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                "ticketcsi.error");
        try {
            ServiceValue serviceValue = treatHeader();

            String pattern = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());
            returnMessage = MessageFormat.format(pattern,
                    serviceValue.getColumn4Chr(),
                    serviceValue.getServicevalueCod());

        } catch (Exception ex) {
            getFacadeContainer().getNotifier().error(TicketCSIServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "ticketcsi.error"));

        }
        return returnMessage;
    }

    @Override
    protected ServiceValue treatHeader() throws InvalidOperationException {

        String categoryCode = null;
        String groupCode = null;
        try {
            categoryCode = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    getEntity().getTicketCategory());

            groupCode = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    getEntity().getTicketCategory().concat(".group"));

        } catch (Exception ex) {
            getFacadeContainer().getNotifier().error(TicketCSIServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "ticketcsi.error"));

        }

        String ticketAreaName = getFacadeContainer().getI18nAPI().iValue(
                getEntity().getTicketArea());
        String categoryName = getFacadeContainer().getI18nAPI().iValue(
                getEntity().getTicketCategory());

        List<MetaData> associations = getFacadeContainer().getMetaDataAPI().findByUserphone(
                MetaNames.TICKET_CSI_USER.value(), getUserphone());

        if (associations == null
            || (associations != null && associations.size() != 1)) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "restriction.ticketCsi.UserphoneAssociation"));
        }

        MetaData association = associations.get(0);

        MetaClient metaClient = getFacadeContainer().getMetaClientAPI().findByMetaAndClient(
                association.getMetaDataPK().getCodClient(),
                association.getMetaDataPK().getCodMeta());

        if (metaClient == null
            || (metaClient != null && !metaClient.getEnabledChr())) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "restriction.ticketCsi.NoEnabledMeta"));
        }

        MetaData userMeta = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                association.getMetaDataPK().getCodClient(),
                association.getMetaDataPK().getCodMeta(), 3L,
                association.getMetaDataPK().getCodeChr());

        MetaData passwordMeta = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                association.getMetaDataPK().getCodClient(),
                association.getMetaDataPK().getCodMeta(), 4L,
                association.getMetaDataPK().getCodeChr());

        String ticketNumber = getFacadeContainer().getTicketCSIAPI().createTicket(
                userMeta.getValueChr().trim(),
                passwordMeta.getValueChr().trim(), categoryCode.trim(),
                groupCode.trim(), ticketAreaName.trim(),
                getEntity().getDescription().trim());

        ServiceValue serviceValue = null;
        serviceValue = new ServiceValue();
        serviceValue.setService(getService());
        serviceValue.setUserphone(getUserphone());
        serviceValue.setMessage(getMessage());
        serviceValue.setRecorddateDat(validateDate());
        serviceValue.setColumn1Chr(ticketAreaName);
        serviceValue.setColumn2Chr(categoryName);
        serviceValue.setColumn3Chr(getEntity().getDescription());
        serviceValue.setColumn4Chr(ticketNumber);

        getFacadeContainer().getServiceValueAPI().create(serviceValue);
        return serviceValue;

    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        return null;
    }

    @Override
    protected void assignServiceEvent() {
    }

}
