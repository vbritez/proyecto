package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Seller;

public abstract class SellerAPI extends AbstractAPI<Seller> {

    public SellerAPI() {
        super(Seller.class);
    }

    public List<Seller> findAllEnabled() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("Seller.findByEnabledChr");
            query.setParameter("enabledChr", true);
            return (List<Seller>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
