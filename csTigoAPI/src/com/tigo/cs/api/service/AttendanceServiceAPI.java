package com.tigo.cs.api.service;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tigo.cs.api.entities.AttendanceService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.view.DataHorary;

public abstract class AttendanceServiceAPI<T extends AttendanceService> extends AbstractServiceAPI<AttendanceService> {

    protected AttendanceEvent tranType;
    protected Map<AttendanceEvent, List<AttendanceEvent>> eventPrecedents;
    ServiceValue serviceValue;
    private boolean createHeader = true;
    private ServiceValueDetail lastEvent;
    private String datePattern;

    public enum AttendanceEvent implements ServiceEvent {

        PRESENCE_INIT(1, new ServiceProps("attendance.name.PresenceInit", "attendance.message.PresenceInit", "")),
        PRESENCE_FINISH(2, new ServiceProps("attendance.name.PresenceFinish", "attendance.message.PresenceFinish", "")),
        BREAK_INIT(3, new ServiceProps("attendance.name.BreakInit", "attendance.message.BreakInit", "")),
        BREAK_FINISH(4, new ServiceProps("attendance.name.BreakFinish", "attendance.message.BreakFinish", "")),
        LUNCH_INIT(5, new ServiceProps("attendance.name.LunchInit", "attendance.message.LunchInit", "")),
        LUNCH_FINISH(6, new ServiceProps("attendance.name.LunchFinish", "attendance.message.LunchFinish", ""));
        protected final int value;
        /**
         * Type of te event, may be 0=Init, 1=Finish
         */
        protected final ServiceProps props;

        private AttendanceEvent(int value, ServiceProps props) {
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

    public AttendanceServiceAPI() {
        AttendanceEvent[] pi = { AttendanceEvent.PRESENCE_FINISH };
        AttendanceEvent[] pf = { AttendanceEvent.PRESENCE_INIT, AttendanceEvent.BREAK_FINISH, AttendanceEvent.LUNCH_FINISH };
        AttendanceEvent[] bi = { AttendanceEvent.PRESENCE_INIT, AttendanceEvent.LUNCH_FINISH };
        AttendanceEvent[] bf = { AttendanceEvent.BREAK_INIT };
        AttendanceEvent[] li = { AttendanceEvent.PRESENCE_INIT, AttendanceEvent.BREAK_FINISH };
        AttendanceEvent[] lf = { AttendanceEvent.LUNCH_INIT };

        eventPrecedents = new HashMap<AttendanceEvent, List<AttendanceEvent>>();
        eventPrecedents.put(AttendanceEvent.PRESENCE_INIT, Arrays.asList(pi));
        eventPrecedents.put(AttendanceEvent.PRESENCE_FINISH, Arrays.asList(pf));
        eventPrecedents.put(AttendanceEvent.BREAK_INIT, Arrays.asList(bi));
        eventPrecedents.put(AttendanceEvent.BREAK_FINISH, Arrays.asList(bf));
        eventPrecedents.put(AttendanceEvent.LUNCH_INIT, Arrays.asList(li));
        eventPrecedents.put(AttendanceEvent.LUNCH_FINISH, Arrays.asList(lf));

    }

    @Override
    public AttendanceService getEntity() {
        createHeader = true;
        if (super.getEntity() == null) {
            setEntity(new AttendanceService());
        }
        return super.getEntity();
    }

    @Override
    public AttendanceService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new AttendanceService());
        }
        return super.getReturnEntity();
    }

    public String getDatePattern() {

        if (datePattern == null) {
            try {
                datePattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "format.datetime.output.default");
            } catch (Exception e) {
                datePattern = "dd/MM/yyyy HH:mm";
            }
        }
        return datePattern;

    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        lastEvent = null;

        Long ref = null;
        ServiceValue sv = treatHeader();
        List<ServiceValueDetail> det = treatDetails(sv);
        String empleado = getMetaForIntegrationValue(
                getEntity().getEmployeeCode(), MetaNames.EMPLOYEE);

        ref = det != null && det.size() > 0 ? det.get(0).getServicevaluedetailCod() : null;
        returnMessage = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());

        try {
            if (lastEvent != null) {
                returnMessage = MessageFormat.format(returnMessage, empleado,
                        lastEvent.getColumn4Chr(), ref);
            } else {
                returnMessage = MessageFormat.format(returnMessage, empleado,
                        ref);
            }
        } catch (Exception e) {
            returnMessage = MessageFormat.format(returnMessage, empleado, "",
                    ref);
        }

        addRouteDetail(getUserphone(), getMessage().getCell());

        return returnMessage;
    }

    /**
     * En este m�todo se validan las marcaciones de los lectores biometricos
     * 
     * la validacion principal es que luego de un evento de presencia solo
     * pueden haber un solo inicio de almuerzo o break
     */
    private void ignoreRestriction() {

        serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentAttendanceServiceValue(
                getEntity().getEmployeeCode(), getService(),
                getUserphone().getClient());

        if (tranType == AttendanceEvent.PRESENCE_INIT) {
            if (serviceValue != null) {
                createHeader = false;
            }
        }

    }

    @Override
    protected void validate() throws InvalidOperationException {
        /*
         * para validacion de marcacion de lectores biometricos
         */

        String attendanceLogic = getFacadeContainer().getClientParameterValueAPI().findByCode(
                getClient().getClientCod(), "service.attendance.logic");
        if (attendanceLogic != null
            && attendanceLogic.compareToIgnoreCase("1") == 0) {
            ignoreRestriction();
            return;
        }

        super.validate();
        AttendanceEvent currentOperation = this.tranType;

        serviceValue = getFacadeContainer().getServiceValueAPI().getCurrentAttendanceServiceValue(
                getEntity().getEmployeeCode(), getService(),
                getUserphone().getClient());

        ServiceValueDetail svd = getFacadeContainer().getServiceValueDetailAPI().getLastAttendanceServiceValueDetail(
                getEntity().getEmployeeCode(), getService(),
                getUserphone().getClient());

        AttendanceEvent lastOperation = null;

        GregorianCalendar cabDate = new GregorianCalendar();
        GregorianCalendar detDate = new GregorianCalendar();
        if (serviceValue != null) {
            cabDate.setTime(serviceValue.getRecorddateDat());
        }
        if (svd != null) {
            detDate.setTime(svd.getRecorddateDat());
            lastOperation = getLastEvent(svd.getColumn1Chr()
                + svd.getColumn2Chr());
        }

        if (lastOperation == null) {

            // para la primera transacción procesada para el usuario.
            lastOperation = AttendanceEvent.PRESENCE_FINISH;
        } else if (lastOperation != AttendanceEvent.PRESENCE_FINISH) {
            // Se debe validar puesto que es posible enviar eventos para
            // cerrar los pendientes.
            try {
                // Estas operaciones no puede realizarse un día anterior a
                // la del presente día
                // switch (currentOperation) {
                // // case BREAK_INIT:
                // // case LUNCH_INIT:
                // // throw new
                // //
                // InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                // //
                // "attendance.message.CannotPerformInitEventsForPreviusDay"));
                // default:
                // // continue validation
                // }
                validatePrecedents(currentOperation, lastOperation, svd);
                createHeader = false;
            } catch (Exception e) {
                String lastOperationDate = new SimpleDateFormat(getDatePattern()).format(svd.getRecorddateDat());
                throw new InvalidOperationException(MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "attendance.message.PendingEvents"),
                        getFacadeContainer().getI18nAPI().iValue(
                                lastOperation.getDescription()),
                        lastOperationDate));
            }

            return;
        }
        validatePrecedents(currentOperation, lastOperation, svd);
    }

    protected void validatePrecedents(AttendanceEvent currentOperation, AttendanceEvent lastOperation, ServiceValueDetail svd) throws InvalidOperationException {

        getFacadeContainer().getNotifier().debug(AttendanceServiceAPI.class,
                getCellphoneNumber().toString(),
                baseLogMessage.concat("Validando marcaciones precedentes."));

        if (svd != null) {
            if (!eventPrecedents.get(currentOperation).contains(lastOperation)) {

                getFacadeContainer().getNotifier().debug(
                        AttendanceServiceAPI.class,
                        getCellphoneNumber().toString(),
                        baseLogMessage.concat("El evento no es valido. Se creara el mensaje de respuesta"));

                getFacadeContainer().getNotifier().debug(
                        AttendanceServiceAPI.class,
                        getCellphoneNumber().toString(),
                        baseLogMessage.concat("Se recuperara formato de fecha de parametros globals para la creacion del mensaje de respuesta."));

                String lastOperationDate = new SimpleDateFormat(getDatePattern()).format(svd.getRecorddateDat());

                String msj = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "attendance.message.PrecedentValidationError"),
                        getFacadeContainer().getI18nAPI().iValue(
                                lastOperation.getDescription()),
                        lastOperationDate);

                getFacadeContainer().getNotifier().debug(
                        AttendanceServiceAPI.class,
                        getCellphoneNumber().toString(),
                        baseLogMessage.concat("Mensaje de respuesta.".concat(msj)));

                getFacadeContainer().getNotifier().debug(
                        AttendanceServiceAPI.class,
                        getCellphoneNumber().toString(),
                        baseLogMessage.concat("El evento no es valido:").concat(
                                msj));

                throw new InvalidOperationException(msj);
            }
        } else if (currentOperation != AttendanceEvent.PRESENCE_INIT) {

            getFacadeContainer().getNotifier().debug(
                    AttendanceServiceAPI.class,
                    getCellphoneNumber().toString(),
                    baseLogMessage.concat("Mensaje de respuesta en el caso de detalle nulo e intento de inicio de asistencia."));

            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "attendance.message.MustInitDayAttendance"));
        }

        getFacadeContainer().getNotifier().debug(AttendanceServiceAPI.class,
                getCellphoneNumber().toString(),
                baseLogMessage.concat("Se validaron los precedentes."));
        validateHorary(svd);

    }

    @Override
    protected ServiceValue treatHeader() throws InvalidOperationException {
        if (serviceValue == null && createHeader) {
            serviceValue = new ServiceValue();
            serviceValue.setService(getService());
            serviceValue.setUserphone(getUserphone());
            serviceValue.setMessage(getMessage());
            serviceValue.setRecorddateDat(validateDate());
            serviceValue.setColumn1Chr(getEntity().getEmployeeCode());
            serviceValue = getFacadeContainer().getServiceValueAPI().create(
                    serviceValue);

            getFacadeContainer().getNotifier().debug(
                    AttendanceServiceAPI.class,
                    getCellphoneNumber().toString(),
                    baseLogMessage.concat("Se creo una cabecera nueva para la marcacion."));

        } else if (!createHeader) {

            serviceValue = getFacadeContainer().getServiceValueAPI().getLastAttendanceServiceValue(
                    getEntity().getEmployeeCode(), getService(),
                    getUserphone().getClient());

            getFacadeContainer().getNotifier().debug(
                    AttendanceServiceAPI.class,
                    getCellphoneNumber().toString(),
                    baseLogMessage.concat(
                            "No se creo una cabecera nueva para la marcacion. Se utilizara una cabecera existente. Id.").concat(
                            serviceValue.getServicevalueCod().toString()));

        }
        return serviceValue;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) throws InvalidOperationException {
        List<ServiceValueDetail> svdList = new ArrayList<ServiceValueDetail>();
        ServiceValueDetail serviceValueDetail;

        // Unico conjunto de detalles para asistencia.
        serviceValueDetail = new ServiceValueDetail();
        serviceValueDetail.setServiceValue(serviceValue);
        serviceValueDetail.setMessage(getMessage());
        serviceValueDetail.setRecorddateDat(validateDate());
        serviceValueDetail.setColumn1Chr(getEntity().getEvent().substring(0, 1));
        serviceValueDetail.setColumn2Chr(getEntity().getEvent().substring(1));
        serviceValueDetail.setColumn3Chr(getEntity().getObservation());

        /*
         * agregamos la duracion del evento
         */
        if (tranType.equals(AttendanceEvent.PRESENCE_FINISH)) {
            lastEvent = getFacadeContainer().getServiceValueDetailAPI().getPresenceInitAttendanceEventServiceValueDetail(
                    serviceValue.getColumn1Chr(), getService(),
                    getUserphone().getClient());
        } else if (tranType.equals(AttendanceEvent.BREAK_FINISH)
            || tranType.equals(AttendanceEvent.LUNCH_FINISH)) {
            lastEvent = getFacadeContainer().getServiceValueDetailAPI().getLastAttendanceServiceValueDetail(
                    serviceValue.getColumn1Chr(), getService(),
                    getUserphone().getClient());
        }

        getFacadeContainer().getNotifier().debug(
                AttendanceServiceAPI.class,
                getCellphoneNumber().toString(),
                baseLogMessage.concat("Se recupero ultimo evento para calcular la duraci�n."));

        /*
         * agregamos la duracion en el caso que no se sea un evento de inicio de
         * dia
         */
        if (lastEvent != null) {
            // serviceValueDetail.setColumn4Chr(DateUtil.getPeriod(
            // lastEvent.getRecorddateDat().getTime(),
            // new Date().getTime()));

            /*
             * Cambiamos la fecha actual por la fecha en que el mensaje fue
             * originado
             */
            serviceValueDetail.setColumn4Chr(DateUtil.getPeriod(
                    lastEvent.getRecorddateDat().getTime(),
                    validateDate().getTime()));
            /*
             * Guardamos en el column5 la duracion de la asistencia en
             * milisegundos
             */
            serviceValueDetail.setColumn5Chr(String.valueOf(differenceBetweenDates(
                    lastEvent.getRecorddateDat(), validateDate())));

            /*
             * Si el campo hourWorkMilliseconds es diferente a nulo significa
             * que en el metodo verifyHorario se seteo un valor porque el dia de
             * inicio de presencia y de finalizacion de presencia fue diferente
             */
            if (getEntity().getHoursWorkMilliseconds() != null) {
                Long extraMilliseconds = Long.valueOf(serviceValueDetail.getColumn5Chr())
                    - getEntity().getHoursWorkMilliseconds();
                serviceValueDetail.setColumn6Chr(DateUtil.getPeriodFormat(extraMilliseconds));
                serviceValueDetail.setColumn7Chr(extraMilliseconds.toString());

            } else {

                if (getEntity().getExtraMinutes() != null) {
                    /*
                     * Guardamos en el column6 las horas extras en formato
                     * humano
                     */
                    serviceValueDetail.setColumn6Chr(getEntity().getExtraMinutes());
                }

                if (getEntity().getExtraMilliseconds() != null) {
                    /* Guardamos en el column7 las horas extras en milisegundos */
                    serviceValueDetail.setColumn7Chr(getEntity().getExtraMilliseconds().toString());
                }
            }

            lastEvent = serviceValueDetail;
        }

        getFacadeContainer().getNotifier().debug(
                AttendanceServiceAPI.class,
                getCellphoneNumber().toString(),
                baseLogMessage.concat("Se calcul� la duraci�n de la asistencia."));

        getFacadeContainer().getNotifier().debug(
                AttendanceServiceAPI.class,
                getCellphoneNumber().toString(),
                baseLogMessage.concat("obtenemos el ultimo detalle antes de realizar la marcacion actual."));

        ServiceValueDetail svd = getFacadeContainer().getServiceValueDetailAPI().getLastAttendanceServiceValueDetail(
                getEntity().getEmployeeCode(), getService(),
                getUserphone().getClient());

        serviceValueDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                serviceValueDetail);
        svdList.add(serviceValueDetail);

        if (tranType.equals(AttendanceEvent.PRESENCE_INIT)) {

            if (getEntity().getDelayMinutes() != null) {
                /*
                 * Guardamos en el column8 las horas de restraso en formato
                 * humano
                 */
                serviceValueDetail.setColumn8Chr(getEntity().getDelayMinutes());
            }

            if (getEntity().getDelayMilliseconds() != null) {
                /* Guardamos en el column9 las horas de retraso en milisegundos */
                serviceValueDetail.setColumn9Chr(getEntity().getDelayMilliseconds().toString());
            }

            getFacadeContainer().getNotifier().debug(
                    AttendanceServiceAPI.class,
                    getCellphoneNumber().toString(),
                    baseLogMessage.concat("Agregando agrupador de eventos. Marcacion de Inicio de Presencia."));

            serviceValueDetail.setColumn10Chr(serviceValueDetail.getServicevaluedetailCod().toString());
            serviceValueDetail = getFacadeContainer().getServiceValueDetailAPI().edit(
                    serviceValueDetail);

            getFacadeContainer().getNotifier().debug(
                    AttendanceServiceAPI.class,
                    getCellphoneNumber().toString(),
                    baseLogMessage.concat("Agrupador de eventos agregado. Marcacion de Inicio de Presencia."));
        } else {

            getFacadeContainer().getNotifier().debug(
                    AttendanceServiceAPI.class,
                    getCellphoneNumber().toString(),
                    baseLogMessage.concat("Agregando agrupador de eventos."));

            if (svd != null) {
                serviceValueDetail.setColumn10Chr(svd.getColumn10Chr());
            } else {
                serviceValueDetail.setColumn10Chr(serviceValueDetail.getServicevaluedetailCod().toString());
            }
            serviceValueDetail = getFacadeContainer().getServiceValueDetailAPI().edit(
                    serviceValueDetail);

            getFacadeContainer().getNotifier().debug(
                    AttendanceServiceAPI.class,
                    getCellphoneNumber().toString(),
                    baseLogMessage.concat("Agrupador de eventos agregado. Evento de finalizacion."));
        }

        getFacadeContainer().getNotifier().debug(AttendanceServiceAPI.class,
                getCellphoneNumber().toString(),
                baseLogMessage.concat("Se guardo el nuevo detalle."));

        return svdList;
    }

    @Override
    protected void assignServiceEvent() {
        String discriminator = getEntity().getEvent();
        if (discriminator.equals("PI")) {
            tranType = AttendanceEvent.PRESENCE_INIT;
        } else if (discriminator.equals("PF")) {
            tranType = AttendanceEvent.PRESENCE_FINISH;
        } else if (discriminator.equals("BI")) {
            tranType = AttendanceEvent.BREAK_INIT;
        } else if (discriminator.equals("BF")) {
            tranType = AttendanceEvent.BREAK_FINISH;
        } else if (discriminator.equals("LI")) {
            tranType = AttendanceEvent.LUNCH_INIT;
        } else if (discriminator.equals("LF")) {
            tranType = AttendanceEvent.LUNCH_FINISH;
        }
        getReturnEntity().setEvent(getEntity().getEvent());
    }

    protected AttendanceEvent getLastEvent(String discriminator) {

        getFacadeContainer().getNotifier().debug(
                AttendanceServiceAPI.class,
                getCellphoneNumber().toString(),
                baseLogMessage.concat("Se recuperara ultimo evento de la base de datos."));

        AttendanceEvent lastEvent = AttendanceEvent.PRESENCE_INIT;
        if (discriminator.equals("PF")) {
            lastEvent = AttendanceEvent.PRESENCE_FINISH;
        } else if (discriminator.equals("BI")) {
            lastEvent = AttendanceEvent.BREAK_INIT;
        } else if (discriminator.equals("BF")) {
            lastEvent = AttendanceEvent.BREAK_FINISH;
        } else if (discriminator.equals("LI")) {
            lastEvent = AttendanceEvent.LUNCH_INIT;
        } else if (discriminator.equals("LF")) {
            lastEvent = AttendanceEvent.LUNCH_FINISH;
        }

        getFacadeContainer().getNotifier().debug(
                AttendanceServiceAPI.class,
                getCellphoneNumber().toString(),
                baseLogMessage.concat("Se recupero el ultimo evento de la base de datos."));

        return lastEvent;
    }

    private static final DateFormat dfformat = new SimpleDateFormat("HH:mm");
    private static final DateFormat dfformat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public void validateHorary(ServiceValueDetail svd) {
        try {

            MetaDataPK pk = new MetaDataPK();
            // PK del METADATA
            pk.setCodClient(getUserphone().getClient().getClientCod());
            pk.setCodMeta(7L);
            pk.setCodeChr(getEntity().getEmployeeCode());
            pk.setCodMember(1L);

            MetaData employee = getFacadeContainer().getMetaDataAPI().find(pk);

            /* Recuperamos todos los HORARIOS asociados a AL EMPLEADO */
            List<MetaData> horaryList = getFacadeContainer().getMetaDataAPI().findWithMetadatas(
                    pk, 29l);

            String aux = "";
            String where = "    WHERE o.dataPK.codigo in (%s) ";
            /* Significa que el empleado tiene algun horario asignado */
            if (horaryList != null && horaryList.size() > 0) {

                /* Concatenamos los codigos de metadata */
                for (MetaData md : horaryList) {
                    aux += md.getMetaDataPK().getCodeChr() + ",";
                }

                /* Recuperamos la listas de DataHorary del empleado */
                if (aux != null && !aux.isEmpty()) {
                    aux = aux.substring(0, aux.length() - 1);
                    where = String.format(where, aux);
                    List<DataHorary> listDataHorary = getFacadeContainer().getDataHoraryAPI().findRange(
                            null, where);

                    if (listDataHorary != null) {
                        for (DataHorary dh : listDataHorary) {
                            Date actualDate = validateDate();

                            dh.setDateFrom(dfformat2.parse(dh.getVigenciaDesde()));
                            dh.setDateTo(dh.getVigenciaHasta() != null
                                && !dh.getVigenciaHasta().equals("") ? dfformat2.parse(dh.getVigenciaHasta()) : null);

                            dh.setEnabled(dh.getHabilitado().equals("1") ? true : false);
                            dh.setTolerance(Long.valueOf(dh.getToleranciaGeneral()));
                            dh.setSetTolerance(dh.getAjustarTolerancia().equals(
                                    "1") ? true : false);
                            dh.setEveryDay(dh.getTodosLosDias().equals("1") ? true : false);

                            /* Verificamos que el horario este habilitado */

                            if (dh.getEnabled()) {
                                /*
                                 * Verificamos si el horario sigue en vigencia
                                 */
                                if (dh.getDateTo() == null) {

                                    if (actualDate.getTime() >= dh.getDateFrom().getTime()) {
                                        /*
                                         * procesamos el la marcacion con el
                                         * horario
                                         */
                                        verifyHorary(actualDate, dh, employee,
                                                svd);
                                    }
                                } else if (actualDate.getTime() >= dh.getDateFrom().getTime()
                                    && actualDate.getTime() < dh.getDateTo().getTime()) {
                                    /*
                                     * procesamos el la marcacion con el horario
                                     */
                                    verifyHorary(actualDate, dh, employee, svd);
                                }
                            }

                        }
                    }

                }

            }

        } catch (Exception e) {

        }

    }

    private void verifyHorary(Date actualDate, DataHorary dh, MetaData employee, ServiceValueDetail svd) {
        try {
            MetaDataPK pk = new MetaDataPK();
            // PK del METADATA
            pk.setCodClient(getUserphone().getClient().getClientCod());
            pk.setCodMeta(29L);
            pk.setCodeChr(dh.getDataPK().getCodigo());
            pk.setCodMember(1L);

            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(actualDate);
            calendarDate.set(Calendar.SECOND, 0);
            calendarDate.set(Calendar.MILLISECOND, 0);
            int dia = calendarDate.get(Calendar.DAY_OF_WEEK);
            List<String> listDias = Arrays.asList(dh.getDias().split(","));
            List<String> listHoraEntrada = Arrays.asList(dh.getHorasEntrada().split(
                    ","));

            List<String> listHoraTrabajada = Arrays.asList(dh.getHorasTrabajada().split(
                    ","));
            List<String> listTolerancias = Arrays.asList(dh.getTolerancias().split(
                    ","));

            /*
             * Le restamos uno al dia ya que para nostros domingo es 0 y
             * calendar toma domingo como 1
             */
            dia = dia - 1;
            getEntity().setCodMetadataHorary(dh.getDataPK().getCodigo());
            getEntity().setDay(dia);

            Date horaEntrada = dfformat.parse(listHoraEntrada.get(getEntity().getDay()));
            Date horaTrabajada = dfformat.parse(listHoraTrabajada.get(getEntity().getDay()));

            Calendar horaEntradaCalendar = Calendar.getInstance();
            horaEntradaCalendar.setTime(calendarDate.getTime());
            horaEntradaCalendar.set(Calendar.HOUR_OF_DAY,
                    horaEntrada.getHours());
            horaEntradaCalendar.set(Calendar.MINUTE, horaEntrada.getMinutes());

            Calendar horaSalidaCalendar = Calendar.getInstance();
            horaSalidaCalendar.setTime(horaEntradaCalendar.getTime());
            horaSalidaCalendar.add(Calendar.HOUR_OF_DAY,
                    horaTrabajada.getHours());
            horaSalidaCalendar.add(Calendar.MINUTE, horaTrabajada.getMinutes());

            getEntity().setCheckinTime(
                    dfformat.format(horaEntradaCalendar.getTime()));
            getEntity().setCheckoutTime(
                    dfformat.format(horaSalidaCalendar.getTime()));

            /*
             * Verificamos si tiene chequeado la opcion de tolerancia general
             */
            if (dh.getSetTolerance()) {
                /*
                 * Le sumamos a la hora de entrada la tolerancia general
                 * permitida
                 */

                getEntity().setTolerance(dh.getTolerance().intValue());
                // horaEntradaCalendar = addMinutesToDate(horaEntradaCalendar,
                // dh.getTolerance().intValue());
            } else {
                /*
                 * Le sumamos a la horade entrada la tolerancia asociada al dia
                 */
                int tol = Integer.valueOf(listTolerancias.get(getEntity().getDay()));
                getEntity().setTolerance(tol);
                // horaEntradaCalendar = addMinutesToDate(horaEntradaCalendar,
                // tol);
            }

            /*
             * Verificamos que el dia tenga un horario configurado, nosotros
             * manejamos los dias de 0 a 6 empezando de Domingo, por eso
             * restamos menos 1
             */
            if (listDias.get(dia).equals("1")) {

                /* Si se trata del evento de inicio de asistencia */
                if (tranType.equals(AttendanceEvent.PRESENCE_INIT)) {

                    /*
                     * La marcacion de inicio de asistencia fue fuera de hora
                     */

                    if (calendarDate.getTime().getTime() > horaEntradaCalendar.getTime().getTime()
                        && calendarDate.getTime().getTime() <= horaSalidaCalendar.getTime().getTime()) {
                        // String delay = String.valueOf(differenceBetweenDates(
                        // calendarDate.getTime(),
                        // horaEntradaCalendar.getTime()));

                        String delay = DateUtil.getPeriod(
                                horaEntradaCalendar.getTime().getTime(),
                                calendarDate.getTime().getTime());
                        getEntity().setDelayMinutes(delay);
                        getEntity().setDelayMilliseconds(
                                differenceBetweenDates(
                                        horaEntradaCalendar.getTime(),
                                        calendarDate.getTime()));

                        /* Notificar a supervisores */
                        /*
                         * Recuperamos todos los supervisores asociados a al
                         * horario
                         */
                        List<MetaData> associateSupMetadataList = getFacadeContainer().getMetaDataAPI().findWithMetadatas(
                                pk, 30l);

                        if (associateSupMetadataList != null
                            && associateSupMetadataList.size() > 0) {

                            List<String> nrosTelefonicosList = new ArrayList<String>();

                            for (MetaData md : associateSupMetadataList) {
                                MetaData m = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                        md.getMetaDataPK().getCodClient(),
                                        md.getMetaDataPK().getCodMeta(), 1L,
                                        md.getMetaDataPK().getCodeChr());

                                if (m != null) {
                                    nrosTelefonicosList.add(m.getValueChr());
                                }
                            }

                            getFacadeContainer().getSmsQueueAPI().sendToJMS(
                                    nrosTelefonicosList,
                                    getApplication(),
                                    MessageFormat.format(
                                            getFacadeContainer().getI18nAPI().iValue(
                                                    "attendance.message.send.supervisor"),
                                            employee.getValueChr()));
                        }

                    }

                    /* Si se trata del evento de finalizacion de asistencia */
                } else if (tranType.equals(AttendanceEvent.PRESENCE_FINISH)) {
                    /*
                     * La marcacion de finalizacion de asistencia fue despues de
                     * la hora de Salida
                     */
                    // Calendar calendarInicioAsistenciaDate =
                    // Calendar.getInstance();
                    // calendarInicioAsistenciaDate.setTime(svd.getRecorddateDat());
                    /*
                     * Recuperamos el dia en que inicio la asistencia el
                     * empleado
                     */
                    // int diaInicioAsistencia =
                    // calendarInicioAsistenciaDate.get(Calendar.DAY_OF_WEEK);
                    //
                    // diaInicioAsistencia = diaInicioAsistencia - 1;

                    /*
                     * Si el dia en que inicio es diferente al dia en que cerro
                     * restamos la hora de salida del horario - la hora de
                     * entrada para saber las horas que debe trabajar y luego
                     * restamos esto con la duracion total de la asistencia para
                     * saber las horas extras
                     */
                    // if (diaInicioAsistencia != dia) {
                    // Long horasTrabajo = differenceBetweenDates(
                    // horaEntradaCalendar.getTime(),
                    // horaSalidaCalendar.getTime());
                    //
                    // getEntity().setHoursWorkMilliseconds(horasTrabajo);
                    //
                    // } else {

                    if (calendarDate.getTime().getTime() > horaSalidaCalendar.getTime().getTime()) {
                        // String extra =
                        // String.valueOf(differenceBetweenDates(
                        // calendarDate.getTime(),
                        // horaSalidaCalendar.getTime()));
                        String extra = DateUtil.getPeriod(
                                horaSalidaCalendar.getTime().getTime(),
                                calendarDate.getTime().getTime());
                        getEntity().setExtraMinutes(extra);
                        getEntity().setExtraMilliseconds(
                                differenceBetweenDates(
                                        horaSalidaCalendar.getTime(),
                                        calendarDate.getTime()));

                    }
                    // }
                }
            }

        } catch (Exception e) {

        }

    }

    private static Calendar addMinutesToDate(Calendar date, int minutes) {
        date.add(Calendar.MINUTE, minutes);
        return date;
    }

    private static long differenceBetweenDates(Date date1, Date date2) {
        long milis1 = date1.getTime();
        long milis2 = date2.getTime();
        long diff = milis2 - milis1;

        return diff;

    }
}
