package com.tigo.cs.facade.jms.consumer;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.tigo.cs.api.entities.TrackingService;
import com.tigo.cs.api.enumerate.ServiceIdentifier;
import com.tigo.cs.api.interfaces.FacadeContainer;
import com.tigo.cs.api.service.TrackingServiceAPI;

@MessageDriven(mappedName = "jms/TrackQueue",
        activationConfig = { @ActivationConfigProperty(
                propertyName = "acknowledgeMode",
                propertyValue = "Auto-acknowledge"), @ActivationConfigProperty(
                propertyName = "destinationType",
                propertyValue = "javax.jms.Queue") })
@ServiceIdentifier(id = ServiceIdentifier.Id.RASTREO)
public class TrackConsumer extends TrackingServiceAPI<TrackingService> implements MessageListener {

    @EJB
    private FacadeContainer facadeContainer;

    @Override
    public FacadeContainer getFacadeContainer() {
        return facadeContainer;
    }

    @Override
    protected void assignEvent() {
    }

    @Override
    public void convertToBean() {
    }

    @Override
    public void onMessage(Message message) {
        getFacadeContainer().getNotifier().debug(getClass(), null,
                "Procesando mensaje");
        ObjectMessage msg = null;
        try {
            if (message instanceof ObjectMessage) {
                msg = (ObjectMessage) message;
                TrackingService jmsBean = (TrackingService) msg.getObject();
                execute(jmsBean);
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
