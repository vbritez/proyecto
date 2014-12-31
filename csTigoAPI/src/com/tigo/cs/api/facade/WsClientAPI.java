package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.WsClient;

public class WsClientAPI extends AbstractAPI<WsClient> {

    public WsClientAPI() {
        super(WsClient.class);
    }

    public WsClient findByConsumerID(String consumerID) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManagerMTS();
            Query query = em.createNamedQuery("WsClient.findByConsumerId");
            query.setParameter("consumerId", consumerID);
            return (WsClient) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(WsClientAPI.class, null,
                    e.getMessage(), e);
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
