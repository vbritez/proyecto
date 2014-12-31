package com.tigo.cs.android.asynctask.response;

import com.tigo.cs.android.R;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.AttendanceService;

public class AttendanceResponseAsyncTask extends AbstractResponseManagerAsyncTask<AttendanceService, AttendanceResponseAsyncTask.AttendanceResponseEvent> {

    protected enum AttendanceResponseEvent implements ServiceResponseEvent {

        PRESENCE_INIT("PI", R.string.attendance_presence_init_message_title, R.string.attendance_presence_init_message_success, R.string.attendance_presence_init_message_success),
        PRESENCE_FINISH("PF", R.string.attendance_presence_finish_message_title, R.string.attendance_presence_finish_message_success, R.string.attendance_presence_finish_message_error),
        BREAK_INIT("BI", R.string.attendance_break_init_message_title, R.string.attendance_break_init_message_success, R.string.attendance_break_init_message_error),
        BREAK_FINISH("BF", R.string.attendance_break_finish_message_title, R.string.attendance_break_finish_message_success, R.string.attendance_break_finish_message_error),
        LUNCH_INIT("LI", R.string.attendance_lunch_init_message_title, R.string.attendance_lunch_init_message_success, R.string.attendance_lunch_init_message_error),
        LUNCH_FINISH("LF", R.string.attendance_lunch_finish_message_title, R.string.attendance_lunch_finish_message_success, R.string.attendance_lunch_finish_message_error),
        REGISTER("REGISTER", R.string.attendance_register_message_title, R.string.attendance_register_message_success, R.string.attendance_register_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private AttendanceResponseEvent(String event, Integer title,
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
    public AttendanceService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new AttendanceService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(AttendanceResponseEvent.REGISTER)) {
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
                    AttendanceResponseEvent.PRESENCE_INIT.getEvent()) == 0) {
            event = AttendanceResponseEvent.PRESENCE_INIT;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    AttendanceResponseEvent.PRESENCE_FINISH.getEvent()) == 0) {
            event = AttendanceResponseEvent.PRESENCE_FINISH;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    AttendanceResponseEvent.BREAK_INIT.getEvent()) == 0) {
            event = AttendanceResponseEvent.BREAK_INIT;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    AttendanceResponseEvent.BREAK_FINISH.getEvent()) == 0) {
            event = AttendanceResponseEvent.BREAK_FINISH;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    AttendanceResponseEvent.LUNCH_INIT.getEvent()) == 0) {
            event = AttendanceResponseEvent.LUNCH_INIT;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    AttendanceResponseEvent.LUNCH_FINISH.getEvent()) == 0) {
            event = AttendanceResponseEvent.LUNCH_FINISH;
        } else {
            event = AttendanceResponseEvent.REGISTER;
        }

    }

    @Override
    protected void processResponse() {

    }
}
