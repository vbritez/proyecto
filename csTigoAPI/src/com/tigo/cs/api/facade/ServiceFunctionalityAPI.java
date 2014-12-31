package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.Functionality;
import com.tigo.cs.domain.ServiceFunctionality;

public abstract class ServiceFunctionalityAPI extends AbstractAPI<ServiceFunctionality> {

    public ServiceFunctionalityAPI() {
        super(ServiceFunctionality.class);
    }

    public ServiceFunctionality findByCodeAndFunctionality(Long codService, Long codFunctionality) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT sf "
                + "FROM ServiceFunctionality sf "
                + "WHERE sf.service.serviceCod = :codServ "
                + "AND sf.functionality.functionalityCod = :codFunc ");
            query.setParameter("codServ", codService);
            query.setParameter("codFunc", codFunctionality);
            return (ServiceFunctionality) query.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Functionality> getTrackingFunctionalityByClient(Long codClient) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createQuery("SELECT sf.functionality "
                + "FROM ServiceFunctionality sf, "
                + "IN (sf.clientServiceFunctionalityList) csfl "
                + "WHERE sf.service.serviceCod = 4 "
                + "AND sf.functionality.functionalityCod IN (2, 3, 4) "
                + "AND csfl.clientServiceFunctionalityPK.codClient = :codClient");
            query.setParameter("codClient", codClient);
            return (List<Functionality>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<Functionality> getCustodianFunctionalityByClient(Long codClient) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("SELECT sf.functionality "
                + "FROM ServiceFunctionality sf, "
                + "IN (sf.clientServiceFunctionalityList) csfl "
                + "WHERE sf.service.serviceCod = 6 "
                + "AND sf.functionality.functionalityCod IN (5, 6, 7, 8, 9, 10) "
                + "AND csfl.clientServiceFunctionalityPK.codClient = :codClient");
            query.setParameter("codClient", codClient);
            return (List<Functionality>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
