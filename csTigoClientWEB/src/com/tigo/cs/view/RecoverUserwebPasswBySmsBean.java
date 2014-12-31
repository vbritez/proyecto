package com.tigo.cs.view;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.Cryptographer;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.UserwebFacade;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Tania Nunez
 * @version $Id: RecoverUserwebPasswBean.java 91 2011-11-28 20:16:12Z
 *          javier.kovacs $
 */
@ManagedBean(name = "recoverUserwebpasswBySmsBean")
@ViewScoped
public class RecoverUserwebPasswBySmsBean extends SMBaseBean {

    private static final long serialVersionUID = -3074514262978725621L;

    private final Integer PASSWORD_LENGHT = 10;

    private String username;
    private BigInteger phoneNumber;
    private Boolean enviado = false;
    private Application application;
    private ApplicationBean applicationBean;
    @EJB
    private UserwebFacade userwebFacade;
    @EJB
    private I18nBean i18n;
    @EJB
    private FacadeContainer facadeContainer;

    /**
     * Creates a new instance of LoginBean
     */
    public RecoverUserwebPasswBySmsBean() {
        if (getCurrentViewId().indexOf("recoverUserwebpasswBySmsBean") < 0
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
        applicationBean = ApplicationBean.getInstance();
        application = applicationBean.getApplication();
    }

    public String recoverPassword() {
        try {
            Userweb user = userwebFacade.getUserwebByUsername(username);
            if (phoneNumber.equals(user.getCellphoneNum())) {
                // generar contraseña randomica letras + nros
                String newPassword = randomString(PASSWORD_LENGHT);

                String msg = MessageFormat.format(
                        i18n.iValue("web.client.recoverpwdsms.message.SMSMessage"),
                        newPassword);

                facadeContainer.getSmsQueueAPI().sendToJMS(
                        phoneNumber.toString(), application, msg);

                user.setPasswordChr(Cryptographer.sha256Doble(newPassword));
                user.setChangepasswChr(true);

                if (user.getClient().getEnabledChr()
                    && userwebFacade.isUserwebBlocked(user) != null) {
                    user.setEnabledChr(true);
                    user.setPwdRetryCount(new BigInteger("0"));
                }

                userwebFacade.edit(user);
                enviado = true;

            } else {
                setErrorMessage(
                        "ERROR",
                        i18n.iValue("web.client.recoverpwdsms.message.UserAndPhoneNumberDoesntMatch"));
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

    String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random rnd = new Random();

    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();

    }

    // String validCharacters = $('0', '9').join() + $('A', 'Z').join();
    //
    // String randomString(int length) {
    // return $(validCharacters).shuffle().slice(length).toString();
    // }

    public BigInteger getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(BigInteger phoneNumber) {
        this.phoneNumber = phoneNumber;
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

}
