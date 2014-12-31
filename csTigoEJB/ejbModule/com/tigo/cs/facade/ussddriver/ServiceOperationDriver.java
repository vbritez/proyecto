package com.tigo.cs.facade.ussddriver;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.driver.DriverInterface;

import com.tigo.cs.api.entities.ServiceOperationService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.enumerate.ServiceIdentifier.Id;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.ServiceOperationServiceAPI;
import com.tigo.cs.api.util.ServiceResult;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Service;

/***
 * 
 * Driver para guardar el metadata creado por el cliente desde USSD.
 * 
 * 
 * @author exttnu
 * 
 */

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.OPERACIONES_SERVICIO)
public class ServiceOperationDriver extends ServiceOperationServiceAPI<ServiceOperationService> implements DriverInterface {

    private static final String SC_SERVICE = "SC_SERVICE";
    private static final String SERVICECODE_POS = "SERVICECODE_POS";
    private static final String MARKING_POS = "MARKING_POS";

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public void convertToBean() {
    }

    @Override
    protected void assignEvent() throws InvalidOperationException {
        try {
            // TRAER LOS SERVICIOS Y BUSCAR EL SELECCIONADO
            Integer pos = Integer.parseInt(getNodeValue(SERVICECODE_POS));
            List<Service> serviceList = facadeContainer.getServiceAPI().getServiceByUserphoneAndFuncionality(
                    getUserphone().getUserphoneCod(), 6L);
            Service service = serviceList.get(pos - 1);

            getEntity().setServiceToOperate(service.getServiceCod());
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

            // SETEAR EL EVENTO
            List<ServiceResult> resultList = null;
            String discriminator = getNodeValue(SC_SERVICE);
            if (discriminator.equals(ServiceOperationEvent.DELETE_SERVICE.getDescription())) {
                tranType = ServiceOperationEvent.DELETE_SERVICE;
                resultList = facadeContainer.getServiceOperationAPI().lastMarks(
                        serviceName, getUserphone(), 4);
                getEntity().setEvent(ServiceOperationEvent.DELETE_SERVICE.getDescription());

            }

            // TRAER LAS MARCACIONES Y BUSCAR EL SELECCIONADO
            pos = Integer.parseInt(getNodeValue(MARKING_POS));

            ServiceResult result = resultList.get(pos - 1);
            getEntity().setId(
                    result.getServiceValue() != null ? result.getServiceValue().getServicevalueCod().toString() : result.getServiceValueDetail().getServicevaluedetailCod().toString());

        } catch (Exception e) {
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.ERROR, e.getMessage());
            String msg = getFacadeContainer().getI18nAPI().iValue(
                    "error.DispatchmentGeneralError");
            throw new InvalidOperationException(msg);
        }
    }

    @Override
    public Long getFunctionality() {
        return 6L;
    }

}
