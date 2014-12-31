package com.tigo.cs.api.service;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;
import com.tigo.cs.commons.util.Cypher;
import com.tigo.cs.domain.AndroidVersion;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.DataCheck;
import com.tigo.cs.domain.Message;
import com.tigo.cs.domain.Route;
import com.tigo.cs.domain.Userphone;

public abstract class BasicAPI<T extends BaseServiceBean> {

    public static String NO_INTEGRATION_ENABLED_MSG = null;
    public static String MALFORMED_MSG = null;
    public static String CANNOT_PROCESS_MSG = null;
    public static String CANNOT_PROCESS_WS = null;

    private T entity;
    private T returnEntity;
    protected boolean initMessages = false;

    protected HashMap<String, HashMap<String, String>> nodes;
    protected Userphone userphone;
    private Client client;
    private Message message;
    private BigInteger cellphoneNumber;

    private Application application;
    protected String returnMessage;
    protected String shortNumber;
    private String grossMessageIn;
    private String[] messageBody;
    private Long fiveMinute;

    protected BasicAPI() {
    }

    public abstract FacadeContainer getFacadeContainer();

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public T getReturnEntity() {
        return returnEntity;
    }

    public void setReturnEntity(T returnEntity) {
        this.returnEntity = returnEntity;
    }

    protected void init() {
        this.init(entity);
    }

    protected void init(T entity) {
        this.init(entity.getMsisdn(), entity.toStringWithHash());
    }

    protected void init(String msisdn, String grossMessageIn) {
        setGrossMessageIn(grossMessageIn);
        this.init(msisdn);
    }

    protected Date validateDate() {
        if (entity.getGeneredDate() != null) {
            return new Date(entity.getGeneredDate());
        } else if (entity.getArrivedEventDate() != null) {
            return new Date(entity.getArrivedEventDate());
        } else {
            return new Date();

        }
    }

    private void init(String msisdn) {
        if (entity != null) {
            entity.setArrivedEventDate(new Date().getTime());
        }
        setCellphoneNumber(new BigInteger(msisdn));
        setMessage(null);
        setUserphone(null);
        setReturnEntity(null);
        try {
            if (!initMessages) {
                shortNumber = getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "sms.shortnumber");
                NO_INTEGRATION_ENABLED_MSG = getFacadeContainer().getI18nAPI().iValue(
                        "restriction.ServiceDataIntegrationDisabled");
                MALFORMED_MSG = getFacadeContainer().getI18nAPI().iValue(
                        "restriction.InvalidMessageStructure");
                CANNOT_PROCESS_MSG = getFacadeContainer().getI18nAPI().iValue(
                        "restriction.MessageCannotBeProcessed");
                CANNOT_PROCESS_WS = getFacadeContainer().getI18nAPI().iValue(
                        "restriction.CannotComunicateWithClientServer");
                returnMessage = CANNOT_PROCESS_MSG;
                initMessages = true;
            }
        } catch (Exception e) {
            Logger.getLogger(AbstractServiceAPI.class.getName()).log(
                    Level.SEVERE, e.getMessage(), e);
        }

    }

    public String getShortNumber() {
        try {
            if (getEntity().getUssd() != null && getEntity().getUssd()) {
                return getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "ussd.shortNumber");
            } else {
                return getFacadeContainer().getGlobalParameterAPI().findByCode(
                        "sms.shortnumber");
            }
        } catch (Exception e) {
            Logger.getLogger(AbstractServiceAPI.class.getName()).log(
                    Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public HashMap<String, HashMap<String, String>> getNodes() {
        return nodes;
    }

    public void setNodes(HashMap<String, HashMap<String, String>> nodes) {
        this.nodes = nodes;
    }

    public String getGrossMessageIn() {
        return grossMessageIn;
    }

    public void setGrossMessageIn(String grossMessageIn) {
        if (this.grossMessageIn == null) {
            this.grossMessageIn = grossMessageIn;
        }
    }

    public BigInteger getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(BigInteger cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public Application getApplication() {
        return application;
    }

    protected void setApplication(Application application) {
        this.application = application;
    }

    public Userphone getUserphone() {
        if (userphone == null) {
            try {
                userphone = getFacadeContainer().getUserphoneAPI().findActiveByCellphoneNum(
                        getCellphoneNumber());
                client = userphone.getClient();
            } catch (Exception e) {
                userphone = null;
            }
        }
        return userphone;
    }

    public void setUserphone(Userphone userphone) {
        this.userphone = userphone;
    }

    public Client getClient() throws InvalidOperationException {
        if (client == null) {
            if (getUserphone() != null) {
                client = getUserphone().getClient();
            }
        }
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    protected Cell getCell() {

        if (getEntity().getLatitude() == null
            && getEntity().getLongitude() == null
            && getEntity().getRequiresLocation()) {
            return getFacadeContainer().getCellAPI().getCellId(getUserphone(),
                    getLAC(), getCellId());
        }
        return null;
    }

    protected Integer getCellId() {
        if (entity.getCellId() != null) {
            return entity.getCellId();
        } else {
            return null;
        }
    }

    protected Integer getLAC() {

        if (entity.getLac() != null) {
            return entity.getLac();
        } else {
            return null;
        }

    }

    protected String getNodeValue(String nodeCode) {
        if (nodes.get(nodeCode) != null) {
            return nodes.get(nodeCode).get("Value");
        }
        return null;
    }

    protected String getNotNullNodeValueFrom(String... nodeCode) {
        if (nodeCode.length > 0) {
            for (int i = 0; i < nodeCode.length; i++) {
                String node = nodeCode[i];
                if (nodes.get(node) != null) {
                    return nodes.get(node).get("Value");
                }
            }
        }
        return null;
    }

    public String[] getMessageBody() {
        if (messageBody == null) {
            if (getEntity().getGrossMessage().contains("ü")) {
                getFacadeContainer().getNotifier().info(BasicAPI.class,
                        getCellphoneNumber().toString(),
                        "Special Char Converted: ü to ~");
                getEntity().setGrossMessage(
                        getEntity().getGrossMessage().replace("ü", "~"));
            }
            messageBody = getEntity().getGrossMessage().split("\\%+\\*");
        }
        return messageBody;
    }

    public void setMessageBody(String[] messageBody) {
        this.messageBody = messageBody;
    }

    public String getMsgElement(int index) throws InvalidOperationException {
        try {
            return getMessageBody()[index];
        } catch (Exception e) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "restriction.InvalidMessageStructure"));
        }
    }

    public int getMsgLength() {
        return getMessageBody().length;
    }

    public Message getMessage(String messageIn, String messageOut) {
        String cellphoneNumber = CellPhoneNumberUtil.correctMsisdnToLocalFormat(getCellphoneNumber().toString());
        if (message == null) {
            message = new Message();
            /*
             * si el campo generated date tiene algun valor, verificamos que la
             * hora de la generacion del evento no sea mucho mayor que la hora
             * de arribo del mensaje
             */

            // if (entity.getGeneredDate() != null) {
            // // TODO: fix
            // message.setDateinDat(new Date(entity.getGeneredDate()));
            // } else if (entity.getArrivedEventDate() != null) {
            // message.setDateinDat(new Date(entity.getArrivedEventDate()));
            // } else {
            // message.setDateinDat(new Date());
            // }

            message.setDateinDat(validateDate());
            if (messageIn.length() > 2000) {
                messageIn = messageIn.substring(0, 1999);
                message.setMessageinChr(messageIn);
                message.setLengthinNum(messageIn.length());
            }
            message.setDestinationChr(getShortNumber());
            message.setOriginChr(cellphoneNumber);
            if (entity != null && entity.getLatitude() != null
                && entity.getLongitude() != null) {
                message.setLatitude(entity.getLatitude());
                message.setLongitude(entity.getLongitude());
            }

            message.setApplication(getApplication());

            if (getUserphone() != null) {
                message.setUserphone(getUserphone());
            }
            if (messageOut != null) {
                message.setDateoutDat(new Date());
                message.setMessageoutChr(messageOut);
                message.setLengthoutNum(messageOut.length());
                messageOut = null;

            }
            message.setCell(getCell());
            try {
                message.setClient(getClient());
            } catch (InvalidOperationException e) {
                e.printStackTrace();
            }
            message = getFacadeContainer().getMessageAPI().create(message);
        }
        if (messageOut != null) {
            message.setDateoutDat(new Date());
            message.setMessageoutChr(messageOut);
            message.setLengthoutNum(messageOut.length());
            message = getFacadeContainer().getMessageAPI().edit(message);
        }

        return message;
    }

    public Message getMessage() {
        return getMessage(getGrossMessageIn(), null);
    }

    public Message setMessageForFailedOperation(String messageOut) {
        return getMessage(getGrossMessageIn(), messageOut);
    }

    public Message updateMessage(String messageOut) {
        return getMessage(getGrossMessageIn(), messageOut);
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * validar si el userphone se encuentre en una de las celdas cuyo area de
     * cobertura incluye al siguiente punto en su hoja de ruta
     * 
     * @throws InvalidOperationException
     */
    protected void validate() throws InvalidOperationException, OperationNotAllowedException {

        Long fiveMinutes = 5 * 60 * 1000L;

        try {
            if (entity.getHash() != null) {
                String hash = Cypher.decrypt(entity.getMsisdn(),
                        entity.getHash());
                // if (hash.compareTo(entity.getMsisdn() +
                // entity.getTimestamp()) == 0) {
                // if (new Date().getTime() - entity.getTimestamp() >=
                // fiveMinutes) {
                // throw new InvalidOperationException(MessageFormat.format(
                // getFacadeContainer().getI18nAPI().iValue(
                // "restiction.hmac"),
                // entity.toStringWithHash()));
                // }
                // } else {
                // throw new
                // InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                // "restiction.hmac"));
                // }

                /*
                 * validamos el imei y el imsi del registro de mensajes
                 */

                if (getUserphone().getCurrentImei() != null
                    && getEntity().getImei() != null
                    && getUserphone().getCurrentImei().compareToIgnoreCase(
                            getEntity().getImei()) != 0) {

                    getFacadeContainer().getNotifier().debug(
                            getClass(),
                            getCellphoneNumber().toString(),
                            MessageFormat.format(
                                    "BMA - ANDROID - El IMEI del equipo no es el habilitado para marcaciones: {0}",
                                    getEntity().getImei()));

                    throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                            "restiction.imei"));

                }

                if (getUserphone().getCurrentImsi() != null
                    && getEntity().getImsi() != null
                    && getUserphone().getCurrentImsi().compareToIgnoreCase(
                            getEntity().getImsi()) != 0) {

                    getFacadeContainer().getNotifier().debug(
                            getClass(),
                            getCellphoneNumber().toString(),
                            MessageFormat.format(
                                    "BMA - ANDROID - El IMSI del equipo no es el habilitado para marcaciones: {0}",
                                    getEntity().getImsi()));

                    throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                            "restiction.imsi"));

                }

            }

        } catch (Exception e) {
            throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                    "restiction.hmac"));
        }

        if (getEntity().getVersionName() != null) {

            getFacadeContainer().getNotifier().debug(
                    getClass(),
                    getCellphoneNumber().toString(),
                    MessageFormat.format(
                            "BMA - ANDROID - Se valida la version de la aplicacion: {0}",
                            getEntity().getVersionName()));

            AndroidVersion androidVersion = getFacadeContainer().getAndroidVersionAPI().findByVersionName(
                    getEntity().getVersionName());

            if (androidVersion == null) {
                androidVersion = getFacadeContainer().getAndroidVersionAPI().findByVersionName(
                        "default");
            }

            if (androidVersion != null && androidVersion.getEnabled()
                && androidVersion.getDeprecatedMessage() != null) {

                getFacadeContainer().getNotifier().debug(
                        getClass(),
                        getCellphoneNumber().toString(),
                        MessageFormat.format(
                                "BMA - ANDROID - Deprecated version. Enviando mensaje al usuario: {0}",
                                getEntity().getVersionName()));

                getFacadeContainer().getSmsQueueAPI().sendToJMS(
                        getEntity().getMsisdn(), getApplication(),
                        androidVersion.getDeprecatedMessage());

            } else if (androidVersion != null && !androidVersion.getEnabled()) {

                getFacadeContainer().getNotifier().info(
                        getClass(),
                        getCellphoneNumber().toString(),
                        MessageFormat.format(
                                "BMA - ANDROID - Version deshabilitada: {0}",
                                getEntity().getVersionName()));

                throw new InvalidOperationException(androidVersion.getDisabledMessage());
            }
        }

        /*
         * verificamos que el usuario tenga asignada una hoja de ruta
         */
        if (getFacadeContainer().getRouteAPI().validateRouteUserphone(userphone)) {

            Cell cell = getCell();

            Route route = getFacadeContainer().getRouteAPI().currentRoute(
                    userphone);

            if (!route.getOffrouteMarkotion()) {
                if (cell == null) {
                    getFacadeContainer().getNotifier().signal(
                            BasicAPI.class,
                            Action.ERROR,
                            getFacadeContainer().getI18nAPI().iValue(
                                    "ussd.validator.CellNotFound")
                                + getCellId());
                    throw new InvalidOperationException(getFacadeContainer().getI18nAPI().iValue(
                            "ussd.validator.ErrorInDataProcessing"));
                }
            }
            getFacadeContainer().getRouteDetailAPI().getNextRouteDetailInCoverage(
                    getUserphone(), cell);
        }

    }

    protected void validateEntry(String valueDescription, String value, DataCheck dataCheck) throws InvalidOperationException {
        if (value != null) {
            value = value.trim();
        }
        /*
         * verificamos que el valor dentro del HM pase el chequeo del tipo de
         * dato
         */

        /*
         * verificamos si el valor es opcional y si es opcional que el usuario
         * haya introducido algun valor
         */
        if (!dataCheck.getOptional()) {
            if (value == null || (value != null && value.length() == 0)) {
                String msj = MessageFormat.format(
                        getFacadeContainer().getI18nAPI().iValue(
                                "check.datatype.message.Optional"),
                        valueDescription);
                setMessageForFailedOperation(msj);
                throw new InvalidOperationException(msj);
            }
        }

        /*
         * verificamos si el valor coincide con la expresion regular definida
         * para ese tipo de dato
         */
        if (value != null
            && !value.matches(dataCheck.getCheckDatatype().getRegexp())) {
            String msj = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            dataCheck.getCheckDatatype().getI18nMessage()),
                    valueDescription, value);
            setMessageForFailedOperation(msj);
            throw new InvalidOperationException(msj);
        }

        if (value != null && value.length() > dataCheck.getSizeNum()) {
            String msj = MessageFormat.format(
                    getFacadeContainer().getI18nAPI().iValue(
                            "check.datatype.message.MaxLength"),
                    valueDescription, value, value.length(),
                    dataCheck.getSizeNum());
            throw new InvalidOperationException(msj);
        }

        if (value != null
            && dataCheck.getCheckDatatype().getAditionalValidation()) {
            if (dataCheck.getCheckDatatype().getDatatypeChr().equals("date")) {

                boolean validDate = true;
                String[] componentes = value.split("/");
                Integer dia = Integer.parseInt(componentes[0]);
                Integer mes = Integer.parseInt(componentes[1]);
                Integer anho = Integer.parseInt(componentes[2]);
                /*
                 * validamos que si el dia es 31 el mes sea valido
                 */
                if (dia == 31
                    && (mes == 4 || mes == 6 || mes == 9 || mes == 11)) {
                    validDate = false;
                } /*
                   * validamos que el dia 30 corresponda a los meses restantes y
                   * no sea el mes 2
                   */else if (dia >= 30 && mes == 2) {
                    validDate = false;
                } /*
                   * validamos que el anho sea bisiestos y el mes es 2 y el dia
                   * 29
                   */else if (mes == 2 && dia == 29
                    && !(anho % 4 == 0 && (anho % 100 != 0 || anho % 400 == 0))) {
                    validDate = false;
                }
                if (!validDate) {
                    String msj = MessageFormat.format(
                            getFacadeContainer().getI18nAPI().iValue(
                                    dataCheck.getCheckDatatype().getI18nMessage()),
                            valueDescription, value);
                    setMessageForFailedOperation(msj);
                    throw new InvalidOperationException(msj);
                }
            }
        }
    }

}
