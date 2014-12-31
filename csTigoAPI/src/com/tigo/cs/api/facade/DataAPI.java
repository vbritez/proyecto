package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.view.DataProduct;

/**
 *
 * @author Miguel Zorrilla
 */
public abstract class DataAPI extends AbstractAPI<DataProduct> {

    public DataAPI() {
        super(DataProduct.class);
    }


    public <M> M findByClass(Class<M> dataClass, Object id){
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            return em.find(dataClass, id);
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public <M> List<M> findRange(Class<M> aClass, int[] range, String whereCriteria, String orderbyCriteria) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String JPQL = "SELECT o FROM ".concat(aClass.getSimpleName()).concat(" o ");
            if (whereCriteria != null && whereCriteria.trim().length() > 0) {
                JPQL += " WHERE " + whereCriteria.trim();
            }
            if (orderbyCriteria != null && orderbyCriteria.trim().length() > 0) {
                JPQL += " ORDER BY " + orderbyCriteria.trim();
            }
            getFacadeContainer().getNotifier().signal(getClass(), Action.INFO, JPQL);
            Query q = em.createQuery(JPQL);

            //Si el rando fue pasado como parametro entonces, se setea
            //el maxResult y firstResult
            if (range != null) {
                if (range.length > 1) {
                    q.setMaxResults(range[1] - range[0]);
                }
                if (range.length > 0) {
                    q.setFirstResult(range[0]);
                }
            }

            return (List<M>) q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public <M> Integer count(Class<M> aClass, String whereCriteria) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String JPQL = String.format("SELECT COUNT(*) FROM %s o", aClass.getSimpleName());
            if (whereCriteria != null && whereCriteria.trim().length() > 0) {
                JPQL += " WHERE " + whereCriteria.trim();
            }

            Query q = em.createQuery(JPQL);

            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        }finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
