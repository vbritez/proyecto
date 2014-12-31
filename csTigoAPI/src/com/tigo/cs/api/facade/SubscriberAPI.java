package com.tigo.cs.api.facade;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Subscriber;
import com.tigo.cs.domain.WsClient;

public class SubscriberAPI extends AbstractAPI<Subscriber> {

    public SubscriberAPI() {
        super(Subscriber.class);
    }

    public Subscriber findByCi(String ci) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManagerMTS();
            Query query = em.createNamedQuery("Subscriber.findByIdentification");
            query.setParameter("ci", ci);
            return (Subscriber) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(SubscriberAPI.class, null,
                    e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public boolean register(WsClient wsClient, String ci, Date birthDate, String address, String city, byte[] frontPhoto, byte[] backPhoto, String source, String registrationChannel) throws GenericFacadeException {

        Subscriber subscriber = getFacadeContainer().getSubscriberAPI().findByCi(
                ci);
        if (subscriber == null) {
            subscriber = new Subscriber();
            subscriber.setCiChr(ci);
            subscriber.setBirthdateDat(birthDate);
            subscriber.setAddressChr(address);
            subscriber.setCityChr(city);
            subscriber.setFrontPhotoByt(frontPhoto);
            subscriber.setBackPhotoByt(backPhoto);
            subscriber.setSourceChr(source);
            subscriber.setRegistrationChannelChr(registrationChannel);
            subscriber.setInsertedOnDat(new Date());
            subscriber.setWsClient(wsClient);
            getFacadeContainer().getSubscriberAPI().create(subscriber);
        } else {
            return false;
        }

        return true;
    }

    public boolean update(WsClient wsClient, String ci, Date birthDate, String address, String city, byte[] frontPhoto, byte[] backPhoto, String source, String registrationChannel) throws GenericFacadeException {

        Subscriber subscriber = getFacadeContainer().getSubscriberAPI().findByCi(
                ci);
        if (subscriber != null) {
            subscriber.setCiChr(ci);
            subscriber.setBirthdateDat(birthDate);
            subscriber.setAddressChr(address);
            subscriber.setCityChr(city);
            subscriber.setFrontPhotoByt(frontPhoto);
            subscriber.setBackPhotoByt(backPhoto);
            subscriber.setSourceChr(source);
            subscriber.setRegistrationChannelChr(registrationChannel);
            subscriber.setUpdatedOnDat(new Date());
            subscriber.setWsClient(wsClient);
            getFacadeContainer().getSubscriberAPI().edit(subscriber);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public Subscriber create(Subscriber entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManagerMTS();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            em.persist(entity);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return entity;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    @Override
    public Subscriber edit(Subscriber entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManagerMTS();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            em.merge(entity);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return entity;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(getClass(), null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
