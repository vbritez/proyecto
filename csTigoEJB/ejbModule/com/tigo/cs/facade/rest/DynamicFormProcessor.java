package com.tigo.cs.facade.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.DynamicFormService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.FeatureServiceAPI;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFeature;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.Userphone;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.FORMULARIO_DINAMICO)
public class DynamicFormProcessor extends FeatureServiceAPI<DynamicFormService> {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    protected void assignEvent() throws InvalidOperationException {
    }

    @Override
    public void convertToBean() throws InvalidOperationException, NumberFormatException, InvalidOperationException {
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public ClientFeature getClientFeature() throws InvalidOperationException {
        try {
            if (clientFeature == null) {
                Long dynamicFormFeatureCod;

                dynamicFormFeatureCod = Long.parseLong(getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "feature.dynamicform.code"));

                clientFeature = getFacadeContainer().getClientFeatureAPI().getClientFeature(
                        getClient().getClientCod(), dynamicFormFeatureCod);
                if (clientFeature == null) {
                    throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                            "message.ussd.validator.InvalidShortcutCode"));
                }
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    getFacadeContainer().getI18nAPI().iValue(
                            "message.feature.dynamicform.GlobalParameter"), e);
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "restriction.GenericError"));

        }
        return clientFeature;
    }

    @Override
    public Client getClient() {
        return getUserphone().getClient();
    }

    @Override
    public Userphone getUserphone() {
        if (userphone == null) {
            try {
                userphone = getFacadeContainer().getUserphoneAPI().findByMsisdnAndActive(
                        getCellphoneNumber(), true);
            } catch (Exception e) {
                userphone = null;
            }
        }
        return userphone;
    }

    @Override
    protected FeatureElement getFeatureElement() {
        if (featureElement == null) {
            if (getEntity().getDynamicFormCod() != null) {
                featureElement = getFacadeContainer().getFeatureElementAPI().find(
                        getEntity().getDynamicFormCod());
            }
        }
        return featureElement;
    }

}
