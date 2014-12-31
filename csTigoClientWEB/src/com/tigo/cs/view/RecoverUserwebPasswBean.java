package com.tigo.cs.view;

import java.io.File;
import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.mail.Mailer;
import com.tigo.cs.commons.util.Cryptographer;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Tania Nunez
 * @version $Id: RecoverUserwebPasswBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "recoverUserwebpasswBean")
@ViewScoped
public class RecoverUserwebPasswBean extends SMBaseBean {

    private static final long serialVersionUID = -3074514262978725621L;
    private String username;
    private String mail;
    private String userHash;
    private String newpassword1;
    private String newpassword2;
    private Boolean enviado = false;
    private Boolean canChangePassw = false;
    private Boolean recoverPasswSuccess = false;
    @EJB
    private UserwebFacade userwebFacade;
    @EJB
    private GlobalParameterFacade globalParamFacade;
    @EJB
    private I18nBean i18n;

    /**
     * Creates a new instance of LoginBean
     */
    public RecoverUserwebPasswBean() {
        if (getCurrentViewId().indexOf("userchangepassword") < 0
            && SecurityBean.getInstance().getLoggedInUserClient() != null) {
            SecurityBean.getInstance().terminateSession();
        }

        try {
            TimeZone.setDefault(ApplicationBean.getInstance().getDefaultTimeZone());
            Locale.setDefault(ApplicationBean.getInstance().getDefaultLocale());
        } catch (Exception e) {
            System.out.println(MessageFormat.format(
                    i18n.iValue("web.client.backingBean.loginUserWeb.messages.TimeZomeSetError"),
                    ApplicationBean.getInstance().getDefaultTimeZoneID()));
        }
    }

    @PostConstruct
    public void init() {
        HttpServletRequest op = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String hash = op.getParameter("h");
        username = op.getParameter("u");
        if (hash != null && username != null) {
            try {
                Userweb userweb = userwebFacade.getUserwebByUsername(username);
                if (userweb != null
                    && hash.equals(userweb.getChangepasshashChr())) {
                    canChangePassw = true;
                } else {
                    canChangePassw = false;
                }
            } catch (Exception e) {
                setErrorMessage(e.getMessage());
            }
        }

    }

    public String recoverPassword() {
        try {
            Userweb user = userwebFacade.getUserwebByUsername(username);
            if (mail.equals(user.getMailChr())) {
                if (Mailer.isValidEmailAddress(user.getMailChr())) {
                    sendEmail(user);
                    userwebFacade.setUserwebHash(username, userHash);
                    enviado = true;
                } else {
                    setErrorMessage(
                            "ERROR",
                            i18n.iValue("web.client.login.message.UserAndMailDoesntMatch"));
                    return null;
                }
            } else {
                setErrorMessage(
                        "ERROR",
                        i18n.iValue("web.client.login.message.UserAndMailDoesntMatch"));
                return null;
            }
            return null;
        } catch (GenericFacadeException ex) {
            setErrorMessage(
                    "ERROR",
                    i18n.iValue("web.client.login.message.UserAndMailDoesntMatch"));
            return null;
        } catch (Exception e) {
            setErrorMessage(
                    "ERROR",
                    i18n.iValue("web.client.backingBean.message.GeneralApplicationError"));
            return null;
        }
    }

    private void sendEmail(Userweb user) throws Exception {
        Mailer mailer = new Mailer();
        mailer.setHost(globalParamFacade.findByCode("mail.smtp.host"));
        mailer.setPuerto(Integer.valueOf(globalParamFacade.findByCode("mail.smtp.port")));
        mailer.setSsl(globalParamFacade.findByCode("mail.smtp.ssl").equalsIgnoreCase(
                "S"));
        mailer.setAutenticacion(globalParamFacade.findByCode("mail.smtp.auth").equalsIgnoreCase(
                "S"));
        mailer.setCuenta(globalParamFacade.findByCode("mail.smtp.account"));
        mailer.setUsuario(globalParamFacade.findByCode("mail.smtp.user"));
        mailer.setPassword(globalParamFacade.findByCode("mail.smtp.pwd"));
        mailer.setAliasFrom(globalParamFacade.findByCode("mail.aliasfrom"));

        File[] adjuntos = new File[1];
        adjuntos[0] = new File(getFacesContext().getExternalContext().getRealPath(
                globalParamFacade.findByCode("mail.logo.tigo")));

        String cMail = contenidoMail;
        cMail = cMail.replaceAll("__URL__", obtainURLForEmail(user));

        mailer.enviarEmailHtml(globalParamFacade.findByCode("mail.subject"),
                cMail, adjuntos, user.getMailChr());

    }

    private final String contenidoMail = "<table cellpadding=\"0\" cellspacing=\"0\"><tr><th align=\"left\"><img src=\"cid:adj_0\" /></th></tr><tr><td style=\"font: 12px Arial ,sans-serif; color: #808285; padding-top: 15px; padding-bottom: 15px;\">"
        + "Estimado cliente de Tigo,<br /><br />"
        + "Usted ha solicitado restaurar su contraseña.<br /><br />"
        + "Por favor Haga click en el link mas abajo para confirmar el cambio .<br /><br />"
        + "<a href=\"__URL__\">__URL__</a><br />"
        + ""
        + "</td></tr><tr><td align=\"left\"></td></tr></table>";

    private String getSessionHash(Userweb user) {
        try {
            return Cryptographer.sha256Doble(user.getUsernameChr()
                + getTimeString() + getSessionId());
        } catch (Exception e) {
            return null;
        }
    }

    private String obtainURLForEmail(Userweb user) throws Exception {
        // String url =
        // globalParamFacade.findByCode("mail.url.conf") +
        // "/domain/cstigowebclient/userrecoverpassw.xhtml";
        String url = globalParamFacade.findByCode("mail.url.conf"); // "http://localhost:8080/userrecoverpassw.jsf"
        url += "?u=" + user.getUsernameChr();
        /* obtiene el hash del usuario */
        userHash = getSessionHash(user);
        url += "&h=" + userHash;
        return url;
    }

    private String getTimeString() {
        GregorianCalendar currDate = new GregorianCalendar();
        return "" + currDate.get(GregorianCalendar.YEAR)
            + currDate.get(GregorianCalendar.MONTH)
            + currDate.get(GregorianCalendar.DATE)
            + currDate.get(GregorianCalendar.HOUR_OF_DAY)
            + currDate.get(GregorianCalendar.MINUTE)
            + currDate.get(GregorianCalendar.SECOND)
            + currDate.get(GregorianCalendar.MILLISECOND);
    }

    public String changePassword() {
        try {
            SecurityBean.getInstance().recoverCurrentUserClientPassword(
                    username, newpassword1, newpassword2);
            recoverPasswSuccess = true;
            return null;
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
            return null;
        }
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnviado() {
        return enviado;
    }

    public void setEnviado(Boolean enviado) {
        this.enviado = enviado;
    }

    public Boolean getCanChangePassw() {
        return canChangePassw;
    }

    public void setCanChangePassw(Boolean canChangePassw) {
        this.canChangePassw = canChangePassw;
    }

    public String getNewpassword1() {
        return newpassword1;
    }

    public void setNewpassword1(String newpassword1) {
        this.newpassword1 = newpassword1;
    }

    public String getNewpassword2() {
        return newpassword2;
    }

    public void setNewpassword2(String newpassword2) {
        this.newpassword2 = newpassword2;
    }

    public Boolean getRecoverPasswSuccess() {
        return recoverPasswSuccess;
    }

    public void setRecoverPasswSuccess(Boolean recoverPasswSuccess) {
        this.recoverPasswSuccess = recoverPasswSuccess;
    }
}
