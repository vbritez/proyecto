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
import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

/***
 * 
 * Driver dinamico para crear la lista de Metas que se le va a devolver al
 * cliente, de las cuales va a elegir para crear un nuevo Metadata.
 * 
 * @author exttnu
 * 
 */

@Stateless
public class MetaCrudDynamicDriver extends AbstractServiceAPI<BaseServiceBean> implements DynamicEntryDriverInterface {

    private static final String SC_DATA = "SC_DATA";

    @EJB
    private FacadeContainer facadeContainer;
    private MetaCrudEvent event;

    protected enum MetaCrudEvent {

        CREATE(1, "metadata.name.create"),
        READ(2, "metadata.name.read"),
        UPDATE(3, "metadata.name.update"),
        DELETE(4, "metadata.name.delete");
        protected final int value;
        protected final String description;

        private MetaCrudEvent(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public int getValue() {
            return value;
        }
    }

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
            assignEvent();

            List<Meta> umList = null;

            switch (event) {
            case CREATE:
                umList = facadeContainer.getUserphoneMetaAPI().getUserphoneMetaList(
                        getUserphone(), true, false, false);
                break;
            case UPDATE:
                umList = facadeContainer.getUserphoneMetaAPI().getUserphoneMetaList(
                        getUserphone(), false, true, false);
                break;
            case DELETE:
                umList = facadeContainer.getUserphoneMetaAPI().getUserphoneMetaList(
                        getUserphone(), false, false, true);
                break;
            case READ:
                umList = facadeContainer.getMetaAPI().findMetaByClientAndEnabled(
                        getClient().getClientCod(), true);
                break;
            default:
                break;
            }

            if (umList == null || umList.size() == 0) {
                response.setOk(false);
                response.setMessage(getFacadeContainer().getI18nAPI().iValue(
                        "metadata.crud.messages.DeniedOperation"));
                return response;
            }
            response.setOk(true);
            List<String> entriesTitle = new ArrayList<String>();

            response.setMessage(facadeContainer.getI18nAPI().iValue(
                    event.getDescription()));
            response.setTitle(facadeContainer.getI18nAPI().iValue(
                    event.getDescription()));

            for (Meta entry : umList) {
                entriesTitle.add(entry.getDescriptionChr());
            }

            response.setEntries(entriesTitle);

            return response;
        } catch (InvalidOperationException e) {
            response.setOk(false);
            return response;
        } catch (Exception e) {
            response.setOk(false);
            facadeContainer.getNotifier().error(MetaCrudDynamicDriver.class,
                    null, e.getMessage(), e);
            return response;
        }
    }

    @Override
    protected void assignEvent() {
        String discriminator = getNodeValue(SC_DATA);
        if (discriminator.equals(MetaCrudEvent.CREATE.getDescription())) {
            event = MetaCrudEvent.CREATE;
        } else if (discriminator.equals(MetaCrudEvent.UPDATE.getDescription())) {
            event = MetaCrudEvent.UPDATE;
        } else if (discriminator.equals(MetaCrudEvent.DELETE.getDescription())) {
            event = MetaCrudEvent.DELETE;
        } else if (discriminator.equals(MetaCrudEvent.READ.getDescription())) {
            event = MetaCrudEvent.READ;
        }
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
