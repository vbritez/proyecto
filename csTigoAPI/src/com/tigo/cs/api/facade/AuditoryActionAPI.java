package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.AuditoryAction;

public abstract class AuditoryActionAPI extends AbstractAPI<AuditoryAction> {

    public AuditoryActionAPI() {
        super(AuditoryAction.class);
    }

    public AuditoryAction findByAction(Action action) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("AuditoryAction.findByAuditoryActionId");
            query.setParameter("auditoryActionId", new Integer(action.value()));
            return (AuditoryAction) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    private List<AuditoryAction> findAuditoryActionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AuditoryAction.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public AuditoryAction findAuditoryAction(Integer id) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            return em.find(AuditoryAction.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
