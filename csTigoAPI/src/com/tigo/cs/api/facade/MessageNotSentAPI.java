package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;

import com.tigo.cs.domain.Message;
import com.tigo.cs.domain.MessageNotSent;
import com.tigo.cs.domain.Service;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: MessageNotSentFacade.java 7 2011-11-18 11:12:15Z miguel.maciel
 *          $
 */
public abstract class MessageNotSentAPI extends AbstractAPI<MessageNotSent> {

    public MessageNotSentAPI() {
        super(MessageNotSent.class);
    }

    public MessageNotSent create(MessageNotSent messageNotSent) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            em.getTransaction().begin();
            Service service = messageNotSent.getService();
            if (service != null) {
                service = em.getReference(service.getClass(),
                        service.getServiceCod());
                messageNotSent.setService(service);
            }
            Message message = messageNotSent.getMessage();
            if (message != null) {
                message = em.getReference(message.getClass(),
                        message.getMessageCod());
                messageNotSent.setMessage(message);
            }
            em.persist(messageNotSent);
            if (service != null) {
                service.getMessageNotSentList().add(messageNotSent);
                service = em.merge(service);
            }
            if (message != null) {
                message.getMessageNotSentList().add(messageNotSent);
                message = em.merge(message);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return em.find(MessageNotSent.class,
                messageNotSent.getMessageNotSentCod());
    }

}
