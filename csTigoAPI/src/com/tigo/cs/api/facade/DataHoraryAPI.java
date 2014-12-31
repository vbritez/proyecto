package com.tigo.cs.api.facade;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.tigo.cs.domain.view.DataHorary;

public class DataHoraryAPI extends AbstractAPI<DataHorary> {

    public DataHoraryAPI() {
        super(DataHorary.class);
    }

    public DataHorary findEntityByClientMetaCodigo(Long codClient, Long codMeta, String codigo) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + " SELECT da "
                + " FROM DataHorary da "
                + " WHERE da.dataPK.codClient = :clientcod "
                + " AND da.dataPK.codMeta = :metaCod "
                + " AND da.dataPK.codigo = :codigo ");
            query.setParameter("clientcod", codClient);
            query.setParameter("metaCod", codMeta);
            query.setParameter("codigo", codigo);
            query.setMaxResults(1);
            return (DataHorary) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null
                && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public List<DataHorary> findByClient(Long codClient) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query query = em.createQuery("" + " SELECT da "
                + " FROM DataHorary da "
                + " WHERE da.dataPK.codClient = :clientcod  ");
            query.setParameter("clientcod", codClient);
            return query.getResultList();
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
