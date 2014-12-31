package com.tigo.cs.facade;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Subscriber;
import com.tigo.cs.domain.WsClient;

@Stateless
public class SubscriberFacade extends AbstractFacade<Subscriber> {

    public SubscriberFacade() {
        super(Subscriber.class);
    }

    public Subscriber findByCi(String ci) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query query = em.createNamedQuery("Subscriber.findByIdentification");
            query.setParameter("ci", ci);
            return (Subscriber) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            notifier.error(null, SubscriberFacade.class, null, e.getMessage(),
                    e);
            throw new GenericFacadeException(this.getClass(), e);
        }
    }

    public boolean register(WsClient wsClient, String ci, Date birthDate, String address, String city, byte[] frontPhoto, byte[] backPhoto, String source, String registrationChannel) throws GenericFacadeException {

        Date startDate = new Date();

        Subscriber subscriber = findByCi(ci);
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
            create(subscriber);
        } else {
            return false;
        }

        Date endate = new Date();

        notifier.info(null, getClass(), null,
                "REGISTER TIME - " + (endate.getTime() - startDate.getTime()));
        return true;
    }

    public boolean update(WsClient wsClient, String ci, Date birthDate, String address, String city, byte[] frontPhoto, byte[] backPhoto, String source, String registrationChannel) throws GenericFacadeException {

        Date startDate = new Date();

        Subscriber subscriber = findByCi(ci);
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
            edit(subscriber);
        } else {
            return false;
        }

        Date endate = new Date();

        notifier.info(null, getClass(), null,
                "UPDATE TIME - " + (endate.getTime() - startDate.getTime()));
        return true;
    }

    @Override
    public Subscriber create(Subscriber entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.persist(entity);
            return entity;
        } catch (Exception e) {
            notifier.error(null, getClass(), null, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Subscriber edit(Subscriber entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.merge(entity);
            return entity;
        } catch (Exception e) {
            notifier.error(null, getClass(), null, e.getMessage(), e);
            return null;
        }
    }
}
