package com.tigo.cs.view.administrative;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientGoal;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.TrackingConfiguration;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.ClientFacade;
import com.tigo.cs.facade.ClientGoalFacade;
import com.tigo.cs.facade.ServiceFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.ApplicationBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id: CrudMetaDataGuardBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "crudAdminGoalsBean")
@ViewScoped
public class CrudAdminGoalsBean extends AbstractCrudBeanClient<Userphone, UserphoneFacade> implements Serializable{
	
    /**
     * 
     */
    private static final long serialVersionUID = 296909095164968934L;
    	
	@EJB
	private UserphoneFacade userphoneFacade;
	@EJB
    private ClassificationFacade classificationFacade;
	@EJB
	private ClientGoalFacade clientGoalFacade;
	@EJB
    private ClientFacade clientFacade;
	@EJB
    private ServiceFacade serviceFacade;
	
	private ClientGoal entityDetail;
	private MetaData userphoneMeta; 
	private Userphone selectedUserphone;
	protected List<Userphone> userphoneList;
	private Service selectedService;
	protected List<Service> serviceList;
	private List<String> dayList;
	private List<Service> serviceWithGoalList;
	private Boolean showDetail = false;
	
	private Client client;
	private Integer daysOption;
	private TrackingConfiguration everyDayTrackingConfig;
	private List<TrackingConfiguration> trackingConfigurationList;
	private Boolean deassignConfigurationsMassively = false;
	private Boolean assignConfigurationsMassively = false;
	private Boolean isEditing;
	private Date oldDateFrom;
	private Date oldDateTo;	
	
	private PaginationHelper paginationHelper;
	
	public CrudAdminGoalsBean() {
	    super(Userphone.class, UserphoneFacade.class);
        setAditionalFilter("o.enabledChr = true");
	}

	private DataModel<ClientGoal> dataModelDetail;
	private Map<Object, Boolean> selectedItemsDetail;
	private PaginationHelper paginationHelperDetail;
	private int lastActualPage = -1;
	private String tableDetailsTitle;	
	
	public String getTableDetailsTitle() {
		tableDetailsTitle = i18n.iValue("web.client.trackingconf.screen.title.TotaLListOf");
		return tableDetailsTitle;
	}

	public void setTableDetailsTitle(String tableDetailsTitle) {
		this.tableDetailsTitle = tableDetailsTitle;
	}

	@Override
	public String cancelEditing() {
	    showDetail = false;
		super.cancelEditing();
		resetVariables();
		return null;
	}
		
	public void resetVariables(){
		deassignConfigurationsMassively = false;
		assignConfigurationsMassively = false;
		setSelectedItemsDetail(null);
	}

	public String newEntityDetail(){	
		entityDetail = new ClientGoal();
		entityDetail.setDayTo(31);
		isEditing = false;		
		
		return null;
	}
	
	public String editEntityDetail(){	
		if (getSelectedItemDetail() == null)
			return null;
			
		entityDetail = getSelectedItemDetail();	
		selectedService = entityDetail.getService();
		isEditing = true;
		return null;
	}	

	public String adminGoals() {
	    showDetail = false;
		dataModelDetail = null;
		paginationHelperDetail = null;

		setEntity(new Userphone());
		return null;
	}
	
	public String showDetail() {
	    this.editEntity();
	    showDetail = true;
        dataModelDetail = null;
        paginationHelperDetail = null;
        return null;
    }
	
	public String cancelEditingDetail(){
		entityDetail = null;
		isEditing = false;
		resetVariables();
		return null;
	}
		
	public String deleteEditingDetail(){
		List<ClientGoal> selectedDetailList = getSelectedItemDetailList();
		if (selectedDetailList == null || selectedDetailList.size() == 0){
			setWarnMessage(i18n.iValue("web.client.crudadmingoal.service.message.NotRegistrySelectedError"));
			return null;
		}
		
		/*
		 * BORRA LOGICAMENTE LOS CLIENTGOAL SELECCIONADOS, NO BORRA FISICAMENTE.
		 * PONE LA FECHA DE VALIDEZ HASTA A LA FECHA DE HOY
		 */
		try{
			clientGoalFacade.deleteEntities(selectedDetailList);
    		clearData();
    		resetPaginationDetail();
    		setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingSucces"));
    		return null;
		} catch (Exception e) {
			setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
		}
		return null;
	}
		
	public String saveEditingDetail(){
		if (selectedService == null){
		    setWarnMessage(i18n.iValue("web.client.crudadmingoal.service.message.ServiceCanNotBeEmpty"));
		    return null;
		}
		if (entityDetail.getDescription() == null || entityDetail.getDescription().equals("")){
            setWarnMessage(i18n.iValue("web.client.crudadmingoal.service.message.DescriptionCanNotBeEmpty"));
            return null;
        }
		if (entityDetail.getGoal() == null){
            setWarnMessage(i18n.iValue("web.client.crudadmingoal.service.message.GoalCanNotBeEmpty"));
            return null;
        }		
		
		try{
    		if (!isEditing){ // si es una nueva meta
        		entityDetail.setClient(SecurityBean.getInstance().getLoggedInUserClient().getClient());
        		entityDetail.setValidityDateFrom(new Date());
        		entityDetail.setService(selectedService);    		
        		clientGoalFacade.saveEdit(entityDetail, SecurityBean.getInstance().getLoggedInUserClient());
        		
    		}
    		else{
//    		    if (selectedService.getServiceCod() != entityDetail.getService().getServiceCod()){
//    		        setWarnMessage(i18n.iValue("web.client.crudadmingoal.service.message.CanNotChangeServiceWhenEditing"));
//    	            return null;
//    		    }    		
    			entityDetail.setService(selectedService);   
    		    clientGoalFacade.saveEdit(entityDetail, SecurityBean.getInstance().getLoggedInUserClient());
    		}
    		setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
    		clearData();
            resetPaginationDetail();
            
		}catch (Exception e) {
		    setErrorMessage(i18n.iValue("web.client.backingBean.message.Error"));
        }
		return null;
	}
	
	private String getDescription(){
	    SimpleDateFormat sdf = new SimpleDateFormat(ApplicationBean.getInstance().getDefaultOutputDateFormat());
	    String desc = "(".concat(sdf.format(entityDetail.getValidityDateFrom())).concat(")");
	    
	    return desc;
	}
	
	private void clearData(){
	    isEditing = false;
        entityDetail = null;
    }
	
		
	public String deassignPeriodsMassively(){
		setEntity(null);
        for (Userphone up : getDataModel()) {
            if (getSelectedItems().get(up.getUserphoneCod())) {
                if (getEntity() == null) {
                    setEntity(getFacade().find(up.getUserphoneCod()));
                }
            }
        }
        if (getEntity() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
            return null;
        }
        
		dataModelDetail = null;
		paginationHelperDetail = null;
		deassignConfigurationsMassively = true;
		return null;
	}
	
	public String saveAssignPeriods(){		
		//TRAER LAS METAS SELECCIONADAS
	    List<ClientGoal> selectedClientGoalList = getSelectedItemDetailList();
	    
		//TRAER LOS USERPHONES SELECCIONADOS
		List<Userphone> selectedUserphoneList = getSelectedItemList();
		
		// VERIFICA QUE SE SELECCIONE UNA SOLA META POR SERVICIO
		Map<Long, Integer> countServiceMap = new HashMap<Long, Integer>();
		for (ClientGoal goal : selectedClientGoalList) {
			if (countServiceMap.get(goal.getService().getServiceCod()) == null)
				countServiceMap.put(goal.getService().getServiceCod(), 1);
			else{
				countServiceMap.put(goal.getService().getServiceCod(), countServiceMap.get(goal.getService().getServiceCod()) + 1);
			}
		}
		
		for (Map.Entry<Long, Integer> entry : countServiceMap.entrySet()) {
		    Integer value = entry.getValue();
		    if (value > 1){
		    	//showDetail();
		    	entityDetail = null;
		    	setWarnMessage(i18n.iValue("web.client.crudadmingoal.service.message.CanNotSelectMoreThanOneGoalForUserphone"));
		    	return null;
		    }
		    	
		}
		
				
		//REMUEVE LOS TRACKINGPERIODS QUE YA NO ESTAN SELECCIONADOS
		for(ClientGoal tp : getNotSelectedItemDetailList()){
			List<Userphone> uList = clientGoalFacade.getUserphoneListByClientGoal(tp.getClientGoalCod());
			for(Userphone md : selectedUserphoneList){
				if (uList.contains(md))
					uList.remove(md);
			}
			tp.setUserphones(uList);
			clientGoalFacade.edit(tp);
		}			
		
		//AGREGA TODOS LOS TRACKINGPERIODS A LOS GUARDIAS
		for (ClientGoal tp : selectedClientGoalList) {
			List<Userphone> uList = null;
			uList = clientGoalFacade.getUserphoneListByClientGoal(tp.getClientGoalCod());
			for (Userphone u : selectedUserphoneList) {
				if (uList == null)
					uList = new ArrayList<Userphone>();
				if (!uList.contains(u))
					uList.add(u);
				
			}			
			tp.setUserphones(uList);
			clientGoalFacade.edit(tp);
		}			

        reset("web.client.backingBean.abstractCrudBean.message.SaveSuccess");
		setEntity(null);
		entityDetail = null;
		resetVariables();
		return null;
	}
	
	public String assignGoalsMassively(){
		setEntity(null);
        for (Userphone u : getDataModel()) {
            if (getSelectedItems().get(u.getUserphoneCod())) {
                if (getEntity() == null) {
                    setEntity(getFacade().find(u.getUserphoneCod()));
                }
            }
        }
        if (getEntity() == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
            return null;
        }
        
		dataModelDetail = null;
		paginationHelperDetail = null;
		assignConfigurationsMassively = true;
		return null;
	}
			
		
	///--------------------------------------------------------------------------------//
	// --------------------------------------------------------------------------------------
	// LIST AND TABLE METHODS
	// --------------------------------------------------------------------------------------
	
	public void getDayTo(String dayFrom){
	    Integer dayTo = Integer.parseInt(dayFrom) - 1;
	    if (dayTo == 0)
	        dayTo = 31;
	    entityDetail.setDayTo(dayTo);
	}
	
	public PaginationHelper getPaginationHelperDetail() {
		if (paginationHelperDetail == null) {
			int pageSize = getRowQuantSelected().length() > 0 ? Integer
					.valueOf(getRowQuantSelected()).intValue() : 0;

			paginationHelperDetail = new PaginationHelper(pageSize) {

				String innerWhere = null;
				Integer count = null;

				@Override
				public int getItemsCount() {

					String where = "";
					if (innerWhere == null) {
						innerWhere = where;
					} else {
						if (innerWhere.compareTo(where) == 0) {
							return count;
						}
					}

//					if (getEntity() != null && getEntity().getUserphoneCod() != null){ //se trae toda la lista de conf del cliente
//                        where = where.concat(" ,IN (o.userphones) m WHERE m.userphoneCod = ".concat(getEntity().getUserphoneCod().toString()));
//                    } else {
                        where = where.concat(" WHERE 1 = 1 ");
//                    }
					
					where = where.concat(" AND o.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.validityDateTo = null ");
                    
//					if (!assignConfigurations && !assignConfigurationsMassively) { //se trae toda la lista de conf del cliente
//						where = where.concat(" AND md.CODE_CHR = '".concat(getEntity().getMetaDataPK().getCodeChr())).concat("'");
//					}

					
					count = clientGoalFacade.count(where);
					return count;
				}

				@Override
				public DataModel createPageDataModel() {
					String where = "";

//					if (getEntity() != null && getEntity().getUserphoneCod() != null){ //se trae toda la lista de conf del cliente
//						where = where.concat(" ,IN (o.userphones) m WHERE m.userphoneCod = ".concat(getEntity().getUserphoneCod().toString()));
//					} else {
						where = where.concat(" WHERE 1 = 1 ");
//					}
					where = where.concat(" AND o.client = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
					where = where.concat(" AND o.validityDateTo = null ");
										
					String orderby = "o.validityDateFrom ";

					return new ListDataModelViewCsTigo(clientGoalFacade.findRange(
							new int[] { getPageFirstItem(),
									getPageFirstItem() + getPageSize() },
							where, orderby));
				}
			};
			if (lastActualPage >= 0) {
				paginationHelperDetail.setActualPage(lastActualPage);
				lastActualPage = -1;
			}
		}

		return paginationHelperDetail;
	}

	
	protected void setPaginationHelperDetail(PaginationHelper paginationHelperDetail) {
		this.paginationHelperDetail = paginationHelperDetail;
	}

	public DataModel<ClientGoal> getDataModelDetail() {
		if (dataModelDetail == null) {
			dataModelDetail = getPaginationHelperDetail().createPageDataModel();
			if (showDetail){
				//SELECCIONAR LOS SHIFTPERIODS ASIGNADOS AL GUARDIA
				List<ClientGoal> selectedClientGoalList = clientGoalFacade.getClietGoalListByUserphone(getSelectedItemList().get(0).getUserphoneCod());
				for (ClientGoal cg : dataModelDetail) {
		            if (contiene(selectedClientGoalList, cg))
		            	getSelectedItemsDetail().put(cg.getClientGoalCod(), true);
		        }
			}
		}

		return dataModelDetail;
	}
	
	private Boolean contiene(List<ClientGoal> spList, ClientGoal sp){
        for (ClientGoal cg : spList) {
            if (cg.getClientGoalCod().equals(sp.getClientGoalCod()))
                return true;
        }
        return false;
    }
		
	public void setDataModelDetail(DataModel<ClientGoal> dataModelDetail) {
		this.dataModelDetail = dataModelDetail;
	}

	public String nextPageDetail() {
		getPaginationHelperDetail().nextPage();
		dataModelDetail = null; // For data model recreation
		selectedItemsDetail = null; // For clearing selection
		return null;
	}
	
	public void resetPaginationDetail(){
		entityDetail = null;
	    paginationHelperDetail = null; // For pagination recreation
	    dataModelDetail = null; // For data model recreation
	    selectedItemsDetail = null; // For clearing selection
	}

	public String previousPageDetail() {
		getPaginationHelperDetail().previousPage();
		dataModelDetail = null; // For data model recreation
		selectedItemsDetail = null; // For clearing selection
		return null;
	}

	public Map<Object, Boolean> getSelectedItemsDetail() {
		if (selectedItemsDetail == null) {
			selectedItemsDetail = new HashMap<Object, Boolean>();
		}

		return selectedItemsDetail;
	}

	public void setSelectedItemsDetail(Map<Object, Boolean> selectedItems) {
		this.selectedItemsDetail = selectedItems;
	}

	public String applySortDetail() {
		dataModelDetail = null; // For data model recreation
		selectedItemsDetail = null; // For clearing selection
		return null;
	}
		
	public ClientGoal getSelectedItemDetail(){
		entityDetail = null;
        for (ClientGoal sp : dataModelDetail) {
            if (getSelectedItemsDetail().get(sp.getClientGoalCod())) {
                if (entityDetail == null) {
                	entityDetail = clientGoalFacade.find(sp.getClientGoalCod());
                } else {
                	entityDetail = null;
                    setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectJustOneEdit"));
                    return null;
                }
            }
        }

        if (entityDetail == null) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
        }
        
        return entityDetail;
	}	
	
	public List<ClientGoal> getSelectedItemDetailList(){
		entityDetail = null;
		List<ClientGoal> tpList = new ArrayList<ClientGoal>();
        for (ClientGoal sp : dataModelDetail) {
            if (getSelectedItemsDetail().get(sp.getClientGoalCod())) {
                entityDetail = clientGoalFacade.find(sp.getClientGoalCod());
                tpList.add(sp);
            }
        }
        return tpList;
	}
	
	public List<ClientGoal> getNotSelectedItemDetailList(){
		entityDetail = null;
		List<ClientGoal> spList = new ArrayList<ClientGoal>();
        for (ClientGoal sp : dataModelDetail) {
            if (!getSelectedItemsDetail().get(sp.getClientGoalCod())) {
                entityDetail = clientGoalFacade.find(sp.getClientGoalCod());
                spList.add(sp);
            }
        }
        return spList;
	}
		
	
	///--------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------
    // GETTER Y SETTER 
    // --------------------------------------------------------------------------------------
	
	public ClientGoal getEntityDetail() {
		return entityDetail;
	}

	public void setEntityDetail(ClientGoal entityDetailPeriod) {
		this.entityDetail = entityDetailPeriod;
	}

	public Integer getDaysOption() {
		return daysOption;
	}

	public void setDaysOption(Integer daysOption) {
		this.daysOption = daysOption;
	}	
	
	public TrackingConfiguration getEveryDayTrackingConfig() {
		if (everyDayTrackingConfig == null)
			everyDayTrackingConfig = new TrackingConfiguration();
		return everyDayTrackingConfig;
	}

	public void setEveryDayShiftConfig(TrackingConfiguration everyDayShiftConfig) {
		this.everyDayTrackingConfig = everyDayShiftConfig;
	}
	
	public List<TrackingConfiguration> getTrackingConfigurationList() {
		return trackingConfigurationList;
	}

	public void setTrackingConfigurationList(List<TrackingConfiguration> trackingConfigurationList) {
		this.trackingConfigurationList = trackingConfigurationList;
	}
	
	public Boolean getDeassignConfigurationsMassively() {
        return deassignConfigurationsMassively;
    }

    public void setDeassignConfigurationsMassively(Boolean deassignConfigurationsMassively) {
        this.deassignConfigurationsMassively = deassignConfigurationsMassively;
    }

    public Boolean getAssignConfigurationsMassively() {
		return assignConfigurationsMassively;
	}

	public void setAssignConfigurationsMassively(Boolean assignConfigurationsMassively) {
		this.assignConfigurationsMassively = assignConfigurationsMassively;
	}

	public Boolean getIsEditing() {
		return isEditing;
	}

	public void setIsEditing(Boolean isEditing) {
		this.isEditing = isEditing;
	}
	
	public Date getOldDateFrom() {
		return oldDateFrom;
	}

	public void setOldDateFrom(Date oldDateFrom) {
		this.oldDateFrom = oldDateFrom;
	}

	public Date getOldDateTo() {
		return oldDateTo;
	}

	public void setOldDateTo(Date oldDateTo) {
		this.oldDateTo = oldDateTo;
	}
	
	public List<Userphone> getSelectedItemList(){
		List<Userphone> uList = new ArrayList<Userphone>();
        for (Userphone userphone : getDataModel()) {
            if (super.getSelectedItems().get(userphone.getUserphoneCod())) {
//                Userphone entity = getFacade().find(userphone.getUserphoneCod());
                uList.add(userphone);
            }
        }
        return uList;
	}
	
	public static void main (String args[]){
		
		GregorianCalendar calendar = new GregorianCalendar();
		final int currentDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) - 7) % 7;
		calendar.add(Calendar.DAY_OF_YEAR, currentDayOfWeek);

		System.out.println(calendar.getTime());
	}
		
	public MetaData getUserphoneMeta() {
		return userphoneMeta;
	}
	
	public void setUserphoneMeta(MetaData userphoneMeta) {
		this.userphoneMeta = userphoneMeta;
	}

	public Userphone getSelectedUserphone() {
		return selectedUserphone;
	}

	public void setSelectedUserphone(Userphone selectedUserphone) {
		this.selectedUserphone = selectedUserphone;
	}
	
	public List<Userphone> getUserphoneList() {
		if (userphoneList == null) {
			userphoneList = userphoneFacade.findByUserwebAndClassification(SecurityBean.getInstance().getLoggedInUserClient());
		}
		return userphoneList;
	}

	public void setUserphoneList(List<Userphone> userphoneList) {
		this.userphoneList = userphoneList;
	}
		
    public Boolean getShowDetail() {
        return showDetail;
    }

    public void setShowDetail(Boolean showDetail) {
        this.showDetail = showDetail;
    }

    public Client getClient() {
        if (client == null) {
            client = SecurityBean.getInstance().getLoggedInUserClient().getClient();
        }
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        if (this.client != null) {
            setAditionalFilter("o.client.clientCod = ".concat(this.client.getClientCod().toString()));
        }
    }
        
    public Service getSelectedService() {
        return selectedService;
    }
    
    public List<Service> getServiceList() {
        if (serviceList == null){
            List<Service> auxServiceList = clientFacade.findServices(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
            try{
                if (auxServiceList != null){
                    serviceList = new ArrayList<Service>();
                    for (Service service : auxServiceList) {
                        if (getServiceWithGoalList().contains(service))
                            serviceList.add(service);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return serviceList;
    }
        
    public List<Service> getServiceWithGoalList() {
        if (serviceWithGoalList == null){
            serviceWithGoalList = new ArrayList<Service>();
            serviceWithGoalList.add(serviceFacade.find(1L));
            serviceWithGoalList.add(serviceFacade.find(2L));
            serviceWithGoalList.add(serviceFacade.find(3L));
            serviceWithGoalList.add(serviceFacade.find(5L));
            serviceWithGoalList.add(serviceFacade.find(6L));
            serviceWithGoalList.add(serviceFacade.find(7L));
            serviceWithGoalList.add(serviceFacade.find(10L));
            serviceWithGoalList.add(serviceFacade.find(15L));
            serviceWithGoalList.add(serviceFacade.find(17L));
            serviceWithGoalList.add(serviceFacade.find(18L));
        }
        return serviceWithGoalList;
    }

    public void setServiceWithGoalList(List<Service> serviceWithGoalList) {
        this.serviceWithGoalList = serviceWithGoalList;
    }

    public void setSelectedService(Service selectedService) {
        this.selectedService = selectedService;
    }
    
    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }
        
    public List<String> getDayList() {
        if (dayList == null){
            dayList = new ArrayList<String>();
            for (int i = 1; i <= 31; i++) {
                dayList.add(String.valueOf(i));
            }
        }
        return dayList;
    }

    public void setDayList(List<String> dayList) {
        this.dayList = dayList;
    }

    @Override
    public String getReportWhereCriteria() {
        // TODO Auto-generated method stub
        return null;
    }

    
    ///--------------------------------------------------------------------------------//
    // --------------------------------------------------------------------------------------
    // METODOS REIMPLEMENTADOS
    // --------------------------------------------------------------------------------------
    @Override
    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            int pageSize = getRowQuantSelected().length() > 0 ? Integer.valueOf(getRowQuantSelected()).intValue() : 0;

            paginationHelper = new PaginationHelper(pageSize) {

                String innerWhere = null;
                Integer count = null;

                @Override
                public int getItemsCount() {

                    String where = null;
                    if (!getFilterSelectedField().equals("-1") && getFilterCriteria().length() > 0) {
                        Class<?> fieldClass = getFieldType(getFilterSelectedField());

                        if (fieldClass.equals(Integer.class) || fieldClass.equals(Long.class) || fieldClass.equals(BigInteger.class)) {
                            where = " where o.".concat(getFilterSelectedField()).concat(" = ").concat(getFilterCriteria());
                        } else if (fieldClass.equals(String.class)) {
                            where = " where lower(o.".concat(getFilterSelectedField()).concat(") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat("%'");
                        }
                    }
                    if (getAditionalFilter() != null && getAditionalFilter().trim().length() > 0) {
                        if (where == null) {
                            where = " where ";
                        } else {
                            where = where.concat(" AND ");
                        }
                        where = where.concat(" (").concat(getAditionalFilter().trim()).concat(") ");
                    }

                    if (where == null) {
                        where = " where ";
                    } else {
                        where = where.concat(" AND");
                    }
                    where = where.concat(" o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));

                    //se agrega la clasificacion
//                    List<Classification> classifications = classificationFacade.findByUserweb(SecurityBean.getInstance().getLoggedInUserClient());
                    
                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
                    where += "  AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphoneCod AND u.client = o.client AND cl.codClient = o.client AND cl in (:classifications)) ";
                    
                    
                    /*
                     * esto se verifica para no ejecutar la sentencia cada vez
                     * que se solicita la cantidad de registros a mostrar, se
                     * retorna el valor ya en memoria si la sentencia no cambio.
                     * Esto se realiza para no realizar mas de una vez la
                     * consulta a la base de datos
                     */

                    if (innerWhere == null) {
                        innerWhere = where;
                    } else {
                        if (innerWhere.compareTo(where) == 0) {
                            return count;
                        }
                    }
                    count = getFacade().count(where, classifications);
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = null;
                    if (!getFilterSelectedField().equals("-1") && getFilterCriteria().length() > 0) {
                        Class<?> fieldClass = getFieldType(getFilterSelectedField());

                        if (fieldClass.equals(Integer.class) || fieldClass.equals(Long.class) || fieldClass.equals(BigInteger.class)) {
                            where = " where o.".concat(getFilterSelectedField()).concat(" = ").concat(getFilterCriteria());
                        } else if (fieldClass.equals(String.class)) {
                            where = " where lower(o.".concat(getFilterSelectedField()).concat(") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat("%'");
                        }
                    }
                    if (getAditionalFilter() != null && getAditionalFilter().trim().length() > 0) {
                        if (where == null) {
                            where = " where ";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(getAditionalFilter().trim()).concat(") ");
                    }

                    if (where == null) {
                        where = " where ";
                    } else {
                        where = where.concat(" AND");
                    }
                    where = where.concat(" o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));

                    //se agrega la clasificacion
//                    List<Classification> classifications = classificationFacade.findByUserweb(SecurityBean.getInstance().getLoggedInUserClient());

                    List<Classification> classifications = classificationFacade.findByUserwebWithChilds(SecurityBean.getInstance().getLoggedInUserClient());
                    where += "  AND EXISTS ( SELECT u FROM Userphone u , IN (u.classificationList) cl WHERE u.userphoneCod = o.userphoneCod AND u.client = o.client AND cl.codClient = o.client AND cl in (:classifications)) ";
                                        
                    String orderby = "o.".concat(getSortHelper().getField()).concat(getSortHelper().isAscending() ? " ASC" : " DESC");

                    return new ListDataModelViewCsTigo(getFacade().findRange(new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, where, orderby, classifications));
                }
            };
            if (lastActualPage >= 0) {
                paginationHelper.setActualPage(lastActualPage);
                lastActualPage = -1;
            }
        }

        return paginationHelper;
    }
    
    @Override
    protected void setPaginationHelper(PaginationHelper paginationHelper) {
        super.setPaginationHelper(paginationHelper);
    }

    @Override
    public void applyQuantity(ValueChangeEvent evnt) {
        paginationHelper = null; // For pagination recreation
        setDataModel(null); // For data model recreation
        setSelectedItems(null); // For clearing selection
    }
    
    
    @Override
    public String cleanFilter() {
        setFilterSelectedField("-1");
        setFilterCriteria("");
        paginationHelper = null; // For pagination recreation
        setDataModel(null); // For data model recreation
        setSelectedItems(null); // For clearing selection
       setAditionalFilter("o.enabledChr = true");
       return null;
    }
    
  
}




