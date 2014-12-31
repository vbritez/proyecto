package com.tigo.cs.api.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.enumerate.Restriction;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.ClientServiceFunctionality;
import com.tigo.cs.domain.ussd.UssdMenuEntryUssdUser;

public abstract class ServiceBasicAPI<T extends BaseServiceBean> extends BasicAPI<T> {

    /**
     * 
     * Realiza validaciones de habilitacion de servicios
     * @throws InvalidOperationException 
     * 
     */
    @Override
    protected void validate() throws InvalidOperationException {

        if (getUserphone() == null) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    Restriction.PHONE_NOT_REGISTERED.getMessage()));
        }

        if (!getUserphone().getEnabledChr()) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    Restriction.PHONE_WITH_SERVICE_NOT_ALLOWED.getMessage()));
        }

        if (!getUserphone().getClient().getEnabledChr()) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    Restriction.CLIENT_WITH_SERVICE_NOT_ALLOWED.getMessage()));
        }

        List<ClientServiceFunctionality> clientServiceFunctionalities = getFacadeContainer().getClientServiceFunctionalityAPI().findByUserphone(
                getUserphone());

        if (clientServiceFunctionalities == null
            || (clientServiceFunctionalities != null && clientServiceFunctionalities.size() == 0)) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    Restriction.PHONE_WITH_SERVICE_NOT_REGISTERED.getMessage()));
        }

        if (getEntity().getUssd()) {
            List<UssdMenuEntryUssdUser> list = getFacadeContainer().getUssdMenuEntryUssdUserAPI().findByUssduserId(
                    getUserphone().getUserphoneCod());
            if (list == null || (list != null && list.size() == 0)) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "ussd.validator.UserHasNotEnabledUSSDServices"));
            }
        }

        super.validate();

    }

    public String getShortNumber() {
        if (shortNumber == null) {
            try {
                shortNumber = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "ussd.shortNumber");
            } catch (Exception e) {
                Logger.getLogger(AbstractServiceAPI.class.getName()).log(
                        Level.SEVERE, e.getMessage(), e);
            }
        }
        return shortNumber;
    }

    @Override
    public Application getApplication() {
        if (super.getApplication() == null) {
            try {
                if (getEntity().getApplicationKey() != null) {
                    setApplication(getFacadeContainer().getApplicationAPI().findByKey(
                            getEntity().getApplicationKey()));
                }
            } catch (Exception e) {
                Logger.getLogger(AbstractServiceAPI.class.getName()).log(
                        Level.SEVERE, e.getMessage(), e);
            }
        }
        return super.getApplication();
    }

    public abstract void convertToBean() throws InvalidOperationException, NumberFormatException, InvalidOperationException;

}
