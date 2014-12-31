package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import com.tigo.cs.domain.Auditory;

public abstract class AuditoryAPI extends AbstractAPI<Auditory> {

    public AuditoryAPI() {
        super(Auditory.class);
    }

    private List<Auditory> findAuditoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Auditory.class));
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

    public Auditory findAuditory(Long id) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            return em.find(Auditory.class, id);
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
