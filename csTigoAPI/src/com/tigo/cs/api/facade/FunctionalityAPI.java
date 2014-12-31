package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Functionality;

public abstract class FunctionalityAPI extends AbstractAPI<Functionality> {

    public FunctionalityAPI() {
        super(Functionality.class);
    }

    public List<Functionality> findAllSelectable() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("Functionality.findAllSelectable");
            return (List<Functionality>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
