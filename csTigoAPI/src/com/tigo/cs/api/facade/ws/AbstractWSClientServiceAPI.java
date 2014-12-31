package com.tigo.cs.api.facade.ws;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.namespace.QName;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.facade.AbstractAPI;

public abstract class AbstractWSClientServiceAPI<T> extends AbstractAPI<T> implements Serializable {

    private static final long serialVersionUID = -3523061131966666876L;

    private T wsClient;

    protected abstract int retryCount();

    protected abstract URL getWSDL();

    protected String getStringWSDL() {
        return getWSDL() != null ? getWSDL().getPath() : "null";
    }

    protected abstract String getPlatformName();

    protected abstract int getTimeout();

    protected abstract QName getQname();

    protected abstract T createClient();

    public T getClient() throws InvalidOperationException {
        if (wsClient == null) {
            postConstruct();
        }

        String message = "{0} - Se obtuvo instancia del cliente WS ";
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        return wsClient;
    }

    public AbstractWSClientServiceAPI(Class<T> clazz) {
        super(clazz);
    }

    protected void postConstruct() throws InvalidOperationException {
        try {

//            getFacadeContainer().getNotifier().debug(
//                    getClass(),
//                    null,
//                    MessageFormat.format(
//                            "{0} - WSDL para la conexion al WS de {0}:{1}. TIMEOUT: {2}",
//                            getPlatformName(), getWSDL(), getTimeout()));

            /*
             * verificar si hace ping al servidor antes de crear el cliente WS
             */
            if (!checkIfURLExists(getStringWSDL(), getTimeout())) {
                getFacadeContainer().getNotifier().warn(
                        LbsGwServiceAPI.class,
                        null,
                        MessageFormat.format("{0} - {0} WSDL Unreachable:{0}",
                                getPlatformName(), getWSDL()));

                throw new InvalidOperationException(MessageFormat.format(
                        "{0} te informa que el servicio se encuentra en mantenimiento. Favor, vuelva a intentar en unos instantes.",
                        getPlatformName()));
            }

            if (wsClient == null) {
            	wsClient = createClient();
            }

        } catch (InvalidOperationException ex) {
            throw ex;
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().error(
                    AbstractWSClientServiceAPI.class,
                    null,
                    MessageFormat.format(
                            "{0} - No se pudo conectar al {0}Service",
                            getPlatformName()), ex);
        }

    }

    protected boolean checkIfURLExists(String targetUrl, int timeout) {
        if (targetUrl == null) {

            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    MessageFormat.format("{0} - Error verifying URL. Is null.",
                            getPlatformName()), null);
            return false;
        }
        HttpURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpURLConnection) new URL(targetUrl).openConnection();
            httpUrlConn.setRequestMethod("GET");

            httpUrlConn.setConnectTimeout(timeout);
            httpUrlConn.setReadTimeout(timeout);

            Integer responseCode = httpUrlConn.getResponseCode();
            String responseMessage = httpUrlConn.getResponseMessage();

//            getFacadeContainer().getNotifier().debug(
//                    getClass(),
//                    null,
//                    MessageFormat.format(
//                            "{0} - Verifying WSDL URL: Response code: {1}",
//                            getPlatformName(), responseCode));
//
//            getFacadeContainer().getNotifier().debug(
//                    getClass(),
//                    null,
//                    MessageFormat.format(
//                            "{0} - Verifying WSDL URL: Response message: {1}",
//                            getPlatformName(), responseMessage));

            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    MessageFormat.format("{0} - Error verifying URL",
                            getPlatformName()), e);
            return false;
        }
    }

}
