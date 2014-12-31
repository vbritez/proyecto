package com.tigo.cs.android.asynctask.response;

import java.util.Date;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.MainActivity;
import com.tigo.cs.android.activity.messagehistory.MessageHistoryActivity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.android.util.MessageState;
import com.tigo.cs.api.entities.BaseServiceBean;

public abstract class AbstractResponseManagerAsyncTask<T extends BaseServiceBean, Y extends ServiceResponseEvent> extends AbstractAsyncTask<T> {

    /*
     * atributos y clases necesarios para el procesamiento de respuestas de
     * servicios
     */

    protected T responseEntity;
    protected String grossMessageIn;
    protected String[] messageBody;
    protected ServiceEventEntity serviceEventEntity;
    protected ServiceEntity serviceEntity;
    protected String notificationMessage;
    protected Y event;
    private MessageEntity messageEntity;

    @Override
    public T getResponseEntity() {
        return responseEntity;
    }

    @Override
    public void setResponseEntity(T entity) {
        this.responseEntity = entity;
    }

    public String[] getMessageBody() {
        if (messageBody == null) {
            messageBody = getResponseEntity().getGrossMessage().split("\\%+\\*");
        }
        return messageBody;
    }

    public void setMessageBody(String[] messageBody) {
        this.messageBody = messageBody;
    }

    public String getMsgElement(int index) throws ArrayIndexOutOfBoundsException {
        return getMessageBody()[index];
    }

    public int getMsgLength() {
        return getMessageBody().length;
    }

    public MessageEntity getMessageEntity() {
        return messageEntity;
    }

    protected Long persistMessage(String messageOut) {

        if (messageEntity == null) {
            messageEntity = new MessageEntity();
            messageEntity.setEventDate(new Date());
            if (messageOut != null) {
                messageEntity.setMessage(messageOut);
            }
            messageEntity.setService(getServiceEntity());
            if ((getServiceEventEntity() == null)
                || (getServiceEventEntity() != null
                    && getServiceEventEntity().getNotifyMessage() && getResponseEntity().getResponse() != null)) {
                messageEntity.setMessage(getResponseEntity().getResponse());
                messageEntity.setState(MessageState.RECEIVED.ordinal());
            }
            /*
             * TODO: no tengo idea de porque se hace esto
             */
            else {
                messageEntity.setState(MessageState.DELIVERED.ordinal());
            }
            messageEntity.setService(getServiceEntity());
            Long id = CsTigoApplication.getMessageHelper().insert(messageEntity);
            messageEntity.setId(id);
        } else {
            if (messageOut != null) {
                messageEntity.setMessage(messageOut);
            }
            CsTigoApplication.getMessageHelper().update(messageEntity);
        }
        return messageEntity.getId();
    }

    protected Long persistMessage() {
        return persistMessage(null);
    }

    public ServiceEventEntity getServiceEventEntity() {
        if (serviceEventEntity == null
            && getResponseEntity().getEvent() != null) {
            serviceEventEntity = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                    getResponseEntity().getServiceCod(),
                    getResponseEntity().getEvent());
        }
        return serviceEventEntity;
    }

    public ServiceEntity getServiceEntity() {
        if (serviceEntity == null) {
            serviceEntity = CsTigoApplication.getServiceHelper().findByServiceCod(
                    getResponseEntity().getServiceCod());
        }
        return serviceEntity;
    }

    protected void validate() throws Exception {
        if (!CsTigoApplication.getGlobalParameterHelper().getDeviceEnabled()) {

            CsTigoApplication.vibrate(false);
            CsTigoApplication.showNotification(
                    CSTigoNotificationID.SERVICE_UPDATE,
                    CsTigoApplication.getContext().getString(
                            R.string.disabled_device),
                    CsTigoApplication.getContext().getString(
                            R.string.disabled_device_desc), MainActivity.class);

            String messageIn = CsTigoApplication.getContext().getString(
                    R.string.disabled_device_desc);
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setEventDate(new Date());
            messageEntity.setMessage(messageIn);
            messageEntity.setService(getServiceEntity());
            messageEntity.setState(MessageState.RECEIVED.ordinal());
            CsTigoApplication.getMessageHelper().insert(messageEntity);
            throw new Exception("");
        }
    }

    protected void notificate() {
        if ((getServiceEventEntity() == null)
            || (getServiceEventEntity() != null && getServiceEventEntity().getNotifyMessage())) {

            Integer title = event.getTitle();
            Integer desc = event.getSuccessMessage();

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

    @Override
    protected void execute() {
        try {
            assignEvent();
            assignServiceEvent();
            convertToBean();
            validate();
            processResponse();
            persistMessage();
            notificate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void assignEvent();

    protected abstract void assignServiceEvent();

    protected abstract void convertToBean();

    protected abstract void processResponse();

}
