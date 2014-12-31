package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigo.cs.api.entities.FleetService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class FleetServiceAPI<T extends FleetService> extends AbstractServiceAPI<FleetService> {

    protected FleetEvent tranType;
    public static final String RETIRED_VEHICLE = "RETIRED";
    public static final String TRAVEL = "TRAVEL";
    public static final String RETURNED_VEHICLE = "RETURNED";

    public enum FleetEvent implements ServiceEvent {

        RETIRED_VEHICLE(1, new ServiceProps("fleet.name.RetiredVehicle", "fleet.message.RetiredVehicle", "")),
        RETURNED_VEHICLE(2, new ServiceProps("fleet.name.ReturnedVehicle", "fleet.message.ReturnedVehicle", "")),
        TRAVEL(3, new ServiceProps("fleet.name.Travel", "fleet.message.Travel", ""));
        protected final int value;
        protected final ServiceProps props;

        private FleetEvent(int value, ServiceProps props) {
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
    }

    public FleetServiceAPI() {
    }

    @Override
    public FleetService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new FleetService());
        }
        return super.getEntity();
    }

    @Override
    public FleetService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new FleetService());
        }
        return super.getReturnEntity();
    }

    @Override
    protected void assignServiceEvent() {
        String discriminator = getEntity().getEvent();
        if (discriminator.equals("FRT")) {
            tranType = FleetEvent.RETIRED_VEHICLE;
        } else if (discriminator.equals("FDV")) {
            tranType = FleetEvent.RETURNED_VEHICLE;
        } else if (discriminator.equals("FRC")) {
            tranType = FleetEvent.TRAVEL;
        }
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        ServiceValue serviceValue = null;
        ServiceValueDetail serviceValueDetail = null;
        String returnMessage = "";

        String finalClientCod = getEntity().getClientCode();
        String driver = "";
        String vehicle = "";
        Long ref = null;

        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());

        switch (tranType) {
        case RETIRED_VEHICLE:

            serviceValue = treatRetiredVehicle();
            driver = serviceValue.getColumn1Chr();
            vehicle = serviceValue.getColumn2Chr();

            ref = serviceValue.getServicevalueCod();

            driver = getMetaForIntegrationValue(serviceValue.getColumn1Chr(),
                    MetaNames.EMPLOYEE);
            vehicle = getMetaForIntegrationValue(serviceValue.getColumn2Chr(),
                    MetaNames.VECHICLE);

            returnMessage = MessageFormat.format(returnMessage, vehicle,
                    driver, ref);
            break;
        case RETURNED_VEHICLE:

            serviceValue = treatReturnedVehicle();
            vehicle = serviceValue.getColumn2Chr();

            ref = serviceValue.getServicevalueCod();

            vehicle = getMetaForIntegrationValue(serviceValue.getColumn2Chr(),
                    MetaNames.VECHICLE);

            returnMessage = MessageFormat.format(returnMessage, vehicle,
                    serviceValue.getColumn8Chr(), ref);
            // addRouteDetail(getUserphone(), getMessage().getCell());
            break;
        case TRAVEL:

            serviceValueDetail = treatTravel();

            ref = serviceValueDetail != null ? serviceValueDetail.getServicevaluedetailCod() : null;

            finalClientCod = serviceValueDetail.getColumn1Chr();

            finalClientCod = getMetaForIntegrationValue(
                    serviceValueDetail.getColumn1Chr(), MetaNames.CLIENT);

            vehicle = serviceValueDetail.getServiceValue().getColumn2Chr();

            vehicle = getMetaForIntegrationValue(
                    serviceValueDetail.getServiceValue().getColumn2Chr(),
                    MetaNames.VECHICLE);

            returnMessage = MessageFormat.format(returnMessage, vehicle,
                    finalClientCod, serviceValueDetail.getColumn3Chr(), ref);
            // addRouteDetail(getUserphone(), getMessage().getCell());
            break;

        }
        addRouteDetail(getUserphone(), getMessage().getCell());
        return returnMessage;

    }

    private ServiceValue treatRetiredVehicle() throws InvalidOperationException, GenericFacadeException {
        ServiceValue serviceValue = null;

        serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentFleetServiceValue(
                getService(), getUserphone());
        /*
         * recuperamos el ultimo detalle del servicio actual y usuario
         * telefonico y verificamos el evento del mismo.
         */
        if (serviceValue == null) {
            /*
             * verificamos que el ultimo detalle sea el de vehiculo devuelto,
             * solo asi realizar la operacion de "RETIRO"
             */
            serviceValue = new ServiceValue();
            serviceValue.setService(getService());
            serviceValue.setUserphone(getUserphone());
            serviceValue.setMessage(getMessage());
            serviceValue.setRecorddateDat(validateDate());
            /*
             * recuperamos los datos del mensaje
             */

            /*
             * verificamos que no se reciban mas datos de los esperados
             */
            if (getEntity().getDriverCode() != null
                && getEntity().getVehicleCode() != null) {
                /*
                 * detalles[0] --> COD.CHOFER (HEADER-column1Chr) detalles[1]
                 * --> COD.VEHICULO (HEADER-column2Chr) detalles[2] --> KM.
                 * INICIAL (HEADER-column3Chr)
                 */
                /*
                 * verificamos que el chofer no tenga actualmente un vehiculo en
                 * estado "EN RETIRO"
                 */
                ServiceValue lastServiceValue = getFacadeContainer().getServiceValueAPI().getDriverCurrentlyWithVehicle(
                        getService(), getEntity().getDriverCode(),
                        getUserphone().getClient());
                if (lastServiceValue != null) {

                    String driver = getEntity().getDriverCode();
                    String vehicle = lastServiceValue.getColumn2Chr();

                    driver = getMetaForIntegrationValue(
                            getEntity().getDriverCode(), MetaNames.EMPLOYEE);
                    vehicle = getMetaForIntegrationValue(
                            lastServiceValue.getColumn2Chr(),
                            MetaNames.VECHICLE);

                    throw new InvalidOperationException(MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "fleet.message.DriverCurrentlyVehicle"),
                            driver, vehicle));
                }

                lastServiceValue = getFacadeContainer().getServiceValueAPI().getVehicleCurrentlyRetired(
                        getService(), getEntity().getVehicleCode(),
                        getUserphone().getClient());
                if (lastServiceValue != null) {
                    String vehicle = getEntity().getVehicleCode();
                    vehicle = getMetaForIntegrationValue(
                            getEntity().getVehicleCode(), MetaNames.VECHICLE);

                    throw new InvalidOperationException(MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "fleet.message.VehicleCurrentlyRetired"),
                            vehicle));
                }
                serviceValue.setColumn1Chr(getEntity().getDriverCode());
                serviceValue.setColumn2Chr(getEntity().getVehicleCode());
                serviceValue.setColumn3Chr(getEntity().getInitialKm());
                {
                    try {
                        String datetimePattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                                "format.datetime.input.default");
                        datetimePattern = datetimePattern != null ? datetimePattern : "dd/MM/yyyy HH:mm:ss";
                        SimpleDateFormat sdf = new SimpleDateFormat(datetimePattern);
                        /*
                         * finalmente almacenamos la fecha/hora del mensaje en
                         * la cabecera
                         */
                        serviceValue.setColumn6Chr(sdf.format(getMessage().getDateinDat()));
                    } catch (Exception ex) {
                        Logger.getLogger(FleetServiceAPI.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                }
                serviceValue = getFacadeContainer().getServiceValueAPI().create(
                        serviceValue);
                /*
                 * una vez que se ha creado la "CABECERA" con los datos del
                 * inicio del recorrido, almacenamos los mismos datos para poder
                 * visualizarlos luego con los detalles y poder localizar ese
                 * evento dentro del "MAPA DE LOCALIZACION".
                 */

                /*
                 * Al almacenar en los detalles tenemos dos detalles que
                 * completar el codigo del cliente y la cantidad de kilometro
                 * recorridos durante ese evento, que en este caso por el
                 * primero, el cliente sera report.client.fleet.title.Retire y
                 * el kilometraje 0.
                 */
                ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
                serviceValueDetail.setServiceValue(serviceValue);
                serviceValueDetail.setMessage(getMessage());
                serviceValueDetail.setRecorddateDat(validateDate());
                serviceValueDetail.setColumn1Chr("report.client.fleet.title.Retire");
                serviceValueDetail.setColumn2Chr(serviceValue.getColumn3Chr());
                getFacadeContainer().getServiceValueDetailAPI().create(
                        serviceValueDetail);
            } else {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "fleet.message.CouldNotEditHeader"));
            }
        } else {
            throw new InvalidOperationException(MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "fleet.message.CurrentlyRetired"),
                    serviceValue.getColumn2Chr()));
        }

        return serviceValue;
    }

    private ServiceValue treatReturnedVehicle() throws GenericFacadeException, InvalidOperationException {

        ServiceValue serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentFleetServiceValue(
                getService(), getUserphone());
        if (serviceValue != null) {
            /*
             * recuperamos los datos del mensaje
             */
            if (getEntity().getFinalKm() != null) {
                try {
                    /*
                     * detalles[0] --> KM. Final (HEADER-column4Chr) detalles[1]
                     * --> Observación (HEADER-column5Chr)
                     */
                    serviceValue.setColumn4Chr(getEntity().getFinalKm());
                    /*
                     * verificamos si se cargo alguna observacion para la
                     * devolucion del vehiculo
                     */
                    serviceValue.setColumn5Chr(getEntity().getObservation());

                    /*
                     * Una vez que almacenamos el kilometraje final y la
                     * observacion, almacenamos la fecha/hora de la entrega del
                     * vehiculo para ello obtenemos el formato de entrada de
                     * fecha/hora de los parametros globales
                     */
                    String datetimePattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                            "format.datetime.input.default");
                    datetimePattern = datetimePattern != null ? datetimePattern : "dd/MM/yyyy HH:mm:ss";
                    SimpleDateFormat sdf = new SimpleDateFormat(datetimePattern);
                    /*
                     * finalmente almacenamos la fecha/hora del mensaje en la
                     * cabecera
                     */
                    serviceValue.setColumn7Chr(sdf.format(getMessage().getDateinDat()));

                    if (Integer.parseInt(serviceValue.getColumn4Chr()) < (Integer.parseInt(serviceValue.getColumn3Chr()))) {
                        throw new InvalidOperationException(MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "fleet.message.InvalidOperationTravelSumRestriction"),
                                serviceValue.getColumn3Chr()));
                    }

                    /*
                     * agregamos la duracion del retiro
                     */

                    serviceValue.setColumn8Chr(DateUtil.getPeriod(
                            serviceValue.getMessage().getDateinDat().getTime(),
                            new Date().getTime()));

                    serviceValue = getFacadeContainer().getServiceValueAPI().edit(
                            serviceValue);

                    /*
                     * Una vez almacenados y validadas las restricciones del
                     * servicio almacenamos los detalles de la devolucion del
                     * vehiculo en los detalles, previendo el mismo caso del
                     * retiro. Este proceso lo realizamos para poder localizar
                     * la devolucion
                     */

                    /*
                     * Para los valores del campo cliente y kilometro recorrido,
                     * el primero lo dejamos con el valor
                     * report.client.fleet.title.Return y el segundo dejamos la
                     * diferencia entre el km final y la suma del kilometraje
                     * recorrido y el inicial.
                     */
                    ServiceValueDetail serviceValueDetail = new ServiceValueDetail();
                    serviceValueDetail.setServiceValue(serviceValue);
                    serviceValueDetail.setMessage(getMessage());
                    serviceValueDetail.setRecorddateDat(validateDate());
                    Integer recorridoFinal = Integer.parseInt(serviceValue.getColumn4Chr());
                    serviceValueDetail.setColumn1Chr("report.client.fleet.title.Return");
                    serviceValueDetail.setColumn2Chr(recorridoFinal.toString());

                    /*
                     * agregamos la duracion del ultimo recorrido hasta la
                     * devolucions del vehiculo
                     */
                    ServiceValueDetail lastSvd = getFacadeContainer().getServiceValueDetailAPI().getLastServiceValueDetail(
                            getUserphone(), getService(), "");
                    serviceValueDetail.setColumn3Chr(DateUtil.getPeriod(
                            lastSvd.getMessage().getDateinDat().getTime(),
                            new Date().getTime()));

                    getFacadeContainer().getServiceValueDetailAPI().create(
                            serviceValueDetail);

                } catch (Exception ex) {
                    if (ex instanceof InvalidOperationException) {
                        throw (InvalidOperationException) ex;
                    }
                    throw new GenericFacadeException(this.getClass(), getFacadeContainer().getI18nAPI().iValue(
                            "error.DetailErrorDueInternalError"));
                }

            } else {
                throw new GenericFacadeException(this.getClass(), getFacadeContainer().getI18nAPI().iValue(
                        "fleet.message.CouldNotEditHeader"));
            }
        } else {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "fleet.message.MustRetireBeforeReturn"));
        }
        return serviceValue;

    }

    private ServiceValueDetail treatTravel() throws InvalidOperationException, GenericFacadeException {
        ServiceValue serviceValue = null;
        ServiceValueDetail serviceValueDetail = null;
        serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentFleetServiceValue(
                getService(), getUserphone());
        if (serviceValue != null) {
            /*
             * recuperamos los datos del mensaje
             */
            /*
             * detalles[0] --> Cod.Cliente (DETAIL-column1Chr) detalles[1] -->
             * KM. Recorrido (DETAIL-column2Chr)
             */
            serviceValueDetail = new ServiceValueDetail();
            serviceValueDetail.setMessage(getMessage());
            serviceValueDetail.setRecorddateDat(validateDate());
            serviceValueDetail.setServiceValue(serviceValue);
            serviceValueDetail.setColumn1Chr(getEntity().getClientCode());
            serviceValueDetail.setColumn2Chr(getEntity().getTraveledKm());

            /*
             * validamos que el kilometraje del recorrido anterior sea menor al
             * kilometraje actual.
             */
            ServiceValueDetail lastSvd = getFacadeContainer().getServiceValueDetailAPI().getLastServiceValueDetail(
                    getUserphone(), getService(), "");
            if (Integer.parseInt(lastSvd.getColumn2Chr()) > Integer.parseInt(serviceValueDetail.getColumn2Chr())) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "fleet.message.MustBeGreaterTravelKm"));
            }

            /*
             * agregamos la duracion del recorrido
             */
            serviceValueDetail.setColumn3Chr(DateUtil.getPeriod(
                    lastSvd.getMessage().getDateinDat().getTime(),
                    new Date().getTime()));

            getFacadeContainer().getServiceValueDetailAPI().create(
                    serviceValueDetail);

        } else {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "fleet.message.MustRetireBeforeTravel"));
        }
        return serviceValueDetail;
    }

    @Override
    protected ServiceValue treatHeader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
