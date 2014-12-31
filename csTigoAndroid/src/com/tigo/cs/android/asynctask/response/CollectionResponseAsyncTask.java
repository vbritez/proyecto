package com.tigo.cs.android.asynctask.response;

import com.tigo.cs.android.R;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.CollectionService;

public class CollectionResponseAsyncTask extends AbstractResponseManagerAsyncTask<CollectionService, CollectionResponseAsyncTask.CollectionResponseEvent> {

    protected enum CollectionResponseEvent implements ServiceResponseEvent {

        OPEN("OPEN", R.string.collection_open_message_title, R.string.collection_open_message_success, R.string.collection_open_message_success),
        CLOSE("CLOSE", R.string.collection_close_message_title, R.string.collection_close_message_success, R.string.collection_close_message_error),
        REG("REG", R.string.collection_reg_message_title, R.string.collection_reg_message_success, R.string.collection_reg_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private CollectionResponseEvent(String event, Integer title,
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
    public CollectionService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new CollectionService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(CollectionResponseEvent.REG)) {
                getResponseEntity().setResponse(getMsgElement(1));
            }
        }
    }

    @Override
    protected void assignEvent() {
        if (getResponseEntity().getGrossMessage() != null) {
            getResponseEntity().setEvent("REGISTER");
        }
    }

    @Override
    protected void assignServiceEvent() {
        if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    CollectionResponseEvent.OPEN.getEvent()) == 0) {
            event = CollectionResponseEvent.OPEN;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    CollectionResponseEvent.CLOSE.getEvent()) == 0) {
            event = CollectionResponseEvent.CLOSE;
        } else {
            event = CollectionResponseEvent.REG;
        }

    }

    @Override
    protected void processResponse() {

    }
}
