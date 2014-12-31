package com.tigo.cs.api.facade.ws;

import java.util.HashMap;
import java.util.Map;

import com.tigo.cs.api.util.JMSProperties;
import com.tigo.cs.api.util.NCenterJmsResponse;

public class NCenterJmsAPI 
//extends AbstractAPI<String> 
{

    public NCenterJmsAPI() {
//        super(String.class);
    }

//    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass().getSimpleName());
//    private JmsClient jmsClient;
//    private boolean connectedJMS;
//    private String jmsQueueName;
//    private String jmsQueueConnFactory;
//    private String initialContextFactory;
//    private String jmsserver;
//    private String urlpkgprefixes;
//    private String destino;
//    private String idPlataforma;
//    private String proceso;
//    private String mensaje;
//    private NCenterJmsResponse nCenterJmsResponse = null;

//    public Map<JMSProperties, String> getDefaultParameters() {
//        try {
//            Map<JMSProperties, String> defaultParameters = new HashMap<JMSProperties, String>();
//            defaultParameters.put(
//                    JMSProperties.JMSQUEUENAME,
//                    getFacadeContainer().getGlobalParameterAPI().findByCode(
//                            "ncenter.jms.queuename"));
//            defaultParameters.put(
//                    JMSProperties.JMSQUEUECONNFACTORY,
//                    getFacadeContainer().getGlobalParameterAPI().findByCode(
//                            "ncenter.jms.connectionFactory"));
//            defaultParameters.put(
//                    JMSProperties.INITIALCONTEXTFACTORY,
//                    getFacadeContainer().getGlobalParameterAPI().findByCode(
//                            "ncenter.jms.NamingContextFactory"));
//            defaultParameters.put(
//                    JMSProperties.JMSSERVER,
//                    getFacadeContainer().getGlobalParameterAPI().findByCode(
//                            "ncenter.jms.serverhost"));
//            defaultParameters.put(
//                    JMSProperties.URLPKGPREFIXES,
//                    getFacadeContainer().getGlobalParameterAPI().findByCode(
//                            "ncenter.jms.ejbpkgs"));
//            defaultParameters.put(JMSProperties.IDPLATAFORMA, "4");
//            defaultParameters.put(JMSProperties.PROCESO, "4");
//            return defaultParameters;
//        } catch (Exception e) {
//            getFacadeContainer().getNotifier().error(getClass(),
//                    "No se creo la parametrizacion por defecto", e);
//            return null;
//        }
//
//    }
//
//    public NCenterJmsResponse send2JMS(HashMap<JMSProperties, String> parameters) {
//
//        jmsQueueName = parameters.get(JMSProperties.JMSQUEUENAME);
//        jmsQueueConnFactory = parameters.get(JMSProperties.JMSQUEUECONNFACTORY);
//        initialContextFactory = parameters.get(JMSProperties.INITIALCONTEXTFACTORY);
//        jmsserver = parameters.get(JMSProperties.JMSSERVER);
//        urlpkgprefixes = parameters.get(JMSProperties.URLPKGPREFIXES);
//        destino = parameters.get(JMSProperties.DESTINO);
//        idPlataforma = parameters.get(JMSProperties.IDPLATAFORMA);
//
//        proceso = parameters.get(JMSProperties.PROCESO);
//        mensaje = parameters.get(JMSProperties.MENSAJE);
//
//        try {
//            setConnection2Jms();
//            // enviar un map al jms
//            HashMap<String, String> mapMessage = new HashMap<String, String>();
//            mapMessage.put("destino", destino);
//            mapMessage.put("plataforma", idPlataforma + "");
//            mapMessage.put("proceso", proceso + "");
//            mapMessage.put("mensaje", mensaje);
//            boolean ok = jmsClient.sndMessage2Jms(mapMessage);
//            if (!ok) {
//                // Intentar reconexion
//                setConnection2Jms();
//                logger.info("Reconexion a la cola JMS");
//                if (connectedJMS) {
//                    ok = jmsClient.sndMessage2Jms(mapMessage);
//                }
//            }
//            return nCenterJmsResponse = new NCenterJmsResponse(ok, "00", "Mensaje enviado al JMS: "
//                + jmsQueueName, parameters);
//
//        } catch (Exception e) {
//            logger.error("[" + destino + "] Error al enviar a JMS", e);
//            return nCenterJmsResponse = new NCenterJmsResponse(false, "01", "Mensaje NO enviado al JMS: "
//                + jmsQueueName, parameters);
//        }
//
//    }
//
//    private void setConnection2Jms() {
//        try {
//            jmsClient = new JmsClient(jmsQueueName, jmsQueueConnFactory, initialContextFactory, jmsserver, urlpkgprefixes);
//            connectedJMS = jmsClient.jmsConn();
//            logger.info("Estado de conexion al JMS = " + connectedJMS);
//
//        } catch (Exception e) {
//            connectedJMS = false;
//            logger.error("No se pudo conectar al JMS", e);
//        }
//
//    }

}
