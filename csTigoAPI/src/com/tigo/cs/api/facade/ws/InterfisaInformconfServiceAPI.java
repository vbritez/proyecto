package com.tigo.cs.api.facade.ws;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

import javax.xml.namespace.QName;

import com.interfisa.ws.WsTigo;
import com.interfisa.ws.WsTigo_Service;
import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;

public class InterfisaInformconfServiceAPI extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = -3523061131966666876L;
    protected WsTigo interfisaService = null;

    int cantReintentos = 1;

    public InterfisaInformconfServiceAPI() {
        super(String.class);
    }

    public void postConstruct() throws InvalidOperationException {
        try {

            getFacadeContainer().getNotifier().info(getClass(), null,
                    "INTERFISA - Se creara el WS para consultas a INTERFISA");
            interfisaService = null;

            String wsdl = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "interfisa.ws.wsdl");

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "INTERFISA - WSDL para la conexion al WS de INTERFISA:{0}",
                            wsdl));

            Integer timeout = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "interfisa.ws.timeout"));

            /*
             * verificar si hace ping al servidor antes de crear el cliente WS
             */
            if (!checkIfURLExists(wsdl, timeout)) {
                getFacadeContainer().getNotifier().warn(
                        LbsGwServiceAPI.class,
                        null,
                        MessageFormat.format(
                                "INTERFISA - INTERFISA WSDL Unreachable:{0}",
                                wsdl));

                throw new InvalidOperationException("INTERFISA te informa que el servicio se encuentra en mantenimiento. Favor, vuelva a intentar en unos instantes o contacte con el Call Center linea baja 0800115000");
            }

            if (interfisaService == null) {
                try {
                    QName qName = new QName("http://ws.interfisa.com/", "wsTigo");

                    interfisaService = new WsTigo_Service(new URL(wsdl), qName).getWsTigoPort();
                    getFacadeContainer().getNotifier().info(
                            getClass(),
                            null,
                            MessageFormat.format(
                                    "INTERFISA - WS Client INTERFISA creado exitosamente:{0}",
                                    wsdl));
                } catch (Exception e) {
                    getFacadeContainer().getNotifier().signal(
                            LbsGwServiceAPI.class, Action.ERROR,
                            "No se pudo conectar al LbsGwService", e);
                }

            }

        } catch (InvalidOperationException ex) {
            throw ex;
        } catch (GenericFacadeException ex) {
            getFacadeContainer().getNotifier().error(
                    InterfisaInformconfServiceAPI.class, null,
                    "INTERFISA - No se pudo conectar al INTERFISAService", ex);
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().error(
                    InterfisaInformconfServiceAPI.class, null,
                    "INTERFISA - No se pudo conectar al INTERFISAService", ex);
        }

    }

    public boolean checkIfURLExists(String targetUrl, int timeout) {
        if (targetUrl == null) {

            getFacadeContainer().getNotifier().error(getClass(), null,
                    "Error verifying URL. Is null.", null);
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

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "INTERFISA - Verifying WSDL URL: Response code: {0}",
                            responseCode));

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "INTERFISA - Verifying WSDL URL: Response message: {0}",
                            responseMessage));

            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    "Error verifying URL", e);
            return false;
        }
    }

    public WsTigo getInterfisaService() throws InvalidOperationException {
        if (interfisaService == null) {
            postConstruct();
        }

        String message = "INTERFISA - Se obtuvo instancia del cliente WS ";
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        return interfisaService;
    }

    public String sendQuery(String document, String msisdn) throws InvalidOperationException {

        String message = MessageFormat.format(
                "INTERFISA - Se envia consulta a interfisa."
                    + "|operation:solicitudInformconf | Desde|msisdn: {0}. Documento: {1} ",
                msisdn, document);

        getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

        try {
            if (getInterfisaService() != null) {

                Date antes = new Date();
                String retornoInterfisaInformconf = getInterfisaService().solicitudInformconf(
                        document, msisdn);
                retornoInterfisaInformconf = retornoInterfisaInformconf.trim();
                Date despues = new Date();

                message = MessageFormat.format(
                        "INTERFISA - TIME - Se invoco al WS de INTERFISA. "
                            + "|operation:solicitudInformconf |param-document:{0}>>Response: {1}. >>Tiempo en milisegundos: {2} ",
                        document, retornoInterfisaInformconf, despues.getTime()
                            - antes.getTime());

                getFacadeContainer().getNotifier().info(getClass(), msisdn,
                        message);

                return retornoInterfisaInformconf;

            }
        } catch (InvalidOperationException e) {
            throw e;
        } catch (Exception e) {
            if (e.getCause() instanceof SocketTimeoutException) {

                message = MessageFormat.format(
                        "INTERFISA - El tiempo de espera ha sido superado, vuelva a intentarlo. "
                            + "|operation:solicitudInformconf |param-document:{0}>>Desde|msisdn: {1} ",
                        document, msisdn);

                getFacadeContainer().getNotifier().warn(getClass(), msisdn,
                        message);

                throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
            }
            interfisaService = null;
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    msisdn,
                    MessageFormat.format(
                            "INTERFISA - Se recreara instancia del cliente WS por problemas. Documento: {0}.",
                            document), e);
            throw new InvalidOperationException("El servicio no pudo validar los datos ingresados.");

        }
        return null;

    }
}
