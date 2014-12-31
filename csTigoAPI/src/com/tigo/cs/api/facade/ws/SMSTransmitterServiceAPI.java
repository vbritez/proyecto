package com.tigo.cs.api.facade.ws;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import javax.xml.namespace.QName;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.ejb.SMSTransmitterException;
import com.tigo.cs.domain.Application;
import com.tigo.sms.ws.SMSTransmitter;

public abstract class SMSTransmitterServiceAPI extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = -2221175954191416280L;

    protected static SMSTransmitter smsTransmitter;

    public SMSTransmitterServiceAPI() {
        super(String.class);
    }

    public void postConstruct() {
        try {
            String wsdlPattern = "http://{0}:{1}/smsservices?wsdl";
            String host;

            Integer timeout = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.timeout"));

            host = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "sms.ws.host");

            String port = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "sms.ws.port");
            String wsdl = MessageFormat.format(wsdlPattern, host, port);

            QName serviceName = new QName("http://ws.sms.tigo.com/", "SMSTransmitterService");

            /*
             * verificar si hace ping al servidor antes de crear el cliente WS
             */
            if (!checkIfURLExists(wsdl, timeout)) {
                getFacadeContainer().getNotifier().warn(
                        getClass(),
                        null,
                        MessageFormat.format("SMS - SMS WSDL Unreachable:{0}",
                                wsdl));

                throw new InvalidOperationException("El WS del SMS Manager no esta disponible.");
            }

            URL url = new URL(wsdl);

            if (smsTransmitter == null) {
                smsTransmitter = javax.xml.ws.Service.create(url, serviceName).getPort(
                        SMSTransmitter.class);
            }
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().warn(getClass(), null,
                    "No se pudo conectar al SMSTransmitterService");
        }
    }

    public boolean checkIfURLExists(String targetUrl, int timeout) {
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) new URL(targetUrl).openConnection();
            httpUrlConn.setRequestMethod("GET");

            httpUrlConn.setConnectTimeout(timeout);
            httpUrlConn.setReadTimeout(timeout);

            Integer responseCode = httpUrlConn.getResponseCode();
            String responseMessage = httpUrlConn.getResponseMessage();

            getFacadeContainer().getNotifier().debug(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "SMS - Verifying WSDL URL: Response code: {0}",
                            responseCode));

            getFacadeContainer().getNotifier().debug(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "SMS - Verifying WSDL URL: Response message: {0}",
                            responseMessage));

            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().warn(getClass(), null,
                    "Error verifying URL:" + e.getMessage());
            smsTransmitter = null;
            return false;
        }
    }

    public SMSTransmitter getSmsTransmitter() {
        if (smsTransmitter == null) {
            postConstruct();
        }
        return smsTransmitter;
    }

    public List<String> sendBroadcastMessage(Application application, List<String> cellphoneNumList, String smsContent) throws SMSTransmitterException {
        if (getSmsTransmitter() != null && application != null) {
            try {

                getFacadeContainer().getNotifier().info(this.getClass(),
                        cellphoneNumList.toString(),
                        "Mensaje a enviar:" + smsContent);

                return getSmsTransmitter().sendBroadcastMessage(
                        application.getKey(), cellphoneNumList, smsContent);
            } catch (Exception e) {
                getFacadeContainer().getNotifier().warn(this.getClass(), null,
                        "Problemas conectando al SMS Manager.");
                smsTransmitter = null;
                return null;
            }
        } else {
            getFacadeContainer().getNotifier().warn(this.getClass(), null,
                    "No se pudo conectar al SMSTransmitterService");
            throw new SMSTransmitterException("Debe asisgnar previamente la aplicacion del transmitter");
        }

    }

    public boolean sendMessage(Application application, String cellphoneNum, String smsContent) {

        if (getSmsTransmitter() != null && application != null) {
            try {

                getFacadeContainer().getNotifier().info(this.getClass(),
                        cellphoneNum, "Mensaje a enviar:" + smsContent);

                return getSmsTransmitter().sendMessage(application.getKey(),
                        cellphoneNum, smsContent);
            } catch (Exception e) {
                getFacadeContainer().getNotifier().warn(
                        this.getClass(),
                        cellphoneNum,
                        "Problemas conectando al SMS Manager.Se regenerara el cliente en la proxima invocacion.");
                smsTransmitter = null;
                return false;
            }
        } else {
            getFacadeContainer().getNotifier().warn(this.getClass(),
                    cellphoneNum,
                    "No se pudo conectar al SMSTransmitterService");
            return false;
        }

    }

}
