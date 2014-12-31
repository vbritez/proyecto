package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Image;
import com.tigo.cs.domain.New;

public abstract class NewAPI extends AbstractAPI<New> {

    public NewAPI() {
        super(New.class);
    }

    public New findByNewCod(Long newCod) throws MoreThanOneResultException, GenericFacadeException {
        try {
            return findEntityWithNamedQuery("findByNewCod",
                    addSingleParam("newsCod", newCod));
        } catch (Exception e) {
            throw new GenericFacadeException(getClass());
        }
    }

    public List<New> getNewsList(Integer maxResult) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT n FROM New n");
            if (maxResult != null) {
                query.setMaxResults(maxResult);
            }
            return (List<New>) query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            // throw new GenericFacadeException(this.getClass(), e);
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Image> findImagesByNewCod(Long newCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT images "
                + "FROM New n, IN (n.images) images "
                + "WHERE n.newCod = :newCod");
            query.setParameter("newCod", newCod);
            return query.getResultList();

        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
