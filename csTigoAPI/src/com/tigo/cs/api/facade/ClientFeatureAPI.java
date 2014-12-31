package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ClientFeature;

public abstract class ClientFeatureAPI extends AbstractAPI<ClientFeature> {

    public ClientFeatureAPI() {
        super(ClientFeature.class);
    }

    @Override
    public ClientFeature edit(ClientFeature entity) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().begin();
            }
            ClientFeature clientFeatureManaged = find(
                    entity.getClientFeatureCod(), true);
            clientFeatureManaged.setClient(entity.getClient());
            clientFeatureManaged.setClientFeatureCod(entity.getClientFeatureCod());
            clientFeatureManaged.setFeature(entity.getFeature());
            clientFeatureManaged.setFeatureElements(entity.getFeatureElements());
            clientFeatureManaged.setEnabledChr(entity.getEnabledChr());
            clientFeatureManaged.setMaxElementNum(entity.getMaxElementNum());
            clientFeatureManaged.setMaxElementTransient(entity.getMaxElementTransient());
            clientFeatureManaged.setMaxEntryNum(entity.getMaxEntryNum());
            clientFeatureManaged.setShortcutNum(entity.getShortcutNum());
            clientFeatureManaged.setShortcutTransient(entity.getShortcutTransient());

            em.merge(clientFeatureManaged);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().commit();
            }
            return clientFeatureManaged;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(UseradminAPI.class, null,
                    e.getMessage(), e);
            if (getFacadeContainer().isEntityManagerTransactional()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientFeature> getClientFeature(Long clientCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT DISTINCT cf "
                + "FROM ClientFeature cf "
                + "WHERE cf.client.clientCod = :clientCod "
                + "AND cf.enabledChr = true ");
            query.setParameter("clientCod", clientCod);
            return query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public List<ClientFeature> getClientFeatureByShowOnMenu(Long clientCod, Boolean showOnMenu) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT DISTINCT cf "
                + "FROM ClientFeature cf "
                + "WHERE cf.client.clientCod = :clientCod "
                + "AND cf.enabledChr = true "
                + "AND cf.feature.showOnMenuChr = :showOnMenu ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("showOnMenu", showOnMenu);
            return query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public List<ClientFeature> getClientFeatureGeneral(Long clientCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT DISTINCT cf "
                + "FROM ClientFeature cf "
                + "WHERE cf.client.clientCod = :clientCod");
            query.setParameter("clientCod", clientCod);
            return query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ClientFeature findByShortcutCode(String shortCutTransient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            int shortCutNum = Integer.parseInt(shortCutTransient);

            Query query = em.createQuery("SELECT DISTINCT cf "
                + "FROM ClientFeature cf "
                + "WHERE cf.shortcutNum = :shortCutNum");
            query.setParameter("shortCutNum", shortCutNum);
            query.setMaxResults(1);
            return (ClientFeature) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ClientFeature getClientFeature(Long clientCod, Long featureCod) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + " SELECT DISTINCT cf "
                + "FROM ClientFeature cf "
                + " WHERE cf.client.clientCod = :clientCod "
                + " AND cf.feature.featureCode = :featureCod ");
            query.setParameter("clientCod", clientCod);
            query.setParameter("featureCod", featureCod);
            return (ClientFeature) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
