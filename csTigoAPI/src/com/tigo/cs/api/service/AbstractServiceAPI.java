package com.tigo.cs.api.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.tigo.cs.api.annotations.MetaColumn;
import com.tigo.cs.api.entities.BaseServiceBean;
import com.tigo.cs.api.entities.ServiceBean;
import com.tigo.cs.api.enumerate.Restriction;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.enumerates.MetaNames;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.exception.MalformedSMSException;
import com.tigo.cs.api.exception.OperationNotAllowedException;
import com.tigo.cs.api.exception.TrackingException;
import com.tigo.cs.api.interfaces.ClientWSOperation;
import com.tigo.cs.api.interfaces.ServiceEvent;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.Cypher;
import com.tigo.cs.domain.Cell;
import com.tigo.cs.domain.Meta;
import com.tigo.cs.domain.MetaClient;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaMember;
import com.tigo.cs.domain.Service;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.ws.client.ClientWS;
import com.tigo.cs.ws.client.ClientWSService;
import com.tigo.cs.ws.client.Customer;
import com.tigo.cs.ws.client.Guard;
import com.tigo.cs.ws.client.Motive;
import com.tigo.cs.ws.client.Product;

public abstract class AbstractServiceAPI<T extends BaseServiceBean> extends ServiceBasicAPI<T> implements ClientWSOperation {

    protected Long serviceCod = null;
    private static final String CELL_ID = "CELL_ID";
    private static final String LAC = "LAC";
    private static final String CHANNEL = "CHANNEL";
    private static final String USSD = "USSD";
    protected Service service;
    protected Boolean validation = false;
    protected Meta meta;

    private ClientWSService wsService;
    private String wsURL = null;
    private final String basePattern = "[{0} - {1}] - ";

    protected String baseLogMessage = null;
    protected String channel;

    public AbstractServiceAPI() {
    }

    public String execute(T entity) {
        setEntity(entity);
        return execute();
    }

    public String execute(String msisdn, HashMap<String, HashMap<String, String>> hm) {
        setNodes(hm);
        setEntity(null);
        getEntity().setMsisdn(msisdn);
        getEntity().setUssd(true);
        getEntity().setGrossMessage(hm.toString());
        setGrossMessageIn(getEntity().toStringWithHash());
        setCellphoneNumber(new BigInteger(msisdn));
        getEntity().setApplicationKey("u55d");
        Integer cellIDUSSD = null;
        Integer lacUSSD = null;
        if (getNodeValue(CELL_ID) != null) {
            cellIDUSSD = Integer.parseInt(getNodeValue(CELL_ID));
        }
        if (getNodeValue(LAC) != null) {
            lacUSSD = Integer.parseInt(getNodeValue(LAC));
        }
        if (cellIDUSSD != null && getEntity().getCellId() == null) {
            getEntity().setCellId(cellIDUSSD);
        }
        if (lacUSSD != null && getEntity().getLac() == null) {
            getEntity().setLac(lacUSSD);
        }

        setChannel(getNodeValue(CHANNEL));
        return execute();
    }

    public String execute() {
        Date startDate = new Date();
        try {

            try {
                getFacadeContainer().getNotifier().debug(
                        getClass(),
                        getEntity().getMsisdn(),
                        MessageFormat.format(
                                "BMA - SERVICE - INPUT - El mensaje a procesar es: {0}",
                                getEntity().toString()));
            } catch (Exception e) {

            }
            init();

            baseLogMessage = MessageFormat.format(
                    basePattern,
                    getService() != null ? getService().getDescriptionChr() : "NOSERVICEDATALOADED",
                    getUserphone() != null ? getUserphone().getCellphoneNum().toString() : "NOUSERPHONEDATALOADED");

            try {
                getFacadeContainer().getNotifier().debug(
                        getClass(),
                        getEntity().getMsisdn(),
                        MessageFormat.format(
                                "BMA - SERVICE - Se procesara un mensaje del servicio de {0}.",
                                getService().getDescriptionChr()));
            } catch (Exception e) {

            }

            if (getEntity().getAndroid()) {
                try {

                    getFacadeContainer().getNotifier().debug(
                            getClass(),
                            getEntity().getMsisdn(),
                            MessageFormat.format(
                                    "ANDROID - GPS - Estado del GPS: {0}",
                                    (getEntity().getGpsEnabled() != null ? getEntity().getGpsEnabled() ? "ON" : "OFF" : "UNKNOW")));
                } catch (IndexOutOfBoundsException e) {
                    getFacadeContainer().getNotifier().warn(getClass(),
                            getEntity().getMsisdn(),
                            "ANDROID - GPS - No se obtuvo datos del estado del GPS");
                }
                try {
                    getFacadeContainer().getNotifier().debug(
                            getClass(),
                            getEntity().getMsisdn(),
                            MessageFormat.format(
                                    "ANDROID - GPS - Precision del GPS: {0}",
                                    getEntity().getGpsAccuracy()));
                } catch (IndexOutOfBoundsException e) {
                    getFacadeContainer().getNotifier().warn(getClass(),
                            getEntity().getMsisdn(),
                            "ANDROID - GPS - No se obtuvo datos de la precision del GPS");
                }

                try {

                    getFacadeContainer().getNotifier().debug(
                            getClass(),
                            getEntity().getMsisdn(),
                            MessageFormat.format(
                                    "ANDROID - VERSION - Version de la aplicacion instalada en el movil: {0}",
                                    getEntity().getVersionName() != null ? getEntity().getVersionName() : "UNKNOW"));
                } catch (IndexOutOfBoundsException e) {
                    getFacadeContainer().getNotifier().warn(getClass(),
                            getEntity().getMsisdn(),
                            "ANDROID - VERSION - No se obtuvo datos de la version de la aplicacion");
                }
            }

            assignEvent();
            assignServiceEvent();
            convertToBean();
            validate();
            returnMessage = processService();
            if (getMessage() != null) {
                getMessage().setService(getService());
                getMessage().setVersionName(getEntity().getVersionName());
            }
            updateMessage(returnMessage);

        } catch (TrackingException ex) {
            getReturnEntity().setEvent(null);
            getFacadeContainer().getNotifier().warn(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage());
            setMessageForFailedOperation(ex.getMessage());
            returnMessage = null;
            if (getMessage() != null) {
                getMessage().setService(getService());
                getMessage().setVersionName(getEntity().getVersionName());
            }
            updateMessage(ex.getMessage());
        } catch (InvalidOperationException ex) {
            getReturnEntity().setEvent(null);
            getFacadeContainer().getNotifier().warn(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage());
            setMessageForFailedOperation(ex.getMessage());
            returnMessage = ex.getMessage();
            if (getEntity().getAndroid()) {
                getEntity().setEncryptResponse(false);
                if (getEntity().getServiceCod() == null) {
                    setServiceCod(0L);
                }
            }
            if (getMessage() != null) {
                getMessage().setService(getService());
                getMessage().setVersionName(getEntity().getVersionName());
            }
            updateMessage(returnMessage);
        } catch (OperationNotAllowedException ex) {
            getReturnEntity().setEvent(null);
            getFacadeContainer().getNotifier().warn(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage());
            setMessageForFailedOperation(ex.getMessage());
            returnMessage = ex.getMessage();
            if (getEntity().getAndroid()) {
                getEntity().setEncryptResponse(false);
                if (getEntity().getServiceCod() == null) {
                    setServiceCod(0L);
                }
            }
            if (getMessage() != null) {
                getMessage().setService(getService());
                getMessage().setVersionName(getEntity().getVersionName());
            }
            updateMessage(returnMessage);
        } catch (Exception ex) {
            getReturnEntity().setEvent(null);
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
            String msg = getFacadeContainer().getI18nAPI().iValue(
                    "error.DispatchmentGeneralError");
            getMessage(getEntity().toStringWithHash(), msg);
            returnMessage = msg;
            if (getEntity().getAndroid()) {
                getEntity().setEncryptResponse(false);
                if (getEntity().getServiceCod() == null) {
                    setServiceCod(0L);
                }
            }
            if (getMessage() != null) {
                getMessage().setService(getService());
                getMessage().setVersionName(getEntity().getVersionName());
            }
            updateMessage(returnMessage);
        }

        /*
         * Ajustes para agregar la confirmacion de envio de SMS segun la
         * configuracion del cliente esta configuracion se realiza en la
         * aplicacion administrativa, por defecto se envian las confirmaciones
         */

        if (getReturnEntity().getResponse() == null) {
            getReturnEntity().setResponse(returnMessage);
        }

        String returnMessageNoServiceCod = null;
        if (getEntity().getAndroid() && returnMessage != null) {
            if (getEntity().getEncryptResponse() && returnMessage != null) {
                try {
                    getFacadeContainer().getNotifier().info(getClass(),
                            getCellphoneNumber().toString(),
                            "Mensaje desencriptado:  " + returnMessage);
                    returnMessage = Cypher.encrypt(getEntity().getMsisdn(),
                            returnMessage);
                } catch (Exception e) {
                    getFacadeContainer().getNotifier().error(
                            AbstractServiceAPI.class,
                            getCellphoneNumber().toString(), e.getMessage(), e);
                    returnMessage = getFacadeContainer().getI18nAPI().iValue(
                            "error.DispatchmentGeneralError");
                }
            }
            if (getServiceCod() != null && returnMessage != null) {
                if (getFacadeContainer().isSendSMSViaWS()
                    && (getEntity().getSendSMS() != null && getEntity().getSendSMS())) {
                    returnMessageNoServiceCod = returnMessage;
                }
                returnMessage = getServiceCod().toString().concat("%*").concat(
                        returnMessage);
            }
        }

        String messageCutted = null;
        if (getFacadeContainer().isCutMessage() && returnMessage != null
            && returnMessage.length() >= 154
            && (getChannel() == null || getChannel().equals(USSD))) {
            messageCutted = returnMessage.substring(0, 153);
            messageCutted = messageCutted.concat(" [...]");
        }

        try {
            if (getFacadeContainer().isSendSMSViaWS() && returnMessage != null
                && getEntity().getSendSMS() && getClient().getSendConfirm()) {
                if (returnMessageNoServiceCod != null) {
                    getFacadeContainer().getSmsQueueAPI().sendToJMS(
                            getCellphoneNumber().toString(), getApplication(),
                            returnMessageNoServiceCod);
                } else {
                    getFacadeContainer().getSmsQueueAPI().sendToJMS(
                            getCellphoneNumber().toString(), getApplication(),
                            returnMessage);
                }
            }

            if (getClient() != null && !getClient().getSendConfirm()) {
                return null;
            }
        } catch (InvalidOperationException e) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), e.getMessage(), e);
            e.printStackTrace();
        }

        if (messageCutted != null) {
            return messageCutted;
        }

        Date endDate = new Date();
        // getFacadeContainer().getNotifier().debug(
        // getClass(),
        // getCellphoneNumber().toString(),
        // MessageFormat.format(
        // "BMA - SERVICE - TIME - El mensaje de servicio se proceso en {0,number,#} milisegundos",
        // endDate.getTime() - startDate.getTime()));

        return returnMessage;
    }

    protected abstract void assignEvent() throws InvalidOperationException;

    public abstract String processService() throws GenericFacadeException, InvalidOperationException, OperationNotAllowedException;

    /**
     * Create the header of the transaction if necessary, in any case save the
     * transaction through the {@link ServiceValue} entity.
     * <p>
     * For the operation this method has available the instance variable of the
     * class.
     * 
     * @return a {@code ServiceValue} entity representing the transaction
     *         header. ejb remote interfaces.
     * @throws InvalidOperationException
     */
    protected abstract ServiceValue treatHeader() throws InvalidOperationException;

    /**
     * Create the detail of the transaction if necessary.
     * <p>
     * For the operation this method has available the instance variable of the
     * class.
     * 
     * @param serviceValue
     *            the header of the detail
     * @return A list of {@link ServiceValueDetail} representing the transaction
     *         details. ejb remote interfaces.
     * @throws InvalidOperationException
     */
    protected abstract List<ServiceValueDetail> treatDetails(ServiceValue serviceValue) throws InvalidOperationException;

    /**
     * Set the {@link ServiceEvent} type given specific value in the string
     * message.
     * <p>
     * For example, considering a Collection implementation:
     * 
     * <pre>
     * String discriminator = getMsgElement(1);
     * if (discriminator.equals(&quot;CCO&quot;)) {
     *     tranType = CollectionTransaction.SEARCH_CLIENT_BYCODE;
     * } else if (discriminator.equals(&quot;CNO&quot;)) {
     *     tranType = CollectionTransaction.SEARCH_CLIENT_BYNAME;
     * } else {
     *     tranType = CollectionTransaction.COLLECTION_REGISTRATION;
     * }
     * </pre>
     * 
     * Where tranType is Enum type instance that should implements ServiceEvent.
     * This method is intentionally called in the constructor of the inherited
     * AbstractDriver.
     * 
     * @throws MalformedSMSException
     *             if the discriminator can not be resolved.
     */
    protected void assignServiceEvent() {
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Service getService() {
        if (service == null && !validation) {
            service = getFacadeContainer().getServiceAPI().find(getServiceCod());
        }
        return service;
    }

    public Long getServiceCod() {
        if (serviceCod == null) {
            ServiceIdentifier annotation = getClass().getAnnotation(
                    ServiceIdentifier.class);
            if (annotation != null) {
                if (annotation.id().getValidation()) {
                    validation = true;
                } else {
                    serviceCod = annotation.id().value();
                }
            }
        }
        return serviceCod;
    }

    public void setServiceCod(Long serviceCod) {
        this.serviceCod = serviceCod;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Boolean getValidation() {
        return validation;
    }

    protected boolean hasMetaIntegrationEnabled(Long codClient, MetaNames meta) {
        MetaClient metaClient = getFacadeContainer().getMetaClientAPI().findByMetaAndClient(
                codClient, meta.value());
        if (metaClient != null && metaClient.getEnabledChr()) {
            return true;
        }
        return false;
    }

    protected boolean hasWSIntegrationEnabled() {
        try {
            String enabled = getFacadeContainer().getClientParameterValueAPI().findByCode(
                    getClient().getClientCod(), "client.ws.enabled");
            if (enabled != null && enabled.equals("1")) {
                String url = getFacadeContainer().getClientParameterValueAPI().findByCode(
                        getClient().getClientCod(), "client.ws.url");
                if (url != null && !url.isEmpty()) {
                    wsURL = url;
                    return true;
                }
            }
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
        }
        return false;
    }

    public QName getWSServiceQName(URL url) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(url.openStream());
            Element docEle = document.getDocumentElement();
            String namespaceURI = docEle.getAttribute("targetNamespace");
            String localPart = docEle.getAttribute("name");
            QName qName = new QName(namespaceURI, localPart);
            return qName;
        } catch (SAXException ex) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
        } catch (IOException ex) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
        }
        return null;
    }

    public QName getWSPortQName(URL url) {
        QName qName = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(url.openStream());
            Element docEle = document.getDocumentElement();
            String namespaceURI = docEle.getAttribute("targetNamespace");

            NodeList serviceList = docEle.getElementsByTagName("service");
            for (int i = 0; i < serviceList.getLength(); i++) {
                // obtengo el primer y unico elemento service
                Node serviceNode = serviceList.item(i);
                if (serviceNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element serviceElem = (Element) serviceNode;
                    NodeList portList = serviceElem.getElementsByTagName("port");

                    for (int j = 0; j < portList.getLength(); j++) {
                        Node portNode = portList.item(j);
                        if (portNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element portElem = (Element) portNode;
                            String port = portElem.getAttribute("name");
                            qName = new QName(namespaceURI, port);
                        }
                    }
                }
            }
            return qName;
        } catch (SAXException ex) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
        } catch (IOException ex) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            getFacadeContainer().getNotifier().error(AbstractServiceAPI.class,
                    getCellphoneNumber().toString(), ex.getMessage(), ex);
        }
        return null;
    }

    protected ClientWSService getWSService() throws MalformedURLException {
        if (wsService == null) {
            wsService = new ClientWSService(new URL(wsURL));
        }
        return wsService;
    }

    protected ClientWS getWSPort() throws MalformedURLException {
        try {
            return getWSService().getClientWSPort();
        } catch (Exception e) {
            return null;
        }
    }

    protected String getReturnMessageByMetaDataCode(MetaNames meta, String code, ServiceEvent event) throws InvalidOperationException {
        String name = getMetaDataByCode(meta, code);
        if (name != null) {
            String pattern = getFacadeContainer().getI18nAPI().iValue(
                    event.getSuccessMessage());
            return MessageFormat.format(
                    pattern,
                    getFacadeContainer().getI18nAPI().iValue(meta.description()),
                    code, name);
        } else {
            String pattern = getFacadeContainer().getI18nAPI().iValue(
                    event.getNoMatchMessage());
            return MessageFormat.format(
                    pattern,
                    code,
                    getFacadeContainer().getI18nAPI().iValue(meta.description()));
        }
    }

    protected String getReturnMessageByMetaDataValue(MetaNames meta, String value, ServiceEvent event, int limit) {
        try {
            String pattern = getFacadeContainer().getI18nAPI().iValue(
                    event.getNoMatchMessage());
            String returnMessage = MessageFormat.format(pattern, value);
            List<String> values = getMetaDataByValue(meta, value);
            if (values != null && values.size() > 0) {
                int totalValues = values.size();
                int displayValues = values.size() > limit ? limit : totalValues;

                String numberPattern = "\n{0,number,#}){1}";
                String result = "";
                for (int i = 0; i < displayValues; i++) {
                    result += MessageFormat.format(numberPattern, i + 1,
                            values.get(i));
                }
                pattern = getFacadeContainer().getI18nAPI().iValue(
                        event.getSuccessMessage());
                returnMessage = MessageFormat.format(
                        pattern,
                        getFacadeContainer().getI18nAPI().iValue(
                                meta.description()), value, displayValues,
                        totalValues, result);

            } else {
                returnMessage = MessageFormat.format(
                        pattern,
                        value,
                        getFacadeContainer().getI18nAPI().iValue(
                                meta.description()));
            }
            return returnMessage;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().log(getClass(),
                    getCellphoneNumber().toString(), Action.ERROR,
                    e.getMessage(), e);
            return NO_INTEGRATION_ENABLED_MSG;
        }
    }

    protected String getMetaDataByCode(MetaNames meta, String code) throws InvalidOperationException {

        List<MetaMember> memberList = getFacadeContainer().getMetaMemberAPI().findReturnableByCodMeta(
                meta.value());
        String valor = "";
        for (MetaMember member : memberList) {
            MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                    getClient().getClientCod(), meta.value(),
                    member.getMetaMemberPK().getMemberCod(), code);
            String result = getReturnMessageMetaData(md);
            if (result != null) {
                valor = valor.concat(result);
            }
        }
        return valor != null && valor.trim().length() > 0 ? valor : null;

    }

    protected String getMetaDataForIntegrationByCode(MetaNames meta, String code) throws InvalidOperationException {

        MetaData md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                getClient().getClientCod(), meta.value(), 1L, code);

        if (md != null) {
            return md.getValueChr();
        }
        return null;
    }

    protected List<String> getMetaDataByValue(MetaNames meta, String value) {

        try {

            /*
             * se modifica la busqueda por valor, configurable por mas de un
             * campo y por cada espacio que existe en el value se hace una
             * busqueda por ej. si value es: Miguel Maciel se buscaran todas las
             * coincidencias con Miguel y con Maciel
             */
            String wherePattern = " upper (m.valueChr) LIKE upper(''%{0}%'') ";
            String where = "";
            try {
                if (value.contains(" ")) {
                    String[] values = value.split(" ");
                    for (int i = 0; i < values.length; i++) {
                        where = where.concat(MessageFormat.format(wherePattern,
                                values[i]));
                        if (i != values.length - 1) {
                            where = where.concat(" OR ");
                        }
                    }
                    where = "(".concat(where).concat(")");
                } else {
                    where = MessageFormat.format(wherePattern, value);
                }

            } catch (Exception e) {

            }
            List<String> resultList = null;
            List<MetaData> mdList = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndValue(
                    getClient().getClientCod(), meta.value(), where);

            Map<String, String> resultMap = null;

            for (MetaData md : mdList) {
                if (resultMap == null) {
                    resultMap = new HashMap<String, String>();
                }
                String codeChr = resultMap.get(md.getMetaDataPK().getCodeChr());
                if (codeChr != null) {
                    String pattern = "{0} {1}";
                    String result = MessageFormat.format(pattern, codeChr,
                            md.getValueChr());
                    resultMap.put(md.getMetaDataPK().getCodeChr(), result);
                } else {
                    String pattern = "{0},{1}";
                    String result = MessageFormat.format(pattern,
                            md.getMetaDataPK().getCodeChr(), md.getValueChr());
                    resultMap.put(md.getMetaDataPK().getCodeChr(), result);

                }
            }

            for (String codeChr : resultMap.keySet()) {
                if (resultList == null) {
                    resultList = new ArrayList<String>();
                }
                resultList.add(resultMap.get(codeChr));
            }

            return resultList;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().log(getClass(),
                    getCellphoneNumber().toString(), Action.ERROR,
                    e.getMessage(), e);
        }
        return null;

    }

    private List<MetaData> findMetaList(Long metaValue, String where) throws InvalidOperationException {
        return getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndValue(
                getClient().getClientCod(), metaValue, where);
    }

    private String getReturnMessageMetaData(MetaData metaData) {
        String valor = null;
        String metaMemberName;
        if (metaData != null && metaData.getValueChr() != null) {
            metaMemberName = metaData.getMetaMember().getDescriptionChr().concat(
                    ": ");
            valor = MessageFormat.format("\n{0}{1}", metaMemberName,
                    metaData.getValueChr());
        }
        return valor != null ? valor : "";
    }

    public String getMetaByCodeSearchMessage(String code, ServiceEvent event, MetaNames meta) {
        String returnMessage = NO_INTEGRATION_ENABLED_MSG;
        try {
            if (hasMetaIntegrationEnabled(getClient().getClientCod(), meta)) {
                returnMessage = getReturnMessageByMetaDataCode(meta, code,
                        event);
            } else if (hasWSIntegrationEnabled()) {
                String value = null;

                switch (meta) {
                case CLIENT:
                    Customer customer = getCustomerByCode(code);
                    if (customer != null) {
                        value = customer.getName();
                    }
                    break;

                case PRODUCT:
                    Product product = getProductByCode(code);
                    if (product != null) {
                        value = product.getName();
                    }
                    break;
                case GUARD:
                    Guard guard = getGuardByCode(code);
                    if (guard != null) {
                        value = guard.getName();
                    }
                    break;

                case MOTIVE:
                    Motive motive = getMotiveByCode(code);
                    if (motive != null) {
                        value = motive.getName();
                    }
                    break;
                }
                if (value != null) {
                    String pattern = getFacadeContainer().getI18nAPI().iValue(
                            event.getSuccessMessage());
                    return MessageFormat.format(
                            pattern,
                            getFacadeContainer().getI18nAPI().iValue(
                                    meta.description()), code, value);
                } else {
                    String pattern = getFacadeContainer().getI18nAPI().iValue(
                            event.getNoMatchMessage());
                    return MessageFormat.format(pattern, code,
                            meta.description());
                }

            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().log(getClass(),
                    getCellphoneNumber().toString(), Action.ERROR,
                    e.getMessage(), e);
            return NO_INTEGRATION_ENABLED_MSG;
        }
        return returnMessage;
    }

    public List<MetaData> findMetaAll(MetaNames meta, Long metaMember) throws InvalidOperationException {
        return getFacadeContainer().getMetaDataAPI().findByClientMetaMember(
                getClient().getClientCod(), meta.value(), metaMember);
    }

    public String getMetaByValueSearchMessage(String value, ServiceEvent event, MetaNames meta, int limit) {
        try {
            if (hasMetaIntegrationEnabled(getClient().getClientCod(), meta)) {
                returnMessage = getReturnMessageByMetaDataValue(meta, value,
                        event, limit);
            } else if (hasWSIntegrationEnabled()) {
                returnMessage = MessageFormat.format(
                        event.getNoMatchMessage(),
                        value,
                        getFacadeContainer().getI18nAPI().iValue(
                                meta.description()));

                Integer size = null;
                Integer maxResult = null;
                List<String> valueList = null;
                switch (meta) {
                case CLIENT:
                    size = getCustomerByNameCount(value);
                    maxResult = (size <= limit) ? size : limit;

                    List<Customer> customerList = getCustomerByName(value,
                            maxResult);
                    if (customerList != null && customerList.size() > 0) {
                        valueList = new ArrayList<String>();
                        for (Customer customer : customerList) {
                            valueList.add(MessageFormat.format("{0}: {1}\n",
                                    customer.getCode(), customer.getName()));
                        }
                    }
                    break;

                case PRODUCT:
                    size = getProductByNameCount(value);
                    maxResult = (size <= limit) ? size : limit;

                    List<Product> productList = getProductByName(value,
                            maxResult);
                    if (productList != null && productList.size() > 0) {
                        valueList = new ArrayList<String>();
                        for (Product product : productList) {
                            valueList.add(MessageFormat.format("{0}: {1}\n",
                                    product.getCode(), product.getName()));
                        }
                    }
                    break;

                case MOTIVE:
                    size = getMotiveByNameCount(value);
                    maxResult = (size <= limit) ? size : limit;

                    List<Motive> motiveList = getMotiveByName(value, maxResult);
                    if (motiveList != null && motiveList.size() > 0) {
                        valueList = new ArrayList<String>();
                        for (Motive motive : motiveList) {
                            valueList.add(MessageFormat.format("{0}: {1}\n",
                                    motive.getCode(), motive.getName()));
                        }
                    }
                    break;
                case GUARD:
                    size = getGuardByNameCount(value);
                    maxResult = (size <= limit) ? size : limit;

                    List<Guard> guardList = getGuardByName(value, maxResult);
                    if (guardList != null && guardList.size() > 0) {
                        valueList = new ArrayList<String>();
                        for (Guard guard : guardList) {
                            valueList.add(MessageFormat.format("{0}: {1}\n",
                                    guard.getCode(), guard.getName()));
                        }
                    }
                    break;
                }

                if (valueList != null && valueList.size() > 0) {
                    String returnValue = "";
                    for (int i = 0; i < maxResult; i++) {
                        String valueItem = valueList.get(i);
                        returnValue += String.valueOf(i + 1).concat(")").concat(
                                valueItem);
                    }
                    returnMessage = MessageFormat.format(
                            event.getSuccessMessage(), meta.description(),
                            value, maxResult, size, returnValue);
                }
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().log(getClass(),
                    getCellphoneNumber().toString(), Action.ERROR,
                    e.getMessage(), e);
            return NO_INTEGRATION_ENABLED_MSG;
        }
        return returnMessage;
    }

    protected String getMetaForIntegrationValue(String codeMeta, MetaNames meta) throws InvalidOperationException {

        String code = codeMeta;
        if (hasMetaIntegrationEnabled(getClient().getClientCod(), meta)) {
            String name = getMetaDataForIntegrationByCode(meta, code);
            code = (name != null) ? code.concat(", ").concat(name) : code;

        } else if (hasWSIntegrationEnabled()) {
            switch (meta) {
            case CLIENT:
                Customer customer = null;
                try {
                    customer = getCustomerByCode(code);
                } catch (MalformedURLException e) {
                }
                code = (customer != null) ? code.concat(", ").concat(
                        customer.getName()) : code;
                break;
            case PRODUCT:
                Product product = null;
                try {
                    product = getProductByCode(code);
                } catch (MalformedURLException e) {
                }
                code = (product != null) ? code.concat(", ").concat(
                        product.getName()) : code;
                break;
            case MOTIVE:
                Motive motive = null;
                try {
                    motive = getMotiveByCode(code);
                } catch (MalformedURLException e) {
                }
                code = (motive != null) ? code.concat(", ").concat(
                        motive.getName()) : code;
                break;
            case GUARD:
                Guard guard = null;
                try {
                    guard = getGuardByCode(code);
                } catch (MalformedURLException e) {
                }
                code = (guard != null) ? code.concat(", ").concat(
                        guard.getName()) : code;
                break;

            }

        }
        return code;
    }

    public Long getFunctionality() {
        return getEntity().getAndroid() ? 5L : 0L;
    }

    @Override
    protected void validate() throws InvalidOperationException {
        super.validate();
        List<Service> services = null;
        List<Meta> metas = null;
        if (getUserphone() != null) {
            Long functionality = getFunctionality();

            services = getFacadeContainer().getServiceAPI().getServiceByUserphoneAndFuncionality(
                    getUserphone().getUserphoneCod(), functionality);
            metas = getFacadeContainer().getMetaAPI().findMetaByClientAndEnabled(
                    getUserphone().getClient().getClientCod(), true);
        }
        if (!getValidation() && getMeta() != null) {
            if (!metas.contains(getMeta())) {
                String msj = getFacadeContainer().getI18nAPI().iValue(
                        Restriction.CLIENT_WITH_META_NOT_ALLOWED.getMessage());
                msj = MessageFormat.format(msj, getMeta().getDescriptionChr());
                throw new OperationNotAllowedException(msj);
            }
        } else if (!getValidation() && getService() != null) {
            if (!services.contains(getService())) {
                throw new OperationNotAllowedException(getFacadeContainer().getI18nAPI().iValue(
                        Restriction.PHONE_WITH_SERVICE_NOT_REGISTERED.getMessage()));
            }
        } else if (!getValidation()) {
            throw new OperationNotAllowedException(getFacadeContainer().getI18nAPI().iValue(
                    Restriction.SERVICE_DOES_NOT_EXIST.getMessage()));
        }
        validateMetaAssociation(getEntity().getClass(), getEntity());

    }

    public boolean addRouteDetail(Userphone userphone, Cell cell) {
        try {
            return getFacadeContainer().getRouteDetailUserphoneAPI().addRouteDetailUserphone(
                    userphone, cell);
        } catch (InvalidOperationException ex) {
            getFacadeContainer().getNotifier().signal(
                    AbstractServiceAPI.class,
                    Action.ERROR,
                    getFacadeContainer().getI18nAPI().iValue(
                            "error.ErrorAddingDetailsOfRoadmap"), ex);
            return false;
        }
    }

    private void validateMetaAssociation(Class<? extends ServiceBean> clazz, ServiceBean instance) throws InvalidOperationException {
        try {
            Field[] fieds = clazz.getDeclaredFields();
            for (Field field : fieds) {
                MetaColumn annotation = field.getAnnotation(MetaColumn.class);
                if (annotation != null) {
                    Long metaDataCode = annotation.metaname().value();

                    /*
                     * validamos que el cliente tenga integracion de metadatas
                     */

                    MetaClient mc = getFacadeContainer().getMetaClientAPI().findByMetaAndClient(
                            getUserphone().getClient().getClientCod(),
                            metaDataCode);
                    if (mc != null && mc.getEnabledChr()) {

                        /*
                         * una vez que obtenemos el codigo del metadato,
                         * verificamos si este tiene una restriccion
                         */

                        String memberMethod = "get".concat(
                                field.getName().substring(0, 1).toUpperCase()).concat(
                                field.getName().substring(1));
                        Method method = instance.getClass().getMethod(
                                memberMethod, (Class<?>[]) null);

                        String codeChr = (String) method.invoke(instance,
                                (Object[]) null);

                        MetaData md = null;
                        if (codeChr != null) {
                            md = getFacadeContainer().getMetaDataAPI().findByClientMetaMemberAndCode(
                                    getUserphone().getClient().getClientCod(),
                                    metaDataCode, 1L, codeChr);
                        }

                        if (codeChr != null
                            && md == null
                            && !getUserphone().getClient().getRegNonexistentMeta()) {
                            String msg = getFacadeContainer().getI18nAPI().iValue(
                                    "restriction.metadata.CanNotRegisterNonexistentMetaData");
                            msg = MessageFormat.format(msg,
                                    mc.getMeta().getDescriptionChr(), codeChr);
                            throw new ValidationException(msg);
                        } else {
                            if (getFacadeContainer().getMetaDataAPI().findMetaAssociation(
                                    getUserphone().getClient().getClientCod(),
                                    metaDataCode, 1L, codeChr, null)) {
                                if (!getFacadeContainer().getMetaDataAPI().findMetaAssociation(
                                        getUserphone().getClient().getClientCod(),
                                        metaDataCode, 1L, codeChr,
                                        getUserphone().getUserphoneCod())) {

                                    String mdMessage = codeChr;
                                    if (md != null) {
                                        mdMessage = ("(").concat(codeChr).concat(
                                                ",").concat(
                                                md.getValueChr().concat(")"));
                                    }

                                    String msgPattern = getFacadeContainer().getI18nAPI().iValue(
                                            "restriction.metadata.association");
                                    throw new InvalidOperationException(MessageFormat.format(
                                            msgPattern,
                                            getFacadeContainer().getI18nAPI().iValue(
                                                    annotation.metaname().description()),
                                            mdMessage));
                                }
                            }
                        }
                    }
                }
                if (field.getType().equals(List.class)) {
                    String memberMethod = "get".concat(
                            field.getName().substring(0, 1).toUpperCase()).concat(
                            field.getName().substring(1));
                    Method method = instance.getClass().getMethod(memberMethod,
                            (Class<?>[]) null);
                    List<ServiceBean> list = ((List<ServiceBean>) method.invoke(
                            instance, (Object[]) null));
                    if (list != null && list.size() > 0) {
                        for (ServiceBean representation : list) {
                            validateMetaAssociation(representation.getClass(),
                                    representation);
                        }
                    }
                }

                if (field.getType().equals(ServiceBean.class)) {
                    String memberMethod = "get".concat(
                            field.getName().substring(0, 1).toUpperCase()).concat(
                            field.getName().substring(1));
                    Method method = instance.getClass().getMethod(memberMethod,
                            (Class<?>[]) null);

                    ServiceBean att = ((ServiceBean) method.invoke(instance,
                            (Object[]) null));
                    validateMetaAssociation(att.getClass(), att);
                }
            }
        } catch (InvalidOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidOperationException(e.getMessage());
        }
    }

    @Override
    public Customer getCustomerByCode(String code) throws MalformedURLException {
        try {
            return getWSPort().getCustomerByCode(code);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Customer> getCustomerByName(String name, int size) throws MalformedURLException {
        try {
            return getWSPort().getCustomerByName(name, size);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getCustomerByNameCount(String name) throws MalformedURLException {
        try {
            return getWSPort().getCustomerByNameCount(name);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Product getProductByCode(String code) throws MalformedURLException {

        try {
            return getWSPort().getProductByCode(code);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Product> getProductByName(String name, int size) throws MalformedURLException {
        try {
            return getWSPort().getProductByName(name, size);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getProductByNameCount(String name) throws MalformedURLException {
        try {
            return getWSPort().getProductByNameCount(name);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Guard getGuardByCode(String code) throws MalformedURLException {
        try {
            return getWSPort().getGuardByCode(code);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Guard> getGuardByName(String name, int size) throws MalformedURLException {
        try {
            return getWSPort().getGuardByName(name, size);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getGuardByNameCount(String name) throws MalformedURLException {
        try {
            return getWSPort().getGuardByNameCount(name);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Motive getMotiveByCode(String code) throws MalformedURLException {
        try {
            return getWSPort().getMotiveByCode(code);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Motive> getMotiveByName(String name, int size) throws MalformedURLException {
        try {
            return getWSPort().getMotiveByName(name, size);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getMotiveByNameCount(String name) throws MalformedURLException {
        try {
            return getWSPort().getMotiveByNameCount(name);
        } catch (Exception e) {
            return 0;
        }
    }

    protected String getUserCode() throws InvalidOperationException {

        String splitter = null;
        try {
            splitter = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "feature.ussd.usercode.splitter");
        } catch (Exception e) {
            splitter = "%*";
        }

        /*
         * construimos el userCode
         */
        String userCode = "";
        if (getUserphone() != null && getClient().getEnabledChr()
            && getUserphone().getEnabledChr()) {
            userCode = userCode.concat(
                    getUserphone().getUserphoneCod().toString()).concat(" ");
        }

        if (getClient().getEnabledChr()) {
            userCode = userCode.concat(
                    getClient().getUssdProfileId().toString()).concat(" ");
        }

        userCode = userCode.trim().replace(" ", splitter);
        getFacadeContainer().getNotifier().log(getClass(),
                getCellphoneNumber().toString(), Action.INFO,
                " USERCODE: ".concat(userCode));

        return userCode;

    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
