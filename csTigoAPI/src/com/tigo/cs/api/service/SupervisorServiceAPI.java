package com.tigo.cs.api.service;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tigo.cs.api.entities.SupervisorService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.exception.TrackingException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userphone;

public abstract class SupervisorServiceAPI<T extends SupervisorService> extends AbstractServiceAPI<SupervisorService> {

    protected TrackingEvent tranType;

    protected enum TrackingEvent implements ServiceEvent {
        USERPHONE_LOCATE(1, "userphone.locate", new ServiceProps("supervisor.name.userphoneLocate", "supervisor.message.UserphoneLocate", ""));

        protected final int value;
        protected final String event;
        protected final ServiceProps props;

        private TrackingEvent(int value, String event, ServiceProps props) {
            this.value = value;
            this.event = event;
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

    public SupervisorServiceAPI() {
    }

    @Override
    public Application getApplication() {
        if (super.getApplication() == null) {
            setApplication(getFacadeContainer().getApplicationAPI().findByKey(
                    "c13n7"));
        }
        return super.getApplication();
    }

    @Override
    public SupervisorService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new SupervisorService());
            getEntity().setSendSMS(false);
        }
        return super.getEntity();
    }

    @Override
    public SupervisorService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new SupervisorService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        returnMessage = null;
        switch (tranType) {
        case USERPHONE_LOCATE:
            /*
             * Invocar aca al Facade de TrackingOndemand que se encargue de
             * realizar el rastreo, misma logica que el TrackingOnDemand
             */

            getReturnEntity().setEvent(TrackingEvent.USERPHONE_LOCATE.event);
            Userphone u = getFacadeContainer().getUserphoneAPI().find(
                    getEntity().getUserphoneCod());
            if (u != null) {
                List<Userphone> list = new ArrayList<Userphone>();
                list.add(u);
                List<ServiceValue> listServiceValue = getFacadeContainer().getTrackingOnDemandAPI().trackMobiles(
                        list);

                if (listServiceValue != null) {
                    for (ServiceValue sv : listServiceValue) {
                        if (sv.getColumn2Chr().equals("0")) {
                            returnMessage = sv.getUserphone().getUserphoneCod().toString();
                            returnMessage += ("%*" + sv.getColumn3Chr());

                            throw new InvalidOperationException(returnMessage);
                        } else {
                            getReturnEntity().setUserphoneCod(
                                    sv.getUserphone().getUserphoneCod());

                            getReturnEntity().setReturnMessage(
                                    sv.getColumn3Chr());
                            if (sv.getMessage() != null) {
                                if (sv.getMessage().getCell() != null) {
                                    getReturnEntity().setLatitudeNum(
                                            sv.getMessage().getCell().getLatitudeNum());
                                    getReturnEntity().setLongitudeNum(
                                            sv.getMessage().getCell().getLongitudeNum());
                                    getReturnEntity().setAzimuth(
                                            sv.getMessage().getCell().getAzimuthNum());
                                    getReturnEntity().setSite(
                                            sv.getMessage().getCell().getSiteChr());
                                    getReturnEntity().setGps(false);

                                } else if (sv.getMessage().getLatitude() != null
                                    && sv.getMessage().getLongitude() != null) {
                                    getReturnEntity().setLatitudeNum(
                                            sv.getMessage().getLatitude());
                                    getReturnEntity().setLongitudeNum(
                                            sv.getMessage().getLongitude());
                                    getReturnEntity().setGps(true);
                                }
                            }
                        }

                        Long ref = sv.getServicevalueCod();

                        String responseMessage = MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        tranType.getSuccessMessage()),
                                sv.getUserphone().getUserphoneCod(),
                                sv.getUserphone().getNameChr(),
                                sv.getUserphone().getCellphoneNum(), ref);

                        StringBuffer sb = new StringBuffer();
                        String pattern = "{0,number,#}%*{1,number,#.00000}%*{2,number,#.00000}%*{3,number,#}%*{4}%*{5}";

                        String site = getReturnEntity().getSite() != null ? getReturnEntity().getSite() : "";
                        Integer azimuth = getReturnEntity().getAzimuth() != null ? getReturnEntity().getAzimuth() : null;

                        new MessageFormat(pattern, Locale.US).format(
                                new Object[] { getReturnEntity().getUserphoneCod(), getReturnEntity().getLatitudeNum(), getReturnEntity().getLongitudeNum(), azimuth, site, getReturnEntity().getReturnMessage() },
                                sb, new FieldPosition(0));

                        getFacadeContainer().getSmsQueueAPI().sendToJMS(
                                getEntity().getMsisdn(), getApplication(),
                                responseMessage);

                        returnMessage = sb.toString();

                    }
                } else {
                    throw new InvalidOperationException(MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "web.client.backingBean.tracking.messages.CouldNotLocateLine"),
                            u.getCellphoneNum().toString()));
                }
            }

            break;
        }
        return returnMessage;

    }

    /**
     * reimplementamos el metodo por si existe un error, no lanzar al usuario,
     * solo loggear y retornar null
     */
    @Override
    protected void validate() {
        try {
            super.validate();
        } catch (Exception e) {
            throw new TrackingException(e.getMessage());
        }
    }

    @Override
    protected ServiceValue treatHeader() {

        return null;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        throw new UnsupportedOperationException("Not supported for trackin service.");
    }

    @Override
    protected void assignServiceEvent() {
        if (getEntity().getEvent() != null
            && getEntity().getEvent().compareToIgnoreCase("userphone.locate") == 0) {
            tranType = TrackingEvent.USERPHONE_LOCATE;
        }
        getReturnEntity().setEvent(getEntity().getEvent());
    }
}
