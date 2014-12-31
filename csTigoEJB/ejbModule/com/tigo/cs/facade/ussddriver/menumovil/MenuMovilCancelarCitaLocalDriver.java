package com.tigo.cs.facade.ussddriver.menumovil;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.gov.ips.consultas.servicios.RetornoCancelacionesMedicas;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.facade.ussddriver.FeatureEntryDynamicDriver;

@Stateless
public class MenuMovilCancelarCitaLocalDriver {

    @EJB
    private FacadeContainer facadeContainer;
    private HashMap<String, HashMap<String, String>> nodes;

    private static final String CEDULA = "CEDULA";
    private static final String CITAS = "CITAS";
    private static final String CANCELAR = "CANCELAR";
    private static final String OPCION = "OPCION";

    public String execute(final String msisdn, HashMap<String, HashMap<String, String>> nodes) {

        try {
            setNodes(nodes);
            facadeContainer.getNotifier().info(FeatureEntryDynamicDriver.class,
                    msisdn, "Se validara la seleccion del asegurado");

            final String document = getNodeValue(CEDULA);
            final Long token = facadeContainer.getIpsServiceAPI().getLastToken(
                    msisdn);

            final RetornoCancelacionesMedicas cancelacion = facadeContainer.getIpsServiceAPI().getLastCancelCita(
                    msisdn);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

            Thread thread = new Thread() {

                @Override
                public void run() {
                    try{
                        facadeContainer.getIpsServiceAPI().cancelAppointments(
                                document, msisdn, token,
                                cancelacion.getCodEmpresa(),
                                cancelacion.getNroAgend(),
                                cancelacion.getNroRegAmb());
                    } catch (InvalidOperationException e) {
                        facadeContainer.getNotifier().error(getClass(), null,
                                e.getMessage(), e);
                        e.printStackTrace();
                    }
                };
            };

            thread.start();

            String message = "Gracias por utilizar el servicio, en breve recibira un SMS de confirmacion o puede verificar en la opcion 3 de *152#";

            return message;

        } catch (Exception e) {

            facadeContainer.getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            return "Ha ocurrido un error en el procesamiento de la cancelacion";
        }

    }

    public HashMap<String, HashMap<String, String>> getNodes() {
        return nodes;
    }

    public void setNodes(HashMap<String, HashMap<String, String>> nodes) {
        this.nodes = nodes;
    }

    protected String getNodeValue(String nodeCode) {
        if (nodes.get(nodeCode) != null) {
            return nodes.get(nodeCode).get("Value").trim();
        }
        return null;
    }

    protected String getNotNullNodeValueFrom(String... nodeCode) {
        if (nodeCode.length > 0) {
            for (int i = 0; i < nodeCode.length; i++) {
                String node = nodeCode[i];
                if (nodes.get(node) != null) {
                    return nodes.get(node).get("Value").trim();
                }
            }
        }
        return null;
    }
}
