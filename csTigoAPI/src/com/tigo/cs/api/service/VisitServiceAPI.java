package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tigo.cs.api.entities.VisitService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class VisitServiceAPI<T extends VisitService> extends AbstractServiceAPI<VisitService> {

    protected VisitEvent tranType;
    private ServiceValueDetail svd = null;

    protected enum VisitEvent implements ServiceEvent {

        IN(1, new ServiceProps("visit.name.In", "visit.message.In", "")),
        OUT(2, new ServiceProps("visit.name.Out", "visit.message.Out", "")),
        FAST_IN_OUT(3, new ServiceProps("visit.name.FastInOut", "visit.message.FastInOut", ""));
        protected final int value;
        protected final ServiceProps props;

        private VisitEvent(int value, ServiceProps props) {
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

    public VisitServiceAPI() {
    }

    @Override
    public VisitService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new VisitService());
        }
        return super.getEntity();
    }

    @Override
    public VisitService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new VisitService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        ServiceValue serviceValue = treatHeader();
        List<ServiceValueDetail> svdList = treatDetails(serviceValue);

        String client = getMetaForIntegrationValue(getEntity().getClientCode(),
                MetaNames.CLIENT);
        String duracion = svdList.get(0).getColumn5Chr() != null ? svdList.get(
                0).getColumn5Chr() : "";
        Long ref = svdList.get(0).getServicevaluedetailCod();

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());
        returnMessage = MessageFormat.format(returnMessage, client, duracion,
                ref);

        if (tranType.equals(VisitEvent.OUT)
            || tranType.equals(VisitEvent.FAST_IN_OUT)) {
            addRouteDetail(getUserphone(), getMessage().getCell());
        }
        return returnMessage;
    }

    @Override
    protected void validate() throws InvalidOperationException, OperationNotAllowedException {
        super.validate();

        svd = getFacadeContainer().getServiceValueDetailAPI().getServiceValueDetailList(
                getUserphone(), getService(),
                " AND s.column1Chr in ('ENT','SAL') ");

        // NO PERMITE MARCAR DOS ENTRADAS DE SEGUIDO
        if (svd != null
            && svd.getColumn1Chr().equals("ENT")
            && (getEntity().getEvent().equals("ENT") || getEntity().getEvent().equals(
                    "ENTSAL"))) {

            String client = getMetaForIntegrationValue(svd.getColumn2Chr(),
                    MetaNames.CLIENT);
            throw new InvalidOperationException(MessageFormat.format(
                    MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "visit.InvalidOperationPendingVisit"),
                            client), svd.getColumn2Chr()));
        }
        // NO PERMITE MARCAR DOS SALIDAS DE SEGUIDO
        if (svd != null && svd.getColumn1Chr().equals("SAL")
            && getEntity().getEvent().equals("SAL")) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "visit.InvalidOperationNoPendingVisit"));
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
        serviceValueDetail.setColumn1Chr(getEntity().getEvent());
        serviceValueDetail.setRecorddateDat(validateDate());

        if (tranType.equals(VisitEvent.OUT)) {
            getEntity().setClientCode(svd.getColumn2Chr());

            /*
             * formateamos el periodo transcurrido entre la marcacion de entrada
             * y la fecha/hora actual
             */
            serviceValueDetail.setColumn5Chr(DateUtil.getPeriod(
                    svd.getRecorddateDat().getTime(), new Date().getTime()));

            /*
             * guardar duracion entre ENT y SAL en milisegundos, en column6
             */
            Date actual = new Date();
            Long duration = actual.getTime() - svd.getRecorddateDat().getTime();
            serviceValueDetail.setColumn6Chr(duration.toString());

            /*
             * verificamos si se realizo la marcacion en el mismo sitio que la
             * marcacion de entrada
             */

            serviceValueDetail.setColumn7Chr(getFacadeContainer().getCellAPI().isSameSite(
                    svd.getMessage().getCell(), getCell()) ? "1" : "0");

        }
        serviceValueDetail.setColumn2Chr(getEntity().getClientCode());
        serviceValueDetail.setColumn3Chr(getEntity().getMotiveCode());
        serviceValueDetail.setColumn4Chr(getEntity().getObservation());

        svdList.add(getFacadeContainer().getServiceValueDetailAPI().create(
                serviceValueDetail));

        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        String discriminator = getEntity().getEvent();
        if (discriminator.equals("ENT")) {
            tranType = VisitEvent.IN;
        } else if (discriminator.equals("SAL")) {
            tranType = VisitEvent.OUT;
        } else if (discriminator.equals("ENTSAL")) {
            tranType = VisitEvent.FAST_IN_OUT;
        }
        getReturnEntity().setEvent(getEntity().getEvent());
    }

}
