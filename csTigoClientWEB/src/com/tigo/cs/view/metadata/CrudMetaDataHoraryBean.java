package com.tigo.cs.view.metadata;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.persistence.Column;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.PrimarySortedField;
import com.tigo.cs.commons.jpa.PrimarySortedFieldNotFoundException;
import com.tigo.cs.commons.jpa.Searchable;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.util.SortHelper;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.view.Data;
import com.tigo.cs.domain.view.DataEmployee;
import com.tigo.cs.domain.view.DataHorary;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.MetaDataFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.view.service.ConfigurationData;

@ManagedBean(name = "crudMetaDataHoraryBean")
@ViewScoped
public class CrudMetaDataHoraryBean extends AbstractCrudMetaDataBean<DataHorary> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6670040114924234996L;
    private static final Long COD_META = 29L;
    private static final Long COD_META_SUPERVISOR = 30L;
    private static final Long COD_META_EMPLOYEE = 7L;
    private static final Long HORARY_COD_META = 29L;
    private static final Long COD_MEMBER = 1L;
    private final DateFormat dfformat = new SimpleDateFormat("HH:mm");
    private final DateFormat dfformat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final String dias = "{0},{1},{2},{3},{4},{5},{6}";
    private final String horasEntrada = "{0},{1},{2},{3},{4},{5},{6}";
    private final String horasTrabajada = "{0},{1},{2},{3},{4},{5},{6}";
    private final String tolerancias = "{0},{1},{2},{3},{4},{5},{6}";
    private String[] valuesDias;
    private String[] valuesAlldays;
    private String[] valuesHorasEntrada;
    private String[] valuesHorasTrabajadas;
    private String[] valuesTolerancia;

    private Integer daysOption;
    private ConfigurationData everyDayConf;
    private Integer stepFactor;
    private List<ConfigurationData> confList;
    @EJB
    private GlobalParameterFacade globalParameterFacade;
    @EJB
    private MetaDataFacade metadataFacade;
    private BigInteger newSupNumber;
    private String selectedSupNumber;
    private List<String> supNumberList;
    private List<MetaData> associateSupMetadataList;
    private List<MetaData> removeSupMetadataList;
    private MetaData metaDataHorary;

    public CrudMetaDataHoraryBean() {
        super(COD_META, COD_MEMBER);
        setPdfReportName("rep_datahorary");
        setXlsReportName("rep_datahorary_xls");
    }

    @Override
    public String getMetaLabel() {
        if (metaLabel == null) {
            super.setMetaLabel(i18n.iValue("web.client.backingBean.activityCrudMetaData.Horary"));
        }
        return metaLabel;
    }

    @Override
    public String savePlusMember() {
        init();
        if (data.getCode() == null || data.getCode().equals("")) {
            data.setCode(null);
            setWarnMessage(i18n.iValue("web.client.backingBean.metadata.CodeNull"));
            return null;
        }

        if (data.getDescripcion() == null || data.getDescripcion().equals("")) {
            setWarnMessage(i18n.iValue("web.client.backingBean.metadata.horary.DescripcionNull"));
            return null;
        }

        if (data.getDateFrom() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.metadata.horary.DateFromNull"));
            return null;
        }

        // if (data.getDateTo() == null) {
        // setWarnMessage(i18n.iValue("web.client.backingBean.metadata.horary.DateToNull"));
        // return null;
        // }

        if (data.getDateTo() != null
            && data.getDateTo().before(data.getDateFrom())) {
            setWarnMessage(i18n.iValue("web.client.backingBean.metadata.horary.DateToBeforeDateFrom"));
            return null;
        }

        /* Recuperamos el valor de vigencia desde */
        data.setVigenciaDesde(dfformat2.format(data.getDateFrom()));
        /* Recuperamos el valor de vigencia hasta */
        data.setVigenciaHasta(data.getDateTo() != null ? dfformat2.format(data.getDateTo()) : null);
        /* Recuperamos el valor si selecciono o no asjutar la tolerancia */
        data.setAjustarTolerancia(data.getSetTolerance() ? "1" : "0");
        /* Recuperamos el valor de habilitado */
        data.setHabilitado(data.getEnabled() ? "1" : "0");
        /*
         * Recuperamos el valor de tolerancia general el cual unicamente se
         * tendra en cuenta si se selecciono ajustar tolerancia
         */
        data.setToleranciaGeneral(data.getTolerance().toString());

        /* Si no selecciono ninguna opcion en el campo Dias de la semana */
        if (daysOption.equals(-1)) {
            setWarnMessage(i18n.iValue("web.client.backingBean.metadata.horary.DayNull"));
            return null;

            /*
             * Si selecciono todos los dias se recorre hasta 7 y se guarda en el
             * array los valores seteados para hora de inicio, hora de salida y
             * tolerancia
             */
        } else if (daysOption.equals(1)) {
            for (int i = 0; i < 7; i++) {

//                if (everyDayConf.getDuration().before(
//                        everyDayConf.getStartTime())) {
//                    setWarnMessage(i18n.iValue("web.client.backingBean.metadata.horary.EndTimeBeforeStartTime"));
//                    return null;
//                }

                valuesHorasEntrada[i] = dfformat.format(everyDayConf.getStartTime());
                valuesHorasTrabajadas[i] = dfformat.format(everyDayConf.getDuration());
                valuesTolerancia[i] = everyDayConf.getTolerance().toString();
            }
            data.setDias(MessageFormat.format(dias, valuesAlldays));
            data.setHorasEntrada(MessageFormat.format(horasEntrada,
                    valuesHorasEntrada));
            data.setHorasTrabajada(MessageFormat.format(horasTrabajada,
                    valuesHorasTrabajadas));
            data.setTolerancias(MessageFormat.format(tolerancias,
                    valuesTolerancia));
            data.setTodosLosDias("1");
        } else if (daysOption.equals(2)) {
            data.setTodosLosDias("0");
            for (int i = 0; i < 7; i++) {
                /*
                 * Si el dia de la semana esta chequeado se setea los valores de
                 * hora de inicio, hora salida y tolerancia para ese dia de la
                 * semana
                 */
                if (getConfList().get(i).getChecked()) {

//                    if (getConfList().get(i).getDuration().before(
//                            getConfList().get(i).getStartTime())) {
//                        setWarnMessage(MessageFormat.format(
//                                i18n.iValue("web.client.backingBean.metadata.horary.EndTimeBeforeStartTime2"),
//                                getConfList().get(i).getDayOfWeekString()));
//                        return null;
//                    }

                    valuesHorasEntrada[i] = dfformat.format(getConfList().get(i).getStartTime());
                    valuesHorasTrabajadas[i] = dfformat.format(getConfList().get(
                            i).getDuration());
                    valuesTolerancia[i] = getConfList().get(i).getTolerance().toString();
                    valuesDias[i] = "1";
                }

            }

            data.setDias(MessageFormat.format(dias, valuesDias));
            data.setHorasEntrada(MessageFormat.format(horasEntrada,
                    valuesHorasEntrada));
            data.setHorasTrabajada(MessageFormat.format(horasTrabajada,
                    valuesHorasTrabajadas));
            data.setTolerancias(MessageFormat.format(tolerancias,
                    valuesTolerancia));
        }

        super.savePlusMember();
        selectedSupNumber = null;
        newSupNumber = null;
        supNumberList = null;
        associateSupMetadataList = null;
        removeSupMetadataList = null;
        metaDataHorary = null;
        confList = null;
        everyDayConf = null;
        init();

        return null;

    }

    @Override
    public String editEntityPlusMember() {
        super.editEntityPlusMember();
        selectedSupNumber = null;
        newSupNumber = null;
        supNumberList = null;
        associateSupMetadataList = null;
        removeSupMetadataList = null;
        metaDataHorary = null;
        init();
        try {

            /*
             * Recuperamos si la configuracion de horario es de todos los dias o
             * es de dias especificos
             */
            if (data.getTodosLosDias() != null
                && !data.getTodosLosDias().equals("")) {

                /*
                 * Si es uno es todos los dias, de lo contrario es de dias
                 * especificos
                 */
                if (data.getTodosLosDias().equals("1")) {
                    daysOption = 1;
                } else {
                    daysOption = 2;
                }
            }

            if (data.getVigenciaDesde() != null
                && !data.getVigenciaDesde().equals("")) {
                data.setDateFrom(dfformat2.parse(data.getVigenciaDesde()));
            }

            if (data.getVigenciaHasta() != null
                && !data.getVigenciaHasta().equals("")) {
                data.setDateTo(dfformat2.parse(data.getVigenciaHasta()));
            }

            if (data.getToleranciaGeneral() != null
                && !data.getToleranciaGeneral().equals("")) {
                data.setTolerance(Long.valueOf(data.getToleranciaGeneral()));
            }

            if (data.getHabilitado() != null
                && !data.getHabilitado().equals("")) {
                if (data.getHabilitado().equals("1")) {
                    data.setEnabled(true);
                }
            }

            if (data.getAjustarTolerancia() != null
                && !data.getAjustarTolerancia().equals("")) {
                if (data.getAjustarTolerancia().equals("1")) {
                    data.setSetTolerance(true);
                }
            }

            /*
             * Recuperamos los dias que se guardan asi 0,0,0,1,0,0,0, Los dias
             * de la semana estan separados por coma empezando por domingo, 1
             * significa que ese dia esta seleccionado y 0 que no
             */
            if (data.getDias() != null && !data.getDias().equals("")) {

                /*
                 * Si el campo daysOption es igual a 2 significa que los
                 * horarios con especificos
                 */
                if (daysOption.equals(2)) {
                    createConfigurationData(
                            Arrays.asList(data.getDias().split(",")),
                            Arrays.asList(data.getHorasEntrada().split(",")),
                            Arrays.asList(data.getHorasTrabajada().split(",")),
                            Arrays.asList(data.getTolerancias().split(",")));

                } else if (daysOption.equals(1)) {
                    List<String> listHoraEntrada = Arrays.asList(data.getHorasEntrada().split(
                            ","));
                    List<String> listHoraSalida = Arrays.asList(data.getHorasTrabajada().split(
                            ","));
                    List<String> listTolerancias = Arrays.asList(data.getTolerancias().split(
                            ","));
                    getEveryDayConf().setStartTime(
                            dfformat.parse(listHoraEntrada.get(0)));
                    getEveryDayConf().setDuration(
                            dfformat.parse(listHoraSalida.get(0)));
                    getEveryDayConf().setTolerance(
                            Long.valueOf(listTolerancias.get(0)));
                }

            }

            /* Recuperamos todos los supervisores asociados a al horario */
            associateSupMetadataList = metaDataFacade.findWithMetadatas(
                    getMetaDataHorary().getMetaDataPK(), COD_META_SUPERVISOR);

            /*
             * Recorremos la lista de contactos asociados a la policia y lo
             * agregamos a nuestra lista y lo eliminamos de la lista de
             * contactos
             */
            if (associateSupMetadataList != null
                && associateSupMetadataList.size() > 0) {
                for (MetaData m : associateSupMetadataList) {
                    getSupNumberList().add(m.getValueChr());
                }
            }

        } catch (Exception e) {
            notifier.signal(getClass(), Action.ERROR, e.getMessage(), e);
        }
        return null;
    }

    public List<ConfigurationData> getConfList() {
        if (confList == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            confList = new ArrayList<ConfigurationData>();
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Sunday"), 1, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Monday"), 2, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Tuesday"), 3, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Wednesday"), 4, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Thursday"), 5, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Friday"), 6, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
            confList.add(new ConfigurationData(i18n.iValue("web.client.guardroundconf.screen.field.Saturday"), 7, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false));
        }
        return confList;
    }

    public Integer getStepFactor() {
        if (stepFactor == null) {
            try {
                String factor = globalParameterFacade.findByCode("horary.interval");
                if (factor != null)
                    stepFactor = Integer.parseInt(factor);
                else
                    stepFactor = 10;
            } catch (GenericFacadeException e) {
                stepFactor = 10;
            }
        }
        return stepFactor;
    }

    public void createConfigurationData(List<String> days, List<String> horaEntrada, List<String> horaSalida, List<String> tolerancia) {
        /* Recorremos la lista de dias */
        for (int i = 0; i < days.size(); i++) {
            /* Si es 1 significa que ese dia esta seleccionado */
            if (days.get(i).equals("1")) {
                getConfList().get(i).setChecked(true);
                try {
                    getConfList().get(i).setStartTime(
                            dfformat.parse(horaEntrada.get(i)));
                    getConfList().get(i).setDuration(
                            dfformat.parse(horaSalida.get(i)));
                } catch (Exception e) {
                    notifier.signal(getClass(), Action.ERROR, e.getMessage(), e);
                }
                getConfList().get(i).setTolerance(
                        Long.valueOf(tolerancia.get(i)));
            }
        }

    }

    public Integer getDaysOption() {
        return daysOption;
    }

    public void setDaysOption(Integer daysOption) {
        this.daysOption = daysOption;
    }

    public ConfigurationData getEveryDayConf() {
        if (everyDayConf == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            everyDayConf = new ConfigurationData(null, 0, cal.getTime(), cal.getTime(), new Long(getStepFactor()), 0L, false, false);
        }
        return everyDayConf;
    }

    public void setEveryDayConf(ConfigurationData everyDayConf) {
        this.everyDayConf = everyDayConf;
    }

    public void setStepFactor(Integer stepFactor) {
        this.stepFactor = stepFactor;
    }

    public void setConfList(List<ConfigurationData> confList) {
        this.confList = confList;
    }

    public void init() {

        for (int i = 0; i < 7; i++) {
            getValuesDias()[i] = "0";
            getValuesAlldays()[i] = "1";
            getValuesHorasEntrada()[i] = "00:00";
            getValuesHorasTrabajadas()[i] = "00:00";
            getValuesTolerancia()[i] = "0";

        }

    }

    @Override
    public String cancelEditing() {
        super.cancelEditing();
        selectedSupNumber = null;
        newSupNumber = null;
        supNumberList = null;
        associateSupMetadataList = null;
        removeSupMetadataList = null;
        confList = null;
        everyDayConf = null;
        init();
        return null;
    }

    @Override
    public String addToList() {
        MetaData md = null;
        if (newSupNumber != null) {
            md = new MetaData();
            md.setMetaDataPK(new MetaDataPK(getUserweb().getClient().getClientCod(), COD_META_SUPERVISOR, COD_MEMBER, metadataFacade.getMetadaDataSupervisorSeq().toString()));
            md.setValueChr(newSupNumber.toString());
            getSupNumberList().add(newSupNumber.toString());
            getAssociateSupMetadataList().add(md);
            newSupNumber = null;
        }
        return null;
    }

    @Override
    public String removeFromList() {
        if (selectedSupNumber != null) {
            getSupNumberList().remove(selectedSupNumber);
            for (MetaData m : getAssociateSupMetadataList()) {
                if (m.getValueChr().equals(selectedSupNumber)) {
                    getAssociateSupMetadataList().remove(m);
                    getRemoveSupMetadataList().add(m);
                    break;
                }
            }
            newSupNumber = null;
        }
        return null;
    }

    public List<String> getSupNumberList() {
        if (supNumberList == null) {
            supNumberList = new ArrayList<String>();
        }
        return supNumberList;
    }

    public void setSupNumberList(List<String> supNumberList) {
        this.supNumberList = supNumberList;
    }

    public String getSelectedSupNumber() {
        return selectedSupNumber;
    }

    public void setSelectedSupNumber(String selectedSupNumber) {
        this.selectedSupNumber = selectedSupNumber;
    }

    public BigInteger getNewSupNumber() {
        return newSupNumber;
    }

    public void setNewSupNumber(BigInteger newSupNumber) {
        this.newSupNumber = newSupNumber;
    }

    public List<MetaData> getAssociateSupMetadataList() {
        if (associateSupMetadataList == null) {
            associateSupMetadataList = new ArrayList<MetaData>();
        }
        return associateSupMetadataList;
    }

    public void setAssociateSupMetadataList(List<MetaData> associateSupMetadataList) {
        this.associateSupMetadataList = associateSupMetadataList;
    }

    public List<MetaData> getRemoveSupMetadataList() {
        if (removeSupMetadataList == null) {
            removeSupMetadataList = new ArrayList<MetaData>();
        }
        return removeSupMetadataList;
    }

    public void setRemoveSupMetadataList(List<MetaData> removeSupMetadataList) {
        this.removeSupMetadataList = removeSupMetadataList;
    }

    @Override
    protected String associateMetaDatas(MetaDataPK pk) {

        MetaData persistedMeta = metaDataFacade.find(pk, "metadatas");
        /*
         * como en ambas entidades son listas, por la relacion many-to-many
         * creamos las listas, y agregamos los metadatas que se encuentran en
         * supMetadataList en el meta de horario , y eliminamos de este meta los
         * que se encuentran en removeSupMetadataList
         */
        List<MetaData> listSupervisor = new ArrayList<MetaData>();

        /*
         * Si la lista de supervisores asociados es diferente a nulo lo
         * recorremos y por cada supervisor asociado recuperamos la lista de
         * horarios a la cual ésta se encuentra asociada y si el horario ya ya
         * no se encuentra asociado lo agregamos y editamos este meta
         */
        if (getAssociateSupMetadataList() != null
            && getAssociateSupMetadataList().size() > 0) {
            for (MetaData m : getAssociateSupMetadataList()) {
                List<MetaData> metadataList = null;
                try {
                    metadataList = facade.find(m.getMetaDataPK(), "metadatas",
                            "metaData").getMetaData();
                } catch (NullPointerException e) {
                    try {
                        createOrSaveMetaData(m);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    metadataList = facade.find(m.getMetaDataPK(), "metadatas",
                            "metaData").getMetaData();
                }
                if (metadataList == null) {
                    metadataList = new ArrayList<MetaData>();
                }
                if (!metadataList.contains(persistedMeta)) {
                    metadataList.add(persistedMeta);
                    m.setMetaData(metadataList);
                    facade.edit(m);
                }
            }
            listSupervisor = getAssociateSupMetadataList();

        }
        /*
         * Agregamos a al horario que se esta editando la lista de supervisores
         * que se asociaron
         */
        persistedMeta.setMetadatas(listSupervisor);

        /*
         * Recorremos la lista de supervisores que se eliminaron del horario que
         * esta siendo editada y por cada metadata recuperamos la lista de
         * metadatas a la cual ésta se encuentra asociada y si este metaData se
         * encuentra en la lista lo eliminamos y editamos el metadata de horario
         */
        for (MetaData m : getRemoveSupMetadataList()) {

            List<MetaData> metadataList = null;
            try {
                metadataList = facade.find(m.getMetaDataPK(), "metaData").getMetaData();
            } catch (NullPointerException e) {
                try {
                    createOrSaveMetaData(m);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                metadataList = facade.find(m.getMetaDataPK(), "metaData").getMetaData();
            }

            if (metadataList != null && metadataList.size() > 0
                && metadataList.contains(persistedMeta)) {
                metadataList.remove(persistedMeta);
                m.setMetaData(metadataList);
                facade.edit(m);
            }
        }

        return null;
    }

    public String[] getValuesDias() {
        if (valuesDias == null) {
            valuesDias = new String[7];
        }
        return valuesDias;
    }

    public void setValuesDias(String[] valuesDias) {
        this.valuesDias = valuesDias;
    }

    public String[] getValuesAlldays() {
        if (valuesAlldays == null) {
            valuesAlldays = new String[7];
        }
        return valuesAlldays;
    }

    public void setValuesAlldays(String[] valuesAlldays) {
        this.valuesAlldays = valuesAlldays;
    }

    public String[] getValuesHorasEntrada() {
        if (valuesHorasEntrada == null) {
            valuesHorasEntrada = new String[7];
        }
        return valuesHorasEntrada;
    }

    public void setValuesHorasEntrada(String[] valuesHorasEntrada) {
        this.valuesHorasEntrada = valuesHorasEntrada;
    }

    public String[] getValuesHorasTrabajadas() {
        if (valuesHorasTrabajadas == null) {
            valuesHorasTrabajadas = new String[7];
        }
        return valuesHorasTrabajadas;
    }

    public void setValuesHorasTrabajadas(String[] valuesHorasTrabajadas) {
        this.valuesHorasTrabajadas = valuesHorasTrabajadas;
    }

    public String[] getValuesTolerancia() {
        if (valuesTolerancia == null) {
            valuesTolerancia = new String[7];
        }
        return valuesTolerancia;
    }

    public void setValuesTolerancia(String[] valuesTolerancia) {
        this.valuesTolerancia = valuesTolerancia;
    }

    /* Data model para empleados */

    private DataModel<DataEmployee> dataModelEmployee;
    private PaginationHelper paginationHelperEmployee;
    private SortHelper sortHelperEmployee;
    private String filterSelectedFieldEmployee;
    private String filterCriteriaEmployee;
    private String aditionalFilterEmployee;
    private List<SelectItem> filterFieldsEmployee;
    private String primarySortedFieldEmployee;
    private boolean primarySortedFieldAscEmployee = true;
    private String rowQuantSelectedEmployee;
    protected Class<DataEmployee> dataEmployeeClass;
    protected Map<Object, Boolean> selectedItemsEmployee;
    private boolean shownEmployee;
    private String metaLabel2;
    private String codigosEmpleado;

    private void initialize() {

        // Initialize filter criteria with no filter
        filterSelectedFieldEmployee = "-1";
        filterCriteriaEmployee = "";

        // Initialize sort with default values
        sortHelperEmployee = new SortHelper();
        try {
            sortHelperEmployee.setField(getPrimarySortedFieldEmployee());
            sortHelperEmployee.setAscending(primarySortedFieldAscEmployee);
        } catch (PrimarySortedFieldNotFoundException ex) {
            java.util.logging.Logger.getLogger(AbstractCrudBean.class.getName()).log(
                    Level.SEVERE,
                    i18n.iValue("web.client.backingBean.message.Error")
                        + ex.getMessage(), ex);
        }
    }

    /* Metodo invocado al presionar el boton Desvincular */
    public String desvincular() {

        /* Recuperamos la lista de empleados seleccionados para desvincular */
        List<MetaData> listData = new ArrayList<MetaData>();
        for (Data dataBean : dataModelEmployee) {
            if (getSelectedItemsEmployee().get(dataBean.getDataPK())) {
                MetaDataPK pk2 = new MetaDataPK();
                pk2.setCodClient(dataBean.getDataPK().getCodClient());
                pk2.setCodMeta(dataBean.getDataPK().getCodMeta());
                pk2.setCodeChr(dataBean.getDataPK().getCodigo());
                pk2.setCodMember(1L);
                MetaData md = metaDataFacade.find(pk2);
                if (md != null) {
                    listData.add(md);
                }

            }
        }

        if (listData.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOne"));
            return null;
        }

        /* Recuperamos la lista de empleados asociados al horario */

        MetaData metaDataHorario = getMetaDataHorary();
        List<MetaData> associateEmployeeList = metaDataFacade.findWithMetadatasViceversa(
                metaDataHorario.getMetaDataPK(), COD_META_EMPLOYEE);

        /*
         * Recorremos la lista de empleados seleccionados y por cada uno
         * recuperamos sus horarios asociados y desvinculamos el horario que se
         * desea
         */
        for (MetaData dt : listData) {
            /* Aca como recupero los horarios del empleado */
            List<MetaData> associateHoraryList = facade.find(
                    dt.getMetaDataPK(), "metadatas", "metaData").getMetadatas();

            associateHoraryList.remove(metaDataHorario);
            associateEmployeeList.remove(dt);

            dt.setMetadatas(associateHoraryList);
            metaDataFacade.edit(dt);

        }

        metaDataHorario.setMetaData(associateEmployeeList);
        metaDataFacade.edit(metaDataHorario);

        String filter = "o.dataPK.codClient = %s AND o.dataPK.codMeta = %s AND o.dataPK.codigo in (%s)";
        String aux = "";
        if (associateEmployeeList != null && associateEmployeeList.size() > 0) {
            for (MetaData m : associateEmployeeList) {
                aux += m.getMetaDataPK().getCodeChr() + ",";
            }
            aux = aux.substring(0, aux.length() - 1);
        }

        setAditionalFilterEmployee(String.format(filter,
                getUserweb().getClient().getClientCod(),
                String.valueOf(COD_META_EMPLOYEE), aux.equals("") ? "''" : aux));

        filterSelectedFieldEmployee = "-1";
        filterCriteriaEmployee = "";
        paginationHelperEmployee = null; // For pagination recreation
        dataModelEmployee = null; // For data model recreation
        getSelectedItemsEmployee().clear();// clearing selection
        setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DesvinculacionSuccess"));
        return null;
    }

    /* Metodo invocado al presionar el boton Empleados */
    public String showEmployee() {
        data = null;
        metaDataHorary = null;
        for (Data dataBean : dataModelSpecific) {
            if (getSelectedItems().get(dataBean.getDataPK())) {
                if (data == null) {
                    data = dataFacade.findByClass(getDataClass(),
                            dataBean.getDataPK());
                } else {
                    data = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOne"));
                    return null;
                }
            }
        }

        if (data == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOne"));
            return null;
        }
        /* Inicializamos los valores referentes al DataModel de empleados */
        initialize();
        String filter = " o.dataPK.codClient = %s AND o.dataPK.codMeta = %s AND o.dataPK.codigo in (%s) ";

        MetaData metaDataHorary = getMetaDataHorary();
        List<MetaData> associateEmployeeList = metaDataFacade.findWithMetadatasViceversa(
                metaDataHorary.getMetaDataPK(), COD_META_EMPLOYEE);
        String aux = "";
        codigosEmpleado = "";
        if (associateEmployeeList != null && associateEmployeeList.size() > 0) {
            for (MetaData m : associateEmployeeList) {
                aux += m.getMetaDataPK().getCodeChr() + ",";
                codigosEmpleado += "'" + m.getMetaDataPK().getCodeChr() + "',";
            }
            codigosEmpleado = codigosEmpleado.substring(0,
                    codigosEmpleado.length() - 1);
            aux = aux.substring(0, aux.length() - 1);
        }

        setAditionalFilterEmployee(String.format(filter,
                getUserweb().getClient().getClientCod(),
                String.valueOf(COD_META_EMPLOYEE), aux.equals("") ? "''" : aux));
        shownEmployee = true;
        return null;
    }

    /* Metodo invocado al presionar el boton reasignar */
    @Override
    public String showReasignar() {

        super.showReasignar();

        listEmployeeSelected = new ArrayList<MetaData>();
        for (Data dataBean : dataModelEmployee) {
            if (getSelectedItemsEmployee().get(dataBean.getDataPK())) {
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

    public MetaData getMetaDataHorary() {
        if (metaDataHorary == null) {
            MetaDataPK pk = new MetaDataPK();
            // PK del METADATA
            pk.setCodClient(data.getDataPK().getCodClient());
            pk.setCodMeta(data.getDataPK().getCodMeta());
            pk.setCodeChr(data.getDataPK().getCodigo());
            pk.setCodMember(1L);
            return metaDataFacade.find(pk);
        }

        return metaDataHorary;
    }

    /* Metodo invocado al presionar el boton Asociar */
    @Override
    public String associateHorary() {
        super.associateHorary();
        filterSelectedFieldEmployee = "-1";
        filterCriteriaEmployee = "";
        paginationHelperEmployee = null; // For pagination recreation
        dataModelEmployee = null; // For data model recreation
        getSelectedItemsEmployee().clear();// clearing selection
        setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.AssociateSuccess"));

        return null;
    }

    protected String getPrimarySortedFieldEmployee() throws PrimarySortedFieldNotFoundException {
        if (primarySortedFieldEmployee == null) {
            Field[] fieds = DataEmployee.class.getDeclaredFields();
            for (Field field : fieds) {
                PrimarySortedField annotation = field.getAnnotation(PrimarySortedField.class);
                if (annotation != null) {
                    primarySortedFieldEmployee = field.getName();
                    primarySortedFieldAscEmployee = annotation.ascending();
                    break;
                }
            }
            if (primarySortedFieldEmployee == null) {
                throw new PrimarySortedFieldNotFoundException(MetaData.class);
            }
        }
        return primarySortedFieldEmployee;
    }

    public Map<Object, Boolean> getSelectedItemsEmployee() {
        if (selectedItemsEmployee == null) {
            selectedItemsEmployee = new HashMap<Object, Boolean>();
        }

        return selectedItemsEmployee;
    }

    public void setSelectedItemsEmployee(Map<Object, Boolean> selectedItemsEmployee) {
        this.selectedItemsEmployee = selectedItemsEmployee;
    }

    public List<SelectItem> getFilterFieldsEmployee() {
        if (filterFieldsEmployee == null) {
            filterFieldsEmployee = new ArrayList<SelectItem>();

            Field[] fields = DataEmployee.class.getSuperclass().getDeclaredFields();
            for (Field field : fields) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null) {
                    String colLabel = this.getMetaLabel() != null
                        && annotation.label().equals("Valor") ? this.getMetaLabel() : i18n.iValue(annotation.label());
                    if (annotation.position() < 0
                        || annotation.position() > filterFieldsEmployee.size()) {
                        filterFieldsEmployee.add(new SelectItem(field.getName().concat(
                                !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    } else {
                        filterFieldsEmployee.add(
                                annotation.position(),
                                new SelectItem(field.getName().concat(
                                        !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    }
                }
            }

            Field[] fieds = DataEmployee.class.getDeclaredFields();
            for (Field field : fieds) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null) {
                    String colLabel = this.getMetaLabel() != null
                        && annotation.label().equals("Valor") ? this.getMetaLabel() : i18n.iValue(annotation.label());
                    if (annotation.position() < 0
                        || annotation.position() > filterFieldsEmployee.size()) {
                        filterFieldsEmployee.add(new SelectItem(field.getName().concat(
                                !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    } else {
                        filterFieldsEmployee.add(
                                annotation.position(),
                                new SelectItem(field.getName().concat(
                                        !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    }
                }
            }
        }

        return filterFieldsEmployee;
    }

    public <M> List<SelectItem> getFilterFieldsPlusMembersEmployee() {
        if (filterFieldsEmployee == null) {
            filterFieldsEmployee = new ArrayList<SelectItem>();
            Field[] fieds = DataEmployee.class.getDeclaredFields();
            for (Field field : fieds) {
                Searchable annotation = field.getAnnotation(Searchable.class);
                if (annotation != null) {
                    String colLabel = this.getMetaLabel() != null
                        && annotation.label().equals("Valor") ? this.getMetaLabel() : i18n.iValue(annotation.label());
                    if (annotation.position() < 0
                        || annotation.position() > filterFieldsEmployee.size()) {
                        filterFieldsEmployee.add(new SelectItem(field.getName().concat(
                                !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    } else {
                        filterFieldsEmployee.add(
                                annotation.position(),
                                new SelectItem(field.getName().concat(
                                        !annotation.internalField().isEmpty() ? ".".concat(annotation.internalField()) : ""), colLabel));
                    }
                }
            }
        }

        return filterFieldsEmployee;
    }

    public SortHelper getSortHelperEmployee() {
        return sortHelperEmployee;
    }

    public void setSortHelperEmployee(SortHelper sortHelperEmployee) {
        this.sortHelperEmployee = sortHelperEmployee;
    }

    public String getFilterCriteriaEmployee() {
        return filterCriteriaEmployee;
    }

    public void setFilterCriteriaEmployee(String filterCriteriaEmployee) {
        this.filterCriteriaEmployee = filterCriteriaEmployee;
    }

    public String getFilterSelectedFieldEmployee() {
        return filterSelectedFieldEmployee;
    }

    public void setFilterSelectedFieldEmployee(String filterSelectedFieldEmployee) {
        this.filterSelectedFieldEmployee = filterSelectedFieldEmployee;
    }

    public String getAditionalFilterEmployee() {
        return aditionalFilterEmployee;
    }

    public void setAditionalFilterEmployee(String aditionalFilterEmployee) {
        this.aditionalFilterEmployee = aditionalFilterEmployee;
    }

    public DataModel<DataEmployee> getDataModelEmployee() {
        if (dataModelEmployee == null) {
            dataModelEmployee = getPaginationHelperEmployee().createPageDataModel();
        }
        return dataModelEmployee;
    }

    public void setDataModelEmployee(DataModel<DataEmployee> dataModelEmployee) {
        this.dataModelEmployee = dataModelEmployee;
    }

    public PaginationHelper getPaginationHelperEmployee() {
        if (paginationHelperEmployee == null) {
            int pageSize = getRowQuantSelectedEmployee().length() > 0 ? Integer.valueOf(
                    getRowQuantSelectedEmployee()).intValue() : 0;

            paginationHelperEmployee = new PaginationHelper(pageSize) {

                @Override
                public int getItemsCount() {
                    String where = null;
                    if (!filterSelectedFieldEmployee.equals("-1")
                        && filterCriteriaEmployee.length() > 0) {
                        Class<?> fieldClass = getFieldTypeEmployee(filterSelectedFieldEmployee);

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = "o.".concat(filterSelectedFieldEmployee).concat(
                                    " = ").concat(filterCriteriaEmployee);
                        } else if (fieldClass.equals(String.class)) {
                            where = "lower(o.".concat(
                                    filterSelectedFieldEmployee).concat(
                                    ") LIKE '%").concat(
                                    filterCriteriaEmployee.toLowerCase()).concat(
                                    "%'");
                        }
                    }
                    if (aditionalFilterEmployee != null
                        && aditionalFilterEmployee.trim().length() > 0) {
                        if (where == null) {
                            where = "";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(
                                aditionalFilterEmployee.trim()).concat(") ");
                    }

                    return dataFacade.count(DataEmployee.class, where);
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = null;
                    if (!filterSelectedFieldEmployee.equals("-1")
                        && filterCriteriaEmployee.length() > 0) {
                        Class<?> fieldClass = getFieldTypeEmployee(filterSelectedFieldEmployee);

                        if (fieldClass.equals(Integer.class)
                            || fieldClass.equals(Long.class)
                            || fieldClass.equals(BigInteger.class)) {
                            where = "o.".concat(filterSelectedFieldEmployee).concat(
                                    " = ").concat(filterCriteriaEmployee);
                        } else if (fieldClass.equals(String.class)) {
                            where = "lower(o.".concat(
                                    filterSelectedFieldEmployee).concat(
                                    ") LIKE '%").concat(
                                    filterCriteriaEmployee.toLowerCase()).concat(
                                    "%'");
                        }
                    }
                    if (aditionalFilterEmployee != null
                        && aditionalFilterEmployee.trim().length() > 0) {
                        if (where == null) {
                            where = "";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(
                                aditionalFilterEmployee.trim()).concat(") ");
                    }
                    String beginOrderBy = sortHelperEmployee.getField().equals(
                            "codeChr") ? "o.dataPK." : "o.";
                    String orderby = beginOrderBy.concat(
                            sortHelperEmployee.getField()).concat(
                            sortHelperEmployee.isAscending() ? " ASC" : " DESC");
                    List<DataEmployee> list = dataFacade.findRange(
                            DataEmployee.class,
                            new int[] { getPageFirstItem(), getPageFirstItem()
                                + getPageSize() }, where, orderby);
                    return new ListDataModelViewCsTigo(list);
                }
            };
        }

        return paginationHelperEmployee;
    }

    protected Class<?> getFieldTypeEmployee(String fieldName) {
        try {
            String internalFieldName = "";
            if (fieldName.indexOf(".") >= 0) {
                internalFieldName = fieldName.substring(fieldName.indexOf(".") + 1);
                fieldName = fieldName.substring(0, fieldName.indexOf("."));
            }
            String methodName = "get".concat(
                    fieldName.substring(0, 1).toUpperCase()).concat(
                    fieldName.substring(1));

            if (internalFieldName.isEmpty()) {
                return DataEmployee.class.getMethod(methodName, new Class[0]).getReturnType();
            }

            Class<?> internalClass = DataEmployee.class.getMethod(methodName,
                    new Class[0]).getReturnType();
            methodName = "get".concat(
                    internalFieldName.substring(0, 1).toUpperCase()).concat(
                    internalFieldName.substring(1));

            return internalClass.getMethod(methodName, new Class[0]).getReturnType();
        } catch (Exception e) {
            return Object.class;
        }
    }

    public String nextPageEmployee() {
        dataModelEmployee = null; // resetDataModel();
        getPaginationHelperEmployee().nextPage();
        selectedItemsEmployee = null; // For clearing selection
        return null;
    }

    public String previousPageEmployee() {
        dataModelEmployee = null; // resetDataModel();
        getPaginationHelperEmployee().previousPage();
        selectedItemsEmployee = null; // For clearing selection
        return null;
    }

    public String applyFilterEmployee() {
        if (!filterSelectedFieldEmployee.equals("-1")) {
            Class<?> fieldClass = getFieldType(filterSelectedFieldEmployee);

            if ((fieldClass.equals(Integer.class) && !NumberUtil.isInteger(filterCriteriaEmployee))
                || (fieldClass.equals(Long.class) && !NumberUtil.isLong(filterCriteriaEmployee))
                || (fieldClass.equals(BigInteger.class) && !NumberUtil.isBigInteger(filterCriteriaEmployee))) {
                setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustBeInteger"));
                filterCriteriaEmployee = "";
            } else {
                signalRead();
            }
            paginationHelperEmployee = null; // For pagination recreation
            dataModelEmployee = null;
            selectedItemsEmployee = null; // For clearing selection
        } else {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectFindOption"));
        }
        return null;
    }

    public String cleanFilterEmployee() {
        dataModelEmployee = null;
        filterSelectedFieldEmployee = "-1";
        filterCriteriaEmployee = "";
        paginationHelperEmployee = null; // For pagination recreation
        selectedItemsEmployee = null; // For clearing selection
        return null;
    }

    public String applySortEmployee() {
        dataModelEmployee = null;// resetDataModel();//
        // clearSpecificDataModel();
        selectedItemsEmployee = null; // For clearing selection
        return null;
    }

    public void applyQuantityEmployee(ValueChangeEvent evnt) {
        paginationHelperEmployee = null; // For pagination recreation
        selectedItemsEmployee = null; // For clearing selection
        dataModelEmployee = null; // For data model recreation
    }

    public String getRowQuantSelectedEmployee() {
        if (rowQuantSelectedEmployee == null) {
            rowQuantSelectedEmployee = ApplicationBean.getInstance().getDefaultCrudPaginationPageSize();
        }
        return rowQuantSelectedEmployee;
    }

    public void setRowQuantSelectedEmployee(String rowQuantSelectedEmployee) {
        this.rowQuantSelectedEmployee = rowQuantSelectedEmployee;
    }

    public boolean isShownEmployee() {
        return shownEmployee;
    }

    public void setShownEmployee(boolean shownEmployee) {
        this.shownEmployee = shownEmployee;
    }

    @Override
    public boolean isReasignar() {
        return reasignar;
    }

    @Override
    public void setReasignar(boolean reasignar) {
        this.reasignar = reasignar;
    }

    public String cancelEditingEmployee() {
        shownEmployee = false;
        dataModelEmployee = null;
        filterSelectedFieldEmployee = "-1";
        filterCriteriaEmployee = "";
        paginationHelperEmployee = null; // For pagination recreation
        selectedItemsEmployee = null; // For clearing selection
        data = null;
        return null;
    }

    

    /* GENERATE REPORT OF EMPLOYEE BY HORARY */
    public String viewPDF2() {
        generateReport2("rep_dataemployee_horary", ReportType.PDF);
        signalReport(ReportType.PDF);
        return null;
    }

    public String viewXLS2() {
        generateReport2("rep_dataemployee_horary_xls", ReportType.XLS);
        signalReport(ReportType.XLS);
        return null;
    }

    public String getMetaLabel2() {
        if (metaLabel2 == null) {
            metaLabel2 = i18n.iValue("web.client.backingBean.crudmetadata.employee.horary");
        }
        return metaLabel2;
    }

    public String generateReport2(String reportName, ReportType reportType) {

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getWhereReport2());
        params.put("ORDER_BY", getOrderByReport2());
        String label = getMetaLabel2() != null ? getMetaLabel2() : "Valor";
        params.put("META_LABEL", label);
        params.put(
                "TITLE",
                MessageFormat.format(
                        i18n.iValue("web.client.backingBean.abstractCrudBean.message.ListOf"),
                        label));
        params.put("USER",
                SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());
        params.put("HORARY_DESCRIPTION", data.getDescripcion());

        ClientFile cf = fileFacade.getClientLogo(getUserweb().getClient());
        try {
            if (cf != null) {
                params.put("CLIENT_LOGO",
                        JRImageLoader.loadImage(cf.getFileByt()));
            }
        } catch (JRException ex) {
        }
        Connection conn = SMBaseBean.getDatabaseConnecction();
        JasperReportUtils.respondReport(reportName, params, true, conn,
                reportType);
        return null;
    }

    public String getWhereReport2() {
        Class<?> fieldClass = getFieldTypeEmployee(getFilterSelectedFieldEmployee());
        String where = String.format(" AND o.CODIGO IN (%s) ", codigosEmpleado);
        if (!fieldClass.equals(Object.class)) {
            String filterAttributeColumnName = getAttributeColumnName(getFilterSelectedFieldEmployee());
            filterAttributeColumnName = "o.".concat(filterAttributeColumnName);

            if (fieldClass.getSuperclass().equals(Number.class)) {
                where = " AND ".concat(filterAttributeColumnName).concat(" = ").concat(
                        getFilterCriteria());
            } else {
                where = " AND lower(".concat(filterAttributeColumnName).concat(
                        ") LIKE '%").concat(
                        getFilterCriteriaEmployee().toLowerCase()).concat("%'");
            }
        }
        if (getReportWhereCriteria2() != null) {
            where = where.concat(getReportWhereCriteria2());
        }
        return where;
    }

    public String getReportWhereCriteria2() {
        if (DataEmployee.class.getSimpleName().equals("MetaData")) {
            String filter = " AND o.COD_CLIENT = %s AND o.COD_META = %s AND o.COD_MEMBER = %s";
            return String.format(filter,
                    getUserweb().getClient().getClientCod(),
                    String.valueOf(cod_meta), String.valueOf(cod_member));
        } else {
            String filter = " AND o.COD_CLIENT = %s AND o.COD_META = %s ";
            return String.format(filter,
                    getUserweb().getClient().getClientCod(),
                    String.valueOf(COD_META_EMPLOYEE));
        }

    }

    protected String getAttributeColumnNameEmployee(String fieldName) {
        try {
            String internalFieldName = "";
            if (fieldName.indexOf(".") >= 0) {
                internalFieldName = fieldName.substring(fieldName.indexOf(".") + 1);
                fieldName = fieldName.substring(0, fieldName.indexOf("."));
            }
            Field field = null;
            try {
                field = DataEmployee.class.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                field = DataEmployee.class.getSuperclass().getDeclaredField(
                        fieldName);
            }

            if (field != null && !internalFieldName.isEmpty()) {
                field = field.getType().getDeclaredField(internalFieldName);
            }

            if (field != null) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null && annotation.name() != null
                    && !annotation.name().isEmpty()) {
                    return annotation.name();
                }
            }
        } catch (Exception e) {
            this.signal(
                    Action.ERROR,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.abstractCrudBean.message.AttributeValueObtainingError"),
                            getDataClass().getSimpleName()), e);
        }

        return null;
    }

    public String getOrderByReport2() {
        // Prepare orderby clause
        String sortAttributeColumnName = getAttributeColumnNameEmployee(getSortHelperEmployee().getField());
        if (getSortHelperEmployee().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "o.".concat(sortAttributeColumnName);
        } /*
           * la columna a ordenar es parte de la clave primaria de los metadatas
           */else if (getSortHelperEmployee().getField().toUpperCase().contains(
                "PK")) {
            sortAttributeColumnName = "o.".concat(sortAttributeColumnName);
        } else {
            sortAttributeColumnName = "o1.".concat(sortAttributeColumnName);
        }
        return "ORDER BY ".concat(sortAttributeColumnName).concat(
                getSortHelperEmployee().isAscending() ? " ASC" : " DESC");
    }

}