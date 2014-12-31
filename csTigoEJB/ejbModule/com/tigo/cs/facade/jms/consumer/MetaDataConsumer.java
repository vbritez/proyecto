package com.tigo.cs.facade.jms.consumer;

import javax.ejb.EJB;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.tigo.cs.api.facade.jms.MetaDataMessage;
import com.tigo.cs.api.interfaces.FacadeContainer;

//@MessageDriven(mappedName = "jms/MetaDataQueue", activationConfig = {
//        @ActivationConfigProperty(propertyName = "acknowledgeMode",
//                propertyValue = "Auto-acknowledge"),
//        @ActivationConfigProperty(propertyName = "destinationType",
//                propertyValue = "javax.jms.Queue") })
public class MetaDataConsumer implements MessageListener {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public void onMessage(Message message) {
        facadeContainer.getNotifier().debug(getClass(), null,
                "Procesando mensaje");
        ObjectMessage msg = null;
        try {
            if (message instanceof ObjectMessage) {
                msg = (ObjectMessage) message;
                MetaDataMessage jmsBean = (MetaDataMessage) msg.getObject();

                facadeContainer.getMetaDataAPI().processUserphoneMeta(
                        jmsBean.getMetaData(), jmsBean.getUserphoneCode());

            } else {
                facadeContainer.getNotifier().warn(getClass(), null,
                        "Mensaje de tipo incorrecto");
            }
        } catch (Exception e) {

            facadeContainer.getNotifier().error(getClass(), null,
                    "Error procesando el MetaData: ".concat(e.getMessage()), e);
        }
    }
}
