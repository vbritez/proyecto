package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.GenericFacadeException;
import com.tigo.cs.domain.ServiceColumn;

public abstract class ServiceColumnAPI extends AbstractAPI<ServiceColumn> {

    public ServiceColumnAPI() {
        super(ServiceColumn.class);
    }

    public List<ServiceColumn> getColumnData(Long serviceId, String table) throws GenericFacadeException {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();

            Query query = em.createNamedQuery("ServiceColumn.getColumnData");
            query.setParameter("serviceCod", serviceId);
            query.setParameter("tableChr", table);
            return (List<ServiceColumn>) query.getResultList();
        } catch (Exception e) {
            throw new GenericFacadeException(this.getClass(), e);
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }
}
