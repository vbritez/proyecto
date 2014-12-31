package com.tigo.cs.view.metadata;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.tigo.cs.api.exception.AssociateHoraryException;
import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.IconType;
import com.tigo.cs.domain.MapMark;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.ShiftPeriod;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.view.Data;
import com.tigo.cs.domain.view.DataIcon;
import com.tigo.cs.facade.IconTypeFacade;
import com.tigo.cs.facade.MapMarkFacade;
import com.tigo.cs.facade.view.DataFacade;
import com.tigo.cs.facade.view.DataIconFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.view.pbeans.HoraryPojo;

/**
 * 
 * @author Miguel Zorrillasav
 */
public class AbstractCrudMetaDataBean<T extends Data> extends AbstractCrudBean {

    private static final long serialVersionUID = -5239986958724371857L;
    protected DataModel<T> dataModelSpecific;
    @EJB
    protected DataFacade dataFacade;
    protected T data;
    protected Class<T> dataClass;
    protected String urlIconDefault = "http://maps.google.com/mapfiles/ms/micons/blue-dot.png";
    protected String descriptionUrl = "";

    // ICONS
    protected Boolean geoCoor;
    protected MetaData selectedDataIcon;
    protected List<MetaData> dataIconList;
    protected Map<String, Boolean> selectedDataIconMap;

    // MAPS
    protected MapModel mapModel; // si
    protected LatLng mapCenter; // si
    protected Integer mapZoom; // si
    protected String mapType; // si
    protected Marker selectedMarker; // si
    private boolean clientMarker = false; // si
    private boolean editMarker = false;
    private LatLngBounds lastBounds;
    protected MapMark mapMark;
    @EJB
    protected MapMarkFacade mapMarksFacade;
    @EJB
    private IconTypeFacade iconTypeFacade;
    private Map<String, String> dataIconDescription;
    @EJB
    private DataIconFacade dataIconFacade;

    public AbstractCrudMetaDataBean(Long cod_meta, Long cod_member) {
        super(cod_meta, cod_member);
        String filter = "o.dataPK.codClient = %s AND o.dataPK.codMeta = %s";
        setAditionalFilter(String.format(filter,
                getUserweb().getClient().getClientCod(),
                String.valueOf(cod_meta)));
    }

    @PostConstruct
    private void init() {
        IconType defaultIcon = iconTypeFacade.findDefaultIcon();
        urlIconDefault = defaultIcon.getUrl();
        descriptionUrl = defaultIcon.getDescription();

    }

    // GETTERS AND SETTERS
    public AbstractAPI getDataFacade() {
        return dataFacade;
    }

    public void setDataFacade(DataFacade dataFacade) {
        this.dataFacade = dataFacade;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public DataModel<T> getDataModelSpecific() {
        if (dataModelSpecific == null) {
            dataModelSpecific = getPaginationHelper().createPageDataModel();
        }
        return dataModelSpecific;
    }

    public void setDataModelSpecific(DataModel<T> dataModelSpecific) {
        this.dataModelSpecific = dataModelSpecific;
    }

    @Override
    protected Class<T> getDataClass() {
        if (dataClass == null) {
            ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
            Type[] ta = t.getActualTypeArguments();
            dataClass = (Class<T>) ta[0];
        }
        return dataClass;
    }

    // OVERRIDEN MOTHODS
    @Override
    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;

            paginationHelper = new PaginationHelper(pageSize) {

                @Override
                public int getItemsCount() {
                    String where = null;
                    if (!filterSelectedField.equals("-1")
                        && filterCriteria.length() > 0) {
                        Class<?> fieldClass = getFieldType(filterSelectedField);

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = "o.".concat(filterSelectedField).concat(
                                    " = ").concat(filterCriteria);
                        } else if (fieldClass.equals(String.class)) {
                            where = "lower(o.".concat(filterSelectedField).concat(
                                    ") LIKE '%").concat(
                                    filterCriteria.toLowerCase()).concat("%'");
                        }
                    }
                    if (aditionalFilter != null
                        && aditionalFilter.trim().length() > 0) {
                        if (where == null) {
                            where = "";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(
                                aditionalFilter.trim()).concat(") ");
                    }

                    return dataFacade.count(getDataClass(), where);
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = null;
                    if (!filterSelectedField.equals("-1")
                        && filterCriteria.length() > 0) {
                        Class<?> fieldClass = getFieldType(filterSelectedField);

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = "o.".concat(filterSelectedField).concat(
                                    " = ").concat(filterCriteria);
                        } else if (fieldClass.equals(String.class)) {
                            where = "lower(o.".concat(filterSelectedField).concat(
                                    ") LIKE '%").concat(
                                    filterCriteria.toLowerCase()).concat("%'");
                        }
                    }
                    if (aditionalFilter != null
                        && aditionalFilter.trim().length() > 0) {
                        if (where == null) {
                            where = "";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(
                                aditionalFilter.trim()).concat(") ");
                    }
                    String beginOrderBy = sortHelper.getField().equals(
                            "codeChr") ? "o.dataPK." : "o.";
                    String orderby = beginOrderBy.concat(sortHelper.getField()).concat(
                            sortHelper.isAscending() ? " ASC" : " DESC");
                    List<T> list = dataFacade.findRange(getDataClass(),
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby);
                    return new ListDataModelViewCsTigo(list);
                }
            };
        }

        return paginationHelper;
    }

    @Override
    public String nextPage() {
        dataModelSpecific = null; // resetDataModel();
        return super.nextPage();
    }

    @Override
    public String previousPage() {
        dataModelSpecific = null; // resetDataModel();
        return super.previousPage();
    }

    @Override
    public String applyFilter() {
        if (!filterSelectedField.equals("-1")) {
            Class<?> fieldClass = getFieldType(filterSelectedField);

            if ((fieldClass.equals(Integer.class) && !NumberUtil.isInteger(filterCriteria))
                || (fieldClass.equals(Long.class) && !NumberUtil.isLong(filterCriteria))
                || (fieldClass.equals(BigInteger.class) && !NumberUtil.isBigInteger(filterCriteria))) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustBeInteger"));
                filterCriteria = "";
            } else {
                signalRead();
            }
            paginationHelper = null; // For pagination recreation
            dataModelSpecific = null;
            selectedItems = null; // For clearing selection
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectFindOption"));
        }
        return null;
    }

    @Override
    public String cleanFilter() {
        dataModelSpecific = null;
        return super.cleanFilter();
    }

    @Override
    public String applySort() {
        dataModelSpecific = null;// resetDataModel();//
                                 // clearSpecificDataModel();
        return super.applySort();
    }

    @Override
    public void applyQuantity(ValueChangeEvent evnt) {
        super.applyQuantity(evnt);
        dataModelSpecific = null; // For data model recreation
    }

    @Override
    public String newEntity() {
        try {
            data = getDataClass().newInstance();
            userphoneList = userPhoneFacade.findByUserwebAndClassification(getUserweb());
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.abstractServiceBean.message.NewEntityError"));
        }

        mapModel = null;
        getMapModel();
        mapMark = null;
        geoCoor = false;
        selectedDataIconMap = null;
        selectedDataIcon = null;
        MetaData aux = new MetaData(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 21L, 1L, "-1");
        aux.setValueChr(urlIconDefault);

        if (getDataIconList().contains(aux)) {
            dataIconList.remove(aux);
            getDataIconDescription().remove(aux.getValueChr());
        }
        return null;
    }

    @Override
    public String cancelEditing() {
        data = null;
        selectedAssociatedUserphones = null;
        selectedUserphoneList = null;
        associatedUserphonesList = null;
        userphoneList = null;
        validatedAllUserphones = false;
        validatedAllUserphonesAssociated = false;
        selectedDataIcon = null;
        geoCoor = false;
        selectedDataIconMap = null;
        mapModel = null;
        mapMark = null;
        return super.cancelEditing();
    }




    @Override
    public String procesarArchivo() {
        super.procesarArchivo();
        data = null;
        return null;
    }

    @Override
    public String deleteEntitiesMass() {
        for (Data dataBean : dataModelSpecific) {
            /*
             * Creamos una instancia de MetaDataPK en base a los DataBean que
             * nos vinos como parametro al seleccionados en los checkbox
             */
            Client client = getUserweb().getClient();
            MetaDataPK pk = new MetaDataPK();
            // PK del METADATA
            pk.setCodClient(client.getClientCod());
            pk.setCodMeta(cod_meta);
            pk.setCodeChr(dataBean.getCode());

            Map<Integer, String> members = dataBean.getMembers();
            Set<Entry<Integer, String>> memberSet = members.entrySet();
            for (Entry<Integer, String> entry : memberSet) {
                pk.setCodMember(new Long(entry.getKey()));
                MetaData metaData = new MetaData(pk);

                if (pk.getCodMember() == 1L) {
                    /*
                     * recuperamos el metadato persistido actualmente en la base
                     * de datos, para desreferenciar a las entidades userphones
                     * que puedan estar asociadas a la misma, obtenemos la lista
                     * de userphones asociados a este meta
                     */
                    // MetaData persistedMeta = metaDataFacade.find(pk,
                    // "userphones","shiftPeriods");
                    // if (persistedMeta != null) {
                    // if (persistedMeta.getUserphones() != null
                    // && persistedMeta.getUserphones().size() > 0) {
                    // List<MetaData> metadataList = new ArrayList<MetaData>();
                    //
                    // /*
                    // * desreferenciamos al meta con el userphone
                    // */
                    // for (Userphone uphone : persistedMeta.getUserphones()) {
                    // uphone.setMetaData(metadataList);
                    // userPhoneFacade.edit(uphone);
                    // }
                    // }
                    // }

                    MetaData persistedMeta = metaDataFacade.find(pk,
                            "userphones", "shiftPeriods","metadatas", "metaData");

                    if (persistedMeta != null) {
                        if (persistedMeta.getMetaDataPK().getCodMeta().equals(
                                21L)) {
                            List<MetaData> list = metaDataFacade.findByClientMemberValue(
                                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                                    -1L, persistedMeta.getValueChr());

                            if (list != null && list.size() > 0) {
                                for (MetaData m : list) {
                                    try {
                                        metaDataFacade.remove(m);
                                    } catch (Exception e) {
                                        this.signal(
                                                Action.ERROR,
                                                i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                                                e);
                                    }
                                }
                            }
                        }
                        if (persistedMeta.getUserphones() != null
                            && persistedMeta.getUserphones().size() > 0) {

                            /*
                             * desreferenciamos al meta con el userphone
                             */

                            for (Userphone userphone : persistedMeta.getUserphones()) {
                                Userphone persistedUserphone = userPhoneFacade.find(
                                        userphone.getUserphoneCod(), "metaData");
                                persistedUserphone.getMetaData().remove(
                                        persistedMeta);
                                userPhoneFacade.edit(persistedUserphone);
                            }

                            persistedMeta.setUserphones(new ArrayList<Userphone>());
                            metaDataFacade.edit(persistedMeta);

                        }

                        if (persistedMeta.getShiftPeriods() != null
                            && persistedMeta.getShiftPeriods().size() > 0) {

                            /*
                             * desreferenciamos al meta con la ronda de guardia
                             */

                            for (ShiftPeriod sp : persistedMeta.getShiftPeriods()) {
                                ShiftPeriod persistedShift = shiftPeriodFacade.find(
                                        sp.getShiftPeriodCod(), "metaDatas");
                                persistedShift.getMetaDatas().remove(
                                        persistedMeta);
                                shiftPeriodFacade.edit(persistedShift);
                            }

                            persistedMeta.setShiftPeriods(new ArrayList<ShiftPeriod>());
                            metaDataFacade.edit(persistedMeta);

                        }
                        
                        /*Del meta que esta siendo editado buscamos la lista en la cual esta se encuentra éste se encuentra relacionada
                         *, eso lo recuperamos del campo metaData, tenemos que recorrer esta lista y eliminar este meta. Y en el campo
                         *metaData se encuentra la lista de metadatas asociados a este meta, aqui le seteamos una lista vacia para desrefenciar*/
                        if(persistedMeta.getMetaData() != null && persistedMeta.getMetaData().size() > 0 ) {
                             for(MetaData m : persistedMeta.getMetaData()) {
                                 List<MetaData> metadataList = facade.find(m.getMetaDataPK(),
                                            "metadatas").getMetadatas();
                                    if (metadataList != null && metadataList.size() > 0
                                        && metadataList.contains(persistedMeta)) {
                                        metadataList.remove(persistedMeta);
                                        m.setMetaData(metadataList);
                                        metaDataFacade.edit(m);
                                    }
                             }
                             
                             persistedMeta.setMetaData(new ArrayList<MetaData>());
                             metaDataFacade.edit(persistedMeta);
                        }
                        
                        
                        /*
                         * try{
                         * 
                         * Verificamos si tiene un MapMark asociado de ser asi
                         * eliminamos dicha relacion if(persistedMeta != null &&
                         * persistedMeta.getMapMark() != null) { MapMark mm =
                         * mapMarksFacade
                         * .find(persistedMeta.getMapMark().getMapMarkCod
                         * (),"metaDataList","routeDetails"); if(mm != null &&
                         * (mm.getMetaDataList() != null &&
                         * mm.getMetaDataList().size() >0)) { List<MetaData>
                         * list = mm.getMetaDataList(); for(MetaData m : list) {
                         * if
                         * (m.getMetaDataPK().equals(persistedMeta.getMetaDataPK
                         * ())) { list.remove(m); break; } }
                         * mm.setMetaDataList(list); mm =
                         * mapMarksFacade.edit(mm); //En caso que el MapMark no
                         * tenga relacion con MetaData y Hoja de Ruta lo
                         * eliminamos if((mm.getMetaDataList() == null ||
                         * mm.getMetaDataList().size() == 0) &&(
                         * mm.getRouteDetails() == null ||
                         * mm.getRouteDetails().size() == 0)) {
                         * mapMarksFacade.remove(mm); } }
                         * 
                         * 
                         * } }catch(Exception e) { this.signal( Action.ERROR,
                         * i18n.iValue(
                         * "web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"
                         * ), e); }
                         */

                        try {

                            /*
                             * Verificamos si tiene un MapMark asociado de ser
                             * asi eliminamos dicha relacion
                             */
                            if (persistedMeta != null
                                && persistedMeta.getMapMark() != null) {
                                MapMark mm = mapMarksFacade.find(
                                        persistedMeta.getMapMark().getMapMarkCod(),
                                        "routeDetails");
                                if (mm != null
                                    && (mm.getRouteDetails() != null && mm.getRouteDetails().size() == 0)) {
                                    mapMarksFacade.remove(mm);
                                }
                            }
                        } catch (Exception e) {
                            this.signal(
                                    Action.ERROR,
                                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"),
                                    e);
                        }

                    }
                }
            }

        }
        super.deleteEntitiesMass();
        dataModelSpecific = null;
        return null;
    }

    // NEW METHODS
    public String deleteEntitiesPlusMember() {
        boolean flagAtLeastOne = false;
        String entitiesNotDeletedInUse = "";
        String entitiesNotDeletedError = "";
        for (Data dataBean : dataModelSpecific) {
            if (getSelectedItems().get(dataBean.getDataPK())) {
                flagAtLeastOne = true;
                try {
                    // deleteMetaDatasByCodeChr(dataBean.getCodeChr());

                    /*
                     * Creamos una instancia de MetaDataPK en base a los
                     * DataBean que nos vinos como parametro al seleccionados en
                     * los checkbox
                     */
                    Client client = getUserweb().getClient();
                    MetaDataPK pk = new MetaDataPK();
                    // PK del METADATA
                    pk.setCodClient(client.getClientCod());
                    pk.setCodMeta(cod_meta);
                    pk.setCodeChr(dataBean.getCode());

                    Map<Integer, String> members = dataBean.getMembers();
                    Set<Entry<Integer, String>> memberSet = members.entrySet();
                    for (Entry<Integer, String> entry : memberSet) {
                        pk.setCodMember(new Long(entry.getKey()));
                        MetaData metaData = new MetaData(pk);

                        if (pk.getCodMember() == 1L) {
                            /*
                             * recuperamos el metadato persistido actualmente en
                             * la base de datos, para desreferenciar a las
                             * entidades userphones que puedan estar asociadas a
                             * la misma, obtenemos la lista de userphones
                             * asociados a este meta
                             */
                            MetaData persistedMeta = metaDataFacade.find(pk,
                                    "userphones", "shiftPeriods","metadatas", "metaData");

                            if (persistedMeta.getMetaDataPK().getCodMeta().equals(
                                    21L)) {
                                List<MetaData> list = metaDataFacade.findByClientMemberValue(
                                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                                        -1L, persistedMeta.getValueChr());

                                if (list != null && list.size() > 0) {
                                    for (MetaData m : list) {
                                        try {
                                            metaDataFacade.remove(m);
                                        } catch (Exception e) {
                                            this.signal(
                                                    Action.ERROR,
                                                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                                                    e);
                                        }
                                    }
                                }
                            }

                            if (persistedMeta != null) {
                                if (persistedMeta.getUserphones() != null
                                    && persistedMeta.getUserphones().size() > 0) {

                                    /*
                                     * desreferenciamos al meta con el userphone
                                     */

                                    for (Userphone userphone : persistedMeta.getUserphones()) {
                                        Userphone persistedUserphone = userPhoneFacade.find(
                                                userphone.getUserphoneCod(),
                                                "metaData");
                                        persistedUserphone.getMetaData().remove(
                                                persistedMeta);
                                        userPhoneFacade.edit(persistedUserphone);
                                    }

                                    persistedMeta.setUserphones(new ArrayList<Userphone>());
                                    metaDataFacade.edit(persistedMeta);

                                }

                                if (persistedMeta.getShiftPeriods() != null
                                    && persistedMeta.getShiftPeriods().size() > 0) {

                                    /*
                                     * desreferenciamos al meta con la ronda de
                                     * guardia
                                     */

                                    for (ShiftPeriod sp : persistedMeta.getShiftPeriods()) {
                                        ShiftPeriod persistedShift = shiftPeriodFacade.find(
                                                sp.getShiftPeriodCod(),
                                                "metaDatas");
                                        persistedShift.getMetaDatas().remove(
                                                persistedMeta);
                                        shiftPeriodFacade.edit(persistedShift);
                                    }

                                    persistedMeta.setShiftPeriods(new ArrayList<ShiftPeriod>());
                                    metaDataFacade.edit(persistedMeta);

                                }
                                
                                /*Del meta que esta siendo editado buscamos la lista en la cual esta se encuentra éste se encuentra relacionada
                                 *, eso lo recuperamos del campo metaData, tenemos que recorrer esta lista y eliminar este meta. Y en el campo
                                 *metaData se encuentra la lista de metadatas asociados a este meta, aqui le seteamos una lista vacia para desrefenciar*/
                                if(persistedMeta.getMetaData() != null && persistedMeta.getMetaData().size() > 0 ) {
                                     for(MetaData m : persistedMeta.getMetaData()) {
                                         List<MetaData> metadataList = facade.find(m.getMetaDataPK(),
                                                    "metadatas").getMetadatas();
                                            if (metadataList != null && metadataList.size() > 0
                                                && metadataList.contains(persistedMeta)) {
                                                metadataList.remove(persistedMeta);
                                                m.setMetaData(metadataList);
                                                metaDataFacade.edit(m);
                                            }
                                     }
                                     
                                     persistedMeta.setMetaData(new ArrayList<MetaData>());
                                     metaDataFacade.edit(persistedMeta);
                                }
                            }

                            /*
                             * try{
                             * 
                             * Verificamos si tiene un MapMark asociado de ser
                             * asi eliminamos dicha relacion if(persistedMeta !=
                             * null && persistedMeta.getMapMark() != null) {
                             * MapMark mm =
                             * mapMarksFacade.find(persistedMeta.getMapMark
                             * ().getMapMarkCod
                             * (),"metaDataList","routeDetails"); if(mm != null
                             * && (mm.getMetaDataList() != null &&
                             * mm.getMetaDataList().size() >0)) { List<MetaData>
                             * list = mm.getMetaDataList(); for(MetaData m :
                             * list) {
                             * if(m.getMetaDataPK().equals(persistedMeta.
                             * getMetaDataPK())) { list.remove(m); break; } }
                             * mm.setMetaDataList(list); mm =
                             * mapMarksFacade.edit(mm); //En caso que el MapMark
                             * no tenga relacion con MetaData y Hoja de Ruta lo
                             * eliminamos if((mm.getMetaDataList() == null ||
                             * mm.getMetaDataList().size() == 0) &&(
                             * mm.getRouteDetails() == null ||
                             * mm.getRouteDetails().size() == 0)) {
                             * mapMarksFacade.remove(mm); } }
                             * 
                             * 
                             * } }catch(Exception e) { this.signal(
                             * Action.ERROR, i18n.iValue(
                             * "web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"
                             * ), e); }
                             */

                            try {

                                /*
                                 * Verificamos si tiene un MapMark asociado de
                                 * ser asi eliminamos dicha relacion
                                 */
                                if (persistedMeta != null
                                    && persistedMeta.getMapMark() != null) {
                                    MapMark mm = mapMarksFacade.find(
                                            persistedMeta.getMapMark().getMapMarkCod(),
                                            "routeDetails");
                                    if (mm != null
                                        && (mm.getRouteDetails() != null && mm.getRouteDetails().size() == 0)) {
                                        mapMarksFacade.remove(mm);
                                    }
                                }
                            } catch (Exception e) {
                                this.signal(
                                        Action.ERROR,
                                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"),
                                        e);
                            }

                        }

                    }

                    deleteMetaDatasByCodeChrFromDeleteEntitiesPlusMember(dataBean.getCode());
                    notifier.signal(
                            CrudMetaDataClientBean.class,
                            Action.DELETE,
                            "Se han borrado datos de '"
                                + metaLabel
                                + "' para el cliente "
                                + getUserweb().getClient().getClientCod().toString()
                                + ", codigo: " + metaLabel + ".");
                } catch (EJBException ejbe) {
                    if (ejbe.getCausedByException().getClass().equals(
                            javax.transaction.RollbackException.class)) {
                        entitiesNotDeletedInUse += ", ".concat(dataBean.toString());
                    } else {
                        entitiesNotDeletedError += ", ".concat(dataBean.toString());
                    }
                } catch (Exception e) {
                    // @APP NOTIFICATION
                    this.signal(
                            Action.ERROR,
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"),
                            e);
                    entitiesNotDeletedError += ", ".concat(dataBean.toString());
                }

            }
        }

        if (flagAtLeastOne) {
            filterSelectedField = "-1";
            filterCriteria = "";
            paginationHelper = null; // For pagination recreation
            dataModelSpecific = null; // For data model recreation
            selectedItems = null; // For clearing selection

            if (entitiesNotDeletedInUse.length() == 0
                && entitiesNotDeletedError.length() == 0) {
                setSuccessMessage(getOkMessageOnDeleteByGroup());
            } else {
                if (entitiesNotDeletedInUse.length() > 0) {
                    entitiesNotDeletedInUse = entitiesNotDeletedInUse.substring(2);
                    setWarnMessage(getWarnMessageOnDeleteConstraintByGroup().concat(
                            entitiesNotDeletedInUse).concat("."));
                }
                if (entitiesNotDeletedError.length() > 0) {
                    entitiesNotDeletedError = entitiesNotDeletedError.substring(2);
                    setWarnMessage(getErrorMessageOnDeleteByGroup().concat(
                            entitiesNotDeletedError).concat("."));
                    // @APP NOTIFICATION
                    this.signal(
                            Action.ERROR,
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"));
                }
            }
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneDelete"));
        }

        return null;
    }

    public String editEntityPlusMember() {
        // metaClient = null;
        data = null;
        for (Data dataBean : dataModelSpecific) {
            if (getSelectedItems().get(dataBean.getDataPK())) {
                if (data == null) {
                    data = dataFacade.findByClass(getDataClass(),
                            dataBean.getDataPK());
                } else {
                    data = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
                    return null;
                }
            }
        }

        if (data == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
            return null;
        }

        MetaDataPK pk = new MetaDataPK();
        // PK del METADATA
        pk.setCodClient(data.getDataPK().getCodClient());
        pk.setCodMeta(data.getDataPK().getCodMeta());
        pk.setCodeChr(data.getDataPK().getCodigo());
        pk.setCodMember(1L);
        MetaData md = metaDataFacade.find(pk, "userphones");
        userphoneList = userPhoneFacade.findByUserwebAndClassification(getUserweb());
        if (md.getUserphones() != null && md.getUserphones().size() > 0) {
            for (Userphone u : md.getUserphones()) {
                getAssociatedUserphonesList().add(u);
                userphoneList.remove(u);
            }
        }

        mapModel = null;
        geoCoor = false;
        selectedDataIconMap = null;
        selectedDataIcon = null;
        mapMark = null;

        /*
         * Buscamos el metadata cuyo member sea -1 para saber si tiene algun
         * punto asignado
         */
        selectedDataIcon = metaDataFacade.findByClientMetaMemberAndCode(
                data.getDataPK().getCodClient(), data.getDataPK().getCodMeta(),
                -1l, data.getDataPK().getCodigo());
        if (selectedDataIcon != null) {
            /* Agregamos al hashmap para que aparezca con el check */
            getSelectedDataIconMap().put(selectedDataIcon.getValueChr(), true);
            geoCoor = true;
            getMapModel();
            mapMark = md.getMapMark();
            if (mapMark != null) {
                LatLng latlng = new LatLng(mapMark.getLatitudeNum(), mapMark.getLongitudeNum());
                selectedMarker = new ClientMarker(latlng, selectedDataIcon.getValueChr());
                selectedMarker.setTitle(mapMark.getTitleChr());
                selectedMarker.setData(mapMark.getDescriptionChr());
                getMapModel().addOverlay(selectedMarker);

            }
            MetaData aux = new MetaData(md.getMetaDataPK().getCodClient(), 21L, 1L, "-1");
            aux.setValueChr(urlIconDefault);

            if (getDataIconList().contains(aux)) {
                dataIconList.remove(aux);
                getDataIconDescription().remove(aux.getValueChr());
            }

        } else {
            if (md != null && md.getMapMark() != null) {
                selectedDataIcon = new MetaData(md.getMetaDataPK().getCodClient(), 21L, 1L, "-1");
                selectedDataIcon.setValueChr(urlIconDefault);
                if (getDataIconList() == null) {
                    dataIconList = new ArrayList<MetaData>();

                }

                if (!getDataIconList().contains(selectedDataIcon)) {
                    dataIconList.add(selectedDataIcon);
                }

                /* Agregamos al hashmap para que aparezca con el check */
                getSelectedDataIconMap().put(selectedDataIcon.getValueChr(),
                        true);
                getDataIconDescription().put(urlIconDefault, descriptionUrl);
                geoCoor = true;
                getMapModel();
                mapMark = md.getMapMark();
                if (mapMark != null) {
                    LatLng latlng = new LatLng(mapMark.getLatitudeNum(), mapMark.getLongitudeNum());
                    selectedMarker = new ClientMarker(latlng, selectedDataIcon.getValueChr());
                    selectedMarker.setTitle(mapMark.getTitleChr());
                    selectedMarker.setData(mapMark.getDescriptionChr());
                    getMapModel().addOverlay(selectedMarker);
                }
            }
        }

        return null;
    }

    public String deleteEditingPlusMember() {
        /*
         * try{
         * 
         * //Recuperamos el MetaDato con member 1 MetaData md =
         * metaDataFacade.findByClientMetaMemberAndCode
         * (data.getDataPK().getCodClient(), data.getDataPK().getCodMeta(), 1l,
         * data.getCode());
         * 
         * //Verificamos si tiene un MapMark asociado de ser asi eliminamos
         * dicha relacion if(md != null && md.getMapMark() != null) { MapMark mm
         * = mapMarksFacade.find(md.getMapMark().getMapMarkCod(),"metaDataList",
         * "routeDetails"); if(mm != null && (mm.getMetaDataList() != null &&
         * mm.getMetaDataList().size() >0)) { List<MetaData> list =
         * mm.getMetaDataList(); for(MetaData m : list) {
         * if(m.getMetaDataPK().equals(md.getMetaDataPK())) { list.remove(m);
         * break; } } mm.setMetaDataList(list); mm = mapMarksFacade.edit(mm);
         * //En caso que el MapMark no tenga relacion con MetaData y Hoja de
         * Ruta lo eliminamos if((mm.getMetaDataList() == null ||
         * mm.getMetaDataList().size() == 0) &&( mm.getRouteDetails() == null ||
         * mm.getRouteDetails().size() == 0)) { mapMarksFacade.remove(mm); } }
         * 
         * 
         * } }catch(Exception e) { this.signal( Action.ERROR, i18n.iValue(
         * "web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"
         * ), e); }
         */

        try {
            MetaDataPK pk = new MetaDataPK(data.getDataPK().getCodClient(), data.getDataPK().getCodMeta(), 1l, data.getCode());
            MetaData md = metaDataFacade.find(pk, "userphones", "shiftPeriods","metadatas", "metaData");

            if (md.getUserphones() != null && md.getUserphones().size() > 0) {

                /*
                 * desreferenciamos al meta con el userphone
                 */

                for (Userphone userphone : md.getUserphones()) {
                    Userphone persistedUserphone = userPhoneFacade.find(
                            userphone.getUserphoneCod(), "metaData");
                    persistedUserphone.getMetaData().remove(md);
                    userPhoneFacade.edit(persistedUserphone);
                }

                md.setUserphones(new ArrayList<Userphone>());
                metaDataFacade.edit(md);

            }

            if (md.getShiftPeriods() != null && md.getShiftPeriods().size() > 0) {

                /*
                 * desreferenciamos al meta con la ronda de guardia
                 */

                for (ShiftPeriod sp : md.getShiftPeriods()) {
                    ShiftPeriod persistedShift = shiftPeriodFacade.find(
                            sp.getShiftPeriodCod(), "metaDatas");
                    persistedShift.getMetaDatas().remove(md);
                    shiftPeriodFacade.edit(persistedShift);
                }

                md.setShiftPeriods(new ArrayList<ShiftPeriod>());
                metaDataFacade.edit(md);

            }
            
            /*Del meta que esta siendo editado buscamos la lista en la cual esta se encuentra éste se encuentra relacionada
             *, eso lo recuperamos del campo metaData, tenemos que recorrer esta lista y eliminar este meta. Y en el campo
             *metaData se encuentra la lista de metadatas asociados a este meta, aqui le seteamos una lista vacia para desrefenciar*/
            if(md.getMetaData() != null && md.getMetaData().size() > 0 ) {
                 for(MetaData m : md.getMetaData()) {
                     List<MetaData> metadataList = facade.find(m.getMetaDataPK(),
                                "metadatas").getMetadatas();
                        if (metadataList != null && metadataList.size() > 0
                            && metadataList.contains(md)) {
                            metadataList.remove(md);
                            m.setMetaData(metadataList);
                            metaDataFacade.edit(m);
                        }
                 }
                 md.setMetaData(new ArrayList<MetaData>());
                 metaDataFacade.edit(md);
            }
            
            

            if (md.getMetaDataPK().getCodMeta().equals(21L)) {
                List<MetaData> list = metaDataFacade.findByClientMemberValue(
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                        -1L, md.getValueChr());

                if (list != null && list.size() > 0) {
                    for (MetaData m : list) {
                        try {
                            metaDataFacade.remove(m);
                        } catch (Exception e) {
                            this.signal(
                                    Action.ERROR,
                                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                                    e);
                        }
                    }
                }
            }

            this.deleteMetaDatasByCodeChr(data.getCode());
            /*
             * Verificamos si tiene un MapMark asociado de ser asi eliminamos
             * dicha relacion
             */
            if (md != null && md.getMapMark() != null) {
                MapMark mm = mapMarksFacade.find(
                        md.getMapMark().getMapMarkCod(), "routeDetails");
                if (mm != null
                    && (mm.getRouteDetails() != null && mm.getRouteDetails().size() == 0)) {
                    mapMarksFacade.remove(mm);
                }
            }
        } catch (Exception e) {
            this.signal(
                    Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"),
                    e);
        }

        dataModelSpecific = null;
        data = null;
        return null;
    }

    /*
     * Asociacion de metadata (un metadata) y userphone
     * 
     * Metodo invocado al guardar un nuevo meta o al editar uno existente
     */
    public String savePlusMember() {

        if (data.getCode() == null || data.getCode().equals("")) {
            data.setCode(null);
            setErrorMessage(i18n.iValue("web.client.backingBean.metadata.CodeNull"));
            return null;
        }

        try {
            Client client = getUserweb().getClient();
            MetaDataPK pk = new MetaDataPK();
            // PK del METADATA
            pk.setCodClient(client.getClientCod());
            pk.setCodMeta(cod_meta);
            pk.setCodeChr(data.getCode());

            /*
             * else{ try{
             * 
             * /*En caso de no tener seleccionado ningun icono verificamos si se
             * trata de una actualizacion para eliminar el metadata con member
             * -1 y recuperamos el Metadata con member 1 para eliminar el punto
             * de la tabla mapMark
             */
            /*
             * MetaData md =
             * metaDataFacade.findByClientMetaMemberAndCode(data.getDataPK
             * ().getCodClient(), data.getDataPK().getCodMeta(), 1l,
             * data.getCode());
             * 
             * metaDataFacade.deleteMetaDataByMember(pk.getCodClient(),
             * pk.getCodMeta(), -1L, pk.getCodeChr()); /*Verificamos si tiene un
             * MapMark asociado de ser asi eliminamos dicha relacion if(md !=
             * null && md.getMapMark() != null) { MapMark mm =
             * mapMarksFacade.find
             * (md.getMapMark().getMapMarkCod(),"metaDataList","routeDetails");
             * if(mm != null && (mm.getMetaDataList() != null &&
             * mm.getMetaDataList().size() >0)) { List<MetaData> list =
             * mm.getMetaDataList(); for(MetaData m : list) {
             * if(m.getMetaDataPK().equals(md.getMetaDataPK())) {
             * list.remove(m); break; } } mm.setMetaDataList(list); mm =
             * mapMarksFacade.edit(mm); //En caso que el MapMark no tenga
             * relacion con MetaData y Hoja de Ruta lo eliminamos
             * if((mm.getMetaDataList() == null || mm.getMetaDataList().size()
             * == 0) &&( mm.getRouteDetails() == null ||
             * mm.getRouteDetails().size() == 0)) { mapMarksFacade.remove(mm); }
             * } } }catch(Exception e) { this.signal( Action.ERROR, i18n.iValue(
             * "web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"
             * ), e); }
             * 
             * }
             */
            MapMark aux = null;
            Map<Integer, String> members = data.getMembers();
            Set<Entry<Integer, String>> memberSet = members.entrySet();
            for (Entry<Integer, String> entry : memberSet) {
                pk.setCodMember(new Long(entry.getKey()));
                MetaData metaData = new MetaData(pk);

                // Obtiene el valor para el meta-data "valueChr"
                String member = members.get(entry.getKey());
                String memberMethod = "get".concat(
                        member.substring(0, 1).toUpperCase()).concat(
                        member.substring(1));
                Method method = data.getClass().getMethod(memberMethod,
                        (Class<?>[]) null);
                String metaMemberValue = (String) method.invoke(data,
                        (Object[]) null);

                // Los metamembers 1 representan el mÃ­nimo dato para el
                // meta-data, no puede ser nulo
                if (entry.getKey() == 1
                    && (metaMemberValue == null || metaMemberValue.isEmpty())) {
                    throw new Exception(" El código y el campo " + member
                        + " requieren de un valor para continuar.");
                } else if (metaMemberValue != null
                    && !metaMemberValue.isEmpty()) {
                    metaData.setValueChr(metaMemberValue);
                    if (pk.getCodMember() == 1L) {
                        try {
                            aux = metaDataFacade.find(pk).getMapMark();
                        } catch (Exception e) {

                        }
                    }
                    createOrSaveMetaData(metaData);
                    if (pk.getCodMember() == 1L) {

                        /*
                         * en el caso que el usuario haya seleccionado un
                         * userphone para asociarlo a este metadato, se realizas
                         * la persistencia de esta relacion
                         */

                        /*
                         * recuperamos el metadato persistido actualmente en la
                         * base de datos, para desreferenciar a las entidades
                         * userphones que puedan estar asociadas a la misma,
                         * obtenemos la lista de userphones asociados a este
                         * meta
                         */
                        MetaData persistedMeta = metaDataFacade.find(pk,
                                "userphones");

                        /*
                         * como en ambas entidades son listas, por la relacion
                         * many-to-many creamos las listas, y asignamos tanto el
                         * meta como el userphone para realizar la persistencia
                         * de la entidad. esto hacemos para que la referencia de
                         * meta este en userphone, y la refenrecia de userphone,
                         * este en meta. esta asignacion se realiza solo para el
                         * caso del member 1L
                         */
                        List<Userphone> listUserphones = new ArrayList<Userphone>();

                        if (associatedUserphonesList != null
                            && associatedUserphonesList.size() > 0) {
                            for (Userphone u : associatedUserphonesList) {
                                List<MetaData> metadataList = userPhoneFacade.find(
                                        u.getUserphoneCod(), "metaData").getMetaData();
                                if (metadataList == null) {
                                    metadataList = new ArrayList<MetaData>();
                                }
                                if (!metadataList.contains(persistedMeta)) {
                                    metadataList.add(persistedMeta);
                                    u.setMetaData(metadataList);
                                    userPhoneFacade.edit(u);
                                }
                            }
                            listUserphones = associatedUserphonesList;

                        }
                        persistedMeta.setUserphones(listUserphones);

                        for (Userphone u : userphoneList) {
                            List<MetaData> metadataList = userPhoneFacade.find(
                                    u.getUserphoneCod(), "metaData").getMetaData();
                            if (metadataList != null && metadataList.size() > 0
                                && metadataList.contains(persistedMeta)) {
                                metadataList.remove(persistedMeta);
                                u.setMetaData(metadataList);
                                userPhoneFacade.edit(u);
                            }
                        }

                        associateMetaDatas(pk);

                        if (selectedDataIcon != null) {
                            /*
                             * Verificamos si selecciono algun icono de ser asi
                             * tambie tuvo que agregar un punto al mapa
                             */
                            if (mapMark != null) {
                                /*
                                 * buscamos el MapMark de ese cliente y lat y
                                 * long si no existe creamos y si existe le
                                 * agregamos la relacion con metaData y
                                 * actualizamos
                                 */

                                MapMark md2 = mapMarksFacade.findByClientLatLng(
                                        SecurityBean.getInstance().getLoggedInUserClient().getClient(),
                                        mapMark.getLatitudeNum(),
                                        mapMark.getLongitudeNum());
                                if (md2 != null) {
                                    md2.setTitleChr(mapMark.getTitleChr());
                                    md2.setDescriptionChr(mapMark.getDescriptionChr());
                                    mapMark = mapMarksFacade.edit(md2);
                                } else {
                                    mapMark.setTitleChr(persistedMeta.getMetaClient().getMeta().getDescriptionChr()
                                        + ": "
                                        + persistedMeta.getMetaDataPK().getCodeChr()
                                        + "-" + persistedMeta.getValueChr());
                                    mapMark.setDescriptionChr(persistedMeta.getMetaClient().getMeta().getDescriptionChr()
                                        + ". Codigo: "
                                        + persistedMeta.getMetaDataPK().getCodeChr()
                                        + ", Nombre:"
                                        + persistedMeta.getValueChr());
                                    mapMark = mapMarksFacade.create(mapMark);
                                }

                                persistedMeta.setMapMark(mapMark);
                                createOrSaveMetaData(persistedMeta);

                                if (!selectedDataIcon.getValueChr().equals(
                                        urlIconDefault)) {
                                    pk.setCodMember(-1L);
                                    MetaData metaData2 = new MetaData(pk);
                                    metaData2.setValueChr(selectedDataIcon.getValueChr());
                                    createOrSaveMetaData(metaData2);
                                }
                            } else {
                                setWarnMessage(i18n.iValue("web.client.backingBean.metadata.mustselectIcon"));
                                return null;
                            }
                        } else {
                            try {

                                /*
                                 * En caso de no tener seleccionado ningun icono
                                 * verificamos si se trata de una actualizacion
                                 * para eliminar el metadata con member -1 y
                                 * recuperamos el Metadata con member 1 para
                                 * eliminar el punto de la tabla mapMark
                                 */

                                metaDataFacade.deleteMetaDataByMember(
                                        pk.getCodClient(), pk.getCodMeta(),
                                        -1L, pk.getCodeChr());
                                /*
                                 * Verificamos si tiene un MapMark asociado y si
                                 * el MapMark no esta asignado a ninguna hoja de
                                 * ruta lo eliminamos
                                 */
                                if (aux != null) {
                                    MapMark mm = mapMarksFacade.find(
                                            aux.getMapMarkCod(), "routeDetails");
                                    if (mm != null
                                        && (mm.getRouteDetails() == null || mm.getRouteDetails().size() == 0)) {
                                        mapMarksFacade.remove(mm);
                                    }
                                }
                            } catch (Exception e) {
                                this.signal(
                                        Action.ERROR,
                                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"),
                                        e);
                            }
                        }

                    }
                }
            }

            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));

        } catch (AssociateHoraryException e) {
            setWarnMessage(e.getMessage());
            this.signal(
                    Action.WARNING,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                    e);
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.message.Error")
                + e.getMessage() + ".");
            this.signal(
                    Action.ERROR,
                    i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotSaveError"),
                    e);
            // Las excepciones ya fueron tratadas en la clase padre
            // De igual manera se espera que al primer error en la escritura de
            // los datos en la BD
            // se interrumpa la operaciÃ³n
        }
        data = null;
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModelSpecific = null; // For data model recreation
        getSelectedItems().clear();// clearing selection
        selectedUserphone = null;
        selectedAssociatedUserphones = null;
        selectedUserphoneList = null;
        associatedUserphonesList = null;
        validatedAllUserphones = false;
        validatedAllUserphonesAssociated = false;
        userphoneList = null;
        dataModel = null;
        geoCoor = false;
        selectedDataIconMap = null;

        return null;
    }

    // NEW METHODS

    /**
     * Asociacion masiva de metadatas y userphones
     * 
     * metodo que posibilita la renderizacion de la seccion de asociacion masiva
     * de metadatas y userphones
     * 
     * @return
     */
    public String associatePlusMember() {
        /*
         * Al invocar este metodo desde la pantalla de metadatos, el boton
         * asociar metas a userphones, se renderiza la seccion de asociar metas
         */
        data = null;
        for (Data dataBean : dataModelSpecific) {
            if (getSelectedItems().get(dataBean.getDataPK())) {
                if (data == null) {
                    data = dataFacade.findByClass(getDataClass(),
                            dataBean.getDataPK());
                    associateSectionVisible = true;
                    break;
                }
            }
        }

        if (data == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneAssociate"));
        }

        userphoneList = userPhoneFacade.findByUserwebAndClassification(getUserweb());
        return null;
    }

    /**
     * Asociaciom masica de metadatas y userphones
     * 
     * metodo que realiza la pesistencia de la relacion existente entre uno o
     * varios MetaDatas y un Userphone
     * 
     * @return
     */
    public String saveAssociatePlusMember() {

        boolean flagAtLeastOne = false;
        String entitiesNotDeletedInUse = "";
        String entitiesNotDeletedError = "";

        /*
         * creamos las listas de meta y userphone para asiganar al momento de la
         * persistencia
         */
        List<MetaData> metadataList = new ArrayList<MetaData>();
        List<Userphone> listUserphones = new ArrayList<Userphone>();

        /*
         * Si selecciono la opciÃ³n Todos entonces la lista de userphones a
         * asociar sera las de userphoneList de lo contrario seran las de
         * selectedUserphoneList
         */
        if (validatedAllUserphones) {
            listUserphones = this.getUserphoneList();
        } else {
            listUserphones = getSelectedUserphoneList();
        }

        for (Data dataBean : dataModelSpecific) {
            if (getSelectedItems().get(dataBean.getDataPK())) {
                flagAtLeastOne = true;

                /*
                 * realizamos la busqueda del objeto persistido con la clave
                 * primaria del registro seleccionado
                 */
                MetaDataPK pk = new MetaDataPK();
                // PK del METADATA
                pk.setCodClient(dataBean.getDataPK().getCodClient());
                pk.setCodMeta(dataBean.getDataPK().getCodMeta());
                pk.setCodeChr(dataBean.getDataPK().getCodigo());
                pk.setCodMember(1L);

                /*
                 * obtenemos el objeto, con la lista de userphones asignados al
                 * meta
                 */
                MetaData persistedMeta = metaDataFacade.find(pk, "userphones");
                /*
                 * almacenamos en una lista los metadatos nuevos a ser
                 * relacionados con los userphones seleccionados
                 */
                metadataList.add(persistedMeta);
                flagAtLeastOne = true;

            }
        }

        if (flagAtLeastOne) {

            try {
                /*
                 * asignamos a los metadatos los userphones seleccionados
                 */
                if (listUserphones != null && listUserphones.size() > 0) {
                    for (Userphone u : listUserphones) {
                        // Recuperamos los metadatos del userphone
                        List<MetaData> listMetadata = userPhoneFacade.find(
                                u.getUserphoneCod(), "metaData").getMetaData();
                        if (listMetadata == null) {
                            listMetadata = new ArrayList<MetaData>();
                        }
                        // Recorremos la lista de metadatos del userphone para
                        // asociar los metadatos seleccionados
                        for (MetaData md : metadataList) {
                            if (!listMetadata.contains(md)) {
                                listMetadata.add(md);
                            }

                        }

                        u.setMetaData(listMetadata);
                        userPhoneFacade.edit(u);
                    }

                }

                // Recorremos la lista de metadatos seleccionados para asociarle
                // los userphones seleccionados
                for (MetaData meta : metadataList) {
                    /*
                     * En caso que el metadato no tenga ningun userphone
                     * asociado, le asiganamos directamente la lista de
                     * userphones seleccionados
                     */
                    if (meta.getUserphones() == null) {
                        meta.setUserphones(listUserphones);
                    } else {
                        /*
                         * De lo contrario recorremos la lista de userphones
                         * seleccionados para agregar a las del metadato
                         */
                        for (Userphone u : listUserphones) {
                            if (!meta.getUserphones().contains(u)) {
                                meta.getUserphones().add(u);
                            }
                        }
                    }
                    /*
                     * persistimos la entidad
                     */
                    createOrSaveMetaData(meta);
                }
            } catch (EJBException ejbe) {
                this.signal(
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"),
                        ejbe);
            } catch (Exception e) {
                // @APP NOTIFICATION
                this.signal(
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"),
                        e);

            }

            filterSelectedField = "-1";
            filterCriteria = "";
            paginationHelper = null; // For pagination recreation
            dataModelSpecific = null; // For data model recreation
            selectedItems = null; // For clearing selection
            selectedUserphone = null;
            associateSectionVisible = false;
            selectedAssociatedUserphones = null;
            selectedUserphoneList = null;
            associatedUserphonesList = null;
            validatedAllUserphones = false;
            validatedAllUserphonesAssociated = false;
            userphoneList = null;
            data = null;

            if (entitiesNotDeletedInUse.length() == 0
                && entitiesNotDeletedError.length() == 0) {
                setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));

            } else {
                this.signal(
                        Action.ERROR,
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ApplicationCouldNotDeleteError"));
            }
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneDelete"));
        }

        return null;
    }

    public String cancelAssociate() {
        associateSectionVisible = false;
        return this.cancelEditing();
    }

    public Boolean getGeoCoor() {
        return geoCoor;
    }

    public void setGeoCoor(Boolean geoCoor) {
        this.geoCoor = geoCoor;
    }

    public MetaData getSelectedDataIcon() {
        return selectedDataIcon;
    }

    public void setSelectedDataIcon(MetaData selectedDataIcon) {
        this.selectedDataIcon = selectedDataIcon;
    }

    public List<MetaData> getDataIconList() {
        if (dataIconList == null) {
            dataIconList = metaDataFacade.findByClientMetaMember(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    21L, 1L);
        }
        return dataIconList;
    }

    public void setDataIconList(List<MetaData> dataIconList) {
        this.dataIconList = dataIconList;
    }

    public Map<String, Boolean> getSelectedDataIconMap() {
        if (selectedDataIconMap == null) {
            selectedDataIconMap = new HashMap<String, Boolean>();
        }
        return selectedDataIconMap;
    }

    public void setSelectedDataIconMap(Map<String, Boolean> selectedDataIconMap) {
        this.selectedDataIconMap = selectedDataIconMap;
    }

    public void listenerDataIconSelect(MetaData ip) {
        if (getMapModel().getMarkers() != null
            && getMapModel().getMarkers().size() > 0) {
            for (Marker m : getMapModel().getMarkers()) {
                m.setIcon(ip.getValueChr());
                getMapModel().getMarkers().remove(m);
                getMapModel().addOverlay(m);
            }
        }
        selectedDataIconMap = null;
        selectedDataIcon = ip;
        getSelectedDataIconMap().put(ip.getValueChr(), true);
    }

    public void listenerCheckGeoCoor() {
        getMapModel();
        selectedDataIcon = null;
        selectedDataIconMap = null;
    }

    // METODOS DE MAPAS
    public void onMarkerSelect(OverlaySelectEvent event) {
        if (event.getOverlay() instanceof ClientMarker) {
            selectedMarker = (Marker) event.getOverlay();
            clientMarker = true;
            editMarker = false;
        } else if (event.getOverlay() instanceof Marker) {
            selectedMarker = (Marker) event.getOverlay();
            clientMarker = false;
            editMarker = false;
        }
    }

    public void onMapStateChange(StateChangeEvent event) {
        double x = (event.getBounds().getNorthEast().getLat() + event.getBounds().getSouthWest().getLat()) / 2;
        double y = (event.getBounds().getNorthEast().getLng() + event.getBounds().getSouthWest().getLng()) / 2;
        LatLng z = new LatLng(x, y);
        mapCenter = z;
        lastBounds = event.getBounds();

        mapZoom = event.getZoomLevel();
    }

    // MAPS
    public MapModel getMapModel() {
        if (mapModel == null) {
            mapModel = new DefaultMapModel();
            mapCenter = ApplicationBean.getInstance().getMapStartingPoint();
            mapZoom = Integer.parseInt(ApplicationBean.getInstance().getMapStartingZoom());
            mapType = ApplicationBean.getInstance().getMapStartingType();
        }
        return mapModel;
    }

    public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }

    public LatLng getMapCenter() {
        return mapCenter;
    }

    public void setMapCenter(LatLng mapCenter) {
        this.mapCenter = mapCenter;
    }

    public Integer getMapZoom() {
        return mapZoom;
    }

    public void setMapZoom(Integer mapZoom) {
        this.mapZoom = mapZoom;
    }

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public Marker getSelectedMarker() {
        return selectedMarker;
    }

    public void setSelectedMarker(Marker selectedMarker) {
        this.selectedMarker = selectedMarker;
    }

    public boolean isClientMarker() {
        return clientMarker;
    }

    public void setClientMarker(boolean clientMarker) {
        this.clientMarker = clientMarker;
    }

    public boolean isEditMarker() {
        return editMarker;
    }

    public void setEditMarker(boolean editMarker) {
        this.editMarker = editMarker;
    }

    public LatLngBounds getLastBounds() {
        return lastBounds;
    }

    public void setLastBounds(LatLngBounds lastBounds) {
        this.lastBounds = lastBounds;
    }

    public String getMapCenterStr() {
        if (mapCenter != null) {
            return mapCenter.getLat() + "," + mapCenter.getLng();
        }
        return "0,0";
    }

    public void onPointSelect(PointSelectEvent event) {
        /*
         * Aca verificamos que se agregue solo un punto por metadata que se este
         * creando o editando
         */
        if (mapModel.getMarkers().size() == 0) {
            LatLng latlng = event.getLatLng();
            ClientMarker nuevaMarca = new ClientMarker(latlng, selectedDataIcon.getValueChr());
            mapModel.addOverlay(nuevaMarca);
            if (mapMark == null) {
                mapMark = new MapMark();
                Double latitude = event.getLatLng().getLat();
                Double longitude = event.getLatLng().getLng();
                mapMark.setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
                mapMark.setLatitudeNum(latitude);
                mapMark.setLongitudeNum(longitude);
                mapMark.setUserweb(SecurityBean.getInstance().getLoggedInUserClient());
            }

        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.metadata.geoCoord.not.unique"));
        }

    }

    public void saveClientMarker() {
        if (clientMarker) {
            if (mapMark == null) {
                Double latitude = selectedMarker.getLatlng().getLat();
                Double longitude = selectedMarker.getLatlng().getLng();
                mapMark = new MapMark();
                mapMark.setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
                mapMark.setLatitudeNum(latitude);
                mapMark.setLongitudeNum(longitude);
                mapMark.setUserweb(SecurityBean.getInstance().getLoggedInUserClient());
                mapMark.setTitleChr(selectedMarker.getTitle());
                mapMark.setDescriptionChr(selectedMarker.getData().toString());
            } else {

                mapMark.setTitleChr(selectedMarker.getTitle());
                mapMark.setDescriptionChr(selectedMarker.getData().toString());
            }
        }
    }

    public void deleteClientMarker() {
        if (clientMarker) {
            try {
                mapMark = null;
                mapModel.getMarkers().remove(selectedMarker);
            } catch (Exception e) {
                notifier.log(getClass(), null, Action.ERROR, e.getMessage());
                setErrorMessage(i18n.iValue("web.client.backingbean.error.restriction.integrity"));
            }
        }
    }

    public Map<String, String> getDataIconDescription() {
        if (dataIconDescription == null) {
            dataIconDescription = new HashMap<String, String>();
            List<DataIcon> list = dataIconFacade.findByClient(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
            if (list != null) {
                for (DataIcon di : list) {
                    dataIconDescription.put(di.getUrl(), di.getDescripcion());
                }
            }
        }
        return dataIconDescription;
    }

    public void setDataIconDescription(Map<String, String> dataIconDescription) {
        this.dataIconDescription = dataIconDescription;
    }

    protected String associateMetaDatas(MetaDataPK pk) throws Exception {
        return null;
    }

    /* ASOCIACION DE HORARIOS */

    protected List<MetaData> listEmployeeSelected;
    protected boolean reasignar;
    protected HashMap<Integer, Boolean> hashMapDias;
    protected HashMap<Integer, List<HoraryPojo>> hashMapHorarios;
    protected final DateFormat dfformat = new SimpleDateFormat("HH:mm");
    protected static final Long COD_META_EMPLOYEE = 7L;
    protected static final Long COD_MEMBER = 1L;
    protected static final Long HORARY_COD_META = 29L;
    protected List<MetaData> associatedHoraryList;
    protected List<MetaData> horaryList;
    protected List<MetaData> selectedHoraryList;
    protected Boolean validatedAllHorary = false;
    protected Boolean validatedAllHoraryAssociated = false;
    protected List<MetaData> selectedAssociatedHoraryList;

    
    public String showReasignar() {

        horaryList = new ArrayList<MetaData>();
        /*
         * Recuperamos todos los horarios configurados de member 2 que es el
         * campo Habilitado
         */
        List<MetaData> horaryListEnabled = facade.findByClientMetaMember(
                getUserweb().getClient().getClientCod(), HORARY_COD_META, 2L);

        for (MetaData m : horaryListEnabled) {
            /*
             * Si el valor es igual a 1 significa que el horario se encuentra
             * habilitado y lo agregamos la lista horaryList
             */
            if (m.getValueChr().equals("1")) {
                MetaData aux = facade.findByClientMetaMemberAndCode(
                        getUserweb().getClient().getClientCod(),
                        HORARY_COD_META, COD_MEMBER,
                        m.getMetaDataPK().getCodeChr());
                if (aux != null) {
                    horaryList.add(aux);
                }

            }
        }
        return null;
    }

    public void cargarHashMapHorarios() {

        /*
         * Recorremos la lista de horarios habilitados y por cada horario
         * recuperamos el metadata con member 7 (dia), 8 (horaEntrada) y 9
         * (horaSalida)
         */
        for (MetaData m : getHoraryList()) {

            MetaData dia = facade.findByClientMetaMemberAndCode(
                    getUserweb().getClient().getClientCod(), HORARY_COD_META,
                    7l, m.getMetaDataPK().getCodeChr());

            MetaData horaEntrada = facade.findByClientMetaMemberAndCode(
                    getUserweb().getClient().getClientCod(), HORARY_COD_META,
                    8l, m.getMetaDataPK().getCodeChr());

            MetaData horaTrabajada = facade.findByClientMetaMemberAndCode(
                    getUserweb().getClient().getClientCod(), HORARY_COD_META,
                    9l, m.getMetaDataPK().getCodeChr());

            List<String> diasList = Arrays.asList(dia.getValueChr().split(","));
            List<String> horaEntradaList = Arrays.asList(horaEntrada.getValueChr().split(
                    ","));
            List<String> horaTrabajadaList = Arrays.asList(horaTrabajada.getValueChr().split(
                    ","));

            /* Recorremos por los dias de la semana */
            for (int i = 0; i < 7; i++) {
                /*
                 * Si el dia de la semana es 1 significa que existe un horario
                 * configurado para ese dia entonces recuperamos la lista de
                 * HoraryPojo en donde se guarda la hora de entrada, horas
                 * trabajadas la descripcion y el codigo de metadata
                 */
                if (diasList.get(i).equals("1")) {
                    getHashMapDias().put(i, true);

                    if (getHashMapHorarios().get(i) == null) {
                        getHashMapHorarios().put(i, new ArrayList<HoraryPojo>());
                    }
                    try {
                        HoraryPojo hp = new HoraryPojo(dfformat.parse(horaEntradaList.get(i)), dfformat.parse(horaTrabajadaList.get(i)), m.getValueChr(), m.getMetaDataPK().getCodeChr());
                        getHashMapHorarios().get(i).add(hp);
                    } catch (ParseException e) {
                        notifier.signal(getClass(), Action.ERROR,
                                e.getMessage(), e);
                    }

                } else {
                    getHashMapDias().put(i, false);
                }

            }

        }

    }

    /* Metodo invocado al presionar el boton Asociar */
    public String associateHorary() {
        /*
         * Recorremos por todos los dias de la semana y volvemos a deseleccionar
         * toda la lista de horarios por cada dia de la semana
         */
        for (int i = 0; i < 7; i++) {
            if (getHashMapDias().get(i)) {
                for (HoraryPojo hp : getHashMapHorarios().get(i)) {
                    hp.setChecked(false);
                }
            }
        }

        /*
         * Recorremos la lista de horarios seleccionados y para cada dia de la
         * semana recuperamos la lista de horarios configurados y si el codigo
         * de metadata coinciden le ponemos como checked true
         */
        for (MetaData horario : getSelectedHoraryList()) {
            for (int i = 0; i < 7; i++) {
                if (getHashMapDias().get(i)) {
                    for (HoraryPojo hp : getHashMapHorarios().get(i)) {
                        if (hp.getCode().equals(
                                horario.getMetaDataPK().getCodeChr())) {
                            hp.setChecked(true);
                            break;
                        }
                    }
                }
            }
        }

        /*
         * Recorremos la lista de empleados seleccionados y luego la lista de
         * horarios seleccionados y por cada horario recuperamos sus dias member
         * 7, horaEntrada member 8, horaSalida member 9 y la lista de empleados
         * asociados y le asignamos el empleado seleccionado
         */
        if (listEmployeeSelected != null && listEmployeeSelected.size() > 0) {
            for (MetaData empleado : listEmployeeSelected) {
                for (MetaData horario : getSelectedHoraryList()) {

                    MetaData dia = facade.findByClientMetaMemberAndCode(
                            getUserweb().getClient().getClientCod(),
                            HORARY_COD_META, 7l,
                            horario.getMetaDataPK().getCodeChr());

                    MetaData horaEntrada = facade.findByClientMetaMemberAndCode(
                            getUserweb().getClient().getClientCod(),
                            HORARY_COD_META, 8l,
                            horario.getMetaDataPK().getCodeChr());

                    MetaData horaTrabajada = facade.findByClientMetaMemberAndCode(
                            getUserweb().getClient().getClientCod(),
                            HORARY_COD_META, 9l,
                            horario.getMetaDataPK().getCodeChr());

                    List<String> diasList = Arrays.asList(dia.getValueChr().split(
                            ","));
                    List<String> horaEntradaList = Arrays.asList(horaEntrada.getValueChr().split(
                            ","));
                    List<String> horaTrabajadaList = Arrays.asList(horaTrabajada.getValueChr().split(
                            ","));

                    /*
                     * Recorremos los dias de la semana y recuperamos la hora de
                     * entrada y la hora trabajada de cada dia, la hora de
                     * salida calculamos hora de entrada mas horas trabajadas
                     */
                    for (int i = 0; i < 7; i++) {
                        try {
                            Integer day = Integer.valueOf(diasList.get(i));
                            Date entrada = dfformat.parse(horaEntradaList.get(i));
                            Date horaTrabajo = dfformat.parse(horaTrabajadaList.get(i));
                            Calendar horaEntradaCalendar = Calendar.getInstance();
                            horaEntradaCalendar.setTime(entrada);
                            Calendar horaSalidaCalendar = Calendar.getInstance();
                            horaSalidaCalendar.setTime(entrada);
                            horaSalidaCalendar.add(Calendar.HOUR_OF_DAY,
                                    horaTrabajo.getHours());
                            horaSalidaCalendar.add(Calendar.MINUTE,
                                    horaTrabajo.getMinutes());
                            String aux = verificarHorario(day,
                                    horaEntradaCalendar.getTime(),
                                    horaSalidaCalendar.getTime(),
                                    horario.getValueChr(),
                                    horario.getMetaDataPK().getCodeChr());

                            /*
                             * Si el horario se solapa con otro el metodo
                             * horarioSolapado va a devolver un string distinto
                             * a nulo
                             */
                            if (aux != null) {
                                setWarnMessage(aux);
                                return null;
                            }
                        } catch (Exception e) {
                            notifier.signal(getClass(), Action.ERROR,
                                    e.getMessage(), e);
                        }

                    }
                    /*
                     * Recuperamos la lista de empleados asociados al horario y
                     * si ya no se encuentra lo agregamos
                     */
                    List<MetaData> associateEmployeeList = metaDataFacade.findWithMetadatasViceversa(
                            horario.getMetaDataPK(), COD_META_EMPLOYEE);

                    if (associateEmployeeList == null) {
                        associateEmployeeList = new ArrayList<MetaData>();
                    }
                    if (!associateEmployeeList.contains(empleado)) {
                        associateEmployeeList.add(empleado);
                        horario.setMetaData(associateEmployeeList);
                        facade.edit(horario);
                    }
                }

                /*
                 * Recorremos la lista de horarios y si no se encuentra entre
                 * los seleccionados eliminamos al empleado de la lista de
                 * empleados asociado al horario
                 */
                for (MetaData m : horaryList) {

                    if (!getSelectedHoraryList().contains(m)) {
                        List<MetaData> metadataList = facade.find(
                                m.getMetaDataPK(), "metaData").getMetaData();
                        if (metadataList != null && metadataList.size() > 0
                            && metadataList.contains(empleado)) {
                            metadataList.remove(empleado);
                            m.setMetaData(metadataList);
                            facade.edit(m);
                        }
                    }
                }
            }
        }
        setReasignar(false);
        hashMapDias = null;
        hashMapHorarios = null;
        selectedHoraryList = null;
        return null;
    }

    public String cancelAssociateHorary() {
        setReasignar(false);
        hashMapDias = null;
        hashMapHorarios = null;
        selectedHoraryList = null;
        return null;
    }

    public String verificarHorario(Integer dia, Date horaEntrada, Date horaSalida, String horario, String code) {
        if (getHashMapDias().get(dia)) {
            for (HoraryPojo pj : getHashMapHorarios().get(dia)) {

                /*
                 * Verificar solo entre los horarios seleccionados y no
                 * verificar los mismos
                 */
                if (pj.getChecked() && !code.equals(pj.getCode())) {

                    /* Si los horarios se solapan */
                    if ((horaEntrada.getTime() >= pj.getStartTime().getTime() && horaEntrada.getTime() < pj.getEndTime().getTime())
                        || (horaSalida.getTime() >= pj.getStartTime().getTime() && horaSalida.getTime() < pj.getEndTime().getTime())) {
                        return MessageFormat.format(
                                i18n.iValue("web.client.backingBean.metadata.horary.HoraryConflict"),
                                horario, pj.getDescription());
                    }

                }
            }
        } else {
            return null;
        }
        return null;
    }
    
    public List<MetaData> getAssociatedHoraryList() {
        if (associatedHoraryList == null) {
            associatedHoraryList = new ArrayList<MetaData>();
        }
        return associatedHoraryList;
    }

    public void setAssociatedHoraryList(List<MetaData> associatedHoraryList) {
        this.associatedHoraryList = associatedHoraryList;
    }

    public List<MetaData> getHoraryList() {

        return horaryList;
    }

    public void setHoraryList(List<MetaData> horaryList) {
        this.horaryList = horaryList;
    }

    public List<MetaData> getSelectedHoraryList() {
        if (selectedHoraryList == null) {
            selectedHoraryList = new ArrayList<MetaData>();
        }
        return selectedHoraryList;
    }

    public void setSelectedHoraryList(List<MetaData> selectedHoraryList) {
        this.selectedHoraryList = selectedHoraryList;
    }

    public Boolean getValidatedAllHorary() {
        return validatedAllHorary;
    }

    public void setValidatedAllHorary(Boolean validatedAllHorary) {
        this.validatedAllHorary = validatedAllHorary;
    }

    public Boolean getValidatedAllHoraryAssociated() {
        return validatedAllHoraryAssociated;
    }

    public void setValidatedAllHoraryAssociated(Boolean validatedAllHoraryAssociated) {
        this.validatedAllHoraryAssociated = validatedAllHoraryAssociated;
    }

    public List<MetaData> getSelectedAssociatedHoraryList() {
        return selectedAssociatedHoraryList;
    }

    public void setSelectedAssociatedHoraryList(List<MetaData> selectedAssociatedHoraryList) {
        this.selectedAssociatedHoraryList = selectedAssociatedHoraryList;
    }


    public HashMap<Integer, Boolean> getHashMapDias() {
        if (hashMapDias == null) {
            hashMapDias = new HashMap<Integer, Boolean>();
        }
        return hashMapDias;
    }

    public void setHashMapDias(HashMap<Integer, Boolean> hashMapDias) {
        this.hashMapDias = hashMapDias;
    }

    public HashMap<Integer, List<HoraryPojo>> getHashMapHorarios() {
        if (hashMapHorarios == null) {
            hashMapHorarios = new HashMap<Integer, List<HoraryPojo>>();
        }
        return hashMapHorarios;
    }

    public void setHashMapHorarios(HashMap<Integer, List<HoraryPojo>> hashMapHorarios) {
        this.hashMapHorarios = hashMapHorarios;
    }

    public boolean isReasignar() {
        return reasignar;
    }

    public void setReasignar(boolean reasignar) {
        this.reasignar = reasignar;
    }

}

class ClientMarker extends Marker {

    private static final long serialVersionUID = 3850314913292747045L;

    public ClientMarker(LatLng latLng, String url) {
        super(latLng, null, null, url);
    }
}
