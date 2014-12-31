package com.tigo.cs.android.asynctask.response;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;
import com.tigo.cs.android.helper.domain.IconEntity;
import com.tigo.cs.android.helper.domain.MetaEntity;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.helper.domain.UserphoneEntity;
import com.tigo.cs.android.service.LocationService;
import com.tigo.cs.android.service.ServiceResponseEvent;
import com.tigo.cs.android.util.MessageState;
import com.tigo.cs.api.entities.IconService;
import com.tigo.cs.api.entities.PermissionService;
import com.tigo.cs.api.entities.UserphoneService;

public class PermissionResponseAsyncTask extends AbstractResponseManagerAsyncTask<PermissionService, PermissionResponseAsyncTask.PermissionResponseEvent> {

    protected enum PermissionResponseEvent implements ServiceResponseEvent {

        DEVICE_ENABLE("DEV", R.string.permission_enable_device_message_title, R.string.permission_enable_device_message_success, R.string.permission_enable_device_message_success),
        SERVICE_UPDATE("CS", R.string.permission_service_update_message_title, R.string.permission_service_update_message_success, R.string.permission_service_update_message_error),
        METADATA_READ("CM", R.string.permission_metadata_read_message_title, R.string.permission_metadata_read_message_success, R.string.permission_metadata_create_message_error),
        METADATA_CREATE("CR", R.string.permission_metadata_create_message_title, R.string.permission_metadata_create_message_success, R.string.permission_metadata_create_message_error),
        SERVICE_DELETABLE("DS", R.string.permission_service_deletable_message_title, R.string.permission_service_deletable_message_success, R.string.permission_service_deletable_message_error),
        USERPHONE_UPDATE("userphone.update", R.string.permission_userphone_update_message_title, R.string.permission_userphone_update_message_success, R.string.permission_userphone_update_message_error),
        ICON_UPDATE("icon.update", R.string.permission_update_icon_message_title, R.string.permission_update_icon_message_success, R.string.permission_update_icon_message_error),
        ICON_UPDATE_ON_DEMAND("icon.update.ondemand", R.string.permission_update_icon_message_title, R.string.permission_update_icon_message_success, R.string.permission_update_icon_message_error),
        ICON_UPDATE_REQUEST("icon.update.request", R.string.permission_update_icon_message_title, R.string.permission_update_icon_message_success, R.string.permission_update_icon_message_error),
        ICON_UPDATE_REQUEST_ON_DEMAND("icon.update.request.ondemand", R.string.permission_update_icon_message_title, R.string.permission_update_icon_message_success, R.string.permission_update_icon_message_error);

        private final String event;
        private final Integer title;
        private final Integer successMessage;
        private final Integer errorMessage;

        private PermissionResponseEvent(String event, Integer title,
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

    private static Gson gson;

    public PermissionResponseAsyncTask() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    }

    @Override
    public PermissionService getResponseEntity() {
        if (responseEntity == null) {
            responseEntity = new PermissionService();
        }
        return super.getResponseEntity();
    }

    @Override
    protected void convertToBean() {
        if (getResponseEntity().getGrossMessage() != null) {

            switch (event) {
            case DEVICE_ENABLE:
                getResponseEntity().setMsisdn(getMsgElement(2));
                break;

            case METADATA_READ:
            case METADATA_CREATE:
            case SERVICE_UPDATE:
            case SERVICE_DELETABLE:
                getResponseEntity().setResponseList(new ArrayList<String>());
                for (int i = 2; i < getMsgLength(); i++) {
                    getResponseEntity().getResponseList().add(getMsgElement(i));
                }
                break;
            }
        }

    }

    @Override
    protected void assignEvent() {
        if (getResponseEntity().getEvent() == null) {
            getResponseEntity().setEvent(getMsgElement(1));
        }
    }

    @Override
    protected void assignServiceEvent() {
        if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.DEVICE_ENABLE.getEvent()) == 0) {
            event = PermissionResponseEvent.DEVICE_ENABLE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.SERVICE_UPDATE.getEvent()) == 0) {
            event = PermissionResponseEvent.SERVICE_UPDATE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.METADATA_CREATE.getEvent()) == 0) {
            event = PermissionResponseEvent.METADATA_CREATE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.METADATA_READ.getEvent()) == 0) {
            event = PermissionResponseEvent.METADATA_READ;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.SERVICE_DELETABLE.getEvent()) == 0) {
            event = PermissionResponseEvent.SERVICE_DELETABLE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.USERPHONE_UPDATE.getEvent()) == 0) {
            event = PermissionResponseEvent.USERPHONE_UPDATE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.ICON_UPDATE.getEvent()) == 0) {
            event = PermissionResponseEvent.ICON_UPDATE;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.ICON_UPDATE_ON_DEMAND.getEvent()) == 0) {
            event = PermissionResponseEvent.ICON_UPDATE_ON_DEMAND;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.ICON_UPDATE_REQUEST.getEvent()) == 0) {
            event = PermissionResponseEvent.ICON_UPDATE_REQUEST;
        } else if (getResponseEntity().getEvent() != null
            && getResponseEntity().getEvent().compareToIgnoreCase(
                    PermissionResponseEvent.ICON_UPDATE_REQUEST_ON_DEMAND.getEvent()) == 0) {
            event = PermissionResponseEvent.ICON_UPDATE_REQUEST_ON_DEMAND;
        }

    }

    @Override
    protected void validate() throws Exception {
        if (!event.equals(PermissionResponseEvent.DEVICE_ENABLE)) {
            super.validate();
        }
    }

    @Override
    protected void processResponse() {

        ServiceEntity service = null;
        ServiceEventEntity serviceEvent = null;

        switch (event) {
        case DEVICE_ENABLE:

            CsTigoApplication.getMetaHelper().disableAllCreatableMeta();
            CsTigoApplication.getMetaHelper().disableAllReadableMeta();
            CsTigoApplication.getServiceHelper().disableAllService();

            /*
             * rehabilitamos el dispositivo si el mensaje contiene un numero
             * telefonico
             */
            GlobalParameterEntity deviceEnabling = CsTigoApplication.getGlobalParameterHelper().getDeviceEnabledEntity();
            deviceEnabling.setParameterValue("1");
            CsTigoApplication.getGlobalParameterHelper().update(deviceEnabling);
            /*
             * agregamos el numero telefonico a la base de datos del dispositivo
             */
            GlobalParameterEntity cellphonenum = CsTigoApplication.getGlobalParameterHelper().getDeviceCellphoneNumEntity();
            cellphonenum.setParameterValue(getResponseEntity().getMsisdn());
            CsTigoApplication.getGlobalParameterHelper().update(cellphonenum);
            /*
             * hay q ver que abrir al terminar el proceso a quien notificar
             */

            break;
        case SERVICE_UPDATE:

            CsTigoApplication.getServiceHelper().disableAllService();

            /*
             * verificamos los servicios que nos retorna la plataforma
             */

            for (String item : getResponseEntity().getResponseList()) {

                Integer serviceCod = Integer.parseInt(item);
                ServiceEntity serviceEntityToEnable = CsTigoApplication.getServiceHelper().findByServiceCod(
                        serviceCod);

                if (serviceEntityToEnable != null) {

                    if (!serviceEntityToEnable.getPlatformService()) {
                        serviceEntityToEnable.setPlatformService(true);
                    }
                    serviceEntityToEnable.setEnabled(true);
                    CsTigoApplication.getServiceHelper().update(
                            serviceEntityToEnable);
                }
            }
            /*
             * hay q ver que abrir al terminar el proceso a quien notificar
             */

            break;
        case METADATA_READ:

            /*
             * verificamos los servicios que nos retorna la plataforma
             */

            for (String item : getResponseEntity().getResponseList()) {

                Long metaCod = Long.parseLong(item);
                MetaEntity metaEntity = CsTigoApplication.getMetaHelper().findByMetaCod(
                        metaCod);

                if (metaEntity != null) {
                    metaEntity.setCanRead(true);
                    CsTigoApplication.getMetaHelper().update(metaEntity);
                }
            }
            /*
             * hay q ver que abrir al terminar el proceso a quien notificar
             */

            break;
        case METADATA_CREATE:
            /*
             * verificamos los servicios que nos retorna la plataforma
             */

            for (String item : getResponseEntity().getResponseList()) {

                Long metaCod = Long.parseLong(item);
                MetaEntity metaEntity = CsTigoApplication.getMetaHelper().findByMetaCod(
                        metaCod);

                if (metaEntity != null) {
                    metaEntity.setCanCreate(true);
                    CsTigoApplication.getMetaHelper().update(metaEntity);
                }
            }
            /*
             * hay q ver que abrir al terminar el proceso a quien notificar
             */

            break;
        case SERVICE_DELETABLE:

            for (String item : getResponseEntity().getResponseList()) {

                Integer serviceCod = Integer.parseInt(item);
                ServiceEntity serviceEntityToDelete = CsTigoApplication.getServiceHelper().findByServiceCod(
                        serviceCod);

                if (serviceEntityToDelete != null) {
                    serviceEntityToDelete.setCanDelete(true);
                    CsTigoApplication.getServiceHelper().update(
                            serviceEntityToDelete);
                }
            }

            break;
        case USERPHONE_UPDATE:

            Class entityClass = CsTigoApplication.getServiceClass(getResponseEntity().getResponseListClassName());

            for (String item : getResponseEntity().getResponseList()) {
                UserphoneService userphoneService = (UserphoneService) gson.fromJson(
                        item, entityClass);

                UserphoneEntity entity = CsTigoApplication.getUserphoneHelper().findByUserphoneCod(
                        userphoneService.getUserphoneCod());

                if (entity == null) {
                    entity = new UserphoneEntity();
                    entity.setCellphoneNumber(userphoneService.getCellphone());
                    entity.setUserphoneCod(userphoneService.getUserphoneCod());
                    entity.setName(userphoneService.getName());
                    entity.setEnabled(userphoneService.getEnabled().equals(1) ? true : false);
                    CsTigoApplication.getUserphoneHelper().insert(entity);
                } else {
                    entity.setCellphoneNumber(userphoneService.getCellphone());
                    entity.setUserphoneCod(userphoneService.getUserphoneCod());
                    entity.setName(userphoneService.getName());
                    entity.setEnabled(userphoneService.getEnabled().equals(1) ? true : false);
                    CsTigoApplication.getUserphoneHelper().update(entity);
                }
            }

            break;
        case ICON_UPDATE_REQUEST_ON_DEMAND:
            service = CsTigoApplication.getServiceHelper().findByServiceCod(0);
            serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                    0, "icon.update.ondemand");

        case ICON_UPDATE_REQUEST:

            PermissionService entity = null;

            if (service == null) {
                service = CsTigoApplication.getServiceHelper().findByServiceCod(
                        0);
            }

            if (serviceEvent == null) {
                serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        0, "icon.update");
            }

            entity = new PermissionService();
            entity.setServiceCod(service.getServicecod());
            entity.setEvent(serviceEvent.getServiceEventCod());
            entity.setRequiresLocation(serviceEvent.getRequiresLocation());

            Long messageId = persistMessage();
            getMessageEntity().setState(MessageState.PREPARED_TO_SEND.ordinal());
            CsTigoApplication.getMessageHelper().update(getMessageEntity());

            entity.setMessageId(messageId);

            Intent intent = new Intent(CsTigoApplication.getContext(), LocationService.class);
            intent.putExtra("service", entity);

            CsTigoApplication.getContext().startService(intent);

            break;

        case ICON_UPDATE_ON_DEMAND:
        case ICON_UPDATE:
            Class entityClassIcon = CsTigoApplication.getServiceClass(getResponseEntity().getResponseListClassName());

            List<IconEntity> iconEntityList = CsTigoApplication.getIconHelper().findAll();

            for (String item : getResponseEntity().getResponseList()) {
                IconService iconService = (IconService) gson.fromJson(item,
                        entityClassIcon);

                IconEntity iconEntity = CsTigoApplication.getIconHelper().findByUrl(
                        iconService.getUrl());
                if (iconEntity == null) {
                    iconEntity = new IconEntity();
                    iconEntity.setUrl(iconService.getUrl());
                    iconEntity.setCode(iconService.getCode());
                    iconEntity.setDescription(iconService.getDescription());
                    iconEntity.setImage(iconService.getImage());
                    iconEntity.setDefaultIcon(iconService.getDefaultIcon() == 1 ? true : false);
                    CsTigoApplication.getIconHelper().insert(iconEntity);
                } else {
                    iconEntityList.remove(iconEntity);
                    if (iconService.getUrl().compareToIgnoreCase(
                            iconEntity.getUrl()) != 0) {
                        iconEntity.setUrl(iconService.getUrl());
                        iconEntity.setCode(iconService.getCode());
                        iconEntity.setDescription(iconService.getDescription());
                        iconEntity.setImage(iconService.getImage());
                        iconEntity.setDefaultIcon(iconService.getDefaultIcon() == 1 ? true : false);
                        CsTigoApplication.getIconHelper().update(iconEntity);
                    }
                }
            }

            for (IconEntity iconEntity : iconEntityList) {
                CsTigoApplication.getIconHelper().delete(iconEntity);
            }

            break;
        }

        getResponseEntity().setResponse(
                CsTigoApplication.getContext().getString(
                        event.getSuccessMessage()));

    }

    public Drawable drawableFromUrl(String url, String src_name) throws java.net.MalformedURLException, java.io.IOException {
        return Drawable.createFromStream(
                ((java.io.InputStream) new java.net.URL(url).getContent()),
                src_name);
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }
}
