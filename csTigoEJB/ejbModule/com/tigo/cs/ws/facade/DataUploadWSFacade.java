/**
 * 
 */
package com.tigo.cs.ws.facade;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.MetaData;
import com.tigo.cs.domain.MetaDataPK;
import com.tigo.cs.domain.Userweb;
import com.tigo.cs.domain.view.Data;
import com.tigo.cs.domain.view.DataArpTipoFactura;
import com.tigo.cs.domain.view.DataBank;
import com.tigo.cs.domain.view.DataClient;
import com.tigo.cs.domain.view.DataDeliveryman;
import com.tigo.cs.domain.view.DataDeposit;
import com.tigo.cs.domain.view.DataEmployee;
import com.tigo.cs.domain.view.DataGuard;
import com.tigo.cs.domain.view.DataMotive;
import com.tigo.cs.domain.view.DataProduct;
import com.tigo.cs.domain.view.DataVehicle;

/**
 * 
 * Fachada EJB con business methods para las operaciones del Web Service de
 * carga de datos (meta-datos) de los Clientes Corporativos.
 * 
 * @author Miguel Zorrilla
 * @since CS Fase 7
 * 
 */
@Stateless
public class DataUploadWSFacade extends WSBase {

    @EJB
    private FacadeContainer ussdFacadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return ussdFacadeContainer;
    }

    /**
     * 
     */
    public DataUploadWSFacade() {
    }

    /**
     * Operaci�n para la carga de meta-datos de clientes.
     * 
     * @param user
     *            Usuario web del cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param clients
     *            Lista de Clientes. Un {@link DataClient} representa el
     *            cliente.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     * */
    public DataUploadResponse uploadDataClients(String user, String password, List<DataClient> clients) {
        return genericDataUpload(user, password, clients, 1L);
    }

    /**
     * Operaci�n para la carga de meta-datos de productos.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param products
     *            Lista de Productos. Un {@link DataProduct} representa el
     *            producto.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     * */
    public DataUploadResponse uploadDataProducts(String user, String password, List<DataProduct> products) {
        return genericDataUpload(user, password, products, 2L);
    }

    /**
     * Operaci�n para la carga de meta-datos de motivos.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param motives
     *            Lista de Motivos. Un {@link DataMotive} representa el motivo.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     * */
    public DataUploadResponse uploadDataMotives(String user, String password, List<DataMotive> motives) {
        return genericDataUpload(user, password, motives, 3L);
    }

    /**
     * Operaci�n para la carga de meta-datos de guardias.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param guards
     *            Lista de Guardias. Un {@link DataGuard} representa el guardia.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     * */
    public DataUploadResponse uploadDataGuards(String user, String password, List<DataGuard> guards) {
        return genericDataUpload(user, password, guards, 4L);
    }

    /**
     * Operaci�n para la carga de meta-datos de repartidores.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param deliverymans
     *            Lista de Repartidores. Un {@link DataDeliveryman} representa
     *            el repartidor.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     * */
    public DataUploadResponse uploadDataDeliverymans(String user, String password, List<DataDeliveryman> deliverymans) {
        return genericDataUpload(user, password, deliverymans, 5L);
    }

    /**
     * Operaci�n para la carga de meta-datos de tipo de facturas de ARP.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param tipofacturas
     *            Lista de Tipos de Facturas de ARP. Un
     *            {@link DataArpTipoFactura} representa el tipo de factura.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     * */
    public DataUploadResponse uploadDataARPTipoFacturas(String user, String password, List<DataArpTipoFactura> tipofacturas) {
        return genericDataUpload(user, password, tipofacturas, 6L);
    }

    /**
     * Operaci�n para la carga de meta-datos de empleados.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param employees
     *            Lista de Empleados. Un {@link DataEmployee} representa el
     *            empleado.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     **/
    public DataUploadResponse uploadDataEmployees(String user, String password, List<DataEmployee> employees) {
        return genericDataUpload(user, password, employees, 7L);
    }

    /**
     * Operaci�n para la carga de meta-datos de veh�culos.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param vehicles
     *            Lista de Vehiculos. Un {@link DataVehicle} representa el
     *            veh�culo.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     **/
    public DataUploadResponse uploadDataVehicles(String user, String password, List<DataVehicle> vehicles) {
        return genericDataUpload(user, password, vehicles, 8L);
    }

    /**
     * Operaci�n para la carga de meta-datos de bancos.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param bancos
     *            Lista de Bancos. Un {@link DataBanco} representa el banco.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     **/
    public DataUploadResponse uploadDataBanks(String user, String password, List<DataBank> bancos) {
        return genericDataUpload(user, password, bancos, 9L);
    }

    /**
     * Operaci�n para la carga de meta-datos de dep�sitos.
     * 
     * @param user
     *            Usuario web del Cliente.
     * @param password
     *            Contrase�a del usuario web.
     * @param deposits
     *            Lista de Dep�sitos. Un {@link DataDeposit} representa el
     *            deposito.
     * @return {@link DataUploadResponse} que contiene informaciones de acuerdo
     *         al caso de �xito, que pueden ser: <br>
     *         0 - Success: Si todos los meta-datos pudieron ser persistidos. <br>
     *         10 - Partial Success: En caso de haya podido persistirse algunos
     *         de los meta-datos. <br>
     *         100 - Error: En caso de no haya podido persistirse ning�n
     *         meta-dato.
     * 
     **/
    public DataUploadResponse uploadDataDeposits(String user, String password, List<DataDeposit> deposits) {
        return genericDataUpload(user, password, deposits, 10L);
    }

    private DataUploadResponse genericDataUpload(String user, String password, List<? extends Data> datas, Long codMeta) {
        List<String> keys = new ArrayList<String>();
        // Meta meta = metaFacade.find(cod_meta, "metaMemberList");

        // Validaci�n de usuario
        Userweb userweb;
        try {
            userweb = validateUser(user, password);
        } catch (AuthenticationException e) {
            getFacadeContainer().getNotifier().signal(DataMigration.class,
                    Action.ERROR, e.getMessage() + " User: " + user);
            return responseError(datas, e.getMessage());
        }

        // Para cada cliente de la lista
        for (Data data : datas) {
            boolean onePersisted = false;

            Set<Integer> memberOrders = data.getMembers().keySet();
            // Para cada meta-member del meta
            for (Integer memberOrder : memberOrders) {
                String value = getMemberValue(data,
                        data.getMembers().get(memberOrder));

                // Solo si el valor no es nulo, se intenta persistir.
                if (value != null) {
                    MetaData md = new MetaData();
                    MetaDataPK mdPK = new MetaDataPK(userweb.getClient().getClientCod(), codMeta, memberOrder.longValue(), data.getCode());
                    md.setMetaDataPK(mdPK);
                    md.setValueChr(value);
                    try {
                        getFacadeContainer().getMetaDataAPI().edit(md);
                        onePersisted = true;
                    } catch (Exception e) {
                        // Do nothing, just log
                        getFacadeContainer().getNotifier().signal(
                                DataUploadWSFacade.class, Action.WARNING,
                                e.getMessage());
                        /*
                         * Throwable cause = e.getCause(); while
                         * (cause.getCause() != null) { cause =
                         * cause.getCause(); }
                         * 
                         * if (cause instanceof BatchUpdateException){ if
                         * (cause.getMessage().contains("ORA-00001")){ try {
                         * 
                         * } catch (Exception e2) { } } }
                         * System.out.println(cause.getMessage());
                         */
                    }
                }
            }
            if (!onePersisted) {
                getFacadeContainer().getNotifier().signal(
                        DataUploadWSFacade.class,
                        Action.WARNING,
                        "Error al persistir meta-data durante el upload de datos. Clave duplicada o valores nulos para el registro con clave: "
                            + data.getCode());
                keys.add(data.getCode());
            }
        }
        if (!keys.isEmpty()) {
            return responsePartialSuccess(keys);
        }

        return responseSuccess();
    }

    /**
     * Obtiene el valor para cada meta-member asociado al Meta por Generics.
     * 
     * @return El valor del meta-member del meta en cuestion. Retorna
     *         <code>null</code> en caso de que el m�todo devuelva un empty
     *         string, nulo o haya ocurrido alg�n error.
     * */
    private String getMemberValue(Object o, String methodName) {
        String memberValue = null;
        try {
            String capitalMethodName = methodName.substring(0, 1).toUpperCase()
                + methodName.substring(1);
            Method method = o.getClass().getMethod("get" + capitalMethodName,
                    (Class<?>[]) null);
            Object value = method.invoke(o, (Object[]) null);
            if (value != null) {
                memberValue = method.invoke(o, (Object[]) null).toString();
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().signal(
                    DataUploadWSFacade.class,
                    Action.ERROR,
                    "Error al obtener valor del meta-member durante el upload de datos.",
                    e);
            memberValue = null;
        }
        return memberValue;
    }

    /**
     * Obtiene todos los keys de una lista de meta-datos.
     * 
     * @return Lista de keys (pks) de una lista de meta-datos.
     * @param datas
     *            una lista de datas, potencialmente meta-datos recibidos por el
     *            Cliente por el upload de datos.
     * */
    protected List<String> getAllKeys(List<? extends Data> datas) {
        List<String> keys = new ArrayList<String>();
        for (Data data : datas) {
            keys.add(data.getCode());
        }
        return keys;
    }

    private DataUploadResponse responseError(List<? extends Data> datas, String detailMessage) {
        DataUploadResponse resp = new DataUploadResponse();
        resp.setCode(UploadResponseOperations.ERROR.getCode());
        resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                UploadResponseOperations.ERROR.getMessage())
            + ". " + detailMessage);
        resp.setDataKeys(getAllKeys(datas).toArray(new String[0]));
        return resp;
    }

    private DataUploadResponse responsePartialSuccess(List<String> keys) {
        DataUploadResponse resp = new DataUploadResponse();
        resp.setCode(UploadResponseOperations.PARTIAL_SUCCESS.getCode());
        resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                UploadResponseOperations.PARTIAL_SUCCESS.getMessage()));
        resp.setDataKeys(keys.toArray(new String[0]));
        return resp;
    }

    private DataUploadResponse responseSuccess() {
        DataUploadResponse resp = new DataUploadResponse();
        resp.setCode(UploadResponseOperations.SUCCESS.getCode());
        resp.setDescription(getFacadeContainer().getI18nAPI().iValue(
                UploadResponseOperations.SUCCESS.getMessage()));
        resp.setDataKeys((String[]) null);
        return resp;
    }

    public static void main(String args[]) {
        DataVehicle dc = new DataVehicle();

        Map<Integer, String> members = dc.getMembers();
        Set<Entry<Integer, String>> entrySet = members.entrySet();
        for (Entry<Integer, String> entry : entrySet) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
}
