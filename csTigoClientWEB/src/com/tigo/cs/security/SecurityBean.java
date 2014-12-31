package com.tigo.cs.security;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.Cryptographer;
import com.tigo.cs.commons.util.SessionUtility;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Screenclient;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.ScreenClientFacade;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.view.ApplicationBean;

@ManagedBean(name = "securityBean")
@SessionScoped
public class SecurityBean extends SMBaseBean {

    private static final long serialVersionUID = -6088672880929878271L;
    public static final String LOGGEDIN_TYPE_VAR_NAME = "loggedin_type";
    public static final String LOGGEDIN_TYPE_ADMIN = "loggedin_admin";
    public static final String LOGGEDIN_TYPE_CLIENT = "loggedin_client";
    private final String pwdRegex = "(\\w*[A-Z]+\\w*\\d+\\w*)|(\\w*\\d+\\w*[A-Z]+\\w*)";
    @EJB
    private UserwebFacade userwebFacade;
    @EJB
    private ScreenClientFacade screenClientFacade;
    @EJB
    private GlobalParameterFacade globalParamFacade;
    @EJB
    private I18nBean i18n;
    private Userweb loggedInUserClient;
    private String pagesNoExplicitAuthClient;
    private final String sessionId;
    private final String ipAddress;
    private ApplicationBean appBean;
    private List<String> pages = new ArrayList<String>();

    public SecurityBean() {
        sessionId = SessionUtility.getSessionId();
        ipAddress = SessionUtility.getIpAddress();
    }

    public static SecurityBean getInstance() {
        return (SecurityBean) getBean("securityBean");
    }

    private ApplicationBean getApplicationBean() {
        if (appBean == null) {
            appBean = ApplicationBean.getInstance();
        }
        return appBean;
    }

    public boolean isLoggedInClient() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession currentHttpSession = (HttpSession) facesContext.getExternalContext().getSession(
                false);

        return currentHttpSession != null && loggedInUserClient != null
            && loggedInUserClient.getUsernameChr() != null;
    }

    /**
     * Inits a session
     * 
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public String initUserClientSession(String username, String password) throws Exception {
        Userweb user;

        try {
            user = userwebFacade.getUserwebByUsername(username);
        } catch (Exception e) {
            throw new Exception(i18n.iValue("web.client.login.message.UserAndPasswordDoesntMatch"));
        }

        if (user != null) {
            if (user.getPasswordChr().equals(
                    Cryptographer.sha256Doble(password))) {
                if (user.getEnabledChr() && user.getClient().getEnabledChr()) {
                    setLoggedInUserClient(user);

                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
                            LOGGEDIN_TYPE_VAR_NAME, LOGGEDIN_TYPE_CLIENT);
                    if (user.getChangepasswChr()) {
                        user.setPwdRetryCount(new BigInteger("0"));
                        userwebFacade.edit(user);
                        return "/domain/cstigowebclient/userchangepassword";
                    } else {
                        user.setPwdRetryCount(new BigInteger("0"));
                        userwebFacade.edit(user);
                        return "/domain/cstigowebclient/home";
                    }
                } else {
                    throw new Exception(i18n.iValue("web.client.login.message.UserDisabled"));
                }
            } else {
                Integer wrongpwd = user.getPwdRetryCount() == null ? 0 : user.getPwdRetryCount().intValue();
                wrongpwd++; // suma del actual intento fallido

                if (wrongpwd.intValue() >= getApplicationBean().getLoginRetryLimit()) {
                    user.setEnabledChr(false);
                    user.setPwdRetryCount(new BigInteger(wrongpwd.toString()));
                    userwebFacade.edit(user);
                    if (wrongpwd.intValue() == getApplicationBean().getLoginRetryLimit()) {
                        throw new Exception(i18n.iValue("web.client.login.message.UserBlockedByFailedAttemps"));
                    }
                } else {
                    user.setPwdRetryCount(new BigInteger(wrongpwd.toString()));
                    userwebFacade.edit(user);
                }
                if (user.getEnabledChr()) {
                    throw new Exception(i18n.iValue("web.client.login.message.UserAndPasswordDoesntMatch"));
                } else {
                    throw new Exception(i18n.iValue("web.client.login.message.UserDisabled"));
                }
            }
        } else {
            throw new Exception(i18n.iValue("web.client.login.message.UserAndPasswordDoesntMatch"));
        }
    }

    public void terminateSession() {
        loggedInUserClient = null;

        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(
                true);
        session.invalidate();
        session = (HttpSession) fc.getExternalContext().getSession(true);
    }

    /**
     * Terminates the current session
     * 
     * @return
     */
    public String logoutClientSession() {
        terminateSession();
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().redirect(
                    fc.getExternalContext().getRequestContextPath()
                        + "/loginclient.jsf");
        } catch (IOException ex) {
        }

        return null;
    }

    /**
     * Changes the current logged in user password
     * 
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    public String changeCurrentUserClientPassword(String oldPassword, String newPassword, String confirmPassword) throws Exception {

        if (loggedInUserClient == null) {
            return null;
        }

        if (!loggedInUserClient.getPasswordChr().equals(
                Cryptographer.sha256Doble(oldPassword))) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.OldPwdDoesntMatch"));
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.PwdsDoesntMatch"));
        }
        if (newPassword.length() < 8) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.PwdLengthRestriction"));
        }
        if (!newPassword.matches(pwdRegex)) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.PwdCharactersRestriction"));
        }

        try {
            loggedInUserClient.setPasswordChr(Cryptographer.sha256Doble(newPassword));
            loggedInUserClient.setChangepasswChr(false);
            userwebFacade.edit(loggedInUserClient);
            return "home";
        } catch (Exception e) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.ExceptionChangingPwd")
                + e);
        }

    }

    public String recoverCurrentUserClientPassword(String username, String newPassword, String confirmPassword) throws Exception {
        Userweb user;
        String pwdRegex = globalParamFacade.findByCode("password.regexp");
        Integer pwdMinLength = Integer.parseInt(globalParamFacade.findByCode("password.length.min"));
        Integer pwdMaxLength = Integer.parseInt(globalParamFacade.findByCode("password.length.max"));
        try {
            user = userwebFacade.getUserwebByUsername(username);
        } catch (Exception e) {
            throw new Exception(i18n.iValue("web.client.login.message.UserAndPasswordDoesntMatch"));
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.PwdsDoesntMatch"));
        }

        if (newPassword.length() < pwdMinLength) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.PwdLengthRestriction"));
        }

        if (newPassword.length() > pwdMaxLength) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.PwdLengthMaxRestriction"));
        }

        if (!newPassword.matches(pwdRegex)) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.PwdCharactersRestriction"));
        }
        try {
            user.setPasswordChr(Cryptographer.sha256Doble(newPassword));
            user.setChangepasswChr(false);
            userwebFacade.edit(user);
            return null;
        } catch (Exception e) {
            throw new Exception(i18n.iValue("web.client.changepwd.message.ExceptionChangingPwd")
                + e);
        }
    }

    public static boolean isSessionExpired(FacesContext fc) {
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(
                false);
        boolean newSession = (session == null) || (session.isNew());

        // Si la sessi�n es nueva pero no se trata de la p�gina LOGIN o INDEX
        // retorna sesion expirada (TRUE).
        if (newSession
            && !fc.getViewRoot().getViewId().endsWith("/loginclient.xhtml")
            && !fc.getViewRoot().getViewId().endsWith("/index.xhtml")) {
            return true;
        }
        return false;
    }

    public boolean verifyPageAccess(String viewId) {
        boolean onAuthorizedPage = viewId.endsWith("/loginclient.xhtml")
            || viewId.endsWith("/index.xhtml")
            || viewId.endsWith("/userrecoverpassw.xhtml")
            || viewId.endsWith("/downloadapps.xhtml")
            || viewId.endsWith("/userrecoverpasswbysms.xhtml")
            || viewId.endsWith("/sp/spwelcome.xhtml");
        if (onAuthorizedPage) {
            return true;
        }
        if (isLoggedInClient()) {
            try {
                onAuthorizedPage = getPagesNoExplicitAuthClient().indexOf(
                        "|" + viewId + "|") >= 0;
                if (onAuthorizedPage) {
                    return true;
                }

                // Para manejo de accesos a pantallas de charts, se toma el
                // permiso de acceso a la pantalla de reporte equivalente
                if (viewId.endsWith("_charts.xhtml")) {
                    viewId = viewId.replaceAll("_charts.xhtml", ".xhtml");
                }

                return userwebFacade.isScreenAllowebForUserclient(
                        loggedInUserClient.getUserwebCod(), viewId,
                        loggedInUserClient.getAdminNum());
            } catch (GenericFacadeException ex) {
                setErrorMessage(
                        i18n.iValue("web.client.changepwd.message.AccessVerificationFailed"),
                        ex.getMessage());
                Logger.getLogger(SecurityBean.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

        return false;
    }

    private String getPagesNoExplicitAuthClient() throws GenericFacadeException {
        if (pagesNoExplicitAuthClient == null) {
            pagesNoExplicitAuthClient = "";
            List<Screenclient> screensNoAuth = screenClientFacade.getScreensNoSecurityAuthClient();
            for (int i = 0; i < screensNoAuth.size(); i++) {
                pagesNoExplicitAuthClient += "|"
                    + screensNoAuth.get(i).getPageChr() + "|";
            }
        }
        return pagesNoExplicitAuthClient;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public Userweb getLoggedInUserClient() {
        return loggedInUserClient;
    }

    public void setLoggedInUserClient(Userweb loggedInUserClient) {
        this.loggedInUserClient = loggedInUserClient;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

}
