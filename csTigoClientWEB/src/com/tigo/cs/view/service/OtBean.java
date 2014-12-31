/**
 * 
 */
package com.tigo.cs.view.service;

import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.JRImageLoader;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.tigo.cs.commons.report.JasperReportUtils;
import com.tigo.cs.commons.report.ReportType;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFile;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.view.DataActivity;
import com.tigo.cs.domain.view.DataClient;
import com.tigo.cs.domain.view.DataStatus;
import com.tigo.cs.domain.view.DataZone;
import com.tigo.cs.domain.view.Ot;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.ClientFileFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.facade.view.DataActivityFacade;
import com.tigo.cs.facade.view.DataClientFacade;
import com.tigo.cs.facade.view.DataStatusFacade;
import com.tigo.cs.facade.view.DataZoneFacade;
import com.tigo.cs.facade.view.OtFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

/**
 * Backing Bean para el servicio de Manejo de OTs.
 * 
 * @author Miguel Zorrilla
 * @since CS Fase 7
 */

@ManagedBean(name = "otBean")
@ViewScoped
public class OtBean extends AbstractCrudBeanClient<Ot, OtFacade> {

    // Statics AND/OR final fields
    private static final long serialVersionUID = 3842447504634795315L;
    private static SimpleDateFormat sdFormat = null;
    private final Map<Long, Integer> groupColors = new HashMap<Long, Integer>();
    public static final Integer COD_SERVICE = 16;
    public static final Long META_CLIENT = 1L;
    public static final Long META_ACTIVITIES = 11L;
    public static final Long META_STATUS = 12L;
    public static final Long META_ZONE = 13L;
    
    // __________________________________________________________________________________________
    // Mapped to xhml components
    private Ot selectedOt;
    private ScheduleModel eventModel = new DefaultScheduleModel();
    private List<Userphone> userphoneList;
    private Userphone selectedUserphone;
    private List<Userphone> userphoneListSearch;
    private Userphone selectedUserphoneSearch;
    private Ot newOt;
    private List<DataActivity> activityList;
    private List<DataStatus> statusList;
    private DataActivity selectedActivity;
    private DataClient selectedDataClient;
    private DataZone selectedDataZone;
    private DataStatus selectedDataStatus;
    private Date dateFrom;
    private Date dateTo;
    private Date assignedDate;
    private ScheduleEvent event;
    private String selectedWorkingGroupTitle;
    private Date selectedDate;
    private List<OtDetail> detalles = new ArrayList<OtDetail>();
    private boolean showAllGroups = false;
    private Boolean canModifyOt;
    private DataClient eventDataClient;
    private Ot selectedOtOnSchedule;
    private Client client;
    private List<Ot> completeList;
    private List<DataStatus> statusListDialog;
    private DataStatus selectedStatusListDialog;
    private String activityDescription;
    private boolean parametersValidated = true;
    private Boolean newOTBoolean = false;

    public enum OTStatus {
        NUEVO(0, "Nuevo"),
        ASIGNADO(1, "Asignado"),
        INICIADO(2, "Iniciado"),
        POSTERGADO(3, "Postergado"),
        CANCELADO(4, "Suspendido"),
        FINALIZADO(5, "Finalizado");

        protected final Integer value;
        protected final String status;

        private OTStatus(Integer value, String status) {
            this.value = value;
            this.status = status;
        }

        public Integer getValue() {
            return value;
        }

        public String getStatus() {
            return status;
        }
    }
    
    // __________________________________________________________________________________________
    // Managed Beans
    @EJB
    private OtFacade otFacade;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private DataActivityFacade dataActivityFacade;
    @EJB
    private DataClientFacade dataClientFacade;
    @EJB
    private DataZoneFacade dataZoneFacade;
    @EJB
    private DataStatusFacade dataStatusFacade;
    @EJB
    protected ClassificationFacade classificationFacade;
    @EJB
    private ClientFileFacade clientFileFacade;
    
    
    

    // __________________________________________________________________________________________
    // class members
    private int maxColors = 10;

    /**
     * Default constructor for backing bean
     */
    public OtBean() {
    	super(Ot.class, OtFacade.class);
    	setAditionalFilter("o.statusCod = '0' ");
        setPdfReportName("rep_ot");
        setXlsReportName("rep_ot_xls");    	
    }

    @PostConstruct
    public void initialize() {
        if (sdFormat == null) {
            sdFormat = new SimpleDateFormat(ApplicationBean.getInstance().getOtScheduleDateFormat());
        }
        selectedDate = Calendar.getInstance().getTime();
        selectedDataStatus = dataStatusFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                META_STATUS, OTStatus.NUEVO.value.toString());

//        FacesContext ctx = FacesContext.getCurrentInstance();
//        ctx.getExternalContext().getRequestMap().put("postConstructStatus","Invoked");
    }

    // __________________________________________________________________________________________
    // GETTERS & SETTERS

	public Boolean getNewOTBoolean() {
		return newOTBoolean;
	}

	public void setNewOTBoolean(Boolean newOTBoolean) {
		this.newOTBoolean = newOTBoolean;
	}
    
	public String getActivityDescription() {
		return activityDescription;
	}


	public boolean isParametersValidated() {
		return parametersValidated;
	}

	public void setParametersValidated(boolean parametersValidated) {
		this.parametersValidated = parametersValidated;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

    public Ot getSelectedOtOnSchedule() {    	
        return selectedOtOnSchedule;
    }


	public DataStatus getSelectedStatusListDialog() {
		return selectedStatusListDialog;
	}

	public void setSelectedStatusListDialog(DataStatus selectedStatusListDialog) {
		this.selectedStatusListDialog = selectedStatusListDialog;
	}

	public List<DataStatus> getStatusListDialog() {
		return statusListDialog;
	}

	public void setStatusListDialog(List<DataStatus> statusListDialog) {
		this.statusListDialog = statusListDialog;
	}

	public void setSelectedOtOnSchedule(Ot selectedOtOnSchedule) {
        this.selectedOtOnSchedule = selectedOtOnSchedule;
    }

    public Boolean getCanModifyOt() {
        return canModifyOt;
    }

    public void setCanModifyOt(Boolean canModifyOt) {
        this.canModifyOt = canModifyOt;
    }

    public List<Userphone> getUserphoneListSearch() {
        if (userphoneListSearch == null) {
            try {
                userphoneListSearch = userphoneFacade.findByUserwebAndClassificationAndService(SecurityBean.getInstance().getLoggedInUserClient(), Long.valueOf(COD_SERVICE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userphoneListSearch;
    }

    public void setUserphoneListSearch(List<Userphone> userphoneListSearch) {
        this.userphoneListSearch = userphoneListSearch;
    }

    public Userphone getSelectedUserphoneSearch() {
        return selectedUserphoneSearch;
    }

    public void setSelectedUserphoneSearch(Userphone selectedUserphoneSearch) {
        this.selectedUserphoneSearch = selectedUserphoneSearch;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedWorkingGroupTitle() {
        return selectedWorkingGroupTitle;
    }

    public void setSelectedWorkingGroupTitle(String selectedWorkingGroupTitle) {
        this.selectedWorkingGroupTitle = selectedWorkingGroupTitle;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public SimpleDateFormat getSdFormat() {
        if (sdFormat == null)
            sdFormat = new SimpleDateFormat(ApplicationBean.getInstance().getOtScheduleDateFormat());
        return sdFormat;
    }

    public void setSdFormat(SimpleDateFormat sdFormat) {
        OtBean.sdFormat = sdFormat;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

	public Date getDateFrom() {
        return dateFrom;
    }

	public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

	public Date getDateTo() {
        return dateTo;
    }

	public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Ot getSelectedOt() {
        return selectedOt;

    }

    public void setSelectedOt(Ot ot) {
        this.selectedOt = ot;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

	public List<Userphone> getUserphoneList() {
        if (showAllGroups) {
            try {
                userphoneList = userphoneFacade.findByUserwebAndClassificationAndService(
                        SecurityBean.getInstance().getLoggedInUserClient(), Long.valueOf(COD_SERVICE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (getEntity() != null) {
                userphoneList = otFacade.getUserphoneByZoneClassifAndService(
                		getEntity().getZoneCod(), SecurityBean.getInstance().getLoggedInUserClient());
            }
        }
        return userphoneList;
    }

	public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

	public Userphone getSelectedUserphone() {
        return selectedUserphone;
    }

	public void setSelectedUserphone(Userphone selectedUserphone) {
        this.selectedUserphone = selectedUserphone;
    }

    public Ot getNewOt() {
        if (newOt == null)
            newOt = new Ot();
        return newOt;
    }

    public void setNewOt(Ot newOt) {
        this.newOt = newOt;
    }

    public List<DataActivity> getActivityList() {
        if (activityList == null) {
            activityList = dataActivityFacade.findByClientMeta(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),META_ACTIVITIES);
        }
        return activityList;
    }

    public void setActivityList(List<DataActivity> activityList) {
        this.activityList = activityList;
    }

    public DataActivity getSelectedActivity() {
        return selectedActivity;
    }

    public void setSelectedActivity(DataActivity selectedActivity) {
        this.selectedActivity = selectedActivity;
    }

    public DataClient getSelectedDataClient() {
        return null;
    }

    public void setSelectedDataClient(DataClient selectedDataClient) {
        this.selectedDataClient = selectedDataClient;
    }

    public DataZone getSelectedDataZone() {
        return null;
    }

    public void setSelectedDataZone(DataZone selectedDataZone) {
        this.selectedDataZone = selectedDataZone;
    }

    public DataStatus getSelectedDataStatus() {
        return selectedDataStatus;
    }

    public void setSelectedDataStatus(DataStatus selectedDataStatus) {
        this.selectedDataStatus = selectedDataStatus;
    }

    public List<DataStatus> getStatusList() {
        if (statusList == null) {
            statusList = dataStatusFacade.findByClientMeta(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    META_STATUS);
        }
        return statusList;
    }

    public void setStatusList(List<DataStatus> statusList) {
        this.statusList = statusList;
    }

    public List<OtDetail> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OtDetail> detalles) {
        this.detalles = detalles;
    }

    public boolean isShowAllGroups() {
        return showAllGroups;
    }

    public void setShowAllGroups(boolean showAllGroups) {
        this.showAllGroups = showAllGroups;
    }

    public DataClient getEventDataClient() {
        return eventDataClient;
    }

    public void setEventDataClient(DataClient eventDataClient) {
        this.eventDataClient = eventDataClient;
    }

    // __________________________________________________________________________________________
    // IMPLEMENTACI�N DE M�TODOS ABSTRACTOS

    /**
     * {@inheritDoc}
     * 
     * */
    @Override
	public OtFacade getFacade() {
        return otFacade;
    }

    /**
     * {@inheritDoc}
     * 
     * */
    public String getWhereCriteria() {

        String whereCriteria = "";

        if (selectedDataStatus != null) {
            whereCriteria += " AND o.statusCod = '" + selectedDataStatus.getDataPK().getCodigo() + "'";
        } else if (selectedDataStatus == null) {
            whereCriteria = " AND o.statusCod = '0' ";
        }

        if (selectedUserphoneSearch != null) {
            whereCriteria += " AND o.codUserphone = "
                + selectedUserphoneSearch.getUserphoneCod();
        }

        if (dateFrom != null) {
//            try {
//                String dateFromStr = sdFormat.format(dateFrom);
//                if (selectedDataStatus != null
//                    && selectedDataStatus.getDataPK().getCodigo().equals(OTStatus.NUEVO.value.toString()))
//                    whereCriteria += " AND o.createdDate >= to_date('"
//                        + dateFromStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
//                else
//                    whereCriteria += " AND o.assignedDate >= to_date('"
//                        + dateFromStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
//            } catch (Exception e) {
//            }
        	try {
				String dateFromStr = sdFormat.format(dateFrom);
				whereCriteria += " AND o.createdDate >= to_date('"
						+ dateFromStr + "', 'dd/MM/yyyy hh24:mi:ss') ";                
            } catch (Exception e) {
            }
        }

        if (dateTo != null) {
//            try {
//                GregorianCalendar gc = new GregorianCalendar();
//                gc.setTime(dateTo);
//                gc.set(Calendar.HOUR_OF_DAY, 23);
//                gc.set(Calendar.MINUTE, 59);
//                String dateToStr = sdFormat.format(gc.getTime());
//                if (selectedDataStatus != null
//                    && selectedDataStatus.getDataPK().getCodigo().equals(OTStatus.NUEVO.value.toString()))
//                    whereCriteria += " AND o.createdDate <= to_date('"
//                        + dateToStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
//                else
//                    whereCriteria += " AND o.assignedDate <= to_date('"
//                        + dateToStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
//            } catch (Exception e) {
//            }
        	try {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(dateTo);
                gc.set(Calendar.HOUR_OF_DAY, 23);
                gc.set(Calendar.MINUTE, 59);
                String dateToStr = sdFormat.format(gc.getTime());                
                    whereCriteria += " AND o.createdDate <= to_date('"
                        + dateToStr + "', 'dd/MM/yyyy hh24:mi:ss') ";                
            } catch (Exception e) {
            }
        }

        return whereCriteria;
    }

//    @Override
    public String getDataModelQuery() {
        String query = "";
        if (getSelectedDataStatus() != null
            && getSelectedDataStatus().getCode().equals(OTStatus.NUEVO.value.toString())) {
            query = " SELECT {0} " + " FROM Ot o "
                + " WHERE o.codClient = {1} ";
//            	+ " AND o.codService = {2} ";
        } else {
            query = "" + "  SELECT {0} " + "  FROM Ot o, Userphone u,  "
                + "  IN (u.classificationList) cl "
//                + "  IN (u.clientServiceFunctionalityList) f "
                + "  WHERE o.codClient = {1} AND u.enabledChr = true "
//                + "  AND f.serviceFunctionality.service.serviceCod = {2} "
//                + "  AND o.codService = {2}    "
                + "  AND o.codUserphone = u.userphoneCod "
                + "  AND cl IN (:classifications) ";
        }

        return query;
    }

    @Override
    public String generateReport(ReportType reportType, String reportName) {

        Map<Object, Object> params = new HashMap<Object, Object>();
        params.put("WHERE", getWhereReport());
        params.put("ORDER_BY", getOrderByReport());
        params.put("USER",SecurityBean.getInstance().getLoggedInUserClient().getNameChr());
        params.put(JRParameter.REPORT_LOCALE, i18n.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, i18n.getResourceBundle());

        ClientFile cf = clientFileFacade.getClientLogo(getClient());
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
    
    public String getWhereReport() {
        String where = "";
        Long clientCod = getClient().getClientCod();
        if (getSelectedDataStatus() != null
            && getSelectedDataStatus().getCode().equals(OTStatus.NUEVO.value.toString())) {
            try {
                where = MessageFormat.format(
                        " AND o.COD_CLIENT = {0} AND o.COD_SERVICE = {1} ",
                        clientCod, String.valueOf(COD_SERVICE));
            } catch (Exception e) {
                where = MessageFormat.format(
                        " AND o.COD_CLIENT = {0} AND o.COD_SERVICE = {1} ",
                        clientCod, COD_SERVICE);
                e.printStackTrace();
            }
            where += " AND o.STATUS_COD = '0' ";

        } else {
            String classifications = classificationFacade.getClassificationUserwebString(SecurityBean.getInstance().getLoggedInUserClient());

            try {
                where = " AND o.COD_CLIENT = ".concat(clientCod.toString()).concat(
                        " AND o.COD_SERVICE = ").concat(
                        		String.valueOf(COD_SERVICE));
            } catch (Exception e) {
                where = " AND o.COD_CLIENT = ".concat(clientCod.toString()).concat(
                        " AND o.COD_SERVICE = ").concat(COD_SERVICE.toString());
                e.printStackTrace();
            }
            where += MessageFormat.format(" and EXISTS "
                + "(select * from USERPHONE_CLASSIFICATION uc "
                + "where uc.cod_userphone = u.userphone_cod "
                + "and uc.cod_classification in ({0})) ", classifications);
            where += " AND o.STATUS_COD = '"
                + getSelectedDataStatus().getCode() + "'";

        }

        if (selectedUserphoneSearch != null) {
            where += " AND o.COD_USERPHONE = "
                + selectedUserphoneSearch.getUserphoneCod();
        }

        if (dateFrom != null) {
            try {
                String dateFromStr = sdFormat.format(dateFrom);
                if (selectedDataStatus != null
                    && selectedDataStatus.getDataPK().getCodigo().equals(OTStatus.NUEVO.value.toString()))
                    where += " AND o.CREATED_DATE >= to_date('"
                        + dateFromStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
                else
                    where += " AND o.ASSIGNED_DATE >= to_date('"
                        + dateFromStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
            } catch (Exception e) {
            }
        }

        if (dateTo != null) {
            try {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(dateTo);
                gc.set(Calendar.HOUR_OF_DAY, 23);
                gc.set(Calendar.MINUTE, 59);
                String dateToStr = sdFormat.format(gc.getTime());
                if (selectedDataStatus != null
                    && selectedDataStatus.getDataPK().getCodigo().equals(OTStatus.NUEVO.value.toString()))
                    where += " AND o.CREATED_DATE <= to_date('"
                        + dateToStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
                else
                    where += " AND o.ASSIGNED_DATE <= to_date('"
                        + dateToStr + "', 'dd/MM/yyyy hh24:mi:ss') ";
            } catch (Exception e) {
            }
        }

        return where;
    }
    
    public String getOrderByReport() {
        String sortAttributeColumnName = getAttributeColumnName(getSortHelper().getField());
        if (getSortHelper().getField().indexOf(".") < 0) {
            sortAttributeColumnName = "o.".concat(sortAttributeColumnName);
        }
        return "ORDER BY ".concat(sortAttributeColumnName).concat(
                getSortHelper().isAscending() ? " ASC" : " DESC");
    }

    // __________________________________________________________________________________________
    // MANEJO DE EVENTOS Y METODOS PARA EL CALENDARIO

    /**
     * Listener del p:schedule para programar evento.
     * 
     * Al seleccionar un evento del Schedule, la aplicaci�n proporcionar�
     * informaci�n detallada de la actividad.
     * 
     * @throws ServiceIdNotFoundException
     * */
    public void onEventSelect(ScheduleEntrySelectEvent selectEvent) throws Exception {
        event = selectEvent.getScheduleEvent();
        Ot ot = (Ot) event.getData();
        setEntity(ot);
        showOtDetail(ot);
    }
    
    private void showOtDetail(Ot ot){
    	detalles = new ArrayList<OtDetail>();
    	showAllGroups = false;
    	selectedStatusListDialog = null;
    	userphoneList = null;
    	
    	selectedUserphone = userphoneFacade.find(ot.getCodUserphone());
    	
    	/*si no es el usuario por defecto (igual a 0), se busca la lista de usuarios telefonicos*/
        if (!ot.getCodUserphone().equals(0L)){
        	userphoneList = otFacade.getUserphoneByZoneClassifAndService(ot
				.getZoneCod(), SecurityBean.getInstance().getLoggedInUserClient());
        }
        
        if (!ot.getCodUserphone().equals(0L) && !userphoneList.contains(selectedUserphone) ) {
            showAllGroups = true;
            userphoneList = userphoneFacade.findByUserwebAndClassificationAndService(
            		SecurityBean.getInstance().getLoggedInUserClient(), Long.valueOf(COD_SERVICE));
        }

        List<ServiceValueDetail> dets = otFacade.getOtDetails(ot.getServicevalueCod());
        for (ServiceValueDetail svd : dets) {
            OtDetail det = new OtDetail();
            det.setDate(svd.getRecorddateDat());
            det.setEventCod(svd.getColumn1Chr());
            DataStatus status = otFacade.getDataStatusByCode(svd.getColumn1Chr(), getClient().getClientCod());
            det.setEventDesc(status != null ? status.getDescripcion() : "");
            det.setObs(svd.getColumn2Chr());
            detalles.add(det);
        }        

        /*se verifica que solo se pueda modificar el grupo de trabajo y el estado si esta en estado ASIGNADO o POSPUESTO*/
        if (ot.getStatusCod().equals(OTStatus.ASIGNADO.value.toString()) || ot.getStatusCod().equals(OTStatus.POSTERGADO.value.toString())){
            canModifyOt = true;
            List<DataStatus> list = new ArrayList<DataStatus>();
			DataStatus dsCanceled = dataStatusFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), META_STATUS,
									OTStatus.CANCELADO.value.toString());
			list.add(dsCanceled);

	        if (ot.getStatusCod().equals(OTStatus.POSTERGADO.value.toString())){
            	DataStatus dsAssigned = dataStatusFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
	                    META_STATUS, OTStatus.ASIGNADO.value.toString());
            	list.add(dsAssigned);
            }
            statusListDialog = list;
        }
        else{
            canModifyOt = false;
        }
        

        eventDataClient = dataClientFacade.findEntityByClientMetaCodigo(
        		SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), META_CLIENT, ot.getClientCod());

        selectedOtOnSchedule = ot;        
    }

    /**
     * Suceso que ocurre al mover la actividad (evento): para Manejo de OTs
     * requiere la modificaci�n del assignedDate, sumando <code>dayDelta</code>
     * al assignedDate original de la OT. Misma l�gica se aplica al endDate. Se
     * persisten los nuevos valores en la base de datos para reflejar el cambio
     * y se actualiza la OT asignada al evento originalmente con
     * <code>setData(Object ot)</code> para reflejarlo en la vista.
     * 
     * @param event
     *            objecto evento para su proceso.
     * 
     **/
    public void onEventMove(ScheduleEntryMoveEvent event) {
        Ot ot = (Ot) event.getScheduleEvent().getData();
        Date originalAssignedDate = ot.getAssignedDate();

        /*Solo se puede reasignar OT si estado es ASIGNADO o POSTERGADO*/
        if (ot.getStatusCod().equals(OTStatus.ASIGNADO.value.toString()) 
        		|| ot.getStatusCod().equals(OTStatus.POSTERGADO.value.toString())) {
            /* Calculo para nuevo valor de assigned date.*/
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(originalAssignedDate);
            gc.add(Calendar.DATE, event.getDayDelta());
            gc.add(Calendar.MINUTE, event.getMinuteDelta());
            String newAssignedDate = sdFormat.format(gc.getTime());

            if (gc.getTime().after(new Date())) {
                ot.setAssignedDate(gc.getTime());
                gc.setTime(ot.getEndDate());
                gc.add(Calendar.DATE, event.getDayDelta());
                gc.add(Calendar.MINUTE, event.getMinuteDelta());
                String endDate = sdFormat.format(gc.getTime());

                /* Asignacion de la nueva fecha y hora en la base de datos*/
                getFacade().assignDate(ot.getServicevalueCod(),
                        newAssignedDate, endDate, ot.getStatusCod());

                // Asignaci�n de la nueva fecha y hora a la OT
                // ot.setAssignedDate(newAssignedDate);
                ot.setEndDate(gc.getTime());

                System.out.println("event MOVED...");
            } else {
//                addEvent(ot);
//                updateScheduleSearchValues();
            	
            	/*si no se selecciono una OT en la tabla(cuando se quiere agendar), se muestra todos los de la busqueda. Sino, se busca solo de la seleccionada*/
            	if (getEntity() == null)
            		updateScheduleSearchValues();
            	else
            		findScheduledOt(ot);
                setWarnMessage(i18n.iValue("ot.message.CannotAssignOTBeforeToday"));
            }
        } else {
//            addEvent(ot);
//            updateScheduleSearchValues();
        	
        	/*si no se selecciono una OT en la tabla(cuando se quiere agendar), se muestra todos los de la busqueda. Sino, se busca solo de la seleccionada*/
        	if (getEntity() == null)
        		updateScheduleSearchValues();
        	else
        		findScheduledOt(ot);
            setWarnMessage(MessageFormat.format(i18n.iValue("ot.message.CannotMoveOT"), ot.getStatusDescription()));
        }
    }

    /**
     * Suceso que ocurre al mover la actividad (evento): para Manejo de OTs
     * requiere la modificaci�n del endDate, sumando <code>dayDelta</code> al
     * endDate original de la OT. Se persisten los nuevos valores en la base de
     * datos para reflejar el cambio y se actualiza la OT asignada al evento
     * originalmente con <code>setData(Object ot)</code> para reflejarlo en la
     * vista.
     * 
     * @param event
     *            objecto evento para su proceso.
     * 
     **/
    public void onEventResize(ScheduleEntryResizeEvent event) {
        Ot ot = (Ot) event.getScheduleEvent().getData();
        Date endDate = ot.getEndDate();
        Date originalAssignedDate = ot.getAssignedDate();

        if (ot.getStatusCod().equals(OTStatus.ASIGNADO.value.toString()) 
        		|| ot.getStatusCod().equals(OTStatus.POSTERGADO.value.toString())) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(endDate);
            gc.add(Calendar.DATE, event.getDayDelta());
            gc.add(Calendar.MINUTE, event.getMinuteDelta());
            String newEndDate = sdFormat.format(gc.getTime());
            
            if (originalAssignedDate.after(new Date())) {    
                getFacade().assignDate(ot.getServicevalueCod(),
                        sdFormat.format(ot.getAssignedDate()), newEndDate, ot.getStatusCod());
                ot.setEndDate(gc.getTime());
    
                System.out.println("event RESIZED...");
            }else{
            	if (getEntity() == null)
            		updateScheduleSearchValues();
            	else
            		findScheduledOt(ot);
                setWarnMessage(i18n.iValue("ot.message.CanNotResizeOT"));
//                eventModel.clear();
//                addEvent(ot);
            }
                
        } else {
        	if (getEntity() == null)
        		updateScheduleSearchValues();
        	else
        		findScheduledOt(ot);
            setWarnMessage(i18n.iValue("ot.message.CanNotResizeOT"));
//            eventModel.clear();
//            addEvent(ot);
        }
    }

    public void onDragAndDrop(DragDropEvent dragDropEvent) {
        /*
         * Evento que ocurre al soltar un objeto sobre otro, aplicado para
         * prueba de dragAndDrop de un registro de tabla sobre el calendario
         */
        Object source = dragDropEvent.getSource();
        Object o = dragDropEvent.getData();
        System.out.println("Se agrego:" + o.toString() + " El source: "
            + source.toString());
    }


    /**
     * Backing bean method para asignar una actividad(evento) al Schedule:
     * Adem�s agrega persiste los atributos <code>assignedDate</code> y
     * <code>endDate</code> en la base de datos.
     * 
     * @param ot
     *            objeto {@link Ot} para la asignaci�n de actividad.
     * */
    public String assignCurrentActivity() {
        Ot ot = getEntity();
        
        /*validar que se haya seleccionado una OT*/
        if (ot == null) {
            setWarnMessage(i18n.iValue("ot.message.MustSelectOt"));
            return null;
        }
        
        /*validar que se haya selecconado un usuario telefonico*/
        if (selectedUserphone == null) {
            setWarnMessage(i18n.iValue("ot.message.MustSelectWorkingGroup"));
            return null;
        }
        
        /*validar que se haya selecconado una fecha*/
        if (assignedDate == null) {
            setWarnMessage(i18n.iValue("ot.message.MustSelectDate"));
            return null;
        }
        
        /*verificar que no haya otra actividad para el usuario que empice a la misma hora*/
        List<Ot> list = otFacade.getOtListByUserphoneStatusDate(selectedUserphone.getUserphoneCod(), 
        		        Arrays.asList(OTStatus.ASIGNADO.value.toString(), OTStatus.INICIADO.value.toString()), 
        		        assignedDate, assignedDate);
        if (list != null && list.size() > 0){
        	setWarnMessage(MessageFormat.format(i18n.iValue("ot.message.AlreadyAssignedOTFor"), selectedUserphone.getNameChr(), sdFormat.format(assignedDate)));
            return null;
        }        

        if (ot != null && assignedDate != null) {
            ot.setAssignedDate(assignedDate);
            try {
            	GregorianCalendar gc = new GregorianCalendar();
            	
                if (assignedDate.before(gc.getTime())) {
                    setWarnMessage(i18n.iValue("ot.message.AssignedDateFromToday"));
                    return null;
                }
                if (assignedDate.before(ot.getCreatedDate())) {
                    setWarnMessage(i18n.iValue("ot.message.AssignedDateAfterCreatedDate"));
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                setErrorMessage(i18n.iValue("ot.message.ErrorOnSave"));
                return null;
            }
            DataStatus assignedStatus = dataStatusFacade.findEntityByClientMetaCodigo(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    					META_STATUS, OTStatus.ASIGNADO.getValue().toString());
            ot.setStatusCod(assignedStatus.getCode());
            ot.setStatusDescription(assignedStatus.getDescripcion());            
            ot.setCodUserphone(selectedUserphone.getUserphoneCod());
            
            addEvent(ot);
            
            /*se crea la observacion para crear el svd con el nuevo estado asignado*/
            String observation = MessageFormat.format(i18n.iValue("ot.message.observation.OTAssignedTo"), selectedUserphone.getNameChr(),sdFormat.format(new Date()));
            getFacade().assignDate(ot.getServicevalueCod(),getSdFormat().format(assignedDate),
                    getSdFormat().format(ot.getEndDate()),selectedUserphone.getUserphoneCod(),
                    OTStatus.ASIGNADO.getValue().toString(), observation);

            
            reloadPaginationHelper();
            setEntity(null);
            assignedDate = null;
            selectedUserphone = null;
            userphoneList = null;
            setEntity(null);

            /* Para que el calendario vaya a la fecha en que se agendo la OT */
            selectedDate = ot.getAssignedDate();
            reloadPaginationHelper();
//            updateScheduleSearchValues();   
//            refreshCalendarWithSelectedOt(ot);            

            setSuccessMessage(i18n.iValue("ot.message.SaveSuccess"));
        } else {
            setWarnMessage(i18n.iValue("ot.message.AssignedDateNotNull"));
            return null;
        }
        return null;
    }    

    /**
     * Rellena el Schedule con actividades desde la base de datos.
     * */
    public void populateSchedule() {
        /*
         * FIXME: Es necesario parametrizar el c�digo de estado de OT
         * "Pendientes". Para ello utilizar la tabla CLIENT_PARAMETER y
         * CLIENT_PARAMETER_VALUE para guardar los valores y debe corresponderse
         * a un registro de la tabla META_DATA para el META "Status".
         */
        List<Ot> ots = getFacade().getNotFinalizedOts(getClient().getClientCod(), "4");
        for (Ot ot : ots) {
            addEvent(ot);
        }
    }

    /**
     * Agrega una actividad (evento) al componente Schedule a partir de una
     * {@link Ot}.
     * 
     * */
    public void addEvent(Ot ot) {

        GregorianCalendar assignedDateGC = new GregorianCalendar();
        // assignedDate = ot.getAssignedDate();
        assignedDateGC.setTime(ot.getAssignedDate());
        /*
         * Calculo de endDate: Si se trata de una actividad para agendar el
         * endDate es igual a la suma de assignedDate m�s la duraci�n en minutos
         * que tenga asignada en el meta "Activity" la actividad en cuesti�n. En
         * otro caso, si la ot ya ha sido agendada en endDate es el valor
         * persistido en la base de datos.
         */
        // Date endDate;
        GregorianCalendar gc = new GregorianCalendar();
        if (ot.getEndDate() == null) {
            gc.setTime(assignedDateGC.getTime());
            try {
                gc.add(Calendar.MINUTE,
                        Integer.parseInt(ot.getActivityDuration()));
            } catch (Exception e) {
                gc.add(Calendar.MINUTE, 60);
            }
            // endDate = gc.getTime();
            ot.setEndDate(gc.getTime());
        } else {
            gc.setTime(ot.getEndDate());
        }

        DefaultScheduleEvent event = new DefaultScheduleEvent();
        event.setData(ot);
        event.setId(ot.getOtCode());
        event.setTitle(ot.getActivityDescription());
        event.setStyleClass(getStyleClassName(ot));
        event.setStartDate(assignedDateGC.getTime());
        event.setEndDate(gc.getTime());
        eventModel.addEvent(event);
    }
    
    private Date calculateOtEndDate(Ot ot){
    	 GregorianCalendar assignedDateGC = new GregorianCalendar();
         // assignedDate = ot.getAssignedDate();
         assignedDateGC.setTime(ot.getAssignedDate());
         /*
          * Calculo de endDate: Si se trata de una actividad para agendar el
          * endDate es igual a la suma de assignedDate m�s la duraci�n en minutos
          * que tenga asignada en el meta "Activity" la actividad en cuesti�n. En
          * otro caso, si la ot ya ha sido agendada en endDate es el valor
          * persistido en la base de datos.
          */
        GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(assignedDateGC.getTime());
		try {
			gc.add(Calendar.MINUTE, Integer.parseInt(ot.getActivityDuration()));
		} catch (Exception e) {
			gc.add(Calendar.MINUTE, 60);
		}
		// endDate = gc.getTime();
//		ot.setEndDate(gc.getTime());
        return gc.getTime();
    }

    /**
     * Retorna el nombre del HTML StyleClass para la actividad (event) en
     * cuesti�n seg�n el USERPHONE.
     * 
     * @param ot
     *            para la preparaci�n del nombre del StyleClass.
     * */
    public String getStyleClassName(Ot ot) {
        if (ot.getCodUserphone() == null) {
            return null;
        } else {
            if (groupColors.containsKey(ot.getCodUserphone())) {
                return "schedule_group_"
                    + groupColors.get(ot.getCodUserphone());
            } else {
                groupColors.put(ot.getCodUserphone(), maxColors--);
                return "schedule_group_"
                    + groupColors.get(ot.getCodUserphone());
            }
        }
    }

    /**
     * Suceso que ocurre al seleccionar una actividad (evento): se selecciona
     * una actividad, lo cual hace que se actualice el <code>eventModel</code> y
     * cargue solo las actividades agendadas de todos los userphones
     * correspondientes a la zona del cliente seleccionado.
     * 
     * @param event
     *            objecto evento para su proceso.
     * 
     **/
    public void onRowSelect(SelectEvent event) {
        selectedUserphone = null;
        Ot ot = (Ot) event.getObject();
        findScheduledOt(ot);
    }
    
    /**
     * Metodo que trae todas las ots que estan en curso (ASIGNADO, INICIADO, POSTERGADO)
     * y agrega al calendario si el estado seleccionado en el buscador es NUEVO
     * Sino, se agrega solo la OT seleccionada al calendario
     * */
    private void findScheduledOt(Ot ot){
    	eventModel.clear();
        if (selectedDataStatus != null
            && selectedDataStatus.getDataPK().getCodigo().equals(OTStatus.NUEVO.value.toString())) {
            /*se buscan todos los userphones que tengan asignada la zona de la ot seleccionada*/
        	userphoneList = otFacade.getUserphoneByZoneClassifAndService( 
                    ot.getZoneCod(), SecurityBean.getInstance().getLoggedInUserClient());

        	/*se buscan todas las ots que NO esten en estado finalizado (4=suspendido, 5=cancelado)*/
            List<Ot> otList = otFacade.getOtListByZoneClassificationStatus(ot.getZoneCod(),
            		Arrays.asList(OTStatus.ASIGNADO.value.toString(), OTStatus.INICIADO.value.toString(), OTStatus.POSTERGADO.value.toString()),
            		SecurityBean.getInstance().getLoggedInUserClient());
        	
//        	List<Ot> otList = otFacade.getOtListByUserphoneInZone(
//                    ot.getZoneCod(),
//                    Arrays.asList(OTStatus.CANCELADO.value.toString(), OTStatus.FINALIZADO.value.toString()),
//                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
            
            /*se agregan las ots al calendario*/
            for (Ot otObj : otList) {
                addEvent(otObj);
            }
        } else {
            selectedDate = ot.getAssignedDate();
            addEvent(ot);
        }
    }

    public void refreshCalendarWithSelectedOt(Ot ot) {
        eventModel.clear();
        selectedUserphone = null;
        selectedDate = ot.getAssignedDate();
        addEvent(ot);
    }

    /**
     * Suceso que ocurre al seleccionar un Grupo de trabajo: se selecciona un
     * grupo de trabajo, lo cual hace que se actualice el
     * <code>eventModel</code> y cargue solo las actividades agendadas del grupo
     * seleccionado.
     * 
     **/
    public void onChangeUserphone() {
        eventModel.clear();
        if (selectedUserphone != null) {
            List<Ot> otList = otFacade.getOtListByUserphoneZoneStatus(
                    selectedUserphone.getUserphoneCod(),
                    getEntity().getZoneCod(),
                    Arrays.asList(OTStatus.ASIGNADO.value.toString(), OTStatus.INICIADO.value.toString(), OTStatus.POSTERGADO.value.toString()));
            for (Ot otObj : otList) {
                addEvent(otObj);
            }
        } else {
//            List<Ot> otList = otFacade.getOtListByUserphoneInZone(
//                    selectedOt.getZoneCod(),
//                    Arrays.asList(OTStatus.CANCELADO.value.toString(), OTStatus.FINALIZADO.value.toString()),
//                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
        	List<Ot> otList = otFacade.getOtListByZoneClassificationStatus(getEntity().getZoneCod(),
            		Arrays.asList(OTStatus.ASIGNADO.value.toString(), OTStatus.INICIADO.value.toString(), OTStatus.POSTERGADO.value.toString()),
            		SecurityBean.getInstance().getLoggedInUserClient());
            for (Ot otObj : otList) {
                addEvent(otObj);
            }
        }
    }
    
    private Boolean existsOt(String otCode){
    	List<Ot> list = otFacade.getOtList(otCode, getClient().getClientCod());
    	if (list != null && list.size() > 0)
    		return true;
    	else 
    		return false;
    }

    /**
     * Backing bean method para guardar una actividad creada desde la interfaz:
     * El usuario crea una nueva actividad completando los campos obligatorios,
     * para luego guardarlo en la BD.
     * 
     * */
    public void saveOt(ActionEvent event) {
    	boolean datasValidated = true;
    	
    	/*se comento esta parte porque ahora se puede meter como no meter el codigo ot*/
//        if (newOt.getOtCode() == null) {
//            setWarnMessage(i18n.iValue("ot.message.MustSelectCodOt"));
//            datasValidated = false;
//            RequestContext context = RequestContext.getCurrentInstance();  
//            context.addCallbackParam("datasValidated", datasValidated); 
//            return;
//        }
    	
    	if (newOt.getOtCode() != null && existsOt(newOt.getOtCode())) {
    		setWarnMessage(i18n.iValue("ot.message.OtCodeMustBeUnique"));
    		datasValidated = false;
    		RequestContext context = RequestContext.getCurrentInstance();  
    		context.addCallbackParam("datasValidated", datasValidated); 
    		return;
    	}
    	
        if (selectedActivity == null) {
            setWarnMessage(i18n.iValue("ot.message.MustSelectActivity"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return;
        }
        if (selectedDataClient == null) {
            setWarnMessage(i18n.iValue("ot.message.MustSelectClient"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return;
        }
        if (selectedDataZone == null) {
            setWarnMessage(i18n.iValue("ot.message.MustSelectZone"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return;
        }

        newOt.setCodService(COD_SERVICE.longValue());
        newOt.setCodClient(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
        newOt.setActivityCod(selectedActivity.getCode());
        newOt.setActivityDescription(selectedActivity.getDescripcion());
        newOt.setActivityDuration(selectedActivity.getDuracion());
        newOt.setClientCod(selectedDataClient.getDataPK().getCodigo());
        newOt.setClientDescription(selectedDataClient.getCliente());
        newOt.setZoneCod(selectedDataZone.getDataPK().getCodigo());
        newOt.setStatusCod(OTStatus.NUEVO.value.toString());

        // newOt.setCreatedDate(DateUtil.getFormattedDate(new Date(),
        // appBean.getOtScheduleDateFormat()));
        newOt.setCreatedDate(new Date());
        try {
            otFacade.saveEditing(newOt);
//            tableModel = new LazyEntityDataModel();
            reloadPaginationHelper();
            selectedDate = Calendar.getInstance().getTime();
            clearDialog();
            setSuccessMessage(i18n.iValue("ot.message.SaveSuccess"));
        } catch (Exception e) {
            setErrorMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SavingError"));
            e.printStackTrace();
        }
        
        // SE ENVIA PARAMETRO DE VALIDACION
        newOt = null;
        RequestContext context = RequestContext.getCurrentInstance();  
        context.addCallbackParam("datasValidated", datasValidated); 
    }

    /**
     * Backing bean method que autocompleta las opciones del componente visual
     * p:autocomplete en la variable <code>results</code> de acuerdo al texto
     * ingresado por el usuario.
     * 
     * * @param query el texto ingresado por el usuario que debe ser
     * autocompletado-
     * 
     * */
    public List<DataClient> complete(String query) {
        List<DataClient> results = new ArrayList<DataClient>();
        results = dataClientFacade.findByClientMetaClientName(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                META_CLIENT, query);

        return results;
    }

    /**
     * Backing bean method que autocompleta las opciones del componente visual
     * p:autocomplete en la variable <code>results</code> de acuerdo al texto
     * ingresado por el usuario.
     * 
     * * @param query el texto ingresado por el usuario que debe ser
     * autocompletado-
     * 
     * */
    public List<DataZone> completeDataZone(String query) {
        List<DataZone> results = new ArrayList<DataZone>();
        results = dataZoneFacade.findByClientMetaClientName(
                SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                META_ZONE, query);

        return results;
    }

    public String find() {  
//        updateScheduleSearchValues();        
        setEntity(null);
        selectedUserphone = null;
        userphoneList = null;
        selectedDate = Calendar.getInstance().getTime();
    	reloadPaginationHelper();        
        return null;
    }

    private void updateScheduleSearchValues(){   
        eventModel.clear();   
    	if (!selectedDataStatus.getDataPK().getCodigo().equals(OTStatus.NUEVO.value.toString())){
	        /*se agrega esto que al buscar por userphone, se muestren todos a la vez*/
			if (completeList != null) {
				for (Ot otObj : completeList) {
					addEvent(otObj);
				}
			}
    	}    		
    }
    
    public void clearDialog(ActionEvent event) {
        clearDialog();
    }

    private void clearDialog() {
        newOt = null;
        selectedActivity = null;
        selectedUserphone = null;
        selectedDataZone = null;
    }

    public void adjustOt() {
    	Ot ot = null;
//    	if (getEvent() != null && getEvent().getData() != null)
//    		ot = (Ot) getEvent().getData();
//    	else
    	ot = getEntity();
    		
        boolean datasValidated = true;
        

        if (getSelectedUserphone() == null){
        	datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            context.addCallbackParam("adjust", true); 
        	setWarnMessage(i18n.iValue("ot.message.MustSelectWorkingGroup"));
            return;
        }
        
        if (eventDataClient != null && eventDataClient.getNrocontacto().length() > 20){
        	datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            context.addCallbackParam("adjust", true); 
        	setWarnMessage(i18n.iValue("ot.message.NumberContactLenght"));
            return;
        }
        
        if (eventDataClient != null && eventDataClient.getNombrecontacto().length() > 50){
        	datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            context.addCallbackParam("adjust", true); 
        	setWarnMessage(i18n.iValue("ot.message.ContactLenght"));
            return;
        }
        
        if (eventDataClient != null && eventDataClient.getObservacion().length() > 50){
        	datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            context.addCallbackParam("adjust", true); 
        	setWarnMessage(i18n.iValue("ot.message.ObservationLenght"));
            return;
        }
        
        /*verificar que si se cambia de userphone, que el nuevo userphone no tenga otra ot a la misma hroa asignada*/
        if (canModifyOt && ot.getCodUserphone() != null && !ot.getCodUserphone().equals(getSelectedUserphone().getUserphoneCod())){
	        List<Ot> list = otFacade.getOtListByUserphoneStatusDate(selectedUserphone.getUserphoneCod(), 
			        Arrays.asList(OTStatus.ASIGNADO.value.toString(), OTStatus.INICIADO.value.toString()), 
			        ot.getAssignedDate(), ot.getAssignedDate());
			if (list != null && list.size() > 0){
				setWarnMessage(MessageFormat.format(i18n.iValue("ot.message.AlreadyAssignedOTFor"), selectedUserphone.getNameChr(), sdFormat.format(ot.getAssignedDate())));
				datasValidated = false;
	            RequestContext context = RequestContext.getCurrentInstance();  
	            context.addCallbackParam("datasValidated", datasValidated); 
	            context.addCallbackParam("adjust", true); 
				return;
			} 
        }
        
        /*si se cambio la fecha de asignacion, guardar en la bd*/
        if (ot.getStatusCod().equals(OTStatus.ASIGNADO.getValue().toString()) 
        		|| ot.getStatusCod().equals(OTStatus.POSTERGADO.getValue().toString())){
        	Ot otOld = otFacade.find(ot.getOtCod());
        	
        	/*se verifica que si se cambio la fecha y no se selecciono el estado CANCELADO, se verifica que la fecja
        	 * sea posterior a la actual
        	 * */
        	if (assignedDateChanged(ot.getAssignedDate(), otOld.getAssignedDate())){
        		if ((selectedStatusListDialog != null  
        				&& !selectedStatusListDialog.getDataPK().getCodigo().equals(OTStatus.CANCELADO.getValue().toString()))
        				|| selectedStatusListDialog == null){
	        	
	        		if (ot.getAssignedDate().before(new Date())){
		        		setWarnMessage(i18n.iValue("ot.message.AssignedDateFromToday"));
		        		datasValidated = false;
		                RequestContext context = RequestContext.getCurrentInstance();  
		                context.addCallbackParam("datasValidated", datasValidated); 
		                context.addCallbackParam("adjust", true); 
		                ot.setAssignedDate(otOld.getAssignedDate());
		                return;
		        	}else{
			        	Date endDate = calculateOtEndDate(ot);
			        	ot.setEndDate(endDate);
			        	otFacade.assignDate(ot.getServicevalueCod(), sdFormat.format(ot.getAssignedDate()), sdFormat.format(endDate), ot.getStatusCod());
		        	}
        		}
        	}
        	/*si se encuentra en estado POSTERGADO, y se selecciona el estado asignado, se debe verificar que la fecha de asignacion
        	 * sea posterior a la actual
        	 * */
        	if (ot.getStatusCod().equals(OTStatus.POSTERGADO.getValue().toString()) 
        			&& selectedStatusListDialog != null 
        			&& selectedStatusListDialog.getDataPK().getCodigo().equals(OTStatus.ASIGNADO.getValue().toString())){
        		if (ot.getAssignedDate().before(new Date())){
	        		setWarnMessage(i18n.iValue("ot.message.AssignedDateFromToday"));
	        		datasValidated = false;
	                RequestContext context = RequestContext.getCurrentInstance();  
	                context.addCallbackParam("datasValidated", datasValidated); 
	                context.addCallbackParam("adjust", true); 
	                ot.setAssignedDate(otOld.getAssignedDate());
	                return;
        		}
        	}
        }
        
        /*si se modifico el usuario telefonico*/
        if (canModifyOt && ot.getCodUserphone() != null && !ot.getCodUserphone().equals(getSelectedUserphone().getUserphoneCod())){
        	String observation = MessageFormat.format(i18n.iValue("ot.message.observation.OTAssignedTo"), getSelectedUserphone().getNameChr(), sdFormat.format(new Date()));
            otFacade.changeUserphone(ot.getServicevalueCod(),getSelectedUserphone().getUserphoneCod(), OTStatus.ASIGNADO.getValue().toString(), observation );
        }
        
        /*si se modifico el estado de la OT*/
        if (selectedStatusListDialog != null){
        	String observation = MessageFormat.format(i18n.iValue("ot.message.observation.OTStatusChanged"), selectedStatusListDialog.getDescripcion(), sdFormat.format(new Date()));
            otFacade.changeStatus(ot.getServicevalueCod(), selectedStatusListDialog.getDataPK().getCodigo(), observation );
        }
        
        
        try {
        	if (eventDataClient != null){
	            List<DataClient> datas = new ArrayList<DataClient>();
	            datas.add(eventDataClient);
	            dataClientFacade.updateDataClientList(SecurityBean.getInstance().getLoggedInUserClient(), datas, META_CLIENT);
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSuccessMessage(i18n.iValue("ot.message.SaveSuccess"));
        reloadPaginationHelper();
        ot.setCodUserphone(getSelectedUserphone().getUserphoneCod());
        ot.setNameChr(getSelectedUserphone().getNameChr());
        reloadPaginationHelper();
        userphoneList = null;
        showAllGroups = false;
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("adjust", true); 
        context.addCallbackParam("datasValidated", datasValidated);
        return ;
    }


	private boolean assignedDateChanged(Date date1, Date date2) {
		if (date1.equals(date2))
			return false;
		else
			return true;
	}

	@Override
	public String getReportWhereCriteria() {
		// TODO Auto-generated method stub
		return null;
	}

	public Client getClient() {
		if (client == null) {
			client = SecurityBean.getInstance().getLoggedInUserClient().getClient();
		}
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	/*----------------------------------------------------------------------------------------*/
	@Override
	public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(
                    getRowQuantSelected()).intValue() : 0;
                    
                paginationHelper = new PaginationHelper(pageSize) {
                Integer count = null;
                List<Classification> classifications = null;

                @Override
                public int getItemsCount() {                    
                    String sql = getDataModelQuery();
                    sql = MessageFormat.format(sql, "DISTINCT o", getClient().getClientCod().toString(), String.valueOf(COD_SERVICE));
    				sql = sql.concat(getWhereCriteria());
                    if (sql.contains("classifications")){
                        classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
                    }                    
                    List<Ot> list = otFacade.findRange(sql, null, null, null, classifications);
                    return list != null ? list.size() : 0;
                }

                @Override
                public DataModel createPageDataModel() {
                    String orderby = "o.".concat(getSortHelper().getField()).concat(
                    		getSortHelper().isAscending() ? " ASC" : " DESC");

                    String sql = getDataModelQuery();
                    sql = MessageFormat.format(sql, "DISTINCT o", getClient().getClientCod().toString(), String.valueOf(COD_SERVICE));
    				sql = sql.concat(getWhereCriteria());
                    if (sql.contains("classifications")){
                        classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
                    }
                    
                    completeList = otFacade.findRange(sql, null, null, orderby, classifications);                    
                    
                    /*actualiza el calendario con la lista completa*/
                    updateScheduleSearchValues(); 
                    return new ListDataModelViewCsTigo(getRangeList(completeList, new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }));
//                    return new ListDataModelViewCsTigo(otFacade.findRange(sql,new int[] { getPageFirstItem(), getPageFirstItem()
//                            + getPageSize() }, null, orderby,classifications));
                }
            };
        }
        return paginationHelper;
    }
	
	/** 
	 *  Metodo que a partir de una lista, 
	 *  te devuelve la sublista segun el rango range
	 * 
	 * */
	private List<Ot> getRangeList(List<Ot> list, int[] range){
		if (range.length > 1) {
            if ((list.size() - 1) >= range[1])
                return list.subList(range[0], range[1]);
            else
                return list.subList(range[0],list.size());
        }
        if (range.length > 0)
            return list.subList(range[0],
            		list.size());
        if (range.length == 0)
            return list;
        
        return list;
	}
		
	public String clean() {
		selectedDataStatus = dataStatusFacade.findEntityByClientMetaCodigo(getClient().getClientCod(), META_STATUS, OTStatus.NUEVO.value.toString());
		selectedUserphoneSearch = null;
		dateFrom = null;
		dateTo = null;
		reloadPaginationHelper();
		return null;
	}
		
	public String scheduleOT() {
		String retVal = super.editEntity();
		
		if (getEntity() == null)
			return null;
		
		if (getEntity() != null && !getEntity().getStatusCod().equals(OTStatus.NUEVO.value.toString())){
			setWarnMessage(i18n.iValue("ot.message.OTAlreadyScheduled"));
			setEntity(null);
			return null;
		}
//		getUserphoneList();
//		selectedOt = getEntity();
		findScheduledOt(getEntity());
		return retVal;
	}
	
	@Override
	public String cancelEditing() {
		String retVal = super.cancelEditing();
		setEntity(null);	
		updateScheduleSearchValues();
		return retVal;
	}
	
	private List<Ot> getSelectedList(){
		List<Ot> list = new ArrayList<Ot>();
		Iterator<Ot> iteratorDataModel = getDataModel().iterator();
        for (; iteratorDataModel.hasNext();) {
            Ot currentEntity = iteratorDataModel.next();
            Object obj = getKeyValue(currentEntity);
            if (getSelectedItems().get(obj)) {
            	list.add(currentEntity);
            }
        }
        return list;
	}
	
	public String deleteOT(){
		setEntity(null);
		List<Ot> otListToRemove = getSelectedList();

        if (otListToRemove.isEmpty()) {
            setWarnMessage(i18n.iValue("ot.message.MustSelectAtLeastOneToDelete"));
            return null;
        }
        try{
        	for (Ot ot : otListToRemove) {
            	if (!ot.getStatusCod().equals(OTStatus.NUEVO.value.toString())){
            		setEntity(null);
            		setWarnMessage(i18n.iValue("ot.message.CanOnlyDeleteNewOT"));
                    return null;
            	}
    	        getFacade().deleteEntity(ot);
			}
        	
	        clean();
	        setEntity(null);
//	        updateScheduleSearchValues();
	        reloadPaginationHelper();
	        setSuccessMessage(i18n.iValue("ot.message.SuccessfullyDeletedRecord"));
        }catch (Exception e) {
			setErrorMessage(i18n.iValue("ot.message.CouldNotDeleteRecord"));
		}
        return null;
	}
	
	
	@Override
	public String editEntity() {
		String retVal = super.editEntity();
		
		if (getEntity() == null){
            return null;
		}
		showOtDetail(getEntity());
//		selectedOt = getEntity();
		return retVal;
	}
	
	public void handleClose(CloseEvent event) {  
		setEntity(null);
    } 
	
	public void handleCloseNewOT(CloseEvent event) {  
//		newOt = null;
		newOTBoolean = false;
		clearDialog();
    } 
	
	@Override
	public String applyFilter() {
        paginationHelper = null; // For pagination recreation
        setSelectedItems(null); // For clearing selection
        setDataModel(getPaginationHelper().createPageDataModel());
        
        return null;
    }
	
	public String newOT() {  
//		newOt = new Ot();
		newOTBoolean = true;
	    return null;
	}
}
