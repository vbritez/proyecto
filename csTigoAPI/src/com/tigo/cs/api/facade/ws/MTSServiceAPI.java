package com.tigo.cs.api.facade.ws;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import us.inswitch.mts.ws.server.MTS;
import us.inswitch.mts.ws.server.MTSServiceLocator;

import com.tigo.cs.commons.jpa.GenericFacadeException;

public class MTSServiceAPI extends AbstractWSClientServiceAPI<MTS> implements Serializable {

    private static final long serialVersionUID = -6175337473294621406L;
    protected MTS mtsService = null;

    public MTSServiceAPI() {
        super(MTS.class);
    }

    @Override
    protected int retryCount() {
        return 1;
    }

    @Override
    protected URL getWSDL() {
        try {
            return new URL(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "mts.ws.wsdl"));
        } catch (GenericFacadeException e) {
        } catch (MalformedURLException e) {
        }
        return null;
    }

    @Override
    protected String getStringWSDL() {
        try {
            return getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "mts.ws.wsdl");
        } catch (GenericFacadeException e) {
            return null;
        }
    }

    @Override
    protected String getPlatformName() {
        return "MTS";
    }

    @Override
    protected int getTimeout() {
        try {
            return Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "mts.ws.timeout"));
        } catch (GenericFacadeException e) {
        }
        return 100;
    }

    @Override
    protected QName getQname() {
        return new QName("http://server.ws.mts.inswitch.us/", "MTSService");
    }

    @Override
    public MTS createClient() {
        MTSServiceLocator locator = new MTSServiceLocator();
        try {
            return locator.getMTSPort(getWSDL());
        } catch (ServiceException e) {
            return null;
        }

        // return new MTSService(getWSDL(), getQname()).getMTSPort();
    }

    @Override
    protected boolean checkIfURLExists(String targetUrl, int timeout) {
        if (targetUrl == null) {

            getFacadeContainer().getNotifier().error(
                    getClass(),
                    null,
                    MessageFormat.format("{0} - Error verifying URL. Is null.",
                            getPlatformName()), null);
            return false;
        }
        HttpsURLConnection httpUrlConn;
        try {
            httpUrlConn = (HttpsURLConnection) new URL(targetUrl).openConnection();
            httpUrlConn.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            httpUrlConn.setRequestMethod("GET");

            httpUrlConn.setConnectTimeout(timeout);
            httpUrlConn.setReadTimeout(timeout);

            Integer responseCode = httpUrlConn.getResponseCode();
            String responseMessage = httpUrlConn.getResponseMessage();

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "{0} - Verifying WSDL URL: Response code: {1}",
                            getPlatformName(), responseCode));

            getFacadeContainer().getNotifier().debug(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "{0} - Verifying WSDL URL: Response message: {1}",
                            getPlatformName(), responseMessage));

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
