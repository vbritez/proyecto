package com.tigo.cs.api.facade.ws;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.axis.client.Stub;

import py.gov.ips.consultas.servicios.Menu;
import py.gov.ips.consultas.servicios.RetornoAgendamientoMedico;
import py.gov.ips.consultas.servicios.RetornoCancelacionesMedicas;
import py.gov.ips.consultas.servicios.ServiciosConsultasSIH;
import py.gov.ips.consultas.servicios.ServiciosConsultasSIHServiceLocator;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.facade.AbstractAPI;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Auditory;

public class IpsServiceAPI extends AbstractAPI<String> implements Serializable {

    private static final long serialVersionUID = -3523061131966666876L;
    protected ServiciosConsultasSIH serviciosConsultasSIH = null;

    public static final Map<String, Long> lastToken = new ConcurrentHashMap<String, Long>(50000, 0.75f, 8);
    public static final Map<String, Menu> lastSpecialty = new ConcurrentHashMap<String, Menu>(50000, 0.75f, 8);
    public static final Map<String, Menu> lastSpecialtyType = new ConcurrentHashMap<String, Menu>(50000, 0.75f, 8);
    public static final Map<String, Menu> lastZone = new ConcurrentHashMap<String, Menu>(50000, 0.75f, 8);
    public static final Map<String, Menu> lastMedicCenter = new ConcurrentHashMap<String, Menu>(50000, 0.75f, 8);
    public static final Map<String, Menu> lastHorarioMedicCenter = new ConcurrentHashMap<String, Menu>(50000, 0.75f, 8);
    public static final Map<String, RetornoCancelacionesMedicas> lastCancelCita = new ConcurrentHashMap<String, RetornoCancelacionesMedicas>(50000, 0.75f, 8);

    public static final Map<String, RetornoAgendamientoMedico> lastRetornoSpecialty = new ConcurrentHashMap<String, RetornoAgendamientoMedico>(50000, 0.75f, 8);
    public static final Map<String, RetornoAgendamientoMedico> lastRetornoZone = new ConcurrentHashMap<String, RetornoAgendamientoMedico>(50000, 0.75f, 8);
    public static final Map<String, RetornoAgendamientoMedico> lastRetornoMedicCenter = new ConcurrentHashMap<String, RetornoAgendamientoMedico>(50000, 0.75f, 8);
    public static final Map<String, RetornoAgendamientoMedico> lastRetornoHorarioMedicCenter = new ConcurrentHashMap<String, RetornoAgendamientoMedico>(50000, 0.75f, 8);
    public static final Map<String, RetornoAgendamientoMedico> lastRetornoMedic = new ConcurrentHashMap<String, RetornoAgendamientoMedico>(50000, 0.75f, 8);

    public static final Map<String, RetornoCancelacionesMedicas[]> lastRetornoCancelCitaMedic = new ConcurrentHashMap<String, RetornoCancelacionesMedicas[]>(50000, 0.75f, 8);

    int cantReintentos = 1;

    public IpsServiceAPI() {
        super(String.class);
    }

    public void postConstruct() throws InvalidOperationException {
        try {

            getFacadeContainer().getNotifier().info(getClass(), null,
                    "IPS - Se creara el WS para consultas a IPS");
            serviciosConsultasSIH = null;
            String wsdlPattern = "http://{0}:{1}/{2}?wsdl";

            Integer timeout = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.timeout"));
            String host = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.hostname");
            String port = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.port");
            String path = getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.path");
            String wsdl = MessageFormat.format(wsdlPattern, host, port, path);
            cantReintentos = Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.maxretry"));

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "IPS - WSDL para la conexion al WS de IPS:{0}",
                            wsdl));

            /*
             * verificar si hace ping al servidor antes de crear el cliente WS
             */
            if (!checkIfURLExists(wsdl, timeout)) {
                getFacadeContainer().getNotifier().warn(
                        LbsGwServiceAPI.class,
                        null,
                        MessageFormat.format("IPS - IPS WSDL Unreachable:{0}",
                                wsdl));

                throw new InvalidOperationException("IPS te informa que el servicio se encuentra en mantenimiento. Favor, vuelva a intentar en unos instantes o contacte con el Call Center linea baja 0800115000");
            }

            URL url = new URL(wsdl);
            ServiciosConsultasSIHServiceLocator locator = new ServiciosConsultasSIHServiceLocator();
            locator.getEngine().setOption("sendMultiRefs", false);
            serviciosConsultasSIH = locator.getServiciosConsultasSIHPort(url);
            org.apache.axis.client.Stub s = (Stub) serviciosConsultasSIH;
            s.setTimeout(timeout);

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "IPS - WS Client IPS creado exitosamente:{0}", wsdl));

        } catch (InvalidOperationException ex) {
            throw ex;
        } catch (GenericFacadeException ex) {
            getFacadeContainer().getNotifier().error(IpsServiceAPI.class, null,
                    "IPS - No se pudo conectar al IpsService", ex);
        } catch (MalformedURLException ex) {
            getFacadeContainer().getNotifier().error(IpsServiceAPI.class, null,
                    "IPS - No se pudo conectar al IpsService", ex);
        } catch (Exception ex) {
            getFacadeContainer().getNotifier().error(IpsServiceAPI.class, null,
                    "IPS - No se pudo conectar al IpsService", ex);
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

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "IPS - Verifying WSDL URL: Response code: {0}",
                            responseCode));

            getFacadeContainer().getNotifier().info(
                    getClass(),
                    null,
                    MessageFormat.format(
                            "IPS - Verifying WSDL URL: Response message: {0}",
                            responseMessage));

            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    "Error verifying URL", e);
            return false;
        }
    }

    public ServiciosConsultasSIH getServiciosConsultasSIH() throws InvalidOperationException {
        if (serviciosConsultasSIH == null) {
            postConstruct();
        }

        String message = "IPS - Se obtuvo instancia del cliente WS ";
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        return serviciosConsultasSIH;
    }

    private String getWSEntidad() {
        try {
            return getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.entidad");
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(
                    IpsServiceAPI.class,
                    null,
                    "IPS - No se pudo obtener la entidad para conectarse al WS de IPS",
                    e);
            return null;
        }

    }

    private String getWSUser() {
        try {
            return getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.user");
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(
                    IpsServiceAPI.class,
                    null,
                    "IPS - No se pudo obtener el usuario para conectarse al WS de IPS",
                    e);
            return null;
        }
    }

    private String getWSPassword() {
        try {
            return getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "menumovil.ips.ws.pass");
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(
                    IpsServiceAPI.class,
                    null,
                    "IPS - No se pudo obtener el password para conectarse al WS de IPS",
                    e);
            return null;
        }
    }

    public Long getLastToken(String msisdn) {
        return lastToken.get(msisdn);
    }

    public Menu getLastSpecialty(String msisdn) {
        return lastSpecialty.get(msisdn);
    }

    public Menu getLastSpecialtyType(String msisdn) {
        return lastSpecialtyType.get(msisdn);
    }

    public Menu getLastZone(String msisdn) {
        return lastZone.get(msisdn);
    }

    public Menu getLastHorarioMedicCenter(String msisdn) {
        return lastHorarioMedicCenter.get(msisdn);
    }

    public Menu getLastMedicCenter(String msisdn) {
        return lastMedicCenter.get(msisdn);
    }

    public Map<String, Long> getLastToken() {
        return lastToken;
    }

    public Map<String, Menu> getLastSpecialty() {
        return lastSpecialty;
    }

    public Map<String, Menu> getLastSpecialtyType() {
        return lastSpecialtyType;
    }

    public Map<String, Menu> getLastZone() {
        return lastZone;
    }

    public Map<String, Menu> getLastHorarioMedicCenter() {
        return lastHorarioMedicCenter;
    }

    public Map<String, Menu> getLastMedicCenter() {
        return lastMedicCenter;
    }

    public Map<String, RetornoAgendamientoMedico> getLastRetornoSpecialty() {
        return lastRetornoSpecialty;
    }

    public Map<String, RetornoAgendamientoMedico> getLastRetornoZone() {
        return lastRetornoZone;
    }

    public Map<String, RetornoAgendamientoMedico> getLastRetornoMedicCenter() {
        return lastRetornoMedicCenter;
    }

    public Map<String, RetornoAgendamientoMedico> getLastRetornoHorarioMedicCenter() {
        return lastRetornoHorarioMedicCenter;
    }

    public Map<String, RetornoAgendamientoMedico> getLastRetornoMedic() {
        return lastRetornoMedic;
    }

    public Map<String, RetornoCancelacionesMedicas[]> getLastRetornoCancelCitaMedic() {
        return lastRetornoCancelCitaMedic;
    }

    public Map<String, RetornoCancelacionesMedicas> getLastCancelCita() {
        return lastCancelCita;
    }

    public RetornoCancelacionesMedicas getLastCancelCita(String msisdn) {
        return lastCancelCita.get(msisdn);
    }

    public void initForMsisdn(String msisdn) {

        String message = MessageFormat.format(
                "IPS - Se eliminaran los valores del hash para volver a recuperar del WS."
                    + "|operation:validarCi | Desde|msisdn: {0} ", msisdn);

        getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

        lastToken.remove(msisdn);
        lastSpecialty.remove(msisdn);
        lastSpecialtyType.remove(msisdn);
        lastZone.remove(msisdn);
        lastMedicCenter.remove(msisdn);
        lastCancelCita.remove(msisdn);
        lastHorarioMedicCenter.remove(msisdn);
        lastRetornoSpecialty.remove(msisdn);
        lastRetornoMedicCenter.remove(msisdn);
        lastRetornoHorarioMedicCenter.remove(msisdn);
        lastRetornoZone.remove(msisdn);
        lastRetornoMedic.remove(msisdn);
        lastRetornoCancelCitaMedic.remove(msisdn);

        message = MessageFormat.format("IPS - Size  lastToken: {0,number,#}",
                lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastSpecialty: {0,number,#}", lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastSpecialtyType: {0,number,#}", lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format("IPS  Size  lastZone: {0,number,#}",
                lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastMedicCenter: {0,number,#}", lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastCancelCita: {0,number,#}", lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastRetornoSpecialty: {0,number,#}",
                lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastRetornoMedicCenter: {0,number,#}",
                lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastRetornoZone: {0,number,#}", lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastRetornoMedic: {0,number,#}", lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Size  lastRetornoCancelCitaMedic: {0,number,#}",
                lastToken.size());
        getFacadeContainer().getNotifier().debug(getClass(), null, message);

        message = MessageFormat.format(
                "IPS - Se eliminaron los valores del hash para volver a recuperar del WS."
                    + "|operation:validarCi | Desde|msisdn: {0} ", msisdn);

        getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

    }

    public RetornoAgendamientoMedico validateDocument(String document, String msisdn) throws InvalidOperationException{
        try {
            for (int i = 0; i < cantReintentos; i++) {
                try {
                    if (getServiciosConsultasSIH() != null) {

                        Date antes = new Date();

                        String message = MessageFormat.format(
                                "IPS - TIME - Se invoca al WS de IPS. Intento:{0}"
                                    + "|operation:validarCi |user:{1}|entity:{2}|"
                                    + "param-document:{3}>>Desde|msisdn: {4} ",
                                i, getWSUser(), getWSEntidad(), document,
                                msisdn);

                        getFacadeContainer().getNotifier().info(getClass(),
                                msisdn, message);

                        RetornoAgendamientoMedico retornoAgendamientoMedico = getServiciosConsultasSIH().validarCi(
                                document, msisdn, getWSEntidad(), getWSUser(),
                                getWSPassword());
                        Date despues = new Date();

                        message = MessageFormat.format(
                                "IPS - TIME - Se invoco al WS de IPS. Intento:{0}"
                                    + "|operation:validarCi |user:{1}|entity:{2}|"
                                    + "param-document:{3}|param-token:{6,number,#}>>Desde|msisdn: {4}>>Tiempo en milisegundos: {5} ",
                                i, getWSUser(), getWSEntidad(), document,
                                msisdn, despues.getTime() - antes.getTime(),
                                retornoAgendamientoMedico.getToken());

                        getFacadeContainer().getNotifier().info(getClass(),
                                msisdn, message);

                        if (retornoAgendamientoMedico != null
                            && retornoAgendamientoMedico.getCodError() != null) {

                            message = MessageFormat.format(
                                    "IPS - Error en la validacion de documento.Codigo Error: {5}. Descripcion: {6}. Intento:{0}"
                                        + "|operation:validarCi |user:{1}|entity:{2}|"
                                        + "param-document:{3}>>Desde|msisdn: {4} ",
                                    i, getWSUser(), getWSEntidad(), document,
                                    msisdn,
                                    retornoAgendamientoMedico.getCodError(),
                                    retornoAgendamientoMedico.getDesError());

                            getFacadeContainer().getNotifier().error(
                                    getClass(), msisdn, message, null);

                            throw new InvalidOperationException(retornoAgendamientoMedico.getDesError());
                        }

                        initForMsisdn(msisdn);
                        lastToken.put(msisdn,
                                retornoAgendamientoMedico.getToken());

                        return retornoAgendamientoMedico;
                    }
                } catch (InvalidOperationException e) {
                    throw e;
                } catch (Exception e) {
                    if (e.getCause() instanceof SocketTimeoutException) {

                        String message = MessageFormat.format(
                                "IPS - El tiempo de espera ha sido superado, vuelva a intentarlo. Intento:{0}"
                                    + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                                    + "param-document:{3}>>Desde|msisdn: {4} ",
                                i, getWSUser(), getWSEntidad(), document,
                                msisdn);

                        getFacadeContainer().getNotifier().warn(getClass(),
                                msisdn, message);

                        throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
                    }
                    serviciosConsultasSIH = null;
                    getFacadeContainer().getNotifier().error(
                            getClass(),
                            msisdn,
                            MessageFormat.format(
                                    "User:{0}. Entidad: {1}. Se reintentara validar el documento. Documento: {2}.",
                                    getWSUser(), getWSEntidad(), document), e);

                    throw new InvalidOperationException("El servicio no pudo validar los datos ingresados. Favor, consulte con el call center de IPS, linea baja 0800115000.");

                }
            }
        } catch (InvalidOperationException e) {
            throw e;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(
                    IpsServiceAPI.class,
                    msisdn,
                    MessageFormat.format(
                            "No se pudo conectar al IpsService. User:{0}. Entidad:{1}.",
                            getWSUser(), getWSEntidad()), e);
        }
        /*
         * si llego aca es porque luego de X intentos exploto
         */

        String message = MessageFormat.format(
                "El WS de IPS no estuvo disponible después de {0} intentos al momento de validar el documento."
                    + "|operation:validarCi|user:{1}|entity:{2}|"
                    + "param-document:{3}>>Retorno|exception:NameException|"
                    + "error-expectation:ALARM >>Desde|msisdn: {4} ",
                cantReintentos, getWSUser(), getWSEntidad(), document, msisdn);

        Auditory auditory = getFacadeContainer().getNotifier().signalMenuMovil(
                getClass(), Action.ERROR, message);

        String transId = ">>Transaction|source:auditory|id:{0,number,#}";

        getFacadeContainer().getNotifier().error(
                getClass(),
                msisdn,
                MessageFormat.format(message.concat(transId),
                        auditory.getAuditoryCod()), null);
        throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
    }

    public RetornoCancelacionesMedicas[] appointmentsByDocument(String document, String msisdn, Long token) throws InvalidOperationException{

        if (lastRetornoCancelCitaMedic.containsKey(msisdn)) {

            String message = MessageFormat.format(
                    "IPS - Se recuperara la lista de citas a cancelar del cache para no consultar a IPS. Intento:{0}"
                        + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                        + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                    0, getWSUser(), getWSEntidad(), document, msisdn, token);

            getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

            return lastRetornoCancelCitaMedic.get(msisdn);
        }

        String message = MessageFormat.format(
                "IPS - Se recuperara la lista de citas a cancelar del WS. Intento:{0}"
                    + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                0, getWSUser(), getWSEntidad(), document, msisdn, token);

        getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

        for (int i = 0; i < cantReintentos; i++) {
            try {
                if (getServiciosConsultasSIH() != null) {

                    message = MessageFormat.format(
                            "IPS - TIME - Se invoca al WS de IPS. Intento:{0}"
                                + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    Date antes = new Date();
                    RetornoCancelacionesMedicas[] citas = getServiciosConsultasSIH().buscarCitasPorCi(
                            document, msisdn, getWSEntidad(), token,
                            getWSUser(), getWSPassword());

                    Date despues = new Date();

                    message = MessageFormat.format(
                            "IPS - TIME - Se invoco al WS de IPS. Intento:{0}"
                                + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{6,number,#}>>Desde|msisdn: {4}>>Tiempo en milisegundos: {5} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            despues.getTime() - antes.getTime(), token);
                    lastRetornoCancelCitaMedic.put(msisdn, citas);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    if (citas != null && citas.length > 0) {
                        RetornoCancelacionesMedicas cita = citas[0];

                        if (cita.getCodError() != null) {
                            message = MessageFormat.format(
                                    "IPS - Ocurrio un error durante la invocación al método buscar citas por CI. Cod Error: {0}. Descripcion: {1}",
                                    cita.getCodError(), cita.getDesError());
                            getFacadeContainer().getNotifier().signal(
                                    getClass(), msisdn, Action.ERROR, message);

                            message = MessageFormat.format(
                                    "IPS - Ocurrio un error durante la invocación al método buscar citas por CI. Cod Error: {5}. Descripcion: {6}. Intento:{0}"
                                        + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                                        + "param-document:{3}|param-token:{7,number,#}>>Desde|msisdn: {4} ",
                                    i, getWSUser(), getWSEntidad(), document,
                                    msisdn, cita.getCodError(),
                                    cita.getDesError(), token);

                            getFacadeContainer().getNotifier().signal(
                                    getClass(), msisdn, Action.ERROR, message);

                            throw new InvalidOperationException(cita.getDesError());
                        }

                    } else {

                        message = MessageFormat.format(
                                "IPS - No se obtuvieron citas en la invocacion al metodo buscar citas por ci. Intento:{0}"
                                    + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                                    + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4}",
                                i, getWSUser(), getWSEntidad(), document,
                                msisdn, token);

                        getFacadeContainer().getNotifier().signal(getClass(),
                                msisdn, Action.ERROR, message);
                        return null;
                    }

                    message = MessageFormat.format(
                            "IPS - Se obtuvieron las citas exitosamente. Intento:{0}"
                                + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    getFacadeContainer().getNotifier().signal(getClass(),
                            msisdn, Action.INFO, message);

                    return citas;
                }
            } catch (InvalidOperationException e) {
                throw e;
            } catch (Exception e) {
                if (e.getCause() instanceof SocketTimeoutException) {
                    message = MessageFormat.format(
                            "IPS - El tiempo de espera ha sido superado, vuelva a intentarlo. Intento:{0}"
                                + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().warn(getClass(), msisdn,
                            message);

                    throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
                }
                serviciosConsultasSIH = null;

                message = MessageFormat.format(
                        "IPS - Se reintentara consultar las citas por CI. Intento:{0}"
                            + "|operation:buscarCitasPorCi |user:{1}|entity:{2}|"
                            + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                        i, getWSUser(), getWSEntidad(), document, msisdn, token);

                getFacadeContainer().getNotifier().warn(getClass(), msisdn,
                        message);

                throw new InvalidOperationException("El servicio no pudo validar los datos ingresados. Favor, consulte con el call center de IPS, linea baja 0800115000.");
            }
        }

        message = MessageFormat.format(
                "IPS - El WS de IPS no estuvo disponible después de {0} intentos al momento de buscar citas agendadas por CI."
                    + "|operation:buscarCitasPorCi|user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Retorno|exception:NameException|"
                    + "error-expectation:ALARM >>Desde|msisdn: {4} ",
                cantReintentos, getWSUser(), getWSEntidad(), document, msisdn,
                token);

        Auditory auditory = getFacadeContainer().getNotifier().signalMenuMovil(
                getClass(), Action.ERROR, message);

        String transId = ">>Transaction|source:auditory|id:{0,number,#}";

        getFacadeContainer().getNotifier().error(
                getClass(),
                msisdn,
                MessageFormat.format(message.concat(transId),
                        auditory.getAuditoryCod()), null);

        /*
         * si llego aca es porque luego de X intentos exploto
         */
        throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
    }

    public String cancelAppointments(String document, String msisdn, Long token, Integer codEmpresa, Long nroAgend, Integer nroRegAmb) throws InvalidOperationException {
        // for (int i = 0; i < cantReintentos; i++) {
        try {
            if (getServiciosConsultasSIH() != null) {

                String message = MessageFormat.format(
                        "IPS - TIME - Se invoca al WS de IPS. Intento:{0}"
                            + "|operation:cancelarCitas |user:{1}|entity:{2}|"
                            + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                        0, getWSUser(), getWSEntidad(), document, msisdn, token);

                getFacadeContainer().getNotifier().info(getClass(), msisdn,
                        message);

                Date antes = new Date();
                String result = serviciosConsultasSIH.cancelarCitas(document,
                        msisdn, token, codEmpresa, nroAgend, nroRegAmb,
                        getWSEntidad(), getWSUser(), getWSPassword());
                Date despues = new Date();

                message = MessageFormat.format(
                        "IPS - TIME - Se invoco al WS de IPS. Intento:{0}"
                            + "|operation:cancelarCitas |user:{1}|entity:{2}|"
                            + "param-document:{3}|param-token:{6,number,#}>>Desde|msisdn: {4}>>Tiempo en milisegundos: {5} ",
                        0, getWSUser(), getWSEntidad(), document, msisdn,
                        despues.getTime() - antes.getTime(), token);

                getFacadeContainer().getNotifier().info(getClass(), msisdn,
                        message);

                message = MessageFormat.format(
                        "IPS - Se ha realizado la solicitud de cancelación de cita exitosamente. Intento:{0}"
                            + "|operation:cancelarCitas |user:{1}|entity:{2}|"
                            + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                        0, getWSUser(), getWSEntidad(), document, msisdn, token);

                getFacadeContainer().getNotifier().signal(getClass(), msisdn,
                        Action.INFO, message);

                initForMsisdn(msisdn);
                return result;
            }
        } catch (Exception e) {
            if (e.getCause() instanceof SocketTimeoutException) {

                String message = MessageFormat.format(
                        "IPS - El tiempo de espera ha sido superado, vuelva a intentarlo. Intento:{0}"
                            + "|operation:cancelarCitas |user:{1}|entity:{2}|"
                            + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                        0, getWSUser(), getWSEntidad(), document, msisdn, token);

                getFacadeContainer().getNotifier().warn(getClass(), msisdn,
                        message);

                throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
            }
            serviciosConsultasSIH = null;
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    msisdn,
                    MessageFormat.format(
                            "User:{0}. Entidad: {1}. Se reintentara cancelar la cita. Documento: {2}.",
                            getWSUser(), getWSEntidad(), document), e);
            throw new InvalidOperationException("El servicio no pudo validar los datos ingresados. Favor, consulte con el call center de IPS, linea baja 0800115000.");
        }
        // }

        String message = MessageFormat.format(
                "El WS de IPS no estuvo disponible después de {0} intentos al momento de Cancelar citas."
                    + "|operation:cancelarCitas|user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Retorno|exception:NameException|"
                    + "error-expectation:ALARM >>Desde|msisdn: {4} ",
                cantReintentos, getWSUser(), getWSEntidad(), document, msisdn,
                token);

        Auditory auditory = getFacadeContainer().getNotifier().signalMenuMovil(
                getClass(), Action.ERROR, message);

        String transId = ">>Transaction|source:auditory|id:{0,number,#}";

        getFacadeContainer().getNotifier().error(
                getClass(),
                msisdn,
                MessageFormat.format(message.concat(transId),
                        auditory.getAuditoryCod()), null);

        /*
         * si llego aca es porque luego de X intentos exploto
         */
        throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
    }

    public RetornoAgendamientoMedico findEspecialty(String document, String msisdn, Long token) throws InvalidOperationException {

        if (lastRetornoSpecialty.containsKey(msisdn)) {

            String message = MessageFormat.format(
                    "IPS - Se recuperara la especialidad del cache para no consultar a IPS. Intento:{0}"
                        + "|operation:consultaEspecialidades |user:{1}|entity:{2}|"
                        + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                    0, getWSUser(), getWSEntidad(), document, msisdn, token);

            getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

            return lastRetornoSpecialty.get(msisdn);
        }

        String message = MessageFormat.format(
                "IPS - Se recuperara la especialidad del WS. Intento:{0}"
                    + "|operation:consultaEspecialidades |user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                0, getWSUser(), getWSEntidad(), document, msisdn, token);

        getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

        for (int i = 0; i < cantReintentos; i++) {
            try {
                if (getServiciosConsultasSIH() != null) {

                    message = MessageFormat.format(
                            "IPS - TIME - Se invoca al WS de IPS. Intento:{0}"
                                + "|operation:consultaEspecialidades |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    Date antes = new Date();
                    RetornoAgendamientoMedico retornoAgendamientoMedico = serviciosConsultasSIH.consultaEspecialidades(
                            document, msisdn, getWSEntidad(), token,
                            getWSUser(), getWSPassword());

                    Date despues = new Date();

                    message = MessageFormat.format(
                            "IPS - TIME - Se invoco al WS de IPS. Intento:{0}"
                                + "|operation:consultaEspecialidades |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{6,number,#}>>Desde|msisdn: {4}>>Tiempo en milisegundos: {5} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            despues.getTime() - antes.getTime(), token);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    if (retornoAgendamientoMedico != null
                        && retornoAgendamientoMedico.getCodError() != null) {

                        message = MessageFormat.format(
                                "IPS - Error en la busqueda de especialidades. Codigo Error: {5}. Descripcion: {6}. Intento:{0}"
                                    + "|operation:consultaEspecialidades |user:{1}|entity:{2}|"
                                    + "param-document:{3}|param-token:{7,number,#}>>Desde|msisdn: {4} ",
                                i, getWSUser(), getWSEntidad(), document,
                                msisdn,
                                retornoAgendamientoMedico.getCodError(),
                                retornoAgendamientoMedico.getDesError(), token);

                        getFacadeContainer().getNotifier().error(getClass(),
                                msisdn, message, null);

                        throw new InvalidOperationException(retornoAgendamientoMedico.getDesError());
                    }

                    lastRetornoSpecialty.put(msisdn, retornoAgendamientoMedico);

                    return retornoAgendamientoMedico;
                }
            } catch (InvalidOperationException e) {
                throw e;
            } catch (Exception e) {
                if (e.getCause() != null
                    && e.getCause() instanceof SocketTimeoutException) {

                    message = MessageFormat.format(
                            "IPS - El tiempo de espera ha sido superado, vuelva a intentarlo. Intento:{0}"
                                + "|operation:consultaEspecialidades |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().warn(getClass(), msisdn,
                            message);

                    throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
                }
                serviciosConsultasSIH = null;
                getFacadeContainer().getNotifier().error(
                        getClass(),
                        msisdn,
                        MessageFormat.format(
                                "User:{0}. Entidad: {1}. Se reintentara buscar la especialidad. Documento: {2}.",
                                getWSUser(), getWSEntidad(), document), e);
                throw new InvalidOperationException("El servicio no pudo validar los datos ingresados. Favor, consulte con el call center de IPS, linea baja 0800115000.");
            }
        }

        message = MessageFormat.format(
                "El WS de IPS no estuvo disponible después de {0} intentos al momento de Consultar especialidades."
                    + "|operation:consultaEspecialidades|user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Retorno|exception:NameException|"
                    + "error-expectation:ALARM >>Desde|msisdn: {4} ",
                cantReintentos, getWSUser(), getWSEntidad(), document, msisdn,
                token);

        Auditory auditory = getFacadeContainer().getNotifier().signalMenuMovil(
                getClass(), Action.ERROR, message);

        String transId = ">>Transaction|source:auditory|id:{0,number,#}";

        getFacadeContainer().getNotifier().error(
                getClass(),
                msisdn,
                MessageFormat.format(message.concat(transId),
                        auditory.getAuditoryCod()), null);

        /*
         * si llego aca es porque luego de X intentos exploto
         */
        throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
    }

    public RetornoAgendamientoMedico findMedicCenter(String document, String msisdn, Long token, Short specialtyCod) throws InvalidOperationException {

        if (lastRetornoMedicCenter.containsKey(msisdn)) {

            String message = MessageFormat.format(
                    "IPS - Se recuperara el centro medico del cache para no consultar a IPS. Intento:{0}"
                        + "|operation:consultaCentroMedico |user:{1}|entity:{2}|"
                        + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                    0, getWSUser(), getWSEntidad(), document, msisdn, token);

            getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

            return lastRetornoMedicCenter.get(msisdn);
        }

        String message = MessageFormat.format(
                "IPS - Se recuperara el centro medico del WS. Intento:{0}"
                    + "|operation:consultaCentroMedico |user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                0, getWSUser(), getWSEntidad(), document, msisdn, token);

        getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

        for (int i = 0; i < cantReintentos; i++) {
            try {
                if (getServiciosConsultasSIH() != null) {

                    message = MessageFormat.format(
                            "IPS - TIME - Se invoca al WS de IPS. Intento:{0}"
                                + "|operation:consultaCentroMedico |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    Date antes = new Date();
                    RetornoAgendamientoMedico retornoAgendamientoMedico = serviciosConsultasSIH.consultaCentroMedico(
                            document, msisdn, getWSEntidad(), token,
                            specialtyCod, getWSUser(), getWSPassword());

                    Date despues = new Date();

                    message = MessageFormat.format(
                            "IPS - TIME - Se invoco al WS de IPS. Intento:{0}"
                                + "|operation:consultaCentroMedico |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4}>>Tiempo en milisegundos: {5} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            despues.getTime() - antes.getTime(), token);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    if (retornoAgendamientoMedico != null
                        && retornoAgendamientoMedico.getCodError() != null) {

                        getFacadeContainer().getNotifier().error(
                                getClass(),
                                msisdn,
                                MessageFormat.format(
                                        "IPS -User: {0}. Entidad:{1}. Error en la busqueda de especialidades. Documento: {2}. Cuenta: {3}. Codigo Error: {4}. Descripcion: {5}  ",
                                        getWSUser(),
                                        getWSEntidad(),
                                        document,
                                        msisdn,
                                        retornoAgendamientoMedico.getCodError(),
                                        retornoAgendamientoMedico.getDesError()),
                                null);
                        throw new InvalidOperationException(retornoAgendamientoMedico.getDesError());
                    }

                    lastRetornoMedicCenter.put(msisdn,
                            retornoAgendamientoMedico);
                    return retornoAgendamientoMedico;

                }
            } catch (InvalidOperationException e) {
                throw e;
            } catch (Exception e) {
                if (e.getCause() instanceof SocketTimeoutException) {

                    message = MessageFormat.format(
                            "IPS - El tiempo de espera ha sido superado, vuelva a intentarlo. Intento:{0}"
                                + "|operation:consultaCentroMedico |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().warn(getClass(), msisdn,
                            message);

                    throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
                }
                serviciosConsultasSIH = null;
                getFacadeContainer().getNotifier().error(
                        getClass(),
                        msisdn,
                        MessageFormat.format(
                                "IPS - User:{0}. Entidad: {1}. Se reintentara buscar el centro medico. Documento: {2}.",
                                getWSUser(), getWSEntidad(), document), e);
                throw new InvalidOperationException("El servicio no pudo validar los datos ingresados. Favor, consulte con el call center de IPS, linea baja 0800115000.");
            }
        }

        message = MessageFormat.format(
                "IPS - El WS de IPS no estuvo disponible después de {0} intentos al momento de Consultar especialidades."
                    + "|operation:consultaEspecialidades|user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Retorno|exception:NameException|"
                    + "error-expectation:ALARM >>Desde|msisdn: {4} ",
                cantReintentos, getWSUser(), getWSEntidad(), document, msisdn,
                token);

        Auditory auditory = getFacadeContainer().getNotifier().signalMenuMovil(
                getClass(), Action.ERROR, message);

        String transId = ">>Transaction|source:auditory|id:{0,number,#}";

        getFacadeContainer().getNotifier().error(
                getClass(),
                msisdn,
                MessageFormat.format(message.concat(transId),
                        auditory.getAuditoryCod()), null);

        /*
         * si llego aca es porque luego de X intentos exploto
         */
        throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
    }

    public RetornoAgendamientoMedico findMedic(String document, String msisdn, Long token, Integer codEmpresa, Integer specialtyCod, String turno) throws InvalidOperationException {

        if (lastRetornoMedic.containsKey(msisdn)) {

            String message = MessageFormat.format(
                    "IPS - Se recuperara el centro medico del cache para no consultar a IPS. Intento:{0}"
                        + "|operation:consultaMedicos |user:{1}|entity:{2}|"
                        + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                    0, getWSUser(), getWSEntidad(), document, msisdn, token);

            getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

            return lastRetornoMedic.get(msisdn);
        }

        String message = MessageFormat.format(
                "IPS - Se recuperara el centro medico del WS. Intento:{0}"
                    + "|operation:consultaMedicos |user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                0, getWSUser(), getWSEntidad(), document, msisdn, token);

        getFacadeContainer().getNotifier().info(getClass(), msisdn, message);

        for (int i = 0; i < cantReintentos; i++) {
            try {
                if (getServiciosConsultasSIH() != null) {

                    message = MessageFormat.format(
                            "IPS - TIME - Se invoca al WS de IPS. Intento:{0}"
                                + "|operation:consultaMedicos |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    Date antes = new Date();
                    RetornoAgendamientoMedico retornoAgendamientoMedico = serviciosConsultasSIH.consultaMedicos(
                            document, msisdn, getWSEntidad(), token,
                            codEmpresa, specialtyCod, getWSUser(),
                            getWSPassword(), turno);

                    Date despues = new Date();
                    message = MessageFormat.format(
                            "IPS - TIME - Se invoco al WS de IPS. Intento:{0}"
                                + "|operation:consultaMedicos |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{6,number,#}>>Desde|msisdn: {4}>>Tiempo en milisegundos: {5} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            despues.getTime() - antes.getTime(), token);

                    getFacadeContainer().getNotifier().info(getClass(), msisdn,
                            message);

                    if (retornoAgendamientoMedico != null
                        && retornoAgendamientoMedico.getCodError() != null) {
                        getFacadeContainer().getNotifier().error(
                                getClass(),
                                msisdn,
                                MessageFormat.format(
                                        "IPS - User: {0}. Entidad:{1}. Error en la busqueda de especialidades. Documento: {2}. Cuenta: {3}. Codigo Error: {4}. Descripcion: {5}  ",
                                        getWSUser(),
                                        getWSEntidad(),
                                        document,
                                        msisdn,
                                        retornoAgendamientoMedico.getCodError(),
                                        retornoAgendamientoMedico.getDesError()),
                                null);
                        throw new InvalidOperationException(retornoAgendamientoMedico.getDesError());
                    }

                    lastRetornoMedic.put(msisdn, retornoAgendamientoMedico);
                    return retornoAgendamientoMedico;
                }
            } catch (InvalidOperationException e) {
                throw e;
            } catch (Exception e) {
                if (e.getCause() instanceof SocketTimeoutException) {

                    message = MessageFormat.format(
                            "IPS - El tiempo de espera ha sido superado, vuelva a intentarlo. Intento:{0}"
                                + "|operation:consultaMedicos |user:{1}|entity:{2}|"
                                + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                            i, getWSUser(), getWSEntidad(), document, msisdn,
                            token);

                    getFacadeContainer().getNotifier().warn(getClass(), msisdn,
                            message);

                    throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
                }
                serviciosConsultasSIH = null;
                getFacadeContainer().getNotifier().error(
                        getClass(),
                        msisdn,
                        MessageFormat.format(
                                "IPS - User:{0}. Entidad: {1}. Se reintentara buscar el medico. Documento: {2}.",
                                getWSUser(), getWSEntidad(), document), e);
                throw new InvalidOperationException("El servicio no pudo validar los datos ingresados. Favor, consulte con el call center de IPS, linea baja 0800115000.");
            }
        }

        message = MessageFormat.format(
                "El WS de IPS no estuvo disponible después de {0} intentos al momento de buscar medico."
                    + "|operation:consultaMedicos|user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Retorno|exception:NameException|"
                    + "error-expectation:ALARM >>Desde|msisdn: {4} ",
                cantReintentos, getWSUser(), getWSEntidad(), document, msisdn,
                token);

        Auditory auditory = getFacadeContainer().getNotifier().signalMenuMovil(
                getClass(), Action.ERROR, message);

        String transId = ">>Transaction|source:auditory|id:{0,number,#}";

        getFacadeContainer().getNotifier().error(
                getClass(),
                msisdn,
                MessageFormat.format(message.concat(transId),
                        auditory.getAuditoryCod()), null);

        throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
    }

    public String scheduleAppointments(String document, String msisdn, Long token, Integer codEmpresa, Integer specialtyCod, String codMedicoTurnoFecha) throws InvalidOperationException {
        // for (int i = 0; i < cantReintentos; i++) {
        try {
            if (getServiciosConsultasSIH() != null) {

                String message = MessageFormat.format(
                        "IPS - TIME - Se invoca al WS de IPS. Intento:{0}"
                            + "|operation:confirmarTurno |user:{1}|entity:{2}|"
                            + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                        0, getWSUser(), getWSEntidad(), document, msisdn, token);

                getFacadeContainer().getNotifier().info(getClass(), msisdn,
                        message);

                Date antes = new Date();

                String result = serviciosConsultasSIH.confirmarTurno(document,
                        msisdn, getWSEntidad(), token, codEmpresa,
                        specialtyCod, codMedicoTurnoFecha, getWSUser(),
                        getWSPassword());

                Date despues = new Date();
                message = MessageFormat.format(
                        "IPS - TIME - Se invoco al WS de IPS. Intento:{0}"
                            + "|operation:confirmarTurno |user:{1}|entity:{2}|"
                            + "param-document:{3}|param-token:{6,number,#}>>Desde|msisdn: {4}>>Tiempo en milisegundos: {5} ",
                        0, getWSUser(), getWSEntidad(), document, msisdn,
                        despues.getTime() - antes.getTime(), token);

                getFacadeContainer().getNotifier().info(getClass(), msisdn,
                        message);

                getFacadeContainer().getNotifier().signal(
                        getClass(),
                        msisdn,
                        Action.INFO,
                        MessageFormat.format(
                                "Se ha realizado la solicitud de agendamiento de cita exitosamente. Documento: {0}. Respuesta: {1}",
                                document, result));

                initForMsisdn(msisdn);
                return result;

            }
        } catch (Exception e) {
            if (e.getCause() instanceof SocketTimeoutException) {

                String message = MessageFormat.format(
                        "IPS - El tiempo de espera ha sido superado, vuelva a intentarlo. Intento:{0}"
                            + "|operation:consultaMedicos |user:{1}|entity:{2}|"
                            + "param-document:{3}|param-token:{5,number,#}>>Desde|msisdn: {4} ",
                        0, getWSUser(), getWSEntidad(), document, msisdn, token);

                getFacadeContainer().getNotifier().warn(getClass(), msisdn,
                        message);

                throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
            }
            serviciosConsultasSIH = null;
            getFacadeContainer().getNotifier().error(
                    getClass(),
                    msisdn,
                    MessageFormat.format(
                            "IPS - User:{0}. Entidad: {1}. Se reintentara agendar la cita. Documento: {2}.",
                            getWSUser(), getWSEntidad(), document), e);
            throw new InvalidOperationException("El servicio no pudo validar los datos ingresados. Favor, consulte con el call center de IPS, linea baja 0800115000.");
        }
        // }

        String message = MessageFormat.format(
                "IPS - El WS de IPS no estuvo disponible después de {0} intentos al momento de buscar medico."
                    + "|operation:confirmarTurno|user:{1}|entity:{2}|"
                    + "param-document:{3}|param-token:{5,number,#}>>Retorno|exception:NameException|"
                    + "error-expectation:ALARM >>Desde|msisdn: {4} ",
                cantReintentos, getWSUser(), getWSEntidad(), document, msisdn,
                token);

        Auditory auditory = getFacadeContainer().getNotifier().signalMenuMovil(
                getClass(), Action.ERROR, message);

        String transId = ">>Transaction|source:auditory|id:{0,number,#}";

        getFacadeContainer().getNotifier().error(
                getClass(),
                msisdn,
                MessageFormat.format(message.concat(transId),
                        auditory.getAuditoryCod()), null);

        throw new InvalidOperationException("Tiempo excedido. Favor, vuelva a intentar");
    }

}
