package com.tigo.cs.api.facade;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Classification;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.Userphone;
import com.tigo.cs.domain.Userweb;

public abstract class ClassificationAPI extends AbstractAPI<Classification> {

    public ClassificationAPI() {
        super(Classification.class);
    }

    public Classification getRootClassification(Client client) throws EmptyResultException, MoreThanOneResultException {
        addParam("client", client);
        return findEntityWithNamedQuery("Classification.findClientRoot",
                finderParams);
    }

    public Classification getRoot(Classification classification) throws EmptyResultException, MoreThanOneResultException {
        addParam("classificationCod", classification.getClassificationCod());
        return findEntityWithNamedQuery("Classification.findRoot", finderParams);
    }

    public List<Classification> getChildsClassification(Classification classification, boolean eager) {

        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            String whereLevel = "AND level-1 <= {0} ";
            String sql = "SELECT * FROM CLASSIFICATION c " + "WHERE 1 = 1 "
                + "AND c.COD_CLIENT = {0} {1}"
                + "START WITH c.CLASSIFICATION_COD = {2} "
                + "CONNECT BY PRIOR c.CLASSIFICATION_COD = c.ROOT ";

            if (classification.getDepthNum() != null) {
                whereLevel = MessageFormat.format(whereLevel,
                        classification.getDepthNum());
                sql = MessageFormat.format(
                        sql,
                        classification.getCodClient().getClientCod().toString(),
                        whereLevel,
                        classification.getClassificationCod().toString());
            } else {
                sql = MessageFormat.format(
                        sql,
                        classification.getCodClient().getClientCod().toString(),
                        "", classification.getClassificationCod().toString());
            }
            Query q = em.createNativeQuery(sql, Classification.class);
            List<Classification> classificationList = q.getResultList();
            if (eager) {
                if (classificationList != null) {
                    Query query = em.createQuery(""
                        + "SELECT distinct c FROM Classification c LEFT JOIN FETCH c.classificationList "
                        + "WHERE c in (:classifList)");
                    query.setParameter("classifList", q.getResultList());
                    return query.getResultList();
                }
                return null;
            }
            return classificationList;

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

    public List<Classification> findByUserweb(Userweb userweb) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT u.classificationList "
                + "FROM Userweb u WHERE u.userwebCod = :userwebCod");
            query.setParameter("userwebCod", userweb.getUserwebCod());
            return query.getResultList();
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

    public List<Classification> findByUserwebWithChilds(Userweb userweb) {
        List<Classification> classificationsUnique = findByUserweb(userweb);
        List<Classification> classifications = new ArrayList<Classification>();
        for (Classification classification : classificationsUnique) {
            classifications.addAll(getChildsClassification(classification, true));
        }

        Set<Classification> setClassifications = new HashSet<Classification>(classifications);
        List<Classification> listClassifications = new ArrayList<Classification>(setClassifications);

        return listClassifications.size() > 0 ? listClassifications : null;

    }

    public List<Classification> findByUserphone(Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT u.classificationList "
                + "FROM Userphone u " + "WHERE u.userwebCod = :userwebCod");
            query.setParameter("userwebCod", userphone.getUserphoneCod());
            return query.getResultList();
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

    public String getClassificationUserwebString(Userweb userweb) {
        List<Classification> classifications = findByUserwebWithChilds(userweb);
        String result = " ";
        for (Classification classification : classifications) {
            result += classification.getClassificationCod().toString() + " ";
        }
        result = result.trim().replace(" ", ",");
        return result;
    }

    public List<Classification> getEagerClassificationListByClient(Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("Classification.findEagerByClientCod");
            query.setParameter("client", client);

            List<Classification> classifications = query.getResultList();

            Set<Classification> setClassifications = new HashSet<Classification>(classifications);
            List<Classification> listClassifications = new ArrayList<Classification>(setClassifications);

            return listClassifications;
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

    public Classification findRootByClient(Client client) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("Classification.findClientRoot");
            query.setParameter("client", client);
            if (query.getResultList() != null
                && query.getResultList().size() == 1) {
                return (Classification) query.getSingleResult();
            } else {
                return null;
            }
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

    public List<Classification> findListByClient(Client client) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("Classification.findByClient");
            query.setParameter("client", client);

            List<Classification> classifications = query.getResultList();

            Set<Classification> setClassifications = new HashSet<Classification>(classifications);
            List<Classification> listClassifications = new ArrayList<Classification>(setClassifications);

            return listClassifications;
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

    /*
     * xq estan aca??
     */
    public String saveEdit(Classification entity) throws Exception {

        try {
            if (entity.getClassificationCod() == null) {
                super.create(entity);
            } else {
                super.edit(entity);
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public String deleteEntity(Classification entity) throws Exception {
        Classification classif = null;
        try {
            classif = super.find(entity.getClassificationCod());
            if (classif == null) {
                throw new Exception(facadeContainer.getI18nAPI().iValue(
                        "web.admin.backingBean.AbstractCrudBean.message.InexistentRecordDelete"));
            }

            super.remove(entity);

        } catch (Exception e) {
            throw new Exception(facadeContainer.getI18nAPI().iValue(
                    "web.admin.backingBean.AbstractCrudBean.signal.UnexpectedApplicationDeletingError"));
        }

        return null;

    }
    
    public List<Classification> findByUserphoneWithChilds(Userphone userphone) {
        List<Classification> classificationsUnique = findByUserphone2(userphone);
        List<Classification> classifications = new ArrayList<Classification>();
        for (Classification classification : classificationsUnique) {
            classifications.addAll(getChildsClassification(classification, true));
        }

        Set<Classification> setClassifications = new HashSet<Classification>(classifications);
        List<Classification> listClassifications = new ArrayList<Classification>(setClassifications);

        return listClassifications.size() > 0 ? listClassifications : null;

    }
    
    public List<Classification> findByUserphone2(Userphone userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT u.classificationList "
                + "FROM Userphone u " + "WHERE u.userphoneCod = :userphoneCod");
            query.setParameter("userphoneCod", userphone.getUserphoneCod());
            return query.getResultList();
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
}
