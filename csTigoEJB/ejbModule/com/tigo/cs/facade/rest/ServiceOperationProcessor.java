package com.tigo.cs.facade.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.entities.ServiceOperationService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.enumerate.ServiceIdentifier.Id;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.ServiceOperationServiceAPI;
import com.tigo.cs.api.util.ServiceResult;

@Stateless
@ServiceIdentifier(id = ServiceIdentifier.Id.OPERACIONES_SERVICIO)
public class ServiceOperationProcessor extends ServiceOperationServiceAPI<ServiceOperationService> {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
        switch (getEntity().getServiceToOperate().intValue()) {
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

        switch (tranType) {
        case DELETE_SERVICE:

            List<ServiceResult> resultList = getFacadeContainer().getServiceOperationAPI().lastMarks(
                    serviceName, getUserphone(), 4);

            Integer pos = getEntity().getPosition();

            ServiceResult result = resultList.get(pos - 1);
            getEntity().setId(
                    result.getServiceValue() != null ? result.getServiceValue().getServicevalueCod().toString() : result.getServiceValueDetail().getServicevaluedetailCod().toString());

            break;
        }
    }
}
