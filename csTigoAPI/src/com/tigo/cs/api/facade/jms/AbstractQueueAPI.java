package com.tigo.cs.api.facade.jms;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.tigo.cs.api.facade.AbstractAPI;

public abstract class AbstractQueueAPI<T extends Serializable> extends AbstractAPI<String> implements Serializable {

    private QueueConnectionFactory factory;
    private Queue queue;
    private QueueConnection cnn;
    private QueueSession session;
    private QueueSender sender;

    protected AbstractQueueAPI() {
        super(String.class);
    }

    private static final long serialVersionUID = 3867155173587839450L;

    protected abstract String getConnectionFactory();

    protected abstract String getDestinyResource();

    @PostConstruct
    private void postConstruct() {
        try {
            factory = (QueueConnectionFactory) getObjectByJNDI(getConnectionFactory());
            queue = (Queue) getObjectByJNDI(getDestinyResource());
            cnn = factory.createQueueConnection();
            session = cnn.createQueueSession(false,
                    QueueSession.AUTO_ACKNOWLEDGE);
            sender = session.createSender(queue);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    private void preDestroy() {
        try {
            sender.close();
            session.close();
            cnn.close();
            getFacadeContainer().getNotifier().info(getClass(), null,
                    "Destruida cola JMS" + getDestinyResource());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendToJMS(T message) {

        try {
            getFacadeContainer().getNotifier().debug(getClass(), null,
                    "Enviando mensaje a servidor JMS" + getDestinyResource());

            if (queue != null) {

                ObjectMessage objectMessage = session.createObjectMessage(message);

                sender.send(objectMessage);

                getFacadeContainer().getNotifier().debug(getClass(), null,
                        "Lista enviada exitosamente");
            } else {
                getFacadeContainer().getNotifier().warn(getClass(), null,
                        "Error al instanciar contexto");
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    "Error enviando texto a JMS ", e);
        }
    }

    public Object getObjectByJNDI(String ejbName) {
        try {
            Context ctx = new InitialContext();
            return ctx.lookup(ejbName);
        } catch (NamingException e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    "Error al instanciar contexto", null);
            return null;
        }
    }

}
