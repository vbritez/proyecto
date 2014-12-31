package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import com.tigo.cs.domain.ClientParameter;

/**
 * 
 * @author Javier Kovacs
 * @version $Id: ClientParameterFacade.java 134 2011-12-01 12:48:19Z
 *          javier.kovacs $
 */

public abstract class ClientParameterAPI extends AbstractAPI<ClientParameter> {

    public ClientParameterAPI() {
        super(ClientParameter.class);
    }

    private List<ClientParameter> findClientParameterEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ClientParameter.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ClientParameter findClientParameter(String id) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            return em.find(ClientParameter.class, id);
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
