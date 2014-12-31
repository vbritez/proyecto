package com.tigo.cs.android.asynctask.response;

import java.text.MessageFormat;

import android.content.Intent;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.service.LocationService;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.TrackingService;

public class TrackingResponseAsyncTask extends AbstractResponseManagerAsyncTask<TrackingService, TrackingResponseAsyncTask.TrackingResponseEvent> {

    protected enum TrackingResponseEvent implements ServiceResponseEvent {

        LOCATE("LOC", R.string.tracking_location_message_title, R.string.tracking_location_message_success, R.string.tracking_location_message_success),
        LOCATE_NO_WAIT("LOC_NO_WAIT", R.string.tracking_location_message_title, R.string.tracking_location_message_success, R.string.tracking_location_message_success),
        REGISTER("REGISTER", R.string.tracking_location_message_title, R.string.tracking_location_message_success, R.string.tracking_location_message_success),
        REGISTER_NO_WAIT("REGISTER", R.string.tracking_location_message_title, R.string.tracking_location_message_success, R.string.tracking_location_message_success);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private TrackingResponseEvent(String event, Integer title,
                Integer successMessage, Integer errorMessage) {

            this.event = event;
            this.title = title;
            this.successMessage = successMessage;
            this.errorMessage = errorMessage;
        }

        @Override
        public String getEvent() {
            return event;
        }

        @Override
        public Integer getTitle() {
            return title;
        }

        @Override
        public Integer getErrorMessage() {
            return errorMessage;
        }

        @Override
        public Integer getSuccessMessage() {
            return successMessage;
        }
    }

    @Override
    public TrackingService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new TrackingService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(TrackingResponseEvent.LOCATE)) {
                getResponseEntity().setResponse(getMsgElement(1));
            } else if (event.equals(TrackingResponseEvent.LOCATE_NO_WAIT)) {
                getResponseEntity().setResponse(getMsgElement(1));
            }
        }
    }

    @Override
    protected void assignEvent() {
        if (getResponseEntity().getGrossMessage() != null) {
            getResponseEntity().setEvent(getMsgElement(1));
        }
    }

    @Override
    protected void assignServiceEvent() {
        if (getResponseEntity().getEvent().compareToIgnoreCase(
                TrackingResponseEvent.LOCATE.getEvent()) == 0) {
            event = TrackingResponseEvent.LOCATE;
        } else if (getResponseEntity().getEvent().compareToIgnoreCase(
                TrackingResponseEvent.LOCATE_NO_WAIT.getEvent()) == 0) {
            event = TrackingResponseEvent.LOCATE_NO_WAIT;
        } else {
            event = TrackingResponseEvent.REGISTER;
        }

    }

    @Override
    protected void processResponse() {
        TrackingService entity = null;
        ServiceEntity service = null;
        ServiceEventEntity serviceEvent = null;
        switch (event) {
        case LOCATE_NO_WAIT:
            service = CsTigoApplication.getServiceHelper().findByServiceCod(4);
            serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                    4, "REGISTER_NO_WAIT");
        case LOCATE:

            if (service == null) {
                service = CsTigoApplication.getServiceHelper().findByServiceCod(
                        4);
            }
            if (serviceEvent == null) {
                serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        4, "REGISTER");
            }

            entity = new TrackingService();
            entity.setServiceCod(service.getServicecod());
            entity.setEvent(serviceEvent.getServiceEventCod());
            entity.setEncryptResponse(true);

            String msg = MessageFormat.format(serviceEvent.getMessagePattern(),
                    service.getServicecod());

            entity.setResponse(msg);
            Long messageId = persistMessage(msg);
            entity.setMessageId(messageId);

            Intent trackingIntent = new Intent(CsTigoApplication.getContext(), LocationService.class);
            trackingIntent.putExtra("service", entity);

            CsTigoApplication.getContext().startService(trackingIntent);

            break;

        default:
            break;
        }

    }
}
