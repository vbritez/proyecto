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
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.FeatureEntryType;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

@Stateless
public class FeatureEntryDynamicDriver extends AbstractFeatureBasicServiceAPI<DynamicFormService> implements DynamicEntryDriverInterface {

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

            FeatureElementEntry featureElementEntry = facadeContainer.getFeatureElementEntryAPI().find(
                    Long.valueOf(code));
            FeatureEntryType featureEntryType = facadeContainer.getFeatureEntryTypeAPI().find(
                    4L);
            setFeatureElement(featureElementEntry.getFeatureElement());

            init(getEntity());
            validate();

            facadeContainer.getNotifier().debug(
                    FeatureEntryDynamicDriver.class, null,
                    "Code que viene como parametro: " + code);
            facadeContainer.getNotifier().debug(
                    FeatureEntryDynamicDriver.class,
                    null,
                    "FeatureElement: "
                        + featureElementEntry.getFeatureElement().getFeatureElementCod()
                        + ":"
                        + featureElementEntry.getFeatureElement().getDescriptionChr());
            facadeContainer.getNotifier().debug(
                    FeatureEntryDynamicDriver.class,
                    null,
                    "FeatureElementEntry: "
                        + featureElementEntry.getFeatureElementEntryCod());
            facadeContainer.getNotifier().debug(
                    FeatureEntryDynamicDriver.class, null,
                    "FeatureEntryType: " + featureEntryType.getNameChr());

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
                        FeatureEntryDynamicDriver.class,
                        null,
                        "SI se encontro " + entriesTitle.size()
                            + " entradas de tipo OPTION");
            } else {
                facadeContainer.getNotifier().warn(
                        FeatureEntryDynamicDriver.class, null,
                        "NO se encontro entradas de tipo OPTION");
            }
            facadeContainer.getNotifier().info(FeatureEntryDynamicDriver.class,
                    null, entriesTitle.toString());

            response.setEntries(entriesTitle);

            return response;
        } catch (InvalidOperationException e) {
            response.setMessage(e.getMessage());
            response.setOk(false);
            facadeContainer.getNotifier().error(
                    FeatureEntryDynamicDriver.class, null, e.getMessage(), e);
            return response;
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setOk(false);
            facadeContainer.getNotifier().error(
                    FeatureEntryDynamicDriver.class, null, e.getMessage(), e);
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

}
