package com.tigo.cs.facade.ussddriver.menumovil;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.response.VirtualEntryResponse;
import py.com.lothar.ussddriverinterfaces.virtual.VirtualEntryDriverInterface;
import py.gov.ips.consultas.servicios.RetornoCancelacionesMedicas;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.util.StringUtils;

@Stateless
public class MenuMovilCancelarCitaVirtualDriver implements VirtualEntryDriverInterface {

    @EJB
    private FacadeContainer facadeContainer;
    @EJB
    private MenuMovilCancelarCitaLocalDriver driver;
    private HashMap<String, HashMap<String, String>> nodes;

    private static final String CEDULA = "CEDULA";
    private static final String CITAS = "CITAS";
    private static final String OPCION = "OPCION";

    private String backText;
    private String nextText;
    private String exitText;

    private String backOption;
    private String nextOption;
    private String exitOption;

    private Integer maxLength;

    @PostConstruct
    private void init() {
        try {
            backText = facadeContainer.getGlobalParameterAPI().findByCode(
                    "ussd.text.back");
            nextText = facadeContainer.getGlobalParameterAPI().findByCode(
                    "ussd.text.next");
            exitText = facadeContainer.getGlobalParameterAPI().findByCode(
                    "ussd.text.exit");

            backOption = facadeContainer.getGlobalParameterAPI().findByCode(
                    "ussd.option.back");
            nextOption = facadeContainer.getGlobalParameterAPI().findByCode(
                    "ussd.option.next");
            exitOption = facadeContainer.getGlobalParameterAPI().findByCode(
                    "ussd.option.exit");

            maxLength = Integer.parseInt(facadeContainer.getGlobalParameterAPI().findByCode(
                    "ussd.text.maxlength"));
        } catch (Exception e) {
            facadeContainer.getNotifier().error(getClass(), null,
                    "No se pudieron cargar las opciones de menu USSD", e);
        }

    }

    @Override
    public VirtualEntryResponse requestVirtual(String msisdn, String code, HashMap<String, HashMap<String, String>> nodes, List<String> pages) {
        VirtualEntryResponse virtual = new VirtualEntryResponse();
        virtual.setContinueTree(false);
        virtual.setOk(true);
        virtual.setFinalMenu(false);
        try {
            setNodes(nodes);

            facadeContainer.getNotifier().info(getClass(), msisdn,
                    "Se creara el driver de consulta de citas");

            String document = getNodeValue(CEDULA);
            Long token = facadeContainer.getIpsServiceAPI().getLastToken(msisdn);

            RetornoCancelacionesMedicas[] citas = facadeContainer.getIpsServiceAPI().appointmentsByDocument(
                    document, msisdn, token);

            List<String> paginas = null;
            if (pages == null) {

                citas = facadeContainer.getIpsServiceAPI().appointmentsByDocument(
                        document, msisdn, token);

                if (citas == null || (citas != null && citas.length == 0)) {

                    facadeContainer.getNotifier().warn(
                            getClass(),
                            msisdn,
                            MessageFormat.format(
                                    "No se recupero ninguna cita. Documento: {0}",
                                    document));

                    virtual.setContinueTree(false);
                    virtual.setOk(false);
                    virtual.setFinalMenu(true);
                    virtual.setMessage("No posee citas medicas agendadas.");
                    return virtual;
                }

                paginas = paginas("Consulta de citas existentes", citas);
                virtual.setEntries(paginas);
            } else {
                paginas = pages;
            }

            String title = code.contains(CITAS) ? CITAS : OPCION;
            if (code.replaceFirst(title, "").equalsIgnoreCase("1")) {
                // primera pagina
                virtual.setTitle(paginas.get(0));
            } else {

                int pagActual = Integer.parseInt(code.replaceFirst(title, ""));
                int pagAnterior = pagActual - 1;
                HashMap<String, String> hm = nodes.get(title + pagAnterior);
                String pagAntMostrada = hm.get("Description");
                String valorIngresado = hm.get("Value");

                if (valorIngresado.equalsIgnoreCase(backOption)
                    && pagAntMostrada.contains(backOption + "- " + backText)) {
                    virtual.setEntries(paginas);
                    virtual.setTitle(paginas.get(paginas.indexOf(pagAntMostrada) - 1));
                } else if (valorIngresado.equalsIgnoreCase(nextOption)
                    && pagAntMostrada.contains(nextOption + "- " + nextText)) {
                    virtual.setEntries(paginas);
                    virtual.setTitle(paginas.get(paginas.indexOf(pagAntMostrada) + 1));
                } else if (valorIngresado.equalsIgnoreCase(exitOption)
                    && pagAntMostrada.contains(exitOption + "- " + exitText)) {
                    virtual.setOk(false);
                    virtual.setMessage("Gracias por utilizar el servicio.");
                } else {
                    try {
                        int posicion = Integer.parseInt(valorIngresado);

                        if (title.contains(OPCION)) {

                            if (posicion == 1) {
                                /*
                                 * invocar driver
                                 */

                                virtual.setContinueTree(true);
                                virtual.setFinalMenu(true);
                                virtual.setOk(false);
                                virtual.setMessage(driver.execute(msisdn,
                                        getNodes()));

                            }

                        } else if (title.contains(CITAS)) {

                            /*
                             * agregamos aca las opciones de la cita
                             */

                            if (posicion > 0 && posicion <= citas.length) {

                                RetornoCancelacionesMedicas cancelacion = citas[posicion - 1];

                                facadeContainer.getIpsServiceAPI().getLastCancelCita().put(
                                        msisdn, cancelacion);

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                String pattern = "{0} - {1} - {2}\n\nUsted Desea";
                                String message = MessageFormat.format(
                                        pattern,
                                        cancelacion.getTurnoCita(),
                                        cancelacion.getNombreMedico(),
                                        simpleDateFormat.format(cancelacion.getFechaCita().getTime()));

                                RetornoCancelacionesMedicas aux = new RetornoCancelacionesMedicas();

                                List<String> paginaOpcion = paginas(message,
                                        new String[] { "Cancelar" });
                                virtual.setTitle(paginaOpcion.get(0));
                                virtual.setEntries(paginaOpcion);
                                virtual.setNewCode(OPCION);
                            } else {
                                virtual.setEntries(paginas);
                                virtual.setTitle(pagAntMostrada);
                            }

                        } else {
                            virtual.setEntries(paginas);
                            virtual.setTitle(pagAntMostrada);
                        }
                    } catch (Exception e) {
                        if (e.getCause().getCause() instanceof InvalidOperationException) {
                            throw new InvalidOperationException(e.getCause().getCause().getMessage());
                        }
                        virtual.setEntries(paginas);
                        virtual.setTitle(pagAntMostrada);
                    }

                }
            }
        } catch (InvalidOperationException e) {
            virtual.setOk(false);
            virtual.setContinueTree(false);
            virtual.setMessage(e.getMessage());
        } catch (Exception e) {
            if (e.getCause().getCause() instanceof InvalidOperationException) {
                virtual.setOk(false);
                virtual.setContinueTree(false);
                virtual.setMessage(e.getCause().getCause().getMessage());
            }
        }

        virtual.setMessage(StringUtils.replaceAccents(virtual.getMessage()));
        return virtual;
    }

    /*
     * se crearan por paginas y niveles
     */
    private List<String> paginas(String title, String[] opciones) {

        List<String> paginas = new ArrayList<String>();
        String mensaje = "";
        title = title + "\n";
        String opcion = getOptionsMenuString();
        int j = 1;
        for (String retorno : opciones) {

            String especialidad = j++ + "- " + retorno.trim() + "\n";

            if (title.length() + mensaje.length() + retorno.length()
                + opcion.length() > maxLength) {
                // mensaje += opcion;
                paginas.add(mensaje);
                mensaje = especialidad;
            } else {
                mensaje += especialidad;
            }

        }
        if (!paginas.contains(mensaje) && !mensaje.isEmpty()) {
            // mensaje += opcion;
            paginas.add(mensaje);
        }
        String[] vector = paginas.toArray(new String[0]);
        if (vector.length == 1) {
            String nuevoMsj = title + vector[0];
            vector[0] = nuevoMsj;
        } else {
            String nuevoMsj = title + vector[0] + getOptionsSgteString();
            vector[0] = nuevoMsj;
            nuevoMsj = title + vector[vector.length - 1]
                + getOptionsAtrasString();
            vector[vector.length - 1] = nuevoMsj;
            for (int i = 1; i < vector.length - 1; i++) {
                nuevoMsj = title + vector[i] + getOptionsMenuString();
                vector[i] = nuevoMsj;
            }
        }
        paginas = new ArrayList<String>(vector.length);
        paginas.addAll(Arrays.asList(vector));
        return paginas;

    }

    /*
     * se crearan por paginas y niveles
     */
    private List<String> paginas(String title, RetornoCancelacionesMedicas[] retornoCancelacionesMedicas) {

        List<String> paginas = new ArrayList<String>();
        String mensaje = "";
        title = title + "\n";
        String opcion = getOptionsMenuString();
        int j = 1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        for (RetornoCancelacionesMedicas retorno : retornoCancelacionesMedicas) {

            String pattern = "{0}-{1}-{2}-{3}";
            String message = MessageFormat.format(pattern,
                    retorno.getNombreEspecialidad(), retorno.getTurnoCita(),
                    retorno.getNombreMedico(),
                    simpleDateFormat.format(retorno.getFechaCita().getTime()));

            String especialidad = j++ + "- " + message.trim() + "\n";

            if (title.length() + mensaje.length() + message.length()
                + opcion.length() > maxLength) {
                // mensaje += opcion;
                paginas.add(StringUtils.replaceAccents(mensaje));
                mensaje = especialidad;
            } else {
                mensaje += especialidad;
            }

        }
        if (!paginas.contains(mensaje) && !mensaje.isEmpty()) {
            // mensaje += opcion;
            paginas.add(StringUtils.replaceAccents(mensaje));
        }
        String[] vector = paginas.toArray(new String[0]);
        if (vector.length == 1) {
            String nuevoMsj = title + vector[0];
            vector[0] = nuevoMsj;
        } else {
            String nuevoMsj = title + vector[0] + getOptionsSgteString();
            vector[0] = nuevoMsj;
            nuevoMsj = title + vector[vector.length - 1]
                + getOptionsAtrasString();
            vector[vector.length - 1] = nuevoMsj;
            for (int i = 1; i < vector.length - 1; i++) {
                nuevoMsj = title + vector[i] + getOptionsMenuString();
                vector[i] = nuevoMsj;
            }
        }
        paginas = new ArrayList<String>(vector.length);
        paginas.addAll(Arrays.asList(vector));
        return paginas;

    }

    private String getOptionsMenuString() {
        String opciones = "{0}- {1}\n{2}- {3}";
        opciones = MessageFormat.format(opciones, backOption, backText,
                nextOption, nextText);
        return opciones;
    }

    private String getOptionsAtrasString() {
        String opciones = "{0}- {1}";
        opciones = MessageFormat.format(opciones, backOption, backText);
        return opciones;
    }

    private String getOptionsSgteString() {
        String opciones = "{0}- {1}";
        opciones = MessageFormat.format(opciones, nextOption, nextText);
        return opciones;
    }

    private String getOptionsSalirString() {
        String opciones = "{0}- {1}";
        opciones = MessageFormat.format(opciones, exitOption, exitText);
        return opciones;
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
