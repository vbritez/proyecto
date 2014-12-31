package com.tigo.cs.api.facade;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Functionality;
import com.tigo.cs.domain.Service;

public abstract class ServiceAPI extends AbstractAPI<Service> {

    public ServiceAPI() {
        super(Service.class);
    }

    public Service findByServiceCod(Long serviceCod) throws MoreThanOneResultException, GenericFacadeException {
        try {
            return findEntityWithNamedQuery("Service.findByServiceCod",
                    addSingleParam("serviceCod", serviceCod));
        } catch (EmptyResultException ex) {
            return null;
        } catch (MoreThanOneResultException ex) {
            throw ex;
        } catch (Exception e) {
            throw new GenericFacadeException(getClass());
        }
    }

    public List<Functionality> getFunctionalities(Long serviceCod, boolean includeBasic) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT DISTINCT fL "
                + "FROM Service s, " + "IN (s.functionalitySet) fL "
                + "WHERE s.serviceCod = :serviceCod "
                + (!includeBasic ? "AND fL.functionalityCod > 0" : ""));
            query.setParameter("serviceCod", serviceCod);
            return query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Service> findByUserphone(Long userphone) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query q = em.createQuery(""
                + " SELECT l.serviceFunctionality.service "
                + " from Userphone u, in (u.clientServiceFunctionalityList) l"
                + " where u.userphoneCod = :userphone");
            q.setParameter("userphone", userphone);

            List<Service> result = q.getResultList();

            return result;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Service> getServiceAllAvailableList() throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("Service.findAllAvailable");
            return (List<Service>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        }
    }

    public List<Object[]> executeNativeQuery(String sql) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery(sql);
            return (List<Object[]>) q.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public Object executeNativeQuerySingleResult(String sql) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createNativeQuery(sql);
            return (Object) q.getSingleResult();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    /**
     * Obtiene la lista de c�digos de servicios habilitados para un userphone.
     * */
    public List<Long> getServiceCodByUserphoneAndFuncionality(Long userphone, Long functionality) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(""
                + " SELECT DISTINCT l.serviceFunctionality.service.serviceCod "
                + " from Userphone u, in (u.clientServiceFunctionalityList) l"
                + " where u.userphoneCod = :userphone"
                + " AND l.serviceFunctionality.functionality.functionalityCod = :functionality");
            q.setParameter("userphone", userphone);
            q.setParameter("functionality", functionality);

            List<Long> result = q.getResultList();

            return result;
        } catch (NoResultException e) {
            return new ArrayList<Long>();
        } catch (Exception e) {
            throw new GenericFacadeException(ServiceAPI.class, "Cannot obtain list of service by userphone from facade.", e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Service> getServiceByUserphoneAndFuncionality(Long userphone, Long functionality) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery(""
                + " SELECT DISTINCT l.serviceFunctionality.service "
                + " from Userphone u, in (u.clientServiceFunctionalityList) l"
                + " where u.userphoneCod = :userphone"
                + " AND l.serviceFunctionality.functionality.functionalityCod = :functionality");
            q.setParameter("userphone", userphone);
            q.setParameter("functionality", functionality);

            List<Service> result = q.getResultList();
            return result;
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Service> findServiceEntities() {
        return findServiceEntities(true, -1, -1);
    }

    public List<Service> findServiceEntities(int maxResults, int firstResult) {
        return findServiceEntities(false, maxResults, firstResult);
    }

    private List<Service> findServiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Service.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

}
