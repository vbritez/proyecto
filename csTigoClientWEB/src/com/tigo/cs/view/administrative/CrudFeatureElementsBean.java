package com.tigo.cs.view.administrative;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.commons.util.ListDataModelViewCsTigo;
import com.tigo.cs.commons.util.PaginationHelper;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.ClientFeature;
import com.tigo.cs.domain.DataCheck;
import com.tigo.cs.domain.Feature;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.FeatureEntryType;
import com.tigo.cs.domain.FeatureValue;
import com.tigo.cs.domain.PhoneList;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.FeatureElementEntryFacade;
import com.tigo.cs.facade.FeatureElementFacade;
import com.tigo.cs.facade.FeatureEntryTypeFacade;
import com.tigo.cs.facade.FeatureFacade;
import com.tigo.cs.facade.FeatureValueFacade;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.PhoneListFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;
import com.tigo.cs.view.service.DynamicFormConfBean;

/**
 * 
 * @author Tania NuÃ±ez
 * @version $Id: CrudAdminRolesBean.java 233 2011-12-26 12:47:58Z miguel.maciel
 *          $
 */
@ManagedBean(name = "crudFeatureElementsBean")
@ViewScoped
public class CrudFeatureElementsBean extends AbstractCrudBeanClient<FeatureElement, FeatureElementFacade> implements Serializable {

    static final long serialVersionUID = 1819958394089004180L;
    
    private static final String BLACKLIST = "B";
    private static final String WHITELIST = "W";  
    private static final String INPUT = "INPUT";
    private static final String OUTPUT = "OUTPUT";
    private static final String OPTION = "OPTION";
    private static final String ITEM_LIST = "ITEM_LIST";
    private static final String INTERNAL = "INTERNAL";
    private static final String EXTERNAL = "EXTERNAL";
    private static final String OPEN = "OPEN";
    private static final String BIFURCATION = "BIFURCATION";
    
    @EJB
    private ClassificationFacade classificationFacade;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private FeatureFacade featureFacade;
    @EJB
    private FeatureElementFacade featureElementFacade;
    @EJB
    private FeatureEntryTypeFacade featureEntryTypeElementFacade;
    @EJB
    private PhoneListFacade phoneListFacade;
    @EJB
    private GlobalParameterFacade globalParameterFacade;
    @EJB
    private FeatureElementEntryFacade featureElementEntryFacade;
    @EJB
    private FeatureValueFacade featureValueFacade;
    private Feature selectedFeature;
    private List<Feature> featureList;
    protected String userTypeOption;
    protected Boolean validatedAllUsers = false;
    protected List<Userphone> userphoneList;
    protected List<Userphone> selectedUserphoneList;
    
    protected Boolean validatedAllPhones = false;
    protected List<PhoneList> phoneList;
    protected List<PhoneList> blackPhoneList;
    protected List<PhoneList> selectedPhoneList;
    private TreeNode root;
    private TreeNode selectedNodes;
    private Map<String, String> cssIconMap;
    private List<TreeNode> selectednodesList;
    
    private FeatureElementEntry currentFeatElemEntry;
    private List<FeatureEntryType> fetEntryTypeList;
    private String selectedTypeEntry;
    private ClientFeature selectedClientFeature;
    
    private List<FeatureElementEntry> elementEntryList;
    
    protected List<Classification> classificationList; // Lista de clasificacion del usuario logueado
    protected List<Classification> selectedClassificationList; 
    protected Boolean validatedAllClassification = false;
    
    private Boolean newChild = false;
    private List<FeatureElementEntry> featureElementEntryList;
    
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;
    
    private String rootFeatureElementEntry;
    private String ussdApplicationServerId;
    private String driverId;
    private String validationId;
    private String dynamicDriver;
    
    private boolean editing; // variable que indica si se puede o no editar el arbol de encuesta.
    
    //CAMBIO DE VIVI
	 private boolean editItemList = false; //propiedad para saber si el nodo seleccionado para editar es de tipo ITEM_LIST
	 private boolean nodeBifurcate = false; //propiedad para saber si el nodo seleccionado es de tipo bifurcation
	 private boolean bifurcation = false;//propiedad para saber si se esta creando una bifurcacion
	 private Long idTransient = 0L;
	 private Map<Long, FeatureElementEntry> featuresElementEntriesMap;
	 private List<FeatureElementEntry> featureElementEntriesList;
	 private boolean newChildBifurcation = false;	 
	 private boolean onlyDescriptionEdition = false;	 
	 
	 @PostConstruct
	 public void init(){
           try {
            rootFeatureElementEntry = globalParameterFacade.findByCode("feature.ussdmenuentry.root.code");
            ussdApplicationServerId = globalParameterFacade.findByCode("feature.ussdapplicationserver.code");
            driverId = globalParameterFacade.findByCode("feature.ussddriver.code");
            validationId = globalParameterFacade.findByCode("feature.ussdvalidator.code");
            dynamicDriver = globalParameterFacade.findByCode("feature.ussddynamicdriver.code");
           } catch (GenericFacadeException e) {
               e.printStackTrace();
           }
	 }
	 
	 
	public boolean isOnlyDescriptionEdition() {
		return onlyDescriptionEdition;
	}

	public void setOnlyDescriptionEdition(boolean onlyDescriptionEdition) {
		this.onlyDescriptionEdition = onlyDescriptionEdition;
	}

	public Map<String, String> getCssIconMap() {
        if (cssIconMap == null){
            cssIconMap = new HashMap<String, String>();
            cssIconMap.put("BIFURCATION", "customicon-bifurcation");
            cssIconMap.put("INPUT", "customicon-input");            
            cssIconMap.put("OUTPUT", "customicon-output");
            cssIconMap.put("ITEM_LIST", "customicon-itemlist");
            cssIconMap.put("OPTION", "customicon-option");
        }
        return cssIconMap;
    }

    public void setCssIconMap(Map<String, String> cssIconMap) {
        this.cssIconMap = cssIconMap;
    }
    
    public String getNodeType(FeatureElementEntry node){
//        FeatureElementEntry entry = (FeatureElementEntry)node.getData();
        return getCssIconMap().get(node.getCodFeatureEntryType().getNameChr());
    }

    private PaginationHelper paginationHelper;

    public CrudFeatureElementsBean() {
        super(FeatureElement.class, FeatureElementFacade.class);
        setAditionalFilter("o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
        setAditionalFilter("o.enabledChr = true ");
    }

    @Override
    public String getReportWhereCriteria() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String newEntity() {
        validatedAllPhones = false;
        validatedAllUsers = false;
        selectedFeature = null;
        selectedPhoneList = null;
        selectedUserphoneList = null;
        selectedClassificationList = null;
        currentFeatElemEntry = new FeatureElementEntry();
        setSelectedItems(null);
        nodeBifurcate = false;
        super.newEntity();
        createEntryTree();
        return null;
    }
        
    private boolean startPeriodDateAfterToday(Date startPeriod){
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        GregorianCalendar startPeriodGC = new GregorianCalendar();
        startPeriodGC.setTime(startPeriod);
        startPeriodGC.set(Calendar.HOUR_OF_DAY, 0);
        startPeriodGC.set(Calendar.MINUTE, 0);
        startPeriodGC.set(Calendar.SECOND, 0);
        startPeriodGC.set(Calendar.MILLISECOND, 0);
        
        if (startPeriodGC.getTime().before(today.getTime())){            
            return false;
        }
        return true;
    }
    
    private boolean validadedFields() {

        // SE VERIFICA QUE SE HAYA SELECCIONADO UN FEATURE
        if (selectedFeature == null){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.FeatureCanNotBeEmpty"));
            return false;
        }
        
        selectedClientFeature = featureFacade.getClientFeature(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 
                selectedFeature.getFeatureCode());
         
        Integer count = featureElementFacade.getFeatureElementCount(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 
            															selectedFeature.getFeatureCode());
        
        // SE VERIFICA QUE NO SE HAYA LLEGADO A LA MAXIMA CANTIDAD DE FEATURE ELEMENTS CREADOS
        if (getEntity().getFeatureElementCod() == null && count >= selectedClientFeature.getMaxElementNum()){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.ReachedMaxElement"));
            return false;
        }
    	
        if (getEntity().getDescriptionChr() == null || getEntity().getDescriptionChr().equals("")){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.DescriptionCanNotBeEmpty"));
            return false;
        }
        
        if (getEntity().getDescriptionChr() != null && getEntity().getDescriptionChr().length() > 50){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.DescriptionSizeValidation"));
            return false;
        }
        
        if (getEntity().getStartPeriodDat() == null){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustInsertStartDatePeriod"));
            return false;
        }
        // SI ES NUEVO FEATURE ELEMENT Y SI LA FECHA DE INICIO ES MENOR A HOY
        if (getEntity().getFeatureElementCod() == null && !startPeriodDateAfterToday(getEntity().getStartPeriodDat())){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.StartDateAfterToday"));
            return false;
        }
        if (getEntity().getFinishPeriodDat() == null){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustInsertFinishDatePeriod"));
            return false;
        }
        if (userTypeOption == null || userTypeOption.equals("")){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.UserTypeCanNotBeEmpty"));
            return false;
        }
        
        if (userTypeOption != null 
                && userTypeOption.equals(INTERNAL) 
                && (selectedUserphoneList == null || selectedUserphoneList.size() == 0) 
                && !validatedAllUsers){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectAtLeastOneUserphone"));
            return false;
        }
        
        if (userTypeOption != null 
                && (userTypeOption.equals(EXTERNAL) /*|| userTypeOption.equals(OPEN)*/)
                && (selectedPhoneList == null || selectedPhoneList.size() == 0) 
                && !validatedAllPhones){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectAtLeastOnePhoneList"));
            return false;
        }
        
        if (root.getChildren().get(0).getChildCount() <= 0 ){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.EntryListCanNotBeEmpty"));
            return false;
        }
        
        if (selectedFeature != null){ 
//            selectedClientFeature = featureFacade.getClientFeature(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 
//                    selectedFeature.getFeatureCode());

            if (selectedClientFeature != null && root.getChildren().get(0).getChildCount() > selectedClientFeature.getMaxEntryNum() ){
                setWarnMessage(MessageFormat.format(i18n.iValue("web.client.screen.featureelement.message.EntriesMoreThanMaxValue"), selectedClientFeature.getMaxEntryNum()));
                return false;
            }
        }
        
        if ((selectedClassificationList == null || selectedClassificationList.size() == 0) 
                && !validatedAllClassification){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectAtLeastOneClassification"));
            return false;
        }
        
        return true;
    }

    public String save(){
        if (validadedFields()){
            saveEditing();
        }
        return null;
    }
    
    @Override
    public String saveEditing() {
        try {            
            ClientFeature clientFeature = featureFacade.getClientFeature(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), 
                                         selectedFeature.getFeatureCode());
            getEntity().setEnabledChr(true);     
            getEntity().setClientFeature(clientFeature);
            getEntity().setMaxEntryNum(clientFeature.getMaxEntryNum());
            getEntity().setMaxFeatureValue(clientFeature.getMaxElementNum());
            getEntity().setClassifications(selectedClassificationList != null ? selectedClassificationList : classificationList);
 			getEntity().setFeatureElementEntries(Arrays.asList(getRootNode()));
//          getEntity().setFeatureElementEntries(getFeatureElementEntryList());
//			getEntity().setFeatureElementEntries(Arrays.asList(prueba()));

            getEntity().setOpenChr(false);
            
            if (userTypeOption.equals(INTERNAL)){
                getEntity().setUserphones(selectedUserphoneList != null ? selectedUserphoneList : userphoneList);
                getEntity().setPhoneLists(null);
            }
            else{
                getEntity().setPhoneLists(selectedPhoneList != null ? selectedPhoneList : phoneList);
                getEntity().setUserphones(null);
                if (userTypeOption.equals(OPEN))
                    getEntity().setOpenChr(true);
            }
            
            String retVal = featureElementFacade.saveEdit(getEntity(), rootFeatureElementEntry, ussdApplicationServerId, driverId, validationId, dynamicDriver);
            super.reset("web.client.backingBean.abstractCrudBean.message.SaveSuccess");
            setEntity(null);
            setLastActualPage(getPaginationHelper().getActualPage());
            setPaginationHelper(null); // For pagination recreation
            setDataModel(null); // For data model recreation
            setSelectedItems(null); // For clearing selection

            clearData();

            return retVal;
        } catch (EJBException ex) {
            setErrorMessage(MessageFormat.format(i18n.iValue("web.admin.backingBean.commons.message.AnErrorHasOcurred"), ex.getMessage()));
        } catch (Exception ex) {
            setErrorMessage(MessageFormat.format(i18n.iValue("web.admin.backingBean.commons.message.AnErrorHasOcurred"), ex.getMessage()));
        }
        selectedNodes= null;
        editing=false;
        onlyDescriptionEdition = false;
        return null;
    }
    
    private void getFeatureElementEntryListFromTree(TreeNode root){
        if (featureElementEntryList == null)
            featureElementEntryList = new ArrayList<FeatureElementEntry>();
        List<TreeNode> children = root.getChildren();
        for (int i = 0; i < children.size(); i++) {            
            TreeNode child = children.get(i);
            FeatureElementEntry entry = (FeatureElementEntry)child.getData();
            entry.setOrderNum(i+1);
            if (entry.getFeatureElementEntries() != null && entry.getFeatureElementEntries().size() > 0){
                getFeatureElementEntryListFromTree(child);
                featureElementEntryList.add(entry);
            }
            else{
                featureElementEntryList.add(entry);
            }
                
        }
    }

    @Override
    public String editEntity() { 
        super.editEntity();
        if(getEntity() == null) {
        	return null;
        }
        
        /* verificar si el feature element que se quiere editar
         * ya tiene algun dato cargado. 
         * Si ya tiene, no se pueden agregar/eliminar nodos al arbol, solo editar nombre y descripcion.
         * Sino, si se puede agregar/eliminar/modificar el arbol entero.
         */
        List<FeatureValue> datas =  featureValueFacade.getFeatureValueList(getEntity().getFeatureElementCod());
        if (datas == null || datas.size() == 0){
        	/*editing= false significa que todavia no hay valores, y se puede modificar el arbol*/
        	editing = false;
        }else{
        	editing = true;
        	onlyDescriptionEdition = true;
        }
        
        /*traer la lista de fee para cargar en el hash*/
    	if (featuresElementEntriesMap == null)
    		featuresElementEntriesMap = new HashMap<Long, FeatureElementEntry>();
    	
    	if (getFeatureElementEntriesList() != null && getFeatureElementEntriesList().size() > 0){
    		for (FeatureElementEntry fee : getFeatureElementEntriesList()) {
    			fee.setIdTransient(fee.getFeatureElementEntryCod());
				featuresElementEntriesMap.put(fee.getFeatureElementEntryCod(), fee);
			}
    	}
        
        nodeBifurcate = false;
        
        currentFeatElemEntry = new FeatureElementEntry();
        selectedFeature = getEntity().getClientFeature().getFeature();
        List<Userphone> featureUserphoneList = featureElementFacade.getUserphoneList(getEntity().getFeatureElementCod());
        List<PhoneList> featurePhoneList = featureElementFacade.getPhoneList(getEntity().getFeatureElementCod());
        
        if(getEntity().getOpenChr()){
            getBlackPhoneList();
            userTypeOption = OPEN;
            validatedAllPhones = false;
            if (featurePhoneList != null && blackPhoneList != null 
                    && featurePhoneList.size() > 0
                    && featurePhoneList.size() == blackPhoneList.size()){
                validatedAllPhones = true;
                selectedPhoneList = null;
            }else{
                selectedPhoneList = featurePhoneList;
            }
            
        }else if (featureUserphoneList != null && featureUserphoneList.size() > 0){
            getUserphoneList();
            userTypeOption = INTERNAL;
            validatedAllUsers = false;
            if (featureUserphoneList != null && userphoneList != null 
                    && featureUserphoneList.size() > 0
                    && featureUserphoneList.size() == userphoneList.size()){
                validatedAllUsers = true;
                selectedUserphoneList = null;
            }else{
                selectedUserphoneList = featureUserphoneList;
            }
        }else if(featurePhoneList != null && featurePhoneList.size() > 0){
            getPhoneList();
            userTypeOption = EXTERNAL;
            if (phoneList != null 
                    && featurePhoneList.size() > 0
                    && featurePhoneList.size() == phoneList.size()){
                validatedAllPhones = true;
                selectedPhoneList = null;
            }else{
                selectedPhoneList = featurePhoneList;
            }
        }
        
        getClassificationList();
        List<Classification> featureClassificationList = featureElementFacade.getClassificationList(getEntity().getFeatureElementCod());
        if (featureClassificationList.size() == classificationList.size()){
            validatedAllClassification = true;
            selectedClassificationList = null;
        }else{
            validatedAllClassification = false;
            selectedClassificationList = featureClassificationList;
        }
        
        createEntryTree();
        return null;
    }

    @Override
    public String deleteEntities() {
        List<FeatureElement> selectedEntities = getSelectedEntities();
        if (selectedEntities == null || selectedEntities.isEmpty()) {
            setWarnMessage(i18n.iValue("web.client.backingBean.message.MustSelectAtLeastOneToDelete"));
            return null;
        }

        try {
            featureElementFacade.deleteEntity(selectedEntities, rootFeatureElementEntry);
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingSucces"));
            setEntity(null);
            setLastActualPage(getPaginationHelper().getActualPage());
            setPaginationHelper(null); // For pagination recreation
            setDataModel(null); // For data model recreation
            setSelectedItems(null); // For clearing selection
            
        } catch (EJBException ex) {
            setErrorMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotDeleteFeatureElementConstraint"));
        } catch (Exception ex) {
            setErrorMessage(i18n.iValue("web.client.backingBean.commons.message.AnErrorHasOcurred"));
        }

        return null;
    }
    
    private List<FeatureElement> getSelectedEntities(){
        FeatureElement entity = null;
        List<FeatureElement>  list = new ArrayList<FeatureElement>();
        Iterator<FeatureElement> iteratorDataModel = getDataModel().iterator();
        for (; iteratorDataModel.hasNext();) {
            FeatureElement currentEntity = iteratorDataModel.next();
            Object obj = getKeyValue(currentEntity);
            if (getSelectedItems().get(obj)) {
               entity = getFacade().find(obj);
               list.add(entity);
            }
        }

        if (list.size() == 0) {
            setWarnMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.MustSelectOneEdit"));
        }        
        return list;
    }

    @Override
    public String cancelEditing() {
//        setSelectedScreenclientList(null);
//        setSelectedServiceFuntionalityList(null);
        super.cancelEditing();  
        selectedNodes = null;
        editing = false;
        onlyDescriptionEdition = false;
        bifurcation = false;
        featuresElementEntriesMap = null;
        idTransient = 0L;
        return null;
    }

    public void selectAllPhones() {
        if (validatedAllPhones)
            selectedPhoneList = null;
    }
    
    private void createEntryTree() {
        try {
            root = new DefaultTreeNode("root", null);
            if (getEntity() != null && getEntity().getFeatureElementCod() != null){            	
            	 // CREAR EL ARBOL DE FEATUREELEMENTSENTRY
            	createFeatureElementEntryTree();
            	
            }else{
                FeatureElementEntry entry = new FeatureElementEntry("(Inserte titulo)");
                FeatureEntryType type = featureElementFacade.getFeatureEntryType(OUTPUT);
                entry.setCodFeatureEntryType(type);
                entry.setOrderNum(1);
                entry.setIdTransient(idTransient);
                entry.setCodOwnerEntry(null);
                
                BigDecimal dataCkeckCod;
                DataCheck dataCheck = null;
                try {
                    dataCkeckCod = new BigDecimal(globalParameterFacade.findByCode("feature.datacheck.code"));
                    dataCheck = featureElementFacade.getDataCheckAlphanumeric(dataCkeckCod);     
                } catch (GenericFacadeException e) {                   
                }   
                entry.setDataCheck(dataCheck);
                
                TreeNode child = new DefaultTreeNode(entry, root);
                //child.setParent(root);
                getFeaturesElementEntriesMap().put(entry.getIdTransient(), entry);
            }         
            root.setExpanded(true);
        } catch (Exception ex) {
        }

    }
        
    public String newFeatureElementEntry(){
        currentFeatElemEntry = new FeatureElementEntry();
        return null;
    }
    
    private FeatureElementEntry clone(FeatureElementEntry entity){
        FeatureElementEntry clone = new FeatureElementEntry();
        clone.setCodFeatureEntryType(entity.getCodFeatureEntryType());
        clone.setDataCheck(entity.getDataCheck());
        clone.setDescriptionChr(entity.getDescriptionChr());
        clone.setFeatureElement(entity.getFeatureElement());
        clone.setFeatureElementEntries(entity.getFeatureElementEntries());
        clone.setCodOwnerEntry(entity.getCodOwnerEntry());
        clone.setFeatureElementEntryCod(entity.getFeatureElementEntryCod());
        clone.setFeatureValueData(entity.getFeatureValueData());
        clone.setFinalChr(entity.getFinalChr());
        clone.setIdUssdMenuEntry(entity.getIdUssdMenuEntry());
        clone.setOrderNum(entity.getOrderNum());
        clone.setTitleChr(entity.getTitleChr());
        clone.setIdTransient(entity.getIdTransient());
        return clone;
    }
    
    public String newChild() {
        boolean datasValidated = true;
        selectedTypeEntry = INPUT;
        
        if (selectedNodes == null){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectOneTreeNode"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }
        
        /*se verifica que el arbol pueda ser editable con la variable onlyDescriptionEdition*/
        if (onlyDescriptionEdition){
        	setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddChild"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }
        
        /*si se agrega hijo a una bifurcacion, se retorna directamente sin validar nada*/
        FeatureElementEntry currentEntry = (FeatureElementEntry)selectedNodes.getData();
        if(currentEntry.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(BIFURCATION)) {
        	clearElementEntryData();
        	newChildBifurcation = true;
        	RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }else{
        	newChild = true;
        }
       
        
        /*En caso que el nodo seleccionado sea un hijo del nodo raiz y no sea una bifurcacion al cual si se le puede agregar hijos*/
        if (selectedNodes.getParent().getParent() != null && selectedNodes.getParent().getParent().equals(root)
        		&& !currentEntry.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(BIFURCATION)) {
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddChild"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }

        /*En caso que el nodo seleccionado sea un hijo de itemList tampoco se les puede agregar mas hijos*/
        if (selectedNodes.getParent().getParent() != null && 
        	currentEntry.getCodOwnerEntry().getCodFeatureEntryType().getNameChr().equalsIgnoreCase(ITEM_LIST)) {
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddChild"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }
        
        /*En caso que el nodo seleccionado sea un itemList y el padre un option, tampoco se les puede agregar mas hijos*/
        if (selectedNodes.getParent().getParent() != null && 
        	currentEntry.getCodOwnerEntry().getCodFeatureEntryType().getNameChr().equalsIgnoreCase(OPTION)) {
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddChild"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }
        
        /*si se esta editando, se debe traer los hijos del fee que se selecciono*/
//        if (editing && currentEntry.getFeatureElementEntries() == null){
//        	currentEntry.setFeatureElementEntries(featureElementEntryFacade.getChildren(currentEntry.getFeatureElementEntryCod()));
//        }
        
        /*Si el nodo seleccionado tiene hijos*/
        if(currentEntry.getFeatureElementEntries() != null) {
        	/*Si el nodo seleccionado no es de tipo bifurcacion al cual si se le puede agregar hijos*/
        	/*Recorremos los hijos del nodo seleccionado para verificar si ya no tiene alguna bifurcacion,
        	 * en este caso ya no se le podra agregar hijos*/
        	if(!currentEntry.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(BIFURCATION)) {
        		for(FeatureElementEntry fe : currentEntry.getFeatureElementEntries()) {
        			if(fe.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(BIFURCATION)) {
        				 setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddChild"));
        		         datasValidated = false;
        		         RequestContext context = RequestContext.getCurrentInstance();  
        		         context.addCallbackParam("datasValidated", datasValidated); 
        		         return null;
        			}
        		}
        	}
        }
        
        
        clearElementEntryData();
        
        if(currentEntry.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(BIFURCATION)) {
        	newChildBifurcation = true;
        }else{
        	newChild = true;
        }

        RequestContext context = RequestContext.getCurrentInstance();  
        context.addCallbackParam("datasValidated", datasValidated); 
        return null;
    } 
    
    public String editChild() {         
         if (selectedNodes == null){
             setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectOneTreeNode"));
             return null;
         }

         editItemList = false;
         newChild = false;
         option1 = null;
         option2 = null;
         option3 = null;
         option4 = null;
         option5 = null;
         
         currentFeatElemEntry = clone((FeatureElementEntry)selectedNodes.getData());         
         
         if(currentFeatElemEntry.getCodFeatureEntryType().getNameChr().equals(ITEM_LIST) 
        		 || currentFeatElemEntry.getCodFeatureEntryType().getNameChr().equals(BIFURCATION)
        	|| currentFeatElemEntry.getCodOwnerEntry() == null) {
        	 editItemList = true;
         }
         
         if (currentFeatElemEntry.getCodFeatureEntryType() != null && currentFeatElemEntry.getCodFeatureEntryType().getNameChr().equals(OPTION)){
             if (currentFeatElemEntry.getFeatureElementEntries() != null && currentFeatElemEntry.getFeatureElementEntries().size() >= 1){
                 option1 = currentFeatElemEntry.getFeatureElementEntries().get(0).getTitleChr();
             }
             if (currentFeatElemEntry.getFeatureElementEntries() != null && currentFeatElemEntry.getFeatureElementEntries().size() >= 2){
                 option2 = currentFeatElemEntry.getFeatureElementEntries().get(1).getTitleChr();
             }
             if (currentFeatElemEntry.getFeatureElementEntries() != null && currentFeatElemEntry.getFeatureElementEntries().size() >= 3){
                 option3 = currentFeatElemEntry.getFeatureElementEntries().get(2).getTitleChr();
             }
             if (currentFeatElemEntry.getFeatureElementEntries() != null && currentFeatElemEntry.getFeatureElementEntries().size() >= 4){
                 option4 = currentFeatElemEntry.getFeatureElementEntries().get(3).getTitleChr();
             }
             if (currentFeatElemEntry.getFeatureElementEntries() != null && currentFeatElemEntry.getFeatureElementEntries().size() >= 5){
                 option5 = currentFeatElemEntry.getFeatureElementEntries().get(4).getTitleChr();
             }
             selectedTypeEntry = OPTION;
         }else if (currentFeatElemEntry.getCodFeatureEntryType() != null && currentFeatElemEntry.getCodFeatureEntryType().getNameChr().equals(INPUT)){
             selectedTypeEntry = INPUT;
         }else if (currentFeatElemEntry.getCodFeatureEntryType() != null && currentFeatElemEntry.getCodFeatureEntryType().getNameChr().equals(OUTPUT)){
             selectedTypeEntry = OUTPUT;
         }else if (currentFeatElemEntry.getCodFeatureEntryType() != null && currentFeatElemEntry.getCodFeatureEntryType().getNameChr().equals(BIFURCATION)){
        	 selectedTypeEntry = BIFURCATION;
         }else if (currentFeatElemEntry.getCodFeatureEntryType() != null && currentFeatElemEntry.getCodFeatureEntryType().getNameChr().equals(ITEM_LIST)){
        	 selectedTypeEntry = ITEM_LIST;
         }
         
//         RequestContext context = RequestContext.getCurrentInstance();  
//         context.addCallbackParam("datasValidated", datasValidated);
         return null;
    } 
    
    public String deleteChild() {        
        if (onlyDescriptionEdition){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotDeleteRoot"));            
            return null;
        }
        
        if (selectedNodes.getParent().equals(root)){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotDeleteRoot"));            
            return null;
        }
        
        //Recuperamos el nodo que se desea eliminar
        FeatureElementEntry currentEntry = (FeatureElementEntry) selectedNodes.getData();
        //Recorremos todos los hijos del nodo para borrarlo del hashMap
        if(currentEntry.getFeatureElementEntries() != null) {
        	for(int i = 0 ; i < currentEntry.getFeatureElementEntries().size();i++) {
            	getFeaturesElementEntriesMap().remove(currentEntry.getFeatureElementEntries().get(i).getIdTransient());
            }
        }
        
        
        //Recuperamos el padre del nodo que se desea eliminar
        FeatureElementEntry padre = currentEntry.getCodOwnerEntry();
        
        //Si el padre es distinto a nulo, es decir no es el nodo raiz
        if(padre != null) {       
        	//Recorremos los hijos del padre para eliminar este hijo
        	if(padre.getFeatureElementEntries() != null) {
        		for(int i = 0 ; i < padre.getFeatureElementEntries().size();i++) {
        			if(padre.getFeatureElementEntries().get(i).getIdTransient() == currentEntry.getIdTransient()) {
        			    if(getFeaturesElementEntriesMap().get(padre.getIdTransient()).getFeatureElementEntries() != null) {
        			        getFeaturesElementEntriesMap().get(padre.getIdTransient()).getFeatureElementEntries().remove(i);
        			    }
        			}
                }
        	}
        }
        
        /* DESDE ACA */
        TreeNode parent = selectedNodes.getParent().getParent(); 
        TreeNode nodoSeleccionado = selectedNodes.getParent();
        //selectedNodes.setParent(null);
        
        /* se saca a selectedNode de su padre */
        List<TreeNode> nodes = nodoSeleccionado.getChildren();
        Iterator<TreeNode> it = nodes.iterator();
        while (it.hasNext()) {
            TreeNode treeNode = it.next();
            FeatureElementEntry child = (FeatureElementEntry) treeNode.getData();
            FeatureElementEntry selectedNode = (FeatureElementEntry) selectedNodes.getData();
            if (child.getIdTransient().longValue() == selectedNode.getIdTransient().longValue()) {
                it.remove();
            }
        }
        
        /*se guarda en una lista temporal a los hijos del nodo seleccionado*/
        List<TreeNode> childrenNodes = new ArrayList<TreeNode>();
        for (TreeNode node : nodoSeleccionado.getChildren()) {
            childrenNodes.add(node);
        }
        
        /*se setea a null al padre del nodo seleccionado, y al nodo seleccionado*/
        //nodoSeleccionado.setParent(null);
        
        /* se elimina el hijo seleccionado del padre */
        nodes = parent.getChildren();
        it = nodes.iterator();
        while (it.hasNext()) {
            TreeNode treeNode = it.next();
            FeatureElementEntry child = (FeatureElementEntry) treeNode.getData();
            FeatureElementEntry selectedNode = (FeatureElementEntry) nodoSeleccionado.getData();
            if (child.getIdTransient().longValue() == selectedNode.getIdTransient().longValue()) {
                it.remove();
            }
        }
        
        nodoSeleccionado = null;
        
        /*se crea un nuevo nodo en el arbol para el nodo que esta siendo editado*/
        nodoSeleccionado = new DefaultTreeNode(padre, parent);
        
        /*se recorren a los hijos del nodo seleccionado y se referencia al nuevo nodo padre*/
        if (childrenNodes != null && childrenNodes.size() > 0){
            for (int i = 0; i < childrenNodes.size(); i++) {
                TreeNode node = childrenNodes.get(i);
                node.setParent(nodoSeleccionado);
            }
        }
        
        /*se reordenan los hijos del padre*/
        
        /* SE CARGA LA LISTA DE HIJOS DEL PADRE DEL SELECCIONADO PARA PODER DESFERENCIAR*/
        List<TreeNode> unorderedNodeList = new ArrayList<TreeNode>();
        for (TreeNode treeNode : parent.getChildren()) {
            unorderedNodeList.add(treeNode);
        }
        
        /* SE PONE SU PADRE A NULL Y SE GUARDAN LOS VALORES DEL ORDEN EN UN ARRAY*/
        List<Integer> orderNumList = new ArrayList<Integer>();
        for (int j = 0; j < unorderedNodeList.size(); j++) {
            TreeNode node = unorderedNodeList.get(j);
            FeatureElementEntry value = (FeatureElementEntry)node.getData();
            orderNumList.add(value.getOrderNum());
            node.setParent(null);
        }
        
        Collections.sort(orderNumList);

        /* SE VUELVE A SETEAR LOS HIJOS AL PADRE, PERO EN ORDEN SEGUN EL ORDERNUM*/
        for (Integer i : orderNumList) {
        	for (int j = 0; j < unorderedNodeList.size(); j++) {
                FeatureElementEntry node = (FeatureElementEntry)unorderedNodeList.get(j).getData();
                if (i == node.getOrderNum()){
                    unorderedNodeList.get(j).setParent(parent);
                    break;
                }
            }
		}
        parent.setExpanded(true);
        nodoSeleccionado.setExpanded(true);
        /* HASTA ACA*/
        
        
        //borramos del hashmap el nodo seleccionado
        getFeaturesElementEntriesMap().remove(currentEntry.getIdTransient());
        selectedNodes.setParent(null);
        selectedNodes = null;        
        return null;
    }
    
    private Boolean validateSaveElementEntry(){
        if (currentFeatElemEntry.getTitleChr() == null || currentFeatElemEntry.getTitleChr().equals("")){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectTitle"));            
            return false;
        }
        
        if (currentFeatElemEntry.getDescriptionChr() == null || currentFeatElemEntry.getDescriptionChr().equals("")){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectDescription"));
            return false;
        }
        
        if (currentFeatElemEntry.getTitleChr() != null && currentFeatElemEntry.getTitleChr().length() > 50){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.TitleSizeValidation"));
            return false;
        }
        
        if(!editItemList && !newChildBifurcation) {
	        if (selectedTypeEntry == null || selectedTypeEntry.equals("") ){
	            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectType"));
	            return false;
	        }
        }
        
        if (selectedTypeEntry != null 
                && selectedNodes.getParent().equals(root) 
                && !newChild 
                && (selectedTypeEntry.equals(OPTION) || selectedTypeEntry.equals(INPUT))){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.RootMustBeOutput"));
            return false;
        }
        
        if ((selectedTypeEntry != null && selectedTypeEntry.equals(OPTION)) 
                && ((option1 == null || option1.equals("")) 
                && (option2 == null || option2.equals("")) 
                && (option3 == null || option3.equals("")) 
                && (option4 == null || option4.equals("")) 
                && (option5 == null || option5.equals("")))){  
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectAtLeastOneOption"));
            return false;
        }   
        
        if ((selectedTypeEntry != null && selectedTypeEntry.equals(OPTION)) 
                && ((option1 != null && !option1.equals("") && option1.length() > 50) 
                || (option2 != null && !option1.equals("") && option2.length() > 50) 
                || (option3 != null && !option1.equals("") && option3.length() > 50) 
                || (option4 != null && !option1.equals("") && option4.length() > 50) 
                || (option5 != null && !option1.equals("") && option5.length() > 50))){ 
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectAtLeastOneOption"));
            return null;
        } 
        return true;
    }
    
    public String saveElementEntry() {
    	getFeaturesElementEntriesMap();
        boolean datasValidated = true;
        if (currentFeatElemEntry.getTitleChr() == null || currentFeatElemEntry.getTitleChr().equals("")){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectTitle"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }
        
        if (currentFeatElemEntry.getDescriptionChr() == null || currentFeatElemEntry.getDescriptionChr().equals("")){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectDescription"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }
        
        if (currentFeatElemEntry.getTitleChr() != null && currentFeatElemEntry.getTitleChr().length() > 50){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.TitleSizeValidation"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }
        
        if(!editItemList && !newChildBifurcation) {
	        if (selectedTypeEntry == null || selectedTypeEntry.equals("") ){
	            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectType"));
	            datasValidated = false;
	            RequestContext context = RequestContext.getCurrentInstance();  
	            context.addCallbackParam("datasValidated", datasValidated); 
	            return null;
	        }
        }
        
        if (selectedTypeEntry != null 
                && selectedNodes.getParent().equals(root) 
                && !newChild 
                && (selectedTypeEntry.equals(OPTION) || selectedTypeEntry.equals(INPUT))){
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.RootMustBeOutput"));
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            return null;
        }
        
        if ((selectedTypeEntry != null && selectedTypeEntry.equals(OPTION)) 
                && ((option1 == null || option1.equals("")) 
                && (option2 == null || option2.equals("")) 
                && (option3 == null || option3.equals("")) 
                && (option4 == null || option4.equals("")) 
                && (option5 == null || option5.equals("")))){        
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectAtLeastOneOption"));
            return null;
        }   
        
        if ((selectedTypeEntry != null && selectedTypeEntry.equals(OPTION)) 
                && ((option1 != null && !option1.equals("") && option1.length() > 50) 
                || (option2 != null && !option1.equals("") && option2.length() > 50) 
                || (option3 != null && !option1.equals("") && option3.length() > 50) 
                || (option4 != null && !option1.equals("") && option4.length() > 50) 
                || (option5 != null && !option1.equals("") && option5.length() > 50))){        
            datasValidated = false;
            RequestContext context = RequestContext.getCurrentInstance();  
            context.addCallbackParam("datasValidated", datasValidated); 
            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectAtLeastOneOption"));
            return null;
        } 
        
        elementEntryList = new ArrayList<FeatureElementEntry>();
        
        // SE TRAE Y SETEA EL DATACHECK DE GLOBAL PARAMETER
        DataCheck dataCheck = featureElementFacade.getDataCheckAlphanumeric(getDataCheck());
        currentFeatElemEntry.setDataCheck(dataCheck);
        
        // SE TRAE EL TYPE ENTRY Y SE SETEA
        FeatureEntryType type = null;

        if (selectedTypeEntry != null && selectedTypeEntry.equals(INPUT)){
            type = featureElementFacade.getFeatureEntryType(INPUT);
            
            /*Si se esta editando un nodo y ahora es un INPUT, NO debe tener hijos*/
			if (currentFeatElemEntry.getFeatureElementEntries() != null) {
				for (FeatureElementEntry fee : currentFeatElemEntry.getFeatureElementEntries()) {
					if (getFeaturesElementEntriesMap().get(fee.getIdTransient()) != null)
						getFeaturesElementEntriesMap().remove(fee.getIdTransient());
				}
			}
            currentFeatElemEntry.setFeatureElementEntries(null);
            if (!newChild && !newChildBifurcation){
                /* se elimina los hijops del nodo seleccionado */
                List<TreeNode> nodes = selectedNodes.getChildren();
                Iterator<TreeNode> it = nodes.iterator();
                while (it.hasNext()) {
                    TreeNode treeNode = it.next();
                    it.remove();
                }
            }
        }
        
        if ( selectedTypeEntry != null && selectedTypeEntry.equals(OUTPUT)){
            type = featureElementFacade.getFeatureEntryType(OUTPUT);
            
            /*Si se esta editando un nodo y ahora es un OUTPUT, NO debe tener hijos*/
            if (currentFeatElemEntry.getCodOwnerEntry() != null){
            	if (currentFeatElemEntry.getFeatureElementEntries() != null) {
    				for (FeatureElementEntry fee : currentFeatElemEntry.getFeatureElementEntries()) {
    					if (getFeaturesElementEntriesMap().get(fee.getIdTransient()) != null)
    						getFeaturesElementEntriesMap().remove(fee.getIdTransient());
    				}
    			}
	            currentFeatElemEntry.setFeatureElementEntries(null);
	            if (!newChild && !newChildBifurcation){
	                /* se elimina los hijops del nodo seleccionado */
                    List<TreeNode> nodes = selectedNodes.getChildren();
                    Iterator<TreeNode> it = nodes.iterator();
                    while (it.hasNext()) {
                        TreeNode treeNode = it.next();
                        it.remove();
                    }
		       }
            }
        }

        if ( selectedTypeEntry != null && selectedTypeEntry.equals(OPTION)){
            type = featureElementFacade.getFeatureEntryType(OPTION);            
            currentFeatElemEntry.setCodFeatureEntryType(type);
        	if (!onlyDescriptionEdition){
	            
	            /*se setean las opciones*/
	            List<String> optionList = new ArrayList<String>();
	            if (option1 != null && !option1.equals(""))
	                optionList.add(option1);
	            if (option2 != null && !option2.equals(""))
	                optionList.add(option2);
	            if (option3 != null && !option3.equals(""))
	                optionList.add(option3);
	            if (option4 != null && !option4.equals(""))
	                optionList.add(option4);
	            if (option5 != null && !option5.equals(""))
	                optionList.add(option5);
	            
	            List<FeatureElementEntry> entries = new ArrayList<FeatureElementEntry>();
	            FeatureEntryType itemType = featureElementFacade.getFeatureEntryType(ITEM_LIST);
	            
	            for (int i = 0; i < optionList.size(); i++) {
	                String option = optionList.get(i);
	                FeatureElementEntry optionEntry = new FeatureElementEntry();
	                optionEntry.setTitleChr(option);
	                optionEntry.setDescriptionChr(option);
	                optionEntry.setDataCheck(dataCheck);
	                optionEntry.setOrderNum(i+1);
	                optionEntry.setCodFeatureEntryType(itemType);
	                optionEntry.setCodOwnerEntry(currentFeatElemEntry);               
	                optionEntry.setIdTransient(++idTransient);
	                entries.add(optionEntry);
	                elementEntryList.add(optionEntry);
	                getFeaturesElementEntriesMap().put(optionEntry.getIdTransient(), optionEntry);
	            }
	            currentFeatElemEntry.setFeatureElementEntries(entries);
        	}
        }
        
        /*Obtenemos el nodo al cual se le esta agregando o editando un hijo*/
        FeatureElementEntry fe = (FeatureElementEntry) selectedNodes.getData();
        
        if(fe.getCodFeatureEntryType() != null) {
        	if(editItemList) {
        		if(fe.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(ITEM_LIST)) {
            		type = featureElementFacade.getFeatureEntryType(ITEM_LIST);
            	}else if(fe.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(BIFURCATION)) {
            		type = featureElementFacade.getFeatureEntryType(BIFURCATION);
            	}
        	}else if(newChildBifurcation){
        		type = featureElementFacade.getFeatureEntryType(ITEM_LIST);
        	}
        }

        /*se setea el type del fe actual*/
        currentFeatElemEntry.setCodFeatureEntryType(type);

        /*Si es un nuevo hijo*/
        if(newChild || newChildBifurcation) {
        	/*Al que se esta creando le seteamos como padre el nodo seleccionado y un id temporal*/
        	currentFeatElemEntry.setCodOwnerEntry(fe);
        	currentFeatElemEntry.setIdTransient(++idTransient);
        }else {
        	currentFeatElemEntry.setCodOwnerEntry(fe.getCodOwnerEntry());
        	currentFeatElemEntry.setIdTransient(fe.getIdTransient());
        }
        
        /*Si se esta creando un nuevo hijo*/
		if(newChild || newChildBifurcation) {
			//Recuperamos del hashmap el nodo seleccionado al cual se le esta agregando un nuevo hijo
			FeatureElementEntry aux = getFeaturesElementEntriesMap().get(fe.getIdTransient());
    		if(aux.getFeatureElementEntries() != null) {
    			getFeaturesElementEntriesMap().get(aux.getIdTransient()).getFeatureElementEntries().add(currentFeatElemEntry);
    			getFeaturesElementEntriesMap().put(currentFeatElemEntry.getIdTransient(),currentFeatElemEntry);
    		}else{
    			List<FeatureElementEntry> list = new ArrayList<FeatureElementEntry>();
    			list.add(currentFeatElemEntry);
    			getFeaturesElementEntriesMap().get(aux.getIdTransient()).setFeatureElementEntries(list);
    			getFeaturesElementEntriesMap().put(currentFeatElemEntry.getIdTransient(),currentFeatElemEntry);
    		}
    		getFeaturesElementEntriesMap();
    		//Si se esta editando
		}else{
			/*Si el padre del nodo que se esta editando no es nulo, es decir no es la raiz*/
			if(currentFeatElemEntry.getCodOwnerEntry() != null) {
				//Recuperamos del hashmap el padre del nodo seleccionado que se esta editando
    			FeatureElementEntry padre = getFeaturesElementEntriesMap().get(fe.getCodOwnerEntry().getIdTransient());
				//Recorremos los hijos del padre del nodo seleccionado para recuperar el que se edito
				for(int k = 0; k < padre.getFeatureElementEntries().size(); k++) {
					if(equals(padre.getFeatureElementEntries().get(k).getIdTransient(), fe.getIdTransient())) {
						//borramos este hijo de su padre
						padre.getFeatureElementEntries().remove(fe);
						//le agregamos el nuevo ya editado
						padre.getFeatureElementEntries().add(k,currentFeatElemEntry); 
						//borramos del hashmap el nodo editado
						getFeaturesElementEntriesMap().remove(padre.getFeatureElementEntries().get(k).getIdTransient());
						//borramos el padre del hashmap
						getFeaturesElementEntriesMap().remove(padre.getIdTransient());
						//agregamos de vuelta al hashmap el padre ya con su hijo editado
						getFeaturesElementEntriesMap().put(padre.getIdTransient(), padre);
						//agregamos de vuelta al hashmap el hijo editado
						getFeaturesElementEntriesMap().put(currentFeatElemEntry.getIdTransient(), currentFeatElemEntry);
						break;
					}
				}
				/*Recorremos todos los hijos del nodo seleccionado para cambiar la referencia del papa de los mismos al actual*/
				if(currentFeatElemEntry.getFeatureElementEntries() != null) {
					for(int k = 0; k < currentFeatElemEntry.getFeatureElementEntries().size(); k++) {
						//Recuperamos el hijo k y le seteamos como padre al actual
						getFeaturesElementEntriesMap().get(currentFeatElemEntry.getIdTransient()).getFeatureElementEntries().get(k).setCodOwnerEntry(currentFeatElemEntry);
					}
				}
				
			}else {
				/*Buscamos a todos los hijos del nodo raiz */
				if(fe.getFeatureElementEntries() != null) {
					for(int k = 0; k < fe.getFeatureElementEntries().size(); k++) {
    					//Recuperamos el hijo k y le seteamos como padre al actual
						FeatureElementEntry hijok = getFeaturesElementEntriesMap().get(fe.getIdTransient()).getFeatureElementEntries().get(k);
						getFeaturesElementEntriesMap().get(fe.getIdTransient()).getFeatureElementEntries().get(k).setCodOwnerEntry(currentFeatElemEntry);
						currentFeatElemEntry.getFeatureElementEntries().get(k).setIdTransient(hijok.getFeatureElementEntryCod());
    				}
				}
				//borramos del hashmap el nodo que se edito y luego lo ponemos ya con los cambios realizados
				getFeaturesElementEntriesMap().remove(fe.getIdTransient());
				getFeaturesElementEntriesMap().put(currentFeatElemEntry.getIdTransient(), currentFeatElemEntry);
			}
		
		}	

        elementEntryList.add(currentFeatElemEntry);
        getFeaturesElementEntriesMap();
       
        // SE ARMA EL ARBOL
        if (newChild || newChildBifurcation){
            currentFeatElemEntry.setOrderNum(getMaxValueOrder(selectedNodes.getChildren()) + 1);
            TreeNode child = new DefaultTreeNode(currentFeatElemEntry, selectedNodes);
            child.setExpanded(true);
            selectedNodes.setExpanded(true);
            
        }else{
        	/*se crea un nuevo nodo para el nodo que se esta editando y se referencia de vuelta*/
        	
        	/*se trae el padre del nodo seleccionado*/
            TreeNode parent = selectedNodes.getParent();   
            
            /*se guarda en una lista temporal a los hijos del nodo seleccionado*/
            List<TreeNode> childrenNodes = new ArrayList<TreeNode>();
            for (TreeNode node : selectedNodes.getChildren()) {
                childrenNodes.add(node);
            }
            
            /* se elimina el hijo seleccionado del padre */
            List<TreeNode> nodes = parent.getChildren();
            Iterator<TreeNode> it = nodes.iterator();
            while (it.hasNext()) {
                TreeNode treeNode = it.next();
                FeatureElementEntry children = (FeatureElementEntry) treeNode.getData();
                FeatureElementEntry selectedNode = (FeatureElementEntry) selectedNodes.getData();
                if (children.getIdTransient().longValue() == selectedNode.getIdTransient().longValue()) {
                    it.remove();
                }
            }
            
            /*se setea a null al padre del nodo seleccionado, y al nodo seleccionado*/
            //selectedNodes.setParent(null);
            selectedNodes = null;
            
            /*se crea un nuevo nodo en el arbol para el nodo que esta siendo editado*/
            selectedNodes = new DefaultTreeNode(currentFeatElemEntry, parent);
            
            /*se recorren a los hijos del nodo seleccionado y se referencia al nuevo nodo padre*/
            if (childrenNodes != null && childrenNodes.size() > 0){
                for (int i = 0; i < childrenNodes.size(); i++) {
                    TreeNode node = childrenNodes.get(i);
                    node.setParent(selectedNodes);
                }
            }
            
            /*se reordenan los hijos del padre*/
            
            /* SE CARGA LA LISTA DE HIJOS DEL PADRE DEL SELECCIONADO PARA PODER DESFERENCIAR*/
            List<TreeNode> unorderedNodeList = new ArrayList<TreeNode>();
            for (TreeNode treeNode : parent.getChildren()) {
                unorderedNodeList.add(treeNode);
            }
            
            /* SE PONE SU PADRE A NULL Y SE GUARDAN LOS VALORES DEL ORDEN EN UN ARRAY*/
            List<Integer> orderNumList = new ArrayList<Integer>();
            for (int j = 0; j < unorderedNodeList.size(); j++) {
                TreeNode node = unorderedNodeList.get(j);
                FeatureElementEntry value = (FeatureElementEntry)node.getData();
                orderNumList.add(value.getOrderNum());
                node.setParent(null);
            }
            
            Collections.sort(orderNumList);

            /* SE VUELVE A SETEAR LOS HIJOS AL PADRE, PERO EN ORDEN SEGUN EL ORDERNUM*/
            for (Integer i : orderNumList) {
            	for (int j = 0; j < unorderedNodeList.size(); j++) {
                    FeatureElementEntry node = (FeatureElementEntry)unorderedNodeList.get(j).getData();
                    if (i == node.getOrderNum()){
                    	TreeNode nodo = unorderedNodeList.get(j);
                    	nodo.setParent(parent);
                        break;
                    }
                }
			}
                        
            parent.setExpanded(true);
            selectedNodes.setExpanded(true);
        }
        
        // SE LIMPIAN LAS VARIABLES
        clearElementEntryData();
        
        // SE ENVIA PARAMETRO DE VALIDACION
        RequestContext context = RequestContext.getCurrentInstance();  
        context.addCallbackParam("datasValidated", datasValidated); 
        getFeaturesElementEntriesMap();
        return null;
    }
    
    private BigDecimal getDataCheck() {
        BigDecimal dataCkeckCod;
        try {
            dataCkeckCod = new BigDecimal(globalParameterFacade.findByCode("feature.datacheck.code"));
        } catch (GenericFacadeException e) {
            notifier.logError(
                    DynamicFormConfBean.class,
                    "SaveElementEntry",
                    "Verifique que exista un Parametro Global que apunte al datackeck que almacena datos alfanumericos.");
            return null;
        }
        return dataCkeckCod;
    }
    
    private Boolean equals(Long v1, Long v2){
    	if (v1 != null && v2 != null && v1.equals(v2))
    		return true;
    	else
    		return false;
    }    
    
    private Integer getMaxValueOrder(List<TreeNode> children){
    	Integer maxValue = 0;
    	for (TreeNode treeNode : children) {
    		FeatureElementEntry fe = (FeatureElementEntry) treeNode.getData();
			if (fe.getOrderNum() > maxValue)
				maxValue = fe.getOrderNum(); 
		}
    	return maxValue;
    }
    
    private void clearElementEntryData() {
        currentFeatElemEntry = null;
        currentFeatElemEntry = new FeatureElementEntry();
        selectedTypeEntry = null;
        option1 = null;
        option2 = null;
        option3 = null;
        option4 = null;
        option5 = null;
        bifurcation = false;
        editItemList = false;
        nodeBifurcate = false;
        newChildBifurcation = false;
    }
    
    private void clearData(){
        selectedFeature = null;
        selectedTypeEntry = null;
        selectedClassificationList = null;
        selectedPhoneList = null;
        selectedUserphoneList = null;
        validatedAllClassification = false;
        validatedAllPhones = false;
        validatedAllUsers = false;
        featureElementEntryList = null;
        userTypeOption = null;
        featuresElementEntriesMap = null;
        idTransient = 0L;
    }

    public void selectAllClassification() {
        if (validatedAllClassification)
            selectedClassificationList = null;
    }
    
    //
    // GETTER Y SETTER
    
    public Feature getSelectedFeature() {
        return selectedFeature;
    }

    public void setSelectedFeature(Feature selectedFeature) {
        this.selectedFeature = selectedFeature;
    }

    public List<Feature> getFeatureList() {
        if (featureList == null){
        	featureList = featureFacade.getFeatureByClientByShowOnMenu(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), true);
        }
        return featureList;
    }

    public void setFeatureList(List<Feature> featureList) {
        this.featureList = featureList;
    }

    public String getUserTypeOption() {
        return userTypeOption;
    }

    public void setUserTypeOption(String userTypeOption) {
        this.userTypeOption = userTypeOption;
    }

    public Boolean getValidatedAllUsers() {
        return validatedAllUsers;
    }

    public void setValidatedAllUsers(Boolean validatedAllUsers) {
        this.validatedAllUsers = validatedAllUsers;
    }  
    
    public void selectAllUserphones() {
        if (validatedAllUsers)
            selectedUserphoneList = null;
    }

    public List<Userphone> getUserphoneList() {
        if (userphoneList == null) {
            userphoneList = userphoneFacade.findByUserwebAndClassification(SecurityBean.getInstance().getLoggedInUserClient());
        }
        return userphoneList;
    }

    public List<Userphone> getSelectedUserphoneList() {
        return selectedUserphoneList;
    }

    public void setUserphoneList(List<Userphone> userphoneList) {
        this.userphoneList = userphoneList;
    }

    public void setSelectedUserphoneList(List<Userphone> selectedUserphoneList) {
        this.selectedUserphoneList = selectedUserphoneList;
    }

    public List<PhoneList> getPhoneList() {
        if (phoneList == null){
            try {
                phoneList = phoneListFacade.getPhoneListByClientType(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), WHITELIST);
            } catch (MoreThanOneResultException e) {
                phoneList = null;
            } catch (GenericFacadeException e) {
                phoneList = null;
            }
        }
        return phoneList;
    }

    public List<PhoneList> getSelectedPhoneList() {
        return selectedPhoneList;
    }

    public void setPhoneList(List<PhoneList> phoneList) {
        this.phoneList = phoneList;
    }

    public void setSelectedPhoneList(List<PhoneList> selectedPhoneList) {
        this.selectedPhoneList = selectedPhoneList;
    }
  
    public Boolean getValidatedAllPhones() {
        return validatedAllPhones;
    }

    public void setValidatedAllPhones(Boolean validatedAllPhones) {
        this.validatedAllPhones = validatedAllPhones;
    }

    public List<PhoneList> getBlackPhoneList() {
        if (blackPhoneList == null){
            try {
                blackPhoneList = phoneListFacade.getPhoneListByClientType(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(), BLACKLIST);
            } catch (MoreThanOneResultException e) {
            } catch (GenericFacadeException e) {
            }
        }
        return blackPhoneList;
    }

    public void setBlackPhoneList(List<PhoneList> blackPhoneList) {
        this.blackPhoneList = blackPhoneList;
    }
    
    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public List<TreeNode> getSelectednodesList() {
        if (selectednodesList == null) {
            selectednodesList = new ArrayList<TreeNode>();
        }
        return selectednodesList;
    }

    public void setSelectednodesList(List<TreeNode> selectednodesList) {
        this.selectednodesList = selectednodesList;
    }
    
    public FeatureElementEntry getCurrentFeatElemEntry() {
        return currentFeatElemEntry;
    }

    public void setCurrentFeatElemEntry(FeatureElementEntry currentFeatElemEntry) {
        this.currentFeatElemEntry = currentFeatElemEntry;
    }
   
    public List<FeatureEntryType> getFetEntryTypeList() {
        if (fetEntryTypeList == null){
            fetEntryTypeList = featureEntryTypeElementFacade.findAll();
        }
        return fetEntryTypeList;
    }

    public void setFetEntryTypeList(List<FeatureEntryType> fetEntryTypeList) {
        this.fetEntryTypeList = fetEntryTypeList;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getOption5() {
        return option5;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public void setOption5(String option5) {
        this.option5 = option5;
    }

    public String getSelectedTypeEntry() {
        return selectedTypeEntry;
    }

    public void setSelectedTypeEntry(String selectedTypeEntry) {
        this.selectedTypeEntry = selectedTypeEntry;
    }

    public List<FeatureElementEntry> getElementEntryList() {
        return elementEntryList;
    }

    public void setElementEntryList(List<FeatureElementEntry> elementEntryList) {
        this.elementEntryList = elementEntryList;
    }    
    
    public Boolean getValidatedAllClassification() {
        return validatedAllClassification;
    }

    public void setValidatedAllClassification(Boolean validatedAllClassification) {
        this.validatedAllClassification = validatedAllClassification;
    } 
    
    /*Retorna la lista de clasificaciÃ³n o clasificaciones asociadas al usuario logueado*/
    public List<Classification> getClassificationList() {
        if (classificationList == null) {
            classificationList = classificationFacade.getEagerClassificationListByClient(SecurityBean.getInstance()
                    .getLoggedInUserClient().getClient());
        }
        return classificationList;
    }

    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }
    
    public List<Classification> getSelectedClassificationList() {
        return selectedClassificationList;
    }

    public void setSelectedClassificationList(List<Classification> selectedClassificationList) {
        this.selectedClassificationList = selectedClassificationList;
    }
    
    public List<FeatureElementEntry> getFeatureElementEntryList() {
        featureElementEntryList = null;
        getFeatureElementEntryListFromTree(root.getChildren().get(0));
        
        FeatureElementEntry title = (FeatureElementEntry)root.getChildren().get(0).getData();
        title.setFeatureElementEntries(featureElementEntryList);
        
        return Arrays.asList(title);
    }

    public void setFeatureElementEntryList(List<FeatureElementEntry> featureElementEntryList) {
        this.featureElementEntryList = featureElementEntryList;
    }
    
    /*
     * METODOS SOBREESCRITOS
     * 
     * */    


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
                            where = "where o.".concat(getFilterSelectedField()).concat(" = ").concat(getFilterCriteria());
                        } else if (fieldClass.equals(String.class)) {
                            where = "where lower(o.".concat(getFilterSelectedField()).concat(") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat("%'");
                        }
                    }
                    if (getAditionalFilter() != null && getAditionalFilter().trim().length() > 0) {
                        if (where == null) {
                            where = "where ";
                        } else {
                            where = where.concat(" AND ");
                        }
                        where = where.concat(" (").concat(getAditionalFilter().trim()).concat(") ");
                    }

                    if (where == null) {
                        where = "where ";
                    } else {
                        where = where.concat(" AND");
                    }
                    where = where.concat(" o.clientFeature.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.clientFeature.feature.showOnMenuChr = true ");
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
                    count = getFacade().count(where);
                    return count;
                }

                @Override
                public DataModel createPageDataModel() {
                    String where = null;
                    if (!getFilterSelectedField().equals("-1") && getFilterCriteria().length() > 0) {
                        Class<?> fieldClass = getFieldType(getFilterSelectedField());

                        if (fieldClass.equals(Integer.class) || fieldClass.equals(Long.class) || fieldClass.equals(BigInteger.class)) {
                            where = "where o.".concat(getFilterSelectedField()).concat(" = ").concat(getFilterCriteria());
                        } else if (fieldClass.equals(String.class)) {
                            where = "where lower(o.".concat(getFilterSelectedField()).concat(") LIKE '%").concat(getFilterCriteria().toLowerCase()).concat("%'");
                        }
                    }
                    if (getAditionalFilter() != null && getAditionalFilter().trim().length() > 0) {
                        if (where == null) {
                            where = "where ";
                        } else {
                            where = where.concat(" AND");
                        }
                        where = where.concat(" (").concat(getAditionalFilter().trim()).concat(") ");
                    }

                    if (where == null) {
                        where = "where ";
                    } else {
                        where = where.concat(" AND");
                    }
                    where = where.concat(" o.clientFeature.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
                    where = where.concat(" AND o.clientFeature.feature.showOnMenuChr = true ");
                    
                    String orderby = "o.".concat(getSortHelper().getField()).concat(getSortHelper().isAscending() ? " ASC" : " DESC");

                    return new ListDataModelViewCsTigo(getFacade().findRange(new int[] { getPageFirstItem(), getPageFirstItem() + getPageSize() }, where, orderby));
                }
            };
            if (getLastActualPage() >= 0) {
                paginationHelper.setActualPage(getLastActualPage());
                setLastActualPage(-1);
            }
        }

        return paginationHelper;
    }
    
    @Override
    public void applyQuantity(ValueChangeEvent evnt) {
        paginationHelper = null; // For pagination recreation
        setDataModel(null); // For data model recreation
        setSelectedItems(null); // For clearing selection
    }    

    public boolean getEditing() {
        return editing;
    }

	public void setEditing(boolean editing) {
		this.editing = editing;
	}
	
	
	//CAMBIO VIVI
	/*Metodo llamado al apretar el boton Agregar Bifurcacion*/
	 public String newBifurcation() {
	        boolean datasValidated = true;
	        
	        if (selectedNodes == null){
	            setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectOneTreeNode"));
	            datasValidated = false;
	            RequestContext context = RequestContext.getCurrentInstance();  
	            context.addCallbackParam("datasValidated", datasValidated); 
	            return null;
	        }
	        
	        /*verifica que pueda agregarse bifurcacion al arbol*/
	        if (onlyDescriptionEdition){
	        	setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddBifurcation"));
	            datasValidated = false;
	            RequestContext context = RequestContext.getCurrentInstance();  
	            context.addCallbackParam("datasValidated", datasValidated); 
	            return null;
	        }	        
	        
	        FeatureElementEntry currentEntry = (FeatureElementEntry) selectedNodes.getData();
	        /*En caso que no sea el titulo principal al cual siempre se le puede agregar BIFURCACION
	         * y el nodo seleccionado no sea de tipo ITEM LIST al cual tambien se le puede agregar BIFURCACION*/
	        if(!selectedNodes.getParent().equals(root) && !currentEntry.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(ITEM_LIST)) {
	        	setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddBifurcation"));
                datasValidated = false;
                RequestContext context = RequestContext.getCurrentInstance();  
                context.addCallbackParam("datasValidated", datasValidated); 
                return null;
	        }
	        
	        /*En caso que el seleccionado sea itemlist pero su padre sea option, no se puede agregar bifurcacion*/
	        if(!selectedNodes.getParent().equals(root) && currentEntry.getCodOwnerEntry().getCodFeatureEntryType().getNameChr().equalsIgnoreCase(OPTION)) {
	        	setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddBifurcation"));
                datasValidated = false;
                RequestContext context = RequestContext.getCurrentInstance();  
                context.addCallbackParam("datasValidated", datasValidated); 
                return null;
	        }
	        
	        
	        /*Si el nodo seleccionado tiene hijos*/
	        if(currentEntry.getFeatureElementEntries() != null) {
	        	/*Recorremos los hijos del nodo seleccionado para verificar si ya no tiene alguna bifurcacion,
	        	 * en este caso ya no se le podra agregar hijos*/
	        		for(FeatureElementEntry fe : currentEntry.getFeatureElementEntries()) {
	        			if(fe.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(BIFURCATION)) {
	        				setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.CanNotAddBifurcation"));
	        		        datasValidated = false;
	        		        RequestContext context = RequestContext.getCurrentInstance();  
	        		        context.addCallbackParam("datasValidated", datasValidated); 
	        		        return null;
	        			}
	        		}
	        	
	        }
	        
	        clearElementEntryData();
	        currentFeatElemEntry = null;
	        currentFeatElemEntry = new FeatureElementEntry();
	        selectedTypeEntry = BIFURCATION;
	        bifurcation = true;
	        RequestContext context = RequestContext.getCurrentInstance();  
	        context.addCallbackParam("datasValidated", datasValidated);
	        return null;
	    }
	 
	 /*Metodo invocado al guardar una nueva bifurcacion*/
	 public String saveBifurcationElementEntry() {
		boolean datasValidated = true;
        /*El titulo/codigo no puede ser vacio*/
        if (currentFeatElemEntry.getTitleChr() == null || currentFeatElemEntry.getTitleChr().equals("")){
           setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectTitle"));
           datasValidated = false;
           RequestContext context = RequestContext.getCurrentInstance();  
           context.addCallbackParam("datasValidated", datasValidated); 
           return null;
        }
       
        /*La descripcion no puede ser vacio*/
        if (currentFeatElemEntry.getDescriptionChr() == null || currentFeatElemEntry.getDescriptionChr().equals("")){
           setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectDescription"));
           datasValidated = false;
           RequestContext context = RequestContext.getCurrentInstance();  
           context.addCallbackParam("datasValidated", datasValidated); 
           return null;
        }
       
        /*El tamaño del titulo no puede ser mayor a 50 caracteres*/
        if (currentFeatElemEntry.getTitleChr() != null && currentFeatElemEntry.getTitleChr().length() > 50){
           setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.TitleSizeValidation"));
           datasValidated = false;
           RequestContext context = RequestContext.getCurrentInstance();  
           context.addCallbackParam("datasValidated", datasValidated); 
           return null;
        }

        /*Debe tener por lo menos una opcion completada*/
        if ((option1 == null || option1.equals("")) 
               && (option2 == null || option2.equals("")) 
               && (option3 == null || option3.equals("")) 
               && (option4 == null || option4.equals("")) 
               && (option5 == null || option5.equals(""))){        
           datasValidated = false;
           RequestContext context = RequestContext.getCurrentInstance();  
           context.addCallbackParam("datasValidated", datasValidated); 
           setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.MustSelectAtLeastOneOption"));
           return null;
        }   
       
        /*El tamaño de las opciones no puede ser mayor a 50 caracteres*/
        if ((option1 != null && !option1.equals("") && option1.length() > 50) 
               || (option2 != null && !option1.equals("") && option2.length() > 50) 
               || (option3 != null && !option1.equals("") && option3.length() > 50) 
               || (option4 != null && !option1.equals("") && option4.length() > 50) 
               || (option5 != null && !option1.equals("") && option5.length() > 50)){        
           datasValidated = false;
           RequestContext context = RequestContext.getCurrentInstance();  
           context.addCallbackParam("datasValidated", datasValidated); 
           setWarnMessage(i18n.iValue("web.client.screen.featureelement.message.OptionSizeValidation"));
           return null;
        } 
       
       
        if (elementEntryList == null)
           elementEntryList = new ArrayList<FeatureElementEntry>();
       
        // SE TRAE Y SETEA EL DATACHECK DE GLOBAL PARAMETER
        BigDecimal dataCkeckCod;
        try {
           dataCkeckCod = new BigDecimal(globalParameterFacade.findByCode("feature.datacheck.code"));
        } catch (GenericFacadeException e) {
           notifier.logError(CrudFeatureElementsBean.class, "SaveElementEntry", "Verifique que exista un Parametro Global que apunte al datackeck que almacena datos alfanumericos.");
           return null;
        }
        DataCheck dataCheck = featureElementFacade.getDataCheckAlphanumeric(dataCkeckCod);        
        currentFeatElemEntry.setDataCheck(dataCheck);
       
		// SE TRAE EL TYPE ENTRY Y SE SETEA
		FeatureEntryType type = null;
		type = featureElementFacade.getFeatureEntryType(BIFURCATION);
		currentFeatElemEntry.setCodFeatureEntryType(type);
       
		List<String> optionList = new ArrayList<String>();
		if (option1 != null && !option1.equals(""))
			optionList.add(option1);
		if (option2 != null && !option2.equals(""))
			optionList.add(option2);
		if (option3 != null && !option3.equals(""))
			optionList.add(option3);
		if (option4 != null && !option4.equals(""))
			optionList.add(option4);
		if (option5 != null && !option5.equals(""))
			optionList.add(option5);

		List<FeatureElementEntry> entries = new ArrayList<FeatureElementEntry>();
		FeatureEntryType itemType = featureElementFacade
				.getFeatureEntryType(ITEM_LIST);

		/* Obtenemos el nodo al cual se le esta agregando una bifurcacion */
		FeatureElementEntry fe = (FeatureElementEntry) selectedNodes.getData();
		/*
		 * Al que se esta creando le seteamos como padre el nodo seleccionado y
		 * un id temporal
		 */
		currentFeatElemEntry.setCodOwnerEntry(fe);
		currentFeatElemEntry.setIdTransient(++idTransient);

		for (int i = 0; i < optionList.size(); i++) {
			String option = optionList.get(i);
			FeatureElementEntry optionEntry = new FeatureElementEntry();
			optionEntry.setTitleChr(option);
			optionEntry.setDescriptionChr(option);
			optionEntry.setDataCheck(dataCheck);
			optionEntry.setOrderNum(i + 1);
			optionEntry.setCodFeatureEntryType(itemType);
			optionEntry.setCodOwnerEntry(currentFeatElemEntry);
			optionEntry.setIdTransient(++idTransient);
			entries.add(optionEntry);
			getFeaturesElementEntriesMap().put(optionEntry.getIdTransient(),
					optionEntry);
			elementEntryList.add(optionEntry);
		}
		currentFeatElemEntry.setFeatureElementEntries(entries);
           
           
           /*Recuperamos del hashmap el nodo seleccionado y le agregamos un nuevo hijo, luego lo borramos del hashmap
            * y lo volvemos a insertar ya con los cambios realizados*/
//			FeatureElementEntry f = getFeaturesElementEntries().get(fe.getIdTransient());
			
		if (fe.getFeatureElementEntries() != null) {
			getFeaturesElementEntriesMap().get(fe.getIdTransient())
					.getFeatureElementEntries().add(currentFeatElemEntry);
			getFeaturesElementEntriesMap()
					.put(currentFeatElemEntry.getIdTransient(),
							currentFeatElemEntry);
		} else {
			List<FeatureElementEntry> list = new ArrayList<FeatureElementEntry>();
			list.add(currentFeatElemEntry);
			getFeaturesElementEntriesMap().get(fe.getIdTransient())
					.setFeatureElementEntries(list);
			getFeaturesElementEntriesMap()
					.put(currentFeatElemEntry.getIdTransient(),
							currentFeatElemEntry);
		}

		elementEntryList.add(currentFeatElemEntry);
		getFeaturesElementEntriesMap();
       // SE ARMA EL ARBOL
       
       /*En caso de ser una nueva bifurcacion*/
       if (bifurcation){
           Integer lastNumberOrder = selectedNodes.getChildren().size();
           currentFeatElemEntry.setOrderNum(lastNumberOrder+1);
           TreeNode child = new DefaultTreeNode(currentFeatElemEntry, selectedNodes);
           //child.setParent(selectedNodes);
           child.setExpanded(true);
           selectedNodes.setExpanded(true);
           if (currentFeatElemEntry.getFeatureElementEntries() != null && !currentFeatElemEntry.getCodFeatureEntryType().getNameChr().equals(OUTPUT)){
               for (FeatureElementEntry ele : currentFeatElemEntry.getFeatureElementEntries()) {
                   TreeNode option = new DefaultTreeNode(ele, child);
                   //option.setParent(child);
               }     
           }
               
           selectedNodes.setExpanded(true);
       }
       
       // SE LIMPIAN LAS VARIABLES
       clearElementEntryData();
       
       // SE ENVIA PARAMETRO DE VALIDACION
       RequestContext context = RequestContext.getCurrentInstance();  
       context.addCallbackParam("datasValidated", datasValidated); 
       getFeaturesElementEntriesMap();
       return null;
   }
	 
	 
	 
	 /*Metodo invocado al seleccionar un nodo*/
	 public void onNodeSelect(NodeSelectEvent event) {
		nodeBifurcate = false;
	    FeatureElementEntry fe = (FeatureElementEntry) event.getTreeNode().getData();
		 if(fe.getCodFeatureEntryType().getNameChr().equals(BIFURCATION)) {
			 nodeBifurcate = true;
		 }
	 }
	 
	public boolean isEditItemList() {
		return editItemList;
	}

	public void setEditItemList(boolean editItemList) {
		this.editItemList = editItemList;
	}

	public boolean isNodeBifurcate() {
		return nodeBifurcate;
	}

	public void setNodeBifurcate(boolean nodeBifurcate) {
		this.nodeBifurcate = nodeBifurcate;
	}

	public boolean isBifurcation() {
		return bifurcation;
	}

	public void setBifurcation(boolean bifurcation) {
		this.bifurcation = bifurcation;
	}

	public Map<Long, FeatureElementEntry> getFeaturesElementEntriesMap() {
		 if (featuresElementEntriesMap == null) {
			 featuresElementEntriesMap = new HashMap<Long, FeatureElementEntry>();
	        }
		return featuresElementEntriesMap;
	}

	public void setFeaturesElementEntriesMap(Map<Long, FeatureElementEntry> featuresElementEntries) {
		this.featuresElementEntriesMap = featuresElementEntries;
	}
	
	public FeatureElementEntry getRootNode() {
		for (Map.Entry<Long, FeatureElementEntry> entry : getFeaturesElementEntriesMap().entrySet()) {
		    FeatureElementEntry value = entry.getValue();
	        if (value.getCodOwnerEntry() == null)
	        	return value;
		    
		}
	    return null;
//		return getFeaturesElementEntriesMap().get(0);
	}
	
	
	 // Lo que se cambio para el Primefaces3.2
    private void createFeatureElementEntryTree() {
        try {
        	//Recuperamos el FeatureElementEntry raiz para el FeatureElement que se esta editando
            FeatureElementEntry raiz = featureElementEntryFacade.getRootFeatureElementEntryEager(getEntity());
            root = new DefaultTreeNode("root", null);

            /*Creamos nuestro tree node pasandole como parametro la raiz y la lista de featureElementEntry del featureElement
             * que se esta editando*/
            raiz.setIdTransient(raiz.getFeatureElementEntryCod());
            TreeNode arbol = createTree(raiz,getFeatureElementEntriesList());
            arbol.setExpanded(true);
            arbol.setParent(root);
            // root.getChildren().add(arbol);

            featureElementEntriesList = null;

        } catch (Exception ex) {
            Logger.getLogger(CrudUserphoneBean.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

    }
    

    private TreeNode createTree(FeatureElementEntry raiz, Collection<FeatureElementEntry> lista) {
        TreeNode node = new DefaultTreeNode(raiz, null);
        TreeNode child = null;

        List<FeatureElementEntry> hijos = getChildren(raiz, lista);
        raiz.setFeatureElementEntries(hijos);

        for (FeatureElementEntry hijo : hijos) {
        	if (hijo.getFeatureElementEntries() != null && hijo.getFeatureElementEntries().size() > 0){
        		if (editing || (!hijo.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(OPTION) && !editing)){
        			child = createTree(hijo, lista);
                    child.setParent(node);
        		}else{
//        			hijo.setFeatureElementEntries(null);
        			child = new DefaultTreeNode(hijo, node);
                    //child.setParent(node);
        		}
        			
//            if (hijo.getFeatureElementEntries() != null && (!hijo.getCodFeatureEntryType().getNameChr().equalsIgnoreCase(OPTION)) && !editing) {
//                child = createTree(hijo, lista);
//                child.setParent(node);
            } else {
//            	hijo.setFeatureElementEntries(null);
                child = new DefaultTreeNode(hijo, node);
                //child.setParent(node);
            }
            // node.addChild(child);

        }
        return node;
    }

    private List<FeatureElementEntry> getChildren(FeatureElementEntry nodo, Collection<FeatureElementEntry> lista) {
        for (FeatureElementEntry fe : lista) {
            if (nodo.getFeatureElementEntryCod() != null
                && fe.getFeatureElementEntryCod() != null
                && nodo.getFeatureElementEntryCod().equals(
                		fe.getFeatureElementEntryCod())) {
                return fe.getFeatureElementEntries();
            }
        }
        return null;
    }

    // fin - Lo que se cambio para Primefaces 3.2
    

    /*En caso que el featureElementEntriesList sea nulo, recuperamos por EAGER toda la lista de FeatureElementEntry 
     para el FeatureElement que se esta editando*/
    public List<FeatureElementEntry> getFeatureElementEntriesList() {
    	if(featureElementEntriesList == null) {
    		featureElementEntriesList = featureElementEntryFacade.getEagerFeatureElementEntryList(getEntity());
    		for (FeatureElementEntry fee : featureElementEntriesList) {
				fee.setIdTransient(fee.getFeatureElementEntryCod());
			}
    	}
		return featureElementEntriesList;
	}

	public void setFeatureElementEntriesList(
			List<FeatureElementEntry> featureElementEntriesList) {
		this.featureElementEntriesList = featureElementEntriesList;
	}

	public boolean isNewChildBifurcation() {
		return newChildBifurcation;
	}

	public void setNewChildBifurcation(boolean newChildBifurcation) {
		this.newChildBifurcation = newChildBifurcation;
	}
	
	public void handleClose(CloseEvent event) {  
//		selectedNodes = null;
        bifurcation = false;
    }

}
