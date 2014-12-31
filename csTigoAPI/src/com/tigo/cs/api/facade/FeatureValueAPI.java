package com.tigo.cs.api.facade;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.commons.log.Action;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.FeatureElement;
import com.tigo.cs.domain.FeatureValue;

public abstract class FeatureValueAPI extends AbstractAPI<FeatureValue> {

    public FeatureValueAPI() {
        super(FeatureValue.class);
    }

    public int count(FeatureElement featureElement) {

        String whereCriteria = "WHERE o.featureElement = {0,number,#} ";
        whereCriteria = MessageFormat.format(whereCriteria,
                featureElement.getFeatureElementCod());

        return super.count(whereCriteria);
    }

    public int getCantExterno(Long featureElement) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT count(fv.featureValueCod) FROM FeatureValue fv "
                + " WHERE fv.featureElement.featureElementCod = :featureElement "
                + " AND fv.userphone is null");
            query.setParameter("featureElement", featureElement);
            return ((Long) query.getSingleResult()).intValue();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return Integer.parseInt("0");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public int getCantInterno(Long featureElement) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT count(fv.featureValueCod) FROM FeatureValue fv "
                + " WHERE fv.featureElement.featureElementCod = :featureElement "
                + " AND fv.phone is null");
            query.setParameter("featureElement", featureElement);
            return ((Long) query.getSingleResult()).intValue();
        } catch (Exception e) {
            return Integer.parseInt("0");
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureValueAPI> findFeatureValuePhone(Long phoneCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery(""
                + " SELECT distinct fv FROM FeatureValue fv LEFT JOIN FETCH fv.message"
                + " WHERE fv.phone.phoneCod = :phoneCod ");
            query.setParameter("phoneCod", phoneCod);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<FeatureValueAPI>();
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureValue> findRange(int[] range, String whereCriteria, String orderbyCriteria, List<Classification> classifications, Date from, Date to) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            String JPQL = "SELECT o FROM ".concat(
                    FeatureValue.class.getSimpleName()).concat(
                    " o LEFT JOIN FETCH o.message ");
            if (whereCriteria != null && whereCriteria.trim().length() > 0) {
                JPQL += whereCriteria.trim();
            }
            if (orderbyCriteria != null && orderbyCriteria.trim().length() > 0) {
                JPQL += " ORDER BY " + orderbyCriteria.trim();
            }
            getFacadeContainer().getNotifier().signal(getClass(), Action.INFO,
                    JPQL);
            Query q = em.createQuery(JPQL);
            if (classifications != null) {
                q.setParameter("classifications", classifications);
            }

            if (from != null) {
                q.setParameter("dateFrom", from);
            }

            if (to != null) {
                q.setParameter("dateTo", to);
            }

            // Si el rando fue pasado como parametro entonces, se setea
            // el maxResult y firstResult
            if (range != null) {
                if (range.length > 1) {
                    q.setMaxResults(range[1] - range[0]);
                }
                if (range.length > 0) {
                    q.setFirstResult(range[0]);
                }
            }

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

    public Integer count(String whereCriteria, List<Classification> classifications, Date from, Date to) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            String JPQL = String.format("SELECT COUNT(*) FROM %s o ",
                    FeatureValue.class.getSimpleName());
            if (whereCriteria != null && whereCriteria.trim().length() > 0) {
                JPQL += whereCriteria.trim();
            }

            Query q = em.createQuery(JPQL);

            if (classifications != null) {
                q.setParameter("classifications", classifications);
            }

            if (from != null) {
                q.setParameter("dateFrom", from);
            }

            if (to != null) {
                q.setParameter("dateTo", to);
            }
            getFacadeContainer().getNotifier().log(getClass(), null,
                    Action.INFO, JPQL);

            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            return 0;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<FeatureValue> getFeatureValueList(Long featureElementCode) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT fv FROM FeatureValue fv "
                + " WHERE fv.featureElement.featureElementCod = :featureElementCode ");
            query.setParameter("featureElementCode", featureElementCode);
            return query.getResultList();
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

}
