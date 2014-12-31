package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.Message;

public abstract class MessageAPI extends AbstractAPI<Message> {

    public MessageAPI() {
        super(Message.class);
    }

    public List<Message> getMessages(String query) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(query);
            return q.getResultList();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Message getMessage(String query) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query q = em.createQuery(query);
            q.setMaxResults(1);
            return (Message) q.getSingleResult();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @Override
    public Message edit(Message entity) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }

            Message message = em.find(Message.class, entity.getMessageCod());

            message.setApplication(entity.getApplication());
            message.setCell(entity.getCell());
            message.setClient(entity.getClient());
            message.setDateinDat(entity.getDateinDat());
            message.setDateoutDat(entity.getDateoutDat());
            message.setDestinationChr(entity.getDestinationChr());
            message.setLatitude(entity.getLatitude());
            message.setLongitude(entity.getLongitude());
            message.setMessageinChr(entity.getMessageinChr());
            message.setMessageNotSentList(entity.getMessageNotSentList());
            message.setMessageoutChr(entity.getMessageoutChr());
            message.setOriginChr(entity.getOriginChr());
            message.setServiceValueCollection(entity.getServiceValueCollection());
            message.setServiceValueDetailCollection(entity.getServiceValueDetailCollection());
            message.setUseradmin(entity.getUseradmin());
            message.setUserphone(entity.getUserphone());
            message.setUserweb(entity.getUserweb());
            message.setService(entity.getService());
            message.setVersionName(entity.getVersionName());

            message = em.merge(message);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return message;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
        return null;

    }
}
