package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.ussd.UssdDriver;

public class UssdDriverAPI extends AbstractAPI<UssdDriver> {

    public UssdDriverAPI() {
        super(UssdDriver.class);
    }

    public UssdDriver findById(String id) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT m.id FROM UssdDriver m "
                + " WHERE m.id = :id");
            q.setParameter("code", id);
            return (UssdDriver) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
