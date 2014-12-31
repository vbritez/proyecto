package com.tigo.cs.android.asynctask.response;

import java.io.Serializable;
import java.util.HashMap;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.messagehistory.MessageHistoryActivity;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.api.entities.MetadataCrudService;

public class MetadataCrudResponseAsyncTask extends AbstractResponseManagerAsyncTask<MetadataCrudService, MetadataCrudResponseAsyncTask.MetadataCrudResponseEvent> {

    protected enum MetadataCrudResponseEvent implements ServiceResponseEvent {

        CREATE("metadata.name.create", R.string.metadatacrud_create_message_title, R.string.metadatacrud_create_message_success, R.string.metadatacrud_create_message_success),
        READ("metadata.name.read", R.string.metadatacrud_read_message_title, R.string.metadatacrud_read_message_success, R.string.metadatacrud_read_message_success),
        REGISTER("REGISTER", R.string.metadatacrud_register_message_title, R.string.metadatacrud_register_message_success, R.string.metadatacrud_register_message_success),
        GEOLOCATION("GEOLOCATION", R.string.metadatacrud_geolocation_message_title, R.string.metadatacrud_geolocation_message_success, R.string.metadatacrud_geolocation_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private MetadataCrudResponseEvent(String event, Integer title,
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
    public MetadataCrudService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new MetadataCrudService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(MetadataCrudResponseEvent.REGISTER)) {
                getResponseEntity().setResponse(getMsgElement(1));
            }
        }
    }

    @Override
    protected void assignEvent() {

    }

    @Override
    protected void assignServiceEvent() {
        if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    MetadataCrudResponseEvent.CREATE.getEvent()) == 0) {
            event = MetadataCrudResponseEvent.CREATE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    MetadataCrudResponseEvent.READ.getEvent()) == 0) {
            event = MetadataCrudResponseEvent.READ;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    MetadataCrudResponseEvent.GEOLOCATION.getEvent()) == 0) {
            event = MetadataCrudResponseEvent.GEOLOCATION;
        } else {
            event = MetadataCrudResponseEvent.REGISTER;
        }

    }

    @Override
    protected void notificate() {

        switch (event) {
        case GEOLOCATION:
            if ((getServiceEventEntity() == null)
                || (getServiceEventEntity() != null && getServiceEventEntity().getNotifyMessage())) {

                Integer title = event.getTitle();
                Integer desc = event.getSuccessMessage();

                CsTigoApplication.vibrate(CsTigoApplication.getGlobalParameterHelper().getPlatformVibrate());
                if (getResponseEntity().getResponseList() != null
                    && getResponseEntity().getResponseList().size() > 0) {
                    HashMap<String, Serializable> mapExtras = new HashMap<String, Serializable>();
                    mapExtras.put("metadata_crud_service", getResponseEntity());
                    CsTigoApplication.showNotification(
                            CSTigoNotificationID.SERVICE_UPDATE,
                            CsTigoApplication.getContext().getString(title),
                            CsTigoApplication.getContext().getString(desc),
                            (getResponseEntity().getActivityToOpen() != null ? CsTigoApplication.getServiceClass(getResponseEntity().getActivityToOpen()) : MessageHistoryActivity.class),
                            mapExtras);
                } else {
                    CsTigoApplication.showNotification(
                            CSTigoNotificationID.SERVICE_UPDATE,
                            CsTigoApplication.getContext().getString(title),
                            CsTigoApplication.getContext().getString(
                                    event.getErrorMessage()),
                            (getResponseEntity().getActivityToOpen() != null ? CsTigoApplication.getServiceClass(getResponseEntity().getActivityToOpen()) : MessageHistoryActivity.class));
                }
            }
            break;
        default:
            super.notificate();
        }
    }

    @Override
    protected void processResponse() {

    }
}
