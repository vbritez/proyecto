package com.tigo.cs.ws.facade;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.commons.util.CellPhoneNumberUtil;
import com.tigo.cs.commons.util.DateUtil;
import com.tigo.cs.domain.Application;
import com.tigo.cs.domain.FeatureValue;
import com.tigo.cs.domain.FeatureValueData;
import com.tigo.cs.domain.Message;
import com.tigo.cs.domain.ServiceValue;
import com.tigo.cs.domain.ServiceValueDetail;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.ws.data.ArpCabData;
import com.tigo.cs.ws.data.ArpDetData;
import com.tigo.cs.ws.data.ArpResponse;
import com.tigo.cs.ws.data.AsistenciaCabData;
import com.tigo.cs.ws.data.AsistenciaDetData;
import com.tigo.cs.ws.data.AsistenciaResponse;
import com.tigo.cs.ws.data.CobranzaCabData;
import com.tigo.cs.ws.data.CobranzaCobrosData;
import com.tigo.cs.ws.data.CobranzaFacturaData;
import com.tigo.cs.ws.data.CobranzaResponse;
import com.tigo.cs.ws.data.ConsultorioCabData;
import com.tigo.cs.ws.data.ConsultorioDetData;
import com.tigo.cs.ws.data.CourierCab;
import com.tigo.cs.ws.data.CourierDet;
import com.tigo.cs.ws.data.CourierResponse;
import com.tigo.cs.ws.data.DataConf;
import com.tigo.cs.ws.data.DeliveryCabData;
import com.tigo.cs.ws.data.DeliveryResponse;
import com.tigo.cs.ws.data.FlotaCabData;
import com.tigo.cs.ws.data.FlotaResponse;
import com.tigo.cs.ws.data.FlotaTramoData;
import com.tigo.cs.ws.data.FormularioDinamicoCab;
import com.tigo.cs.ws.data.FormularioDinamicoDet;
import com.tigo.cs.ws.data.FormularioDinamicoResponse;
import com.tigo.cs.ws.data.GuardiaCabData;
import com.tigo.cs.ws.data.GuardiaDetData;
import com.tigo.cs.ws.data.GuardiaResponse;
import com.tigo.cs.ws.data.InformconfData;
import com.tigo.cs.ws.data.InformconfResponse;
import com.tigo.cs.ws.data.InventarioDAData;
import com.tigo.cs.ws.data.InventarioDAResponse;
import com.tigo.cs.ws.data.InventarioSTDResponse;
import com.tigo.cs.ws.data.InventarioStdData;
import com.tigo.cs.ws.data.MedicoCabData;
import com.tigo.cs.ws.data.MedicoDetData;
import com.tigo.cs.ws.data.OTCab;
import com.tigo.cs.ws.data.OTDet;
import com.tigo.cs.ws.data.OTResponse;
import com.tigo.cs.ws.data.PedidoCab;
import com.tigo.cs.ws.data.PedidoCabVP;
import com.tigo.cs.ws.data.PedidoDet;
import com.tigo.cs.ws.data.PedidoResponse;
import com.tigo.cs.ws.data.ProductoMedData;
import com.tigo.cs.ws.data.RondaGuardiaCabData;
import com.tigo.cs.ws.data.RondaGuardiaDetData;
import com.tigo.cs.ws.data.RondaGuardiaResponse;
import com.tigo.cs.ws.data.TrackingData;
import com.tigo.cs.ws.data.TrackingResponse;
import com.tigo.cs.ws.data.VisitaData;
import com.tigo.cs.ws.data.VisitaPedidoResponse;
import com.tigo.cs.ws.data.VisitaResponse;
import com.tigo.cs.ws.data.VisitaVPData;
import com.tigo.cs.ws.data.VisitadorMedicoCabData;
import com.tigo.cs.ws.data.VisitadorMedicoResponse;

/**
 * 
 * @author Miguel Zorrilla
 */
@Stateless
public class DataMigration extends WSBase {

    @EJB
    private FacadeContainer facadeContainer;
    private final static DateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    protected Application application;

    public enum WSOperation {

        SUCCESS(0, "Exito", "client.data.ws.SuccessMessage"),
        ERROR(100, "Error", "client.data.ws.ErrorMessage");
        private final int code;
        private final String description;
        private final String message;

        WSOperation(int code, String description, String message) {
            this.code = code;
            this.description = description;
            this.message = message;
        }

        /**
         *
         */
        public int code() {
            return code;
        }

        /**
         * Nombre del meta en singular.
         */
        public String description() {
            return description;
        }

        /**
         * Nombre del meta en singular.
         */
        public String getMessage() {
            return message;
        }
    }

    @PostConstruct
    public void postConstruct() {
        application = getFacadeContainer().getApplicationAPI().find(4L);
    }

    public String verifyException(String user, Exception e) {

        String verify = facadeContainer.getNotifier().verifyException(user, e);
        if (verify == null) {
            verify = facadeContainer.getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError");
        }
        return DataConf.ERROR_KEY_WORD + " - " + verify;
    }

    // -------------------------------------------------------------------------
    // SERVICIO 1 = VISITAS
    // -------------------------------------------------------------------------
    /**
     * Retorna todas las visitas realizadas por los usuarios telefónicos de un
     * cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importVisitas(long id, String user, String password, int limit) {
        /*
         * obs4dev ********
         * 
         * Este método devuelve un registro por cada detalle de visita en la
         * tabla SERVICE_VALUE_DETAIL. Los eventos de entrada y salida de una
         * visita representan 2 registros, el evento VISITA-RAPIDA representa un
         * registro.
         */
        String result = "";
        List<VisitaData> visitas = new ArrayList<VisitaData>();
        Userweb userweb;

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "visit", false);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT svd "
                + "FROM ServiceValueDetail svd, IN(svd.serviceValue.userphone.classificationList) cl "
                + "WHERE svd.serviceValue.service.serviceCod = :serviceCod "
                + "AND svd.servicevaluedetailCod > :fromId "
                + "AND svd.serviceValue.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND svd.serviceValue.enabledChr = true AND svd.enabledChr = true "
                + "ORDER BY svd.servicevaluedetailCod ");
            query.setParameter("serviceCod", 1L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValueDetail> svdList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean

            // Trae el formato de la duracion de la visita de acuerdo al cliente
            String durationFormat = null;
            durationFormat = getFacadeContainer().getClientParameterValueAPI().findByCode(
                    userweb.getClient().getClientCod(),
                    "client.duration.format");
            if (durationFormat == null)
                durationFormat = getFacadeContainer().getI18nAPI().iValue(
                        "web.client.screen.configuration.field.ExtendedDurationFormatValue");

            for (ServiceValueDetail svd : svdList) {
                VisitaData visita = new VisitaData();
                visita.setId(svd.getServicevaluedetailCod());
                visita.setNroTelefono(svd.getServiceValue().getUserphone().getCellphoneNum().toString());
                visita.setUsuarioTelefono(svd.getServiceValue().getUserphone().getNameChr());
                visita.setFechahora(svd.getRecorddateDat());
                visita.setEvento(getVisitEvent(svd.getColumn1Chr()));
                visita.setCliente(svd.getColumn2Chr());
                visita.setMotivo(svd.getColumn3Chr());
                visita.setObservacion(svd.getColumn4Chr());

                visita.setDuracion(svd.getColumn5Chr());
                try {
                    if (svd.getColumn6Chr() != null)
                        visita.setDuracion(DateUtil.getPeriodWithFormat(
                                durationFormat, 0L,
                                Long.parseLong(svd.getColumn6Chr())));
                    else
                        visita.setDuracion(svd.getColumn5Chr());
                } catch (Exception e) {
                    visita.setDuracion(svd.getColumn5Chr());
                }

                visita.setLatitud("");
                visita.setLongitud("");
                if (svd.getMessage() != null) {
                    Message m = svd.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        visita.setLatitud(m.getLatitude().toString());
                        visita.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        visita.setLatitud(m.getCell().getLatitudeNum().toString());
                        visita.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                visitas.add(visita);
            }

            result = getXStream().toXML(visitas);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de visitas. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;

    }

    /**
     * Retorna todas las visitas realizadas por los usuarios telefónicos de un
     * cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public VisitaResponse importVisitasComplex(long id, String user, String password, int limit) {
        VisitaResponse resp = new VisitaResponse();
        /*
         * obs4dev ********
         * 
         * Este método devuelve un registro por cada detalle de visita en la
         * tabla SERVICE_VALUE_DETAIL. Los eventos de entrada y salida de una
         * visita representan 2 registros, el evento VISITA-RAPIDA representa un
         * registro.
         */
        List<VisitaData> visitas = new ArrayList<VisitaData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "visit", false);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT svd "
                + "FROM ServiceValueDetail svd, IN(svd.serviceValue.userphone.classificationList) cl "
                + "WHERE svd.serviceValue.service.serviceCod = :serviceCod "
                + "AND svd.servicevaluedetailCod > :fromId "
                + "AND svd.serviceValue.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND svd.serviceValue.enabledChr = true AND svd.enabledChr = true "
                + "ORDER BY svd.servicevaluedetailCod ");
            query.setParameter("serviceCod", 1L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValueDetail> svdList = query.getResultList();

            // Trae el formato de la duracion de la visita de acuerdo al cliente
            String durationFormat = null;
            durationFormat = getFacadeContainer().getClientParameterValueAPI().findByCode(
                    userweb.getClient().getClientCod(),
                    "client.duration.format");
            if (durationFormat == null)
                durationFormat = getFacadeContainer().getI18nAPI().iValue(
                        "web.client.screen.configuration.field.ExtendedDurationFormatValue");

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValueDetail svd : svdList) {
                VisitaData visita = new VisitaData();
                visita.setId(svd.getServicevaluedetailCod());
                visita.setNroTelefono(svd.getServiceValue().getUserphone().getCellphoneNum().toString());
                visita.setUsuarioTelefono(svd.getServiceValue().getUserphone().getNameChr());
                visita.setFechahora(svd.getRecorddateDat());
                visita.setEvento(getVisitEvent(svd.getColumn1Chr()));
                visita.setCliente(svd.getColumn2Chr());
                visita.setMotivo(svd.getColumn3Chr());
                visita.setObservacion(svd.getColumn4Chr());
                visita.setDuracion(svd.getColumn5Chr());

                try {
                    if (svd.getColumn6Chr() != null)
                        visita.setDuracion(DateUtil.getPeriodWithFormat(
                                durationFormat, 0L,
                                Long.parseLong(svd.getColumn6Chr())));
                    else
                        visita.setDuracion(svd.getColumn5Chr());
                } catch (Exception e) {
                    visita.setDuracion(svd.getColumn5Chr());
                }

                visita.setLatitud("");
                visita.setLongitud("");
                if (svd.getMessage() != null) {
                    Message m = svd.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        visita.setLatitud(m.getLatitude().toString());
                        visita.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        visita.setLatitud(m.getCell().getLatitudeNum().toString());
                        visita.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                visitas.add(visita);
            }
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de visitas. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(visitas.toArray(new VisitaData[0]));
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((VisitaData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((VisitaData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private String getVisitEvent(String visitEvent) {
        if (visitEvent.toUpperCase().equals("ENT")) {
            return "Entrada de la visita";
        } else if (visitEvent.toUpperCase().equals("SAL")) {
            return "Salida de la visita";
        } else {
            return "Visita rapida";
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 2 = PEDIDOS
    // -------------------------------------------------------------------------
    /**
     * Retorna todos los pedidos realizados por los usuarios telefónicos de un
     * cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importPedidos(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada cabecera de pedido en la
         * tabla SERVICE_VALUE Los detalles del pedidos se obtienen de los
         * registros asociados en la tabla SERVICE_VALUE_DETAIL
         */
        String result = "";
        List<PedidoCab> pedidos = new ArrayList<PedidoCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "order", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl "
                + "LEFT OUTER join sv.serviceValueDetailCollection svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND sv.column9Chr IS NULL "
                + "AND cl IN(:classificationList) "
                + "AND svd.enabledChr = true "
                + "AND (svd.enabledChr = true OR svd.enabledChr is null)"
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 2L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svdList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (ServiceValue sv : svdList) {
                PedidoCab pedidoCab = new PedidoCab();
                pedidoCab.setId(sv.getServicevalueCod());
                pedidoCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                pedidoCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                pedidoCab.setFechaHora(sv.getMessage().getDateinDat());
                pedidoCab.setCliente(sv.getColumn1Chr());
                pedidoCab.setTipoFactura(sv.getColumn2Chr());
                pedidoCab.setListaPrecio(sv.getColumn3Chr());
                pedidoCab.setTotalPedido(sv.getColumn4Chr());
                pedidoCab.setMarcado(sv.getColumn5Chr());
                pedidoCab.setObservacion(sv.getColumn6Chr());
                pedidoCab.setLatitud("");
                pedidoCab.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        pedidoCab.setLatitud(m.getLatitude().toString());
                        pedidoCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        pedidoCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        pedidoCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<PedidoDet> detalles = new ArrayList<PedidoDet>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    PedidoDet pedidoDet = new PedidoDet();
                    pedidoDet.setId(svd.getServicevaluedetailCod());
                    pedidoDet.setProducto(svd.getColumn1Chr());
                    pedidoDet.setCantidad(svd.getColumn2Chr());
                    pedidoDet.setDescuento(svd.getColumn3Chr());
                    pedidoDet.setPrecio(svd.getColumn4Chr());
                    pedidoDet.setTotal(svd.getColumn5Chr());
                    detalles.add(pedidoDet);
                }
                pedidoCab.setDetalles(detalles);
                pedidos.add(pedidoCab);
            }

            result = getXStream().toXML(pedidos);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de pedidos. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public PedidoResponse importPedidosComplex(long id, String user, String password, int limit) {
        PedidoResponse resp = new PedidoResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada cabecera de pedido en la
         * tabla SERVICE_VALUE Los detalles del pedidos se obtienen de los
         * registros asociados en la tabla SERVICE_VALUE_DETAIL
         */
        List<PedidoCab> pedidos = new ArrayList<PedidoCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "order", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl "
                + "LEFT OUTER join sv.serviceValueDetailCollection svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND sv.column9Chr IS NULL "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true "
                + "AND (svd.enabledChr = true OR svd.enabledChr is null)"
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 2L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svdList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (ServiceValue sv : svdList) {
                PedidoCab pedidoCab = new PedidoCab();
                pedidoCab.setId(sv.getServicevalueCod());
                pedidoCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                pedidoCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                pedidoCab.setFechaHora(sv.getMessage().getDateinDat());
                pedidoCab.setCliente(sv.getColumn1Chr());
                pedidoCab.setTipoFactura(sv.getColumn2Chr());
                pedidoCab.setListaPrecio(sv.getColumn3Chr());
                pedidoCab.setTotalPedido(sv.getColumn4Chr());
                pedidoCab.setMarcado(sv.getColumn5Chr());
                pedidoCab.setObservacion(sv.getColumn6Chr());
                pedidoCab.setLatitud("");
                pedidoCab.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        pedidoCab.setLatitud(m.getLatitude().toString());
                        pedidoCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        pedidoCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        pedidoCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<PedidoDet> detalles = new ArrayList<PedidoDet>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    PedidoDet pedidoDet = new PedidoDet();
                    pedidoDet.setId(svd.getServicevaluedetailCod());
                    pedidoDet.setProducto(svd.getColumn1Chr());
                    pedidoDet.setCantidad(svd.getColumn2Chr());
                    pedidoDet.setDescuento(svd.getColumn3Chr());
                    pedidoDet.setPrecio(svd.getColumn4Chr());
                    pedidoDet.setTotal(svd.getColumn5Chr());
                    detalles.add(pedidoDet);
                }
                pedidoCab.setDetalles(detalles);
                pedidos.add(pedidoCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(pedidos.toArray(new PedidoCab[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de pedidos. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((PedidoCab[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((PedidoCab[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 2 = PEDIDOS ILIMITADOS
    // -------------------------------------------------------------------------

    /**
     * Retorna todos los pedidos ilimitados realizados por los usuarios
     * telefonicos de un cliente, sin distinci�n entre pedidos abiertos o
     * cerrados.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importPedidosIlimitados(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada cabecera de pedido en la
         * tabla SERVICE_VALUE Los detalles del pedidos se obtienen de los
         * registros asociados en la tabla SERVICE_VALUE_DETAIL
         */
        String result = "";
        List<PedidoCab> pedidos = new ArrayList<PedidoCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "order", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl "
                + "LEFT OUTER join sv.serviceValueDetailCollection svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND sv.column9Chr IS NOT NULL "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true "
                + "AND (svd.enabledChr = true OR svd.enabledChr is null)"
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 2L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svdList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (ServiceValue sv : svdList) {
                PedidoCab pedidoCab = new PedidoCab();
                pedidoCab.setId(sv.getServicevalueCod());
                pedidoCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                pedidoCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                pedidoCab.setFechaHora(sv.getMessage().getDateinDat());
                pedidoCab.setCliente(sv.getColumn1Chr());
                pedidoCab.setTipoFactura(sv.getColumn2Chr());
                pedidoCab.setListaPrecio(sv.getColumn3Chr());
                pedidoCab.setTotalPedido(sv.getColumn4Chr());
                pedidoCab.setMarcado(sv.getColumn5Chr());
                pedidoCab.setObservacion(sv.getColumn6Chr());
                pedidoCab.setEstado(sv.getColumn9Chr().equals("1") ? "C" : "A");
                pedidoCab.setLatitud("");
                pedidoCab.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        pedidoCab.setLatitud(m.getLatitude().toString());
                        pedidoCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        pedidoCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        pedidoCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<PedidoDet> detalles = new ArrayList<PedidoDet>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    PedidoDet pedidoDet = new PedidoDet();
                    pedidoDet.setId(svd.getServicevaluedetailCod());
                    pedidoDet.setProducto(svd.getColumn1Chr());
                    pedidoDet.setCantidad(svd.getColumn2Chr());
                    pedidoDet.setDescuento(svd.getColumn3Chr());
                    pedidoDet.setPrecio(svd.getColumn4Chr());
                    pedidoDet.setTotal(svd.getColumn5Chr());
                    detalles.add(pedidoDet);
                }
                pedidoCab.setDetalles(detalles);
                pedidos.add(pedidoCab);
            }

            result = getXStream().toXML(pedidos);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de pedidos. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().warn(DataMigration.class, null,
                    ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public PedidoResponse importPedidosIlimitadosComplex(long id, String user, String password, int limit) {
        PedidoResponse resp = new PedidoResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada cabecera de pedido en la
         * tabla SERVICE_VALUE Los detalles del pedidos se obtienen de los
         * registros asociados en la tabla SERVICE_VALUE_DETAIL
         */
        List<PedidoCab> pedidos = new ArrayList<PedidoCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "order", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl "
                + "LEFT OUTER join sv.serviceValueDetailCollection svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND sv.column9Chr IS NOT NULL "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true "
                + "AND (svd.enabledChr = true OR svd.enabledChr is null)"
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 2L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svdList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (ServiceValue sv : svdList) {
                PedidoCab pedidoCab = new PedidoCab();
                pedidoCab.setId(sv.getServicevalueCod());
                pedidoCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                pedidoCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                pedidoCab.setFechaHora(sv.getMessage().getDateinDat());
                pedidoCab.setCliente(sv.getColumn1Chr());
                pedidoCab.setTipoFactura(sv.getColumn2Chr());
                pedidoCab.setListaPrecio(sv.getColumn3Chr());
                pedidoCab.setTotalPedido(sv.getColumn4Chr());
                pedidoCab.setMarcado(sv.getColumn5Chr());
                pedidoCab.setObservacion(sv.getColumn6Chr());
                pedidoCab.setEstado(sv.getColumn9Chr().equals("1") ? "C" : "A");
                pedidoCab.setLatitud("");
                pedidoCab.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        pedidoCab.setLatitud(m.getLatitude().toString());
                        pedidoCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        pedidoCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        pedidoCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<PedidoDet> detalles = new ArrayList<PedidoDet>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    PedidoDet pedidoDet = new PedidoDet();
                    pedidoDet.setId(svd.getServicevaluedetailCod());
                    pedidoDet.setProducto(svd.getColumn1Chr());
                    pedidoDet.setCantidad(svd.getColumn2Chr());
                    pedidoDet.setDescuento(svd.getColumn3Chr());
                    pedidoDet.setPrecio(svd.getColumn4Chr());
                    pedidoDet.setTotal(svd.getColumn5Chr());
                    detalles.add(pedidoDet);
                }
                pedidoCab.setDetalles(detalles);
                pedidos.add(pedidoCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(pedidos.toArray(new PedidoCab[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de pedidos ilimitados. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((PedidoCab[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((PedidoCab[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 3 = VISITA-PEDIDOS
    // -------------------------------------------------------------------------
    /**
     * Retorna todos los registros del servicio visita-pedido realizados por los
     * usuarios telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importVisitaPedidos(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada visita abierta, los
         * detalles de la visita son los pedidos realizados en medio de la
         * visita.
         * 
         * Visitas: En la tabla SERVICE_VALUE_DETAIL se dispone de 2 registros
         * para la entrada y salida de la visita, para simplificar estos
         * registros se transforman en un solo registro para el cliente.
         * 
         * Pedidos: Los pedidos,a su vez, tienen una estructura
         * cabecera-detalle. La cabecera del pedido se encuentra en
         * SERVICE_VALUE Y los detalles en SERVICE_VALUE_DETAIL.
         */
        String result = "";
        List<VisitaVPData> visitas = new ArrayList<VisitaVPData>();

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            Userweb userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "visitorder", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND UPPER (sv.column1Chr) LIKE 'APERTURA' "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 3L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                VisitaVPData visita = new VisitaVPData();
                // Recorremos los detalles: las entradas y salidas de las
                // visitas
                Collection<ServiceValueDetail> visitasDets = sv.getServiceValueDetailCollection();
                for (ServiceValueDetail visitaDet : visitasDets) {
                    // Seteamos los datos: Los eventos de entrada y salida van
                    // en una sola línea
                    if (visitaDet.getColumn1Chr().toUpperCase().equals("ENT")) {
                        visita.setId(visitaDet.getServicevaluedetailCod());
                        visita.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                        visita.setUsuarioTelefono(sv.getUserphone().getNameChr());
                        visita.setCliente(visitaDet.getColumn2Chr());
                        visita.setObservacionEntrada(visitaDet.getColumn4Chr());
                        visita.setFechaHoraEntrada(visitaDet.getRecorddateDat());
                        visita.setLatitudEntrada("");
                        visita.setLongitudEntrada("");
                        if (visitaDet.getMessage() != null) {
                            Message m = visitaDet.getMessage();
                            if (m.getLatitude() != null
                                && m.getLongitude() != null) {
                                visita.setLatitudEntrada(m.getLatitude().toString());
                                visita.setLongitudEntrada(m.getLongitude().toString());
                            } else if (m.getCell() != null) {
                                visita.setLatitudEntrada(m.getCell().getLatitudeNum().toString());
                                visita.setLongitudEntrada(m.getCell().getLongitudeNum().toString());
                            }
                        }

                        visita.setPedidos(getPedidoCabVP(visita.getId()));
                    } else {
                        visita.setMotivo(visitaDet.getColumn3Chr());
                        visita.setObservacionSalida(visitaDet.getColumn4Chr());
                        visita.setFechaHoraSalida(visitaDet.getRecorddateDat());
                        visita.setDuracion(visitaDet.getColumn7Chr());
                        visita.setLatitudSalida("");
                        visita.setLongitudSalida("");
                        if (visitaDet.getMessage() != null) {
                            Message m = visitaDet.getMessage();
                            if (m.getLatitude() != null
                                && m.getLongitude() != null) {
                                visita.setLatitudSalida(m.getLatitude().toString());
                                visita.setLongitudSalida(m.getLongitude().toString());
                            } else if (m.getCell() != null) {
                                visita.setLatitudSalida(m.getCell().getLatitudeNum().toString());
                                visita.setLongitudSalida(m.getCell().getLongitudeNum().toString());
                            }
                        }
                    }
                }
                visitas.add(visita);
            }
            result = getXStream().toXML(visitas);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de visita-pedidos. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

        return result;
    }

    public VisitaPedidoResponse importVisitaPedidosComplex(long id, String user, String password, int limit) {
        VisitaPedidoResponse resp = new VisitaPedidoResponse();

        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada visita abierta, los
         * detalles de la visita son los pedidos realizados en medio de la
         * visita.
         * 
         * Visitas: En la tabla SERVICE_VALUE_DETAIL se dispone de 2 registros
         * para la entrada y salida de la visita, para simplificar estos
         * registros se transforman en un solo registro para el cliente.
         * 
         * Pedidos: Los pedidos,a su vez, tienen una estructura
         * cabecera-detalle. La cabecera del pedido se encuentra en
         * SERVICE_VALUE Y los detalles en SERVICE_VALUE_DETAIL.
         */
        List<VisitaVPData> visitas = new ArrayList<VisitaVPData>();

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            Userweb userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "visitorder", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND UPPER (sv.column1Chr) LIKE 'APERTURA' "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 3L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                VisitaVPData visita = new VisitaVPData();
                // Recorremos los detalles: las entradas y salidas de las
                // visitas
                Collection<ServiceValueDetail> visitasDets = sv.getServiceValueDetailCollection();
                for (ServiceValueDetail visitaDet : visitasDets) {
                    // Seteamos los datos: Los eventos de entrada y salida van
                    // en una sola línea
                    if (visitaDet.getColumn1Chr().toUpperCase().equals("ENT")) {
                        visita.setId(visitaDet.getServicevaluedetailCod());
                        visita.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                        visita.setUsuarioTelefono(sv.getUserphone().getNameChr());
                        visita.setCliente(visitaDet.getColumn2Chr());
                        visita.setObservacionEntrada(visitaDet.getColumn4Chr());
                        visita.setFechaHoraEntrada(visitaDet.getRecorddateDat());
                        visita.setLatitudEntrada("");
                        visita.setLongitudEntrada("");
                        if (visitaDet.getMessage() != null) {
                            Message m = visitaDet.getMessage();
                            if (m.getLatitude() != null
                                && m.getLongitude() != null) {
                                visita.setLatitudEntrada(m.getLatitude().toString());
                                visita.setLongitudEntrada(m.getLongitude().toString());
                            } else if (m.getCell() != null) {
                                visita.setLatitudEntrada(m.getCell().getLatitudeNum().toString());
                                visita.setLongitudEntrada(m.getCell().getLongitudeNum().toString());
                            }
                        }

                        visita.setPedidos(getPedidoCabVP(visita.getId()));
                    } else {
                        visita.setMotivo(visitaDet.getColumn3Chr());
                        visita.setObservacionSalida(visitaDet.getColumn4Chr());
                        visita.setFechaHoraSalida(visitaDet.getRecorddateDat());
                        visita.setDuracion(visitaDet.getColumn7Chr());
                        visita.setLatitudSalida("");
                        visita.setLongitudSalida("");
                        if (visitaDet.getMessage() != null) {
                            Message m = visitaDet.getMessage();
                            if (m.getLatitude() != null
                                && m.getLongitude() != null) {
                                visita.setLatitudSalida(m.getLatitude().toString());
                                visita.setLongitudSalida(m.getLongitude().toString());
                            } else if (m.getCell() != null) {
                                visita.setLatitudSalida(m.getCell().getLatitudeNum().toString());
                                visita.setLongitudSalida(m.getCell().getLongitudeNum().toString());
                            }
                        }
                    }
                }
                visitas.add(visita);
            }
            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(visitas.toArray(new VisitaVPData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de visita-pedido. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((VisitaVPData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((VisitaVPData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private List<PedidoCabVP> getPedidoCabVP(long visitaId) {
        List<PedidoCabVP> pedidos = new ArrayList<PedidoCabVP>();
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            /*
             * PEDIDOS Se obtiene las cabeceras de los pedidos de acuerdo al id
             * del registro de la entradad de visita
             */
            Query qPedidos = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv ,IN(sv.serviceValueDetailCollection) svd"
                + "WHERE sv.column5Chr = :codVisitaEnt "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod");
            qPedidos.setParameter("codVisitaEnt", String.valueOf(visitaId));
            List<ServiceValue> pedidosList = qPedidos.getResultList();

            // Recorremos la cabecera y seteamos datos de la misma.
            for (ServiceValue svPedCab : pedidosList) {
                PedidoCabVP pedidoCab = new PedidoCabVP();
                pedidoCab.setId(svPedCab.getServicevalueCod());
                pedidoCab.setFechaHora(svPedCab.getRecorddateDat());
                pedidoCab.setTipoFactura(svPedCab.getColumn2Chr());
                pedidoCab.setListaPrecio(svPedCab.getColumn3Chr());
                pedidoCab.setTotalPedido(svPedCab.getColumn6Chr());
                pedidoCab.setLatitud("");
                pedidoCab.setLongitud("");
                if (svPedCab.getMessage() != null) {
                    Message m = svPedCab.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        pedidoCab.setLatitud(m.getLatitude().toString());
                        pedidoCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        pedidoCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        pedidoCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                // Recorremos los detalles y setamos datos.
                List<PedidoDet> detalles = new ArrayList<PedidoDet>();
                for (ServiceValueDetail svd : svPedCab.getServiceValueDetailCollection()) {
                    PedidoDet pedidoDet = new PedidoDet();
                    pedidoDet.setId(svd.getServicevaluedetailCod());
                    pedidoDet.setProducto(svd.getColumn1Chr());
                    pedidoDet.setCantidad(svd.getColumn2Chr());
                    pedidoDet.setDescuento(svd.getColumn3Chr());
                    pedidoDet.setPrecio(svd.getColumn4Chr());
                    pedidoDet.setTotal(svd.getColumn5Chr());
                    detalles.add(pedidoDet);
                }
                if (detalles.size() > 0) {
                    pedidoCab.setDetalles(detalles);
                }
                pedidos.add(pedidoCab);
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage());
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return pedidos;
    }

    // -------------------------------------------------------------------------
    // SERVICIO 6 = GUARDIAS
    // -------------------------------------------------------------------------

    /**
     * Retorna todas las rondas de guardia realizadas por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */

    public String importGuardias(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         */
        String result = "";
        List<GuardiaCabData> rondas = new ArrayList<GuardiaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "guard", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 6L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                GuardiaCabData guardiaCab = new GuardiaCabData();
                guardiaCab.setId(sv.getServicevalueCod());
                guardiaCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                guardiaCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                guardiaCab.setFechaHoraApertura(sv.getColumn2Chr());
                guardiaCab.setObservacionApertura(sv.getColumn3Chr());
                guardiaCab.setLugar(sv.getColumn4Chr());
                guardiaCab.setFechaHoraCierre(sv.getColumn5Chr());
                guardiaCab.setObservacionCierre(sv.getColumn6Chr());
                guardiaCab.setLatitudApertura("");
                guardiaCab.setLongitudApertura("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        guardiaCab.setLatitudApertura(m.getLatitude().toString());
                        guardiaCab.setLongitudApertura(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        guardiaCab.setLatitudApertura(m.getCell().getLatitudeNum().toString());
                        guardiaCab.setLongitudApertura(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<GuardiaDetData> marcaciones = new ArrayList<GuardiaDetData>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    GuardiaDetData marcacion = new GuardiaDetData();
                    marcacion.setId(svd.getServicevaluedetailCod());
                    marcacion.setFechaHora(svd.getRecorddateDat());
                    marcacion.setObservacion(svd.getColumn1Chr());
                    marcacion.setFueraDeTiempo(svd.getColumn2Chr());
                    marcacion.setLatitud("");
                    marcacion.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            marcacion.setLatitud(m.getLatitude().toString());
                            marcacion.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            marcacion.setLatitud(m.getCell().getLatitudeNum().toString());
                            marcacion.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }
                    marcaciones.add(marcacion);
                }
                guardiaCab.setDetalle(marcaciones);
                rondas.add(guardiaCab);
            }
            result = getXStream().toXML(rondas);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de guardias. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public GuardiaResponse importGuardiasComplex(long id, String user, String password, int limit) {
        GuardiaResponse resp = new GuardiaResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         */
        List<GuardiaCabData> rondas = new ArrayList<GuardiaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "guard", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 6L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                GuardiaCabData guardiaCab = new GuardiaCabData();
                guardiaCab.setId(sv.getServicevalueCod());
                guardiaCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                guardiaCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                guardiaCab.setFechaHoraApertura(sv.getColumn2Chr());
                guardiaCab.setObservacionApertura(sv.getColumn3Chr());
                guardiaCab.setLugar(sv.getColumn4Chr());
                guardiaCab.setFechaHoraCierre(sv.getColumn5Chr());
                guardiaCab.setObservacionCierre(sv.getColumn6Chr());
                guardiaCab.setLatitudApertura("");
                guardiaCab.setLongitudApertura("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        guardiaCab.setLatitudApertura(m.getLatitude().toString());
                        guardiaCab.setLongitudApertura(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        guardiaCab.setLatitudApertura(m.getCell().getLatitudeNum().toString());
                        guardiaCab.setLongitudApertura(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<GuardiaDetData> marcaciones = new ArrayList<GuardiaDetData>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    GuardiaDetData marcacion = new GuardiaDetData();
                    marcacion.setId(svd.getServicevaluedetailCod());
                    marcacion.setFechaHora(svd.getRecorddateDat());
                    marcacion.setObservacion(svd.getColumn1Chr());
                    marcacion.setFueraDeTiempo(svd.getColumn2Chr());
                    marcacion.setLatitud("");
                    marcacion.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            marcacion.setLatitud(m.getLatitude().toString());
                            marcacion.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            marcacion.setLatitud(m.getCell().getLatitudeNum().toString());
                            marcacion.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }
                    marcaciones.add(marcacion);
                }
                guardiaCab.setDetalle(marcaciones);
                rondas.add(guardiaCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(rondas.toArray(new GuardiaCabData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de guardias. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((GuardiaCabData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((GuardiaCabData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 5 = COBRANZAS
    // -------------------------------------------------------------------------

    /**
     * Retorna todas las cobranzas realizadas por los usuarios telefónicos de un
     * cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importCobranzas(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         * 
         * Los tipos de detalles facturas y cobros. Es factura cuando en la
         * tabla SERVICE_VALUE_DETAIL el campo COLUMN1_CHR = INVOICE, es cobro
         * sino no lo es.
         */

        String result = ""; // resultado a retornar
        List<CobranzaCabData> cobranzas = new ArrayList<CobranzaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "collection", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 5L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                CobranzaCabData cobranza = new CobranzaCabData();
                cobranza.setId(sv.getServicevalueCod());
                cobranza.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                cobranza.setUsuarioTelefono(sv.getUserphone().getNameChr());
                cobranza.setFechaHora(sv.getMessage().getDateinDat());
                cobranza.setCliente(sv.getColumn1Chr());
                cobranza.setNroRecibo(sv.getColumn2Chr());
                cobranza.setLatitud("");
                cobranza.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        cobranza.setLatitud(m.getLatitude().toString());
                        cobranza.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        cobranza.setLatitud(m.getCell().getLatitudeNum().toString());
                        cobranza.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<CobranzaFacturaData> facturas = new ArrayList<CobranzaFacturaData>();
                List<CobranzaCobrosData> cobros = new ArrayList<CobranzaCobrosData>();

                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    if (svd.getColumn1Chr().toUpperCase().equals("INVOICE")) {
                        // Es una factura
                        CobranzaFacturaData factura = new CobranzaFacturaData();
                        factura.setNroFactura(svd.getColumn2Chr());
                        factura.setMonto(svd.getColumn3Chr());
                        facturas.add(factura);
                    } else {
                        // Es un cobro
                        CobranzaCobrosData cobro = new CobranzaCobrosData();
                        cobro.setTipoCobranza(svd.getColumn2Chr());
                        cobro.setMontoCobrado(svd.getColumn3Chr());
                        cobro.setBanco(svd.getColumn4Chr());
                        cobro.setCheque(svd.getColumn5Chr());
                        cobro.setFechaCheque(svd.getColumn6Chr());
                        cobro.setFechaVencimientoCheque(svd.getColumn7Chr());
                        cobro.setObservacion(svd.getColumn8Chr());
                        cobros.add(cobro);
                    }
                }
                cobranza.setFacturas(facturas);
                cobranza.setCobros(cobros);
                cobranzas.add(cobranza);
            }

            result = getXStream().toXML(cobranzas);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de cobranzas. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public CobranzaResponse importCobranzasComplex(long id, String user, String password, int limit) {
        CobranzaResponse resp = new CobranzaResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         * 
         * Los tipos de detalles facturas y cobros. Es factura cuando en la
         * tabla SERVICE_VALUE_DETAIL el campo COLUMN1_CHR = INVOICE, es cobro
         * sino no lo es.
         */

        List<CobranzaCabData> cobranzas = new ArrayList<CobranzaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "collection", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 5L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                CobranzaCabData cobranza = new CobranzaCabData();
                cobranza.setId(sv.getServicevalueCod());
                cobranza.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                cobranza.setUsuarioTelefono(sv.getUserphone().getNameChr());
                cobranza.setFechaHora(sv.getMessage().getDateinDat());
                cobranza.setCliente(sv.getColumn1Chr());
                cobranza.setNroRecibo(sv.getColumn2Chr());
                cobranza.setLatitud("");
                cobranza.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        cobranza.setLatitud(m.getLatitude().toString());
                        cobranza.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        cobranza.setLatitud(m.getCell().getLatitudeNum().toString());
                        cobranza.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<CobranzaFacturaData> facturas = new ArrayList<CobranzaFacturaData>();
                List<CobranzaCobrosData> cobros = new ArrayList<CobranzaCobrosData>();

                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    if (svd.getColumn1Chr().toUpperCase().equals("INVOICE")) {
                        // Es una factura
                        CobranzaFacturaData factura = new CobranzaFacturaData();
                        factura.setId(svd.getServicevaluedetailCod());
                        factura.setNroFactura(svd.getColumn2Chr());
                        factura.setMonto(svd.getColumn3Chr());
                        facturas.add(factura);
                    } else {
                        // Es un cobro
                        CobranzaCobrosData cobro = new CobranzaCobrosData();
                        cobro.setId(svd.getServicevaluedetailCod());
                        cobro.setTipoCobranza(svd.getColumn2Chr());
                        cobro.setMontoCobrado(svd.getColumn3Chr());
                        cobro.setBanco(svd.getColumn4Chr());
                        cobro.setCheque(svd.getColumn5Chr());
                        cobro.setFechaCheque(svd.getColumn6Chr());
                        cobro.setFechaVencimientoCheque(svd.getColumn7Chr());
                        cobro.setObservacion(svd.getColumn8Chr());
                        cobros.add(cobro);
                    }
                }
                cobranza.setFacturas(facturas);
                cobranza.setCobros(cobros);
                cobranzas.add(cobranza);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(cobranzas.toArray(new CobranzaCabData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de Cobranzas. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((CobranzaCabData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((CobranzaCabData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 7 = DELIVERY
    // -------------------------------------------------------------------------

    /**
     * Retorna todas las entregas realizadas por los usuarios telefónicos de un
     * cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importDeliveries(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         * 
         * El detalle de una entrega es una lista de remisiones.
         */
        String result = "";
        List<DeliveryCabData> deliveries = new ArrayList<DeliveryCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "delivery", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 7L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                DeliveryCabData delivery = new DeliveryCabData();
                delivery.setId(sv.getServicevalueCod());
                delivery.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                delivery.setUsuarioTelefono(sv.getUserphone().getNameChr());
                delivery.setFechaHora(sv.getMessage().getDateinDat());
                delivery.setCliente(sv.getColumn1Chr());
                delivery.setLatitud("");
                delivery.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        delivery.setLatitud(m.getLatitude().toString());
                        delivery.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        delivery.setLatitud(m.getCell().getLatitudeNum().toString());
                        delivery.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<String> remisiones = new ArrayList<String>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    remisiones.add(svd.getColumn1Chr());
                }
                delivery.setRemisiones(remisiones);
                deliveries.add(delivery);
            }

            result = getXStream().toXML(deliveries);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de entregas DA. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public DeliveryResponse importDeliveriesComplex(long id, String user, String password, int limit) {
        DeliveryResponse resp = new DeliveryResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         * 
         * El detalle de una entrega es una lista de remisiones.
         */
        List<DeliveryCabData> deliveries = new ArrayList<DeliveryCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "delivery", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 7L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                DeliveryCabData delivery = new DeliveryCabData();
                delivery.setId(sv.getServicevalueCod());
                delivery.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                delivery.setUsuarioTelefono(sv.getUserphone().getNameChr());
                delivery.setFechaHora(sv.getMessage().getDateinDat());
                delivery.setCliente(sv.getColumn1Chr());
                delivery.setLatitud("");
                delivery.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        delivery.setLatitud(m.getLatitude().toString());
                        delivery.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        delivery.setLatitud(m.getCell().getLatitudeNum().toString());
                        delivery.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<String> remisiones = new ArrayList<String>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    remisiones.add(svd.getColumn1Chr());
                }
                delivery.setRemisiones(remisiones);
                deliveries.add(delivery);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(deliveries.toArray(new DeliveryCabData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de Entregas. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((DeliveryCabData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((DeliveryCabData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 8 = INVENTARIO DA
    // -------------------------------------------------------------------------
    /**
     * Retorna los registros de inventarios realizadas por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importInventarioDA(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         * 
         * El detalle de una entrega es una lista de remisiones.
         */
        String result = "";
        List<InventarioDAData> inventarios = new ArrayList<InventarioDAData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "inventoryDA", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 8L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();
            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    InventarioDAData inventario = new InventarioDAData();
                    inventario.setId(svd.getServicevaluedetailCod());
                    inventario.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                    inventario.setUsuarioTelefono(sv.getUserphone().getNameChr());
                    inventario.setFechaHora(svd.getRecorddateDat());
                    inventario.setSupeR(svd.getColumn1Chr());
                    inventario.setRepartidor(svd.getColumn2Chr());
                    inventario.setTipoBandeja(svd.getColumn3Chr());
                    inventario.setUbicacion(svd.getColumn4Chr());
                    inventario.setCantidad(svd.getColumn5Chr());
                    inventario.setLatitud("");
                    inventario.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            inventario.setLatitud(m.getLatitude().toString());
                            inventario.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            inventario.setLatitud(m.getCell().getLatitudeNum().toString());
                            inventario.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }

                    inventarios.add(inventario);
                }
            }
            result = getXStream().toXML(inventarios);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de inventarios. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;

    }

    public InventarioDAResponse importInventarioDAComplex(long id, String user, String password, int limit) {
        InventarioDAResponse resp = new InventarioDAResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         * 
         * El detalle de una entrega es una lista de remisiones.
         */
        List<InventarioDAData> inventarios = new ArrayList<InventarioDAData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "inventoryDA", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 8L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();
            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    InventarioDAData inventario = new InventarioDAData();
                    inventario.setId(svd.getServicevaluedetailCod());
                    inventario.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                    inventario.setUsuarioTelefono(sv.getUserphone().getNameChr());
                    inventario.setFechaHora(svd.getRecorddateDat());
                    inventario.setSupeR(svd.getColumn1Chr());
                    inventario.setRepartidor(svd.getColumn2Chr());
                    inventario.setTipoBandeja(svd.getColumn3Chr());
                    inventario.setUbicacion(svd.getColumn4Chr());
                    inventario.setCantidad(svd.getColumn5Chr());
                    inventario.setLatitud("");
                    inventario.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            inventario.setLatitud(m.getLatitude().toString());
                            inventario.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            inventario.setLatitud(m.getCell().getLatitudeNum().toString());
                            inventario.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }

                    inventarios.add(inventario);
                }
            }
            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(inventarios.toArray(new InventarioDAData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de inventario. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((InventarioDAData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((InventarioDAData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    // -------------------------------------------------------------------------
    // SERVICIO 9 = ARP
    // -------------------------------------------------------------------------
    /**
     * Retorna los registros de ARP realizadas por los usuarios telefónicos de
     * un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importArp(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         * 
         * Los detalles de ARP son guia+cota y las matriculas.
         */
        String result = "";
        List<ArpCabData> arps = new ArrayList<ArpCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "arp", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 9L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                ArpCabData arpCab = new ArpCabData();
                arpCab.setId(sv.getServicevalueCod());
                arpCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                arpCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                arpCab.setFechaHora(sv.getMessage().getDateinDat());
                arpCab.setNroFactura(sv.getColumn1Chr());
                arpCab.setTipoFactura(sv.getColumn2Chr());
                arpCab.setMonto(sv.getColumn3Chr());
                arpCab.setLatitud("");
                arpCab.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        arpCab.setLatitud(m.getLatitude().toString());
                        arpCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        arpCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        arpCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<ArpDetData> detalles = new ArrayList<ArpDetData>();
                List<String> matriculas = new ArrayList<String>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    ArpDetData detalle = new ArpDetData();
                    if (svd.getColumn1Chr().equals("GC")) {
                        detalle.setNroGuia(svd.getColumn2Chr());
                        detalle.setCota(svd.getColumn3Chr());
                        detalle.setMonto(svd.getColumn4Chr());
                        detalles.add(detalle);
                    } else {
                        matriculas.add(svd.getColumn2Chr());
                    }
                }
                arpCab.setDetalles(detalles);
                arpCab.setMatriculas(matriculas);
                arps.add(arpCab);
            }

            result = getXStream().toXML(arps);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de ARP. User: " + user + ". From id: "
                        + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public ArpResponse importArpComplex(long id, String user, String password, int limit) {
        ArpResponse resp = new ArpResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         * 
         * Los detalles de ARP son guia+cota y las matriculas.
         */
        List<ArpCabData> arps = new ArrayList<ArpCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "arp", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 9L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                ArpCabData arpCab = new ArpCabData();
                arpCab.setId(sv.getServicevalueCod());
                arpCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                arpCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                arpCab.setFechaHora(sv.getMessage().getDateinDat());
                arpCab.setNroFactura(sv.getColumn1Chr());
                arpCab.setTipoFactura(sv.getColumn2Chr());
                arpCab.setMonto(sv.getColumn3Chr());
                arpCab.setLatitud("");
                arpCab.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        arpCab.setLatitud(m.getLatitude().toString());
                        arpCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        arpCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        arpCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<ArpDetData> detalles = new ArrayList<ArpDetData>();
                List<String> matriculas = new ArrayList<String>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    ArpDetData detalle = new ArpDetData();
                    if (svd.getColumn1Chr().equals("GC")) {
                        detalle.setNroGuia(svd.getColumn2Chr());
                        detalle.setCota(svd.getColumn3Chr());
                        detalle.setMonto(svd.getColumn4Chr());
                        detalles.add(detalle);
                    } else {
                        matriculas.add(svd.getColumn2Chr());
                    }
                }
                arpCab.setDetalles(detalles);
                arpCab.setMatriculas(matriculas);
                arps.add(arpCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(arps.toArray(new ArpCabData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de ARP. User: " + user + ". From id: "
                        + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((ArpCabData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((ArpCabData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 10 = INVENTARIO STD
    // -------------------------------------------------------------------------
    /**
     * Retorna los registros de inventarios realizadas por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importInventarioEstandar(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE_DETAIL. La cabecera solo se utiliza para obtener datos
         * del phone user.
         */
        String result = "";
        List<InventarioStdData> inventarios = new ArrayList<InventarioStdData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "inventory", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 10L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    InventarioStdData inventario = new InventarioStdData();
                    inventario.setId(svd.getServicevaluedetailCod());
                    inventario.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                    inventario.setUsuarioTelefono(sv.getUserphone().getNameChr());
                    inventario.setFechaHora(svd.getRecorddateDat());
                    inventario.setDeposito(svd.getColumn1Chr());
                    inventario.setProducto(svd.getColumn2Chr());
                    inventario.setCantidad(svd.getColumn3Chr());
                    inventario.setLatitud("");
                    inventario.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            inventario.setLatitud(m.getLatitude().toString());
                            inventario.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            inventario.setLatitud(m.getCell().getLatitudeNum().toString());
                            inventario.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }

                    inventarios.add(inventario);
                }
            }
            result = getXStream().toXML(inventarios);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de inventarios. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public InventarioSTDResponse importInventarioEstandarComplex(long id, String user, String password, int limit) {
        InventarioSTDResponse resp = new InventarioSTDResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE_DETAIL. La cabecera solo se utiliza para obtener datos
         * del phone user.
         */
        List<InventarioStdData> inventarios = new ArrayList<InventarioStdData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "inventory", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 10L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    InventarioStdData inventario = new InventarioStdData();
                    inventario.setId(svd.getServicevaluedetailCod());
                    inventario.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                    inventario.setUsuarioTelefono(sv.getUserphone().getNameChr());
                    inventario.setFechaHora(svd.getRecorddateDat());
                    inventario.setDeposito(svd.getColumn1Chr());
                    inventario.setProducto(svd.getColumn2Chr());
                    inventario.setCantidad(svd.getColumn3Chr());
                    inventario.setLatitud("");
                    inventario.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            inventario.setLatitud(m.getLatitude().toString());
                            inventario.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            inventario.setLatitud(m.getCell().getLatitudeNum().toString());
                            inventario.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }
                    inventarios.add(inventario);
                }
            }
            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(inventarios.toArray(new InventarioStdData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de inventarios. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((InventarioStdData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((InventarioStdData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 11 = ASISITENCIA
    // -------------------------------------------------------------------------
    /**
     * Retorna las asistencias realizadas por los usuarios telefónicos de un
     * cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importAsistencias(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE que es de uno por día. Los detalles son las marcaciones
         * durante del día de los eventos de entrada y salida.
         */

        String result = "";
        List<AsistenciaCabData> asistencias = new ArrayList<AsistenciaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "attendance", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 11L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                AsistenciaCabData asistenciaCab = new AsistenciaCabData();
                asistenciaCab.setId(sv.getServicevalueCod());
                asistenciaCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                asistenciaCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                asistenciaCab.setFecha(sv.getRecorddateDat());
                asistenciaCab.setCodigoEmpleado(sv.getColumn1Chr());

                List<AsistenciaDetData> detalles = new ArrayList<AsistenciaDetData>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    AsistenciaDetData asistenciaDet = new AsistenciaDetData();
                    asistenciaDet.setId(svd.getServicevaluedetailCod());
                    asistenciaDet.setFechaHora(svd.getRecorddateDat());
                    asistenciaDet.setEvento(getEventName(svd.getColumn1Chr()));
                    asistenciaDet.setTipo(svd.getColumn2Chr().toUpperCase().equals(
                            "I") ? "Inicio" : "Fin");

                    asistenciaDet.setLatitud("");
                    asistenciaDet.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            asistenciaDet.setLatitud(m.getLatitude().toString());
                            asistenciaDet.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            asistenciaDet.setLatitud(m.getCell().getLatitudeNum().toString());
                            asistenciaDet.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }
                    detalles.add(asistenciaDet);
                }
                asistenciaCab.setDetalles(detalles);
                asistencias.add(asistenciaCab);
            }

            result = getXStream().toXML(asistencias);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de asistencias. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public AsistenciaResponse importAsistenciasComplex(long id, String user, String password, int limit) {
        AsistenciaResponse resp = new AsistenciaResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE que es de uno por día. Los detalles son las marcaciones
         * durante del día de los eventos de entrada y salida.
         */

        List<AsistenciaCabData> asistencias = new ArrayList<AsistenciaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "attendance", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 11L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                AsistenciaCabData asistenciaCab = new AsistenciaCabData();
                asistenciaCab.setId(sv.getServicevalueCod());
                asistenciaCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                asistenciaCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                asistenciaCab.setFecha(sv.getRecorddateDat());
                asistenciaCab.setCodigoEmpleado(sv.getColumn1Chr());

                List<AsistenciaDetData> detalles = new ArrayList<AsistenciaDetData>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    AsistenciaDetData asistenciaDet = new AsistenciaDetData();
                    asistenciaDet.setId(svd.getServicevaluedetailCod());
                    asistenciaDet.setFechaHora(svd.getRecorddateDat());
                    asistenciaDet.setEvento(getEventName(svd.getColumn1Chr()));
                    asistenciaDet.setTipo(svd.getColumn2Chr().toUpperCase().equals(
                            "I") ? "Inicio" : "Fin");

                    asistenciaDet.setLatitud("");
                    asistenciaDet.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            asistenciaDet.setLatitud(m.getLatitude().toString());
                            asistenciaDet.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            asistenciaDet.setLatitud(m.getCell().getLatitudeNum().toString());
                            asistenciaDet.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }

                    detalles.add(asistenciaDet);
                }
                asistenciaCab.setDetalles(detalles);
                asistencias.add(asistenciaCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(asistencias.toArray(new AsistenciaCabData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de asistencias. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((AsistenciaCabData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((AsistenciaCabData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private String getEventName(String prefix) {
        if (prefix.toUpperCase().equals("P")) {
            return "Presencia del dia";
        } else if (prefix.toUpperCase().equals("L")) {
            return "Almuerzo";
        } else {
            return "Receso";
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 12 = FLOTAS
    // -------------------------------------------------------------------------
    /**
     * Retorna los recorridos de las flotas realizadas por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importFlotas(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE que es de uno por día. Los detalles son las recorridos
         * durante del día de los vehículos.
         */

        String result = "";
        List<FlotaCabData> flotas = new ArrayList<FlotaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "fleet", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 12L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                FlotaCabData flotaCab = new FlotaCabData();
                flotaCab.setId(sv.getServicevalueCod());
                flotaCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                flotaCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                flotaCab.setCodigoChofer(sv.getColumn1Chr());
                flotaCab.setCodigoVehiculo(sv.getColumn2Chr());
                flotaCab.setKilometrajeInicial(sv.getColumn3Chr());
                flotaCab.setKilometrajeFinal(sv.getColumn4Chr());
                flotaCab.setObservacionDevolucion(sv.getColumn5Chr());
                flotaCab.setFechaHoraRetiro(sv.getColumn6Chr());
                flotaCab.setFechaHoraDevolucion(sv.getColumn7Chr());
                flotaCab.setDuracionRecorrido(sv.getColumn8Chr());
                flotaCab.setLatitudApertura("");
                flotaCab.setLongitudApertura("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        flotaCab.setLatitudApertura(m.getLatitude().toString());
                        flotaCab.setLongitudApertura(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        flotaCab.setLatitudApertura(m.getCell().getLatitudeNum().toString());
                        flotaCab.setLongitudApertura(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<FlotaTramoData> tramos = new ArrayList<FlotaTramoData>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    FlotaTramoData tramo = new FlotaTramoData();
                    tramo.setCliente(svd.getColumn1Chr());
                    tramo.setKilometrajeActual(svd.getColumn2Chr());
                    tramo.setDuracion(svd.getColumn3Chr());
                    tramo.setFechaHora(svd.getRecorddateDat());
                    tramo.setLatitud("");
                    tramo.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            tramo.setLatitud(m.getLatitude().toString());
                            tramo.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            tramo.setLatitud(m.getCell().getLatitudeNum().toString());
                            tramo.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }

                    tramos.add(tramo);
                }
                flotaCab.setTramos(tramos);
                flotas.add(flotaCab);
            }

            result = getXStream().toXML(flotas);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de flotas. User: " + user + ". From id: "
                        + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;

    }

    public FlotaResponse importFlotasComplex(long id, String user, String password, int limit) {
        FlotaResponse resp = new FlotaResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE que es de uno por día. Los detalles son las recorridos
         * durante del día de los vehículos.
         */

        List<FlotaCabData> flotas = new ArrayList<FlotaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "fleet", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 12L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                FlotaCabData flotaCab = new FlotaCabData();
                flotaCab.setId(sv.getServicevalueCod());
                flotaCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                flotaCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                flotaCab.setCodigoChofer(sv.getColumn1Chr());
                flotaCab.setCodigoVehiculo(sv.getColumn2Chr());
                flotaCab.setKilometrajeInicial(sv.getColumn3Chr());
                flotaCab.setKilometrajeFinal(sv.getColumn4Chr());
                flotaCab.setObservacionDevolucion(sv.getColumn5Chr());
                flotaCab.setFechaHoraRetiro(sv.getColumn6Chr());
                flotaCab.setFechaHoraDevolucion(sv.getColumn7Chr());
                flotaCab.setDuracionRecorrido(sv.getColumn8Chr());
                flotaCab.setLatitudApertura("");
                flotaCab.setLongitudApertura("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        flotaCab.setLatitudApertura(m.getLatitude().toString());
                        flotaCab.setLongitudApertura(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        flotaCab.setLatitudApertura(m.getCell().getLatitudeNum().toString());
                        flotaCab.setLongitudApertura(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<FlotaTramoData> tramos = new ArrayList<FlotaTramoData>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    FlotaTramoData tramo = new FlotaTramoData();
                    tramo.setCliente(svd.getColumn1Chr());
                    tramo.setKilometrajeActual(svd.getColumn2Chr());
                    tramo.setDuracion(svd.getColumn3Chr());
                    tramo.setFechaHora(svd.getRecorddateDat());
                    tramo.setLatitud("");
                    tramo.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            tramo.setLatitud(m.getLatitude().toString());
                            tramo.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            tramo.setLatitud(m.getCell().getLatitudeNum().toString());
                            tramo.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }

                    tramos.add(tramo);
                }
                flotaCab.setTramos(tramos);
                flotas.add(flotaCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(flotas.toArray(new FlotaCabData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de flotas. User: " + user + ". From id: "
                        + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((FlotaCabData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((FlotaCabData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    // -------------------------------------------------------------------------
    // SERVICIO 13 = INFORMCONF
    // -------------------------------------------------------------------------
    /**
     * Retorna las consultas a informconf realizadas por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importInformconf(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE. No hay datos del servicio en la tabla
         * SERVICE_VALUE_DETAIL.
         */

        String result = "";
        List<InformconfData> informconfs = new ArrayList<InformconfData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "informconf", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 13L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                InformconfData informconf = new InformconfData();
                informconf.setId(sv.getServicevalueCod());
                informconf.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                informconf.setUsuarioTelefono(sv.getUserphone().getNameChr());
                informconf.setFechaHora(sv.getMessage().getDateinDat());
                informconf.setPersona(sv.getColumn1Chr());
                informconf.setNombre(sv.getColumn2Chr());
                informconf.setLatitud("");
                informconf.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        informconf.setLatitud(m.getLatitude().toString());
                        informconf.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        informconf.setLatitud(m.getCell().getLatitudeNum().toString());
                        informconf.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                informconfs.add(informconf);
            }

            result = getXStream().toXML(informconfs);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de informconf. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public InformconfResponse importInformconfComplex(long id, String user, String password, int limit) {
        InformconfResponse resp = new InformconfResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE. No hay datos del servicio en la tabla
         * SERVICE_VALUE_DETAIL.
         */

        List<InformconfData> informconfs = new ArrayList<InformconfData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "informconf", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 13L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                InformconfData informconf = new InformconfData();
                informconf.setId(sv.getServicevalueCod());
                informconf.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                informconf.setUsuarioTelefono(sv.getUserphone().getNameChr());
                informconf.setFechaHora(sv.getMessage().getDateinDat());
                informconf.setPersona(sv.getColumn1Chr());
                informconf.setNombre(sv.getColumn2Chr());
                informconf.setLatitud("");
                informconf.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        informconf.setLatitud(m.getLatitude().toString());
                        informconf.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        informconf.setLatitud(m.getCell().getLatitudeNum().toString());
                        informconf.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }
                informconfs.add(informconf);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(informconfs.toArray(new InformconfData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de informconf. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((InformconfData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((InformconfData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public String importTracking(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE. No hay datos del servicio en la tabla
         * SERVICE_VALUE_DETAIL.
         */

        String result = "";
        List<TrackingData> trackings = new ArrayList<TrackingData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "tracking", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 4L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                TrackingData tracking = new TrackingData();
                tracking.setId(sv.getServicevalueCod());
                tracking.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                tracking.setUsuarioTelefono(sv.getUserphone().getNameChr());
                tracking.setFechaHora(sv.getMessage().getDateinDat());
                tracking.setLatitud("");
                tracking.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        tracking.setLatitud(m.getLatitude().toString());
                        tracking.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        tracking.setLatitud(m.getCell().getLatitudeNum().toString());
                        tracking.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                trackings.add(tracking);
            }

            result = getXStream().toXML(trackings);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de rastreo. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public TrackingResponse importTrackingComplex(long id, String user, String password, int limit) {
        TrackingResponse resp = new TrackingResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve una línea por cada registro en la tabla
         * SERVICE_VALUE. No hay datos del servicio en la tabla
         * SERVICE_VALUE_DETAIL.
         */

        List<TrackingData> trackings = new ArrayList<TrackingData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "tracking", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 4L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                TrackingData tracking = new TrackingData();
                tracking.setId(sv.getServicevalueCod());
                tracking.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                tracking.setUsuarioTelefono(sv.getUserphone().getNameChr());
                tracking.setFechaHora(sv.getMessage().getDateinDat());
                tracking.setLatitud("");
                tracking.setLongitud("");
                if (sv.getMessage() != null) {
                    Message m = sv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        tracking.setLatitud(m.getLatitude().toString());
                        tracking.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        tracking.setLatitud(m.getCell().getLatitudeNum().toString());
                        tracking.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                trackings.add(tracking);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(trackings.toArray(new TrackingData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de rastreos. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((TrackingData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((TrackingData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public String sendMessage(String user, String password, String mobileNum, String message) {
        /*
         * obs4dev ********
         * 
         * Este metodo envia el mensaje especificado en message al numero
         * especificado en mobileNum.
         */
        String result = "";
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticacion
            userweb = validateUser(user, password);

            // 2) Verificar que el destinatario pertenezca a la plataforma y sea
            // de la clasificacion
            Query query = em.createQuery("" + "SELECT u "
                + "FROM Userphone u, IN(u.classificationList) cl "
                + "WHERE cl IN(:classificationList) "
                + "AND u.cellphoneNum = :cellphoneNum "
                + "AND u.enabledChr = true ");
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setParameter(
                    "cellphoneNum",
                    new BigInteger(CellPhoneNumberUtil.correctMsisdnToInternationalFormat(mobileNum)));
            List<ServiceValue> svList = query.getResultList();

            if (svList != null && svList.size() > 0) {

                getFacadeContainer().getSmsQueueAPI().sendToJMS(
                        CellPhoneNumberUtil.correctMsisdnToLocalFormat(mobileNum),
                        application, message);

                result = getFacadeContainer().getI18nAPI().iValue(
                        "client.data.ws.SuccessfulSendingMessage");
                getFacadeContainer().getNotifier().signal(
                        DataMigration.class,
                        Action.INFO,
                        "Enviando mensaje al numero: " + mobileNum
                            + " desde el Usuario: " + user + ".");
            } else {
                getFacadeContainer().getNotifier().signal(
                        DataMigration.class,
                        Action.INFO,
                        "No se enviando mensaje al numero: " + mobileNum
                            + " desde el Usuario: " + user
                            + " porque no pertenecer a su clasificacion.");
                return DataConf.ERROR_KEY_WORD
                    + getFacadeContainer().getI18nAPI().iValue(
                            "ejb.ws.data.DestNotAllowedError");
            }
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;

    }

    private int getMaxLimit() {
        try {
            return Integer.parseInt(getFacadeContainer().getGlobalParameterAPI().findByCode(
                    "ws.dataimport.limit.max"));
        } catch (GenericFacadeException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, ex.getMessage());
            return 0;
        }
    }

    private static XStream getXStream() {
        XStream xs = new XStream(new PureJavaReflectionProvider(), new DomDriver());
        xs.autodetectAnnotations(true);
        return xs;
    }

    // -------------------------------------------------------------------------
    // SERVICIO 15 = RONDAS DE GUARDIAS
    // -------------------------------------------------------------------------

    /**
     * Retorna todas las rondas de guardia realizadas por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */

    public String importRondasGuardias(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         */
        String result = "";
        List<RondaGuardiaCabData> rondas = new ArrayList<RondaGuardiaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "shiftguard", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 15L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                RondaGuardiaCabData guardiaCab = new RondaGuardiaCabData();
                guardiaCab.setId(sv.getServicevalueCod());
                guardiaCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                guardiaCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                guardiaCab.setFechaRonda(sv.getRecorddateDat());
                guardiaCab.setCodGuardia(sv.getColumn1Chr());

                List<RondaGuardiaDetData> marcaciones = new ArrayList<RondaGuardiaDetData>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    RondaGuardiaDetData marcacion = new RondaGuardiaDetData();
                    marcacion.setId(svd.getServicevaluedetailCod());
                    marcacion.setFechaHora(svd.getRecorddateDat());
                    marcacion.setObservacion(svd.getColumn1Chr());
                    marcacion.setFueraDeTiempo(svd.getColumn2Chr());
                    marcacion.setLatitud("");
                    marcacion.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            marcacion.setLatitud(m.getLatitude().toString());
                            marcacion.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            marcacion.setLatitud(m.getCell().getLatitudeNum().toString());
                            marcacion.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }
                    marcaciones.add(marcacion);
                }
                guardiaCab.setDetalle(marcaciones);
                rondas.add(guardiaCab);
            }
            result = getXStream().toXML(rondas);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de rondas de guardias. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public RondaGuardiaResponse importRondasGuardiasComplex(long id, String user, String password, int limit) {
        RondaGuardiaResponse resp = new RondaGuardiaResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         */
        List<RondaGuardiaCabData> rondas = new ArrayList<RondaGuardiaCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "shiftguard", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 15L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            // 4) Fetch de los resultados y almacenamiento en el bean
            for (ServiceValue sv : svList) {
                RondaGuardiaCabData guardiaCab = new RondaGuardiaCabData();
                guardiaCab.setId(sv.getServicevalueCod());
                guardiaCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                guardiaCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                guardiaCab.setFechaRonda(sv.getRecorddateDat());
                guardiaCab.setCodGuardia(sv.getColumn1Chr());

                List<RondaGuardiaDetData> marcaciones = new ArrayList<RondaGuardiaDetData>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    RondaGuardiaDetData marcacion = new RondaGuardiaDetData();
                    marcacion.setId(svd.getServicevaluedetailCod());
                    marcacion.setFechaHora(svd.getRecorddateDat());
                    marcacion.setObservacion(svd.getColumn1Chr());
                    marcacion.setFueraDeTiempo(svd.getColumn2Chr());
                    marcacion.setLatitud("");
                    marcacion.setLongitud("");
                    if (svd.getMessage() != null) {
                        Message m = svd.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            marcacion.setLatitud(m.getLatitude().toString());
                            marcacion.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            marcacion.setLatitud(m.getCell().getLatitudeNum().toString());
                            marcacion.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }
                    marcaciones.add(marcacion);
                }
                guardiaCab.setDetalle(marcaciones);
                rondas.add(guardiaCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(rondas.toArray(new RondaGuardiaCabData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de rondas de guardia. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((RondaGuardiaCabData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((RondaGuardiaCabData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 17 = VISITADORES MEDICOS
    // -------------------------------------------------------------------------

    /**
     * Retorna todas las visitas medicas realizadas por los usuarios telefónicos
     * de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */

    public String importVisitadoresMedicos(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         */
        String result = "";
        List<VisitadorMedicoCabData> visitasMed = new ArrayList<VisitadorMedicoCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "visitmedic", true);

            // 3) Obtención de datos de visitadores medicos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.column1Chr = 'MV' "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 17L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            for (ServiceValue sv : svList) {
                VisitadorMedicoCabData visitadorCab = new VisitadorMedicoCabData();
                visitadorCab.setId(sv.getServicevalueCod());
                visitadorCab.setFecha(sv.getMessage().getDateinDat());
                visitadorCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                visitadorCab.setUsuarioTelefono(sv.getUserphone().getNameChr());

                query = em.createQuery(""
                    + "SELECT DISTINCT sv "
                    + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                    + "WHERE sv.service.serviceCod = :serviceCod "
                    + "AND sv.column3Chr = 'CL' "
                    + "AND sv.column1Chr = :serviceValueCod "
                    + "AND sv.userphone.client.clientCod = :clientCod "
                    + "AND cl IN(:classificationList) "
                    + "AND sv.enabledChr = true "
                    + "AND svd.enabledChr = true "
                    + "ORDER BY sv.servicevalueCod ");
                query.setParameter("serviceCod", 17L);
                query.setParameter("serviceValueCod",
                        sv.getServicevalueCod().toString());
                query.setParameter("clientCod",
                        userweb.getClient().getClientCod());
                query.setParameter(
                        "classificationList",
                        getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                                userweb));
                List<ServiceValue> svConsultorios = query.getResultList();

                // Lista de cabeceras de consultorios por cabecera de visitador
                List<ConsultorioCabData> consultorios = new ArrayList<ConsultorioCabData>();
                for (ServiceValue sv2 : svConsultorios) {
                    ConsultorioCabData consultorioCab = new ConsultorioCabData();
                    consultorioCab.setId(sv2.getServicevalueCod());
                    consultorioCab.setFechaHora(sv2.getMessage().getDateinDat());
                    consultorioCab.setCodLocal(sv2.getColumn2Chr());
                    consultorioCab.setLatitud("");
                    consultorioCab.setLongitud("");
                    if (sv2.getMessage() != null) {
                        Message m = sv2.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            consultorioCab.setLatitud(m.getLatitude().toString());
                            consultorioCab.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            consultorioCab.setLatitud(m.getCell().getLatitudeNum().toString());
                            consultorioCab.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }

                    // Lista de detalles de consultorio
                    List<ConsultorioDetData> consultorioDetalles = new ArrayList<ConsultorioDetData>();
                    for (ServiceValueDetail svd : sv2.getServiceValueDetailCollection()) {
                        ConsultorioDetData consultorioDet = new ConsultorioDetData();
                        consultorioDet.setId(svd.getServicevaluedetailCod());
                        consultorioDet.setEvento(svd.getColumn1Chr());
                        consultorioDet.setKmInicial(svd.getColumn2Chr());
                        consultorioDet.setKmFinal(svd.getColumn3Chr());
                        consultorioDet.setObservacion(svd.getColumn4Chr());
                        consultorioDet.setFechaHora(svd.getRecorddateDat());
                        consultorioDetalles.add(consultorioDet);
                    }
                    consultorioCab.setDetalles(consultorioDetalles);

                    query = em.createQuery(""
                        + "SELECT DISTINCT sv "
                        + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                        + "WHERE sv.service.serviceCod = :serviceCod "
                        + "AND sv.column3Chr = 'ME' "
                        + "AND sv.column1Chr = :serviceValueCod "
                        + "AND sv.userphone.client.clientCod = :clientCod "
                        + "AND cl IN(:classificationList) "
                        + "AND sv.enabledChr = true "
                        + "AND svd.enabledChr = true "
                        + "ORDER BY sv.servicevalueCod ");
                    query.setParameter("serviceCod", 17L);
                    query.setParameter("serviceValueCod",
                            sv2.getServicevalueCod().toString());
                    query.setParameter("clientCod",
                            userweb.getClient().getClientCod());
                    query.setParameter(
                            "classificationList",
                            getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                                    userweb));
                    List<ServiceValue> svMedicos = query.getResultList();

                    // Lista de medicos por cabecera de consultorio
                    List<MedicoCabData> medicosCabecera = new ArrayList<MedicoCabData>();
                    for (ServiceValue sv3 : svMedicos) {
                        MedicoCabData medicoCab = new MedicoCabData();
                        medicoCab.setId(sv3.getServicevalueCod());
                        medicoCab.setCodMedico(sv3.getColumn2Chr());
                        medicoCab.setLatitud("");
                        medicoCab.setLongitud("");
                        if (sv3.getMessage() != null) {
                            Message m = sv3.getMessage();
                            if (m.getLatitude() != null
                                && m.getLongitude() != null) {
                                medicoCab.setLatitud(m.getLatitude().toString());
                                medicoCab.setLongitud(m.getLongitude().toString());
                            } else if (m.getCell() != null) {
                                medicoCab.setLatitud(m.getCell().getLatitudeNum().toString());
                                medicoCab.setLongitud(m.getCell().getLongitudeNum().toString());
                            }
                        }

                        // Lista de detalles de medicos
                        List<MedicoDetData> medicosDet = new ArrayList<MedicoDetData>();
                        for (ServiceValueDetail svd2 : sv3.getServiceValueDetailCollection()) {
                            MedicoDetData medicoDet = new MedicoDetData();
                            medicoDet.setId(svd2.getServicevaluedetailCod());
                            medicoDet.setEvento(svd2.getColumn1Chr());
                            medicoDet.setCodMotivo(svd2.getColumn2Chr());
                            medicoDet.setObservacion(svd2.getColumn6Chr());
                            medicosDet.add(medicoDet);
                        }

                        // Lista de cabecera de productos
                        query = em.createQuery(""
                            + "SELECT DISTINCT sv "
                            + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                            + "WHERE sv.service.serviceCod = :serviceCod "
                            + "AND sv.column3Chr = 'PR' "
                            + "AND sv.column1Chr = :serviceValueCod "
                            + "AND sv.userphone.client.clientCod = :clientCod "
                            + "AND cl IN(:classificationList) "
                            + "AND sv.enabledChr = true "
                            + "AND svd.enabledChr = true "
                            + "ORDER BY sv.servicevalueCod ");
                        query.setParameter("serviceCod", 17L);
                        query.setParameter("serviceValueCod",
                                sv3.getServicevalueCod().toString());
                        query.setParameter("clientCod",
                                userweb.getClient().getClientCod());
                        query.setParameter(
                                "classificationList",
                                getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                                        userweb));
                        List<ServiceValue> svProductos = query.getResultList();

                        List<ProductoMedData> productosDet = new ArrayList<ProductoMedData>();
                        for (ServiceValue sv4 : svProductos) {
                            for (ServiceValueDetail svd3 : sv4.getServiceValueDetailCollection()) {
                                ProductoMedData producto = new ProductoMedData();
                                producto.setId(svd3.getServicevaluedetailCod());
                                producto.setCodProducto(svd3.getColumn1Chr());
                                producto.setCantidad(svd3.getColumn2Chr());
                                productosDet.add(producto);
                            }
                        }

                        medicoCab.setProductos(productosDet);
                        medicoCab.setDetalles(medicosDet);
                        medicosCabecera.add(medicoCab);
                    }

                    consultorioCab.setMedicos(medicosCabecera);

                    consultorios.add(consultorioCab);
                }
                visitadorCab.setConsultoriosCab(consultorios);
                visitasMed.add(visitadorCab);
            }
            result = getXStream().toXML(visitasMed);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de visitadores medicos. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public VisitadorMedicoResponse importVisitadoresMedicosComplex(long id, String user, String password, int limit) {
        VisitadorMedicoResponse resp = new VisitadorMedicoResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cabecera de la tabla
         * SERVICE_VALUE.
         */
        List<VisitadorMedicoCabData> visitadores = new ArrayList<VisitadorMedicoCabData>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "visitmedic", true);

            // 3) Obtención de datos de visitadores medicos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.column1Chr = 'MV' "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 17L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            for (ServiceValue sv : svList) {
                VisitadorMedicoCabData visitadorCab = new VisitadorMedicoCabData();
                visitadorCab.setId(sv.getServicevalueCod());
                visitadorCab.setFecha(sv.getMessage().getDateinDat());
                visitadorCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                visitadorCab.setUsuarioTelefono(sv.getUserphone().getNameChr());

                query = em.createQuery(""
                    + "SELECT DISTINCT sv "
                    + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                    + "WHERE sv.service.serviceCod = :serviceCod "
                    + "AND sv.column3Chr = 'CL' "
                    + "AND sv.column1Chr = :serviceValueCod "
                    + "AND sv.userphone.client.clientCod = :clientCod "
                    + "AND cl IN(:classificationList) "
                    + "AND sv.enabledChr = true "
                    + "AND svd.enabledChr = true "
                    + "ORDER BY sv.servicevalueCod ");
                query.setParameter("serviceCod", 17L);
                query.setParameter("serviceValueCod",
                        sv.getServicevalueCod().toString());
                query.setParameter("clientCod",
                        userweb.getClient().getClientCod());
                query.setParameter(
                        "classificationList",
                        getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                                userweb));
                List<ServiceValue> svConsultorios = query.getResultList();

                // Lista de cabeceras de consultorios por cabecera de visitador
                List<ConsultorioCabData> consultorios = new ArrayList<ConsultorioCabData>();
                for (ServiceValue sv2 : svConsultorios) {
                    ConsultorioCabData consultorioCab = new ConsultorioCabData();
                    consultorioCab.setId(sv2.getServicevalueCod());
                    consultorioCab.setFechaHora(sv2.getMessage().getDateinDat());
                    consultorioCab.setCodLocal(sv2.getColumn2Chr());
                    consultorioCab.setLatitud("");
                    consultorioCab.setLongitud("");
                    if (sv2.getMessage() != null) {
                        Message m = sv2.getMessage();
                        if (m.getLatitude() != null && m.getLongitude() != null) {
                            consultorioCab.setLatitud(m.getLatitude().toString());
                            consultorioCab.setLongitud(m.getLongitude().toString());
                        } else if (m.getCell() != null) {
                            consultorioCab.setLatitud(m.getCell().getLatitudeNum().toString());
                            consultorioCab.setLongitud(m.getCell().getLongitudeNum().toString());
                        }
                    }

                    // Lista de detalles de consultorio
                    List<ConsultorioDetData> consultorioDetalles = new ArrayList<ConsultorioDetData>();
                    for (ServiceValueDetail svd : sv2.getServiceValueDetailCollection()) {
                        ConsultorioDetData consultorioDet = new ConsultorioDetData();
                        consultorioDet.setId(svd.getServicevaluedetailCod());
                        consultorioDet.setEvento(svd.getColumn1Chr());
                        consultorioDet.setKmInicial(svd.getColumn2Chr());
                        consultorioDet.setKmFinal(svd.getColumn3Chr());
                        consultorioDet.setObservacion(svd.getColumn4Chr());
                        consultorioDet.setFechaHora(svd.getRecorddateDat());
                        consultorioDetalles.add(consultorioDet);
                    }
                    consultorioCab.setDetalles(consultorioDetalles);

                    query = em.createQuery(""
                        + "SELECT DISTINCT sv "
                        + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                        + "WHERE sv.service.serviceCod = :serviceCod "
                        + "AND sv.column3Chr = 'ME' "
                        + "AND sv.column1Chr = :serviceValueCod "
                        + "AND sv.userphone.client.clientCod = :clientCod "
                        + "AND cl IN(:classificationList) "
                        + "AND sv.enabledChr = true "
                        + "AND svd.enabledChr = true "
                        + "ORDER BY sv.servicevalueCod ");
                    query.setParameter("serviceCod", 17L);
                    query.setParameter("serviceValueCod",
                            sv2.getServicevalueCod().toString());
                    query.setParameter("clientCod",
                            userweb.getClient().getClientCod());
                    query.setParameter(
                            "classificationList",
                            getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                                    userweb));
                    List<ServiceValue> svMedicos = query.getResultList();

                    // Lista de medicos por cabecera de consultorio
                    List<MedicoCabData> medicosCabecera = new ArrayList<MedicoCabData>();
                    for (ServiceValue sv3 : svMedicos) {
                        MedicoCabData medicoCab = new MedicoCabData();
                        medicoCab.setId(sv3.getServicevalueCod());
                        medicoCab.setCodMedico(sv3.getColumn2Chr());
                        medicoCab.setLatitud("");
                        medicoCab.setLongitud("");
                        if (sv3.getMessage() != null) {
                            Message m = sv3.getMessage();
                            if (m.getLatitude() != null
                                && m.getLongitude() != null) {
                                medicoCab.setLatitud(m.getLatitude().toString());
                                medicoCab.setLongitud(m.getLongitude().toString());
                            } else if (m.getCell() != null) {
                                medicoCab.setLatitud(m.getCell().getLatitudeNum().toString());
                                medicoCab.setLongitud(m.getCell().getLongitudeNum().toString());
                            }
                        }

                        // Lista de detalles de medicos
                        List<MedicoDetData> medicosDet = new ArrayList<MedicoDetData>();
                        for (ServiceValueDetail svd2 : sv3.getServiceValueDetailCollection()) {
                            MedicoDetData medicoDet = new MedicoDetData();
                            medicoDet.setId(svd2.getServicevaluedetailCod());
                            medicoDet.setEvento(svd2.getColumn1Chr());
                            medicoDet.setCodMotivo(svd2.getColumn2Chr());
                            medicoDet.setObservacion(svd2.getColumn6Chr());
                            medicoDet.setFechaHora(svd2.getRecorddateDat());
                            medicosDet.add(medicoDet);
                        }

                        // Lista de cabecera de productos
                        query = em.createQuery(""
                            + "SELECT DISTINCT sv "
                            + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                            + "WHERE sv.service.serviceCod = :serviceCod "
                            + "AND sv.column3Chr = 'PR' "
                            + "AND sv.column1Chr = :serviceValueCod "
                            + "AND sv.userphone.client.clientCod = :clientCod "
                            + "AND cl IN(:classificationList) "
                            + "AND sv.enabledChr = true "
                            + "AND svd.enabledChr = true "
                            + "ORDER BY sv.servicevalueCod ");
                        query.setParameter("serviceCod", 17L);
                        query.setParameter("serviceValueCod",
                                sv3.getServicevalueCod().toString());
                        query.setParameter("clientCod",
                                userweb.getClient().getClientCod());
                        query.setParameter(
                                "classificationList",
                                getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                                        userweb));
                        List<ServiceValue> svProductos = query.getResultList();

                        List<ProductoMedData> productosDet = new ArrayList<ProductoMedData>();
                        for (ServiceValue sv4 : svProductos) {
                            for (ServiceValueDetail svd3 : sv4.getServiceValueDetailCollection()) {
                                ProductoMedData producto = new ProductoMedData();
                                producto.setId(svd3.getServicevaluedetailCod());
                                producto.setFechaHora(svd3.getRecorddateDat());
                                producto.setCodProducto(svd3.getColumn1Chr());
                                producto.setCantidad(svd3.getColumn2Chr());
                                productosDet.add(producto);
                            }
                        }

                        medicoCab.setProductos(productosDet);
                        medicoCab.setDetalles(medicosDet);
                        medicosCabecera.add(medicoCab);
                    }

                    consultorioCab.setMedicos(medicosCabecera);

                    consultorios.add(consultorioCab);
                }
                visitadorCab.setConsultoriosCab(consultorios);
                visitadores.add(visitadorCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(visitadores.toArray(new VisitadorMedicoCabData[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de visitadores medicos. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((VisitadorMedicoCabData[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((VisitadorMedicoCabData[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 18 = COURIER
    // -------------------------------------------------------------------------
    /**
     * Retorna todos los registros de courier realizados por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importCourier(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada cabecera de courier en la
         * tabla SERVICE_VALUE Los detalles del courier se obtienen de los
         * registros asociados en la tabla SERVICE_VALUE_DETAIL
         */
        String result = "";
        List<CourierCab> couriers = new ArrayList<CourierCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "courrier", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 18L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (ServiceValue sv : svList) {
                CourierCab courierCab = new CourierCab();
                courierCab.setId(sv.getServicevalueCod());
                courierCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                courierCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                courierCab.setFechaHora(sv.getMessage().getDateinDat());
                courierCab.setReceptor(sv.getColumn1Chr());
                courierCab.setObservacion(sv.getColumn2Chr());
                courierCab.setMotivo(sv.getColumn3Chr());
                Message m = sv.getMessage();
                if (m.getLatitude() != null && m.getLongitude() != null) {
                    courierCab.setLatitud(m.getLatitude().toString());
                    courierCab.setLongitud(m.getLongitude().toString());
                } else if (m.getCell() != null) {
                    courierCab.setLatitud(m.getCell().getLatitudeNum().toString());
                    courierCab.setLongitud(m.getCell().getLongitudeNum().toString());
                }

                List<CourierDet> detalles = new ArrayList<CourierDet>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    CourierDet courierDet = new CourierDet();
                    courierDet.setId(svd.getServicevaluedetailCod());
                    courierDet.setItem(svd.getColumn1Chr());
                    detalles.add(courierDet);
                }
                courierCab.setDetalles(detalles);
                couriers.add(courierCab);
            }

            result = getXStream().toXML(couriers);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de courier. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public CourierResponse importCourierComplex(long id, String user, String password, int limit) {
        CourierResponse resp = new CourierResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada cabecera de courier en la
         * tabla SERVICE_VALUE Los detalles del courier se obtienen de los
         * registros asociados en la tabla SERVICE_VALUE_DETAIL
         */
        List<CourierCab> couriers = new ArrayList<CourierCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "courrier", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 18L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (ServiceValue sv : svList) {
                CourierCab courierCab = new CourierCab();
                courierCab.setId(sv.getServicevalueCod());
                courierCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                courierCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                courierCab.setFechaHora(sv.getMessage().getDateinDat());
                courierCab.setReceptor(sv.getColumn1Chr());
                courierCab.setObservacion(sv.getColumn2Chr());
                courierCab.setMotivo(sv.getColumn3Chr());
                Message m = sv.getMessage();
                if (m.getLatitude() != null && m.getLongitude() != null) {
                    courierCab.setLatitud(m.getLatitude().toString());
                    courierCab.setLongitud(m.getLongitude().toString());
                } else if (m.getCell() != null) {
                    courierCab.setLatitud(m.getCell().getLatitudeNum().toString());
                    courierCab.setLongitud(m.getCell().getLongitudeNum().toString());
                }

                List<CourierDet> detalles = new ArrayList<CourierDet>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    CourierDet courierDet = new CourierDet();
                    courierDet.setId(svd.getServicevaluedetailCod());
                    courierDet.setItem(svd.getColumn1Chr());
                    detalles.add(courierDet);
                }
                courierCab.setDetalles(detalles);
                couriers.add(courierCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(couriers.toArray(new CourierCab[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de courrier. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((CourierCab[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((CourierCab[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 16 = OT
    // -------------------------------------------------------------------------
    /**
     * Retorna todos los registros de courier realizados por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importOT(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada OT de courier en la tabla
         * SERVICE_VALUE Los detalles del OT se obtienen de los registros
         * asociados en la tabla SERVICE_VALUE_DETAIL
         */
        String result = "";
        List<OTCab> ots = new ArrayList<OTCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "ot", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 16L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (ServiceValue sv : svList) {
                OTCab otCab = new OTCab();
                otCab.setId(sv.getServicevalueCod());
                otCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                otCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                otCab.setOtCode(sv.getColumn1Chr());
                otCab.setActivityCod(sv.getColumn2Chr());
                otCab.setClientCod(sv.getColumn3Chr());
                otCab.setZoneCod(sv.getColumn4Chr());
                otCab.setCreatedDate(sv.getColumn5Chr());
                otCab.setAssignedDate(sv.getColumn6Chr());
                otCab.setStatusCod(sv.getColumn7Chr());
                otCab.setEndDate(sv.getColumn8Chr());
                otCab.setClientCod(sv.getColumn9Chr());
                otCab.setEndDate(sv.getColumn10Chr());

                List<OTDet> detalles = new ArrayList<OTDet>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    OTDet otDet = new OTDet();
                    otDet.setId(svd.getServicevaluedetailCod());
                    otDet.setEvento(svd.getColumn1Chr());
                    otDet.setObservacion(svd.getColumn2Chr());
                    detalles.add(otDet);
                }
                otCab.setDetalles(detalles);
                ots.add(otCab);
            }

            result = getXStream().toXML(ots);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de OT. User: " + user + ". From id: "
                        + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public OTResponse importOTComplex(long id, String user, String password, int limit) {
        OTResponse resp = new OTResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada cabecera de OT en la tabla
         * SERVICE_VALUE Los detalles del OT se obtienen de los registros
         * asociados en la tabla SERVICE_VALUE_DETAIL
         */
        List<OTCab> ots = new ArrayList<OTCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "ot", true);

            // 3) Obtención de datos
            Query query = em.createQuery(""
                + "SELECT DISTINCT sv "
                + "FROM ServiceValue sv, IN(sv.userphone.classificationList) cl, IN(sv.serviceValueDetailCollection) svd "
                + "WHERE sv.service.serviceCod = :serviceCod "
                + "AND sv.servicevalueCod > :fromId "
                + "AND sv.userphone.client.clientCod = :clientCod "
                + "AND cl IN(:classificationList) "
                + "AND sv.enabledChr = true " + "AND svd.enabledChr = true "
                + "ORDER BY sv.servicevalueCod ");
            query.setParameter("serviceCod", 16L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<ServiceValue> svList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (ServiceValue sv : svList) {
                OTCab otCab = new OTCab();
                otCab.setId(sv.getServicevalueCod());
                otCab.setNroTelefono(sv.getUserphone().getCellphoneNum().toString());
                otCab.setUsuarioTelefono(sv.getUserphone().getNameChr());
                otCab.setOtCode(sv.getColumn1Chr());
                otCab.setActivityCod(sv.getColumn2Chr());
                otCab.setClientCod(sv.getColumn3Chr());
                otCab.setZoneCod(sv.getColumn4Chr());
                otCab.setCreatedDate(sv.getColumn5Chr());
                otCab.setAssignedDate(sv.getColumn6Chr());
                otCab.setStatusCod(sv.getColumn7Chr());
                otCab.setEndDate(sv.getColumn8Chr());
                otCab.setClientCod(sv.getColumn9Chr());
                otCab.setEndDate(sv.getColumn10Chr());

                List<OTDet> detalles = new ArrayList<OTDet>();
                for (ServiceValueDetail svd : sv.getServiceValueDetailCollection()) {
                    OTDet otDet = new OTDet();
                    otDet.setId(svd.getServicevaluedetailCod());
                    otDet.setEvento(svd.getColumn1Chr());
                    otDet.setObservacion(svd.getColumn2Chr());
                    detalles.add(otDet);
                }
                otCab.setDetalles(detalles);
                ots.add(otCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(ots.toArray(new OTCab[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de OT. User: " + user + ". From id: "
                        + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((OTCab[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((OTCab[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 100 = FEATURE
    // -------------------------------------------------------------------------
    /**
     * Retorna todos los registros de courier realizados por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importFeature(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada respuesta ingresada por un
         * usuario en el servicio de formulario dinamico
         */
        String result = "";
        List<FormularioDinamicoCab> dynamicForms = new ArrayList<FormularioDinamicoCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "feature", null);

            // 3) Obtención de datos
            Query query = em.createQuery("SELECT DISTINCT fe.featureValueList FROM FeatureElement fe , IN (fe.featureValueList) fv,"
                + " IN (fv.featureValueData) fvd,"
                + " IN (fe.classifications) cl"
                + " LEFT JOIN fe.userphones LEFT JOIN  fe.phoneLists"
                + " WHERE fe.clientFeature.feature.featureCode = :serviceCod"
                + " AND fv.featureValueCod > :fromId"
                + " AND  fe.clientFeature.client.clientCod = :clientCod"
                + " AND cl IN(:classificationList)");
            query.setParameter("serviceCod", 1L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<FeatureValue> fvList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (FeatureValue fv : fvList) {
                FormularioDinamicoCab dynamicFormCab = new FormularioDinamicoCab();
                dynamicFormCab.setId(fv.getFeatureValueCod());
                dynamicFormCab.setNroTelefono(fv.getUserphone() != null ? fv.getUserphone().getCellphoneNum().toString() : fv.getPhone() != null ? fv.getPhone().getCellphoneNum().toString() : fv.getMessage().getOriginChr());
                dynamicFormCab.setUsuarioTelefono(fv.getUserphone() != null ? fv.getUserphone().getNameChr() : fv.getPhone() != null ? fv.getPhone().getNameChr() : "");
                dynamicFormCab.setDescripcion(fv.getFeatureElement().getDescriptionChr());
                dynamicFormCab.setFechaRespuesta(sqlSdf.format(fv.getMessage().getDateinDat()));

                dynamicFormCab.setLatitud("");
                dynamicFormCab.setLongitud("");
                if (fv.getMessage() != null) {
                    Message m = fv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        dynamicFormCab.setLatitud(m.getLatitude().toString());
                        dynamicFormCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        dynamicFormCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        dynamicFormCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<FormularioDinamicoDet> detalles = new ArrayList<FormularioDinamicoDet>();
                for (FeatureValueData fvd : fv.getFeatureValueData()) {
                    FormularioDinamicoDet dynamicFormDet = new FormularioDinamicoDet();
                    dynamicFormDet.setId(fvd.getFeatureValueDataCod());
                    dynamicFormDet.setPregunta(fvd.getFeatureElementEntry().getTitleChr());
                    dynamicFormDet.setRespuesta(fvd.getDataChr());
                    detalles.add(dynamicFormDet);
                }
                dynamicFormCab.setDetalles(detalles);
                dynamicForms.add(dynamicFormCab);
            }

            result = getXStream().toXML(dynamicForms);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de Features. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public FormularioDinamicoResponse importFeatureComplex(long id, String user, String password, int limit) {
        FormularioDinamicoResponse resp = new FormularioDinamicoResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada respuesta ingresada por un
         * usuario en el servicio de formulario dinamico
         */
        List<FormularioDinamicoCab> dynamicForms = new ArrayList<FormularioDinamicoCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "feature", null);

            // 3) Obtención de datos
            Query query = em.createQuery("SELECT DISTINCT fe.featureValueList FROM FeatureElement fe , IN (fe.featureValueList) fv,"
                + " IN (fv.featureValueData) fvd,"
                + " IN (fe.classifications) cl"
                + " LEFT JOIN fe.userphones LEFT JOIN  fe.phoneLists"
                + " WHERE fe.clientFeature.feature.featureCode = :serviceCod"
                + " AND fv.featureValueCod > :fromId"
                + " AND  fe.clientFeature.client.clientCod = :clientCod"
                + " AND cl IN(:classificationList)");

            query.setParameter("serviceCod", 1L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<FeatureValue> fvList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (FeatureValue fv : fvList) {
                FormularioDinamicoCab dynamicFormCab = new FormularioDinamicoCab();
                dynamicFormCab.setId(fv.getFeatureValueCod());
                dynamicFormCab.setNroTelefono(fv.getUserphone() != null ? fv.getUserphone().getCellphoneNum().toString() : fv.getPhone() != null ? fv.getPhone().getCellphoneNum().toString() : fv.getMessage().getOriginChr());
                dynamicFormCab.setUsuarioTelefono(fv.getUserphone() != null ? fv.getUserphone().getNameChr() : fv.getPhone() != null ? fv.getPhone().getNameChr() : "");
                dynamicFormCab.setDescripcion(fv.getFeatureElement().getDescriptionChr());
                dynamicFormCab.setFechaRespuesta(sqlSdf.format(fv.getMessage().getDateinDat()));

                dynamicFormCab.setLatitud("");
                dynamicFormCab.setLongitud("");
                if (fv.getMessage() != null) {
                    Message m = fv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        dynamicFormCab.setLatitud(m.getLatitude().toString());
                        dynamicFormCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        dynamicFormCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        dynamicFormCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<FormularioDinamicoDet> detalles = new ArrayList<FormularioDinamicoDet>();
                for (FeatureValueData fvd : fv.getFeatureValueData()) {
                    FormularioDinamicoDet dynamicFormDet = new FormularioDinamicoDet();
                    dynamicFormDet.setId(fvd.getFeatureValueDataCod());
                    dynamicFormDet.setPregunta(fvd.getFeatureElementEntry().getTitleChr());
                    dynamicFormDet.setRespuesta(fvd.getDataChr());
                    detalles.add(dynamicFormDet);
                }
                dynamicFormCab.setDetalles(detalles);
                dynamicForms.add(dynamicFormCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(dynamicForms.toArray(new FormularioDinamicoCab[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de Features. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((FormularioDinamicoCab[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((FormularioDinamicoCab[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // SERVICIO 20 = DYNAMIC FORM
    // -------------------------------------------------------------------------
    /**
     * Retorna todos los registros de courier realizados por los usuarios
     * telefónicos de un cliente.
     * 
     * @param id
     *            Id del último registro leído. Se filtrarán los registros de
     *            inserción posterior a este id.
     * @param user
     *            Usuario que ejecuta el método.
     * @param password
     *            Contraseña para autenticar el usuario.
     * @param limit
     *            Cantidad máxima de registros a retornar.
     * @return String XML Serializado que representa un dato complejo. En caso
     *         de error. La palabra clave <CODE>ERROR</CODE> antecede al mensaje
     *         de error.
     * 
     */
    public String importDynamicForm(long id, String user, String password, int limit) {
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada respuesta ingresada por un
         * usuario en el servicio de formulario dinamico
         */
        String result = "";
        List<FormularioDinamicoCab> dynamicForms = new ArrayList<FormularioDinamicoCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "dynamicform", null);

            // 3) Obtención de datos
            Query query = em.createQuery("SELECT DISTINCT fv"
                + " FROM FeatureValue fv, "
                + " IN (fv.featureElement.classifications) cl,"
                + " IN (fv.featureValueData) fvd "
                + " WHERE fv.featureElement.clientFeature.feature.featureCode = :serviceCod "
                + " AND fv.featureValueCod > :fromId"
                + " AND fv.featureElement.clientFeature.client.clientCod = :clientCod"
                + " AND cl IN(:classificationList)");
            query.setParameter("serviceCod", 20L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<FeatureValue> fvList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (FeatureValue fv : fvList) {
                FormularioDinamicoCab dynamicFormCab = new FormularioDinamicoCab();
                dynamicFormCab.setId(fv.getFeatureValueCod());
                dynamicFormCab.setNroTelefono(fv.getUserphone().getCellphoneNum().toString());
                dynamicFormCab.setUsuarioTelefono(fv.getUserphone().getNameChr());
                dynamicFormCab.setDescripcion(fv.getFeatureElement().getDescriptionChr());
                dynamicFormCab.setFechaRespuesta(sqlSdf.format(fv.getMessage().getDateinDat()));

                dynamicFormCab.setLatitud("");
                dynamicFormCab.setLongitud("");
                if (fv.getMessage() != null) {
                    Message m = fv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        dynamicFormCab.setLatitud(m.getLatitude().toString());
                        dynamicFormCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        dynamicFormCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        dynamicFormCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<FormularioDinamicoDet> detalles = new ArrayList<FormularioDinamicoDet>();
                for (FeatureValueData fvd : fv.getFeatureValueData()) {
                    FormularioDinamicoDet dynamicFormDet = new FormularioDinamicoDet();
                    dynamicFormDet.setId(fvd.getFeatureValueDataCod());
                    dynamicFormDet.setPregunta(fvd.getFeatureElementEntry().getTitleChr());
                    dynamicFormDet.setRespuesta(fvd.getDataChr());
                    detalles.add(dynamicFormDet);
                }
                dynamicFormCab.setDetalles(detalles);
                dynamicForms.add(dynamicFormCab);
            }

            result = getXStream().toXML(dynamicForms);
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de Formulario Dinamico. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD + " - " + ex.getMessage();
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return DataConf.ERROR_KEY_WORD
                + getFacadeContainer().getI18nAPI().iValue(
                        "ejb.ws.data.UnexpectedError");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return result;
    }

    public FormularioDinamicoResponse importDynamicFormComplex(long id, String user, String password, int limit) {
        FormularioDinamicoResponse resp = new FormularioDinamicoResponse();
        /*
         * guia p/ el desarrollador ************************
         * 
         * Este método devuelve un registro por cada respuesta ingresada por un
         * usuario en el servicio de formulario dinamico
         */
        List<FormularioDinamicoCab> dynamicForms = new ArrayList<FormularioDinamicoCab>();
        Userweb userweb;
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            // 1) Autenticación
            userweb = validateUser(user, password);

            // 2) Validacion de limit
            limit = Math.min(limit, getMaxLimit());

            // 2.1) Validacion de tiempo
            validateAccess(id, userweb, "dynamicform", null);

            // 3) Obtención de datos
            Query query = em.createQuery("SELECT DISTINCT fv"
                + " FROM FeatureValue fv, "
                + " IN (fv.featureElement.classifications) cl,"
                + " IN (fv.featureValueData) fvd "
                + " WHERE fv.featureElement.clientFeature.feature.featureCode = :serviceCod "
                + " AND fv.featureValueCod > :fromId"
                + " AND fv.featureElement.clientFeature.client.clientCod = :clientCod"
                + " AND cl IN(:classificationList)");
            query.setParameter("serviceCod", 20L);
            query.setParameter("fromId", id);
            query.setParameter("clientCod", userweb.getClient().getClientCod());
            query.setParameter(
                    "classificationList",
                    getFacadeContainer().getClassificationAPI().findByUserwebWithChilds(
                            userweb));
            query.setMaxResults(limit);
            List<FeatureValue> fvList = query.getResultList();

            /*
             * 4) Fetch de los resultados de la cabecera y almacenamiento en el
             * bean, fetch de los detalles a su vez.
             */
            for (FeatureValue fv : fvList) {
                FormularioDinamicoCab dynamicFormCab = new FormularioDinamicoCab();
                dynamicFormCab.setId(fv.getFeatureValueCod());
                dynamicFormCab.setNroTelefono(fv.getUserphone().getCellphoneNum().toString());
                dynamicFormCab.setUsuarioTelefono(fv.getUserphone().getNameChr());
                dynamicFormCab.setDescripcion(fv.getFeatureElement().getDescriptionChr());
                dynamicFormCab.setFechaRespuesta(sqlSdf.format(fv.getFeatureElement().getStartPeriodDat()));

                dynamicFormCab.setLatitud("");
                dynamicFormCab.setLongitud("");
                if (fv.getMessage() != null) {
                    Message m = fv.getMessage();
                    if (m.getLatitude() != null && m.getLongitude() != null) {
                        dynamicFormCab.setLatitud(m.getLatitude().toString());
                        dynamicFormCab.setLongitud(m.getLongitude().toString());
                    } else if (m.getCell() != null) {
                        dynamicFormCab.setLatitud(m.getCell().getLatitudeNum().toString());
                        dynamicFormCab.setLongitud(m.getCell().getLongitudeNum().toString());
                    }
                }

                List<FormularioDinamicoDet> detalles = new ArrayList<FormularioDinamicoDet>();
                for (FeatureValueData fvd : fv.getFeatureValueData()) {
                    FormularioDinamicoDet dynamicFormDet = new FormularioDinamicoDet();
                    dynamicFormDet.setId(fvd.getFeatureValueDataCod());
                    dynamicFormDet.setPregunta(fvd.getFeatureElementEntry().getTitleChr());
                    dynamicFormDet.setRespuesta(fvd.getDataChr());
                    detalles.add(dynamicFormDet);
                }
                dynamicFormCab.setDetalles(detalles);
                dynamicForms.add(dynamicFormCab);
            }

            resp.setCode(WSOperation.SUCCESS.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    WSOperation.SUCCESS.getMessage()));
            resp.setResults(dynamicForms.toArray(new FormularioDinamicoCab[0]));
            getFacadeContainer().getNotifier().signal(
                    DataMigration.class,
                    Action.INFO,
                    "Exportando datos de Formulario Dinamico. User: " + user
                        + ". From id: " + id + ". Limit: " + limit);
            return resp;
        } catch (AuthenticationException ex) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.WARNING, ex.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(ex.getMessage());
            resp.setResults((FormularioDinamicoCab[]) null);
            return resp;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            resp.setCode(WSOperation.ERROR.code());
            resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                    "ejb.ws.data.UnexpectedError"));
            resp.setResults((FormularioDinamicoCab[]) null);
            return resp;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
