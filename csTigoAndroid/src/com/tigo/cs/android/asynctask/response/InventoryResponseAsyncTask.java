package com.tigo.cs.android.asynctask.response;

import com.tigo.cs.android.R;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.InventoryService;

public class InventoryResponseAsyncTask extends AbstractResponseManagerAsyncTask<InventoryService, InventoryResponseAsyncTask.InventoryResponseEvent> {

    protected enum InventoryResponseEvent implements ServiceResponseEvent {

        REGISTER("REGISTER", R.string.inventory_register_message_title, R.string.inventory_register_message_success, R.string.inventory_register_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private InventoryResponseEvent(String event, Integer title,
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
    public InventoryService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new InventoryService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(InventoryResponseEvent.REGISTER)) {
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
        event = InventoryResponseEvent.REGISTER;
    }

    @Override
    protected void processResponse() {

    }
}
