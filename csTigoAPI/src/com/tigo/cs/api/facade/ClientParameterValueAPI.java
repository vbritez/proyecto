package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.domain.ClientParameterValue;

public abstract class ClientParameterValueAPI extends AbstractAPI<ClientParameterValue> {

    public ClientParameterValueAPI() {
        super(ClientParameterValue.class);
    }

    public String findByCode(Long codClient, String codClientParameter) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("ClientParameterValue.findByCodClientAndCodClientParameter");
            query.setParameter("codClient", codClient);
            query.setParameter("codClientParameter", codClientParameter);
            return ((ClientParameterValue) query.getSingleResult()).getValueChr();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public List<ClientParameterValue> findByClient(Long clientCod) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + "SELECT csfL "
                + "FROM Client c, " + "IN (c.clientParameterValueList) csfL "
                + "WHERE c.clientCod = :clientcod ");
            query.setParameter("clientcod", clientCod);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public ClientParameterValue findByPK(Long codClient, String codClientParameter) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createNamedQuery("ClientParameterValue.findByCodClientAndCodClientParameter");
            query.setParameter("codClient", codClient);
            query.setParameter("codClientParameter", codClientParameter);
            return ((ClientParameterValue) query.getSingleResult());
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }
    }

    public void editValue(ClientParameterValue value) {
        ClientParameterValue managedValue = find(value.getClientParameterValuePK());
        managedValue.setValueChr(value.getValueChr());
        edit(managedValue);
    }

    @Override
    public void remove(ClientParameterValue entity) {
        super.remove(find(entity.getClientParameterValuePK(), true));
    }
}
