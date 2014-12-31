package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.ClientFeature;
import com.tigo.cs.domain.Feature;

public abstract class FeatureAPI extends AbstractAPI<Feature> {

    public FeatureAPI() {
        super(Feature.class);
    }

    public List<Feature> getFeatureByClient(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT c.feature "
                + "FROM ClientFeature c "
                + "WHERE c.client.clientCod = :clientCod ");
            query.setParameter("clientCod", clientCod);
            return (List<Feature>) query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ClientFeature getClientFeature(Long clientCod, Long featureCode) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery(" SELECT c "
                + " FROM ClientFeature c "
                + " WHERE c.client.clientCod = :clientCod "
                + " AND c.feature.featureCode = :featureCode ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("featureCode", featureCode);
            return (ClientFeature) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
    public List<Feature> getFeatureByShowOnMenu(Boolean showOnMenu) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT f "
                + "FROM Feature f "
                + "WHERE f.showOnMenuChr = :showOnMenu ");
            query.setParameter("showOnMenu", showOnMenu);
            return (List<Feature>) query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
    
    public List<Feature> getFeatureByClientByShowOnMenu(Long clientCod, Boolean showOnMenu) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery( " SELECT c.feature "
						                + " FROM ClientFeature c "
						                + " WHERE c.client.clientCod = :clientCod "
						                + " AND c.feature.showOnMenuChr = :showOnMenu ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("showOnMenu", showOnMenu);
            return (List<Feature>) query.getResultList();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
