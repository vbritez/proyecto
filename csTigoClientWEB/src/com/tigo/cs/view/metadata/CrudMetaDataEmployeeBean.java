package com.tigo.cs.view.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.map.LatLng;

import com.tigo.cs.api.exception.AssociateHoraryException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.view.Data;
import com.tigo.cs.domain.view.DataEmployee;
import com.tigo.cs.view.pbeans.HoraryPojo;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CrudMetaDataEmployeeBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "crudMetaDataEmployeeBean")
@ViewScoped
public class CrudMetaDataEmployeeBean extends AbstractCrudMetaDataBean<DataEmployee> implements Serializable {

    private static final long serialVersionUID = 8198876820931165682L;
    private static final Long COD_META = 7L;
    private static final Long COD_MEMBER = 1L;

    public CrudMetaDataEmployeeBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_dataemployee");
        setXlsReportName("rep_dataemployee_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.employeeCrudMetaData.Employee"));
        }
        return metaLabel;
    }

    @Override
    public String editEntityPlusMember() {
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

        /* Recuperamos todos los HORARIOS asociados a AL EMPLEADO */
        List<MetaData> nationalPoliceContactsList = metaDataFacade.findWithMetadatas(
                pk, HORARY_COD_META);

        /* Recuperamos todos los horarios configurados para el cliente */
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
             * habilitado entonces recuperamos el metadata con member 1
             * (descripcion), 7 (dia), 8 (horaEntrada) y 9 (horaSalida)
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

        /*
         * Recorremos la lista de horarios asociados y lo agregamos a nuestra
         * lista y lo eliminamos de la lista de horarios
         */
        if (nationalPoliceContactsList != null
            && nationalPoliceContactsList.size() > 0) {
            for (MetaData m : nationalPoliceContactsList) {
                getAssociatedHoraryList().add(m);
                horaryList.remove(m);
            }
        }

        cargarHashMapHorarios();

        return null;
    }

    @Override
    public String cancelEditing() {
        super.cancelEditing();
        horaryList = null;
        selectedHoraryList = null;
        associatedHoraryList = null;
        selectedAssociatedHoraryList = null;
        validatedAllHorary = false;
        validatedAllHoraryAssociated = false;
        return null;
    }

    public String addHoraryToList() {
        if (validatedAllHorary) {
            for (MetaData m : horaryList) {
                getAssociatedHoraryList().add(m);
            }
            horaryList = null;
            selectedHoraryList = null;
            validatedAllHorary = false;
            return null;
        }

        if (selectedHoraryList != null && selectedHoraryList.size() > 0) {
            for (MetaData m : selectedHoraryList) {
                getAssociatedHoraryList().add(m);
                getHoraryList().remove(m);
            }
            selectedHoraryList = null;
        }
        return null;
    }

    public String removeHoraryFromList() {

        /*
         * Si esta seleccionado eliminar todos los horarios asociados entonces
         * todos los horarios agregamos a la lista de horarios
         */
        if (validatedAllHoraryAssociated) {
            for (MetaData m : associatedHoraryList) {
                getHoraryList().add(m);
            }
            associatedHoraryList = null;
            selectedAssociatedHoraryList = null;
            validatedAllHoraryAssociated = false;
            return null;
        }

        /*
         * Si solo se desea eliminar un horario de la lista de horarios
         * asociados buscamos de la lista de horarios y lo quitamos y agregamos
         * a la lista de horarios
         */
        if (selectedAssociatedHoraryList != null
            && selectedAssociatedHoraryList.size() > 0) {

            for (MetaData m : selectedAssociatedHoraryList) {
                getAssociatedHoraryList().remove(m);
                getHoraryList().add(m);
            }
            selectedAssociatedHoraryList = null;
        }

        return null;
    }

    @Override
    public String savePlusMember() {
        try {

            if (data.getCode() == null || data.getCode().equals("")) {
                data.setCode(null);
                setErrorMessage(i18n.iValue("web.client.backingBean.metadata.CodeNull"));
                return null;
            }

            return super.savePlusMember();
        } finally {

            horaryList = null;
            selectedHoraryList = null;
            associatedHoraryList = null;
            selectedAssociatedHoraryList = null;
            validatedAllHorary = false;
            validatedAllHoraryAssociated = false;

        }
    }

    @Override
    protected String associateMetaDatas(MetaDataPK pk) throws Exception {

        MetaData persistedMeta = metaDataFacade.find(pk, "metadatas");
        /*
         * como en ambas entidades son listas, por la relacion many-to-many
         * creamos las listas, y agregamos los metadatas que se encuentran en
         * associatedHoraryList en el meta de empleado que se edita, y
         * eliminamos de este meta los que se encuentran en horaryList
         */
        List<MetaData> listHoraries = new ArrayList<MetaData>();

        /*
         * Si la lista de horarios asociados es diferente a nulo lo recorremos y
         * por cada horario asociado recuperamos la lista de empleados a la cual
         * ésta se encuentra asociada y si el empleado asociado ya no se
         * encuentra lo agregamos y editamos este meta
         */
        if (associatedHoraryList != null && associatedHoraryList.size() > 0) {
            for (MetaData m : associatedHoraryList) {

                /*
                 * Recorremos por los dias de la semana y recuperamos la lista
                 * de horarios que tiene cada dia de la semana y si el codigo de
                 * meta son iguales, siginifica que ese horario esta
                 * seleccionado como para asociar
                 */
                for (int i = 0; i < 7; i++) {
                    if (getHashMapDias().get(i)) {
                        for (HoraryPojo hp : getHashMapHorarios().get(i)) {
                            if (hp.getCode().equals(
                                    m.getMetaDataPK().getCodeChr())) {
                                hp.setChecked(true);
                                break;
                            }
                        }
                    }
                }

                /*
                 * Como los dias, horas de entrada, y horas trabajadas son meta
                 * members diferentes recuperamos estos
                 */
                MetaData dia = facade.findByClientMetaMemberAndCode(
                        getUserweb().getClient().getClientCod(),
                        HORARY_COD_META, 7l, m.getMetaDataPK().getCodeChr());

                MetaData horaEntrada = facade.findByClientMetaMemberAndCode(
                        getUserweb().getClient().getClientCod(),
                        HORARY_COD_META, 8l, m.getMetaDataPK().getCodeChr());

                MetaData horaTrabajada = facade.findByClientMetaMemberAndCode(
                        getUserweb().getClient().getClientCod(),
                        HORARY_COD_META, 9l, m.getMetaDataPK().getCodeChr());

                List<String> diasList = Arrays.asList(dia.getValueChr().split(
                        ","));
                List<String> horaEntradaList = Arrays.asList(horaEntrada.getValueChr().split(
                        ","));
                List<String> horaTrabajadaList = Arrays.asList(horaTrabajada.getValueChr().split(
                        ","));

                for (int i = 0; i < 7; i++) {
                    String aux = "";
                    try {
                        /* Recuperamos el dia de la semana */
                        Integer day = Integer.valueOf(diasList.get(i));
                        /*
                         * Recuperamos la hora de entrada y lo parseamos en
                         * formato HH:mm
                         */
                        Date entrada = dfformat.parse(horaEntradaList.get(i));
                        /*
                         * Recuperamos las horas trabajadas y lo parseamos en
                         * formato HH:mm
                         */
                        Date horaTrabajo = dfformat.parse(horaTrabajadaList.get(i));
                        /*
                         * Para saber la hora de salida le sumamos a la hora de
                         * entrada las horas trabajadas
                         */
                        Calendar horaEntradaCalendar = Calendar.getInstance();
                        horaEntradaCalendar.setTime(entrada);
                        Calendar horaSalidaCalendar = Calendar.getInstance();
                        horaSalidaCalendar.setTime(entrada);
                        horaSalidaCalendar.add(Calendar.HOUR_OF_DAY,
                                horaTrabajo.getHours());
                        horaSalidaCalendar.add(Calendar.MINUTE,
                                horaTrabajo.getMinutes());
                        aux = verificarHorario(day,
                                horaEntradaCalendar.getTime(),
                                horaSalidaCalendar.getTime(), m.getValueChr(),
                                m.getMetaDataPK().getCodeChr());

                        /*
                         * Si el horario se solapa con otro el metodo
                         * horarioSolapado va a devolver un string distinto a
                         * nulo
                         */
                        if (aux != null) {
                            throw new Exception(aux);

                        }
                    } catch (Exception e) {
                        notifier.signal(getClass(), Action.ERROR,
                                e.getMessage(), e);
                        throw new AssociateHoraryException(aux);
                    }

                }

                /*
                 * Recuperamos la lista de empleados asociados al horario, y si
                 * ya no se encuentra lo agregamos y editamos este horario
                 */
                List<MetaData> metadataList = facade.find(m.getMetaDataPK(),
                        "metadatas", "metaData").getMetaData();
                if (metadataList == null) {
                    metadataList = new ArrayList<MetaData>();
                }
                if (!metadataList.contains(persistedMeta)) {
                    metadataList.add(persistedMeta);
                    m.setMetaData(metadataList);
                    facade.edit(m);
                }
            }
            listHoraries = associatedHoraryList;

        }
        /*
         * Agregamos al empleado que se esta editando la lista de horarios que
         * se asociaron
         */
        persistedMeta.setMetadatas(listHoraries);

        /*
         * Recorremos la lista de horarios que no estan asociados al empleado
         * que esta siendo editado y por cada horario recuperamos la lista de
         * empleados a la cual ésta se encuentra asociada y si este empleado se
         * encuentra en la lista lo eliminamos y editamos el metadata de horario
         */
        for (MetaData m : horaryList) {
            List<MetaData> metadataList = facade.find(m.getMetaDataPK(),
                    "metaData").getMetaData();
            if (metadataList != null && metadataList.size() > 0
                && metadataList.contains(persistedMeta)) {
                metadataList.remove(persistedMeta);
                m.setMetaData(metadataList);
                facade.edit(m);
            }
        }

        return null;
    }

    @Override
    public String showReasignar() {

        super.showReasignar();

        listEmployeeSelected = new ArrayList<MetaData>();
        for (Data dataBean : dataModelSpecific) {
            if (getSelectedItems().get(dataBean.getDataPK())) {
                MetaDataPK pk = new MetaDataPK();
                // PK del METADATA
                pk.setCodClient(dataBean.getDataPK().getCodClient());
                pk.setCodMeta(dataBean.getDataPK().getCodMeta());
                pk.setCodeChr(dataBean.getDataPK().getCodigo());
                pk.setCodMember(1L);
                MetaData md = metaDataFacade.find(pk);
                if (md != null) {
                    listEmployeeSelected.add(md);
                }
            }

        }

        if (listEmployeeSelected.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOne"));
            return null;
        }

        cargarHashMapHorarios();
        setReasignar(true);
        return null;
    }

    @Override
    public String associateHorary() {
        // TODO Auto-generated method stub
        super.associateHorary();
        filterSelectedField = "-1";
        filterCriteria = "";
        paginationHelper = null; // For pagination recreation
        dataModelSpecific = null; // For data model recreation
        getSelectedItems().clear();// clearing selection
        setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.AssociateSuccess"));
        return null;
    }

}
