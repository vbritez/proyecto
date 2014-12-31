package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.tigo.cs.api.entities.ServiceOperationService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.enumerate.ServiceIdentifier.Id;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.api.util.ServiceResult;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class ServiceOperationServiceAPI<T extends ServiceOperationService> extends AbstractServiceAPI<ServiceOperationService> {

    private static final String ENT = "ENT";
    private static final String SAL = "SAL";
    private static final String ENTSAL = "ENTSAL";
    protected ServiceOperationEvent tranType;
    protected Id serviceName;

    protected enum ServiceOperationEvent implements ServiceEvent {

        RETRIEVE_DELETABLE_SERVICE("RETDS", new ServiceProps("serviceoperation.name.RetrieveDeletableService", "", "")),
        DELETE_SERVICE("serviceoperation.name.DeleteService", new ServiceProps("serviceoperation.name.DeleteService", "", ""));
        protected final String value;
        protected final ServiceProps props; // serviceoperation.name.DeleteService

        private ServiceOperationEvent(String value, ServiceProps props) {
            this.value = value;
            this.props = props;
        }

        @Override
        public String getSuccessMessage() {
            return props.getSuccesMessage();
        }

        @Override
        public String getNoMatchMessage() {
            return props.getNoMatchMessage();
        }

        @Override
        public String getDescription() {
            return props.getDescription();
        }

        public String getValue() {
            return value;
        }
    }

    public ServiceOperationServiceAPI() {
    }

    @Override
    public ServiceOperationService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new ServiceOperationService());
        }
        return super.getEntity();
    }

    @Override
    public ServiceOperationService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new ServiceOperationService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        String returnMessage = tranType.value;
        getReturnEntity().setResponseList(new ArrayList<String>());
        switch (tranType) {
        case DELETE_SERVICE:
            Service service = getFacadeContainer().getServiceAPI().find(
                    serviceName.value());
            try {
                eliminateMarking(serviceName, Long.valueOf(getEntity().getId()));
                getEntity().setEncryptResponse(false);
            } catch (Exception e) {
                returnMessage = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "service.operation.messages.CouldNotDeleteMarking"),
                        getEntity().getId(), service.getDescriptionChr());
                String response =  MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "service.operation.messages.CouldNotDeleteMarking"),
                        getEntity().getId(), service.getDescriptionChr());
                getReturnEntity().setResponse(response);
                break;
            }
            returnMessage = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue("service.operation.messages.MarkingSuccesfullyDeleted"),
                    service.getDescriptionChr(), new Long(getEntity().getId())); 
            
            String response = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "service.operation.messages.MarkingSuccesfullyDeleted"),
                    service.getDescriptionChr(), new Long(getEntity().getId()));
            getReturnEntity().setResponse(response);
            break;

        case RETRIEVE_DELETABLE_SERVICE:

            List<ServiceResult> serviceResults = getFacadeContainer().getServiceOperationAPI().lastMarks(
                    serviceName, getUserphone(), 4);

            String pattern = "{0}%*{1,number,#}%*{2}";
            String serviceResultString = "";
            if (serviceResults != null && serviceResults.size() > 0) {
                for (ServiceResult serviceResult : serviceResults) {
                    serviceResultString = serviceResultString.concat(serviceResult.getResult()).concat("  ");
                    getReturnEntity().getResponseList().add(serviceResult.getResult());
                }
            } else {
                throw new InvalidOperationException("No se recupero ninguna marcacion para el servicio");
            }
            serviceResultString = serviceResultString.trim().replace("  ", "%*");

            returnMessage = MessageFormat.format(pattern, tranType.getValue(),
                    getEntity().getServiceToOperate(), serviceResultString);

            setServiceCod(ServiceIdentifier.Id.OPERACIONES_SERVICIO.value());
            
            break;

        }

        if (getReturnEntity().getResponseList().size() == 1) {
            getReturnEntity().getResponseList().add("");
        }
        
        getReturnEntity().setServiceToOperate(serviceName.value());
        getReturnEntity().setEvent(getEntity().getEvent());
        return returnMessage;
    }

    @Override
    protected void assignServiceEvent() {
        if (getEntity().getEvent().equals(ServiceOperationEvent.RETRIEVE_DELETABLE_SERVICE.getValue())) {
            tranType = ServiceOperationEvent.RETRIEVE_DELETABLE_SERVICE;
        } else if (getEntity().getEvent().equals(ServiceOperationEvent.DELETE_SERVICE.getValue())) {
            tranType = ServiceOperationEvent.DELETE_SERVICE;
        } 
    }

    @Override
    protected ServiceValue treatHeader() {
        return null;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        return null;
    }

    private void eliminateMarking(Id service, Long id) {
        ServiceValue sv = null;
        ServiceValueDetail svd = null;
        switch (service) {
        case VISITA:
            svd = deleteEntEntSal(service.value(), id);
            deleteHeader(svd.getServiceValue());
            break;

        case VISITA_PEDIDO:
            svd = deleteVisitOrder(service.value(), id);

            /*
             * Recuperamos todas las cabeceras correspondiente a productos de la
             * entrada que se desea eliminar
             */
            List<ServiceValue> listSD = getFacadeContainer().getServiceValueAPI().findSD(
                    svd.getServiceValue(), svd);

            if (listSD != null) {
                for (ServiceValue sd : listSD) {
                    deleteHeaderDetail(sd);
                }
            }

            break;

        case PEDIDO:
        case COBRANZA:
        case DELIVERY:
        case COURRIER:
        case FLOTA:
            sv = getFacadeContainer().getServiceValueAPI().find(id);
            deleteHeaderDetail(sv);
            break;

        case INVENTARIO_STANDARD:
            svd = getFacadeContainer().getServiceValueDetailAPI().find(id);
            disableDetail(svd);
            break;

        case VISITA_MEDICA:
            sv = getFacadeContainer().getServiceValueAPI().find(id);
            deleteHeaderDetail(sv);

            // SE RECUPERAN LAS CABECERAS DE PRODUCTOS ASOCIADOS AL MEDICO PARA
            // BORRARLAS
            List<ServiceValue> productHeaderList = getFacadeContainer().getServiceValueAPI().findProductHeaderForVisitMedic(
                    sv.getServicevalueCod().toString());

            if (productHeaderList != null) {
                for (ServiceValue sd : productHeaderList) {
                    deleteHeaderDetail(sd);
                }
            }

            // SE VERIFICA SI LA CABECERA DE CONSULTORIO SE QUEDO SIN MEDICOS,
            // SI ES ASI, SE BORRA
            Integer count = getFacadeContainer().getServiceValueAPI().getCountMedicsForClinic(
                    sv.getColumn1Chr());
            if (count != null && count == 0) {
                ServiceValue svClinic = getFacadeContainer().getServiceValueAPI().find(
                        new Long(sv.getColumn1Chr()));
                deleteHeaderDetail(svClinic);
            }
            break;

        default:
            break;
        }
    }

    public ServiceValue deleteHeaderDetail(ServiceValue sv) {
        /*
         * Obtenemos el service value cuyo id corresponde al pasado como
         * par�metro
         */
        // ServiceValue sv =
        // getFacadeContainer().getServiceValueAPI().find(svId);
        /*
         * Recuperamos la lista de service value detail asociado al sv y luego
         * lo recorremos para deshabilitarlo
         */
        List<ServiceValueDetail> listSvd = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                sv);

        if (listSvd != null) {
            for (ServiceValueDetail servicesValueDetail : listSvd) {
                servicesValueDetail.setEnabledChr(false);
                getFacadeContainer().getServiceValueDetailAPI().edit(
                        servicesValueDetail);
            }
        }
        deleteHeader(sv);
        return sv;
    }

    public void deleteHeader(ServiceValue serviceValue) {
        /*
         * Recuperamos la cantidad de registros habilitados en el SVD asociados
         * con el service value, y en caso que sean 0 lo deshabilitamos.
         */
        Long countEnabled2 = getFacadeContainer().getServiceValueDetailAPI().getCountServiceValueEnabled(
                serviceValue.getServicevalueCod());

        if (countEnabled2 != null && countEnabled2.equals(0L)) {
            disableHeader(serviceValue);
        }

    }

    public Long getFunctionality() {
        return 6L;
    }

//    public Long getServiceCod() {
//        if (serviceCod == null) {
//            ServiceIdentifier annotation = getClass().getAnnotation(
//                    ServiceIdentifier.class);
//            if (annotation != null) {
//                serviceCod = new Long(getEntity().getServiceToOperate());
//            }
//        }
//        return serviceCod;
//    }

    public ServiceValueDetail deleteEntEntSal(Long serviceCode, Long svdCode) {
        /*
         * Obtenemos el service value detail cuyo id corresponde al pasado como
         * par�metro
         */
        ServiceValueDetail svd = getFacadeContainer().getServiceValueDetailAPI().find(
                svdCode);
        /*
         * En caso de que el registro corresponda a una ENTRADA recuperamos el
         * registro de SALIDA del mismo
         */
        if (svd.getColumn1Chr().equals(ENT)) {
            ServiceValueDetail svdSal = getFacadeContainer().getServiceValueDetailAPI().getServiceValueDetailNextId(
                    getUserphone().getUserphoneCod(), serviceCode, svdCode);
            /*
             * Verificamos que el registro devuelto corresponda a una salida y
             * lo deshabilitamos
             */
            if (svdSal != null && svdSal.getColumn1Chr().equals(SAL)) {
                svdSal.setEnabledChr(false);
                getFacadeContainer().getServiceValueDetailAPI().edit(svdSal);
            }

            disableDetail(svd);
            /*
             * En caso que el registro devuelto corresponda a una visita rapida,
             * lo deshabilitamos directamente
             */
        } else if (svd.getColumn1Chr().equals(ENTSAL)) {
            disableDetail(svd);
        }

        return svd;
    }

    public ServiceValueDetail deleteVisitOrder(Long serviceCode, Long svdCode) {
        /*
         * Obtenemos el service value detail cuyo id corresponde al pasado como
         * par�metro
         */
        ServiceValueDetail svd = getFacadeContainer().getServiceValueDetailAPI().find(
                svdCode);
        /*
         * En caso de que el registro corresponda a una ENTRADA recuperamos el
         * registro de SALIDA del mismo
         */
        if (svd.getColumn1Chr().equals(ENT)) {
            ServiceValueDetail svdSal = getFacadeContainer().getServiceValueDetailAPI().getServiceValueDetailSAL(
                    getUserphone().getUserphoneCod(), serviceCode, svdCode);
            /*
             * Verificamos que el registro devuelto corresponda a una salida y
             * lo deshabilitamos
             */
            if (svdSal != null && svdSal.getColumn1Chr().equals(SAL)) {
                svdSal.setEnabledChr(false);
                getFacadeContainer().getServiceValueDetailAPI().edit(svdSal);
            }

            disableDetail(svd);
            /*
             * En caso que el registro devuelto corresponda a una visita rapida,
             * lo deshabilitamos directamente
             */
        } else if (svd.getColumn1Chr().equals(ENTSAL)) {
            disableDetail(svd);
        }

        return svd;
    }

    private void disableHeader(ServiceValue serviceValue) {
        serviceValue.setEnabledChr(false);
        getFacadeContainer().getServiceValueAPI().edit(serviceValue);
    }

    private void disableDetail(ServiceValueDetail svd) {
        svd.setEnabledChr(false);
        getFacadeContainer().getServiceValueDetailAPI().edit(svd);
    }
}
