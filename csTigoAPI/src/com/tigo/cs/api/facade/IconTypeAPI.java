package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.IconType;

public class IconTypeAPI extends AbstractAPI<IconType> {

    public IconTypeAPI() {
        super(IconType.class);
    }
    
    
    public IconType findByUrl(String url) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT ip FROM IconType ip "
                + "WHERE ip.url = :url ");
            query.setParameter("url", url);
            query.setMaxResults(1);
            return (IconType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(IconTypeAPI.class, null,
                    e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
    public List<IconType> findIconEnabled() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT ip FROM IconType ip "
                + "WHERE ip.enabled = true and ip.iconDefault = false " +
                " ORDER BY ip.iconTypeCod");
            return (List<IconType>) query.getResultList();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(IconTypeAPI.class, null,
                    e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
    public IconType findDefaultIcon() {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT ip FROM IconType ip "
                + "WHERE ip.iconDefault = true ");
            query.setMaxResults(1);
            return (IconType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(IconTypeAPI.class, null,
                    e.getMessage(), e);
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
