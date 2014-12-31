package com.tigo.cs.api.service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tigo.cs.api.entities.ShiftGuardService;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.ShiftConfiguration;

public abstract class ShiftGuardServiceAPI<T extends ShiftGuardService> extends AbstractServiceAPI<ShiftGuardService> {

    private ShiftGuardEvent tranType;
    private String codGuard;
    private String observation;
    private Client client;
    private ShiftConfiguration shiftConfiguration;

    private Date startTime = null;
    private Date endTime = null;

    protected enum ShiftGuardEvent implements ServiceEvent {

        REGISTER(1, new ServiceProps("shiftguard.event.Registration", "shiftguard.message.Registration", ""));
        protected final int value;
        protected final ServiceProps props;

        private ShiftGuardEvent(int value, ServiceProps props) {
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

    public ShiftGuardServiceAPI() {
    }

    @Override
    public ShiftGuardService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new ShiftGuardService());
            codGuard = null;
            observation = null;
            client = null;
            shiftConfiguration = null;
        }
        return super.getEntity();
    }

    @Override
    public ShiftGuardService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new ShiftGuardService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        try {
            client = getUserphone().getClient();
            codGuard = getEntity().getGuardCode();
            observation = getEntity().getObservation();

            Date dateinDat = new Date();

            /*
             * Restriccion: el guardia debe existir como un metadata, ya que se
             * verifica que al menos el meta_member 1 este asignado en la base
             * de datos
             */

            MetaData guardia = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                    client.getClientCod(), 4L, 1L, codGuard);
            if (guardia == null) {
                /*
                 * creamos el mensaje de retorno en el caso que el guardia no
                 * este dado de alta en la plataforma
                 */
                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                        "guard.GuardNotEnabled");
                returnMessage = MessageFormat.format(returnMessage, codGuard);
                return returnMessage;
            }

            /*
             * recuperamos la configuracion actual para el guardia para el
             * cliente al que esta asignado la linea desde la cual se esta
             * realizando la marcacion, y ademas el objeto Date actual
             */
            shiftConfiguration = getFacadeContainer().getShiftConfigurationAPI().getConfiguration(
                    client, codGuard, dateinDat);

            if (shiftConfiguration == null) {
                /*
                 * el guardia no tiene un turno asignado
                 */
                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                        "guard.GuardWithNoShift");

                /*
                 * agregamos los datos del guardia que registra la marcacion
                 */

                returnMessage = MessageFormat.format(
                        returnMessage,
                        guardia != null ? codGuard.concat(", ").concat(
                                guardia.getValueChr()) : codGuard);
                return returnMessage;

            }

            /*
             * verificamos cual es el rango actual del proceso y validamos que
             * se el mensaje actual se encuentre en ese periodo para realizar la
             * maracacion
             */
            startTime = shiftConfiguration.getNextExecutionTime();
            endTime = shiftConfiguration.getNextExecutionToleranceTime();

            /*
             * obtenemos el nombre del guardia y la hora del evento
             */
            String defaultOutputDateTimeFormat = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "format.datetime.output.default");
            SimpleDateFormat sdf = new SimpleDateFormat(defaultOutputDateTimeFormat);
            String dateStr = sdf.format(shiftConfiguration.getNextExecutionTime());

            String guardMessage = guardia != null ? codGuard.concat(", ").concat(
                    guardia.getValueChr()) : codGuard;

            /*
             * una vez que nos aseguramos que el rango es el correcto
             * verificamos si el usuario ya realizo marcaciones para el rango, o
             * si el proceso ya envio un mensaje al usuario
             */

            /*
             * verificamos en este periodo si el guardia ya realizo su
             * marcacion, verificando SUS DETALLES
             */
            if (!getFacadeContainer().getServiceValueDetailAPI().getMarkOnShift(
                    client, codGuard, shiftConfiguration, startTime, endTime)) {
                /*
                 * el guardia no realizo su marcacion, recuperar su cabecera y
                 * agregar el nuevo detalle
                 * 
                 * recuperamos la cabecera del servicio correspondiente a la
                 * configuracion actual y a la fecha del turno actual
                 */
                ServiceValue sv = treatHeader();
                treatDetails(sv);

                /*
                 * una vez creados los mensajes del servicio actualizamos el
                 * campo de metadatos correspondiente al guardia actual, y
                 * asignamos el cellphoneNum si el mensaje se envio desde un
                 * numero diferente
                 */

                MetaData metaGuardCellphoneNum = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                        client.getClientCod(), 4L, 2L, codGuard);
                if (metaGuardCellphoneNum == null) {
                    /*
                     * en el caso que el guardia no tenga asignado un numero
                     * telefonico, se le asigna el actual
                     */
                    MetaDataPK pk = new MetaDataPK();
                    pk.setCodClient(client.getClientCod());
                    pk.setCodMeta(4L);
                    pk.setCodMember(2L);
                    pk.setCodeChr(codGuard);
                    metaGuardCellphoneNum = new MetaData();
                    metaGuardCellphoneNum.setMetaDataPK(pk);
                    metaGuardCellphoneNum.setValueChr(getUserphone().getCellphoneNum().toString());
                    getFacadeContainer().getMetaDataAPI().create(
                            metaGuardCellphoneNum);
                } else {
                    /*
                     * verificamos que el guardia este marcando desde el numero
                     * que tiene configurado en el member 2 de su metadata, si
                     * no es el mismo, se actualiza por el actual, esto para
                     * notificarle en el proceso a este ultimo numero telefonico
                     * en el caso de no realizar la marcacion a hora
                     */

                    if (metaGuardCellphoneNum.getValueChr().compareTo(
                            getUserphone().getCellphoneNum().toString()) != 0) {
                        metaGuardCellphoneNum.setValueChr(getUserphone().getCellphoneNum().toString());
                        getFacadeContainer().getMetaDataAPI().edit(
                                metaGuardCellphoneNum);
                    }
                }

                /*
                 * se ha procesado el mensaje, creamos el mensaje de retorno
                 */
                returnMessage = tranType.getSuccessMessage();
                returnMessage = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(returnMessage),
                        guardMessage, dateStr);

                /*
                 * antes de retornar el mensaje agregamos el detalle de hoja de
                 * ruta
                 */
                addRouteDetail(getUserphone(), getMessage().getCell());

                return returnMessage;

            } else {
                /*
                 * el guardia ya realizo su marcacion del turno actual, o el
                 * proceso ya genero un detalle para el turno actual. Retornamos
                 * un mensaje al userphone que su marcacion no pudo ser
                 * registrada
                 */

                returnMessage = getFacadeContainer().getI18nAPI().iValue(
                        "guard.AlreadyMarkOnShift");
                returnMessage = MessageFormat.format(returnMessage,
                        guardMessage, dateStr);
                return returnMessage;
            }

        } catch (Exception e) {
            return returnMessage;
        }
    }

    @Override
    protected ServiceValue treatHeader() {
        /*
         * recuperamos la cabecera del servicio correspondiente a la
         * configuracion actual y a la fecha del turno actual
         */

        /*
         * calculamos el rango de duracion de la cabecera
         */

        Date startTimeServiceValue = shiftConfiguration.getStartTime();
        Long duration = shiftConfiguration.getDuration() * 60 * 1000;
        Date endTimeServiceValue = new Date(startTimeServiceValue.getTime()
            + duration);

        /*
         * verificamos con el rango total del proceso si es necesario crear la
         * cabecera, verificando LA CABECERA
         */
        ServiceValue sv = getFacadeContainer().getServiceValueAPI().getMarkOnShift(
                client, codGuard, shiftConfiguration, startTimeServiceValue,
                endTimeServiceValue);
        if (sv == null) {
            /*
             * el guardia aun no realizo marcaciones el dia de hoy, se debe
             * crear la cabecera del servicio
             */
            sv = new ServiceValue();
            sv.setService(getService());
            sv.setUserphone(getUserphone());
            sv.setMessage(getMessage());
            sv.setRecorddateDat(validateDate());

            /*
             * datos propios del servicio Verificar el Documento de
             * Especificacion de Servicios para mas informacion
             * 
             * http://10.32.0.77/svn/soluciones-documentation/Especificaciones
             * de Servicios.xlsx
             * 
             * column1Chr : codigo del guardia column2Chr : codigo del
             * shift_period column3Chr : codigo del shift_cconfiguration
             */
            sv.setColumn1Chr(codGuard);
            sv.setColumn2Chr(shiftConfiguration.getShiftConfigurationPK().getCodShiftPeriod().toString());
            sv.setColumn3Chr(shiftConfiguration.getShiftConfigurationPK().getShiftConfigurationCod().toString());
            getFacadeContainer().getServiceValueAPI().create(sv);
        }
        return sv;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {

        List<ServiceValueDetail> list = new ArrayList<ServiceValueDetail>();

        /*
         * creamos el detalle
         */
        ServiceValueDetail svd = new ServiceValueDetail();
        svd.setMessage(getMessage());
        svd.setRecorddateDat(validateDate());
        svd.setServiceValue(serviceValue);

        /*
         * datos propios del servicio Verificar el Documento de Especificacion
         * de Servicios para mas informacion
         * 
         * http://10.32.0.77/svn/soluciones-documentation/Especificaciones de
         * Servicios.xlsx
         * 
         * column1Chr : observacion de la marcacion column2Chr : tipo de
         * marcacion (solo se guarda desde el servicio)
         */
        svd.setColumn1Chr(observation);

        list.add(getFacadeContainer().getServiceValueDetailAPI().create(svd));
        return list;
    }

    @Override
    protected void assignServiceEvent() {
        tranType = ShiftGuardEvent.REGISTER;
        getReturnEntity().setEvent("REGISTER");
    }

}
