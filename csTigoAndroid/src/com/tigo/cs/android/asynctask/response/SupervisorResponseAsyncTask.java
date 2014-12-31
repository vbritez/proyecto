package com.tigo.cs.android.asynctask.response;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;
import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.messagehistory.MessageHistoryActivity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.helper.domain.UserphoneEntity;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.android.util.MessageState;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.SupervisorService;

public class SupervisorResponseAsyncTask extends AbstractResponseManagerAsyncTask<SupervisorService, SupervisorResponseAsyncTask.SupervisorResponseEvent> {

    private static Gson gson;
    private static HashMap<String, String> hashMapGlobalMessageTracking;

    protected enum SupervisorResponseEvent implements ServiceResponseEvent {

        USERPHONE_LOCATE("userphone.locate", R.string.supervisor_location_message_title, R.string.supervisor_location_message_success, R.string.supervisor_location_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private SupervisorResponseEvent(String event, Integer title,
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
    protected Long persistMessage(String messageOut) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setEventDate(new Date());
        if (messageOut != null) {
            messageEntity.setMessage(messageOut);
        }
        UserphoneEntity u = CsTigoApplication.getUserphoneHelper().findByUserphoneCod(
                getResponseEntity().getUserphoneCod());
        String mess = u != null ? MessageFormat.format(
                CsTigoApplication.getContext().getString(
                        R.string.supervisor_message_UserphoneLocate),
                u.getUserphoneCod(), u.getName(), u.getCellphoneNumber()) : "";
        messageEntity.setMessage(mess.concat(getHashMapGlobalMessageTracking().get(
                getResponseEntity().getReturnMessage())));
        messageEntity.setService(getServiceEntity());
        messageEntity.setState(MessageState.RECEIVED.ordinal());
        if ((getResponseEntity().getGrossMessage() != null && getMsgLength() > 3)
            || (getResponseEntity().getLatitudeNum() != null && getResponseEntity().getLongitudeNum() != null)) {
            try {
                if (gson == null) {
                    gson = new Gson();
                }
                messageEntity.setJsonData(gson.toJson(getResponseEntity()));
                messageEntity.setEntityClass(CsTigoApplication.getServiceClass(getResponseEntity().getClass().getName()));

            } catch (Exception e) {
                Notifier.error(
                        getClass(),
                        CsTigoApplication.getContext().getString(
                                R.string.entity_cant_convert_gson_err));
            }
            messageEntity.setServiceEvent(getServiceEventEntity());
            Long id = CsTigoApplication.getMessageHelper().insert(messageEntity);
            getResponseEntity().setMessageId(id);
            return id;
        } else {
            return CsTigoApplication.getMessageHelper().insert(messageEntity);
        }

    };

    @Override
    protected void notificate() {
        if ((getServiceEventEntity() == null)
            || (getServiceEventEntity() != null && getServiceEventEntity().getNotifyMessage())) {

            Integer title = event.getTitle();
            Integer desc = event.getSuccessMessage();

            CsTigoApplication.vibrate(CsTigoApplication.getGlobalParameterHelper().getPlatformVibrate());

            if (getResponseEntity().getMessageId() != null) {
                HashMap<String, Serializable> mapExtras = new HashMap<String, Serializable>();
                mapExtras.put("supervisor_service", getResponseEntity());
                CsTigoApplication.showNotification(
                        CSTigoNotificationID.SERVICE_UPDATE,
                        CsTigoApplication.getContext().getString(title),
                        CsTigoApplication.getContext().getString(desc),
                        (getServiceEventEntity() != null ? getServiceEventEntity().getActivityToOpen() : MessageHistoryActivity.class),
                        mapExtras);
            } else {
                UserphoneEntity u = CsTigoApplication.getUserphoneHelper().findByUserphoneCod(
                        getResponseEntity().getUserphoneCod());
                String mess = u != null ? MessageFormat.format(
                        CsTigoApplication.getContext().getString(
                                event.getErrorMessage()), u.getName()) : "";
                CsTigoApplication.showNotification(
                        CSTigoNotificationID.SERVICE_UPDATE,
                        CsTigoApplication.getContext().getString(title),
                        mess.concat(getHashMapGlobalMessageTracking().get(
                                getResponseEntity().getResponse()) == null ? mess : getHashMapGlobalMessageTracking().get(
                                getResponseEntity().getResponse())),
                        MessageHistoryActivity.class);
            }

        }
    }

    @Override
    public SupervisorService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new SupervisorService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {

            if (event.equals(SupervisorResponseEvent.USERPHONE_LOCATE)) {
                if (getMsgLength() > 3) {
                    getResponseEntity().setUserphoneCod(
                            Long.valueOf(getMsgElement(1)));
                    getResponseEntity().setLatitudeNum(
                            Double.valueOf(getMsgElement(2)));
                    getResponseEntity().setLongitudeNum(
                            Double.valueOf(getMsgElement(3)));
                    try {
                        getResponseEntity().setAzimuth(
                                Integer.valueOf(getMsgElement(4)));
                    } catch (Exception e) {

                    }
                    getResponseEntity().setSite(getMsgElement(5));
                    getResponseEntity().setReturnMessage(getMsgElement(6));

                } else {
                    getResponseEntity().setUserphoneCod(
                            Long.valueOf(getMsgElement(1)));
                    getResponseEntity().setResponse(getMsgElement(2));
                }
            }
        } else {
            if (getResponseEntity().getLatitudeNum() == null
                && getResponseEntity().getLongitudeNum() == null) {
                String[] mess = getResponseEntity().getResponse().split(
                        "\\%+\\*");
                getResponseEntity().setUserphoneCod(Long.valueOf(mess[0]));
                getResponseEntity().setResponse(mess[1]);
            }

        }
    }

    @Override
    protected void assignEvent() {
        getResponseEntity().setEvent(
                SupervisorResponseEvent.USERPHONE_LOCATE.event);
    }

    @Override
    protected void assignServiceEvent() {
        if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    SupervisorResponseEvent.USERPHONE_LOCATE.getEvent()) == 0) {
            event = SupervisorResponseEvent.USERPHONE_LOCATE;
        }
    }

    @Override
    protected void processResponse() {

    }

    public HashMap<String, String> getHashMapGlobalMessageTracking() {
        if (hashMapGlobalMessageTracking == null) {
            hashMapGlobalMessageTracking = new HashMap<String, String>();
            hashMapGlobalMessageTracking.put(
                    "tracking.status.NoStatusMessage",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_NoStatusMessage));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.OTA",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_OTA));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.LBS",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_LBS));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidCellInfoGpsUnknowState",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidCellInfoGpsUnknowState));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidCellInfoGpsOn",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidCellInfoGpsOn));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidCellInfoGpsOff",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidCellInfoGpsOff));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidGeoPoint",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidGeoPoint));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidNoApp",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidNoApp));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.NoLocation",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_NoLocation));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.NoLocationNoCellInfo",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_NoLocationNoCellInfo));
        }
        return hashMapGlobalMessageTracking;
    }
}
