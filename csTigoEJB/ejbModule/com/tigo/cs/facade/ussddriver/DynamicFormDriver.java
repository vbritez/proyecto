package com.tigo.cs.facade.ussddriver;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.DynamicFormService;
import com.tigo.cs.api.entities.DynamicFormValueDataService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.FeatureServiceAPI;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFeature;
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.FeatureEntryType;
import com.tigo.cs.domain.Userphone;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.FORMULARIO_DINAMICO)
public class DynamicFormDriver extends FeatureServiceAPI<DynamicFormService> implements DriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
        getEntity().setEvent("REGISTER");
    }

    @Override
    public void convertToBean() {
        List<DynamicFormValueDataService> dataList = new ArrayList<DynamicFormValueDataService>();

        for (FeatureElementEntry entry : getPersistibleValuesInSelectedPath()) {
            String value = getNodeValue(entry.getFeatureElementEntryCod().toString());
            if (value != null) {
                if (entry.getCodFeatureEntryType().getFeatureEntryTypeCod() == 5L) {
                    FeatureEntryType featureEntryType = getFacadeContainer().getFeatureEntryTypeAPI().find(
                            4L);
                    List<FeatureElementEntry> entradas = getFacadeContainer().getFeatureElementEntryAPI().findByOwnerAndType(
                            getFeatureElement(), entry, featureEntryType);

                    try {
                        value = entradas.get(Integer.valueOf(value) - 1).getTitleChr();
                    } catch (Exception e) {
                        returnMessage = MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "feature.message.InputValueError"),
                                entry);
                        value = null;
                    }
                }
                if (entry.getCodFeatureEntryType().getFeatureEntryTypeCod() == 1L) {
                    FeatureElementEntry selected = getFacadeContainer().getFeatureElementEntryAPI().findByCode(
                            Long.parseLong(value));
                    value = selected.getTitleChr();
                }
            }
            DynamicFormValueDataService data = new DynamicFormValueDataService();
            data.setCodFeatureElementEntryBean(entry.getFeatureElementEntryCod());
            data.setData(value);
            data.setServiceCod(getService().getServiceCod().intValue());

            dataList.add(data);
        }

        getEntity().getValueBean().setEntryList(dataList);
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

}
