package com.tigo.cs.android.asynctask.response;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.messagehistory.MessageHistoryActivity;
import com.tigo.cs.android.activity.tigomoney.TigoMoneyTmpEntity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.helper.domain.SessionEntity;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.android.util.MessageState;
import com.tigo.cs.api.entities.TigoMoneyService;

public class TigoMoneyResponseAsyncTask extends AbstractResponseManagerAsyncTask<TigoMoneyService, TigoMoneyResponseAsyncTask.TigoMoneyResponseEvent> {

    private Boolean smsResponse = true;

    protected enum TigoMoneyResponseEvent implements ServiceResponseEvent {

        LOGIN("LOGIN", R.string.tigomoney_login_message_title, R.string.tigomoney_login_message_success, R.string.tigomoney_login_message_error),
        CONSULTID("CONSULTID", R.string.tigomoney_consulting_id_message_title, R.string.tigomoney_consulting_id_message_success, R.string.tigomoney_consulting_id_message_error),
        REGISTER("REGISTER", R.string.tigomoney_register_message_title, R.string.tigomoney_register_message_success, R.string.tigomoney_register_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private TigoMoneyResponseEvent(String event, Integer title,
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
    public TigoMoneyService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new TigoMoneyService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(TigoMoneyResponseEvent.LOGIN)) {
                getResponseEntity().setCellphoneNumber(getMsgElement(2));
                getResponseEntity().setLoginResponse(getMsgElement(3));
                getResponseEntity().setMessage(getMsgElement(4));
            } else if (event.equals(TigoMoneyResponseEvent.CONSULTID)) {
                getResponseEntity().setIdentification(getMsgElement(2));
                getResponseEntity().setIdStatus(getMsgElement(3));
                getResponseEntity().setMessage(getMsgElement(4));
                if (getResponseEntity().getIdStatus().equalsIgnoreCase(
                        "NOTREGISTERED")) {
                    getResponseEntity().setName(getMsgElement(5));
                }
            } else if (event.equals(TigoMoneyResponseEvent.REGISTER)) {
                getResponseEntity().setIdentification(getMsgElement(2));
                getResponseEntity().setRegistrationResponse(getMsgElement(3));
                getResponseEntity().setMessage(getMsgElement(4));
                getResponseEntity().setMessageId(
                        getMsgElement(5) != null ? Long.parseLong(getMsgElement(5)) : null);
            }
        }
    }

    public TigoMoneyResponseAsyncTask() {
    }

    @Override
    protected void assignEvent() {
        if (getResponseEntity().getEvent() == null) {
            getResponseEntity().setEvent(getMsgElement(1));
            smsResponse = true;
        } else {
            smsResponse = false;
        }
    }

    @Override
    protected void assignServiceEvent() {
        if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    TigoMoneyResponseEvent.LOGIN.getEvent()) == 0) {
            event = TigoMoneyResponseEvent.LOGIN;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    TigoMoneyResponseEvent.CONSULTID.getEvent()) == 0) {
            event = TigoMoneyResponseEvent.CONSULTID;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    TigoMoneyResponseEvent.REGISTER.getEvent()) == 0) {
            event = TigoMoneyResponseEvent.REGISTER;
        }
    }

    @Override
    protected void processResponse() {

        switch (event) {
        case LOGIN:
            if (getResponseEntity().getLoginResponse().equals("0")) {
                TelephonyManager telephonyManager = (TelephonyManager) CsTigoApplication.getContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
                SessionEntity entity = new SessionEntity();
                entity.setCellphoneNumber(getResponseEntity().getCellphoneNumber());
                GregorianCalendar cal = new GregorianCalendar();
                cal.add(Calendar.HOUR_OF_DAY, 12);
                entity.setExpirationDate(cal.getTime());
                entity.setImsi(telephonyManager.getSubscriberId());
                CsTigoApplication.getSessionHelper().insert(entity);
            }
            getResponseEntity().setResponse(getResponseEntity().getMessage());
            break;

        case CONSULTID:
            if (getResponseEntity().getIdStatus().equals("NOTREGISTERED")) {
                TigoMoneyTmpEntity currentEntity = CsTigoApplication.getCurrentTigoMoneyEntity();
                if (currentEntity == null) {
                    currentEntity = new TigoMoneyTmpEntity();
                }
                currentEntity.setName(getResponseEntity().getName());
                currentEntity.setIdentification(getResponseEntity().getIdentification());
                CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);
            } else {
                getServiceEventEntity().setActivityToOpen(null);
            }
            getResponseEntity().setResponse(getResponseEntity().getMessage());
            break;
        case REGISTER:
            MessageEntity message = CsTigoApplication.getMessageHelper().find(
                    getResponseEntity().getMessageId());
            if (smsResponse) {
                if (getResponseEntity().getRegistrationResponse() != null
                    && getResponseEntity().getRegistrationResponse().equalsIgnoreCase(
                            "OK")) {
                    message.setForceInternet(true);
                    message.setState(MessageState.RETRY.ordinal());
                    CsTigoApplication.getMessageHelper().update(message);
                }
                getResponseEntity().setResponse(
                        getResponseEntity().getMessage());

            } else {
                if ((getResponseEntity().getRegistrationResponse() != null && getResponseEntity().getRegistrationResponse().equals(
                        "OK"))
                    || getResponseEntity().getRegistrationResponse() == null) {
                    if (message.getState() == MessageState.RETRY.ordinal()) {
                        message.setState(MessageState.SENDED.ordinal());
                        CsTigoApplication.getMessageHelper().update(message);
                        getServiceEventEntity().setNotifyMessage(false);
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void notificate() {
        if ((getServiceEventEntity() == null)
            || (getServiceEventEntity() != null && getServiceEventEntity().getNotifyMessage())) {

            Integer title = event.getTitle();
            Integer desc = event.getSuccessMessage();
            if (getResponseEntity().getRegistrationResponse() != null
                && getResponseEntity().getRegistrationResponse().equals(
                        "IGNORE")) {

                return;
            } else if (getResponseEntity().getLoginResponse() != null
                && getResponseEntity().getLoginResponse().equals("0")) {
                desc = R.string.tigomoney_login_message_success;
            } else if (getResponseEntity().getLoginResponse() != null
                && !getResponseEntity().getLoginResponse().equals("0")) {
                desc = R.string.tigomoney_login_message_error;
            }

            if (getResponseEntity().getIdStatus() != null
                && getResponseEntity().getIdStatus().equals("NOTREGISTERED")) {
                desc = R.string.tigomoney_user_not_registered;
            } else if (getResponseEntity().getIdStatus() != null
                && getResponseEntity().getIdStatus().equals("REGISTERED")) {
                desc = R.string.tigomoney_user_already_registered;
            } else if (getResponseEntity().getIdStatus() != null
                && getResponseEntity().getIdStatus().equals("ERROR")) {
                desc = R.string.tigomoney_consulting_id_message_error;
            }

            if (getResponseEntity().getRegistrationResponse() != null
                && getResponseEntity().getRegistrationResponse().equals("OK")) {
                desc = R.string.tigomoney_register_message_success;
            } else if (getResponseEntity().getRegistrationResponse() != null
                && getResponseEntity().getRegistrationResponse().equals("ERROR")) {
                desc = R.string.tigomoney_register_message_error;
            }

            if (getServiceEventEntity() == null) {
                desc = event.getErrorMessage();
            }

            CsTigoApplication.vibrate(CsTigoApplication.getGlobalParameterHelper().getPlatformVibrate());

            CsTigoApplication.showNotification(
                    CSTigoNotificationID.SERVICE_UPDATE,
                    CsTigoApplication.getContext().getString(title),
                    CsTigoApplication.getContext().getString(desc),
                    (getServiceEventEntity() != null
                        && getServiceEventEntity().getActivityToOpen() != null ? getServiceEventEntity().getActivityToOpen() : MessageHistoryActivity.class));

        }
    }

    // getResponseEntity().setResponse(
    // CsTigoApplication.getContext().getString(
    // event.getSuccessMessage()));
}
