package com.tigo.cs.api.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.TerportService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.Cryptographer;
import com.tigo.cs.commons.util.ExceptionUtility;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class TerportServiceAPI<T extends TerportService> extends AbstractServiceAPI<TerportService> {

    private static final String LOGIN_CODE = "0";
    private static final String ERROR_CODE = "1";
    private static final String DISABLED_USER_CODE = "2";
    private static final String SUCCESS_CODE = "-1";

    protected TerportEvent tranType;

    protected enum TerportEvent implements ServiceEvent {

        LOGIN(1, new ServiceProps("terport.name.In", "terport.message.In", "")),
        REGISTER(2, new ServiceProps("terport.name.Out", "terport.message.Out", ""));
        protected final int value;
        protected final ServiceProps props;

        private TerportEvent(int value, ServiceProps props) {
            this.value = value;
            this.props = props;
        }

        @Override
        public String getSuccessMessage() {
            return props.getSuccesMessage();
        }

        @Override
        public String getNoMatchMessage() {
            return props.getNoMatchMessage();
        }

        @Override
        public String getDescription() {
            return props.getDescription();
        }
    }

    public TerportServiceAPI() {
    }

    @Override
    public TerportService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new TerportService());
        }
        return super.getEntity();
    }

    @Override
    public TerportService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new TerportService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        switch (tranType) {
        case LOGIN:

            /*
             * verificamos el usuario y la contraseña, esto creara un id de
             * session con el cual se realizaran la validacion para el registro
             * de transacciones
             */

            MetaData username = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                    getClient().getClientCod(), MetaNames.MANAGER.value(), 1L,
                    getEntity().getUsername());

            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "terport.message.Login"), LOGIN_CODE,
                    username.getMetaDataPK().getCodeChr());

            break;

        case REGISTER:

            ServiceValue sv = treatHeader();
            List<ServiceValueDetail> svdList = treatDetails(sv);

            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "terport.message.Register"), SUCCESS_CODE,
                    sv.getColumn1Chr(),
                    svdList.get(0).getServicevaluedetailCod());

            break;
        }

        return returnMessage;
    }

    @Override
    protected void validate() throws InvalidOperationException {
        super.validate();

        switch (tranType) {
        case LOGIN:

            MetaData username = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                    getClient().getClientCod(), MetaNames.MANAGER.value(), 1L,
                    getEntity().getUsername());
            if (username == null) {
                throw new InvalidOperationException(MessageFormat.format(
                        "{0}%*{1}",
                        ERROR_CODE,
                        getFacadeContainer().getI18nAPI().iValue(
                                "terport.service.InvalidCredentials")));
            }

            MetaData enabled = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                    getClient().getClientCod(), MetaNames.MANAGER.value(), 4L,
                    username.getMetaDataPK().getCodeChr());

            if (enabled.getValueChr().compareToIgnoreCase("No") == 0) {

                throw new InvalidOperationException(MessageFormat.format(
                        "{0}%*{1}",
                        DISABLED_USER_CODE,
                        getFacadeContainer().getI18nAPI().iValue(
                                "terport.service.DisabledUser")));
            }

            MetaData password = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                    getClient().getClientCod(), MetaNames.MANAGER.value(), 2L,
                    getEntity().getUsername());

            String passwordCrypto;
            try {
                passwordCrypto = Cryptographer.sha256Doble(getEntity().getPassword());
                if (password.getValueChr().compareTo(passwordCrypto) != 0) {

                    /*
                     * aca meter el password retry count
                     */

                    MetaData passwordRetryCount = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                            getClient().getClientCod(),
                            MetaNames.MANAGER.value(), 6L,
                            username.getMetaDataPK().getCodeChr());

                    Integer loginRetryLimit = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "password.loginretry"));
                    Integer retry = Integer.parseInt(passwordRetryCount.getValueChr());

                    retry++;

                    passwordRetryCount.setValueChr(retry.toString());
                    getFacadeContainer().getMetaDataAPI().edit(
                            passwordRetryCount);

                    if (retry >= loginRetryLimit) {
                        enabled.setValueChr("0");
                        getFacadeContainer().getMetaDataAPI().edit(enabled);
                        throw new InvalidOperationException(MessageFormat.format(
                                "{0}%*{1}",
                                DISABLED_USER_CODE,
                                getFacadeContainer().getI18nAPI().iValue(
                                        "terport.service.DisabledUserRetryLimit")));
                    }

                    throw new InvalidOperationException(MessageFormat.format(
                            "{0}%*{1}",
                            ERROR_CODE,
                            getFacadeContainer().getI18nAPI().iValue(
                                    "terport.service.InvalidCredentials")));
                }

            } catch (NoSuchAlgorithmException e) {
                getFacadeContainer().getNotifier().error(
                        getClass(),
                        getCellphoneNumber().toString(),
                        "No se puede desencriptar la contraseña. "
                            + ExceptionUtility.getStackTrace(e), null);
                throw new InvalidOperationException(MessageFormat.format(
                        "{0}%*{1}",
                        ERROR_CODE,
                        getFacadeContainer().getI18nAPI().iValue(
                                "terport.service.InvalidCredentials")));
            } catch (UnsupportedEncodingException e) {
                getFacadeContainer().getNotifier().error(
                        getClass(),
                        getCellphoneNumber().toString(),
                        "No se puede desencriptar la contraseña. "
                            + ExceptionUtility.getStackTrace(e), null);
                throw new InvalidOperationException(MessageFormat.format(
                        "{0}%*{1}",
                        ERROR_CODE,
                        getFacadeContainer().getI18nAPI().iValue(
                                "terport.service.InvalidCredentials")));
            } catch (NumberFormatException e) {
                getFacadeContainer().getNotifier().error(
                        getClass(),
                        getCellphoneNumber().toString(),
                        "No se puede desencriptar la contraseña. "
                            + ExceptionUtility.getStackTrace(e), null);
                throw new InvalidOperationException(MessageFormat.format(
                        "{0}%*{1}",
                        ERROR_CODE,
                        getFacadeContainer().getI18nAPI().iValue(
                                "terport.service.InvalidCredentials")));
            } catch (GenericFacadeException e) {
                getFacadeContainer().getNotifier().error(
                        getClass(),
                        getCellphoneNumber().toString(),
                        "No se puede desencriptar la contraseña. "
                            + ExceptionUtility.getStackTrace(e), null);
                throw new InvalidOperationException(MessageFormat.format(
                        "{0}%*{1}",
                        ERROR_CODE,
                        getFacadeContainer().getI18nAPI().iValue(
                                "terport.service.InvalidCredentials")));
            }

            if (passwordCrypto.compareTo(password.getValueChr()) != 0) {

            }

            break;

        case REGISTER:
            break;
        }

        /*
         * aca metemos si vamos a validar alguna sesion
         */

    }

    @Override
    protected ServiceValue treatHeader() {

        return null;

    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {

        ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
        serviceValueDetail.setServiceValue(serviceValue);
        serviceValueDetail.setMessage(getMessage());
        serviceValueDetail.setRecorddateDat(validateDate());
        serviceValueDetail.setColumn1Chr(getEntity().getContainer());
        serviceValueDetail.setColumn2Chr(getEntity().getUbication());
        serviceValueDetail.setColumn3Chr(getEntity().getRegistrationNumber());

        List<ServiceValueDetail> list = new ArrayList<ServiceValueDetail>();
        list.add(getFacadeContainer().getServiceValueDetailAPI().create(
                serviceValueDetail));

        return list;
    }

    @Override
    protected void assignServiceEvent() {
        if (getEntity().getEvent().compareToIgnoreCase("LOGIN") == 0) {
            tranType = TerportEvent.LOGIN;
        } else {
            tranType = TerportEvent.REGISTER;
        }
    }

}
