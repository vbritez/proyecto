package com.tigo.cs.android.asynctask.response;

import com.tigo.cs.android.R;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.VisitService;

public class VisitResponseAsyncTask extends AbstractResponseManagerAsyncTask<VisitService, VisitResponseAsyncTask.VisitResponseEvent> {

    protected enum VisitResponseEvent implements ServiceResponseEvent {

        ENT("ENT", R.string.visit_init_message_title, R.string.visit_init_message_success, R.string.visit_init_message_success),
        SAL("SAL", R.string.visit_end_message_title, R.string.visit_end_message_success, R.string.visit_end_message_error),
        ENTSAL("ENTSAL", R.string.visit_quick_message_title, R.string.visit_quick_message_success, R.string.visit_quick_message_error),
        REGISTER("REGISTER", R.string.visit_register_message_title, R.string.visit_register_message_success, R.string.visit_register_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private VisitResponseEvent(String event, Integer title,
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
    public VisitService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new VisitService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(VisitResponseEvent.REGISTER)) {
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
                    VisitResponseEvent.ENT.getEvent()) == 0) {
            event = VisitResponseEvent.ENT;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    VisitResponseEvent.SAL.getEvent()) == 0) {
            event = VisitResponseEvent.SAL;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    VisitResponseEvent.ENTSAL.getEvent()) == 0) {
            event = VisitResponseEvent.ENTSAL;
        } else {
            event = VisitResponseEvent.REGISTER;
        }

    }

    @Override
    protected void processResponse() {

    }
}
