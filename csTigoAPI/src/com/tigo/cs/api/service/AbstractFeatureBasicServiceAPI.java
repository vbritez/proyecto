package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigo.cs.api.entities.DynamicFormService;
import com.tigo.cs.api.entities.DynamicFormValueDataService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.util.NumberUtil;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFeature;
import com.tigo.cs.domain.DataCheck;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.Phone;
import com.tigo.cs.domain.Userphone;

public abstract class AbstractFeatureBasicServiceAPI<T extends DynamicFormService> extends AbstractServiceAPI<T> {

    private Phone phone;
    protected ClientFeature clientFeature;
    protected FeatureElement featureElement;

    protected FeatureEvent tranType;

    protected static enum FeatureEvent implements ServiceEvent {

        REGISTER(1, new ServiceProps("feature.name.Register", "feature.message.Register", "feature.unsuccessful.Register")),
        FIND(2, new ServiceProps("feature.name.Find", "feature.message.Find", "feature.unsuccessful.Find")),
        VALIDATE(2, new ServiceProps("feature.name.Validate", "feature.message.Validate", "feature.unsuccessful.Validate"));
        protected final int value;
        protected final ServiceProps props;

        private FeatureEvent(int value, ServiceProps props) {
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

    public AbstractFeatureBasicServiceAPI() {
    }

    @Override
    protected void init() {
        setFeatureElement(null);
        setPhone(null);
        setClientFeature(null);
        super.init();
    }

    /**
     * Validacion de phone o userphone para retornar los userCodes
     * correspondientes
     * 
     * 
     * Pasos a realizar para retornar los userCodes y si paso la validacion 1.-
     * Verificar el ShortCut code que se marco y si este es uno valido de un
     * cliente habilitado y que tenga al menos un feature element disponible o
     * si el cliente tiene feature elements abiertos
     * 
     * 2.- Si pasa esta validacion recuperamos el codigo del perfil que esta
     * disponible para los feature element abiertos para ese shortcut code
     * 
     * 3.- si es un phone verificamos su usercode y retornamos este id (esto
     * implica que solo se mostraran las entradas permitidas para este usercode)
     * 
     * 4.- Si recibimos el codigo del feature verificamos si el phone esta en
     * una lista negra de phones
     * 
     * @throws InvalidOperationException
     * 
     * 
     * 
     */
    @Override
    protected void validate() throws InvalidOperationException {

        /*
         * recuperamos el clientFeature habilitado que se corresponde con este
         * shortcutCode
         */
        getClientFeature();

        /*
         * el cliente no esta habilitado
         */
        if (!getClient().getEnabledChr()) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "message.ussd.validator.InvalidShortcutCode"));
        }

        if (getFeatureElement() != null) {

            if (!getFeatureElement().getEnabledChr()) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "feature.ussd.DisabledFeature"));
            }

            if (getPhone() != null
                && getFacadeContainer().getPhoneAPI().isBlacklisted(
                        getFeatureElement(), getPhone())) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "feature.ussd.DisabledFeature"));
            }

            Calendar c = Calendar.getInstance();
            Date now = new Date();
            c.setTime(now);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            now = c.getTime();

            /*
             * verificamos que pueda
             */

            if (getFeatureElement().getFinishPeriodDat() != null
                && getFeatureElement().getFinishPeriodDat().getTime() < now.getTime()) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "feature.ussd.DisabledFeature"));
            }

            validateEntries();

        }

        /*
         * En el caso de que el codigo de servicio no sea el de prestaciones se
         * debe hacer la validaciones propias de los demas servicios
         */
        if (getService() != null && getService().getServiceCod() != 100L) {
            super.validate();
        }

    }

    protected void validateEntries() throws InvalidOperationException {
        /*
         * validate entries
         */

        List<FeatureElementEntry> entries = getFacadeContainer().getFeatureElementEntryAPI().findPersisitibles(
                getFeatureElement());
        for (FeatureElementEntry entry : entries) {

            String value = getEntryValue(entry.getFeatureElementEntryCod());
            if (value != null) {
                if (entry.getDataCheck() != null) {
                    validateEntry(entry.getTitleChr(), value,
                            entry.getDataCheck());
                }
            }
        }

        entries = getFacadeContainer().getFeatureElementEntryAPI().findOutput(
                getFeatureElement());
        for (FeatureElementEntry entry : entries) {
            String value = getEntryValue(entry.getFeatureElementEntryCod());
            if (value != null) {
                if (entry.getDataCheck() != null) {
                    validateEntry(entry.getTitleChr(), value,
                            entry.getDataCheck(), entry);
                }
            }
        }
    }

    protected String getEntryValue(Long entryCode) {

        if (getEntity().getValueBean() != null) {
            for (DynamicFormValueDataService entry : getEntity().getValueBean().getEntryList()) {
                if (entry.getCodFeatureElementEntryBean().compareTo(entryCode) == 0) {
                    return entry.getData();
                }
            }
        }

        return null;
    }

    protected void validateEntry(String valueDescription, String value, DataCheck dataCheck, FeatureElementEntry entry) throws InvalidOperationException {
        super.validateEntry(valueDescription, value, dataCheck);

        if (entry.getCodFeatureEntryType().getNameChr().compareToIgnoreCase(
                "OPTION") == 0) {
            try {
                if (!NumberUtil.isInteger(value)) {
                    String msj = MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "check.datatype.message.Option"), value,
                            valueDescription);
                    throw new InvalidOperationException(msj);
                }
                Integer pos = Integer.parseInt(value);
                List<FeatureElementEntry> entries = entry.getFeatureElementEntries();
                if (entries == null || pos <= 0
                    || (entries != null && entries.size() < pos)) {
                    String msj = MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "check.datatype.message.Option"), value,
                            valueDescription);
                    throw new InvalidOperationException(msj);
                }

            } catch (InvalidOperationException e) {
                throw e;
            } catch (Exception e) {
                getFacadeContainer().getNotifier().error(

                AbstractFeatureBasicServiceAPI.class,
                        getCellphoneNumber().toString(), e.getMessage(), e);
            }
        } else if (entry.getCodFeatureEntryType().getNameChr().compareToIgnoreCase(
                "OUTPUT") == 0) {
            try {
                if (!NumberUtil.isInteger(value)) {
                    String msj = getFacadeContainer().getI18nAPI().iValue(
                            "check.datatype.message.Output");
                    throw new InvalidOperationException(msj);
                }

                Integer intValue = Integer.parseInt(value);
                if (intValue == 0) {
                    String msj = getFacadeContainer().getI18nAPI().iValue(
                            "check.datatype.message.Output");
                    throw new InvalidOperationException(msj);
                }
            } catch (InvalidOperationException e) {
                throw e;
            } catch (Exception e) {
                getFacadeContainer().getNotifier().error(

                AbstractFeatureBasicServiceAPI.class,
                        getCellphoneNumber().toString(), e.getMessage(), e);
            }
        }

    }

    @Override
    protected String getUserCode() throws InvalidOperationException {

        if (getShortCutCode() != null) {

            String splitter = null;
            try {
                splitter = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "feature.ussd.usercode.splitter");
            } catch (Exception e) {
                splitter = "%*";
            }

            /*
             * construimos el userCode
             */
            String userCode = "";
            if (getUserphone() != null && getClient().getEnabledChr()
                && getUserphone().getEnabledChr()) {
                userCode = userCode.concat(
                        getUserphone().getUserphoneCod().toString()).concat(" ");
            }
            if (getPhone() != null) {
                userCode = userCode.concat(getPhone().getPhoneCod().toString()).concat(
                        " ");
            }
            if (getClient().getEnabledChr() && getClientFeature() != null
                && getClientFeature().getEnabledChr()
                && getClientFeature().getClient().getUssdProfileId() != null) {
                userCode = userCode.concat(
                        getClientFeature().getClient().getUssdProfileId().toString()).concat(
                        " ");
            }

            if (getRoot() != null) {
                return null;
            }

            return userCode.trim().replace(" ", splitter);
        }

        return null;

    }

    protected String getRoot() {
        return getNodeValue("ROOT");
    }

    protected String getShortCutCode() {
        return getNodeValue("SHORTCUT_CODE");
    }

    @Override
    public Userphone getUserphone() {
        if (userphone == null) {
            try {
                userphone = getFacadeContainer().getUserphoneAPI().findByMsisdnAndActiveAndClient(
                        getCellphoneNumber(), getClient().getClientCod(), true);
            } catch (Exception e) {
                userphone = null;
            }
        }
        return userphone;
    }

    protected Phone getPhone() throws InvalidOperationException {
        if (phone == null) {
            phone = getFacadeContainer().getPhoneAPI().findByMsisdnAndClient(
                    getCellphoneNumber(), getClient().getClientCod());
        }
        return phone;
    }

    @Override
    public Client getClient() throws InvalidOperationException {
        return getClientFeature().getClient();
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    protected FeatureElement getFeatureElement() {
        if (featureElement == null) {
            if (getRoot() != null) {
                featureElement = getFacadeContainer().getFeatureElementAPI().find(
                        Long.valueOf(getRoot()));
            }
        }
        return featureElement;
    }

    public void setFeatureElement(FeatureElement featureElement) {
        this.featureElement = featureElement;
    }

    public ClientFeature getClientFeature() throws InvalidOperationException {
        if (clientFeature == null) {
            String shortCut = getShortCutCode();
            clientFeature = getFacadeContainer().getClientFeatureAPI().findByShortcutCode(
                    shortCut);
            if (clientFeature == null) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "message.ussd.validator.InvalidShortcutCode"));
            }
        }
        return clientFeature;
    }

    public void setClientFeature(ClientFeature clientFeature) {
        this.clientFeature = clientFeature;
    }

    @Override
    public Application getApplication() {
        if (super.getApplication() == null) {
            try {
                setApplication(getFacadeContainer().getApplicationAPI().find(
                        Long.valueOf(getFacadeContainer().getGlobalParameterAPI().findByCode(
                                "application.code.ussd.feature"))));
            } catch (Exception e) {
                Logger.getLogger(AbstractServiceAPI.class.getName()).log(
                        Level.SEVERE, e.getMessage(), e);
            }
        }
        return super.getApplication();
    }

    @Override
    protected void assignServiceEvent() {
        if (getEntity().getEvent().compareToIgnoreCase("FIND") == 0) {
            tranType = FeatureEvent.FIND;
        } else if (getEntity().getEvent().compareToIgnoreCase("VALIDATE") == 0) {
            tranType = FeatureEvent.VALIDATE;
        } else {
            tranType = FeatureEvent.REGISTER;
        }
    }

    /**
     * Metodo que busca todos los códigos de entradas de USSD ingresados,
     * correspondientes a un formulario dinámico o encuesta. El código en el
     * HashMap de USSD que sea numérico, Ej: 10163000, que corresponde a una
     * entrada en el Formulario Dinamico, se guarda en una lista para ver cual
     * es el camino seleccionado por el userphone.
     * 
     * */
    public List<FeatureElementEntry> getPersistibleValuesInSelectedPath() {
        List<FeatureElementEntry> toReturn = new ArrayList<FeatureElementEntry>();

        List<Long> keyList = new ArrayList<Long>();
        for (Map.Entry<String, HashMap<String, String>> entry : nodes.entrySet()) {
            String key = entry.getKey();
            if (NumberUtil.isNumber(key)) {
                keyList.add(Long.parseLong(key));
            }
        }
        Collections.sort(keyList);

        for (Long key : keyList) {
            FeatureElementEntry fee = getFacadeContainer().getFeatureElementEntryAPI().find(
                    key);
            if (fee != null && fee.getCodFeatureEntryType().getPersistibleChr()) {
                toReturn.add(fee);
            }
        }
        return toReturn;

    }

}
