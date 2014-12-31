package com.tigo.cs.view;

import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.octo.captcha.module.servlet.image.SimpleImageCaptchaServlet;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.facade.GlobalParameterFacade;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.security.Notifier;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: LoginUserwebBean.java 3469 2014-02-19 13:56:33Z viviana.britez
 *          $
 */
@ManagedBean(name = "loginWebuserBean")
@RequestScoped
public class LoginUserwebBean extends SMBaseBean {

    @EJB
    private I18nBean i18n;
    @EJB
    private Notifier notifier;

    private static final long serialVersionUID = -4845556363187375053L;
    String user;
    String password;
    private String oldpassword;
    private String newpassword1;
    private String newpassword2;
    private String captchaAnswer;
    private String messageWelcome;
    private String titleMessage;
    @EJB
    private GlobalParameterFacade globalParameterFacade;

    /**
     * Creates a new instance of LoginBean
     */
    public LoginUserwebBean() {
        if (getCurrentViewId().indexOf("userchangepassword") < 0
            && SecurityBean.getInstance().getLoggedInUserClient() != null) {
            SecurityBean.getInstance().terminateSession();
        }

        try {
            TimeZone.setDefault(ApplicationBean.getInstance().getDefaultTimeZone());
            Locale.setDefault(ApplicationBean.getInstance().getDefaultLocale());
        } catch (Exception e) {
            notifier.error(
                    getClass(),
                    null,
                    MessageFormat.format(
                            i18n.iValue("web.client.backingBean.loginUserWeb.messages.TimeZomeSetError"),
                            ApplicationBean.getInstance().getDefaultTimeZoneID()),
                    e);
        }
    }

    public String initSession() {
        try {
            if (captchaAnswer == null || captchaAnswer.trim().isEmpty()) {
                setErrorMessage(i18n.iValue("web.client.login.screen.label.CaptchaAnswer"));
                return null;
            }

            FacesContext fc = getFacesContext();
            HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
            if (!SimpleImageCaptchaServlet.validateResponse(request,
                    captchaAnswer)) {
                setErrorMessage(i18n.iValue("web.client.login.screen.label.InvalidCaptchaAnswer"));
                captchaAnswer = null;
                return null;
            }
            captchaAnswer = null;
            return SecurityBean.getInstance().initUserClientSession(user,
                    password);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String verifyConstraintError=  notifier.verifyException(user, e);
            if(verifyConstraintError != null){
                errorMessage =verifyConstraintError;
            }
            setErrorMessage(i18n.iValue("web.client.messages.title.Error"),
                    errorMessage);
            captchaAnswer = null;
            return null;
        }
    }

    public String changePassword() {
        try {
            return SecurityBean.getInstance().changeCurrentUserClientPassword(
                    oldpassword, newpassword1, newpassword2);
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
            return null;
        }
    }

    public String recoverPassword() {
        try {
            return "/userrecoverpassw";
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
            return null;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getCaptchaAnswer() {
        return captchaAnswer;
    }

    public void setCaptchaAnswer(String captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }

    public String getTimeString() {
        GregorianCalendar currDate = new GregorianCalendar();
        return "" + currDate.get(GregorianCalendar.YEAR)
            + currDate.get(GregorianCalendar.MONTH)
            + currDate.get(GregorianCalendar.DATE)
            + currDate.get(GregorianCalendar.HOUR_OF_DAY)
            + currDate.get(GregorianCalendar.MINUTE)
            + currDate.get(GregorianCalendar.SECOND)
            + currDate.get(GregorianCalendar.MILLISECOND);
    }

    public String getMessageWelcome() {
        if (messageWelcome == null) {
            try {
                messageWelcome = globalParameterFacade.findByCode("message.welcome.android.redirect");
            } catch (GenericFacadeException e) {
                notifier.error(getClass(), null, e.getMessage(), e);
            }
        }
        return messageWelcome;
    }

    public void setMessageWelcome(String messageWelcome) {
        this.messageWelcome = messageWelcome;
    }

    public String getTitleMessage() {
        if (titleMessage == null) {
            try {
                titleMessage = globalParameterFacade.findByCode("title.welcome.android.redirect");
            } catch (GenericFacadeException e) {
                notifier.error(getClass(), null, e.getMessage(), e);
            }
        }
        return titleMessage;
    }

    public void setTitleMessage(String titleMessage) {
        this.titleMessage = titleMessage;
    }
}
