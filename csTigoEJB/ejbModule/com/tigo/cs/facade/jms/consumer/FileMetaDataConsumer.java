package com.tigo.cs.facade.jms.consumer;

import javax.ejb.EJB;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.tigo.cs.api.facade.FileMetaDataAPI;
import com.tigo.cs.api.facade.jms.FileMetaDataMessage;
import com.tigo.cs.api.interfaces.FacadeContainer;

//@MessageDriven(mappedName = "jms/FileMetaDataQueue", activationConfig = {
//        @ActivationConfigProperty(propertyName = "acknowledgeMode",
//                propertyValue = "Auto-acknowledge"),
//        @ActivationConfigProperty(propertyName = "destinationType",
//                propertyValue = "javax.jms.Queue") })
public class FileMetaDataConsumer extends FileMetaDataAPI implements MessageListener {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    public void onMessage(Message message) {
        getFacadeContainer().getNotifier().debug(getClass(), null,
                "Procesando mensaje");
        ObjectMessage msg = null;
        try {
            if (message instanceof ObjectMessage) {
                msg = (ObjectMessage) message;
                FileMetaDataMessage jmsBean = (FileMetaDataMessage) msg.getObject();
                procesarCargaMasiva(jmsBean);
            } else {
                getFacadeContainer().getNotifier().warn(getClass(), null,
                        "Mensaje de tipo incorrecto");
            }
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    "Error procesando el Mensaje: " + e.getMessage(), e);
        }
    }

}
