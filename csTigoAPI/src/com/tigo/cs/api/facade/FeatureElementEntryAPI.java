package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.FeatureElementEntry;
import com.tigo.cs.domain.FeatureEntryType;

public abstract class FeatureElementEntryAPI extends AbstractAPI<FeatureElementEntry> {

    public FeatureElementEntryAPI() {
        super(FeatureElementEntry.class);
    }

    public List<FeatureElementEntry> findByOwnerAndType(FeatureElement featureElement, FeatureElementEntry featureElementEntry, FeatureEntryType featureEntryType) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT e From FeatureElementEntry f, "
                + "in (f.featureElementEntries) e "
                + "WHERE f.featureElement.featureElementCod = :featureElement "
                + "AND f.featureElementEntryCod.featureElementEntryCod = :featureElementEntry "
                + "AND e.codFeatureEntryType.featureEntryTypeCod = :featureEntryType "
                + "ORDER BY e.orderNum");

            q.setParameter("featureElement",
                    featureElement.getFeatureElementCod());
            q.setParameter("featureElementEntry",
                    featureElementEntry.getFeatureElementEntryCod());
            q.setParameter("featureEntryType",
                    featureEntryType.getFeatureEntryTypeCod());
            return q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public List<FeatureElementEntry> findPersisitibles(FeatureElement featureElement) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT e FROM FeatureElement f, IN (f.featureElementEntries) e "
                + "WHERE e.codFeatureEntryType.persistibleChr = true "
                + "AND f.featureElementCod = :featureElement "
                + "ORDER BY e.featureElementEntryCod ");

            q.setParameter("featureElement",
                    featureElement.getFeatureElementCod());

            return q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public List<FeatureElementEntry> findOutput(FeatureElement featureElement) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT e FROM FeatureElement f, IN (f.featureElementEntries) e "
                + "WHERE e.codFeatureEntryType.nameChr = 'OUTPUT' "
                + "AND f.featureElementCod = :featureElement "
                + "ORDER BY e.featureElementEntryCod ");

            q.setParameter("featureElement",
                    featureElement.getFeatureElementCod());

            return q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public FeatureElementEntry findByCode(Long featureElementEntryCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(" SELECT e FROM FeatureElementEntry e "
                + " WHERE e.featureElementEntryCod = :featureElementEntryCod ");

            q.setParameter("featureElementEntryCod", featureElementEntryCod);

            return (FeatureElementEntry) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public FeatureElementEntry getRootFeatureElementEntry(FeatureElement featureElement) throws EmptyResultException, MoreThanOneResultException {
        addParam("featureElement", featureElement);
        return findEntityWithNamedQuery(
                "FeatureElementEntry.findFeatureElementEntryRoot", finderParams);
    }

    public FeatureElementEntry getRootFeatureElementEntryEager(FeatureElement featureElement) throws EmptyResultException, MoreThanOneResultException {
        addParam("featureElement", featureElement);
        return findEntityWithNamedQuery(
                "FeatureElementEntry.findEagerFeatureElementEntryRoot",
                finderParams);
    }

    public List<FeatureElementEntry> getEagerFeatureElementEntrySortedList(FeatureElement featureElement) {
        List<FeatureElementEntry> listFeatureElementEntry = getEagerFeatureElementEntryList(featureElement);

        Collections.sort(listFeatureElementEntry);
        return listFeatureElementEntry;
    }

    public List<FeatureElementEntry> getEagerFeatureElementEntryList(FeatureElement featureElement) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("FeatureElementEntry.findEagerFeatureElementEntry");
            query.setParameter("featureElement", featureElement);

            List<FeatureElementEntry> featureElementEntries = query.getResultList();

            Set<FeatureElementEntry> setFeatureElementEntries = new HashSet<FeatureElementEntry>(featureElementEntries);
            List<FeatureElementEntry> listFeatureElementEntry = new ArrayList<FeatureElementEntry>(setFeatureElementEntries);

            return listFeatureElementEntry;
        } catch (Exception e) {
            getFacadeContainer().getNotifier().error(ClassificationAPI.class,
                    null, e.getMessage(), e);
            return null;

        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureElementEntry> getChildren(Long featureElementEntryCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT f From FeatureElementEntry f "
                + "WHERE f.codOwnerEntry.featureElementCod = :code ");

            q.setParameter("code", featureElementEntryCod);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
