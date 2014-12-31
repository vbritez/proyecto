package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.entities.DynamicFormElementEntryService;
import com.tigo.cs.api.entities.DynamicFormElementService;
import com.tigo.cs.api.entities.DynamicFormService;
import com.tigo.cs.api.entities.DynamicFormValueDataService;
import com.tigo.cs.api.entities.DynamicFormValueService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.FeatureValue;
import com.tigo.cs.domain.FeatureValueData;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class FeatureServiceAPI<T extends BaseServiceBean> extends AbstractFeatureBasicServiceAPI<DynamicFormService> {

    public FeatureServiceAPI() {
    }

    @Override
    public DynamicFormService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new DynamicFormService());
            getEntity().setValueBean(new DynamicFormValueService());
        }
        return super.getEntity();
    }

    @Override
    public DynamicFormService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new DynamicFormService());
            getReturnEntity().setElementBean(
                    new ArrayList<DynamicFormElementService>());
        }
        return super.getReturnEntity();
    }

    @Override
    protected void assignServiceEvent() {
        String discriminator = getEntity().getEvent();
        if (discriminator.equals("REGISTER")) {
            tranType = FeatureEvent.REGISTER;
        } else if (discriminator.equals("FIND")) {
            tranType = FeatureEvent.FIND;
        } else if (discriminator.equals("VALIDATE")) {
            tranType = FeatureEvent.VALIDATE;
        }
        getReturnEntity().setEvent(getEntity().getEvent());
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        returnMessage = null;
        switch (tranType) {
        case REGISTER:

            try {
                /*
                 * creamos la cabecera del feature element
                 */

                FeatureValue featureValue = new FeatureValue();
                featureValue.setFeatureElement(getFeatureElement());
                featureValue.setMessage(getMessage());

                if (!getFeatureElement().getOpenChr()) {
                    if (getFeatureElement().getUserphones() != null
                        && getFeatureElement().getUserphones().size() > 0) {
                        featureValue.setUserphone(getUserphone());
                    }
                    if (getFeatureElement().getPhoneLists() != null
                        && getFeatureElement().getPhoneLists().size() > 0) {
                        featureValue.setPhone(getPhone());
                    }
                }

                featureValue = getFacadeContainer().getFeatureValueAPI().create(
                        featureValue);

                /*
                 * obtenemos las entradas que tiene
                 */

                for (DynamicFormValueDataService v : getEntity().getValueBean().getEntryList()) {
                    FeatureValueData data = new FeatureValueData();
                    FeatureElementEntry entry = getFacadeContainer().getFeatureElementEntryAPI().find(
                            v.getCodFeatureElementEntryBean());
                    data.setFeatureElementEntry(entry);
                    data.setDataChr(v.getData());
                    data.setFeatureValue(featureValue);

                    getFacadeContainer().getFeatureValueDataAPI().create(data);
                }

                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                        tranType.getSuccessMessage());
                returnMessage = MessageFormat.format(
                        returnMessage,
                        getFeatureElement().getClientFeature().getFeature().getDescriptionChr(),
                        getFeatureElement().getDescriptionChr());
                updateMessage(returnMessage);

            } catch (InvalidOperationException e) {
                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                        tranType.getNoMatchMessage());
                returnMessage = MessageFormat.format(
                        returnMessage,
                        getFeatureElement().getClientFeature().getFeature().getDescriptionChr(),
                        getFeatureElement().getDescriptionChr(), e.getMessage());
            } catch (Exception e) {
                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                        tranType.getNoMatchMessage());
                returnMessage = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                tranType.getNoMatchMessage()),
                        getFeatureElement().getClientFeature().getFeature().getDescriptionChr(),
                        getFeatureElement().getDescriptionChr(), "");
            }
            break;

        case FIND:

            List<FeatureElement> list = getFacadeContainer().getFeatureElementAPI().findByUserphone(
                    getUserphone());

            if (list == null || (list != null && list.size() <= 0)) {
                throw new InvalidOperationException("El usuario no posee formularios dinámicos disponibles en este momento");
            }

            /*
             * una ves que obtenemos la lista de formularios dinamicos que tiene
             * asignado el usuario, creamos el bean para enviarlo al usuario
             */
            getFacadeContainer().getNotifier().info(getClass(),
                    getEntity().getMsisdn(),
                    "Se obtiene la lista de formularios dinámicos a ser enviados a los usuarios.");

            /*
             * recorremos la lista de formularios para crear el bean a ser
             * enviado al usuario
             */

            for (FeatureElement featureElement : list) {

                getFacadeContainer().getNotifier().debug(
                        getClass(),
                        getEntity().getMsisdn(),
                        MessageFormat.format("Formulario Dinamico: {0}",
                                featureElement.getDescriptionChr()));

                DynamicFormElementService dynamicFormElementBean = new DynamicFormElementService();
                dynamicFormElementBean.setDynamicFormElementCod(featureElement.getFeatureElementCod());
                dynamicFormElementBean.setDescription(featureElement.getDescriptionChr());
                dynamicFormElementBean.setStartDate(featureElement.getStartPeriodDat());
                dynamicFormElementBean.setFinishDate(featureElement.getFinishPeriodDat());

                /*
                 * obtenemos la lista de entradas que estas asociadas al
                 * formulario dinamico
                 */

                List<FeatureElementEntry> entries = getFacadeContainer().getFeatureElementEntryAPI().getEagerFeatureElementEntrySortedList(
                        featureElement);

                /*
                 * por cada entrada del formulario creamos el bean a ser enviado
                 * al mismo
                 */
                dynamicFormElementBean.setEntryList(new ArrayList<DynamicFormElementEntryService>());
                for (FeatureElementEntry featureElementEntry : entries) {

                    DynamicFormElementEntryService dynamicFormElementEntryBean = new DynamicFormElementEntryService();
                    dynamicFormElementEntryBean.setEntryCod(featureElementEntry.getFeatureElementEntryCod());
                    dynamicFormElementEntryBean.setDescription(featureElementEntry.getDescriptionChr());
                    dynamicFormElementEntryBean.setFinalEntry(featureElementEntry.getFinalChr());
                    dynamicFormElementEntryBean.setOrderNum(featureElementEntry.getOrderNum());
                    dynamicFormElementEntryBean.setTitle(featureElementEntry.getTitleChr());
                    dynamicFormElementEntryBean.setMultiSelectOption(featureElementEntry.getMultiSelectOption());
                    if (featureElementEntry.getCodOwnerEntry() != null) {
                        dynamicFormElementEntryBean.setOwnerEntryCod(featureElementEntry.getCodOwnerEntry().getFeatureElementEntryCod());
                    }
                    dynamicFormElementEntryBean.setEntryTypeCod(featureElementEntry.getCodFeatureEntryType().getFeatureEntryTypeCod());

                    /*
                     * agregamos el elemento del formulario dinamico a la lista
                     * de entradas
                     */

                    getFacadeContainer().getNotifier().debug(
                            getClass(),
                            getEntity().getMsisdn(),
                            MessageFormat.format(
                                    "Formulario Dinamico: {0}. Agregando elemento de formulario: {1}",
                                    featureElement.getDescriptionChr(),
                                    featureElementEntry.getDescriptionChr()));

                    dynamicFormElementBean.getEntryList().add(
                            dynamicFormElementEntryBean);
                }
                if (dynamicFormElementBean.getEntryList().size() == 1) {
                    dynamicFormElementBean.getEntryList().add(
                            new DynamicFormElementEntryService());
                }
                /*
                 * agregamos en el objeto de
                 */
                getReturnEntity().getElementBean().add(dynamicFormElementBean);
            }
            if (getReturnEntity().getElementBean().size() == 1) {
                getReturnEntity().getElementBean().add(
                        new DynamicFormElementService());
            }

            getReturnEntity().setResponse("Formularios dinámicos actualizados");
            break;

        default:
            break;
        }

        return returnMessage;
    }

    @Override
    protected void validate() throws InvalidOperationException {

        super.validate();

        /*
         * verificamos si el feature tiene un limite de marcaciones
         */
        if (getFeatureElement() != null) {
            Integer count = getFacadeContainer().getFeatureValueAPI().count(
                    getFeatureElement());
            if (!(getFeatureElement().getMaxEntryNum() != null && count != null && count < getFeatureElement().getMaxEntryNum())) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "feature.ussd.DisabledFeature"));
            }
        }

        /*
         * Verificamos que no se hayan cargado valores nulos en el formulario
         */
        if (tranType == FeatureEvent.REGISTER) {
            for (DynamicFormValueDataService data : getEntity().getValueBean().getEntryList()) {
                if (data.getData() == null) {
                    throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                            "feature.ussd.validator.ValueCanNotBeNull"));
                }
            }
        }
    }

    @Override
    protected ServiceValue treatHeader() {
        return null;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        return null;
    }

    @Override
    public String getShortNumber() {
        try {
            return getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "ussd.shortNumber.feature");
        } catch (Exception e) {
            Logger.getLogger(AbstractServiceAPI.class.getName()).log(
                    Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

}
