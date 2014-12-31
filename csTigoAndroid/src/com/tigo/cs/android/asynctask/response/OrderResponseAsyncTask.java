package com.tigo.cs.android.asynctask.response;

import com.tigo.cs.android.R;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.OrderService;

public class OrderResponseAsyncTask extends AbstractResponseManagerAsyncTask<OrderService, OrderResponseAsyncTask.OrderResponseEvent> {

    protected enum OrderResponseEvent implements ServiceResponseEvent {

        INI("INI", R.string.order_init_message_title, R.string.order_init_message_success, R.string.order_init_message_success),
        END("END", R.string.order_end_message_title, R.string.order_end_message_success, R.string.order_end_message_error),
        REG("REG", R.string.order_reg_message_title, R.string.order_reg_message_success, R.string.order_reg_message_error),
        REGISTER("REGISTER", R.string.order_register_message_title, R.string.order_register_message_success, R.string.order_register_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private OrderResponseEvent(String event, Integer title,
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
    public OrderService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new OrderService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(OrderResponseEvent.REGISTER)) {
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
                    OrderResponseEvent.INI.getEvent()) == 0) {
            event = OrderResponseEvent.INI;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    OrderResponseEvent.END.getEvent()) == 0) {
            event = OrderResponseEvent.END;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    OrderResponseEvent.REG.getEvent()) == 0) {
            event = OrderResponseEvent.REG;
        } else {
            event = OrderResponseEvent.REGISTER;
        }

    }

    @Override
    protected void processResponse() {

    }
}
