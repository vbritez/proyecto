package com.tigo.cs.facade.ussddriver.menumovil;

import java.text.MessageFormat;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import py.com.lothar.ussddriverinterfaces.response.ValidationResponse;
import py.com.lothar.ussddriverinterfaces.validator.ValidationDriverInterface;
import py.gov.ips.consultas.servicios.RetornoAgendamientoMedico;

import com.tigo.cs.api.exception.InvalidOperationException;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.commons.util.StringUtils;

@Stateless
public class MenuMovilValidateCi implements ValidationDriverInterface {

    private static final String CEDULA = "CEDULA";
    protected HashMap<String, HashMap<String, String>> nodes;

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public ValidationResponse validate(String msisdn, HashMap<String, HashMap<String, String>> nodes) {

        String message = MessageFormat.format(
                "IPS - Ingreso al metodo de validacion de documento"
                    + "|operation:validarCi |" + "Desde|msisdn: {0} ", msisdn);

        facadeContainer.getNotifier().info(getClass(), msisdn, message);

        boolean valid = false;
        setNodes(nodes);
        message = "ATENCION\nNumero de C.I. no\ntiene derecho.";

        try {

            String document = getNodeValue(CEDULA);

            facadeContainer.getNotifier().debug(
                    getClass(),
                    msisdn,
                    "Luego de obtener el numero de documento verificamos que el usuario ha insertado datos validos");

            if (document == null
                || (document != null && document.trim().length() <= 0)) {
                String msg = "No ha ingresado su numero de cedula: {0}";
                msg = MessageFormat.format(msg, message);
                facadeContainer.getNotifier().warn(getClass(), msisdn, msg);
                return new ValidationResponse(valid, null, message);
            }

            message = MessageFormat.format(
                    "ATENCION\nNumero de C.I. {0} no\ntiene derecho.", document);

            facadeContainer.getNotifier().info(getClass(), msisdn,
                    MessageFormat.format("Documento a validar: {0}", document));

            RetornoAgendamientoMedico retornoAgendamientoMedico = facadeContainer.getIpsServiceAPI().validateDocument(
                    document, msisdn);

            /*
             * en el caso que no se pudo recuperar informacion del metodo menu
             * movil del WS, el retorno es nulo vallidar eso
             */
            if (retornoAgendamientoMedico == null) {
                String msg = "El metodo de validacion de documentos del WS de Menu Movil IPS no retorno ningun valor: {0}";
                msg = MessageFormat.format(msg, message);
                facadeContainer.getNotifier().warn(getClass(), msisdn, msg);
                return new ValidationResponse(valid, null, message);
            }

            facadeContainer.getNotifier().info(getClass(), msisdn,
                    "Verificamos si la respuesta produjo un error en la invocacion del metodo.");

            /*
             * verificamos si retorno un codigo de error la aplicacion
             */
            if (retornoAgendamientoMedico.getCodError() != null) {
                String msg = "Se produjo un error al consumir el WS de Menu Movil IPS. Error al validar documento de identidad: {0}, {1}";
                msg = MessageFormat.format(msg,
                        retornoAgendamientoMedico.getCodError(),
                        retornoAgendamientoMedico.getDesError());
                facadeContainer.getNotifier().warn(getClass(), msisdn, msg);
                return new ValidationResponse(valid, null, message);
            }

            facadeContainer.getNotifier().info(
                    getClass(),
                    msisdn,
                    MessageFormat.format(
                            "Verificacion de documentos exitosa. Documento: {0}. Nombre Asegurado: {1}",
                            retornoAgendamientoMedico.getNroCi(),
                            retornoAgendamientoMedico.getNomApe()));
            valid = true;
            ValidationResponse response = new ValidationResponse(valid, null, retornoAgendamientoMedico.getNomApe());
            response.setEntryTitle(StringUtils.replaceAccents(retornoAgendamientoMedico.getNomApe()));
            return response;
        } catch (InvalidOperationException e) {
            return new ValidationResponse(valid, null, e.getMessage());
        } catch (Exception e) {
            if (e.getCause().getCause() instanceof InvalidOperationException) {
                return new ValidationResponse(valid, null, e.getCause().getCause().getMessage());
            }
            facadeContainer.getNotifier().error(getClass(), msisdn,
                    e.getMessage(), e);
            return new ValidationResponse(valid, null, message);
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
