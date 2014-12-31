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
import com.tigo.cs.api.enumerate.ServiceIdentifier.Id;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.FeatureServiceAPI;
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.FeatureEntryType;
import com.tigo.cs.domain.Service;

@Stateless
@ServiceIdentifier(id = Id.PRESTACIONES)
public class FeatureDriver extends FeatureServiceAPI<DynamicFormService> implements DriverInterface {

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
    public Service getService() {
        if (service == null) {
            service = getFacadeContainer().getServiceAPI().find(getServiceCod());
        }
        return service;
    }

    @Override
    public Long getServiceCod() {
        if (serviceCod == null) {
            ServiceIdentifier annotation = getClass().getAnnotation(
                    ServiceIdentifier.class);
            if (annotation != null) {
                serviceCod = annotation.id().value();
            }
        }
        return serviceCod;
    }

}
