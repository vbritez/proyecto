package com.tigo.cs.android.asynctask.response;

import java.util.ArrayList;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.ServiceOperationDataEntity;
import com.tigo.cs.android.helper.domain.ServiceOperationEntity;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.ServiceOperationService;

public class ServiceOperationResponseAsyncTask extends AbstractResponseManagerAsyncTask<ServiceOperationService, ServiceOperationResponseAsyncTask.ServiceOperationResponseEvent> {

    protected enum ServiceOperationResponseEvent implements ServiceResponseEvent {

        SERVICE_DELETABLE_RETRIEVE("RETDS", R.string.service_deletable_retrieve_title, R.string.service_deletable_retrieve_message_success, R.string.service_deletable_retrieve_message_error),
        SERVICEOPERATION_DELETE_SERVICE("serviceoperation.name.DeleteService", R.string.service_deletable_title, R.string.service_deletable_message_success, R.string.service_deletable_message_error),
        REGISTER("REGISTER", R.string.register_message_title, R.string.register_message_title, R.string.register_message_title);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private ServiceOperationResponseEvent(String event, Integer title,
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

    public ServiceOperationResponseAsyncTask() {
    }

    @Override
    public ServiceOperationService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new ServiceOperationService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(ServiceOperationResponseEvent.SERVICE_DELETABLE_RETRIEVE)) {
                Long serviceToOperate = null;
                try {
                    serviceToOperate = Long.parseLong(getMsgElement(2));

                    getResponseEntity().setServiceToOperate(serviceToOperate);
                    getResponseEntity().setResponseList(new ArrayList<String>());
                    for (int i = 3; i < getMsgLength(); i++) {
                        getResponseEntity().getResponseList().add(
                                getMsgElement(i));
                    }
                    getResponseEntity().setResponseListClassName(
                            String.class.getName());
                } catch (Exception e) {
                }
            }else if (event.equals(ServiceOperationResponseEvent.SERVICEOPERATION_DELETE_SERVICE)){
                getResponseEntity().setEvent(getMsgElement(1));
                getResponseEntity().setResponse(getMsgElement(2));
            }
        }
    }

    @Override
    protected void assignEvent() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (getMsgLength() > 1) {
                getResponseEntity().setEvent(getMsgElement(1));
                getResponseEntity().setResponse(getMsgElement(2));
            } else {
                getResponseEntity().setResponse(getMsgElement(1));
            }

        }
    }

    @Override
    protected void assignServiceEvent() {
        if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    ServiceOperationResponseEvent.SERVICE_DELETABLE_RETRIEVE.getEvent()) == 0) {
            event = ServiceOperationResponseEvent.SERVICE_DELETABLE_RETRIEVE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    ServiceOperationResponseEvent.SERVICEOPERATION_DELETE_SERVICE.getEvent()) == 0) {
            event = ServiceOperationResponseEvent.SERVICEOPERATION_DELETE_SERVICE;
        }else
            event = ServiceOperationResponseEvent.REGISTER;
    }

    @Override
    protected void processResponse() {

        switch (event) {
        case SERVICE_DELETABLE_RETRIEVE:
            ServiceOperationEntity serviceOperationEntity = new ServiceOperationEntity();

            serviceOperationEntity.setServicecod(getResponseEntity().getServiceToOperate().intValue());
            serviceOperationEntity.setOperationname(getResponseEntity().getEvent());

            Long id = CsTigoApplication.getServiceOperationHelper().insert(
                    serviceOperationEntity);
            serviceOperationEntity = CsTigoApplication.getServiceOperationHelper().find(
                    id);

            int c = 1;
            for (String item : getResponseEntity().getResponseList()) {
                if (!item.isEmpty()) {
                    ServiceOperationDataEntity dataEntity = new ServiceOperationDataEntity();

                    dataEntity.setCodServiceOperation(serviceOperationEntity.getId());
                    dataEntity.setServicemsg(item);
                    dataEntity.setPos(c++);

                    CsTigoApplication.getServiceOperationDataHelper().insert(
                            dataEntity);
                }
            }
            getResponseEntity().setResponse(
                    CsTigoApplication.getContext().getString(
                            event.getSuccessMessage()));
            break;

        case SERVICEOPERATION_DELETE_SERVICE:
            break;
        }

    }

}
