package com.tigo.cs.view.administrative;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.domain.ClientServiceFunctionality;
import com.tigo.cs.domain.Functionality;
import com.tigo.cs.domain.RoleClient;
import com.tigo.cs.domain.RoleClientScreen;
import com.tigo.cs.domain.Screenclient;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ServiceFunctionality;
import com.tigo.cs.facade.ClientFacade;
import com.tigo.cs.facade.ClientServiceFunctionalityFacade;
import com.tigo.cs.facade.RoleClientFacade;
import com.tigo.cs.facade.ScreenClientFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.SessionBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

/**
 * 
 * @author Tania Nuñez
 * @version $Id: CrudAdminRolesBean.java 233 2011-12-26 12:47:58Z miguel.maciel
 *          $
 */
@ManagedBean(name = "crudAdminRolesBean")
@ViewScoped
public class CrudAdminRolesBean extends AbstractCrudBeanClient<RoleClient, RoleClientFacade> implements Serializable {

    static final long serialVersionUID = 1819958394089004180L;
    @EJB
    private ScreenClientFacade screenClientFacade;
    @EJB
    private ClientFacade clientFacade;
    @EJB
    private ClientServiceFunctionalityFacade csfFacade;
    @EJB
    private RoleClientFacade roleclientFacade;
    private List<Screenclient> screenclientList;
    private Screenclient[] selectedScreenclientList;
    private List<ServiceFunctionality> serviceFunctionalityList;
    private ServiceFunctionality[] selectedServiceFuntionalityList;

    public CrudAdminRolesBean() {
        super(RoleClient.class, RoleClientFacade.class);
        setAditionalFilter("o.client.clientCod = ".concat(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod().toString()));
    }

    @Override
    public String getReportWhereCriteria() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String newEntity() {
        getScreenclientList(); // Force to construct list
        getServiceFunctionalityList();
        setSelectedScreenclientList(null);
        setSelectedServiceFuntionalityList(null);
        setSelectedItems(null);
        return super.newEntity();
    }

    @Override
    public String saveEditing() {
        try {
            // SAVE SELECTED SCREENCLIENT
            List<Screenclient> scList = Arrays.asList(getSelectedScreenclientList());
            List<RoleClientScreen> selectedRcs = new ArrayList<RoleClientScreen>();
            for (Screenclient screen : scList) {
                RoleClientScreen rcs = new RoleClientScreen();
                rcs.setScreenclient(screen);
                selectedRcs.add(rcs);
            }

            // SAVE SELECTED SERVICE-FUNTIONALITY
            List<ServiceFunctionality> sfList = Arrays.asList(getSelectedServiceFuntionalityList());
            List<ClientServiceFunctionality> selectedCsf = new ArrayList<ClientServiceFunctionality>();
            for (ServiceFunctionality sf : sfList) {
                Service service = sf.getService();
                Functionality functionality = sf.getFunctionality();
                ClientServiceFunctionality csf = csfFacade.getClientServiceFunctionality(
                        SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                        service.getServiceCod(),
                        functionality.getFunctionalityCod());
                selectedCsf.add(csf);
            }

            getEntity().setClient(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient());
            getEntity().setClientServiceFunctionalityList(selectedCsf);
            getEntity().setRoleClientScreenList(selectedRcs);
            String retVal = roleclientFacade.saveEdit(getEntity());
            // super.reset("web.client.backingBean.abstractCrudBean.message.SaveSuccess");
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.SaveSuccess"));
            setEntity(null);
            setLastActualPage(getPaginationHelper().getActualPage());
            setPaginationHelper(null); // For pagination recreation
            setDataModel(null); // For data model recreation
            setSelectedItems(null); // For clearing selection

            setSelectedScreenclientList(null);
            setSelectedServiceFuntionalityList(null);

            SessionBean.getInstance().setClientModules(null);

            return retVal;
        } catch (EJBException ex) {
            setErrorMessage(i18n.iValue("web.admin.backingBean.commons.message.AnErrorHasOcurred"));
        } catch (Exception ex) {
            setErrorMessage(i18n.iValue("web.admin.backingBean.commons.message.AnErrorHasOcurred"));
        }
        return null;
    }

    @Override
    public String editEntity() {
        // getScreenclientList(); //Force to construct list

        String retVal = super.editEntity();
        List<Screenclient> selectedScreenClient = new ArrayList<Screenclient>();
        if (getEntity() != null) {
            for (RoleClientScreen sf : getFacade().getRoleclientscreenListByRole(
                    getEntity().getRoleClientCod())) {
                // for (ServiceFunctionality sf :
                // getFacade().getAllServiceFunctionalities()) {
                Screenclient screen = sf.getScreenclient();
                selectedScreenClient.add(screen);
            }

            List<ServiceFunctionality> selectedServfunc = new ArrayList<ServiceFunctionality>();
            for (ClientServiceFunctionality csf : csfFacade.getServiceFunctionalitiesByRole(getEntity().getRoleClientCod())) {
                selectedServfunc.add(csf.getServiceFunctionality());
            }

            setSelectedServiceFuntionalityList(selectedServfunc.toArray(new ServiceFunctionality[0]));
            setSelectedScreenclientList(selectedScreenClient.toArray(new Screenclient[0]));
        }

        return retVal;
    }

    @Override
    public String deleteEntities() {
        Iterator<RoleClient> iteratorDataModel = getDataModel().iterator();
        List<RoleClient> selectedRoleclientList = new ArrayList<RoleClient>();
        for (; iteratorDataModel.hasNext();) {
            RoleClient currentEntity = iteratorDataModel.next();
            Object obj = getKeyValue(currentEntity);
            if (getSelectedItems().get(obj)) {
                selectedRoleclientList.add(currentEntity);
            }
        }

        if (selectedRoleclientList == null || selectedRoleclientList.isEmpty()) {
            setWarnMessage(i18n.iValue("web.client.backingBean.message.MustSelectAtLeastOneToDelete"));
            return null;
        }

        try {
            for (RoleClient rc : selectedRoleclientList) {

                List<RoleClientScreen> roleClientPersisted = roleclientFacade.findScreens(rc);
                if (roleClientPersisted != null
                    && roleClientPersisted.size() > 0) {
                    setErrorMessage(i18n.iValue("web.roleadmin.deleting.HaveScreensSelected"));
                    return null;
                }

                List<ClientServiceFunctionality> clientServiceFunctionalities = roleclientFacade.findFunctionalities(rc);
                if (clientServiceFunctionalities != null
                    && clientServiceFunctionalities.size() > 0) {
                    setErrorMessage(i18n.iValue("web.roleadmin.deleting.HaveFunctionalitiesSelected"));
                    return null;
                }

                roleclientFacade.deleteEditing(rc);
            }
            // super.reset("web.client.backingBean.abstractCrudBean.message.DeletingSucces");
            setSuccessMessage(i18n.iValue("web.client.backingBean.abstractCrudBean.message.DeletingSucces"));
            setEntity(null);
            setLastActualPage(getPaginationHelper().getActualPage());
            setPaginationHelper(null); // For pagination recreation
            setDataModel(null); // For data model recreation
            setSelectedItems(null); // For clearing selection

            setSelectedScreenclientList(null);
            setSelectedServiceFuntionalityList(null);
        } catch (EJBException ex) {
            setErrorMessage(i18n.iValue("web.client.backingBean.commons.message.AnErrorHasOcurred"));
        } catch (Exception ex) {
            setErrorMessage(i18n.iValue("web.client.backingBean.commons.message.AnErrorHasOcurred"));
        }

        return null;
    }

    @Override
    public String cancelEditing() {
        setSelectedScreenclientList(null);
        setSelectedServiceFuntionalityList(null);
        return super.cancelEditing();
    }

    public List<Screenclient> getScreenclientList() {
        if (screenclientList == null) {
            screenclientList = new ArrayList<Screenclient>();
            screenclientList.addAll(screenClientFacade.getScreenclientListByClientNoAdministrative(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod()));
        }
        return screenclientList;
    }

    public void setScreenclientList(List<Screenclient> screenclientList) {
        this.screenclientList = screenclientList;
    }

    public Screenclient[] getSelectedScreenclientList() {
        return selectedScreenclientList;
    }

    public void setSelectedScreenclientList(Screenclient[] selectedScreenclientList) {
        this.selectedScreenclientList = selectedScreenclientList;
    }

    public List<ServiceFunctionality> getServiceFunctionalityList() {
        if (serviceFunctionalityList == null) {
            serviceFunctionalityList = new ArrayList<ServiceFunctionality>(clientFacade.getServiceFunctionalitiesByClient(
                    SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod(),
                    1L));
        }
        return serviceFunctionalityList;
    }

    public void setServiceFunctionalityList(List<ServiceFunctionality> serviceFunctionalityList) {
        this.serviceFunctionalityList = serviceFunctionalityList;
    }

    public ServiceFunctionality[] getSelectedServiceFuntionalityList() {
        return selectedServiceFuntionalityList;
    }

    public void setSelectedServiceFuntionalityList(ServiceFunctionality[] selectedServiceFuntionalityList) {
        this.selectedServiceFuntionalityList = selectedServiceFuntionalityList;
    }

}
