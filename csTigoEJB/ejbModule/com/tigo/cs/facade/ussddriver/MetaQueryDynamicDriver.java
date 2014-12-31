package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.dynamicentrydriver.DynamicEntryDriverInterface;
import py.com.lothar.ussddriverinterfaces.response.DynamicEntryResponse;

import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.enumerate.Restriction;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.AbstractServiceAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

@Stateless
public class MetaQueryDynamicDriver extends AbstractServiceAPI<BaseServiceBean> implements DynamicEntryDriverInterface {

    private static final String SC_DATA = "SC_DATA";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public DynamicEntryResponse requestEntry(String msisdn, String code, HashMap<String, HashMap<String, String>> hm) {

        DynamicEntryResponse response = new DynamicEntryResponse();
        try {
            setNodes(hm);
            setEntity(new BaseServiceBean());
            getEntity().setMsisdn(msisdn);
            getEntity().setGrossMessage(hm.toString());
            setGrossMessageIn(getEntity().getGrossMessage());
            init(getEntity());
            validate();

            List<MetaClient> metaClientList = facadeContainer.getMetaClientAPI().findByClientAndEnabled(
                    getClient().getClientCod(), true);

            response.setOk(true);
            List<String> entriesTitle = new ArrayList<String>();

            response.setMessage(facadeContainer.getI18nAPI().iValue(
                    getNodeValue(SC_DATA)));
            response.setTitle(facadeContainer.getI18nAPI().iValue(
                    getNodeValue(SC_DATA)));

            for (MetaClient entry : metaClientList) {
                entriesTitle.add(entry.getMeta().getDescriptionChr());
            }

            response.setEntries(entriesTitle);

            return response;
        } catch (InvalidOperationException e) {
            response.setOk(false);
            return response;
        } catch (Exception e) {
            response.setOk(false);
            facadeContainer.getNotifier().error(MetaQueryDynamicDriver.class,
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
    }
}
