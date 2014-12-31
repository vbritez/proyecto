package com.tigo.cs.facade.jms.consumer;

import java.text.MessageFormat;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.tigo.cs.api.facade.jms.SMSJmsBean;
import com.tigo.cs.api.interfaces.FacadeContainer;

@MessageDriven(mappedName = "jms/SMSQueue", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode",
                propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType",
                propertyValue = "javax.jms.Queue") })
public class SMSConsumer implements MessageListener {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public void onMessage(Message message) {
        facadeContainer.getNotifier().debug(getClass(), null,
                "Iniciamos el Consumo de Mensajes de la Cola JMS para envio de SMS");
        ObjectMessage msg = null;
        try {
            if (message instanceof ObjectMessage) {
                msg = (ObjectMessage) message;
                SMSJmsBean jmsBean = (SMSJmsBean) msg.getObject();

                if (jmsBean.getCellphoneNumList() != null) {
                    facadeContainer.getNotifier().info(
                            getClass(),
                            null,
                            MessageFormat.format("Se envia SMS. MSISDN: {0}",
                                    jmsBean.getCellphoneNumList().toString()));
                    facadeContainer.getSmsTransmitterServiceAPI().sendBroadcastMessage(
                            jmsBean.getApplication(),
                            jmsBean.getCellphoneNumList(), jmsBean.getMessage());
                } else {
                    facadeContainer.getNotifier().info(
                            getClass(),
                            null,
                            MessageFormat.format("Se envia SMS. MSISDN: {0}",
                                    jmsBean.getCellphoneNum()));
                    facadeContainer.getSmsTransmitterServiceAPI().sendMessage(
                            jmsBean.getApplication(),
                            jmsBean.getCellphoneNum(), jmsBean.getMessage());
                }

            } else {
                facadeContainer.getNotifier().warn(getClass(), null,
                        "Mensaje de tipo incorrecto");
            }
        } catch (Exception e) {
            facadeContainer.getNotifier().error(getClass(), null,
                    "Error procesando el Mensaje: " + e.getMessage(), e);
        }
    }
}
