package com.tigo.cs.android.asynctask.response;

import com.tigo.cs.android.R;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.VisitMedicService;

public class MedicVisitResponseAsyncTask extends AbstractResponseManagerAsyncTask<VisitMedicService, MedicVisitResponseAsyncTask.MedicVisitResponseEvent> {

    protected enum MedicVisitResponseEvent implements ServiceResponseEvent {

        CLINIC_START("CS", R.string.medic_visit_clinic_start_message_title, R.string.medic_visit_clinic_start_message_success, R.string.medic_visit_clinic_start_message_error),
        CLINIC_END("CE", R.string.medic_visit_clinic_end_message_title, R.string.medic_visit_clinic_end_message_success, R.string.medic_visit_clinic_end_message_error),
        MEDIC_START("MS", R.string.medic_visit_medic_start_message_title, R.string.medic_visit_medic_start_message_success, R.string.medic_visit_medic_start_message_error),
        MEDIC_END("ME", R.string.medic_visit_medic_end_message_title, R.string.medic_visit_medic_end_message_success, R.string.medic_visit_medic_end_message_error),
        PRODUCT_REGISTER("PR", R.string.medic_visit_product_reg_message_title, R.string.medic_visit_product_reg_message_success, R.string.medic_visit_product_reg_message_error),
        CLINIC_QUICK("CQ", R.string.medic_visit_clinic_quick_message_title, R.string.medic_visit_clinic_quick_message_success, R.string.medic_visit_clinic_quick_message_error),
        REGISTER("REGISTER", R.string.medic_visit_register_message_title, R.string.medic_visit_register_message_success, R.string.medic_visit_register_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private MedicVisitResponseEvent(String event, Integer title,
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
    public VisitMedicService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new VisitMedicService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(MedicVisitResponseEvent.REGISTER)) {
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
                    MedicVisitResponseEvent.CLINIC_START.getEvent()) == 0) {
            event = MedicVisitResponseEvent.CLINIC_START;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    MedicVisitResponseEvent.CLINIC_END.getEvent()) == 0) {
            event = MedicVisitResponseEvent.CLINIC_END;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    MedicVisitResponseEvent.MEDIC_START.getEvent()) == 0) {
            event = MedicVisitResponseEvent.MEDIC_START;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    MedicVisitResponseEvent.MEDIC_END.getEvent()) == 0) {
            event = MedicVisitResponseEvent.MEDIC_END;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    MedicVisitResponseEvent.PRODUCT_REGISTER.getEvent()) == 0) {
            event = MedicVisitResponseEvent.PRODUCT_REGISTER;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    MedicVisitResponseEvent.CLINIC_QUICK.getEvent()) == 0) {
            event = MedicVisitResponseEvent.CLINIC_QUICK;
        } else {
            event = MedicVisitResponseEvent.REGISTER;
        }

    }

    @Override
    protected void processResponse() {

    }
}
