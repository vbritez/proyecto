package com.tigo.cs.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Moduleclient;
import com.tigo.cs.domain.Screenclient;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.ModuleclientFacade;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;
import com.tigo.cs.view.pbeans.MenuModuleClient;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "sessionBean")
@SessionScoped
public class SessionBean extends SMBaseBean {

    private static final long serialVersionUID = 9060250094658127837L;
    @EJB
    private ModuleclientFacade mcFacade;
    @EJB
    private Notifier notifier;
    private List<MenuModuleClient> clientModules = null;

    public SessionBean() {
    }

    public static SessionBean getInstance() {
        return (SessionBean) getBean("sessionBean");
    }

    /**
     * Por cada m贸dulo activo en la plataforma se busca los screenclient
     * (pantalla) habilitado para el usuario web.
     * 
     * Al recuperar tanto los m贸dulos como las pantallas se cambian el atributo
     * descripci贸n que contiene un key de I18N por su valor correspondiente en
     * el bundle. De acuerdo a esto es necesario recorrer la lista para cambiar
     * los valores.
     * 
     * Si el usuario no tiene habilitado ninguna pantalla para un m贸dulo, el
     * m贸dulo no se muestra en la barra del men煤 de la app.
     */
    public List<MenuModuleClient> getClientModules() throws GenericFacadeException {
        if (clientModules == null) {
            clientModules = new ArrayList<MenuModuleClient>();
            try {
                List<Moduleclient> mcList = mcFacade.getActiveModules();
                Userweb user = SecurityBean.getInstance().getLoggedInUserClient();
                for (Moduleclient moduleclient : mcList) {
                    moduleclient.setDescriptionChr(moduleclient.getDescriptionChr());
                    List<Screenclient> screenClientList = mcFacade.getScreenclientListByModuleAndUserweb(moduleclient.getModuleclientCod(), user.getUserwebCod(), user.getAdminNum());
                    for (Screenclient screenclient : screenClientList) {
                        screenclient.setDescriptionChr(screenclient.getDescriptionChr());
                    }
                    if (screenClientList.size() > 0) {
                        clientModules.add(new MenuModuleClient(moduleclient,
                                screenClientList));
                    }
                }
            } catch (GenericFacadeException ex) {
                Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return clientModules;
    }

    public void setClientModules(List<MenuModuleClient> clientModules) {
        this.clientModules = clientModules;
    }
    
    public void signalPageAcces() {
        notifier.signal(this.getClass(), Action.ACCESS, SecurityBean.getInstance().getLoggedInUserClient(), null, getCurrentViewId(), "Acceso a la pantalla.", getSessionId(), getIpAddress());
    }
    
    /**
     * M閠odo utilizado en el componente
     * <code>p:growl<code> para capturar mensajes desde el contexto de validaci髇
     * (javax.validation.contraints) y evitar que el t韙ulo y el detalle
     * del mensaje sea el mismo, asignando el t韙ulo el nivel de severidad (Error, Info, etc).
     * */
    public boolean isSummaryEqualsToDetail() {
        List<FacesMessage> msgs = getFacesContext().getMessageList();
        for (FacesMessage facesMessage : msgs) {
            if (facesMessage.getSummary().equals(facesMessage.getDetail())) {
                facesMessage.setDetail(new String(facesMessage.getDetail()));
                String severityType = facesMessage.getSeverity().toString();
                facesMessage.setSummary(new String(
                        severityType.substring(0, severityType.indexOf(" "))));
            }
        }
        return true;
    }
}
