package com.tigo.cs.api.service;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import py.com.lothar.validacion.ws.ReturnData;
import py.com.lothar.validacion.ws.ValidacionClienteWSInterface;
import us.inswitch.mts.ws.server.BindPack;
import us.inswitch.mts.ws.server.MTS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tigo.cs.api.entities.MTSResponseBean;
import com.tigo.cs.api.entities.TigoMoneyPhotoService;
import com.tigo.cs.api.entities.TigoMoneyService;
import com.tigo.cs.api.entities.ValidacionClienteBCCSNacResponseBean;
import com.tigo.cs.api.entities.ValidationClientCIBean;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.api.util.ServiceProps;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;

public abstract class TigoMoneyServiceAPI<T extends TigoMoneyService> extends AbstractServiceAPI<TigoMoneyService> {

    protected TigoMoneyEvent tranType;

    private Gson gson;
    private TigoMoneyPhotoService photos;
    private SimpleDateFormat sdf;

    protected enum TigoMoneyEvent implements ServiceEvent {

        LOGIN("LOGIN", new ServiceProps("tigomoney.name.Login", "", "")),
        CONSULTID("CONSULTID", new ServiceProps("tigomoney.name.ConsultId", "", "")), // permission.name.IconUpdate
        REGISTER("REGISTER", new ServiceProps("tigomoney.name.Register", "", ""));

        protected final String value;
        protected final ServiceProps props;

        private TigoMoneyEvent(String value, ServiceProps props) {
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

    public TigoMoneyServiceAPI() {
    }

    @Override
    public TigoMoneyService getEntity() {
        if (super.getEntity() == null) {
            setEntity(new TigoMoneyService());
        }
        return super.getEntity();
    }

    @Override
    public TigoMoneyService getReturnEntity() {
        if (super.getReturnEntity() == null) {
            setReturnEntity(new TigoMoneyService());
        }
        return super.getReturnEntity();
    }

    @Override
    protected void validate() throws InvalidOperationException {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        photos = gson.fromJson(getEntity().getPhotoEntity(),
                TigoMoneyPhotoService.class);
        getEntity().setPhotoEntity(null);
        setGrossMessageIn(getEntity().toStringWithHash());
        super.validate();
    }

    @Override
    public String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException {
        String returnMessage = tranType.value;
        String status = null;
        switch (tranType) {
        case LOGIN:
            getEntity().setRequiresLocation(false);

            MTSResponseBean responseMTS = initSession();
            String message = "";
            if (responseMTS.getAutenticated()) {
                message = getFacadeContainer().getI18nAPI().iValue(
                        "tigomoney.message.UserAuthenticatedSuccessfully");
                getFacadeContainer().getNotifier().debug(getClass(),
                        getEntity().getMsisdn(), message);
            } else {
                message = getFacadeContainer().getI18nAPI().iValue(
                        "tigomoney.message.CellphoneOrPasswordIncorrect");
                getFacadeContainer().getNotifier().debug(getClass(),
                        getEntity().getMsisdn(),
                        message + ". " + responseMTS.getMessage());
            }
            getReturnEntity().setLoginResponse(
                    responseMTS.getResponseCode().toString());
            getReturnEntity().setMessage(message);
            getReturnEntity().setResponse(message);
            getReturnEntity().setCellphoneNumber(
                    getEntity().getCellphoneNumber());
            returnMessage = getEntity().getEvent() + "%*"
                + getEntity().getCellphoneNumber() + "%*"
                + responseMTS.getResponseCode().toString() + "%*" + message;

            break;

        case CONSULTID:
            getEntity().setRequiresLocation(false);
            ValidationClientCIBean responseValidacionCliente = null;
            String name = "";
            Date date = new Date(getEntity().getBirthDate());
            try {

                boolean existsCI = getFacadeContainer().getTigoMoneyWSServiceAPI().existsCI(
                        getEntity().getIdentification(),
                        getEntity().getSource());

                if (!existsCI) {
                    // ValidacionClienteBCCSNacResponseBean
                    // responseValidacionCliente = validacionCliente(
                    // getEntity().getIdentification(), date);

                    /*
                     * Consultamos al servicio que consulta la BD de la policia
                     * nacional
                     */
                    responseValidacionCliente = getFacadeContainer().getValidationClientCIAPI().retrievePoliceBaseInfo(
                            getEntity().getMsisdn(),
                            getEntity().getIdentification(), date);

                    /*
                     * Si la cedula no es valida y el codigo de respuesta es -1
                     * significa que se recupero datos del servicio pero la
                     * CEDULA no concuerda con la fecha de nacimiento ingresada
                     */

                    if (!responseValidacionCliente.getValidCI()
                        && responseValidacionCliente.getResponseCode().equals(
                                "-1")) {
                        status = "NOTVALID";
                        getReturnEntity().setIdStatus(status);
                        getReturnEntity().setIdentification(
                                getEntity().getIdentification());
                        message = MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "tigomoney.message.ciAndBirhDontMatch"),
                                getEntity().getIdentification());
                        getReturnEntity().setMessage(message);
                        getReturnEntity().setResponse(message);

                    } else {
                        /*
                         * Si no es valida la cedula y el codigo de respuesta es
                         * ditinto a -1 significa o que el servicio no devolvio
                         * ningun dato o hubo algun error al consultar, de ser
                         * asi consultamos a la BD del BCCS
                         */
                        if (!responseValidacionCliente.getValidCI()

                            && !responseValidacionCliente.getResponseCode().equals(
                                    "-1")) {
                            responseValidacionCliente = getFacadeContainer().getValidationClientCIAPI().getClientBirthday(
                                    getEntity().getMsisdn(),
                                    getEntity().getIdentification(), date);
                        }

                    }

                    if (responseValidacionCliente.getValidCI()) {

                        status = "NOTREGISTERED";
                        name = responseValidacionCliente.getName().trim() + " "
                            + responseValidacionCliente.getLastname().trim();
                        getReturnEntity().setIdStatus(status);
                        getReturnEntity().setIdentification(
                                getEntity().getIdentification());
                        getReturnEntity().setName(name);
                        message = MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "tigomoney.message.CINotRegistered"),
                                getEntity().getIdentification());
                        getReturnEntity().setMessage(message);
                        getReturnEntity().setResponse(message);
                    } else {
                        status = "NOTVALID";
                        getReturnEntity().setIdStatus(status);
                        getReturnEntity().setIdentification(
                                getEntity().getIdentification());
                        message = MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "tigomoney.message.ciAndBirhDontMatch"),
                                getEntity().getIdentification());
                        getReturnEntity().setMessage(message);
                        getReturnEntity().setResponse(message);
                    }
                } else {
                    status = "REGISTERED";
                    getReturnEntity().setIdStatus(status);
                    getReturnEntity().setIdentification(
                            getEntity().getIdentification());
                    message = MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "tigomoney.message.ciAlreadyExists"),
                            getEntity().getIdentification());
                    getReturnEntity().setMessage(message);
                    getReturnEntity().setResponse(message);
                }

            } catch (Exception e) {
                status = "ERROR";
                getReturnEntity().setIdStatus(status);

                getReturnEntity().setIdentification(
                        getEntity().getIdentification());
                message = getFacadeContainer().getI18nAPI().iValue(
                        "tigomoney.message.ErrorInConsultingCI");
                getReturnEntity().setMessage(message);
                getReturnEntity().setResponse(message);
            }
            returnMessage = getEntity().getEvent() + "%*"
                + getEntity().getIdentification() + "%*" + status + "%*"
                + message + "%*" + name;

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    getEntity().getMsisdn(),
                    MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "tigomoney.message.validateci.message.response"),
                            getEntity().getIdentification(),
                            getSimpleDateFormat().format(date), status,
                            message, getEntity().getMsisdn(),
                            getEntity().getSource()

                    ));

            break;

        case REGISTER:

            getEntity().setPhotoEntity(null);
            String state = "OK";
            boolean responseOk = false;
            try {

                if (photos == null
                    && (getEntity().getGrossMessage() == null || getEntity().getGrossMessage().equals(
                            ""))) {
                    state = "IGNORE";
                    message = MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    "tigomoney.message.ErrorPhotoCINull"),
                            getEntity().getIdentification(),
                            getEntity().getMsisdn(),
                            getEntity().getCellphoneNumber());
                    getReturnEntity().setMessage(message);
                    getReturnEntity().setResponse(message);
                    getReturnEntity().setRegistrationResponse(state);
                    returnMessage = getEntity().getEvent() + "%*"
                        + getEntity().getIdentification() + "%*" + state + "%*"
                        + message + "%*" + getEntity().getMessageId();
                } else {

                    boolean existsCI = getFacadeContainer().getTigoMoneyWSServiceAPI().existsCI(
                            getEntity().getIdentification(),
                            getEntity().getSource());
                    if (!existsCI) {
                        responseOk = getFacadeContainer().getTigoMoneyWSServiceAPI().register(
                                getEntity().getIdentification(),
                                new Date(getEntity().getBirthDate()),
                                getEntity().getAddress(),
                                getEntity().getCity(),
                                photos != null ? photos.getFrontPhoto() : null,
                                photos != null ? photos.getBackPhoto() : null,
                                getEntity().getSource(),
                                getEntity().getRegistrationChannel());
                    } else {
                        responseOk = getFacadeContainer().getTigoMoneyWSServiceAPI().update(
                                getEntity().getIdentification(),
                                new Date(getEntity().getBirthDate()),
                                getEntity().getAddress(),
                                getEntity().getCity(),
                                photos != null ? photos.getFrontPhoto() : null,
                                photos != null ? photos.getBackPhoto() : null,
                                getEntity().getSource(),
                                getEntity().getRegistrationChannel());
                    }

                    if (responseOk) {

                        message = MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "tigomoney.message.CISuccessfullyRegistered"),
                                getEntity().getIdentification());
                        getReturnEntity().setMessage(message);
                        getReturnEntity().setResponse(message);
                        getReturnEntity().setRegistrationResponse(state);
                        returnMessage = getEntity().getEvent() + "%*"
                            + getEntity().getIdentification() + "%*" + state
                            + "%*" + message + "%*"
                            + getEntity().getMessageId();
                    } else {
                        state = "ERROR";
                        message = MessageFormat.format(
                                getFacadeContainer().getI18nAPI().iValue(
                                        "tigomoney.message.ErrorInRegistereringCI"),
                                getEntity().getIdentification());
                        getReturnEntity().setMessage(message);
                        getReturnEntity().setResponse(message);
                        getReturnEntity().setRegistrationResponse(state);
                        returnMessage = getEntity().getEvent() + "%*"
                            + getEntity().getIdentification() + "%*" + state
                            + "%*" + message + "%*"
                            + getEntity().getMessageId();
                    }
                }
            } catch (Exception e) {
                state = "ERROR";
                message = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "tigomoney.message.ErrorInRegistereringCI"),
                        getEntity().getIdentification());
                getReturnEntity().setMessage(message);
                getReturnEntity().setResponse(message);
                getReturnEntity().setRegistrationResponse(state);
                returnMessage = getEntity().getEvent() + "%*"
                    + getEntity().getIdentification() + "%*" + state + "%*"
                    + message + "%*" + getEntity().getMessageId();
                // throw new
                // InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                // "tigomoney.message.ErrorInRegistereringCI"));
            }
            getReturnEntity().setMessageId(getEntity().getMessageId());
        }
        getReturnEntity().setEvent(getEntity().getEvent());
        getEntity().setSendSMS(false);
        setServiceCod(-3L);

        return returnMessage;
    }

    private MTSResponseBean initSession() {
        MTSResponseBean toReturn = new MTSResponseBean();
        MTS mtsClient = null;
        try {
            mtsClient = getFacadeContainer().getMtsServiceAPI().getClient();
            String cellphone = CellPhoneNumberUtil.correctMsisdnToLocalFormat(getEntity().getCellphoneNumber());
            BindPack response;
            String ip = "";
            try {
                ip = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "mts.ws.ip");
            } catch (GenericFacadeException e) {
                ip = "10.90.3.46";
            }
            response = mtsClient.bind(cellphone, getEntity().getPassword(),
                    "1", ip);
            if (response != null) {
                toReturn.setAutenticated(response.getStatus() == 0 ? true : false);
                toReturn.setMessage(response.getMensaje());
                toReturn.setResponseCode(response.getStatus());
            } else {
                toReturn.setAutenticated(false);
                toReturn.setMessage("Error en la consulta al WS.");
                toReturn.setResponseCode(-1);
            }
        } catch (InvalidOperationException e) {
            e.printStackTrace();
            toReturn.setAutenticated(false);
            toReturn.setMessage("Error en la consulta al WS.");
            toReturn.setResponseCode(-1);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            toReturn.setAutenticated(false);
            toReturn.setMessage("Error en la consulta al WS.");
            toReturn.setResponseCode(-1);
        }
        return toReturn;
    }

    private ValidacionClienteBCCSNacResponseBean validacionCliente(String ci, Date birthDate) {
        ValidacionClienteBCCSNacResponseBean toReturn = new ValidacionClienteBCCSNacResponseBean();
        ValidacionClienteWSInterface validacionClient = null;
        try {
            validacionClient = getFacadeContainer().getValidacionClienteBCCSNacServiceAPI().getClient();

            SimpleDateFormat sdf;
            String pattern = "";
            try {
                pattern = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "validacionclientebccsnac.ws.datepattern");
            } catch (GenericFacadeException e) {
                e.printStackTrace();
                pattern = "ddMMyyyy";
            }
            sdf = new SimpleDateFormat(pattern);

            List<ReturnData> response = validacionClient.validar(ci,
                    "0000000000", sdf.format(birthDate));
            if (response != null && response.size() > 0) {
                for (ReturnData returnData : response) {
                    if (returnData != null) {
                        if (returnData.getKey().equalsIgnoreCase("KYC_LOW")) {
                            toReturn.setValidCI(false);
                            toReturn.setResponseCode(returnData.getKey());
                        } else if (returnData.getKey().equalsIgnoreCase(
                                "KYC_MEDIUM")) {
                            toReturn.setValidCI(true);
                            toReturn.setResponseCode(returnData.getKey());
                            toReturn.setName(returnData.getNombre());
                            toReturn.setLastname(returnData.getApellido());
                        }
                        break;
                    }
                }
            } else {
                toReturn.setValidCI(false);
                toReturn.setResponseCode(null);
                toReturn.setName(null);
                toReturn.setLastname(null);
            }

        } catch (InvalidOperationException e) {
            e.printStackTrace();
            toReturn.setValidCI(false);
            toReturn.setResponseCode(null);
            toReturn.setName(null);
            toReturn.setLastname(null);
        }
        return toReturn;
    }

    private SimpleDateFormat getSimpleDateFormat() {
        if (sdf == null) {
            sdf = new SimpleDateFormat("ddMMyyyy");
        }
        return sdf;
    }

    @Override
    protected void assignServiceEvent() {
        if (getEntity().getEvent().equals("LOGIN")) {
            tranType = TigoMoneyEvent.LOGIN;
        } else if (getEntity().getEvent().equals("CONSULTID")) {
            tranType = TigoMoneyEvent.CONSULTID;
        } else {
            tranType = TigoMoneyEvent.REGISTER;
        }
    }

    @Override
    protected ServiceValue treatHeader() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) {
        throw new UnsupportedOperationException();
    }

}