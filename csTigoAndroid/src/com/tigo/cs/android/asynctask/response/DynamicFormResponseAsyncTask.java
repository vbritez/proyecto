package com.tigo.cs.android.asynctask.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.DynamicFormElementEntity;
import com.tigo.cs.android.helper.domain.DynamicFormElementEntryEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.service.LocationService;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.api.entities.DynamicFormElementEntryService;
import com.tigo.cs.api.entities.DynamicFormElementService;
import com.tigo.cs.api.entities.DynamicFormService;

public class DynamicFormResponseAsyncTask extends AbstractResponseManagerAsyncTask<DynamicFormService, DynamicFormResponseAsyncTask.DynamicFormResponseEvent> {

    protected enum DynamicFormResponseEvent implements ServiceResponseEvent {

        FIND("FIND", R.string.dynamic_form_find_message_title, R.string.dynamic_form_find_message_success, R.string.dynamic_form_find_message_error),
        UPDATE("UPDATE", R.string.dynamic_form_update_message_title, R.string.dynamic_form_update_message_success, R.string.dynamic_form_update_message_error),
        REGISTER("REGISTER", R.string.dynamic_form_register_message_title, R.string.dynamic_form_register_message_success, R.string.dynamic_form_register_message_error),
        GENERAL("GENERAL", R.string.dynamic_form_register_message_title, R.string.dynamic_form_register_message_success, R.string.dynamic_form_register_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private DynamicFormResponseEvent(String event, Integer title,
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
    public DynamicFormService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new DynamicFormService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {
            if (event.equals(DynamicFormResponseEvent.UPDATE)) {
                getResponseEntity().setResponse(
                        CsTigoApplication.getContext().getString(
                                DynamicFormResponseEvent.UPDATE.getSuccessMessage()));
            }
        }
    }

    @Override
    protected void assignEvent() {
        if (getResponseEntity().getGrossMessage() != null) {
            getResponseEntity().setEvent(getMsgElement(1));
        }
    }

    @Override
    protected void assignServiceEvent() {
        if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    DynamicFormResponseEvent.FIND.getEvent()) == 0) {
            event = DynamicFormResponseEvent.FIND;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    DynamicFormResponseEvent.UPDATE.getEvent()) == 0) {
            event = DynamicFormResponseEvent.UPDATE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    DynamicFormResponseEvent.REGISTER.getEvent()) == 0) {
            event = DynamicFormResponseEvent.REGISTER;
        } else {
            event = DynamicFormResponseEvent.GENERAL;
        }
    }

    @Override
    protected void processResponse() {

        switch (event) {
        case UPDATE:

            ServiceEventEntity serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                    getResponseEntity().getServiceCod(),
                    DynamicFormResponseEvent.FIND.getEvent());

            DynamicFormService entity = new DynamicFormService();
            entity.setServiceCod(serviceEvent.getService().getServicecod());
            entity.setEvent(serviceEvent.getServiceEventCod());
            entity.setRequiresLocation(serviceEvent.getRequiresLocation());
            entity.setSendSMS(false);

            Long messageId = persistMessage();
            entity.setMessageId(messageId);

            Intent trackingIntent = new Intent(CsTigoApplication.getContext(), LocationService.class);
            trackingIntent.putExtra("service", entity);

            CsTigoApplication.getContext().startService(trackingIntent);

            break;

        case FIND:

            List<DynamicFormElementEntity> entities = CsTigoApplication.getDynamicFormElementHelper().findAll();

            List<Long> entitiesCod = new ArrayList<Long>();
            for (DynamicFormElementEntity dynamicForm : entities) {
                entitiesCod.add(dynamicForm.getDynamicFormElementCod());
            }

            for (DynamicFormElementService dynamicFormElementService : getResponseEntity().getElementBean()) {

                if (dynamicFormElementService == null) {
                    continue;
                }

                /*
                 * creamos la entidad a ser almacenada en la base de datos
                 */

                DynamicFormElementEntity dynamicFormElementEntity = null;
                boolean update = true;

                dynamicFormElementEntity = CsTigoApplication.getDynamicFormElementHelper().findByCod(
                        dynamicFormElementService.getDynamicFormElementCod());

                if (dynamicFormElementEntity == null) {
                    update = false;
                    dynamicFormElementEntity = new DynamicFormElementEntity();
                    dynamicFormElementEntity.setDynamicFormElementCod(dynamicFormElementService.getDynamicFormElementCod());
                    dynamicFormElementEntity.setDescription(dynamicFormElementService.getDescription());
                }

                dynamicFormElementEntity.setStartDate(dynamicFormElementService.getStartDate());
                dynamicFormElementEntity.setFinishDate(dynamicFormElementService.getFinishDate());
                if (!update) {
                    CsTigoApplication.getDynamicFormElementHelper().insert(
                            dynamicFormElementEntity);
                } else {
                    CsTigoApplication.getDynamicFormElementHelper().update(
                            dynamicFormElementEntity);

                    /*
                     * como es una actualizacion eliminamos todas las entradas
                     * del formulario actual y guardamos de nuevo
                     */

                    List<DynamicFormElementEntryEntity> entryList = CsTigoApplication.getDynamicFormElementEntryHelper().findByDynamicFormElement(
                            dynamicFormElementEntity);
                    for (DynamicFormElementEntryEntity entry : entryList) {
                        CsTigoApplication.getDynamicFormElementEntryHelper().delete(
                                entry);
                    }

                }
                
                //Collections<DynamicFormElementEntryService> aaaA=dynamicFormElementService.getEntryList();
                List<DynamicFormElementEntryService> lista = dynamicFormElementService.getEntryList();
                Collections.sort(lista,
                        new Comparator<DynamicFormElementEntryService>() {

                            @Override
                            public int compare(DynamicFormElementEntryService lhs, DynamicFormElementEntryService rhs) {
                                if (lhs.getOrderNum() > rhs.getOrderNum()) {
                                    return -1;
                                } else
                                    return 1;

                            }
                        });
                for (DynamicFormElementEntryService dynamicFormElementEntryService : dynamicFormElementService.getEntryList()) {
                //for (DynamicFormElementEntryService dynamicFormElementEntryService : lista) {

                    if (dynamicFormElementEntryService == null) {
                        continue;
                    }

                    DynamicFormElementEntryEntity dynamicFormElementEntryEntity = new DynamicFormElementEntryEntity();
                    dynamicFormElementEntryEntity.setDynamicFormElementEntryCod(dynamicFormElementEntryService.getEntryCod());
                    dynamicFormElementEntryEntity.setDynamicFormElement(dynamicFormElementEntity);
                    dynamicFormElementEntryEntity.setTitle(dynamicFormElementEntryService.getTitle());
                    dynamicFormElementEntryEntity.setDescription(dynamicFormElementEntryService.getDescription());
                    dynamicFormElementEntryEntity.setFinalEntry(dynamicFormElementEntryService.getFinalEntry());
                    dynamicFormElementEntryEntity.setMultiSelectOption(dynamicFormElementEntryService.getMultiSelectOption());
                    dynamicFormElementEntryEntity.setOrderNum(dynamicFormElementEntryEntity.getOrderNum());
                    Long ownerCod = dynamicFormElementEntryService.getOwnerEntryCod();
                    if (ownerCod != null) {
                        dynamicFormElementEntryEntity.setOwnerEntry(CsTigoApplication.getDynamicFormElementEntryHelper().findByCod(
                                ownerCod));
                    }
                    dynamicFormElementEntryEntity.setEntryType(CsTigoApplication.getDynamicFormEntryTypeHelper().findByCod(
                            dynamicFormElementEntryService.getEntryTypeCod()));
                    CsTigoApplication.getDynamicFormElementEntryHelper().insert(
                            dynamicFormElementEntryEntity);

                }

                entitiesCod.remove(dynamicFormElementService.getDynamicFormElementCod());

            }

            for (Long entityCod : entitiesCod) {

                DynamicFormElementEntity dynamicFormElementEntityToDelete = CsTigoApplication.getDynamicFormElementHelper().findByCod(
                        entityCod);

                List<DynamicFormElementEntryEntity> entryList = CsTigoApplication.getDynamicFormElementEntryHelper().findByDynamicFormElement(
                        dynamicFormElementEntityToDelete);
                for (DynamicFormElementEntryEntity entry : entryList) {
                    CsTigoApplication.getDynamicFormElementEntryHelper().delete(
                            entry);
                }

                CsTigoApplication.getDynamicFormElementHelper().delete(
                        dynamicFormElementEntityToDelete);

            }

            break;
        default:
            break;
        }
    }

}
