package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.tigo.cs.api.entities.MetaDataServiceBean;
import com.tigo.cs.api.entities.MetadataCrudService;
import com.tigo.cs.api.enumerate.Restriction;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.MapMark;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaClientPK;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaMember;
import com.tigo.cs.domain.MetaMemberPK;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.view.DataIcon;

public abstract class MetadataCrudServiceAPI<T extends MetadataCrudService> extends AbstractServiceAPI<MetadataCrudService> {

    protected MetaCrudEvent tranType;
    private MetaNames metaName;

    protected enum MetaCrudEvent implements ServiceEvent {

        CREATE(1, new ServiceProps("metadata.name.create", "metadata.message.create", "metadata.nomatchmessage.create")),
        READ(2, new ServiceProps("metadata.name.read", "metadata.message.read", "metadata.nomatchmessage.read")),
        UPDATE(3, new ServiceProps("metadata.name.update", "metadata.message.update", "metadata.nomatchmessage.update")),
        DELETE(4, new ServiceProps("metadata.name.delete", "metadata.message.delete", "metadata.nomatchmessage.delete")),
        SEARCH_BY_CODE(5, new ServiceProps("metadataquery.name.QueryByCode", "metadataquery.message.QueryByCode", "metadataquery.nomatchmessage.QueryByCode")),
        SEARCH_BY_NAME(6, new ServiceProps("metadataquery.name.QueryByName", "metadataquery.message.QueryByName", "metadataquery.nomatchmessage.QueryByName")),
        FIND_ALL(7, new ServiceProps("metadataquery.name.FindAll", "metadataquery.message.FindAll", "metadataquery.nomatchmessage.FindAll")),
        GEOLOCATION(8, new ServiceProps("metadataquery.name.geoLocation", "metadataquery.message.geoLocation", "metadataquery.nomatchmessage.geoLocation"));
        protected final int value;
        protected final ServiceProps props;

        private MetaCrudEvent(int value, ServiceProps props) {
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

    public MetadataCrudServiceAPI() {
    }

    @Override
    public MetadataCrudService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new MetadataCrudService());
        }
        return super.getEntity();
    }

    @Override
    public MetadataCrudService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new MetadataCrudService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        String code = null;

        switch (tranType) {
        case CREATE:
            MetaData m = getFacadeContainer().getMetaDataAPI().getLastMetadataClientMetaMember(
                    getClient().getClientCod(), getMeta().getMetaCod(), 1L);

            if (getEntity().getCode() == null) {
                Long pk = null;
                if (m != null) {
                    try {
                        pk = Long.parseLong(m.getMetaDataPK().getCodeChr()) + 1;
                    } catch (NumberFormatException e) {
                        throw new InvalidOperationException(MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "metadata.crud.CanNotGenerateCode"),
                                getMeta().getDescriptionChr()));
                    }
                } else {
                    pk = 1L;
                }
                code = pk.toString();
            } else {
                code = getEntity().getCode();
            }
            try {
                MapMark mark = null;
                /*
                 * Si tiene latitud y longitud, significa que el userphone marco
                 * que el metadata este asociado con un marker
                 */
                if (getEntity().getLatitude() != null
                    && getEntity().getLongitude() != null) {

                    mark = getFacadeContainer().getMapMarkAPI().findByClientLatLng(
                            getClient(), getEntity().getLatitude(),
                            getEntity().getLongitude());

                    if (mark != null) {
                        mark.setTitleChr(getMeta().getDescriptionChr() + ": "
                            + code + "-" + getEntity().getValue());
                        mark.setDescriptionChr(getMeta().getDescriptionChr()
                            + ". Codigo: " + code + ", Nombre:"
                            + getEntity().getValue());
                        getFacadeContainer().getMapMarkAPI().edit(mark);
                    } else {
                        mark = new MapMark();
                        mark.setClient(getClient());
                        mark.setLatitudeNum(getEntity().getLatitude());
                        mark.setLongitudeNum(getEntity().getLongitude());
                        mark.setTitleChr(getMeta().getDescriptionChr() + ": "
                            + code + "-" + getEntity().getValue());
                        mark.setDescriptionChr(getMeta().getDescriptionChr()
                            + ". Codigo: " + code + ", Nombre:"
                            + getEntity().getValue());
                        mark.setCreatedateDat(new Date());
                        getFacadeContainer().getMapMarkAPI().create(mark);
                    }

                }

                Long memberCode = m != null ? m.getMetaMember().getMetaMemberPK().getMemberCod() : 1L;
                MetaMemberPK pk = new MetaMemberPK();
                pk.setCodMeta(getMeta().getMetaCod());
                pk.setMemberCod(memberCode);
                MetaMember metaMember = getFacadeContainer().getMetaMemberAPI().find(
                        pk);

                MetaClientPK clientPk = new MetaClientPK();
                clientPk.setCodClient(getClient().getClientCod());
                clientPk.setCodMeta(getMeta().getMetaCod());
                MetaClient metaClient = getFacadeContainer().getMetaClientAPI().find(
                        clientPk);

                getFacadeContainer().getMetaDataAPI().create(metaClient,
                        getMeta(), metaMember, code, getEntity().getValue(),
                        mark);

                /* se guarda icono seleccionado */
                if (getEntity().getIconCodeSelected() != null) {
                    try {
                        DataIcon dataIcon = getFacadeContainer().getDataIconAPI().findEntityByClientMetaCodigo(
                                getClient().getClientCod(), 21L,
                                getEntity().getIconCodeSelected());
                        MetaMember mMember = getFacadeContainer().getMetaMemberAPI().findByCodMetaAndMemberCod(
                                getMeta().getMetaCod(), -1L);

                        getFacadeContainer().getMetaDataAPI().create(
                                m.getMetaClient(), getMeta(), mMember, code,
                                dataIcon.getUrl(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        getFacadeContainer().getNotifier().error(
                                MetadataCrudServiceAPI.class,
                                getCellphoneNumber().toString(),
                                e.getMessage(), e);
                    }
                }

            } catch (Exception e) {
                return MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "metadata.crud.messages.CouldNotCreateMetadata"),
                        getFacadeContainer().getI18nAPI().iValue(
                                metaName.description()));
            }

            String metaCreated = getMetaForIntegrationValue(code, metaName);

            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "metadata.crud.messages.MetadataSuccesfullyCreated"),
                    getFacadeContainer().getI18nAPI().iValue(
                            metaName.description()), metaCreated);

            return returnMessage;

        case SEARCH_BY_CODE:
            getEntity().setAndroid(false);
            returnMessage = getMetaByCodeSearchMessage(getEntity().getValue(),
                    tranType, metaName);
            return returnMessage;

        case SEARCH_BY_NAME:
            getEntity().setAndroid(false);
            returnMessage = getMetaByValueSearchMessage(getEntity().getValue(),
                    tranType, metaName, 5);
            getReturnEntity().setResponse(returnMessage);
            return returnMessage;

        case FIND_ALL:

            List<MetaData> list = findMetaAll(metaName, 1L);

            if (list == null) {
                return null;
            }
            List<MetaDataServiceBean> beans = new ArrayList<MetaDataServiceBean>();

            for (MetaData metaData : list) {

                MetaData enabled = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                        getClient().getClientCod(), metaName.value(), 2L,
                        metaData.getMetaDataPK().getCodeChr());

                if (enabled != null
                    && enabled.getValueChr().compareToIgnoreCase("Si") == 0) {

                    MetaDataServiceBean bean = new MetaDataServiceBean();
                    bean.setMemberCod(1L);
                    bean.setCode(metaData.getMetaDataPK().getCodeChr());
                    bean.setValue(metaData.getValueChr());
                    beans.add(bean);
                }
            }

            return new Gson().toJson(beans);

        case GEOLOCATION:
            /*
             * Si llega la latitud y longitud recuperamos directamente de la
             * lista de metadatos de estos valores, de lo contrario recuperamos
             * de los datos de celdas
             */
            returnMessage = null;
            try {
                if (getEntity().getLatitude() != null
                    && getEntity().getLongitude() != null) {
                    List<MetadataCrudService> listMd = getFacadeContainer().getMetaDataAPI().findMetadataGeoLocation(
                            getClient().getClientCod(),
                            getEntity().getMetaCode(),
                            getEntity().getMemberCode(),
                            getEntity().getLatitude(),
                            getEntity().getLongitude());

                    if (listMd == null || listMd.isEmpty()) {
                        getReturnEntity().setResponse(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "metadataquery.message.NoMetadataClientsNear"));
                        getReturnEntity().setResponseList(listMd);
                        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                                "metadataquery.message.NoMetadataClientsNear");
                    } else {
                        getReturnEntity().setActivityToOpen(
                                getEntity().getActivityToOpen());
                        getReturnEntity().setResponseList(listMd);
                        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                                tranType.getSuccessMessage());
                    }
                } else {
                    Cell cell = getCell();
                    if (cell == null) {
                        Cell cellId = getFacadeContainer().getCellAPI().getCellId(
                                getUserphone(), getLAC(), getCellId());
                        if (cellId != null) {
                            List<MetadataCrudService> listMd = getFacadeContainer().getMetaDataAPI().findMetadataGeoLocation(
                                    getClient().getClientCod(),
                                    getEntity().getMetaCode(),
                                    getEntity().getMemberCode(),
                                    cellId.getLatitudeNum(),
                                    cellId.getLongitudeNum());
                            if (listMd == null || listMd.isEmpty()) {
                                getReturnEntity().setResponse(
                                        getFacadeContainer().getI18nAPI().iValue(
                                                "metadataquery.message.NoMetadataClientsNear"));
                                getReturnEntity().setResponseList(listMd);
                                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                                        "metadataquery.message.NoMetadataClientsNear");
                            } else {
                                getReturnEntity().setActivityToOpen(
                                        getEntity().getActivityToOpen());
                                getReturnEntity().setResponseList(listMd);
                                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                                        tranType.getSuccessMessage());
                            }
                        } else {
                            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                                    tranType.getNoMatchMessage());
                        }
                    } else {
                        List<MetadataCrudService> listMd = getFacadeContainer().getMetaDataAPI().findMetadataGeoLocation(
                                getClient().getClientCod(),
                                getEntity().getMetaCode(),
                                getEntity().getMemberCode(),
                                cell.getLatitudeNum(), cell.getLongitudeNum());

                        if (listMd == null || listMd.isEmpty()) {
                            getReturnEntity().setResponse(
                                    getFacadeContainer().getI18nAPI().iValue(
                                            "metadataquery.message.NoMetadataClientsNear"));
                            getReturnEntity().setResponseList(listMd);
                            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                                    "metadataquery.message.NoMetadataClientsNear");
                        } else {
                            getReturnEntity().setActivityToOpen(
                                    getEntity().getActivityToOpen());
                            getReturnEntity().setResponseList(listMd);
                            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                                    tranType.getSuccessMessage());
                        }
                    }
                }

            } catch (Exception e) {
                getFacadeContainer().getNotifier().error(getClass(),
                        getUserphone().getCellphoneNum().toString(),
                        e.getMessage(), e);
                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                        tranType.getNoMatchMessage());
            }

            if (getReturnEntity().getResponseList() != null
                && getReturnEntity().getResponseList().size() == 1) {
                getReturnEntity().getResponseList().add(
                        getReturnEntity().getResponseList().get(0));
            }
            return returnMessage;
        }

        return returnMessage;
    }

    @Override
    protected void validate() throws InvalidOperationException {
        super.validate();

        if (!hasMetaIntegrationEnabled(getClient().getClientCod(),
                MetaNames.findByValue(getMeta().getMetaCod().intValue()))) {
            String msj = getFacadeContainer().getI18nAPI().iValue(
                    Restriction.CLIENT_WITH_META_NOT_ALLOWED.getMessage());
            msj = MessageFormat.format(msj, getMeta().getDescriptionChr());
            throw new OperationNotAllowedException(msj);
        }

        if (tranType.equals(MetaCrudEvent.CREATE)) {
            if (getEntity().getCode() != null) {
                MetaData m = null;
                try {
                    m = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                            getClient().getClientCod(), getMeta().getMetaCod(),
                            1L, getEntity().getCode());
                } catch (Exception e) {
                }
                // if (m != null) {
                // throw new InvalidOperationException(MessageFormat.format(
                // getFacadeContainer().getI18nAPI().iValue(
                // "metadata.crud.CodeAlreadyExists"),
                // getEntity().getCode(),
                // getMeta().getDescriptionChr()));
                // }

            }
        }
    }

    @Override
    protected void assignServiceEvent() {
        metaName = MetaNames.findByValue(getEntity().getMetaCode());
        if (tranType != null && tranType.equals(MetaCrudEvent.READ)) {
            if (getEntity().getEvent().compareTo("C") == 0) {
                tranType = MetaCrudEvent.SEARCH_BY_CODE;
                getReturnEntity().setEvent(MetaCrudEvent.READ.getDescription());
            } else if (getEntity().getEvent().compareTo("N") == 0) {
                tranType = MetaCrudEvent.SEARCH_BY_NAME;
                getReturnEntity().setEvent(MetaCrudEvent.READ.getDescription());
            }
        } else if (getEntity().getEvent().compareToIgnoreCase("ALL") == 0) {
            tranType = MetaCrudEvent.FIND_ALL;
        } else if (getEntity().getEvent().compareToIgnoreCase("GEOLOCATION") == 0) {
            tranType = MetaCrudEvent.GEOLOCATION;
        }
        setMeta(getFacadeContainer().getMetaAPI().find(metaName.value()));
    }

    @Override
    protected ServiceValue treatHeader() {
        return null;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        return null;
    }

}
