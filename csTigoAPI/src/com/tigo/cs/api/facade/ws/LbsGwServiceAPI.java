package com.tigo.cs.api.facade.ws;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.ws.BindingProvider;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.lbs.ws.LbsGwService;
import com.tigo.lbs.ws.LbsGwServiceService;
import com.tigo.lbs.ws.LocationResponse;

public abstract class LbsGwServiceAPI extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = 7562592085897842427L;
    protected static LbsGwService lbsGwService;

    public LbsGwServiceAPI() {
        super(String.class);
    }

    public void postConstruct() {
        try {

            Integer timeout = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.timeout"));

            String wsdlPattern = "http://{0}:{1}/{2}?wsdl";
            String host = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tracking.lbs.ws.host");
            String port = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tracking.lbs.ws.port");
            String path = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "tracking.lbs.ws.path");
            String wsdl = MessageFormat.format(wsdlPattern, host, port, path);

            /*
             * verificar si hace ping al servidor antes de crear el cliente WS
             */
            if (!checkIfURLExists(wsdl, timeout)) {
                getFacadeContainer().getNotifier().warn(
                        getClass(),
                        null,
                        MessageFormat.format("LBS - LBS WSDL Unreachable:{0}",
                                wsdl));

                throw new InvalidOperationException("El Ws del Lbs Service no esta disponible.");
            }

            URL url = new URL(wsdl);

            if (lbsGwService == null) {
                lbsGwService = new LbsGwServiceService(url).getLbsGwServicePort();
                ((BindingProvider) lbsGwService).getRequestContext().put(
                        "sun.net.client.defaultConnectTimeout",
                        new Integer(100));
            }
        } catch (GenericFacadeException ex) {
            getFacadeContainer().getNotifier().warn(LbsGwServiceAPI.class,
                    null, "No se pudo conectar al LbsGwService");
            lbsGwService = null;
        } catch (MalformedURLException ex) {
            getFacadeContainer().getNotifier().warn(LbsGwServiceAPI.class,
                    null, "No se pudo conectar al LbsGwService");
            lbsGwService = null;
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().warn(LbsGwServiceAPI.class,
                    null, "No se pudo conectar al LbsGwService");
            lbsGwService = null;
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
                            "LBS - Verifying WSDL URL: Response code: {0}",
                            responseCode));

            getFacadeContainer().getNotifier().debug(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "LBS - Verifying WSDL URL: Response message: {0}",
                            responseMessage));

            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().warn(getClass(), null,
                    "Error verifying URL:" + e.getMessage());
            lbsGwService = null;
            return false;
        }
    }

    public LbsGwService getLbsGwService() {
        if (lbsGwService == null) {
            postConstruct();
        }
        return lbsGwService;
    }

    public LocationResponse locate(String msisdn) {
        if (getLbsGwService() != null) {
            try {
                return getLbsGwService().locate(msisdn);
            } catch (Exception e) {
                getFacadeContainer().getNotifier().warn(
                        this.getClass(),
                        msisdn,
                        "Problemas conectando al SMS Manager. Se regenerara el cliente en la proxima invocacion.");
                lbsGwService = null;
            }
        }
        return null;
    }
}
