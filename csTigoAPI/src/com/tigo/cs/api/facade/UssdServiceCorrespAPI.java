package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ussd.UssdServiceCorresp;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: UssdServiceCorrespAPI.java 2246 2012-12-21 19:39:48Z
 *          miguel.maciel $
 */
public abstract class UssdServiceCorrespAPI extends AbstractAPI<UssdServiceCorresp> {

    public UssdServiceCorrespAPI() {
        super(UssdServiceCorresp.class);
    }

    public List<UssdServiceCorresp> findByCodService(Long codService) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("UssdServiceCorresp.findByCodService");
            query.setParameter("codService", codService);
            return (List<UssdServiceCorresp>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
