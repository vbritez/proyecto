package com.tigo.cs.view.administrative;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
import com.tigo.cs.domain.RoleClient;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.ClassificationFacade;
import com.tigo.cs.facade.RoleClientFacade;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.metadata.AbstractCrudBeanClient;

/**
 * 
 * @author Tania Nuñez
 * @version $Id$
 */
@ManagedBean(name = "crudUserwebBean")
@ViewScoped
public class CrudUserwebBean extends AbstractCrudBeanClient<Userweb, UserwebFacade> implements Serializable {

    private static final long serialVersionUID = -267114861436404431L;
    @EJB
    private ClassificationFacade classificationFacade;
    @EJB
    private RoleClientFacade roleclientFacade;
    private Client client;
    private List<Classification> classificationList;
    private TreeNode root;
    private List<TreeNode> selectednodesList;
    private TreeNode[] selectedNodes;
    private Boolean treeChecked = false;
    private List<RoleClient> roleclientList;
    private List<RoleClient> selectedRoleclientList;

    public CrudUserwebBean() {
        super(Userweb.class, UserwebFacade.class);
    }

    @Override
    public String newEntity() {
        String retVal = super.newEntity();
        if (client != null && getEntity() != null) {
            getEntity().setClient(client);
        }
        if (getClient() != null) {
            createClassificationTree();
            /*
             * setSelectednodesList(null); getSelectednodesList();
             */
        }
        return retVal;
    }

    @Override
    public String editEntity() {
        // super.editEntity("clientServiceFunctionalityList");
        String retVal = super.editEntity("clientServiceFunctionalityList");
        if (getEntity() != null) {
            createClassificationTree();
            // TRAER LAS CLASIFICACIONES
            List<Classification> classifListSelected = null;
            classifListSelected = getFacade().getClassificationsByUserweb(getEntity().getUserwebCod());
            if (classifListSelected != null && classifListSelected.size() > 0) {
                setSelectednodesList(null);
                getSelectednodesList();
                selectClassificationInTree(root, classifListSelected);
            }

            List<RoleClient> roleclientlist = roleclientFacade.getRoleclientList(getEntity().getUserwebCod());
            setSelectedRoleclientList(roleclientlist);
        }
        return retVal;

    }

    @Override
    public String saveEditing() {
        if (getEntity().getPasswordChr() == null) {
            getEntity().setPasswordChr("");
            getEntity().setChangepasswChr(true);
        }

        // SAVE CLASSIFICATION
        List<Classification> classifList = new ArrayList<Classification>();
        selectednodesList = Arrays.asList(selectedNodes);

        if (getSelectednodesList() != null && getSelectednodesList().size() > 0) {
            for (TreeNode nodo : getSelectednodesList()) {
                Classification actualClassif = (Classification) nodo.getData();
                if (actualClassif != null) {
                    // Classification classif =
                    // classificationFacade.find(actualClassif.getClassificationCod());
                    classifList.add(actualClassif);
                }
            }
        }

        // if (getSelectedRoleclientList() != null &&
        // getSelectedRoleclientList().size() > 0) {
        getEntity().setRoleClientList(getSelectedRoleclientList());
        // }

        getEntity().setClassificationList(classifList);
        String retVal = super.saveEditing();

        if (getEntity() == null) {
            setSelectedRoleclientList(null);
            setSelectednodesList(null);
            setTreeChecked(false);
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
    public String cancelEditing() {
        setSelectednodesList(null);
        setSelectedRoleclientList(null);
        setTreeChecked(false);
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

    public List<RoleClient> getRoleclientList() {
        if (roleclientList == null) {
            roleclientList = roleclientFacade.getAllEnabledRoleclientByClient(SecurityBean.getInstance().getLoggedInUserClient().getClient().getClientCod());
        }
        return roleclientList;
    }

    public List<RoleClient> getSelectedRoleclientList() {
        return selectedRoleclientList;
    }

    public void setSelectedRoleclientList(List<RoleClient> selectedRoleclientList) {
        this.selectedRoleclientList = selectedRoleclientList;
    }

    public void setRoleclientList(List<RoleClient> roleclientList) {
        this.roleclientList = roleclientList;
    }

    public List<Classification> getClassificationList() {
        if (classificationList == null) {
            classificationList = classificationFacade.findAll();
        }
        return classificationList;
    }

    public void setClassificationList(List<Classification> classificationList) {
        this.classificationList = classificationList;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
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

    @Override
    public boolean showAllInQuantityList() {
        return true;
    }

    @Override
    public String getReportWhereCriteria() {
        return "";
    }
}
