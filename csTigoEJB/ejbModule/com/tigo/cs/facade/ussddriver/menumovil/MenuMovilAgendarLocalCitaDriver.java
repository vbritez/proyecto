package com.tigo.cs.facade.ussddriver.menumovil;

import java.util.HashMap;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.gov.ips.consultas.servicios.Menu;
import py.gov.ips.consultas.servicios.RetornoAgendamientoMedico;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;

@Stateless
public class MenuMovilAgendarLocalCitaDriver {

    @EJB
    private FacadeContainer facadeContainer;
    private HashMap<String, HashMap<String, String>> nodes;

    private static final String CEDULA = "CEDULA";
    private static final String TURNO = "TURNO";

    public String execute(final String msisdn, HashMap<String, HashMap<String, String>> nodes) {

        facadeContainer.getNotifier().info(getClass(), msisdn,
                "IPS - Ingresa al driver de agendamiento");

        setNodes(nodes);
        final String document = getNodeValue(CEDULA);
        final Long token = facadeContainer.getIpsServiceAPI().getLastToken(
                msisdn);

        Thread thread = new Thread() {

            @Override
            public void run() {

                Set<String> keys = getNodes().keySet();

                int maxTurno = 0;
                for (String nodeKey : keys) {
                    if (nodeKey.contains(TURNO)) {
                        int currentValue = Integer.parseInt(nodeKey.replaceFirst(
                                TURNO, ""));
                        if (currentValue > maxTurno) {
                            maxTurno = currentValue;
                        }
                    }
                }
                String value = getNodeValue(TURNO + maxTurno);

                Menu lastSpecialty = facadeContainer.getIpsServiceAPI().getLastSpecialty(
                        msisdn);

                Menu lastSpecialtyType = facadeContainer.getIpsServiceAPI().getLastSpecialtyType(
                        msisdn);
                Menu zona = facadeContainer.getIpsServiceAPI().getLastZone(
                        msisdn);
                final Menu medicCenter = facadeContainer.getIpsServiceAPI().getLastMedicCenter(
                        msisdn);

                final Menu horario = facadeContainer.getIpsServiceAPI().getLastHorarioMedicCenter(
                        msisdn);
                String turnoString = null;
                if (horario != null) {
                    turnoString = horario.getCodRetorno();
                }

                final Integer specialtyCod = lastSpecialtyType != null ? Integer.parseInt(lastSpecialtyType.getCodRetorno()) : Integer.parseInt(lastSpecialty.getCodRetorno());

                RetornoAgendamientoMedico medic;
                try {
                    medic = facadeContainer.getIpsServiceAPI().findMedic(
                            document, msisdn, token,
                            Integer.parseInt(medicCenter.getCodRetorno()),
                            specialtyCod, turnoString);

                    int posicion = Integer.parseInt(value);

                    final Menu turno = medic.getListaMenu()[posicion - 1];

                    facadeContainer.getIpsServiceAPI().scheduleAppointments(
                            document, msisdn, token,
                            Integer.parseInt(medicCenter.getCodRetorno()),
                            specialtyCod, turno.getCodRetorno());
                } catch (NumberFormatException e) {
                    facadeContainer.getNotifier().info(getClass(), msisdn,
                            "IPS - Error invocando al WS de IPS en el hilo de ejecucion. ");
                } catch (InvalidOperationException e) {
                    facadeContainer.getNotifier().info(getClass(), msisdn,
                            "IPS - Error invocando al WS de IPS en el hilo de ejecucion. ");
                }
            };
        };

        thread.start();

        String message = "Gracias por utilizar el servicio, en breve recibira un SMS de confirmacion o puede verificar en la opcion 3 de *152#";

        return message;
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
