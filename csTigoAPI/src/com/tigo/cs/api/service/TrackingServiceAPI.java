package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.List;

import com.tigo.cs.api.entities.TrackingService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.exception.TrackingException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class TrackingServiceAPI<T extends TrackingService> extends AbstractServiceAPI<TrackingService> {

    public TrackingServiceAPI() {
    }

    @Override
    public TrackingService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new TrackingService());
            getEntity().setSendSMS(false);
        }
        return super.getEntity();
    }

    @Override
    public TrackingService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new TrackingService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        try {
            treatHeader();
            String message = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue("tracking.message"),
                    getEntity().getMsisdn());
            getFacadeContainer().getNotifier().info(TrackingServiceAPI.class,
                    getCellphoneNumber().toString(), message);
            getReturnEntity().setResponse(message);
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
        }
        return null;
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
        ServiceValue serviceValue = null;
        serviceValue = new ServiceValue();
        serviceValue.setService(getService());
        serviceValue.setUserphone(getUserphone());
        serviceValue.setMessage(getMessage());
        serviceValue.setRecorddateDat(validateDate());
        serviceValue.setColumn1Chr(getEntity().getDate());
        serviceValue.setColumn2Chr("1");

        String trackingStatusMessage = "tracking.status.OTA";
        if (getEntity().getAndroid()) {
            /*
             * si el canal de este registro es android verificamos primero si
             * tiene datos de latitude y longitud
             */
            if (getEntity().getLatitude() != null
                && getEntity().getLongitude() != null) {
                /*
                 * al enviar esta informacion, el GPS tuvo que estar encendido
                 */
                trackingStatusMessage = "tracking.status.AndroidGeoPoint";
            } else {
                /*
                 * el dispositivo solo envio informacion de celda, verificamos
                 * el estado del GPS
                 */
                if (getEntity().getGpsEnabled() == null) {
                    trackingStatusMessage = "tracking.status.AndroidCellInfoGpsUnknowState";
                } else if (getEntity().getGpsEnabled()) {
                    trackingStatusMessage = "tracking.status.AndroidCellInfoGpsOn";
                } else {
                    trackingStatusMessage = "tracking.status.AndroidCellInfoGpsOff";
                }
            }
        }
        serviceValue.setColumn3Chr(trackingStatusMessage);
        getFacadeContainer().getServiceValueAPI().create(serviceValue);
        return serviceValue;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        throw new UnsupportedOperationException("Not supported for trackin service.");
    }

    @Override
    protected void assignServiceEvent() {
        getReturnEntity().setEvent(getEntity().getEvent());
    }
}
