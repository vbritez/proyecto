package com.tigo.cs.api.service;

import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.IconService;
import com.tigo.cs.api.entities.PermissionService;
import com.tigo.cs.api.entities.UserphoneService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.IconType;
import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.view.DataIcon;

public abstract class PermissionServiceAPI<T extends PermissionService> extends AbstractServiceAPI<PermissionService> {

    protected PermissionEvent tranType;

    protected enum PermissionEvent implements ServiceEvent {

        SERVICE_ENABLED("CS", new ServiceProps("permission.name.ServiceEnabled", "", "")),
        METADATA_READ("CM", new ServiceProps("permission.name.MetaDataEnabled", "", "")),
        SERVICE_DELETABLE("DS", new ServiceProps("permission.name.ServiceDelete", "", "")),
        DEVICE_ENABLED("DEV", new ServiceProps("permission.name.MetaDataEnabled", "", "")),
        METADATA_CREATION("CR", new ServiceProps("permission.name.MetaDataCreation", "", "")),
        USERPHONE_UPDATE("userphone.update", new ServiceProps("permission.name.UserphoneUpdate", "", "")),
        ICON_UPDATE("icon.update", new ServiceProps("permission.name.IconUpdate", "", "")),
        ICON_UPDATE_ON_DEMAND("icon.update.ondemand", new ServiceProps("permission.name.IconUpdate", "", ""));

        protected final String value;
        protected final ServiceProps props;

        private PermissionEvent(String value, ServiceProps props) {
            this.value = value;
            this.props = props;
        }

        @Override
        public String getSuccessMessage() {
            return props.getSuccesMessage();
        }

        @Override
        public String getNoMatchMessage() {
            return props.getNoMatchMessage();
        }

        @Override
        public String getDescription() {
            return props.getDescription();
        }

    }

    public PermissionServiceAPI() {
    }

    @Override
    public PermissionService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new PermissionService());
        }
        return super.getEntity();
    }

    @Override
    public PermissionService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new PermissionService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        String returnMessage = tranType.value;

        List<Long> services = null;
        getReturnEntity().setResponseList(new ArrayList<String>(5));
        switch (tranType) {
        case SERVICE_ENABLED:
            getEntity().setRequiresLocation(false);
            services = getFacadeContainer().getServiceAPI().getServiceCodByUserphoneAndFuncionality(
                    getUserphone().getUserphoneCod(), 5L);
            for (Long cod : services) {
                String item = String.valueOf(cod);
                returnMessage += ("%*" + item);
                getReturnEntity().getResponseList().add(item);
            }

            break;
        case SERVICE_DELETABLE:
            getEntity().setRequiresLocation(false);
            services = getFacadeContainer().getServiceAPI().getServiceCodByUserphoneAndFuncionality(
                    getUserphone().getUserphoneCod(), 6L);
            for (Long cod : services) {
                String item = String.valueOf(cod);
                returnMessage += ("%*" + item);
                getReturnEntity().getResponseList().add(item);
            }

            break;
        case METADATA_READ:
            getEntity().setRequiresLocation(false);
            returnMessage = tranType.value;
            List<Long> metadatas = getFacadeContainer().getMetaAPI().getMetadataCodByUserphone(
                    getUserphone().getClient().getClientCod());
            for (Long cod : metadatas) {
                String item = String.valueOf(cod);
                returnMessage += ("%*" + item);
                getReturnEntity().getResponseList().add(item);
            }

            break;
        case DEVICE_ENABLED:
            getEntity().setRequiresLocation(false);
            getEntity().setEncryptResponse(false);
            returnMessage += ("%*" + getUserphone().getCellphoneNum().toString());

            Userphone userphone = getUserphone();
            userphone.setCurrentImei(getEntity().getImei());
            userphone.setCurrentImsi(getEntity().getImsi());

            setUserphone(getFacadeContainer().getUserphoneAPI().addDeviceInfo(
                    userphone));

            break;
        case METADATA_CREATION:
            getEntity().setRequiresLocation(false);
            List<Meta> umList = getFacadeContainer().getUserphoneMetaAPI().getUserphoneMetaList(
                    getUserphone(), true, false, false);
            for (Meta um : umList) {
                String item = String.valueOf(um.getMetaCod());
                returnMessage += ("%*" + item);
                getReturnEntity().getResponseList().add(item);
            }

            break;
        case USERPHONE_UPDATE:

            List<Classification> list = getFacadeContainer().getClassificationAPI().findByUserphoneWithChilds(
                    getUserphone());

            if (list == null) {
                throw new InvalidOperationException("El usuario no tiene clasificacion asignada. Asigne clasificación al usuario.");
            }

            List<Userphone> userphones = getFacadeContainer().getUserphoneAPI().findByUserphonesWithTrackingService(
                    getUserphone());

            getReturnEntity().setResponseListClassName(
                    UserphoneService.class.getName());

            for (Userphone u : userphones) {
                UserphoneService userphoneService = new UserphoneService();
                userphoneService.setName(u.getNameChr());
                userphoneService.setCellphone(u.getCellphoneNum().toString());
                userphoneService.setUserphoneCod(u.getUserphoneCod());
                userphoneService.setEnabled(u.getEnabledChr() ? 1 : 0);
                getReturnEntity().getResponseList().add(
                        userphoneService.toString());
            }

            break;
        case ICON_UPDATE_ON_DEMAND:
        case ICON_UPDATE:
            List<DataIcon> dataIconList = getFacadeContainer().getDataIconAPI().findByClient(
                    getClient().getClientCod());

            getReturnEntity().setResponseListClassName(
                    IconService.class.getName());

            for (DataIcon icon : dataIconList) {
                IconService iconService = new IconService();
                iconService.setCode(icon.getCode());
                iconService.setUrl(icon.getUrl());
                // byte[] bytebuff = null;

                IconType iconType = getFacadeContainer().getIconTypeAPI().findByUrl(
                        icon.getUrl());

                // try {
                // URL url = new URL(icon.getUrl());
                // ByteArrayOutputStream bis = new ByteArrayOutputStream();
                // InputStream is = null;
                // is = url.openStream();
                // bytebuff = new byte[4096];
                // int n;
                //
                // while ((n = is.read(bytebuff)) > 0) {
                // bis.write(bytebuff, 0, n);
                // }
                //
                // } catch (MalformedURLException e) {
                // e.printStackTrace();
                // } catch (IOException e) {
                // e.printStackTrace();
                // }

                if (iconType != null) {
                    iconService.setImage(iconType.getFileByt());
                }
                iconService.setDescription(icon.getDescripcion());
                iconService.setDefaultIcon(0);
                getReturnEntity().getResponseList().add(iconService.toString());
            }

            break;

        }

        if (getReturnEntity().getResponseList().size() == 1) {
            getReturnEntity().getResponseList().add(
                    getReturnEntity().getResponseList().get(0));
        }
        getReturnEntity().setEvent(getEntity().getEvent());
        getEntity().setSendSMS(false);
        setServiceCod(0L);
        return returnMessage;
    }

    @Override
    protected void assignServiceEvent() {
        if (getEntity().getEvent().equals("CS")) {
            tranType = PermissionEvent.SERVICE_ENABLED;
        } else if (getEntity().getEvent().equals("CM")) {
            tranType = PermissionEvent.METADATA_READ;
        } else if (getEntity().getEvent().equals("DEV")) {
            tranType = PermissionEvent.DEVICE_ENABLED;
        } else if (getEntity().getEvent().equals("CR")) {
            tranType = PermissionEvent.METADATA_CREATION;
        } else if (getEntity().getEvent().equals("DS")) {
            tranType = PermissionEvent.SERVICE_DELETABLE;
        } else if (getEntity().getEvent().equals("userphone.update")) {
            tranType = PermissionEvent.USERPHONE_UPDATE;
        } else if (getEntity().getEvent().equals("icon.update")) {
            tranType = PermissionEvent.ICON_UPDATE;
        } else if (getEntity().getEvent().equals("icon.update.ondemand")) {
            tranType = PermissionEvent.ICON_UPDATE_ON_DEMAND;
        }
    }

    @Override
    protected ServiceValue treatHeader() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        throw new UnsupportedOperationException();
    }

}
