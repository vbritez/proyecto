package com.tigo.cs.view;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.facade.I18nBean;
import com.tigo.cs.facade.UserphoneFacade;
import com.tigo.cs.security.SecurityBean;

/**
 * 
 * @author Javier Kovacs
 * @version $Id$
 */
@ManagedBean(name = "smsSenderBean")
@ViewScoped
public class SMSSenderBean extends SMBaseBean {

    private static final long serialVersionUID = -7776228684842785501L;
    private static final int SMS_CHAR_MAX_COUNT = 160;
    @EJB
    private UserphoneFacade userphoneFacade;
    private List<Userphone> userphoneList;
    private List<Userphone> selectedUserphoneList;
    private String smsContent;
    private ApplicationBean applicationBean;
    private Application application;
    @EJB
    private FacadeContainer facadeContainer;
    @EJB
    private I18nBean i18n;

    @PostConstruct
    public void postConstruct() {
        applicationBean = ApplicationBean.getInstance();
        application = applicationBean.getApplication();
    }

    public String sendSMS() {
        if (selectedUserphoneList == null || selectedUserphoneList.size() <= 0) {
            setWarnMessage(i18n.iValue("web.client.home.backingbean.message.receiverSelectionRestriction"));
            return null;
        }
        if (smsContent.length() > SMS_CHAR_MAX_COUNT) {
            setWarnMessage(MessageFormat.format(
                    i18n.iValue("web.client.home.backingbean.message.SmsCharMaxCount"),
                    SMS_CHAR_MAX_COUNT));
            return null;
        }

        try {
            List<String> cellphonenumList = new ArrayList<String>();
            for (Userphone u : selectedUserphoneList) {
                cellphonenumList.add(u.getCellphoneNum().toString());
            }

            facadeContainer.getSmsQueueAPI().sendToJMS(cellphonenumList,
                    application, smsContent);

            setSuccessMessage(i18n.iValue("web.client.home.backingbean.message.SendSucces"));
            selectedUserphoneList = null;
            smsContent = null;

        } catch (Exception e) {
            setErrorMessage((i18n.iValue("web.client.backingBean.message.GeneralApplicationError")));
        }

        return null;
    }

    public String cleanSMS() {
        selectedUserphoneList = null;
        smsContent = null;
        return null;
    }

    public List<Userphone> getSelectedUserphoneList() {
        return selectedUserphoneList;
    }

    public void setSelectedUserphoneList(List<Userphone> selectedUserphoneList) {
        this.selectedUserphoneList = selectedUserphoneList;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
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
}
