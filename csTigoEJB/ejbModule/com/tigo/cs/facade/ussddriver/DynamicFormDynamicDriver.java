package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.dynamicentrydriver.DynamicEntryDriverInterface;
import py.com.lothar.ussddriverinterfaces.response.DynamicEntryResponse;

import com.tigo.cs.api.entities.DynamicFormService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.AbstractFeatureBasicServiceAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFeature;
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.FeatureEntryType;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userphone;

@Stateless
public class DynamicFormDynamicDriver extends AbstractFeatureBasicServiceAPI<DynamicFormService> implements DynamicEntryDriverInterface {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public DynamicEntryResponse requestEntry(String msisdn, String code, HashMap<String, HashMap<String, String>> hm) {

        DynamicEntryResponse response = new DynamicEntryResponse();
        try {
            setNodes(hm);
            setEntity(new DynamicFormService());
            getEntity().setMsisdn(msisdn);
            getEntity().setGrossMessage(hm.toString());
            setGrossMessageIn(getEntity().getGrossMessage());
            init(getEntity());
            validate();

            FeatureElementEntry featureElementEntry = facadeContainer.getFeatureElementEntryAPI().find(
                    Long.valueOf(code));
            FeatureEntryType featureEntryType = facadeContainer.getFeatureEntryTypeAPI().find(
                    4L);

            facadeContainer.getNotifier().debug(DynamicFormDynamicDriver.class,
                    null, "Code que viene como parametro: " + code);
            facadeContainer.getNotifier().debug(
                    DynamicFormDynamicDriver.class,
                    null,
                    "FeatureElement: "
                        + featureElementEntry.getFeatureElement().getFeatureElementCod()
                        + ":"
                        + featureElementEntry.getFeatureElement().getDescriptionChr());
            facadeContainer.getNotifier().debug(
                    DynamicFormDynamicDriver.class,
                    null,
                    "FeatureElementEntry: "
                        + featureElementEntry.getFeatureElementEntryCod());
            facadeContainer.getNotifier().debug(DynamicFormDynamicDriver.class,
                    null, "FeatureEntryType: " + featureEntryType.getNameChr());

            List<FeatureElementEntry> entradas = facadeContainer.getFeatureElementEntryAPI().findByOwnerAndType(
                    featureElementEntry.getFeatureElement(),
                    featureElementEntry, featureEntryType);

            response.setOk(true);
            List<String> entriesTitle = new ArrayList<String>();
            response.setMessage(featureElementEntry.getTitleChr());
            response.setTitle(featureElementEntry.getTitleChr());
            for (FeatureElementEntry entry : entradas) {
                entriesTitle.add(entry.getTitleChr());
            }

            if (entriesTitle.size() > 0) {
                facadeContainer.getNotifier().info(
                        DynamicFormDynamicDriver.class,
                        null,
                        "SI se encontro " + entriesTitle.size()
                            + " entradas de tipo OPTION");
            } else {
                facadeContainer.getNotifier().warn(
                        DynamicFormDynamicDriver.class, null,
                        "NO se encontro entradas de tipo OPTION");
            }
            facadeContainer.getNotifier().info(DynamicFormDynamicDriver.class,
                    null, entriesTitle.toString());

            response.setEntries(entriesTitle);

            return response;
        } catch (InvalidOperationException e) {
            response.setMessage(e.getMessage());
            response.setOk(false);
            facadeContainer.getNotifier().error(DynamicFormDynamicDriver.class,
                    null, e.getMessage(), e);
            return response;
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setOk(false);
            facadeContainer.getNotifier().error(DynamicFormDynamicDriver.class,
                    null, e.getMessage(), e);
            return response;
        }
    }

    @Override
    protected void assignEvent() {
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        throw new UnsupportedOperationException("No debe invocarse este metodo");
    }

    @Override
    protected ServiceValue treatHeader() {
        throw new UnsupportedOperationException("No debe invocarse este metodo");
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        throw new UnsupportedOperationException("No debe invocarse este metodo");
    }

    @Override
    public void convertToBean() {
        throw new UnsupportedOperationException("No debe invocarse este metodo");
    }

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }
    
    public void validateEntries(){    	
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
