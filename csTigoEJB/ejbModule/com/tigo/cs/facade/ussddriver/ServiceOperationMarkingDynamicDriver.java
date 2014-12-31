package com.tigo.cs.facade.ussddriver;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.dynamicentrydriver.DynamicEntryDriverInterface;
import py.com.lothar.ussddriverinterfaces.response.DynamicEntryResponse;

import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.enumerate.Restriction;
import com.tigo.cs.api.enumerate.ServiceIdentifier.Id;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.AbstractServiceAPI;
import com.tigo.cs.api.util.ServiceResult;
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
public class ServiceOperationMarkingDynamicDriver extends AbstractServiceAPI<BaseServiceBean> implements DynamicEntryDriverInterface {

    private static final String SERVICECODE_POS = "SERVICECODE_POS";

    @EJB
    private FacadeContainer facadeContainer;
    private Id serviceName;

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

            List<String> entriesTitle = new ArrayList<String>();

            List<ServiceResult> serviceResults = facadeContainer.getServiceOperationAPI().lastMarks(
                    serviceName, getUserphone(), 4);
            if (serviceResults == null || serviceResults.size() == 0) {
                response.setOk(false);
                Service service = getFacadeContainer().getServiceAPI().find(
                        serviceName.value());
                response.setMessage(MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "service.operation.messages.NoMarkings"),
                        service.getDescriptionChr()));
                return response;
            }
            response.setOk(true);
            response.setMessage(facadeContainer.getI18nAPI().iValue(
                    "service.operation.markings"));
            response.setTitle(facadeContainer.getI18nAPI().iValue(
                    "service.operation.markings"));

            for (ServiceResult entry : serviceResults) {
                entriesTitle.add(entry.getResult());
            }

            response.setEntries(entriesTitle);

            return response;
        } catch (InvalidOperationException e) {
            response.setMessage(e.getMessage());
            response.setOk(false);
            facadeContainer.getNotifier().error(
                    ServiceOperationMarkingDynamicDriver.class, null,
                    e.getMessage(), e);
            return response;
        } catch (Exception e) {
            response.setOk(false);
            String msg = getFacadeContainer().getI18nAPI().iValue(
                    "error.DispatchmentGeneralError");
            facadeContainer.getNotifier().error(
                    ServiceOperationMarkingDynamicDriver.class, null,
                    e.getMessage(), e);
            response.setMessage(msg);
            return response;
        }
    }

    @Override
    protected void assignEvent() {
        Integer pos = Integer.parseInt(getNodeValue(SERVICECODE_POS));
        List<Service> serviceList = facadeContainer.getServiceAPI().getServiceByUserphoneAndFuncionality(
                getUserphone().getUserphoneCod(), 6L);
        Service service = serviceList.get(pos - 1);

        switch (service.getServiceCod().intValue()) {
        case 1:
            serviceName = Id.VISITA;
            break;
        case 2:
            serviceName = Id.PEDIDO;
            break;
        case 3:
            serviceName = Id.VISITA_PEDIDO;
            break;
        case 5:
            serviceName = Id.COBRANZA;
            break;
        case 7:
            serviceName = Id.DELIVERY;
            break;
        case 10:
            serviceName = Id.INVENTARIO_STANDARD;
            break;
        case 12:
            serviceName = Id.FLOTA;
            break;
        case 17:
            serviceName = Id.VISITA_MEDICA;
            break;
        case 18:
            serviceName = Id.COURRIER;
            break;
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
