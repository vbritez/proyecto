package com.tigo.cs.facade.ussddriver.menumovil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.response.VirtualEntryResponse;
import py.com.lothar.ussddriverinterfaces.virtual.VirtualEntryDriverInterface;
import py.gov.ips.consultas.servicios.Menu;
import py.gov.ips.consultas.servicios.RetornoAgendamientoMedico;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.util.ExceptionUtility;
import com.tigo.cs.commons.util.StringUtils;

@Stateless
public class MenuMovilAgendarCitaVirtualDriver implements VirtualEntryDriverInterface {

    @EJB
    private FacadeContainer facadeContainer;
    @EJB
    private MenuMovilAgendarLocalCitaDriver driver;
    private HashMap<String, HashMap<String, String>> nodes;

    private static final String CEDULA = "CEDULA";
    private static final String ESPECIALIDAD = "ESPECIALIDAD";
    private static final String TIPO = "TIPO";
    private static final String ZONA = "ZONA";
    private static final String CENTRO_MEDICO = "CENTRO_MEDICO";
    private static final String TURNO = "TURNO";
    private static final String HORARIO = "HORARIO";

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
                    "IPS - Ingresa al driver virtual de agendamiento");

            String document = getNodeValue(CEDULA);
            Long token = facadeContainer.getIpsServiceAPI().getLastToken(msisdn);

            facadeContainer.getNotifier().info(getClass(), msisdn,
                    "IPS - Obtuvo el token" + token);

            List<String> paginas = null;
            if (pages == null) {

                facadeContainer.getNotifier().info(getClass(), msisdn,
                        "IPS - Se creara las pagina de especialidades");

                RetornoAgendamientoMedico retornoAgendamientoMedico = facadeContainer.getIpsServiceAPI().findEspecialty(
                        document, msisdn, token);

                if (retornoAgendamientoMedico == null
                    || (retornoAgendamientoMedico != null && retornoAgendamientoMedico.getListaMenu() == null)
                    || (retornoAgendamientoMedico != null
                        && retornoAgendamientoMedico.getListaMenu() != null && retornoAgendamientoMedico.getListaMenu().length <= 0)) {

                    facadeContainer.getNotifier().warn(
                            getClass(),
                            msisdn,
                            MessageFormat.format(
                                    "No se recupero ninguna especialidad. Token: {0}",
                                    facadeContainer.getIpsServiceAPI().getLastToken(
                                            msisdn)));

                    virtual.setContinueTree(false);
                    virtual.setOk(false);
                    virtual.setFinalMenu(true);
                    virtual.setMessage("No se recupero la lista de especialidades. Intente nuevamente en unos momentos.");
                    return virtual;
                }

                facadeContainer.getNotifier().info(getClass(), msisdn,
                        "IPS - Creando pantallas");

                paginas = paginas("Especialidades",
                        retornoAgendamientoMedico.getListaMenu());
                virtual.setEntries(paginas);
            } else {
                facadeContainer.getNotifier().info(getClass(), msisdn,
                        "IPS - Se reutilizara la pantalla mostrada anteriormente");

                paginas = pages;
            }

            String title = code.contains(ESPECIALIDAD) ? ESPECIALIDAD : code.contains(TIPO) ? TIPO : code.contains(ZONA) ? ZONA : code.contains(TURNO) ? TURNO : code.contains(HORARIO) ? HORARIO : CENTRO_MEDICO;
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
                    facadeContainer.getNotifier().info(getClass(), msisdn,
                            "IPS - Usuario ingreso opcion");

                    try {
                        int posicion = Integer.parseInt(valorIngresado);

                        if (title.contains(TURNO)) {

                            facadeContainer.getNotifier().info(getClass(),
                                    msisdn, "IPS - Usuario ingreso TURNO");

                            virtual.setContinueTree(true);
                            virtual.setFinalMenu(true);
                            virtual.setOk(false);
                            virtual.setMessage(driver.execute(msisdn,
                                    getNodes()));

                        } else if (title.contains(HORARIO)) {

                            facadeContainer.getNotifier().info(getClass(),
                                    msisdn, "IPS - Usuario ingreso HORARIO");

                            Menu lastSpecialty = facadeContainer.getIpsServiceAPI().getLastSpecialty(
                                    msisdn);

                            Menu lastSpecialtyType = facadeContainer.getIpsServiceAPI().getLastSpecialtyType(
                                    msisdn);

                            Integer specialtyCod = null;
                            if (lastSpecialtyType != null) {
                                specialtyCod = Integer.parseInt(lastSpecialtyType.getCodRetorno());
                            } else {
                                specialtyCod = Integer.parseInt(lastSpecialty.getCodRetorno());
                            }

                            Menu lastMedicCenter = facadeContainer.getIpsServiceAPI().getLastMedicCenter(
                                    msisdn);

                            if (posicion > 0
                                && posicion <= lastMedicCenter.getListaRetorno().length) {

                                Menu horario = lastMedicCenter.getListaRetorno()[posicion - 1];
                                facadeContainer.getIpsServiceAPI().getLastHorarioMedicCenter().put(
                                        msisdn, horario);

                                RetornoAgendamientoMedico medic = facadeContainer.getIpsServiceAPI().findMedic(
                                        document,
                                        msisdn,
                                        token,
                                        Integer.parseInt(lastMedicCenter.getCodRetorno()),
                                        specialtyCod, horario.getCodRetorno());

                                List<String> paginaMedic = paginas(
                                        "Medico - Turno - Fecha",
                                        medic.getListaMenu());
                                virtual.setTitle(paginaMedic.get(0));
                                virtual.setEntries(paginaMedic);
                                virtual.setNewCode(TURNO);
                            }
                        } else if (title.contains(CENTRO_MEDICO)) {

                            facadeContainer.getNotifier().info(getClass(),
                                    msisdn,
                                    "IPS - Usuario ingreso CENTRO MEDICO");

                            Menu lastSpecialty = facadeContainer.getIpsServiceAPI().getLastSpecialty(
                                    msisdn);

                            Menu lastSpecialtyType = facadeContainer.getIpsServiceAPI().getLastSpecialtyType(
                                    msisdn);

                            Integer specialtyCod = null;
                            if (lastSpecialtyType != null) {
                                specialtyCod = Integer.parseInt(lastSpecialtyType.getCodRetorno());
                            } else {
                                specialtyCod = Integer.parseInt(lastSpecialty.getCodRetorno());
                            }

                            Menu zona = facadeContainer.getIpsServiceAPI().getLastZone(
                                    msisdn);

                            if (posicion > 0
                                && posicion <= zona.getListaRetorno().length) {

                                /*
                                 * aca obtenermos el centro medico que
                                 * selecciono el usuario
                                 */

                                Menu medicCenter = zona.getListaRetorno()[posicion - 1];
                                facadeContainer.getIpsServiceAPI().getLastMedicCenter().put(
                                        msisdn, medicCenter);

                                if (medicCenter.getListaRetorno() != null) {
                                    List<String> paginaMedic = paginas("Turno",
                                            medicCenter.getListaRetorno());
                                    virtual.setTitle(paginaMedic.get(0));
                                    virtual.setEntries(paginaMedic);
                                    virtual.setNewCode(HORARIO);
                                } else {
                                    RetornoAgendamientoMedico medic = facadeContainer.getIpsServiceAPI().findMedic(
                                            document,
                                            msisdn,
                                            token,
                                            Integer.parseInt(medicCenter.getCodRetorno()),
                                            specialtyCod, null);
                                    List<String> paginaMedic = paginas(
                                            "Medico - Turno - Fecha",
                                            medic.getListaMenu());
                                    virtual.setTitle(paginaMedic.get(0));
                                    virtual.setEntries(paginaMedic);
                                    virtual.setNewCode(TURNO);
                                }
                            }
                        } else if (title.contains(ZONA)) {

                            facadeContainer.getNotifier().info(getClass(),
                                    msisdn, "IPS - Usuario ingreso ZONA");

                            /*
                             * si entro aca es por que selecciono ZONA hay que
                             * mostrarle los centros medicos
                             */

                            Menu lastSpecialty = facadeContainer.getIpsServiceAPI().getLastSpecialty(
                                    msisdn);

                            Menu lastSpecialtyType = facadeContainer.getIpsServiceAPI().getLastSpecialtyType(
                                    msisdn);

                            Short specialtyCod = null;
                            if (lastSpecialtyType != null) {
                                specialtyCod = Short.parseShort(lastSpecialtyType.getCodRetorno());
                            } else {
                                specialtyCod = Short.parseShort(lastSpecialty.getCodRetorno());
                            }

                            RetornoAgendamientoMedico medicCenter = facadeContainer.getIpsServiceAPI().findMedicCenter(
                                    document, msisdn, token, specialtyCod);

                            Menu zona = medicCenter.getListaMenu()[posicion - 1];
                            facadeContainer.getIpsServiceAPI().getLastZone().put(
                                    msisdn, zona);

                            Menu[] tipos = zona.getListaRetorno();

                            /*
                             * verificamos si tiene tipo
                             */
                            if (tipos != null && tipos.length > 0) {
                                List<String> paginaTipos = paginas(
                                        "Centros Medicos", tipos);
                                virtual.setTitle(paginaTipos.get(0));
                                virtual.setEntries(paginaTipos);
                                virtual.setNewCode(CENTRO_MEDICO);
                            } else {

                                // TODO: llorar esto no puede pasar

                            }

                        } else if (title.contains(TIPO)) {

                            facadeContainer.getNotifier().info(getClass(),
                                    msisdn, "IPS - Usuario ingreso TIPO");

                            Menu lastSpecialty = facadeContainer.getIpsServiceAPI().getLastSpecialty(
                                    msisdn);

                            if (posicion > 0
                                && posicion <= lastSpecialty.getListaRetorno().length) {

                                Menu specialtyType = lastSpecialty.getListaRetorno()[posicion - 1];
                                facadeContainer.getIpsServiceAPI().getLastSpecialtyType().put(
                                        msisdn, specialtyType);

                                RetornoAgendamientoMedico medicCenter = facadeContainer.getIpsServiceAPI().findMedicCenter(
                                        document,
                                        msisdn,
                                        token,
                                        Short.parseShort(specialtyType.getCodRetorno()));

                                List<String> paginaZona = paginas("Zonas",
                                        medicCenter.getListaMenu());
                                virtual.setTitle(paginaZona.get(0));
                                virtual.setEntries(paginaZona);
                                virtual.setNewCode(ZONA);
                            }

                        } else if (title.contains(ESPECIALIDAD)
                            && pages != null) {

                            facadeContainer.getNotifier().info(getClass(),
                                    msisdn,
                                    "IPS - Usuario ingreso ESPECIALIDAD");

                            RetornoAgendamientoMedico retornoAgendamientoMedico = facadeContainer.getIpsServiceAPI().findEspecialty(
                                    document, msisdn, token);

                            if (retornoAgendamientoMedico == null
                                || (retornoAgendamientoMedico != null && retornoAgendamientoMedico.getListaMenu() == null)
                                || (retornoAgendamientoMedico != null
                                    && retornoAgendamientoMedico.getListaMenu() != null && retornoAgendamientoMedico.getListaMenu().length <= 0)) {

                                facadeContainer.getNotifier().warn(
                                        getClass(),
                                        msisdn,
                                        MessageFormat.format(
                                                "No se recupero ninguna especialidad. Token: {0}",
                                                facadeContainer.getIpsServiceAPI().getLastToken(
                                                        msisdn)));

                                virtual.setContinueTree(false);
                                virtual.setOk(false);
                                virtual.setFinalMenu(true);
                                virtual.setMessage("No se recupero la lista de especialidades. Intente nuevamente en unos momentos.");
                                return virtual;
                            }

                            Menu[] tipos = retornoAgendamientoMedico.getListaMenu()[posicion - 1].getListaRetorno();

                            /*
                             * si entro aca es porque ya selecciono la
                             * especialidad
                             */

                            Menu specialty = retornoAgendamientoMedico.getListaMenu()[posicion - 1];

                            facadeContainer.getIpsServiceAPI().getLastSpecialty().put(
                                    msisdn, specialty);

                            /*
                             * verificamos si tiene tipo
                             */
                            if (tipos != null && tipos.length > 0) {
                                List<String> paginaTipos = paginas("Tipos",
                                        tipos);
                                virtual.setTitle(paginaTipos.get(0));
                                virtual.setEntries(paginaTipos);
                                virtual.setNewCode(TIPO);
                            } else {

                                /*
                                 * no tiene tipo entonces mostrar seleccion
                                 */
                                if (posicion > 0
                                    && posicion < retornoAgendamientoMedico.getListaMenu().length) {

                                    RetornoAgendamientoMedico medicCenter = facadeContainer.getIpsServiceAPI().findMedicCenter(
                                            document,
                                            msisdn,
                                            token,
                                            Short.parseShort(specialty.getCodRetorno()));

                                    List<String> paginaZona = paginas("Zonas",
                                            medicCenter.getListaMenu());
                                    virtual.setTitle(paginaZona.get(0));
                                    virtual.setEntries(paginaZona);
                                    virtual.setNewCode(ZONA);
                                }

                            }
                        } else {
                            facadeContainer.getNotifier().info(getClass(),
                                    msisdn, "IPS - Mostando pagina anterior");

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
            virtual.setOk(false);
            virtual.setContinueTree(false);
            virtual.setMessage("Favor vuelva a intentar");
            facadeContainer.getNotifier().error(
                    getClass(),
                    msisdn,
                    "IPS - Error mosntrando menu:"
                        + ExceptionUtility.getStackTrace(e), e);
        }
        virtual.setTitle(StringUtils.replaceAccents(virtual.getTitle()));
        virtual.setMessage(StringUtils.replaceAccents(virtual.getMessage()));
        return virtual;
    }

    /*
     * se crearan por paginas y niveles
     */
    private List<String> paginas(String title, Menu[] especialidades) {

        List<String> paginas = new ArrayList<String>();
        String mensaje = "";
        title = title + "\n";
        String opcion = getOptionsMenuString();
        int j = 1;
        for (Menu menu : especialidades) {

            String especialidad = j++ + "- " + menu.getDesRetorno() + "\n";

            if (title.length() + mensaje.length()
                + menu.getDesRetorno().length() + opcion.length() > maxLength) {
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
