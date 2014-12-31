package com.tigo.cs.api.service;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.entities.VisitMedicService;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class VisitMedicServiceAPI<T extends VisitMedicService> extends AbstractServiceAPI<VisitMedicService> {

    protected VisitMedicEvent tranType;
    protected ServiceValue medicSV;
    protected ServiceValue productSV;
    protected ServiceValue currentClinicSV;
    protected List<ServiceValue> clinics;
    protected List<ServiceValue> medics;
    protected List<ServiceValueDetail> clinicDetails;
    protected List<ServiceValueDetail> medicDetails;
    protected List<ServiceValueDetail> ProductDetails;

    public enum VisitMedicEvent implements ServiceEvent {

        CLINIC_START(1, new ServiceProps("visitmedic.name.ClinicStart", "visitmedic.message.ClinicStart", "")),
        CLINIC_END(2, new ServiceProps("visitmedic.name.ClinicEnd", "visitmedic.message.ClinicEnd", "")),
        MEDIC_START(3, new ServiceProps("visitmedic.name.MedicStart", "visitmedic.message.MedicStart", "")),
        MEDIC_END(4, new ServiceProps("visitmedic.name.MedicEnd", "visitmedic.message.MedicEnd", "")),
        PRODUCT_REGISTER(5, new ServiceProps("visitmedic.name.ProductRegister", "visitmedic.message.ProductRegister", "")),
        CLINIC_QUICK(6, new ServiceProps("visitmedic.name.ClinicQuick", "visitmedic.message.ClinicQuick", ""));
        protected final int value;
        protected final ServiceProps props;

        private VisitMedicEvent(int value, ServiceProps props) {
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

    public VisitMedicServiceAPI() {
    }

    @Override
    public VisitMedicService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new VisitMedicService());
            tranType = null;
            medicSV = null;
            productSV = null;
            currentClinicSV = null;
            clinics = null;
            medics = null;
            clinicDetails = null;
            medicDetails = null;
            ProductDetails = null;
        }
        return super.getEntity();
    }

    @Override
    public VisitMedicService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new VisitMedicService());
        }
        return super.getReturnEntity();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {

        ServiceValue current = null;
        ServiceValue currentServiceValue = null;
        ServiceValue currentClinic = null;
        ServiceValue currentMedic = null;

        String clinicCode = null;
        String markKm = null;
        String medicCode = null;
        String motiveCode = null;
        String nextVisit = null;
        String notificate = null;
        String observation = null;
        String notificationDesc = null;
        String notificationMessage = "";
        String dateHourPattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                "services.format.datetime");

        String messagePattern = getFacadeContainer().getI18nAPI().iValue(
                tranType.getSuccessMessage());

        Long ref = null;
        String clinic = "";
        String medic = "";

        switch (tranType) {
        case CLINIC_START:

            /*
             * obtenemos los valores a registrar
             */
            clinicCode = getEntity().getPlaceCode();
            markKm = getEntity().getInitialKm();
            observation = getEntity().getObservation();

            currentServiceValue = getFacadeContainer().getServiceValueAPI().findCurrentMedicalVisitorServiceValue(
                    getUserphone(), getService());

            if (currentServiceValue == null) {
                /*
                 * para el caso de ser la primera marcacion del dia, se crea la
                 * cabecera del servicio
                 */
                currentServiceValue = new ServiceValue();
                currentServiceValue.setService(getService());
                currentServiceValue.setMessage(getMessage());
                currentServiceValue.setRecorddateDat(validateDate());
                currentServiceValue.setUserphone(getUserphone());
                /*
                 * agregamos el identificador a la cabecera del servicio
                 */
                currentServiceValue.setColumn1Chr("MV");
                currentServiceValue = getFacadeContainer().getServiceValueAPI().create(
                        currentServiceValue);
            }

            /*
             * creamos la cabecera de la visita al sanatorio
             */
            current = new ServiceValue();
            current.setService(getService());
            current.setMessage(getMessage());
            current.setUserphone(getUserphone());
            current.setRecorddateDat(validateDate());
            /*
             * agregamos la referencia a la cabecera diaria del servicio
             */
            current.setColumn1Chr(currentServiceValue.getServicevalueCod().toString());
            current.setColumn2Chr(clinicCode);
            current.setColumn3Chr("CL");
            current = getFacadeContainer().getServiceValueAPI().create(current);

            /*
             * unca vez creada la cabecera, creamos el registro de entrada como
             * un detalle de la cabecera de la visita al sanatorio
             */

            ServiceValueDetail clinicVisitStartDetail = new ServiceValueDetail();
            clinicVisitStartDetail.setServiceValue(current);
            clinicVisitStartDetail.setMessage(getMessage());
            clinicVisitStartDetail.setRecorddateDat(validateDate());
            clinicVisitStartDetail.setColumn1Chr("ENT");
            /*
             * obtenemos el kilometraje que se ha introducido para la marcacion
             */

            clinicVisitStartDetail.setColumn2Chr(markKm);
            /*
             * agregamos la observavion de la marcacion
             */
            clinicVisitStartDetail.setColumn4Chr(observation);
            clinicVisitStartDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                    clinicVisitStartDetail);

            /*
             * una vez creados los registros de la entrada del servicio creamos
             * el mensaje de respuesta
             */

            clinic = getMetaForIntegrationValue(clinicCode, MetaNames.CLINIC);

            ref = current.getServicevalueCod();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());

            returnMessage = MessageFormat.format(returnMessage, clinic, ref);
            break;
        case CLINIC_END:

            /*
             * obtenemos los datos que el usuario telefonico ha introducido para
             * realizar el registro del evento
             */

            observation = getEntity().getObservation();

            /*
             * obtenemos el registro actual de la clinica, que es el ultimo
             * resgistro de entrada
             */

            current = getFacadeContainer().getServiceValueAPI().findCurrentClinic(
                    getUserphone(), getService());

            /*
             * agragamos el detalle del registro de la marcacion de la salida
             * del sanatorio
             */

            ServiceValueDetail clinicVisitEndDetail = new ServiceValueDetail();
            clinicVisitEndDetail.setServiceValue(current);
            clinicVisitEndDetail.setMessage(getMessage());
            clinicVisitEndDetail.setRecorddateDat(validateDate());
            clinicVisitEndDetail.setColumn1Chr("SAL");
            /*
             * agregamos la observavion de la marcacion
             */
            clinicVisitEndDetail.setColumn4Chr(observation);
            /*
             * agregamos la duracion de la visita. obtenemos el resgisitro de
             * entrada a la clinica
             */
            List<ServiceValueDetail> svdList = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                    current);
            Long initTime = svdList.get(svdList.size() - 1).getMessage().getDateinDat().getTime();
            Long finishTime = new Date().getTime();

            clinicVisitEndDetail.setColumn3Chr(new Long((finishTime - initTime)).toString());

            clinicVisitStartDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                    clinicVisitEndDetail);

            /*
             * recuperamos el metadata del sanatorio donde se realizo la
             * marcacion
             */

            clinic = getMetaForIntegrationValue(current.getColumn2Chr(),
                    MetaNames.CLINIC);

            ref = current.getServicevalueCod();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());

            returnMessage = MessageFormat.format(returnMessage, clinic,
                    DateUtil.getPeriod(0L, finishTime - initTime), ref);
            break;
        case MEDIC_START:

            /*
             * obtenemos los valores a registrar
             */
            medicCode = getEntity().getMedicCode();
            observation = getEntity().getObservation();

            ServiceValue currentClinicValue = getFacadeContainer().getServiceValueAPI().findCurrentClinic(
                    getUserphone(), getService());

            /*
             * creamos la cabecera de la visita al sanatorio
             */
            current = new ServiceValue();
            current.setService(getService());
            current.setMessage(getMessage());
            current.setRecorddateDat(validateDate());
            current.setUserphone(getUserphone());

            /*
             * agregamos la referencia a la cabecera al sanatorio
             */
            current.setColumn1Chr(currentClinicValue.getServicevalueCod().toString());
            current.setColumn2Chr(medicCode);
            current.setColumn3Chr("ME");
            current = getFacadeContainer().getServiceValueAPI().create(current);

            /*
             * unca vez creada la cabecera, creamos el registro de entrada como
             * un detalle de la cabecera de la visita al sanatorio
             */

            ServiceValueDetail medicVisitStartDetail = new ServiceValueDetail();
            medicVisitStartDetail.setServiceValue(current);
            medicVisitStartDetail.setMessage(getMessage());
            medicVisitStartDetail.setRecorddateDat(validateDate());
            medicVisitStartDetail.setColumn1Chr("ENT");

            /*
             * agregamos la observavion de la marcacion
             */
            medicVisitStartDetail.setColumn5Chr(observation);
            clinicVisitStartDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                    medicVisitStartDetail);

            /*
             * una vez creados los registros de la entrada del servicio creamos
             * el mensaje de respuesta
             */

            /*
             * recuperamos el metadata del sanatorio donde se realizo la
             * marcacion
             */

            clinic = getMetaForIntegrationValue(
                    currentClinicValue.getColumn2Chr(), MetaNames.CLINIC);

            /*
             * recuperamos el metadata del medico donde se realizo la marcacion
             */

            medic = getMetaForIntegrationValue(current.getColumn2Chr(),
                    MetaNames.MEDIC);

            ref = current.getServicevalueCod();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());

            // if (hasMetaIntegrationEnabled(getClient().getClientCod(),
            // MetaNames.MEDIC)) {
            //
            // medicDesc = getMetaForIntegrationValue(
            // currentClinicValue.getColumn2Chr(), MetaNames.MEDIC);
            // }

            returnMessage = MessageFormat.format(messagePattern, clinic, medic,
                    ref);
            break;

        case MEDIC_END:

            /*
             * obtenemos los datos que el usuario telefonico ha introducido para
             * realizar el registro del evento
             */

            motiveCode = getEntity().getMotiveCode();

            notificate = getEntity().getNotificate();
            if (notificate != null && notificate.compareTo("0") != 0) {
                nextVisit = getEntity().getNextVisit();
                notificationDesc = getEntity().getNotificationDesc();
            } else {
                nextVisit = null;
                notificate = null;
            }

            notificationDesc = getEntity().getNotificationDesc();
            observation = getEntity().getObservation();

            /*
             * obtenemos el registro actual de la clinica, que es el ultimo
             * resgistro de entrada
             */

            current = getFacadeContainer().getServiceValueAPI().findCurrentMedic(
                    getUserphone(), getService());
            currentClinic = getFacadeContainer().getServiceValueAPI().findCurrentClinic(
                    getUserphone(), getService());

            /*
             * agragamos el detalle del registro de la marcacion de la salida
             * del sanatorio
             */

            ServiceValueDetail medicVisitEndDetail = new ServiceValueDetail();
            medicVisitEndDetail.setServiceValue(current);
            medicVisitEndDetail.setMessage(getMessage());
            medicVisitEndDetail.setRecorddateDat(validateDate());
            medicVisitEndDetail.setColumn1Chr("SAL");

            /*
             * agregamos el motivo de la visita
             */
            medicVisitEndDetail.setColumn2Chr(motiveCode);

            /*
             * agregamos la fecha de la proxima visita
             */
            medicVisitEndDetail.setColumn3Chr(nextVisit);

            /*
             * agregamos el tipo de notificacion para el servicio
             */

            medicVisitEndDetail.setColumn4Chr(notificate);

            /*
             * mensaje de notificacion
             */
            medicVisitEndDetail.setColumn5Chr(notificationDesc);

            /*
             * agregamos la observavion de la marcacion
             */
            medicVisitEndDetail.setColumn6Chr(observation);

            /*
             * agregamos la duracion de la visita. obtenemos el resgisitro de
             * entrada a la clinica
             */
            svdList = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                    current);
            initTime = svdList.get(svdList.size() - 1).getMessage().getDateinDat().getTime();
            finishTime = new Date().getTime();

            medicVisitEndDetail.setColumn7Chr(new Long((finishTime - initTime)).toString());

            clinicVisitStartDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                    medicVisitEndDetail);

            /*
             * agregamos el registro del servicio de agendamiento en el caso que
             * ser haya realizado
             */

            if (nextVisit != null && notificate != null) {

                Calendar calendar = Calendar.getInstance();

                Date fechaEvento = new Date(Long.parseLong(nextVisit));

                DateFormat df = new SimpleDateFormat(dateHourPattern);
                String fechaHoraEvento = df.format(fechaEvento);
                calendar.setTime(fechaEvento);

                /*
                 * un dia antes
                 */
                if (notificate.equals("1")) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                }/*
                  * una hora antes
                  */
                else if (notificate.equals("2")) {
                    calendar.add(Calendar.HOUR_OF_DAY, -1);
                }/*
                  * tres horas antes
                  */
                else if (notificate.equals("3")) {
                    calendar.add(Calendar.HOUR_OF_DAY, -3);
                }

                String fechaNotificacion = df.format(calendar.getTime());

                ServiceValue serviceValue = new ServiceValue();
                serviceValue.setService(getFacadeContainer().getServiceAPI().find(
                        14L));
                serviceValue.setUserphone(getUserphone());
                serviceValue.setMessage(getMessage());
                serviceValue.setRecorddateDat(validateDate());
                serviceValue.setColumn1Chr(fechaHoraEvento);
                serviceValue.setColumn2Chr(fechaNotificacion);
                serviceValue.setColumn3Chr(notificationDesc);
                getFacadeContainer().getServiceValueAPI().create(serviceValue);

                String notificationPattern = getFacadeContainer().getI18nAPI().iValue(
                        "visitmedic.message.Notificate");

                notificationMessage = MessageFormat.format(notificationPattern,
                        notificationDesc, fechaNotificacion);

            }

            /*
             * una vez creados los registros de la entrada del servicio creamos
             * el mensaje de respuesta
             */

            /*
             * recuperamos el metadata del sanatorio donde se realizo la
             * marcacion
             */

            clinic = getMetaForIntegrationValue(currentClinic.getColumn2Chr(),
                    MetaNames.CLINIC);

            // if (hasMetaIntegrationEnabled(getClient().getClientCod(),
            // MetaNames.CLINIC)) {
            //
            // clinicDesc = getMetaForIntegrationValue(
            // currentClinic.getColumn2Chr(), MetaNames.CLINIC);
            // }

            /*
             * recuperamos el metadata del medico donde se realizo la marcacion
             */

            medic = getMetaForIntegrationValue(current.getColumn2Chr(),
                    MetaNames.MEDIC);

            // if (hasMetaIntegrationEnabled(getClient().getClientCod(),
            // MetaNames.MEDIC)) {
            //
            // medicDesc = getMetaForIntegrationValue(
            // current.getColumn2Chr(), MetaNames.MEDIC);
            // }

            ref = current.getServicevalueCod();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());

            returnMessage = MessageFormat.format(messagePattern, clinic, medic,
                    DateUtil.getPeriod(0L, finishTime - initTime),
                    notificationMessage, ref);

            break;
        case CLINIC_QUICK:

            /*
             * marcacion de una visita rapida al medico. este evento implica
             * crear la cabecera del sanatorio y la cabecera del medico
             */

            /*
             * obtenemos los valores a registrar
             */
            clinicCode = getEntity().getPlaceCode();
            medicCode = getEntity().getMedicCode();
            markKm = getEntity().getInitialKm();
            motiveCode = getEntity().getMotiveCode();
            notificate = getEntity().getNotificate();
            if (notificate != null && notificate.compareTo("0") != 0) {
                nextVisit = getEntity().getNextVisit();
                notificate = getEntity().getNotificationDesc();
            } else {
                nextVisit = null;
                notificate = null;
            }

            observation = getEntity().getObservation();

            currentServiceValue = getFacadeContainer().getServiceValueAPI().findCurrentMedicalVisitorServiceValue(
                    getUserphone(), getService());

            if (currentServiceValue == null) {
                /*
                 * para el caso de ser la primera marcacion del dia, se crea la
                 * cabecera del servicio
                 */
                currentServiceValue = new ServiceValue();
                currentServiceValue.setService(getService());
                currentServiceValue.setMessage(getMessage());
                currentServiceValue.setRecorddateDat(validateDate());
                currentServiceValue.setUserphone(getUserphone());
                /*
                 * agregamos el identificador a la cabecera del servicio
                 */
                currentServiceValue.setColumn1Chr("MV");
                currentServiceValue = getFacadeContainer().getServiceValueAPI().create(
                        currentServiceValue);
            }

            /*
             * creamos la cabecera de la visita al sanatorio
             */
            currentClinic = new ServiceValue();
            currentClinic.setService(getService());
            currentClinic.setMessage(getMessage());
            currentClinic.setRecorddateDat(validateDate());
            currentClinic.setUserphone(getUserphone());

            /*
             * agregamos la referencia a la cabecera diaria del servicio
             */
            currentClinic.setColumn1Chr(currentServiceValue.getServicevalueCod().toString());
            currentClinic.setColumn2Chr(clinicCode);
            currentClinic.setColumn3Chr("CL");
            currentClinic = getFacadeContainer().getServiceValueAPI().create(
                    currentClinic);

            /*
             * unca vez creada la cabecera, creamos el registro de entrada como
             * un detalle de la cabecera de la visita al sanatorio
             */

            ServiceValueDetail clinicVisitQuickDetail = new ServiceValueDetail();
            clinicVisitQuickDetail.setServiceValue(currentClinic);
            clinicVisitQuickDetail.setMessage(getMessage());
            clinicVisitQuickDetail.setRecorddateDat(validateDate());
            clinicVisitQuickDetail.setColumn1Chr("ENTSAL");
            /*
             * obtenemos el kilometraje que se ha introducido para la marcacion
             */

            clinicVisitQuickDetail.setColumn2Chr(markKm);
            /*
             * agregamos la observavion de la marcacion
             */
            clinicVisitQuickDetail.setColumn4Chr(observation);
            clinicVisitQuickDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                    clinicVisitQuickDetail);

            /*
             * creamos la cabecera de la visita al sanatorio
             */
            currentMedic = new ServiceValue();
            currentMedic.setService(getService());
            currentMedic.setMessage(getMessage());
            currentMedic.setRecorddateDat(validateDate());
            currentMedic.setUserphone(getUserphone());

            /*
             * agregamos la referencia a la cabecera al sanatorio
             */
            currentMedic.setColumn1Chr(currentClinic.getServicevalueCod().toString());
            currentMedic.setColumn2Chr(medicCode);
            currentMedic.setColumn3Chr("ME");
            currentMedic = getFacadeContainer().getServiceValueAPI().create(
                    currentMedic);

            /*
             * unca vez creada la cabecera, creamos el registro de entrada como
             * un detalle de la cabecera de la visita al sanatorio
             */

            ServiceValueDetail medicVisitQuickDetail = new ServiceValueDetail();
            medicVisitQuickDetail.setServiceValue(currentMedic);
            medicVisitQuickDetail.setMessage(getMessage());
            medicVisitQuickDetail.setRecorddateDat(validateDate());
            medicVisitQuickDetail.setColumn1Chr("ENTSAL");

            /*
             * agregamos el motivo de la visita
             */
            medicVisitQuickDetail.setColumn2Chr(motiveCode);

            /*
             * agregamos la fecha de la proxima visita
             */
            medicVisitQuickDetail.setColumn3Chr(nextVisit);

            /*
             * agregamos el tipo de notificacion para el servicio
             */

            medicVisitQuickDetail.setColumn4Chr(notificate);

            /*
             * agregamos la observavion de la marcacion
             */
            medicVisitQuickDetail.setColumn5Chr(observation);
            medicVisitQuickDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                    medicVisitQuickDetail);

            /*
             * registramos los productos del registro del evento
             */

            if (getEntity().getDetail() != null) {
                /*
                 * separamos los detalles que recibimos en el mensaje
                 */

                ServiceValue currentProduct = getFacadeContainer().getServiceValueAPI().findCurrentProduct(
                        getUserphone(), getService());

                /*
                 * creamos la cabecera de registro de productos
                 */
                if (currentProduct == null) {
                    currentProduct = new ServiceValue();
                    currentProduct.setMessage(getMessage());
                    currentProduct.setRecorddateDat(validateDate());
                    currentProduct.setService(getService());
                    currentProduct.setUserphone(getUserphone());
                    currentProduct.setColumn1Chr(currentMedic.getServicevalueCod().toString());
                    currentProduct.setColumn3Chr("PR");
                    currentProduct = getFacadeContainer().getServiceValueAPI().create(
                            currentProduct);
                }

                for (OrderDetailService detail : getEntity().getDetail()) {
                    /*
                     * recuperamos cada item y separamos cada uno para registrar
                     * el producto en un registro de detalle
                     */

                    ServiceValueDetail productDetail = new ServiceValueDetail();
                    productDetail.setServiceValue(currentProduct);
                    productDetail.setMessage(getMessage());
                    productDetail.setRecorddateDat(validateDate());

                    String productCode = detail.getProductCode();
                    String quantity = detail.getQuantity();

                    productDetail.setColumn1Chr(productCode);
                    productDetail.setColumn2Chr(quantity);
                    productDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                            productDetail);

                }
            }

            if (nextVisit != null && notificate != null) {
                /*
                 * creamos el registro del servicio de agendamiento de eventos
                 */
                Calendar calendar = Calendar.getInstance();
                Date fechaEvento = new Date(Long.parseLong(nextVisit));

                DateFormat df = new SimpleDateFormat(dateHourPattern);
                String fechaHoraEvento = df.format(fechaEvento);
                calendar.setTime(fechaEvento);

                /*
                 * un dia antes
                 */
                if (notificate.equals("1")) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                }/*
                  * una hora antes
                  */
                else if (notificate.equals("2")) {
                    calendar.add(Calendar.HOUR_OF_DAY, -1);
                }/*
                  * tres horas antes
                  */
                else if (notificate.equals("3")) {
                    calendar.add(Calendar.HOUR_OF_DAY, -3);
                }

                String fechaNotificacion = df.format(calendar.getTime());

                ServiceValue serviceValue = new ServiceValue();
                serviceValue.setService(getFacadeContainer().getServiceAPI().find(
                        14L));
                serviceValue.setUserphone(getUserphone());
                serviceValue.setMessage(getMessage());
                serviceValue.setRecorddateDat(validateDate());
                serviceValue.setColumn1Chr(fechaHoraEvento);
                serviceValue.setColumn2Chr(fechaNotificacion);
                serviceValue.setColumn3Chr(notificationDesc);
                getFacadeContainer().getServiceValueAPI().create(serviceValue);

                String notificationPattern = getFacadeContainer().getI18nAPI().iValue(
                        "visitmedic.message.Notificate");

                notificationMessage = MessageFormat.format(notificationPattern,
                        notificationDesc, fechaNotificacion);
            }

            /*
             * una vez creados los registros de la entrada del servicio creamos
             * el mensaje de respuesta
             */

            /*
             * recuperamos el metadata del sanatorio donde se realizo la
             * marcacion
             */
            clinic = getMetaForIntegrationValue(currentClinic.getColumn2Chr(),
                    MetaNames.CLINIC);

            // if (hasMetaIntegrationEnabled(getClient().getClientCod(),
            // MetaNames.CLINIC)) {
            //
            // clinicDesc = getMetaForIntegrationValue(
            // currentClinic.getColumn2Chr(), MetaNames.CLINIC);
            // }

            /*
             * recuperamos el metadata del medico donde se realizo la marcacion
             */
            medic = getMetaForIntegrationValue(currentMedic.getColumn2Chr(),
                    MetaNames.MEDIC);

            // if (hasMetaIntegrationEnabled(getClient().getClientCod(),
            // MetaNames.MEDIC)) {
            //
            // medicDesc = getMetaForIntegrationValue(
            // currentMedic.getColumn2Chr(), MetaNames.MEDIC);
            // }

            ref = currentClinic.getServicevalueCod();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());

            returnMessage = MessageFormat.format(messagePattern, clinic, medic,
                    notificationMessage, ref);

            break;
        case PRODUCT_REGISTER:

            currentClinic = getFacadeContainer().getServiceValueAPI().findCurrentClinic(
                    getUserphone(), getService());
            currentMedic = getFacadeContainer().getServiceValueAPI().findCurrentMedic(
                    getUserphone(), getService());

            current = getFacadeContainer().getServiceValueAPI().findCurrentProduct(
                    getUserphone(), getService());

            /*
             * creamos la cabecera de registro de productos
             */
            if (current == null) {
                current = new ServiceValue();
                current.setMessage(getMessage());
                current.setRecorddateDat(validateDate());
                current.setService(getService());
                current.setUserphone(getUserphone());
                current.setColumn1Chr(currentMedic.getServicevalueCod().toString());
                current.setColumn3Chr("PR");
                current = getFacadeContainer().getServiceValueAPI().create(
                        current);
            }

            for (OrderDetailService detail : getEntity().getDetail()) {
                /*
                 * recuperamos cada item y separamos cada uno para registrar el
                 * producto en un registro de detalle
                 */

                ServiceValueDetail productDetail = new ServiceValueDetail();
                productDetail.setServiceValue(current);
                productDetail.setMessage(getMessage());
                productDetail.setRecorddateDat(validateDate());
                String productCode = detail.getProductCode();
                String quantity = detail.getQuantity();

                productDetail.setColumn1Chr(productCode);
                productDetail.setColumn2Chr(quantity);
                productDetail = getFacadeContainer().getServiceValueDetailAPI().create(
                        productDetail);

            }

            /*
             * recuperamos el metadata del sanatorio donde se realizo la
             * marcacion
             */

            clinic = getMetaForIntegrationValue(currentClinic.getColumn2Chr(),
                    MetaNames.CLINIC);

            // if (hasMetaIntegrationEnabled(getClient().getClientCod(),
            // MetaNames.CLINIC)) {
            //
            // clinicDesc = getMetaForIntegrationValue(
            // currentClinic.getColumn2Chr(), MetaNames.CLINIC);
            // }

            /*
             * recuperamos el metadata del medico donde se realizo la marcacion
             */

            medic = getMetaForIntegrationValue(currentMedic.getColumn2Chr(),
                    MetaNames.MEDIC);

            // if (hasMetaIntegrationEnabled(getClient().getClientCod(),
            // MetaNames.MEDIC)) {
            //
            // medicDesc = getMetaForIntegrationValue(
            // currentMedic.getColumn2Chr(), MetaNames.MEDIC);
            // }

            ref = current.getServicevalueCod();

            returnMessage = getFacadeContainer().getI18nAPI().iValue(
                    tranType.getSuccessMessage());

            returnMessage = MessageFormat.format(messagePattern, clinic, medic,
                    ref);
            break;
        }

        return returnMessage;
    }

    /**
     * Verificacion de posibilidad de registro de eventos del servicio
     * 
     * @return
     * @throws InvalidOperationException
     */

    @Override
    protected void validate() throws InvalidOperationException {
        super.validate();

        ServiceValue current = null;
        List<ServiceValueDetail> details = null;
        switch (tranType) {
        /*
         * Caso 1: Registros de Inicio de Visita a Sanatorio Para el registro de
         * este evento, el userphone no necesita ningun registro de evento
         * previo, pero en el caso que exista un evento previo, este debe ser el
         * de finalizacion de visita a sanatorio o una visita
         */
        case CLINIC_START:

            current = getFacadeContainer().getServiceValueAPI().findCurrentClinic(
                    getUserphone(), getService());
            if (current == null) {
                return;
            }
            details = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                    current);
            if (details != null) {
                /*
                 * verificamos que el ultimo registro de detalle sea valido.
                 * para ser valido debe ser un evento de finalizacion de visita
                 * o un evento de visita rapida
                 */

                ServiceValueDetail lastDetail = details.get(details.size() - 1);
                if (lastDetail.getColumn1Chr().compareTo("ENT") != 0) {
                    return;
                }
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "visitmedic.ClinicStartRestriction"));
            }
            return;
        case CLINIC_END:

            current = getFacadeContainer().getServiceValueAPI().findCurrentClinic(
                    getUserphone(), getService());
            details = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                    current);
            if (details != null) {
                /*
                 * verificamos que el ultimo registro de detalle sea valido.
                 * para ser valido debe ser un evento de finalizacion de visita
                 * o un evento de visita rapida
                 */

                ServiceValueDetail lastDetail = details.get(details.size() - 1);
                if (lastDetail.getColumn1Chr().compareTo("ENT") == 0) {
                    /*
                     * verificamos que la visita del medico este finalizada
                     */
                    current = getFacadeContainer().getServiceValueAPI().findCurrentMedic(
                            getUserphone(), getService());
                    if (current == null) {
                        return;
                    }
                    details = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                            current);
                    lastDetail = details.get(details.size() - 1);
                    if (lastDetail.getColumn1Chr().compareTo("SAL") == 0) {
                        return;
                    } else {
                        throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                                "visitmedic.MedicNotEndedRestriction"));
                    }
                }
            }
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "visitmedic.ClinicNoStartedVisitRestriction"));
        case CLINIC_QUICK:

            current = getFacadeContainer().getServiceValueAPI().findCurrentClinic(
                    getUserphone(), getService());
            if (current == null) {
                return;
            }
            details = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                    current);
            if (details != null) {
                /*
                 * verificamos que el ultimo registro de detalle sea valido.
                 * para ser valido debe ser un evento de finalizacion de visita
                 * o un evento de visita rapida
                 */

                ServiceValueDetail lastDetail = details.get(details.size() - 1);
                if (lastDetail.getColumn1Chr().compareTo("ENT") != 0) {
                    return;
                }
            }

            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "visitmedic.ClinicStartRestriction"));
        case MEDIC_START:

            currentClinicSV = getFacadeContainer().getServiceValueAPI().findCurrentClinic(
                    getUserphone(), getService());
            details = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                    currentClinicSV);
            if (details != null) {
                /*
                 * verificamos que el ultimo registro de detalle sea valido.
                 * para ser valido debe ser un evento de finalizacion de visita
                 * o un evento de visita rapida
                 */

                ServiceValueDetail lastDetail = details.get(details.size() - 1);
                if (lastDetail.getColumn1Chr().compareTo("ENT") == 0) {
                    /*
                     * verificamos que no haya registros de medicos y que el
                     * ultimo registro se de salida
                     */
                    current = getFacadeContainer().getServiceValueAPI().findCurrentMedic(
                            getUserphone(), getService());
                    if (current == null) {
                        return;
                    } else {
                        details = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                                current);
                        lastDetail = details.get(details.size() - 1);
                        if (lastDetail.getColumn1Chr().compareTo("ENT") != 0) {
                            return;
                        }
                    }
                } else {
                    throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                            "visitmedic.OpenVisitLocal"));
                }
            }

            if (currentClinicSV == null) {
                throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                        "visitmedic.OpenVisitLocal"));
            }

            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "visitmedic.MedicStartRestriction"));
        case MEDIC_END:

            current = getFacadeContainer().getServiceValueAPI().findCurrentMedic(
                    getUserphone(), getService());
            details = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                    current);
            if (details != null) {
                /*
                 * verificamos que el ultimo registro de detalle sea valido.
                 * para ser valido debe ser un evento de finalizacion de visita
                 * o un evento de visita rapida
                 */

                ServiceValueDetail lastDetail = details.get(details.size() - 1);
                if (lastDetail.getColumn1Chr().compareTo("ENT") == 0) {
                    return;
                }
            }

            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "visitmedic.MedicNoStartedVisitRestriction"));

        case PRODUCT_REGISTER:

            current = getFacadeContainer().getServiceValueAPI().findCurrentMedic(
                    getUserphone(), getService());
            details = getFacadeContainer().getServiceValueDetailAPI().findByServiceValue(
                    current);
            if (details != null) {
                /*
                 * verificamos que el ultimo registro de detalle sea valido.
                 * para ser valido debe ser un evento de finalizacion de visita
                 * o un evento de visita rapida
                 */

                ServiceValueDetail lastDetail = details.get(details.size() - 1);
                if (lastDetail.getColumn1Chr().compareTo("ENT") == 0) {
                    return;
                }
            }

            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "visitmedic.MedicNoStartedVisitProductRestriction"));
        }

        return;

    }

    @Override
    protected ServiceValue treatHeader() {
        return null;
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        return null;
    }

    @Override
    protected void assignServiceEvent() {

        String discriminator = getEntity().getEvent();
        if (discriminator.equals("CS")) {
            tranType = VisitMedicEvent.CLINIC_START;
        } else if (discriminator.equals("CE")) {
            tranType = VisitMedicEvent.CLINIC_END;
        } else if (discriminator.equals("MS")) {
            tranType = VisitMedicEvent.MEDIC_START;
        } else if (discriminator.equals("ME")) {
            tranType = VisitMedicEvent.MEDIC_END;
        } else if (discriminator.equals("CQ")) {
            tranType = VisitMedicEvent.CLINIC_QUICK;
        }
        discriminator = getEntity().getEvent().substring(0, 2);
        if (discriminator.equals("PR")) {
            tranType = VisitMedicEvent.PRODUCT_REGISTER;
        }
    }

}
