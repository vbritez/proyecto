package com.tigo.cs.view.administrative;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientServiceFunctionality;
import com.tigo.cs.domain.Functionality;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ServiceFunctionality;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.UserphoneMeta;
import com.tigo.cs.domain.UserphoneMetaPK;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.ClientFacade;
import com.tigo.cs.facade.ClientServiceFunctionalityFacade;
import com.tigo.cs.facade.MetaClientFacade;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.facade.UserphoneMetaFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

/**
 * 
 * @author Miguel Zorrilla
 * @version $Id$
 */
@ManagedBean(name = "crudUserphoneBean")
@ViewScoped
public class CrudUserphoneBean extends AbstractCrudBeanClient<Userphone, UserphoneFacade> implements Serializable {

    private static final long serialVersionUID = -7313026512028202026L;
    @EJB
    private ClientFacade clientFacade;
    @EJB
    private ClassificationFacade classificationFacade;
    @EJB
    private UserphoneFacade userphoneFacade;
    @EJB
    private ClientServiceFunctionalityFacade csfFacade;
    private Client client;
    private List<ServiceFunctionality> serviceFunctionalityList;
    private List<ServiceFunctionality> selectedServiceFunctionalityList;
    private List<Classification> classificationList;
    private List<Service> serviceList;
    private Map<Long, Boolean> selectedServiceMap;
    private Map<Long, List<Functionality>> functionalityMap;
    private Map<Long, Object[]> selectedFunctionalityMap;
    private TreeNode root;
    private TreeNode[] selectedNodes;
    private List<TreeNode> selectednodesList;
    private String hasChangeName = "false";
    private String saveHistory;
    private Boolean treeChecked = false;
    
    private List<MetaClient> metaClientList;
    private Map<Long, Boolean> selectedMetaClientMap;
    private Map<Long, UserphoneMeta> selectedUserphoneMetaMap;
    private Map<Long, Boolean> disabledMap; // hasmap para habilitar los campos crear,eliminar,actualizar dependiendo si el meta esta seleccionado o no
    @EJB
    private MetaClientFacade metaClientFacade;
    @EJB private UserphoneMetaFacade userphoneMetaFacade;
    private Map<Long, Boolean> disabledServiceMap; 
    private Boolean supervisor;

    public CrudUserphoneBean() {
        super(Userphone.class, UserphoneFacade.class);
        setAditionalFilter("o.enabledChr = true");
    }

    public static CrudUserphoneBean getInstance() {
        return (CrudUserphoneBean) getBean("crudUserphoneBean");
    }
    
    public Boolean getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(Boolean supervisor) {
		this.supervisor = supervisor;
	}

	public String onNameChange() {
        hasChangeName = "true";
        return null;
    }

    public String onSavehistoryChange() {
        saveHistory = "";
        return null;
    }

    @Override
    public String newEntity() {
        String retVal = super.newEntity();
        if (client != null && getEntity() != null) {
            getEntity().setClient(client);
        }
        return retVal;
    }

    @Override
    public String editEntity() {
        String retVal = super.editEntity("clientServiceFunctionalityList","trackingPeriods","clientGoals");

        /*
         * traemos las funcionalidades y los servicios
         * habilitados para el cliente al cual corresponde el userphone
         */
        if (getEntity() != null) {
            for (ServiceFunctionality sf : clientFacade.getServiceFunctionalitiesByClient(getEntity().getClient().getClientCod())) {
             
                /*
                 * agregamos las funcionalidades que el cliente tiene
                 * disponibles
                 */
                Functionality functionality = sf.getFunctionality();
                if (functionality.getFunctionalityCod() == 6L) {
                	   /*
                     * agregamos los servicios a la lista de servicios, unicamente los que tienen la funcionalidad
                     * de eliminacion de marcacion
                     */
                    Service service = sf.getService();
                    if (!serviceList.contains(service)) {
                        serviceList.add(service);
                    }
                    getDisabledServiceMap().put(service.getServiceCod(), true);
                    if (!getFunctionalityMap().containsKey(
                            service.getServiceCod())
                        || getFunctionalityMap().get(service.getServiceCod()) == null) {
                        getFunctionalityMap().put(service.getServiceCod(),
                                new ArrayList<Functionality>());
                    }
                    ArrayList<Functionality> auxList = (ArrayList<Functionality>) (getFunctionalityMap().get(service.getServiceCod()));
                    auxList.add(functionality);
                    getFunctionalityMap().put(service.getServiceCod(), auxList);
                }                
            }
        
            // TRAER LA LISTA DE SERVICEFUNCIONALITIES DEL USERPHONE SELECCIONADO
            for (ServiceFunctionality sf : getFacade().getServiceFunctionalitiesByUserphone(getEntity().getUserphoneCod())) {
                Service service = sf.getService();
                Functionality functionality = sf.getFunctionality();
                if(functionality.getFunctionalityCod() == 6L) {
	                if (!getSelectedServiceMap().containsKey(
	                        service.getServiceCod())
	                    || getSelectedServiceMap().get(service.getServiceCod()) == null) {
	                    getSelectedServiceMap().put(service.getServiceCod(),
	                            Boolean.TRUE);
	                }
	                
	                if (!getSelectedFunctionalityMap().containsKey(
	                        service.getServiceCod())
	                    || getSelectedFunctionalityMap().get(
	                            service.getServiceCod()) == null) {
	                    getSelectedFunctionalityMap().put(service.getServiceCod(),
	                            new Object[0]);
	                }
	                ArrayList auxList = new ArrayList(Arrays.asList(getSelectedFunctionalityMap().get(
	                        service.getServiceCod())));
	                	auxList.add(functionality);
	                	getSelectedFunctionalityMap().put(service.getServiceCod(),
	                        auxList.toArray());
	                	 getDisabledServiceMap().put(service.getServiceCod(), false);
                }
                
                if (service.getServiceCod().longValue() == 25L) {
                	supervisor = true;
                }

            }

            // CREAR EL ARBOL DE CLASIFICACIONES Y TRAER LAS CLASIFICACIONES
            // SELECCIONADAS
            createClassificationTree();

            List<Classification> classifListSelected = null;
            classifListSelected = getFacade().getClassificationsByUserphone(getEntity().getUserphoneCod());
            if (classifListSelected != null && classifListSelected.size() > 0) {
                setSelectednodesList(null);
                getSelectednodesList();
                selectClassificationInTree(root, classifListSelected);
            }
            
            hasChangeName = getEntity().getNameChr();
            
            List<UserphoneMeta> list = userphoneFacade.findUserphoneMeta(SecurityBean.getInstance().getLoggedInUserClient().getClient(), getEntity());
            /*Recorremos la lista de userphoneMeta y seteamos los metas asociados al userphone */
            	for(UserphoneMeta um : list ) {
            		getSelectedMetaClientMap().put(um.getMeta().getMetaCod(), true);
            		getSelectedUserphoneMetaMap().put(um.getMeta().getMetaCod(), um);
            		getDisabledMap().put(um.getMeta().getMetaCod(), false);
            	}
  
            	
            /*Recorremos la lista de userphoneMeta y seteamos los metas que no estan asociados al userphone y en su value UserphoneMeta le seteamos una
                * instancia vacia para que no de un error NullPointer y tambien seteamos el hashmap disabledMap para
                * que los campos crear,actualizar y eliminar aparezcan como deshabilitados*/
            for(MetaClient cm : getMetaClientList()) {
                if(!selectedMetaClientMap.containsKey(cm.getMeta().getMetaCod())){
                	getSelectedMetaClientMap().put(cm.getMeta().getMetaCod(), false);
                	getSelectedUserphoneMetaMap().put(cm.getMeta().getMetaCod(), new UserphoneMeta());
                	getDisabledMap().put(cm.getMeta().getMetaCod(), true);
                }
            }
        }
        return retVal;
    }

    public void onTreeNodeClicked(NodeSelectEvent e) {
        setTreeChecked(true);
    }

    private void selectClassificationInTree(TreeNode raiz, List<Classification> selectedlist) {
        List<TreeNode> children = raiz.getChildren();

        for (Classification c : selectedlist) {
            if (raiz.getData() != null && raiz.getData().equals(c)) {
                raiz.setSelected(true);
                getSelectednodesList().add(raiz);
            }
        }

        for (TreeNode hijo : children) {
            selectClassificationInTree(hijo, selectedlist);
        }
    }

    @Override
    public String saveEditing() {
    	
    	List<ClientServiceFunctionality> csfList = new ArrayList<ClientServiceFunctionality>();
    	List<ServiceFunctionality> sfList =  getFacade().getServiceFunctionalitiesByUserphone(
                getEntity().getUserphoneCod());
                
        for (Service sf : getServiceList()) {
            if (getSelectedServiceMap().containsKey(sf.getServiceCod())
                && getSelectedServiceMap().get(sf.getServiceCod())) {
            	
                for (Object obj : getSelectedFunctionalityMap().get(
                        sf.getServiceCod())) {
                    Functionality function = (Functionality) obj;
                    ClientServiceFunctionality csf = csfFacade.getClientServiceFunctionality(
                            getClient().getClientCod(), sf.getServiceCod(),
                            function.getFunctionalityCod());
                    csfList.add(csf);
                }
            }

        }
        
        for(ServiceFunctionality sf : sfList) {
        	if(sf.getServiceFunctionalityPK().getCodFunctionality() != 6L) {
        		ClientServiceFunctionality csf = csfFacade.getClientServiceFunctionality(
                        getClient().getClientCod(), sf.getServiceFunctionalityPK().getCodService(),
                        sf.getServiceFunctionalityPK().getCodFunctionality());
        		csfList.add(csf);
        	}
        }

        getEntity().setClientServiceFunctionalityList(csfList);
    	
        // SAVE CLASSIFICATION
    	List<Classification> classifList = new ArrayList<Classification>();
        selectednodesList = Arrays.asList(selectedNodes);
        
         if (getSelectednodesList() != null && getSelectednodesList().size() > 0) {
            for (TreeNode nodo : getSelectednodesList()) {
                Classification actualClassif = (Classification) nodo.getData();
                if (actualClassif != null) {
                    Classification classif = classificationFacade.find(actualClassif.getClassificationCod());
                    classifList.add(classif);
                }
            }
        }

        // csfList.addAll(csfListBasics);
        // getEntity().setClientServiceFunctionalityList(csfList);
        getEntity().setClassificationList(classifList);  
        
      //SAVE USERPHONEMETA
        /*Recorremos la lista de userphoneMeta verificamos cuales se deben eliminar y cuales insertar o editarlos */
    	for(MetaClient mc : getMetaClientList()) {
    		UserphoneMeta um = userphoneMetaFacade.getUserphoneMeta(mc.getMeta(), getEntity());
    		if(selectedMetaClientMap.get(mc.getMeta().getMetaCod())) {
    			if(um != null) {
    			    UserphoneMeta u = selectedUserphoneMetaMap.get(mc.getMeta().getMetaCod());
                    u.setCreateChr(u.getCreateChr() == null ? false : u.getCreateChr());
                    u.setUpdateChr(u.getUpdateChr() == null ? false : u.getUpdateChr());
                    u.setDeleteChr(u.getDeleteChr() == null ? false : u.getDeleteChr());
    				userphoneMetaFacade.edit(u);
    			}else{
    				UserphoneMetaPK pk = new UserphoneMetaPK();
    				pk.setCodMeta(mc.getMeta().getMetaCod());
    				pk.setCodUserphone(getEntity().getUserphoneCod());
    				UserphoneMeta u = selectedUserphoneMetaMap.get(mc.getMeta().getMetaCod());
    				u.setCreateChr(u.getCreateChr() == null ? false : u.getCreateChr());
    				u.setUpdateChr(u.getUpdateChr() == null ? false : u.getUpdateChr());
    				u.setDeleteChr(u.getDeleteChr() == null ? false : u.getDeleteChr());
    				u.setId(pk);
    				u.setMeta(mc.getMeta());
    				u.setUserphone(getEntity());
    				userphoneMetaFacade.create(u);
    			}
    		}else{
    			if(um != null) {
    				userphoneMetaFacade.remove(um);
    			}
    		}
    	}
    	
    	boolean alreadyHasService = false;
    	if (supervisor){
    		List<ClientServiceFunctionality> list = userphoneFacade.getClientServiceFunctionalitiesByUserphone(getEntity().getUserphoneCod());
    		if (list != null){
	    		for (ClientServiceFunctionality csf : list) {
					if (csf.getClientServiceFunctionalityPK().getCodService() == 25L){
						alreadyHasService = true;
						break;
					}
				}
	    		if (!alreadyHasService){
	    			List<ClientServiceFunctionality> l0 = clientFacade.getClientServiceFunctionalitiesByClientService(getClient().getClientCod(), 25L);
	    			if (l0 != null){
	    				for (ClientServiceFunctionality sf : l0) {
	    					ClientServiceFunctionality csf = csfFacade.getClientServiceFunctionality(
	    	                        getClient().getClientCod(), sf.getClientServiceFunctionalityPK().getCodService(),
	    	                        sf.getClientServiceFunctionalityPK().getCodFunctionality());
	    					list.add(csf);
						}
	    			}
//	    			ClientServiceFunctionality csf0 = csfFacade.getClientServiceFunctionality(getClient().getClientCod(), 25L, 0L);
//	    			if (csf0 != null)
//	    				list.add(csf0);
//	    			ClientServiceFunctionality csf5 = csfFacade.getClientServiceFunctionality(getClient().getClientCod(), 25L, 5L);
//	    			if (csf5 != null)
//	    				list.add(csf5);
	    			
	    			getEntity().setClientServiceFunctionalityList(list);
	    		}
    		}
    	}else{
    		List<ClientServiceFunctionality> list = userphoneFacade.getClientServiceFunctionalitiesByUserphone(getEntity().getUserphoneCod());
    		if (list != null){
	    		for (ClientServiceFunctionality csf : list) {
					if (csf.getClientServiceFunctionalityPK().getCodService() == 25L && 
							(csf.getClientServiceFunctionalityPK().getCodFunctionality() == 5 || csf.getClientServiceFunctionalityPK().getCodFunctionality() == 0
									|| csf.getClientServiceFunctionalityPK().getCodFunctionality() == 1)){
						getEntity().getClientServiceFunctionalityList().remove(csf);
					}
				}
    		}
    	}

        String retVal = null;
        try {
            retVal = userphoneFacade.saveEdit(getEntity(), getSaveHistory());
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
            setEntity(null);
        } catch (GenericFacadeException e) {
            setErrorMessage(i18n.iValue("web.admin.backingBean.commons.message.AnErrorHasOcurred"));
            setEntity(null);
        }
        
        
        setSupervisor(false);
        setLastActualPage(getPaginationHelper().getActualPage());
        setPaginationHelper(null); // For pagination recreation
        setDataModel(null); // For data model recreation
        setSelectedItems(null); // For clearing selection

        setHasChangeName("false");
        setTreeChecked(false);
        setSaveHistory("false");
        setSelectedMetaClientMap(null);
        setSelectedUserphoneMetaMap(null);
        setDisabledMap(null);
//        setFilterSelectedField("-1");
//        setFilterCriteria("");
        setSelectedServiceFunctionalityList(null);
        setSelectedServiceMap(null);
        setSelectedFunctionalityMap(null);
        setFunctionalityMap(null);
        setDisabledServiceMap(null);
        return retVal;
    }

    @Override
    public String cancelEditing() {
        setHasChangeName("false");
        setTreeChecked(false);
        setSaveHistory("false");
        setSelectedServiceFunctionalityList(null);
        setSelectedServiceMap(null);
        setSelectedFunctionalityMap(null);
        setFunctionalityMap(null);
        setDisabledMap(null);
        setSelectedMetaClientMap(null);
        setSelectedUserphoneMetaMap(null);
        setDisabledServiceMap(null);
        return super.cancelEditing();
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

    public List<ServiceFunctionality> getSelectedServiceFunctionalityList() {
        return selectedServiceFunctionalityList;
    }

    public void setSelectedServiceFunctionalityList(List<ServiceFunctionality> selectedServiceFunctionalityList) {
        this.selectedServiceFunctionalityList = selectedServiceFunctionalityList;
    }

    public List<ServiceFunctionality> getServiceFunctionalityList() {
        if (serviceFunctionalityList == null && client != null) {
            serviceFunctionalityList = new ArrayList<ServiceFunctionality>(
                    clientFacade.getServiceFunctionalitiesByClient(getClient().getClientCod()));
        }
        return serviceFunctionalityList;
    }

    public void setServiceFunctionalityList(List<ServiceFunctionality> serviceFunctionalityList) {
        this.serviceFunctionalityList = serviceFunctionalityList;
    }

    public List<Classification> getClassificationList() {
        if (classificationList == null) {
            // classificationList = classificationFacade.findAll();
            classificationList = classificationFacade.getEagerClassificationListByClient(getClient());
        }
        return classificationList;
    }

    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    @Override
    public boolean showAllInQuantityList() {
        return true;
    }

    @Override
    public String getReportWhereCriteria() {
        return null;
    }

    public Map<Long, List<Functionality>> getFunctionalityMap() {
        if (functionalityMap == null) {
            functionalityMap = new HashMap<Long, List<Functionality>>();
        }
        return functionalityMap;
    }

    public void setFunctionalityMap(Map<Long, List<Functionality>> functionalityMap) {
        this.functionalityMap = functionalityMap;
    }

    public Map<Long, Object[]> getSelectedFunctionalityMap() {
        if (selectedFunctionalityMap == null) {
            selectedFunctionalityMap = new HashMap<Long, Object[]>();
        }
        return selectedFunctionalityMap;
    }

    public void setSelectedFunctionalityMap(Map<Long, Object[]> selectedFunctionalityMap) {
        this.selectedFunctionalityMap = selectedFunctionalityMap;
    }

    public Map<Long, Boolean> getSelectedServiceMap() {
        if (selectedServiceMap == null) {
            selectedServiceMap = new HashMap<Long, Boolean>();
        }
        return selectedServiceMap;
    }

    public void setSelectedServiceMap(Map<Long, Boolean> selectedServiceMap) {
        this.selectedServiceMap = selectedServiceMap;
    }

    public List<Service> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<Service>();
            serviceList.add(new Service(-1L));
        }
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public Boolean getTreeChecked() {
        return treeChecked;
    }

    public void setTreeChecked(Boolean treeChecked) {
        this.treeChecked = treeChecked;
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

    public String getHasChangeName() {
        return hasChangeName;
    }

    public void setHasChangeName(String hasChangeName) {
        this.hasChangeName = hasChangeName;
    }

    public String getSaveHistory() {
        return saveHistory;
    }

    public void setSaveHistory(String saveHistory) {
        this.saveHistory = saveHistory;
    }

    
    //Lo que se cambio para el Primefaces3.2
    private void createClassificationTree() {
        try {
            Classification raiz = classificationFacade.findRootByClient(getClient());
            classificationList = classificationFacade.getEagerClassificationListByClient(getClient());
            root = new DefaultTreeNode("root", null);

            TreeNode arbol = createTree(getRootFromClassificationList(raiz), getClassificationList());
            arbol.setExpanded(true);
            arbol.setParent(root);
            //root.getChildren().add(arbol);

            classificationList = null;

        } catch (GenericFacadeException ex) {
            Logger.getLogger(CrudUserphoneBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Classification getRootFromClassificationList(Classification raiz) {
        for (Classification nodo : getClassificationList()) {
            if (raiz.equals(nodo)) {
                return nodo;
            }
        }
        return null;
    }

    private TreeNode createTree(Classification raiz, Collection<Classification> lista) {
        TreeNode node = new DefaultTreeNode(raiz, null);
        TreeNode child = null;

        Collection<Classification> hijos = getChildren(raiz, lista);

        for (Classification hijo : hijos) {
            if (hijo.getClassificationList() != null) {
                child = createTree(hijo, lista);
                child.setParent(node); 
            } else {
                child = new DefaultTreeNode(hijo, node);
                child.setParent(node);
            }
            //node.addChild(child); 
            
        }
        return node;
    }

    private Collection<Classification> getChildren(Classification nodo, Collection<Classification> lista) {
        for (Classification classif : lista) {
            if (nodo.getClassificationCod() != null && classif.getClassificationCod() != null && nodo.getClassificationCod().equals(classif.getClassificationCod())) {
                return classif.getClassificationList();
            }
        }
        return null;
    }
    
    public List<MetaClient> getMetaClientList() {
        if (metaClientList == null){
        	metaClientList = metaClientFacade.getMetaClientList(SecurityBean.getInstance().getLoggedInUserClient().getClient());
        }
        return metaClientList;
    }
    
    public void setMetaClientList(List<MetaClient> metaClientList) {
    	this.metaClientList = metaClientList;
    }

	public Map<Long, Boolean> getSelectedMetaClientMap() {
		 if (selectedMetaClientMap == null) {
			 selectedMetaClientMap = new HashMap<Long, Boolean>();
	        }
		return selectedMetaClientMap;
	}

	public void setSelectedMetaClientMap(Map<Long, Boolean> selectedMetaClientMap) {
		this.selectedMetaClientMap = selectedMetaClientMap;
	}

	public Map<Long, UserphoneMeta> getSelectedUserphoneMetaMap() {
		 if (selectedUserphoneMetaMap == null) {
			 selectedUserphoneMetaMap = new HashMap<Long, UserphoneMeta>();
	        }
		return selectedUserphoneMetaMap;
	}

	public void setSelectedUserphoneMetaMap(
			Map<Long, UserphoneMeta> selectedUserphoneMetaMap) {
		this.selectedUserphoneMetaMap = selectedUserphoneMetaMap;
	}
	
	  public Map<Long, Boolean> getDisabledMap() {
	        if (disabledMap == null) {
	            disabledMap = new HashMap<Long, Boolean>();
	        }
	        return disabledMap;
	    }

	    public void setDisabledMap(Map<Long, Boolean> disabledMap) {
	        this.disabledMap = disabledMap;
	    }
    
	 
	    /*Metodo invocado por el listener del checkbox de los metas, de manera a que si se selecciona habilite los campos crear,actualizar y eliminar
	     * si se deselecciona los vuelva a deshabilitar*/
	    public void checkMetaListener() {
	      for(MetaClient mc: getMetaClientList()) {
	          if(selectedMetaClientMap.get(mc.getMeta().getMetaCod())) {
	        	  	UserphoneMeta um = userphoneMetaFacade.getUserphoneMeta(mc.getMeta(), getEntity());
	        	  	if(um == null) {
	        	  		um = selectedUserphoneMetaMap.get(mc.getMeta().getMetaCod());
	        	  	}
	              	selectedUserphoneMetaMap.put(mc.getMeta().getMetaCod(), um);
	                disabledMap.put(mc.getMeta().getMetaCod(), false);
	              
	          }else {
	        	  selectedUserphoneMetaMap.put(mc.getMeta().getMetaCod(), new UserphoneMeta());
	              disabledMap.put(mc.getMeta().getMetaCod(), true);
	          }
	      }     
	    }
	    
	    /*Metodo invocado por el listener del checkbox de los servicios de eliminacion de marcacion, 
	     * de manera a que si se selecciona un servicio habilite el campo de eliminar marcacion y
	     * si se deselecciona los vuelva a deshabilitar*/
	    public void checkServiceListener() {
	      for(Service s: getServiceList()) {
	    	  if(selectedServiceMap.get(s.getServiceCod()) != null) {
		    	  if(selectedServiceMap.get(s.getServiceCod())) {
		    		  	if(getSelectedFunctionalityMap().get(
		                          s.getServiceCod()) != null) {
		    		  		 ArrayList auxList = new ArrayList(Arrays.asList(getSelectedFunctionalityMap().get(
			                          s.getServiceCod())));
		    		  		getSelectedFunctionalityMap().put(s.getServiceCod(),
			                          auxList.toArray());
		    		  	}else {
		    		  		getSelectedFunctionalityMap().put(s.getServiceCod(),
		                              new Object[0]);
		    		  	}
		                 disabledServiceMap.put(s.getServiceCod(),false);
		    	  }else{
		    		  getSelectedFunctionalityMap().put(s.getServiceCod(),
                              new Object[0]);
		    		  disabledServiceMap.put(s.getServiceCod(),true);
		    	  }
	    	  }
	      }
	    }
	    
	    
	    public Map<Long, Boolean> getDisabledServiceMap() {
	        if (disabledServiceMap == null) {
	            disabledServiceMap = new HashMap<Long, Boolean>();
	        }
	        return disabledServiceMap;
	    }

	    public void setDisabledServiceMap(Map<Long, Boolean> disabledServiceMap) {
	        this.disabledServiceMap = disabledServiceMap;
	    }
    
}
