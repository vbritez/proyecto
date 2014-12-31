package com.tigo.cs.android.asynctask.response;

import com.tigo.cs.android.R;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.CourrierService;

public class CourierResponseAsyncTask extends AbstractResponseManagerAsyncTask<CourrierService, CourierResponseAsyncTask.CourierResponseEvent> {

    protected enum CourierResponseEvent implements ServiceResponseEvent {

        REGISTER("PR", R.string.courier_register_message_title, R.string.courier_register_message_success, R.string.courier_register_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private CourierResponseEvent(String event, Integer title,
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
    public CourrierService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new CourrierService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(CourierResponseEvent.REGISTER)) {
                getResponseEntity().setResponse(getMsgElement(1));
            }
        }
    }

    @Override
    protected void assignEvent() {
        if (getResponseEntity().getGrossMessage() != null) {
            getResponseEntity().setEvent("PR");
        }
    }

    @Override
    protected void assignServiceEvent() {
        event = CourierResponseEvent.REGISTER;
    }

    @Override
    protected void processResponse() {

    }
}
