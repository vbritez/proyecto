package com.tigo.cs.facade.ussddriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.dynamicentrydriver.DynamicEntryDriverInterface;
import py.com.lothar.ussddriverinterfaces.response.DynamicEntryResponse;

import com.tigo.cs.api.entities.ServiceOperationService;
import com.tigo.cs.api.enumerate.Restriction;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.ServiceOperationServiceAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Service;
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
public class ServiceOperationDynamicDriver extends ServiceOperationServiceAPI<ServiceOperationService> implements DynamicEntryDriverInterface {

    private static final String SC_SERVICE = "SC_SERVICE";

    @EJB
    private FacadeContainer facadeContainer;

    private ServiceOperationEvent event;

    protected enum ServiceOperationEvent {

        DELETE_SERVICE(1, "serviceoperation.name.DeleteService");
        protected final int value;
        protected final String description;

        private ServiceOperationEvent(int value, String description) {
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
            setEntity(new ServiceOperationService());
            getEntity().setMsisdn(msisdn);
            getEntity().setGrossMessage(hm.toString());
            setGrossMessageIn(getEntity().getGrossMessage());
            init(getEntity());
            validate();
            assignEvent();

            List<String> entriesTitle = new ArrayList<String>();
            List<Service> serviceList = null;

            switch (event) {
            case DELETE_SERVICE:
                serviceList = facadeContainer.getServiceAPI().getServiceByUserphoneAndFuncionality(
                        getUserphone().getUserphoneCod(), 6L);
                if (serviceList == null || serviceList.size() == 0) {
                    response.setOk(false);
                    response.setMessage(getFacadeContainer().getI18nAPI().iValue(
                            "service.operation.messages.DeniedOperation"));
                    return response;
                }

                break;
            default:
                break;
            }
            response.setOk(true);
            response.setMessage(facadeContainer.getI18nAPI().iValue(
                    event.getDescription()));
            response.setTitle(facadeContainer.getI18nAPI().iValue(
                    event.getDescription()));

            for (Service entry : serviceList) {
                entriesTitle.add(entry.getDescriptionChr());
            }
            response.setEntries(entriesTitle);

            return response;
        } catch (InvalidOperationException e) {
            response.setMessage(e.getMessage());
            response.setOk(false);
            facadeContainer.getNotifier().error(
                    ServiceOperationDynamicDriver.class, null, e.getMessage(),
                    e);
            return response;
        } catch (Exception e) {
            response.setOk(false);
            String msg = getFacadeContainer().getI18nAPI().iValue(
                    "error.DispatchmentGeneralError");
            facadeContainer.getNotifier().error(
                    ServiceOperationDynamicDriver.class, null, e.getMessage(),
                    e);
            response.setTitle(msg);
            return response;
        }
    }

    @Override
    protected void assignEvent() {
        String discriminator = getNodeValue(SC_SERVICE);
        if (discriminator.equals(ServiceOperationEvent.DELETE_SERVICE.getDescription())) {
            event = ServiceOperationEvent.DELETE_SERVICE;
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
