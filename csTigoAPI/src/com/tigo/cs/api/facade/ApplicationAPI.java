package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.Application;

public abstract class ApplicationAPI extends AbstractAPI<Application> {

    public ApplicationAPI() {
        super(Application.class);
    }

    public Application findByKey(String key) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("Application.findByApplicationKey");
            query.setParameter("key", key);
            return (Application) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
